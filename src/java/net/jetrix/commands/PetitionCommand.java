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

import net.jetrix.*;
import net.jetrix.messages.*;

import java.util.*;

/**
 * Send a request for assistance to all operators online.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class PetitionCommand implements Command
{
    public String[] getAliases()
    {
        return (new String[]{"petition", "omsg"});
    }

    public int getAccessLevel()
    {
        return AccessLevel.PLAYER;
    }

    public String getUsage(Locale locale)
    {
        return "/petition <" + Language.getText("command.params.message", locale) + ">";
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command.petition.description", locale);
    }

    public void execute(CommandMessage m)
    {
        String cmd = m.getCommand();
        Client client = (Client) m.getSource();

        if (m.getParameterCount() >= 1)
        {
            Iterator operators = ClientRepository.getInstance().getOperators();

            if (operators.hasNext())
            {
                PlineMessage petition = new PlineMessage();
                String message = m.getText();
                petition.setKey("command.tell.format", client.getUser().getName(), message);

                while (operators.hasNext())
                {
                    Client operator = (Client) operators.next();
                    operator.sendMessage(petition);
                    operator.getUser().setProperty("command.tell.reply_to", client.getUser().getName());
                }

                PlineMessage response = new PlineMessage();
                response.setKey("command.petition.sent");
                client.sendMessage(response);
            }
            else
            {
                // no operator online
                PlineMessage response = new PlineMessage();
                response.setKey("command.petition.no_operator");
                client.sendMessage(response);
            }
        }
        else
        {
            // not enough parameters
            Locale locale = client.getUser().getLocale();
            PlineMessage response = new PlineMessage();
            String message = "<red>" + cmd + " <blue><" + Language.getText("command.params.message", locale) + ">";
            response.setText(message);
            client.sendMessage(response);
        }
    }
}
