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

package net.jetrix.winlist;

/**
 * The score of a player or team in a winlist.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class WinlistScore
{
    private String name;
    private int type;
    private long score;

    public static final int TYPE_PLAYER = 0;
    public static final int TYPE_TEAM = 1;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public long getScore()
    {
        return score;
    }

    public void setScore(long score)
    {
        this.score = score;
    }

    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof WinlistScore)) return false;

        final WinlistScore score = (WinlistScore) o;

        if (type != score.type) return false;
        if (!name.equals(score.name)) return false;

        return true;
    }

    public int hashCode()
    {
        int result;
        result = name.hashCode();
        result = 29 * result + type;
        return result;
    }

    public String toString()
    {
        return "[Score name=" + name + " value=" + score + " type=" + type + "]";
    }
}
