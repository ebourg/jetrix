/**
 * Java TetriNET Server
 * Copyright (C) 2001  Emmanuel Bourg
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

package org.lfjr.jts;

import java.util.*;


/**
 * Flood filter. Controls the data rate of a message flow. A flood filter
 * is defined by a maximum number a messages sent over a given time frame.
 * 
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class FloodFilter
{
    private int capacity;
    private int delay;
    private long fifo[];

    private int index;


    /**
     * Creates a new filter.
     *
     *
     * @param capacity maximum number of messages allowed in the time frame
     * @param delay    time frame
     *
     */
    public FloodFilter(int capacity, int delay)
    {
        fifo = new long[capacity];
        this.capacity = capacity;
        this.delay = delay;
    }


    /**
     * Records a new message timestamp and checks the data rate.
     *
     *
     * @param d message timestamp
     *
     * @return <tt>true</tt> if over <tt>capacity</tt> messages in less than the <tt>delay</tt> specified
     *
     */
    public boolean isRateExceeded(Date d)
    {
        long t = d.getTime();

        long t1 = fifo[index];

        fifo[index] = t;

        index = (index + 1) % capacity;

        return (t - t1) < delay;
    }

}

