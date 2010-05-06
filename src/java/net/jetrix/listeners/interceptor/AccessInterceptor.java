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

import net.jetrix.ChannelManager;
import net.jetrix.Client;
import net.jetrix.ClientRepository;
import net.jetrix.Server;
import net.jetrix.clients.QueryClient;
import net.jetrix.config.ServerConfig;
import net.jetrix.messages.NoConnectingMessage;

import java.net.InetAddress;
import java.util.logging.Logger;

/**
 * Interceptor checking the access to the server. The client will be rejected
 * if the server is full, locked, or if it has exceeded the maximum number of
 * concurrent connections.
 *
 * @since 0.3
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class AccessInterceptor implements ClientInterceptor
{
    private Logger log = Logger.getLogger("net.jetrix");

    public boolean isValidating()
    {
        return true;
    }

    public void process(Client client) throws ClientValidationException
    {
        ServerConfig serverConfig = Server.getInstance().getConfig();
        InetAddress address = client.getInetAddress();
        
        // check if the server is locked
        if (serverConfig.getStatus() == ServerConfig.STATUS_LOCKED && !(client instanceof QueryClient))
        {
            log.info("Server locked, client rejected (" + address + ").");
            client.send(new NoConnectingMessage("The server is locked."));
            client.disconnect();
            throw new ClientValidationException();
        }
        
        // check if the server is full
        ClientRepository repository = ClientRepository.getInstance();
        if (repository.getClientCount() >= serverConfig.getMaxPlayers()
                && !(client instanceof QueryClient))
        {
            log.info("Server full, client rejected (" + address + ").");
            client.send(new NoConnectingMessage("Server is full!"));
            client.disconnect();
            throw new ClientValidationException();
        }
        
        // test concurrent connections from the same host
        // todo include pending connections
        int maxConnections = serverConfig.getMaxConnections();
        if (maxConnections > 0 && repository.getHostCount(address) >= maxConnections)
        {
            log.info("Too many connections from the same host, client rejected (" + address + ").");
            client.send(new NoConnectingMessage("Too many connections from your host!"));
            client.disconnect();
            throw new ClientValidationException();
        }
        
        // look for compatible channels
        String protocol = client.getProtocol().getName();
        if (!(client instanceof QueryClient)
                && client.getUser().isPlayer()
                && !ChannelManager.getInstance().hasCompatibleChannels(protocol))
        {
            String type = protocol.equals("tetrinet") ? "TetriNET" : "TetriFast";
            log.info("No " + type + " channels available, client rejected (" + address + ").");
            client.send(new NoConnectingMessage("Sorry there are no " + type + " channels available."));
            client.disconnect();
            throw new ClientValidationException();
        }
    }
}
