/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2004  Emmanuel Bourg
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
import java.util.concurrent.*;
import java.util.logging.*;

import net.jetrix.clients.*;
import net.jetrix.commands.*;
import net.jetrix.config.*;
import net.jetrix.messages.*;

/**
 * Main class, starts server components.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class Server implements Runnable, Destination
{
    private static Server instance;

    private ServerConfig config;
    private BlockingQueue<Message> queue;
    private ChannelManager channelManager;
    private Logger log = Logger.getLogger("net.jetrix");
    private Client console;

    private Server()
    {
        System.out.println("Jetrix TetriNET Server " + ServerConfig.VERSION + ", Copyright (C) 2001-2004 Emmanuel Bourg\n");

        // spawn the server message queue
        queue = new LinkedBlockingQueue<Message>();

        // add the stop hook
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                if (config != null)
                {
                    instance.stop();
                }
            }
        });

        config = new ServerConfig();
    }

    /**
     * Return the unique instance of the server.
     */
    public static Server getInstance()
    {
        if (instance == null)
        {
            instance = new Server();
        }

        return instance;
    }

    /**
     * Server initialization.
     */
    private void init()
    {
        // read the server configuration
        config.load();
        config.setRunning(true);

        // prepare the loggers
        LogManager.init();

        // display the systray icon (windows only)
        SystrayManager.open();

        // spawning persistent channels
        channelManager = ChannelManager.getInstance();
        channelManager.clear();

        for (ChannelConfig cc : config.getChannels())
        {
            cc.setPersistent(true);
            channelManager.createChannel(cc);
        }

        // start the client listeners
        for (Listener listener : config.getListeners())
        {
            if (listener.isAutoStart())
            {
                listener.start();
            }
        }

        // start the services
        for (Service service : config.getServices())
        {
            if (service.isAutoStart())
            {
                log.info("Starting service " + service.getName());
                service.start();
            }
        }

        // check the availability of a new release
        String latestVersion = getLatestVersion();
        if (latestVersion != null && ServerConfig.VERSION.compareTo(latestVersion) < 0)
        {
            log.warning("A new version is available (" + latestVersion + "), download it on http://jetrix.sf.net now!");
        }

        // start the server console
        console = new ConsoleClient();
        new Thread(console).start();

        log.info("Server ready!");
    }

    /**
     * Return the version of the latest release. The version of the last stable
     * release is stored on the Jetrix site (http://jetrix.sf.net/version.php),
     * this file is build dynamically everyday and reuse the version specified
     * in the project.properties file.
     */
    private String getLatestVersion()
    {
        String version = null;

        try
        {
            URL url = new URL("http://jetrix.sourceforge.net/version.php");

            HttpURLConnection conn = null;
            try
            {
                conn = (HttpURLConnection) url.openConnection();

                // read the first line of the file
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                version = in.readLine();
            }
            finally
            {
                conn.disconnect();
            }
        }
        catch (IOException e)
        {
            // too bad...
        }

        return version;
    }

    /**
     * Start the server.
     */
    public void start()
    {
        Thread server = new Thread(this);
        server.start();
    }

    /**
     * Stop the server.
     */
    protected void stop()
    {
        config.setRunning(false);

        // stop the listeners
        for (Listener listener : config.getListeners())
        {
            if (listener.isRunning())
            {
                listener.stop();
            }
        }

        // stop the services
        for (Service service : config.getServices())
        {
            if (service.isRunning())
            {
                service.stop();
            }
        }

        // disconnect all clients
        disconnectAll();

        // close the channels
        ChannelManager.getInstance().closeAll();

        // stop the server thread
        queue.add(new ShutdownMessage());
    }

    /**
     * Disconnect all clients from the server.
     */
    protected void disconnectAll()
    {
        ClientRepository repository = ClientRepository.getInstance();

        Iterator<Client> clients = repository.getClients();
        while (clients.hasNext())
        {
            clients.next().disconnect();
        }

        // disconnect the console client as well
        console.disconnect();
    }

    public void run()
    {
        init();

        while (config.isRunning())
        {
            try
            {
                // fetching next message waiting in the queue
                Message message = queue.take();

                log.finest("[server] processing " + message);

                // processing message

                if (message instanceof AddPlayerMessage)
                {
                    // looking for a channel with room left
                    Channel channel = channelManager.getOpenedChannel();

                    if (channel != null)
                    {
                        log.finest("[server] assigning client to channel " + channel);
                        channel.send(message);
                    }
                    else
                    {
                        // send server full message or create a new channel
                        log.finest("[server] no available channels!");
                    }
                }
                else if (message instanceof CommandMessage)
                {
                    CommandManager.getInstance().execute((CommandMessage) message);
                }
                else
                {
                    log.info("[server] Message not processed " + message);
                }

                /*switch(message.getCode())
                {
                    case Message.MSG_ADDPLAYER:
                    case Message.MSG_RESTART:
                    case Message.MSG_SHUTDOWN:
                        config.setRunning(false);
                        break;

                    case Message.MSG_UNKNOWN:
                    case Message.MSG_COMMAND:
                }*/
            }
            catch (InterruptedException e)
            {
                log.log(Level.WARNING, e.getMessage(), e);
            }
        }

        // remove the system tray icon
        SystrayManager.close();
    }

    /**
     * Add a message to the server message queue.
     */
    public void send(Message message)
    {
        queue.add(message);
    }

    /**
     * Return the server configuration.
     */
    public ServerConfig getConfig()
    {
        return config;
    }

    /**
     * Server entry point.
     *
     * @param args start parameters
     */
    public static void main(String[] args)
    {
        Server server = Server.getInstance();
        server.start();
    }

}
