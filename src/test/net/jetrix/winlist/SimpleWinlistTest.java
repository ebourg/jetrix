/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2004  Emmanuel Bourg
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
import net.jetrix.config.*;

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

        result.update(user2, false);
        result.update(user1, true);

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

        result.update(user2, false);
        result.update(user1, true);

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

        result.update(user3, false);
        result.update(user2, false);
        result.update(user1, true);

        winlist.saveGameResult(result);

        Score score1 = winlist.getScore("user1", Score.TYPE_PLAYER);
        Score score2 = winlist.getScore("user2", Score.TYPE_PLAYER);
        Score score3 = winlist.getScore("user3", Score.TYPE_PLAYER);
        assertNotNull("score of the 1st player", score1);
        assertNull("score of the 2nd player", score2);
        assertNull("score of the 3rd player", score3);
        assertEquals("score of the 1st player", 3, score1.getScore());
    }

    public void testSaveGameResult4() throws Exception
    {
        User user1 = new User("user1");
        User user2 = new User("user2");
        User user3 = new User("user3");
        User user4 = new User("user4");
        User user5 = new User("user5");
        User user6 = new User("user6");

        result.update(user6, false);
        Thread.sleep(100);
        result.update(user5, false);
        Thread.sleep(100);
        result.update(user4, false);
        Thread.sleep(100);
        result.update(user3, false);
        Thread.sleep(100);
        result.update(user2, false);
        Thread.sleep(100);
        result.update(user1, true);

        winlist.saveGameResult(result);

        Score score2 = winlist.getScore("user2", Score.TYPE_PLAYER);
        assertNotNull("score of the 2nd player null", score2);
        assertEquals("score of the 1st player", 1, score2.getScore());

        assertNull("score of the 3rd player not null", winlist.getScore("user3", Score.TYPE_PLAYER));
        assertNull("score of the 4th player not null", winlist.getScore("user4", Score.TYPE_PLAYER));
        assertNull("score of the 5th player not null", winlist.getScore("user5", Score.TYPE_PLAYER));
        assertNull("score of the 6th player not null", winlist.getScore("user6", Score.TYPE_PLAYER));
    }

    public void testSaveLoad()
    {
        WinlistConfig config = new WinlistConfig();
        SimpleWinlist winlist = getWinlist();
        winlist.setId("test");
        winlist.init(config);

        // add two scores to the list
        Score score1 = new Score();
        score1.setName("LFJR");
        score1.setScore(4321);
        score1.setType(Score.TYPE_TEAM);

        Score score2 = new Score();
        score2.setName("Smanux");
        score2.setScore(1234);
        score2.setType(Score.TYPE_PLAYER);

        winlist.scores.add(score1);
        winlist.scores.add(score2);

        // save the list
        winlist.save();

        // read the list
        SimpleWinlist winlist2 = getWinlist();
        winlist2.setId("test");
        winlist2.init(config);
        winlist2.load();

        assertEquals("winlist size", winlist.scores.size(), winlist2.scores.size());
        assertTrue("score 1 missing", winlist2.scores.contains(score1));
        assertTrue("score 2 missing", winlist2.scores.contains(score2));
    }

    public void testClear()
    {
        SimpleWinlist winlist = getWinlist();
        winlist.setId("test");
        winlist.init(new WinlistConfig());

        // add two scores to the list
        Score score = new Score();
        score.setName("LFJR");
        score.setScore(4321);
        score.setType(Score.TYPE_TEAM);

        winlist.scores.add(score);

        assertNotNull("score not found", winlist.getScore(score.getName(), score.getType()));

        winlist.clear();

        assertNull("score not cleared", winlist.getScore(score.getName(), score.getType()));
    }

    public void testSize()
    {
        SimpleWinlist winlist = getWinlist();

        // add two scores to the list
        Score score = new Score();
        score.setName("LFJR");
        score.setScore(4321);
        score.setType(Score.TYPE_TEAM);

        winlist.scores.add(score);

        assertEquals("size of the winlist", 1, winlist.size());
    }

    protected SimpleWinlist getWinlist()
    {
        return new SimpleWinlist();
    }

}
