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

package org.lfjr.jts.commands;

import java.util.*;

import org.lfjr.jts.*;
import org.lfjr.jts.config.*;

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

    public String getUsage()
    {
        return "/join <channel name|channel number";
    }

    public String getDescription()
    {
        return "Join or create a channel.";
    }

    public void execute(Message m)
    {
        TetriNETClient client = (TetriNETClient)m.getSource();

        Channel target = ChannelManager.getInstance().getChannel(m.getStringParameter(2));
        if (target != null)
        {
            if ( target.isFull() )
            {
                // sending channel full message
                Message channelfull = new Message(Message.MSG_PLINE);
                channelfull.setParameters(new Object[] { new Integer(0), ChatColors.darkBlue+"That channel is "+ChatColors.red+"FULL"+ChatColors.darkBlue+"!" });
                client.sendMessage(channelfull);
            }
            else
            {
                // adding the ADDPLAYER message to the queue of the target channel
                Message move = new Message(Message.MSG_ADDPLAYER, new Object[] { m.getSource() });
                target.addMessage(move);
            }
        }
    }
}
