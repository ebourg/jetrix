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

package net.jetrix.listeners.interceptor;

import net.jetrix.Client;
import net.jetrix.ClientRepository;
import net.jetrix.clients.QueryClient;
import net.jetrix.messages.channel.PlineMessage;

/**
 * Interceptor displaying the number of players and spectators currently online.
 *
 * @since 0.3
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class OnlineUsersInterceptor implements ClientInterceptor
{
    public boolean isValidating()
    {
        return false;
    }

    public void process(Client client)
    {
        if (!(client instanceof QueryClient))
        {
            // display the number of players online
            int playerCount = ClientRepository.getInstance().getPlayerCount();
            int spectatorCount = ClientRepository.getInstance().getSpectatorCount();

            PlineMessage online = new PlineMessage();
            String key = playerCount + spectatorCount > 1 ? "server.users-online" : "server.user-online";
            String pkey = playerCount > 1 ? "key:common.players" : "key:common.player";
            String skey = spectatorCount > 1 ? "key:common.spectators" : "key:common.spectator";
            online.setKey(key, playerCount, pkey, spectatorCount, skey);

            client.send(online);
        }
    }
}
