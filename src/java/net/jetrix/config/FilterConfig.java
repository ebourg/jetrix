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

package net.jetrix.config;

import java.util.*;

/**
 * Filter configuration.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class FilterConfig
{
    private String name;
    private String classname;
    private Properties props;

    public FilterConfig()
    {
        props = new Properties();
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setClassname(String classname)
    {
        this.classname = classname;
    }

    public String getClassname()
    {
        return classname;
    }

    public void setParameter(String name, String value)
    {
        props.setProperty(name, value);
    }

    public String getParameter(String name)
    {
        return props.getProperty(name);
    }

    public String toString()
    {
        return "[FilterConfig name=" + name + " class=" + classname + " params=" + props + "]";
    }

}
