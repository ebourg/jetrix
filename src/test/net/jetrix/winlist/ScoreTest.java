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

/**
 * JUnit TestCase for the class net.jetrix.winlist.Score.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ScoreTest extends TestCase
{
    public void testEquals()
    {
        Score score1 = new Score();
        score1.setName("user1");
        score1.setType(Score.TYPE_PLAYER);
        score1.setScore(1000);

        Score score2 = new Score();
        score2.setName("user1");
        score2.setType(Score.TYPE_PLAYER);
        score2.setScore(2000);

        Score score3 = new Score();
        score3.setName("user1");
        score3.setType(Score.TYPE_TEAM);
        score3.setScore(2000);

        assertTrue("score value not ignored", score1.equals(score2) && score2.equals(score1));
        assertFalse("score type ignored", score2.equals(score3) || score3.equals(score2));
    }
}
