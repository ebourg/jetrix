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

package net.jetrix.messages;

import net.jetrix.*;

/**
 * Message generated when the /move command is issued. On receiving this message
 * the channel will switch the two specified slots.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class PlayerSwitchMessage extends Message
{
    private int slot1;
    private int slot2;

    public int getSlot1()
    {
        return slot1;
    }

    public void setSlot1(int slot1)
    {
        this.slot1 = slot1;
    }

    public int getSlot2()
    {
        return slot2;
    }

    public void setSlot2(int slot2)
    {
        this.slot2 = slot2;
    }
}
