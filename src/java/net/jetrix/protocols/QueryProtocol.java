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

package net.jetrix.protocols;

import java.util.*;

import net.jetrix.*;
import net.jetrix.messages.*;

/**
 * Query Protocol. See http://jetrix.sourceforge.net/dev-guide.php#section2-4
 * for more information.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class QueryProtocol implements Protocol
{
    /** Line terminator */
    public static final char EOL = 0x0A;

    /** Response terminator */
    public static final String OK = "+OK";

    public String getName()
    {
        return "query";
    }

    public Message getMessage(String message)
    {
        CommandMessage m = null;
        if (isQueryCommand(message))
        {
            m = new CommandMessage();
            m.setCommand(message);
        }
        return m;
    }

    public String translate(Message m, Locale locale)
    {
        if (m instanceof PlineMessage) { return translate((PlineMessage) m, locale); }
        else if (m instanceof NoConnectingMessage) { return translate((NoConnectingMessage) m, locale); }
        else
        {
            return null;
        }
    }

    public String translate(PlineMessage m, Locale locale)
    {
        StringBuffer message = new StringBuffer();
        message.append(m.getText());
        message.append(EOL);

        // add the response terminator except for playerquery
        //if (!m.getText().startsWith("Number"))
        {
            message.append(OK);
            message.append(EOL);
        }

        return message.toString();
    }

    public String translate(NoConnectingMessage m, Locale locale)
    {
        StringBuffer message = new StringBuffer();
        message.append("noconnecting ");
        message.append(m.getText());
        return message.toString();
    }

    public String applyStyle(String text)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Test if the specified string is a valid query request.
     */
    public static boolean isQueryCommand(String command)
    {
        return ("listuser".equals(command)
                || "playerquery".equals(command)
                || "listchan".equals(command)
                || "version".equals(command));
    }

}
