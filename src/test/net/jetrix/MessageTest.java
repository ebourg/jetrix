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

package net.jetrix;

import java.util.Locale;

import junit.framework.TestCase;

import net.jetrix.messages.*;
import net.jetrix.protocols.*;

/**
 * JUnit TestCase for the class net.jetrix.Message.
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class MessageTest extends TestCase {

    public void testGetRawMessage()
    {
        PlineMessage message = new PlineMessage("hello!");

        String raw = message.getRawMessage(new TetrinetProtocol(), Locale.FRENCH);

        assertNotNull("raw message is null", raw);

        String raw2 = message.getRawMessage(new TetrinetProtocol(), Locale.FRENCH);

        assertTrue("raw message not cached", raw == raw2);
    }
}
