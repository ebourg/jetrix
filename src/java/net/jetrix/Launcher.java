/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2004  Emmanuel Bourg
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

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * An application launcher executing a specified class and building dynamically
 * its class path.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class Launcher {

    /**
     * Server entry point. All classes, jar and zip files in the lib
     * subdirectory are automatically added to the classpath.
     *
     * @param args start parameters
     */
    public static void main(String[] args) throws Exception
    {
        // build the list of JARs in the ./lib directory
        File repository = new File("lib/");
        List jars = new ArrayList();
        jars.add(repository.toURL());
        jars.add(new File("lang/").toURL());

        File[] files = repository.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            String filename = files[i].getAbsolutePath();
            if (filename.endsWith(".jar") || filename.endsWith(".zip"))
            {
                jars.add(files[i].toURL());
            }
        }

        URL[] urls = new URL[jars.size()];
        for (int i = 0; i < jars.size(); i++)
        {
            urls[i] = (URL) jars.get(i);
        }

        URLClassLoader loader = new URLClassLoader(urls, null);
        Thread.currentThread().setContextClassLoader(loader);

        // run the main method of the specified class
        Class serverClass = loader.loadClass("net.jetrix.Server");
        serverClass.getMethod("main", new Class[] { args.getClass() }).invoke(null, new Object[] { args });
    }

}
