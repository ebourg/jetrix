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
import net.jetrix.messages.*;
import org.apache.commons.collections.*;

/**
 * CommandManager
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class CommandManager
{
    private static CommandManager instance = new CommandManager();
    private Map commands;
    private Map commandSet;

    private CommandManager()
    {
        commands = new TreeMap();
        commandSet = new TreeMap();
    }

    public static CommandManager getInstance()
    {
        return instance;
    }

    public void addCommand(Command command)
    {
        String aliases[] = command.getAliases();
        for (int i = 0; i < aliases.length; i++)
        {
            addCommandAlias(command, aliases[i]);
        }

        if (aliases.length > 0) commandSet.put(command.getAliases()[0], command);
    }

    public void addCommandAlias(Command command, String alias)
    {
        commands.put(alias.toLowerCase(), command);
    }

    /**
     * Return the command matching the specified alias. The alias is not
     * case sensitive. If no command matches the exact alias specified,
     * it will return the first command starting with the alias.
     */
    public Command getCommand(String alias)
    {
        Command command = (Command)commands.get(alias.toLowerCase());

        if (command == null)
        {
            Iterator aliases = commands.keySet().iterator();
            while (command == null && aliases.hasNext())
            {
                String name = ((String)aliases.next()).toLowerCase();
                if (name.startsWith(alias.toLowerCase()))
                {
                    command = (Command)commands.get(name);
                }
            }
        }

        return command;
    }

    /**
     * Return all commands available to clients with the lowest
     * access level.
     */
    public Iterator getCommands()
    {
         return getCommands(0);
    }

    /**
     * Return all commands available to clients with the specified
     * access level.
     */
    public Iterator getCommands(final int accessLevel)
    {
        Predicate availableToLevel = new Predicate() {
            public boolean evaluate(Object obj)
            {
                Command command = (Command)obj;
                return command.getAccessLevel() <= accessLevel;
            }
        };

        return new FilterIterator(commandSet.values().iterator(), availableToLevel);
    }

    /**
     * Execute the command specified in the message. This method checks if
     * the client has the access level required to use the command.
     */
    public void execute(CommandMessage m)
    {
        Command command = getCommand(m.getCommand());
        Client client = (Client)m.getSource();
        if (command == null)
        {
            PlineMessage response = new PlineMessage();
            response.setKey("command.invalid");
            client.sendMessage(response);
        }
        else
        {
            // check the access level
            if (client.getPlayer().getAccessLevel() >= command.getAccessLevel())
            {
                command.execute(m);
            }
            else
            {
                // denying access
                PlineMessage response = new PlineMessage();
                response.setKey("command.denied");
                client.sendMessage(response);
            }
        }
    }

    /**
     * Clear all commands.
     */
    public void clear()
    {
        commands.clear();
        commandSet.clear();
    }

}
