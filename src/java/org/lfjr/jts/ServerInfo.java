/**
 * Java TetriNET Server
 * Copyright (C) 2001  Emmanuel Bourg
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

package org.lfjr.jts;

import java.util.*;

/**
 * Données partagées propres au serveur .
 *
 *
 * @author Emmanuel Bourg
 *
 */
public class ServerInfo
{
    static int    LOGIN_PORT = 31457;

    static int    MAX_CLIENT = 50;
    static int    FIELD_MAXX = 12;    // Maximum horizontal size of playing field
    static int    FIELD_MAXY = 22;    // Maximum vertical size of playing field


    static String VERSION = "0.0.8";
    boolean       end = false;
    int           nbClient = 0;
    int           nbChannel;
    String[]      winlist;
    Vector        playerList = new Vector();


    /**
     * Incrémente le compteur de clients connectés.
     *
     *
     * @see
     */
    public synchronized void incClient()
    {
        nbClient = nbClient + 1;
    }


    /**
     * Décrémente le compteur de clients connectés.
     *
     *
     * @see
     */
    public synchronized void decClient()
    {
        nbClient = nbClient - 1;
    }

}


