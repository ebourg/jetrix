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
import net.jetrix.messages.*;

/**
 * Kick a player out of the server.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class KickCommand implements Command
{
    private int accessLevel = 1;
    private Logger logger = Logger.getLogger("net.jetrix");

    public String[] getAliases()
    {
        return (new String[] { "kick", "disconnect" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage(Locale locale)
    {
        return "/kick <" + Language.getText("command.params.player_name_num", locale) + ">";
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command.kick.description", locale);
    }

    public void execute(CommandMessage m)
    {
        String cmd = m.getCommand();
        Client client = (Client)m.getSource();

        if (m.getParameterCount() >= 1)
        {
            String targetName = m.getParameter(0);
            Client target = null;

            // checking if the second parameter is a slot number
            try
            {
                int slot = Integer.parseInt(targetName);
                if (slot >= 1 && slot <= 6)
                {
                    Channel channel = client.getChannel();
                    target = channel.getClient(slot);
                }
            }
            catch (NumberFormatException e) {}

            if (target == null)
            {
                // target is still null, the second parameter is a playername
                ClientRepository repository = ClientRepository.getInstance();
                target = repository.getClient(targetName);
            }

            if (target == null)
            {
                // no player found
                PlineMessage response = new PlineMessage();
                response.setKey("command.player_not_found", new Object[] { targetName });
                client.sendMessage(response);
            }
            else
            {
                // player found
                logger.info(target.getUser().getName() + " (" + target.getInetAddress() + ") has been kicked by " + client.getUser().getName() + " (" + client.getInetAddress() + ")");
                target.disconnect();
            }
        }
        else
        {
            // not enough parameters
            PlineMessage response = new PlineMessage();
            response.setText("<red>" + cmd + "<blue> <playername|playernumber>");
            client.sendMessage(response);
        }
    }

}
