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

import net.jetrix.config.ServerConfig;
import net.jetrix.listeners.HttpListener;
import org.jdesktop.jdic.tray.SystemTray;
import org.jdesktop.jdic.tray.TrayIcon;

import javax.swing.*;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.PopupMenuEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            SystemTray tray = null;

            try
            {
                tray = SystemTray.getDefaultSystemTray();
            }
            catch (Throwable t)
            {
                log.log(Level.FINE, "System tray unavailable", t);
                return;
            }

            // adjust the look & feel
            System.setProperty("javax.swing.adjustPopupLocationToFit", "false");

            try
            {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception e)
            {
                log.log(Level.WARNING, e.getMessage(), e);
            }

            // build the menu items
            JMenuItem itemAdmin = new JMenuItem("Administration");
            Font font = itemAdmin.getFont();
            itemAdmin.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));

            itemAdmin.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    openWebAdmin();
                }
            });

            JMenuItem itemLink = new JMenuItem("Jetrix Website");
            itemLink.addActionListener(new OpenURLActionListener("http://jetrix.sourceforge.net"));

            JMenuItem itemSupport = new JMenuItem("Technical Support");
            itemSupport.addActionListener(new OpenURLActionListener("http://sourceforge.net/forum/forum.php?forum_id=172941"));

            JMenuItem itemExit = new JMenuItem("Stop & Exit");
            itemExit.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    Server.getInstance().stop();
                }
            });

            // build the menu
            final JPopupMenu menu = new JPopupMenu();
            menu.add(itemAdmin);
            menu.add(itemLink);
            menu.add(itemSupport);
            menu.addSeparator();
            menu.add(itemExit);

            menu.addPopupMenuListener(new PopupMenuListener()
            {
                public void popupMenuWillBecomeVisible(PopupMenuEvent e)
                {
                    // un arm the items
                    MenuElement[] elements = menu.getSubElements();
                    for (MenuElement element : elements)
                    {
                        JMenuItem item = (JMenuItem) element;
                        item.setArmed(false);
                    }
                }

                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { }

                public void popupMenuCanceled(PopupMenuEvent e) { }
            });

            // build the trayIcon icon
            ImageIcon icon = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("jetrix-16x16.png"));

            trayIcon = new TrayIcon(icon, TITLE, menu);
            trayIcon.setIconAutoSize(true);
            trayIcon.addActionListener(new ActionListener()
            {
                private long timestamp;

                public void actionPerformed(ActionEvent e)
                {
                    // emulates a double click listener
                    if (e.getWhen() - timestamp < 750)
                    {
                        openWebAdmin();
                    }

                    timestamp = e.getWhen();
                }
            });

            // display the trayIcon icon
            tray.addTrayIcon(trayIcon);
        }
    }

    /**
     * Remove the system trayIcon menu.
     */
    public static void close()
    {
        if (trayIcon != null)
        {
            SystemTray.getDefaultSystemTray().removeTrayIcon(trayIcon);
            trayIcon = null;
        }
    }

    /**
     * Display a baloon message on the trayIcon icon.
     *
     * @param message the message to display
     * @param type    the type of the message (0: info, 1: error, 2: warning, 3: none)
     * @since 0.3
     */
    public static void notify(String message, int type)
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
            // open the browser
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
        }
        catch (IOException e)
        {
            log.log(Level.WARNING, e.getMessage(), e);
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
