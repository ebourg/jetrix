/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001  Emmanuel Bourg
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

    TetriNETClient[] listeJoueurs = new TetriNETClient[6];

    public Channel()
    {
        this(new ChannelConfig());
    }

    public Channel(ChannelConfig cconf)
    {
    	this.cconf = cconf;
    	
    	// opening channel message queue
        mq = new MessageQueue();
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
                
            	switch(m.getCode())
            	{
            	    case Message.MSG_TEAM:
            	        slot = ((Integer)m.getParameter(0)).intValue();            	        
            	        listeJoueurs[slot - 1].getPlayer().setTeam((String)m.getParameter(1));            	                    	      
            	        sendAll(m);            	    
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
            	        
            	        int slot=0;
            	        int i = 0;
            	        while(i<listeJoueurs.length && slot==0)
            	        {
            	            if (listeJoueurs[i] == client) { slot = i; }
            	            i++;
            	        }
            	        
            	        sendAll()
            	        break;        	          	                	                		            		
            	}         
            	       
                /*     
                else if ("disconnected".equals(cmd))
                {

                    int l1 = Integer.parseInt(stringtokenizer.nextToken());

                    //System.out.println("Player leaving " + l1);

                    if (l1>0 && l1<=MAX_PLAYER)
                    {
                        listeJoueurs.setElementAt(null, l1 - 1);
                    }

                    //System.out.println(listeJoueurs);

                    int l3 = 0;

                    while (l3<listeJoueurs.size())
                    {
                        TetriNETClient tetrinetclient7 = (TetriNETClient) listeJoueurs.elementAt(l3);

                        if (tetrinetclient7!=null)
                        {
                            tetrinetclient7.sendMessage("playerleave " + l1);
                        }

                        l3++;
                    }
                }                                                                                                            
                else if ("server".equals(cmd))
                {
                    // server command
                    String svrcmd = stringtokenizer.nextToken();

                    if ("addclient".equals(svrcmd))
                    {
                        System.out.println("new client, thanks !");

                        int i2;

                        for (i2 = 0; i2<6 && listeJoueurs.elementAt(i2)!=null; i2++);

                        if (i2>=6)
                        {
                            //System.out.println("arf, plus de place ! On fait quoi ? " + this);
                        }
                        else
                        {
                            TetriNETClient tetrinetclient2 = (TetriNETClient) incomingClients.pop();

                            listeJoueurs.setElementAt(tetrinetclient2, i2);
                            tetrinetclient2.sendMessage("pline 0 \022Hello " + tetrinetclient2.getNickname() + ", you are in channel " + this);
                            tetrinetclient2.sendMessage("playernum " + (i2 + 1));
                            tetrinetclient2.setSlot(i2 + 1);

                            int l4 = 0;

                            while (l4<listeJoueurs.size())
                            {
                                TetriNETClient tetrinetclient11 = (TetriNETClient) listeJoueurs.elementAt(l4);

                                if (tetrinetclient11!=null && l4!=i2)
                                {
                                    tetrinetclient11.sendMessage("playerjoin " + (i2 + 1) + " " + tetrinetclient2.getNickname());
                                    tetrinetclient2.sendMessage("playerjoin " + (l4 + 1) + " " + tetrinetclient11.getNickname());
                                    tetrinetclient2.sendMessage("team " + (l4 + 1) + " " + tetrinetclient11.getTeam());
                                }

                                l4++;
                            }
                        }
                    }
                }

                */

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
	
	System.out.println("Channel "+cconf.getName()+" closed");
    }


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

