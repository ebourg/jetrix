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

package net.jetrix.commands;

import java.util.*;
import net.jetrix.*;
import net.jetrix.config.*;
import net.jetrix.messages.*;

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

    public void execute(CommandMessage m)
    {
        Client client = (Client)m.getSource();
        ChannelManager channelManager = ChannelManager.getInstance();

        PlineMessage response = new PlineMessage();
        response.setText(Color.darkBlue+"TetriNET Channel Lister - (Type "+Color.red+"/join "+Color.purple+"channelname"+Color.darkBlue+")");
        client.sendMessage(response);

        Iterator it = channelManager.channels();
        int i = 1;
        while(it.hasNext())
        {
            Channel channel = (Channel)it.next();
            ChannelConfig conf = channel.getConfig();

            String cname = conf.getName();
            while (cname.length() < 6) cname += " ";

            String message = Color.darkBlue+"("+(client.getChannel().getConfig().getName().equals(conf.getName())?Color.red:Color.purple)+i+Color.darkBlue+") " + Color.purple + cname + "\t"
                             + (channel.isFull()?Color.darkBlue+"["+Color.red+"FULL"+Color.darkBlue+"]       ":Color.darkBlue+"["+Color.aqua+"OPEN"+Color.blue+"-" + channel.getPlayerCount() + "/"+conf.getMaxPlayers() + Color.darkBlue + "]")
                             + (channel.getGameState()!=Channel.GAME_STATE_STOPPED?Color.gray+" {INGAME} ":"                  ")
                             + Color.black + conf.getDescription();

            PlineMessage response2 = new PlineMessage();
            response2.setText(message);
            client.sendMessage(response2);

            i = i + 1;
        }
    }
}
