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

import java.util.*;

/**
 * A user connected to the server. The user can be a player or a spectator.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class User
{
    private String name;
    private String team;
    private int accessLevel;
    private int status;
    private boolean registered;
    private boolean playing;
    private int type;
    private Locale locale;
    private Map<String, Object> props;
    private Set<String> ignoredUsers;

    public static final int STATUS_OK  = 0;
    public static final int STATUS_AFK = 1;
    
    public static final int USER_PLAYER = 0;
    public static final int USER_SPECTATOR = 1;

    public User() { }

    public User(String name)
    {
        this.name = name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
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
     * Tells if the specified nickname is ignored by this user.
     *
     * @param name the name of the user
     * @return <code></code>
     * @since 0.2
     */
    public boolean ignores(String name)
    {
        return ignoredUsers == null ? false : ignoredUsers.contains(name.toLowerCase());
    }

    /**
     * Add the specified name to the list of ignored users.
     *
     * @param name
     * @since 0.2
     */
    public void ignore(String name)
    {
        if (ignoredUsers == null)
        {
            ignoredUsers = new TreeSet<String>();
        }

        ignoredUsers.add(name.toLowerCase());
    }

    /**
     * Remove the specified name from the list of ignored users.
     *
     * @param name
     * @since 0.2
     */
    public void unignore(String name)
    {
        if (ignoredUsers != null)
        {
            ignoredUsers.remove(name.toLowerCase());
        }
    }

    /**
     * Return the list of ignored players.
     *
     * @since 0.2
     */
    public Set<String> getIgnoredUsers()
    {
        return ignoredUsers == null ? new TreeSet<String>() : ignoredUsers;
    }

    /**
     * Set an extended property for this player.
     */
    public void setProperty(String key, Object value)
    {
        if (props == null)
        {
            props = new HashMap<String, Object>();
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
        return "[User " + name + " <" + team + "> playing=" + playing + "]";
    }

}
