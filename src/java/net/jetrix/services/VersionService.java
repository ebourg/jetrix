/**
 * Jetrix TetriNET Server
 * Copyright (C) 2005  Emmanuel Bourg
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

package net.jetrix.services;

import net.jetrix.config.ServerConfig;

import java.net.*;
import java.io.*;
import java.util.logging.*;

/**
 * Service checking the availability of a new release.
 *
 * @since 0.2
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class VersionService extends CronService
{
    /** The latest stable version known. */
    private static String latestVersion;

    public VersionService()
    {
        // default frequency : once a day
        setPattern("0 0 * * * *");
    }

    public String getName()
    {
        return "Version Checker";
    }

    protected void run()
    {
        updateLatestVersion();

        if (isNewVersionAvailable())
        {
            log.warning("A new version is available (" + VersionService.getLatestVersion() + "), download it on http://jetrix.sf.net now!");
        }
    }

    /**
     * Return the version of the latest release. The version of the last stable
     * release is stored on the Jetrix site (http://jetrix.sf.net/version.php),
     * this file is build dynamically everyday and reuse the version specified
     * in the project.properties file.
     */
    private static String fetchLatestVersion()
    {
        String version = null;

        try
        {
            URL url = new URL("http://jetrix.sourceforge.net/version.php");

            HttpURLConnection conn = null;
            try
            {
                conn = (HttpURLConnection) url.openConnection();

                // read the first line of the file
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                version = in.readLine();
            }
            finally
            {
                conn.disconnect();
            }
        }
        catch (IOException e)
        {
            log.log(Level.WARNING, "Unable to check the availability of a new version", e);
        }

        return version;
    }

    /**
     * Update the latest stable version known.
     */
    public static void updateLatestVersion()
    {
        latestVersion = fetchLatestVersion();
    }

    /**
     * Return the latest stable version known.
     */
    public static String getLatestVersion()
    {
        return latestVersion;
    }

    /**
     * Check if the latest stable version is more recent than the current version.
     */
    public static boolean isNewVersionAvailable()
    {
        return latestVersion != null && ServerConfig.VERSION.compareTo(latestVersion) < 0;
    }
}
