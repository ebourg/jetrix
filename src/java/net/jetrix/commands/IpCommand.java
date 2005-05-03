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
 * Display the IP of a player.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class IpCommand extends AbstractCommand implements ParameterCommand
{
    public IpCommand()
    {
        setAccessLevel(AccessLevel.OPERATOR);
    }

    public String[] getAliases()
    {
        return (new String[]{"ip"});
    }

    public String getUsage(Locale locale)
    {
        return "/ip <" + Language.getText("command.params.player_name_num", locale) + ">";
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
            client.send(new PlineMessage("command.player_not_found", targetName));
        }
        else
        {
            // player found
            String hostname = target.getInetAddress().getHostName();
            String hostaddress = target.getInetAddress().getHostAddress();
            String message = "<darkBlue>[<red>" + target.getUser().getName() + "</red>] " + hostname;
            if (!hostname.equals(hostaddress)) message += " (" + hostaddress + ")";
            PlineMessage reponse = new PlineMessage(message);
            client.send(reponse);
        }
    }

}
