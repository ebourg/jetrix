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

package net.jetrix;

import junit.framework.*;
import net.jetrix.messages.*;

/**
 * JUnit TestCase for the class net.jetrix.Field.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class FieldTest extends TestCase
{
    private Field field;

    protected void setUp()
    {
        field = new Field();
    }

    public void testFullUpdate()
    {
        StringBuffer buffer = new StringBuffer();
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

    public void testPartialUpdate3()
    {
        FieldMessage message = new FieldMessage();
        message.setField("+3H-4H.5H");
        field.update(message);

        assertEquals("block at (0, 0)", Field.SPECIAL_SWITCHFIELD, field.getBlock(0, 0));
        assertEquals("block at (1, 0)", Field.SPECIAL_GRAVITY, field.getBlock(1, 0));
        assertEquals("block at (2, 0)", Field.SPECIAL_QUAKEFIELD, field.getBlock(2, 0));
    }

    public void testGetFieldString()
    {
        StringBuffer buffer = new StringBuffer();
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

}
