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

import java.util.List;

/**
 * Information about a tetrinet server retrieved through the query protocol.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class QueryInfo
{
    private String hostname;
    private String version;
    private List<PlayerInfo> players;
    private List<ChannelInfo> channels;
    private long ping;

    public String getHostname()
    {
        return hostname;
    }

    public void setHostname(String hostname)
    {
        this.hostname = hostname;
    }

    /**
     * Return the version of the server.
     */
    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public List<PlayerInfo> getPlayers()
    {
        return players;
    }

    public void setPlayers(List<PlayerInfo> players)
    {
        this.players = players;
    }

    /**
     * Return the list of players in the specified channel.
     *
     * @param channel the name of the channel
     */
    public List getPlayers(String channel)
    {
        return players;
    }

    public List getChannels()
    {
        return channels;
    }

    public void setChannels(List<ChannelInfo> channels)
    {
        this.channels = channels;
    }

    public long getPing()
    {
        return ping;
    }

    public void setPing(long ping)
    {
        this.ping = ping;
    }
}
