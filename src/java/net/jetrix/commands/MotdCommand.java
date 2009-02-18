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

import net.jetrix.*;
import net.jetrix.config.*;
import net.jetrix.messages.channel.CommandMessage;
import net.jetrix.messages.channel.PlineMessage;

/**
 * Display the message of the day.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class MotdCommand extends AbstractCommand
{
    public String getAlias()
    {
        return "motd";
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client) m.getSource();
        ServerConfig config = Server.getInstance().getConfig();

        // send the message of the day line by line
        if (config.getMessageOfTheDay() != null)
        {
            String[] lines = config.getMessageOfTheDay().split("\n");

            for (String line : lines)
            {
                client.send(new PlineMessage("<gray>" + line));
            }
        }
    }
}
