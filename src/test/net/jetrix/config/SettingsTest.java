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

package net.jetrix.config;

import junit.framework.*;

/**
 * JUnit TestCase for the class net.jetrix.config.Settings
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class SettingsTest extends TestCase
{
    public static void testNormalize1()
    {
        Settings s = new Settings();
        int[] tab = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        s.normalize(tab);
        assertEquals("Erreur de normalisation", 100, sum(tab));
    }

    public static void testNormalize2()
    {
        Settings s = new Settings();
        int[] tab = { 0, 0, 0, 0, 0 };
        s.normalize(tab);
        assertEquals("Erreur de normalisation", 100, sum(tab));
    }

    public static void testNormalize3()
    {
        Settings s = new Settings();
        int[] tab = { 100, 200, 300, 50, 100, 50, 250, 300 };
        s.normalize(tab);
        assertEquals("Erreur de normalisation", 100, sum(tab));
    }

    public static void testNormalize4()
    {
        Settings s = new Settings();
        int[] tab = { 8, 14, 1, 19, 5, 15, 3, 17, 6, 12 };
        s.normalize(tab);
        assertEquals("Erreur de normalisation", 100, sum(tab));
    }

    public static void testNormalize5()
    {
        Settings s = new Settings();
        int[] tab4 = { 8, 14, 1, 19, 5, 15, 3, 17, 6, 12 };
        int[] tab5 = { 8, 14, 1, 19, 5, 15, 3, 17, 6, 12 };
        s.normalize(tab4);
        assertTrue("Erreur de normalisation", equals(tab4, tab5));
    }

    private static long sum(int[] tab)
    {
        long s = 0;
        for(int i=0; i<tab.length; i++) s = s + tab[i];
        return s;
    }

    private static boolean equals(int[] a, int[] b)
    {
        boolean equals = true;

        if (a.length == b.length)
        {
            int i=0;
            while(equals && i<a.length)
            {
                equals = (a[i]==b[i]);
                i++;
            }
        }
        else
        {
            equals = false;
        }

        return equals;
    }

    public static Test suite()
    {
        return new TestSuite(SettingsTest.class);
    }

}