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
 * Color codes.
 *
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public interface ChatColors
{    
    char red         = '\u0014';
    char black       = '\u0004';
    
    char green       = '\u000c'; // not sure 
    char lightGreen  = '\u000e';
    
    char darkBlue    = '\u0011'; //
    char blue        = '\u0005'; //
    char cyan        = '\u0003';    
    
    char aqua        = '\u0017'; //
    
    char yellow      = '\u0019'; 
    char kaki        = '\u0012'; //        
    char brown       = '\u0010';
     
    char lightgray   = '\u000f'; // or \u0015
    char gray	     = '\u0006'; // or \u000b
    
    char lightPurple = '\u0008'; //
    char purple      = '\u0013'; //    
        
    char bold        = '\u0002'; //
    char italic      = '\u0016'; //

    char white = '\u0018'; //

    char[] slots = { darkBlue, kaki, brown, aqua, green, purple  };

}