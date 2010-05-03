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

package net.jetrix.protocols;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.jetrix.Protocol;

/**
 * Abstract protocol implementing the readLine(Reader) method.
 *
 * @since 0.3
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public abstract class AbstractProtocol implements Protocol
{
    /** Maximum size allowed for a message frame. */
    private static final int MAX_SIZE = 8192;

    /** Maximum delay allowed for parsing a message frame (in milliseconds). */
    private static final int MAX_DELAY = 10000;
    
    /**
     * Reads the next message frame from the specified input stream.
     * No charset decoding is performed at this stage.
     */
    public byte[] readFrame(InputStream in) throws IOException
    {
        ByteArrayOutputStream input = new ByteArrayOutputStream(256);
        
        long time = 0;
        
        int b;
        while ((b = in.read()) != -1 && b != getEOL() && b != 0x0A && b != 0x0D && input.size() < MAX_SIZE)
        {
            input.write(b);
            
            if (input.size() == 1)
            {
                // let's start monitoring the input speed
                time = System.currentTimeMillis();
            }
            else if (System.currentTimeMillis() - time > MAX_DELAY)
            {
                throw new IOException("Slow input detected (" + input.size() + " bytes over " + (System.currentTimeMillis() - time) + "ms)");
            }
        }
        
        if (b == -1)
        {
            throw new IOException("End of stream");
        }
        
        if (input.size() >= MAX_SIZE)
        {
            throw new IOException("Message frame exceeded the " + MAX_SIZE + " limit");
        }
        
        return input.toByteArray();
    }

    public String readLine(InputStream in, String encoding) throws IOException
    {
        return new String(readFrame(in), encoding);
    }

    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Protocol that = (Protocol) o;

        if (!getName().equals(that.getName()))
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return getName().hashCode();
    }

    public String toString()
    {
        return "[Protocol name=" + getName() + "]";
    }
}
