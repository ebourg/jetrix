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

package org.lfjr.jts.filter;

import java.util.*;
import java.lang.reflect.*;
import org.lfjr.jts.*;

/**
 * Manages channel filters. Filter instances are obtained by calling the
 * getFilter() method. The FilterManager is in charge to serve a new
 * instance of the filter or the same unique instance depending on the
 * type of the filter (static or not).
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class FilterManager
{
    private static FilterManager instance = new FilterManager();
    private Hashtable staticFilters;

    private FilterManager()
    {
        staticFilters = new Hashtable();
    }

    /**
     * Returns an instance of this filter. If the fileter is static the same
     * instance will always be returned, if not a new instance is created.
     */
    public static FilterManager getInstance()
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
    public synchronized MessageFilter getFilter(String classname) throws FilterException
    {
        // is there an entry for this class in the hashtable ?
        Object obj = staticFilters.get(classname);
        if (obj!=null) { return (MessageFilter)obj; }

        MessageFilter filter = null;

        try {
            // checking if the filter is a singleton
            Class filterClass = Class.forName(classname);
            Method isSingletonMethod = filterClass.getMethod("isSingleton", null);
            Boolean isSingleton = (Boolean)isSingletonMethod.invoke(null, null);

            // constructing a new filter
            filter = (MessageFilter)filterClass.newInstance();

            // adding filter to the hashtable if it's a singleton
            if (isSingleton.booleanValue()) { staticFilters.put(classname, filter); }
        }
        catch (Exception e)
        {
            throw new FilterException(e);
        }

        return filter;
    }
}
