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

package net.jetrix.agent;

import net.jetrix.protocols.QueryProtocol;
import net.jetrix.Message;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class QueryAgent implements Agent
{
    private String hostname;
    private Socket socket;
    private BufferedReader in;
    private Writer out;

    public void connect(String hostname) throws IOException
    {
        this.hostname = hostname;
        socket = new Socket(hostname, 31457);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        socket.setSoTimeout(10000);
    }

    public void disconnect() throws IOException
    {
        if (socket != null)
        {
            socket.close();
        }
    }

    public void send(String message) throws IOException
    {
        out.write(message);
        out.write(0xFF);
        out.flush();
    }

    public void send(Message message) throws IOException
    {
        throw new UnsupportedOperationException("QueryAgent is not asynchonous");
    }

    public void receive(Message message) throws IOException
    {
        throw new UnsupportedOperationException("QueryAgent is not asynchonous");
    }

    /**
     * Fetch the information about the server.
     */
    public QueryInfo getInfo() throws IOException
    {
        QueryInfo info = new QueryInfo();

        info.setHostname(hostname);
        info.setVersion(getVersion());
        info.setPing(getPing());
        info.setChannels(getChannels());
        info.setPlayers(getPlayers());

        return info;
    }

    /**
     * Return the version of the server.
     */
    public String getVersion() throws IOException
    {
        // send the command
        send("version");

        // read the result
        String version = in.readLine();
        in.readLine();

        return version;
    }

    /**
     * Return the number of players logged in (usualy in the first channel only).
     */
    public int getPlayerNumber() throws IOException
    {
        // send the command
        send("playerquery");

        // read the result
        String line = in.readLine();

        if (line.startsWith("Number of players logged in: "))
        {
            return Integer.parseInt(line.substring(line.indexOf(":") + 1).trim());
        }
        else
        {
            throw new IOException("Invalid response : " + line);
        }
    }

    public List<ChannelInfo> getChannels() throws IOException
    {
        // send the command
        send("listchan");

        List<ChannelInfo> channels = new ArrayList<ChannelInfo>();

        // read the result
        String line = null;
        while (!(line = in.readLine()).equals(QueryProtocol.OK))
        {
            List<String> tokens = parseQuotedTokens(line);

            ChannelInfo channel = new ChannelInfo();
            channel.setName(tokens.get(0));
            channel.setDescription(tokens.get(1));
            channel.setPlayernum(Integer.parseInt(tokens.get(2)));
            channel.setPlayermax(Integer.parseInt(tokens.get(3)));
            channel.setPriority(Integer.parseInt(tokens.get(4)));
            channel.setStatus(Integer.parseInt(tokens.get(5)));

            channels.add(channel);
        }

        return channels;
    }

    public List<PlayerInfo> getPlayers() throws IOException
    {
        // send the command
        send("listuser");

        List<PlayerInfo> players = new ArrayList<PlayerInfo>();

        // read the result
        String line = null;
        while (!(line = in.readLine()).equals(QueryProtocol.OK))
        {
            List<String> tokens = parseQuotedTokens(line);

            PlayerInfo player = new PlayerInfo();
            player.setNick(tokens.get(0));
            player.setTeam(tokens.get(1));
            player.setVersion(tokens.get(2));
            player.setSlot(Integer.parseInt(tokens.get(3)));
            player.setStatus(Integer.parseInt(tokens.get(4)));
            player.setAuthenticationLevel(Integer.parseInt(tokens.get(5)));
            player.setChannel(tokens.get(6));

            players.add(player);
        }

        return players;
    }

    /**
     * Return the ping time to the server in milliseconds.
     */
    public long getPing() throws IOException
    {
        long time = System.currentTimeMillis();
        getVersion();
        return (System.currentTimeMillis() - time) / 2;
    }

    List<String> parseQuotedTokens(String s)
    {
        List<String> tokens = new ArrayList<String>();

        if (s == null)
        {
            return new ArrayList<String>();
        }

        StringBuffer token = new StringBuffer();
        boolean quote = false;

        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);

            if (c == '"')
            {
                if (quote)
                {
                    tokens.add(token.toString());
                    token = new StringBuffer();
                }

                quote = !quote;
            }
            else if (c == ' ')
            {
                if (quote)
                {
                    token.append(c);
                }
                else if (token.length() > 0)
                {
                    tokens.add(token.toString());
                    token = new StringBuffer();
                }
            }
            else
            {
                token.append(c);
                if (i == s.length() - 1)
                {
                    tokens.add(token.toString());
                }
            }
        }

        return tokens;
    }
}
