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

package net.jetrix.protocols;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit TestCase for the class net.jetrix.protocols.IRCMessage.
 *
 * @author Emmanuel Bourg
 */
public class IRCMessageTest
{
    @Test
    public void testSetPrefix1()
    {
        IRCMessage message = new IRCMessage();
        message.setPrefix("nick!user@host");

        assertEquals("nick", "nick", message.getNick());
        assertEquals("user", "user", message.getUser());
        assertEquals("host", "host", message.getHost());
    }

    @Test
    public void testSetPrefix2()
    {
        IRCMessage message = new IRCMessage();
        message.setPrefix("nick!user");

        assertEquals("nick", "nick", message.getNick());
        assertEquals("user", "user", message.getUser());
        assertEquals("host", null, message.getHost());
    }

    @Test
    public void testSetPrefix3()
    {
        IRCMessage message = new IRCMessage();
        message.setPrefix("nick@host");

        assertEquals("nick", "nick", message.getNick());
        assertEquals("user", null, message.getUser());
        assertEquals("host", "host", message.getHost());
    }

    @Test
    public void testSetPrefix4()
    {
        IRCMessage message = new IRCMessage();
        message.setPrefix("nick");

        assertEquals("nick", "nick", message.getNick());
        assertEquals("user", null, message.getUser());
        assertEquals("host", null, message.getHost());
    }

    @Test
    public void testGetPrefix1()
    {
        IRCMessage message = new IRCMessage();
        message.setNick("nick");
        message.setUser("user");
        message.setHost("host");

        assertEquals("prefix", "nick!user@host", message.getPrefix());
    }

    @Test
    public void testGetPrefix2()
    {
        IRCMessage message = new IRCMessage();
        message.setNick("nick");
        message.setUser("user");

        assertEquals("prefix", "nick!user", message.getPrefix());
    }

    @Test
    public void testGetPrefix3()
    {
        IRCMessage message = new IRCMessage();
        message.setNick("nick");
        message.setHost("host");

        assertEquals("prefix", "nick@host", message.getPrefix());
    }

    @Test
    public void testGetPrefix4()
    {
        IRCMessage message = new IRCMessage();
        message.setNick("nick");

        assertEquals("prefix", "nick", message.getPrefix());
    }

    @Test
    public void testParse()
    {
        String line = "PRIVMSG #tetrinet1 :Hi there!";

        IRCMessage message = IRCMessage.parse(line);

        assertNotNull("null message", message);

        assertEquals("command", IRCCommand.PRIVMSG, message.getCommand());
        assertEquals("numeric", 0, message.getReply());

        assertEquals("parameter count", 2, message.getParameterCount());
        assertEquals("param 1", "#tetrinet1", message.getParameter(0));
        assertEquals("param 2", "Hi there!", message.getParameter(1));
    }

    @Test
    public void testParseWithPrefix()
    {
        String line = ":nick!user@host PRIVMSG #tetrinet1 :Hi there!";

        IRCMessage message = IRCMessage.parse(line);

        assertNotNull("null message", message);

        assertEquals("nick", "nick", message.getNick());
        assertEquals("user", "user", message.getUser());
        assertEquals("host", "host", message.getHost());

        assertEquals("command", IRCCommand.PRIVMSG, message.getCommand());
        assertEquals("numeric", 0, message.getReply());

        assertEquals("parameter count", 2, message.getParameterCount());
        assertEquals("param 1", "#tetrinet1", message.getParameter(0));
        assertEquals("param 2", "Hi there!", message.getParameter(1));
    }

    @Test
    public void testParseEmptyMessage()
    {
        String line = "";

        IRCMessage message = IRCMessage.parse(line);

        assertNotNull("message is null", message);
        assertNull("command", message.getCommand());
        assertEquals("reply", 0, message.getReply());
    }

    @Test
    public void testToString()
    {
        IRCMessage message = new IRCMessage(IRCCommand.PRIVMSG);

        message.addParameter("#tetrinet1");
        message.addParameter("Hi there!");

        assertEquals("message", "PRIVMSG #tetrinet1 :Hi there!", message.toString());
    }

    @Test
    public void testToStringWithPrefix()
    {
        IRCMessage message = new IRCMessage(IRCCommand.PRIVMSG);
        message.setNick("nick");
        message.setUser("user");
        message.setHost("host");

        message.addParameter("#tetrinet1");
        message.addParameter("Hi there!");

        assertEquals("message", ":nick!user@host PRIVMSG #tetrinet1 :Hi there!", message.toString());
    }

    @Test
    public void testToStringWithNumericReply()
    {
        IRCMessage message = new IRCMessage(IRCReply.RPL_MOTD);
        message.setPrefix("localhost");
        message.addParameter("Smanux");
        message.addParameter("Message of the day");

        assertEquals("message", ":localhost 372 Smanux :Message of the day", message.toString());
    }

    @Test
    public void testToStringWithoutParameter()
    {
        IRCMessage message = new IRCMessage(IRCCommand.LIST);
        message.setPrefix("localhost");

        assertEquals("message", ":localhost LIST", message.toString());
    }

    @Test
    public void testToStringWithSemicolon()
    {
        IRCMessage message = new IRCMessage(IRCCommand.PRIVMSG);
        message.addParameter("#tetrinet");
        message.addParameter(":)");

        assertEquals("message", "PRIVMSG #tetrinet ::)", message.toString());
    }
}
