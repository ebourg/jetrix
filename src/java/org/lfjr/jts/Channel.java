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

    private static final int GAME_STATE_STOPPED = 0;
    private static final int GAME_STATE_STARTED = 1;    
    private static final int GAME_STATE_PAUSED  = 2;

    private int gameState;

    private TetriNETClient[] listeJoueurs = new TetriNETClient[6];
    
    private ArrayList filters; 

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
            	        listeJoueurs[slot - 1].getPlayer().setTeam((String)m.getParameter(1));            	                    	      
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
            	        sendAll(m);
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
            	        sendAll(m, slot);
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
            	        m.setRawMessage(raw);
            	        sendAll(m);
            	        break;          
            	        
            	    case Message.MSG_ENDGAME:
            	        gameState = GAME_STATE_STOPPED;
            	        sendAll(m);
            	        break;    
            	        
            	    case Message.MSG_DISCONNECTED:
            	        // searching player slot
            	        TetriNETClient client = (TetriNETClient)m.getParameter(0);
            	        
            	        slot=0;
            	        int i = 0;
            	        while(i<listeJoueurs.length && slot==0)
            	        {
            	            if (listeJoueurs[i] == client) { slot = i; }
            	            i++;
            	        }
            	        
            	        listeJoueurs[slot] = null;
            	        m.setRawMessage("playerleave " + slot);
            	        
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
                        for (slot=0; slot<6 && listeJoueurs[slot]!=null; slot++);

                        if (slot>=6)
                        {
                            System.out.println("Panic, no slot available");
                        }
                        else
                        {
                            listeJoueurs[slot]= client;
                            
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
            	            
            	            // sending player list to incomming player
            	            /*Message mteam = new Message(Message.MSG_TEAM);
            	            Object paramsteam[] = { new Integer(slot+1), client.getPlayer().getTeam() };
            	            mteam.setParameters(paramsteam);*/            	            
            	            
            	            // sending team list to incomming player
            	                   
            	            // sending welcome massage to incomming player
            	            Message mwelcome = new Message(Message.MSG_PLINE);
            	            Object paramswelcome[] = { new Integer(0), ChatColors.gray+"Hello "+client.getPlayer().getName()+", you are in channel " + cconf.getName() };
            	            mwelcome.setParameters(paramswelcome);            	            
            	            client.sendMessage(mwelcome);            	            
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
        for (int i=0; i<listeJoueurs.length; i++)
        {
             TetriNETClient client = listeJoueurs[i];
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
        for (int i=0; i<listeJoueurs.length; i++)
        {
             if (i+1 != slot)
             {
                 TetriNETClient client = listeJoueurs[i];
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
            if (listeJoueurs[i++]!=null)
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

