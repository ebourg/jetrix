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

/**
 * Internal message sent between server, channels and client handlers.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class Message
{
    public static final int TYPE_SERVER  = 0; // disconnected, / commands, noconnecting
    public static final int TYPE_CHANNEL = 1; // pline, plineact, playerjoin, team, playerleave, startgame
    public static final int TYPE_INGAME  = 2; // f, sb, lvl, playerlost, pause, newgame, endgame, gmsg

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
    public static final int MSG_GMSG         = 10;
    public static final int MSG_SB           = 11;
    public static final int MSG_LVL          = 12;
    public static final int MSG_FIELD        = 13;
    public static final int MSG_PLAYERLOST   = 14;
    public static final int MSG_DISCONNECTED = 15;
    public static final int MSG_NOCONNECTING = 16;
    public static final int MSG_ADDPLAYER    = 17;
    public static final int MSG_RESTART      = 18;
    public static final int MSG_SHUTDOWN     = 19;
    public static final int MSG_SLASHCMD     = 20;

    private Object params[];
    private int type;
    private int code;
    private String raw;
    private Object source; // (server, channel, client) should use a dedicated interface ()

    public Message()
    {
        type = MSG_UNKNOWN;
    }

    public Message(int code)
    {
        this.code = code;
    }

    public int getType()
    {
        return type;
    }

    public int getCode()
    {
        return code;
    }

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
                  
                case MSG_ENDGAME:
                  //raw = "startgame 0 " + (Integer)getParameter(0);
                  raw = "endgame";
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

            }
        }

        return raw;
    }

    public Object[] getParameters()
    {
        return params;
    }

    public Object getParameter(int i)
    {
        return params[i];
    }

    public int getNbParameters()
    {
        return params.length;
    }
    
    public Object getSource()
    {
        return source;	
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public void setRawMessage(String raw)
    {
        this.raw = raw;
    }

    public void setParameters(Object[] params)
    {
        this.params = params;
        raw = null;
    }
    
    public void setSource(Object source)
    {
        this.source = source;
    }    

    public String toString()
    {
        return "[Message type="+type+" code="+code+" params="+params+"]";
    }
}