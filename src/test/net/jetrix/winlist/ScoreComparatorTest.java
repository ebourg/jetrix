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
 * JUnit TestCase for the class net.jetrix.winlist.ScoreComparator.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ScoreComparatorTest extends TestCase
{
    public void testCompare()
    {
        Score score1 = new Score();
        score1.setScore(100);
        Score score2 = new Score();
        score2.setScore(200);

        ScoreComparator comparator = new ScoreComparator();

        assertTrue("s1 == s1", comparator.compare(score1, score1) == 0);
        assertTrue("s1 > s2", comparator.compare(score1, score2) > 0);
        assertTrue("s2 < s1", comparator.compare(score2, score1) < 0);
    }
}
