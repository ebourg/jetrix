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

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.FileHandler;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.IOException;

import net.jetrix.config.ServerConfig;

/**
 * Manages the output of the logger.
 *
 * @since 0.1.4
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
class LogManager
{
    /**
     * Prepare the logger. Log messages are appended to the console and
     * to a log file.
     */
    public static void init()
    {
        Logger log = Logger.getLogger("net.jetrix");
        log.setUseParentHandlers(false);
        log.setLevel(Level.ALL);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        final boolean debug = "true".equals(System.getProperty("jetrix.debug"));
        if (debug)
        {
            consoleHandler.setLevel(Level.ALL);
        }
        log.addHandler(consoleHandler);
        consoleHandler.setFormatter(new Formatter()
        {
            Date date = new Date();
            private String format = debug ? "HH:mm:ss,SSS" : "HH:mm:ss";
            private SimpleDateFormat formatter;

            public synchronized String format(LogRecord record)
            {
                date.setTime(record.getMillis());
                if (formatter == null)
                {
                    formatter = new SimpleDateFormat(format);
                }
                return "[" + formatter.format(date) + "] ["
                        + record.getLevel().getLocalizedName() + "] "
                        + formatMessage(record) + "\n";
            }
        });

        try
        {
            ServerConfig config = Server.getInstance().getConfig();

            FileHandler fileHandler = new FileHandler(config.getAccessLogPath(), 1000000, 10);
            fileHandler.setLevel(Level.CONFIG);
            log.addHandler(fileHandler);
            fileHandler.setFormatter(new Formatter()
            {
                Date dat = new Date();
                private final static String format = "yyyy-MM-dd HH:mm:ss";
                private SimpleDateFormat formatter;

                public synchronized String format(LogRecord record)
                {
                    dat.setTime(record.getMillis());
                    if (formatter == null)
                    {
                        formatter = new SimpleDateFormat(format);
                    }
                    return "[" + formatter.format(dat) + "] "
                            + formatMessage(record) + "\n";
                }
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
