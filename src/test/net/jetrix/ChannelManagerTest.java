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

import junit.framework.*;

import net.jetrix.config.*;

/**
 * JUnit TestCase for the class net.jetrix.ChannelManager.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ChannelManagerTest extends TestCase
{
    private ChannelManager manager;
    private ChannelConfig config1;
    private ChannelConfig config2;

    public void setUp()
    {
        manager = ChannelManager.getInstance();
        manager.clear();

        config1 = new ChannelConfig();
        config1.setName("test1");

        config2 = new ChannelConfig();
        config2.setName("test2");
    }

    public void tearDown()
    {
        manager.clear();
    }

    public void testCreateChannel()
    {
        assertEquals("channel count before creation", 0, manager.getChannelCount());

        // add a test channel
        manager.createChannel(config1, false);

        assertEquals("channel count after creation", 1, manager.getChannelCount());
    }

    public void testGetChannel()
    {
        // add a test channel
        String name = config1.getName();
        manager.createChannel(config1, false);

        // retrieve the channel
        Channel channel = manager.getChannel(name);
        assertNotNull("channel not found", channel);
        assertEquals("channel name", name, channel.getConfig().getName());
    }

    public void testGetChannelSharp()
    {
        // add a test channel
        String name = config1.getName();
        manager.createChannel(config1, false);

        // retrieve the channel
        Channel channel = manager.getChannel("#" + name);
        assertNotNull("channel not found", channel);
        assertEquals("channel name", name, channel.getConfig().getName());
    }

    public void testGetChannelMixedCase()
    {
        // add a test channel
        String name = config1.getName();
        manager.createChannel(config1, false);

        // retrieve the channel
        Channel channel = manager.getChannel("tEsT1");
        assertNotNull("channel not found", channel);
        assertEquals("channel name", name, channel.getConfig().getName());
    }

    public void testGetChannelPartialName()
    {
        // add a test channel
        config1.setName("xzyt");
        manager.createChannel(config1, false);
        manager.createChannel(config2, false);

        // retrieve the channel
        Channel channel = manager.getChannel("test", true);
        assertNotNull("channel not found", channel);
        assertEquals("channel name", config2.getName(), channel.getConfig().getName());
    }

    public void testGetChannelByNumber()
    {
        // add a test channel
        String name = config1.getName();
        manager.createChannel(config1, false);

        // retrieve the channel
        Channel channel = manager.getChannel(0);
        assertNotNull("channel not found", channel);
        assertEquals("channel name", name, channel.getConfig().getName());

        Channel channel2 = manager.getChannel(1);
        assertNull("channel found at index 1", channel2);
    }

    public void testGetOpenedChannel()
    {
        // add a test channel
        config1.setMaxPlayers(0);
        manager.createChannel(config1, false);

        assertNull("closed channel returned", manager.getOpenedChannel());

        config1.setMaxPlayers(6);
        assertNotNull("opened channel not found", manager.getOpenedChannel());
    }

    public void testRemoveChannel()
    {
        // add a test channel
        manager.createChannel(config1, false);

        assertEquals("channel count before removal", 1, manager.getChannelCount());
        manager.removeChannel(config1.getName());
        assertEquals("channel count after removal", 0, manager.getChannelCount());
    }

}
