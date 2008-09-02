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

import java.io.*;
import java.util.logging.Level;

import net.jetrix.config.*;

/**
 * A winlist caompatible with the tetrinetx winlist format.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TetrixWinlist extends SimpleWinlist
{
    public static final int STRUCT_SIZE = 40;
    public static final int DEFAULT_WINLIST_SIZE = 5120;

    private long scoreCount = DEFAULT_WINLIST_SIZE;
    private String filename;

    public void init(WinlistConfig config)
    {
        super.init(config);

        filename = config.getString("file", null);
        if (filename == null)
        {
            filename = "game.winlist" + getId();
        }
    }

    protected void load()
    {
        File file = new File(filename);

        if (file.exists())
        {
            InputStream in = null;

            try
            {
                in = new BufferedInputStream(new FileInputStream(file));

                scoreCount = Math.max(scoreCount,  file.length() / STRUCT_SIZE);
                byte[] struct = new byte[STRUCT_SIZE];

                while (in.read(struct) != -1)
                {
                    Score score = buildScore(struct);
                    if (score == null) break;
                    scores.add(score);
                }
            }
            catch (IOException e)
            {
                log.log(Level.WARNING, "Unable to read the winlist file " + file, e);
            }
            finally
            {
                close(in);
            }
        }

        initialized = true;
    }

    protected void save()
    {
        File file = new File(filename);
        OutputStream out = null;

        try
        {
            out = new BufferedOutputStream(new FileOutputStream(file));

            for (int i = 0; i < scoreCount; i++)
            {
                Score score = null;
                if (i < scores.size())
                {
                    score = (Score) scores.get(i);
                }

                byte[] struct = buildStruct(score);
                out.write(struct);
            }

            out.flush();
        }
        catch (IOException e)
        {
            log.log(Level.WARNING, "Unable to write the winlist file " + file, e);
        }
        finally
        {
            close(out);
        }
    }

    /**
     * Build a score from a tetrix winlist structure.
     */
    protected Score buildScore(byte[] struct)
    {
        Score score = null;

        if (struct[0] != 0)
        {
            score = new Score();
            score.setName(new String(struct, 1, 31).trim());
            score.setType(struct[0] == 0x70 ? Score.TYPE_PLAYER : Score.TYPE_TEAM);
            long scoreValue = getUnsignedByte(struct, 32)
                    + (getUnsignedByte(struct, 33) << 8)
                    + (getUnsignedByte(struct, 34) << 16)
                    + (getUnsignedByte(struct, 35) << 24);
            score.setScore(scoreValue);
        }

        return score;
    }

    /**
     * Build a tetrix winlist structure from a score.
     */
    protected byte[] buildStruct(Score score)
    {
        byte[] struct = new byte[STRUCT_SIZE];

        if (score != null)
        {
            // type
            struct[0] = score.getType() == Score.TYPE_PLAYER ? (byte) 'p' : (byte) 't';

            // name
            byte[] name = score.getName().getBytes();
            for (int i = 0; i < name.length; i++)
            {
                struct[i + 1] = name[i];
            }

            // score
            struct[35] = (byte) ((score.getScore() >> 24) % 256);
            struct[34] = (byte) ((score.getScore() >> 16) % 256);
            struct[33] = (byte) ((score.getScore() >> 8) % 256);
            struct[32] = (byte) (score.getScore() % 256);

            // ???
            struct[36] = 1;
        }

        return struct;
    }

    private int getUnsignedByte(byte bloc[], int offset)
    {
        byte b = bloc[offset];
        return b < 0 ? b + 256 : b;
    }

}
