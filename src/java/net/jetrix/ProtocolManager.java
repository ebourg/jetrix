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

package net.jetrix;

import java.util.*;
import java.lang.reflect.*;

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
    private Map protocols;

    private ProtocolManager()
    {
        protocols = new HashMap();
    }

    /**
     * Returns the instance of the ProtocolManager.
     */
    public static ProtocolManager getInstance()
    {
        return instance;
    }

    /**
     * Returns a filter of the specified class. If the filter is declared as
     * a singleton, the instance will be stored and returned on further calls
     * for the same filter.
     *
     * @param classname Classname of the filter to return
     *
     * @return Filter of the specified class.
     */
    public synchronized Protocol getProtocol(String classname)
    {
        // is there an entry for this class in the hashtable ?
        Object obj = protocols.get(classname);
        if (obj != null) { return (Protocol)obj; }

        Protocol protocol = null;

        try {
            // constructing a new protocol
            Class protocolClass = Class.forName(classname);
            protocol = (Protocol)protocolClass.newInstance();

            // adding the protocol to the hashtable
            protocols.put(classname, protocol);
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
