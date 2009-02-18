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

package net.jetrix.agent;

import java.io.IOException;

import net.jetrix.messages.channel.*;
import net.jetrix.protocols.TspecProtocol;

/**
 * TSpec agent to log on a TetriNET server as a spectator.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TSpecAgent extends TetrinetAgent
{
    private String password;

    public TSpecAgent(String name, String password)
    {
        super(name);
        this.password = password;
        this.protocol = new TspecProtocol();
    }

    public void connect(String hostname) throws IOException
    {
        connect(hostname, 31458, password);
    }

    public void onMessage(PlayerNumMessage m)
    {
        super.onMessage(m);

        // send the tspec password as the team name
        TeamMessage team = new TeamMessage();
        team.setSlot(m.getSlot());
        team.setName(password);
        try
        {
            send(team);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
