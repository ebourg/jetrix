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

package net.jetrix.winlist;

/**
 * JUnit TestCase for the class net.jetrix.winlist.TetrixWinlist.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TetrixWinlistTest extends SimpleWinlistTest
{
    private byte[] struct =
            new byte[]{0x74, 0x68, 0x65, 0x6c, 0x6c, 0x27, 0x73, 0x5f, 0x70, 0x6c, 0x61, 0x79, 0x65, 0x72, 0x73, (byte) 0x86,
                       0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                       (byte) 0x8a, 0x05, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00};

    private Score score;

    public void setUp()
    {
        super.setUp();
        score = new Score("hell's_players\u2020", Score.TYPE_TEAM, 1418);
    }

    public void testBuildScore() throws Exception
    {
        TetrixWinlist winlist = new TetrixWinlist();

        Score score2 = winlist.buildScore(struct);
        assertNotNull("null score", score2);
        assertEquals("name", score.getName(), score2.getName());
        assertEquals("score", score.getScore(), score2.getScore());
        assertEquals("type", score.getType(), score2.getType());
    }

    public void testBuildStruct() throws Exception
    {
        TetrixWinlist winlist = new TetrixWinlist();

        byte[] struct2 = winlist.buildStruct(score);
        assertNotNull("null struct", struct2);
        assertEquals("length", struct.length, struct2.length);
        for (int i = 0; i < struct.length; i++)
        {
            assertEquals("byte " + i, struct[i], struct2[i]);
        }
    }

    protected SimpleWinlist getWinlist()
    {
        return new TetrixWinlist();
    }

}
