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
 * Send a message to all clients on the server.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class BroadcastCommand extends AbstractCommand implements ParameterCommand
{
    public BroadcastCommand()
    {
        setAccessLevel(AccessLevel.OPERATOR);
    }

    public String[] getAliases()
    {
        return (new String[] { "broadcast", "br", "gmsg", "shout" });
    }

    public String getUsage(Locale locale)
    {
        return "/br <" + Language.getText("command.params.message", locale) + ">";
    }

    public int getParameterCount()
    {
        return 1;
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client) m.getSource();

        // preparing message
        PlineMessage response = new PlineMessage();
        response.setKey("command.broadcast.message", client.getUser().getName(), m.getText());
        response.setSource(client);

        for (Client target : ClientRepository.getInstance().getClients())
        {
            target.send(response);
        }
    }

}
