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
import java.net.*;
import java.util.*;
import java.util.logging.*;
import org.lfjr.jts.config.*;

/**
 * Listens for incomming connexion.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ClientListener extends Thread
{
    private ServerConfig serverConfig;

    private ServerSocket serverSocket;
    private Socket socket;
    private Logger logger;

    public ClientListener()
    {
        serverConfig = TetriNETServer.getInstance().getConfig();
    }

    public void run()
    {
        logger = Logger.getLogger("net.jetrix");
        
        try
        {
            serverSocket = new ServerSocket(serverConfig.getPort(), 50, serverConfig.getHost());
            logger.info("Listening at tetrinet port " + serverConfig.getPort() 
                + ( (serverConfig.getHost() != null)?", bound to " + serverConfig.getHost():"") );
        }
        catch (IOException e)
        {
            logger.severe("Cannot open ServerSocket");
            serverConfig.setRunning(false);
            e.printStackTrace();
        }

        while (serverConfig.isRunning())
        {
            try
            {
                // waiting for connexions
                socket = serverSocket.accept();

                // logging connexion
                // ....
                logger.info("Incoming player: " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());

                // checking if server is full
                // ....
                /*if (si.nbClient>=si.MAX_CLIENT)
                {
                    System.out.println("Server full, client rejected.");
                    sendMessage("noconnecting Server is full!");
                    socket.close();
                }
                else continue*/

                // validating client
                TetriNETPlayer player = new TetriNETPlayer();
                TetriNETClient client = new TetriNETClient(player, socket);
                initializeConnexion(client);

                //si.playerList.addElement(client);
                //si.incClient();

                // testing name unicity
                // ....

                // testing concurrent connexions from the same host
                // ....

                // testing ban list
                // ....

                //System.out.println("Client accepted, " + si.nbClient + " client(s) online.");

                /*Message m = new Message(Message.MSG_PLINE);
                Object params[] = { new Integer(0), ChatColors.bold+"Welcome to Jetrix TetriNET Server "+ServerConfig.VERSION+" !" };
                m.setParameters(params);
                client.sendMessage(m);*/

                // sending message of the day
                BufferedReader motd = new BufferedReader(new StringReader( serverConfig.getMessageOfTheDay() ));
                String motdline;
                while( (motdline = motd.readLine() ) != null )
                {
                    Message m = new Message(Message.MSG_PLINE);
                    Object params2[] = { new Integer(0), ChatColors.gray + motdline };
                    m.setParameters(params2);
                    client.sendMessage(m);
                }
                motd.close();


                // channel assignation
                // (looks for a default channel with room left, creates one if needed)
                // ....

                /*
                Channel ch = null;

                Enumeration e = channelList.elements();

                while( e.hasMoreElements() && ch==null)
                {
                    Channel ch2 = (Channel)e.nextElement();
                    if (!ch2.isFull()) ch = ch2;
                }

                if (ch==null)
                {
                    ch = new Channel();
                    ch.start();
                }

                channelList.addElement(ch);

                //ch.addClient(client);
                client.assignChannel(ch);
                client.start();*/

                // forwarding client to server for channel assignation
                Message m = new Message(Message.MSG_ADDPLAYER);
                Object[] params2 = { client };
                m.setParameters(params2);
                TetriNETServer.getInstance().addMessage(m);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    private void initializeConnexion(TetriNETClient client) throws UnknownEncryptionException
    {
        String init="", dec;
        Vector tokens = new Vector();

        try
        {
            init = client.readLine();
        }
        catch (IOException e) { e.printStackTrace(); }

        dec = decode(init);

        // init string parsing "tetrisstart <nickname> <version>"
        StringTokenizer st = new StringTokenizer(dec, " ");

        while (st.hasMoreTokens())
        {
            tokens.addElement(st.nextToken());
        }

        if (tokens.size()>3)
        {
            Message m = new Message(Message.MSG_NOCONNECTING);
            m.setParameters(new Object[] { "No space allowed in nickname !" });
            client.sendMessage(m);
        }

        client.getPlayer().setName((String)tokens.elementAt(1));
        client.setClientVersion((String)tokens.elementAt(2));
    }


    /**
     * Decodes TetriNET client initialization string
     *
     * @param initString initialization string
     *
     * @return decoded string
     *
     * @throws UnknownEncryptionException
     */
    protected String decode(String initString) throws UnknownEncryptionException
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
