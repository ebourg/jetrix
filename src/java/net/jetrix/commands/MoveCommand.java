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
public class MoveCommand extends AbstractCommand implements ParameterCommand
{
    public String getAlias()
    {
        return "move";
    }

    public String getUsage(Locale locale)
    {
        return "/move <" + Language.getText("command.params.player_num", locale) + "> <" + Language.getText("command.params.slot_num", locale) + ">";
    }

    public int getParameterCount()
    {
        return 2;
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client) m.getSource();

        // parse the two slot numbers
        int slot1 = m.getIntParameter(0, 0);
        int slot2 = m.getIntParameter(1, 0);

        if (slot1 >= 1 && slot1 <= 6 && slot2 >= 1 && slot2 <= 6 && slot1 != slot2)
        {
            // send the switch message to the channel
            PlayerSwitchMessage pswitch = new PlayerSwitchMessage();
            pswitch.setSlot1(slot1);
            pswitch.setSlot2(slot2);
            client.getChannel().send(pswitch);
        }
    }
}
