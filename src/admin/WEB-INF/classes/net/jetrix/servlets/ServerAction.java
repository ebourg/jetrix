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

import net.jetrix.config.ServerConfig;
import net.jetrix.Server;
import net.jetrix.Banlist;

/**
 * Action Servlet handling actions on the server.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ServerAction extends HttpServlet
{

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String action = request.getParameter("action");

        if ("general".equals(action))
        {
            ServerConfig config = Server.getInstance().getConfig();

            config.setMaxConnections(Integer.parseInt(request.getParameter("maxConnections")));
            config.setMaxPlayers(Integer.parseInt(request.getParameter("maxPlayers")));
            config.setOpPassword(request.getParameter("opPassword"));
            config.setLocale(request.getParameter("locale"));
            config.setMessageOfTheDay(request.getParameter("motd"));
        }
        else if ("banlist.add".equals(action))
        {
            String pattern = request.getParameter("pattern");
            Banlist.getInstance().ban(pattern);
        }
        else if ("banlist.remove".equals(action))
        {
            String pattern = request.getParameter("pattern");
            Banlist.getInstance().unban(pattern);
        }
        else if ("gc".equals(action))
        {
            System.gc();
        }

        response.sendRedirect("/server.jsp");
    }
}
