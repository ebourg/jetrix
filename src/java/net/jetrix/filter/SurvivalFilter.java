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

package net.jetrix.filter;

import java.util.*;

import net.jetrix.messages.OneLineAddedMessage;
import net.jetrix.messages.TwoLinesAddedMessage;
import net.jetrix.messages.FourLinesAddedMessage;
import net.jetrix.messages.ClearLineMessage;
import net.jetrix.Message;

/**
 * Survival mode. Every time the player clears 2 or more lines, he received
 * extra clear lines. This mode is usually played with an immediate sudden
 * death (1 line every 5 seconds).
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class SurvivalFilter extends GenericFilter
{
    public void onMessage(OneLineAddedMessage m, List<Message> out)
    {
        out.add(m);

        if (m.getFromSlot() != 0)
        {
            ClearLineMessage clear = new ClearLineMessage();
            clear.setSlot(m.getFromSlot());

            out.add(clear);
        }
    }

    public void onMessage(TwoLinesAddedMessage m, List<Message> out)
    {
        out.add(m);

        if (m.getFromSlot() != 0)
        {
            ClearLineMessage clear = new ClearLineMessage();
            clear.setSlot(m.getFromSlot());

            out.add(clear);
            out.add(clear);
        }
    }

    public void onMessage(FourLinesAddedMessage m, List<Message> out)
    {
        out.add(m);

        if (m.getFromSlot() != 0)
        {
            ClearLineMessage clear = new ClearLineMessage();
            clear.setSlot(m.getFromSlot());

            out.add(clear);
            out.add(clear);
            out.add(clear);
            out.add(clear);
        }
    }
}
