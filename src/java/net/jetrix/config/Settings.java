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

import static net.jetrix.config.Block.*;
import static net.jetrix.config.Special.*;

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
        }
    }

    public int getStartingLevel()
    {
        boolean useDefaultSettings = defaultStartingLevel && defaultSettings != null && this != defaultSettings;
        return useDefaultSettings ? defaultSettings.getStartingLevel() : startingLevel;
    }

    public int getStackHeight()
    {
        boolean useDefaultSettings = defaultStackHeight && defaultSettings != null && this != defaultSettings;
        return useDefaultSettings ? defaultSettings.getStackHeight() : stackHeight;
    }

    public int getLinesPerLevel()
    {
        boolean useDefaultSettings = defaultLinesPerLevel && defaultSettings != null && this != defaultSettings;
        return useDefaultSettings ? defaultSettings.getLinesPerLevel() : linesPerLevel;
    }

    public int getLinesPerSpecial()
    {
        boolean useDefaultSettings = defaultLinesPerSpecial && defaultSettings != null && this != defaultSettings;
        return useDefaultSettings ? defaultSettings.getLinesPerSpecial() : linesPerSpecial;
    }

    public int getLevelIncrease()
    {
        boolean useDefaultSettings = defaultLevelIncrease && defaultSettings != null && this != defaultSettings;
        return useDefaultSettings ? defaultSettings.getLevelIncrease() : levelIncrease;
    }

    public int getSpecialAdded()
    {
        boolean useDefaultSettings = defaultSpecialAdded && defaultSettings != null && this != defaultSettings;
        return useDefaultSettings ? defaultSettings.getSpecialAdded() : specialAdded;
    }

    public int getSpecialCapacity()
    {
        boolean useDefaultSettings = defaultSpecialCapacity && defaultSettings != null && this != defaultSettings;
        return useDefaultSettings ? defaultSettings.getSpecialCapacity() : specialCapacity;
    }

    public boolean getAverageLevels()
    {
        boolean useDefaultSettings = defaultAverageLevels && defaultSettings != null && this != defaultSettings;
        return useDefaultSettings ? defaultSettings.getAverageLevels() : averageLevels;
    }

    public boolean getClassicRules()
    {
        boolean useDefaultSettings = defaultClassicRules && defaultSettings != null && this != defaultSettings;
        return useDefaultSettings ? defaultSettings.getClassicRules() : classicRules;
    }

    public boolean getSameBlocks()
    {
        boolean useDefaultSettings = defaultSameBlocks && defaultSettings != null && this != defaultSettings;
        return useDefaultSettings ? defaultSettings.getSameBlocks() : sameBlocks;
    }

    public int getOccurancy(Block piece)
    {
        boolean useDefaultSettings = defaultBlockOccurancy && defaultSettings != null && this != defaultSettings;
        return useDefaultSettings ? defaultSettings.getOccurancy(piece) : blockOccurancy[piece.getValue()];
    }

    public int getOccurancy(Special special)
    {
        boolean useDefaultSettings = defaultSpecialOccurancy && defaultSettings != null && this != defaultSettings;
        return useDefaultSettings ? defaultSettings.getOccurancy(special) : specialOccurancy[special.getValue()];
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

    /**
     * Set the occurancy of a block.
     *
     * @since 0.1.4
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
     * @since 0.1.4
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


    public void setLineOccurancy(int occurancy) { setOccurancy(LINE, occurancy); }
    public void setSquareOccurancy(int occurancy) { setOccurancy(SQUARE, occurancy); }
    public void setLeftLOccurancy(int occurancy) { setOccurancy(LEFTL, occurancy); }
    public void setRightLOccurancy(int occurancy) { setOccurancy(RIGHTL, occurancy); }
    public void setLeftZOccurancy(int occurancy) { setOccurancy(LEFTZ, occurancy); }
    public void setRightZOccurancy(int occurancy) { setOccurancy(RIGHTZ, occurancy); }
    public void setHalfCrossOccurancy(int occurancy) { setOccurancy(HALFCROSS, occurancy); }

    public void setAddLineOccurancy(int occurancy) { setOccurancy(ADDLINE, occurancy); }
    public void setClearLineOccurancy(int occurancy) { setOccurancy(CLEARLINE, occurancy); }
    public void setNukeFieldOccurancy(int occurancy) { setOccurancy(NUKEFIELD, occurancy); }
    public void setRandomClearOccurancy(int occurancy) { setOccurancy(RANDOMCLEAR, occurancy); }
    public void setSwitchFieldOccurancy(int occurancy) { setOccurancy(SWITCHFIELD, occurancy); }
    public void setClearSpecialOccurancy(int occurancy) { setOccurancy(CLEARSPECIAL, occurancy); }
    public void setGravityOccurancy(int occurancy) { setOccurancy(GRAVITY, occurancy); }
    public void setQuakeFieldOccurancy(int occurancy) { setOccurancy(QUAKEFIELD, occurancy); }
    public void setBlockBombOccurancy(int occurancy) { setOccurancy(BLOCKBOMB, occurancy); }

}
