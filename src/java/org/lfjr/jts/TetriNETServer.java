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
import java.util.*;
import org.lfjr.jts.config.*;

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

    private static final String VERSION = "0.0.8+";

    private TetriNETServer()
    {
        System.out.println("Jetrix TetriNET Server " + VERSION + ", Copyright (C) 2001-2002 Emmanuel Bourg\n");
        instance = this;

        // reading server configuration
        conf = new ServerConfig();
        conf.load();
        conf.setRunning(true);

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

        System.out.println("Server started...");
    }


    public void run()
    {
        while (conf.isRunning())
        {
            try
            {
                // fetching next message waiting in the queue
                Message m = mq.get();

                //System.out.println("Server: processing "+m);

                // processing message
                switch(m.getCode())
                {
                    case Message.MSG_ADDPLAYER:
                        // looking for a channel with room left
                        Channel ch = channelManager.getOpenedChannel();

                        if (ch!=null)
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
                        String cmd = m.getStringParameter(1);
                        TetriNETClient client = (TetriNETClient)m.getSource();

                        if ("/list".equalsIgnoreCase(cmd))
                        {
                            Message response = new Message(Message.MSG_PLINE);
                            Object params[] = { new Integer(0), ChatColors.darkBlue+"TetriNET Channel Lister - (Type "+ChatColors.red+"/join "+ChatColors.purple+"channelname"+ChatColors.darkBlue+")" };
                            response.setParameters(params);
                            client.sendMessage(response);

                            Iterator it = channelManager.channels();
                            int i=1;
                            while(it.hasNext())
                            {
                                Channel channel = (Channel)it.next();
                                ChannelConfig conf = channel.getConfig();

                                String cname = conf.getName();
                                while (cname.length()<6) cname += " ";

                                String message = ChatColors.darkBlue+"("+(client.getChannel().getConfig().getName().equals(conf.getName())?ChatColors.red:ChatColors.purple)+i+ChatColors.darkBlue+") " + ChatColors.purple + cname + "\t"
                                                 + (channel.isFull()?ChatColors.darkBlue+"["+ChatColors.red+"FULL"+ChatColors.darkBlue+"]       ":ChatColors.darkBlue+"["+ChatColors.aqua+"OPEN"+ChatColors.blue+"-" + channel.getNbPlayers() + "/"+conf.getMaxPlayers() + ChatColors.darkBlue + "]")
                                                 + (channel.getGameState()!=Channel.GAME_STATE_STOPPED?ChatColors.gray+" {INGAME} ":"                  ")
                                                 + ChatColors.black + conf.getDescription();

                                Message response2 = new Message(Message.MSG_PLINE);
                                Object params2[] = { new Integer(0), message };
                                response2.setParameters(params2);
                                client.sendMessage(response2);

                                i = i + 1;
                            }
                        }
                        else if ("/version".equalsIgnoreCase(cmd))
                        {
                            String version1 = ChatColors.darkBlue + "" + ChatColors.bold + "JetriX/" + VERSION;
                            String version2 = ChatColors.purple+"VM"+ChatColors.darkBlue+": " + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version") + " " + System.getProperty("java.vm.info");
                            String version3 = ChatColors.purple+"OS"+ChatColors.darkBlue+": " + System.getProperty("os.name") + " " + System.getProperty("os.version") +"; " + System.getProperty("os.arch");
                                                        
                            Message response1 = new Message(Message.MSG_PLINE, new Object[] { new Integer(0), version1 });
                            Message response2 = new Message(Message.MSG_PLINE, new Object[] { new Integer(0), version2 });
                            Message response3 = new Message(Message.MSG_PLINE, new Object[] { new Integer(0), version3 });
                            client.sendMessage(response1);
                            client.sendMessage(response2);
                            client.sendMessage(response3);
                        }                        
                        else if ("/who".equalsIgnoreCase(cmd))
                        {
                            Message response = new Message(Message.MSG_PLINE, new Object[] { new Integer(0), ChatColors.darkBlue+"/who is not implemented yet" });
                            client.sendMessage(response);
                        }
                        else if ("/op".equalsIgnoreCase(cmd))
                        {
                            Message response = new Message(Message.MSG_PLINE, new Object[] { new Integer(0), ChatColors.darkBlue+"/op is not implemented yet" });
                            client.sendMessage(response);
                        }
                        else
                        {
                            Message response = new Message(Message.MSG_PLINE, new Object[] { new Integer(0), ChatColors.red+"Invalid /COMMAND" });
                            client.sendMessage(response);
                        }
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