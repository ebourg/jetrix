/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2004  Emmanuel Bourg
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

import net.jetrix.messages.*;

/**
 * A command consummes a CommandMessage to execute a specific operation.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public interface Command
{
    /**
     * Return the different names available to invoke this command.
     * An alias doesn't contain the leading character used to call
     * a command ("/" or "!"). The first alias in the array is the 
     * default name that will be displayed in the /help list.
     */
    String[] getAliases();

    /**
     * Return the usage of this command, for example
     * <tt>/cmd &lt;param1&gt; &lt;param2&gt;</tt>
     *
     * @param locale the locale to be used for the usage
     */
    String getUsage(Locale locale);

    /**
     * Return a description of this command.
     *
     * @param locale the locale to be used for the description
     */
    String getDescription(Locale locale);

    /**
     * Return the required access level to execute this command.
     */
    int getAccessLevel();

    /**
     * Set the access level required to execute this command.
     *
     * @since 0.2
     *
     * @param level the access level
     */
    void setAccessLevel(int level);

    /**
     * Tells if the command is hidden from the /help list.
     *
     * @since 0.2
     */
    boolean isHidden();

    /**
     * Change the hidden status of the command on the /help list.
     *
     * @since 0.2
     *
     * @param hidden
     */
    void setHidden(boolean hidden);

    /**
     * Execute the command.
     */
    void execute(CommandMessage message);

}
