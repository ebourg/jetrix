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
import net.jetrix.config.*;
import net.jetrix.messages.*;

/**
 * Teleport a player to another channel.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TeleportCommand implements Command
{
    private int accessLevel = 1;

    public String[] getAliases()
    {
        return (new String[] { "teleport", "tp" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage(Locale locale)
    {
        return "/teleport <" + Language.getText("command.params.player_name_num", locale) + "> <" + Language.getText("command.params.channel_name_num", locale) + ">";
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command.teleport.description", locale);
    }

    public void execute(CommandMessage m)
    {
        String cmd = m.getCommand();
        Client client = (Client)m.getSource();

        if (m.getParameterCount() >= 2)
        {
            String targetName = m.getParameter(0);
            Client target = null;

            // checking if the second parameter is a slot number
            try
            {
                int slot = Integer.parseInt(targetName);
                if (slot >= 1 && slot <= 6)
                {
                    Channel channel = client.getChannel();
                    target = channel.getClient(slot);
                }
            }
            catch (NumberFormatException e) {}

            if (target == null)
            {
                // target is still null, the second parameter is a playername
                ClientRepository repository = ClientRepository.getInstance();
                target = repository.getClient(targetName);
            }

            if (target == null)
            {
                // no player found
                PlineMessage response = new PlineMessage();
                response.setKey("command.player_not_found", new Object[] { targetName });
                client.sendMessage(response);
            }
            else
            {
                // player found
                Channel channel = JoinCommand.getChannelByName(m.getParameter(1));

                if (channel != null)
                {
                    if ( channel.isFull() )
                    {
                        // sending channel full message
                        PlineMessage channelfull = new PlineMessage();
                        channelfull.setKey("command.join.full");
                        client.sendMessage(channelfull);
                    }
                    else
                    {
                        // adding the ADDPLAYER message to the queue of the target channel
                        AddPlayerMessage move = new AddPlayerMessage(target);
                        channel.sendMessage(move);

                        PlineMessage teleported = new PlineMessage();
                        Object[] params = new Object[] { target.getUser().getName(), channel.getConfig().getName() };
                        teleported.setKey("command.teleport.message", params);
                        client.sendMessage(teleported);
                    }
                }
            }
        }
        else
        {
            // not enough parameters
            String message = "<red>" + cmd + "<blue> <player name|player number> <channel name|channel number>";
            PlineMessage response = new PlineMessage(message);
            client.sendMessage(response);
        }
    }
}
