/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001  Emmanuel Bourg
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


/**
 * Channel configuration.
 *
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ChannelConfig
{
    protected Settings settings;
    protected String name = "noname";
    protected String description;
    protected int maxPlayers = 6;

    public ChannelConfig()
    {
    	this(ServerConfig.getInstance().getDefaultSettings());
    }

    public ChannelConfig(Settings settings)
    {
        this.settings = settings;	
    }

    /**
     * Set game parameters.
     *
     *
     * @param settings
     *
     */
    public void setSettings(Settings settings)
    {
        this.settings = settings;
    }


    /**
     * Set channel name.
     *
     *
     * @param name
     *
     */
    public void setName(String name)
    {
        this.name = name;
    }


    /**
     * Set the description shown on entering the channel.
     *
     *
     * @param description
     *
     */
    public void setDescription(String description)
    {
        this.description = description;
    }


    /**
     * Set the maximum number of players allowed at the same time in the channel.
     *
     *
     * @param maxPlayers
     *
     */
    public void setMaxPlayers(int maxPlayers)
    {
        this.maxPlayers = maxPlayers;
    }


    /**
     * Get game parameters.
     *
     *
     * @return
     *
     */
    public Settings getSettings()
    {
        return settings;
    }


    /**
     * Get channel name.
     *
     *
     * @return
     *
     */
    public String getName()
    {
        return name;
    }


    /**
     * Get channel description.
     *
     *
     * @return
     *
     */
    public String getDescription()
    {
        return description;
    }


    /**
     * Get maximum number of players allowed.
     *
     *
     * @return
     *
     */
    public int getMaxPlayers()
    {
        return maxPlayers;
    }

}

