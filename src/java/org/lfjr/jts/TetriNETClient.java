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
import org.lfjr.jts.config.*;

/**
 * Thread assurant la communication avec un client TetriNET.
 *
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
class TetriNETClient extends Thread
{
    public Socket          socket;
    private BufferedReader in;
    private BufferedWriter out;
    ServerConfig conf;

    private String clientVersion;
    
    private int slot;
    
    Channel channel;
    TetriNETServer server;
    TetriNETPlayer player;
    

    /**
     * Constructor declaration
     *
     *
     * @param s Socket de connexion du client.
     * @param sinfo Objet contenant les informations partagées du serveur.
     *
     * @see
     */
    public TetriNETClient(TetriNETPlayer player) throws IOException
    {
    	this.player = player;
        conf = ServerConfig.getInstance();
        socket = player.getSocket();

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));             
    }


    public void run()
    {
        try
        {           
	    // lecture en boucle des commandes et forward au channel
             
	    String s;	
	    Message m;
	    	
            while (true)
            {
            	s = readLine();
            	m = new Message();
            	m.setRawMessage(s);
            	//System.out.println(s);
            	StringTokenizer st = new StringTokenizer(s, " ");    	

                String cmd = st.nextToken();
                
                // team <slot> teamname
                if ("team".equals(cmd))
                {
                    m.setType(Message.TYPE_CHANNEL);
                    m.setCode(Message.MSG_TEAM);
                                        
                    Integer slot = new Integer(st.nextToken());
                    String teamname = st.hasMoreTokens()?st.nextToken():"";                    
                    Object[] params = { slot, teamname };
                    
                    m.setParameters(params);
                }       
                /*         
                else if ("pline".equals(cmd))
                {
                    int    j = Integer.parseInt(st.nextToken());
                    String s4 = st.nextToken();

                    if (!s4.startsWith("/"))
                    {
                        int j4 = 0;

                        while (j4<listeJoueurs.size())
                        {
                            TetriNETClient tetrinetclient9 = (TetriNETClient) listeJoueurs.elementAt(j4);

                            if (tetrinetclient9!=null && j4 + 1!=j)
                            {
                                tetrinetclient9.sendMessage("pline " + j + " " + ChatColors.slots[j4] + message.substring(8));
                            }

                            j4++;
                        }
                    }
                }
                // gmsg <playername> message
                else if ("gmsg".equals(cmd))
                {
                    //int k = Integer.parseInt(st.nextToken());
                    int j2 = 0;

                    while (j2<listeJoueurs.size())
                    {
                        TetriNETClient tetrinetclient3 = (TetriNETClient) listeJoueurs.elementAt(j2);

                        if (tetrinetclient3!=null)
                        {
                            tetrinetclient3.sendMessage(message);
                        }

                        j2++;
                    }
                }
                else if ("plineact".equals(cmd))
                {
                    int l = Integer.parseInt(st.nextToken());
                    int k2 = 0;

                    while (k2<listeJoueurs.size())
                    {
                        TetriNETClient tetrinetclient4 = (TetriNETClient) listeJoueurs.elementAt(k2);

                        if (tetrinetclient4!=null && k2 + 1!=l)
                        {
                            tetrinetclient4.sendMessage(message);
                        }

                        k2++;
                    }
                }
                // startgame <0 = stop | 1 = start> <playernumber>
                else if ("startgame".equals(cmd))
                {
                    boolean flag = "1".equals(st.nextToken());
                    int     l2 = Integer.parseInt(st.nextToken());

                    if (flag)
                    {
                        String s5 = "newgame " + stackHeight + " " + startingLevel + " " + linesForLevel + " " + levelIncrease + " " + linesForSpecial + " " + specialsAdded + " " + capacity + " ";

                        for (int i5 = 0; i5<pieceOccurancy.length; i5++)
                        {
                            for (int l5 = 0; l5<pieceOccurancy[i5]; l5++)
                            {
                                s5 = s5 + (i5 + 1);
                            }
                        }

                        s5 = s5 + " ";

                        for (int j5 = 0; j5<specialOccurancy.length; j5++)
                        {
                            for (int i6 = 0; i6<specialOccurancy[j5]; i6++)
                            {
                                s5 = s5 + (j5 + 1);
                            }

                        }

                        s5 = s5 + " " + (averagedLevels ? "1" : "0") + " " + (classicRules ? "1" : "0");
                        gameState = GAME_STATE_STARTED;

                        int k5 = 0;

                        while (k5<listeJoueurs.size())
                        {
                            TetriNETClient tetrinetclient12 = (TetriNETClient) listeJoueurs.elementAt(k5);

                            if (tetrinetclient12!=null)
                            {
                                tetrinetclient12.sendMessage(s5);
                            }

                            k5++;
                        }
                    }
                    else
                    {
                        gameState = GAME_STATE_STOPPED;

                        int k4 = 0;

                        while (k4<listeJoueurs.size())
                        {
                            TetriNETClient tetrinetclient10 = (TetriNETClient) listeJoueurs.elementAt(k4);

                            if (tetrinetclient10!=null)
                            {
                                tetrinetclient10.sendMessage("endgame");
                            }

                            k4++;
                        }
                    }
                }
                else if ("pause".equals(cmd))
                {
                    gameState = GAME_STATE_PAUSED;

                    int i1 = 0;

                    while (i1<listeJoueurs.size())
                    {
                        TetriNETClient tetrinetclient1 = (TetriNETClient) listeJoueurs.elementAt(i1);

                        if (tetrinetclient1!=null)
                        {
                            tetrinetclient1.sendMessage("pause " + st.nextToken());
                        }

                        i1++;
                    }
                }
                else if ("playerlost".equals(cmd))
                {
                    int i3 = 0;

                    while (i3<listeJoueurs.size())
                    {
                        TetriNETClient tetrinetclient = (TetriNETClient) listeJoueurs.elementAt(i3);

                        if (tetrinetclient!=null)
                        {
                            tetrinetclient.sendMessage(message);
                        }

                        i3++;
                    }
                }
                else if ("lvl".equals(cmd)) {}
                else if ("sb".equals(cmd))
                {                	
                    int to = Integer.parseInt(st.nextToken());
                    String bonus = st.nextToken();
                    int from = Integer.parseInt(st.nextToken());   
                    
                    //System.out.println(message+" ("+to+":"+bonus+":"+from+")");                 
                    
                    int j3 = 1;

                    while (j3<=listeJoueurs.size())
                    {
                        TetriNETClient client = (TetriNETClient) listeJoueurs.elementAt(j3-1);

                        if (client!=null && (j3 != from)) { client.sendMessage(message); }

                        j3++;
                    }
                }
                else if ("f".equals(cmd))
                {

                    int k1 = Integer.parseInt(st.nextToken());
                    int k3 = 0;

                    while (k3<listeJoueurs.size())
                    {
                        TetriNETClient tetrinetclient6 = (TetriNETClient) listeJoueurs.elementAt(k3);

                        if (tetrinetclient6!=null && k3 + 1!=k1)
                        {
                            tetrinetclient6.sendMessage(message);
                        }

                        k3++;
                    }
                }
                
                */
                

        
                                                
                channel.addMessage(m);                                    
            }
        }
        catch (IOException e)
        {        
            Message m = new Message();
            m.setCode(Message.MSG_DISCONNECTED);
            Object[] params = { new Integer(slot) };
            channel.addMessage(m);
        }
        finally
        {
            try
            {
            	in.close();
            }
            catch (IOException e) { e.printStackTrace(); }
            
            try
            {
            	out.close();
            }
            catch (IOException e) { e.printStackTrace(); }
            
            try
            {
            	socket.close(); 
            }
            catch (IOException e) { e.printStackTrace(); }                        
            
            System.out.println(this+" leaved");

            //si.decClient();
            //si.playerList.removeElement(this);
        }
    }


    public BufferedWriter getOutputWriter()
    {
        return out;
    }


    public void sendMessage(Message m)
    {
    	String s = m.getRawMessage();
    	
    	try 
    	{
    	    //synchronized(out)
    	    {
                out.write(s + (char)255, 0, s.length() + 1);
                out.flush();
            }
            
            //System.out.println(">"+s);
	}
	catch (SocketException e) { System.out.println(e.getMessage()); }
	catch (Exception e) { /*e.printStackTrace();*/ }
    }


    String readLine() throws IOException
    {
	int    readChar;
	String input = "";
	
	while ((readChar = in.read())!=-1 && readChar!=255)
	{
	    if (readChar!=10 && readChar!=13)
	    {
	        input += (char) readChar;
	    }
	}

	if (readChar==-1) throw new IOException("client disconnected");
	
	return input;
    }

    public void assignChannel(Channel ch)
    {
        channel = ch;	
    	
    }


    public TetriNETPlayer getPlayer()
    {
    	return player;
    }

    public void setClientVersion(String v)
    {
    	clientVersion = v;
    }    

    public void setSlot(int s)
    {
    	slot = s;
    }

    public int getSlot()
    {
    	return slot;
    }

    public String toString()
    {
        return "[Player "+player.getName()+" <"+player.getTeam()+">]";
    }

}

