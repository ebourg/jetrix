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
import net.jetrix.messages.*;

/**
 * Send a message to all clients on the server.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class BroadcastCommand implements Command
{
    private int accessLevel = 1;

    public String[] getAliases()
    {
        return (new String[] { "broadcast", "br", "gmsg", "shout" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage()
    {
        return "/br <message>";
    }

    public String getDescription()
    {
        return "Send a message to all clients on the server.";
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client)m.getSource();

        if (m.getParameterCount() >= 1)
        {
            // preparing message
            PlineMessage response = new PlineMessage();

            String messageBody = m.getText();
            String message = Color.bold + Color.red + "[Broadcast from " + Color.purple
                             + client.getPlayer().getName() + Color.red + "] "
                             + Color.darkBlue + messageBody;
            response.setText(message);

            Iterator clients = ClientRepository.getInstance().getClients();
            while (clients.hasNext())
            {
                Client target = (Client)clients.next();
                target.sendMessage(response);
            }
        }
        else
        {
            // not enough parameters
            PlineMessage response = new PlineMessage();
            String message = Color.red + m.getCommand() + Color.blue + " <message>";
            response.setText(message);
            client.sendMessage(response);
        }
    }

}
