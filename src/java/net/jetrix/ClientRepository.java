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

import java.net.InetAddress;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Repository of clients connected to the server.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ClientRepository
{
    private static ClientRepository instance = new ClientRepository();
    private Map<String, Client> clients = new ConcurrentSkipListMap<String, Client>();

    private ClientRepository()
    {
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
    public Iterator<Client> getPlayers()
    {
        List<Client> player = new ArrayList<Client>();

        for (Client client : clients.values())
        {
            if (client.getUser().isPlayer())
            {
                player.add(client);
            }
        }

        return player.iterator();
    }

    /**
     * Return the number of players connected to this server.
     */
    public int getPlayerCount()
    {
        int count = 0;
        for (Client client : clients.values())
        {
            if (client.getUser().isPlayer())
            {
                count++;
            }
        }

        return count;
    }

    /**
     * Return an iterator of spectators online in alphabetical order.
     */
    public Iterator<Client> getSpectators()
    {
        List<Client> spectators = new ArrayList<Client>();

        for (Client client : clients.values())
        {
            if (client.getUser().isSpectator())
            {
                spectators.add(client);
            }
        }

        return spectators.iterator();
    }

    /**
     * Return the number of spectators connected to this server.
     */
    public int getSpectatorCount()
    {
        int count = 0;
        for (Client client : clients.values())
        {
            if (client.getUser().isSpectator())
            {
                count++;
            }
        }

        return count;
    }

    /**
     * Return an iterator of operators online in alphabetical order.
     */
    public Iterator<Client> getOperators()
    {
        List<Client> operators = new ArrayList<Client>();

        for (Client client : clients.values())
        {
            if (client.getUser().getAccessLevel() > 0)
            {
                operators.add(client);
            }
        }

        return operators.iterator();
    }

    /**
     * Return the client of the player or spectator with
     * the specified nickname.
     *
     * @param nickname nickname of the client to return
     */
    public Client getClient(String nickname)
    {
        return (nickname == null) ? null : clients.get(nickname.toLowerCase());
    }

    /**
     * Return an iterator of all clients online in alphabetical order.
     */
    public Collection<Client> getClients()
    {
        return clients.values();
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

        for (Client client : clients.values())
        {
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
