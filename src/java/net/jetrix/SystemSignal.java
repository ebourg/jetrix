/**
 * Jetrix TetriNET Server
 * Copyright (C) 2009  Emmanuel Bourg
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

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Wrapper around the sun.misc.Signal class to prevent linkage errors.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
class SystemSignal
{
    /**
     * Intercepts a system signal and executes the specified hook.
     *
     * @param signal the signal to be intercepted (INT, KILL, HUP...)
     * @param hook   the code to be executed
     */
    public static void handle(String signal, final Runnable hook)
    {
        Signal.handle(new Signal(signal), new SignalHandler()
        {
            public void handle(Signal signal)
            {
                hook.run();
            }
        });
    }
}
