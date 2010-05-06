/**
 * Jetrix TetriNET Server
 * Copyright (C) 2010  Emmanuel Bourg
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

package net.jetrix.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.jetrix.Client;
import net.jetrix.Message;
import net.jetrix.messages.channel.PlineMessage;
import net.jetrix.messages.channel.StartGameMessage;
import net.jetrix.messages.channel.GmsgMessage;
import net.jetrix.messages.channel.TextMessage;
import net.jetrix.protocols.TetrifastProtocol;
import org.apache.commons.lang.StringUtils;

/**
 * Filter displaying a warning at the beginning of the game if everyone
 * isn't playing with the same piece delay.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 * @since 0.3
 */
public class UnbalancedSpeedWarningFilter extends GenericFilter
{
    public void onMessage(StartGameMessage m, List<Message> out)
    {
        Iterator<Client> players = getChannel().getPlayers();
        
        // collect the name of the TetriFast and TetriNET players
        List<String> fastClients = new ArrayList<String>();
        List<String> normalClients = new ArrayList<String>();
        while (players.hasNext())
        {
            Client client = players.next();
            if (client != null && client.getUser().isPlayer())
            {
                if (client.getProtocol() instanceof TetrifastProtocol)
                {
                    fastClients.add(client.getUser().getName());
                }
                else
                {
                    normalClients.add(client.getUser().getName());
                }
            }
        }
        
        if (!normalClients.isEmpty() && !fastClients.isEmpty())
        {
            TextMessage warning = createWarningMessage(normalClients, fastClients);
            out.add(warning);
            
            out.add(m);

            GmsgMessage gmsg = new GmsgMessage();
            gmsg.setKey(warning.getKey());
            gmsg.setParams(warning.getParams());
            out.add(gmsg);
        }
        else
        {
            out.add(m);
        }
    }

    private TextMessage createWarningMessage(List<String> normalClients, List<String> fastClients)
    {
        String type;
        List<String> clients;
        if (normalClients.size() < fastClients.size())
        {
            type = "normal";
            clients = normalClients;
        }
        else
        {
            type = "fast";
            clients = fastClients;
        }
        
        PlineMessage message = new PlineMessage();
        if (clients.size() == 1)
        {
            message.setKey("filter.unbalanced_speed." + type + ".one", clients.get(0));
        }
        else
        {
            List<String> firstClients = clients.subList(0, clients.size() - 1);
            String lastClient = clients.get(clients.size() - 1); 
            System.out.println("first: " + firstClients);
            System.out.println("last: " + lastClient);
            
            message.setKey("filter.unbalanced_speed." + type + ".many", StringUtils.join(firstClients.toArray(), ", "), lastClient);
        }
        
        return message;
    }

    public String getName()
    {
        return "Unbalanced speed warning";
    }

    public String getDescription()
    {
        return "Displays a warning at the beginning of the game if everyone isn't playing with the same piece delay";
    }

    public String getVersion()
    {
        return "1.0";
    }

    public String getAuthor()
    {
        return "Emmanuel Bourg";
    }
}
