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

import java.util.*;

import net.jetrix.winlist.*;

/**
 * A non persistent, in memory winlist.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class SimpleWinlist implements Winlist
{
    private String id;
    private List scores;

    public SimpleWinlist()
    {
        scores = new ArrayList();

        WinlistScore score1 = new WinlistScore();
        score1.setName("Bozo");
        score1.setScore(111);
        scores.add(score1);

        WinlistScore score2 = new WinlistScore();
        score2.setName("Waza");
        score2.setScore(222);
        scores.add(score2);

        WinlistScore score3 = new WinlistScore();
        score3.setName("Blop");
        score3.setScore(333);
        score3.setType(1);
        scores.add(score3);
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public WinlistScore getScore(String name)
    {
        return null;
    }

    public List getScores(long offset, long length)
    {
        return scores.subList(0, Math.min(scores.size(), (int) length));
    }

    public void saveGameResult(GameResult result)
    {
    }

}
