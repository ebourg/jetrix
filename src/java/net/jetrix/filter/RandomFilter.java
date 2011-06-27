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

import java.util.List;

import net.jetrix.Message;
import net.jetrix.config.Block;
import net.jetrix.config.Occurancy;
import net.jetrix.config.Settings;
import net.jetrix.config.Special;
import net.jetrix.messages.channel.NewGameMessage;

/**
 * A filter randomizing the blocks and the specials occurancies when the game starts.
 *
 * @since 0.3
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class RandomFilter extends GenericFilter
{
    public void onMessage(NewGameMessage m, List<Message> out)
    {
        // get the settings
        Settings settings = m.getSettings();
        
        // randomize the block and special occurancies
        settings.setBlockOccurancy(getRandomOccurancy(Block.class));
        settings.setSpecialOccurancy(getRandomOccurancy(Special.class));
        
        // forward the new game message
        out.add(m);
    }

    private <T extends Enum> Occurancy<T> getRandomOccurancy(Class<T> enumType)
    {
        Occurancy<T> occurancy = new Occurancy<T>();
        for (T element : enumType.getEnumConstants())
        {
            occurancy.setOccurancy(element, (int) (Math.random() * 100));
        }
        
        occurancy.normalize();
        
        return occurancy;
    }
}
