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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.jetrix.Message;
import net.jetrix.protocols.QueryProtocol;

/**
 * Agent performing query requests on a TetriNET server.
 *
 * @see <a href="See http://jetrix.sourceforge.net/dev-guide.php#section2-4">Query Protocol Documentation</a>
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

    private Logger log = Logger.getLogger("net.jetrix");

    public void connect(String hostname) throws IOException
    {
        this.hostname = hostname;
        socket = new Socket();
        socket.connect(new InetSocketAddress(hostname, 31457), 5000);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "ISO-8859-1"));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "ISO-8859-1"));
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
        // prepare the pattern matcher
        Pattern pattern = Pattern.compile("\"(.*)\" \"(.*)\" ([0-9]+) ([0-9]+) ([0-9]+|N/A) ([0-9]+)");

        // send the command
        send("listchan");

        List<ChannelInfo> channels = new ArrayList<ChannelInfo>();

        // read the result
        String line = null;
        QueryProtocol protocol = new QueryProtocol();
        while (!QueryProtocol.OK.equals(line = protocol.readLine(in)))
        {
            Matcher matcher = pattern.matcher(line);

            if (matcher.matches())
            {
                int i = 1;
                ChannelInfo channel = new ChannelInfo();
                channel.setName(matcher.group(i++));
                channel.setDescription(matcher.group(i++));
                channel.setPlayernum(Integer.parseInt(matcher.group(i++)));
                channel.setPlayermax(Integer.parseInt(matcher.group(i++)));
                String priority = matcher.group(i++);
                if (!"N/A".equals(priority))
                {
                    channel.setPriority(Integer.parseInt(priority));
                }
                channel.setStatus(Integer.parseInt(matcher.group(i++)));

                channels.add(channel);
            }
            else
            {
                log.warning("Invalid response for the listchan message (" + hostname + ") : " + line);
            }
        }

        return channels;
    }

    public List<PlayerInfo> getPlayers() throws IOException
    {
        // prepare the pattern matcher
        Pattern pattern = Pattern.compile("\"(.*)\" \"(.*)\" \"(.*)\" ([0-9]+) ([0-9]+) ([0-9]+) \"(.*)\"");

        // send the command
        send("listuser");

        List<PlayerInfo> players = new ArrayList<PlayerInfo>();

        // read the result
        String line = null;
        QueryProtocol protocol = new QueryProtocol();
        while (!QueryProtocol.OK.equals(line = protocol.readLine(in)))
        {
            Matcher matcher = pattern.matcher(line);

            if (matcher.matches())
            {
                int i = 1;
                PlayerInfo player = new PlayerInfo();
                player.setNick(matcher.group(i++));
                player.setTeam(matcher.group(i++));
                player.setVersion(matcher.group(i++));
                player.setSlot(Integer.parseInt(matcher.group(i++)));
                player.setStatus(Integer.parseInt(matcher.group(i++)));
                player.setAuthenticationLevel(Integer.parseInt(matcher.group(i++)));
                player.setChannel(matcher.group(i++));

                players.add(player);
            }
            else
            {
                log.warning("Invalid response for the listuser message (" + hostname + ") : " + line);
            }
        }

        return players;
    }

    /**
     * Return the round trip time to the server in milliseconds.
     */
    public int getPing() throws IOException
    {
        long time = System.currentTimeMillis();
        getVersion();
        return (int) (System.currentTimeMillis() - time);
    }

}
