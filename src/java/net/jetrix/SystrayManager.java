/**
 * Jetrix TetriNET Server
 * Copyright (C) 2004  Emmanuel Bourg
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

package net.jetrix;

import java.io.*;
import java.util.logging.*;

import net.jetrix.config.*;
import snoozesoft.systray4j.*;

/**
 * Manages the system tray (windows only).
 *
 * @since 0.1.4
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class SystrayManager
{
    private static Logger log = Logger.getLogger("net.jetrix");

    /**
     * Create and display the system tray menu.
     */
    public static void open()
    {
        if (SysTrayMenu.isAvailable())
        {
            // build the menu items
            SysTrayMenuItem itemExit = new SysTrayMenuItem("Stop && Exit", "exit");
            itemExit.addSysTrayMenuListener(new SysTrayMenuAdapter()
            {
                public void menuItemSelected(SysTrayMenuEvent event)
                {
                    Server.getInstance().stop();
                }
            });

            final ServerConfig config = Server.getInstance().getConfig();

            SysTrayMenuItem itemAdmin = new SysTrayMenuItem("Administration", "admin");
            SysTrayMenuListener adminListener = new SysTrayMenuAdapter()
            {
                public void menuItemSelected(SysTrayMenuEvent event)
                {
                    Runtime runtime = Runtime.getRuntime();
                    try
                    {
                        String adminUrl = "http://operator:" + config.getOpPassword() + "@localhost:8080";
                        runtime.exec("rundll32 url.dll,FileProtocolHandler " + adminUrl);
                    }
                    catch (IOException e)
                    {
                        log.log(Level.WARNING, e.getMessage(), e);
                    }
                }

                public void iconLeftDoubleClicked(SysTrayMenuEvent event)
                {
                    menuItemSelected(event);
                }
            };
            itemAdmin.addSysTrayMenuListener(adminListener);

            // build the systray icon
            SysTrayMenuIcon icon = new SysTrayMenuIcon(Thread.currentThread().getContextClassLoader().getResource("jetrix.ico"));
            icon.addSysTrayMenuListener(adminListener);

            // build the menu
            SysTrayMenu menu = new SysTrayMenu(icon);
            menu.setToolTip("Jetrix TetriNET Server");
            menu.addItem(itemExit);
            menu.addItem(itemAdmin);

            menu.showIcon();
        }
    }

    /**
     * Remove the system tray menu.
     */
    public static void close()
    {
        SysTrayMenu.dispose();
    }

}
