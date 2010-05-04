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
import java.util.concurrent.*;
import java.util.logging.*;

import net.jetrix.*;
import net.jetrix.config.*;
import net.jetrix.messages.*;
import net.jetrix.messages.channel.*;

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
    private String agent;
    private String version;
    private Protocol protocol;
    private Channel channel;
    private User user;
    protected Date connectionTime;
    protected long lastMessageTime;
    protected boolean disconnected;
    private boolean running;

    protected InputStream in;
    protected OutputStream out;
    protected Socket socket;
    protected String encoding = "Cp1252";
    protected ServerConfig serverConfig;
    protected BlockingQueue<Message> queue;
    
    protected Logger log = Logger.getLogger("net.jetrix");

    public TetrinetClient()
    {
        if (isAsynchronous())
        {
            queue = new LinkedBlockingQueue<Message>();
        }
    }

    public TetrinetClient(User user)
    {
        this();
        this.user = user;
    }

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
        if (log.isLoggable(Level.FINE))
        {
            log.fine("Client started " + this);
        }

        running = true;

        if (isAsynchronous())
        {
            // start the message sender thread
            new MessageSender("sender-" + user.getName()).start();
        }

        connectionTime = new Date();

        // get the server configuration if possible
        Server server = Server.getInstance();
        if (server != null)
        {
            serverConfig = server.getConfig();
        }

        try
        {
            while (!disconnected && serverConfig.isRunning())
            {
                // fetch the next message
                Message message = receive();

                // discard unknown messages
                if (message == null) continue;

                // todo check the slot on channel messages

                if (message.getDestination() != null)
                {
                    message.getDestination().send(message);
                }
                else if (message instanceof ClientInfoMessage)
                {
                    ClientInfoMessage info = (ClientInfoMessage) message;
                    setAgent(info.getName());
                    setVersion(info.getVersion());
                    if ("gtetrinet".equalsIgnoreCase(info.getName()))
                    {
                        encoding = "UTF-8";
                    }
                }
                else if (channel != null)
                {
                    // send the message to the channel assigned to this client
                    channel.send(message);
                }
                else
                {
                    // no channel assigned, the message is sent to the server
                    server.send(message);
                }
            }
        }
        catch (IOException e)
        {
            if (Server.getInstance().getConfig().isRunning())
            {
                log.log(Level.SEVERE, "Connexion error with the tetrinet client '" + getUser().getName() + "'", e);
            }
        }
        finally
        {
            if (!socket.isClosed())
            {
                disconnect();
                try { in.close(); }     catch (IOException e) { e.printStackTrace(); }
                try { out.close(); }    catch (IOException e) { e.printStackTrace(); }
                try { socket.close(); } catch (IOException e) { e.printStackTrace(); }
            }

            // unregister the client from the server
            ClientRepository.getInstance().removeClient(this);

            // remove the player from the channel
            if (channel != null)
            {
                // todo: remove the client from all channels if it supports multiple channels (IRC clients)
                DisconnectedMessage disconnect = new DisconnectedMessage();
                disconnect.setClient(this);
                channel.send(disconnect);
            }
        }
    }

    public void send(Message message)
    {
        // check the ignore list
        if (message instanceof TextMessage)
        {
            Destination source = message.getSource();
            if (source instanceof Client)
            {
                Client client = (Client) source;
                if (getUser().ignores(client.getUser().getName()))
                {
                    if (log.isLoggable(Level.FINEST))
                    {
                        log.finest("Message dropped, player " + client.getUser().getName() + " ignored");
                    }
                    
                    return;
                }
            }
        }

        if (isAsynchronous() && running)
        {
            // add to the queue
            queue.add(message);
        }
        else
        {
            // write directly
            write(message);
        }
    }

    /**
     * Write the message on the output stream.
     */
    private void write(Message message)
    {
        String rawMessage = message.getRawMessage(getProtocol(), user.getLocale());

        if (rawMessage != null)
        {
            try
            {
                synchronized (out)
                {
                    out.write(rawMessage.getBytes(getEncoding()));
                    out.write(getProtocol().getEOL());
                    out.flush();
                }

                if (log.isLoggable(Level.FINEST))
                {
                    log.finest("> " + rawMessage);
                }
            }
            catch (SocketException e)
            {
                if (log.isLoggable(Level.FINE))
                {
                    log.fine(e.getMessage());
                }
            }
            catch (Exception e)
            {
                log.log(Level.INFO, getUser().toString(), e);
            }
        }
        else
        {
            log.warning("Message not sent, raw message missing " + message);
        }
    }

    public Message receive() throws IOException
    {
        // read raw message from socket
        String line = protocol.readLine(in, getEncoding());
        lastMessageTime = System.currentTimeMillis();
        if (log.isLoggable(Level.FINER))
        {
            log.finer("RECV: " + line);
        }

        // build server message
        Message message = getProtocol().getMessage(line);
        //message.setRawMessage(getProtocol(), line);
        if (message != null)
        {
            message.setSource(this);
        }

        return message;
    }

    public void setSocket(Socket socket)
    {
        this.socket = socket;
        try
        {
            in  = new BufferedInputStream(socket.getInputStream());
            out = new BufferedOutputStream(socket.getOutputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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

    public boolean supportsMultipleChannels()
    {
        return false;
    }

    public boolean supportsAutoJoin()
    {
        return true;
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

    public String getAgent()
    {
        return agent;
    }

    public void setAgent(String agent)
    {
        this.agent = agent;
    }

    public Date getConnectionTime()
    {
        return connectionTime;
    }

    public long getIdleTime()
    {
        return System.currentTimeMillis() - lastMessageTime;
    }

    public String getEncoding()
    {
        return encoding;
    }

    public void disconnect()
    {
        disconnected = true;

        // notify the message sender thread
        if (queue != null)
        {
            queue.add(new ShutdownMessage());
        }

        try
        {
            socket.shutdownOutput();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Tells if the messages are sent asynchroneously to the client.
     *
     * @since 0.2
     */
    protected boolean isAsynchronous()
    {
        return true;
    }

    public String toString()
    {
        return "[Client " + getInetAddress() + " type=" + agent + " " + version + "]";
    }

    /**
     * A thread sending the message to the client asynchroneously.
     *
     * @since 0.2
     */
    private class MessageSender extends Thread
    {
        private int index;
        private long timestamp[];
        private int capacity = 10;
        private int delay = 100;

        public MessageSender(String name)
        {
            super(name);

            timestamp = new long[capacity];
        }

        public void run()
        {
            while (!disconnected)
            {
                try
                {
                    // take the message
                    Message message = queue.take();

                    if (disconnected)
                    {
                        return;
                    }

                    // delay
                    if (isRateExceeded(System.currentTimeMillis()))
                    {
                        sleep(10);
                    }

                    // write the message
                    write(message);
                }
                catch (InterruptedException e)
                {
                    log.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }

        private boolean isRateExceeded(long t)
        {
            long t1 = timestamp[index];
            timestamp[index] = t;
            index = (index + 1) % capacity;

            return (t - t1) < delay;
        }
    }

}
