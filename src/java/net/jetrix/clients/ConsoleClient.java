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
import net.jetrix.*;
import net.jetrix.config.*;

/**
 * Command line console.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ConsoleClient implements Client
{
    private BufferedReader dis = new BufferedReader(new InputStreamReader(System.in));
    private ServerConfig conf;
    private Protocol protocol;
    private User user;
    private Channel channel;

    public ConsoleClient()
    {
        conf = Server.getInstance().getConfig();
        protocol = ProtocolManager.getInstance().getProtocol("net.jetrix.protocols.ConsoleProtocol");
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
        while (conf.isRunning())
        {
            try
            {
                Message m = receiveMessage();
                if (m == null) continue;
                Server.getInstance().sendMessage(m);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(Message message)
    {
        String msg = protocol.translate(message, user.getLocale());
        if (msg != null) System.out.println(msg);
    }

    public Message receiveMessage() throws IOException
    {
        String cmd = dis.readLine();
        Message m = protocol.getMessage(cmd);
        if (m != null) m.setSource(this);
        
        return m;
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

    public void disconnect() { }

}
