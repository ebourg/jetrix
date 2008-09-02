/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2005  Emmanuel Bourg
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
import java.util.*;
import java.util.logging.*;


import net.jetrix.*;
import net.jetrix.clients.*;
import net.jetrix.config.*;
import net.jetrix.listeners.interceptor.*;
import net.jetrix.messages.*;
import net.jetrix.services.*;

/**
 * Abstract Listener waiting for incomming clients.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public abstract class ClientListener extends AbstractService implements Listener
{
    private ServerSocket serverSocket;
    private Socket socket;
    protected int port;
    private Logger log;
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
        log = Logger.getLogger("net.jetrix");
        ServerConfig serverConfig = Server.getInstance().getConfig();
        running = true;

        try
        {
            // bind the listener to the host & port
            serverSocket = new ServerSocket(getPort(), 50, serverConfig.getHost());
            log.info("Listening at " + getName() + " port " + getPort()
                    + ((serverConfig.getHost() != null) ? ", bound to " + serverConfig.getHost() : ""));
        }
        catch (BindException e)
        {
            log.severe("Unable to bind " + getName() + " listener at port " + getPort());
            running = false;
        }
        catch (IOException e)
        {
            log.severe("Cannot open ServerSocket");
            e.printStackTrace();
        }

        while (serverConfig.isRunning() && running)
        {
            try
            {
                // waiting for connexions
                socket = serverSocket.accept();
                socket.setSoTimeout(10000);

                InetAddress address = socket.getInetAddress();

                // log the connection
                log.info("Incoming client " + address.getHostAddress() + ":" + socket.getPort());

                // test the ban list
                if (Banlist.getInstance().isBanned(address))
                {
                    socket.close();
                    log.info("Banned host, client rejected (" + address + ")");
                    continue;
                }

                // spawn the client verifier processing the interceptors
                new ClientVerifier(socket).start();
            }
            catch (Exception e)
            {
                try
                {
                    if (socket != null)
                    {
                        socket.close();
                    }
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }

                if (Server.getInstance().getConfig().isRunning())
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    /**
     * Start the listener.
     */
    public void start()
    {
        (new Thread(this, "listener: " + getName())).start();
    }

    /**
     * Stop the listener.
     */
    public void stop()
    {
        try
        {
            log.info("Stopping listener " + getName());
            running = false;
            serverSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean isRunning()
    {
        return running;
    }

    public String toString()
    {
        return "[Listener name='" + getName() + "' port=" + getPort() + "]";
    }

    /**
     * Thread checking incoming connections and spawning a new client
     * if everything is ok.
     *
     * @since 0.2
     */
    private class ClientVerifier extends Thread
    {
        private Socket socket;

        public ClientVerifier(Socket socket)
        {
            super("client-verifier:" + socket.getInetAddress().getHostAddress());
            this.socket = socket;
        }

        public void run()
        {
            ServerConfig serverConfig = Server.getInstance().getConfig();
            InetAddress address = socket.getInetAddress();

            try
            {
                Client client = getClient(socket);

                if (client != null)
                {
                    User user = client.getUser();
                    user.setLocale(serverConfig.getLocale());

                    // todo move the declaration of the interceptors in the server configuration
                    Collection<ClientInterceptor> validators = new ArrayList<ClientInterceptor>();
                    validators.add(new AccessInterceptor());
                    validators.add(new NameCheckInterceptor());

                    // run the validators
                    for (ClientInterceptor interceptor : validators)
                    {
                        interceptor.process(client);
                    }

                    log.fine("Client accepted (" + address + ")");
                    socket.setSoTimeout(serverConfig.getTimeout() * 1000);

                    if (!(client instanceof QueryClient))
                    {
                        // add the client to the repository
                        ClientRepository repository = ClientRepository.getInstance();
                        repository.addClient(client);

                        Collection<ClientInterceptor> interceptors = new ArrayList<ClientInterceptor>();
                        interceptors.add(new MotdInterceptor());
                        interceptors.add(new OnlineUsersInterceptor());
                        interceptors.add(new ServerStatsInterceptor());

                        // run the interceptors
                        for (ClientInterceptor interceptor : interceptors)
                        {
                            interceptor.process(client);
                        }

                        // forward the client to the server for channel assignation
                        if (client.supportsAutoJoin())
                        {
                            AddPlayerMessage m = new AddPlayerMessage();
                            m.setClient(client);
                            Server.getInstance().send(m);
                        }
                    }

                    // start the client
                    (new Thread(client, "client: " + client.getUser().getName())).start();
                }
            }
            catch (Exception e)
            {
                try
                {
                    if (socket != null)
                    {
                        socket.close();
                    }
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }

                if (!(e instanceof ClientValidationException))
                {
                    e.printStackTrace();
                }
            }

        }
    }

}
