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

package org.lfjr.jts;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;
import org.lfjr.jts.config.*;
import org.lfjr.jts.commands.*;

/**
 * Main class, starts server components.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TetriNETServer implements Runnable
{
    private static TetriNETServer instance;

    private ServerConfig conf;
    private MessageQueue mq;
    private ChannelManager channelManager;
    private Logger logger;

    private TetriNETServer()
    {
        System.out.println("Jetrix TetriNET Server " + ServerConfig.VERSION + ", Copyright (C) 2001-2002 Emmanuel Bourg\n");
        instance = this;

        // reading server configuration
        conf = new ServerConfig();
        conf.load();
        conf.setRunning(true);
        
        // loading localized strings
        //Language.load(new Locale("en"));

        // preparing logger        
        logger = Logger.getLogger("net.jetrix");
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.INFO);
        
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
                StringBuffer text = new StringBuffer();
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
                    StringBuffer text = new StringBuffer();
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
        
        // adding shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("bye bye");
            }
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
        new ServerConsole();

        // starting client listener
        ClientListener cl = new ClientListener();
        cl.start();

        logger.info("Server started...");
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
                switch(m.getCode())
                {
                    case Message.MSG_ADDPLAYER:
                        // looking for a channel with room left
                        Channel ch = channelManager.getOpenedChannel();

                        if (ch != null)
                        {
                            ch.addMessage(m);
                        }
                        else
                        {
                            // send server full message or create a new channel
                        }
                        break;

                    case Message.MSG_RESTART:
                        break;

                    case Message.MSG_SHUTDOWN:
                        conf.setRunning(false);
                        break;

                    case Message.MSG_UNKNOWN:
                        break;

                    case Message.MSG_SLASHCMD:
                        CommandManager.getInstance().execute(m);
                        break;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Add a message to the server message queue.
     *
     * @param args Arguments de démarrage du serveur.
     */
    protected void addMessage(Message m)
    {
        mq.put(m);
    }

    public static TetriNETServer getInstance()
    {
        return instance;
    }

    public ServerConfig getConfig()
    {
        return conf;
    }

    /**
     * Server entry point.
     *
     * @param args start parameters
     */
    public static void main(String[] args)
    {
        new TetriNETServer();
    }

}
