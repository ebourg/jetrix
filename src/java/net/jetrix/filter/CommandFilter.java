/**
 * Jetrix TetriNET Server
 * Copyright (C) 2004  Emmanuel Bourg
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

package net.jetrix.filter;

import java.util.List;

import net.jetrix.messages.CommandMessage;
import net.jetrix.commands.Command;
import net.jetrix.commands.CommandManager;
import net.jetrix.config.FilterConfig;

/**
 * A filter executing a specific command. This filter is useful to add
 * a command to a specific channel. For example:
 *
 * <pre>
 * &lt;channel name="duel">
 *   &lt;filters>
 *     &lt;filter name="command">
 *       &lt;param name="class" value="net.jetrix.command.ModeCommand">
 *     &lt;/filter>
 *   &lt;/filters>
 * &lt;/channel>
 * </pre>
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class CommandFilter extends GenericFilter {

    private Command command;

    public void init(FilterConfig conf) {
        String cls = conf.getString("class", null);
        try {
            command = (Command) Class.forName(cls).newInstance();
        } catch (Exception e) {
            log.warning(e.getMessage());
        }
    }

    public void onMessage(CommandMessage m, List out) {

        for (String alias : command.getAliases())
        {
            if (m.getCommand().equals(alias))
            {
                CommandManager.getInstance().execute(m, command);
                return;
            }
        }

        out.add(m);
    }

}
