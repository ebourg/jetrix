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

package net.jetrix.messages;

import net.jetrix.*;
import java.util.*;

/**
 * A generic internationalized text message.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public abstract class TextMessage extends ChannelMessage
{
    private String text;
    private String key;
    private Object params[];
    private Map texts;

    public String getText()
    {
        Locale defaultLocale = null;

        if (Server.getInstance() != null)
        {
            defaultLocale = Server.getInstance().getConfig().getLocale();
        }
        else
        {
            defaultLocale = Locale.getDefault();
        }

        return getText(defaultLocale);
    }

    public String getText(Locale locale)
    {
        if (key == null)
        {
            return text;
        }
        else
        {
            if (texts == null) { texts = new HashMap(); }

            String s = (String)texts.get(locale);

            if (s == null)
            {
                s = Language.getText(key, params, locale);
                texts.put(locale, s);
            }

            return s;
        }
    }

    public void setText(String text)
    {
        this.text = text;
        this.key = null;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public void setKey(String key, Object[] params)
    {
        this.key = key;
        this.params = params;
    }

}
