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

package net.jetrix.listeners;

import java.io.*;
import java.net.*;
import java.util.*;
import net.jetrix.*;
import net.jetrix.clients.*;
import net.jetrix.messages.*;

/**
 * Listener for tspec clients.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TSpecListener extends TetrinetListener
{
    public TSpecListener()
    {
        port = 31458;
    }

    public String getName()
    {
        return "tspec";
    }

    public Client getClient(Socket socket) throws Exception
    {
        String init = null;

        try
        {
            init = readLine(socket);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        String dec = decode(init);

        // init string parsing "team <nickname> <password>"
        StringTokenizer st = new StringTokenizer(dec, " ");
        List tokens = new ArrayList();

        while (st.hasMoreTokens())
        {
            tokens.add(st.nextToken());
        }

        if (tokens.size() > 3)
        {
            return null;
        }

        TetrinetClient client = new TetrinetClient();
        User user = new User();
        user.setName((String) tokens.get(1));
        user.setSpectator();
        client.setSocket(socket);
        client.setUser(user);
        client.setProtocol(ProtocolManager.getInstance().getProtocol("net.jetrix.protocols.TspecProtocol"));

        if (tokens.size() > 3)
        {
            Message m = new NoConnectingMessage("No space allowed in nickname !");
            client.sendMessage(m);
            return null;
        }

        return client;
    }

}
