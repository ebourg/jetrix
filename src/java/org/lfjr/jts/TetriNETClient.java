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
    public Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private ServerConfig conf;

    private String clientVersion;

    //private int slot;

    private Channel channel;
    private TetriNETServer server;
    private TetriNETPlayer player;

    private boolean running;


    public TetriNETClient(TetriNETPlayer player) throws IOException
    {
    	this.player = player;
        conf = ServerConfig.getInstance();
        socket = player.getSocket();

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        running = true;
    }


    public void run()
    {
    	System.out.println("Client started "+this);
    	
        try
        {
	    String s;
	    Message m;

            while ( running && conf.isRunning() )
            {
            	// reading raw message from socket
            	s = readLine();
            	StringTokenizer st = new StringTokenizer(s, " ");
            	String cmd = st.nextToken();

            	try
            	{

            	// building server message
            	m = new Message();
            	m.setRawMessage(s);

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
                // pline <slot> text
                else if ("pline".equals(cmd))
                {
                    m.setType(Message.TYPE_CHANNEL);
                    m.setCode(Message.MSG_PLINE);

                    Integer slot = new Integer(st.nextToken());
                    String text = st.nextToken();
                    Object[] params = { slot, text };
                    m.setParameters(params);
                }
                // gmsg playername+text
                else if ("gmsg".equals(cmd))
                {
                    m.setType(Message.TYPE_CHANNEL);
                    m.setCode(Message.MSG_GMSG);

                    String text = st.nextToken();
                    Object[] params = { text };
                    m.setParameters(params);
                }
                // plineact <slot> emote
                else if ("plineact".equals(cmd))
                {
                    m.setType(Message.TYPE_CHANNEL);
                    m.setCode(Message.MSG_PLINEACT);

                    Integer slot = new Integer(st.nextToken());
                    String text = st.nextToken();
                    Object[] params = { slot, text };
                    m.setParameters(params);
                }
                // startgame <0 = stop | 1 = start> <playernumber>
                else if ("startgame".equals(cmd))
                {
                    m.setType(Message.TYPE_CHANNEL);
                    m.setCode("1".equals(st.nextToken())?Message.MSG_STARTGAME:Message.MSG_ENDGAME);
                    if (Message.MSG_ENDGAME == m.getCode()) m.setRawMessage("endgame");

                    Integer slot = new Integer(st.nextToken());
                    Object[] params = { slot };
                    m.setParameters(params);
                }
                // pause <slot>
                else if ("pause".equals(cmd))
                {
                    m.setType(Message.TYPE_CHANNEL);
                    m.setCode(Message.MSG_PAUSE);

                    Integer slot = new Integer(st.nextToken());
                    Object[] params = { slot };
                    m.setParameters(params);
                }
                // playerlost <slot>
                else if ("playerlost".equals(cmd))
                {
                    m.setType(Message.TYPE_CHANNEL);
                    m.setCode(Message.MSG_PLAYERLOST);

                    Integer slot = new Integer(st.nextToken());
                    Object[] params = { slot };
                    m.setParameters(params);
                }
                // lvl <slot> <level> (not sure on this one)
                else if ("lvl".equals(cmd))
                {
                    m.setType(Message.TYPE_CHANNEL);
                    m.setCode(Message.MSG_LVL);

                    Integer slot = new Integer(st.nextToken());
                    Integer level = new Integer(st.nextToken());
                    Object[] params = { slot, level };
                    m.setParameters(params);
                }
                // sb <to> <bonus> <from>
                else if ("sb".equals(cmd))
                {
                    m.setType(Message.TYPE_CHANNEL);
                    m.setCode(Message.MSG_SB);

                    Integer to   = new Integer(st.nextToken());
                    String bonus = st.nextToken();
                    Integer from = new Integer(st.nextToken());
                    Object[] params = { to, bonus, from };
                    m.setParameters(params);
                }
                // f <slot> <field>
                else if ("f".equals(cmd))
                {
                    m.setType(Message.TYPE_CHANNEL);
                    m.setCode(Message.MSG_LVL);

                    Integer slot = new Integer(st.nextToken());
                    String field = st.nextToken();
                    Object[] params = { slot, field };
                    m.setParameters(params);
                }

                channel.addMessage(m);

        	}
		catch (NumberFormatException e)
		{
		    System.out.println("Bad format message");
		    e.printStackTrace();
		}
            } // end while
        }
        catch (IOException e)
        {
            Message m = new Message();
            m.setCode(Message.MSG_DISCONNECTED);
            Object[] params = { this };
            channel.addMessage(m);
        }
        finally
        {
            try { in.close(); }     catch (IOException e) { e.printStackTrace(); }
            try { out.close(); }    catch (IOException e) { e.printStackTrace(); }
            try { socket.close(); } catch (IOException e) { e.printStackTrace(); }
        }
    }


    public BufferedWriter getOutputWriter()
    {
        return out;
    }


    /**
     * Send message to client. The raw message property must be set.
     *
     * @param m message to send
     */     
    public void sendMessage(Message m)
    {
    	if (m.getRawMessage() != null)
    	{

    	try
    	{
    	    synchronized(out)
    	    {
                out.write(m.getRawMessage() + (char)255, 0, m.getRawMessage().length() + 1);
                out.flush();                
            }

            System.out.println("> "+m.getRawMessage());
	}
	catch (SocketException e) { System.out.println(e.getMessage()); }
	catch (Exception e) { /*e.printStackTrace();*/ }
	
        }
        else { System.out.println("Message not sent, raw message missing "+m); }
    }


    /**
     * Read a line sent by the tetrinet client.
     *
     * @return line sent
     */
    protected String readLine() throws IOException
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
    
    public Channel getChannel()
    {
        return channel;	
    }


    public TetriNETPlayer getPlayer()
    {
    	return player;
    }

    public void setClientVersion(String v)
    {
    	clientVersion = v;
    }
/*
    public void setSlot(int s)
    {
    	slot = s;
    }

    public int getSlot()
    {
    	return slot;
    }
*/
    public void disconnect()
    {
        running = false;
    }

    public String toString()
    {
        return "[Player "+player.getName()+" <"+player.getTeam()+">]";
    }

}

