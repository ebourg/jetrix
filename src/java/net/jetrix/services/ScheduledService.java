/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2004  Emmanuel Bourg
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

package net.jetrix.services;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A service running a task at a fixed rate. Services based on ScheduledService
 * expect a <tt>delay</tt> parameter for the delay in milliseconds before the
 * task is first executed, and a <tt>period</tt> parameter for the time in
 * milliseconds between successive executions of the task.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 * @since 0.2
 */
public abstract class ScheduledService extends AbstractService
{
    private long period;
    private long delay;
    private Timer timer;

    /**
     * Initialization performed before the timer is started.
     */
    protected void init() { }

    public void start()
    {
        if (!isRunning())
        {
            init();

            TimerTask task = new TimerTask()
            {
                public void run()
                {
                    ScheduledService.this.run();
                }
            };

            // start the timer
            timer = new Timer();
            timer.schedule(task, delay, period);
        }
    }

    public void stop()
    {
        // stop the timer
        if (isRunning())
        {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * Check is the service is running.
     */
    public boolean isRunning()
    {
        return timer != null;
    }

    /**
     * Execute the task
     */
    protected abstract void run();

    /**
     * Get the time in milliseconds between successive executions of the task.
     */
    public long getPeriod()
    {
        return period;
    }

    /**
     * Set the time in milliseconds between successive executions of the task.
     */
    public void setPeriod(long period)
    {
        this.period = period;
    }

    /**
     * Get the delay in milliseconds before the task is first executed.
     */
    public long getDelay()
    {
        return delay;
    }

    /**
     * Set the delay in milliseconds before the task is first executed.
     */
    public void setDelay(long delay)
    {
        this.delay = delay;
    }

}
