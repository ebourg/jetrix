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

import junit.framework.TestCase;
import net.jetrix.User;

/**
 * JUnit TestCase for the class net.jetrix.winlist.SimpleWinlist.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class SimpleWinlistTest extends TestCase {

    private SimpleWinlist winlist;
    private GameResult result;

    public void setUp()
    {
        winlist = new SimpleWinlist();
        winlist.setPersistent(false);
        result = new GameResult();
    }

    public void testSaveGameResult1()
    {
        User user1 = new User("user1");
        User user2 = new User("user2");

        result.update(user1, true);
        result.update(user2, false);

        winlist.saveGameResult(result);

        Score score1 = winlist.getScore("user1", Score.TYPE_PLAYER);
        Score score2 = winlist.getScore("user2", Score.TYPE_PLAYER);
        assertNotNull("score of the 1st player", score1);
        assertNull("score of the 2nd player", score2);
        assertEquals("score of the 1st player", 2, score1.getScore());
    }

    public void testSaveGameResult2()
    {
        User user1 = new User("user1");
        User user2 = new User("user2");
        user1.setTeam("team1");

        result.update(user1, true);
        result.update(user2, false);

        winlist.saveGameResult(result);

        Score score1 = winlist.getScore("user1", Score.TYPE_PLAYER);
        Score score2 = winlist.getScore("user2", Score.TYPE_PLAYER);
        Score scoreT1 = winlist.getScore("team1", Score.TYPE_TEAM);
        assertNull("score of the 1st player", score1);
        assertNull("score of the 2nd player", score2);
        assertNotNull("score of the 1st team", scoreT1);
        assertEquals("score of the 1st player", 2, scoreT1.getScore());
    }

    public void testSaveGameResult3()
    {
        User user1 = new User("user1");
        User user2 = new User("user2");
        User user3 = new User("user3");

        result.update(user1, true);
        result.update(user2, false);
        result.update(user3, false);

        winlist.saveGameResult(result);

        Score score1 = winlist.getScore("user1", Score.TYPE_PLAYER);
        Score score2 = winlist.getScore("user2", Score.TYPE_PLAYER);
        Score score3 = winlist.getScore("user3", Score.TYPE_PLAYER);
        assertNotNull("score of the 1st player", score1);
        assertNull("score of the 2nd player", score2);
        assertNull("score of the 3rd player", score3);
        assertEquals("score of the 1st player", 3, score1.getScore());
    }

    public void testSaveGameResult4()
    {
        User user1 = new User("user1");
        User user2 = new User("user2");
        User user3 = new User("user3");
        User user4 = new User("user4");
        User user5 = new User("user5");

        result.update(user1, true);
        result.update(user2, false);
        result.update(user3, false);
        result.update(user4, false);
        result.update(user5, false);

        winlist.saveGameResult(result);

        Score score2 = winlist.getScore("user2", Score.TYPE_PLAYER);
        assertNotNull("score of the 2nd player", score2);
        assertEquals("score of the 1st player", 1, score2.getScore());
    }

}
