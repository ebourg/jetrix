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
import net.jetrix.messages.*;

/**
 * Summon a player to the current channel.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class SummonCommand extends AbstractCommand implements ParameterCommand
{
    public SummonCommand()
    {
        setAccessLevel(AccessLevel.OPERATOR);
    }

    public String getAlias()
    {
        return "summon";
    }

    public String getUsage(Locale locale)
    {
        return "/summon <" + Language.getText("command.params.player_name", locale) + ">";
    }

    public int getParameterCount()
    {
        return 1;
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client) m.getSource();

        String targetName = m.getParameter(0);

        ClientRepository repository = ClientRepository.getInstance();
        Client target = repository.getClient(targetName);

        if (target == null)
        {
            // no player found
            client.send(new PlineMessage("command.player_not_found", targetName));
        }
        else
        {
            // player found
            Channel channel = client.getChannel();

            if (target == client)
            {
                PlineMessage cantsummon = new PlineMessage();
                cantsummon.setKey("command.summon.yourself");
                client.send(cantsummon);
            }
            else if (channel == target.getChannel())
            {
                PlineMessage cantsummon = new PlineMessage();
                cantsummon.setKey("command.summon.same_channel", target.getUser().getName());
                client.send(cantsummon);
            }
            else if (channel.isFull())
            {
                // sending channel full message
                PlineMessage channelfull = new PlineMessage();
                channelfull.setKey("command.summon.full");
                client.send(channelfull);
            }
            else
            {
                // adding the ADDPLAYER message to the queue of the target channel
                AddPlayerMessage move = new AddPlayerMessage(target);
                channel.send(move);

                PlineMessage summoned1 = new PlineMessage();
                summoned1.setKey("command.summon.summoned", target.getUser().getName());
                client.send(summoned1);

                PlineMessage summoned2 = new PlineMessage();
                summoned2.setKey("command.summon.summoned_by", client.getUser().getName());
                target.send(summoned2);
            }
        }
    }
}
