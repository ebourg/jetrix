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

import java.util.logging.*;
import java.util.*;

import net.jetrix.messages.*;

/**
 * A game field.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class Field
{
    public static final int WIDTH = 12;
    public static final int HEIGHT = 22;

    public static final byte BLOCK_VOID   = '0';
    public static final byte BLOCK_BLUE   = '1';
    public static final byte BLOCK_YELLOW = '2';
    public static final byte BLOCK_GREEN  = '3';
    public static final byte BLOCK_PURPLE = '4';
    public static final byte BLOCK_RED    = '5';

    public static final byte SPECIAL_ADDLINE      = 'a';
    public static final byte SPECIAL_CLEARLINE    = 'c';
    public static final byte SPECIAL_NUKEFIELD    = 'n';
    public static final byte SPECIAL_RANDOMCLEAR  = 'r';
    public static final byte SPECIAL_SWITCHFIELD  = 's';
    public static final byte SPECIAL_CLEARSPECIAL = 'b';
    public static final byte SPECIAL_GRAVITY      = 'g';
    public static final byte SPECIAL_QUAKEFIELD   = 'q';
    public static final byte SPECIAL_BLOCKBOMB    = 'o';

    private static final byte blocks[] =
            new byte[] { BLOCK_VOID, BLOCK_BLUE, BLOCK_YELLOW, BLOCK_GREEN, BLOCK_PURPLE, BLOCK_RED, SPECIAL_ADDLINE,
                         SPECIAL_CLEARLINE, SPECIAL_NUKEFIELD, SPECIAL_RANDOMCLEAR, SPECIAL_SWITCHFIELD,
                         SPECIAL_CLEARSPECIAL, SPECIAL_GRAVITY, SPECIAL_QUAKEFIELD, SPECIAL_BLOCKBOMB };

    private byte[][] field = new byte[WIDTH][HEIGHT];

    private Logger log = Logger.getLogger("net.jetrix");

    public Field()
    {
        clear();
    }

    public Field(byte[][] field)
    {
        this.field = field;
    }

    /**
     * Update the field with the specified FieldMessage.
     */
    public void update(FieldMessage message)
    {
        String fieldString = message.getField();
        if (fieldString != null && fieldString.length() > 0)
        {
            char first = fieldString.charAt(0);
            if (first >= 0x21 && first <= 0x2f)
            {
                // partial update
                StringTokenizer tokenizer = new StringTokenizer(fieldString, "!\"#$%&'()*+,-./", true);

                while (tokenizer.hasMoreTokens())
                {
                    // block type
                    String type = tokenizer.nextToken();
                    byte color = blocks[type.charAt(0) - 0x21];

                    // locations
                    String locations = tokenizer.nextToken();
                    for (int i = 0; i < locations.length(); i = i + 2)
                    {
                        int x = locations.charAt(i) - '3';
                        int y = HEIGHT - (locations.charAt(i + 1) - '3') - 1;
                        field[x][y] = color;
                    }
                }

            }
            else if (fieldString.length() == WIDTH * HEIGHT)
            {
                // full update
                for (int i = 0; i < fieldString.length(); i++)
                {
                    field[i % WIDTH][HEIGHT - i / WIDTH - 1] = (byte) fieldString.charAt(i);
                }
            }
            else
            {
                // malformed message
                log.warning("Malformed field update received from " + message.getSource());
            }
        }
    }

    /**
     * Return the string representing this field as used in the
     * {@link FieldMessage} messages.
     */
    public String getFieldString()
    {
        byte[] buffer = new byte[WIDTH * HEIGHT];
        int k = 0;
        for (int j = HEIGHT - 1; j >= 0; j--)
        {
            for (int i = 0; i < WIDTH; i++)
            {
                buffer[k++] = field[i][j];
            }
        }

        return new String(buffer);
    }

    /**
     * Return the block at the specified location.
     *
     * @param x
     * @param y
     */
    public byte getBlock(int x, int y)
    {
        return field[x][y];
    }

    /**
     * Clear the field.
     */
    public void clear()
    {
        for (int i = 0; i < WIDTH; i++)
        {
            for (int j = 0; j < HEIGHT; j++)
            {
                field[i][j] = BLOCK_VOID;
            }
        }
    }
}
