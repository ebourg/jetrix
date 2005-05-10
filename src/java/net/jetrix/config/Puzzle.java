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

package net.jetrix.config;

import net.jetrix.*;

/**
 * A field with specific game settings.
 *
 * @since 0.3
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class Puzzle
{
    private Field field;
    private Settings settings;
    private String name;
    private String author;
    private String comment;
    private String key;

    public Field getField()
    {
        return field;
    }

    public void setField(Field field)
    {
        this.field = field;
    }

    public Settings getSettings()
    {
        return settings;
    }

    public void setSettings(Settings settings)
    {
        this.settings = settings;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }
}
