/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001  Emmanuel Bourg
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

package org.lfjr.jts.patcher;

import java.io.*;
import java.util.zip.*;

public class UpdateList
{
    static String path;		
	
    public static void main(String[] argv) throws IOException
    {
    	//String path;
    	
    	if (argv.length > 0) path=argv[0]; else path=".";
    	
    	browseDirectory(new File(path));    	    	    
    }


    public static void browseDirectory(File directory) throws IOException
    {
    	
    	File listeFichiers[] = directory.listFiles();
    
        for (int i=0; i<listeFichiers.length; i++)
        {
    	    File f = listeFichiers[i];
    	    if ( f.isFile() ) 
    	    {
    	    	String name = f.toString().substring(path.toString().length()+1);
    	        System.out.println( name + "\t" + getFileCRC32(f) + "\t" + f.length() );
    	    }
    	    else
    	    {
    	        browseDirectory(f);
    	    }
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

}