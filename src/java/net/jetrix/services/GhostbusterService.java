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

package net.jetrix.services;

import net.jetrix.Client;
import net.jetrix.ClientRepository;
import net.jetrix.Message;
import net.jetrix.messages.NoopMessage;

import java.util.logging.*;

/**
 * A service to remove players not properly disconnected from the server due to
 * a network issue.
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class GhostbusterService extends ScheduledService
{
    private Logger log = Logger.getLogger("net.jetrix");

    public String getName()
    {
        return "Ghostbuster - ghost clients killer";
    }

    protected void init()
    {
        setDelay(10000);
        setPeriod(10000);
    }

    public void run()
    {
        // get the list of clients connected
        for (Client client : ClientRepository.getInstance().getClients())
        {
            if (client.getIdleTime() > 5000)
            {
                log.finest("checking connection for " + client.getUser().getName());

                try
                {
                    Message noop = new NoopMessage();
                    noop.setDestination(client);
                    client.send(noop);
                }
                catch (Exception e)
                {
                    log.info(e.getMessage());
                }
            }
        }
    }
}
