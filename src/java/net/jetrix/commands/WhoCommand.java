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
 * List all players connected to the server.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class WhoCommand implements Command
{
    public String[] getAliases()
    {
        return (new String[] { "who", "w", "cwho" });
    }

    public int getAccessLevel()
    {
        return AccessLevel.PLAYER;
    }

    public String getUsage(Locale locale)
    {
        return "/who";
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command.who.description", locale);
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client) m.getSource();
        ChannelManager channelManager = ChannelManager.getInstance();

        PlineMessage response = new PlineMessage();
        response.setKey("command.who.header");        
        client.sendMessage(response);

        Iterator it = channelManager.channels();
        while(it.hasNext())
        {
            Channel channel = (Channel) it.next();

            // skipping empty channels
            if (channel.getPlayerCount() > 0)
            {
                ChannelConfig conf = channel.getConfig();

                boolean isInChannel = false;
                String channelColor = "<purple>";
                StringBuffer message = new StringBuffer();
                message.append("[" + conf.getName() + "] <darkBlue>");

                for (int i = 1; i <= 6; i++)
                {
                    Client clientInChannel = channel.getClient(i);
                    if (clientInChannel != null)
                    {
                        User user = clientInChannel.getUser();
                        if (user.getAccessLevel() > 0) message.append("<b>");
                        if (clientInChannel.getProtocol().getName().equals("tetrifast")) message.append("<i>");
                        message.append(" " + user.getName());
                        if (clientInChannel.getProtocol().getName().equals("tetrifast")) message.append("</i>");
                        if (user.getAccessLevel() > 0) message.append("</b>");
                    }

                    if (client == clientInChannel)
                    {
                        isInChannel = true;
                    }
                }

                if (isInChannel) channelColor = "<red>";

                Message response2 = new PlineMessage(channelColor + message.toString());
                client.sendMessage(response2);
            }
        }
    }
}
