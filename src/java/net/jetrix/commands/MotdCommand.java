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

import java.io.*;
import java.util.*;
import net.jetrix.*;
import net.jetrix.config.*;
import net.jetrix.messages.*;

/**
 * Display the message of the day.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class MotdCommand implements Command
{
    private int accessLevel = 0;

    public String[] getAliases()
    {
        return (new String[] { "motd" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage(Locale locale)
    {
        return "/motd";
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command.motd.description", locale);
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client)m.getSource();
        ServerConfig conf = Server.getInstance().getConfig();

        try
        {
            BufferedReader motd = new BufferedReader(new StringReader( conf.getMessageOfTheDay() ));
            String motdline;
            while( (motdline = motd.readLine() ) != null )
            {
                Message response = new PlineMessage("<gray>" + motdline);
                client.sendMessage(response);
            }
            motd.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
