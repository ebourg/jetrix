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

package net.jetrix.config;

import java.io.*;
import java.net.*;
import java.util.*;

import net.jetrix.filter.*;
import net.jetrix.commands.*;
import org.apache.commons.digester.*;

/**
 * Server configuration. This objet reads and retains server parameters,
 * channel definitions and the ban list.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ServerConfig
{
    private InetAddress host;
    private int port;
    private int timeout;
    private int maxChannels;
    private int maxPlayers;
    private int maxConnexions;
    private String oppass;
    private String accesslogPath;
    private String errorlogPath;
    private String motd;
    private String name;
    private Locale locale;

    // private List bans;
    private List channels;
    private List globalFilters;
    private boolean running;

    public static final String VERSION = "@version@";
    public static final int DEFAULT_PORT = 31457;

    /**
     * Constructor declaration
     */
    public ServerConfig()
    {
        channels = new ArrayList();
        globalFilters = new ArrayList();
        // bans = new ArrayList();
        port = DEFAULT_PORT;
    }

    /**
     * Loads the default configuration file <tt>config.xml</tt>.
     */
    public void load()
    {
        load("config.xml");
    }

    public void load(String filename)
    {
        try
        {
            Digester digester = new Digester();
            // register the JetriX configuration file DTD
            URL url = ServerConfig.class.getClassLoader().getResource("tetrinet-server.dtd");
            digester.register("-//LFJR//Jetrix TetriNET Server//EN", url.toString());
            digester.setValidating(true);
            //digester.setDebug(2);
            digester.push(this);

            // server parameters
            digester.addCallMethod("tetrinet-server", "setHost", 1);
            digester.addCallParam("tetrinet-server", 0, "host");
            digester.addCallMethod("tetrinet-server", "setPort", 1, new Class[] {Integer.TYPE});
            digester.addCallParam("tetrinet-server", 0, "port");
            digester.addCallMethod("tetrinet-server/language", "setLocale", 0);
            digester.addCallMethod("tetrinet-server/timeout", "setTimeout", 0, new Class[] {Integer.TYPE});
            digester.addCallMethod("tetrinet-server/max-channel", "setMaxChannel", 0, new Class[] {Integer.TYPE});
            digester.addCallMethod("tetrinet-server/max-players", "setMaxPlayers", 0, new Class[] {Integer.TYPE});
            digester.addCallMethod("tetrinet-server/max-connexions", "setMaxConnexions", 0, new Class[] {Integer.TYPE});
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
            digester.addCallMethod("*/block-occurancy/leftl", "setLeftLOccurancy", 0, new Class[] {Integer.TYPE});
            digester.addCallMethod("*/block-occurancy/leftz", "setLeftZOccurancy", 0, new Class[] {Integer.TYPE});
            digester.addCallMethod("*/block-occurancy/square", "setSquareOccurancy", 0, new Class[] {Integer.TYPE});
            digester.addCallMethod("*/block-occurancy/rightl", "setRightLOccurancy", 0, new Class[] {Integer.TYPE});
            digester.addCallMethod("*/block-occurancy/rightz", "setRightZOccurancy", 0, new Class[] {Integer.TYPE});
            digester.addCallMethod("*/block-occurancy/halfcross", "setHalfCrossOccurancy", 0, new Class[] {Integer.TYPE});
            digester.addCallMethod("*/block-occurancy/line", "setLineOccurancy", 0, new Class[] {Integer.TYPE});
            digester.addCallMethod("*/block-occurancy", "normalizeBlockOccurancy", 0, (Class[])null);
            digester.addCallMethod("*/special-occurancy/addline", "setAddLineOccurancy", 0, new Class[] {Integer.TYPE});
            digester.addCallMethod("*/special-occurancy/clearline", "setClearLineOccurancy", 0, new Class[] {Integer.TYPE});
            digester.addCallMethod("*/special-occurancy/nukefield", "setNukeFieldOccurancy", 0, new Class[] {Integer.TYPE});
            digester.addCallMethod("*/special-occurancy/randomclear", "setRandomClearOccurancy", 0, new Class[] {Integer.TYPE});
            digester.addCallMethod("*/special-occurancy/switchfield", "setSwitchFieldOccurancy", 0, new Class[] {Integer.TYPE});
            digester.addCallMethod("*/special-occurancy/clearspecial", "setClearSpecialOccurancy", 0, new Class[] {Integer.TYPE});
            digester.addCallMethod("*/special-occurancy/gravity", "setGravityOccurancy", 0, new Class[] {Integer.TYPE});
            digester.addCallMethod("*/special-occurancy/quakefield", "setQuakeFieldOccurancy", 0, new Class[] {Integer.TYPE});
            digester.addCallMethod("*/special-occurancy/blockbomb", "setBlockBombOccurancy", 0, new Class[] {Integer.TYPE});
            digester.addCallMethod("*/special-occurancy", "normalizeSpecialOccurancy", 0, (Class[])null);

            // channel configuration
            digester.addObjectCreate("*/channel", "net.jetrix.config.ChannelConfig");
            digester.addSetNext("*/channel", "addChannel", "net.jetrix.config.ChannelConfig");
            digester.addCallMethod("*/channel", "setName", 1);
            digester.addCallParam("*/channel", 0, "name");
            digester.addCallMethod("*/channel/password", "setPassword", 0);
            digester.addCallMethod("*/channel/access-level", "setAccessLevel", 0, new Class[] {Integer.TYPE});
            digester.addCallMethod("*/channel/description", "setDescription", 0);
            digester.addCallMethod("*/channel/max-players", "setMaxPlayers", 0, new Class[] {Integer.TYPE});

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

            // command definitions
            digester.addObjectCreate("*/command", null, "class");
            digester.addSetNext("*/command", "addCommand", "net.jetrix.commands.Command");
            digester.addCallMethod("*/filter/access-level", "setAccessLevel", 0,  new Class[] {Integer.TYPE});

            digester.parse(new File(filename));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public InetAddress getHost()
    {
        return host;
    }

    public void setHost(InetAddress host)
    {
        this.host = host;
    }

    public void setHost(String host)
    {
        // a value of "[ALL]" stands for any IP
        if (!"[ALL]".equals(host))
        {
            try {
                this.host = InetAddress.getByName(host);
            }
            catch(UnknownHostException e) { e.printStackTrace(); }
        }
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    public void setLocale(String language)
    {
        this.locale = new Locale(language);
    }

    public int getTimeout()
    {
        return timeout;
    }

    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    public int getMaxChannels()
    {
        return maxChannels;
    }

    public void setMaxChannels(int maxChannels)
    {
        this.maxChannels = maxChannels;
    }

    public int getMaxPlayers()
    {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers)
    {
        this.maxPlayers = maxPlayers;
    }

    public int getMaxConnexions()
    {
        return maxConnexions;
    }

    public void setMaxConnexions(int maxConnexions)
    {
        this.maxConnexions = maxConnexions;
    }

    public String getOpPassword()
    {
        return oppass;
    }

    public void setOpPassword(String oppass)
    {
        this.oppass = oppass;
    }

    public String getAccessLogPath()
    {
        return accesslogPath;
    }

    public void setAccessLogPath(String accesslogPath)
    {
        this.accesslogPath = accesslogPath;
    }

    public String getErrorLogPath()
    {
        return errorlogPath;
    }

    public void setErrorLogPath(String errorlogPath)
    {
        this.errorlogPath = errorlogPath;
    }

    public String getMessageOfTheDay()
    {
        return motd;
    }

    public void setMessageOfTheDay(String motd)
    {
        this.motd = motd;
    }

    public boolean isRunning()
    {
        return running;
    }

    public void setRunning(boolean running)
    {
        this.running = running;
    }

    public void setDefaultSettings(Settings defaultSettings)
    {
        Settings.setDefaultSettings(defaultSettings);
    }

    public Iterator getChannels()
    {
        return channels.iterator();
    }

    public void addChannel(ChannelConfig cconf)
    {
        channels.add(cconf);
    }

    public Iterator getGlobalFilters()
    {
        return globalFilters.iterator();
    }

    public void addFilter(FilterConfig fconf)
    {
        globalFilters.add(fconf);
    }

    public void addFilterAlias(String name, String classname)
    {
        FilterManager.getInstance().addFilterAlias(name, classname);
    }

    public void addCommand(Command command)
    {
        CommandManager.getInstance().addCommand(command);
    }

}
