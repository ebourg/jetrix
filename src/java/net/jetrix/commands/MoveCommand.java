/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2003  Emmanuel Bourg
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
 * Move a player to a new slot or switch two players.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class MoveCommand implements Command
{
    private int accessLevel = 0;

    public String[] getAliases()
    {
        return (new String[] { "move" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage(Locale locale)
    {
        return "/move <" + Language.getText("command.params.player_num", locale) + "> <" + Language.getText("command.params.slot_num", locale) + ">";
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command.move.description", locale);
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client)m.getSource();

        if (m.getParameterCount() >= 2)
        {
            // parse the two slot numbers
            int slot1 = 0;
            int slot2 = 0;

            try
            {
                slot1 = Integer.parseInt(m.getParameter(0));
                slot2 = Integer.parseInt(m.getParameter(1));
            }
            catch (NumberFormatException e) { }

            if (slot1 >= 1 && slot1 <= 6 && slot2 >= 1 && slot2 <= 6) {
                // send the switch message to the channel
                PlayerSwitchMessage pswitch = new PlayerSwitchMessage();
                pswitch.setSlot1(slot1);
                pswitch.setSlot2(slot2);
                client.getChannel().sendMessage(pswitch);
            }
        }
        else
        {
            // not enough parameters
            Locale locale = client.getUser().getLocale();
            String message = "<red>" + m.getCommand() + "<blue> <" + Language.getText("command.params.player_num", locale) + "> <" + Language.getText("command.params.slot_num", locale) + ">";
            PlineMessage response = new PlineMessage(message);
            client.sendMessage(response);
        }
    }
}
