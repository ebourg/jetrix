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

import junit.framework.TestCase;

import java.util.List;

import net.jetrix.agent.ChannelInfo;
import net.jetrix.agent.PlayerInfo;
import net.jetrix.agent.QueryAgent;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class QueryAgentTest extends TestCase
{
    public void testGetVersion() throws Exception
    {
        QueryAgent agent = new QueryAgent();
        agent.connect("tetrinet.fr");
        String version = agent.getVersion();
        agent.disconnect();

        assertEquals("version", "1.13.2ice Dual server", version);
    }

    public void testGetPlayerNumber() throws Exception
    {
        QueryAgent agent = new QueryAgent();
        agent.connect("tetrinet.fr");
        int count = agent.getPlayerNumber();
        agent.disconnect();

        assertTrue("player number", count >= 0) ;
    }

    public void testGetChannels() throws Exception
    {
        QueryAgent agent = new QueryAgent();
        agent.connect("tetrinet.fr");
        List<ChannelInfo> channels = agent.getChannels();
        agent.disconnect();

        assertNotNull("null list", channels);
        assertFalse("list is empty", channels.isEmpty());
    }

    public void testGetPlayers() throws Exception
    {
        QueryAgent agent = new QueryAgent();
        agent.connect("tetrinet.fr");
        List<PlayerInfo> players = agent.getPlayers();
        agent.disconnect();

        assertNotNull("null list", players);
        assertFalse("list is empty", players.isEmpty());
    }

    public void testGetPing() throws Exception
    {
        QueryAgent agent = new QueryAgent();
        agent.connect("tetrinet.fr");
        long ping = agent.getPing();
        agent.disconnect();

        assertTrue("ping", ping > 0);
    }

    public void testParseQuotedTokens()
    {
        QueryAgent agent = new QueryAgent();

        String s = "\"a a\" \"bb\" cc dd \"ee\"";

        List<String> tokens = agent.parseQuotedTokens(s);

        assertNotNull("null list", tokens);
        assertEquals("1st token", "a a", tokens.get(0));
        assertEquals("2nd token", "bb", tokens.get(1));
        assertEquals("3rd token", "cc", tokens.get(2));
        assertEquals("4th token", "dd", tokens.get(3));
        assertEquals("5th token", "ee", tokens.get(4));
    }
}
