/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2005  Emmanuel Bourg
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

import java.net.*;
import java.util.*;

import net.jetrix.*;
import net.jetrix.clients.*;
import net.jetrix.messages.*;
import net.jetrix.messages.channel.*;
import net.jetrix.protocols.*;

/**
 * Listener for tetrinet and tetrifast clients.
 *
 * @author Emmanuel Bourg
 */
public class TetrinetListener extends ClientListener
{
    private ProtocolManager protocolManager = ProtocolManager.getInstance();

    public TetrinetListener()
    {
        port = 31457;
    }

    public String getName()
    {
        return "tetrinet & tetrifast";
    }

    public Client getClient(Socket socket) throws Exception
    {
        // read the first line sent by the client
        Protocol protocol1 = protocolManager.getProtocol(TetrinetProtocol.class);
        String init = protocol1.readLine(socket.getInputStream(), "Cp1252");

        // test if the client is using the query protocol
        Protocol protocol = protocolManager.getProtocol(QueryProtocol.class);
        Message message = protocol.getMessage(init);

        if (message != null)
        {
            QueryClient client = new QueryClient();
            client.setProtocol(protocol);
            client.setSocket(socket);
            client.setUser(new User("query"));
            client.setFirstMessage(message);
            return client;
        }

        String dec = TetrinetProtocol.decode(init);

        // init string parsing "tetrisstart <nickname> <version>"
        StringTokenizer st = new StringTokenizer(dec, " ");
        List<String> tokens = new ArrayList<String>();

        while (st.hasMoreTokens())
        {
            tokens.add(st.nextToken());
        }

        TetrinetClient client = new TetrinetClient();
        User user = new User();
        user.setName(tokens.get(1));

        client.setUser(user);
        client.setVersion(tokens.get(2));
        if ((tokens.get(0)).equals(TetrinetProtocol.INIT_TOKEN))
        {
            client.setProtocol(protocolManager.getProtocol(TetrinetProtocol.class));
            client.setAgent("TetriNET");
        }
        else if ((tokens.get(0)).equals(TetrifastProtocol.INIT_TOKEN))
        {
            client.setProtocol(protocolManager.getProtocol(TetrifastProtocol.class));
            client.setAgent("TetriFast");
        }
        else
        {
            return null;
        }

        client.setSocket(socket);

        if (tokens.size() > 3)
        {
            Message m = new NoConnectingMessage("No space allowed in nickname !");
            client.send(m);
            return null;
        }

        // send the client identification request
        client.send(new LevelMessage());

        return client;
    }

}
