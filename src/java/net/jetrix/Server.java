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

package net.jetrix;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;

import net.jetrix.clients.*;
import net.jetrix.config.*;
import net.jetrix.commands.*;
import net.jetrix.listeners.*;
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

    private ServerConfig conf;
    private MessageQueue mq;
    private ChannelManager channelManager;
    private Logger logger;

    private Server()
    {
        System.out.println("Jetrix TetriNET Server " + ServerConfig.VERSION + ", Copyright (C) 2001-2003 Emmanuel Bourg\n");
        instance = this;

        // reading server configuration
        conf = new ServerConfig();
        conf.load();
        conf.setRunning(true);

        // preparing logger
        prepareLoggers();

        // adding shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() { shutdown(); }
        });

        // checking new release availability
        // ....

        // spawning server message queue handler
        mq = new MessageQueue();
        Thread server = new Thread(this);
        server.start();

        // spawning persistent channels
        channelManager = ChannelManager.getInstance();
        Iterator it = conf.getChannels();

        while(it.hasNext())
        {
            ChannelConfig cc = (ChannelConfig)it.next();
            cc.setPersistent(true);
            channelManager.createChannel(cc);
        }

        // starting server console
        (new Thread(new ConsoleClient())).start();

        // starting client listeners
        (new Thread(new TetrinetListener())).start();

        logger.info("Server ready!");
    }

    private void prepareLoggers()
    {
        logger = Logger.getLogger("net.jetrix");
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        String debug = System.getProperty("jetrix.debug");
        if ( "true".equals(debug) ) { consoleHandler.setLevel( Level.ALL); }
        logger.addHandler(consoleHandler);
        consoleHandler.setFormatter(new Formatter() {
            Date dat = new Date();
            private final static String format = "HH:mm:ss";
            private SimpleDateFormat formatter;

            public synchronized String format(LogRecord record) {
                dat.setTime(record.getMillis());
                if (formatter == null) {
                    formatter = new SimpleDateFormat(format);
                }
                return "[" + formatter.format(dat) + "] ["
                    + record.getLevel().getLocalizedName() + "] "
                    + formatMessage(record) + "\n";
            }
        });

        try {
            FileHandler fileHandler = new FileHandler(conf.getAccessLogPath(), 1000000, 10);
            fileHandler.setLevel(Level.CONFIG);
            logger.addHandler(fileHandler);
            fileHandler.setFormatter(new Formatter() {
                Date dat = new Date();
                private final static String format = "yyyy-MM-dd HH:mm:ss";
                private SimpleDateFormat formatter;

                public synchronized String format(LogRecord record) {
                    dat.setTime(record.getMillis());
                    if (formatter == null) {
                        formatter = new SimpleDateFormat(format);
                    }
                    return "[" + formatter.format(dat) + "] "
                        + formatMessage(record) + "\n";
                }
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void run()
    {
        while (conf.isRunning())
        {
            try
            {
                // fetching next message waiting in the queue
                Message m = mq.get();

                logger.finest("Server: processing " + m);

                // processing message

                if ( m instanceof AddPlayerMessage)
                {
                    // looking for a channel with room left
                    Channel ch = channelManager.getOpenedChannel();

                    if (ch != null)
                    {
                        logger.finest("[server] assigning client to channel " + ch);
                        ch.sendMessage(m);
                    }
                    else
                    {
                        // send server full message or create a new channel
                        logger.finest("[server] no available channels!");
                    }
                }
                else if ( m instanceof CommandMessage)
                {
                    CommandManager.getInstance().execute((CommandMessage)m);

                }
                else
                {
                    logger.info("[server] Message not processed " +  m);
                }

                /*switch(m.getCode())
                {
                    case Message.MSG_ADDPLAYER:
                    case Message.MSG_RESTART:
                    case Message.MSG_SHUTDOWN:
                        conf.setRunning(false);
                        break;

                    case Message.MSG_UNKNOWN:
                    case Message.MSG_COMMAND:
                }*/
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Add a message to the server message queue.
     */
    public void sendMessage(Message m)
    {
        mq.put(m);
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

    public ServerConfig getConfig()
    {
        return conf;
    }

    /**
     * Shut the server down.
     */
    protected void shutdown()
    {
        conf.setRunning(false);
        disconnectAll();
    }

    /**
     * Disconnect all clients from the server.
     */
    protected void disconnectAll()
    {
        ClientRepository repository = ClientRepository.getInstance();

        Iterator clients = repository.getClients();
        while (clients.hasNext())
        {
            Client client = (Client)clients.next();
            client.disconnect();
        }
    }

    /**
     * Server entry point. All classes, jar and zip files in the lib
     * subdirectory are automatically added to the classpath.
     *
     * @param args start parameters
     */
    public static void main(String[] args) throws Exception
    {
        // build the list of JARs in the ./lib directory
        File repository = new File("lib/");
        List jars = new ArrayList();
        jars.add(repository.toURL());
        jars.add(new File("lang/").toURL());

        File[] files = repository.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            String filename = files[i].getAbsolutePath();
            if (filename.endsWith(".jar") || filename.endsWith(".zip"))
            {
                jars.add(files[i].toURL());
            }
        }

        URL[] urls = new URL[jars.size()];
        for (int i = 0; i < jars.size(); i++) urls[i] = (URL)jars.get(i);

        URLClassLoader loader = new URLClassLoader(urls, null);

        // start the server
        Class serverClass = loader.loadClass("net.jetrix.Server");
        serverClass.getMethod("getInstance", null).invoke(null, null);
    }

}
