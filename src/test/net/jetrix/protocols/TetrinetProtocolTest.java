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
import junit.framework.*;
import net.jetrix.*;
import net.jetrix.messages.*;

/**
 * JUnit TestCase for the class net.jetrix.protocols.TetrinetProtocolTest
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TetrinetProtocolTest extends TestCase
{
    private Protocol protocol;
    private Locale locale;

    public void setUp()
    {
        protocol = new TetrinetProtocol();
        locale = new Locale("fr");
    }

    public void testTranslatePline()
    {
        PlineMessage message = new PlineMessage();
        message.setSlot(1);
        message.setText("Hello JetriX!");
        assertEquals("pline 1 Hello JetriX!", protocol.translate(message, locale));
    }

    public void testGetMessagePline()
    {
        String raw = "pline 1 hello world!";
        Message message = protocol.getMessage(raw);

        assertNotNull("message not parsed", message);
        assertEquals("message class", PlineMessage.class, message.getClass());

        PlineMessage pline = (PlineMessage) message;
        assertEquals("slot", 1, pline.getSlot());
        assertEquals("text", "hello world!", pline.getText(Locale.ENGLISH));
    }

    public void testGetMessageCommand()
    {
        String raw = "pline 1 /move 1 2";
        Message message = protocol.getMessage(raw);

        assertNotNull("message not parsed", message);
        assertEquals("message class", CommandMessage.class, message.getClass());

        CommandMessage command = (CommandMessage) message;
        assertEquals("slot", 1, command.getSlot());
        assertEquals("command", "move", command.getCommand());
        assertEquals("text", "1 2", command.getText(Locale.ENGLISH));
        assertEquals("parameter count", 2, command.getParameterCount());
        assertEquals("1st parameter", "1", command.getParameter(0));
        assertEquals("2nd parameter", "2", command.getParameter(1));
    }

    public void testTranslatePlineAct()
    {
        PlineActMessage message = new PlineActMessage();
        message.setSlot(1);
        message.setText("feels faster");
        assertEquals("plineact 1 feels faster", protocol.translate(message, locale));
    }

    public void testGetMessagePlineAct()
    {
        String raw = "plineact 1 says hello world!";
        Message message = protocol.getMessage(raw);

        assertNotNull("message not parsed", message);
        assertEquals("message class", PlineActMessage.class, message.getClass());

        PlineActMessage plineact = (PlineActMessage) message;
        assertEquals("slot", 1, plineact.getSlot());
        assertEquals("text", "says hello world!", plineact.getText(Locale.ENGLISH));
    }

    public void testTranslateGmsg()
    {
        GmsgMessage message = new GmsgMessage();
        message.setText("<Smanux> hello world!");
        assertEquals("gmsg <Smanux> hello world!", protocol.translate(message, locale));
    }

    public void testGetMessageGmsg()
    {
        String raw = "gmsg hello world!";
        Message message = protocol.getMessage(raw);

        assertNotNull("message not parsed", message);
        assertEquals("message class", GmsgMessage.class, message.getClass());

        GmsgMessage gmsg = (GmsgMessage) message;
        assertEquals("slot", 1, gmsg.getSlot());
        assertEquals("text", "hello world!", gmsg.getText(Locale.ENGLISH));
    }

    public void testTranslatePlayerJoin()
    {
        JoinMessage message = new JoinMessage();
        message.setSlot(1);
        message.setName("Smanux");
        assertEquals("playerjoin 1 Smanux", protocol.translate(message, locale));
    }

    public void testGetMessagePlayerJoin()
    {
        String raw = "playerjoin 1 Smanux";
        Message message = protocol.getMessage(raw);

        assertNotNull("message not parsed", message);
        assertEquals("message class", JoinMessage.class, message.getClass());

        JoinMessage playerjoin = (JoinMessage) message;
        assertEquals("slot", 1, playerjoin.getSlot());
        assertEquals("name", "Smanux", playerjoin.getName());
    }

    public void testTranslateTeam1()
    {
        TeamMessage msg1 = new TeamMessage();
        msg1.setSlot(1);
        msg1.setName("LFJR");
        assertEquals("team 1 LFJR", protocol.translate(msg1, locale));
    }

    public void testTranslateTeam2()
    {
        TeamMessage msg2 = new TeamMessage();
        msg2.setSlot(1);
        assertEquals("team 1", protocol.translate(msg2, locale));
    }

    public void testGetMessageTeam1()
    {
        String raw = "team 1 LFJR";
        Message message = protocol.getMessage(raw);

        assertNotNull("message not parsed", message);
        assertEquals("message class", TeamMessage.class, message.getClass());

        TeamMessage team = (TeamMessage) message;
        assertEquals("slot", 1, team.getSlot());
        assertEquals("name", "LFJR", team.getName());
    }

    public void testGetMessageTeam2()
    {
        String raw = "team 1";
        Message message = protocol.getMessage(raw);

        assertNotNull("message not parsed", message);
        assertEquals("message class", TeamMessage.class, message.getClass());

        TeamMessage team = (TeamMessage) message;
        assertEquals("slot", 1, team.getSlot());
        assertEquals("name", null, team.getName());
    }

    public void testTranslatePlayerLeave()
    {
        LeaveMessage message = new LeaveMessage();
        message.setSlot(1);
        assertEquals("playerleave 1", protocol.translate(message, locale));
    }

    public void testGetMessagePlayerLeave()
    {
        String raw = "playerleave 1";
        Message message = protocol.getMessage(raw);

        assertNotNull("message not parsed", message);
        assertEquals("message class", LeaveMessage.class, message.getClass());

        LeaveMessage leave = (LeaveMessage) message;
        assertEquals("slot", 1, leave.getSlot());
    }

    public void testTranslatePlayerNum()
    {
        PlayerNumMessage message = new PlayerNumMessage();
        message.setSlot(1);
        assertEquals("playernum 1", protocol.translate(message, locale));
    }

    public void testTranslateEndGame()
    {
        EndGameMessage message = new EndGameMessage();
        assertEquals("endgame", protocol.translate(message, locale));
    }

    public void testTranslatePlayerLost()
    {
        PlayerLostMessage message = new PlayerLostMessage();
        message.setSlot(1);
        assertEquals("playerlost 1", protocol.translate(message, locale));
    }

    public void testTranslateField()
    {
        FieldMessage message = new FieldMessage();
        message.setSlot(1);
        message.setField("XYZABCD");
        assertEquals("f 1 XYZABCD", protocol.translate(message, locale));
    }

    public void testGetMessageField()
    {
        String raw = "f 1 XYZABCD";
        Message message = protocol.getMessage(raw);

        assertNotNull("message not parsed", message);
        assertEquals("message class", FieldMessage.class, message.getClass());

        FieldMessage field = (FieldMessage) message;
        assertEquals("slot", 1, field.getSlot());
        assertEquals("field", "XYZABCD", field.getField());
    }

}
