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

package net.jetrix.messages;

import java.util.*;
import net.jetrix.*;

/**
 * A /command.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class CommandMessage extends Message
{
    private String command;
    private String text;
    private List parameters;

    public CommandMessage()
    {
        parameters = new ArrayList();
    }

    public String getCommand()
    {
        return command;
    }

    public void setCommand(String command)
    {
        this.command = command;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getParameter(int i)
    {
        return (String)parameters.get(i);
    }

    public void addParameter(String obj)
    {
        parameters.add(obj);
    }

    public int getParameterCount()
    {
        return parameters.size();
    }
}
