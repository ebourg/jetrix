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

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.commons.digester.*;
import org.xml.sax.*;

import net.jetrix.*;
import net.jetrix.winlist.*;
import net.jetrix.filter.*;
import net.jetrix.commands.*;

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
    private int timeout;
    private int maxChannels;
    private int maxPlayers;
    private int maxConnections;
    private String oppass;
    private String accesslogPath;
    private String errorlogPath;
    private String motd;
    private Locale locale;

    // private List bans;
    private List channels;
    private List globalFilters;
    private List listeners;
    private boolean running;

    public static final String VERSION = "@version@";

    /**
     * Constructor declaration
     */
    public ServerConfig()
    {
        channels = new ArrayList();
        globalFilters = new ArrayList();
        listeners = new ArrayList();
        // bans = new ArrayList();
    }

    /**
     * Load the default configuration file <tt>config.xml</tt>.
     */
    public void load()
    {
        load("config.xml");
    }

    /**
     * Load the content of the specified configuration file in this object.
     */
    public void load(String filename)
    {
        try
        {
            Digester digester = new Digester();
            // register the JetriX configuration file DTD
            URL url = ServerConfig.class.getClassLoader().getResource("tetrinet-server.dtd");
            digester.register("-//LFJR//Jetrix TetriNET Server//EN", url.toString());
            digester.setValidating(true);
            digester.addRuleSet(new ConfigRuleSet());
            digester.push(this);
            digester.parse(new InputSource(new InputStreamReader(new FileInputStream(filename), "ISO-8859-1")));
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
            try
            {
                this.host = InetAddress.getByName(host);
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
            }
        }
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

    public int getMaxConnections()
    {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections)
    {
        this.maxConnections = maxConnections;
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

    public Settings getDefaultSettings()
    {
        return Settings.getDefaultSettings();
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

    public void addWinlist(WinlistConfig config)
    {
        WinlistManager.getInstance().addWinlist(config);
    }

    public void addCommand(Command command)
    {
        CommandManager.getInstance().addCommand(command);
    }

    public void addListener(Listener listener)
    {
        listeners.add(listener);
    }

    public Iterator getListeners()
    {
        return listeners.iterator();
    }

    public void addBannedHost(String host)
    {
        Banlist.getInstance().ban(host);
    }

}
