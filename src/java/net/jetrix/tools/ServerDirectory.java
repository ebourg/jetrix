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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

/**
 * A utility class that fetches a list of tetrinet servers from online
 * directories like tetrinet.org and tfast.org.
 *
 * @since 0.2
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ServerDirectory
{
    /**
     * Return the list of registered servers.
     */
    public static List<String> getServers() throws Exception
    {
        List<String> servers = new ArrayList<String>();
        URL url = new URL("http://tfast.org/en/servers.php");
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        String line = null;

        while ((line = in.readLine()) != null)
        {
            if (line.contains("serv="))
            {
                int i = line.indexOf("serv=");
                servers.add(line.substring(i + 5, line.indexOf("\"", i)));
            }
        }

        in.close(); // todo finally

        return servers;
    }
}
