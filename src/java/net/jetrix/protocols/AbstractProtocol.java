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

import net.jetrix.Protocol;

import java.io.IOException;
import java.io.Reader;

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
    public String readLine(Reader in) throws IOException
    {
        StringBuilder input = new StringBuilder();

        int readChar;
        while ((readChar = in.read()) != -1 && readChar != getEOL() && readChar != 0x0A && readChar != 0x0D)
        {
            if (readChar != 0x0A && readChar != 0x0D)
            {
                input.append((char) readChar);
            }
        }

        if (readChar == -1)
        {
            throw new IOException("End of stream");
        }

        return input.toString();
    }

    public String toString()
    {
        return "[Protocol name=" + getName() + "]";
    }
}
