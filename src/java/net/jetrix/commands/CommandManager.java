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

import org.apache.commons.collections.*;

import net.jetrix.*;
import net.jetrix.messages.*;

/**
 * CommandManager
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class CommandManager
{
    private static CommandManager instance = new CommandManager();
    private Map<String, Command> commands;
    private Map<String, Command> commandSet;

    private CommandManager()
    {
        commands = new TreeMap<String, Command>();
        commandSet = new TreeMap<String, Command>();
    }

    public static CommandManager getInstance()
    {
        return instance;
    }

    /**
     * Register a new command.
     */
    public void addCommand(Command command)
    {
        String aliases[] = command.getAliases();
        for (int i = 0; i < aliases.length; i++)
        {
            addCommandAlias(command, aliases[i]);
        }

        if (aliases.length > 0) commandSet.put(command.getAliases()[0], command);
    }

    /**
     * Register a new alias for a command.
     */
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
        alias = alias.toLowerCase();
        Command command = commands.get(alias);

        if (command == null)
        {
            Iterator aliases = commands.keySet().iterator();
            while (command == null && aliases.hasNext())
            {
                String name = (String) aliases.next();
                if (name.startsWith(alias))
                {
                    command = commands.get(name);
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
        return getCommands(AccessLevel.PLAYER);
    }

    /**
     * Return all commands available to clients with the specified
     * access level.
     */
    public Iterator getCommands(final int accessLevel)
    {
        Predicate availableToLevel = new Predicate()
        {
            public boolean evaluate(Object obj)
            {
                Command command = (Command) obj;
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
        Client client = (Client) m.getSource();
        if (command == null)
        {
            PlineMessage response = new PlineMessage();
            response.setKey("command.invalid");
            client.send(response);
        }
        else
        {
            // check the access level
            if (client.getUser().getAccessLevel() >= command.getAccessLevel())
            {
                // check the number of parameters
                if (command instanceof ParameterCommand && m.getParameterCount() < ((ParameterCommand) command).getParameterCount())
                {
                    // not enough parameters
                    PlineMessage response = new PlineMessage();
                    response.setText(colorizeUsage(command.getUsage(client.getUser().getLocale())));
                    client.send(response);
                    return;
                }

                // execute the command
                try
                {
                    command.execute(m);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                // denying access
                PlineMessage response = new PlineMessage();
                response.setKey("command.denied");
                client.send(response);
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

    /**
     * Return a colorized usage string of the specified command.
     */
    String colorizeUsage(String usage)
    {
        usage = usage.trim();
        int i = usage.indexOf(" ");

        StringBuffer colorized = new StringBuffer();
        colorized.append("<red>");

        if (i > -1)
        {
            colorized.append(usage.substring(0, i));
            colorized.append(" <blue>");
            colorized.append(usage.substring(i + 1));
        }
        else
        {
            colorized.append(usage);
        }

        return colorized.toString();
    }

}
