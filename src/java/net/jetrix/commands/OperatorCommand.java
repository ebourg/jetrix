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
import net.jetrix.config.*;
import net.jetrix.messages.*;

/**
 * Grant operator status to the player.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class OperatorCommand implements Command
{
    private Logger log = Logger.getLogger("net.jetrix");

    public String[] getAliases()
    {
        return (new String[]{"op", "operator"});
    }

    public int getAccessLevel()
    {
        return AccessLevel.PLAYER;
    }

    public String getUsage(Locale locale)
    {
        return "/op <" + Language.getText("command.params.password", locale) + ">";
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command.operator.description", locale);
    }

    public void execute(CommandMessage m)
    {
        String cmd = m.getCommand();
        Client client = (Client) m.getSource();
        ServerConfig conf = Server.getInstance().getConfig();

        if (m.getParameterCount() >= 1)
        {
            String password = m.getParameter(0);

            if (password.equalsIgnoreCase(conf.getOpPassword()))
            {
                // access granted
                client.getUser().setAccessLevel(1);
                PlineMessage response = new PlineMessage();
                response.setKey("command.operator.granted");
                client.sendMessage(response);
            }
            else
            {
                // access denied, logging attempt
                log.severe(client.getUser().getName() + "(" + client.getInetAddress() + ") attempted to get operator status.");
                PlineMessage response = new PlineMessage();
                response.setKey("command.operator.denied");
                client.sendMessage(response);
            }
        }
        else
        {
            // not enough parameters
            PlineMessage response = new PlineMessage();
            response.setText("<red>" + cmd + "<blue> <password>");
            client.sendMessage(response);
        }
    }
}
