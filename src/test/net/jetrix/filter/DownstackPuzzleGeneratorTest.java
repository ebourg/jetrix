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

import java.io.File;

import junit.framework.TestCase;
import net.jetrix.config.Block;
import net.jetrix.config.Puzzle;
import net.jetrix.config.Settings;
import net.jetrix.config.Special;

/**
 * @author Emmanuel Bourg
 */
public class DownstackPuzzleGeneratorTest extends TestCase
{
    public void testGetNextPuzzle()
    {
        PuzzleGenerator generator = new DownstackPuzzleGenerator();
        
        for (int i = 0; i < 100; i++)
        {
            Puzzle puzzle = generator.getNextPuzzle();
            assertNotNull("puzzle null", puzzle);
            assertNotNull("null settings", puzzle.getSettings());
        }
    }

    public void testLoadPuzzle() throws Exception
    {
        DownstackPuzzleGenerator generator = new DownstackPuzzleGenerator();
        Puzzle puzzle = generator.loadPuzzle(new File("data/puzzle"), "game3");
        
        assertNotNull("puzzle null", puzzle);
        assertEquals("author", "NiLS", puzzle.getAuthor());
        assertEquals("name", "Left or Right", puzzle.getName());
        assertEquals("comment", "no comment", puzzle.getComment());

        assertNotNull("null field", puzzle.getField());
        assertFalse("empty field", puzzle.getField().isEmpty());

        Settings settings = puzzle.getSettings();
        assertNotNull("null settings", settings);
        
        int[] blocks = { 14, 14, 15, 14, 14, 14, 15 };
        for (int i = 0; i < DownstackPuzzleGenerator.BLOCKS.length; i++)
        {
            Block block = DownstackPuzzleGenerator.BLOCKS[i];
            assertEquals(block.getCode() + " occurancy", blocks[i], settings.getOccurancy(block));
        }

        int[] specials = { 32, 18, 1, 11, 3, 14, 1, 6, 14 };
        for (Special special : Special.values())
        {
            assertEquals(special.getCode() + " occurancy", specials[special.ordinal()], settings.getOccurancy(special));
        }
        
        assertEquals("sudden death message", "Time's up! It's SUDDEN DEATH MODE!", settings.getSuddenDeathMessage());
        assertEquals("sudden death delay", 30, settings.getSuddenDeathDelay());
        assertEquals("sudden death time", 180, settings.getSuddenDeathTime());
        assertEquals("sudden death lines", 1, settings.getSuddenDeathLinesAdded());

        // RULES 1 2 1 1 0 18 1 1
        assertEquals("starting level", 1, settings.getStartingLevel());
        assertEquals("lines per level", 2, settings.getLinesPerLevel());
        assertEquals("level increase", 1, settings.getLevelIncrease());
        assertEquals("lines per special", 1, settings.getLinesPerSpecial());
        assertEquals("special added", 0, settings.getSpecialAdded());
        assertEquals("special capacity", 18, settings.getSpecialCapacity());
        assertEquals("classic rules", true, settings.getClassicRules());
        assertEquals("average levels", true, settings.getAverageLevels());
    }
}
