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

import static net.jetrix.GameState.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import java.net.*;

import net.jetrix.config.*;
import net.jetrix.filter.*;
import net.jetrix.messages.*;
import net.jetrix.winlist.*;
import net.jetrix.clients.TetrinetClient;

import org.apache.commons.collections.*;

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

    private BlockingQueue<Message> queue;

    private boolean open;
    private GameState gameState;
    private boolean running = true;
    private GameResult result;

    /** The start time of the channel */
    private long startTime;

    /** set of clients connected to this channel */
    private Set<Client> clients;

    /** slot/player mapping */
    private List<Client> slots;
    private Field[] fields = new Field[6];

    private List<MessageFilter> filters;

    public Channel()
    {
        this(new ChannelConfig());
    }

    public Channel(ChannelConfig channelConfig)
    {
        this.channelConfig = channelConfig;
        this.serverConfig = Server.getInstance().getConfig();
        this.gameState = STOPPED;
        this.clients = new HashSet<Client>();
        this.slots = new ArrayList<Client>(6);

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
        queue = new LinkedBlockingQueue<Message>();

        filters = new ArrayList<MessageFilter>();

        /**
         * Loading filters
         */

        // global filters
        Iterator<FilterConfig> globalFilters = serverConfig.getGlobalFilters();
        while (globalFilters.hasNext())
        {
            addFilter(globalFilters.next());
        }

        // channel filters
        Iterator<FilterConfig> channelFilters = channelConfig.getFilters();
        while (channelFilters.hasNext())
        {
            addFilter(channelFilters.next());
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
            filter.setConfig(filterConfig);
            filter.init();

            // add the filter to the list
            filters.add(filter);
        }
        catch (FilterException e)
        {
            log.log(Level.WARNING, e.getMessage(), e);
        }
    }

    public void removeFilter(String filterName)
    {
        // remove the filter from the filter list
        // @todo filter removal

        // remove the filter from the channel config
    }

    public Iterator<MessageFilter> getFilters()
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

        startTime = System.currentTimeMillis();

        while (running && serverConfig.isRunning()
                && (getConfig().isPersistent() || !clients.isEmpty() || (System.currentTimeMillis() - startTime < 200)))
        {
            LinkedList<Message> list = new LinkedList<Message>();

            try
            {
                // waiting for new messages
                list.add(queue.take());

                // filter the message
                for (MessageFilter filter : filters)
                {
                    int size = list.size();
                    for (int i = 0; i < size; i++)
                    {
                        filter.process(list.removeFirst(), list);
                    }
                }

                // process the message(s)
                while (!list.isEmpty())
                {
                    process(list.removeFirst());
                }
            }
            catch (Exception e)
            {
                log.log(Level.WARNING, e.getMessage(), e);
            }

        }

        // unregister the channel
        if (serverConfig.isRunning())
        {
            ChannelManager.getInstance().removeChannel(this);
        }

        log.info("Channel " + channelConfig.getName() + " closed");
    }

    /**
     * Stop the channel.
     */
    public void close()
    {
        running = false;
        queue.add(new ShutdownMessage());
    }

    private void process(CommandMessage m)
    {
        // forwards the command to the server
        Server.getInstance().send(m);
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
            for (Client client : clients)
            {
                if (client.getUser().isSpectator() && client != m.getSource())
                {
                    client.send(m);
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
        gameState = PAUSED;

        // tell who paused the game if the message comes from a client
        if (m.getSource() instanceof Client)
        {
            Client client = (Client) m.getSource();
            PlineMessage message = new PlineMessage();
            message.setKey("channel.game.paused-by", client.getUser().getName());
            sendAll(message);
        }

        sendAll(m);
    }

    private void process(ResumeMessage m)
    {
        gameState = STARTED;

        // tell who resumed the game if the message comes from a client
        if (m.getSource() instanceof Client)
        {
            Client client = (Client) m.getSource();
            PlineMessage message = new PlineMessage();
            message.setKey("channel.game.resumed-by", client.getUser().getName());
            sendAll(message);
        }

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
            send(endgame);
            result.setEndTime(new Date());

            // looking for the slot of the winner
            slot = 0;
            for (int i = 0; i < slots.size(); i++)
            {
                client = slots.get(i);

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
                send(playerwon);

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
                send(announce);
            }

            // update the winlist with the final result
            Winlist winlist = WinlistManager.getInstance().getWinlist(channelConfig.getWinlistId());
            if (winlist != null)
            {
                winlist.saveGameResult(result);

                List<Score> topScores = winlist.getScores(0, 10);
                WinlistMessage winlistMessage = new WinlistMessage();
                winlistMessage.setScores(topScores);
                sendAll(winlistMessage);
            }

            // update the server statistics
            serverConfig.getStatistics().increaseGameCount();
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
        if (gameState == STOPPED)
        {
            // change the channel state
            gameState = STARTED;

            // tell who started the game if the message comes from a client
            if (m.getSource() instanceof Client)
            {
                Client client = (Client) m.getSource();
                PlineMessage message = new PlineMessage();
                message.setKey("channel.game.started-by", client.getUser().getName());
                sendAll(message);
            }

            // initialiaze the game result
            result = new GameResult();
            result.setStartTime(new Date());
            result.setChannel(this);

            // change the game state of the players
            for (Client client : slots)
            {
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
        end.setSource(m.getSource());
        send(end);
    }

    private void process(EndGameMessage m)
    {
        if (gameState != STOPPED)
        {
            // tell who stopped the game if the message comes from a client
            if (m.getSource() instanceof Client)
            {
                Client client = (Client) m.getSource();
                PlineMessage message = new PlineMessage();
                message.setKey("channel.game.stopped-by", client.getUser().getName());
                sendAll(message);
            }

            gameState = STOPPED;
            sendAll(m);

            // update the status of the remaining players
            for (Client client : slots)
            {
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

    private void process(LeaveMessage m)
    {
        removeClient((Client) m.getSource());
    }

    private void process(AddPlayerMessage m)
    {
        Client client = m.getClient();

        Channel previousChannel = client.getChannel();
        client.setChannel(this);

        // remove the client from the previous channel if it doesn't support multiple channels
        if (previousChannel != null && !client.supportsMultipleChannels())
        {
            // clear the player list
            for (int j = 1; j <= 6; j++)
            {
                if (previousChannel.getPlayer(j) != null)
                {
                    LeaveMessage clear = new LeaveMessage();
                    clear.setSlot(j);
                    client.send(clear);
                }
            }

            previousChannel.removeClient(client);

            // send a message to the previous channel announcing what channel the player joined
            PlineMessage announce = new PlineMessage();
            announce.setKey("channel.join_notice", client.getUser().getName(), channelConfig.getName());
            previousChannel.send(announce);

            // clear the game status of the player
            if (client.getUser().isPlaying())
            {
                client.getUser().setPlaying(false);
                client.send(new EndGameMessage());
            }
        }

        // add the client to the channel
        clients.add(client);

        if (client.getUser().isSpectator())
        {
            // announce the new player to the other clients in the channel
            JoinMessage mjoin = new JoinMessage();
            mjoin.setName(client.getUser().getName());
            //sendAll(mjoin, client);
            sendAll(mjoin);

            // send a boggus slot number for gtetrinet
            PlayerNumMessage mnum = new PlayerNumMessage();
            mnum.setSlot(1);
            client.send(mnum);
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
                client.send(mnum);
            }
        }

        // send the list of spectators
        if (client.getUser().isSpectator())
        {
            sendSpectatorList(client);
        }

        // send the list of players
        for (int i = 0; i < slots.size(); i++)
        {
            Client resident = slots.get(i);
            if (resident != null && resident != client)
            {
                // players...
                JoinMessage mjoin2 = new JoinMessage();
                mjoin2.setChannel(this);
                mjoin2.setSlot(i + 1);
                mjoin2.setName(resident.getUser().getName()); // NPE
                client.send(mjoin2);

                // ...and teams
                TeamMessage mteam = new TeamMessage();
                mteam.setChannel(this);
                mteam.setSource(resident);
                mteam.setSlot(i + 1);
                mteam.setName(resident.getUser().getTeam());
                client.send(mteam);
            }
        }

        // send the fields
        for (int i = 0; i < 6; i++)
        {
            FieldMessage message = new FieldMessage(i + 1, fields[i].getFieldString());
            client.send(message);
        }

        // send the winlist
        Winlist winlist = WinlistManager.getInstance().getWinlist(channelConfig.getWinlistId());
        if (winlist != null)
        {
            List<Score> topScores = winlist.getScores(0, 10);
            WinlistMessage winlistMessage = new WinlistMessage();
            winlistMessage.setScores(topScores);
            client.send(winlistMessage);
        }

        // send a welcome message to the incomming client
        PlineMessage mwelcome = new PlineMessage();
        mwelcome.setKey("channel.welcome", client.getUser().getName(), channelConfig.getName());
        client.send(mwelcome);

        // send the topic of the channel
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
                    client.send(message);
                }
                topic.close();
            }
            catch (Exception e)
            {
                log.log(Level.WARNING, e.getMessage(), e);
            }
        }

        // send the list of spectators
        if (client.getUser().isPlayer())
        {
            sendSpectatorList(client);
        }

        // send the status of the game to the new client
        if (gameState != STOPPED)
        {
            IngameMessage ingame = new IngameMessage();
            ingame.setChannel(this);
            client.send(ingame);

            // tell the player if the game is currently paused
            if (gameState == PAUSED)
            {
                client.send(new PauseMessage());
            }
        }

        // adjust the timeout
        if (client instanceof TetrinetClient)
        {
            int timeout = getConfig().isIdleAllowed() ? 0 : serverConfig.getTimeout() * 1000;
            try
            {
                ((TetrinetClient) client).getSocket().setSoTimeout(timeout);
            }
            catch (SocketException e)
            {
                log.log(Level.WARNING, "Unable to change the timeout", e);
            }
        }
    }

    /**
     * Send the list of spectators in this channel to the specified client.
     */
    private void sendSpectatorList(Client client)
    {
        // extract the list of spectators
        List<String> specnames = new ArrayList<String>();

        for (Client c : clients)
        {
            if (c.getUser().isSpectator())
            {
                specnames.add(c.getUser().getName());
            }
        }

        // send the list of spectators
        if (!specnames.isEmpty())
        {
            SpectatorListMessage spectators = new SpectatorListMessage();
            spectators.setDestination(client);
            spectators.setChannel(getConfig().getName());
            spectators.setSpectators(specnames);
            client.send(spectators);
        }
    }

    public void process(PlayerSwitchMessage m)
    {
        if (gameState == STOPPED)
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
                player1.send(mnum);
            }

            if (player2 != null)
            {
                JoinMessage mjoin = new JoinMessage();
                mjoin.setSlot(m.getSlot1());
                mjoin.setName(player2.getUser().getName());
                sendAll(mjoin);

                PlayerNumMessage mnum = new PlayerNumMessage();
                mnum.setSlot(m.getSlot1());
                player2.send(mnum);
            }
        }
    }

    public void process(Message m)
    {
        if (log.isLoggable(Level.FINEST))
        {
            log.finest("[" + channelConfig.getName() + "] Processing " + m);
        }

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
        else if (m instanceof LeaveMessage) process((LeaveMessage) m);
        else if (m instanceof AddPlayerMessage) process((AddPlayerMessage) m);
        else
        {
            if (log.isLoggable(Level.FINEST))
            {
                log.finest("[" + channelConfig.getName() + "] Message not processed " + m);
            }
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
            if (gameState != STOPPED && client.getUser().isPlaying())
            {
                result.update(client.getUser(), false);
            }

            sendAll(leave);
        }

        // stop the game if the channel is now empty
        if (isEmpty() && running)
        {
            gameState = STOPPED;
        }

        // stop the channel if it's not persistent
        if (clients.isEmpty() && !getConfig().isPersistent())
        {
            send(new ShutdownMessage());
        }
    }

    /**
     * Add a message to the channel message queue.
     *
     * @param message message to add
     */
    public void send(Message message)
    {
        queue.add(message);
    }

    /**
     * Send a message to all players in this channel.
     *
     * @param message the message to send
     */
    private void sendAll(Message message)
    {
        if (message.getDestination() == null)
        {
            message.setDestination(this);
        }

        // @todo add a fast mode to iterate on players only for game messages
        for (Client client : clients)
        {
            client.send(message);
        }
    }

    /**
     * Send a message to all players but the one in the specified slot.
     *
     * @param message the message to send
     * @param slot    the slot to exclude
     */
    private void sendAll(Message message, int slot)
    {
        Client client = getClient(slot);
        sendAll(message, client);
    }

    /**
     * Send a message to all players but the specified client.
     *
     * @param message the message to send
     * @param c       the client to exclude
     */
    private void sendAll(Message message, Client c)
    {
        if (message.getDestination() == null)
        {
            message.setDestination(this);
        }

        // @todo add a fast mode to iterate on players only for game messages
        for (Client client : clients)
        {
            if (client != c)
            {
                client.send(message);
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
    public GameState getGameState()
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
            client = slots.get(slot - 1);
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
    public Iterator<Client> getPlayers()
    {
        return slots.iterator();
    }

    /**
     * Return an iterator of spectators observing this channel.
     */
    public Iterator<Client> getSpectators()
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
        Map<String, String> playingTeams = new HashMap<String, String>();

        int nbTeamsLeft = 0;

        for (int i = 0; i < slots.size(); i++)
        {
            Client client = slots.get(i);

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
