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

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.jcrontab.data.CalendarBuilder;
import org.jcrontab.data.CrontabEntryBean;
import org.jcrontab.data.CrontabEntryException;
import org.jcrontab.data.CrontabParser;

/**
 * A service running a task at a fixed date. Services extending this service
 * expect a <tt>pattern</tt> parameter setting the execution time, this pattern
 * uses the cron format of Unix systems extended with a 6th field for the year
 * (see http://www.nncron.ru/nncronlt/help/EN/working/cron-format.htm).
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 * @since 0.1.4
 */
public abstract class CronService extends AbstractService
{
    protected String pattern;
    private Timer timer;
    private Runnable task;
    private CrontabEntryBean cron;

    public void setPattern(String pattern)
    {
        this.pattern = pattern;
    }

    /**
     * Initialization performed before the timer is started.
     */
    protected void init() { }

    public void start() {
        if (!isRunning()) {
            init();

            // parse the cron pattern
            CrontabParser parser = new CrontabParser();
            try
            {
                cron = parser.marshall(pattern);
                cron.setBSeconds(new boolean[60]);
            }
            catch (CrontabEntryException e)
            {
                e.printStackTrace();
                return;
            }

            task = getTask();

            // start the timer
            timer = new Timer();
            scheduleNext();
        }
    }

    public void stop() {
        // stop the timer
        if (isRunning()) {
            timer.cancel();
            timer = null;
        }
    }

    public boolean isRunning()
    {
        return timer != null;
    }

    /**
     * Return the task to run.
     */
    protected abstract Runnable getTask();

    private class Task extends TimerTask
    {
        public void run()
        {
            task.run();
            scheduleNext();
        }
    }

    private void scheduleNext()
    {
        timer.schedule(new Task(), getNextExecutionDate());
    }

    /**
     * Return the next time the task has to be executed.
     */
    private Date getNextExecutionDate()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 1);
        Date date = new CalendarBuilder().buildCalendar(cron, calendar.getTime());
        System.out.println(date);
        return date;
    }
}
