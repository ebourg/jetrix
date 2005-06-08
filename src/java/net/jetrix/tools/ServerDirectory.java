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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * A utility class that fetches a list of tetrinet servers from online
 * directories like tetrinet.org and tsrv.com.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 * @since 0.2
 */
public class ServerDirectory
{
    /**
     * Return the list of registered servers.
     */
    public static Collection<String> getServers()
    {
        Collection<String> servers = new TreeSet<String>();

        try
        {
            servers.addAll(extractServers(new URL("http://slummy.tetrinet.org/grav/slummy_grav.html"), "<TD><font size=\"+0\"><font face=\"helvetica,arial\">", "<"));
            servers.addAll(extractServers(new URL("http://dieterdhoker.mine.nu:8280/cgi-bin/TSRV/servers.htm"), "serverhost=", "\""));
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        return getDistinctServers(servers);
    }

    /**
     * Extract a list of server from a web page. There must be one server by line only
     *
     * @since 0.3
     *
     * @param url         the URL of the page containing the list of servers
     * @param startString the string right before the server name
     * @param endString   the string to find right after the server name
     */
    private static Collection<String> extractServers(URL url, String startString, String endString)
    {
        Collection<String> servers = new TreeSet<String>();
        BufferedReader in = null;

        try
        {
            in = new BufferedReader(new InputStreamReader(url.openStream()));

            String line = null;

            while ((line = in.readLine()) != null)
            {
                if (line.contains(startString))
                {
                    int i = line.indexOf(startString) + startString.length();
                    servers.add(line.substring(i, line.indexOf(endString, i)).toLowerCase());
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return servers;
    }

    /**
     * Return the distinct hosts from the specified list of servers.
     * Host names (i.e tetrinet.fr) are prefered over host addresses
     * (i.e 194.117.194.68).
     *
     * @since 0.3
     */
    private static Set<String> getDistinctServers(Collection<String> servers)
    {
        Map<String, String> map = new HashMap<String, String>();

        for (String server : servers)
        {
            try
            {
                // get the address of the server
                InetAddress address = InetAddress.getByName(server);

                // check if the server is missing from the map, or registered under a numerical form
                String hostname = map.get(address.getHostAddress());
                if (hostname == null || hostname.equals(address.getHostAddress()))
                {
                    map.put(address.getHostAddress(), server);
                }
            }
            catch (UnknownHostException e)
            {
                // ignored
            }
        }

        return new HashSet<String>(map.values());
    }
}
