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

/**
 * Anti spam over pline filter
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class FloodFilter extends AbstractFilter
{
    private long timestamp[][];
    private int index[];
    private int capacity = 5;
    private int delay = 1000;
    
    public FloodFilter()
    {
    	timestamp = new long[6][capacity];
    	index = new int[6];
    }
    
    public void process(Message m, List out)
    {
        if (m.getType()==Message.TYPE_CHANNEL && m.getCode()==Message.MSG_PLINE) {
            int slot = ((Integer)m.getParameter(1)).intValue();
            if (slot>0 && isRateExceeded(slot-1, new Date())) {
            	Message warning = new Message();
            	Integer from = new Integer(0);
            	String text = ChatColors.red+"Flood blocked from slot "+slot;
                Object[] params = { from, text };
                warning.setParameters(params);
            	out.add(warning);
            } else {
                out.add(m);
            }
        } else {
            out.add(m);
        }
    }

    /**
     * Records a message timestamp and checks the data rate.
     *
     * @param slot	message source slot
     * @param d	message timestamp
     *
     * @return <tt>true</tt> if over <tt>capacity</tt> messages in less than the <tt>delay</tt> specified
     */
    public boolean isRateExceeded(int slot, Date d)
    {
        long t = d.getTime();
        long t1 = timestamp[slot][index[slot]];
        timestamp[slot][index[slot]] = t;
        index[slot] = (index[slot] + 1) % capacity;
        
        return (t - t1) < delay;
    }

    public String getDisplayName() { return "Flood Filter"; }

    public String getShortDescription() { return "Blocks exceeding messages on pline"; }

    public String getVersion() { return "1.0"; }

    public String getAuthor() { return "Emmanuel Bourg"; }

}