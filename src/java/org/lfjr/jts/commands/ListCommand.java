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
 * List available channels.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ListCommand implements Command
{
    private int accessLevel = 0;

    public String[] getAliases()
    {
        return (new String[] { "list", "l" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage()
    {
        return "/list";
    }

    public String getDescription()
    {
        return "List available channels.";
    }

    public void execute(Message m)
    {
        TetriNETClient client = (TetriNETClient)m.getSource();
        ChannelManager channelManager = ChannelManager.getInstance();

        Message response = new Message(Message.MSG_PLINE);
        Object params[] = { new Integer(0), ChatColors.darkBlue+"TetriNET Channel Lister - (Type "+ChatColors.red+"/join "+ChatColors.purple+"channelname"+ChatColors.darkBlue+")" };
        response.setParameters(params);
        client.sendMessage(response);

        Iterator it = channelManager.channels();
        int i = 1;
        while(it.hasNext())
        {
            Channel channel = (Channel)it.next();
            ChannelConfig conf = channel.getConfig();

            String cname = conf.getName();
            while (cname.length() < 6) cname += " ";

            String message = ChatColors.darkBlue+"("+(client.getChannel().getConfig().getName().equals(conf.getName())?ChatColors.red:ChatColors.purple)+i+ChatColors.darkBlue+") " + ChatColors.purple + cname + "\t"
                             + (channel.isFull()?ChatColors.darkBlue+"["+ChatColors.red+"FULL"+ChatColors.darkBlue+"]       ":ChatColors.darkBlue+"["+ChatColors.aqua+"OPEN"+ChatColors.blue+"-" + channel.getPlayerCount() + "/"+conf.getMaxPlayers() + ChatColors.darkBlue + "]")
                             + (channel.getGameState()!=Channel.GAME_STATE_STOPPED?ChatColors.gray+" {INGAME} ":"                  ")
                             + ChatColors.black + conf.getDescription();

            Message response2 = new Message(Message.MSG_PLINE);
            Object params2[] = { new Integer(0), message };
            response2.setParameters(params2);
            client.sendMessage(response2);

            i = i + 1;
        }
    }
}
