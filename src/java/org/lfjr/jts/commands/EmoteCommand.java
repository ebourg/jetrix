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

import org.lfjr.jts.*;

/**
 * Display an emote.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class EmoteCommand implements Command
{
    private int accessLevel = 0;

    public String[] getAliases()
    {
        return (new String[] { "me", "emote" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage()
    {
        return "/me";
    }

    public String getDescription()
    {
        return "Display an emote.";
    }

    public void execute(Message m)
    {
        TetriNETClient client = (TetriNETClient)m.getSource();
        String emote = m.getRawMessage().substring(m.getStringParameter(1).length() + 9);

        Message response = new Message(Message.MSG_PLINEACT);
        response.setParameters(new Object[] { new Integer(client.getChannel().getPlayerSlot(client)), emote });
        client.getChannel().addMessage(response);
    }
}
