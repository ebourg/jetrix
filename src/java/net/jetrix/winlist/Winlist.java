/**
 * Jetrix TetriNET Server
 * Copyright (C) 2003-2004  Emmanuel Bourg
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

package net.jetrix.winlist;

import java.util.*;

import net.jetrix.config.*;

/**
 * A winlist ranking players and teams.
 *
 * @since 0.1.2
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public interface Winlist
{
    /**
     * Return the id for this winlist.
     */
    String getId();

    /**
     * Set the id for this winlist.
     */
    void setId(String id);

    /**
     * Initialize the winlist.
     *
     * @param config the winlist configuration
     */
    void init(WinlistConfig config);

    /**
     * Return the configuration necessary to create the same winlist
     *
     * @since 0.2
     */
    WinlistConfig getConfig();

    /**
     * Return the score of the specified player or team
     *
     * @param name the name of the player or team
     * @param type the score type (0: player, 1: team)
     */
    Score getScore(String name, int type);

    /**
     * Return the score list in the specified range.
     *
     * @param offset the beginning of the range
     * @param length the length of the range
     */
    List<Score> getScores(long offset, long length);

    /**
     * Update the winlist with the specified game result.
     */
    void saveGameResult(GameResult result);

    /**
     * Remove all entries in the winlist
     *
     * @since 0.2
     */
    void clear();

    /**
     * Return the number of entries in the winlist
     *
     * @since 0.2
     */
    int size();

}
