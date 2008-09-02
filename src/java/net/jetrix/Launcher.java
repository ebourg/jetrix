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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;

/**
 * An application launcher executing a specified class and building dynamically
 * its class path.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class Launcher {

    public static final String MAIN_CLASS = "net.jetrix.Server";

    /**
     * Server entry point. All classes, jar and zip files in the lib
     * subdirectory are automatically added to the classpath.
     *
     * @param args start parameters
     */
    public static void main(String[] args) throws Exception
    {
        if (args.length == 1 && "stop".equals(args[0]))
        {
            stop();
        }
        else
        {
            start(args);
        }
    }

    private static void start(String[] args) throws Exception
    {
        // get the files in the lib directory
        File repository = new File("lib/");

        // decompress the pack200 files
        unpack(repository);

        ClassLoader loader = createClassLoader(repository, new File("lang/"));

        Thread.currentThread().setContextClassLoader(loader);

        // run the main method of the specified class
        Class serverClass = loader.loadClass(MAIN_CLASS);
        serverClass.getMethod("main", String[].class).invoke(null, new Object[] { args });
    }

    /**
     * Unpack the pack200 files in the specified directory
     *
     * @param directory
     */
    private static void unpack(File directory) throws IOException
    {
        File[] files = directory.listFiles();

        for (File file : files)
        {
            String filename = file.getAbsolutePath();
            if (filename.endsWith(".pack"))
            {
                // remove the .pack extension at the end of the unpacked file
                String unpackedName = filename.substring(0, filename.lastIndexOf(".pack"));
                JarOutputStream out = new JarOutputStream(new FileOutputStream(unpackedName));

                Pack200.newUnpacker().unpack(file, out);

                out.flush();
                out.close();

                // delete the packed file
                file.delete();
            }
        }
    }

    /**
     * Build a classloader including the jar and zip files in the specified
     * directories. The directories are also included in the classpath.
     * 
     * @param directories
     */
    private static ClassLoader createClassLoader(File... directories) throws Exception
    {
        List<URL> urls = new ArrayList<URL>();

        for (File directory : directories)
        {
            // add the jar and zip files in the directory to the classpath
            File[] files = directory.listFiles();

            for (int i = 0; i < files.length; i++)
            {
                String filename = files[i].getAbsolutePath();
                if (filename.endsWith(".jar") || filename.endsWith(".zip"))
                {
                    urls.add(files[i].toURI().toURL());
                }
            }

            // add the directory to the classpath
            urls.add(directory.toURI().toURL());
        }

        // create the classloader
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), null);
    }

    private static void stop() throws Exception
    {
        // send the "shutdown" to the UDP port 31457
        byte[] msg = "shutdown".getBytes("UTF8");
        DatagramPacket packet = new DatagramPacket(msg, msg.length);
        packet.setAddress(InetAddress.getLocalHost());
        packet.setPort(31457);

        DatagramSocket socket = new DatagramSocket();
        try
        {
            socket.send(packet);
        }
        finally
        {
            socket.close();
        }
    }

}
