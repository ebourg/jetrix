/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2002  Emmanuel Bourg
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

import java.io.*;
import net.jetrix.config.*;

/**
 * Command line console.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
class ServerConsole extends Thread
{
    private BufferedReader dis = new BufferedReader(new InputStreamReader(System.in));
    private ServerConfig conf;

    public ServerConsole()
    {
        conf = Server.getInstance().getConfig();
        start();
    }

    public void run()
    {
        while (conf.isRunning())
        {
            try
            {
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
                        //System.out.println(((Client) si.playerList.elementAt(i)).socket);
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

