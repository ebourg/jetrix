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

package net.jetrix;

/**
 * A service running in background.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 * @since 0.1.4
 */
public interface Service
{
    /**
     * Return the name of the service.
     */
    String getName();

    /**
     * Start the service.
     */
    void start();

    /**
     * Stop the service.
     */
    void stop();

    /**
     * Tell if the service is currently running.
     */
    boolean isRunning();

    /**
     * Tell if the service is started automatically on server startup.
     */
    boolean isAutoStart();

    /**
     * Set the auto start status.
     */
    void setAutoStart(boolean autoStart);
}
