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
import net.jetrix.Message;
import net.jetrix.User;
import net.jetrix.clients.QueryClient;
import net.jetrix.messages.NoConnectingMessage;

/**
 * Interceptor checking the validity of the name. The client will be rejected
 * if the name is already in use, empty or equals to "Server".
 *
 * @since 0.3
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class NameCheckInterceptor implements ClientInterceptor
{
    public boolean isValidating()
    {
        return true;
    }

    public void process(Client client) throws ClientValidationException
    {
        User user = client.getUser();

        // test the name unicity
        ClientRepository repository = ClientRepository.getInstance();
        if (repository.getClient(user.getName()) != null)
        {
            Message m = new NoConnectingMessage("Nickname already in use!");
            client.send(m);
            client.disconnect();
            throw new ClientValidationException();
        }

        // validate the name
        String name = user.getName();
        if (!(client instanceof QueryClient) && (name == null || "server".equalsIgnoreCase(name) || name.indexOf("\u00a0") != -1))
        {
            Message m = new NoConnectingMessage("Invalid name!");
            client.send(m);
            client.disconnect();
            throw new ClientValidationException();
        }
    }
}
