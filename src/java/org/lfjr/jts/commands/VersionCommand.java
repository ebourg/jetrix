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
 * Display the version of the server.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class VersionCommand implements Command
{
    private int accessLevel = 0;

    public String[] getAliases()
    {
        return (new String[] { "version", "--version" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage()
    {
        return "/version";
    }

    public String getDescription()
    {
        return "Display the version of the server.";
    }

    public void execute(Message m)
    {
        TetriNETClient client = (TetriNETClient)m.getSource();

        String version1 = ChatColors.darkBlue + "" + ChatColors.bold + "JetriX/" + ServerConfig.VERSION + " (build:@build.time@)";
        String version2 = ChatColors.purple+"VM"+ChatColors.darkBlue+": " + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version") + " " + System.getProperty("java.vm.info");
        String version3 = ChatColors.purple+"OS"+ChatColors.darkBlue+": " + System.getProperty("os.name") + " " + System.getProperty("os.version") +"; " + System.getProperty("os.arch");

        Message response1 = new Message(Message.MSG_PLINE, new Object[] { new Integer(0), version1 });
        Message response2 = new Message(Message.MSG_PLINE, new Object[] { new Integer(0), version2 });
        Message response3 = new Message(Message.MSG_PLINE, new Object[] { new Integer(0), version3 });
        client.sendMessage(response1);
        client.sendMessage(response2);
        client.sendMessage(response3);
    }
}
