/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2002  Emmanuel Bourg
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

package net.jetrix.protocols;

import net.jetrix.*;
import net.jetrix.messages.*;

/**
 * An abstract protocol to communicate with a client.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TspecProtocol extends TetrinetProtocol
{
    /**
     * Return the name of this protocol
     */
    public String getName()
    {
        return "tspec";
    }

    /**
     * Parse the specified string and return the corresponding server
     * message for this protocol.
     */
    public Message getMessage(String line)
    {
        return null;
    }

    /**
     * Translate the specified message into a string that will be sent
     * to a client using this protocol.
     */
    public String translate(Message m)
    {
        return null;
    }

    public String translate(PlineMessage m)
    {
        return null;
    }

    public String translate(PlineActMessage m)
    {
        return null;
    }

    public String translate(TeamMessage m)
    {
        return null;
    }

    public String translate(JoinMessage m)
    {
        return null;
    }

    public String translate(LeaveMessage m)
    {
        return null;
    }

    public String translate(PlayerNumMessage m)
    {
        return null;
    }

    public String translate(StartGameMessage m)
    {
        return null;
    }

    public String translate(NewGameMessage m)
    {
        return null;
    }

    public String translate(EndGameMessage m)
    {
        return null;
    }

    public String translate(PauseMessage m)
    {
        return null;
    }

    public String translate(ResumeMessage m)
    {
        return null;
    }

    public String translate(GmsgMessage m)
    {
        return null;
    }

    public String translate(LevelMessage m)
    {
        return null;
    }

    public String translate(FieldMessage m)
    {
        return null;
    }

    public String translate(PlayerLostMessage m)
    {
        return null;
    }

    public String translate(DisconnectedMessage m)
    {
        return null;
    }

    public String translate(AddPlayerMessage m)
    {
        return null;
    }

    public String translate(CommandMessage m)
    {
        return null;
    }

    public String translate(OneLineAddedMessage m)
    {
        return null;
    }

    public String translate(TwoLinesAddedMessage m)
    {
        return null;
    }

    public String translate(FourLinesAddedMessage m)
    {
        return null;
    }

    public String translate(AddLineMessage m)
    {
        return null;
    }

    public String translate(ClearLineMessage m)
    {
        return null;
    }

    public String translate(NukeFieldMessage m)
    {
        return null;
    }

    public String translate(RandomClearMessage m)
    {
        return null;
    }

    public String translate(SwitchFieldsMessage m)
    {
        return null;
    }

    public String translate(ClearSpecialsMessage m)
    {
        return null;
    }

    public String translate(GravityMessage m)
    {
        return null;
    }

    public String translate(BlockQuakeMessage m)
    {
        return null;
    }

    public String translate(BlockBombMessage m)
    {
        return null;
    }

    public String toString()
    {
        return "[Protocol name=" + getName() + "]";
    }

}
