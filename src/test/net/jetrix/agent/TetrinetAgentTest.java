/**
 * Jetrix TetriNET Server
 * Copyright (C) 2008  Emmanuel Bourg
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

package net.jetrix.agent;

import junit.framework.TestCase;

import net.jetrix.messages.channel.PlineMessage;
import net.jetrix.messages.channel.TeamMessage;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TetrinetAgentTest extends TestCase
{
    public void testConnect() throws Exception
    {
        TetrinetAgent agent = new TetrinetAgent("JetrixBot");
        agent.connect("tetrinet.fr");
        
        Thread.sleep(200);
        
        agent.send(new PlineMessage("Hi there!"));

        agent.disconnect();
    }
}
