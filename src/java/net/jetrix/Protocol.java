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

package net.jetrix;

import java.util.*;

/**
 * A protocol to communicate with a client.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public abstract class Protocol
{
    private Locale locale;

    /**
     * Return the name of this protocol
     */
    public abstract String getName();

    /**
     * Parse the specified string and return the corresponding server
     * message for this protocol.
     */
    public abstract Message getMessage(String line);

    /**
     * Translate the specified message into a string that will be sent
     * to a client using this protocol.
     */
    public abstract String translate(Message m);

    /**
     * Set the locale to be used for textual messages.
     */
    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    /**
     * Return the locale.
     */
    public Locale getLocale()
    {
        return locale;
    }

    /**
     * Return the map of the color and style codes for this protocol.
     */
    public abstract Map getStyles();

    /**
     * Transform the style tags (<blue>, <u>, etc...) in the specified string
     * into the style codes of this protocol.
     */
    public String applyStyle(String text)
    {
        // to be optimized later
        Map styles = getStyles();
        Iterator keys = styles.keySet().iterator();
        while (keys.hasNext())
        {
            String key = (String)keys.next();
            String value = (String)styles.get(key);
            if (value == null) { value = ""; }
            text = text.replaceAll("<" + key + ">", value);
            text = text.replaceAll("</" + key + ">", value);
        }
        return text;
    }

}
