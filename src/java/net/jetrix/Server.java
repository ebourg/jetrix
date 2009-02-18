/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2009  Emmanuel Bourg
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
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

import net.jetrix.clients.*;
import net.jetrix.commands.*;
import net.jetrix.config.*;
import net.jetrix.messages.*;
import net.jetrix.messages.channel.*;
import net.jetrix.services.VersionService;
import net.jetrix.listeners.ShutdownListener;

/**
 * Main class, starts the server components and handle the server level messages.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class Server implements Runnable, Destination
{
    private static Server instance;

    private Logger log = Logger.getLogger("net.jetrix");

    private File configFile;
    private ServerConfig config;
    private BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
    private ChannelManager channelManager;
    private Client console;

    private Server()
    {
        // add the stop hook
        Thread hook = new Thread("StopHook")
        {
            public void run()
            {
                if (config != null && config.isRunning())
                {
                    log.info("Shutdown command received from the system");
                    instance.stop();
                }
            }
        };
        Runtime.getRuntime().addShutdownHook(hook);

        try
        {
            SystemSignal.handle("INT", hook);
            SystemSignal.handle("TERM", hook);
        }
        catch (Throwable e)
        {
            log.warning("Unable to hook the system signals: " + e.getMessage());
        }
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
        config = new ServerConfig();
        config.load(configFile);
        config.setRunning(true);

        // prepare the loggers
        LogManager.init();
        
        // open the database connections
        if (!config.getDataSources().isEmpty())
        {
            log.info("Initializing the datasources...");
            for (DataSourceConfig datasource : config.getDataSources())
            {
                DataSourceManager.getInstance().setDataSource(datasource, datasource.getName());
            }
        }

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

        // start the shutdown listener
        new ShutdownListener().start();

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
        VersionService.updateLatestVersion();
        if (VersionService.isNewVersionAvailable())
        {
            log.warning("A new version is available (" + VersionService.getLatestVersion() + "), download it on http://jetrix.sf.net now!");
        }

        // start the server console
        console = new ConsoleClient();
        new Thread(console).start();

        log.info("Server ready!");
    }

    /**
     * Start the server.
     */
    public void start()
    {
        Thread server = new Thread(this, "server");
        server.start();
    }

    /**
     * Stop the server.
     */
    public void stop()
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
        send(new ShutdownMessage());
    }

    /**
     * Disconnect all clients from the server.
     */
    private void disconnectAll()
    {
        ClientRepository repository = ClientRepository.getInstance();

        for (Client client : repository.getClients())
        {
            client.disconnect();
        }

        // disconnect the console client as well
        if (console != null)
        {
            console.disconnect();
        }
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

                if (log.isLoggable(Level.FINEST))
                {
                    log.finest("[server] processing " + message);
                }

                // processing message

                if (message instanceof AddPlayerMessage)
                {
                    // looking for a channel with room left
                    int level = ((AddPlayerMessage) message).getClient().getUser().getAccessLevel();
                    Channel channel = channelManager.getHomeChannel(level);

                    if (channel != null)
                    {
                        if (log.isLoggable(Level.FINEST))
                        {
                            log.finest("[server] assigning client to channel " + channel);
                        }
                        channel.send(message);
                    }
                    else
                    {
                        // send server full message or create a new channel
                        if (log.isLoggable(Level.FINEST))
                        {
                            log.finest("[server] no available channels!");
                        }
                    }
                }
                else if (message instanceof CommandMessage)
                {
                    CommandManager.getInstance().execute((CommandMessage) message);
                }
                else if (!(message instanceof ShutdownMessage))
                {
                    log.info("[server] Message not processed " + message);
                }
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
     * Set the server configuration file.
     */
    public void setConfigFile(File configFile)
    {
        this.configFile = configFile;
    }

    /**
     * Server entry point.
     *
     * @param args start parameters
     */
    public static void main(String[] args)
    {
        System.out.println("Jetrix TetriNET Server " + ServerConfig.VERSION + ", Copyright (C) 2001-2009 Emmanuel Bourg\n");

        Server server = Server.getInstance();

        List<String> params = Arrays.asList(args);

        // read the path of the server configuration file
        int p = params.indexOf("--conf");
        if (p != -1 && p + 1 < params.size())
        {
            server.setConfigFile(new File(params.get(p + 1)));
        }
        else
        {
            server.setConfigFile(new File("conf/server.xml"));
        }

        server.start();
    }

}
