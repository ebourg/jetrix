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

    private static Predicate playerPredicate = new Predicate() {
            public boolean evaluate(Object obj)
            {
                Client client = (Client)obj;
                boolean isPlayer =
                    client.getType() == Client.CLIENT_TETRINET
                    || client.getType() == Client.CLIENT_TETRIFAST;
                return isPlayer;
            }
        };

    private static Predicate spectatorPredicate = new Predicate() {
            public boolean evaluate(Object obj)
            {
                Client client = (Client)obj;
                boolean isSpec = client.getType() == Client.CLIENT_TSPEC;
                return isSpec;
            }
        };

    private ClientRepository()
    {
        clients = new TreeMap();
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
        clients.put(client.getPlayer().getName().toLowerCase(), client);
    }

    /**
     * Remove a client from the repository.
     *
     * @param client the client to remove
     */
    public void removeClient(Client client)
    {
        clients.remove(client.getPlayer().getName().toLowerCase());
    }

    /**
     * Return an iterator of players online in alphabetical order.
     */
    public Iterator getPlayers()
    {
        return new FilterIterator(clients.values().iterator(), playerPredicate);
    }

    /**
     * Return the number of players connected to this server.
     */
    public int getPlayerCount()
    {
        return CollectionUtils.select(clients.values(), playerPredicate).size();
    }

    /**
     * Return an iterator of spectators online in alphabetical order.
     */
    public Iterator getSpectators()
    {
        return new FilterIterator(clients.values().iterator(), spectatorPredicate);
    }

    /**
     * Return the number of spectators connected to this server.
     */
    public int getSpectatorCount()
    {
        return CollectionUtils.select(clients.values(), spectatorPredicate).size();
    }

    /**
     * Return the client of the player or spectator with
     * the specified nickname.
     *
     * @param nickname nickname of the client to return
     */
    public Client getClient(String nickname)
    {
        return (Client)clients.get(nickname.toLowerCase());
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
        return count;
    }

    public void clear()
    {
        clients.clear();
    }

}
