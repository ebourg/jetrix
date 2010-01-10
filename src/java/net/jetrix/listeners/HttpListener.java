/**
 * Jetrix TetriNET Server
 * Copyright (C) 2010  Emmanuel Bourg
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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jetrix.Listener;
import net.jetrix.Server;
import net.jetrix.services.AbstractService;

import winstone.Launcher;

/**
 * Web administration console.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class HttpListener extends AbstractService implements Listener
{
    private winstone.Launcher server;
    private Logger log = Logger.getLogger("net.jetrix");
    private int port = 31460;

    public String getName()
    {
        return "web admin";
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public void start()
    {
        if (!isRunning())
        {
            try
            {
                Map<String, String> args = new HashMap<String, String>();
                args.put("warfile", "./lib/jetrix-admin-@version@.war");
                args.put("httpPort", String.valueOf(getPort()));
                //args.put("ajp13Port", "-1");
                args.put("argumentsRealm.passwd.admin", Server.getInstance().getConfig().getAdminPassword());
                args.put("argumentsRealm.roles.admin", "admin");
                //args.put("accessLoggerClassName", "winstone.accesslog.SimpleAccessLogger");
                //args.put("simpleAccessLogger.file", "./log/webadmin_access.log");
                //args.put("simpleAccessLogger.format", "resin");
                args.put("logfile", "./log/webadmin.log");
                args.put("useInvoker", "true");
                args.put("invokerPrefix", "/servlet/");
                args.put("debug", "5");
                args.put("commonLibFolder", "./lib/shared");
                //args.put("useServletReloading", "true");

                Launcher.initLogger(args);

                server = new Launcher(args);

                log.info("Web administration console started on port " + getPort());
            }
            catch (IOException e)
            {
                log.log(Level.SEVERE, "Unable to start the Web administration console on port " + getPort(), e);
            }
        }
    }

    public void stop()
    {
        if (isRunning())
        {
            server.shutdown();
            log.info("Web administration console stopped");
        }
    }

    public void run() { }

    public boolean isRunning()
    {
        return server != null && server.isRunning();
    }
}
