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

package org.lfjr.jts;

import junit.framework.*;

/**
 * JUnit TestCase for the class org.lfjr.jts.Message
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class MessageTest extends TestCase
{
    public MessageTest(String name)
    {
        super(name);
    }
    
    public static void testEquals()
    {
        Message m1 = new Message(Message.MSG_SHUTDOWN);
        Message m2 = new Message(Message.MSG_SHUTDOWN);
        
        assertEquals(true, m2.equals(m1));
        assertEquals(true, m1.equals(m2));
    }

    public static void testGetRawMessage1()
    {
        Message m1 = new Message(Message.MSG_PLINE, new Object[] { new Integer(1), "Hello JetriX !" });
        assertEquals("pline 1 Hello JetriX !", m1.getRawMessage());	
    }

    public static void testGetRawMessage2()
    {
        Message m1 = new Message(Message.MSG_PLAYERJOIN, new Object[] { new Integer(1), "Smanux" });
        assertEquals("playerjoin 1 Smanux", m1.getRawMessage());           	
    }

    public static void testGetRawMessage3()
    {
        Message m1 = new Message(Message.MSG_TEAM, new Object[] { new Integer(1), "LFJR" });
        assertEquals("team 1 LFJR", m1.getRawMessage());
        
        Message m2 = new Message(Message.MSG_TEAM, new Object[] { new Integer(1), null });
        assertEquals("team 1", m2.getRawMessage());                  	
    }

    public static void testGetRawMessage4()
    {
        Message m1 = new Message(Message.MSG_PLAYERLEAVE, new Object[] { new Integer(1) });
        assertEquals("playerleave 1", m1.getRawMessage());           	
    }

    public static void testGetRawMessage5()
    {
        Message m1 = new Message(Message.MSG_PLAYERNUM, new Object[] { new Integer(1) });
        assertEquals("playernum 1", m1.getRawMessage());           	
    }

    public static void testGetRawMessage6()
    {
        Message m1 = new Message(Message.MSG_ENDGAME);
        assertEquals("endgame", m1.getRawMessage());           	
    }

    public static void testGetRawMessage7()
    {
        Message m1 = new Message(Message.MSG_PLAYERLOST, new Object[] { new Integer(1) });
        assertEquals("playerlost 1", m1.getRawMessage());           	
    }

    public static void testGetRawMessage8()
    {
        Message m1 = new Message(Message.MSG_FIELD, new Object[] { new Integer(1), "XYZABCD" });
        assertEquals("f 1 XYZABCD", m1.getRawMessage());           	
    }

    public static void testGetRawMessage9()
    {
        Message m1 = new Message(Message.MSG_PLINEACT, new Object[] { new Integer(1), "feels faster" });
        assertEquals("plineact 1 feels faster", m1.getRawMessage());
    }

    public static Test suite()
    {
        return new TestSuite(MessageTest.class);
    }
}