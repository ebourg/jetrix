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
 * Send a message to all members of the player's team.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TeamMessageCommand extends AbstractCommand implements ParameterCommand
{
    public String[] getAliases()
    {
        return (new String[] { "tmsg", "gu" });
    }

    public String getUsage(Locale locale)
    {
        return "/tmsg <" + Language.getText("command.params.message", locale) + ">";
    }

    public int getParameterCount()
    {
        return 1;
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client) m.getSource();

        if (client.getUser().getTeam() == null)
        {
            // the message can't be sent since the player is not in a team
            PlineMessage response = new PlineMessage();
            response.setKey("command.tmsg.not_in_team");
            client.send(response);
        }
        else
        {
            // preparing message
            PlineMessage response = new PlineMessage();
            response.setKey("command.tmsg.format", client.getUser().getName(), m.getText());

            for (Client target : ClientRepository.getInstance().getClients())
            {
                if (client.getUser().getTeam().equals(target.getUser().getTeam()) && client != target)
                {
                    target.send(response);
                }
            }
        }
    }
}
