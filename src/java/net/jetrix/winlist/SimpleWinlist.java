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

import java.io.*;
import java.util.*;
import java.util.logging.*;

import net.jetrix.messages.*;
import net.jetrix.config.*;
import net.jetrix.*;

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
    protected List<Score> scores;
    protected boolean initialized = false;
    protected boolean persistent = true;

    protected Logger log = Logger.getLogger("net.jetrix");
    protected WinlistConfig config;

    public SimpleWinlist()
    {
        scores = new ArrayList<Score>();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void init(WinlistConfig config)
    {
        this.config = config;
    }

    public WinlistConfig getConfig()
    {
        return config;
    }

    public boolean isPersistent()
    {
        return persistent;
    }

    public void setPersistent(boolean persistent)
    {
        this.persistent = persistent;
    }

    public synchronized Score getScore(String name, int type)
    {
        if (!initialized && persistent)
        {
            load();
        }

        Score score = null;

        Score example = new Score();
        example.setName(name);
        example.setType(type);

        int i = scores.indexOf(example);
        if (i != -1)
        {
            score = (Score) scores.get(i);
        }

        return score;
    }

    public synchronized List<Score> getScores(long offset, long length)
    {
        if (!initialized && persistent)
        {
            load();
        }

        return scores.subList(0, Math.min(scores.size(), (int) length));
    }

    public synchronized void saveGameResult(GameResult result)
    {
        if (!initialized && persistent)
        {
            load();
        }

        int teamCount = result.getTeamCount();
        if (teamCount == 1)
        {
            return;
        }

        // reward the winning player or team
        Score score1 = null;
        long previousRank1 = 0;
        long previousScore1 = 0;
        Collection<GamePlayer> winners = result.getPlayersAtRank(1);
        GamePlayer winner = winners.iterator().next();

        if (winner.isWinner())
        {
            String name = winner.getTeamName() == null ? winner.getName() : winner.getTeamName();
            int type = winner.getTeamName() == null ? Score.TYPE_PLAYER : Score.TYPE_TEAM;
            score1 = getScore(name, type);
            previousRank1 = scores.indexOf(score1) + 1;
            previousRank1 = previousRank1 == 0 ? scores.size() + 1 : previousRank1;

            if (score1 == null)
            {
                // add a new entry into the winlist
                score1 = new Score();
                score1.setName(name);
                score1.setType(type);
                scores.add(score1);
            }

            previousScore1 = score1.getScore();
            int points = teamCount >= 3 ? 3 : 2;
            score1.setScore(score1.getScore() + points);
        }

        // reward the second player or team
        Score score2 = null;
        long previousRank2 = 0;
        long previousScore2 = 0;
        Collection<GamePlayer> seconds = result.getPlayersAtRank(2);
        GamePlayer second = seconds.iterator().next();

        if (teamCount >= 5)
        {
            String name = second.getTeamName() == null ? second.getName() : second.getTeamName();
            int type = second.getTeamName() == null ? Score.TYPE_PLAYER : Score.TYPE_TEAM;
            score2 = getScore(name, type);
            previousRank2 = scores.indexOf(score1) + 1;
            previousRank2 = previousRank2 == 0 ? scores.size() + 1 : previousRank2;

            if (score2 == null)
            {
                // add a new entry into the winlist
                score2 = new Score();
                score2.setName(name);
                score2.setType(type);
                scores.add(score2);
            }

            previousScore2 = score2.getScore();
            score2.setScore(score2.getScore() + 1);
        }

        // sort the winlist
        Collections.sort(scores, new ScoreComparator());

        // announce the new scores of the winners
        Channel channel = result.getChannel();
        if (channel != null && config.getBoolean("display.score", false))
        {
            channel.send(getGainMessage(score1, previousScore1, previousRank1));
            if (score2 != null)
            {
                channel.send(getGainMessage(score2, previousScore2, previousRank2));
            }
        }

        // save the winlist to the external file
        if (persistent)
        {
            save();
        }
    }

    public void clear()
    {
        scores.clear();
        save();
    }

    public int size()
    {
        return scores.size();
    }

    /**
     * Load the winlist from a file.
     */
    protected void load()
    {
        if (log.isLoggable(Level.FINE))
        {
            log.fine("loading winlist " + getId());
        }

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
                        Score score = new Score();
                        score.setName(fields[2]);
                        score.setScore(Long.parseLong(fields[1]));
                        score.setType("p".equals(fields[0]) ? Score.TYPE_PLAYER : Score.TYPE_TEAM);
                        scores.add(score);
                    }
                }
                catch (Exception e)
                {
                    log.log(Level.WARNING, "Unable to read the winlist file " + file, e);
                }
                finally
                {
                    close(reader);
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
            File file = new File(id + ".winlist");
            try
            {
                writer = new BufferedWriter(new FileWriter(file));

                for (Score score : scores)
                {
                    StringBuilder line = new StringBuilder();
                    line.append(score.getType() == Score.TYPE_PLAYER ? "p" : "t");
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
                log.log(Level.WARNING, "Unable to write the winlist file " + file, e);
            }
            finally
            {
                close(writer);
            }
        }
    }

    /**
     * Build a message displaying the new score and rank of a winner.
     */
    protected PlineMessage getGainMessage(Score score, long previousScore, long previousRank)
    {
        StringBuilder key = new StringBuilder();
        key.append("channel.score.");
        key.append(score.getType() == Score.TYPE_PLAYER ? "player" : "team");
        key.append(".");
        key.append(score.getScore() - previousScore > 1 ? "points" : "point");

        long rank = scores.indexOf(score) + 1;
        if (rank == 0)
        {
            rank = scores.size() + 1;
        }

        PlineMessage message = new PlineMessage();
        message.setKey(key.toString(), score.getName(), score.getScore() - previousScore, score.getScore(), rank, previousRank - rank);
        return message;
    }

    /**
     * Close quietly the specified stream or reader.
     */
    void close(Closeable closeable)
    {
        if (closeable != null)
        {
            try
            {
                closeable.close();
            }
            catch (IOException e)
            {
            }
        }
    }

}
