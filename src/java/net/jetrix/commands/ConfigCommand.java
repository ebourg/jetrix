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

package net.jetrix.commands;

import net.jetrix.*;
import net.jetrix.config.*;
import net.jetrix.messages.*;

/**
 * Display the channel settings.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ConfigCommand implements Command
{
    private int accessLevel = 0;

    public String[] getAliases()
    {
        return (new String[] { "config", "conf", "settings" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage()
    {
        return "/config";
    }

    public String getDescription()
    {
        return "Display the channel settings.";
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client)m.getSource();

        Settings s = client.getChannel().getConfig().getSettings();
        String configLines[] = new String[18];

        configLines[0]  = Color.darkBlue + Color.bold + "Blocks			Specials";
        configLines[1]  = Color.darkBlue + "Left L	: "+Color.purple+s.getBlockOccurancy(Settings.BLOCK_LEFTL)+"%"+Color.purple+"		("+Color.purple+"A"+Color.purple+") Add Line	: "+Color.purple+s.getSpecialOccurancy(Settings.SPECIAL_ADDLINE)+"%";
        configLines[2]  = Color.darkBlue + "Right L	: "+Color.purple+s.getBlockOccurancy(Settings.BLOCK_RIGHTL)+"%"+Color.purple+"		("+Color.purple+"C"+Color.purple+") Clear Line	: "+Color.purple+s.getSpecialOccurancy(Settings.SPECIAL_CLEARLINE)+"%";
        configLines[3]  = Color.darkBlue + "Square	: "+Color.purple+s.getBlockOccurancy(Settings.BLOCK_SQUARE)+"%"+Color.purple+"		("+Color.purple+"N"+Color.purple+") Nuke Field	: "+Color.purple+s.getSpecialOccurancy(Settings.SPECIAL_NUKEFIELD)+"%";
        configLines[4]  = Color.darkBlue + "Left Z	: "+Color.purple+s.getBlockOccurancy(Settings.BLOCK_LEFTZ)+"%"+Color.purple+"		("+Color.purple+"R"+Color.purple+") Random Clear	: "+Color.purple+s.getSpecialOccurancy(Settings.SPECIAL_RANDOMCLEAR)+"%";
        configLines[5]  = Color.darkBlue + "Right Z	: "+Color.purple+s.getBlockOccurancy(Settings.BLOCK_RIGHTZ)+"%"+Color.purple+"		("+Color.purple+"S"+Color.purple+") Switch Field	: "+Color.purple+s.getSpecialOccurancy(Settings.SPECIAL_SWITCHFIELD)+"%";
        configLines[6]  = Color.darkBlue + "Cross	: "+Color.purple+s.getBlockOccurancy(Settings.BLOCK_HALFCROSS)+"%"+Color.purple+"		("+Color.purple+"B"+Color.purple+") Clear Special	: "+Color.purple+s.getSpecialOccurancy(Settings.SPECIAL_CLEARSPECIAL)+"%";
        configLines[7]  = Color.darkBlue + "Line	: "+Color.purple+s.getBlockOccurancy(Settings.BLOCK_LINE)+"%"+Color.purple+"		("+Color.purple+"G"+Color.purple+") Gravity	: "+Color.purple+s.getSpecialOccurancy(Settings.SPECIAL_GRAVITY)+"%";
        configLines[8]  = Color.darkBlue + "			("+Color.purple+"Q"+Color.purple+") Quake Field	: "+Color.purple+s.getSpecialOccurancy(Settings.SPECIAL_QUAKEFIELD)+"%";
        configLines[9]  = Color.darkBlue + Color.bold + "Rules" + Color.bold + "			("+Color.purple+"O"+Color.purple+") Blockbomb	: "+Color.purple+s.getSpecialOccurancy(Settings.SPECIAL_BLOCKBOMB)+"%";
        configLines[10] = Color.darkBlue + "Starting Level	: " + Color.blue + s.getStartingLevel();
        configLines[11] = Color.darkBlue + "Lines per Level	: " + Color.blue + s.getLinesPerLevel();
        configLines[12] = Color.darkBlue + "Level Increase	: " + Color.blue + s.getLevelIncrease();
        configLines[13] = Color.darkBlue + "Lines per Special	: " + Color.blue + s.getLinesPerSpecial();
        configLines[14] = Color.darkBlue + "Special Added	: " + Color.blue + s.getSpecialAdded();
        configLines[15] = Color.darkBlue + "Special Capacity	: " + Color.blue + s.getSpecialCapacity();
        configLines[16] = Color.darkBlue + "Classic Rules	: " + Color.blue + (s.getClassicRules()?"yes":"no");
        configLines[17] = Color.darkBlue + "Average Levels	: " + Color.blue + (s.getAverageLevels()?"yes":"no");
        //configLines[18] = "";
        //configLines[19] = Color.darkBlue + Color.bold + "Filters" + Color.bold + "  (type " + Color.red + "/filter" + Color.red + " for details)";
        //configLines[20] = Color.darkBlue + "start, flood, amplifier";

        for (int i = 0; i < configLines.length; i++)
        {
            Message configMessage = new PlineMessage(configLines[i]);
            client.sendMessage(configMessage);
        }
    }
}
