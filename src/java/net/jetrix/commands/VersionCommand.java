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
        return (new String[] { "version" });
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

    public void execute(CommandMessage m)
    {
        Client client = (Client)m.getSource();

        String version1 = Color.darkBlue + "" + Color.bold + "JetriX/" + ServerConfig.VERSION + " (build:@build.time@)";
        String version2 = Color.purple+"VM"+Color.darkBlue+": " + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version") + " " + System.getProperty("java.vm.info");
        String version3 = Color.purple+"OS"+Color.darkBlue+": " + System.getProperty("os.name") + " " + System.getProperty("os.version") +"; " + System.getProperty("os.arch");

        client.sendMessage(new PlineMessage(version1));
        client.sendMessage(new PlineMessage(version2));
        client.sendMessage(new PlineMessage(version3));
    }
}
