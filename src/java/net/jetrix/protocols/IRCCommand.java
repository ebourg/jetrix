/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2004  Emmanuel Bourg
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

/**
 * IRC commands.
 *
 * @since 0.2
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public interface IRCCommand {

    /**
     * Password message
     */
    public static final String PASS = "PASS";

    /**
     * Nick message
     */
    public static final String NICK = "NICK";

    /**
     * User message
     */
    public static final String USER = "USER";

    /**
     * Server message
     */
    public static final String SERVER = "SERVER";

    /**
     * Opererator message
     */
    public static final String OPER = "OPER";

    /**
     * Quit message
     */
    public static final String QUIT = "QUIT";

    /**
     * Server quit message
     */
    public static final String SQUIT = "SQUIT";

    /**
     * Join message
     */
    public static final String JOIN = "JOIN";

    /**
     * Part message
     */
    public static final String PART = "PART";

    /**
     * Mode message
     */
    public static final String MODE = "MODE";

    /**
     * Topic message
     */
    public static final String TOPIC = "TOPIC";

    /**
     * Names message
     */
    public static final String NAMES = "NAMES";

    /**
     * List message
     */
    public static final String LIST = "LIST";

    /**
     * Invite message
     */
    public static final String INVITE  = "INVITE ";

    /**
     * Kick command
     */
    public static final String KICK = "KICK";

    /**
     * Version message
     */
    public static final String VERSION = "VERSION";

    /**
     * Stats message
     */
    public static final String STATS = "STATS";

    /**
     * Links message
     */
    public static final String LINKS = "LINKS";

    /**
     * Time message
     */
    public static final String TIME = "TIME";

    /**
     * Connect message
     */
    public static final String CONNECT = "CONNECT";

    /**
     * Trace message
     */
    public static final String TRACE = "TRACE";

    /**
     * Admin command
     */
    public static final String ADMIN = "ADMIN";

    /**
     * Info command
     */
    public static final String INFO  = "INFO ";

    /**
     * Private message
     */
    public static final String PRIVMSG = "PRIVMSG";

    /**
     * Notice message
     */
    public static final String NOTICE = "NOTICE";

    /**
     * Who query
     */
    public static final String WHO = "WHO";

    /**
     * Whois query
     */
    public static final String WHOIS = "WHOIS";

    /**
     * Whowas
     */
    public static final String WHOWAS = "WHOWAS";

    /**
     * Kill message
     */
    public static final String KILL = "KILL";

    /**
     * Ping message
     */
    public static final String PING  = "PING ";

    /**
     * Pong message
     */
    public static final String PONG = "PONG";

    /**
     * Error
     */
    public static final String ERROR = "ERROR";

}
