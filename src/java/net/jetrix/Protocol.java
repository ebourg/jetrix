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
 * A protocol to communicate with a client. A protocol is responsible for
 * transforming the messages in string format comming for a client into the
 * corresponding server {@link net.jetrix.Message}, as well as performing the
 * reverse operation.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public interface Protocol
{
    /**
     * Return the name of this protocol.
     *
     * @return the name of this protocol
     */
    String getName();

    /**
     * Parse the specified string and return the corresponding server
     * message for this protocol.
     *
     * @param message the client message to parse
     *
     * @return the {@link net.jetrix.Message} equivalent of the specified
     *     String or null if the protocol cannot understand the message.
     */
    Message getMessage(String message);

    /**
     * Translate the specified message into a string that will be sent
     * to a client using this protocol.
     *
     * @param m the message to translate
     * @param locale the locale used for internationalized text messages
     *
     * @return the String equivalent in this protocol for the specified
     *     {@link net.jetrix.Message} or null if it can't be translated.
     */
    String translate(Message m, Locale locale);

    /**
     * Transform the style tags (<tt>&lt;blue&gt;</tt>, <tt>&lt;u&gt;</tt>,
     * etc...) contained in the specified string into the style codes of this
     * protocol.
     *
     * @param text the string to transform
     *
     * @return the stylized representation of the specified string for
     *     this protocol.
     */
    String applyStyle(String text);

}
