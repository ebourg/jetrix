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

import net.jetrix.messages.*;
import net.jetrix.*;
import net.jetrix.config.*;

import java.util.*;

/**
 * Game mod : The first player completing 7 tetris win.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TetrisFilter extends GenericFilter
{
    private int[] tetrisCount = new int[6];
    private int tetrisLimit = 7;
    private boolean addToAll = false;

    public void init(FilterConfig conf)
    {
        tetrisLimit = conf.getInt("limit", tetrisLimit);
        addToAll = conf.getBoolean("addToAll", addToAll);
    }

    public void onMessage(StartGameMessage m, List out)
    {
        Arrays.fill(tetrisCount, 0);

        GmsgMessage message = new GmsgMessage();
        message.setKey("filter.tetris.start_message", new Integer(tetrisLimit));

        out.add(m);
        out.add(message);
    }

    public void onMessage(FourLinesAddedMessage m, List out)
    {
        int from = m.getFromSlot() - 1;
        tetrisCount[from]++;

        if (addToAll)
        {
            out.add(m);
        }

        if (tetrisCount[from] >= tetrisLimit)
        {
            getChannel().sendMessage(new EndGameMessage());

            User winner = getChannel().getPlayer(m.getFromSlot());
            PlineMessage announce = new PlineMessage();
            announce.setKey("channel.player_won", winner.getName());
            getChannel().sendMessage(announce);
        }
        else
        {
            // get the highest tetris count
            int max = 0;
            for (int i = 0; i < 6; i++)
            {
                if (tetrisCount[i] > max)
                {
                    max = tetrisCount[i];
                }
            }

            if (tetrisCount[from] == max)
            {
                // look for the leaders
                List leaders = new ArrayList();
                for (int i = 0; i < 6; i++)
                {
                    if (tetrisCount[i] == max)
                    {
                        Client client = getChannel().getClient(i + 1);
                        if (client != null)
                        {
                            leaders.add(client.getUser().getName());
                        }
                    }
                }

                // announce the leaders
                GmsgMessage announce = new GmsgMessage();

                if (leaders.size() == 1)
                {
                    announce.setKey("filter.tetris.lead", leaders.get(0), new Integer(max));
                }
                else
                {
                    StringBuffer message = new StringBuffer();
                    Iterator it = leaders.iterator();
                    while (it.hasNext())
                    {
                        message.append(it.next());
                        if (it.hasNext())
                        {
                            message.append(", ");
                        }
                    }
                    announce.setKey("filter.tetris.tied", message.toString(), new Integer(max));
                }

                out.add(announce);
            }
        }
    }

    public void onMessage(TwoLinesAddedMessage m, List out)
    {
        if (addToAll)
        {
            out.add(m);
        }
    }

    public void onMessage(OneLineAddedMessage m, List out)
    {
        if (addToAll)
        {
            out.add(m);
        }
    }

    public String getName()
    {
        return "7 Tetris Mod";
    }

    public String getDescription()
    {
        return "Game mod - The first player completing 7 tetris win.";
    }

    public String getVersion()
    {
        return "1.0";
    }

    public String getAuthor()
    {
        return "Emmanuel Bourg";
    }

}
