/**
 * Jetrix TetriNET Server
 * Copyright (C) 2008  Emmanuel Bourg
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

package net.jetrix.tools;

import junit.framework.TestCase;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TetrinetRandomGeneratorTest extends TestCase
{
    public void testRandom() {
        TetrinetRandomGenerator generator = new TetrinetRandomGenerator(0);

        assertEquals("s0", 0x00000000L, generator.getValue());
        generator.nextValue();
        assertEquals("s1", 0x00000001L, generator.getValue());
        generator.nextValue();
        assertEquals("s2", 0x08088406L, generator.getValue());
        generator.nextValue();
        assertEquals("s3", 0xDC6DAC1FL, generator.getValue());
        generator.nextValue();
        assertEquals("s4", 0x33DC589CL, generator.getValue());
        generator.nextValue();
        assertEquals("s5", 0x45DE2B0DL, generator.getValue());
    }

    public void testNextInt() {
        TetrinetRandomGenerator generator = new TetrinetRandomGenerator(0);

        assertEquals("i1",  0x00, generator.nextInt(100));
        assertEquals("i2",  0x00, generator.nextInt(4));
        assertEquals("i3",  0x56, generator.nextInt(100));
        assertEquals("i4",  0x00, generator.nextInt(4));
        assertEquals("i5",  0x1B, generator.nextInt(100));
        assertEquals("i6",  0x02, generator.nextInt(4));
        assertEquals("i7",  0x1F, generator.nextInt(100));
        assertEquals("i8",  0x00, generator.nextInt(4));
        assertEquals("i9",  0x25, generator.nextInt(100));
        assertEquals("i10", 0x01, generator.nextInt(4));
        assertEquals("i11", 0x08, generator.nextInt(100));
        assertEquals("i12", 0x01, generator.nextInt(4));
        assertEquals("i13", 0x07, generator.nextInt(100));
        assertEquals("i14", 0x03, generator.nextInt(4));
        assertEquals("i15", 0x05, generator.nextInt(100));
        assertEquals("i16", 0x01, generator.nextInt(4));
        assertEquals("i17", 0x5B, generator.nextInt(100));
        assertEquals("i18", 0x01, generator.nextInt(4));
    }
}
