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
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import net.jetrix.ClientRepository;
import net.jetrix.Client;
import net.jetrix.Banlist;

/**
 * Action Servlet handling actions on users.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class UserAction extends HttpServlet
{
    private Logger logger = Logger.getLogger("net.jetrix");

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String action = request.getParameter("action");
        String name = request.getParameter("name");

        String redirect = "/user.jsp?name=" + name;
        Client client = ClientRepository.getInstance().getClient(name);

        if ("kick".equals(action))
        {
            logger.info(client.getUser().getName() + " (" + client.getInetAddress() + ") has been kicked by "
                    + request.getRemoteUser() + " (" + request.getRemoteHost() + ")");
        }
        else if ("ban".equals(action))
        {
            Banlist banlist = Banlist.getInstance();
            banlist.ban(client.getInetAddress().getHostAddress());

            logger.info(client.getUser().getName() + " (" + client.getInetAddress() + ") has been banned by "
                    + request.getRemoteUser() + " (" + request.getRemoteHost() + ")");
        }

        client.disconnect();

        response.sendRedirect("/channel.jsp?name=" + client.getChannel().getConfig().getName());
    }

}
