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

import net.jetrix.config.*;

/**
 * Manages Channels
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ChannelManager
{
    private List channels;
    private Map channelMap;
    private static ChannelManager instance = new ChannelManager();

    private ChannelManager()
    {
        channels = new ArrayList();
        channelMap = new TreeMap();
    }

    public static ChannelManager getInstance()
    {
        return instance;
    }

    /**
     * Create a channel and start it immediately.
     *
     * @param config the channel configuration
     */
    public Channel createChannel(ChannelConfig config)
    {
        return createChannel(config, true);
    }

    /**
     * Create a channel initialized with the specified configuration.
     *
     * @param config the channel configuration
     * @param start  initial state
     */
    public Channel createChannel(ChannelConfig config, boolean start)
    {
        Channel channel = new Channel(config);
        if (start)
        {
            channel.start();
        }
        channels.add(channel);
        channelMap.put(config.getName().toLowerCase(), channel);
        return channel;
    }

    /**
     * Remove a channel.
     */
    public void removeChannel(String name)
    {
        throw new UnsupportedOperationException("removeChannel not implemented yet");
    }

    /**
     * Returns the number of existing channels.
     */
    public int getChannelCount()
    {
        return channels.size();
    }

    /**
     * Returns an iterator over the channels available.
     */
    public Iterator channels()
    {
        return channels.iterator();
    }

    /**
     * Looks for a channel with room left.
     *
     * @return <tt>null</tt> if there is no room left in all available channels
     */
    public Channel getOpenedChannel()
    {
        Channel channel = null;
        Iterator it = channels();
        while (it.hasNext() && channel == null)
        {
            Channel channel2 = (Channel) it.next();
            if (!channel2.isFull()) channel = channel2;
        }

        return channel;
    }

    /**
     * Return the channel with the specified name. The leading # is removed from
     * the name before searching. The name is not case sensitive.
     *
     * @param name the name of the channel to find
     * @return
     */
    public Channel getChannel(String name)
    {
        return getChannel(name, false);
    }

    /**
     * Returns the channel with the specified name. The leading # is removed from
     * the name before searching. The name is not case sensitive. If no channel
     * matches the name specified, it can return the first channel starting
     * with the name if the <code>partial</code> parameter is set to
     * <code>true</code>.
     *
     * @param name    the name of the channel to find
     * @param partial use the partial name matching
     *
     * @return instance of the specified channel, <tt>null</tt> if not found
     */
    public Channel getChannel(String name, boolean partial)
    {
        // stripping leading #
        name = name.replaceFirst("#", "").toLowerCase();

        Channel channel = (Channel) channelMap.get(name);

        if (channel == null && partial)
        {
            // match a partial name
            Iterator names = channelMap.keySet().iterator();
            while (channel == null && names.hasNext())
            {
                String name2 = (String) names.next();
                if (name2.startsWith(name))
                {
                    channel = (Channel) channelMap.get(name2);
                }
            }
        }

        return channel;
    }

    /**
     * Clear the channel list.
     */
    public void clear()
    {
        channels.clear();
        channelMap.clear();
    }

    /**
     * Get a channel by number in the list.
     */
    public Channel getChannel(int num)
    {
        return ((num >= 0 && num < channels.size()) ? (Channel) channels.get(num) : null);
    }

}
