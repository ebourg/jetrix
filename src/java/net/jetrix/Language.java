/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2004,2010  Emmanuel Bourg
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
    /** The names of the resource bundles imported. */
    private Set<String> bundleNames = new HashSet<String>();

    private Map<Locale, MultiResourceBundle> bundles = new HashMap<Locale, MultiResourceBundle>();

    private static Language instance = new Language();

    /** The default resource bundle containing the server messages. */
    private static final String DEFAULT_RESOURCE = "jetrix";

    private Language()
    {
        addResources(DEFAULT_RESOURCE);
    }

    /**
     * Return the unique instance of this class.
     */
    public static Language getInstance()
    {
        return instance;
    }

    /**
     * Register an extra set of localized messages.
     * 
     * @param name the base name of the resource bundle
     * @since 0.3
     */
    public void addResources(String name)
    {
        bundleNames.add(name);
    }

    /**
     * Load and return the <tt>ResourceBundle</tt> for the specified locale.
     * Bundles are cached in a local Map.
     *
     * @param locale the locale of the returned bundle if available
     */
    protected ResourceBundle load(Locale locale)
    {
        MultiResourceBundle bundle = new MultiResourceBundle(locale);
        bundles.put(locale, bundle);
        return bundle;
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
        MultiResourceBundle bundle = instance.bundles.get(locale);
        if (bundle == null)
        {
            bundle = instance.new MultiResourceBundle(locale);
        }
        return bundle.isSupported();
    }

    /**
     * Return the list of languages supported by the server.
     */
    public static Collection<Locale> getLocales()
    {
        Collection<Locale> locales = new ArrayList<Locale>();

        for (String language : Locale.getISOLanguages())
        {
            Locale locale = new Locale(language);
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
        try
        {
            ResourceBundle bundle = instance.bundles.get(locale);
            if (bundle == null)
            {
                bundle = instance.load(locale);
            }
            
            return bundle.getString(key);
        }
        catch (Exception e)
        {
            return "[" + locale + ":" + key + "]";
        }
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
            arguments2[i] = getLocalizedArgument(locale, arguments[i]);
        }

        return MessageFormat.format(getText(key, locale), arguments2);
    }

    /**
     * Transforms a localized argument into its actual value. Localized
     * arguments start with the "key:" prefix and refers to another message
     * in the resource bundle.
     *
     * @since 0.3
     * 
     * @param locale   the target locale
     * @param argument the argument to transform
     */
    private static Object getLocalizedArgument(Locale locale, Object argument)
    {
        if (argument instanceof String && ((String) argument).startsWith("key:"))
        {
            return getText(((String) argument).substring(4), locale);
        }
        else
        {
            return argument;
        }
    }

    /**
     * A resource bundle merging several property based resource bundles.
     * 
     * @since 0.3
     */
    private class MultiResourceBundle extends ResourceBundle
    {
        private Locale locale;

        private MultiResourceBundle(Locale locale)
        {
            this.locale = locale;
        }

        private PropertyResourceBundle getPropertyResourceBundle(String name) {
            try
            {
                return (PropertyResourceBundle) PropertyResourceBundle.getBundle(name, locale);
            }
            catch (MissingResourceException e)
            {
                return null;
            }
        }

        protected Object handleGetObject(String key)
        {
            for (String name : bundleNames)
            {
                PropertyResourceBundle bundle = getPropertyResourceBundle(name);
                if (bundle != null)
                {
                    Object value = bundle.handleGetObject(key);
                    if (value != null)
                    {
                        return value;
                    }
                }
            }
            
            return null;
        }

        public Enumeration<String> getKeys()
        {
            return null;
        }

        /**
         * Checks if at least one of the underlying resource bundles supports
         * the locale assigned to this bundle.
         */
        public boolean isSupported()
        {
            for (String name : bundleNames)
            {
                PropertyResourceBundle bundle = getPropertyResourceBundle(name);
                if (bundle != null && bundle.getLocale().equals(locale))
                {
                    return true;
                }
            }
            
            return false;
        }
    }
}
