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
import junit.framework.*;
import net.jetrix.clients.*;

/**
 * JUnit TestCase for the class net.jetrix.ClientRepository
 *
 * @author Emmanuel Bourg
 * @version $Revision, $Date$
 */
public class ClientRepositoryTest extends TestCase
{
    private ClientRepository repository;
    private Client client1, client2, client3, client4;
    private Player player1, player2, player3, player4;

    public void setUp()
    {
        repository = ClientRepository.getInstance();
        client1 = new TetrinetClient();
        client1.setPlayer(new Player("Stormcat"));
        client1.setType(Client.CLIENT_TETRINET);

        client2 = new TetrinetClient();
        client2.setPlayer(new Player("Smanux"));
        client2.setType(Client.CLIENT_TETRINET);

        client3 = new TetrinetClient();
        client3.setPlayer(new Player("Jetrix"));
        client3.setType(Client.CLIENT_TETRIFAST);

        client4 = new TetrinetClient();
        client4.setPlayer(new Player("Test"));
        client4.setType(Client.CLIENT_TSPEC);
    }

    public void tearDown()
    {
        repository.clear();
    }

    public void testGetClient()
    {
        repository.addClient(client1);
        assertEquals("client count after adding the client", 1, repository.getClientCount());
        assertEquals(client1, repository.getClient(client1.getPlayer().getName()));
        assertEquals("non case sensitive search", client1, repository.getClient(client1.getPlayer().getName().toUpperCase()));
        assertEquals("non case sensitive search", client1, repository.getClient(client1.getPlayer().getName().toLowerCase()));
    }

    public void testGetClients()
    {
        // adding clients to the repository
        repository.addClient(client1);
        repository.addClient(client2);
        repository.addClient(client3);
        repository.addClient(client4);

        // looking for players
        StringBuffer playerList = new StringBuffer();
        Iterator players = repository.getPlayers();
        while (players.hasNext())
        {
            Client client = (Client)players.next();
            playerList.append(client.getPlayer().getName());
            if (players.hasNext()) playerList.append(", ");
        }

        // looking for spectators
        StringBuffer spectatorList = new StringBuffer();
        Iterator spectators = repository.getSpectators();
        while (spectators.hasNext())
        {
            Client client = (Client)spectators.next();
            spectatorList.append(client.getPlayer().getName());
            if (spectators.hasNext()) spectatorList.append(", ");
        }

        // testing
        assertEquals("getPlayers", "Jetrix, Smanux, Stormcat", playerList.toString());
        assertEquals("getSpectators", "Test", spectatorList.toString());
    }

    public void testGetClientCount()
    {
        // adding clients to the repository
        repository.addClient(client1);
        repository.addClient(client2);
        repository.addClient(client3);
        repository.addClient(client4);

        assertEquals("getPlayerCount", 3, repository.getPlayerCount());
        assertEquals("getSpectatorCount", 1, repository.getSpectatorCount());
    }

    public void testRemoveClient()
    {
        // adding clients to the repository
        repository.addClient(client1);
        repository.addClient(client2);

        // removing clients from the repository
        repository.removeClient(client1);
        repository.removeClient(client2);

        // testing
        assertEquals("client count after removal", 0, repository.getClientCount());
    }

    public void testClear()
    {
        // adding clients to the repository
        repository.addClient(client1);
        repository.addClient(client2);
        repository.addClient(client3);
        repository.addClient(client4);
        repository.clear();

        // testing
        assertEquals("client count after clearing", 0, repository.getClientCount());
    }

    public static Test suite()
    {
        return new TestSuite(ClientRepositoryTest.class);
    }

}
