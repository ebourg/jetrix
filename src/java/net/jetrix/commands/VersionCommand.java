/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2003  Emmanuel Bourg
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
    public String[] getAliases()
    {
        return (new String[] { "version" });
    }

    public int getAccessLevel()
    {
        return AccessLevel.PLAYER;
    }

    public String getUsage(Locale locale)
    {
        return "/version";
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command.version.description", locale);
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client) m.getSource();

        String version1 = "<darkBlue><b>JetriX/" + ServerConfig.VERSION + " (build:@build.time@)";
        String version2 = "<purple>VM<darkBlue>: " + System.getProperty("java.vm.name") 
                          + " " + System.getProperty("java.vm.version") 
                          + " " + System.getProperty("java.vm.info");
        String version3 = "<purple>OS<darkBlue>: " + System.getProperty("os.name") 
                          + " " + System.getProperty("os.version") 
                          + "; " + System.getProperty("os.arch");

        client.sendMessage(new PlineMessage(version1));
        client.sendMessage(new PlineMessage(version2));
        client.sendMessage(new PlineMessage(version3));
    }
}
