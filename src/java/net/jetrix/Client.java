/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2004  Emmanuel Bourg
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

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Layer handling communication with a client. Incomming messages are turned
 * into a server understandable format and forwarded to the apropriate
 * destination for processing (player's channel or main server thread)
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public interface Client extends Runnable, Destination
{
    /**
     * Return the protocol used by this client.
     */
    Protocol getProtocol();

    /**
     * Return the channel this client subscribed to.
     */
    Channel getChannel();

    /**
     * Set the channel.
     */
    void setChannel(Channel channel);

    /**
     * Tell if the client supports multiple channels simultaneously.
     *
     * @since 0.1.4
     */
    boolean supportsMultipleChannels();

    /**
     * Tell if the client can be affected automatically to a channel
     * on connecting to the server.
     *
     * @since 0.1.4
     */
    boolean supportsAutoJoin();

    /**
     * Return the user associated to this client.
     */
    User getUser();

    /**
     * Return the type of the client.
     */
    String getType();

    /**
     * Return the version number of this client.
     */
    String getVersion();

    /**
     * Return the Internet address of this client.
     */
    InetAddress getInetAddress();

    /**
     * Return the time of the connection to the server.
     */
    Date getConnectionTime();

    /**
     * Send a message to the client. The raw message property must be set.
     *
     * @param m message to send
     */
    void send(Message m);

    /**
     * Receive a message sent by the client.
     */
    Message receiveMessage() throws IOException;

    /**
     * Trigger the disconnection of this client.
     */
    void disconnect();

}
