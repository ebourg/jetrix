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

package net.jetrix.protocols;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * An IRC message.
 *
 * @since 0.2
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class IRCMessage
{
    private String nick;
    private String user;
    private String host;
    private String command;
    private int reply;
    private List<String> parameters = new ArrayList<String>();

    public IRCMessage() { }

    public IRCMessage(String command)
    {
        this.command = command;
    }

    public IRCMessage(int reply)
    {
        this.reply = reply;
    }

    /**
     * Parse the specified line into an IRC message.
     */
    public static IRCMessage parse(String line)
    {
        IRCMessage message = new IRCMessage();

        StringTokenizer st = new StringTokenizer(line);

        if (!st.hasMoreTokens())
        {
            return message;
        }

        // get the first token
        String token = st.nextToken();

        // is this a prefix ?
        if (token.startsWith(":"))
        {
            message.setPrefix(token.substring(1));
            token = st.nextToken();
        }

        // is this a numeric reply ?
        try
        {
            message.setReply(Integer.parseInt(token));
        }
        catch (Exception e)
        {
            message.setCommand(token);
        }

        // parameters
        boolean completed = false;
        while (st.hasMoreTokens() && !completed)
        {
            token = st.nextToken();

            if (token.startsWith(":"))
            {
                message.addParameter(line.substring(line.indexOf(":", 1) + 1));
                completed = true;
            }
            else
            {
                message.addParameter(token);
            }
        }

        return message;
    }

    public String getNick()
    {
        return nick;
    }

    public void setNick(String nick)
    {
        this.nick = nick;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public String getPrefix()
    {
        StringBuilder prefix = new StringBuilder();
        prefix.append(nick);

        if (user != null)
        {
            prefix.append("!").append(user);
        }

        if (host != null)
        {
            prefix.append("@").append(host);
        }

        return prefix.toString();
    }

    public void setPrefix(String prefix)
    {
        if (prefix != null)
        {
            int separator1 = prefix.indexOf("!");
            int separator2 = prefix.indexOf("@");

            if (separator1 == -1 && separator2 == -1)
            {
                // missing user & host
                nick = prefix;
            }
            else if (separator2 == -1)
            {
                // missing host
                nick = prefix.substring(0, separator1);
                user = prefix.substring(separator1 + 1);
            }
            else if (separator1 == -1)
            {
                // missing user
                nick = prefix.substring(0, separator2);
                host = prefix.substring(separator2 + 1);
            }
            else
            {
                nick = prefix.substring(0, separator1);
                user = prefix.substring(separator1 + 1, separator2);
                host = prefix.substring(separator2 + 1);
            }
        }
    }

    public String getCommand()
    {
        return command;
    }

    public void setCommand(String command)
    {
        this.command = command;
    }

    public boolean isCommand(String command)
    {
        return command.equals(this.command);
    }

    public int getReply()
    {
        return reply;
    }

    public void setReply(int reply)
    {
        this.reply = reply;
    }

    public List<String> getParameters()
    {
        return parameters;
    }

    public void setParameters(List<String> parameters)
    {
        this.parameters = parameters;
    }

    public String getParameter(int i)
    {
        return parameters.get(i);
    }

    public void addParameter(String param)
    {
        parameters.add(param);
    }

    public int getParameterCount()
    {
        return parameters.size();
    }

    /**
     * Return the string representation of this message. The resulting string
     * complies with the IRC specification and can be used directly in the
     * communication between servers and clients.
     */
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();

        // prefix
        if (nick != null)
        {
            buffer.append(":").append(getPrefix());
            buffer.append(" ");
        }

        // command
        if (command != null)
        {
            buffer.append(command);
        }
        else
        {
            buffer.append(reply);
        }

        // parameters
        for (int i = 0; i < parameters.size() - 1; i++)
        {
            buffer.append(" ").append(parameters.get(i));
        }

        // add the last parameter
        if (parameters.size() > 0)
        {
            buffer.append(" ");

            String lastParam = String.valueOf(parameters.get(parameters.size() - 1));
            if (lastParam.indexOf(" ") != -1 || lastParam.startsWith(":"))
            {
                buffer.append(":");
            }
            buffer.append(lastParam);
        }

        return buffer.toString();
    }
}
