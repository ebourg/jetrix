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

package net.jetrix;

import java.util.*;

/**
 * Internal message sent between server, channels and client handlers.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public abstract class Message
{
    private Destination source;
    private Date date;
    private Map rawMessages;

    /**
     * Constructs a new server message.
     */
    public Message()
    {
        this.date = new Date();
        rawMessages = new HashMap();
    }

    /**
     * Return the source of this message. The source is a Destination accepting
     * replies to this message.
     */
    public Destination getSource()
    {
        return source;
    }

    /**
     * Set the source of this message.
     */
    public void setSource(Destination source)
    {
        this.source = source;
    }

    /**
     * Returns the creation date of this message.
     */
    public Date getDate()
    {
        return date;
    }

    /**
     * Set the view of the message in the specified protocol.
     */
    public void setRawMessage(Protocol protocol, String message)
    {
        rawMessages.put(protocol, message);
    }

    /**
     * Return the view of this message in the specified protocol.
     */
    public String getRawMessage(Protocol protocol, Locale locale)
    {
        // is the raw message available for the specified protocol
        String msg = (String)rawMessages.get(protocol);

        if (msg == null)
        {
            // building the raw message for this protocol
            msg = protocol.translate(this, locale);
            rawMessages.put(protocol, msg);
        }

        return msg;
    }

}
