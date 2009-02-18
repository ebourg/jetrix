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
import net.jetrix.config.*;
import net.jetrix.messages.*;
import net.jetrix.messages.channel.CommandMessage;
import net.jetrix.messages.channel.PlineMessage;

/**
 * Join or create a channel.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class JoinCommand extends AbstractCommand implements ParameterCommand
{
    public String[] getAliases()
    {
        return (new String[]{"join", "j"});
    }

    public String getUsage(Locale locale)
    {
        return "/join <" + Language.getText("command.params.channel_name_num", locale) + ">"
                + " <" + Language.getText("command.params.password", locale) + ">";
    }

    public int getParameterCount()
    {
        return 1;
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client) m.getSource();

        Channel channel = m.getChannelParameter(0);

        // get the password
        String password = null;
        if (m.getParameterCount() >= 2)
        {
            password = m.getParameter(1);
        }

        // the specified channel was found ?
        if (channel == null)
        {
            // no, let's create it if the message comes from an operator
            if (client.getUser().getAccessLevel() >= AccessLevel.OPERATOR)
            {
                // create the channel
                ChannelConfig config = new ChannelConfig();
                config.setSettings(new Settings());
                config.setName(m.getParameter(0).replaceFirst("#", "")); // todo reject empty names
                config.setDescription("");
                channel = ChannelManager.getInstance().createChannel(config);

                PlineMessage response = new PlineMessage();
                response.setKey("command.join.created", m.getParameter(0));
                client.send(response);
            }
            else
            {
                // unknown channel
                PlineMessage response = new PlineMessage();
                response.setKey("command.join.unknown", m.getParameter(0));
                client.send(response);
            }

        }

        if (channel != null)
        {
            ChannelConfig channelConfig = channel.getConfig(); // NPE

            if (client.getUser().getAccessLevel() < channelConfig.getAccessLevel())
            {
                // deny access
                PlineMessage accessDenied = new PlineMessage();
                accessDenied.setKey("command.join.denied");
                client.send(accessDenied);
            }
            else if (channelConfig.isPasswordProtected() && !channelConfig.getPassword().equals(password))
            {
                // wrong password
                log.severe(client.getUser().getName() + "(" + client.getInetAddress() + ") "
                        + "attempted to join the protected channel '" + channelConfig.getName() + "'.");
                PlineMessage accessDenied = new PlineMessage();
                accessDenied.setKey("command.join.wrong_password");
                client.send(accessDenied);
            }
            else if (channel.isFull() && client.getUser().isPlayer())
            {
                // sending channel full message
                PlineMessage channelfull = new PlineMessage();
                channelfull.setKey("command.join.full");
                client.send(channelfull);
            }
            else
            {
                // adding the ADDPLAYER message to the queue of the target channel
                AddPlayerMessage move = new AddPlayerMessage();
                move.setClient((Client) m.getSource());
                channel.send(move);
            }
        }
    }

}
