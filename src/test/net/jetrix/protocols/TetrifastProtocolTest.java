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

import java.util.Locale;

import junit.framework.TestCase;

import net.jetrix.*;
import net.jetrix.config.*;
import net.jetrix.messages.*;

/**
 * JUnit TestCase for the class net.jetrix.protocols.TetrifastProtocolTest
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TetrifastProtocolTest extends TestCase {

    private Protocol protocol;
    private Locale locale;

    public void setUp()
    {
        protocol = new TetrifastProtocol();
        locale = new Locale("fr");
    }

    public void testTranslatePlayerNum()
    {
        PlayerNumMessage message = new PlayerNumMessage();
        message.setSlot(1);
        assertEquals(")#)(!@(*3 1", protocol.translate(message, locale));
    }

    public void testTranslateNewGame()
    {
        NewGameMessage message = new NewGameMessage();
        message.setSettings(new Settings());
        assertTrue(protocol.translate(message, locale).startsWith("*******"));
    }
}
