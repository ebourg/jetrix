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

package net.jetrix.messages.channel.specials;

import net.jetrix.messages.channel.ChannelMessage;

/**
 * A channel message for special blocks. Specials are attacks applied to
 * another game slot. For specials the first slot is the target of the
 * attack.
 *
 * @author Emmanuel Bourg
 */
public abstract class SpecialMessage extends ChannelMessage
{
    private int fromSlot;
    private String special;

    public int getFromSlot()
    {
        return fromSlot;
    }

    public void setFromSlot(int fromSlot)
    {
        this.fromSlot = fromSlot;
    }
}
