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
import java.util.logging.*;

import net.jetrix.*;
import net.jetrix.messages.*;

/**
 * Kick a player out of the server.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class KickCommand extends AbstractCommand implements ParameterCommand
{
    public KickCommand()
    {
        setAccessLevel(AccessLevel.OPERATOR);
    }

    public String[] getAliases()
    {
        return (new String[]{"kick", "disconnect"});
    }

    public String getUsage(Locale locale)
    {
        return "/kick <" + Language.getText("command.params.player_name_num", locale) + ">";
    }

    public int getParameterCount()
    {
        return 1;
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client) m.getSource();

        String targetName = m.getParameter(0);
        Client target = m.getClientParameter(0);

        if (target == null)
        {
            // no player found
            PlineMessage response = new PlineMessage();
            response.setKey("command.player_not_found", targetName);
            client.send(response);
        }
        else
        {
            // player found
            log.info(target.getUser().getName() + " (" + target.getInetAddress() + ") has been kicked by " + client.getUser().getName() + " (" + client.getInetAddress() + ")");
            target.disconnect();
        }
    }

}
