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
import java.util.regex.*;
import java.net.*;

/**
 * List of hosts banned from the server.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class Banlist
{
    private static Banlist instance = new Banlist();
    private Map banlist;

    private Banlist()
    {
        banlist = new HashMap();
    }

    public static Banlist getInstance()
    {
        return instance;
    }

    public void ban(String host)
    {
        // replace . by \., * by .* and ? by .
        String regexp = host.replaceAll("\\.", "\\\\.").replaceAll("\\*", "(.*)").replaceAll("\\?", ".");
        Pattern pattern = Pattern.compile(regexp);
        banlist.put(host, pattern);
    }

    public void unban(String host)
    {
        banlist.remove(host);
    }

    public boolean isBanned(String host)
    {
        boolean banned = false;

        Iterator it = banlist.values().iterator();
        while (it.hasNext() && !banned)
        {
            Pattern pattern = (Pattern) it.next();
            banned = pattern.matcher(host).matches();
        }

        return banned;
    }

    public boolean isBanned(InetAddress address)
    {
        return isBanned(address.getHostAddress()) || isBanned(address.getHostName());
    }

    public Iterator getBanlist()
    {
        return banlist.keySet().iterator();
    }

    /**
     * Remove all entries in the banlist.
     */
    public void clear()
    {
        banlist.clear();
    }

}
