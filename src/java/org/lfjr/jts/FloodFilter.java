package org.lfjr.jts;

import java.util.*;

public class FloodFilter
{
    private int capacity;
    private int delay;
    private long fifo[];
    
    private int index;
    
    public FloodFilter(int capacity, int delay)
    {
    	fifo = new long[capacity];
    	this.capacity = capacity;
    	this.delay = delay;        	
    }
    
    
    public boolean isRateExceeded(Date d)
    {
    	long t = d.getTime();
    	
    	long t1 = fifo[index];
    	
    	fifo[index] = t;
    	
    	index = (index + 1) % capacity;
    	
    	return (t-t1)<delay;    	    	
    }
    
}