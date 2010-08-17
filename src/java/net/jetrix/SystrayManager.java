/**
 * Jetrix TetriNET Server
 * Copyright (C) 2004-2005  Emmanuel Bourg
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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

import net.jetrix.config.ServerConfig;
import net.jetrix.listeners.HttpListener;

/**
 * Manages the system trayIcon (windows only).
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 * @since 0.2
 */
public class SystrayManager
{
    private static Logger log = Logger.getLogger("net.jetrix");
    private static TrayIcon trayIcon;
    private static final String TITLE = "Jetrix TetriNET Server";

    /**
     * Create and display the system trayIcon menu.
     */
    public static void open()
    {
        if (trayIcon == null)
        {
            SystemTray tray;

            try
            {
                tray = SystemTray.getSystemTray();
            }
            catch (Throwable t)
            {
                log.log(Level.FINE, "System tray unavailable", t);
                return;
            }


            // build the menu items
            Font font = new Font(Font.DIALOG, Font.PLAIN, 11);

            MenuItem itemAdmin = new MenuItem("Administration");
            itemAdmin.setFont(font.deriveFont(Font.BOLD));
            itemAdmin.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    openWebAdmin();
                }
            });

            MenuItem itemLink = new MenuItem("Jetrix Website");
            itemLink.addActionListener(new OpenURLActionListener("http://jetrix.sourceforge.net"));

            MenuItem itemSupport = new MenuItem("Technical Support");
            itemSupport.addActionListener(new OpenURLActionListener("http://sourceforge.net/projects/jetrix/forums/forum/172941"));

            MenuItem itemExit = new MenuItem("Stop & Exit");
            itemExit.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    Server.getInstance().stop();
                }
            });

            // build the menu
            final PopupMenu menu = new PopupMenu();
            menu.add(itemAdmin);
            menu.add(itemLink);
            menu.add(itemSupport);
            menu.addSeparator();
            menu.add(itemExit);
            
            menu.setFont(font);

            // build the trayIcon icon
            String osname = System.getProperty("os.name");
            String iconname = osname.contains("Linux") ? "jetrix-32x32.png" : "jetrix-16x16.png";
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Image icon = new ImageIcon(loader.getResource("icons/" + iconname)).getImage();

            trayIcon = new TrayIcon(icon, TITLE, menu);
            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    openWebAdmin();
                }
            });

            // display the trayIcon icon
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                log.log(Level.WARNING, "Unable to display the tray", e);
            }
        }
    }

    /**
     * Remove the system trayIcon menu.
     */
    public static void close()
    {
        if (trayIcon != null)
        {
            SystemTray.getSystemTray().remove(trayIcon);
            trayIcon = null;
        }
    }

    /**
     * Display a baloon message on the trayIcon icon.
     *
     * @param message the message to display
     * @param type    the type of the message
     * @since 0.3
     */
    public static void notify(String message, TrayIcon.MessageType type)
    {
        if (trayIcon != null)
        {
            trayIcon.displayMessage(TITLE, message, type);
        }
    }

    /**
     * Open the web administration console in a browser window (Win32 only).
     */
    private static void openWebAdmin()
    {
        ServerConfig config = Server.getInstance().getConfig();

        // find the port of the HttpListener
        int port = 0;
        for (Listener listener : config.getListeners())
        {
            if (listener instanceof HttpListener)
            {
                port = listener.getPort();
                break;
            }
        }

        if (port != 0)
        {
            openURL("http://admin:" + config.getAdminPassword() + "@localhost:" + port);
        }
    }

    /**
     * Open the specified URL in the Browser.
     *
     * @since 0.3
     */
    private static void openURL(String url)
    {
        try
        {
            Desktop.getDesktop().browse(new URI(url));
        }
        catch (Exception e)
        {
            log.log(Level.WARNING, "Unable to open the url: " + url, e);
        }
    }

    /**
     * Action listener opening an URL.
     *
     * @since 0.3
     */
    private static class OpenURLActionListener implements ActionListener
    {
        private String url;

        public OpenURLActionListener(String url)
        {
            this.url = url;
        }

        public void actionPerformed(ActionEvent e)
        {
            openURL(url);
        }
    }

}
