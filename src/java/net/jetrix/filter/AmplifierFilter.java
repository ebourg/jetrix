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
import net.jetrix.messages.channel.specials.SpecialMessage;

/**
 * Amplifies specials sent. One line sent will turn into two lines, a tetris
 * into eight, etc... The multiplicating factor is set to 2 by default but
 * can be changed by adding a <tt>factor</tt> parameter to the FilterConfig.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class AmplifierFilter extends MessageFilter
{
    private int factor = 2;

    public void init()
    {
        // read the parameters
        factor = config.getInt("factor", factor);
    }

    public void process(Message m, List<Message> out)
    {
        if (m instanceof SpecialMessage)
        {
            for (int i = 0; i < factor; i++)
            {
                out.add(m);
            }
        }
    }

    public String getName()
    {
        return "Amplifier";
    }

    public String getDescription()
    {
        return "Amplifies specials by sending them twice or more.";
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
