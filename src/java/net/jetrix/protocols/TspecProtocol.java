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

package net.jetrix.protocols;

import net.jetrix.*;
import net.jetrix.messages.*;

import java.util.*;

/**
 * An abstract protocol to communicate with a client.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TspecProtocol extends TetrinetProtocol
{
    /**
     * Return the name of this protocol
     */
    public String getName()
    {
        return "tspec";
    }

    /**
     * Parse the specified string and return the corresponding server
     * message for this protocol.
     */
    public Message getMessage(String line)
    {
        Message message = null;

        if (line.startsWith("pline"))
        {
            SmsgMessage smsg = new SmsgMessage();

            if (line.indexOf("//") == 8)
            {
                // public comment
                smsg.setPrivate(false);
                if (line.length() > 11)
                {
                    smsg.setText(line.substring(11));
                }

                message = smsg;
            }
            else if (line.indexOf("/") != 8)
            {
                // private comment
                smsg.setPrivate(true);
                if (line.length() > 8)
                {
                    smsg.setText(line.substring(8));
                }

                message = smsg;
            }
        }

        return message != null ? message : super.getMessage(line);
    }

    /**
     * Translate the specified message into a string that will be sent
     * to a client using this protocol.
     */
    public String translate(Message m, Locale locale)
    {
        if (m instanceof SpectatorListMessage) { return translate((SpectatorListMessage) m, locale); }
        if (m instanceof SmsgMessage) { return translate((SmsgMessage) m, locale); }
        else
        {
            return super.translate(m, locale);
        }
    }

    public String translate(SpectatorListMessage m, Locale locale)
    {
        StringBuffer message = new StringBuffer();
        message.append("speclist #");
        message.append(m.getChannel());

        for (String spectator : m.getSpectators())
        {
            message.append(" ");
            message.append(spectator);
        }
        
        return message.toString();
    }

    public String translate(SmsgMessage m, Locale locale)
    {
        StringBuffer message = new StringBuffer();
        String name = ((Client) m.getSource()).getUser().getName();

        if (m.isPrivate())
        {
            message.append("smsg ");
            message.append(name);
            message.append(" ");
            message.append(m.getText());
        }
        else
        {
            message.append(super.translate(m, locale));
        }

        return message.toString();
    }

    public String translate(JoinMessage m, Locale locale)
    {
        if (m.getSlot() == 0)
        {
            StringBuffer message = new StringBuffer();
            message.append("specjoin ");
            message.append(m.getName());
            return message.toString();
        }
        else
        {
            return super.translate(m, locale);
        }
    }

    public String translate(LeaveMessage m, Locale locale)
    {
        if (m.getSlot() == 0)
        {
            StringBuffer message = new StringBuffer();
            message.append("specleave ");
            message.append(m.getName());
            return message.toString();
        }
        else
        {
            return super.translate(m, locale);
        }
    }

    public String toString()
    {
        return "[Protocol name=" + getName() + "]";
    }

}
