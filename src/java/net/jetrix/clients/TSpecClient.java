/**
 * Jetrix TetriNET Server
 * Copyright (C) 2004  Emmanuel Bourg
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

import java.io.IOException;

import net.jetrix.Message;
import net.jetrix.messages.channel.SmsgMessage;

/**
 * Spectator client.
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TSpecClient extends TetrinetClient
{
    public static final int TETRIX_MODE = 0;
    public static final int TSERV_MODE = 1;

    private int mode;

    public void setMode(int mode)
    {
        this.mode = mode;
    }

    public int getMode()
    {
        return mode;
    }

    protected boolean isAsynchronous()
    {
        return false;
    }

    public Message receive() throws IOException
    {
        Message message = super.receive();

        if (message instanceof SmsgMessage && mode == TSERV_MODE)
        {
            SmsgMessage smsg = (SmsgMessage) message;
            if (smsg.isPrivate())
            {
                // echo the message to the player
                send(message);
            }
        }

        return message;
    }
}
