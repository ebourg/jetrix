/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2002  Emmanuel Bourg
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

package org.lfjr.jts.filter;

import java.util.*;
import org.lfjr.jts.*;
import org.lfjr.jts.config.*;

/**
 * Anti spam over pline filter
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class FloodFilter extends GenericFilter
{
    private long timestamp[][];
    private int index[];
    private int capacity = 5;
    private int delay = 1000;
    
    public void init(FilterConfig conf)
    {
        // reading parameters
        try { this.capacity = Integer.parseInt(conf.getParameter("capacity")); } catch (Exception e) {}
        try { this.delay = Integer.parseInt(conf.getParameter("delay")); } catch (Exception e) {}
        
        timestamp = new long[6][capacity];
        index = new int[6];
    }
    
    public void onPline(Message m, List out)
    {
        int slot = ((Integer)m.getParameter(1)).intValue();
        if (slot>0 && isRateExceeded(slot-1, new Date())) {
            Message warning = new Message(Message.MSG_PLINE, new Object [] 
                              { new Integer(0), ChatColors.red + "Flood blocked from slot " + slot });
            out.add(warning);
        } else {
            out.add(m);
        }
    }

    /**
     * Records a message timestamp and checks the data rate.
     *
     * @param slot      message source slot
     * @param d         message timestamp
     *
     * @return <tt>true</tt> if over <tt>capacity</tt> messages in less than the <tt>delay</tt> specified
     */
    private boolean isRateExceeded(int slot, Date d)
    {
        long t = d.getTime();
        long t1 = timestamp[slot][index[slot]];
        timestamp[slot][index[slot]] = t;
        index[slot] = (index[slot] + 1) % capacity;
        
        return (t - t1) < delay;
    }

    public static String getName() { return "Flood Filter"; }

    public static String getDescription() { return "Blocks exceeding messages on pline"; }

    public static String getVersion() { return "1.0"; }

    public static String getAuthor() { return "Emmanuel Bourg"; }

}
