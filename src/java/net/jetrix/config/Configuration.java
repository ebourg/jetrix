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

import java.util.Properties;

/**
 * A generic configuration.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class Configuration
{
    protected Properties props;

    public void setParameter(String name, String value)
    {
        if (props == null)
        {
            props = new Properties();
        }
        props.setProperty(name, value);
    }

    public int getInt(String key, int defaultValue)
    {
        int value;

        try
        {
            value = Integer.parseInt(props.getProperty(key));
        }
        catch (Exception e)
        {
            value = defaultValue;
        }

        return value;
    }

    public boolean getBoolean(String key, boolean defaultValue)
    {
        boolean value;

        try
        {
            String property = props.getProperty(key).trim().toLowerCase();
            if ("true".equals(property) || "false".equals(property))
            {
                value = new Boolean(property).booleanValue();
            }
            else
            {
                value = defaultValue;
            }

        }
        catch (Exception e)
        {
            value = defaultValue;
        }

        return value;
    }

    public String getString(String key, String defaultValue)
    {
        String value = props != null ? props.getProperty(key) : null;
        return value != null ? value : defaultValue;
    }
}
