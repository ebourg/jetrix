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
import net.jetrix.messages.*;

/**
 * Summon a player to the current channel.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class SummonCommand implements Command
{
    public String[] getAliases()
    {
        return (new String[] { "summon" });
    }

    public int getAccessLevel()
    {
        return AccessLevel.OPERATOR;
    }

    public String getUsage(Locale locale)
    {
        return "/summon <" + Language.getText("command.params.player_name", locale) + ">";
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command.summon.description", locale);
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
                Channel channel = client.getChannel();

                if (target == client)
                {
                    PlineMessage cantsummon = new PlineMessage();
                    cantsummon.setKey("command.summon.yourself");
                    client.sendMessage(cantsummon);
                }
                else if (channel == target.getChannel())
                {
                    PlineMessage cantsummon = new PlineMessage();
                    cantsummon.setKey("command.summon.same_channel", target.getUser().getName());
                    client.sendMessage(cantsummon);
                }
                else if (channel.isFull())
                {
                    // sending channel full message
                    PlineMessage channelfull = new PlineMessage();
                    channelfull.setKey("command.summon.full");
                    client.sendMessage(channelfull);
                }
                else
                {
                    // adding the ADDPLAYER message to the queue of the target channel
                    AddPlayerMessage move = new AddPlayerMessage(target);
                    channel.sendMessage(move);

                    PlineMessage summoned1 = new PlineMessage();
                    summoned1.setKey("command.summon.summoned", target.getUser().getName());
                    client.sendMessage(summoned1);

                    PlineMessage summoned2 = new PlineMessage();
                    summoned2.setKey("command.summon.summoned_by", client.getUser().getName());
                    target.sendMessage(summoned2);
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
