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

package net.jetrix.winlist;

import java.util.*;

import net.jetrix.*;

/**
 * The result of a game.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class GameResult
{
    private Date startTime;
    private Date endTime;
    private List gamePlayers;

    /**
     * Return the date of the beginning of the game.
     */
    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    /**
     * Return the date of the end of the game.
     */
    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    /**
     * Return the list of players involved in the game. The collection contains
     * instances of GamePlayer.
     */
    public Collection getGamePlayers()
    {
        return gamePlayers;
    }

    public void addGamePlayer(GamePlayer gamePlayer)
    {
        if (gamePlayers == null)
        {
            gamePlayers = new ArrayList();
        }
        gamePlayers.add(gamePlayer);
    }

    /**
     * Update the result of the game by indicating if the specified user won or not.
     */
    public void update(User user, boolean isWinner)
    {
        GamePlayer player = new GamePlayer();
        player.setName(user.getName());
        player.setTeamName(user.getTeam());
        player.setWinner(isWinner);
        player.setEndTime(new Date());
        addGamePlayer(player);
    }

    /**
     * Return the players that finished the game at the specified rank.
     */
    public Collection getPlayersAtRank(int rank) {

        Collection players = new ArrayList();

        if (rank == 1)
        {
            // look for the winners
            Iterator it = gamePlayers.iterator();
            while (it.hasNext())
            {
                GamePlayer player = (GamePlayer) it.next();
                if (player.isWinner())
                {
                    players.add(player);
                }
            }
        }
        else
        {
            // sort by date
            Collections.sort(gamePlayers, new Comparator() {
                public int compare(Object o1, Object o2)
                {
                    GamePlayer player1 = (GamePlayer) o1;
                    GamePlayer player2 = (GamePlayer) o2;
                    return player1.getEndTime().compareTo(player2.getEndTime());
                }
            });

            // find the player at the specified rank
            int i = 1;
            Iterator it = gamePlayers.iterator();
            while (it.hasNext() && i < rank)
            {
                GamePlayer player = (GamePlayer) it.next();
                if (!player.isWinner())
                {
                    i++;
                    if (i == rank)
                    {
                        players.add(player);
                    }
                }
            }
        }

        return players;
    }
}
