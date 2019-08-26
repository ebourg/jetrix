/**
 * Jetrix TetriNET Server
 * Copyright (C) 2004  Emmanuel Bourg
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
 * Block enumeration.
 *
 * @since 0.2
 * 
 * @author Emmanuel Bourg
 */
public enum Block
{
    LINE,
    SQUARE,
    LEFTL,
    RIGHTL,
    LEFTZ,
    RIGHTZ,
    HALFCROSS;

    public String getCode()
    {
        return name().toLowerCase();
    }

    public String getName(Locale locale)
    {
        return Language.getText("command.config.blocks." + getCode(), locale);
    }
}
