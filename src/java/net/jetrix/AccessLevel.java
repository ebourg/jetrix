/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2005  Emmanuel Bourg
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

package net.jetrix;

/**
 * Enumeration of users' access levels.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public interface AccessLevel
{
    /** The lowest access level */
    public static final int PLAYER = 0;

    /** Level of the first player in the channel's player list */
    public static final int CHANNEL_OPERATOR = 1;

    /** Authenticated server operator */
    public static final int OPERATOR = 2;

    /** Server administrator level */
    public static final int ADMINISTRATOR = 100;
}
