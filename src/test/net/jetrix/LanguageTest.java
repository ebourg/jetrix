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

package net.jetrix;

import java.util.Collection;
import java.util.Locale;

import junit.framework.TestCase;

/**
 * JUnit TestCase for the class net.jetrix.Language.
 *
 * @author Emmanuel Bourg
 */
public class LanguageTest extends TestCase
{
    protected void setUp() throws Exception
    {
        Locale.setDefault(Locale.KOREA);
        Language.getInstance().addResources("jetrix");
        Language.getInstance().addResources("foo");
    }

    public void testLoad()
    {
        Language language = Language.getInstance();
        language.load(Locale.FRENCH);
    }

    public void testIsSupported()
    {
        assertTrue("french is not supported", Language.isSupported(Locale.FRENCH));
        assertFalse("korean is supported", Language.isSupported(Locale.KOREAN));
    }

    public void testGetText()
    {
        assertEquals("common.yes in english", "yes", Language.getText("common.yes", Locale.ENGLISH));
        assertEquals("common.yes in french", "oui", Language.getText("common.yes", Locale.FRENCH));
    }

    public void testGetTextWithParameter()
    {
        Object[] params = new Object[] { "Smanux", 123 };
        String englishText = "<gray>Hello Smanux, you are in channel <b>123</b>";
        String frenchText = "<gray>Salut Smanux, tu es dans le channel <b>123</b>";
        assertEquals("welcome message in english", englishText, Language.getText("channel.welcome", Locale.ENGLISH, params));
        assertEquals("welcome message in french", frenchText, Language.getText("channel.welcome", Locale.FRENCH, params));
    }

    public void testGetTestWithLocalizedParameter()
    {
        String englishText = "<gray>Game started by <b>yes</b>";
        String frenchText = "<gray>Partie d�marr�e par <b>oui</b>";
        assertEquals("localized message in english", englishText, Language.getText("channel.game.started-by", Locale.ENGLISH, "key:common.yes"));
        assertEquals("localized message in french", frenchText, Language.getText("channel.game.started-by", Locale.FRENCH, "key:common.yes"));
    }

    public void testGetMissingText()
    {
        Language language = Language.getInstance();
        language.load(Locale.FRENCH);

        String text = Language.getText("xyz", Locale.FRENCH);
        assertNotNull("null string returned", text);
        assertEquals("missing text", "[fr:xyz]", text);
    }

    public void testGetLocales()
    {
        Collection locales = Language.getLocales();

        assertNotNull("null list", locales);
        assertTrue("english locale not found", locales.contains(Locale.ENGLISH));
        assertTrue("french locale not found", locales.contains(Locale.FRENCH));
    }

    public void testAdditionalBundle()
    {
        Language.getInstance().addResources("command.mode");
        assertEquals("<gray>Configuration changed to <b>yes</b>", Language.getText("command.mode.enabled", Locale.ENGLISH, "key:common.yes"));
    }
}
