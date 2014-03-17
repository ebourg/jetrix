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

package net.jetrix.agent;

import net.jetrix.Message;

import java.io.IOException;

/**
 * An agent is a client able to connect to a server.
 *
 * @since 0.2
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public interface Agent
{
    /**
     * Connect to the specified server.
     *
     * @param hostname the name or ip of the server
     */
    void connect(String hostname) throws IOException;

    /**
     * Disconnect from the server.
     */
    void disconnect() throws IOException;

    /**
     * Send a message to the server.
     *
     * @param message the message to send
     */
    void send(Message message) throws IOException;

    /**
     * Receive a message sent by the server.
     * 
     * @param message the message received
     * @throws IOException
     */
    void receive(Message message) throws IOException;
}
