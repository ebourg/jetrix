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
 * A field change message.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class FieldMessage extends ChannelMessage
{
    private String field;

    public FieldMessage() { }

    public FieldMessage(String field)
    {
        this(0, field);
    }

    public FieldMessage(int slot, String field)
    {
        setSlot(slot);
        setField(field);
    }

    public String getField()
    {
        return field;
    }

    public void setField(String field)
    {
        this.field = field;
    }

}
