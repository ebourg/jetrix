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
import java.util.logging.*;

import net.jetrix.*;
import net.jetrix.config.*;
import net.jetrix.messages.*;

/**
 * Join or create a channel.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class JoinCommand implements Command
{
    private int accessLevel = 0;
    private Logger logger = Logger.getLogger("net.jetrix");

    public String[] getAliases()
    {
        return (new String[] { "join", "j" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage(Locale locale)
    {
        return "/join <" + Language.getText("command.params.channel_name_num", locale) + ">"
               + " <" + Language.getText("command.params.password", locale) + ">";
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command.join.description", locale);
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client)m.getSource();

        if (m.getParameterCount() >= 1)
        {
            Channel target = getChannelByName(m.getParameter(0));

            // get the password
            String password = null;
            if (m.getParameterCount() >= 2)
            {
                password = m.getParameter(1);
            }

            if (target != null)
            {
                ChannelConfig channelConfig = target.getConfig(); // NPE

                if (client.getUser().getAccessLevel() < channelConfig.getAccessLevel())
                {
                    // deny access
                    PlineMessage accessDenied = new PlineMessage();
                    accessDenied.setKey("command.join.denied");
                    client.sendMessage(accessDenied);
                }
                else if (channelConfig.isPasswordProtected() && !channelConfig.getPassword().equals(password))
                {
                    // wrong password
                    logger.severe(client.getUser().getName() + "(" + client.getInetAddress() + ") "
                                  + "attempted to join the protected channel '"  + channelConfig.getName() + "'.");
                    PlineMessage accessDenied = new PlineMessage();
                    accessDenied.setKey("command.join.wrong_password");
                    client.sendMessage(accessDenied);
                }
                else if (target.isFull())
                {
                    // sending channel full message
                    PlineMessage channelfull = new PlineMessage();
                    channelfull.setKey("command.join.full");
                    client.sendMessage(channelfull);
                }
                else
                {
                    // adding the ADDPLAYER message to the queue of the target channel
                    AddPlayerMessage move = new AddPlayerMessage();
                    move.setClient((Client)m.getSource());
                    target.sendMessage(move);
                }
            }
        }
        else
        {
            // not enough parameters
            String message = "<red>" + m.getCommand() + "<blue> <channel name|number>";
            PlineMessage response = new PlineMessage(message);
            client.sendMessage(response);
        }
    }

    /**
     * Return the channel associated to the specified name or number
     * as a String ("1", "2", etc).
     */
    protected static Channel getChannelByName(String name)
    {
        Channel channel = null;

        try
        {
            // trying to parse the number
            int num = Integer.parseInt(name) - 1;
            channel = ChannelManager.getInstance().getChannel(num);
        }
        catch (NumberFormatException e)
        {
            channel = ChannelManager.getInstance().getChannel(name);;
        }

        return channel;
    }


}
