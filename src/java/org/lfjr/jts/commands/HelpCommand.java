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

    public String getUsage()
    {
        return "/help";
    }

    public String getDescription()
    {
        return "List all commands available.";
    }

    public void execute(Message m)
    {
        TetriNETClient client = (TetriNETClient)m.getSource();
        CommandManager commandManager = CommandManager.getInstance();

        Message header = new Message(Message.MSG_PLINE);
        String headerBody = ChatColors.bold + ChatColors.darkBlue + "Commands available on the server :";
        header.setParameters(new Object[] { new Integer(0), headerBody });
        client.sendMessage(header);

        Iterator commands = commandManager.getCommands(client.getPlayer().getAccessLevel());
        while (commands.hasNext())
        {
            Command command = (Command)commands.next();

            Message ligne1 = new Message(Message.MSG_PLINE);
            String usage = command.getUsage();
            String line1Body;
            int space = usage.indexOf(" ");
            if (space == -1)
                line1Body = ChatColors.red + usage;
            else
                line1Body = ChatColors.red + usage.substring(0, space) + ChatColors.aqua + usage.substring(space);
            ligne1.setParameters(new Object[] { new Integer(0), line1Body });
            Message ligne2 = new Message(Message.MSG_PLINE);
            ligne2.setParameters(new Object[] { new Integer(0), "         " + command.getDescription() });
            client.sendMessage(ligne1);
            client.sendMessage(ligne2);
        }
    }
}
