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
import java.util.logging.*;
import org.lfjr.jts.config.*;
import org.lfjr.jts.filter.*;

/**
 * Game channel
 *
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class Channel extends Thread
{
    private ChannelConfig channelConfig;
    private ServerConfig serverConfig;
    private Logger logger = Logger.getLogger("net.jetrix");

    private MessageQueue mq;

    private boolean running = true;

    // game states
    public static final int GAME_STATE_STOPPED = 0;
    public static final int GAME_STATE_STARTED = 1;
    public static final int GAME_STATE_PAUSED  = 2;

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

    public Channel(ChannelConfig channelConfig)
    {
        this.channelConfig = channelConfig;
        this.serverConfig  = TetriNETServer.getInstance().getConfig();
        this.gameState = GAME_STATE_STOPPED;

        // opening channel message queue
        mq = new MessageQueue();

        filters = new ArrayList();

        /**
         * Loading filters
         */

        // global filters
        Iterator it = serverConfig.getGlobalFilters();
        while ( it.hasNext() ) { addFilter( (FilterConfig)it.next() ); }

        // channel filters
        it = channelConfig.getFilters();
        while ( it.hasNext() ) { addFilter( (FilterConfig)it.next() ); }
    }

    /**
     * Enable a new filter for this channel.
     */
    public void addFilter(FilterConfig filterConfig)
    {
        FilterManager filterManager = FilterManager.getInstance();
        MessageFilter filter;

        try
        {
            // getting filter instance
            if (filterConfig.getClassname()!=null)
            {
                filter = filterManager.getFilter(filterConfig.getClassname());
            }
            else
            {
                filter = filterManager.getFilterByName(filterConfig.getName());
            }

            // initializing filter
            filter.setChannel(this);
            filter.init(filterConfig);

            // adding filter to the list
            filters.add(filter);

            logger.info("["+channelConfig.getName()+"] loaded filter " + filter.getName() + " " + filter.getVersion());
        }
        catch (FilterException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Main loop. The channel listens for incomming messages until the server
     * or the channel closes. Every message is first passed through the
     * registered filters and then handled by the channel.
     */
    public void run()
    {
        logger.info("Channel " + channelConfig.getName() + " opened");

        while (running && serverConfig.isRunning())
        {
            LinkedList l = new LinkedList();

            try
            {
                l.add( mq.get() );

                // filtering message
                Iterator it = filters.iterator();
                while ( it.hasNext() )
                {
                    MessageFilter filter = (MessageFilter)it.next();
                    int size = l.size();
                    for (int i = 0; i<size; i++) { filter.process( (Message)l.removeFirst(), l ); }
                }

                // processing message(s)
                while ( !l.isEmpty() ) { process( (Message)l.removeFirst() ); }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        logger.info("Channel " + channelConfig.getName() + " closed");
    }

    public void process(Message m)
    {
        int slot;

        switch(m.getCode())
        {
            case Message.MSG_SLASHCMD:
                String cmd = m.getStringParameter(1);

                if ("/join".equalsIgnoreCase(cmd) || "/j".equalsIgnoreCase(cmd))
                {
                    Channel target = ChannelManager.getInstance().getChannel(m.getStringParameter(2));
                    if (target!=null)
                    {
                        if ( target.isFull() )
                        {
                            // sending channel full message
                            Message channelfull = new Message(Message.MSG_PLINE);
                            channelfull.setParameters(new Object[] { new Integer(0), ChatColors.darkBlue+"That channel is "+ChatColors.red+"FULL"+ChatColors.darkBlue+"!" });
                            ((TetriNETClient)m.getSource()).sendMessage(channelfull);
                        }
                        else
                        {
                            Message move = new Message(Message.MSG_ADDPLAYER, new Object[] { m.getSource() });
                            target.addMessage(move);
                        }
                    }

                }
                else if ("/move".equalsIgnoreCase(cmd))
                {
                    Message response = new Message(Message.MSG_PLINE,
                                                   new Object[] { new Integer(0), ChatColors.darkBlue+"/move is not implemented yet" });
                    ((TetriNETClient)m.getSource()).sendMessage(response);
                }
                else if ("/config".equalsIgnoreCase(cmd) || "/conf".equalsIgnoreCase(cmd) || "/settings".equalsIgnoreCase(cmd))
                {
                    Settings s = channelConfig.getSettings();
                    String configLines[] = new String[18];

                    configLines[0]  = ChatColors.darkBlue + ChatColors.bold + "Blocks			Specials";
                    configLines[1]  = ChatColors.darkBlue + "Left L	: "+ChatColors.purple+s.getBlockOccurancy(Settings.BLOCK_LEFTL)+"%"+ChatColors.purple+"		("+ChatColors.purple+"A"+ChatColors.purple+") Add Line	: "+ChatColors.purple+s.getSpecialOccurancy(Settings.SPECIAL_ADDLINE)+"%";
                    configLines[2]  = ChatColors.darkBlue + "Right L	: "+ChatColors.purple+s.getBlockOccurancy(Settings.BLOCK_RIGHTL)+"%"+ChatColors.purple+"		("+ChatColors.purple+"C"+ChatColors.purple+") Clear Line	: "+ChatColors.purple+s.getSpecialOccurancy(Settings.SPECIAL_CLEARLINE)+"%";
                    configLines[3]  = ChatColors.darkBlue + "Square	: "+ChatColors.purple+s.getBlockOccurancy(Settings.BLOCK_SQUARE)+"%"+ChatColors.purple+"		("+ChatColors.purple+"N"+ChatColors.purple+") Nuke Field	: "+ChatColors.purple+s.getSpecialOccurancy(Settings.SPECIAL_NUKEFIELD)+"%";
                    configLines[4]  = ChatColors.darkBlue + "Left Z	: "+ChatColors.purple+s.getBlockOccurancy(Settings.BLOCK_LEFTZ)+"%"+ChatColors.purple+"		("+ChatColors.purple+"R"+ChatColors.purple+") Random Clear	: "+ChatColors.purple+s.getSpecialOccurancy(Settings.SPECIAL_RANDOMCLEAR)+"%";
                    configLines[5]  = ChatColors.darkBlue + "Right Z	: "+ChatColors.purple+s.getBlockOccurancy(Settings.BLOCK_RIGHTZ)+"%"+ChatColors.purple+"		("+ChatColors.purple+"N"+ChatColors.purple+") Switch Field	: "+ChatColors.purple+s.getSpecialOccurancy(Settings.SPECIAL_SWITCHFIELD)+"%";
                    configLines[6]  = ChatColors.darkBlue + "Cross	: "+ChatColors.purple+s.getBlockOccurancy(Settings.BLOCK_HALFCROSS)+"%"+ChatColors.purple+"		("+ChatColors.purple+"B"+ChatColors.purple+") Clear Special	: "+ChatColors.purple+s.getSpecialOccurancy(Settings.SPECIAL_CLEARSPECIAL)+"%";
                    configLines[7]  = ChatColors.darkBlue + "Line	: "+ChatColors.purple+s.getBlockOccurancy(Settings.BLOCK_LINE)+"%"+ChatColors.purple+"		("+ChatColors.purple+"G"+ChatColors.purple+") Gravity	: "+ChatColors.purple+s.getSpecialOccurancy(Settings.SPECIAL_GRAVITY)+"%";
                    configLines[8]  = ChatColors.darkBlue + "			("+ChatColors.purple+"Q"+ChatColors.purple+") Quake Field	: "+ChatColors.purple+s.getSpecialOccurancy(Settings.SPECIAL_QUAKEFIELD)+"%";
                    configLines[9]  = ChatColors.darkBlue + ChatColors.bold + "Rules" + ChatColors.bold + "			("+ChatColors.purple+"O"+ChatColors.purple+") Blockbomb	: "+ChatColors.purple+s.getSpecialOccurancy(Settings.SPECIAL_BLOCKBOMB)+"%";
                    configLines[10] = ChatColors.darkBlue + "Starting Level	: " + ChatColors.blue + s.getStartingLevel();
                    configLines[11] = ChatColors.darkBlue + "Lines per Level	: " + ChatColors.blue + s.getLinesPerLevel();
                    configLines[12] = ChatColors.darkBlue + "Level Increase	: " + ChatColors.blue + s.getLevelIncrease();
                    configLines[13] = ChatColors.darkBlue + "Lines per Special	: " + ChatColors.blue + s.getLinesPerSpecial();
                    configLines[14] = ChatColors.darkBlue + "Special Added	: " + ChatColors.blue + s.getSpecialAdded();
                    configLines[15] = ChatColors.darkBlue + "Special Capacity	: " + ChatColors.blue + s.getSpecialCapacity();
                    configLines[16] = ChatColors.darkBlue + "Classic Rules	: " + ChatColors.blue + (s.getClassicRules()?"yes":"no");
                    configLines[17] = ChatColors.darkBlue + "Average Levels	: " + ChatColors.blue + (s.getAverageLevels()?"yes":"no");
                    //configLines[18] = "";
                    //configLines[19] = ChatColors.darkBlue + ChatColors.bold + "Filters" + ChatColors.bold + "  (type " + ChatColors.red + "/filter" + ChatColors.red + " for details)";
                    //configLines[20] = ChatColors.darkBlue + "start, flood, amplifier";
                                        
                    TetriNETClient client = (TetriNETClient)m.getSource();

                    for (int i = 0; i < configLines.length; i++)
                    {
                        Message configMessage = new Message(Message.MSG_PLINE);
                        configMessage.setParameters(new Object[] { new Integer(0), configLines[i] });
                        client.sendMessage(configMessage);
                    }

                }
                else
                {
                    // forwards the command to the server
                    TetriNETServer.getInstance().addMessage(m);
                }
                break;

            case Message.MSG_TEAM:
                slot = m.getIntParameter(0);
                playerList[slot - 1].getPlayer().setTeam(m.getStringParameter(1));
                sendAll(m, slot);
                break;

            case Message.MSG_GMSG:
                sendAll(m);
                break;

            case Message.MSG_PLINE:
                slot = m.getIntParameter(0);
                String text = m.getStringParameter(1);
                if (!text.startsWith("/")) sendAll(m, slot);
                break;

            case Message.MSG_PLINEACT:
                slot = m.getIntParameter(0);
                sendAll(m, slot);
                break;

            case Message.MSG_PAUSE:
                gameState = GAME_STATE_PAUSED;
                sendAll(m);
                break;

            case Message.MSG_RESUME:
                gameState = GAME_STATE_STARTED;
                sendAll(m);
                break;

            case Message.MSG_PLAYERLOST:
                slot = m.getIntParameter(0);
                TetriNETClient client = (TetriNETClient)playerList[slot-1];
                sendAll(m);

                // sending closing screen
                StringBuffer screenLayout = new StringBuffer();
                for (int i = 0; i < 12*22; i++)
                {
                    screenLayout.append( ( (int)(Math.random() * 4 + 1) ) * (1 - jetrixLogo[i]) );
                    //screenLayout.append( ( (int)(slot%5+1) ) * (1-jetrixLogo[i]) );
                }
                Message endingScreen = new Message(Message.MSG_FIELD);
                endingScreen.setParameters(new Object[] { m.getParameter(0), screenLayout.toString() });
                sendAll(endingScreen);
                
                boolean wasPlaying = client.getPlayer().isPlaying();
                client.getPlayer().setPlaying(false);

                // check for end of game
                if (wasPlaying && countRemainingTeams() <= 1)
                {
                    gameState = Channel.GAME_STATE_STOPPED;
                    Message endgame = new Message(Message.MSG_ENDGAME);
                    sendAll(endgame);
                }

                break;

            case Message.MSG_SB:
                // specials are not forwarded in pure mode
                if ( channelConfig.getSettings().getLinesPerSpecial() >0 )
                {
                    slot = m.getIntParameter(2);
                    sendAll(m, slot);
                }
                break;

            case Message.MSG_LVL:
                // how does it work ?
                break;

            case Message.MSG_FIELD:
                slot = m.getIntParameter(0);
                sendAll(m, slot);
                //sendAll(m);
                break;

            case Message.MSG_STARTGAME:
                if (gameState == GAME_STATE_STOPPED)
                {
                    gameState = GAME_STATE_STARTED;
                    for (int i = 0; i < playerList.length; i++)
                    {
                        if(playerList[i] != null)
                        {
                            client = (TetriNETClient)playerList[i];
                            client.getPlayer().setPlaying(true);
                        }
                    }
                    sendAll(m);
                }
                break;

            case Message.MSG_ENDGAME:
                if (gameState != GAME_STATE_STOPPED)
                {
                    gameState = GAME_STATE_STOPPED;
                    sendAll(m);
                }
                break;

            case Message.MSG_DISCONNECTED:
                // searching player slot
                client = (TetriNETClient)m.getParameter(0);

                slot = 0;
                int i = 0;
                while(i < playerList.length && slot == 0)
                {
                    if (playerList[i] == client) { slot = i; }
                    i++;
                }

                // removing player from channel members
                playerList[slot] = null;

                // sending notification to players
                Message leaveNotice = new Message(Message.MSG_PLAYERLEAVE, new Object[] { new Integer(slot+1) });
                sendAll(leaveNotice);
                
                String disconnectedMessage = ChatColors.gray + client.getPlayer().getName() + " has been disconnected.";
                Message disconnected = new Message(Message.MSG_PLINE, new Object[] { new Integer(0), disconnectedMessage });
                sendAll(disconnected);

                // stopping the game if the channel is now empty
                if (isEmpty() && running) { gameState = GAME_STATE_STOPPED; }

                break;

            case Message.MSG_PLAYERLEAVE:
                slot = m.getIntParameter(0);
                playerList[slot-1] = null;
                sendAll(m);

                // stopping the game if the channel is now empty
                if (isEmpty() && running) { gameState = GAME_STATE_STOPPED; }

                break;

            case Message.MSG_ADDPLAYER:
                client = (TetriNETClient)m.getParameter(0);

                if (client.getChannel()==null)
                {
                    // first channel assigned
                    client.setChannel(this);
                    client.start();
                }
                else
                {
                    // leaving a previous channel
                    Channel previousChannel = client.getChannel();

                    // notice to players in the previous channel
                    Message leave = new Message(Message.MSG_PLAYERLEAVE);
                    leave.setParameters(new Object[] { new Integer(previousChannel.getPlayerSlot(client)) });
                    previousChannel.addMessage(leave);
                    client.setChannel(this);

                    // sending message to the previous channel announcing what channel the player joined
                    Message leave2 = new Message(Message.MSG_PLINE);
                    String leaveMessage = ChatColors.gray + client.getPlayer().getName()+" has joined channel " + ChatColors.bold + channelConfig.getName();
                    leave2.setParameters(new Object[] { new Integer(0), leaveMessage});
                    previousChannel.addMessage(leave2);

                    // ending running game
                    if (previousChannel.getGameState() != Channel.GAME_STATE_STOPPED)
                    {
                        Message endgame = new Message(Message.MSG_ENDGAME);
                        client.sendMessage(endgame);
                    }

                    // clearing player list
                    for (int j=1; j<=6; j++)
                    {
                        if (previousChannel.getPlayer(j)!=null)
                        {
                            Message clear = new Message(Message.MSG_PLAYERLEAVE);
                            clear.setParameters(new Object[] { new Integer(j) });
                            client.sendMessage(clear);
                        }
                    }
                }

                // looking for the first free slot
                for (slot=0; slot<6 && playerList[slot]!=null; slot++);

                if (slot>=6)
                {
                    logger.warning("Panic, no slot available");
                }
                else
                {
                    playerList[slot]= client;
                    client.getPlayer().setPlaying(false);

                    // sending new player notice to other players in the channel
                    Message mjoin = new Message(Message.MSG_PLAYERJOIN);
                    mjoin.setParameters(new Object[] { new Integer(slot+1), client.getPlayer().getName() });
                    sendAll(mjoin, slot+1);

                    // sending slot number to incomming player
                    Message mnum = new Message(Message.MSG_PLAYERNUM);
                    mnum.setParameters(new Object[] { new Integer(slot+1) });
                    client.sendMessage(mnum);

                    // sending player and team list to incomming player
                    for (i=0; i<playerList.length; i++)
                    {
                        if (playerList[i]!=null && i!=slot)
                        {
                            TetriNETClient resident = (TetriNETClient)playerList[i];

                            // players...
                            Message mjoin2 = new Message(Message.MSG_PLAYERJOIN);
                            mjoin2.setParameters(new Object[] { new Integer(i+1), resident.getPlayer().getName() });
                            client.sendMessage(mjoin2);

                            // ...and teams
                            Message mteam = new Message(Message.MSG_TEAM);
                            mteam.setParameters(new Object[] { new Integer(i+1), resident.getPlayer().getTeam() });
                            client.sendMessage(mteam);
                        }
                    }

                    // sending welcome massage to incomming player
                    Message mwelcome = new Message(Message.MSG_PLINE);
                    String welcomeText = ChatColors.gray+"Hello "+client.getPlayer().getName()+", you are in channel " + ChatColors.bold + channelConfig.getName();
                    mwelcome.setParameters(new Object[] { new Integer(0), welcomeText });
                    client.sendMessage(mwelcome);

                    // sending playerlost message if the game has started
                    if (gameState != GAME_STATE_STOPPED)
                    {
                        /*Message lost = new Message(Message.MSG_PLAYERLOST);
                        lost.setParameters(new Object[] { new Integer(slot+1) });
                        sendAll(lost);*/

                        Message ingame = new Message(Message.MSG_INGAME);
                        client.sendMessage(ingame);
                    }

                    // tell the player if the game is currently paused
                    if (gameState == GAME_STATE_PAUSED)
                    {
                        Message pause = new Message(Message.MSG_PAUSE);
                        client.sendMessage(pause);
                    }
                }
                break;
        }
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
        for (int i = 0; i < playerList.length; i++)
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
        for (int i = 0; i < playerList.length; i++)
        {
             if (i + 1 != slot)
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
        return getNbPlayers() >= channelConfig.getMaxPlayers();
    }

    public boolean isEmpty()
    {
        return getNbPlayers() == 0;
    }

    /**
     * Returns the number of players currently in this chanel.
     *
     * @return player count
     */
    public int getNbPlayers()
    {
        int count = 0;

        for (int i = 0; i<channelConfig.getMaxPlayers(); i++)
        {
            if (playerList[i]!=null)
            {
                count++;
            }
        }

        return count;
    }

    /**
     * Returns the channel configuration.
     */
    public ChannelConfig getConfig()
    {
        return channelConfig;
    }

    /**
     * Returns the game state.
     */
    public int getGameState()
    {
        return gameState;
    }

    /**
     * Finds the slot used in the channel by the specified client.
     */
    public int getPlayerSlot(TetriNETClient client)
    {
        int slot = 0;

        for (int i = 0; i<channelConfig.getMaxPlayers(); i++)
        {
            if (playerList[i] == client) slot = i + 1;
        }

        return slot;
    }

    /**
     * Returns the client in the specified slot.
     *
     * @param slot slot number between 1 and 6
     *
     * @return <tt>null</tt> if there is no client in the specified slot, or if the number is out of range
     */
    public TetriNETClient getPlayer(int slot)
    {
        TetriNETClient client = null;

        if (slot >= 1 && slot <= 6) client = playerList[slot - 1];

        return client;
    }

    /**
     * Count how many teams are still fighting for victory. A teamless player
     * is considered as a separate team. The game ends when there is one team
     * left in game OR when the last player loose if only one team took part
     * in the game.
     *
     * @return number of teams still playing
     */
    private int countRemainingTeams()
    {
        Hashtable playingTeams = new Hashtable();

        int nbTeamsLeft = 0;

        for (int i = 0; i < playerList.length; i++)
        {
            TetriNETClient client = playerList[i];

            if ( client != null && client.getPlayer().isPlaying() )
            {
                String team = client.getPlayer().getTeam();

                if (team == null || "".equals(team))
                {
                    nbTeamsLeft++;
                }
                else
                {
                    playingTeams.put(team, team);
                }
            }
        }

        return nbTeamsLeft + playingTeams.size();
    }

}
