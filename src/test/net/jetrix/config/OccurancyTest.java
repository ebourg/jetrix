/**
 * Jetrix TetriNET Server
 * Copyright (C) 2011  Emmanuel Bourg
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

import junit.framework.TestCase;
import junitx.framework.ArrayAssert;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class OccurancyTest extends TestCase
{
    private long sum(int[] values)
    {
        long sum = 0;
        for (int value : values)
        {
            sum = sum + value;
        }
        return sum;
    }

    public void testNormalize1()
    {
        Occurancy s = new Occurancy();
        int[] tab = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        s.normalize(tab);
        assertEquals("Erreur de normalisation", 100, sum(tab));
    }

    public void testNormalize2()
    {
        Occurancy s = new Occurancy();
        int[] tab = { 0, 0, 0, 0, 0 };
        s.normalize(tab);
        assertEquals("Erreur de normalisation", 100, sum(tab));
        assertEquals("tab[0]", 20, tab[0]);
        assertEquals("tab[1]", 20, tab[1]);
        assertEquals("tab[2]", 20, tab[2]);
        assertEquals("tab[3]", 20, tab[3]);
        assertEquals("tab[4]", 20, tab[4]);
    }

    public void testNormalize3()
    {
        Occurancy s = new Occurancy();
        int[] tab = { 100, 200, 300, 50, 100, 50, 250, 300 };
        s.normalize(tab);
        assertEquals("Erreur de normalisation", 100, sum(tab));
    }

    public void testNormalize4()
    {
        Occurancy s = new Occurancy();
        int[] tab = { 8, 14, 1, 19, 5, 15, 3, 17, 6, 12 };
        s.normalize(tab);
        assertEquals("Erreur de normalisation", 100, sum(tab));
    }

    public void testNormalize5()
    {
        Occurancy s = new Occurancy();
        int[] tab4 = { 8, 14, 1, 19, 5, 15, 3, 17, 6, 12 };
        int[] tab5 = { 8, 14, 1, 19, 5, 15, 3, 17, 6, 12 };
        s.normalize(tab4);
        ArrayAssert.assertEquals("Normalization error", tab4, tab5);
    }

    public void testNormalize6()
    {
        Occurancy s = new Occurancy();
        int[] tab = { 0, -10 };
        s.normalize(tab);
        assertEquals("Erreur de normalisation", 100, sum(tab));
        assertEquals("tab[0]", 50, tab[0]);
        assertEquals("tab[1]", 50, tab[1]);
    }
}
