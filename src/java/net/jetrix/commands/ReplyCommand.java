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
 * Reply to a private message sent by a player.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ReplyCommand extends AbstractCommand implements ParameterCommand
{
    public String[] getAliases()
    {
        return (new String[] { "reply", "r" });
    }

    public String getUsage(Locale locale)
    {
        return "/reply <" + Language.getText("command.params.message", locale) + ">";
    }

    public int getParameterCount()
    {
        return 1;
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client) m.getSource();

        String targetName = (String) client.getUser().getProperty("command.tell.reply_to");

        if (targetName == null)
        {
            // no previous message
            PlineMessage response = new PlineMessage();
            response.setKey("command.reply.no_previous_message");
            client.send(response);
            return;
        }

        ClientRepository repository = ClientRepository.getInstance();
        Client target = repository.getClient(targetName);

        if (target == null)
        {
            // previous user no longer connected
            PlineMessage response = new PlineMessage();
            response.setKey("command.player_not_found", targetName);
            client.send(response);
        }
        else
        {
            // player found
            PlineMessage response = new PlineMessage();
            String privateMessage = m.getText();
            response.setKey("command.tell.format", client.getUser().getName(), privateMessage);
            response.setSource(client);
            target.send(response);

            target.getUser().setProperty("command.tell.reply_to", client.getUser());

            // afk message
            if (target.getUser().getStatus() == User.STATUS_AFK)
            {
                String awayMessage = (String) target.getUser().getProperty("command.away.message");
                PlineMessage away = new PlineMessage();
                away.setKey("command.away.player_unavailable" + (awayMessage != null ? "2" : ""), target.getUser().getName(), awayMessage);
                client.send(away);
            }
        }
    }

}
