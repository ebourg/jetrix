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
        PlineMessage msg = new PlineMessage();
        msg.setSlot(1);
        msg.setText("Hello JetriX!");
        assertEquals("pline 1 Hello JetriX!", protocol.translate(msg, locale));
    }

    public void testTranslatePlayerJoin()
    {
        JoinMessage msg = new JoinMessage();
        msg.setSlot(1);
        msg.setName("Smanux");
        assertEquals("playerjoin 1 Smanux", protocol.translate(msg, locale));
    }

    public void testTranslateTeam()
    {
        TeamMessage msg1 = new TeamMessage();
        msg1.setSlot(1);
        msg1.setName("LFJR");
        assertEquals("team 1 LFJR", protocol.translate(msg1, locale));

        TeamMessage msg2 = new TeamMessage();
        msg2.setSlot(1);
        assertEquals("team 1", protocol.translate(msg2, locale));
    }

    public void testTranslatePlayerLeave()
    {
        LeaveMessage msg = new LeaveMessage();
        msg.setSlot(1);
        assertEquals("playerleave 1", protocol.translate(msg, locale));
    }

    public void testTranslatePlayerNum()
    {
        PlayerNumMessage msg = new PlayerNumMessage();
        msg.setSlot(1);
        assertEquals("playernum 1", protocol.translate(msg, locale));
    }

    public void testTranslateEndGame()
    {
        EndGameMessage msg = new EndGameMessage();
        assertEquals("endgame", protocol.translate(msg, locale));
    }

    public void testTranslatePlayerLost()
    {
        PlayerLostMessage msg = new PlayerLostMessage();
        msg.setSlot(1);
        assertEquals("playerlost 1", protocol.translate(msg, locale));
    }

    public void testTranslateField()
    {
        FieldMessage msg = new FieldMessage();
        msg.setSlot(1);
        msg.setField("XYZABCD");
        assertEquals("f 1 XYZABCD", protocol.translate(msg, locale));
    }

    public void testTranslatePlineAct()
    {
        PlineActMessage msg = new PlineActMessage();
        msg.setSlot(1);
        msg.setText("feels faster");
        assertEquals("plineact 1 feels faster", protocol.translate(msg, locale));
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

    public void testGetMessageGmsg()
    {
        String raw = "gmsg says hello world!";
        Message message = protocol.getMessage(raw);

        assertNotNull("message not parsed", message);
        assertEquals("message class", GmsgMessage.class, message.getClass());

        GmsgMessage gmsg = (GmsgMessage) message;
        assertEquals("slot", 1, gmsg.getSlot());
        assertEquals("text", "hello world!", gmsg.getText(Locale.ENGLISH));
    }

}
