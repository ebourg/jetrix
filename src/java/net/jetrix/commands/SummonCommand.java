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
 * Summon a player to the current channel.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class SummonCommand implements Command
{
    private int accessLevel = 1;

    public String[] getAliases()
    {
        return (new String[] { "summon" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage()
    {
        return "/summon <player name>";
    }

    public String getDescription()
    {
        return "Summon a player to the current channel.";
    }

    public void execute(CommandMessage m)
    {
        String cmd = m.getCommand();
        Client client = (Client)m.getSource();

        if (m.getParameterCount() >= 1)
        {
            String targetName = m.getParameter(0);
            Client target = null;

            if (target == null)
            {
                // target is still null, the second parameter is a playername
                ClientRepository repository = ClientRepository.getInstance();
                target = repository.getClient(targetName);
            }

            if (target == null)
            {
                // no player found
                String message = Color.red + "Player " + targetName + " cannot be found on the server.";
                PlineMessage reponse = new PlineMessage(message);
                client.sendMessage(reponse);
            }
            else
            {
                // player found
                Channel channel = client.getChannel();

                if (target == client)
                {
                    PlineMessage cantsummon = new PlineMessage();
                    String msg = "You can't summon yourself!";
                    cantsummon.setText(Color.gray + msg);
                    client.sendMessage(cantsummon);
                }
                else if (channel == target.getChannel())
                {
                    PlineMessage cantsummon = new PlineMessage();
                    String msg = target.getPlayer().getName() + " is already in your channel!";
                    cantsummon.setText(Color.gray + msg);
                    client.sendMessage(cantsummon);
                }
                else if (channel.isFull())
                {
                    // sending channel full message
                    PlineMessage channelfull = new PlineMessage();
                    String msg = Color.darkBlue + "Your channel is "
                                 + Color.red+"FULL" + Color.darkBlue + "!";
                    channelfull.setText(msg);
                    client.sendMessage(channelfull);
                }
                else
                {
                    // adding the ADDPLAYER message to the queue of the target channel
                    AddPlayerMessage move = new AddPlayerMessage(target);
                    channel.addMessage(move);

                    PlineMessage summoned1 = new PlineMessage();
                    String msg = target.getPlayer().getName() + " has been summoned";
                    summoned1.setText(Color.gray + msg);
                    client.sendMessage(summoned1);

                    PlineMessage summoned2 = new PlineMessage();
                    msg = "You have been summoned by " + client.getPlayer().getName() + "!";
                    summoned2.setText(Color.gray + msg);
                    target.sendMessage(summoned2);
                }
            }
        }
        else
        {
            // not enough parameters
            PlineMessage response = new PlineMessage();
            String message = Color.red + cmd + Color.blue + " <player name>";
            response.setText(message);
            client.sendMessage(response);
        }
    }
}
