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

package org.lfjr.jts.config;

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
    private String description;
    private int maxPlayers = 6;
    private boolean persistent;

    /** extended properties */
    private Properties props;

    public ChannelConfig()
    {
        props = new Properties();
    }

    public ChannelConfig(Settings settings)
    {
        this();
        this.settings = settings;
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

    public void setMaxPlayers(Integer maxPlayers)
    {
        this.maxPlayers = maxPlayers.intValue();
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
     *
     * @return
     */
    public Settings getSettings()
    {
        return settings;
    }


    /**
     * Gets channel name.
     *
     * @return
     */
    public String getName()
    {
        return name;
    }


    /**
     * Gets channel description.
     *
     * @return
     */
    public String getDescription()
    {
        return description;
    }


    /**
     * Gets maximum number of players allowed.
     *
     * @return
     */
    public int getMaxPlayers()
    {
        return maxPlayers;
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

    public String getProperty(String name)
    {
        return props.getProperty(name);
    }

}