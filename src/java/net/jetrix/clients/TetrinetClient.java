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

package net.jetrix.clients;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import net.jetrix.*;
import net.jetrix.config.*;
import net.jetrix.messages.*;
import net.jetrix.protocols.*;

/**
 * Layer handling communication with a client. Incomming messages are turned
 * into a server understandable format and forwarded to the apropriate
 * destination for processing (player's channel or main server thread)
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TetrinetClient extends Client
{
    private int type;
    private String version;
    private Protocol protocol;

    // client types
    public static final int CLIENT_TETRINET  = 0;
    public static final int CLIENT_TETRIFAST = 1;
    public static final int CLIENT_TSPEC     = 2;

    private Channel channel;
    private Server server;
    private Player player;

    private boolean disconnected;
    private Logger logger = Logger.getLogger("net.jetrix");

    public TetrinetClient()
    {
        //this.protocol = new TetrinetProtocol();
    }

    public TetrinetClient(Player player, Socket socket)
    {
        this();
        setSocket(socket);
        this.player = player;
    }

    /**
     * Return the protocol used by this client.
     */
    public Protocol getProtocol()
    {
        return this.protocol;
    }

    /**
     * Set the protocol.
     */
    public void setProtocol(Protocol protocol)
    {
        this.protocol = protocol;
    }

    /**
     * Read a line sent by the tetrinet client.
     *
     * @return line sent
     */
    public String readLine() throws IOException
    {
        int readChar;
        Reader in = getReader();
        StringBuffer input = new StringBuffer();

        while ((readChar = in.read()) != -1 && readChar != 255)
        {
            if (readChar != 10 && readChar != 13)
            {
                input.append((char)readChar);
            }
        }

        if (readChar == -1) throw new IOException("client disconnected");

        return input.toString();
    }

}
