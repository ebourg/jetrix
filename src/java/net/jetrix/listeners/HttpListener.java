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

package net.jetrix.listeners;

import java.io.*;
import java.util.logging.*;

import org.mortbay.http.*;
import org.mortbay.util.*;

import net.jetrix.*;

/**
 * Web administration console.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class HttpListener implements Listener
{
    private org.mortbay.jetty.Server jetty;
    private Logger logger = Logger.getLogger("net.jetrix");
    private boolean initialized;

    public HttpListener()
    {
        // configure the log file
        System.setProperty("LOG_CLASSES", "org.mortbay.util.OutputStreamLogSink");
        System.setProperty("LOG_FILE", "log/jetty.log");
        System.setProperty("LOG_DATE_FORMAT", "[yyyy-MM-dd HH:mm:ss] ");
    }

    private void init()
    {
        // authentication realm
        HashUserRealm realm = new HashUserRealm("Jetrix Admin");
        realm.put("operator", Server.getInstance().getConfig().getOpPassword());
        realm.addUserToRole("operator", "operator");

        jetty = new org.mortbay.jetty.Server();
        jetty.addRealm(realm);

        try
        {
            jetty.addListener(new InetAddrPort(getPort()));
            jetty.addWebApplication("/", "./lib/jetrix-admin-@version@.war");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public String getName()
    {
        return "web admin";
    }

    public int getPort()
    {
        return 8080;
    }

    public void start()
    {
        try
        {
            if (!initialized)
            {
                init();
                initialized = true;
            }
            jetty.start();
            logger.info("Web administration console started on port " + getPort());
        }
        catch (MultiException e)
        {
            logger.log(Level.SEVERE, "Unable to start the Web administration console on port " + getPort(), e);
        }
    }

    public void stop()
    {
        try
        {
            jetty.stop();
            logger.info("Web administration console stopped");
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void run() { }

}
