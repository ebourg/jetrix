/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2005  Emmanuel Bourg
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

package net.jetrix;

import net.jetrix.messages.channel.FieldMessage;
import org.junit.Before;
import org.junit.Test;

import static net.jetrix.config.Special.*;
import static org.junit.Assert.*;

/**
 * JUnit TestCase for the class net.jetrix.Field.
 *
 * @author Emmanuel Bourg
 */
public class FieldTest
{
    private Field field;

    @Before
    public void setUp()
    {
        field = new Field();
    }

    @Test
    public void testFullUpdate()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000005500");
        buffer.append("000000005500");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");

        FieldMessage message = new FieldMessage();
        message.setField(buffer.toString());
        field.update(message);

        // red square
        assertEquals("block at (8, 16)", Field.BLOCK_RED, field.getBlock(8, 16));
        assertEquals("block at (8, 17)", Field.BLOCK_RED, field.getBlock(8, 17));
        assertEquals("block at (9, 16)", Field.BLOCK_RED, field.getBlock(9, 16));
        assertEquals("block at (9, 17)", Field.BLOCK_RED, field.getBlock(9, 17));
    }

    @Test
    public void testPartialUpdate1()
    {
        FieldMessage message = new FieldMessage();
        message.setField("$3G3H4H5H");
        field.update(message);

        assertEquals("block at (0, 0)", Field.BLOCK_GREEN, field.getBlock(0, 0));
        assertEquals("block at (1, 0)", Field.BLOCK_GREEN, field.getBlock(1, 0));
        assertEquals("block at (2, 0)", Field.BLOCK_GREEN, field.getBlock(2, 0));
        assertEquals("block at (0, 1)", Field.BLOCK_GREEN, field.getBlock(0, 1));
    }

    @Test
    public void testPartialUpdate2()
    {
        FieldMessage message = new FieldMessage();
        message.setField("$3G3H4H5H\";H<H=H>H");
        field.update(message);

        // green left L
        assertEquals("block at (0, 0)", Field.BLOCK_GREEN, field.getBlock(0, 0));
        assertEquals("block at (1, 0)", Field.BLOCK_GREEN, field.getBlock(1, 0));
        assertEquals("block at (2, 0)", Field.BLOCK_GREEN, field.getBlock(2, 0));
        assertEquals("block at (0, 1)", Field.BLOCK_GREEN, field.getBlock(0, 1));

        // blue line
        assertEquals("block at (8, 0)", Field.BLOCK_BLUE, field.getBlock(8, 0));
        assertEquals("block at (9, 0)", Field.BLOCK_BLUE, field.getBlock(9, 0));
        assertEquals("block at (10,0)", Field.BLOCK_BLUE, field.getBlock(10, 0));
        assertEquals("block at (11,0)", Field.BLOCK_BLUE, field.getBlock(11, 0));
    }

    @Test
    public void testPartialUpdate3()
    {
        FieldMessage message = new FieldMessage();
        message.setField("+3H-4H.5H");
        field.update(message);

        assertEquals("block at (0, 0)", SWITCHFIELD.getLetter(), field.getBlock(0, 0));
        assertEquals("block at (1, 0)", GRAVITY.getLetter(), field.getBlock(1, 0));
        assertEquals("block at (2, 0)", QUAKEFIELD.getLetter(), field.getBlock(2, 0));
    }

    @Test
    public void testGetFieldString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("100000000001");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000005500");
        buffer.append("000000005500");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("000000000000");
        buffer.append("acnrsbgqo000");
        buffer.append("012345000001");

        FieldMessage message = new FieldMessage();
        message.setField(buffer.toString());
        field.update(message);

        assertNotNull("field string null", field.getFieldString());
        assertEquals("field string", buffer.toString(), field.getFieldString());
    }

    @Test
    public void testGetHighest()
    {
        byte[][] array = new byte[Field.WIDTH][Field.HEIGHT];

        field = new Field(array);
        field.clear();

        array[5][15] = Field.BLOCK_BLUE;
        array[0][0] = Field.BLOCK_RED;

        assertEquals("highest block", 15, field.getHighest());
    }

    @Test
    public void testIsEmpty()
    {
        assertTrue("the field is not empty", field.isEmpty());

        field.update(new FieldMessage("+3H-4H.5H"));

        assertFalse("the field is empty", field.isEmpty());
    }

    @Test
    public void testHasHoles()
    {
        byte[][] array = new byte[Field.WIDTH][Field.HEIGHT];

        field = new Field(array);
        field.clear();

        for (int i = 0; i < Field.WIDTH; i++)
        {
            array[i][0] = Field.BLOCK_RED;
            array[i][1] = Field.BLOCK_BLUE;
        }

        assertFalse("no holes", field.hasHoles());

        array[6][0] = Field.BLOCK_VOID;

        assertTrue("one hole", field.hasHoles());
    }
}
