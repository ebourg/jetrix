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

package net.jetrix.commands;

import java.util.*;
import net.jetrix.*;
import net.jetrix.messages.*;

/**
 * Display a random number.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class RandomCommand implements Command
{
    private int accessLevel = 0;
    private Random random = new Random();

    public String[] getAliases()
    {
        return (new String[] { "random", "roll" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage(Locale locale)
    {
        return "/random <min> <max>";
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command.random.description", locale);
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client)m.getSource();

        // get the minimum and maximum values
        int min = 1;
        int max = 6;

        if (m.getParameterCount() >= 2)
        {
            try
            {
                min = Integer.parseInt(m.getParameter(0));
                max = Integer.parseInt(m.getParameter(1));
            }
            catch (NumberFormatException e) { /* keep the default values */ }
        }

        int result = random.nextInt((int)Math.abs(max - min) + 1);

        // display the result
        PlineMessage response = new PlineMessage();
        response.setKey("command.random.result", new Object[] { client.getUser().getName(), new Integer(min), new Integer(max), new Integer(result + min) });
        client.getChannel().sendMessage(response);
    }
}
