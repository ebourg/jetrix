/**
 * Jetrix TetriNET Server
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

import java.io.*;
import org.lfjr.jts.config.*;

/**
 * Gère les commandes tapées sur la console du serveur.
 *
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
class ServerConsole extends Thread
{
    BufferedReader    dis = new BufferedReader(new InputStreamReader(System.in));
    ServerConfig conf;


    /**
     * Constructor declaration
     *
     *
     * @param s
     *
     * @see
     */
    public ServerConsole()
    {
        conf = ServerConfig.getInstance();
        start();
    }


    /**
     * Method declaration
     *
     *
     * @see
     */
    public void run()
    {
        while (true)
        {
            try
            {
            	/*
                while (!dis.ready())
                {
                    yield();
                }*/

                String cmd = dis.readLine();

                // command parsing
                if ("exit".equals(cmd))
                {
                    System.exit(0);
                }

                if ("quit".equals(cmd))
                {
                    System.exit(0);
                }

                if ("shutdown".equals(cmd))
                {
                    System.exit(0);
                }

                if ("list".equals(cmd))
                {
                    System.out.println("\nPlayer list");

                    //for (int i = 0; i<si.playerList.size(); i++)
                    {
                        //System.out.println(((TetriNETClient) si.playerList.elementAt(i)).socket);
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

}

