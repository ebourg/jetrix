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

import junit.framework.*;
import java.util.*;
import net.jetrix.*;
import net.jetrix.messages.channel.CommandMessage;

/**
 * JUnit TestCase for the class net.jetrix.commands.CommandManager
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class CommandManagerTest extends TestCase
{
    private CommandManager commandManager;
    private Command command1;
    private Command command2;
    private Command command3;

    public void setUp()
    {
        commandManager = CommandManager.getInstance();

        command1 = new AbstractCommand() {
            public String[] getAliases() { return new String[] { "aaabbbccc" }; }
            public int getAccessLevel() { return AccessLevel.PLAYER; }
            public void execute(CommandMessage m) { }
        };

        command2 = new AbstractCommand() {
            public String[] getAliases() { return new String[] { "aaabbbddd" }; }
            public int getAccessLevel() { return AccessLevel.PLAYER; }
            public void execute(CommandMessage m) { }
        };

        command3 = new AbstractCommand() {
            public String[] getAliases() { return new String[] { "aaabbbeee" }; }
            public int getAccessLevel() { return AccessLevel.OPERATOR; }
            public void execute(CommandMessage m) { }
        };

    }

    public void tearDown()
    {
        commandManager.clear();
    }

    public void testGetCommand1()
    {
        commandManager.addCommand(command1);
        commandManager.addCommand(command2);
        commandManager.addCommand(command3);

        // testing full command name match
        assertEquals(command1, commandManager.getCommand("aaabbbccc"));
        assertEquals(command2, commandManager.getCommand("aaabbbddd"));
        assertEquals(command3, commandManager.getCommand("aaabbbeee"));
        assertEquals(null, commandManager.getCommand("xyz"));
    }

    public void testGetCommand2()
    {
        commandManager.addCommand(command1);
        commandManager.addCommand(command2);
        commandManager.addCommand(command3);

        // testing unambiguous partial command name match
        assertEquals(command1, commandManager.getCommand("aaabbbc"));
        assertEquals(command2, commandManager.getCommand("aaabbbd"));
        assertEquals(command3, commandManager.getCommand("aaabbbe"));
    }

    public void testGetCommand3()
    {
        commandManager.addCommand(command1);
        commandManager.addCommand(command2);
        commandManager.addCommand(command3);

        // testing ambiguous partial command name match
        assertEquals(command1, commandManager.getCommand("aaa"));
        assertEquals(command1, commandManager.getCommand("aaabbb"));
    }

    public void testGetCommand4()
    {
        commandManager.addCommand(command3);
        commandManager.addCommand(command2);
        commandManager.addCommand(command1);

        // testing ambiguous partial command name match
        assertEquals(command1, commandManager.getCommand("aaa"));
        assertEquals(command1, commandManager.getCommand("aaabbb"));
    }

    public void testGetCommand5()
    {
        commandManager.addCommand(command1);
        commandManager.addCommand(command2);
        commandManager.addCommand(command3);
        commandManager.addCommandAlias(command2, "aaa");
        commandManager.addCommandAlias(command3, "aaabbb");

        assertEquals(command2, commandManager.getCommand("aaa"));
        assertEquals(command3, commandManager.getCommand("aaabbb"));
    }

    public void testGetCommand6()
    {
        commandManager.addCommand(command1);
        commandManager.addCommand(command2);
        commandManager.addCommand(command3);

        // is getCommand case insensitive ?
        assertEquals("Error getCommand is case sensitive", command1, commandManager.getCommand("aAaBbBcCc"));
        assertEquals("Error getCommand is case sensitive", command2, commandManager.getCommand("aaaBBBddd"));
        assertEquals("Error getCommand is case sensitive", command3, commandManager.getCommand("AAAbbbEEE"));
        assertEquals("Error getCommand is case sensitive", command1, commandManager.getCommand("AAA"));
    }

    public void testAddCommandAlias()
    {
        commandManager.addCommand(command1);
        commandManager.addCommandAlias(command1, "xyz");

        assertEquals(command1, commandManager.getCommand("xyz"));
    }

    public void testGetCommands1()
    {
        commandManager.addCommand(command3);
        commandManager.addCommand(command2);
        commandManager.addCommand(command1);

        Iterator commands = commandManager.getCommands();
        assertEquals(command1, commands.next());
        assertEquals(command2, commands.next());
    }

    public void testGetCommands2()
    {
        commandManager.addCommand(command3);
        commandManager.addCommand(command2);
        commandManager.addCommand(command1);

        Iterator commands = commandManager.getCommands(AccessLevel.OPERATOR);
        assertEquals(command1, commands.next());
        assertEquals(command2, commands.next());
        assertEquals(command3, commands.next());
    }

    public void testColorizeUsage()
    {
        assertEquals("<red>/usage", commandManager.colorizeUsage("/usage"));
        assertEquals("<red>/usage <blue>param1 param2", commandManager.colorizeUsage("/usage param1 param2"));
    }

    public void testRemoveCommand()
    {
        commandManager.addCommand(new HelpCommand());

        assertNotNull("help command not found", commandManager.getCommand("help"));

        commandManager.removeCommand(new HelpCommand());

        assertNull("help command not removed", commandManager.getCommand("help"));
    }

}
