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

import java.util.*;
import java.util.logging.*;

import net.jetrix.*;

/**
 * Abstract command.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 * @since 0.2
 */
public abstract class AbstractCommand implements Command
{
    protected int level = AccessLevel.PLAYER;
    protected boolean hidden;

    protected static Logger log = Logger.getLogger("net.jetrix");

    public int getAccessLevel()
    {
        return level;
    }

    public void setAccessLevel(int level)
    {
        this.level = level;
    }

    public boolean isHidden()
    {
        return hidden;
    }

    public void setHidden(boolean hidden)
    {
        this.hidden = hidden;
    }

    public String getUsage(Locale locale)
    {
        return "/" + getAliases()[0];
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command." + getAliases()[0] + ".description", locale);
    }

    /**
     * Return the command alias. This is a method to be overriden only
     * by commands with a unique alias.
     */
    protected String getAlias()
    {
        return null;
    }

    public String[] getAliases()
    {
        if (getAlias() != null)
        {
            return new String[] { getAlias() };
        }
        else
        {
            throw new UnsupportedOperationException("No alias defined for " + getClass());
        }
    }

}
