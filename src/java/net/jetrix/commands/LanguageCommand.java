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

package net.jetrix.commands;

import java.util.*;
import net.jetrix.*;
import net.jetrix.messages.*;

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

    public String getUsage(Locale locale)
    {
        return "/language <" + Language.getText("command.params.lancode", locale) + ">";
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command.language.description", locale);
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client)m.getSource();

        if (m.getParameterCount() >= 1)
        {
            String language = m.getParameter(0);
            Locale l = new Locale(language);

            if (Language.isSupported(l))
            {
                client.getPlayer().setLocale(l);
                client.getProtocol().setLocale(l);

                PlineMessage response = new PlineMessage();
                response.setKey("command.language.changed", new Object[] { l.getDisplayLanguage(l) });
                client.sendMessage(response);
            }
            else
            {
                client.getPlayer().setLocale(l);
                PlineMessage response = new PlineMessage();
                response.setKey("command.language.not_supported");
                client.sendMessage(response);
            }
        }
        else
        {
            // not enough parameters
            String message = "<red>" + m.getCommand() + "<blue> <lancode>";
            PlineMessage response = new PlineMessage(message);
            client.sendMessage(response);
        }
    }

}
