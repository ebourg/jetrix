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

import java.net.*;
import java.util.*;

import org.apache.commons.collections.*;

/**
 * Repository of clients connected to the server.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ClientRepository
{
    private static ClientRepository instance = new ClientRepository();
    private Map clients;

    public static final Predicate PLAYER_PREDICATE = new Predicate()
    {
        public boolean evaluate(Object obj)
        {
            Client client = (Client) obj;
            return client.getUser().isPlayer();
        }
    };

    public static final Predicate SPECTATOR_PREDICATE = new Predicate()
    {
        public boolean evaluate(Object obj)
        {
            Client client = (Client) obj;
            return client.getUser().isSpectator();
        }
    };

    public static final Predicate OPERATOR_PREDICATE = new Predicate()
    {
        public boolean evaluate(Object obj)
        {
            Client client = (Client) obj;
            return client.getUser().getAccessLevel() > 0;
        }
    };

    private ClientRepository()
    {
        FastTreeMap map = new FastTreeMap();
        map.setFast(true);
        clients = map;
    }

    public static ClientRepository getInstance()
    {
        return instance;
    }

    /**
     * Add a client into the repository.
     *
     * @param client the client to add
     */
    public void addClient(Client client)
    {
        if (client != null)
        {
            clients.put(client.getUser().getName().toLowerCase(), client);
        }
    }

    /**
     * Remove a client from the repository.
     *
     * @param client the client to remove
     */
    public void removeClient(Client client)
    {
        if (client != null)
        {
            clients.remove(client.getUser().getName().toLowerCase());
        }
    }

    /**
     * Return an iterator of players online in alphabetical order.
     */
    public Iterator getPlayers()
    {
        return new FilterIterator(clients.values().iterator(), PLAYER_PREDICATE);
    }

    /**
     * Return the number of players connected to this server.
     */
    public int getPlayerCount()
    {
        return CollectionUtils.select(clients.values(), PLAYER_PREDICATE).size();
    }

    /**
     * Return an iterator of spectators online in alphabetical order.
     */
    public Iterator getSpectators()
    {
        return new FilterIterator(clients.values().iterator(), SPECTATOR_PREDICATE);
    }

    /**
     * Return the number of spectators connected to this server.
     */
    public int getSpectatorCount()
    {
        return CollectionUtils.select(clients.values(), SPECTATOR_PREDICATE).size();
    }

    /**
     * Return an iterator of operators online in alphabetical order.
     */
    public Iterator getOperators()
    {
        return new FilterIterator(clients.values().iterator(), OPERATOR_PREDICATE);
    }

    /**
     * Return the client of the player or spectator with
     * the specified nickname.
     *
     * @param nickname nickname of the client to return
     */
    public Client getClient(String nickname)
    {
        return (nickname == null) ? null : (Client) clients.get(nickname.toLowerCase());
    }

    /**
     * Return an iterator of all clients online in alphabetical order.
     */
    public Iterator getClients()
    {
        return clients.values().iterator();
    }

    /**
     * Return the number of clients connected to this server.
     */
    public int getClientCount()
    {
        return clients.size();
    }

    /**
     * Return the number of clients connected from the specified
     * internet address.
     *
     * @param address
     */
    public int getHostCount(InetAddress address)
    {
        int count = 0;

        Iterator it = clients.values().iterator();
        while (it.hasNext())
        {
            Client client = (Client) it.next();
            if (address.equals(client.getInetAddress()))
            {
                count++;
            }
        }

        return count;
    }

    public void clear()
    {
        clients.clear();
    }

}
