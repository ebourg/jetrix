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
import net.jetrix.config.*;
import net.jetrix.messages.*;

/**
 * Display the ping to the server. To compute the ping of tetrinet and
 * tetrifast clients we send the <tt>playernum<tt> message that triggers
 * a <tt>team</tt> message as response. We assume the ping is half the time
 * between the request and the response. Since a command cannot wait for
 * a client response this command must work with the PingFilter that is
 * processing all <tt>team</tt> messages. This command sets the client
 * properties <tt>command.ping=true</tt> and <tt>command.ping.time</tt>
 * and send the <tt>playernum<tt> message. The filter will then listen for
 * <tt>team</tt> messages and check if the property <tt>command.ping</tt>
 * is true. If so it will display the ping of the player.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class PingCommand implements Command
{
    private int accessLevel = 0;

    public String[] getAliases()
    {
        return (new String[] { "ping" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage(Locale locale)
    {
        return "/ping";
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command.ping.description", locale);
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client)m.getSource();
        User user = client.getUser();

        // @todo check if the client use the tetrinet protocol

        // check if a previous /ping request has still to be completed
        if ("true".equals(user.getProperty("command.ping"))) return;

        PlayerNumMessage response = new PlayerNumMessage();
        response.setSlot(client.getChannel().getClientSlot(client));

        // set the client properties to be used by the PingFilter
        user.setProperty("command.ping", "true");
        user.setProperty("command.ping.time", new Long(System.currentTimeMillis()));

        client.sendMessage(response);
    }
}
