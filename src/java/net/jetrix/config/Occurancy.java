/**
 * Jetrix TetriNET Server
 * Copyright (C) 2011  Emmanuel Bourg
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

import java.util.Arrays;

/**
 * The occurancies in percents of a set of elements.
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 * @since 0.3
 */
public class Occurancy<T extends Enum> implements Cloneable
{
    private int[] occurancies;

    /**
     * Sets the occurancy of the specified element.
     * 
     * @param element
     * @param value
     */
    public void setOccurancy(T element, int value)
    {
        if (occurancies == null)
        {
            Class enumType = element.getClass();
            occurancies = new int[enumType.getEnumConstants().length];
        }
        occurancies[element.ordinal()] = value;
    }

    /**
     * Returns the occurancy of the specified element.
     * 
     * @param element
     */
    public int getOccurancy(T element)
    {
        return occurancies[element.ordinal()];
    }

    /**
     * Normalizes the occurancies such that the sum is equal to 100.
     * Negative values are considered null.
     */
    public void normalize()
    {
        normalize(this.occurancies);
    }
    
    void normalize(int[] occurancies)
    {
        int sum = 0;

        // computing sum
        for (int i = 0; i < occurancies.length; i++)
        {
            if (occurancies[i] < 0)
            {
                occurancies[i] = 0;
            }
            sum = sum + occurancies[i];
        }

        if (sum != 100)
        {
            // equalization
            if (sum == 0)
            {
                int v = 100 / occurancies.length;
                for (int i = 0; i < occurancies.length; i++)
                {
                    occurancies[i] = v;
                }
            }
            else
            {
                float f = 100f / sum;
                for (int i = 0; i < occurancies.length; i++)
                {
                    occurancies[i] = (int) (occurancies[i] * f);
                }
            }

            // distributing points left
            sum = 0;
            for (int occurancy : occurancies)
            {
                sum = sum + occurancy;
            }
            int r = 100 - sum;
            int i = 0;
            while (i < occurancies.length && r > 0)
            {
                occurancies[i] = occurancies[i] + 1;
                r = r - 1;
                i = i + 1;
            }
        }
    }

    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Occurancy occurancy = (Occurancy) o;

        return Arrays.equals(occurancies, occurancy.occurancies);
    }

    public int hashCode()
    {
        return Arrays.hashCode(occurancies);
    }

    public final Occurancy<T> clone()
    {
        try
        {
            Occurancy<T> occurancy = (Occurancy<T>) super.clone();
            occurancy.occurancies = occurancies.clone();
            return occurancy;
        }
        catch (CloneNotSupportedException e)
        {
            throw new InternalError();
        }
    }
}
