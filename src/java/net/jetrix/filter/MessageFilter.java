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

/**
 * Abstract class defining a channel filter. A filter transforms a given message
 * into a list of messages. Concrete filters just need to inherit from this
 * class and implement the process() method.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public abstract class MessageFilter
{
    private Properties props;
    private Channel channel;

    /**
     * Indicates if the filter is a slingleton or not. A filter should be
     * a singleton if it's independant from the channel context (for example:
     * a color stripper or a profanity filter). By default a filter is not
     * a singleton. This method must be overwritten to return true if the
     * filter is meant to be instanciated only once.
     *
     * @return <tt>false</tt>
     */
    public static boolean isSingleton()
    {
        return false;
    }

    /**
     * Called by the channel to indicate to a filter that the filter is being placed into service.
     */
    public void init(FilterConfig conf) { }

    /**
     * Called by the channel to indicate to a filter that the filter is being taken out of service.
     */
    public void destroy()
    {
    }

    /**
     * Process a message and outputs messages to the specified List.
     */
    public abstract void process(Message m, List out);

    /**
     * Returns the name of this filter.
     */
    public String getName()
    {
        return "unknown filter";
    }

    /**
     * Returns a short description of this filter.
     */
    public String getDescription()
    {
        return "no description";
    }

    /**
     * Returns the version of this filter
     */
    public String getVersion()
    {
        return "1.0";
    }

    /**
     * Returns the author of this filter.
     */
    public String getAuthor()
    {
        return "unknown";
    }

    /**
     * Gets the filter property indicated by the specified key.
     */
    public final String getProperty(String key)
    {
        return (props == null) ? null : props.getProperty(key);
    }

    /**
     * Sets the filter property indicated by the specified key.
     */
    public final void setProperty(String key, String value)
    {
        if (props == null)
        {
            props = new Properties();
        }
        props.setProperty(key, value);
    }

    /**
     * Returns the channel this filter applies on.
     */
    public final Channel getChannel()
    {
        return this.channel;
    }

    /**
     * Sets the channel this filter applies on.
     */
    public final void setChannel(Channel channel)
    {
        if (!isSingleton())
        {
            this.channel = channel;
        }
    }
}
