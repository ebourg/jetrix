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
    private Command cmd1, cmd2, cmd3;

    public void setUp()
    {
        commandManager = CommandManager.getInstance();

        cmd1 = new Command() {
            public String[] getAliases() { return new String[] { "aaabbbccc" }; }
            public String getUsage(Locale locale) { return null; }
            public String getDescription(Locale locale) { return null; }
            public int getAccessLevel() { return 0; }
            public void execute(CommandMessage m) { }
        };

        cmd2 = new Command() {
            public String[] getAliases() { return new String[] { "aaabbbddd" }; }
            public String getUsage(Locale locale) { return null; }
            public String getDescription(Locale locale) { return null; }
            public int getAccessLevel() { return 0; }
            public void execute(CommandMessage m) { }
        };

        cmd3 = new Command() {
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
        commandManager.addCommand(cmd1);
        commandManager.addCommand(cmd2);
        commandManager.addCommand(cmd3);

        // testing full command name match
        assertEquals(cmd1, commandManager.getCommand("aaabbbccc"));
        assertEquals(cmd2, commandManager.getCommand("aaabbbddd"));
        assertEquals(cmd3, commandManager.getCommand("aaabbbeee"));
        assertEquals(null, commandManager.getCommand("xyz"));
    }

    public void testGetCommand2()
    {
        commandManager.addCommand(cmd1);
        commandManager.addCommand(cmd2);
        commandManager.addCommand(cmd3);

        // testing unambiguous partial command name match
        assertEquals(cmd1, commandManager.getCommand("aaabbbc"));
        assertEquals(cmd2, commandManager.getCommand("aaabbbd"));
        assertEquals(cmd3, commandManager.getCommand("aaabbbe"));
    }

    public void testGetCommand3()
    {
        commandManager.addCommand(cmd1);
        commandManager.addCommand(cmd2);
        commandManager.addCommand(cmd3);

        // testing ambiguous partial command name match
        assertEquals(cmd1, commandManager.getCommand("aaa"));
        assertEquals(cmd1, commandManager.getCommand("aaabbb"));
    }

    public void testGetCommand4()
    {
        commandManager.addCommand(cmd3);
        commandManager.addCommand(cmd2);
        commandManager.addCommand(cmd1);

        // testing ambiguous partial command name match
        assertEquals(cmd1, commandManager.getCommand("aaa"));
        assertEquals(cmd1, commandManager.getCommand("aaabbb"));
    }

    public void testGetCommand5()
    {
        commandManager.addCommand(cmd1);
        commandManager.addCommand(cmd2);
        commandManager.addCommand(cmd3);
        commandManager.addCommandAlias(cmd2, "aaa");
        commandManager.addCommandAlias(cmd3, "aaabbb");

        assertEquals(cmd2, commandManager.getCommand("aaa"));
        assertEquals(cmd3, commandManager.getCommand("aaabbb"));
    }

    public void testGetCommand6()
    {
        commandManager.addCommand(cmd1);
        commandManager.addCommand(cmd2);
        commandManager.addCommand(cmd3);

        // is getCommand case insensitive ?
        assertEquals("Error getCommand is case sensitive", cmd1, commandManager.getCommand("aAaBbBcCc"));
        assertEquals("Error getCommand is case sensitive", cmd2, commandManager.getCommand("aaaBBBddd"));
        assertEquals("Error getCommand is case sensitive", cmd3, commandManager.getCommand("AAAbbbEEE"));
        assertEquals("Error getCommand is case sensitive", cmd1, commandManager.getCommand("AAA"));
    }

    public void testAddCommandAlias()
    {
        commandManager.addCommand(cmd1);
        commandManager.addCommandAlias(cmd1, "xyz");

        assertEquals(cmd1, commandManager.getCommand("xyz"));
    }

    public void testGetCommands1()
    {
        commandManager.addCommand(cmd3);
        commandManager.addCommand(cmd2);
        commandManager.addCommand(cmd1);

        Iterator commands = commandManager.getCommands();
        assertEquals(cmd1, commands.next());
        assertEquals(cmd2, commands.next());
    }

    public void testGetCommands2()
    {
        commandManager.addCommand(cmd3);
        commandManager.addCommand(cmd2);
        commandManager.addCommand(cmd1);

        Iterator commands = commandManager.getCommands(1);
        assertEquals(cmd1, commands.next());
        assertEquals(cmd2, commands.next());
        assertEquals(cmd3, commands.next());
    }

}
