package org.lfjr.jts.config;


public class ChannelConfig
{
    protected Settings settings;    
    protected String name;
    protected String description;
    protected int maxPlayers;
    
    public void setSettings(Settings settings) { this.settings = settings; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }
    
    public Settings setSettings() { return settings ; }
    public String setName() { return name ; }
    public String setDescription() { return description ; }
    public int setMaxPlayers() { return maxPlayers; }

}