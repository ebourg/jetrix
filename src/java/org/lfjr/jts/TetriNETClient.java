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
 * Layer handling communication with a client. Incomming messages are turned
 * into a server understandable format and forwarded to the apropriate
 * destination for processing (player's channel or main server thread)
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TetriNETClient extends Thread
{
    private Socket socket;
    private Reader in;
    private Writer out;
    private ServerConfig serverConfig;

    private int type;
    private String version;

    // client types
    public static final int CLIENT_TETRINET  = 0;
    public static final int CLIENT_TETRIFAST = 1;
    public static final int CLIENT_TSPEC     = 2;

    private Channel channel;
    private TetriNETServer server;
    private TetriNETPlayer player;

    private boolean disconnected;
    private Logger logger = Logger.getLogger("net.jetrix");
    private static List allowedSpecials;

    static
    {
        allowedSpecials = new ArrayList();
        allowedSpecials.add("cs1");
        allowedSpecials.add("cs2");
        allowedSpecials.add("cs4");
        allowedSpecials.add("a");
        allowedSpecials.add("c");
        allowedSpecials.add("n");
        allowedSpecials.add("r");
        allowedSpecials.add("s");
        allowedSpecials.add("b");
        allowedSpecials.add("g");
        allowedSpecials.add("q");
        allowedSpecials.add("o");
    }

    public TetriNETClient()
    {
        TetriNETServer server = TetriNETServer.getInstance();
        if (server != null ) serverConfig = server.getConfig();
    }

    public TetriNETClient(TetriNETPlayer player, Socket socket)
    {
        this();
        setSocket(socket);
        this.player = player;
    }

    /**
     * Main loop listening and parsing messages sent by the client.
     */
    public void run()
    {
        logger.fine("Client started " + this);

        try
        {
            String s;
            Message m;

            while ( !disconnected && serverConfig.isRunning() )
            {
                // reading raw message from socket
                s = readLine();
                logger.finer("RECV: " + s);
                StringTokenizer st = new StringTokenizer(s, " ");

                try
                {

                String cmd = st.nextToken();

                // building server message
                m = new Message();
                m.setRawMessage(s);
                m.setSource(this);

                // team <slot> teamname
                if ("team".equals(cmd))
                {
                    m.setType(Message.TYPE_CHANNEL);
                    m.setCode(Message.MSG_TEAM);

                    Integer slot = new Integer(st.nextToken());
                    String teamname = st.hasMoreTokens()?st.nextToken():"";
                    m.setParameters(new Object[] { slot, teamname });
                }
                // pline <slot> text
                else if ("pline".equals(cmd))
                {
                    m.setType(Message.TYPE_CHANNEL);
                    m.setCode(Message.MSG_PLINE);

                    Integer slot = new Integer(st.nextToken());
                    String text = (st.hasMoreTokens())?st.nextToken():"";
                    Object[] params = { slot, text };
                    m.setParameters(params);
                }
                // gmsg playername+text
                else if ("gmsg".equals(cmd))
                {
                    m.setType(Message.TYPE_CHANNEL);
                    m.setCode(Message.MSG_GMSG);

                    String text = st.nextToken();
                    m.setParameters(new Object[] { text });
                }
                // plineact <slot> emote
                else if ("plineact".equals(cmd))
                {
                    m.setType(Message.TYPE_CHANNEL);
                    m.setCode(Message.MSG_PLINEACT);

                    Integer slot = new Integer(st.nextToken());
                    String text = st.nextToken();
                    m.setParameters(new Object[] { slot, text });
                }
                // startgame <0 = stop | 1 = start> <playernumber>
                else if ("startgame".equals(cmd))
                {
                    m.setType(Message.TYPE_CHANNEL);
                    m.setCode("1".equals(st.nextToken())?Message.MSG_STARTGAME:Message.MSG_ENDGAME);
                    if (Message.MSG_ENDGAME == m.getCode()) m.setRawMessage("endgame"); else m.setRawMessage(null);

                    Integer slot = new Integer(st.nextToken());
                    Object[] params = { slot, channel.getConfig().getSettings() };
                    m.setParameters(params);
                }
                // pause <0 = resume | 1 = pause> <playernumber>
                else if ("pause".equals(cmd))
                {
                    m.setType(Message.TYPE_CHANNEL);
                    String pause = st.nextToken();
                    m.setCode("1".equals(pause)?Message.MSG_PAUSE:Message.MSG_RESUME);

                    m.setRawMessage(null);
                    //Integer slot = new Integer(st.nextToken());
                    m.setParameters(new Object[] { pause });
                }
                // playerlost <slot>
                else if ("playerlost".equals(cmd))
                {
                    m.setType(Message.TYPE_CHANNEL);
                    m.setCode(Message.MSG_PLAYERLOST);

                    Integer slot = new Integer(st.nextToken());
                    m.setParameters(new Object[] { slot });
                }
                // lvl <playernumber> <level>
                else if ("lvl".equals(cmd))
                {
                    m.setType(Message.TYPE_CHANNEL);
                    m.setCode(Message.MSG_LVL);

                    Integer slot = new Integer(st.nextToken());
                    Integer level = new Integer(st.nextToken());
                    m.setParameters(new Object[] { slot, level });
                }
                // sb <to> <bonus> <from>
                else if ("sb".equals(cmd))
                {
                    m.setType(Message.TYPE_CHANNEL);
                    m.setCode(Message.MSG_SB);

                    Integer to   = new Integer(st.nextToken());
                    String special = st.nextToken();
                    Integer from = new Integer(st.nextToken());

                    // check forged specials
                    if (!allowedSpecials.contains(special))
                        throw new IllegalArgumentException("Forged special detected from " + this);

                    m.setParameters(new Object[] { to, special, from });
                }
                // f <slot> <field>
                else if ("f".equals(cmd))
                {
                    m.setType(Message.TYPE_CHANNEL);
                    m.setCode(Message.MSG_FIELD);

                    Integer slot = new Integer(st.nextToken());
                    String field = (st.hasMoreTokens())?st.nextToken():null;
                    m.setParameters(new Object[] { slot, field });
                }

                // any other slash command
                if ( m.getCode() == Message.MSG_PLINE )
                {
                    String text = (String)m.getParameter(1);
                    if (text.startsWith("/"))
                    {
                        m.setType(Message.TYPE_SERVER);
                        m.setCode(Message.MSG_SLASHCMD);
                        ArrayList params = new ArrayList();
                        params.add(m.getParameter(0));
                        params.add(m.getParameter(1));

                        while (st.hasMoreTokens()) params.add(st.nextToken());
                        m.setParameters(params.toArray());
                    }
                }

                // forwarding message to channel
                channel.addMessage(m);

                }
                catch (NumberFormatException e)
                {
                    logger.finer("Bad format message");
                    e.printStackTrace();
                }
                catch (NoSuchElementException e)
                {
                    logger.finer("Bad format message");
                    e.printStackTrace();
                }
                catch (IllegalArgumentException e)
                {
                    logger.warning(e.getMessage());
                    e.printStackTrace();
                }
            } // end while

            Message leaveNotice = new Message(Message.MSG_PLAYERLEAVE);
            leaveNotice.setParameters(new Object[] { new Integer(channel.getPlayerSlot(this)) });
            channel.addMessage(leaveNotice);
        }
        catch (IOException e)
        {
            Message m = new Message();
            m.setCode(Message.MSG_DISCONNECTED);
            m.setParameters(new Object[] { this });
            channel.addMessage(m);
        }
        finally
        {
            try { in.close(); }     catch (IOException e) { e.printStackTrace(); }
            try { out.close(); }    catch (IOException e) { e.printStackTrace(); }
            try { socket.close(); } catch (IOException e) { e.printStackTrace(); }
            ClientRepository.getInstance().removeClient(this);
        }
    }

    /**
     * Send a message to the client. The raw message property must be set.
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

            logger.finest("> " + m.getRawMessage());
        }
        catch (SocketException e) { logger.fine(e.getMessage()); }
        catch (Exception e) { e.printStackTrace(); }

        }
        else { logger.warning("Message not sent, raw message missing " + m); }
    }


    /**
     * Read a line sent by the tetrinet client.
     *
     * @return line sent
     */
    protected String readLine() throws IOException
    {
        int readChar;
        StringBuffer input = new StringBuffer();

        while ((readChar = in.read()) != -1 && readChar != 255)
        {
            if (readChar != 10 && readChar != 13)
            {
                input.append((char)readChar);
            }
        }

        if (readChar == -1) throw new IOException("client disconnected");

        return input.toString();
    }

    public void setChannel(Channel channel)
    {
        this.channel = channel;
    }

    public Channel getChannel()
    {
        return channel;
    }

    public void setPlayer(TetriNETPlayer player)
    {
        this.player = player;
    }

    public TetriNETPlayer getPlayer()
    {
        return player;
    }

    public void setSocket(Socket socket)
    {
        this.socket = socket;
        try
        {
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }
        catch(IOException e) { e.printStackTrace(); }
    }

    public Socket getSocket()
    {
        return socket;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getVersion()
    {
        return version;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public int getType()
    {
        return type;
    }

    /**
     * Triggers the disconnection of this client.
     */
    public void disconnect()
    {
        disconnected = true;
        try { socket.shutdownOutput(); } catch(Exception e) { e.printStackTrace(); }
    }

    public String toString()
    {
        return "[Client " + socket + " type=" + type + "]";
    }

}
