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

/**
 * This exception is thrown by <tt>FilterManager.getFilter(String)</tt> to
 * indicate that the creation of a filter threw a checked exception.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class FilterException extends Exception
{
    public FilterException()
    {
        super();
    }

    public FilterException(String s)
    {
        super(s);
    }

    public FilterException(Throwable t)
    {
        super(t);
    }
}
