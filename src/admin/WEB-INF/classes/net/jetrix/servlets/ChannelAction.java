/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2004  Emmanuel Bourg
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

package net.jetrix.servlets;

import net.jetrix.Channel;
import net.jetrix.ChannelManager;
import net.jetrix.Server;
import net.jetrix.config.ChannelConfig;
import net.jetrix.config.Settings;
import net.jetrix.config.Speed;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.lang.Math.*;

/**
 * Action Servlet handling actions on channels.
 *
 * @author Emmanuel Bourg
 */
public class ChannelAction extends HttpServlet
{

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String name = request.getParameter("name");
        ChannelManager manager = ChannelManager.getInstance();

        if ("new".equals(request.getParameter("action")))
        {
            // create a new channel
            ChannelConfig config = new ChannelConfig();
            config.setSettings(new Settings());
            config.setDescription("");

            // find a name for the channel
            int i = 1;
            while (name == null)
            {
                if (manager.getChannel("tetrinet" + i) == null)
                {
                    name = "tetrinet" + i;
                }
                else
                {
                    i++;
                }
            }

            config.setName(name);

            // spawn the channel
            manager.createChannel(config);
        }
        else
        {
            // update an existing channel
            Channel channel = manager.getChannel(name);
            ChannelConfig config = channel.getConfig();

            String password = request.getParameter("password");
            password = password == null ? null : password.trim();
            password = "".equals(password) ? null : password;

            config.setDescription(request.getParameter("description"));
            config.setAccessLevel(Integer.parseInt(request.getParameter("accessLevel")));
            config.setPassword(password);
            config.setMaxPlayers(max(0, min(6, Integer.parseInt(request.getParameter("maxPlayers")))));
            config.setMaxSpectators(max(0, Integer.parseInt(request.getParameter("maxSpectators"))));
            config.setPersistent("true".equals(request.getParameter("persistent")));
            config.setVisible("true".equals(request.getParameter("visible")));
            config.setIdleAllowed("true".equals(request.getParameter("idle")));
            config.setWinlistId(request.getParameter("winlist"));
            config.setTopic(request.getParameter("topic"));
            config.setSpeed(Speed.valueOf(request.getParameter("speed").toUpperCase()));
        }

        // redirect to the channel page
        response.sendRedirect("/channel.jsp?name=" + name);

        // save the server configuration
        Server.getInstance().getConfig().save();
    }
}
