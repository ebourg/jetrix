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

package org.lfjr.jts;

import java.util.*;
import org.lfjr.jts.config.*;

/**
 * Internal message sent between server, channels and client handlers.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class Message
{
    // Types
    public static final int TYPE_SERVER  = 0; // disconnected, / commands, noconnecting
    public static final int TYPE_CHANNEL = 1; // pline, plineact, playerjoin, team, playerleave, startgame
    public static final int TYPE_INGAME  = 2; // f, sb, lvl, playerlost, pause, newgame, endgame, gmsg

    // Codes
    public static final int MSG_UNKNOWN      = -1;
    public static final int MSG_PLINE        = 0;
    public static final int MSG_PLINEACT     = 1;
    public static final int MSG_TEAM         = 2;
    public static final int MSG_PLAYERJOIN   = 3;
    public static final int MSG_PLAYERLEAVE  = 4;
    public static final int MSG_PLAYERNUM    = 5;
    public static final int MSG_STARTGAME    = 6;
    public static final int MSG_NEWGAME      = 7;
    public static final int MSG_ENDGAME      = 8;
    public static final int MSG_PAUSE        = 9;
    public static final int MSG_RESUME       = 10;
    public static final int MSG_INGAME       = 11;
    public static final int MSG_GMSG         = 12;
    public static final int MSG_SB           = 13;
    public static final int MSG_LVL          = 14;
    public static final int MSG_FIELD        = 15;
    public static final int MSG_TEAMWON      = 16;
    public static final int MSG_PLAYERWON    = 17;
    public static final int MSG_PLAYERLOST   = 18;
    public static final int MSG_DISCONNECTED = 19;
    public static final int MSG_NOCONNECTING = 20;
    public static final int MSG_ADDPLAYER    = 21;
    public static final int MSG_RESTART      = 22;
    public static final int MSG_SHUTDOWN     = 23;
    public static final int MSG_SLASHCMD     = 24;

    private Object params[];
    private int type;
    private int code;
    private String raw;
    private Object source; // (server, channel, client) should use a dedicated interface ()
    private Date date;

    /**
     * Constructs a new server message of unknown signification.
     */
    public Message()
    {
        this(MSG_UNKNOWN);
    }

    /**
     * Constructs a new server message of the specified code.
     */
    public Message(int code)
    {
        this(code, null);
    }

    /**
     * Constructs a new server message with the specified code and parameters.
     */
    public Message(int code, Object[] params)
    {
        this.code = code;
        this.params = params;
        this.date = new Date();
    }

    /**
     * Returns the type of this message. There are 3 types available, server
     * (<tt>Message.TYPE_SERVER</tt>) for internal messages used by the server,
     * channel (<tt>Message.TYPE_CHANNEL</tt>) for messages handled by a game
     * channel, and ingame (<tt>Message.TYPE_INGAME</tt>), same as channel
     * message but restricted to messages related to a running game.
     *
     * @return type of this message
     */
    public int getType()
    {
        return type;
    }

    /**
     * Returns the code of this message. The code defines the semantic of
     * the message.
     *
     * @return code of this message
     */
    public int getCode()
    {
        return code;
    }

    /**
     * Returns the raw String representation of this message. This string is to
     * be sent to a TetriNET client.
     *
     * @return a raw message understandable by a TetriNET client.
     */
    public String getRawMessage()
    {
        if (raw==null)
        {
            switch (code)
            {
                case MSG_PLINE:
                    Integer slot = (Integer)getParameter(0);
                    String message = (String)getParameter(1);
                    raw = "pline " + slot + " " + message;
                    //for (int i=1; i<getParameterCount(); i++) { raw += " " + (String)getParameter(i); }
                    break;

                case MSG_PLINEACT:
                    slot = (Integer)getParameter(0);
                    message = (String)getParameter(1);
                    raw = "plineact " + slot + " " + message;
                    break;

                case MSG_PLAYERJOIN:
                    slot = (Integer)getParameter(0);
                    String playername = (String)getParameter(1);
                    raw = "playerjoin " + slot + " " + playername;
                    break;

                case MSG_TEAM:
                    slot = (Integer)getParameter(0);
                    Object team = getParameter(1);
                    raw = "team " + slot + ( (team==null)?"":" "+team );
                    break;

                case MSG_PLAYERLEAVE:
                    raw = "playerleave " + (Integer)getParameter(0);
                    break;

                case MSG_PLAYERNUM:
                    slot = (Integer)getParameter(0);
                    raw = "playernum " + slot;
                    break;

                case MSG_STARTGAME:
                    Settings s = (Settings)getParameter(1);
                    raw = "newgame " + s.getStackHeight() 
                          + " " + s.getStartingLevel()
                          + " " + s.getLinesPerLevel() 
                          + " " + s.getLevelIncrease()
                          + " " + s.getLinesPerSpecial() 
                          + " " + s.getSpecialAdded()
                          + " " + s.getSpecialCapacity() + " ";

                    for (int i = 0; i<7; i++)
                    {
                        for (int j = 0; j<s.getBlockOccurancy(i); j++) { raw = raw + (i + 1); }
                    }

                    raw += " ";

                    for (int i = 0; i<9; i++)
                    {
                        for (int j = 0; j<s.getSpecialOccurancy(i); j++) { raw = raw + (i + 1); }
                    }

                    raw += " " + (s.getAverageLevels() ? "1" : "0")
                         + " " + (s.getClassicRules()  ? "1" : "0");
                    break;

                case MSG_ENDGAME:
                    raw = "endgame";
                    break;

                case MSG_PAUSE:
                    raw = "pause 1";
                    break;

                case MSG_RESUME:
                    raw = "pause 0";
                    break;

                case MSG_INGAME:
                    raw = "ingame";
                    break;

                case MSG_PLAYERLOST:
                    slot = (Integer)getParameter(0);
                    raw = "playerlost " + slot;
                    break;

                case MSG_FIELD:
                    slot = (Integer)getParameter(0);
                    String layout = (String)getParameter(1);
                    raw = "f " + slot + " " + layout;
                    break;
                  
                case MSG_NOCONNECTING:
                    raw = "noconnecting " + getParameter(0);
                    break;
            }
        }

        return raw;
    }

    /**
     * Returns the array of parameters associated with this message.
     */
    public Object[] getParameters()
    {
        return params;
    }

    /**
     * Returns the parameter at the specified position.
     *
     * @param index  index of parameter to return
     *
     * @return the parameter at the specified position
     *
     * @throw IndexOutOfBoundsException if index is out of range (<tt>index < 0 || index >= size()</tt>).
     */
    public Object getParameter(int index)
    {
        if (params != null && !(index < 0 || index >= getParameterCount()))
        {
            return params[index];
        }
        else
        {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Returns the parameter at the specified position as a string.
     */
    public String getStringParameter(int index)
    {
        return (String)getParameter(index);
    }

    /**
     * Returns the parameter at the specified position as an integer.
     */
    public int getIntParameter(int index)
    {
        return ( (Integer)getParameter(index) ).intValue();
    }

    /**
     * Returns the number of parameters associated with this message.
     */
    public int getParameterCount()
    {
        return (params != null)?params.length:0;
    }

    /**
     * Returns the source of this message.
     */
    public Object getSource()
    {
        return source;
    }

    /**
     * Returns the creation date of this message.
     */
    public Date getDate()
    {
        return date;
    }

    /**
     * Sets the type of this message.
     */
    public void setType(int type)
    {
        this.type = type;
    }

    /**
     * Sets the code of this message.
     */
    public void setCode(int code)
    {
        this.code = code;
    }

    /**
     * Sets the row string representation of this message.
     */
    public void setRawMessage(String raw)
    {
        this.raw = raw;
    }

    /**
     * Sets the array of parameters associated with this message.
     */
    public void setParameters(Object[] params)
    {
        this.params = params;
        //raw = null;
    }

    /**
     * Sets the source of this message.
     */
    public void setSource(Object source)
    {
        this.source = source;
    }

    /**
     * Indicates whether some other Message object is "equal to" this one.
     *
     * @param obj   the reference object with which to compare.
     *
     * @return <tt>true</tt> if this object is the same as the obj argument; <tt>false</tt> otherwise.
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = false;

        if (obj instanceof Message)
        {
            Message m = (Message)obj;
            isEqual = m.getType()==getType() && m.getCode()==getCode() && m.getRawMessage()==getRawMessage();

            /**
             * Should compare parameters one by one since internal messages
             * don't have a raw string representation.
             */

        }

        return isEqual;
    }

    /**
     * String representation of this Message.
     */
    public String toString()
    {
        StringBuffer paramsView = new StringBuffer();
        paramsView.append("{");
        if (params != null)
        {
            for (int i=0; i<params.length; i++)
            {
                paramsView.append(params[i]);
                if (i!=params.length-1) paramsView.append("; ");
            }
        }
        paramsView.append("}");

        return "[Message type="+type+" code="+code+" params="+paramsView+"]";
    }

}
