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

package net.jetrix.listeners;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import net.jetrix.*;
import net.jetrix.config.*;
import net.jetrix.clients.*;
import net.jetrix.messages.*;
import net.jetrix.protocols.*;

/**
 * Listener for tetrinet and tetrifast clients.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TetrinetListener extends ClientListener
{
    private ServerConfig serverConfig;

    private ServerSocket serverSocket;
    private Socket socket;
    private Logger logger = Logger.getLogger("net.jetrix");

    public String getName()
    {
        return "tetrinet & tetrifast";
    }

    public int getPort()
    {
        return Server.getInstance().getConfig().getPort();
    }

    public Client getClient(Socket socket) throws Exception
    {
        String init = null;

        try
        {
            init = readLine(socket);
        }
        catch (IOException e) { e.printStackTrace(); }

        String dec = decode(init);

        // init string parsing "tetrisstart <nickname> <version>"
        StringTokenizer st = new StringTokenizer(dec, " ");
        List tokens = new ArrayList();

        while (st.hasMoreTokens())
        {
            tokens.add(st.nextToken());
        }

        if (tokens.size() > 3)
        {
            return null;
        }

        TetrinetClient client = new TetrinetClient();
        User user = new User();
        user.setName((String)tokens.get(1));
        client.setSocket(socket);
        client.setUser(user);
        client.setVersion((String)tokens.get(2));
        if (((String)tokens.get(0)).equals("tetrisstart"))
        {
            client.setProtocol(ProtocolManager.getInstance().getProtocol("net.jetrix.protocols.TetrinetProtocol"));
        }
        else if (((String)tokens.get(0)).equals("tetrifaster"))
        {
            client.setProtocol(ProtocolManager.getInstance().getProtocol("net.jetrix.protocols.TetrifastProtocol"));
        }
        else
        {
            return null;
        }

        if (tokens.size() > 3)
        {
            Message m = new NoConnectingMessage("No space allowed in nickname !");
            client.sendMessage(m);
            return null;
        }

        return client;
    }


    /**
     * Decodes TetriNET client initialization string
     *
     * @param initString initialization string
     *
     * @return decoded string
     *
     * @throws UnknownEncryptionException
     */
    protected String decode(String initString)
    {
        if (initString.length() % 2 != 0)
        {
            // Invalid Init String: odd length
            return null;
        }

        int[] dec = new int[initString.length() / 2];

        try
        {
            for (int i = 0; i < dec.length; i++)
            {
                dec[i] = Integer.parseInt(initString.substring(i * 2, i * 2 + 2), 16);
            }
        }
        catch (NumberFormatException e)
        {
            // Invalid Init String: illegal characters
            return null;
        }

        // tetrinet test
        char[] data = "tetrisstar".toCharArray();
        int[]  hashString = new int[data.length];

        for (int i = 0; i < data.length; i++)
        {
            hashString[i] = ((data[i] + dec[i]) % 255) ^ dec[i + 1];
        }

        int hashLength = 5;

        for (int i = 5; i == hashLength && i > 0; i--)
        {
            for (int j = 0; j < data.length - hashLength; j++)
            {
                if (hashString[j] != hashString[j + hashLength]) { hashLength--; }
            }
        }

        // tetrifast test
        if (hashLength == 0)
        {
            data = "tetrifaste".toCharArray();
            hashString = new int[data.length];

            for (int i = 0; i < data.length; i++)
            {
                hashString[i] = ((data[i] + dec[i]) % 255) ^ dec[i + 1];
            }

            hashLength = 5;

            for (int i = 5; i == hashLength && i > 0; i--)
            {
                for (int j = 0; j < data.length - hashLength; j++)
                {
                    if (hashString[j] != hashString[j + hashLength]) { hashLength--; }
                }
            }
        }

        if (hashLength == 0)
        {
            // Invalid Init String: decoding failed
            return null;
        }

        StringBuffer s = new StringBuffer();

        for (int i = 1; i<dec.length; i++)
        {
            s.append( (char)(((dec[i] ^ hashString[(i - 1) % hashLength]) + 255 - dec[i - 1]) % 255) );
        }

        return s.toString().replace((char)0, (char)255);
    }

    public String readLine(Socket socket) throws IOException
    {
        int readChar;
        Reader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuffer input = new StringBuffer();

        while ((readChar = in.read()) != -1 && readChar != 255)
        {
            if (readChar != 10 && readChar != 13)
            {
                input.append((char)readChar);
            }
        }

        if (readChar == -1) throw new IOException("client disconnected");

        return input.toString();
    }

    protected String encode(String nickname, int[] ip)
    {
        StringBuffer result = new StringBuffer();
        int offset = 128;
        result.append(Integer.toHexString(offset));

        char[] x = new Integer(54 * ip[0] + 41 * ip[1] + 29 * ip[2] + 17 * ip[3]).toString().toCharArray();
        char[] s = ("tetrisstart " + nickname + " 1.13").toCharArray();

        result.append(arrayToHex(xor(shift(s, offset), x)));

        return result.toString().toUpperCase();
    }

    protected char[] xor(char[] array, char[] offset)
    {
        for (int i = 0; i < array.length; i++)
        {
            array[i] = (char)( array[i] ^ offset[i % offset.length] );
        }

        return array;
    }

    protected char[] shift(char[] array, int offset)
    {
        for (int i = 0; i < array.length; i++)
        {
            array[i] = (char)( (char)(array[i] + offset) % 256 );
        }

        return array;
    }

    protected String arrayToHex(char[] array)
    {
        StringBuffer result = new StringBuffer();

        for (int i = 0; i < array.length; i++)
        {
            result.append(Integer.toHexString(array[i]));
        }

        return result.toString();
    }

}
