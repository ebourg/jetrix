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

import junit.framework.TestCase;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class QueryAgentTest extends TestCase
{
    private String hostname = "tetrinet.fr";

    public void testGetVersion() throws Exception
    {
        QueryAgent agent = new QueryAgent();
        agent.connect(hostname);
        String version = agent.getVersion();
        agent.disconnect();

        assertEquals("version", "1.13.2ice Dual server", version);
    }

    public void testGetPlayerNumber() throws Exception
    {
        QueryAgent agent = new QueryAgent();
        agent.connect(hostname);
        int count = agent.getPlayerNumber();
        agent.disconnect();

        assertTrue("player number", count >= 0) ;
    }

    public void testGetChannels() throws Exception
    {
        QueryAgent agent = new QueryAgent();
        agent.connect(hostname);
        List<ChannelInfo> channels = agent.getChannels();
        agent.disconnect();

        assertNotNull("null list", channels);
        assertFalse("list is empty", channels.isEmpty());
    }

    public void testGetPlayers() throws Exception
    {
        QueryAgent agent = new QueryAgent();
        agent.connect("tetridome.com");
        List<PlayerInfo> players = agent.getPlayers();
        agent.disconnect();

        assertNotNull("null list", players);
        assertFalse("list is empty", players.isEmpty());
    }

    public void testGetPing() throws Exception
    {
        QueryAgent agent = new QueryAgent();
        agent.connect(hostname);
        long ping = agent.getPing();
        agent.disconnect();

        assertTrue("ping", ping > 0);
    }
}
