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

import static net.jetrix.GameState.*;

import java.util.*;

import net.jetrix.*;
import net.jetrix.clients.TetrinetClient;
import net.jetrix.config.*;
import net.jetrix.messages.channel.CommandMessage;
import net.jetrix.messages.channel.PlineMessage;

/**
 * List available channels.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ListCommand extends AbstractCommand
{
    public String[] getAliases()
    {
        return (new String[]{"list", "l"});
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client) m.getSource();
        ChannelManager channelManager = ChannelManager.getInstance();
        Locale locale = client.getUser().getLocale();

        // get the name of the channel of this player to highlight it
        String playerChannel = "";
        if (client.getChannel() != null) playerChannel = client.getChannel().getConfig().getName();

        PlineMessage response = new PlineMessage();
        response.setKey("command.list.header");
        client.send(response);
        
        int i = 0;
        for (Channel channel : channelManager.channels())
        {
            i++;
            
            ChannelConfig conf = channel.getConfig();

            // skip invisible channels
            if (!conf.isVisible())
            {
                continue;
            }
            
            // skip channels with an incompatible speed constraint
            if (client.getUser().isPlayer() && !conf.isProtocolAccepted(client.getProtocol().getName()))
            {
                continue;
            }
            
            StringBuilder message = new StringBuilder();
            message.append("<darkBlue>(" + (playerChannel.equals(conf.getName()) ? "<red>" + i + "</red>" : "<purple>" + i + "</purple>") + ") ");
            message.append("<purple>" + rightPad(conf.getName(), 6) + "</purple>\t");
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
                message.append("[<aqua>" + Language.getText("command.list.status.open", locale) + "</aqua><blue>-" + channel.getPlayerCount() + "/" + conf.getMaxPlayers() + "</blue>]");
            }
            if (channel.getGameState() != STOPPED)
            {
                message.append(" <gray>{" + Language.getText("command.list.status.ingame", locale) + "}</gray> ");
            }
            else
            {
                message.append("                  ");
            }
            message.append("<black>" + conf.getDescription());
            
            client.send(new PlineMessage(message.toString()));
        }
    }

    /**
     * Add spaces at the right of the string if it's shorter than the specified length.
     */
    private String rightPad(String s, int length)
    {
        while (s.length() < length)
        {
            s += " ";
        }
        
        return s;
    }
}
