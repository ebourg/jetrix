/**
 * Jetrix TetriNET Server
 * Copyright (C) 20010  Emmanuel Bourg
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

package net.jetrix.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeMap;

import net.jetrix.AccessLevel;
import net.jetrix.commands.Command;
import net.jetrix.commands.CommandManager;
import net.jetrix.config.ChannelConfig;
import net.jetrix.config.FilterConfig;
import net.jetrix.config.ServerConfig;

/**
 * Generates the documentation for the server commands.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class DocumentationGenerator
{
    public static void main(String[] args) throws Exception
    {
        File configFile = new File("src/etc/conf/server.xml");
        ServerConfig config = new ServerConfig();
        config.load(configFile);

        File file = new File("src/site/commands.html");
        file.getParentFile().mkdirs();

        System.out.println("Exporting commands documentation from " + configFile + " to " + file);
        System.out.println("");

        // collect the general commands
        TreeMap<String, Command> commands = new TreeMap<String, Command>();
        Iterator<Command> it = CommandManager.getInstance().getCommands(AccessLevel.ADMINISTRATOR);
        while (it.hasNext())
        {
            Command command = it.next();
            commands.put(command.getAliases()[0], command);
        }

        // collect the filter commands
        for (ChannelConfig channel : config.getChannels())
        {
            Iterator<FilterConfig> filters = channel.getFilters();
            while (filters.hasNext())
            {
                FilterConfig filter = filters.next();
                if (!filter.isGlobal() && filter.getName().equals("command"))
                {
                    String cls = filter.getProperties().getProperty("class");
                    Command command = (Command) Class.forName(cls).newInstance();
                    commands.put(command.getAliases()[0], command);
                }
            }
        }

        PrintWriter out = new PrintWriter(new FileWriter(file));

        for (Command command : commands.values())
        {
            String alias = command.getAliases()[0];

            out.println("<h2 id=\"command-" + alias + "\">" + alias + "</h2>");
            out.println();
            out.println("<p>" + command.getDescription(Locale.ENGLISH) + "</p>");
            out.println();
            out.println("<div><b>Usage:</b> <tt>" + htmlizeUsage(command.getUsage(Locale.ENGLISH)) + "</tt></div>");
            if (command.getAliases().length > 1)
            {
                out.println("<div><b>Aliases:</b> <tt>" + Arrays.toString(command.getAliases()) + "</tt></div>");
            }
            String role = getRoleName(command.getAccessLevel());
            if (role != null)
            {
                out.println("<div><b>Access Level:</b> " + role + "</div>");
            }
            out.println();
            out.println();

            System.out.println(command.getUsage(Locale.ENGLISH));
        }


        out.flush();
        out.close();
    }

    private static String getRoleName(int level)
    {
        if (level == 0)
        {
            return "Player";
        }
        else if (level == 1)
        {
            return "Channel Operator";
        }
        else if (level == 2)
        {
            return "Operator";
        }
        else if (level == 100)
        {
            return "Administrator";
        }
        else
        {
            return null;
        }
    }

    /**
     * Return a colorized usage string of the specified command.
     */
    private static String htmlizeUsage(String usage)
    {
        StringBuffer htmlized = new StringBuffer();
        htmlized.append("<span style=\"color: red\">");

        for (char c : usage.toCharArray())
        {
            if (c == '<')
            {
                htmlized.append("<span style=\"color: blue\">&lt;");
            }
            else if (c == '>')
            {
                htmlized.append("></span>");
            }
            else
            {
                htmlized.append(c);
            }
        }

        htmlized.append("</span>");

        return htmlized.toString();
    }
}
