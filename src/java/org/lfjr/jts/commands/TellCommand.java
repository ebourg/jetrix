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
 * Send a private message to a player.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TellCommand implements Command
{
    private int accessLevel = 0;

    public String[] getAliases()
    {
        return (new String[] { "tell", "msg", "cmsg", "send" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage()
    {
        return "/tell <playername|playernum> <message>";
    }

    public String getDescription()
    {
        return "Send a private message to a player.";
    }

    public void execute(Message m)
    {
        String cmd = m.getStringParameter(1);
        TetriNETClient client = (TetriNETClient)m.getSource();

        if (m.getParameterCount() > 3)
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
                Message reponse = new Message(Message.MSG_PLINE);
                String privateMessage = m.getRawMessage().substring(cmd.length() + targetName.length() + 10);
                String message = ChatColors.aqua + "{" + client.getPlayer().getName() + "} " + ChatColors.darkBlue + privateMessage;
                reponse.setParameters(new Object[] { new Integer(0), message });
                target.sendMessage(reponse);
            }
        }
        else
        {
            // not enough parameters
            Message response = new Message(Message.MSG_PLINE);
            String message = ChatColors.red + cmd + ChatColors.blue + " <playername|playernumber> <message>";
            response.setParameters(new Object[] { new Integer(0), message });
            client.sendMessage(response);
        }
    }

}
