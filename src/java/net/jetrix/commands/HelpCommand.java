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
 * List all commands available.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class HelpCommand implements Command
{
    private int accessLevel = 0;

    public String[] getAliases()
    {
        return (new String[] { "help", "?", "h" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage(Locale locale)
    {
        return "/help";
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command.help.description", locale);
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client)m.getSource();
        CommandManager commandManager = CommandManager.getInstance();

        PlineMessage header = new PlineMessage();
        header.setKey("command.help.header");
        client.sendMessage(header);

        Iterator commands = commandManager.getCommands(client.getUser().getAccessLevel());
        while (commands.hasNext())
        {
            Command command = (Command)commands.next();

            PlineMessage ligne1 = new PlineMessage();
            String usage = command.getUsage(client.getUser().getLocale());
            String line1Body;
            int space = usage.indexOf(" ");
            if (space == -1)
                line1Body = "<red>" + usage;
            else
                line1Body = "<red>" + usage.substring(0, space) + "<aqua>" + usage.substring(space);
            ligne1.setText(line1Body);
            PlineMessage ligne2 = new PlineMessage("         " + command.getDescription(client.getUser().getLocale()));
            client.sendMessage(ligne1);
            client.sendMessage(ligne2);
        }
    }
}
