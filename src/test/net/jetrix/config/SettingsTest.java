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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit TestCase for the class net.jetrix.config.Settings
 *
 * @author Emmanuel Bourg
 */
public class SettingsTest
{
    private Settings defaultSettings;

    @Before
    public void setUp()
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

        defaultSettings.getBlockOccurancy().setOccurancy(Block.LINE, 10);
        defaultSettings.getSpecialOccurancy().setOccurancy(Special.GRAVITY, 10);

        Settings.setDefaultSettings(defaultSettings);
    }

    @Test
    public void testGetDefaultSettings()
    {
        assertEquals(defaultSettings, Settings.getDefaultSettings());
    }

    @Test
    public void testAverageLevels()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getAverageLevels(), settings.getAverageLevels());
        settings.setAverageLevels(false);
        assertEquals(false, settings.getAverageLevels());
    }

    @Test
    public void testClassicRules()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getClassicRules(), settings.getClassicRules());
        settings.setClassicRules(false);
        assertEquals(false, settings.getClassicRules());
    }

    @Test
    public void testLevelIncrease()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getLevelIncrease(), settings.getLevelIncrease());
        settings.setLevelIncrease(0);
        assertEquals(0, settings.getLevelIncrease());
    }

    @Test
    public void testLinesPerLevel()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getLinesPerLevel(), settings.getLinesPerLevel());
        settings.setLinesPerLevel(0);
        assertEquals(0, settings.getLinesPerLevel());
    }

    @Test
    public void testLinesPerSpecial()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getLinesPerSpecial(), settings.getLinesPerSpecial());
        settings.setLinesPerSpecial(0);
        assertEquals(0, settings.getLinesPerSpecial());
    }

    @Test
    public void testSameBlocks()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getSameBlocks(), settings.getSameBlocks());
        settings.setSameBlocks(false);
        assertEquals(false, settings.getSameBlocks());
    }

    @Test
    public void testSpecialAdded()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getSpecialAdded(), settings.getSpecialAdded());
        settings.setSpecialAdded(0);
        assertEquals(0, settings.getSpecialAdded());
    }

    @Test
    public void testSpecialCapacity()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getSpecialCapacity(), settings.getSpecialCapacity());
        settings.setSpecialCapacity(0);
        assertEquals(0, settings.getSpecialCapacity());
    }

    @Test
    public void testStartingLevel()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getStartingLevel(), settings.getStartingLevel());
        settings.setStartingLevel(0);
        assertEquals(0, settings.getStartingLevel());
    }

    @Test
    public void testStackHeight()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getStackHeight(), settings.getStackHeight());
        settings.setStackHeight(0);
        assertEquals(0, settings.getStackHeight());
    }

    @Test
    public void testSuddenDeathDelay()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getSuddenDeathDelay(), settings.getSuddenDeathDelay());
        settings.setSuddenDeathDelay(0);
        assertEquals(0, settings.getSuddenDeathDelay());
    }

    @Test
    public void testSuddenDeathLinesAdded()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getSuddenDeathLinesAdded(), settings.getSuddenDeathLinesAdded());
        settings.setSuddenDeathLinesAdded(0);
        assertEquals(0, settings.getSuddenDeathLinesAdded());
    }

    @Test
    public void testSuddenDeathMessage()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getSuddenDeathMessage(), settings.getSuddenDeathMessage());
        settings.setSuddenDeathMessage("");
        assertEquals("", settings.getSuddenDeathMessage());
    }

    @Test
    public void testSuddenDeathTime()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getSuddenDeathTime(), settings.getSuddenDeathTime());
        settings.setSuddenDeathTime(0);
        assertEquals(0, settings.getSuddenDeathTime());
    }

    @Test
    public void testBlockOccurancy()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getOccurancy(Block.LINE), settings.getOccurancy(Block.LINE));
        settings.getBlockOccurancy().setOccurancy(Block.LINE, 50);
        assertEquals(50, settings.getOccurancy(Block.LINE));
    }

    @Test
    public void testSpecialOccurancy()
    {
        Settings settings = new Settings();
        assertEquals(defaultSettings.getOccurancy(Special.GRAVITY), settings.getOccurancy(Special.GRAVITY));
        settings.getSpecialOccurancy().setOccurancy(Special.GRAVITY, 50);
        assertEquals(50, settings.getOccurancy(Special.GRAVITY));
    }
}
