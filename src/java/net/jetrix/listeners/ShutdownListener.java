/**
 * Jetrix TetriNET Server
 * Copyright (C) 2008  Emmanuel Bourg
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

package net.jetrix.listeners;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.logging.Level;

import net.jetrix.Listener;
import net.jetrix.Server;
import net.jetrix.services.AbstractService;

/**
 * Listens for shutdown commands from the localhost. This is used to stop
 * the server from the shell on Unix.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ShutdownListener extends AbstractService implements Listener
{
    private static final String SHUTDOWN_COMMAND = "shutdown";

    public String getName()
    {
        return "shutdown";
    }

    public int getPort()
    {
        return 31457;
    }

    public void setPort(int port)
    {
    }

    public void start()
    {
        Thread t = new Thread(this, "listener: " + getName());
        t.setDaemon(true);
        t.start();
    }

    public void stop()
    {
    }

    public void run()
    {
        try
        {
            DatagramSocket socket = new DatagramSocket(new InetSocketAddress(InetAddress.getLocalHost(), getPort()));

            while (true)
            {
                int length = SHUTDOWN_COMMAND.length();
                DatagramPacket packet = new DatagramPacket(new byte[length], length);
                socket.receive(packet);

                if (new String(packet.getData(), "UTF8").equals(SHUTDOWN_COMMAND))
                {
                    log.info("Shutdown command received");
                    Server.getInstance().stop();
                    break;
                }
            }
        }
        catch (IOException e)
        {
            log.log(Level.WARNING, "ShutdownListener error", e);
        }
    }

    public boolean isRunning()
    {
        return true;
    }
}
