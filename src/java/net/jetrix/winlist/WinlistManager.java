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

package net.jetrix.winlist;

import java.util.*;
import java.util.logging.*;

import net.jetrix.config.*;

/**
 * Winlist manager.
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class WinlistManager
{
    private static WinlistManager instance = new WinlistManager();
    private Map winlists;

    private Logger log = Logger.getLogger("net.jetrix");

    private WinlistManager()
    {
        winlists = new TreeMap();
    }

    public static WinlistManager getInstance()
    {
        return instance;
    }

    public void addWinlist(Winlist winlist)
    {
        if (winlist != null && winlist.getId() != null)
        {
            winlists.put(winlist.getId(), winlist);
            log.fine("registered winlist " + winlist.getId());
        }
    }

    public void addWinlist(WinlistConfig config)
    {
        if (config != null && config.getName() != null && config.getClassname() != null)
        {
            try
            {
                Class cls = Class.forName(config.getClassname());
                Winlist winlist = (Winlist) cls.newInstance();
                winlist.setId(config.getName());
                winlist.init(config);

                addWinlist(winlist);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public Winlist getWinlist(String id)
    {
        return (id == null) ? null : (Winlist) winlists.get(id);
    }
}
