/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001  Emmanuel Bourg
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
    public static final int MSG_STARTGAME    = 5;
    public static final int MSG_NEWGAME      = 6;
    public static final int MSG_ENDGAME      = 7;
    public static final int MSG_PAUSE        = 8;
    public static final int MSG_GMSG         = 9;
    public static final int MSG_SB           = 10;
    public static final int MSG_LVL          = 11;
    public static final int MSG_FIELD        = 12;
    public static final int MSG_PLAYERLOST   = 13;
    public static final int MSG_DISCONNECTED = 14;
    public static final int MSG_NOCONNECTING = 15;
    public static final int MSG_ADDPLAYER    = 16;
    public static final int MSG_RESTART      = 17;
    public static final int MSG_SHUTDOWN     = 18;  
    public static final int MSG_SLASHCMD     = 19;
                                   		
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
    }    
}