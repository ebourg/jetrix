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

package org.lfjr.jts.filter;

import java.util.*;
import org.lfjr.jts.*;

/**
 * Display the ping of the player if a <tt>team</tt> message is processed
 * and the player property <tt>command.ping</tt> is set to true.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class PingFilter extends MessageFilter
{

    public final void process(Message m, List out)
    {
        if (m.getCode() == Message.MSG_TEAM)
        {
            TetriNETClient client = (TetriNETClient)m.getSource();
            TetriNETPlayer player = client.getPlayer();

            if ("true".equals((player.getProperty("command.ping"))))
            {
                long delay = (System.currentTimeMillis() - ((Long)player.getProperty("command.ping.time")).longValue()) >> 1;
                String message = ChatColors.darkBlue + "Your ping time is " + ChatColors.bold + delay + "ms";

                Message response = new Message(Message.MSG_PLINE);
                response.setParameters(new Object[] { new Integer(0), message });
                client.sendMessage(response);

                player.setProperty("command.ping", "false");
            }
        }
        out.add(m);
    }

}
