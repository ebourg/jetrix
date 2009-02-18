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

package net.jetrix.protocols;

import net.jetrix.messages.channel.*;

/**
 * Protocol to communicate with Tetrifast compatible clients.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TetrifastProtocol extends TetrinetProtocol
{
    /** Initialization token */
    public static final String INIT_TOKEN = "tetrifaster";

    public String getName()
    {
        return "tetrifast";
    }

    public String translate(PlayerNumMessage m)
    {
        StringBuilder message = new StringBuilder();
        message.append(")#)(!@(*3 ");
        message.append(m.getSlot());
        return message.toString();
    }

    public String translate(NewGameMessage m)
    {
        StringBuilder message = new StringBuilder();
        message.append("*******");
        message.append(super.translate(m).substring(7));
        return message.toString();
    }
}
