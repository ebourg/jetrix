/**
 * Jetrix TetriNET Server
 * Copyright (C) 2004-2005  Emmanuel Bourg
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

import net.jetrix.Language;

import java.util.Locale;

/**
 * Special blocks enumeration.
 *
 * @since 0.2
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public enum Special
{
    ADDLINE('a'),
    CLEARLINE('c'),
    NUKEFIELD('n'),
    RANDOMCLEAR('r'),
    SWITCHFIELD('s'),
    CLEARSPECIAL('b'),
    GRAVITY('g'),
    QUAKEFIELD('q'),
    BLOCKBOMB('o');

    private final char letter;

    Special(char letter)
    {
        this.letter = letter;
    }

    public char getLetter()
    {
        return letter;
    }

    public String getCode()
    {
        return name().toLowerCase();
    }

    public String getName(Locale locale)
    {
        return Language.getText("command.config.specials." + getCode(), locale);
    }

    /**
     * Return the special matching the specified letter.
     *
     * @param letter the letter of the special
     * @since 0.3
     */
    public static Special valueOf(char letter)
    {
        for (Special special : values())
        {
            if (special.getLetter() == letter)
            {
                return special;
            }
        }
        
        return null;
    }
}
