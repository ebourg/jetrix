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

package net.jetrix.config;

import org.apache.commons.digester.*;

/**
 * RuleSet for processing the content of a jetrix configuration file.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ConfigRuleSet extends RuleSetBase
{

    public void addRuleInstances(Digester digester)
    {
        // server parameters
        digester.addCallMethod("tetrinet-server", "setHost", 1);
        digester.addCallParam("tetrinet-server", 0, "host");
        digester.addCallMethod("tetrinet-server/language", "setLocale", 0);
        digester.addCallMethod("tetrinet-server/timeout", "setTimeout", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("tetrinet-server/max-channel", "setMaxChannel", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("tetrinet-server/max-players", "setMaxPlayers", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("tetrinet-server/max-connections", "setMaxConnections", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("tetrinet-server/op-password", "setOpPassword", 0);
        digester.addCallMethod("tetrinet-server/motd", "setMessageOfTheDay", 0);
        digester.addCallMethod("tetrinet-server/access-log", "setAccessLogPath", 1);
        digester.addCallParam("tetrinet-server/access-log", 0, "path");
        digester.addCallMethod("tetrinet-server/error-log", "setErrorLogPath", 1);
        digester.addCallParam("tetrinet-server/error-log", 0, "path");

        // default game settings
        digester.addObjectCreate("tetrinet-server/default-settings", "net.jetrix.config.Settings");
        digester.addSetNext("tetrinet-server/default-settings", "setDefaultSettings", "net.jetrix.config.Settings");

        // channel settings
        digester.addObjectCreate("*/channel/settings", "net.jetrix.config.Settings");
        digester.addSetNext("*/channel/settings", "setSettings", "net.jetrix.config.Settings");

        // any game settings
        digester.addCallMethod("*/starting-level", "setStartingLevel", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/lines-per-level", "setLinesPerLevel", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/level-increase", "setLevelIncrease", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/lines-per-special", "setLinesPerSpecial", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/special-added", "setSpecialAdded", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/special-capacity", "setSpecialCapacity", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/classic-rules", "setClassicRules", 0, new Class[] {Boolean.TYPE});
        digester.addCallMethod("*/average-levels", "setAverageLevels", 0, new Class[] {Boolean.TYPE});
        digester.addCallMethod("*/same-blocks", "setSameBlocks", 0, new Class[] {Boolean.TYPE});
        digester.addCallMethod("*/block-occurancy/leftl", "setLeftLOccurancy", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/block-occurancy/leftz", "setLeftZOccurancy", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/block-occurancy/square", "setSquareOccurancy", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/block-occurancy/rightl", "setRightLOccurancy", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/block-occurancy/rightz", "setRightZOccurancy", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/block-occurancy/halfcross", "setHalfCrossOccurancy", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/block-occurancy/line", "setLineOccurancy", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/block-occurancy", "normalizeBlockOccurancy", 0, (Class[]) null);
        digester.addCallMethod("*/special-occurancy/addline", "setAddLineOccurancy", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/special-occurancy/clearline", "setClearLineOccurancy", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/special-occurancy/nukefield", "setNukeFieldOccurancy", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/special-occurancy/randomclear", "setRandomClearOccurancy", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/special-occurancy/switchfield", "setSwitchFieldOccurancy", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/special-occurancy/clearspecial", "setClearSpecialOccurancy", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/special-occurancy/gravity", "setGravityOccurancy", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/special-occurancy/quakefield", "setQuakeFieldOccurancy", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/special-occurancy/blockbomb", "setBlockBombOccurancy", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/special-occurancy", "normalizeSpecialOccurancy", 0, (Class[]) null);

        // channel configuration
        digester.addObjectCreate("*/channel", "net.jetrix.config.ChannelConfig");
        digester.addSetNext("*/channel", "addChannel", "net.jetrix.config.ChannelConfig");
        digester.addCallMethod("*/channel", "setName", 1);
        digester.addCallParam("*/channel", 0, "name");
        digester.addCallMethod("*/channel/password", "setPassword", 0);
        digester.addCallMethod("*/channel/access-level", "setAccessLevel", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/channel/description", "setDescription", 0);
        digester.addCallMethod("*/channel/topic", "setTopic", 0);
        digester.addCallMethod("*/channel/max-players", "setMaxPlayers", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/channel/max-spectators", "setMaxSpectators", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/channel/winlist", "setWinlistId", 1);
        digester.addCallParam("*/channel/winlist", 0, "name");

        // filter configuration
        digester.addObjectCreate("*/filter", "net.jetrix.config.FilterConfig");
        digester.addSetNext("*/filter", "addFilter", "net.jetrix.config.FilterConfig");
        digester.addCallMethod("*/filter", "setName", 1);
        digester.addCallParam("*/filter", 0, "name");
        digester.addCallMethod("*/filter", "setClassname", 1);
        digester.addCallParam("*/filter", 0, "class");
        digester.addCallMethod("*/filter/param", "setParameter", 2);
        digester.addCallParam("*/filter/param", 0, "name");
        digester.addCallParam("*/filter/param", 1, "value");

        // filter definitions
        digester.addCallMethod("tetrinet-server/filter-definitions/alias", "addFilterAlias", 2);
        digester.addCallParam("tetrinet-server/filter-definitions/alias", 0, "name");
        digester.addCallParam("tetrinet-server/filter-definitions/alias", 1, "class");

        // winlists
        digester.addObjectCreate("tetrinet-server/winlists/winlist", "net.jetrix.config.WinlistConfig");
        digester.addSetNext("tetrinet-server/winlists/winlist", "addWinlist", "net.jetrix.config.WinlistConfig");
        digester.addCallMethod("tetrinet-server/winlists/winlist", "setName", 1);
        digester.addCallParam("tetrinet-server/winlists/winlist", 0, "name");
        digester.addCallMethod("tetrinet-server/winlists/winlist", "setClassname", 1);
        digester.addCallParam("tetrinet-server/winlists/winlist", 0, "class");
        digester.addCallMethod("tetrinet-server/winlists/winlist/param", "setParameter", 2);
        digester.addCallParam("tetrinet-server/winlists/winlist/param", 0, "name");
        digester.addCallParam("tetrinet-server/winlists/winlist/param", 1, "value");

        // command definitions
        digester.addObjectCreate("*/command", null, "class");
        digester.addSetNext("*/command", "addCommand", "net.jetrix.commands.Command");
        digester.addCallMethod("*/filter/access-level", "setAccessLevel", 0,  new Class[] {Integer.TYPE});
        
        // listeners
        digester.addObjectCreate("*/listener", null, "class");
        digester.addCallMethod("*/listener", "setPort", 1, new Class[] {Integer.TYPE});
        digester.addCallParam("*/listener", 0, "port");
        digester.addSetNext("*/listener", "addListener", "net.jetrix.Listener");

        // banlist
        digester.addCallMethod("tetrinet-server/ban/host", "addBannedHost", 0);
    }

}
