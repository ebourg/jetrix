/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001  Emmanuel Bourg
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
 
package org.lfjr.jts;

import java.io.*;

/**
 * FIFO for internal messages.
 *
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class MessageQueue
{
    private MessageQueue.Node head, tail;
    private Object putLock, getLock;
    private boolean closed;
	
	
    public MessageQueue()
    {
    	putLock = new Object();
    	getLock = new Object();
    }


    public Message get() throws InterruptedIOException
    {
    	synchronized(getLock)
    	{
    	    while (head == null && !closed)
    	    {
    	    	try { getLock.wait(2000); } catch(InterruptedException e) { e.printStackTrace();  }
    	    }
    	    
    	    if (closed) throw new InterruptedIOException("MessageQueue closed");
    	    
    	    MessageQueue.Node t = head;
    	    head = head.next;
    	    
    	    return t.value;
    	}    	
    }
    
    
    public void put(Message elem)
    {
    	if (!closed)
    	{
    		
    	synchronized(putLock)
    	{
    	    MessageQueue.Node m = new MessageQueue.Node();
    	    m.value = elem;
    	    
    	    if (tail != null) { tail.next = m; }
    	    if (head == null) { head = m; }
    	    
    	    tail = m;    	    
    	    
    	    synchronized(getLock)
    	    {
    	        getLock.notify();    		
    	    }
    	}    	
    	
    	}    	
    }


    public void close()
    {
    	closed = true;
    	getLock.notifyAll();
    }


    private class Node
    {
        Message value;
        MessageQueue.Node next;      	    	
    }
}