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

package net.jetrix.protocols;

import net.jetrix.Message;
import net.jetrix.messages.channel.CommandMessage;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit TestCase for the class net.jetrix.protocols.QueryProtocolTest
 * 
 * @author Emmanuel Bourg
 */
public class QueryProtocolTest {

    private QueryProtocol protocol;

    @Before
    public void setUp()
    {
        protocol = new QueryProtocol();
    }

    @Test
    public void testIsQueryCommand()
    {
        assertTrue(QueryProtocol.isQueryCommand("listuser"));
        assertTrue(QueryProtocol.isQueryCommand("playerquery"));
        assertTrue(QueryProtocol.isQueryCommand("listchan"));
        assertTrue(QueryProtocol.isQueryCommand("version"));

        assertFalse(QueryProtocol.isQueryCommand("foo"));
    }

    @Test
    public void testGetCommandMessage()
    {
        String raw = "playerquery";
        Message message = protocol.getMessage(raw);

        assertNotNull("message not parsed", message);
        assertEquals("message class", CommandMessage.class, message.getClass());

        CommandMessage command = (CommandMessage) message;
        assertEquals("command", "playerquery", command.getCommand());     
    }
}
