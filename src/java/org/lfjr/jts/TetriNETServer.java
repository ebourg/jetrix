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
 * Main class, starts server components.
 *
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TetriNETServer implements Runnable
{
    ServerConfig conf;
    MessageQueue mq;
    
    Vector channelList = new Vector();    

    public TetriNETServer()
    {
    	System.out.println("Jetrix TetriNET Server " + ServerConfig.VERSION + ", Copyright (C) 2001 Emmanuel Bourg\n");
    	
    	// reading server configuration
    	conf = ServerConfig.getInstance();
    	conf.setRunning(true);
    	
    	// spawning server message queue handler
    	mq = new MessageQueue();
    	Thread server = new Thread(this);
    	server.start();    	
    	
    	// spawning persistent channels
    	Iterator it = conf.getChannels();
    	
    	while(it.hasNext())
    	{
    	    ChannelConfig cc = (ChannelConfig)	it.next();
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
    	
        System.out.println("Server started...");
    }


    public void run()
    {
        while (conf.isRunning())
        {
            //try
            {
            	Message m = mq.get();
            }            
            //catch (IOException e)
            {
                //e.printStackTrace();
            }
        }
    }


    /**
     * Add a message to the server message queue.
     *
     *
     * @param args Arguments de démarrage du serveur.
     */
    protected void addMessage(Message m)
    {
        mq.put(m);
    }

    
    /**
     * Server entry point.
     *
     *
     * @param args start parameters
     */
    public static void main(String[] args)
    {
    	new TetriNETServer();
    }


}

























