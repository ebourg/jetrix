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

import java.util.*;

import junit.framework.*;

/**
 * JUnit TestCase for the class net.jetrix.Language.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class LanguageTest extends TestCase
{
    public void testLoad()
    {
        Language language = Language.getInstance();
        language.load(Locale.FRENCH);
    }

    public void testIsSupported()
    {
        assertTrue("french is not supported", Language.isSupported(Locale.FRENCH));
    }

    public void testGetText()
    {
        assertEquals("common.yes in english", "yes", Language.getText("common.yes", Locale.ENGLISH));
        assertEquals("common.yes in french", "oui", Language.getText("common.yes", Locale.FRENCH));
    }

    public void testGetTextWithParameter()
    {
        Object[] params = new Object[] { "Smanux", "tetrinet1" };
        String englishText = "<gray>Hello Smanux, you are in channel <b>tetrinet1</b>";
        String frenchText = "<gray>Salut Smanux, tu es dans le channel <b>tetrinet1</b>";
        assertEquals("welcome message in english", englishText, Language.getText("channel.welcome", params, Locale.ENGLISH));
        assertEquals("welcome message in french", frenchText, Language.getText("channel.welcome", params, Locale.FRENCH));
    }

    public void testGetMissingText()
    {
        Language language = Language.getInstance();
        language.load(Locale.FRENCH);

        String text = Language.getText("xyz", Locale.FRENCH);
        assertNotNull("null string returned", text);
        assertEquals("missing text", "[fr:xyz]", text);
    }
}
