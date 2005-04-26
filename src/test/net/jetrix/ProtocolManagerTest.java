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

package net.jetrix;

import junit.framework.*;

import net.jetrix.protocols.*;

/**
 * JUnit TestCase for the class net.jetrix.ProtocolManager.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ProtocolManagerTest extends TestCase
{
    public void testGetProtocol()
    {
        ProtocolManager manager = ProtocolManager.getInstance();
        Protocol protocol = manager.getProtocol(TetrinetProtocol.class);

        assertNotNull("no protocol returned", protocol);
        assertEquals("protocol returned", TetrinetProtocol.class, protocol.getClass());
    }

    public void testGetCachedProtocol()
    {
        ProtocolManager manager = ProtocolManager.getInstance();
        Protocol protocol1 = manager.getProtocol(TetrinetProtocol.class);
        Protocol protocol2 = manager.getProtocol(TetrinetProtocol.class);

        assertEquals("the protocol is not cached", protocol1, protocol2);
    }

    public void testGetUnknownProtocol()
    {
        ProtocolManager manager = ProtocolManager.getInstance();
        Exception exception = null;
        try
        {
            manager.getProtocol(Protocol.class);
        }
        catch (Exception e)
        {
            exception = e;
        }

        assertNotNull("no exception raised", exception);
        assertEquals("exception type", IllegalArgumentException.class, exception.getClass());
    }
}
