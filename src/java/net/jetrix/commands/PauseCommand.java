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

package net.jetrix.commands;

import java.util.*;
import net.jetrix.*;
import net.jetrix.messages.*;

/**
 * Pause the game.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class PauseCommand implements Command
{
    private int accessLevel = 1;

    public String[] getAliases()
    {
        return (new String[] { "pause" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage(Locale locale)
    {
        return "/pause";
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command.pause.description", locale);
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client)m.getSource();
        Channel channel = client.getChannel();

        if (channel != null)
        {
            ChannelMessage pause = null;
            if (channel.getGameState() == Channel.GAME_STATE_PAUSED)
            {
                pause = new ResumeMessage();
            }
            else
            {
                pause = new PauseMessage();
            }

            pause.setSlot(channel.getClientSlot(client));
            pause.setSource(client);
            channel.sendMessage(pause);
        }
    }

}
