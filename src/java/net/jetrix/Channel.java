/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2003  Emmanuel Bourg
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

import java.util.*;
import java.util.logging.*;
import java.io.BufferedReader;
import java.io.StringReader;

import org.apache.commons.collections.*;

import net.jetrix.config.*;
import net.jetrix.filter.*;
import net.jetrix.messages.*;
import net.jetrix.winlist.*;

/**
 * Game channel.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class Channel extends Thread implements Destination
{
    private ChannelConfig channelConfig;
    private ServerConfig serverConfig;
    private Logger log = Logger.getLogger("net.jetrix");

    private MessageQueue queue;

    // game states
    public static final int GAME_STATE_STOPPED = 0;
    public static final int GAME_STATE_STARTED = 1;
    public static final int GAME_STATE_PAUSED = 2;

    private boolean open;
    private int gameState;
    private boolean running = true;
    private GameResult result;

    /** set of clients connected to this channel */
    private Set clients;

    /** slot/player mapping */
    private List slots;
    private Field[] fields = new Field[6];

    private List filters;

    public Channel()
    {
        this(new ChannelConfig());
    }

    public Channel(ChannelConfig channelConfig)
    {
        this.channelConfig = channelConfig;
        this.serverConfig = Server.getInstance().getConfig();
        this.gameState = GAME_STATE_STOPPED;
        this.clients = new HashSet();
        this.slots = new ArrayList(6);

        // initialize the slot mapping
        for (int i = 0; i < 6; i++)
        {
            slots.add(null);
        }

        // initialize the players' fields
        for (int i = 0; i < 6; i++)
        {
            fields[i] = new Field();
        }

        // opening channel message queue
        queue = new MessageQueue();

        filters = new ArrayList();

        /**
         * Loading filters
         */

        // global filters
        Iterator it = serverConfig.getGlobalFilters();
        while (it.hasNext())
        {
            addFilter((FilterConfig) it.next());
        }

        // channel filters
        it = channelConfig.getFilters();
        while (it.hasNext())
        {
            addFilter((FilterConfig) it.next());
        }
    }

    /**
     * Enable a new filter for this channel.
     */
    public void addFilter(FilterConfig filterConfig)
    {
        FilterManager filterManager = FilterManager.getInstance();
        MessageFilter filter;

        // add the filter to the channel config if it isn't a global filter

        try
        {
            // getting filter instance
            if (filterConfig.getClassname() != null)
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

            log.fine("[" + channelConfig.getName() + "] loaded filter " + filter.getName() + " " + filter.getVersion());
        }
        catch (FilterException e)
        {
            e.printStackTrace();
        }
    }

    public void removeFilter(String filterName)
    {
        // remove the filter from the filter list
        // @todo filter removal

        // remove the filter from the channel config
    }

    public Iterator getFilters()
    {
        return filters.iterator();
    }

    /**
     * Main loop. The channel listens for incomming messages until the server
     * or the channel closes. Every message is first passed through the
     * registered filters and then handled by the channel.
     */
    public void run()
    {
        log.info("Channel " + channelConfig.getName() + " opened");

        while (running && serverConfig.isRunning())
        {
            LinkedList list = new LinkedList();

            try
            {
                // waiting for new messages
                list.add(queue.get());

                // filtering message
                Iterator it = filters.iterator();
                while (it.hasNext())
                {
                    MessageFilter filter = (MessageFilter) it.next();
                    int size = list.size();
                    for (int i = 0; i < size; i++)
                    {
                        filter.process((Message) list.removeFirst(), list);
                    }
                }

                // processing message(s)
                while (!list.isEmpty())
                {
                    process((Message) list.removeFirst());
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        log.info("Channel " + channelConfig.getName() + " closed");
    }

    private void process(CommandMessage m)
    {
        // forwards the command to the server
        Server.getInstance().sendMessage(m);
    }

    private void process(TeamMessage m)
    {
        Client client = (Client) m.getSource();
        if (client.getUser().isPlayer())
        {
            int slot = m.getSlot();
            getPlayer(slot).setTeam(m.getName());
            sendAll(m, slot);
        }
    }

    private void process(GmsgMessage m)
    {
        sendAll(m);
    }

    private void process(PlineMessage m)
    {
        int slot = m.getSlot();
        String text = m.getText();
        if (!text.startsWith("/"))
        {
            sendAll(m, slot);
        }
    }

    private void process(PlineActMessage m)
    {
        int slot = m.getSlot();
        if (m.getSource() == null)
        {
            // forged by the server, send to all
            sendAll(m);
        }
        else
        {
            sendAll(m, slot);
        }
    }

    private void process(SmsgMessage m)
    {
        if (m.isPrivate())
        {
            // send the message to the spectators only
            Iterator it = clients.iterator();
            while (it.hasNext())
            {
                Client client = (Client) it.next();
                if (client.getUser().isSpectator() && client != m.getSource())
                {
                    client.sendMessage(m);
                }
            }
        }
        else
        {
            sendAll(m);
        }
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
        Client client = getClient(slot);
        User user = client.getUser();
        sendAll(m);

        boolean wasPlaying = user.isPlaying();
        user.setPlaying(false);

        // update the game result
        if (wasPlaying)
        {
            result.update(user, false);
        }

        // check for the end of the game
        if (wasPlaying && countRemainingTeams() <= 1)
        {
            Message endgame = new EndGameMessage();
            sendMessage(endgame);
            result.setEndTime(new Date());

            // looking for the slot of the winner
            slot = 0;
            for (int i = 0; i < slots.size(); i++)
            {
                client = (Client) slots.get(i);

                if (client != null && client.getUser().isPlaying())
                {
                    slot = i + 1;
                    // update the result of the game
                    result.update(client.getUser(), true);
                }
            }

            // announcing the winner
            if (slot != 0)
            {
                PlayerWonMessage playerwon = new PlayerWonMessage();
                playerwon.setSlot(slot);
                sendMessage(playerwon);

                User winner = getPlayer(slot);
                PlineMessage announce = new PlineMessage();
                if (winner.getTeam() == null)
                {
                    announce.setKey("channel.player_won", winner.getName());
                }
                else
                {
                    announce.setKey("channel.team_won", winner.getTeam());
                }
                sendMessage(announce);
            }

            // update the winlist with the final result
            Winlist winlist = WinlistManager.getInstance().getWinlist(channelConfig.getWinlistId());
            if (winlist != null)
            {
                winlist.saveGameResult(result);

                List topScores = winlist.getScores(0, 10);
                WinlistMessage winlistMessage = new WinlistMessage();
                winlistMessage.setScores(topScores);
                sendAll(winlistMessage);
            }
        }
    }

    private void process(SpecialMessage m)
    {
        // specials are not forwarded in pure mode
        if (channelConfig.getSettings().getLinesPerSpecial() > 0)
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

        // update the field of the player
        fields[slot - 1].update(m);

        if (m.getSource() != null)
        {
            sendAll(m, slot);
        }
        else
        {
            sendAll(m);
        }
    }

    private void process(StartGameMessage m)
    {
        if (gameState == GAME_STATE_STOPPED)
        {
            // change the channel state
            gameState = GAME_STATE_STARTED;

            // initialiaze the game result
            result = new GameResult();
            result.setStartTime(new Date());
            result.setChannel(this);

            // change the game state of the players
            Iterator it = slots.iterator();
            while (it.hasNext())
            {
                Client client = (Client) it.next();
                if (client != null && client.getUser().isPlayer())
                {
                    client.getUser().setPlaying(true);
                }
            }

            // clear the players' fields
            for (int i = 0; i < 6; i++)
            {
                fields[i].clear();
            }

            // send the newgame message
            NewGameMessage newgame = new NewGameMessage();
            newgame.setSlot(m.getSlot());
            newgame.setSettings(channelConfig.getSettings());
            if (channelConfig.getSettings().getSameBlocks())
            {
                Random random = new Random();
                newgame.setSeed(random.nextInt());
            }

            sendAll(newgame);
        }
    }

    private void process(StopGameMessage m)
    {
        EndGameMessage end = new EndGameMessage();
        end.setSlot(m.getSlot());
        sendMessage(end);
    }

    private void process(EndGameMessage m)
    {
        if (gameState != GAME_STATE_STOPPED)
        {
            gameState = GAME_STATE_STOPPED;
            sendAll(m);

            // update the status of the remaining players
            for (int i = 0 ; i < slots.size(); i++)
            {
                Client client = (Client) slots.get(i);
                if (client != null)
                {
                    client.getUser().setPlaying(false);
                }
            }
        }
    }

    private void process(DisconnectedMessage m)
    {
        Client client = m.getClient();
        removeClient(client);

        PlineMessage disconnected = new PlineMessage();
        disconnected.setKey("channel.disconnected", client.getUser().getName());
        sendAll(disconnected);
    }

    /*private void process(LeaveMessage m)
    {
        int slot = m.getSlot();
        players.set(slot - 1, null);
        sendAll(m);
    }*/

    private void process(AddPlayerMessage m)
    {
        Client client = m.getClient();

        Channel previousChannel = client.getChannel();
        client.setChannel(this);

        // remove the client from the previous channel
        if (previousChannel != null)
        {
            // clear the player list
            for (int j = 1; j <= 6; j++)
            {
                if (previousChannel.getPlayer(j) != null)
                {
                    LeaveMessage clear = new LeaveMessage();
                    clear.setSlot(j);
                    client.sendMessage(clear);
                }
            }

            previousChannel.removeClient(client);

            // send a message to the previous channel announcing what channel the player joined
            PlineMessage announce = new PlineMessage();
            announce.setKey("channel.join_notice", client.getUser().getName(), channelConfig.getName());
            previousChannel.sendMessage(announce);

            // clear the game status of the player
            if (client.getUser().isPlaying())
            {
                client.getUser().setPlaying(false);
                client.sendMessage(new EndGameMessage());
            }
        }

        // add the client to the channel
        clients.add(client);

        if (client.getUser().isSpectator())
        {
            // announce the new player to the other clients in the channel
            JoinMessage mjoin = new JoinMessage();
            mjoin.setName(client.getUser().getName());
            sendAll(mjoin, client);

            // send a boggus slot number for gtetrinet
            PlayerNumMessage mnum = new PlayerNumMessage();
            mnum.setSlot(1);
            client.sendMessage(mnum);
        }
        else
        {
            // looking for the first free slot
            int slot = 0;
            for (slot = 0; slot < slots.size() && slots.get(slot) != null; slot++) ;

            if (slot >= 6)
            {
                log.warning("[" + getConfig().getName() + "] Panic, no slot available for " + client);
                client.getUser().setSpectator();
            }
            else
            {
                slots.set(slot, client);

                // announce the new player to the other clients in the channel
                JoinMessage mjoin = new JoinMessage();
                mjoin.setSlot(slot + 1);
                mjoin.setName(client.getUser().getName());
                sendAll(mjoin, client);

                // send the slot number assigned to the new player
                PlayerNumMessage mnum = new PlayerNumMessage();
                mnum.setSlot(slot + 1);
                client.sendMessage(mnum);
            }
        }

        // send the list of players
        for (int i = 0; i < slots.size(); i++)
        {
            Client resident = (Client) slots.get(i);
            if (resident != null && resident != client)
            {
                // players...
                JoinMessage mjoin2 = new JoinMessage();
                mjoin2.setSlot(i + 1);
                mjoin2.setName(resident.getUser().getName()); // NPE
                client.sendMessage(mjoin2);

                // ...and teams
                TeamMessage mteam = new TeamMessage();
                mteam.setSlot(i + 1);
                mteam.setName(resident.getUser().getTeam());
                client.sendMessage(mteam);
            }
        }

        // send the fields
        for (int i = 0; i < 6; i++)
        {
            FieldMessage message = new FieldMessage(i + 1, fields[i].getFieldString());
            client.sendMessage(message);
        }

        // send the winlist
        Winlist winlist = WinlistManager.getInstance().getWinlist(channelConfig.getWinlistId());
        if (winlist != null)
        {
            List topScores = winlist.getScores(0, 10);
            WinlistMessage winlistMessage = new WinlistMessage();
            winlistMessage.setScores(topScores);
            client.sendMessage(winlistMessage);
        }

        // send a welcome message to the incomming client
        PlineMessage mwelcome = new PlineMessage();
        mwelcome.setKey("channel.welcome", client.getUser().getName(), channelConfig.getName());
        client.sendMessage(mwelcome);

        // send the message of the day
        if (channelConfig.getTopic() != null)
        {
            BufferedReader topic = new BufferedReader(new StringReader(channelConfig.getTopic()));
            String line = null;
            try
            {
                while ((line = topic.readLine()) != null)
                {
                    PlineMessage message = new PlineMessage();
                    message.setText("<kaki>" + line);
                    client.sendMessage(message);
                }
                topic.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        // send the list of spectators
        List specnames = new ArrayList();
        Iterator it = clients.iterator();
        while (it.hasNext())
        {
            Client c = (Client) it.next();
            if (c.getUser().isSpectator())
            {
                specnames.add(c.getUser().getName());
            }
        }

        if (specnames.size() > 0)
        {
            SpectatorListMessage spectators = new SpectatorListMessage();
            spectators.setChannel(getConfig().getName());
            spectators.setSpectators(specnames);
            client.sendMessage(spectators);
        }

        // send the status of the game to the new client
        if (gameState != GAME_STATE_STOPPED)
        {
            client.sendMessage(new IngameMessage());

            // tell the player if the game is currently paused
            if (gameState == GAME_STATE_PAUSED)
            {
                client.sendMessage(new PauseMessage());
            }
        }

    }

    public void process(PlayerSwitchMessage m)
    {
        if (gameState == GAME_STATE_STOPPED)
        {
            // get the players at the specified slots
            Client player1 = getClient(m.getSlot1());
            Client player2 = getClient(m.getSlot2());

            // swap the players
            slots.set(m.getSlot1() - 1, player2);
            slots.set(m.getSlot2() - 1, player1);

            // make the change visible to all clients
            if (player1 != null)
            {
                LeaveMessage leave1 = new LeaveMessage();
                leave1.setSlot(m.getSlot1());
                sendAll(leave1);
            }

            if (player2 != null)
            {
                LeaveMessage leave2 = new LeaveMessage();
                leave2.setSlot(m.getSlot2());
                sendAll(leave2);
            }

            if (player1 != null)
            {
                JoinMessage mjoin = new JoinMessage();
                mjoin.setSlot(m.getSlot2());
                mjoin.setName(player1.getUser().getName());
                sendAll(mjoin);

                PlayerNumMessage mnum = new PlayerNumMessage();
                mnum.setSlot(m.getSlot2());
                player1.sendMessage(mnum);
            }

            if (player2 != null)
            {
                JoinMessage mjoin = new JoinMessage();
                mjoin.setSlot(m.getSlot1());
                mjoin.setName(player2.getUser().getName());
                sendAll(mjoin);

                PlayerNumMessage mnum = new PlayerNumMessage();
                mnum.setSlot(m.getSlot1());
                player2.sendMessage(mnum);
            }
        }
    }

    public void process(Message m)
    {
        log.finest("[" + channelConfig.getName() + "] Processing " + m);

        if (m instanceof CommandMessage) process((CommandMessage) m);
        else if (m instanceof FieldMessage) process((FieldMessage) m);
        else if (m instanceof SpecialMessage) process((SpecialMessage) m);
        else if (m instanceof LevelMessage) process((LevelMessage) m);
        else if (m instanceof PlayerLostMessage) process((PlayerLostMessage) m);
        else if (m instanceof TeamMessage) process((TeamMessage) m);
        else if (m instanceof PlineMessage) process((PlineMessage) m);
        else if (m instanceof GmsgMessage) process((GmsgMessage) m);
        else if (m instanceof SmsgMessage) process((SmsgMessage) m);
        else if (m instanceof PlineActMessage) process((PlineActMessage) m);
        else if (m instanceof PauseMessage) process((PauseMessage) m);
        else if (m instanceof ResumeMessage) process((ResumeMessage) m);
        else if (m instanceof StartGameMessage) process((StartGameMessage) m);
        else if (m instanceof StopGameMessage) process((StopGameMessage) m);
        else if (m instanceof EndGameMessage) process((EndGameMessage) m);
        else if (m instanceof DisconnectedMessage) process((DisconnectedMessage) m);
        else if (m instanceof PlayerSwitchMessage) process((PlayerSwitchMessage) m);
        //else if (m instanceof LeaveMessage) process((LeaveMessage)m);
        else if (m instanceof AddPlayerMessage) process((AddPlayerMessage) m);
        else
        {
            log.finest("[" + channelConfig.getName() + "] Message not processed " + m);
        }
    }

    /**
     * Remove the specified client from the channel.
     */
    public void removeClient(Client client)
    {
        if (client != null)
        {
            clients.remove(client);

            LeaveMessage leave = new LeaveMessage();
            leave.setName(client.getUser().getName());

            int slot = slots.indexOf(client);
            if (slot != -1)
            {
                slots.set(slot, null);
                leave.setSlot(slot + 1);
            }

            // update the result of the game
            if (gameState != GAME_STATE_STOPPED && client.getUser().isPlaying())
            {
                result.update(client.getUser(), false);
            }

            sendAll(leave);
        }

        // stop the game if the channel is now empty
        if (isEmpty() && running)
        {
            gameState = GAME_STATE_STOPPED;
        }
    }

    /**
     * Add a message to the channel MessageQueue.
     *
     * @param m message to add
     */
    public void sendMessage(Message m)
    {
        queue.put(m);
    }

    /**
     * Send a message to all players in this channel.
     *
     * @param m       the message to send
     */
    private void sendAll(Message m)
    {
        // @todo add a fast mode to iterate on players only for game messages
        Iterator it = clients.iterator();
        while (it.hasNext())
        {
            Client client = (Client) it.next();
            client.sendMessage(m);
        }
    }

    /**
     * Send a message to all players but the one in the specified slot.
     *
     * @param m       the message to send
     * @param slot    the slot to exclude
     */
    private void sendAll(Message m, int slot)
    {
        Client client = getClient(slot);
        sendAll(m, client);
    }

    /**
     * Send a message to all players but the specified client.
     *
     * @param m       the message to send
     * @param c       the client to exclude
     */
    private void sendAll(Message m, Client c)
    {
        // @todo add a fast mode to iterate on players only for game messages
        Iterator it = clients.iterator();
        while (it.hasNext())
        {
            Client client = (Client) it.next();
            if (client != c)
            {
                client.sendMessage(m);
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

        for (int i = 0; i < slots.size(); i++)
        {
            if (slots.get(i) != null)
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
    public int getClientSlot(Client client)
    {
        return (slots.indexOf(client) + 1);
    }

    /**
     * Returns the client in the specified slot.
     *
     * @param slot slot number between 1 and 6
     *
     * @return <tt>null</tt> if there is no client in the specified slot, or if the number is out of range
     */
    public Client getClient(int slot)
    {
        Client client = null;

        if (slot >= 1 && slot <= slots.size())
        {
            client = (Client) slots.get(slot - 1);
        }

        return client;
    }

    /**
     * Returns the client in the specified slot.
     *
     * @param slot slot number between 1 and 6
     *
     * @return <tt>null</tt> if there is no client in the specified slot, or if the number is out of range
     */
    public User getPlayer(int slot)
    {
        Client client = getClient(slot);
        return (client != null) ? client.getUser() : null;
    }

    /**
     * Return an iterator of players in this channel.
     */
    public Iterator getPlayers()
    {
        return slots.iterator();
    }

    /**
     * Return an iterator of spectators observing this channel.
     */
    public Iterator getSpectators()
    {
        return new FilterIterator(clients.iterator(), ClientRepository.SPECTATOR_PREDICATE);
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
        Map playingTeams = new HashMap();

        int nbTeamsLeft = 0;

        for (int i = 0; i < slots.size(); i++)
        {
            Client client = (Client) slots.get(i);

            if (client != null && client.getUser().isPlaying())
            {
                String team = client.getUser().getTeam();

                if (team == null)
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

    /**
     * Return the field of the specified slot.
     */
    public Field getField(int slot)
    {
        return fields[slot];
    }

}
