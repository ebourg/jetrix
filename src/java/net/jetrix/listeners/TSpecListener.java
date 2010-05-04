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

package net.jetrix.listeners;

import java.net.*;
import net.jetrix.*;
import net.jetrix.protocols.TspecProtocol;
import net.jetrix.protocols.TetrinetProtocol;
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
        TetrinetProtocol protocol = ProtocolManager.getInstance().getProtocol(TetrinetProtocol.class);
        String init = protocol.readLine(socket.getInputStream(), "Cp1252");

        TSpecClient client = new TSpecClient();
        client.setProtocol(ProtocolManager.getInstance().getProtocol(TspecProtocol.class));
        client.setSocket(socket);

        User user = new User();
        user.setSpectator();
        client.setUser(user);

        // test if the client is using the tspec protocol (<nickname> <encoded password>)
        if (init.contains(" "))
        {
            client.setMode(TSpecClient.TSERV_MODE);
            user.setName(init.substring(0, init.indexOf(" ")));
        }
        else
        {
            client.setMode(TSpecClient.TETRIX_MODE);
            String dec = TetrinetProtocol.decode(init);

            // init string parsing "tetristart <nickname> <version>"
            String[] tokens = dec.split(" ");

            if (tokens.length > 3)
            {
                Message m = new NoConnectingMessage("No space allowed in nickname !");
                client.send(m);
                return null;
            }

            user.setName(tokens[1]);
        }
        
        client.setAgent("TSpec");
        client.setVersion("1.13");        
        
        return client;
    }
}
