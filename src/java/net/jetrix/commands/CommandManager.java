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

    /** aliases->commands Map */
    private Map<String, Command> commands;

    /** Set of commands sorted alphabetically by the main alias */
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
        for (String alias : command.getAliases())
        {
            addCommandAlias(command, alias);
        }

        if (command.getAliases().length > 0)
        {
            commandSet.put(command.getAliases()[0], command);
        }
    }

    /**
     * Register a new alias for a command.
     */
    public void addCommandAlias(Command command, String alias)
    {
        commands.put(alias.toLowerCase(), command);
    }

    /**
     * Remove a command.
     */
    public void removeCommand(Command command)
    {
        Iterator<String> aliases = commands.keySet().iterator();
        while (aliases.hasNext())
        {
            if (commands.get(aliases.next()).getClass().equals(command.getClass()))
            {
                aliases.remove();
            }
        }

        Iterator<Command> cmds = commandSet.values().iterator();
        while (cmds.hasNext())
        {
            if (cmds.next().getClass().equals(command.getClass()))
            {
                cmds.remove();
            }
        }
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
            Iterator<String> aliases = commands.keySet().iterator();
            while (command == null && aliases.hasNext())
            {
                String name = aliases.next();
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
    public Iterator<Command> getCommands()
    {
        return getCommands(AccessLevel.PLAYER);
    }

    /**
     * Return all commands available to clients with the specified
     * access level.
     */
    public Iterator<Command> getCommands(final int accessLevel)
    {
        List<Command> commands = new ArrayList<Command>();

        for (Command command : commandSet.values())
        {
            if (command.getAccessLevel() <= accessLevel)
            {
                commands.add(command);
            }
        }

        return commands.iterator();
    }

    /**
     * Execute the command specified in the message. This method checks if
     * the client has the access level required to use the command.
     */
    public void execute(CommandMessage m)
    {
        execute(m, getCommand(m.getCommand()));
    }

    /**
     * Execute an unregistered command.
     */
    public void execute(CommandMessage m, Command command)
    {
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

        StringBuilder colorized = new StringBuilder();
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
