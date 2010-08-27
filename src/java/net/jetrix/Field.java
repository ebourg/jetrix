/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2010  Emmanuel Bourg
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

import static net.jetrix.config.Special.*;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import net.jetrix.config.*;
import net.jetrix.messages.channel.*;

/**
 * A game field.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class Field
{
    private static Logger log = Logger.getLogger("net.jetrix");

    public static final int WIDTH = 12;
    public static final int HEIGHT = 22;

    public static final byte BLOCK_VOID     = '0';
    public static final byte BLOCK_BLUE     = '1';
    public static final byte BLOCK_YELLOW   = '2';
    public static final byte BLOCK_GREEN    = '3';
    public static final byte BLOCK_PURPLE   = '4';
    public static final byte BLOCK_RED      = '5';
    public static final byte BLOCK_PREVIOUS = '6';

    /** The index of blocks used in a partial update. */
    private static final byte[] BLOCKS = {
            BLOCK_VOID, BLOCK_BLUE, BLOCK_YELLOW, BLOCK_GREEN, BLOCK_PURPLE, BLOCK_RED,
            (byte) ADDLINE.getLetter(),
            (byte) CLEARLINE.getLetter(),
            (byte) NUKEFIELD.getLetter(),
            (byte) RANDOMCLEAR.getLetter(),
            (byte) SWITCHFIELD.getLetter(),
            (byte) CLEARSPECIAL.getLetter(),
            (byte) GRAVITY.getLetter(),
            (byte) QUAKEFIELD.getLetter(),
            (byte) BLOCKBOMB.getLetter()};

    /** Array of blocks. (0, 0) is the bottom left block, and (11, 21) is the upper right block. */
    private byte[][] field = new byte[WIDTH][HEIGHT];

    public Field()
    {
        clear();
    }

    public Field(byte[][] field)
    {
        this.field = field;
    }

    /**
     * Check if the field is empty.
     */
    public boolean isEmpty()
    {
        for (int i = HEIGHT - 1; i >= 0; i--)
        {
            for (int j = 0; j < WIDTH; j++)
            {
                if (field[j][i] != BLOCK_VOID)
                {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Check if the field contains the specified special block.
     *
     * @param special the special to check
     * @since 0.3
     */
    public boolean contains(Special special)
    {
        for (int i = HEIGHT - 1; i >= 0; i--)
        {
            for (int j = 0; j < WIDTH; j++)
            {
                if (field[j][i] == special.getLetter())
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if the field contains a special block.
     */
    public boolean containsSpecialBlock()
    {
        for (int i = HEIGHT - 1; i >= 0; i--)
        {
            for (int j = 0; j < WIDTH; j++)
            {
                if ('a' <= field[j][i] && field[j][i] <= 'z')
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if the field has holes (i.e. if a gravity block may have an effect).
     *
     * @since 0.3
     */
    public boolean hasHoles()
    {
        for (int i = 1; i <= HEIGHT - 1; i++)
        {
            for (int j = 0; j < WIDTH; j++)
            {
                if (field[j][i] != BLOCK_VOID && field[j][i - 1] == BLOCK_VOID)
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Return the height of the highest block.
     *
     * @since 0.3
     */
    public int getHighest()
    {
        for (int i = HEIGHT - 1; i >= 1; i--)
        {
            for (int j = 0; j < WIDTH; j++)
            {
                if (field[j][i] != BLOCK_VOID)
                {
                    return i;
                }
            }
        }

        return 0;
    }

    /**
     * Update the field with the specified FieldMessage.
     */
    public void update(FieldMessage message)
    {       
        if (message.isPartialUpdate())
        {
            StringTokenizer tokenizer = new StringTokenizer(message.getField(), "!\"#$%&'()*+,-./", true);
            
            while (tokenizer.hasMoreTokens())
            {
                // block type
                String type = tokenizer.nextToken();
                byte color = BLOCKS[type.charAt(0) - 0x21];
                
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
        else if (message.isFullUpdate())
        {
            String fieldString = message.getField();
            for (int i = 0; i < fieldString.length(); i++)
            {
                char c = fieldString.charAt(i);
                if (c != BLOCK_PREVIOUS)
                {
                    field[i % WIDTH][HEIGHT - i / WIDTH - 1] = (byte) c;
                }
            }
        }
        else if (!message.isEmpty())
        {
            // malformed message
            log.warning("Malformed field update received from " + message.getSource());
        }
    }

    /**
     * Return the string representing this field as used in the
     * {@link net.jetrix.messages.channel.FieldMessage} messages.
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
     * Return the block at the specified location. (0, 0) is the bottom left
     * block, and (11, 21) is the upper right block.
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
    

    /**
     * Load the field from the specified file
     *
     * @since 0.3
     */
    public void load(String file) throws IOException
    {
        BufferedReader in = null;

        try
        {
            in = new BufferedReader(new FileReader(file));

            for (int i = 0; i < HEIGHT; i++)
            {
                String line = in.readLine();

                if (line == null || line.length() != WIDTH)
                {
                    throw new IOException("Field format error at line " + i);
                }

                for (int j = 0; j < WIDTH; j++)
                {
                    byte block = (byte) line.charAt(j);

                    if (isValidBlock(block))
                    {
                        field[j][HEIGHT - i - 1] = block;
                    }
                    else
                    {
                        throw new IOException("Illegal block '" + block + "' at line " + i + " , column " + j);
                    }
                }
            }
        }
        finally
        {
            if (in != null)
            {
                in.close();
            }
        }
    }

    /**
     * Save the field to the specified file.
     *
     * @since 0.3
     */
    public void save(String file) throws IOException
    {
        FileOutputStream out = null;

        try
        {
            out = new FileOutputStream(file);

            for (int i = 0; i < WIDTH; i++)
            {
                for (int j = 0; j < HEIGHT; j++)
                {
                    out.write(field[i][HEIGHT - j - 1]);
                }
                out.write('\n');
            }
        }
        finally
        {
            if (out != null)
            {
                out.close();
            }
        }
    }

    private boolean isValidBlock(byte block)
    {
        if (block >= '0' && block <= '6')
        {
            return true;
        }
        else if (Special.valueOf((char) block) != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
