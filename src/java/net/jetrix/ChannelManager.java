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
    private static ChannelManager instance = new ChannelManager();

    private ChannelManager()
    {
        channels = new ArrayList();
    }

    public static ChannelManager getInstance()
    {
        return instance;
    }

    public Channel createChannel()
    {
        return createChannel("jetrix");
    }

    public Channel createChannel(String name)
    {
        ChannelConfig cc = new ChannelConfig();
        cc.setName("jetrix");
        return createChannel(cc);
    }

    public Channel createChannel(ChannelConfig conf)
    {
        Channel ch = new Channel(conf);
        ch.start();
        channels.add(ch);
        return ch;
    }

    public void removeChannel(String name) {}

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
        while( it.hasNext() && channel==null)
        {
            Channel channel2 = (Channel)it.next();
            if (!channel2.isFull()) channel = channel2;
        }

        return channel;
    }

    /**
     * Returns the channel with the specified name. Leading # are removed
     * from the name before searching.
     *
     * @param name  name of the channel to find
     *
     * @return instance of the specified channel, <tt>null</tt> if not found
     */
    public Channel getChannel(String name)
    {
        Channel channel = null;
        
        // stripping leading #
        name = name.replaceFirst("#", "");

        Iterator it = channels();
        while( it.hasNext() && channel==null)
        {
            Channel channel2 = (Channel)it.next();
            if (channel2.getConfig().getName().equalsIgnoreCase(name)) channel = channel2;
        }

        return channel;
    }

    /**
     * Clear the channel list.
     */
    public void clear()
    {
        channels.clear();
    }

    public Channel getChannel(int num)
    {
        return ((num >= 0 && num < channels.size()) ? (Channel)channels.get(num) : null);
    }

}
