/**
 * Jetrix TetriNET Server
 * Copyright (C) 2004  Gamereplay (game@gamereplay.be)
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
import net.jetrix.config.*;
import net.jetrix.*;

/**
 * Switch between preconfigured settings.
 *
 * @author Gamereplay
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ModeCommand extends AbstractCommand
{
    private static int[][] modes =
            {
                {15, 14, 14, 14, 14, 14, 14, 1, 0},
                {10, 15, 15, 15, 15, 15, 15, 1, 1},
                {20, 14, 13, 13, 13, 13, 14, 1, 0},
                {10, 15, 15, 15, 15, 15, 15, 2, 1},
                {25, 12, 13, 13, 12, 12, 13, 1, 0},
                {0,  17, 17, 17, 16, 16, 17, 1, 0},
                {100, 0,  0,  0,  0,  0,  0, 1, 0},
                {0,   0,  0,  0, 50, 50,  0, 1, 1},
                {0,   0, 50, 50,  0,  0,  0, 1, 1},
                {0,   0,  0,  0,  0,  0, 100,1, 1}
            };

    public String getAlias()
    {
        return "mode";
    }

    public String getUsage(Locale locale)
    {
        return "/" + getAlias() + " <0-" + (modes.length - 1) + ">";
    }

    public void updateSetting(Settings settings, int[] mode)
    {
        for (Block block : Block.values())
        {
            settings.setOccurancy(block, mode[block.ordinal()]);
        }

        settings.setLinesPerSpecial(mode[7]);
        settings.setSpecialAdded(mode[8]);
    }

    public void execute(CommandMessage message)
    {
        Client client = (Client) message.getSource();
        Channel channel = client.getChannel();

        if (message.getParameterCount() == 0)
        {
            Locale locale = client.getUser().getLocale();

            // display all modes available
            for (int i = 0; i < modes.length; i++)
            {
                Message tmode = new PlineMessage("<red>/" + getAlias() + " <aqua>" + i + "</aqua> : <darkBlue>" + Language.getText("command.mode.message" + i, locale));
                client.send(tmode);
            }
        }
        else
        {
            int param = -1;
            
            try
            {
                param = Integer.parseInt(message.getParameter(0));
            }
            catch (NumberFormatException e) { }
            
            if (param >= 0 && param < modes.length)
            {
                updateSetting(channel.getConfig().getSettings(), modes[param]);

                PlineMessage enabled = new PlineMessage();
                enabled.setKey("command.mode.enabled", "key:command.mode.message" + param);
                channel.send(enabled);
            }
            else
            {
                PlineMessage error = new PlineMessage();
                error.setText("<red>/" + getAlias() + "</red> <blue><0-" + (modes.length - 1) + "></blue>");
                client.send(error);
            }
        }
    }
}
