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

    public String getUsage(Locale locale)
    {
        return "/list";
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command.list.description", locale);
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client)m.getSource();
        ChannelManager channelManager = ChannelManager.getInstance();
        Locale locale = client.getUser().getLocale();
        
        // get the name of the channel of this player to highlight it
        String playerChannel = new String();
        if (client.getChannel() != null) playerChannel = client.getChannel().getConfig().getName();        

        PlineMessage response = new PlineMessage();
        response.setKey("command.list.header");        
        client.sendMessage(response);

        Iterator it = channelManager.channels();
        int i = 1;
        while(it.hasNext())
        {
            Channel channel = (Channel)it.next();
            ChannelConfig conf = channel.getConfig();

            String cname = conf.getName();
            while (cname.length() < 6) cname += " ";

            StringBuffer message = new StringBuffer();
            message.append("<darkBlue>("+(playerChannel.equals(conf.getName())?"<red>"+i+"</red>":"<purple>"+i+"</purple>")+ ") ");
            message.append("<purple>" + cname + "</purple>\t");
            if (channel.isFull())
            {
                message.append("[<red>" + Language.getText("command.list.status.full", locale) + "</red>]");
                for (int j = 0; j < 11 - Language.getText("command.list.status.full", locale).length(); j++)
                {
                    message.append(" ");
                }
            }
            else
            {
                message.append("[<aqua>" + Language.getText("command.list.status.open", locale) + "</aqua><blue>-" + channel.getPlayerCount() + "/"+conf.getMaxPlayers() + "</blue>]");
            }
            if (channel.getGameState() != Channel.GAME_STATE_STOPPED)
            {
                message.append(" <gray>{" + Language.getText("command.list.status.ingame", locale) + "}</gray> ");
            }
            else
            {
                message.append("                  ");
            }
            message.append("<black>" + conf.getDescription());

            PlineMessage response2 = new PlineMessage();
            response2.setText(message.toString());
            client.sendMessage(response2);

            i = i + 1;
        }
    }
}
