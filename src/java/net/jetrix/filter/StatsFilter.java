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

package net.jetrix.filter;

import java.util.*;
import java.text.*;

import net.jetrix.*;
import net.jetrix.config.*;
import net.jetrix.messages.*;

/**
 * A filter computing and displaying the number of pieces dropped per minute
 * by each player.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class StatsFilter extends GenericFilter
{
    private List stats;
    private long totalTime;
    private long lastStart;
    private long startTime;

    private static DecimalFormat df = new DecimalFormat("0.00");

    public void init(FilterConfig conf)
    {
        stats = new ArrayList(6);
        for (int i = 0; i < 6; i++)
        {
            stats.add(null);
        }
    }

    public void onMessage(StartGameMessage m, List out)
    {
        // reset the played time for this game
        totalTime = 0;

        // register the date of the last start/resume
        lastStart = System.currentTimeMillis();
        startTime = lastStart;

        // initialize the stats
        for (int i = 0; i < 6; i++)
        {
            if (getChannel().getClient(i + 1) != null)
            {
                stats.set(i, new PlayerStats());
            }
            else
            {
                stats.set(i, null);
            }
        }

        out.add(m);
    }

    public void onMessage(EndGameMessage m, List out)
    {
        // forward the message
        out.add(m);
        if (getChannel().getGameState() != Channel.GAME_STATE_STOPPED)
        {
            displayStats(out);
        }
    }

    public void onMessage(PauseMessage m, List out)
    {
        // updating global play time
        long now = System.currentTimeMillis();
        totalTime += (now - lastStart);

        // @todo update individual play time
        for (int i = 0; i < 6; i++)
        {
            PlayerStats playerStats = (PlayerStats) stats.get(i);
            if (playerStats != null && playerStats.playing)
            {
                playerStats.timePlayed += (now - lastStart);
            }
        }

        out.add(m);
    }

    public void onMessage(ResumeMessage m, List out)
    {
        lastStart = new Date().getTime();
        out.add(m);
    }

    public void onMessage(FieldMessage m, List out)
    {
        // ignore the empty field message sent on using specials
        if (m.getField() != null)
        {
            // increasing block count for the updated slot
            PlayerStats playerStats = (PlayerStats) stats.get(m.getSlot() - 1);
            if (playerStats != null && (System.currentTimeMillis() - startTime > 1500))
            {
                playerStats.blockCount++;
            }
        }

        out.add(m);
    }

    public void onMessage(OneLineAddedMessage m, List out)
    {
        out.add(m);
        PlayerStats playerStats = (PlayerStats) stats.get(m.getFromSlot() - 1);
        if (playerStats != null)
        {
            playerStats.linesAdded++;
            // remove 1 block count from any player in an opposite team
            removeBloc(m);
        }
    }

    public void onMessage(TwoLinesAddedMessage m, List out)
    {
        out.add(m);
        PlayerStats playerStats = (PlayerStats) stats.get(m.getFromSlot() - 1);
        if (playerStats != null)
        {
            playerStats.linesAdded += 2;
            // remove 1 block count from any player in an opposite team
            removeBloc(m);
        }
    }

    public void onMessage(FourLinesAddedMessage m, List out)
    {
        out.add(m);
        PlayerStats playerStats = (PlayerStats) stats.get(m.getFromSlot() - 1);
        if (playerStats != null)
        {
            playerStats.linesAdded += 4;
            playerStats.tetrisCount++;
            // remove 1 block count from any player in an opposite team
            removeBloc(m);
        }
    }

    public void onSpecial(SpecialMessage m)
    {
        if (!(m instanceof OneLineAddedMessage)
                && !(m instanceof TwoLinesAddedMessage)
                && !(m instanceof FourLinesAddedMessage))
        {
            PlayerStats playerStats = (PlayerStats) stats.get(m.getSlot() - 1);
            playerStats.specialsReceived++;

            playerStats = (PlayerStats) stats.get(m.getFromSlot() - 1);
            playerStats.specialsSent++;
        }
    }

    public void onMessage(LevelMessage m, List out)
    {
        out.add(m);
        PlayerStats playerStats = (PlayerStats) stats.get(m.getSlot() - 1);
        if (playerStats != null)
        {
            playerStats.level = m.getLevel();
        }
    }

    public void onMessage(LeaveMessage m, List out)
    {
        out.add(m);
        // remove the stats from the list if a player leave the channel
        stats.set(m.getSlot() - 1, null);
    }

    public void onMessage(PlayerLostMessage m, List out)
    {
        long now = System.currentTimeMillis();
        out.add(m);
        PlayerStats playerStats = (PlayerStats) stats.get(m.getSlot() - 1);
        if (playerStats != null)
        {
            playerStats.playing = false;
            playerStats.timePlayed += (now - lastStart);
        }
    }

    /**
     * Decrease the bloc count of players receiving an add to all message since
     * they will send back a field message assimilated by mistake as a bloc fall.
     */
    private void removeBloc(SpecialMessage m)
    {
        int slot = m.getFromSlot();

        if (slot > 0)
        {
            Client fromClient = (Client) m.getSource();

            for (int i = 1; i <= 6; i++)
            {
                Client client = getChannel().getClient(i);
                if (i != slot && client != null)
                {
                    User user = client.getUser();

                    if (user.isPlaying() && (user.getTeam() == null || !user.getTeam().equals(fromClient.getUser().getTeam())))
                    {
                        PlayerStats playerStats = (PlayerStats) stats.get(i - 1);
                        playerStats.blockCount--;
                    }
                }
            }
        }
    }

    private void displayStats(List out)
    {
        long now = System.currentTimeMillis();
        totalTime += (now - lastStart);

        for (int slot = 1; slot <= 6; slot++)
        {
            PlayerStats playerStats = (PlayerStats) stats.get(slot - 1);
            User user = getChannel().getPlayer(slot);

            if (playerStats != null && user != null)
            {
                // update the play time of the remaining players
                if (playerStats.playing)
                {
                    playerStats.timePlayed += (now - lastStart);
                }

                // display the stats
                String ppm = df.format(playerStats.getBlocksPerMinute());

                PlineMessage result = new PlineMessage();
                result.setText("<purple>" + user.getName() + "</purple> : "
                        + playerStats.blockCount + " <aqua>blocks @<red>" + ppm + "</red> bpm, "
                        + "<black>" + playerStats.linesAdded + "</black> added, "
                        + "<black>" + playerStats.tetrisCount + "</black> tetris, "
                        + "<black>" + playerStats.specialsSent + " / " + playerStats.specialsReceived + "</black> specials");

                out.add(result);
            }
        }

        // display the total game time
        PlineMessage time = new PlineMessage();
        time.setText("<brown>Total game time: <black>" + (totalTime / 1000) + "</black> seconds");
        out.add(time);
    }

    public String getName()
    {
        return "Stats Filter";
    }

    public String getDescription()
    {
        return "Displays stats about the game (pieces dropped per minute, lines added to all, time played, etc";
    }

    public String getVersion()
    {
        return "1.0";
    }

    public String getAuthor()
    {
        return "Emmanuel Bourg";
    }

    private class PlayerStats
    {
        long timePlayed;
        int tetrisCount;
        int linesAdded;
        int blockCount;
        int level;
        int specialsSent;
        int specialsReceived;
        boolean playing = true;

        public double getBlocksPerMinute()
        {
            return (double) blockCount * 60000 / (double) timePlayed;
        }
    }

}
