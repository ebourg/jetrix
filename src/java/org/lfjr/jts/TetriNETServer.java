/**
 * Java TetriNET Server
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
import java.net.*;
import java.util.*;


/**
 * Serveur TetriNET. Démarre le serveur, l'écoute des connexions et la console serveur.
 *
 *
 * @author Emmanuel Bourg
 *
 */

public class TetriNETServer implements Runnable
{
    ServerInfo	      si;
    ServerSocket      s;
    Socket            socket;
    
    Vector channelList = new Vector();
    
    boolean running = true;

    TetriNETServer()
    {
    	System.out.println("Java TetriNET Server " + si.VERSION + ", Copyright (C) 2001 Emmanuel Bourg\n");
    	
    	// reading server configuration
    	// (replace with the new ServerConfig implementation when ready)
    	si = new ServerInfo();
    	
    	// spawning server message queue handler
    	// ...
    	
    	// spawning persistent channels
    	// ...
    	
    	// starting server console
        new ServerConsole(si);    	
    	
    	// starting client listener

    	Thread server = new Thread(this);
    	server.start();



        System.out.println("Server started...");
    }


    public void run()
    {
        try
        {
            s = new ServerSocket(si.LOGIN_PORT);
            //s.setSoTimeout(1000);

            //start();
        }
        catch (IOException e)
        {
            System.out.println("Cannot open ServerSocket");
            running = false;
            e.printStackTrace();
        }


        while (running)
        {
            try
            {
                // waiting for connexions
                socket = s.accept();

                //System.out.println("New client " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
                
                // validation du client
                
                TetriNETClient player = new TetriNETClient(socket, si);
                
		/*if (si.nbClient>=si.MAX_CLIENT)
		{
		    System.out.println("Server full, client rejected.");
		    sendMessage("noconnecting Server is full!");
		    socket.close();
		}
		else*/
		{
		si.playerList.addElement(this);
		si.incClient();
		
		//System.out.println("Client accepted, " + si.nbClient + " client(s) online.");
		initialiseConnexion(player);
                
                /** @todo tester l'unicité du pseudo sur le serveur */
                
                Message m = new Message();
                m.setCode(Message.MSG_PLINE);
                Object params[] = { new Integer(0), ChatColors.bold+"Welcome on Java TetriNET Server "+si.VERSION+" !" };
		player.sendMessage(m);
		
		// sending MOTD
		/*
		try
		{
		BufferedReader motd = new BufferedReader(new FileReader("game.motd"));
		
		String motdline = motd.readLine();
		while( motdline != null )
		{
		    player.sendMessage("pline 0 " + ChatColors.gray + motdline);
		    motdline = motd.readLine();
		}
		motd.close();
	 	}
		catch (FileNotFoundException e) 
		{
		    player.sendMessage("pline 0 MOTD missing");
		}
		*/
		
		
		// assignation dans un channel
		
		/* Cherche un channel avec de la place ou en crée un  */
		
		Channel ch = null;
		
		Enumeration e = channelList.elements();
		
		while( e.hasMoreElements() && ch==null)
                {
         	    Channel ch2 = (Channel)e.nextElement();
         	    
         	    if (!ch2.isFull()) ch = ch2;         	    
         	}
 		
		if (ch==null) ch = new Channel();
		
		channelList.addElement(ch);
		
		ch.addClient(player);
		player.assignChannel(ch);
		player.start();
		
		m = new Message();
		m.setCode(Message.MSG_ADDPLAYER);
		Object[] params2 = { player };	
		m.setParameters(params2);
		ch.addMessage(m);	
		
		              
		}                
                		                                                
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    /**
     * Point d'entrée du serveur.
     *
     *
     * @param args Arguments de démarrage du serveur.
     *
     */

    public static void main(String[] args)
    {
    	new TetriNETServer();
    }


    private void initialiseConnexion(TetriNETClient player) throws UnknownEncryptionException
    {
        String init="", dec;
        Vector tokens = new Vector();
	
	try {
        init = player.readLine();
	}
	catch (IOException e) { e.printStackTrace(); }

        dec = decode(init);

        //System.out.println(dec);

        // init string parsing "tetrisstart <nickname> <version>"
        StringTokenizer st = new StringTokenizer(dec, " ");

        while (st.hasMoreTokens())
        {
            tokens.addElement(st.nextToken());
        }

	/*
        for (int i = 0; i<tokens.size(); i++)
        {
            System.out.println(tokens.elementAt(i));
        }*/

        if (tokens.size()>3)
        {
            Message m = new Message();
            m.setCode(Message.MSG_NOCONNECTING);
            Object[] params = { "No space allowed in nickname !" };
            m.setParameters(params);
            player.sendMessage(m);
        }
                
        player.setNickname((String)tokens.elementAt(1));
        player.setClientVersion((String)tokens.elementAt(2));
    }



    /**
     * Décode la chaîne d'initialisation du client TetriNET.
     *
     *
     * @param initString Chaîne d'initialisation
     *
     * @return Chaîne décodée
     *
     * @throws UnknownEncryptionException
     *
     * @see
     */
    public String decode(String initString) throws UnknownEncryptionException
    {
        if (initString.length() % 2 != 0)
        {
            throw new UnknownEncryptionException("Invalid Init String: odd length");
        }

        int[] dec = new int[initString.length() / 2];

        try
        {
            for (int i = 0; i<dec.length; i++)
            {
                dec[i] = Integer.parseInt(initString.substring(i * 2, i * 2 + 2), 16);
            }
        }
        catch (NumberFormatException e)
        {
            throw new UnknownEncryptionException("Invalid Init String: illegal characters");
        }

        char[] data = "tetrisstar".toCharArray();
        int[]  hashString = new int[data.length];

        for (int i = 0; i<data.length; i++)
        {
            hashString[i] = ((data[i] + dec[i]) % 255) ^ dec[i + 1];
        }

        int hashLength = 5;

        for (int i = 5; i==hashLength && i>0; i--)
        {
            for (int j = 0; j<data.length - hashLength; j++)
            {
                if (hashString[j] != hashString[j + hashLength]) { hashLength--; }
            }
        }

        if (hashLength==0)
        {
            throw new UnknownEncryptionException("Invalid Init String: decoding failed");
        }

        String s = "";

        for (int i = 1; i<dec.length; i++)
        {
            s += (char)(((dec[i] ^ hashString[(i - 1) % hashLength]) + 255 - dec[i - 1]) % 255);
        }

        return s.replace((char)0, (char)255);
    }


}

























