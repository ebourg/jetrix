/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2002  Emmanuel Bourg
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

package net.jetrix.patcher;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

/**
 * Jetrix Update - downloads new files from the patch server.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class JetrixUpdate
{
    static Vector update = new Vector();
    static String basedir = "http://tetrinet.lfjr.net/jetrix/autoupdate/";  // should read this from a property file
    static String newsFileName = "news.txt";

    static boolean downloadFailed = false;
    static boolean displayNews = false;


    public static void main(String[] argv) throws IOException
    {
        getUpdate();

        for (int i = 0; i < update.size(); i++)
        {
            StringTokenizer st = new StringTokenizer((String)update.elementAt(i), " \t");
            String fileName = st.nextToken();
            long chksum     = Long.parseLong(st.nextToken());
            long size       = Long.parseLong(st.nextToken());

            long localSum = getFileCRC32(fileName);

            if (chksum != localSum)
            {
                downloadFile(fileName, chksum);
                if (fileName.equals(newsFileName)) displayNews = true;
            }

        }

        if (displayNews) displayNews();
        if (downloadFailed)
        {
            System.out.println("\nDownload failed. Please run again this update.\nContact the update server administrator if the problem persists.");
        }
        else
        {
            System.out.println("\nUpdate completed");
        }

    }


    public static void getUpdate() throws IOException
    {
        URL updateList = new URL(basedir + "update.crc");

        System.out.println("Connecting to update server...");

        HttpURLConnection conn = (HttpURLConnection)updateList.openConnection();
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        System.out.println("Reading update file...");

        String line = br.readLine();

        while (line != null)
        {
            update.addElement(line);
            line = br.readLine();
        }
    }


    public static void downloadFile(String filename, long remoteFileCRC) throws IOException
    {
        URL updateList = new URL(basedir+filename.replace('\\','/'));

        HttpURLConnection conn = (HttpURLConnection)updateList.openConnection();
        BufferedInputStream bis = new BufferedInputStream( conn.getInputStream() );

        File localFile = new File(filename + ".new");
        File parent = localFile.getParentFile();
        if (parent != null) { parent.mkdirs(); }

        FileOutputStream fos = new FileOutputStream(localFile);

        System.out.print("Receiving " + filename);

        int b = bis.read();

        while (b!=-1)
        {
            fos.write(b);
            b = bis.read();
        }

        fos.close();
        bis.close();


        // CRC Check
        long downloadedFileCRC = getFileCRC32(localFile);

        String blank = new String(new char[50 - filename.length()]);

        if (downloadedFileCRC != remoteFileCRC)
        {
            System.out.println(blank + "  [FAILED]");
            downloadFailed = true;
            localFile.delete();
        }
        else
        {
            System.out.println(blank + "  [  OK  ]");
            localFile.renameTo( new File(filename));
        }
    }


    static public long getFileCRC32(File f) throws IOException
    {
        if ( f.exists() && f.isFile() )
        {
            FileInputStream fis = new FileInputStream(f);

            CRC32 check = new CRC32();

            int b = fis.read();

            while(b != -1)
            {
                b = fis.read();
                check.update(b);
            }

            fis.close();

            return check.getValue();
        }
        else
        {
            return 0;
        }
    }

    static public long getFileCRC32(String filename) throws IOException
    {
        File f = new File(filename);
        return getFileCRC32(f);
    }


    public static void displayNews() throws IOException
    {
        System.out.println("\n");

        BufferedReader br = new BufferedReader(new FileReader(newsFileName) );

        String line = br.readLine();

        while (line!=null)
        {
            System.out.println(line);
            line = br.readLine();
        }

        br.close();
    }

}
