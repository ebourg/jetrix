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

package net.jetrix;

import java.util.*;
import java.text.*;

/**
 * Helper class to handle and retrieve localized strings.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class Language
{
    private static Language instance = new Language();
    private Map<Locale,ResourceBundle> bundles;
    private static String resource = "jetrix";

    private Language()
    {
        bundles = new HashMap<Locale, ResourceBundle>();
    }

    /**
     * Return the unique instance of this class.
     */
    public static Language getInstance()
    {
        return instance;
    }

    /**
     * Load and return the <tt>ResourceBundle</tt> for the specified locale.
     * Bundles are cached in a local Map.
     *
     * @param locale the locale of the returned bundle if available
     */
    public ResourceBundle load(Locale locale)
    {
        ResourceBundle bundle = PropertyResourceBundle.getBundle(resource, locale);
        bundles.put(locale, bundle);
        return bundle;
    }

    /**
     * Return the <tt>ResourceBundle</tt> for the specified locale.
     *
     * @param locale the locale of the bundle to return
     */
    public ResourceBundle getResourceBundle(Locale locale)
    {
        return bundles.get(locale);
    }

    /**
     * Tell if the specified locale has a corresponding resource file available.
     *
     * @param locale the locale to test
     *
     * @return <tt>true</tt> if the locale is supported, <tt>false</tt> if not.
     */
    public static boolean isSupported(Locale locale)
    {
        ResourceBundle bundle = instance.getResourceBundle(locale);
        if (bundle == null)
        {
            bundle = PropertyResourceBundle.getBundle(resource, locale);
        }
        return (bundle != null && bundle.getLocale().equals(locale));
    }

    /**
     * Return the list of languages supported by the server.
     */
    public static Collection<Locale> getLocales()
    {
        Collection<Locale> locales = new ArrayList<Locale>();

        String[] languages = Locale.getISOLanguages();

        for (int i = 0; i < languages.length; i++)
        {
            Locale locale = new Locale(languages[i]);
            if (isSupported(locale))
            {
                locales.add(locale);
            }
        }

        return locales;
    }

    /**
     * Return the specified localized text for a given locale.
     *
     * @param key the text key in the resource bundle
     * @param locale the locale of the message
     */
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
            text = "[" + locale + ":" + key + "]";
        }

        return text;
    }

    /**
     * Return the specified localized text for a given locale and replace the
     * parameters with an array of arguments.
     *
     * @since 0.2
     *
     * @param key the text key in the resource bundle
     * @param locale the locale of the message
     * @param arguments the array of arguments
     */
    public static String getText(String key, Locale locale, Object ... arguments)
    {
        // localize the arguments
        Object[] arguments2 = new Object[arguments.length];
        for (int i = 0; i < arguments.length; i++)
        {
            if (arguments[i] instanceof String && ((String) arguments[i]).startsWith("key:"))
            {
                arguments2[i] = getText(((String) arguments[i]).substring(4), locale);
            }
            else
            {
                arguments2[i] = arguments[i];
            }
        }

        return MessageFormat.format(getText(key, locale), arguments2);
    }

}
