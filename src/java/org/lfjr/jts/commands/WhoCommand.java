/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2002  Emmanuel Bourg
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

package org.lfjr.jts.commands;

import java.util.*;

import org.lfjr.jts.*;
import org.lfjr.jts.config.*;

/**
 * List all players connected to the server.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class WhoCommand implements Command
{
    private int accessLevel = 0;

    public String[] getAliases()
    {
        return (new String[] { "who", "w", "cwho" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage()
    {
        return "/who";
    }

    public String getDescription()
    {
        return "List all players connected to the server.";
    }

    public void execute(Message m)
    {
        String cmd = m.getStringParameter(1);
        TetriNETClient client = (TetriNETClient)m.getSource();
        ChannelManager channelManager = ChannelManager.getInstance();

        Message response = new Message(Message.MSG_PLINE);
        Object params[] = { new Integer(0), ChatColors.darkBlue + "Channel\t\tNickname(s)" };
        response.setParameters(params);
        client.sendMessage(response);

        Iterator it = channelManager.channels();
        while(it.hasNext())
        {
            Channel channel = (Channel)it.next();

            // skipping empty channels
            if (channel.getPlayerCount() > 0)
            {
                ChannelConfig conf = channel.getConfig();

                boolean isInChannel = false;
                String channelColor = ChatColors.purple;
                StringBuffer message = new StringBuffer();
                message.append("[" + conf.getName() + "] " + ChatColors.darkBlue);

                for (int i = 1; i <= 6; i++)
                {
                    TetriNETClient clientInChannel = channel.getPlayer(i);
                    if (clientInChannel != null)
                    {
                        if (clientInChannel.getPlayer().getAccessLevel() > 0) message.append(ChatColors.bold);
                        message.append(" " + clientInChannel.getPlayer().getName());
                        if (clientInChannel.getPlayer().getAccessLevel() > 0) message.append(ChatColors.bold);
                    }
                    if (client == clientInChannel) isInChannel = true;
                }

                if (isInChannel) channelColor = ChatColors.red;

                Message response2 = new Message(Message.MSG_PLINE);
                Object params2[] = { new Integer(0), channelColor + message.toString() };
                response2.setParameters(params2);
                client.sendMessage(response2);
            }
        }
    }
}
