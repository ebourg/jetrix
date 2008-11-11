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

import static java.lang.Math.*;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import net.jetrix.*;
import net.jetrix.commands.*;
import net.jetrix.config.*;

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
        ServerConfig config = Server.getInstance().getConfig();

        if ("general".equals(action))
        {
            config.setName(request.getParameter("name"));
            config.setHost(request.getParameter("host"));
            config.setMaxConnections(max(1, Integer.parseInt(request.getParameter("maxConnections"))));
            config.setMaxPlayers(max(0, Integer.parseInt(request.getParameter("maxPlayers"))));
            config.setOpPassword(request.getParameter("opPassword"));
            config.setAdminPassword(request.getParameter("adminPassword"));
            config.setLocale(request.getParameter("locale"));
            config.setMessageOfTheDay(request.getParameter("motd"));
            config.setStatus(Integer.parseInt(request.getParameter("status")));
        }
        else if ("shutdown".equals(action))
        {
            Server.getInstance().stop();
        }
        else if ("listener.start".equals(action))
        {
            int index = Integer.parseInt(request.getParameter("index"));
            Listener listener = (Listener) config.getListeners().get(index);
            listener.start();
        }
        else if ("listener.stop".equals(action))
        {
            int index = Integer.parseInt(request.getParameter("index"));
            Listener listener = (Listener) config.getListeners().get(index);
            listener.stop();
        }
        else if ("service.start".equals(action))
        {
            int index = Integer.parseInt(request.getParameter("index"));
            Service service = (Service) config.getServices().get(index);
            service.start();
        }
        else if ("service.stop".equals(action))
        {
            int index = Integer.parseInt(request.getParameter("index"));
            Service service = (Service) config.getServices().get(index);
            service.stop();
        }
        else if ("command.remove".equals(action))
        {
            try
            {
                Class cls = Class.forName(request.getParameter("command"));
                Command command = (Command) cls.newInstance();
                CommandManager.getInstance().removeCommand(command);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
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
        else if ("datasource.update".equals(action)) 
        {
            DataSourceConfig datasourceConfig = new DataSourceConfig();
            datasourceConfig.setDriver(request.getParameter("driver"));
            datasourceConfig.setUrl(request.getParameter("url"));
            datasourceConfig.setUsername(request.getParameter("username"));
            datasourceConfig.setPassword(request.getParameter("password"));
            datasourceConfig.setMinIdle(Integer.parseInt(request.getParameter("minIdle")));
            datasourceConfig.setMaxActive(Integer.parseInt(request.getParameter("maxActive")));
            
            config.setDataSource(datasourceConfig);
            DataSourceManager.getInstance().setDataSource(datasourceConfig);
        }
        else if ("gc".equals(action))
        {
            System.gc();
        }

        response.sendRedirect("/server.jsp");

        // save the server configuration
        config.save();        
    }
}
