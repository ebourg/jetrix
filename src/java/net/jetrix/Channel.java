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

package net.jetrix;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import net.jetrix.config.*;
import net.jetrix.filter.*;
import net.jetrix.messages.*;

/**
 * Game channel
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class Channel extends Thread implements Destination
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
    private Client[] playerList = new Client[6];

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
        this.serverConfig  = Server.getInstance().getConfig();
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
                // waiting for new messages
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

    private void process(CommandMessage m)
    {
        // forwards the command to the server
        Server.getInstance().sendMessage(m);
    }

    private void process(TeamMessage m)
    {
        int slot = m.getSlot();
        playerList[slot - 1].getPlayer().setTeam(m.getName());
        sendAll(m, slot);
    }

    private void process(GmsgMessage m)
    {
        sendAll(m);
    }

    private void process(PlineMessage m)
    {
        int slot = m.getSlot();
        String text = m.getText();
        if (!text.startsWith("/")) sendAll(m, slot);
    }

    private void process(PlineActMessage m)
    {
        int slot = m.getSlot();
        if (m.getSource() == null)
            // forged by the server, send to all
            sendAll(m);
        else
            sendAll(m, slot);
    }

    private void process(PauseMessage m)
    {
        gameState = GAME_STATE_PAUSED;
        sendAll(m);
    }

    private void process(ResumeMessage m)
    {
        gameState = GAME_STATE_STARTED;
        sendAll(m);
    }

    private void process(PlayerLostMessage m)
    {
        int slot = m.getSlot();
        Client client = (Client)playerList[slot-1];
        sendAll(m);

        // sending closing screen
        StringBuffer screenLayout = new StringBuffer();
        for (int i = 0; i < 12*22; i++)
        {
            screenLayout.append( ( (int)(Math.random() * 4 + 1) ) * (1 - jetrixLogo[i]) );
            //screenLayout.append( ( (int)(slot%5+1) ) * (1-jetrixLogo[i]) );
        }
        FieldMessage endingScreen = new FieldMessage();
        endingScreen.setSlot(m.getSlot());
        endingScreen.setField(screenLayout.toString());
        sendAll(endingScreen);

        boolean wasPlaying = client.getPlayer().isPlaying();
        client.getPlayer().setPlaying(false);

        // check for end of game
        if (wasPlaying && countRemainingTeams() <= 1)
        {
            gameState = Channel.GAME_STATE_STOPPED;
            Message endgame = new EndGameMessage();
            sendAll(endgame);
        }
    }

    private void process(SpecialMessage m)
    {
        // specials are not forwarded in pure mode
        if ( channelConfig.getSettings().getLinesPerSpecial() >0 )
        {
            int slot = m.getFromSlot();
            sendAll(m, slot);
        }
    }

    private void process(LevelMessage m)
    {
        sendAll(m);
    }

    private void process(FieldMessage m)
    {
        int slot = m.getSlot();
        sendAll(m, slot);
    }

    private void process(StartGameMessage m)
    {
        if (gameState == GAME_STATE_STOPPED)
        {
            // change the channel state
            gameState = GAME_STATE_STARTED;

            // change the players state
            for (int i = 0; i < playerList.length; i++)
            {
                if(playerList[i] != null)
                {
                    Client client = (Client)playerList[i];
                    client.getPlayer().setPlaying(true);
                }
            }

            // send the newgame message
            NewGameMessage newgame = new NewGameMessage();
            newgame.setSlot(m.getSlot());
            newgame.setSettings(channelConfig.getSettings());
            sendAll(newgame);
        }
    }

    private void process(StopGameMessage m)
    {
        if (gameState != GAME_STATE_STOPPED)
        {
            gameState = GAME_STATE_STOPPED;
            EndGameMessage end = new EndGameMessage();
            end.setSlot(m.getSlot());
            sendAll(end);
        }
    }

    private void process(EndGameMessage m)
    {
        if (gameState != GAME_STATE_STOPPED)
        {
            gameState = GAME_STATE_STOPPED;
            sendAll(m);
        }
    }

    private void process(DisconnectedMessage m)
    {
        // searching player slot
        Client client = m.getClient();
        int slot = getPlayerSlot(client);

        // removing player from channel members
        playerList[slot - 1] = null;

        // sending notification to players
        LeaveMessage leaveNotice = new LeaveMessage();
        leaveNotice.setSlot(slot);
        sendAll(leaveNotice);

        PlineMessage disconnected = new PlineMessage();
        disconnected.setKey("channel.disconnected", new Object[] { client.getPlayer().getName() });
        sendAll(disconnected);

        // stopping the game if the channel is now empty
        if (isEmpty() && running) { gameState = GAME_STATE_STOPPED; }
    }

    private void process(LeaveMessage m)
    {
        int slot = m.getSlot();
        playerList[slot-1] = null;
        sendAll(m);

        // stopping the game if the channel is now empty
        if (isEmpty() && running) { gameState = GAME_STATE_STOPPED; }
    }

    private void process(AddPlayerMessage m)
    {
        Client client = (Client)m.getClient();

        if (client.getChannel() == null)
        {
            // first channel assigned
            client.setChannel(this);
            (new Thread(client)).start();
        }
        else
        {
            // leaving a previous channel
            Channel previousChannel = client.getChannel();

            // notice to players in the previous channel
            LeaveMessage leave = new LeaveMessage();
            leave.setSlot(previousChannel.getPlayerSlot(client));
            previousChannel.sendMessage(leave);
            client.setChannel(this);

            // sending message to the previous channel announcing what channel the player joined
            PlineMessage leave2 = new PlineMessage();
            leave2.setKey("channel.join_notice", new Object[] { client.getPlayer().getName(), channelConfig.getName() });
            previousChannel.sendMessage(leave2);

            // ending running game
            if (previousChannel.getGameState() != Channel.GAME_STATE_STOPPED)
            {
                Message endgame = new EndGameMessage();
                client.sendMessage(endgame);
            }

            // clearing player list
            for (int j = 1; j <= 6; j++)
            {
                if (previousChannel.getPlayer(j) != null)
                {
                    LeaveMessage clear = new LeaveMessage();
                    clear.setSlot(j);
                    client.sendMessage(clear);
                }
            }
        }

        // looking for the first free slot
        int slot = 0;
        for (slot = 0; slot < 6 && playerList[slot] != null; slot++);

        if (slot >= 6)
        {
            logger.warning("Panic, no slot available");
        }
        else
        {
            playerList[slot]= client;
            client.getPlayer().setPlaying(false);

            // sending new player notice to other players in the channel
            JoinMessage mjoin = new JoinMessage();
            mjoin.setSlot(slot + 1);
            mjoin.setName(client.getPlayer().getName());
            sendAll(mjoin, mjoin.getSlot());

            // sending slot number to incomming player
            PlayerNumMessage mnum = new PlayerNumMessage();
            mnum.setSlot(slot + 1);
            client.sendMessage(mnum);

            // sending player and team list to incomming player
            for (int i = 0; i < playerList.length; i++)
            {
                if (playerList[i]!=null && i!=slot)
                {
                    Client resident = (Client)playerList[i];

                    // players...
                    JoinMessage mjoin2 = new JoinMessage();
                    mjoin2.setSlot(i + 1);
                    mjoin2.setName(resident.getPlayer().getName());
                    client.sendMessage(mjoin2);

                    // ...and teams
                    TeamMessage mteam = new TeamMessage();
                    mteam.setSlot(i + 1);
                    mteam.setName(resident.getPlayer().getTeam());
                    client.sendMessage(mteam);
                }
            }

            // sending welcome massage to incomming player
            PlineMessage mwelcome = new PlineMessage();
            mwelcome.setKey("channel.welcome", new Object[] { client.getPlayer().getName(), channelConfig.getName() });
            client.sendMessage(mwelcome);

            // sending playerlost message if the game has started
            if (gameState != GAME_STATE_STOPPED)
            {
                //Message lost = new Message(Message.MSG_PLAYERLOST);
                //lost.setParameters(new Object[] { new Integer(slot+1) });
                //sendAll(lost);

                Message ingame = new IngameMessage();
                client.sendMessage(ingame);
            }

            // tell the player if the game is currently paused
            if (gameState == GAME_STATE_PAUSED)
            {
                Message pause = new PauseMessage();
                client.sendMessage(pause);
            }
        }
    }

    public void process(Message m)
    {
        logger.finest("[" + channelConfig.getName() + "] Processing " +  m);
        int slot;

        if ( m instanceof CommandMessage) process((CommandMessage)m);
        else if ( m instanceof TeamMessage) process((TeamMessage)m);
        else if ( m instanceof GmsgMessage) process((GmsgMessage)m);
        else if ( m instanceof PlineMessage) process((PlineMessage)m);
        else if ( m instanceof PlineActMessage) process((PlineActMessage)m);
        else if ( m instanceof PauseMessage) process((PauseMessage)m);
        else if ( m instanceof ResumeMessage) process((ResumeMessage)m);
        else if ( m instanceof PlayerLostMessage) process((PlayerLostMessage)m);
        else if ( m instanceof SpecialMessage) process((SpecialMessage)m);
        else if ( m instanceof LevelMessage) process((LevelMessage)m);
        else if ( m instanceof FieldMessage) process((FieldMessage)m);
        else if ( m instanceof StartGameMessage) process((StartGameMessage)m);
        else if ( m instanceof StopGameMessage) process((StopGameMessage)m);
        else if ( m instanceof EndGameMessage) process((EndGameMessage)m);
        else if ( m instanceof DisconnectedMessage) process((DisconnectedMessage)m);
        else if ( m instanceof LeaveMessage) process((LeaveMessage)m);
        else if ( m instanceof AddPlayerMessage) process((AddPlayerMessage)m);
        else
        {
            logger.finest("[" + channelConfig.getName() + "] Message not processed " +  m);
        }

    }

    /**
     * Add a message to the channel MessageQueue.
     *
     * @param m message to add
     */
    public void sendMessage(Message m)
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
             Client client = playerList[i];
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
                 Client client = playerList[i];
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
        return getPlayerCount() >= channelConfig.getMaxPlayers();
    }

    public boolean isEmpty()
    {
        return getPlayerCount() == 0;
    }

    /**
     * Returns the number of players currently in this chanel.
     *
     * @return player count
     */
    public int getPlayerCount()
    {
        int count = 0;

        for (int i = 0; i<channelConfig.getMaxPlayers(); i++)
        {
            if (playerList[i] != null)
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
    public int getPlayerSlot(Client client)
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
    public Client getPlayer(int slot)
    {
        Client client = null;

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
            Client client = playerList[i];

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
