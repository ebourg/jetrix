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
 * A filter computing and displaying the number of pieces dropped per minute
 * by each player.
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class PPMFilter extends GenericFilter
{
    private long totalTime;
    private long lastStop;
    private int blockCount[];
    private long slotTime[];

    
    public void init(FilterConfig conf)
    {
        blockCount = new int[6];
        slotTime = new long[6];
    }
    
    public void onStartGame(Message m, List out)
    {
        totalTime = 0;
        lastStop = new Date().getTime();
        Arrays.fill(blockCount, 0);
        Arrays.fill(slotTime, 0);
        
        out.add(m);
    }
    
    public void onEndGame(Message m, List out)
    {
        // updating play time for each slot
        long now = new Date().getTime();
        for (int slot=0; slot<6; slot++) { slotTime[slot] = slotTime[slot] + (now - lastStop); }
        
        // displaying results
        // ...
        
        out.add(m);
    }
    
    public void onPause(Message m, List out)
    {
        // updating global play time
        long now = new Date().getTime();
        totalTime = totalTime + (now - lastStop);
        
        out.add(m); 
    }


    public void onResume(Message m, List out)
    {
        lastStop = new Date().getTime();
        
        out.add(m); 
    }    

    public void onField(Message m, List out)
    {
        // increasing block count for the updated slot
        int slot = m.getIntParameter(0);
        blockCount[slot]++;        
        
        out.add(m); 
    }
    

    public String getName() { return "PPM Filter"; }

    public String getDescription() { return "Counts pieces per minute"; }

    public String getVersion() { return "1.0"; }

    public String getAuthor() { return "Emmanuel Bourg"; }

}
