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

package net.jetrix;

import java.util.*;

import junit.framework.*;

/**
 * JUnit TestCase for the class net.jetrix.Banlist
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class BanlistTest extends TestCase
{
    private Banlist banlist;

    protected void setUp() throws Exception
    {
        banlist = Banlist.getInstance();
    }

    protected void tearDown() throws Exception
    {
        banlist.clear();
    }

    public void testBanNone()
    {
        assertEquals("isBanned(\"localhost\")", false, banlist.isBanned("localhost"));
        assertEquals("isBanned(\"www.google.com\")", false, banlist.isBanned("www.google.com"));
        assertEquals("isBanned(\"127.0.0.1\")", false, banlist.isBanned("127.0.0.1"));
    }

    public void testBanAll()
    {
        banlist.ban("*");
        assertEquals("isBanned(\"localhost\")", true, banlist.isBanned("localhost"));
        assertEquals("isBanned(\"www.google.com\")", true, banlist.isBanned("www.google.com"));
        assertEquals("isBanned(\"127.0.0.1\")", true, banlist.isBanned("127.0.0.1"));
    }

    public void testBanUnban()
    {
        banlist.ban("*");
        banlist.unban("*");
        assertEquals("isBanned(\"localhost\")", false, banlist.isBanned("localhost"));
    }

    public void testBanPartial1()
    {
        banlist.ban("local*");
        assertEquals("isBanned(\"localhost\")", true, banlist.isBanned("localhost"));
    }

    public void testBanPartial2()
    {
        banlist.ban("*host");
        assertEquals("isBanned(\"localhost\")", true, banlist.isBanned("localhost"));
    }

    public void testBanPartial3()
    {
        banlist.ban("*calho*");
        assertEquals("isBanned(\"localhost\")", true, banlist.isBanned("localhost"));
    }

    public void testBanPartial4()
    {
        banlist.ban("192.168.1?.*");
        assertEquals("isBanned(\"192.168.19.255\")", true, banlist.isBanned("192.168.19.255"));
        assertEquals("isBanned(\"192.168.20.255\")", false, banlist.isBanned("192.168.20.255"));
        assertEquals("isBanned(\"192.168.110.25\")", false, banlist.isBanned("192.168.110.25"));
    }

    public void testBanPartial5()
    {
        banlist.ban("*.168.1?.*");
        assertEquals("isBanned(\"192.168.19.255\")", true, banlist.isBanned("192.168.19.255"));
        assertEquals("isBanned(\"192.168.20.255\")", false, banlist.isBanned("192.168.20.255"));
    }

    public void testExpirationDate() throws Exception
    {
        banlist.ban("*", new Date(System.currentTimeMillis() + 100));
        assertTrue("not banned before expiration date", banlist.isBanned("localhost"));
        Thread.sleep(200);
        assertFalse("still banned after expiration date", banlist.isBanned("localhost"));
    }

}
