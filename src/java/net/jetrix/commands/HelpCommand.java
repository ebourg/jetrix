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

import java.util.*;

import net.jetrix.*;
import net.jetrix.messages.*;

/**
 * List all commands available.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class HelpCommand extends AbstractCommand
{
    public String[] getAliases()
    {
        return (new String[]{"help", "?", "h"});
    }

    public void execute(CommandMessage message)
    {
        Client client = (Client) message.getSource();
        Locale locale = client.getUser().getLocale();

        // send the header
        PlineMessage header = new PlineMessage();
        header.setKey("command.help.header");
        client.send(header);

        // iterate through the commands accessible to the user
        Iterator commands = CommandManager.getInstance().getCommands(client.getUser().getAccessLevel());
        while (commands.hasNext())
        {
            Command command = (Command) commands.next();

            if (command.isHidden())
            {
                continue;
            }

            // parse the usage string, and color and command and the parameters
            String usage = command.getUsage(locale);
            String line1Body;
            int space = usage.indexOf(" ");
            if (space == -1)
            {
                line1Body = "<red>" + usage;
            }
            else
            {
                line1Body = "<red>" + usage.substring(0, space) + "<aqua>" + usage.substring(space);
            }

            // build the lines
            PlineMessage line1 = new PlineMessage(line1Body);
            PlineMessage line2 = new PlineMessage("         " + command.getDescription(locale));

            // send the lines
            client.send(line1);
            client.send(line2);
        }
    }
}
