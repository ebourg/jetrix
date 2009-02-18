/**
 * Jetrix TetriNET Server
 * Copyright (C) 2004  Emmanuel Bourg
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

package net.jetrix.commands;

import java.util.Locale;

import net.jetrix.messages.channel.CommandMessage;
import net.jetrix.messages.channel.PlineMessage;
import net.jetrix.Language;
import net.jetrix.User;
import net.jetrix.Client;

/**
 * Set the away status of a player.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 * @since 0.2
 */
public class AwayCommand extends AbstractCommand
{
    public String[] getAliases()
    {
        return new String[] { "away", "afk" };
    }

    public String getUsage(Locale locale)
    {
        return "/afk <" + Language.getText("command.params.message", locale) + ">";
    }

    public void execute(CommandMessage message)
    {
        Client client = (Client) message.getSource();
        User user = client.getUser();

        PlineMessage response = new PlineMessage();

        if (user.getStatus() == User.STATUS_AFK)
        {
            user.setStatus(User.STATUS_OK);
            response.setKey("command.away.off");
        }
        else
        {
            user.setStatus(User.STATUS_AFK);
            response.setKey("command.away.on");

            if (message.getParameterCount() > 0)
            {
                user.setProperty("command.away.message", message.getText());
            }
            else
            {
                user.setProperty("command.away.message", null);
            }
        }

        client.send(response);
    }
}
