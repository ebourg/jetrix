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
 * Layer handling communication with a client. Incomming messages are turned
 * into a server understandable format and forwarded to the apropriate
 * destination for processing (player's channel or main server thread)
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public abstract class Client implements Runnable, Destination
{
    private Socket socket;
    private Reader in;
    private Writer out;
    private ServerConfig serverConfig;

    private int type;
    private String version;

    // client types
    public static final int CLIENT_TETRINET  = 0;
    public static final int CLIENT_TETRIFAST = 1;
    public static final int CLIENT_TSPEC     = 2;

    private Channel channel;
    private Server server;
    private Player player;

    private boolean disconnected;
    private Logger logger = Logger.getLogger("net.jetrix");
    private static List allowedSpecials;

    public Client()
    {
        Server server = Server.getInstance();
        if (server != null ) serverConfig = server.getConfig();
    }

    public Client(Player player, Socket socket)
    {
        this();
        setSocket(socket);
        this.player = player;
    }

    /**
     * Return the protocol used by this client.
     */
    public abstract Protocol getProtocol();

    /**
     * Main loop listening and parsing messages sent by the client.
     */
    public void run()
    {
        logger.fine("Client started " + this);

        try
        {
            String s;
            Message m;

            while ( !disconnected && serverConfig.isRunning() )
            {
                // reading raw message from socket
                s = readLine();
                logger.finer("RECV: " + s);

                try
                {
                    // building server message
                    m = getProtocol().getMessage(s);
                    if (m == null) continue;
                    //m.setRawMessage(getProtocol().getName(), s);
                    m.setSource(this);

                    // forwarding message to channel
                    channel.sendMessage(m);

                }
                catch (NumberFormatException e)
                {
                    logger.finer("Bad format message");
                    e.printStackTrace();
                }
                catch (NoSuchElementException e)
                {
                    logger.finer("Bad format message");
                    e.printStackTrace();
                }
                catch (IllegalArgumentException e)
                {
                    logger.warning(e.getMessage());
                    e.printStackTrace();
                }
            } // end while

            LeaveMessage leaveNotice = new LeaveMessage();
            leaveNotice.setSlot(channel.getPlayerSlot(this));
            channel.sendMessage(leaveNotice);
        }
        catch (IOException e)
        {
            DisconnectedMessage m = new DisconnectedMessage();
            m.setClient(this);
            channel.sendMessage(m);
        }
        finally
        {
            try { in.close(); }     catch (IOException e) { e.printStackTrace(); }
            try { out.close(); }    catch (IOException e) { e.printStackTrace(); }
            try { socket.close(); } catch (IOException e) { e.printStackTrace(); }
            ClientRepository.getInstance().removeClient(this);
        }
    }

    /**
     * Send a message to the client. The raw message property must be set.
     *
     * @param m message to send
     */
    public void sendMessage(Message m)
    {
        if (m.getRawMessage(getProtocol()) != null)
        {
            try
            {
                synchronized(out)
                {
                    out.write(m.getRawMessage(getProtocol()) + (char)255, 0, m.getRawMessage(getProtocol()).length() + 1);
                    out.flush();
                }

                logger.finest("> " + m.getRawMessage(getProtocol()));
            }
            catch (SocketException e) { logger.fine(e.getMessage()); }
            catch (Exception e) { e.printStackTrace(); }
        }
        else
        {
            logger.warning("Message not sent, raw message missing " + m);
        }
    }


    /**
     * Read a line sent by the tetrinet client.
     *
     * @return line sent
     */
    public abstract String readLine() throws IOException;

    public void setChannel(Channel channel)
    {
        this.channel = channel;
    }

    public Channel getChannel()
    {
        return channel;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setSocket(Socket socket)
    {
        this.socket = socket;
        try
        {
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }
        catch(IOException e) { e.printStackTrace(); }
    }

    public Socket getSocket()
    {
        return socket;
    }

    protected Reader getReader()
    {
        return in;
    }

    protected Writer getWriter()
    {
        return out;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getVersion()
    {
        return version;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public int getType()
    {
        return type;
    }

    /**
     * Triggers the disconnection of this client.
     */
    public void disconnect()
    {
        disconnected = true;
        try { socket.shutdownOutput(); } catch(Exception e) { e.printStackTrace(); }
    }

    public String toString()
    {
        return "[Client " + socket + " type=" + type + "]";
    }

}
