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

package net.jetrix.filter;

import java.util.*;

import net.jetrix.*;
import net.jetrix.messages.*;

/**
 * Display a logo on the field of players loosing the game.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class LogoFilter extends GenericFilter
{

    private short jetrixLogo[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                  0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0,
                                  0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0,
                                  0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0,
                                  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                  0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0,
                                  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                  0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0,
                                  0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0,
                                  0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0,
                                  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                  0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0,
                                  0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0,
                                  0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0,
                                  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                  0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0,
                                  0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0,
                                  0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0,
                                  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                  0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0,
                                  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
                                  0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0};

    public void onMessage(PlayerLostMessage m, List<Message> out)
    {
        // send closing screen
        StringBuffer screenLayout = new StringBuffer();
        for (int i = 0; i < 12 * 22; i++)
        {
            screenLayout.append(((int) (Math.random() * 5 + 1)) * (1 - jetrixLogo[i]));
        }
        FieldMessage endingScreen = new FieldMessage();
        endingScreen.setSlot(m.getSlot());
        endingScreen.setField(screenLayout.toString());

        out.add(m);
        out.add(endingScreen);
    }

    public String getName()
    {
        return "Logo Filter";
    }

    public String getDescription()
    {
        return "Display a custom field when a player lose.";
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
