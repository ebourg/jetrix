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

package net.jetrix.winlist;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;
import net.jetrix.User;

/**
 * JUnit TestCase for the class net.jetrix.winlist.GameResult.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class GameResultTest extends TestCase
{
    public void testUpdate()
    {
        GameResult result = new GameResult();
        User user1 = new User("user1");
        User user2 = new User("user2");
        result.update(user1, true);
        result.update(user2, false);

        Collection players = result.getGamePlayers();
        assertNotNull("null collection of players", players);
        assertEquals("number of players", 2, players.size());

        Iterator it = players.iterator();
        while (it.hasNext())
        {
            GamePlayer player = (GamePlayer) it.next();
            assertTrue("player name", "user1".equals(player.getName()) || "user2".equals(player.getName()));
            if ("user1".equals(player.getName()))
            {
                assertTrue("user1 won", player.isWinner());
            }
            else
            {
                assertFalse("user2 lost", player.isWinner());
            }
        }
    }

    public void testGetPlayerAtRank() throws Exception
    {
        // create a GameResult
        GameResult result = new GameResult();
        User user1a = new User("user1a");
        User user1b = new User("user1b");
        User user2 = new User("user2");
        User user3 = new User("user3");
        User user4 = new User("user4");

        result.update(user4, false);
        Thread.sleep(100);
        result.update(user3, false);
        Thread.sleep(100);
        result.update(user2, false);
        Thread.sleep(100);
        result.update(user1b, true);
        result.update(user1a, true);

        // test the result
        Collection rank1 = result.getPlayersAtRank(1);
        Collection rank2 = result.getPlayersAtRank(2);
        Collection rank3 = result.getPlayersAtRank(3);
        Collection rank4 = result.getPlayersAtRank(4);
        Collection rank5 = result.getPlayersAtRank(5);

        assertEquals("number of players", 5, result.getGamePlayers().size());
        assertEquals("number of players at the 1st rank", 2, rank1.size());
        assertEquals("number of players at the 2nd rank", 1, rank2.size());
        assertEquals("number of players at the 3rd rank", 1, rank3.size());
        assertEquals("number of players at the 4th rank", 1, rank4.size());
        assertEquals("number of players at the 5th rank", 0, rank5.size());

        Iterator it = rank1.iterator();
        while (it.hasNext())
        {
            GamePlayer player = (GamePlayer) it.next();
            assertEquals("player at the 1st rank", "user1", player.getName().substring(0, 5));
        }

        it = rank2.iterator();
        while (it.hasNext())
        {
            GamePlayer player = (GamePlayer) it.next();
            assertEquals("player at the 2nd rank", "user2", player.getName());
        }

        it = rank3.iterator();
        while (it.hasNext())
        {
            GamePlayer player = (GamePlayer) it.next();
            assertEquals("player at the 3rd rank", "user3", player.getName());
        }

        it = rank4.iterator();
        while (it.hasNext())
        {
            GamePlayer player = (GamePlayer) it.next();
            assertEquals("player at the 4th rank", "user4", player.getName());
        }
    }

    public void testGetTeamCount()
    {
        // create a GameResult
        GameResult result = new GameResult();
        User user1 = new User("user1");
        User user2 = new User("user2");
        User user3 = new User("user3");
        User user4 = new User("user4");
        User user5 = new User("user5");
        User user6 = new User("user6");

        user1.setTeam("team1");
        user2.setTeam("team1");
        user3.setTeam("team2");
        user4.setTeam("team2");

        result.update(user1, true);
        result.update(user2, false);
        result.update(user3, false);
        result.update(user4, false);
        result.update(user5, false);
        result.update(user6, false);

        // test the result
        assertEquals("number of teams", 4, result.getTeamCount());
    }
}
