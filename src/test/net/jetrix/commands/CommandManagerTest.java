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
import net.jetrix.messages.*;

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

        command1 = new Command() {
            public String[] getAliases() { return new String[] { "aaabbbccc" }; }
            public String getUsage(Locale locale) { return null; }
            public String getDescription(Locale locale) { return null; }
            public int getAccessLevel() { return 0; }
            public void execute(CommandMessage m) { }
        };

        command2 = new Command() {
            public String[] getAliases() { return new String[] { "aaabbbddd" }; }
            public String getUsage(Locale locale) { return null; }
            public String getDescription(Locale locale) { return null; }
            public int getAccessLevel() { return 0; }
            public void execute(CommandMessage m) { }
        };

        command3 = new Command() {
            public String[] getAliases() { return new String[] { "aaabbbeee" }; }
            public String getUsage(Locale locale) { return null; }
            public String getDescription(Locale locale) { return null; }
            public int getAccessLevel() { return 1; }
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

        Iterator commands = commandManager.getCommands(1);
        assertEquals(command1, commands.next());
        assertEquals(command2, commands.next());
        assertEquals(command3, commands.next());
    }

}
