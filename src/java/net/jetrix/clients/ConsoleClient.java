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

package net.jetrix.clients;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;

import net.jetrix.*;
import net.jetrix.protocols.*;
import net.jetrix.config.*;

/**
 * Command line console.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ConsoleClient implements Client
{
    private Console console = System.console();
    private ServerConfig conf;
    private Protocol protocol;
    private User user;
    private Channel channel;
    private Logger log = Logger.getLogger("net.jetrix");
    private boolean closed = false;

    public ConsoleClient()
    {
        conf = Server.getInstance().getConfig();
        protocol = ProtocolManager.getInstance().getProtocol(ConsoleProtocol.class);
        user = new User();
        user.setName("Admin");
        user.setAccessLevel(100);
        user.setLocale(conf.getLocale());
        user.setSpectator();
    }

    public Protocol getProtocol()
    {
        return this.protocol;
    }

    public void run()
    {
        if (console == null)
        {
            log.info("Console interface unavailable");
            return;
        }

        while (conf.isRunning() && !closed)
        {
            try
            {
                Message message = receive();

                if (message != null)
                {
                    Server.getInstance().send(message);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        if (closed)
        {
            log.info("Input stream closed, shutting down the console...");
        }
    }

    public void send(Message message)
    {
        String msg = protocol.translate(message, user.getLocale());
        if (msg != null)
        {
            console.writer().println(msg);
        }
    }

    public Message receive() throws IOException
    {
        String line = console.readLine();
        if (line == null)
        {
            closed = true;
        }

        Message message = protocol.getMessage(line);
        if (message != null)
        {
            message.setSource(this);
        }

        return message;
    }

    public InetAddress getInetAddress()
    {
        return conf.getHost();
    }

    public void setChannel(Channel channel)
    {
        this.channel = channel;
    }

    public Channel getChannel()
    {
        return channel;
    }

    public boolean supportsMultipleChannels()
    {
        return false;
    }

    public boolean supportsAutoJoin()
    {
        return true;
    }

    public User getUser()
    {
        return user;
    }

    public String getVersion()
    {
        return "1.0";
    }

    public String getType()
    {
        return "Console";
    }

    public Date getConnectionTime()
    {
        return null;
    }

    public long getIdleTime()
    {
        return 0;
    }

    public void disconnect()
    {
    }

}
