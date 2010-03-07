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

package net.jetrix.config;

import java.util.*;

/**
 * Game settings.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class Settings
{
    /** Default settings used by new Settings objets */
    private static Settings defaultSettings;

    /** indicates if this setting objets use the default block occurancy settings */
    private boolean defaultBlockOccurancy;

    /** indicates if this setting objets use the default block occurancy settings */
    private boolean defaultSpecialOccurancy;

    private boolean defaultStartingLevel;
    private boolean defaultStackHeight;
    private boolean defaultLinesPerLevel;
    private boolean defaultLinesPerSpecial;
    private boolean defaultLevelIncrease;
    private boolean defaultSpecialAdded;
    private boolean defaultSpecialCapacity;
    private boolean defaultAverageLevels;
    private boolean defaultClassicRules;
    private boolean defaultSameBlocks;

    private boolean defaultSuddenDeathTime;
    private boolean defaultSuddenDeathMessage;
    private boolean defaultSuddenDeathLinesAdded;
    private boolean defaultSuddenDeathDelay;

    private int startingLevel;
    private int stackHeight;
    private int linesPerLevel;
    private int linesPerSpecial;
    private int levelIncrease;
    private int specialAdded;
    private int specialCapacity;
    private int blockOccurancy[];
    private int specialOccurancy[];
    private boolean averageLevels;
    private boolean classicRules;
    private boolean sameBlocks;

    /** The time in seconds before the sudden death mode starts. */
    private int suddenDeathTime;

    /** The message displayed when the sudden death mode starts. */
    private String suddenDeathMessage;

    /** The delay in seconds between lines additions in sudden death mode. */
    private int suddenDeathDelay;

    /** The number of lines added in sudden death mode. */
    private int suddenDeathLinesAdded;


    /**
     * Creates a new Settings objects using default settings.
     */
    public Settings()
    {
        this(true);
    }

    /**
     * Creates a new Settings object. If <tt>useDefaultSettings</tt> is <tt>true</tt>,
     * calls to getBlockOccurancy() will be forwarded to the default settings
     * objets until the method setBlockOccurancy() is used to overwrite the
     * default configuration (same thing with specials).
     */
    public Settings(boolean useDefaultSettings)
    {
        blockOccurancy = new int[Block.values().length];
        specialOccurancy = new int [Special.values().length];

        if (useDefaultSettings)
        {
            defaultBlockOccurancy = true;
            defaultSpecialOccurancy = true;
            defaultStartingLevel = true;
            defaultStackHeight = true;
            defaultLinesPerLevel = true;
            defaultLinesPerSpecial = true;
            defaultLevelIncrease = true;
            defaultSpecialAdded = true;
            defaultSpecialCapacity = true;
            defaultAverageLevels = true;
            defaultClassicRules = true;
            defaultSameBlocks = true;
            defaultSuddenDeathTime = true;
            defaultSuddenDeathMessage = true;
            defaultSuddenDeathDelay = true;
            defaultSuddenDeathLinesAdded = true;
        }
    }

    /**
     * Returns the default Settings object.
     */
    public static Settings getDefaultSettings()
    {
        return defaultSettings;
    }

    /**
     * Sets the default Settings object.
     */
    public static void setDefaultSettings(Settings defaultSettings)
    {
        Settings.defaultSettings = defaultSettings;
    }

    /**
     * Tell if the current Settings use the default Settings for all its values.
     */
    public boolean useDefaultSettings()
    {
        return defaultBlockOccurancy
                && defaultSpecialOccurancy
                && defaultStartingLevel
                && defaultStackHeight
                && defaultLinesPerLevel
                && defaultLinesPerSpecial
                && defaultLevelIncrease
                && defaultSpecialAdded
                && defaultSpecialCapacity
                && defaultAverageLevels
                && defaultClassicRules
                && defaultSameBlocks
                && defaultSuddenDeathTime
                && defaultSuddenDeathMessage
                && defaultSuddenDeathDelay
                && defaultSuddenDeathLinesAdded;
    }

    public int getStartingLevel()
    {
        return isDefaultStartingLevel() ? defaultSettings.getStartingLevel() : startingLevel;
    }

    public int getStackHeight()
    {
        return isDefaultStackHeight() ? defaultSettings.getStackHeight() : stackHeight;
    }

    public int getLinesPerLevel()
    {
        return isDefaultLinesPerLevel() ? defaultSettings.getLinesPerLevel() : linesPerLevel;
    }

    public int getLinesPerSpecial()
    {
        return isDefaultLinesPerSpecial() ? defaultSettings.getLinesPerSpecial() : linesPerSpecial;
    }

    public int getLevelIncrease()
    {
        return isDefaultLevelIncrease() ? defaultSettings.getLevelIncrease() : levelIncrease;
    }

    public int getSpecialAdded()
    {
        return isDefaultSpecialAdded() ? defaultSettings.getSpecialAdded() : specialAdded;
    }

    public int getSpecialCapacity()
    {
        return isDefaultSpecialCapacity() ? defaultSettings.getSpecialCapacity() : specialCapacity;
    }

    public boolean getAverageLevels()
    {
        return isDefaultAverageLevels() ? defaultSettings.getAverageLevels() : averageLevels;
    }

    public boolean getClassicRules()
    {
        return isDefaultClassicRules() ? defaultSettings.getClassicRules() : classicRules;
    }

    public boolean getSameBlocks()
    {
        return isDefaultSameBlocks() ? defaultSettings.getSameBlocks() : sameBlocks;
    }

    public int getOccurancy(Block piece)
    {
        return isDefaultBlockOccurancy() ? defaultSettings.getOccurancy(piece) : blockOccurancy[piece.getValue()];
    }

    public int getOccurancy(Special special)
    {
        return isDefaultSpecialOccurancy() ? defaultSettings.getOccurancy(special) : specialOccurancy[special.getValue()];
    }

    public void setStartingLevel(int startingLevel)
    {
        this.startingLevel = startingLevel;
        defaultStartingLevel = false;
    }

    public void setStackHeight(int stackHeight)
    {
        this.stackHeight = stackHeight;
        defaultStackHeight = false;
    }

    public void setLinesPerLevel(int linesPerLevel)
    {
        this.linesPerLevel = linesPerLevel;
        defaultLinesPerLevel = false;
    }

    public void setLinesPerSpecial(int linesPerSpecial)
    {
        this.linesPerSpecial = linesPerSpecial;
        defaultLinesPerSpecial = false;
    }

    public void setLevelIncrease(int levelIncrease)
    {
        this.levelIncrease = levelIncrease;
        defaultLevelIncrease = false;
    }

    public void setSpecialAdded(int specialAdded)
    {
        this.specialAdded = specialAdded;
        defaultSpecialAdded = false;
    }

    public void setSpecialCapacity(int specialCapacity)
    {
        this.specialCapacity = specialCapacity;
        defaultSpecialCapacity = false;
    }

    public void setAverageLevels(boolean averageLevels)
    {
        this.averageLevels = averageLevels;
        defaultAverageLevels = false;
    }

    public void setClassicRules(boolean classicRules)
    {
        this.classicRules = classicRules;
        defaultClassicRules = false;
    }

    public void setSameBlocks(boolean sameBlocks)
    {
        this.sameBlocks = sameBlocks;
        defaultSameBlocks = false;
    }


    public int getSuddenDeathTime()
    {
        return isDefaultSuddenDeathTime() ? defaultSettings.getSuddenDeathTime() : suddenDeathTime;
    }

    public void setSuddenDeathTime(int suddenDeathTime)
    {
        this.suddenDeathTime = suddenDeathTime;
        defaultSuddenDeathTime = false;
    }

    public String getSuddenDeathMessage()
    {
        return isDefaultSuddenDeathMessage() ? defaultSettings.getSuddenDeathMessage() : suddenDeathMessage;
    }

    public void setSuddenDeathMessage(String suddenDeathMessage)
    {
        this.suddenDeathMessage = suddenDeathMessage;
        defaultSuddenDeathMessage = false;
    }

    public int getSuddenDeathLinesAdded()
    {
        return isDefaultSuddenDeathLinesAdded() ? defaultSettings.getSuddenDeathLinesAdded() : suddenDeathLinesAdded;
    }

    public void setSuddenDeathLinesAdded(int suddenDeathLinesAdded)
    {
        this.suddenDeathLinesAdded = suddenDeathLinesAdded;
        defaultSuddenDeathLinesAdded = false;
    }

    public int getSuddenDeathDelay()
    {
        return isDefaultSuddenDeathDelay() ? defaultSettings.getSuddenDeathDelay() : suddenDeathDelay;
    }

    public void setSuddenDeathDelay(int suddenDeathDelay)
    {
        this.suddenDeathDelay = suddenDeathDelay;
        defaultSuddenDeathDelay = false;
    }


    /**
     * Set the occurancy of a block.
     *
     * @since 0.2
     *
     * @param block
     * @param occurancy
     */
    public void setOccurancy(Block block, int occurancy)
    {
        if (defaultBlockOccurancy)
        {
            defaultBlockOccurancy = false;
            Arrays.fill(blockOccurancy, 0);
        }

        blockOccurancy[block.getValue()] = occurancy;
    }

    /**
     * Set the occurancy of a special block
     *
     * @since 0.2
     *
     * @param special
     * @param occurancy
     */
    public void setOccurancy(Special special, int occurancy)
    {
        if (defaultSpecialOccurancy)
        {
            defaultSpecialOccurancy = false;
            Arrays.fill(specialOccurancy, 0);
        }

        specialOccurancy[special.getValue()] = occurancy;
    }

    /**
     * Normalize array values to get a sum equals to 100.
     * Any negative value is nullified.
     */
    protected void normalize(int[] tab)
    {
        int sum = 0;

        // computing sum
        for (int i = 0; i < tab.length; i++)
        {
            if (tab[i] < 0)
            {
                tab[i] = 0;
            }
            sum = sum + tab[i];
        }

        if (sum != 100)
        {
            // equalization
            if (sum == 0)
            {
                int v = 100 / tab.length;
                for (int i = 0; i < tab.length; i++)
                {
                    tab[i] = v;
                }
            }
            else
            {
                float f = 100f / sum;
                for (int i = 0; i < tab.length; i++)
                {
                    tab[i] = (int) (tab[i] * f);
                }
            }

            // distributing points left
            sum = 0;
            for (int i = 0; i < tab.length; i++)
            {
                sum = sum + tab[i];
            }
            int r = 100 - sum;
            int i = 0;
            while (i < tab.length && r > 0)
            {
                tab[i] = tab[i] + 1;
                r = r - 1;
                i = i + 1;
            }
        }
    }


    public void normalizeBlockOccurancy()
    {
        normalize(blockOccurancy);
    }

    public void normalizeSpecialOccurancy()
    {
        normalize(specialOccurancy);
    }

    private boolean isDefault()
    {
        return defaultSettings == null || this == defaultSettings;
    }

    /**
     * Tells if the block occurancies of the default settings are used.
     */
    public boolean isDefaultBlockOccurancy()
    {
        return defaultBlockOccurancy && !isDefault();
    }

    public void setDefaultBlockOccurancy(boolean defaultBlockOccurancy)
    {
        this.defaultBlockOccurancy = defaultBlockOccurancy;
    }

    /**
     * Tells if the special occurancies of the default settings are used.
     */
    public boolean isDefaultSpecialOccurancy()
    {
        return defaultSpecialOccurancy && !isDefault();
    }

    public void setDefaultSpecialOccurancy(boolean defaultSpecialOccurancy)
    {
        this.defaultSpecialOccurancy = defaultSpecialOccurancy;
    }

    public boolean isDefaultStartingLevel()
    {
        return defaultStartingLevel && !isDefault();
    }

    public void setDefaultStartingLevel(boolean defaultStartingLevel)
    {
        this.defaultStartingLevel = defaultStartingLevel;
    }

    public boolean isDefaultStackHeight()
    {
        return defaultStackHeight && !isDefault();
    }

    public void setDefaultStackHeight(boolean defaultStackHeight)
    {
        this.defaultStackHeight = defaultStackHeight;
    }

    public boolean isDefaultLinesPerLevel()
    {
        return defaultLinesPerLevel && !isDefault();
    }

    public void setDefaultLinesPerLevel(boolean defaultLinesPerLevel)
    {
        this.defaultLinesPerLevel = defaultLinesPerLevel;
    }

    public boolean isDefaultLinesPerSpecial()
    {
        return defaultLinesPerSpecial && !isDefault();
    }

    public void setDefaultLinesPerSpecial(boolean defaultLinesPerSpecial)
    {
        this.defaultLinesPerSpecial = defaultLinesPerSpecial;
    }

    public boolean isDefaultLevelIncrease()
    {
        return defaultLevelIncrease && !isDefault();
    }

    public void setDefaultLevelIncrease(boolean defaultLevelIncrease)
    {
        this.defaultLevelIncrease = defaultLevelIncrease;
    }

    public boolean isDefaultSpecialAdded()
    {
        return defaultSpecialAdded && !isDefault();
    }

    public void setDefaultSpecialAdded(boolean defaultSpecialAdded)
    {
        this.defaultSpecialAdded = defaultSpecialAdded;
    }

    public boolean isDefaultSpecialCapacity()
    {
        return defaultSpecialCapacity && !isDefault();
    }

    public void setDefaultSpecialCapacity(boolean defaultSpecialCapacity)
    {
        this.defaultSpecialCapacity = defaultSpecialCapacity;
    }

    public boolean isDefaultAverageLevels()
    {
        return defaultAverageLevels && !isDefault();
    }

    public void setDefaultAverageLevels(boolean defaultAverageLevels)
    {
        this.defaultAverageLevels = defaultAverageLevels;
    }

    public boolean isDefaultClassicRules()
    {
        return defaultClassicRules && !isDefault();
    }

    public void setDefaultClassicRules(boolean defaultClassicRules)
    {
        this.defaultClassicRules = defaultClassicRules;
    }

    public boolean isDefaultSameBlocks()
    {
        return defaultSameBlocks && !isDefault();
    }

    public void setDefaultSameBlocks(boolean defaultSameBlocks)
    {
        this.defaultSameBlocks = defaultSameBlocks;
    }

    public boolean isDefaultSuddenDeathTime()
    {
        return defaultSuddenDeathTime && !isDefault();
    }

    public void setDefaultSuddenDeathTime(boolean defaultSuddenDeathTime)
    {
        this.defaultSuddenDeathTime = defaultSuddenDeathTime;
    }

    public boolean isDefaultSuddenDeathMessage()
    {
        return defaultSuddenDeathMessage && !isDefault();
    }

    public void setDefaultSuddenDeathMessage(boolean defaultSuddenDeathMessage)
    {
        this.defaultSuddenDeathMessage = defaultSuddenDeathMessage;
    }

    public boolean isDefaultSuddenDeathLinesAdded()
    {
        return defaultSuddenDeathLinesAdded && !isDefault();
    }

    public void setDefaultSuddenDeathLinesAdded(boolean defaultSuddenDeathLinesAdded)
    {
        this.defaultSuddenDeathLinesAdded = defaultSuddenDeathLinesAdded;
    }

    public boolean isDefaultSuddenDeathDelay()
    {
        return defaultSuddenDeathDelay && !isDefault();
    }

    public void setDefaultSuddenDeathDelay(boolean defaultSuddenDeathDelay)
    {
        this.defaultSuddenDeathDelay = defaultSuddenDeathDelay;
    }

    public boolean isDefaultSuddenDeath()
    {
        return isDefaultSuddenDeathDelay()
                && isDefaultSuddenDeathLinesAdded()
                && isDefaultSuddenDeathMessage()
                && isDefaultSuddenDeathTime();
    }
}
