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

package net.jetrix.filter;

import static net.jetrix.GameState.*;

import java.util.*;
import java.text.*;

import net.jetrix.*;
import net.jetrix.messages.*;
import org.apache.commons.lang.time.StopWatch;

/**
 * A filter computing and displaying the number of pieces dropped per minute
 * by each player.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class StatsFilter extends GenericFilter
{
    private List<PlayerStats> stats;
    private StopWatch stopWatch;

    private static DecimalFormat df = new DecimalFormat("0.00");

    public void init()
    {
        stopWatch = new StopWatch();
        stats = new ArrayList<PlayerStats>(6);
        for (int i = 0; i < 6; i++)
        {
            stats.add(null);
        }
    }

    public void onMessage(StartGameMessage m, List<Message> out)
    {
        if (getChannel().getGameState() == STOPPED)
        {
            // reset and start the stop watch
            stopWatch.reset();
            stopWatch.start();

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
        }

        out.add(m);
    }

    public void onMessage(EndGameMessage m, List<Message> out)
    {
        // forward the message
        out.add(m);

        if (getChannel().getGameState() != STOPPED)
        {
            stopWatch.stop();
            displayStats(out);
        }
    }

    public void onMessage(PauseMessage m, List<Message> out)
    {
        if (getChannel().getGameState() == STARTED)
        {
            // suspend the stop watch
            stopWatch.suspend();
        }

        out.add(m);
    }

    public void onMessage(ResumeMessage m, List<Message> out)
    {
        if (getChannel().getGameState() == PAUSED)
        {
            // resume the stop watch
            stopWatch.resume();
        }

        out.add(m);
    }

    public void onMessage(FieldMessage m, List<Message> out)
    {
        // increase the block count for the updated slot
        PlayerStats playerStats = stats.get(m.getSlot() - 1);
        if (playerStats != null && (stopWatch.getTime() > 1500))
        {
            playerStats.blockCount++;
        }

        out.add(m);
    }

    public void onMessage(OneLineAddedMessage m, List<Message> out)
    {
        out.add(m);

        updateStats(m, 1);

        // remove 1 block count from any player in an opposite team
        removeBlock(m);
    }

    public void onMessage(TwoLinesAddedMessage m, List<Message> out)
    {
        out.add(m);

        updateStats(m, 2);

        // remove 1 block count from any player in an opposite team
        removeBlock(m);
    }

    public void onMessage(FourLinesAddedMessage m, List<Message> out)
    {
        out.add(m);

        updateStats(m, 4);

        // remove 1 block count from any player in an opposite team
        removeBlock(m);
    }

    public void onSpecial(SpecialMessage m, List<Message> out)
    {
        if (!(m instanceof OneLineAddedMessage)
                && !(m instanceof TwoLinesAddedMessage)
                && !(m instanceof FourLinesAddedMessage))
        {
            // add a special received by the target
            PlayerStats playerStats = stats.get(m.getSlot() - 1);
            playerStats.specialsReceived++;

            // remove 2 blocks count from the target
            playerStats.blockCount = playerStats.blockCount - 2;

            // add a special sent by the sender
            playerStats = stats.get(m.getFromSlot() - 1);
            playerStats.specialsSent++;
        }
    }

    public void onMessage(LevelMessage m, List<Message> out)
    {
        out.add(m);
        PlayerStats playerStats = stats.get(m.getSlot() - 1);
        if (playerStats != null)
        {
            playerStats.level = m.getLevel();
        }
    }

    public void onMessage(LeaveMessage m, List<Message> out)
    {
        out.add(m);
        // remove the stats from the list if a player leave the channel
        stats.set(m.getSlot() - 1, null);
    }

    public void onMessage(PlayerLostMessage m, List<Message> out)
    {
        out.add(m);
        PlayerStats playerStats = stats.get(m.getSlot() - 1);
        if (playerStats != null)
        {
            playerStats.playing = false;
            playerStats.timePlayed = stopWatch.getTime();
        }
    }

    /**
     * Decrease the block count of players receiving an add to all message since
     * they will send back a field message assimilated by mistake as a block fall.
     */
    private void removeBlock(SpecialMessage message)
    {
        int slot = message.getFromSlot();

        // find the team of the player sending the message;
        String team = null;

        if (message.getSource() != null && message.getSource() instanceof Client)
        {
            Client client = (Client) message.getSource();
            team = client.getUser().getTeam();
        }

        // check all players...
        for (int i = 1; i <= 6; i++)
        {
            Client client = getChannel().getClient(i);
            if (i != slot && client != null)
            {
                User user = client.getUser();

                // ...still playing, and team-less or in a different team from the sender
                if (user.isPlaying() && (user.getTeam() == null || !user.getTeam().equals(team)))
                {
                    PlayerStats playerStats = stats.get(i - 1);
                    playerStats.blockCount--;
                }
            }
        }
    }

    /**
     * Update the stats of the player sending the specified message.
     *
     * @param message
     * @param lines
     */
    private void updateStats(SpecialMessage message, int lines)
    {
        if (message.getFromSlot() > 0) // ignore messages sent by the server
        {
            PlayerStats playerStats = stats.get(message.getFromSlot() - 1);
            if (playerStats != null)
            {
                playerStats.linesAdded += lines;
                if (lines == 4)
                {
                    playerStats.tetrisCount++;
                }
            }
        }
    }

    private void displayStats(List<Message> out)
    {
        for (int slot = 1; slot <= 6; slot++)
        {
            PlayerStats playerStats = stats.get(slot - 1);
            User user = getChannel().getPlayer(slot);

            if (playerStats != null && user != null)
            {
                // update the play time of the remaining players
                if (playerStats.playing)
                {
                    playerStats.timePlayed = stopWatch.getTime();
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
        time.setText("<brown>Total game time: <black>" + df.format(stopWatch.getTime() / 1000f) + "</black> seconds"); // todo i18n
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
        return "1.1";
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
