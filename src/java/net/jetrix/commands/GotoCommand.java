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

import net.jetrix.*;
import net.jetrix.config.*;
import net.jetrix.messages.*;

import java.util.*;

/**
 * Go to the channel of the specified player. The channel must not be password
 * protected.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class GotoCommand implements Command
{
    public String[] getAliases()
    {
        return (new String[] { "goto", "go" });
    }

    public int getAccessLevel()
    {
        return AccessLevel.PLAYER;
    }

    public String getUsage(Locale locale)
    {
        return "/goto <" + Language.getText("command.params.player_name", locale) + ">";
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command.goto.description", locale);
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client) m.getSource();

        if (m.getParameterCount() >= 1)
        {
            String targetName = m.getParameter(0);

            ClientRepository repository = ClientRepository.getInstance();
            Client target = repository.getClient(targetName);

            if (target == null)
            {
                // no player found
                PlineMessage response = new PlineMessage();
                response.setKey("command.player_not_found", targetName);
                client.sendMessage(response);
            }
            else
            {
                // player found
                Channel channel = target.getChannel();
                ChannelConfig channelConfig = channel.getConfig();

                if (target == client)
                {
                    PlineMessage cantgoto = new PlineMessage();
                    cantgoto.setKey("command.goto.yourself");
                    client.sendMessage(cantgoto);
                }
                else if (channel == client.getChannel())
                {
                    PlineMessage cantgoto = new PlineMessage();
                    cantgoto.setKey("command.goto.same_channel", target.getUser().getName());
                    client.sendMessage(cantgoto);
                }
                else if (channel.isFull())
                {
                    // send a channel full message
                    PlineMessage channelfull = new PlineMessage();
                    channelfull.setKey("command.join.full");
                    client.sendMessage(channelfull);
                }
                else if (client.getUser().getAccessLevel() < channelConfig.getAccessLevel())
                {
                    // check the access level
                    PlineMessage accessDenied = new PlineMessage();
                    accessDenied.setKey("command.join.denied");
                    client.sendMessage(accessDenied);
                }
                else if (channelConfig.isPasswordProtected())
                {
                    // check if the channel is password protected
                    PlineMessage accessDenied = new PlineMessage();
                    accessDenied.setKey("command.goto.password");
                    client.sendMessage(accessDenied);
                }
                else
                {
                    // add the ADDPLAYER message to the queue of the target channel
                    AddPlayerMessage move = new AddPlayerMessage(client);
                    channel.sendMessage(move);
                }
            }
        }
        else
        {
            // not enough parameters
            Locale locale = client.getUser().getLocale();
            PlineMessage response = new PlineMessage();
            String message = "<red>" + m.getCommand() + "<blue> <" + Language.getText("command.params.player_name", locale) + ">";
            response.setText(message);
            client.sendMessage(response);
        }
    }
}
