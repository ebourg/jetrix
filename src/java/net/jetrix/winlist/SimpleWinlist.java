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
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public WinlistScore getScore(String name, int type)
    {
        WinlistScore score = null;

        WinlistScore example = new WinlistScore();
        example.setName(name);
        example.setType(type);

        int i = scores.indexOf(example);
        if (i != -1)
        {
            score = (WinlistScore) scores.get(i);
        }

        return score;
    }

    public List getScores(long offset, long length)
    {
        return scores.subList(0, Math.min(scores.size(), (int) length));
    }

    public void saveGameResult(GameResult result)
    {
        Collection players = result.getGamePlayers();
        Iterator it = players.iterator();
        while (it.hasNext())
        {
            GamePlayer player = (GamePlayer)it.next();
            if (player.isWinner())
            {
                WinlistScore score = getScore(player.getName(), WinlistScore.TYPE_PLAYER);
                if (score == null)
                {
                    score = new WinlistScore();
                    score.setName(player.getName());
                    score.setType(WinlistScore.TYPE_PLAYER);
                    scores.add(score);
                }
                score.setScore(score.getScore() + 1);
            }
        }

        Collections.sort(scores, new ScoreComparator());
    }

}
