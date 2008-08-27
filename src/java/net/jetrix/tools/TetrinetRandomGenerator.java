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

/**
 * Random number generator used by Tetrinet (ie. the Random() function in Delphi)
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TetrinetRandomGenerator
{
    private static final int INCREMENT = 1;
    private static final int MULTIPLIER = 0x08088405;
    private static final long MODULUS = 0x100000000L;

    private long value;

    public TetrinetRandomGenerator(int seed)
    {
        this.value = seed;
    }

    public long getValue()
    {
        return value;
    }

    public synchronized int nextInt(int L) {
        nextValue();

        return (int) (value * L >> 32);
    }

    void nextValue() {
        value = (MULTIPLIER * value + INCREMENT) % MODULUS;
    }
}
