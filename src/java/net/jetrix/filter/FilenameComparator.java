/**
 * Jetrix TetriNET Server
 * Copyright (C) 2005  Emmanuel Bourg
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

package net.jetrix.filter;

import java.io.*;
import java.util.*;

/**
 * An improved lexicographic comparator handling a number in a name as a single
 * character. Unlike the lexicographic order where "foo1.txt" &lt; "foo10.txt" &lt; "foo2.txt"
 * here we have "foo1.txt" &lt; "foo2.txt" &lt; "foo10.txt".
 *
 * todo: move this code to commons-io or commons-lang
 *
 * @since 0.3
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class FilenameComparator implements Comparator<File>
{
    public int compare(File file1, File file2)
    {
        String name1 = file1.getAbsolutePath();
        String name2 = file2.getAbsolutePath();

        int index1 = 0;
        int index2 = 0;

        while (true)
        {
            String token1 = getToken(name1, index1);
            String token2 = getToken(name2, index2);

            if (token1 == null && token2 == null)
            {
                // no more tokens for each name, they are equal
                return 0;
            }

            if (token1 == null)
            {
                // the first name is shorter, it goes first
                return -1;
            }

            if (token2 == null)
            {
                // the second name is shorter, it goes first
                return 1;
            }

            int comp = compareToken(token1, token2);
            if (comp == 0)
            {
                // the tokens are equal, move to the next tokens
                index1 = index1 + token1.length();
                index2 = index2 + token2.length();
            }
            else
            {
                return comp;
            }
        }
    }

    /**
     * Extract from the string  the next token starting at the specified index.
     *
     * @param string the string to parse
     * @param index  the beginning of the token
     */
    String getToken(String string, int index)
    {
        if (string == null || string.length() == 0 || index == string.length())
        {
            return null;
        }
        else
        {
            // are we parsing a string or a number ?
            boolean type = Character.isDigit(string.charAt(index));

            // move forward until a different character type is detected
            int end = index + 1;
            while (end < string.length() && Character.isDigit(string.charAt(end)) == type)
            {
                end++;
            }

            return string.substring(index, end);
        }
    }

    /**
     * Tells if the specified string is a number.
     */
    boolean isNumber(String string)
    {
        if (string == null || string.length() == 0)
        {
            return false;
        }
        else
        {
            return Character.isDigit(string.charAt(0));
        }
    }

    /**
     * Compare two tokens according to their types (string or number).
     */
    int compareToken(String token1, String token2)
    {
        if (isNumber(token1) && isNumber(token2))
        {
            return Integer.parseInt(token1) - Integer.parseInt(token2);
        }
        else
        {
            return token1.compareTo(token2);
        }
    }
}
