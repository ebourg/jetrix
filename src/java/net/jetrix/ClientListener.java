/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2002  Emmanuel Bourg
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package net.jetrix;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import net.jetrix.config.*;
import net.jetrix.messages.*;

/**
 * Abstract Listener waiting for incomming connections.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public abstract class ClientListener implements Runnable
{
    private ServerSocket serverSocket;
    private Socket socket;
    private Logger logger;

    public ClientListener()
    {
        Server server = Server.getInstance();
    }

    /**
     * Return the name of the listener.
     */
    public abstract String getName();

    /**
     * Return the listening port.
     */
    public abstract int getPort();

    /**
     * Initialize a client from a socket.
     */
    public abstract Client getClient(Socket socket) throws Exception;

    /**
     * Main loop waiting for clients and checking basic rules (nickname
     * unicity, ban list, room left on the server). Newly created clients
     * are then forwarded to the main server queue for channel assignation.
     */
    public final void run()
    {
        logger = Logger.getLogger("net.jetrix");
        ServerConfig serverConfig = Server.getInstance().getConfig();

        try
        {
            serverSocket = new ServerSocket(getPort(), 50, serverConfig.getHost());
            logger.info("Listening at " + getName() + " port " + getPort()
                + ( (serverConfig.getHost() != null)?", bound to " + serverConfig.getHost():"") );
        }
        catch (IOException e)
        {
            logger.severe("Cannot open ServerSocket");
            //serverConfig.setRunning(false);
            e.printStackTrace();
        }

        while (serverConfig.isRunning())
        {
            try
            {
                // waiting for connexions
                socket = serverSocket.accept();

                // logging connexion
                logger.info("Incoming client " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());

                Client client = getClient(socket);
                Player player = client.getPlayer();
                player.setLocale(serverConfig.getLocale());
                client.getProtocol().setLocale(player.getLocale());

                // checking if server is full
                ClientRepository repository = ClientRepository.getInstance();
                if (repository.getClientCount() >= serverConfig.getMaxPlayers())
                {
                    logger.info("Server full, client rejected (" + socket.getInetAddress().getHostAddress() + ").");
                    Message m = new NoConnectingMessage("Server is full!");
                    client.sendMessage(m);
                    socket.close();
                    continue;
                }

                // testing name unicity
                if (repository.getClient(player.getName()) == null)
                {
                    repository.addClient(client);
                }
                else
                {
                    Message m = new NoConnectingMessage("Nickname already in use!");
                    client.sendMessage(m);
                    socket.close();
                    continue;
                }

                // testing concurrent connexions from the same host
                // ....

                // testing ban list
                // ....

                logger.fine("Client accepted (" + socket.getInetAddress().getHostAddress() + ")");

                // sending the message of the day
                BufferedReader motd = new BufferedReader(new StringReader( serverConfig.getMessageOfTheDay() ));
                String motdline;
                while( (motdline = motd.readLine() ) != null )
                {
                    PlineMessage m = new PlineMessage();
                    m.setText("<gray>" + motdline);
                    client.sendMessage(m);
                }
                motd.close();

                // forwarding client to server for channel assignation
                AddPlayerMessage m = new AddPlayerMessage();
                m.setClient(client);
                Server.getInstance().sendMessage(m);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public String toString()
    {
        return "[Listener name='" + getName() + "' port=" + getPort() + "]";
    }

}
