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

package org.lfjr.jts.commands;

import java.util.*;
import org.lfjr.jts.*;

/**
 * Set the language of the user.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class LanguageCommand implements Command
{
    private int accessLevel = 0;

    public String[] getAliases()
    {
        return (new String[] { "language", "lang" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage()
    {
        return "/language <lancode>";
    }

    public String getDescription()
    {
        return "Set the language of the user.";
    }

    public void execute(Message m)
    {
        TetriNETClient client = (TetriNETClient)m.getSource();
        
        if (m.getParameterCount() > 2)
        {
            String language = m.getStringParameter(2);
            Locale l = new Locale(language);
            
            if (Language.isSupported(l))
            {
                client.getPlayer().setLocale(l);
                Message response = new Message(Message.MSG_PLINE);
                String message = ChatColors.gray + "Language changed to " + ChatColors.bold + l.getDisplayLanguage(l);
                response.setParameters(new Object[] { new Integer(0), message });
                client.sendMessage(response);
            }
            else
            {
                client.getPlayer().setLocale(l);
                Message response = new Message(Message.MSG_PLINE);
                String message = ChatColors.gray + "Language not supported";
                response.setParameters(new Object[] { new Integer(0), message });
                client.sendMessage(response);
            }            
        }
        else
        {
            // not enough parameters
            Message response = new Message(Message.MSG_PLINE);
            String message = ChatColors.red + m.getStringParameter(1) + ChatColors.blue + " <lancode>";
            response.setParameters(new Object[] { new Integer(0), message });
            client.sendMessage(response);
        }
    }

}
