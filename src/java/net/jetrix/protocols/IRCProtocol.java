/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2003  Emmanuel Bourg
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

import java.util.*;
import net.jetrix.*;
import net.jetrix.messages.*;

/**
 * Protocol to communicate with IRC clients.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class IRCProtocol implements Protocol
{
    private static Map styles = new HashMap();

    static 
    {
        styles.put("red", "\u000304");
        styles.put("black", "\u000301");
        styles.put("green", "\u000303");
        styles.put("lightGreen", "\u000309");
        styles.put("darkBlue", "\u000302");
        styles.put("blue", "\u000312");
        styles.put("cyan", "\u000310");
        styles.put("aqua", "\u000311");
        styles.put("yellow", "\u000308");
        styles.put("kaki", "\u000307");
        styles.put("brown", "\u000305");
        styles.put("lightGray", "\u000315");
        styles.put("gray", "\u000314");
        styles.put("magenta", "\u000313");
        styles.put("purple", "\u000306");
        styles.put("b", "\u0002");
        styles.put("i", "\u0016");
        styles.put("u", "\u0037");
        styles.put("white", "\u000300");
    }

    /**
     * Return the name of this protocol
     */
    public String getName()
    {
        return "IRC";
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
    public String translate(Message m, Locale locale)
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

    public Map getStyles()
    {
        return styles;
    }

    public String applyStyle(String text)
    {
        // to be optimized later
        Map styles = getStyles();
        if (styles == null) return text;
        
        Iterator keys = styles.keySet().iterator();
        while (keys.hasNext())
        {
            String key = (String)keys.next();
            String value = (String)styles.get(key);
            if (value == null) { value = ""; }
            text = text.replaceAll("<" + key + ">", value);
            text = text.replaceAll("</" + key + ">", value);
        }
        return text;
    }

    public String toString()
    {
        return "[Protocol name=" + getName() + "]";
    }

}
