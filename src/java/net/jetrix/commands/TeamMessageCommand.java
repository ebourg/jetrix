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
 * Send a message to all members of the player's team.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TeamMessageCommand implements ParameterCommand
{
    public String[] getAliases()
    {
        return (new String[] { "tmsg", "gu" });
    }

    public int getAccessLevel()
    {
        return AccessLevel.PLAYER;
    }

    public String getUsage(Locale locale)
    {
        return "/tmsg <" + Language.getText("command.params.message", locale) + ">";
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command.team_message.description", locale);
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
            response.setKey("command.team_message.not_in_team");
            client.send(response);
        }
        else
        {
            // preparing message
            PlineMessage response = new PlineMessage();
            response.setKey("command.team_message.format", client.getUser().getName(), m.getText());

            Iterator clients = ClientRepository.getInstance().getClients();
            while (clients.hasNext())
            {
                Client target = (Client) clients.next();
                if (client.getUser().getTeam().equals(target.getUser().getTeam()) && client != target)
                {
                    target.send(response);
                }
            }
        }
    }
}
