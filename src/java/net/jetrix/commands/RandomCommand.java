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

package net.jetrix.commands;

import static java.lang.Math.*;

import java.util.*;
import net.jetrix.*;
import net.jetrix.messages.channel.CommandMessage;
import net.jetrix.messages.channel.PlineMessage;

/**
 * Display a random number.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class RandomCommand extends AbstractCommand
{
    private Random random = new Random();

    public String[] getAliases()
    {
        return (new String[] { "random", "roll" });
    }

    public String getUsage(Locale locale)
    {
        return "/random <min> <max>";
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client) m.getSource();

        // get the minimum and maximum values
        int min = 1;
        int max = 6;

        if (m.getParameterCount() >= 2)
        {
            int a = m.getIntParameter(0, min);
            int b = m.getIntParameter(1, max);

            min = min(a, b);
            max = max(a, b);
        }
        else if (m.getParameterCount() == 1)
        {
            max = Math.max(min, m.getIntParameter(0, max));
        }

        int result = random.nextInt(abs(max - min) + 1);

        // display the result
        PlineMessage response = new PlineMessage();
        response.setKey("command.random.result", client.getUser().getName(), min, max, (result + min));
        client.getChannel().send(response);
    }
}
