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

    public static final int BLOCK_LINE      = 0;
    public static final int BLOCK_SQUARE    = 1;
    public static final int BLOCK_LEFTL     = 2;
    public static final int BLOCK_RIGHTL    = 3;
    public static final int BLOCK_LEFTZ     = 4;
    public static final int BLOCK_RIGHTZ    = 5;
    public static final int BLOCK_HALFCROSS = 6;

    public static final int SPECIAL_ADDLINE      = 0;
    public static final int SPECIAL_CLEARLINE    = 1;
    public static final int SPECIAL_NUKEFIELD    = 2;
    public static final int SPECIAL_RANDOMCLEAR  = 3;
    public static final int SPECIAL_SWITCHFIELD  = 4;
    public static final int SPECIAL_CLEARSPECIAL = 5;
    public static final int SPECIAL_GRAVITY      = 6;
    public static final int SPECIAL_QUAKEFIELD   = 7;
    public static final int SPECIAL_BLOCKBOMB    = 8;


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
        blockOccurancy = new int[7];
        specialOccurancy = new int [9];

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
        }
    }

    public int getStartingLevel()
    {
        if (defaultStartingLevel && defaultSettings!=null && this!=defaultSettings)
            return defaultSettings.getStartingLevel();
        else
            return startingLevel;
    }

    public int getStackHeight()
    {
        if (defaultStackHeight && defaultSettings!=null && this!=defaultSettings)
            return defaultSettings.getStackHeight();
        else
            return stackHeight;
    }

    public int getLinesPerLevel()
    {
        if (defaultLinesPerLevel && defaultSettings!=null && this!=defaultSettings)
            return defaultSettings.getLinesPerLevel();
        else
            return linesPerLevel;
    }

    public int getLinesPerSpecial()
    {
        if (defaultLinesPerSpecial && defaultSettings!=null && this!=defaultSettings)
            return defaultSettings.getLinesPerSpecial();
        else
            return linesPerSpecial;
    }

    public int getLevelIncrease()
    {
        if (defaultLevelIncrease && defaultSettings!=null && this!=defaultSettings)
            return defaultSettings.getLevelIncrease();
        else
            return levelIncrease;
    }

    public int getSpecialAdded()
    {
        if (defaultSpecialAdded && defaultSettings!=null && this!=defaultSettings)
            return defaultSettings.getSpecialAdded();
        else
            return specialAdded;
    }

    public int getSpecialCapacity()
    {
        if (defaultSpecialCapacity && defaultSettings!=null && this!=defaultSettings)
            return defaultSettings.getSpecialCapacity();
        else
            return specialCapacity;
    }

    public boolean getAverageLevels()
    {
        if (defaultAverageLevels && defaultSettings!=null && this!=defaultSettings)
            return defaultSettings.getAverageLevels();
        else
            return averageLevels;
    }

    public boolean getClassicRules()
    {
        if (defaultClassicRules && defaultSettings!=null && this!=defaultSettings)
            return defaultSettings.getClassicRules();
        else
            return classicRules;
    }

    public int getBlockOccurancy(int piece)
    {
        if (defaultBlockOccurancy && defaultSettings!=null && this!=defaultSettings)
            return defaultSettings.getBlockOccurancy(piece);
        else
            return blockOccurancy[piece];
    }

    public int getSpecialOccurancy(int special)
    {
        if (defaultSpecialOccurancy && defaultSettings!=null && this!=defaultSettings)
            return defaultSettings.getSpecialOccurancy(special);
        else
            return specialOccurancy[special];
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

    public void setBlockOccurancy(int piece, int occurancy)
    {
        if (defaultBlockOccurancy)
        {
            defaultBlockOccurancy = false;
            Arrays.fill(blockOccurancy, 0);
        }

        blockOccurancy[piece] = occurancy;
    }

    public void setSpecialOccurancy(int special, int occurancy)
    {
        if (defaultSpecialOccurancy)
        {
            defaultSpecialOccurancy = false;
            Arrays.fill(specialOccurancy, 0);
        }

        specialOccurancy[special] = occurancy;
    }

    /**
     * Normalize array values to get a sum equals to 100.
     * Any negative value is nullified.
     */
    protected void normalize(int[] tab)
    {
        int sum = 0;

        // computing sum
        for (int i=0; i<tab.length; i++)
        {
            if (tab[i]<0) { tab[i]=0; }
            sum = sum + tab[i];
        }

        if (sum != 100)
        {
            // equalization
            if (sum==0)
            {
                int v = 100/tab.length;
                for (int i=0; i<tab.length; i++) { tab[i] = v; }
            }
            else
            {
                float f = 100f/sum;
                for (int i=0; i<tab.length; i++) { tab[i] = (int)(tab[i] * f); }
            }

            // distributing points left
            sum = 0;
            for (int i=0; i<tab.length; i++) { sum = sum + tab[i]; }
            int r = 100 - sum;
            int i = 0;
            while( i < tab.length && r > 0)
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


    public void setLineOccurancy(int occurancy) { setBlockOccurancy(BLOCK_LINE, occurancy); }
    public void setSquareOccurancy(int occurancy) { setBlockOccurancy(BLOCK_SQUARE, occurancy); }
    public void setLeftLOccurancy(int occurancy) { setBlockOccurancy(BLOCK_LEFTL, occurancy); }
    public void setRightLOccurancy(int occurancy) { setBlockOccurancy(BLOCK_RIGHTL, occurancy); }
    public void setLeftZOccurancy(int occurancy) { setBlockOccurancy(BLOCK_LEFTZ, occurancy); }
    public void setRightZOccurancy(int occurancy) { setBlockOccurancy(BLOCK_RIGHTZ, occurancy); }
    public void setHalfCrossOccurancy(int occurancy) { setBlockOccurancy(BLOCK_HALFCROSS, occurancy); }

    public void setAddLineOccurancy(int occurancy) { setSpecialOccurancy(SPECIAL_ADDLINE, occurancy); }
    public void setClearLineOccurancy(int occurancy) { setSpecialOccurancy(SPECIAL_CLEARLINE, occurancy); }
    public void setNukeFieldOccurancy(int occurancy) { setSpecialOccurancy(SPECIAL_NUKEFIELD, occurancy); }
    public void setRandomClearOccurancy(int occurancy) { setSpecialOccurancy(SPECIAL_RANDOMCLEAR, occurancy); }
    public void setSwitchFieldOccurancy(int occurancy) { setSpecialOccurancy(SPECIAL_SWITCHFIELD, occurancy); }
    public void setClearSpecialOccurancy(int occurancy) { setSpecialOccurancy(SPECIAL_CLEARSPECIAL, occurancy); }
    public void setGravityOccurancy(int occurancy) { setSpecialOccurancy(SPECIAL_GRAVITY, occurancy); }
    public void setQuakeFieldOccurancy(int occurancy) { setSpecialOccurancy(SPECIAL_QUAKEFIELD, occurancy); }
    public void setBlockBombOccurancy(int occurancy) { setSpecialOccurancy(SPECIAL_BLOCKBOMB, occurancy); }

}
