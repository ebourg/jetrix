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

package net.jetrix;

import java.util.*;
import java.text.*;

public class Language
{
    private static Language instance = new Language();
    private Map bundles;

    private Language()
    {
        bundles = new HashMap();
    }

    public static Language getInstance()
    {
        return instance;
    }

    public ResourceBundle load(Locale locale)
    {
        ResourceBundle bundle = PropertyResourceBundle.getBundle("lang.jetrix", locale);
        bundles.put(locale, bundle);
        return bundle;
    }

    public ResourceBundle getResourceBundle(Locale locale)
    {
        return (ResourceBundle)bundles.get(locale);
    }

    public static boolean isSupported(Locale locale)
    {
        ResourceBundle bundle = instance.getResourceBundle(locale);
        if (bundle == null)
        {
            bundle = instance.load(locale);
        }
        return bundle.getLocale().equals(locale);
    }

    public static String getText(String key, Locale locale)
    {
        String text = null;
        try
        {
            ResourceBundle bundle = instance.getResourceBundle(locale);
            if (bundle == null)
            {
                bundle = instance.load(locale);
            }
            text = bundle.getString(key);
        }
        catch (Exception e)
        {
            text = "["+key+"]";
        }

        return text;
    }

    public static String getText(String key, Object[] arguments, Locale locale)
    {
        return MessageFormat.format( getText(key, locale), arguments );
    }

}
