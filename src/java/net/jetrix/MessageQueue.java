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

import java.io.*;

/**
 * FIFO for internal messages with blocking output.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class MessageQueue
{
    private MessageQueue.Node head;
    private MessageQueue.Node tail;
    private Object putLock;
    private Object getLock;
    private boolean closed;

    /**
     * Constructs a new empty MessageQueue.
     */
    public MessageQueue()
    {
        putLock = new Object();
        getLock = new Object();
    }

    /**
     * Fetch the next Message in the queue. The thread will wait until a message
     * is put in the queue or until the queue is closed.
     *
     * @return next Message waiting in the queue
     *
     * @exception InterruptedIOException thrown when trying to get an element from a closed queue.
     */
    public Message get() throws InterruptedIOException
    {
        synchronized (getLock)
        {
            // blocking the thread until a new message arrives or the queue is closed
            while (head == null && !closed)
            {
                try
                {
                    getLock.wait(2000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            if (closed)
            {
                throw new InterruptedIOException("MessageQueue closed");
            }

            MessageQueue.Node t = head;
            head = head.next;

            return t.value;
        }
    }

    /**
     * Add a new Message in the queue.
     *
     * @param message the Message to add in the queue
     */
    public void put(Message message)
    {
        if (!closed)
        {
            synchronized (putLock)
            {
                MessageQueue.Node node = new MessageQueue.Node();
                node.value = message;

                if (tail != null)
                {
                    tail.next = node;
                }
                if (head == null)
                {
                    head = node;
                }

                tail = node;

                synchronized (getLock)
                {
                    getLock.notify();
                }
            }
        }
    }

    /**
     * Close the queue. The queue will no longer accept new messages,
     * any call to the put(Message m) method will throw an InterruptedIOException
     */
    public void close()
    {
        closed = true;
        getLock.notifyAll();
    }

    /**
     * An internal node containing a Message and keeping track
     * of the next Message available in the queue.
     */
    private class Node
    {
        Message value;
        MessageQueue.Node next;
    }
}
