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

package org.lfjr.jts.commands;

import org.lfjr.jts.*;
import org.lfjr.jts.config.*;

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

    public void execute(Message m)
    {
        TetriNETClient client = (TetriNETClient)m.getSource();

        Settings s = client.getChannel().getConfig().getSettings();
        String configLines[] = new String[18];

        configLines[0]  = ChatColors.darkBlue + ChatColors.bold + "Blocks			Specials";
        configLines[1]  = ChatColors.darkBlue + "Left L	: "+ChatColors.purple+s.getBlockOccurancy(Settings.BLOCK_LEFTL)+"%"+ChatColors.purple+"		("+ChatColors.purple+"A"+ChatColors.purple+") Add Line	: "+ChatColors.purple+s.getSpecialOccurancy(Settings.SPECIAL_ADDLINE)+"%";
        configLines[2]  = ChatColors.darkBlue + "Right L	: "+ChatColors.purple+s.getBlockOccurancy(Settings.BLOCK_RIGHTL)+"%"+ChatColors.purple+"		("+ChatColors.purple+"C"+ChatColors.purple+") Clear Line	: "+ChatColors.purple+s.getSpecialOccurancy(Settings.SPECIAL_CLEARLINE)+"%";
        configLines[3]  = ChatColors.darkBlue + "Square	: "+ChatColors.purple+s.getBlockOccurancy(Settings.BLOCK_SQUARE)+"%"+ChatColors.purple+"		("+ChatColors.purple+"N"+ChatColors.purple+") Nuke Field	: "+ChatColors.purple+s.getSpecialOccurancy(Settings.SPECIAL_NUKEFIELD)+"%";
        configLines[4]  = ChatColors.darkBlue + "Left Z	: "+ChatColors.purple+s.getBlockOccurancy(Settings.BLOCK_LEFTZ)+"%"+ChatColors.purple+"		("+ChatColors.purple+"R"+ChatColors.purple+") Random Clear	: "+ChatColors.purple+s.getSpecialOccurancy(Settings.SPECIAL_RANDOMCLEAR)+"%";
        configLines[5]  = ChatColors.darkBlue + "Right Z	: "+ChatColors.purple+s.getBlockOccurancy(Settings.BLOCK_RIGHTZ)+"%"+ChatColors.purple+"		("+ChatColors.purple+"N"+ChatColors.purple+") Switch Field	: "+ChatColors.purple+s.getSpecialOccurancy(Settings.SPECIAL_SWITCHFIELD)+"%";
        configLines[6]  = ChatColors.darkBlue + "Cross	: "+ChatColors.purple+s.getBlockOccurancy(Settings.BLOCK_HALFCROSS)+"%"+ChatColors.purple+"		("+ChatColors.purple+"B"+ChatColors.purple+") Clear Special	: "+ChatColors.purple+s.getSpecialOccurancy(Settings.SPECIAL_CLEARSPECIAL)+"%";
        configLines[7]  = ChatColors.darkBlue + "Line	: "+ChatColors.purple+s.getBlockOccurancy(Settings.BLOCK_LINE)+"%"+ChatColors.purple+"		("+ChatColors.purple+"G"+ChatColors.purple+") Gravity	: "+ChatColors.purple+s.getSpecialOccurancy(Settings.SPECIAL_GRAVITY)+"%";
        configLines[8]  = ChatColors.darkBlue + "			("+ChatColors.purple+"Q"+ChatColors.purple+") Quake Field	: "+ChatColors.purple+s.getSpecialOccurancy(Settings.SPECIAL_QUAKEFIELD)+"%";
        configLines[9]  = ChatColors.darkBlue + ChatColors.bold + "Rules" + ChatColors.bold + "			("+ChatColors.purple+"O"+ChatColors.purple+") Blockbomb	: "+ChatColors.purple+s.getSpecialOccurancy(Settings.SPECIAL_BLOCKBOMB)+"%";
        configLines[10] = ChatColors.darkBlue + "Starting Level	: " + ChatColors.blue + s.getStartingLevel();
        configLines[11] = ChatColors.darkBlue + "Lines per Level	: " + ChatColors.blue + s.getLinesPerLevel();
        configLines[12] = ChatColors.darkBlue + "Level Increase	: " + ChatColors.blue + s.getLevelIncrease();
        configLines[13] = ChatColors.darkBlue + "Lines per Special	: " + ChatColors.blue + s.getLinesPerSpecial();
        configLines[14] = ChatColors.darkBlue + "Special Added	: " + ChatColors.blue + s.getSpecialAdded();
        configLines[15] = ChatColors.darkBlue + "Special Capacity	: " + ChatColors.blue + s.getSpecialCapacity();
        configLines[16] = ChatColors.darkBlue + "Classic Rules	: " + ChatColors.blue + (s.getClassicRules()?"yes":"no");
        configLines[17] = ChatColors.darkBlue + "Average Levels	: " + ChatColors.blue + (s.getAverageLevels()?"yes":"no");
        //configLines[18] = "";
        //configLines[19] = ChatColors.darkBlue + ChatColors.bold + "Filters" + ChatColors.bold + "  (type " + ChatColors.red + "/filter" + ChatColors.red + " for details)";
        //configLines[20] = ChatColors.darkBlue + "start, flood, amplifier";

        for (int i = 0; i < configLines.length; i++)
        {
            Message configMessage = new Message(Message.MSG_PLINE);
            configMessage.setParameters(new Object[] { new Integer(0), configLines[i] });
            client.sendMessage(configMessage);
        }
    }
}
