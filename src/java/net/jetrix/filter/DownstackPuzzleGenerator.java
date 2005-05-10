/**
 * Jetrix TetriNET Server
 * Copyright (C) 2005  Emmanuel Bourg
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

package net.jetrix.filter;

import static net.jetrix.config.Block.*;

import java.io.*;
import java.util.logging.*;
import java.util.*;

import net.jetrix.config.*;
import net.jetrix.*;
import org.apache.commons.lang.*;

/**
 * Puzzle generator supporting the puzzle format used by the 2ice server,
 * aka downstack.com. Each puzzle consists in two files, a <tt>.field</tt> file
 * containing the description of the field, and a <tt>.settings<tt> file
 * containing the game settings and the puzzle description. By default the
 * puzzle files are looked for in the <tt>data/puzzle</tt> sub directory, this
 * can be overridden by setting the <tt>path</tt> parameter.
 *
 * @since 0.3
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class DownstackPuzzleGenerator implements PuzzleGenerator
{
    private Logger log = Logger.getLogger("net.jetrix");

    private static final String DEFAULT_PATH = "data/puzzle";

    /** The path where the puzzle fields and settings are located. */
    private String path = DEFAULT_PATH;

    /** The current level played. */
    private int level;

    /** The order or the specials used in the settings file. */
    public static final Block[] BLOCKS = { LEFTL, LEFTZ, SQUARE, RIGHTL, RIGHTZ, HALFCROSS, LINE };

    public void init(Configuration config)
    {
        path = config.getString("path", DEFAULT_PATH);
    }

    public Puzzle getNextPuzzle()
    {
        Puzzle puzzle = new Puzzle();

        try
        {
            // load the field
            File[] levels = getLevels();
            File file = levels[level % levels.length];
            level = level + 1;
            Field field = new Field();
            field.load(file.getAbsolutePath());
            puzzle.setField(field);
            puzzle.setKey(String.valueOf(level));

            // load the settings
            String name = file.getAbsolutePath().replace(".field", ".settings");
            readSettings(puzzle, name);
        }
        catch (IOException e)
        {
            log.log(Level.WARNING, e.getMessage(), e);
        }

        return puzzle;
    }

    /**
     * Find all levels in the puzzle directory.
     */
    private File[] getLevels()
    {
        File directory = new File(path);
        File[] files = directory.listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.endsWith(".field");
            }
        });

        // sort the list of files

        Arrays.sort(files, new FilenameComparator());

        return files;
    }

    /**
     * Parse the specified .settings file and update the puzzle.
     *
     * @param puzzle   the puzzle to update
     * @param filename the settings file to parse
     */
    private void readSettings(Puzzle puzzle, String filename) throws IOException
    {
        Settings settings = new Settings();
        puzzle.setSettings(settings);

        File file = new File(filename);
        if (file.exists())
        {
            BufferedReader in = null;

            try
            {
                in = new BufferedReader(new FileReader(file));

                String line;
                while ((line = in.readLine()) != null)
                {
                    if (line.startsWith("NAME"))
                    {
                        // parse the name and remove the leading and trailing quotes
                        String name = line.substring(4).trim();
                        if (name.startsWith("\"") && name.endsWith("\""))
                        {
                            name = name.substring(1, name.length() - 1);
                        }

                        puzzle.setName(name);
                    }
                    else if (line.startsWith("COMMENT"))
                    {
                        puzzle.setComment(line.substring(7).trim());
                    }
                    else if (line.startsWith("DESIGNER"))
                    {
                        puzzle.setAuthor(line.substring(8).trim());
                    }
                    else if (line.startsWith("SPECIAL"))
                    {
                        // parse the special occurancies
                        String[] occurancy = StringUtils.split(line.substring(7).trim(), ' ');

                        for (Special special : Special.values())
                        {
                            settings.setOccurancy(special, Integer.parseInt(occurancy[special.getValue()]));
                        }
                    }
                    else if (line.startsWith("BLOCK"))
                    {
                        // parse the block occurancies
                        String[] occurancy = StringUtils.split(line.substring(5).trim(), ' ');

                        // careful, it doesn't follow the standard order
                        for (int i = 0; i < BLOCKS.length; i++)
                        {
                            settings.setOccurancy(BLOCKS[i], Integer.parseInt(occurancy[i]));
                        }
                    }
                    else if (line.startsWith("SUDDENDEATHMSG"))
                    {
                        settings.setSuddenDeathMessage(line.substring(14).trim());
                    }
                    else if (line.startsWith("SUDDENDEATH"))
                    {
                        String[] params = StringUtils.split(line.substring(11).trim(), ' ');

                        settings.setSuddenDeathTime(Integer.parseInt(params[0]));
                        settings.setSuddenDeathLinesAdded(Integer.parseInt(params[1]));
                        settings.setSuddenDeathDelay(Integer.parseInt(params[2]));
                    }
                    else if (line.startsWith("RULES"))
                    {
                        String[] params = StringUtils.split(line.substring(5).trim(), ' ');

                        settings.setStartingLevel(Integer.parseInt(params[0]));
                        settings.setLinesPerLevel(Integer.parseInt(params[1]));
                        settings.setLevelIncrease(Integer.parseInt(params[2]));
                        settings.setLinesPerSpecial(Integer.parseInt(params[3]));
                        settings.setSpecialAdded(Integer.parseInt(params[4]));
                        settings.setSpecialCapacity(Integer.parseInt(params[5]));
                        settings.setClassicRules(!"0".equals(params[6]));
                        settings.setAverageLevels(!"0".equals(params[7]));
                    }
                }
            }
            finally
            {
                // close the file
                if (in != null)
                {
                    in.close();
                }
            }
        }

    }


}
