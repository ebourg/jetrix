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

import junit.framework.*;

/**
 * JUnit TestCase for the class net.jetrix.config.Configuration
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ConfigurationTest extends TestCase
{
    private Configuration configuration;

    protected void setUp()
    {
        configuration = new Configuration();
    }

    public void testGetString() {
        assertEquals("unset string", "default", configuration.getString("unset", "default"));
        configuration.setParameter("string", "value");
        assertEquals("string", "value", configuration.getString("string", "default"));
    }

    public void testGetInt() {
        assertEquals("unset integer", 123, configuration.getInt("unset", 123));
        configuration.setParameter("integer", "123");
        assertEquals("integer", 123, configuration.getInt("integer", 456));
    }

    public void testGetBoolean() {

        assertEquals("unset boolean", true, configuration.getBoolean("unset", true));
        assertEquals("unset boolean", false, configuration.getBoolean("unset", false));

        configuration.setParameter("boolean1", "true");
        configuration.setParameter("boolean2", "false");
        configuration.setParameter("boolean3", "error");

        assertEquals("boolean1", true, configuration.getBoolean("boolean1", false));
        assertEquals("boolean2", false, configuration.getBoolean("boolean2", true));
        assertEquals("boolean3", true, configuration.getBoolean("boolean3", true));
        assertEquals("boolean3", false, configuration.getBoolean("boolean3", false));
    }
}
