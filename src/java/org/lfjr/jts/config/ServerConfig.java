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

package org.lfjr.jts.config;

import java.io.*;
import java.net.*;
import java.util.*;

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

    // private ArrayList bans;
    private ArrayList channels;
    private boolean running;

    public static final String VERSION = "@version@";
    public static final int DEFAULT_PORT = 31457;

    /**
     * Constructor declaration
     */
    public ServerConfig()
    {
    	channels = new ArrayList();
    	// bans = new ArrayList();
    }

    public void load()
    {
        load("config.xml");
    }

    public void load(String filename)
    {
        try
        {
            Digester digester = new Digester();
            //digester.setDebug(1);
            digester.push(this);

            // server parameters
            digester.addCallMethod("tetrinet-server", "setHost", 1);
            digester.addCallParam("tetrinet-server", 0, "host");
            digester.addCallMethod("tetrinet-server", "setPort", 1);
            digester.addCallParam("tetrinet-server", 0, "port");
            digester.addCallMethod("tetrinet-server/timeout", "setTimeout", 0, new Class[] {Integer.class});
            digester.addCallMethod("tetrinet-server/max-channel", "setMaxChannel", 0, new Class[] {Integer.class});
            digester.addCallMethod("tetrinet-server/max-players", "setMaxPlayers", 0, new Class[] {Integer.class});
            digester.addCallMethod("tetrinet-server/max-connexions", "setMaxConnexions", 0, new Class[] {Integer.class});
            digester.addCallMethod("tetrinet-server/op-password", "setOpPassword", 0);
            digester.addCallMethod("tetrinet-server/motd", "setMessageOfTheDay", 0);
            digester.addCallMethod("tetrinet-server", "setAccessLogPath", 1);
            digester.addCallParam("tetrinet-server/access-log", 0, "path");
            digester.addCallMethod("tetrinet-server", "setErrorLogPath", 1);
            digester.addCallParam("tetrinet-server/error-log", 0, "path");

            // default game settings
            digester.addObjectCreate("tetrinet-server/default-settings", "org.lfjr.jts.config.Settings");
            digester.addSetNext("tetrinet-server/default-settings", "setDefaultSettings", "org.lfjr.jts.config.Settings");

            // channel settings
            digester.addObjectCreate("*/channel/settings", "org.lfjr.jts.config.Settings");
            digester.addSetNext("*/channel/settings", "setSettings", "org.lfjr.jts.config.Settings");

            // any game settings
            digester.addCallMethod("*/starting-level", "setStartingLevel", 0, new Class[] {Integer.class});
            digester.addCallMethod("*/lines-per-level", "setLinesPerLevel", 0, new Class[] {Integer.class});
            digester.addCallMethod("*/level-increase", "setLevelIncrease", 0, new Class[] {Integer.class});
            digester.addCallMethod("*/lines-per-special", "setLinesPerSpecial", 0, new Class[] {Integer.class});
            digester.addCallMethod("*/special-added", "setSpecialAdded", 0, new Class[] {Integer.class});
            digester.addCallMethod("*/special-capacity", "setSpecialCapacity", 0, new Class[] {Integer.class});
            digester.addCallMethod("*/classic-rules", "setClassicRules", 0, new Class[] {Boolean.class});
            digester.addCallMethod("*/average-levels", "setAverageLevels", 0, new Class[] {Boolean.class});
            digester.addCallMethod("*/block-occurancy/leftl", "setLeftLOccurancy", 0, new Class[] {Integer.class});
            digester.addCallMethod("*/block-occurancy/leftz", "setLeftZOccurancy", 0, new Class[] {Integer.class});
            digester.addCallMethod("*/block-occurancy/square", "setSquareOccurancy", 0, new Class[] {Integer.class});
            digester.addCallMethod("*/block-occurancy/rightl", "setRightLOccurancy", 0, new Class[] {Integer.class});
            digester.addCallMethod("*/block-occurancy/rightz", "setRightZOccurancy", 0, new Class[] {Integer.class});
            digester.addCallMethod("*/block-occurancy/halfcross", "setHalfCrossOccurancy", 0, new Class[] {Integer.class});
            digester.addCallMethod("*/block-occurancy/line", "setLineOccurancy", 0, new Class[] {Integer.class});
            //digester.addCallMethod("*/block-occurancy", "normalizeBlockOccurancy", 1, new Class[] {} );
            digester.addCallMethod("*/special-occurancy/addline", "setAddLineOccurancy", 0, new Class[] {Integer.class});
            digester.addCallMethod("*/special-occurancy/clearline", "setClearLineOccurancy", 0, new Class[] {Integer.class});
            digester.addCallMethod("*/special-occurancy/nukefield", "setNukeFieldOccurancy", 0, new Class[] {Integer.class});
            digester.addCallMethod("*/special-occurancy/randomclear", "setRandomClearOccurancy", 0, new Class[] {Integer.class});
            digester.addCallMethod("*/special-occurancy/switchfield", "setSwitchFieldOccurancy", 0, new Class[] {Integer.class});
            digester.addCallMethod("*/special-occurancy/clearspecial", "setClearSpecialOccurancy", 0, new Class[] {Integer.class});
            digester.addCallMethod("*/special-occurancy/gravity", "setGravityOccurancy", 0, new Class[] {Integer.class});
            digester.addCallMethod("*/special-occurancy/quakefield", "setQuakeFieldOccurancy", 0, new Class[] {Integer.class});
            digester.addCallMethod("*/special-occurancy/blockbomb", "setBlockBombOccurancy", 0, new Class[] {Integer.class});
            //digester.addCallMethod("*/special-occurancy", "normalizeSpecialOccurancy", 0, new Class[] {});

            // channel configuration
            digester.addObjectCreate("*/channel", "org.lfjr.jts.config.ChannelConfig");
            digester.addSetNext("*/channel", "addChannel", "org.lfjr.jts.config.ChannelConfig");
            digester.addCallMethod("*/channel", "setName", 1);
            digester.addCallParam("*/channel", 0, "name");
            digester.addCallMethod("*/channel/description", "setDescription", 0);
            digester.addCallMethod("*/channel/max-players", "setMaxPlayers", 0, new Class[] {Integer.class});

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

    public void setPort(Integer port)
    {
        try { this.port = port.intValue(); }
        catch (Exception e) { this.port = DEFAULT_PORT; }
    }

    public int getTimeout()
    {
        return timeout;
    }

    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    public void setTimeout(Integer timeout)
    {
        this.timeout = timeout.intValue();
    }

    public int getMaxChannels()
    {
        return maxChannels;
    }

    public void setMaxChannels(int maxChannels)
    {
        this.maxChannels = maxChannels;
    }

    public void setMaxChannels(Integer maxChannels)
    {
        this.maxChannels = maxChannels.intValue();
    }

    public int getMaxPlayers()
    {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers)
    {
        this.maxPlayers = maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers)
    {
        this.maxPlayers = maxPlayers.intValue();
    }

    public int getMaxConnexions()
    {
        return maxConnexions;
    }

    public void setMaxConnexions(int maxConnexions)
    {
        this.maxConnexions = maxConnexions;
    }

    public void setMaxConnexions(Integer maxConnexions)
    {
        this.maxConnexions = maxConnexions.intValue();
    }

    public String getOpPassword()
    {
        return oppass;
    }

    public void setOpPassword(String motd)
    {
        this.oppass = oppass;
    }

    public String getAccessLogPath()
    {
        return accesslogPath;
    }

    public void setAccessLogPath(String motd)
    {
        this.accesslogPath = accesslogPath;
    }

    public String getErrorLogPath()
    {
        return motd;
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
}
