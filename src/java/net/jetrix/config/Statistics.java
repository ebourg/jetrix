/**
 * Jetrix TetriNET Server
 * Copyright (C) 2004  Emmanuel Bourg
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

package net.jetrix.config;

/**
 * Basic statistics about the server usage.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class Statistics
{
    private int gameCount;
    private int connectionCount;
    private long startTime;

    public Statistics()
    {
        startTime = System.currentTimeMillis();
    }

    /**
     * Return the uptime of the server (in days).
     */
    public long getUpTime()
    {
        long elaspsed = System.currentTimeMillis() - startTime;
        return elaspsed / (24 * 3600 * 1000);
    }

    /**
     * Return the number of games played.
     */
    public int getGameCount()
    {
        return gameCount;
    }

    /**
     * Add 1 to the number of games played.
     */
    public synchronized void increaseGameCount()
    {
        gameCount++;
    }

    /**
     * Return the number of connections from spectators and players.
     */
    public int getConnectionCount()
    {
        return connectionCount;
    }

    /**
     * Add 1 to the number of connections.
     */
    public synchronized void increaseConnectionCount()
    {
        connectionCount++;
    }
}
