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
    private String topic;
    private int maxPlayers = 6;
    private int maxSpectators = 50;
    private int accessLevel;
    private boolean persistent;
    private String winlistId;

    /** extended properties */
    private Properties props;

    /** channel filter definitions */
    private List filters;

    public ChannelConfig()
    {
        filters = new ArrayList();
        settings = new Settings();
    }

    /**
     * Gets game parameters.
     */
    public Settings getSettings()
    {
        return settings;
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
     * Gets channel name.
     */
    public String getName()
    {
        return name;
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
     * Gets the password.
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Sets the password to enter the channel.
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * Gets channel description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the description shown on entering the channel.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Return the topic.
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Set the topic.
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * Gets maximum number of players allowed.
     */
    public int getMaxPlayers()
    {
        return maxPlayers;
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
     * Gets maximum number of spectators allowed.
     */
    public int getMaxSpectators()
    {
        return maxSpectators;
    }

    /**
     * Sets the maximum number of spectators allowed at the same time in the channel.
     *
     * @param maxSpectators
     */
    public void setMaxSpectators(int maxSpectators)
    {
        this.maxSpectators = maxSpectators;
    }

    /**
     * Gets the minimum access level required to join the channel.
     */
    public int getAccessLevel()
    {
        return accessLevel;
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
     * Tell if the channel will vanish once the last player leave
     *
     * @return <tt>true</tt> if the channel is persistent, <tt>false</tt> if not
     */
    public boolean isPersistent()
    {
        return persistent;
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

    public String getWinlistId()
    {
        return winlistId;
    }

    public void setWinlistId(String winlistId)
    {
        this.winlistId = winlistId;
    }

    public String getProperty(String name)
    {
        return (props == null) ? null : props.getProperty(name);
    }

    public void setProperty(String name, String value)
    {
        if (props == null)
        {
            props = new Properties();
        }
        props.setProperty(name, value);
    }

    /**
     * Tell if a password is required to enter the channel.
     */
    public boolean isPasswordProtected()
    {
        return (password != null);
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
