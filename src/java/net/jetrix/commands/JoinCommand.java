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
import net.jetrix.config.*;
import net.jetrix.messages.*;

/**
 * Join or create a channel.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class JoinCommand implements Command
{
    private int accessLevel = 0;

    public String[] getAliases()
    {
        return (new String[] { "join", "j" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage(Locale locale)
    {
        return "/join <" + Language.getText("command.params.channel_name_num", locale) + ">";
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command.join.description", locale);
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client)m.getSource();

        Channel target = ChannelManager.getInstance().getChannel(m.getParameter(0));
        if (target != null)
        {
            if ( target.isFull() )
            {
                // sending channel full message
                PlineMessage channelfull = new PlineMessage();
                channelfull.setKey("command.join.full");
                client.sendMessage(channelfull);
            }
            else
            {
                // adding the ADDPLAYER message to the queue of the target channel
                AddPlayerMessage move = new AddPlayerMessage();
                move.setClient((Client)m.getSource());
                target.sendMessage(move);
            }
        }
    }
}
