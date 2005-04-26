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

package net.jetrix.messages;

import java.util.*;

import net.jetrix.*;

/**
 * A /command.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class CommandMessage extends TextMessage
{
    private String command;
    private List<String> parameters;

    public CommandMessage()
    {
        parameters = new ArrayList<String>();
    }

    public String getCommand()
    {
        return command;
    }

    public void setCommand(String command)
    {
        this.command = command;
    }

    public String getParameter(int i)
    {
        return parameters.get(i);
    }

    /**
     * Return an integer parameter, or the default value if the specified
     * parameter doesn't map to an integer value.
     *
     * @param i            the index of the parameter
     * @param defaultValue the default value
     */
    public int getIntParameter(int i, int defaultValue)
    {
        int value;

        try
        {
            value = Integer.parseInt(parameters.get(i));
        }
        catch (Exception e)
        {
            value = defaultValue;
        }

        return value;
    }

    /**
     * Return an integer parameter, or null if the specified parameter
     * doesn't map to an integer value.
     *
     * @param i the index of the parameter
     */
    public Integer getIntegerParameter(int i)
    {
        Integer value;

        try
        {
            value = Integer.valueOf(parameters.get(i));
        }
        catch (Exception e)
        {
            value = null;
        }

        return value;
    }

    /**
     * Return the Client object associated to the i-th parameter of the command.
     * The client can be specified as a slot number if he is in the same channel
     * as the client issuing the command, or as a case insensitive name. If no
     * client matches the specified parameter a null value is returned.
     */
    public Client getClientParameter(int i)
    {
        Client client = null;
        String param = getParameter(i);

        // check if the parameter is a slot number
        try
        {
            int slot = Integer.parseInt(param);
            if (slot >= 1 && slot <= 6)
            {
                // find the channel of the client issuing this command
                if (getSource() instanceof Client)
                {
                    Channel channel = ((Client) getSource()).getChannel();
                    client = channel.getClient(slot);
                }
            }
        }
        catch (NumberFormatException e) { }

        if (client == null)
        {
            // the client is still null, the parameter may be a playername
            ClientRepository repository = ClientRepository.getInstance();
            client = repository.getClient(param);
        }

        return client;
    }

    /**
     * Returns the channel associated to the i-th parameter of the command. The
     * channel can be specified by a partial name or by its number. If no channel
     * matches the specified parameter a null value is returned.
     */
    public Channel getChannelParameter(int i)
    {
        Channel channel = null;
        String param = getParameter(i);

        try
        {
            // trying to parse the number
            int num = Integer.parseInt(param) - 1;
            channel = ChannelManager.getInstance().getChannel(num);
        }
        catch (NumberFormatException e)
        {
            channel = ChannelManager.getInstance().getChannel(param, true);
        }

        return channel;
    }

    /**
     * Add a parameter to the command.
     */
    public void addParameter(String obj)
    {
        parameters.add(obj);
    }

    /**
     * Return the number of parameters on this command.
     */
    public int getParameterCount()
    {
        return parameters.size();
    }
}
