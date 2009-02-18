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

import net.jetrix.*;
import net.jetrix.messages.channel.CommandMessage;
import net.jetrix.messages.channel.PlineMessage;

import java.util.*;

/**
 * Send a request for assistance to all operators online.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class PetitionCommand extends AbstractCommand implements ParameterCommand
{
    public String[] getAliases()
    {
        return (new String[]{"petition", "omsg"});
    }

    public String getUsage(Locale locale)
    {
        return "/petition <" + Language.getText("command.params.message", locale) + ">";
    }

    public int getParameterCount()
    {
        return 1;
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client) m.getSource();

        Iterator<Client> operators = ClientRepository.getInstance().getOperators();

        if (operators.hasNext())
        {
            PlineMessage petition = new PlineMessage();
            String message = m.getText();
            petition.setKey("command.tell.format", client.getUser().getName(), message);
            petition.setSource(client);

            while (operators.hasNext())
            {
                Client operator = operators.next();
                operator.send(petition);
                operator.getUser().setProperty("command.tell.reply_to", client.getUser().getName());
            }

            PlineMessage response = new PlineMessage();
            response.setKey("command.petition.sent");
            client.send(response);
        }
        else
        {
            // no operator online
            PlineMessage response = new PlineMessage();
            response.setKey("command.petition.no_operator");
            client.send(response);
        }
    }
}
