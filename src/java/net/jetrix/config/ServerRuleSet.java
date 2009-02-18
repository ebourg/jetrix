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

/**
 * RuleSet for processing the content of a server configuration file.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
class ServerRuleSet extends RuleSetBase
{
    public void addRuleInstances(Digester digester)
    {
        // server parameters
        digester.addCallMethod("tetrinet-server/name", "setName", 0);
        digester.addCallMethod("tetrinet-server", "setHost", 1);
        digester.addCallParam("tetrinet-server", 0, "host");
        digester.addCallMethod("tetrinet-server/language", "setLocale", 0);
        digester.addCallMethod("tetrinet-server/timeout", "setTimeout", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("tetrinet-server/max-channels", "setMaxChannels", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("tetrinet-server/max-players", "setMaxPlayers", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("tetrinet-server/max-connections", "setMaxConnections", 0, new Class[] {Integer.TYPE});
        digester.addCallMethod("tetrinet-server/op-password", "setOpPassword", 0);
        digester.addCallMethod("tetrinet-server/admin-password", "setAdminPassword", 0);
        digester.addCallMethod("tetrinet-server/access-log", "setAccessLogPath", 1);
        digester.addCallParam("tetrinet-server/access-log", 0, "path");
        digester.addCallMethod("tetrinet-server/error-log", "setErrorLogPath", 1);
        digester.addCallParam("tetrinet-server/error-log", 0, "path");
        digester.addCallMethod("tetrinet-server/channels", "setChannelsFile", 1);
        digester.addCallParam("tetrinet-server/channels", 0, "path");

        // command definitions
        digester.addObjectCreate("*/command", null, "class");
        digester.addSetNext("*/command", "addCommand", "net.jetrix.commands.Command");
        digester.addCallMethod("*/command", "setAccessLevel", 1, new Class[] {Integer.TYPE});
        digester.addCallParam("*/command", 0, "access-level");

        // listeners
        digester.addObjectCreate("*/listener", null, "class");
        digester.addSetProperties("*/listener");
        digester.addCallMethod("*/listener", "setAutoStart", 1, new Class[] {Boolean.TYPE});
        digester.addCallParam("*/listener", 0, "auto-start");
        digester.addSetNext("*/listener", "addListener", "net.jetrix.Listener");

        // services
        digester.addObjectCreate("*/service", null, "class");
        digester.addSetProperties("*/service");
        digester.addCallMethod("*/service", "setAutoStart", 1, new Class[] {Boolean.TYPE});
        digester.addCallParam("*/service", 0, "auto-start");
        digester.addSetProperty("*/service/param", "name", "value");
        digester.addSetNext("*/service", "addService", "net.jetrix.Service");

        // banlist
        digester.addCallMethod("tetrinet-server/ban/host", "addBannedHost", 0);

        // datasource
        digester.addObjectCreate("*/datasource", "net.jetrix.config.DataSourceConfig");
        digester.addSetNext("*/datasource", "addDataSource", "net.jetrix.config.DataSourceConfig");
        digester.addCallMethod("*/datasource", "setName", 1);
        digester.addCallParam("*/datasource", 0, "name");
        digester.addCallMethod("*/datasource/driver", "setDriver", 0);
        digester.addCallMethod("*/datasource/url", "setUrl", 0);
        digester.addCallMethod("*/datasource/username", "setUsername", 0);
        digester.addCallMethod("*/datasource/password", "setPassword", 0);
        digester.addCallMethod("*/datasource/min-idle", "setMinIdle", 0);
        digester.addCallMethod("*/datasource/max-active", "setMaxActive", 0);

        // mail session
        digester.addObjectCreate("*/mailserver", "net.jetrix.config.MailSessionConfig");
        digester.addSetNext("*/mailserver", "setMailSessionConfig", "net.jetrix.config.MailSessionConfig");
        digester.addCallMethod("*/mailserver", "setHostname", 1);
        digester.addCallParam("*/mailserver", 0, "host");
        digester.addCallMethod("*/mailserver", "setPort", 1, new Class[] {Integer.TYPE});
        digester.addCallParam("*/mailserver", 0, "port");
        digester.addCallMethod("*/mailserver", "setAuth", 1, new Class[] {Boolean.TYPE});
        digester.addCallParam("*/mailserver", 0, "auth");
        digester.addCallMethod("*/mailserver", "setUsername", 1);
        digester.addCallParam("*/mailserver", 0, "username");
        digester.addCallMethod("*/mailserver", "setPassword", 1);
        digester.addCallParam("*/mailserver", 0, "password");
        digester.addCallMethod("*/mailserver", "setDebug", 1, new Class[] {Boolean.TYPE});
        digester.addCallParam("*/mailserver", 0, "debug");
    }
}
