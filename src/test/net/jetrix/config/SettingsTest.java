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

package net.jetrix.config;

import junit.framework.*;

/**
 * JUnit TestCase for the class net.jetrix.config.Settings
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class SettingsTest extends TestCase
{
    private Settings defaultSettings;

    protected void setUp() throws Exception
    {
        defaultSettings = new Settings(false);
        defaultSettings.setAverageLevels(true);
        defaultSettings.setClassicRules(true);
        defaultSettings.setLevelIncrease(2);
        defaultSettings.setLinesPerLevel(4);
        defaultSettings.setLinesPerSpecial(6);
        defaultSettings.setSameBlocks(true);
        defaultSettings.setSpecialAdded(8);
        defaultSettings.setSpecialCapacity(10);
        defaultSettings.setStackHeight(12);
        defaultSettings.setStartingLevel(14);
        defaultSettings.setSuddenDeathDelay(16);
        defaultSettings.setSuddenDeathLinesAdded(18);
        defaultSettings.setSuddenDeathMessage("Hurry Up!");
        defaultSettings.setSuddenDeathTime(20);

        defaultSettings.setOccurancy(Block.LINE, 10);
        defaultSettings.setOccurancy(Special.GRAVITY, 10);

        Settings.setDefaultSettings(defaultSettings);
    }

    public void testGetDefaultSettings()
    {
        assertEquals(defaultSettings, Settings.getDefaultSettings());
    }

    public void testNormalize1()
    {
        Settings s = new Settings();
        int[] tab = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        s.normalize(tab);
        assertEquals("Erreur de normalisation", 100, sum(tab));
    }

    public void testNormalize2()
    {
        Settings s = new Settings();
        int[] tab = { 0, 0, 0, 0, 0 };
        s.normalize(tab);
        assertEquals("Erreur de normalisation", 100, sum(tab));
        assertEquals("tab[0]", 20, tab[0]);
        assertEquals("tab[1]", 20, tab[1]);
        assertEquals("tab[2]", 20, tab[2]);
        assertEquals("tab[3]", 20, tab[3]);
        assertEquals("tab[4]", 20, tab[4]);
    }

    public void testNormalize3()
    {
        Settings s = new Settings();
        int[] tab = { 100, 200, 300, 50, 100, 50, 250, 300 };
        s.normalize(tab);
        assertEquals("Erreur de normalisation", 100, sum(tab));
    }

    public void testNormalize4()
    {
        Settings s = new Settings();
        int[] tab = { 8, 14, 1, 19, 5, 15, 3, 17, 6, 12 };
        s.normalize(tab);
        assertEquals("Erreur de normalisation", 100, sum(tab));
    }

    public void testNormalize5()
    {
        Settings s = new Settings();
        int[] tab4 = { 8, 14, 1, 19, 5, 15, 3, 17, 6, 12 };
        int[] tab5 = { 8, 14, 1, 19, 5, 15, 3, 17, 6, 12 };
        s.normalize(tab4);
        assertTrue("Erreur de normalisation", equals(tab4, tab5));
    }

    public void testNormalize6()
    {
        Settings s = new Settings();
        int[] tab = { 0, -10 };
        s.normalize(tab);
        assertEquals("Erreur de normalisation", 100, sum(tab));
        assertEquals("tab[0]", 50, tab[0]);
        assertEquals("tab[1]", 50, tab[1]);
    }

    private long sum(int[] tab)
    {
        long s = 0;
        for(int i=0; i<tab.length; i++) s = s + tab[i];
        return s;
    }

    /**
     * todo: replace with ArrayAssert from junit-addons
     */
    private boolean equals(int[] a, int[] b)
    {
        boolean equals = true;

        if (a.length == b.length)
        {
            int i=0;
            while(equals && i<a.length)
            {
                equals = (a[i]==b[i]);
                i++;
            }
        }
        else
        {
            equals = false;
        }

        return equals;
    }

    public void testAverageLevels()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getAverageLevels(), settings.getAverageLevels());
        settings.setAverageLevels(false);
        assertEquals(false, settings.getAverageLevels());
    }

    public void testClassicRules()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getClassicRules(), settings.getClassicRules());
        settings.setClassicRules(false);
        assertEquals(false, settings.getClassicRules());
    }

    public void testLevelIncrease()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getLevelIncrease(), settings.getLevelIncrease());
        settings.setLevelIncrease(0);
        assertEquals(0, settings.getLevelIncrease());
    }

    public void testLinesPerLevel()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getLinesPerLevel(), settings.getLinesPerLevel());
        settings.setLinesPerLevel(0);
        assertEquals(0, settings.getLinesPerLevel());
    }

    public void testLinesPerSpecial()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getLinesPerSpecial(), settings.getLinesPerSpecial());
        settings.setLinesPerSpecial(0);
        assertEquals(0, settings.getLinesPerSpecial());
    }

    public void testSameBlocks()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getSameBlocks(), settings.getSameBlocks());
        settings.setSameBlocks(false);
        assertEquals(false, settings.getSameBlocks());
    }

    public void testSpecialAdded()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getSpecialAdded(), settings.getSpecialAdded());
        settings.setSpecialAdded(0);
        assertEquals(0, settings.getSpecialAdded());
    }

    public void testSpecialCapacity()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getSpecialCapacity(), settings.getSpecialCapacity());
        settings.setSpecialCapacity(0);
        assertEquals(0, settings.getSpecialCapacity());
    }

    public void testStartingLevel()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getStartingLevel(), settings.getStartingLevel());
        settings.setStartingLevel(0);
        assertEquals(0, settings.getStartingLevel());
    }

    public void testStackHeight()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getStackHeight(), settings.getStackHeight());
        settings.setStackHeight(0);
        assertEquals(0, settings.getStackHeight());
    }

    public void testSuddenDeathDelay()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getSuddenDeathDelay(), settings.getSuddenDeathDelay());
        settings.setSuddenDeathDelay(0);
        assertEquals(0, settings.getSuddenDeathDelay());
    }

    public void testSuddenDeathLinesAdded()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getSuddenDeathLinesAdded(), settings.getSuddenDeathLinesAdded());
        settings.setSuddenDeathLinesAdded(0);
        assertEquals(0, settings.getSuddenDeathLinesAdded());
    }

    public void testSuddenDeathMessage()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getSuddenDeathMessage(), settings.getSuddenDeathMessage());
        settings.setSuddenDeathMessage("");
        assertEquals("", settings.getSuddenDeathMessage());
    }

    public void testSuddenDeathTime()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getSuddenDeathTime(), settings.getSuddenDeathTime());
        settings.setSuddenDeathTime(0);
        assertEquals(0, settings.getSuddenDeathTime());
    }

    public void testBlockOccurancy()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getOccurancy(Block.LINE), settings.getOccurancy(Block.LINE));
        settings.setOccurancy(Block.LINE, 50);
        assertEquals(50, settings.getOccurancy(Block.LINE));
    }

    public void testSpecialOccurancy()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getOccurancy(Special.GRAVITY), settings.getOccurancy(Special.GRAVITY));
        settings.setOccurancy(Special.GRAVITY, 50);
        assertEquals(50, settings.getOccurancy(Special.GRAVITY));
    }

}
