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

package net.jetrix.tools;

import junit.framework.TestCase;

import java.util.List;
import java.io.IOException;

import net.jetrix.agent.QueryAgent;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ServerDirectoryTest extends TestCase
{
    public void testGetServers() throws Exception
    {
        List<String> servers = ServerDirectory.getServers();

        assertNotNull("null list", servers);
        assertFalse("empty list", servers.isEmpty());
        assertTrue("missing server tetrinet.org", servers.contains("tetrinet.org"));
    }

    public void testShowVersions() throws Exception
    {
        List<String> servers = ServerDirectory.getServers();

        for (String server : servers)
        {
            try
            {
                QueryAgent agent = new QueryAgent();
                agent.connect(server);
                String version = agent.getVersion();
                agent.disconnect();

                System.out.println(server + "\t\t" + version);
            }
            catch (IOException e)
            {
                System.err.println("Error connecting to " + server + " ("+e.getMessage()+")");
                e.printStackTrace();
            }
        }
    }

}
