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

package org.lfjr.jts.filter;

import java.util.*;
import org.lfjr.jts.*;

/**
 * Defines a generic filter to be used and extended by filter developpers.
 * GenericFilter makes writing filters easier by dispatching messages to an
 * appropriate method according to the type of this message (onPline(),
 * onStartGame(), etc...). The process() method does all the job and
 * shouldn't be overwritten when extending this class.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public abstract class GenericFilter extends MessageFilter
{

    public void process(Message m, List out)
    {
        switch( m.getCode() )
        {
            case Message.MSG_UNKNOWN:      onUnknown(m, out); break;
            case Message.MSG_PLINE:        onPline(m, out); break;
            case Message.MSG_PLINEACT:     onPlineAct(m, out); break;
            case Message.MSG_TEAM:         onTeam(m, out); break;
            case Message.MSG_PLAYERJOIN:   onPlayerJoin(m, out); break;
            case Message.MSG_PLAYERLEAVE:  onPlayerLeave(m, out); break;
            case Message.MSG_PLAYERNUM:    onPlayerNum(m, out); break;
            case Message.MSG_STARTGAME:    onStartGame(m, out); break;
            case Message.MSG_NEWGAME:      onNewGame(m, out); break;
            case Message.MSG_ENDGAME:      onEndGame(m, out); break;
            case Message.MSG_PAUSE:        onPause(m, out); break;
            case Message.MSG_RESUME:       onResume(m, out); break;
            case Message.MSG_GMSG:         onGmsg(m, out); break;
            case Message.MSG_SB:           onSpecial(m, out); break;
            case Message.MSG_LVL:          onLevel(m, out); break;
            case Message.MSG_FIELD:        onField(m, out); break;
            case Message.MSG_PLAYERLOST:   onPlayerLost(m, out); break;
            case Message.MSG_DISCONNECTED: onDisconnected(m, out); break;
            case Message.MSG_ADDPLAYER:    onAddPlayer(m, out); break;
            case Message.MSG_SLASHCMD:     onSlashCommand(m, out); break;
            default: out.add(m);
        }
    }

    public void onUnknown(Message m, List out) { out.add(m); }
    public void onPline(Message m, List out) { out.add(m); }
    public void onPlineAct(Message m, List out) { out.add(m); }
    public void onTeam(Message m, List out) { out.add(m); }
    public void onPlayerJoin(Message m, List out) { out.add(m); }
    public void onPlayerLeave(Message m, List out) { out.add(m); }
    public void onPlayerNum(Message m, List out) { out.add(m); }
    public void onStartGame(Message m, List out) { out.add(m); }
    public void onNewGame(Message m, List out) { out.add(m); }
    public void onEndGame(Message m, List out) { out.add(m); }
    public void onPause(Message m, List out) { out.add(m); }
    public void onResume(Message m, List out) { out.add(m); }
    public void onGmsg(Message m, List out) { out.add(m); }
    public void onSpecial(Message m, List out) { out.add(m); }
    public void onLevel(Message m, List out) { out.add(m); }
    public void onField(Message m, List out) { out.add(m); }
    public void onPlayerLost(Message m, List out) { out.add(m); }
    public void onDisconnected(Message m, List out) { out.add(m); }
    public void onAddPlayer(Message m, List out) { out.add(m); }
    public void onSlashCommand(Message m, List out) { out.add(m); }

}
