/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2003  Emmanuel Bourg
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

package net.jetrix.clients;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import net.jetrix.*;
import net.jetrix.config.*;
import net.jetrix.messages.*;
import net.jetrix.protocols.*;

/**
 * Client for the query protocol on port 31457.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class QueryClient extends TetrinetClient
{
    private Message firstMessage;

    public void run()
    {
        if (log.isLoggable(Level.FINE))
        {
            log.fine("Client started " + this);
        }

        connectionTime = new Date();

        Server server = Server.getInstance();
        if (server != null)
        {
            serverConfig = server.getConfig();
        }

        try
        {
            process(firstMessage);

            while (!disconnected && serverConfig.isRunning())
            {
                process(receiveMessage());
            }
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
        finally
        {
            try { in.close(); } catch (IOException e) { e.printStackTrace(); }
            try { out.close(); } catch (IOException e) { e.printStackTrace(); }
            try { socket.close(); } catch (IOException e) { e.printStackTrace(); }
        }

        if (log.isLoggable(Level.FINE))
        {
            log.fine("Client disconnected (" + getInetAddress().getHostAddress() + ")");
        }
    }

    private void process(Message m)
    {
        if (m != null && m instanceof CommandMessage)
        {
            CommandMessage command = (CommandMessage) m;
            PlineMessage response = new PlineMessage();

            if ("listuser".equals(command.getCommand()))
            {
                // "<nick>" "<team>" "<version>" <slot> <state> <auth> "<channelname>"
                StringBuffer message = new StringBuffer();
                for (Client client : ClientRepository.getInstance().getClients())
                {
                    User user = client.getUser();
                    message.append("\"");
                    message.append(user.getName());
                    message.append("\" \"");
                    message.append(user.getTeam() == null ? "" : user.getTeam());
                    message.append("\" \"");
                    message.append(client.getVersion());
                    message.append("\" ");
                    message.append(client.getChannel().getClientSlot(client));
                    message.append(" ");
                    message.append(user.isPlaying() ? "1" : "0");
                    message.append(" ");
                    message.append(user.getAccessLevel());
                    message.append(" \"");
                    message.append(client.getChannel().getConfig().getName());
                    message.append("\"");
                    message.append(QueryProtocol.EOL);
                }

                response.setText(message.toString());
            }
            else if ("listchan".equals(command.getCommand()))
            {
                // "<name>" "<description>" <playernum> <playermax> <priority> <status>
                StringBuffer message = new StringBuffer();
                for (Channel channel : ChannelManager.getInstance().channels())
                {
                    ChannelConfig config = channel.getConfig();

                    message.append("\"");
                    message.append(config.getName());
                    message.append("\" \"");
                    message.append(config.getDescription());
                    message.append("\" ");
                    message.append(channel.getPlayerCount());
                    message.append(" ");
                    message.append(config.getMaxPlayers());
                    message.append(" 0 ");
                    message.append(channel.getGameState().getValue() + 1);
                    message.append(QueryProtocol.EOL);
                }

                response.setText(message.toString());
            }
            else if ("playerquery".equals(command.getCommand()))
            {
                response.setText("Number of players logged in: " + ClientRepository.getInstance().getClientCount());
            }
            else if ("version".equals(command.getCommand()))
            {
                response.setText("Jetrix/" + ServerConfig.VERSION + QueryProtocol.EOL);
            }

            send(response);
        }
        else
        {
            NoConnectingMessage noconnecting = new NoConnectingMessage();
            noconnecting.setText("Wrong command");
            send(noconnecting);
            disconnected = true;
        }
    }

    /**
     * Set the first command issued by this client.
     */
    public void setFirstMessage(Message firstMessage)
    {
        this.firstMessage = firstMessage;
    }

    public void send(Message m)
    {
        String rawMessage = m.getRawMessage(getProtocol(), null);

        try
        {
            out.write(rawMessage + QueryProtocol.EOL, 0, rawMessage.length() + 1);
            out.flush();

            if (log.isLoggable(Level.FINEST))
            {
                log.finest("> " + rawMessage);
            }
        }
        catch (Exception e)
        {
            if (log.isLoggable(Level.FINE))
            {
                log.fine(e.getMessage());
            }
        }
    }

    public boolean supportsAutoJoin()
    {
        return false;
    }

    protected boolean isAsynchronous()
    {
        return false;
    }

}
