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
import java.text.*;
import org.lfjr.jts.*;
import org.lfjr.jts.config.*;

/**
 * Display the server's time.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TimeCommand implements Command
{
    private int accessLevel = 0;

    public String[] getAliases()
    {
        return (new String[] { "time", "date" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage()
    {
        return "/time";
    }

    public String getDescription()
    {
        return "Display the server's time.";
    }

    public void execute(Message m)
    {
        TetriNETClient client = (TetriNETClient)m.getSource();
        ServerConfig conf = TetriNETServer.getInstance().getConfig();
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, conf.getLocale());

        String message = ChatColors.darkBlue + "Server time is " + ChatColors.purple + df.format(new Date())
                         + ChatColors.darkBlue + " [" + TimeZone.getDefault().getDisplayName(conf.getLocale()) + "]";
        Message response = new Message(Message.MSG_PLINE, new Object[] { new Integer(0), message });
        client.sendMessage(response);
    }
}
