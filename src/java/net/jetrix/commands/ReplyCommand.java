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
 * Reply to a private message sent by a player.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ReplyCommand implements Command
{
    private int accessLevel = 0;

    public String[] getAliases()
    {
        return (new String[] { "reply", "r" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage(Locale locale)
    {
        return "/reply <" + Language.getText("command.params.message", locale) + ">";
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command.reply.description", locale);
    }

    public void execute(CommandMessage m)
    {
        String cmd = m.getCommand();
        Client client = (Client)m.getSource();

        if (m.getParameterCount() >= 1)
        {
            String targetName = (String)client.getUser().getProperty("command.tell.reply_to");

            // aucun message précédent
            if (targetName == null)
            {
                // no previous message
                PlineMessage response = new PlineMessage();
                response.setKey("command.reply.no_previous_message");
                client.sendMessage(response);
                return;
            }

            ClientRepository repository = ClientRepository.getInstance();
            Client target = repository.getClient(targetName);

            if (target == null)
            {
                // previous user no longer connected
                PlineMessage response = new PlineMessage();
                response.setKey("command.player_not_found", new Object[] { targetName });
                client.sendMessage(response);
            }
            else
            {
                // player found
                PlineMessage response = new PlineMessage();
                String privateMessage = m.getText();
                response.setKey("command.tell.format", new Object[] { client.getUser().getName(), privateMessage });
                target.sendMessage(response);

                target.getUser().setProperty("command.tell.reply_to", client.getUser());
            }
        }
        else
        {
            // not enough parameters
            PlineMessage response = new PlineMessage();
            String message = "<red>" + cmd + "<blue><message>";
            response.setText(message);
            client.sendMessage(response);
        }
    }

}
