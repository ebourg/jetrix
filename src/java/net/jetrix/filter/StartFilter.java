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

import static net.jetrix.GameState.*;

import java.util.*;

import net.jetrix.*;
import net.jetrix.commands.StartCommand;
import net.jetrix.config.*;
import net.jetrix.messages.*;

/**
 * Starts the game automatically once everyone said "go".
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class StartFilter extends GenericFilter
{
    private long timestamp[];
    private int delay = 10000;
    private int countdown = 0;

    public void init()
    {
        // reading parameters
        delay = config.getInt("delay", delay);
        countdown = config.getInt("countdown", countdown);

        timestamp = new long[6];
    }

    public void onMessage(PlineMessage m, List<Message> out)
    {
        int slot = m.getSlot();
        String text = m.getText();

        // forward the message
        out.add(m);

        // do not check server messages
        if (slot == 0)
        {
            return;
        }

        // can't start if the game isn't stopped
        if (getChannel().getGameState() != STOPPED)
        {
            return;
        }

        // check if the text starts with "go"
        if (text != null && text.toLowerCase().trim().startsWith("go"))
        {
            // update the timestamp
            long now = System.currentTimeMillis();
            timestamp[slot - 1] = now;

            // check if everyone said "go" in the given delay
            // AFK players shouldn't be checked
            boolean doStart = true;
            int i = 0;
            while (i < 6 && doStart)
            {
                Client player = getChannel().getClient(i + 1);
                doStart = (player == null) || (player != null && (now - timestamp[i]) <= delay);
                i = i + 1;
            }

            if (doStart)
            {
                Arrays.fill(timestamp, 0);

                if (countdown == 0)
                {
                    StartGameMessage startMessage = new StartGameMessage();
                    out.add(startMessage);
                }
                else
                {
                    (new StartCommand.CountDown(getChannel(), countdown)).start();
                }
            }
        }
    }

    public String getName()
    {
        return "Start Filter";
    }

    public String getDescription()
    {
        return "Starts the game automatically once everyone said 'go'.";
    }

    public String getVersion()
    {
        return "1.1";
    }

    public String getAuthor()
    {
        return "Emmanuel Bourg";
    }

}
