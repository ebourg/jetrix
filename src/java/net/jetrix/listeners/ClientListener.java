/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2003  Emmanuel Bourg
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

package net.jetrix.listeners;

import java.io.*;
import java.net.*;
import java.util.logging.*;

import net.jetrix.*;
import net.jetrix.clients.*;
import net.jetrix.config.*;
import net.jetrix.messages.*;

/**
 * Abstract Listener waiting for incomming clients.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public abstract class ClientListener implements Listener
{
    private ServerSocket serverSocket;
    private Socket socket;
    private Logger logger;
    private boolean running;

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
        running = true;

        try
        {
            serverSocket = new ServerSocket(getPort(), 50, serverConfig.getHost());
            logger.info("Listening at " + getName() + " port " + getPort()
                + ( (serverConfig.getHost() != null)?", bound to " + serverConfig.getHost():"") );
        }
        catch (BindException e)
        {
            logger.severe("Unable to bind " + getName() + " listener at port " + getPort());
            running = false;
        }
        catch (IOException e)
        {
            logger.severe("Cannot open ServerSocket");
            e.printStackTrace();
        }

        while (serverConfig.isRunning() && running)
        {
            try
            {
                // waiting for connexions
                socket = serverSocket.accept();
                socket.setSoTimeout(3000);

                // log the connection
                logger.info("Incoming client " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());

                Client client = getClient(socket);
                User user = client.getUser();
                user.setLocale(serverConfig.getLocale());

                // check if the server is full
                ClientRepository repository = ClientRepository.getInstance();
                if (repository.getClientCount() >= serverConfig.getMaxPlayers()
                    && !(client instanceof QueryClient))
                {
                    logger.info("Server full, client rejected (" + socket.getInetAddress().getHostAddress() + ").");
                    Message m = new NoConnectingMessage("Server is full!");
                    client.sendMessage(m);
                    socket.close();
                    continue;
                }

                // test concurrent connections from the same host
                int maxConnections = serverConfig.getMaxConnections();
                if (maxConnections > 0 && repository.getHostCount(client.getInetAddress()) >= maxConnections)
                {
                    logger.info("Too many connections from host, client rejected (" + socket.getInetAddress().getHostAddress() + ").");
                    Message m = new NoConnectingMessage("Too many connections from your host!");
                    client.sendMessage(m);
                    socket.close();
                    continue;
                }

                // testing name unicity
                if (repository.getClient(user.getName()) != null)
                {
                    Message m = new NoConnectingMessage("Nickname already in use!");
                    client.sendMessage(m);
                    socket.close();
                    continue;
                }

                // testing ban list
                // ....

                logger.fine("Client accepted (" + socket.getInetAddress().getHostAddress() + ")");
                socket.setSoTimeout(serverConfig.getTimeout() * 1000);

                if (!(client instanceof QueryClient))
                {
                    // add the client to the repository
                    repository.addClient(client);

                    // send the message of the day
                    BufferedReader motd = new BufferedReader(new StringReader( serverConfig.getMessageOfTheDay() ));
                    String motdline;
                    while( (motdline = motd.readLine() ) != null )
                    {
                        PlineMessage m = new PlineMessage();
                        m.setText("<gray>" + motdline);
                        client.sendMessage(m);
                    }
                    motd.close();

                    // forward the client to the server for channel assignation
                    AddPlayerMessage m = new AddPlayerMessage();
                    m.setClient(client);
                    Server.getInstance().sendMessage(m);
                }

                // start the client
                (new Thread(client)).start();
            }
            catch (Exception e)
            {
                try { socket.close(); } catch (IOException ioe) { ioe.printStackTrace(); }
                e.printStackTrace();
            }
        }
    }

    /**
     * Start the listener.
     */
    public void start()
    {
        (new Thread(this)).start();
    }

    /**
     * Stop the listener.
     */
    public void stop()
    {
        try
        {
            logger.info("Stopping listener " + getName());
            running = false;
            serverSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public String toString()
    {
        return "[Listener name='" + getName() + "' port=" + getPort() + "]";
    }

}
