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

package net.jetrix.config;

import java.util.*;

/**
 * Channel configuration.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ChannelConfig
{
    private Settings settings;
    private String name = "noname";
    private String password;
    private String description;
    private int maxPlayers = 6;
    private int accessLevel;
    private boolean persistent;

    /** extended properties */
    private Properties props;

    /** channel filter definitions */
    private ArrayList filters;

    public ChannelConfig()
    {
        props = new Properties();
        filters = new ArrayList();
        settings = new Settings();
    }

    /**
     * Sets game parameters.
     *
     * @param settings
     */
    public void setSettings(Settings settings)
    {
        this.settings = settings;
    }

    /**
     * Sets channel name.
     *
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Sets the password to enter the channel.
     *
     * @param password
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * Sets the description shown on entering the channel.
     *
     * @param description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Sets the maximum number of players allowed at the same time in the channel.
     *
     * @param maxPlayers
     */
    public void setMaxPlayers(int maxPlayers)
    {
        this.maxPlayers = maxPlayers;
    }

    /**
     * Sets the minimum access level required to join the channel.
     *
     * @param accessLevel
     */
    public void setAccessLevel(int accessLevel)
    {
        this.accessLevel = accessLevel;
    }

    /**
     * Sets channel persistence
     *
     * @param persistent
     */
    public void setPersistent(boolean persistent)
    {
        this.persistent = persistent;
    }

    public void setProperty(String name, String value)
    {
        props.setProperty(name, value);
    }

    /**
     * Gets game parameters.
     */
    public Settings getSettings()
    {
        return settings;
    }

    /**
     * Gets channel name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the password.
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Gets channel description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Gets maximum number of players allowed.
     */
    public int getMaxPlayers()
    {
        return maxPlayers;
    }

    /**
     * Gets the minimum access level required to join the channel.
     */
    public int getAccessLevel()
    {
        return accessLevel;
    }

    /**
     * Tell if the channel will vanish once the last player leave
     *
     * @return <tt>true</tt> if the channel is persistent, <tt>false</tt> if not
     */
    public boolean isPersistent()
    {
        return persistent;
    }

    /**
     * Tell if a password is required to enter the channel.
     */
    public boolean isPasswordProtected()
    {
        return (password == null);
    }

    public String getProperty(String name)
    {
        return props.getProperty(name);
    }

    /**
     * Returns an iterator of registered filters.
     */
    public Iterator getFilters()
    {
        return filters.iterator();
    }

    /**
     * Registers a new filter.
     */
    public void addFilter(FilterConfig fconf)
    {
        filters.add(fconf);
    }

}
