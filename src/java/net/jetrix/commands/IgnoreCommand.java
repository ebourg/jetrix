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
import org.apache.commons.lang.*;

/**
 * Manage the ignore list.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class IgnoreCommand extends AbstractCommand
{
    public String getAlias()
    {
        return "ignore";
    }

    public String getUsage(Locale locale)
    {
        return "/ignore <" + Language.getText("command.params.player_name_num", locale) + ">";
    }

    public void execute(CommandMessage message)
    {
        // get the user sending this command
        Client client = (Client) message.getSource();
        User user = client.getUser();

        // prepare the response
        PlineMessage response = new PlineMessage();

        if (message.getParameterCount() == 0)
        {
            // display the ignore list
            Set ignoredUsers = user.getIgnoredUsers();

            if (ignoredUsers.isEmpty())
            {
                response.setKey("command.ignore.empty");
            }
            else
            {
                response.setKey("command.ignore.list", StringUtils.join(ignoredUsers.iterator(), ", "));
            }
        }
        else
        {
            // get the name of the (un)ignored player
            Client target = message.getClientParameter(0);
            String name = target != null ? target.getUser().getName() : message.getParameter(0);

            // add or remove the name from the ignore list
            if (user.ignores(name))
            {
                user.unignore(name);
                response.setKey("command.ignore.removed", name);
            }
            else
            {
                user.ignore(name);
                response.setKey("command.ignore.added", name);
            }
        }

        // send the response
        client.send(response);
    }
}
