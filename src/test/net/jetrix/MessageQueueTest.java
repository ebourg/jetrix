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

package net.jetrix;

import junit.framework.*;

import net.jetrix.messages.*;

/**
 * JUnit TestCase for the class net.jetrix.MessageQueue
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class MessageQueueTest extends TestCase
{
    private MessageQueue mq;

    protected void setUp() throws Exception
    {
        mq = new MessageQueue();
    }

    public void testOrder() throws Exception
    {
        Message message1 = new CommandMessage();
        Message message2 = new CommandMessage();
        Message message3 = new CommandMessage();

        mq.put(message1);
        mq.put(message2);
        mq.put(message3);

        assertEquals(message1, mq.get());
        assertEquals(message2, mq.get());
        assertEquals(message3, mq.get());
    }
}
