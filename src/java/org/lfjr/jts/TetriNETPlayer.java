/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2002  Emmanuel Bourg
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

package org.lfjr.jts;

import java.net.*;

/**
 * Holds information about a player.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TetriNETPlayer
{
    private String nickname;
    private String team;

    private int status;
    private boolean registered;
    private boolean playing;

    public static final int STATUS_OK  = 0;
    public static final int STATUS_AFK = 1;

    public TetriNETPlayer() { }

    public TetriNETPlayer(String nickname)
    {
        this.nickname = nickname;
    }

    public void setName(String nickname)
    {
        this.nickname = nickname;
    }

    public String getName()
    {
        return nickname;
    }

    public void setTeam(String team)
    {
        this.team = team;
    }

    public String getTeam()
    {
        return team;
    }

    public void setRegistered(boolean registered)
    {
        this.registered = registered;
    }

    public boolean isRegistered()
    {
        return registered;
    }

    public void setPlaying(boolean playing)
    {
        this.playing = playing;
    }

    public boolean isPlaying()
    {
        return playing;
    }

    public String toString()
    {
        return "[Player " + nickname + " <" + team + "> playing=" + playing + "]";
    }

}
