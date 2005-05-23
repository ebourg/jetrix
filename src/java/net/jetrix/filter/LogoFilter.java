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
import java.io.*;

import net.jetrix.*;
import net.jetrix.messages.*;

/**
 * Display a logo on the field of players losing the game.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class LogoFilter extends GenericFilter
{
    private static final String DEFAULT_FIELD = "data/jetrix.field";

    private Field field;

    public void init()
    {
        // load the field
        field = new Field();
        try
        {
            field.load(config.getString("field", DEFAULT_FIELD));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void onMessage(PlayerLostMessage m, List<Message> out)
    {
        // send closing screen
        FieldMessage endingScreen = new FieldMessage();
        endingScreen.setSlot(m.getSlot());
        endingScreen.setField(field.getFieldString());

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
