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

package net.jetrix.clients;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import net.jetrix.*;
import net.jetrix.config.*;
import net.jetrix.messages.*;

/**
 * Layer handling communication with a tetrinet or tetrifast client. Incomming 
 * messages are turned into a server understandable format and forwarded to the 
 * apropriate destination for processing (the player's channel or  the main 
 * server thread)
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TetrinetClient implements Client
{
    private String type;
    private String version;
    private Protocol protocol;
    private Channel channel;
    private User user;
    protected Date connectionTime;
    protected boolean disconnected;

    protected Reader in;
    protected Writer out;
    protected Socket socket;
    protected ServerConfig serverConfig;
    protected Logger logger = Logger.getLogger("net.jetrix");

    public TetrinetClient() { }

    public TetrinetClient(User user, Socket socket)
    {
        this();
        setSocket(socket);
        this.user = user;
    }

    /**
     * Return the protocol used by this client.
     */
    public Protocol getProtocol()
    {
        return this.protocol;
    }

    /**
     * Set the protocol.
     */
    public void setProtocol(Protocol protocol)
    {
        this.protocol = protocol;
    }

    /**
     * Main loop listening and parsing messages sent by the client.
     */
    public void run()
    {
        logger.fine("Client started " + this);
        
        connectionTime = new Date();
        
        Server server = Server.getInstance();
        if (server != null) serverConfig = server.getConfig();        

        try
        {
            while (!disconnected && serverConfig.isRunning())
            {
                Message m = receiveMessage();
                if (m == null) continue;

                if (channel != null)
                {
                    channel.sendMessage(m);
                }
                else
                {
                    // no channel assigned, the message is sent to the server
                    server.sendMessage(m);
                }
            }

            LeaveMessage leaveNotice = new LeaveMessage();
            leaveNotice.setSlot(channel.getClientSlot(this));
            leaveNotice.setName(user.getName());
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

    public void sendMessage(Message m)
    {
        String rawMessage = m.getRawMessage(getProtocol(), user.getLocale());
        
        if (rawMessage != null)
        {
            try
            {
                synchronized(out)
                {
                    out.write(rawMessage + (char)255, 0, rawMessage.length() + 1);
                    out.flush();
                }

                logger.finest("> " + rawMessage);
            }
            catch (SocketException e) { logger.fine(e.getMessage()); }
            catch (Exception e) { e.printStackTrace(); }
        }
        else
        {
            logger.warning("Message not sent, raw message missing " + m);
        }
    }

    public Message receiveMessage() throws IOException
    {
        // read raw message from socket
        String s = readLine();
        logger.finer("RECV: " + s);

        // build server message
        Message m = getProtocol().getMessage(s);
        //m.setRawMessage(getProtocol(), s);
        if (m != null) m.setSource(this);

        return m;
    }

    /**
     * Read a line sent by the tetrinet client.
     *
     * @return line sent
     */
    public String readLine() throws IOException
    {
        int readChar;
        StringBuffer input = new StringBuffer();

        while ((readChar = in.read()) != -1 && readChar != 0xFF && readChar != 0x0A && readChar != 0x0D)
        {
            if (readChar != 0x0A && readChar != 0x0D)
            {
                input.append((char)readChar);
            }
        }

        if (readChar == -1) throw new IOException("client disconnected");

        return input.toString();
    }

    public void setSocket(Socket socket)
    {
        this.socket = socket;
        try
        {
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream(), "ISO-8859-1"));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "ISO-8859-1"));
        }
        catch(IOException e) { e.printStackTrace(); }
    }

    public Socket getSocket()
    {
        return socket;
    }

    public InetAddress getInetAddress()
    {
        return socket.getInetAddress();
    }

    public void setChannel(Channel channel)
    {
        this.channel = channel;
    }

    public Channel getChannel()
    {
        return channel;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public User getUser()
    {
        return user;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getVersion()
    {
        return version;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    public Date getConnectionTime()
    {
        return connectionTime;
    }

    public void disconnect()
    {
        disconnected = true;
        try { socket.shutdownOutput(); } catch(Exception e) { e.printStackTrace(); }
    }

    public String toString()
    {
        return "[Client " + getInetAddress() + " type=" + type + "]";
    }

}
