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

package net.jetrix.listeners;

import junit.framework.*;
import net.jetrix.*;

/**
 * JUnit TestCase for the class net.jetrix.listeners.TetrinetListener
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TetrinetListenerTest extends TestCase
{
    private TetrinetListener listener;

    public void setUp()
    {
        listener = new TetrinetListener();
    }

    public void testXor1()
    {
        char[] tab1 = {1, 2, 3, 4, 5};
        char[] tab2 = {0, 0, 2, 6, 4};
        char[] xor   = {1, 2};

        assertEquals(arrayToString(tab2), arrayToString(listener.xor(tab1, xor)));
    }

    public void testXor2()
    {
        char[] tab1 = {1, 2, 3, 4, 5};
        char[] tab2 = {1, 2, 3, 4, 5};
        char[] xor   = {1, 2};

        assertEquals(arrayToString(tab2), arrayToString(listener.xor(listener.xor(tab1, xor), xor)));
    }

    public void testShift1()
    {
        char[] tab1 = {1, 2, 3, 240, 250};
        char[] tab2 = {51, 52, 53, 34, 44};
        int offset = 50;

        assertEquals(arrayToString(tab2), arrayToString(listener.shift(tab1, offset)));
    }

    public void testShift2()
    {
        char[] tab1 = {1, 2, 3, 240, 250};
        char[] tab2 = {207, 208, 209, 190, 200};
        int offset = -50;

        assertEquals(arrayToString(tab2), arrayToString(listener.shift(tab1, offset)));
    }

    public void testShift3()
    {
        char[] tab1 = {1, 2, 3, 240, 250};
        char[] tab2 = {1, 2, 3, 240, 250};
        int offset = 50;

        assertEquals(arrayToString(tab2), arrayToString(listener.shift(listener.shift(tab1, offset), -offset)));
    }

    public void testEncode()
    {
        int[] ip = {127, 0, 0, 1};
        String nickname = "Pihvi";

        assertEquals("80C210B3134A85CF71E46FD4C124B529AA227A9CFF0702", listener.encode(nickname, ip));
    }

    private String arrayToString(char[] array)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        for (int i = 0; i < array.length; i++)
        {
            buffer.append((int)array[i]);
            if (i < array.length - 1) buffer.append(", ");
        }
        buffer.append("}");

        return buffer.toString();
    }

    public static Test suite()
    {
        return new TestSuite(TetrinetListenerTest.class);
    }

}
