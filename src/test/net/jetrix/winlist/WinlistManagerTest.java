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

package net.jetrix.winlist;

import junit.framework.TestCase;

/**
 * JUnit TestCase for the class net.jetrix.winlist.WinlistManager.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class WinlistManagerTest extends TestCase
{
    public void testGetInstance()
    {
        WinlistManager manager = WinlistManager.getInstance();
        assertNotNull("null manager", manager);
    }

    public void testGetWinlist()
    {
        Winlist winlist1 = new SimpleWinlist();
        winlist1.setId("winlist1");

        Winlist winlist2 = new SimpleWinlist();

        WinlistManager manager = WinlistManager.getInstance();
        manager.addWinlist(winlist1);
        manager.addWinlist(winlist2);

        assertEquals("null winlist", null, manager.getWinlist(null));
        assertEquals("missing winlist", null, manager.getWinlist("winlist2"));
        assertEquals("existing winlist", winlist1, manager.getWinlist("winlist1"));
    }
}
