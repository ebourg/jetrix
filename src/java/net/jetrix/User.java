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

package net.jetrix;

import java.net.*;
import java.util.*;

/**
 * A user connected to the server. The user can be a player or a spectator.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class User
{
    private String nickname;
    private String team;
    private int accessLevel;
    private int status;
    private boolean registered;
    private boolean playing;
    private int type;
    private Locale locale;
    private Map props;

    public static final int STATUS_OK  = 0;
    public static final int STATUS_AFK = 1;
    
    public static final int USER_PLAYER = 0;
    public static final int USER_SPECTATOR = 1;

    public User() { }

    public User(String nickname)
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

    public void setAccessLevel(int accessLevel)
    {
        this.accessLevel = accessLevel;
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public int getStatus()
    {
        return status;
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

    public void setSpectator()
    {
        this.type = USER_SPECTATOR;
    }

    public boolean isSpectator()
    {
        return (type == USER_SPECTATOR);
    }

    public void setPlayer()
    {
        this.type = USER_PLAYER;
    }

    public boolean isPlayer()
    {
        return (type == USER_PLAYER);
    }

    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    public Locale getLocale()
    {
        return locale;
    }

    /**
     * Set an extended property for this player.
     */
    public void setProperty(String key, Object value)
    {
        if (props == null)
        {
            props = new HashMap();
        }
        props.put(key, value);
    }

    /**
     * Return an extended property.
     */
    public Object getProperty(String key)
    {
        return (props == null) ? null : props.get(key);
    }

    public String toString()
    {
        return "[User " + nickname + " <" + team + "> playing=" + playing + "]";
    }

}
