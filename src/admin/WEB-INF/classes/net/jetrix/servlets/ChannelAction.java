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

package net.jetrix.servlets;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import net.jetrix.Channel;
import net.jetrix.ChannelManager;
import net.jetrix.config.ChannelConfig;

/**
 * Action Servlet handling actions on channels.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ChannelAction extends HttpServlet {

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String name = request.getParameter("name");

        Channel channel = ChannelManager.getInstance().getChannel(name);
        ChannelConfig config = channel.getConfig();

        String password = request.getParameter("password");
        password = password == null ? null : password.trim();
        password = "".equals(password) ? null : password;

        config.setAccessLevel(Integer.parseInt(request.getParameter("accessLevel")));
        config.setPassword(password);
        config.setMaxPlayers(Integer.parseInt(request.getParameter("maxPlayers")));
        config.setMaxSpectators(Integer.parseInt(request.getParameter("maxSpectators")));
        config.setPersistent("true".equals(request.getParameter("persistent")));
        config.setTopic(request.getParameter("topic"));

        response.sendRedirect("/channel.jsp?name=" + name);
    }
}
