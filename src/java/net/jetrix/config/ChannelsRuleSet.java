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

package net.jetrix.config;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;
import org.apache.commons.digester.Rule;

/**
 * RuleSet for processing the content of a channel configuration file.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
class ChannelsRuleSet extends RuleSetBase
{
    public void addRuleInstances(Digester digester)
    {
        digester.addCallMethod("tetrinet-channels/motd", "setMessageOfTheDay", 0);

        // default game settings
        digester.addObjectCreate("tetrinet-channels/default-settings", "net.jetrix.config.Settings");
        digester.addSetNext("tetrinet-channels/default-settings", "setDefaultSettings", "net.jetrix.config.Settings");

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
        
        // block occurancy
        digester.addObjectCreate("*/block-occurancy", Occurancy.class.getName());
        digester.addCallMethod("*/block-occurancy", "normalize", 0, (Class[]) null);
        digester.addSetNext("*/block-occurancy", "setBlockOccurancy", Occurancy.class.getName());
        for (Block block : Block.values())
        {
            digester.addRule("*/block-occurancy/" + block.getCode(), new OccurancyRule<Block>(digester, block));
        }
        
        // special occurancy
        digester.addObjectCreate("*/special-occurancy", Occurancy.class.getName());
        digester.addCallMethod("*/special-occurancy", "normalize", 0, (Class[]) null);
        digester.addSetNext("*/special-occurancy", "setSpecialOccurancy", Occurancy.class.getName());
        for (Special special : Special.values())
        {
            digester.addRule("*/special-occurancy/" + special.getCode(), new OccurancyRule<Special>(digester, special));
        }
        
        digester.addCallMethod("*/sudden-death/time", "setSuddenDeathTime", 0, new Class[] { Integer.TYPE });
        digester.addCallMethod("*/sudden-death/message", "setSuddenDeathMessage", 0);
        digester.addCallMethod("*/sudden-death/delay", "setSuddenDeathDelay", 0, new Class[] { Integer.TYPE });
        digester.addCallMethod("*/sudden-death/lines-added", "setSuddenDeathLinesAdded", 0, new Class[] { Integer.TYPE });

        // channel configuration
        digester.addObjectCreate("*/channel", "net.jetrix.config.ChannelConfig");
        digester.addSetNext("*/channel", "addChannel", "net.jetrix.config.ChannelConfig");
        digester.addCallMethod("*/channel", "setName", 1);
        digester.addCallParam("*/channel", 0, "name");
        digester.addRule("*/channel/speed", new Rule(digester) {
            public void body(String text) throws Exception
            {
                getDigester().push(Speed.valueOf(text.toUpperCase()));
            }

            public void end() throws Exception
            {
                getDigester().pop();
            }
        });
        digester.addSetNext("*/channel/speed", "setSpeed");
        digester.addCallMethod("*/channel/password", "setPassword", 0);
        digester.addCallMethod("*/channel/access-level", "setAccessLevel", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("*/channel/idle", "setIdleAllowed", 0, new Class[] {Boolean.TYPE});
        digester.addCallMethod("*/channel/visible", "setVisible", 0, new Class[] {Boolean.TYPE});
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
        digester.addCallMethod("tetrinet-channels/filter-definitions/alias", "addFilterAlias", 2);
        digester.addCallParam("tetrinet-channels/filter-definitions/alias", 0, "name");
        digester.addCallParam("tetrinet-channels/filter-definitions/alias", 1, "class");

        // winlists
        digester.addObjectCreate("tetrinet-channels/winlists/winlist", "net.jetrix.config.WinlistConfig");
        digester.addSetNext("tetrinet-channels/winlists/winlist", "addWinlist", "net.jetrix.config.WinlistConfig");
        digester.addCallMethod("tetrinet-channels/winlists/winlist", "setName", 1);
        digester.addCallParam("tetrinet-channels/winlists/winlist", 0, "name");
        digester.addCallMethod("tetrinet-channels/winlists/winlist", "setClassname", 1);
        digester.addCallParam("tetrinet-channels/winlists/winlist", 0, "class");
        digester.addCallMethod("tetrinet-channels/winlists/winlist/param", "setParameter", 2);
        digester.addCallParam("tetrinet-channels/winlists/winlist/param", 0, "name");
        digester.addCallParam("tetrinet-channels/winlists/winlist/param", 1, "value");
    }

    /**
     * Custom rule to set the block and special occurancies.
     */
    private class OccurancyRule<T extends Enum> extends Rule
    {
        private T element;

        public OccurancyRule(Digester digester, T element)
        {
            super(digester);
            this.element = element;
        }

        public void body(String body) throws Exception
        {
            // get the settings on the stack
            Occurancy<T> occurancy = (Occurancy<T>) digester.peek();

            // get the value of the occurancy
            int value = Integer.parseInt(body);

            // set the value
            occurancy.setOccurancy(element, value);
        }
    }
}
