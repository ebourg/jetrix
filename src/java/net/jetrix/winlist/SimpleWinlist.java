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
import java.io.*;

import net.jetrix.winlist.*;

/**
 * A standard winlist using the same scoring as the original TetriNET : the
 * winner gets 3 points if there was 3 or more players (or different teams)
 * involved in the game, 2 points otherwise; the second gets 1 point if there
 * was 5 or more players in the game. The winlist is saved in a xxxx.winlist
 * file.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class SimpleWinlist implements Winlist
{

    private String id;
    private List scores;
    private boolean initialized = false;

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

    public synchronized WinlistScore getScore(String name, int type)
    {
        if (!initialized)
        {
            load();
        }

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

    public synchronized List getScores(long offset, long length)
    {
        if (!initialized)
        {
            load();
        }

        return scores.subList(0, Math.min(scores.size(), (int) length));
    }

    public synchronized void saveGameResult(GameResult result)
    {
        if (!initialized)
        {
            load();
        }

        int teamCount = getTeamCount(result);
        if (teamCount == 1)
        {
            return;
        }

        // reward the winning player or team
        Collection winners = result.getPlayersAtRank(1);
        Iterator it = winners.iterator();
        GamePlayer winner = (GamePlayer) it.next();
        if (winner.isWinner())
        {
            String name = winner.getTeamName() == null ? winner.getName() : winner.getTeamName();
            int type = winner.getTeamName() == null ? WinlistScore.TYPE_PLAYER : WinlistScore.TYPE_TEAM;
            WinlistScore score = getScore(name, type);
            if (score == null)
            {
                // add a new entry into the winlist
                score = new WinlistScore();
                score.setName(name);
                score.setType(type);
                scores.add(score);
            }
            int points = teamCount >= 3 ? 3 : 2;
            score.setScore(score.getScore() + points);
        }

        // reward the second player or team
        Collection seconds = result.getPlayersAtRank(2);
        it = seconds.iterator();
        GamePlayer second = (GamePlayer) it.next();
        if (teamCount >= 5)
        {
            String name = second.getTeamName() == null ? second.getName() : second.getTeamName();
            int type = second.getTeamName() == null ? WinlistScore.TYPE_PLAYER : WinlistScore.TYPE_TEAM;
            WinlistScore score = getScore(name, type);
            if (score == null)
            {
                // add a new entry into the winlist
                score = new WinlistScore();
                score.setName(name);
                score.setType(type);
                scores.add(score);
            }
            score.setScore(score.getScore() + 1);
        }

        // sort the winlist
        Collections.sort(scores, new ScoreComparator());

        // save the winlist to the external file
        save();
    }

    /**
     * Load the winlist from a file.
     */
    protected void load()
    {
        if (id != null)
        {
            BufferedReader reader = null;
            File file = new File(id + ".winlist");
            if (file.exists())
            {
                try
                {
                    reader = new BufferedReader(new FileReader(file));
                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        String[] fields = line.split("\t");
                        WinlistScore score = new WinlistScore();
                        score.setName(fields[2]);
                        score.setScore(Long.parseLong(fields[1]));
                        score.setType("p".equals(fields[0]) ? WinlistScore.TYPE_PLAYER : WinlistScore.TYPE_TEAM);
                        scores.add(score);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    try { if (reader != null) { reader.close(); } } catch (Exception e) { e.printStackTrace(); }
                }
            }
        }

        initialized = true;
    }

    /**
     * Save the winlist to a file.
     */
    protected void save()
    {
        if (id != null)
        {
            BufferedWriter writer = null;
            try
            {
                writer = new BufferedWriter(new FileWriter(id + ".winlist"));
                Iterator it = scores.iterator();
                while (it.hasNext())
                {
                    WinlistScore score = (WinlistScore) it.next();
                    StringBuffer line = new StringBuffer();
                    line.append(score.getType() == WinlistScore.TYPE_PLAYER ? "p" : "t");
                    line.append("\t");
                    line.append(score.getScore());
                    line.append("\t");
                    line.append(score.getName());
                    line.append("\n");
                    writer.write(line.toString());
                }
                writer.flush();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                try { if (writer != null) { writer.close(); } } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }

    private int getTeamCount(GameResult result)
    {
        Map teams = new HashMap();

        int teamCount = 0;

        Iterator players = result.getGamePlayers().iterator();

        while (players.hasNext())
        {
            GamePlayer player = (GamePlayer) players.next();

            String team = player.getTeamName();

            if (team == null)
            {
                teamCount++;
            }
            else
            {
                teams.put(team, team);
            }
        }

        return teamCount + teams.size();
    }

}
