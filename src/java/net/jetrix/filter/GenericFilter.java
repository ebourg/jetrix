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

package net.jetrix.filter;

import java.util.*;
import net.jetrix.*;
import net.jetrix.messages.*;

/**
 * Defines a generic filter to be used and extended by filter developpers.
 * GenericFilter makes writing filters easier by dispatching messages to an
 * appropriate method according to the type of this message (onPline(),
 * onStartGame(), etc...).
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public abstract class GenericFilter extends MessageFilter
{

    public final void process(Message m, List out)
    {
        // overwritable pre processing
        onMessage(m);

        // message dispatching
        if (m instanceof SpecialMessage)           onMessage((SpecialMessage)m, out);
        else if (m instanceof FieldMessage)        onMessage((FieldMessage)m, out);
        else if (m instanceof PlineMessage)        onMessage((PlineMessage)m, out);
        else if (m instanceof LevelMessage)        onMessage((LevelMessage)m, out);
        else if (m instanceof PlayerLostMessage)   onMessage((PlayerLostMessage)m, out);
        else if (m instanceof PlineActMessage)     onMessage((PlineActMessage)m, out);
        else if (m instanceof TeamMessage)         onMessage((TeamMessage)m, out);
        else if (m instanceof JoinMessage)         onMessage((JoinMessage)m, out);
        else if (m instanceof LeaveMessage)        onMessage((LeaveMessage)m, out);
        else if (m instanceof PlayerNumMessage)    onMessage((PlayerNumMessage)m, out);
        else if (m instanceof StartGameMessage)    onMessage((StartGameMessage)m, out);
        else if (m instanceof StopGameMessage)     onMessage((StopGameMessage)m, out);
        else if (m instanceof NewGameMessage)      onMessage((NewGameMessage)m, out);
        else if (m instanceof EndGameMessage)      onMessage((EndGameMessage)m, out);
        else if (m instanceof PauseMessage)        onMessage((PauseMessage)m, out);
        else if (m instanceof ResumeMessage)       onMessage((ResumeMessage)m, out);
        else if (m instanceof IngameMessage)       onMessage((IngameMessage)m, out);
        else if (m instanceof GmsgMessage)         onMessage((GmsgMessage)m, out);
        else if (m instanceof NoConnectingMessage) onMessage((NoConnectingMessage)m, out);
        else
        {
            onMessage(m, out);
        }
    }

    /**
     * Message pre-processing. This method is called at the beginning of the
     * <tt>process(Message m, List out)</tt> method and allow custom
     * processing for all filtered messages.
     */
    public void onMessage(Message m) { }

    public void onMessage(Message m, List out) { out.add(m); }

    public void onMessage(PlineMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(PlineActMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(TeamMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(JoinMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(LeaveMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(PlayerNumMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(StartGameMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(NewGameMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(EndGameMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(PauseMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(ResumeMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(GmsgMessage m, List out)
    {
        out.add(m);
    }

    private void onMessage(SpecialMessage m, List out)
    {
        // message pre-processing
        onSpecial(m);

        // message dispatching
        if (m instanceof OneLineAddedMessage)        onMessage((OneLineAddedMessage)m, out);
        else if (m instanceof TwoLinesAddedMessage)  onMessage((TwoLinesAddedMessage)m, out);
        else if (m instanceof FourLinesAddedMessage) onMessage((FourLinesAddedMessage)m, out);
        else if (m instanceof AddLineMessage)        onMessage((AddLineMessage)m, out);
        else if (m instanceof ClearLineMessage)      onMessage((ClearLineMessage)m, out);
        else if (m instanceof ClearSpecialsMessage)  onMessage((ClearSpecialsMessage)m, out);
        else if (m instanceof RandomClearMessage)    onMessage((RandomClearMessage)m, out);
        else if (m instanceof BlockQuakeMessage)     onMessage((BlockQuakeMessage)m, out);
        else if (m instanceof BlockBombMessage)      onMessage((BlockBombMessage)m, out);
        else if (m instanceof GravityMessage)        onMessage((GravityMessage)m, out);
        else if (m instanceof NukeFieldMessage)      onMessage((NukeFieldMessage)m, out);
        else if (m instanceof SwitchFieldsMessage)   onMessage((SwitchFieldsMessage)m, out);
    }

    /**
     * Special block message pre-processing. This method is called for all
     * specials filtered and allow custom processing for all specials
     * (lines added, blockbomb switchs, etc...).
     */
    public void onSpecial(Message m) { }

    public void onMessage(LevelMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(FieldMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(PlayerLostMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(DisconnectedMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(AddPlayerMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(CommandMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(OneLineAddedMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(TwoLinesAddedMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(FourLinesAddedMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(AddLineMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(ClearLineMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(NukeFieldMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(RandomClearMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(SwitchFieldsMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(ClearSpecialsMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(GravityMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(BlockQuakeMessage m, List out)
    {
        out.add(m);
    }

    public void onMessage(BlockBombMessage m, List out)
    {
        out.add(m);
    }

}
