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
 * Game channel
 *
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class Channel extends Thread
{
    private ChannelConfig cconf;
    private ServerConfig conf;

    private MessageQueue mq;

    private boolean running = true;
    private boolean persistent = false;
    
    // game states
    private static final int GAME_STATE_STOPPED = 0;
    private static final int GAME_STATE_STARTED = 1;    
    private static final int GAME_STATE_PAUSED  = 2;

    private int gameState;

    // array of clients connected to this channel
    private TetriNETClient[] playerList = new TetriNETClient[6];
    
    private ArrayList filters;
    
    // JetriX logo
    private short jetrixLogo[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,1,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,1,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,1,1,0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,1,0,1,0,1,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,1,0};

    public Channel()
    {
        this(new ChannelConfig());        
    }

    public Channel(ChannelConfig cconf)
    {
        this.cconf = cconf;
        conf = ServerConfig.getInstance();
            
        // opening channel message queue
        mq = new MessageQueue();
        
        filters = new ArrayList();
    }

    public void run()
    {
        System.out.println("Channel "+cconf.getName()+" opened");

        while (running && conf.isRunning())
        {
            try
            {
                Message m = mq.get();
                int slot;
                
                System.out.println("Channel["+cconf.getName()+"]: processing "+m);
                
                    switch(m.getCode())
                    {
                        case Message.MSG_TEAM:
                            slot = ((Integer)m.getParameter(0)).intValue();                            
                            playerList[slot - 1].getPlayer().setTeam((String)m.getParameter(1));                                                      
                            sendAll(m, slot);                        
                            break;
                            
                        case Message.MSG_GMSG:
                            sendAll(m);
                            break;

                        case Message.MSG_PLINE:
                            slot = ((Integer)m.getParameter(0)).intValue();
                            String text = (String)m.getParameter(1);
                            if (!text.startsWith("/")) sendAll(m, slot);
                            break;
                                                        
                        case Message.MSG_PLINEACT:
                            slot = ((Integer)m.getParameter(0)).intValue();
                            sendAll(m, slot);
                            break;
                            
                        case Message.MSG_PAUSE:
                            gameState = GAME_STATE_PAUSED;
                            sendAll(m);
                            break;
                            
                        case Message.MSG_PLAYERLOST:
                            slot = ((Integer)m.getParameter(0)).intValue();
                            TetriNETClient client = (TetriNETClient)playerList[slot-1];
                            client.getPlayer().setPlaying(false);
                            sendAll(m);
                            
                            // sending closing screen
                            StringBuffer screenLayout = new StringBuffer();
                            for (int i=0; i<12*22; i++)
                            {
                            	screenLayout.append( ( (int)(Math.random()*4+1) ) * (1-jetrixLogo[i]) );
                            }
                            Message endingScreen = new Message(Message.MSG_FIELD);
                            Object paramsending[] = { m.getParameter(0), screenLayout.toString() };
                            endingScreen.setParameters(paramsending);
                            sendAll(endingScreen);
                            
                            break;
                            
                        case Message.MSG_SB:
                            slot = ((Integer)m.getParameter(2)).intValue();
                            sendAll(m, slot);
                            break;
                            
                        case Message.MSG_LVL:
                            // how does it work ?
                            break;
                            
                        case Message.MSG_FIELD:
                            slot = ((Integer)m.getParameter(0)).intValue();
                            //sendAll(m, slot);
                            sendAll(m);
                            break;
                            
                        case Message.MSG_STARTGAME:
                            Settings s = cconf.getSettings();
                            String raw = "newgame " + s.getStackHeight() + " " + s.getStartingLevel() + " " + s.getLinesPerLevel() + " " + s.getLevelIncrease() + " " + s.getLinesPerSpecial() + " " + s.getSpecialAdded() + " " + s.getSpecialCapacity() + " ";
                            for (int i = 0; i<7; i++)
                            {
                                for (int j = 0; j<s.getBlockOccurancy(i); j++) { raw = raw + (i + 1); }
                            }
                            
                            raw += " ";
                            
                            for (int i = 0; i<9; i++)
                            {
                                for (int j = 0; j<s.getSpecialOccurancy(i); j++) { raw = raw + (i + 1); }
                            }
                        
                            raw += " " + (s.getAverageLevels() ? "1" : "0") + " " + (s.getClassicRules() ? "1" : "0");
                                                                                    
                            gameState = GAME_STATE_STARTED;
                            for (int i=0; i<playerList.length; i++)
                            {
                                if(playerList[i]!=null)
                                {
                                    client = (TetriNETClient)playerList[i];
                                    client.getPlayer().setPlaying(true);
                                }
                            }                            
                            m.setRawMessage(raw);
                            sendAll(m);
                            break;          
                            
                        case Message.MSG_ENDGAME:
                            gameState = GAME_STATE_STOPPED;
                            sendAll(m);
                            break;    
                            
                        case Message.MSG_DISCONNECTED:
                            // searching player slot
                            client = (TetriNETClient)m.getParameter(0);
                            
                            slot=0;
                            int i = 0;
                            while(i<playerList.length && slot==0)
                            {
                                if (playerList[i] == client) { slot = i; }
                                i++;
                            }
                            
                            // removing player from channel members
                            playerList[slot] = null;
                            
                            // sending notification to players
                            m.setRawMessage("playerleave " + (slot+1));                            
                            sendAll(m);
                            break;
                            
                        case Message.MSG_ADDPLAYER:
                            client = (TetriNETClient)m.getParameter(0);
                            if (client.getChannel()==null)
                            {
                                // first channel assigned
                                client.assignChannel(this);
                                client.start();        
                            }
                            else
                            {
                                // leaving a previous channel
                                // ...
                            }
                            
                            // looking for the first free slot
                        for (slot=0; slot<6 && playerList[slot]!=null; slot++);

                        if (slot>=6)
                        {
                            System.out.println("Panic, no slot available");
                        }
                        else
                        {
                            playerList[slot]= client;
                            client.getPlayer().setPlaying(false);
                            
                            // sending new player notice to other players in the channel
                            Message mjoin = new Message(Message.MSG_PLAYERJOIN);
                            Object paramsjoin[] = { new Integer(slot+1), client.getPlayer().getName() };
                            mjoin.setParameters(paramsjoin);
                            sendAll(mjoin, slot+1);

                            // sending slot number to incomming player                            
                            Message mnum = new Message(Message.MSG_PLAYERNUM);
                            Object paramsnum[] = { new Integer(slot+1) };
                            mnum.setParameters(paramsnum);
                            client.sendMessage(mnum);
                                
                            // sending player and team list to incomming player
                            for (i=0; i<playerList.length; i++)
                            {
                                if (playerList[i]!=null && i!=slot)
                                {
                                    TetriNETClient resident = (TetriNETClient)playerList[i];
                                          
                                    // players...
                                    Message mjoin2 = new Message(Message.MSG_PLAYERJOIN);
                                    Object paramsjoin2[] = { new Integer(i+1), resident.getPlayer().getName() };
                                    mjoin2.setParameters(paramsjoin2);
                                    client.sendMessage(mjoin2);
                                            
                                    // ...and teams
                                    Message mteam = new Message(Message.MSG_TEAM);
                                    Object paramsteam[] = { new Integer(i+1), resident.getPlayer().getTeam() };
                                    mteam.setParameters(paramsteam);
                                    client.sendMessage(mteam);                                                             
                                }
                            }
                                                          
                            // sending welcome massage to incomming player
                            Message mwelcome = new Message(Message.MSG_PLINE);
                            Object paramswelcome[] = { new Integer(0), ChatColors.gray+"Hello "+client.getPlayer().getName()+", you are in channel " + cconf.getName() };
                            mwelcome.setParameters(paramswelcome);                                
                            client.sendMessage(mwelcome);
                            
                            // sending playerlost message if the game has started
                            if (gameState != GAME_STATE_STOPPED)
                            {
                            	System.out.println("blurp");
                                Message lost = new Message(Message.MSG_PLAYERLOST);
                                Object paramslost[] = { new Integer(slot+1) };
                                lost.setParameters(paramslost);
                                sendAll(lost);
                            }
                    }
                            
                    break;                                                                                                                     
                }         

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        
        System.out.println("Channel "+cconf.getName()+" closed");
    }

    /**
     * Add a message to the channel MessageQueue.
     *
     * @param m message to add
     */
    public void addMessage(Message m)
    {
        mq.put(m);
    }

    /**
     * Send a message to all players in this channel.
     *
     * @param m message to send     
     */
    protected void sendAll(Message m)
    {
        for (int i=0; i<playerList.length; i++)
        {
             TetriNETClient client = playerList[i];
             if (client != null) client.sendMessage(m);                          
        }             
    }

    /**
     * Send a message to all players but the one in the specified slot.
     *
     * @param m message to send
     * @param slot 
     */    
    protected void sendAll(Message m, int slot)
    {
        for (int i=0; i<playerList.length; i++)
        {
             if (i+1 != slot)
             {
                 TetriNETClient client = playerList[i];
                 if (client != null) client.sendMessage(m);                          
             }
        }                
    }

    /**
     * Tell if the channel can accept more players.
     *
     * @return <tt>true</tt> if the channel is full, <tt>false</tt> if not
     */
    public boolean isFull()
    {
        int count = 0;

        for (int i = 0; i<cconf.getMaxPlayers(); i++)
        {
            if (playerList[i++]!=null)
            {
                count++;
            }
        }

        return count>=cconf.getMaxPlayers();
    }

    /**
     * Tell if the channel will vanish once the last player leave*
     *
     * @return <tt>true</tt> if the channel is persistent, <tt>false</tt> if not
     */
    public boolean isPersistent()
    {
        return persistent;
    }
    
    public void setPersistent(boolean persistent)
    {
        this.persistent = persistent;
    }


    public void sortPlayers() {}


    public void swichPlayers(int i, int j) {}

}