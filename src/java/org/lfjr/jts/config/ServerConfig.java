/**
 * Java TetriNET Server
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

package org.lfjr.jts.config;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.w3c.dom.*;

/**
 * Singleton reading and containing the server configuration.
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ServerConfig
{

    private static final ServerConfig instance = new ServerConfig();

    private String host;
    private int port;
    private int timeout;
    private int maxChannels;
    private int maxPlayers;
    private int maxConnexions;
    private String oppass;
    private String accesslogPath;
    private String errorlogPath;
    private String motd;

    private Settings defaultSettings;
    // private ArrayList bans;
    private ArrayList channels;

    public static final String VERSION = "0.0.8";

    /**
     * Constructor declaration
     * 
     * 
     */
    private ServerConfig()
    {
        channels = new ArrayList();

        // bans = new ArrayList();

        try
        {
            // reading configuration file
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            dbf.setNamespaceAware(false);

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File("config.xml"));

            // parsing general parameters
            Element root = doc.getDocumentElement();
            NodeList nodes = root.getChildNodes();
            Element defaultSettingsElement = null;
            Element channelsElement = null;
            Element bansElement = null;

            for (int i = 0; i < nodes.getLength(); i++)
            {
                Node n = nodes.item(i);

                if (n.getNodeType() == Node.ELEMENT_NODE)
                {
                    String tagname = ((Element) n).getTagName();
                    String value = null;

                    if (n.getFirstChild() != null)
                    {
                        value = n.getFirstChild().getNodeValue();
                    }

                    if ("timeout".equals(tagname))
                    {
                        timeout = Integer.parseInt(value);
                    }
                    else if ("max-channel".equals(tagname))
                    {
                        maxChannels = Integer.parseInt(value);
                    }
                    else if ("max-players".equals(tagname))
                    {
                        maxPlayers = Integer.parseInt(value);
                    }
                    else if ("max-connexions".equals(tagname))
                    {
                        maxConnexions = Integer.parseInt(value);
                    }
                    else if ("op-password".equals(tagname))
                    {
                        oppass = value;
                    }
                    else if ("access-log".equals(tagname))
                    {
                        accesslogPath = ((Element) n).getAttribute("path");
                    }
                    else if ("error-log".equals(tagname))
                    {
                        errorlogPath = ((Element) n).getAttribute("path");
                    }
                    else if ("motd".equals(tagname))
                    {
                        motd = value;
                    }
                    else if ("settings".equals(tagname))
                    {
                        defaultSettingsElement = (Element) n;
                    }
                    else if ("channels".equals(tagname))
                    {
                        channelsElement = (Element) n;
                    }
                    else if ("bans".equals(tagname))
                    {
                        bansElement = (Element) n;
                    }
                }
            }

            // parsing default settings
            defaultSettings = parseSettings(defaultSettingsElement);

            // parsing channels
            nodes = channelsElement.getChildNodes();

            for (int i = 0; i < nodes.getLength(); i++)
            {
                Node n = nodes.item(i);

                if (n.getNodeType() == Node.ELEMENT_NODE)
                {
                    if ("channel".equals(((Element) n).getTagName()))
                    {
                        channels.add(parseChannel((Element) n, defaultSettings));
                    }
                }
            }

            // parsing banlist


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Method declaration
     * 
     * 
     * @param e
     * 
     * @return
     * 
     */
    private Settings parseSettings(Element e)
    {
        return parseSettings(e, null);
    }


    /**
     * Method declaration
     * 
     * 
     * @param e
     * @param s
     * 
     * @return
     * 
     */
    private Settings parseSettings(Element e, Settings s)
    {
        Settings sc = new Settings(s);

        Element specialOccurancyElement = null;
        Element blockOccurancyElement = null;

        if (e == null)
        {
            return s;

        }

        NodeList nodes = e.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++)
        {
            Node n = nodes.item(i);

            if (n.getNodeType() == Node.ELEMENT_NODE)
            {
                String tagname = ((Element) n).getTagName();
                String value = null;

                if (n.getFirstChild() != null)
                {
                    value = n.getFirstChild().getNodeValue();
                }

                if ("starting-level".equals(tagname))
                {
                    sc.setStartingLevel(Integer.parseInt(value));
                }
                else if ("lines-per-level".equals(tagname))
                {
                    sc.setLinesPerLevel(Integer.parseInt(value));
                }
                else if ("level-increase".equals(tagname))
                {
                    sc.setLevelIncrease(Integer.parseInt(value));
                }
                else if ("lines-per-special".equals(tagname))
                {
                    sc.setLinesPerSpecial(Integer.parseInt(value));
                }
                else if ("special-added".equals(tagname))
                {
                    sc.setSpecialAdded(Integer.parseInt(value));
                }
                else if ("special-capacity".equals(tagname))
                {
                    sc.setSpecialCapacity(Integer.parseInt(value));
                }
                else if ("classic-rules".equals(tagname))
                {
                    sc.setClassicRules("yes".equalsIgnoreCase(value));
                }
                else if ("average-levels".equals(tagname))
                {
                    sc.setAverageLevels("yes".equalsIgnoreCase(value));
                }
                else if ("block-occurancy".equals(tagname))
                {
                    blockOccurancyElement = (Element) n;
                }
                else if ("special-occurancy".equals(tagname))
                {
                    specialOccurancyElement = (Element) n;
                }


            }
        }

        // parsing block occurancy parameters
        if (blockOccurancyElement != null)
        {
            nodes = blockOccurancyElement.getChildNodes();

            boolean cleared = false;

            for (int i = 0; i < nodes.getLength(); i++)
            {
                Node n = nodes.item(i);

                if (n.getNodeType() == Node.ELEMENT_NODE)
                {
                    if ("block".equals(((Element) n).getTagName()))
                    {
                        if (!cleared)
                        {
                            sc.clearBlockOccurancy();

                            cleared = true;
                        }

                        String type = ((Element) n).getAttributeNode("type").getValue();
                        int occurancy = Integer.parseInt(((Element) n).getAttributeNode("occurancy").getValue());

                        if ("leftl".equals(type))
                        {
                            sc.setBlockOccurancy(Settings.BLOCK_LEFTL, occurancy);
                        }
                        else if ("leftz".equals(type))
                        {
                            sc.setBlockOccurancy(Settings.BLOCK_LEFTZ, occurancy);
                        }
                        else if ("square".equals(type))
                        {
                            sc.setBlockOccurancy(Settings.BLOCK_SQUARE, occurancy);
                        }
                        else if ("rightl".equals(type))
                        {
                            sc.setBlockOccurancy(Settings.BLOCK_RIGHTL, occurancy);
                        }
                        else if ("rightz".equals(type))
                        {
                            sc.setBlockOccurancy(Settings.BLOCK_RIGHTZ, occurancy);
                        }
                        else if ("halfcross".equals(type))
                        {
                            sc.setBlockOccurancy(Settings.BLOCK_HALFCROSS, occurancy);
                        }
                        else if ("line".equals(type))
                        {
                            sc.setBlockOccurancy(Settings.BLOCK_LINE, occurancy);
                        }

                    }
                }
            }

            sc.normalizeBlockOccurancy();

        }


        // parsing special occurancy parameters
        if (specialOccurancyElement != null)
        {
            nodes = specialOccurancyElement.getChildNodes();

            boolean cleared = false;

            for (int i = 0; i < nodes.getLength(); i++)
            {
                Node n = nodes.item(i);

                if (n.getNodeType() == Node.ELEMENT_NODE)
                {
                    if ("special".equals(((Element) n).getTagName()))
                    {
                        if (!cleared)
                        {
                            sc.clearSpecialOccurancy();

                            cleared = true;
                        }

                        String type = ((Element) n).getAttributeNode("type").getValue();
                        int occurancy = Integer.parseInt(((Element) n).getAttributeNode("occurancy").getValue());

                        if ("addline".equals(type))
                        {
                            sc.setSpecialOccurancy(Settings.SPECIAL_ADDLINE, occurancy);
                        }
                        else if ("clearline".equals(type))
                        {
                            sc.setSpecialOccurancy(Settings.SPECIAL_CLEARLINE, occurancy);
                        }
                        else if ("nukefield".equals(type))
                        {
                            sc.setSpecialOccurancy(Settings.SPECIAL_NUKEFIELD, occurancy);
                        }
                        else if ("randomclear".equals(type))
                        {
                            sc.setSpecialOccurancy(Settings.SPECIAL_RANDOMCLEAR, occurancy);
                        }
                        else if ("switchfield".equals(type))
                        {
                            sc.setSpecialOccurancy(Settings.SPECIAL_SWITCHFIELD, occurancy);
                        }
                        else if ("clearspecial".equals(type))
                        {
                            sc.setSpecialOccurancy(Settings.SPECIAL_CLEARSPECIAL, occurancy);
                        }
                        else if ("gravity".equals(type))
                        {
                            sc.setSpecialOccurancy(Settings.SPECIAL_GRAVITY, occurancy);
                        }
                        else if ("quakefield".equals(type))
                        {
                            sc.setSpecialOccurancy(Settings.SPECIAL_QUAKEFIELD, occurancy);
                        }
                        else if ("blockbomb".equals(type))
                        {
                            sc.setSpecialOccurancy(Settings.SPECIAL_BLOCKBOMB, occurancy);
                        }

                    }
                }
            }

            sc.normalizeSpecialOccurancy();

        }

        return sc;

    }


    /**
     * Method declaration
     * 
     * 
     * @param e
     * @param defaultSettings
     * 
     * @return
     * 
     */
    private ChannelConfig parseChannel(Element e, Settings defaultSettings)
    {
        ChannelConfig cc = new ChannelConfig();

        NodeList nodes = e.getChildNodes();
        Element settingsElement = null;

        cc.setName(e.getAttributeNode("name").getValue());

        for (int i = 0; i < nodes.getLength(); i++)
        {
            Node n = nodes.item(i);

            if (n.getNodeType() == Node.ELEMENT_NODE)
            {
                String tagname = ((Element) n).getTagName();
                String value = null;

                if (n.getFirstChild() != null)
                {
                    value = n.getFirstChild().getNodeValue();
                }

                if ("description".equals(tagname))
                {
                    cc.setDescription(value);
                }
                else if ("max-players".equals(tagname))
                {
                    cc.setMaxPlayers(Integer.parseInt(value));
                }
                else if ("settings".equals(tagname))
                {
                    settingsElement = (Element) n;
                }
            }
        }

        Settings s = parseSettings(settingsElement, defaultSettings);

        cc.setSettings(s);

        return cc;
    }


    /**
     * Method declaration
     *
     *
     * @param e
     *
     */
    private void parseBanlist(Element e) {}


    /**
     * Method declaration
     * 
     * 
     * @return
     * 
     */
    public static ServerConfig getInstance()
    {
        return instance;
    }

    public int getPort()
    {
        return port;	
    }
    
    public Settings getDefaultSettings()
    {
        return defaultSettings;	
    }
    
    public Iterator getChannels()
    {
        return channels.iterator();
    	
    }

    /**
     * Method declaration
     * 
     */
    public void save() {}

}

