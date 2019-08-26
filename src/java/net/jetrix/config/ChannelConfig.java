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
 */
public class ChannelConfig
{
    private Settings settings;
    private String name = "noname";
    private String password;
    private String description;
    private String topic;
    private int maxPlayers = PLAYER_CAPACITY;
    private int maxSpectators = SPECTATOR_CAPACITY;
    private int accessLevel;
    private boolean persistent;
    private String winlistId;
    private boolean idleAllowed;
    private boolean visible = true;
    private Speed speed = Speed.MIXED;

    /** extended properties */
    private Properties props;

    /** channel filter definitions */
    private List<FilterConfig> filters;

    /** Default spectator capacity */
    public static final int SPECTATOR_CAPACITY = 50;

    /** Default player capacity */
    public static final int PLAYER_CAPACITY = 6;

    public ChannelConfig()
    {
        filters = new ArrayList<FilterConfig>();
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
    public Iterator<FilterConfig> getFilters()
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

    public boolean isIdleAllowed()
    {
        return idleAllowed;
    }

    public void setIdleAllowed(boolean idleAllowed)
    {
        this.idleAllowed = idleAllowed;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public Speed getSpeed()
    {
        return speed;
    }

    public void setSpeed(Speed speed)
    {
        this.speed = speed;
    }

    /**
     * Tells if the specified protocol is compatible with the speed restriction of the channel.
     * 
     * @param protocol the name of the protocol
     * @since 0.3
     */
    public boolean isProtocolAccepted(String protocol)
    {
        switch (speed)
        {
            case NORMAL:
                return !"tetrifast".equals(protocol);
            case FAST:
                return !"tetrinet".equals(protocol);
            default:
                return true;
        }
    }
}
