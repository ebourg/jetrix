/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2005  Emmanuel Bourg
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

package net.jetrix.filter;

import java.util.*;

import net.jetrix.*;
import net.jetrix.messages.*;

/**
 * Display the ping of the player if a <tt>team</tt> message is processed
 * and the player property <tt>command.ping</tt> is set to true. This filter
 * also intercepts in game messages containing only "t" and send back a "PONG"
 * message to the player.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class PingFilter extends MessageFilter
{
    public final void process(Message m, List<Message> out)
    {
        if (m instanceof TeamMessage)
        {
            Client client = (Client) m.getSource();
            User user = client.getUser();

            if ("true".equals((user.getProperty("command.ping"))))
            {
                long delay = (System.currentTimeMillis() - ((Long) user.getProperty("command.ping.time")).longValue()) >> 1;

                PlineMessage response = new PlineMessage();
                response.setKey("command.ping.message", new Long(delay));
                client.send(response);

                user.setProperty("command.ping", "false");
            }
            else
            {
                out.add(m);
            }
        }
        else if (m instanceof GmsgMessage && m.getSource() != null)
        {
            // send back "* PONG" to the player if he just wrote "t"
            GmsgMessage gmsg = (GmsgMessage) m;
            Client client = (Client) m.getSource();

            String text = "<" + client.getUser().getName() + "> t";

            if (text.equals(gmsg.getText()))
            {
                client.send(new GmsgMessage("* PONG"));
            }
            else
            {
                out.add(m);
            }
        }
        else
        {
            out.add(m);
        }
    }

    public String getName()
    {
        return "Ping";
    }

    public String getDescription()
    {
        return "Display the ping time.";
    }

    public String getVersion()
    {
        return "1.1";
    }

    public String getAuthor()
    {
        return "Emmanuel Bourg";
    }

}
