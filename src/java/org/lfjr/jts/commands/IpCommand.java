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

package org.lfjr.jts.commands;

import org.lfjr.jts.*;

/**
 * Display the IP of a player.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class IpCommand implements Command
{
    private int accessLevel = 1;

    public String[] getAliases()
    {
        return (new String[] { "ip" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage()
    {
        return "/ip <player name|number>";
    }

    public String getDescription()
    {
        return "Display the IP of a player.";
    }

    public void execute(Message m)
    {
        String cmd = m.getStringParameter(1);
        TetriNETClient client = (TetriNETClient)m.getSource();

        if (m.getParameterCount() > 2)
        {
            String targetName = m.getStringParameter(2);
            TetriNETClient target = null;

            // checking if the second parameter is a slot number
            try
            {
                int slot = Integer.parseInt(targetName);
                if (slot >= 1 && slot <= 6)
                {
                    Channel channel = client.getChannel();
                    target = channel.getPlayer(slot);
                }
            }
            catch (NumberFormatException e) {}

            if (target == null)
            {
                // target is still null, the second parameter is a playername
                ClientRepository repository = ClientRepository.getInstance();
                target = repository.getClient(targetName);
            }

            if (target == null)
            {
                // no player found
                Message reponse = new Message(Message.MSG_PLINE);
                String message = ChatColors.red + "Player " + targetName + " cannot be found on the server.";
                reponse.setParameters(new Object[] { new Integer(0), message });
                client.sendMessage(reponse);
            }
            else
            {
                // player found
                String hostname = target.getSocket().getInetAddress().getHostName();
                String hostaddress = target.getSocket().getInetAddress().getHostAddress();
                String message = ChatColors.darkBlue + "[" + ChatColors.red + client.getPlayer().getName()
                                 + ChatColors.red + "] " + hostname;
                if (!hostname.equals(hostaddress)) message += " (" + hostaddress + ")";
                Message reponse = new Message(Message.MSG_PLINE);
                reponse.setParameters(new Object[] { new Integer(0), message });
                client.sendMessage(reponse);
            }
        }
        else
        {
            // not enough parameters
            Message response = new Message(Message.MSG_PLINE);
            String message = ChatColors.red + cmd + ChatColors.blue + " <player name|number>";
            response.setParameters(new Object[] { new Integer(0), message });
            client.sendMessage(response);
        }
    }

}