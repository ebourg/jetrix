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

package net.jetrix.filter;

import java.util.*;

import net.jetrix.*;
import net.jetrix.config.*;
import net.jetrix.messages.*;

/**
 * Blocks spam over pline.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class FloodFilter extends GenericFilter
{
    private long timestamp[][];
    private int index[];
    private int capacity = 8;
    private int delay = 5000;

    /** Minimum time between two warnings (in seconds) */
    private int warningPeriod = 10;

    /** Date of the last warning */
    private long lastWarning;

    public void init(FilterConfig conf)
    {
        // reading parameters
        capacity = conf.getInt("capacity", capacity);
        delay = conf.getInt("delay", delay);
        warningPeriod = conf.getInt("warningPeriod", warningPeriod);

        timestamp = new long[6][capacity];
        index = new int[6];
    }

    public void onMessage(PlineMessage m, List out)
    {
        int slot = m.getSlot();
        // no check for server messages
        if (slot < 1 || slot > 6)
        {
            out.add(m);
            return;
        }

        String text = m.getText();
        float charsByLine = 70;
        int lineCount = (int) Math.ceil(text.length() / charsByLine);

        long now = System.currentTimeMillis();
        boolean isRateExceeded = false;
        for (int i = 0; i < lineCount; i++)
        {
            isRateExceeded = isRateExceeded || isRateExceeded(slot - 1, now);
        }

        if (slot > 0 && isRateExceeded)
        {
            if ((now - lastWarning) > warningPeriod * 1000)
            {
                User user = getChannel().getPlayer(slot);
                PlineMessage warning = new PlineMessage();
                warning.setKey("filter.flood.blocked", user.getName());
                out.add(warning);
                lastWarning = now;
            }
        }
        else
        {
            out.add(m);
        }
    }

    /**
     * Records a message timestamp and checks the data rate.
     *
     * @param slot  message source slot
     * @param t     message timestamp
     *
     * @return <tt>true</tt> if over <tt>capacity</tt> messages in less than the <tt>delay</tt> specified
     */
    private boolean isRateExceeded(int slot, long t)
    {
        long t1 = timestamp[slot][index[slot]];
        timestamp[slot][index[slot]] = t;
        index[slot] = (index[slot] + 1) % capacity;

        return (t - t1) < delay;
    }

    public String getName()
    {
        return "Flood Filter";
    }

    public String getDescription()
    {
        return "Blocks exceeding messages on pline";
    }

    public String getVersion()
    {
        return "1.0";
    }

    public String getAuthor()
    {
        return "Emmanuel Bourg";
    }

}
