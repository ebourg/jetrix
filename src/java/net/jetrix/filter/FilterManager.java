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

package net.jetrix.filter;

import java.util.*;
import java.lang.reflect.*;
import net.jetrix.*;

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
    private Map staticFilters;
    private Map filterAliases;

    private FilterManager()
    {
        staticFilters = new HashMap();
        filterAliases = new HashMap();
    }

    /**
     * Returns the instance of the FilterManager.
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
        if (obj != null) { return (MessageFilter)obj; }

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

    /**
     * Returns a filter matching the specified name.
     *
     * @param name name of the filter to return
     *
     * @return Filter of the specified class.
     */
    public MessageFilter getFilterByName(String name) throws FilterException
    {
        Object classname = filterAliases.get(name);

        if (classname != null)
        {
            return getFilter((String)classname);
        }
        else
        {
            throw new FilterException("Cannot find filter " + name);
        }
    }

    /**
     * Defines a new alias for a filter.
     *
     * @param name alias of the filter
     * @param class of the filter
     */
    public void addFilterAlias(String name, String classname)
    {
        filterAliases.put(name, classname);
    }

}
