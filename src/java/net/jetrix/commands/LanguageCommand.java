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
    public String[] getAliases()
    {
        return (new String[]{"language", "lang"});
    }

    public int getAccessLevel()
    {
        return AccessLevel.PLAYER;
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
        Client client = (Client) m.getSource();

        if (m.getParameterCount() >= 1)
        {
            // read the specified locale
            String language = m.getParameter(0);
            Locale locale = new Locale(language);

            if (Language.isSupported(locale))
            {
                // change the locale
                client.getUser().setLocale(locale);

                PlineMessage response = new PlineMessage();
                response.setKey("command.language.changed", locale.getDisplayLanguage(locale));
                client.sendMessage(response);
            }
            else
            {
                PlineMessage response = new PlineMessage();
                response.setKey("command.language.not_supported");
                client.sendMessage(response);
            }
        }
        else
        {
            // list all available locales
            Iterator locales = Language.getLocales().iterator();

            PlineMessage header = new PlineMessage();
            header.setKey("command.language.available");
            client.sendMessage(header);

            while (locales.hasNext())
            {
                Locale locale = (Locale) locales.next();
                PlineMessage line = new PlineMessage();
                line.setKey("command.language.list_format", locale.getLanguage(), locale.getDisplayLanguage(client.getUser().getLocale()));
                client.sendMessage(line);
            }
        }
    }

}
