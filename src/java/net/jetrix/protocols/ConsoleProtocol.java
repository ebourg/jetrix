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

package net.jetrix.protocols;

import java.util.*;
import net.jetrix.*;
import net.jetrix.config.*;
import net.jetrix.messages.*;

/**
 * Protocol to communicate with a console client
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ConsoleProtocol extends Protocol
{

    /**
     * Return the name of this protocol
     */
    public String getName()
    {
        return "console";
    }

    /**
     * Parse the specified string and return the corresponding server
     * message for this protocol.
     */
    public Message getMessage(String line)
    {
        if (line == null) return null;

        StringTokenizer st = new StringTokenizer(line);
        Message m = null;

        if (st.hasMoreTokens())
        {
            String firstWord = st.nextToken();

            CommandMessage command = new CommandMessage();
            command.setCommand(firstWord.substring(1));
            command.setText(line.substring(line.indexOf(" ") + 1));
            while (st.hasMoreTokens()) { command.addParameter(st.nextToken()); }
            m = command;
        }

        return m;
    }

    /**
     * Translate the specified message into a string that will be sent
     * to a client using this protocol.
     */
    public String translate(Message m, Locale locale)
    {
        if ( m instanceof TextMessage) return translate((TextMessage)m, locale);
        else
        {
            return null;
        }
    }

    public String translate(TextMessage m, Locale locale)
    {
        StringBuffer message = new StringBuffer();
        message.append(applyStyle(m.getText(locale)));
        return message.toString();
    }

    public Map getStyles()
    {
        return null;
    }

    public String toString()
    {
        return "[Protocol name=" + getName() + "]";
    }

}
