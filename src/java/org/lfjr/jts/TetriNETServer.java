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
    private ServerConfig conf;
    private MessageQueue mq;
    private static TetriNETServer instance;
    
    private static final String VERSION = "0.0.8";
    
    private Vector channelList = new Vector();    

    public TetriNETServer()
    {
    	System.out.println("Jetrix TetriNET Server " + VERSION + ", Copyright (C) 2001 Emmanuel Bourg\n");
    	
    	// reading server configuration
    	conf = ServerConfig.getInstance();
    	conf.setRunning(true);
    	    	    	
    	// checking new release availability
    	// ....
    	
    	// spawning server message queue handler
    	mq = new MessageQueue();
    	Thread server = new Thread(this);
    	server.start();
    	
    	// spawning persistent channels
    	Iterator it = conf.getChannels();
    	
    	while(it.hasNext())
    	{
    	    ChannelConfig cc = (ChannelConfig)it.next();
    	    Channel ch = new Channel(cc);
    	    ch.setPersistent(true);
    	    ch.start();
    	    channelList.addElement(ch);
    	}
    	
    	// starting server console
        new ServerConsole();    	
    	
    	// starting client listener
    	ClientListener cl = new ClientListener();
    	cl.start();
    	
    	instance = this;
    	
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
            	
            	System.out.println("Server: processing "+m);
            	
            	// processing message
            	switch(m.getCode())
            	{
            	    case Message.MSG_ADDPLAYER:
            	        // looking for a channel with room left
            	        TetriNETClient client = (TetriNETClient)m.getParameter(0);
            	        Channel ch = null;
            	        Enumeration e = channelList.elements();
            	        while( e.hasMoreElements() && ch==null)
            	        {
            	            Channel ch2 = (Channel)e.nextElement();
            	            if (!ch2.isFull()) ch = ch2;
            	        }
            	               
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