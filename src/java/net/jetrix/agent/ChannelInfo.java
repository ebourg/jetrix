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

package net.jetrix.agent;

/**
 * Channel information returned by the query agent.
 *
 * @since 0.2
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ChannelInfo
{
    private String name;
    private String description;
    private int priority;
    private int status;
    private int playernum;
    private int playermax;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public int getPriority()
    {
        return priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public int getPlayernum()
    {
        return playernum;
    }

    public void setPlayernum(int playernum)
    {
        this.playernum = playernum;
    }

    public int getPlayermax()
    {
        return playermax;
    }

    public void setPlayermax(int playermax)
    {
        this.playermax = playermax;
    }

    public String toString()
    {
        return "[Channel name='" + name + "' description='" + description + "']";
    }

}
