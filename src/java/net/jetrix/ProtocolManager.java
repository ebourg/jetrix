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

/**
 * Manages protocols. Protocol instances are obtained by calling the
 * getProtocol() method. The ProtocolManager is in charge to serve the
 * same unique instance of the specified protocol.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ProtocolManager
{
    private static ProtocolManager instance = new ProtocolManager();
    private Map<Class<? extends Protocol>, Protocol> protocols;

    private ProtocolManager()
    {
        protocols = new HashMap<Class<? extends Protocol>, Protocol>();
    }

    /**
     * Returns the instance of the ProtocolManager.
     */
    public static ProtocolManager getInstance()
    {
        return instance;
    }

    /**
     * Returns a protocol of the specified class. If a protocol of this class
     * has already been created, the same instance is returned.
     *
     * @param cls Class of the protocol to return
     *
     * @return Protocol of the specified class.
     */
    public synchronized <P extends Protocol> P  getProtocol(Class<P> cls)
    {
        // is there an entry for this class in the hashtable ?
        Object obj = protocols.get(cls);
        if (obj != null)
        {
            return (P) obj;
        }

        P protocol = null;

        try
        {
            // constructing a new protocol
            protocol = cls.newInstance();

            // adding the protocol to the hashtable
            protocols.put(cls, protocol);
        }
        catch (Exception e)
        {
            IllegalArgumentException iae = new IllegalArgumentException();
            iae.initCause(e);
            throw iae;
        }

        return protocol;
    }

}
