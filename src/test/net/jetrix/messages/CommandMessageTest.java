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

package net.jetrix.messages;

import junit.framework.TestCase;

/**
 * JUnit TestCase for the class net.jetrix.messages.CommandMessage
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class CommandMessageTest extends TestCase {

    public void testGetIntParameter()
    {
        CommandMessage message = new CommandMessage();
        message.addParameter("123");

        assertEquals("1st parameter", 123, message.getIntParameter(0, 456));
        assertEquals("2nd parameter", 456, message.getIntParameter(1, 456));
    }

    public void testGetIntegerParameter()
    {
        CommandMessage message = new CommandMessage();
        message.addParameter("123");

        assertEquals("1st parameter", new Integer(123), message.getIntegerParameter(0));
        assertEquals("2nd parameter", null, message.getIntegerParameter(1));
    }

    public void testGetParameterCount()
    {
        CommandMessage message = new CommandMessage();
        message.addParameter("123");
        message.addParameter("456");

        assertEquals("parameter count", 2, message.getParameterCount());
    }

}
