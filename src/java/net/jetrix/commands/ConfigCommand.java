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

package net.jetrix.commands;

import java.util.*;
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

    public String getUsage(Locale locale)
    {
        return "/config";
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command.config.description", locale);
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client)m.getSource();
        Locale locale = client.getUser().getLocale();

        Settings s = client.getChannel().getConfig().getSettings();
        String configLines[] = new String[18];

        configLines[0]  = "<darkBlue>" + "<b>" + Language.getText("command.config.blocks", locale) + "			" + Language.getText("command.config.specials", locale);
        configLines[1]  = "<darkBlue>" + Language.getText("command.config.blocks.leftl", locale)+"	: <purple>"+s.getBlockOccurancy(Settings.BLOCK_LEFTL)+"%</purple>		(<purple>A</purple>) "+Language.getText("command.config.specials.add_line", locale)+"	: "+"<purple>"+s.getSpecialOccurancy(Settings.SPECIAL_ADDLINE)+"%";
        configLines[2]  = "<darkBlue>" + Language.getText("command.config.blocks.rightl", locale)+"	: <purple>"+s.getBlockOccurancy(Settings.BLOCK_RIGHTL)+"%</purple>		(<purple>C</purple>) "+Language.getText("command.config.specials.clear_line", locale)+"	: "+"<purple>"+s.getSpecialOccurancy(Settings.SPECIAL_CLEARLINE)+"%";
        configLines[3]  = "<darkBlue>" + Language.getText("command.config.blocks.square", locale)+"	: <purple>"+s.getBlockOccurancy(Settings.BLOCK_SQUARE)+"%</purple>		(<purple>N</purple>) "+Language.getText("command.config.specials.nuke", locale)+"	: "+"<purple>"+s.getSpecialOccurancy(Settings.SPECIAL_NUKEFIELD)+"%";
        configLines[4]  = "<darkBlue>" + Language.getText("command.config.blocks.leftz", locale)+"	: <purple>"+s.getBlockOccurancy(Settings.BLOCK_LEFTZ)+"%</purple>		(<purple>R</purple>) "+Language.getText("command.config.specials.random", locale)+"	: "+"<purple>"+s.getSpecialOccurancy(Settings.SPECIAL_RANDOMCLEAR)+"%";
        configLines[5]  = "<darkBlue>" + Language.getText("command.config.blocks.rightz", locale)+"	: <purple>"+s.getBlockOccurancy(Settings.BLOCK_RIGHTZ)+"%</purple>		(<purple>S</purple>) "+Language.getText("command.config.specials.switch", locale)+"	: "+"<purple>"+s.getSpecialOccurancy(Settings.SPECIAL_SWITCHFIELD)+"%";
        configLines[6]  = "<darkBlue>" + Language.getText("command.config.blocks.cross", locale)+"	: <purple>"+s.getBlockOccurancy(Settings.BLOCK_HALFCROSS)+"%</purple>		(<purple>B</purple>) "+Language.getText("command.config.specials.clear_special", locale)+"	: "+"<purple>"+s.getSpecialOccurancy(Settings.SPECIAL_CLEARSPECIAL)+"%";
        configLines[7]  = "<darkBlue>" + Language.getText("command.config.blocks.line", locale)+"	: <purple>"+s.getBlockOccurancy(Settings.BLOCK_LINE)+"%</purple>		(<purple>G</purple>) "+Language.getText("command.config.specials.gravity", locale)+"	: "+"<purple>"+s.getSpecialOccurancy(Settings.SPECIAL_GRAVITY)+"%";
        configLines[8]  = "<darkBlue>" + "			(<purple>Q</purple>) Quake Field	: <purple>"+s.getSpecialOccurancy(Settings.SPECIAL_QUAKEFIELD)+"%";
        configLines[9]  = "<darkBlue>" + "<b>" + Language.getText("command.config.rules", locale) + "<b>" + "			(<purple>O<purple>) "+Language.getText("command.config.specials.blockbomb", locale)+"	: <purple>"+s.getSpecialOccurancy(Settings.SPECIAL_BLOCKBOMB)+"%";
        configLines[10] = "<darkBlue>" + Language.getText("command.config.rules.starting_level", locale) + "	: " + "<blue>" + s.getStartingLevel();
        configLines[11] = "<darkBlue>" + Language.getText("command.config.rules.lines_per_level", locale) + "	: " + "<blue>" + s.getLinesPerLevel();
        configLines[12] = "<darkBlue>" + Language.getText("command.config.rules.level_increase", locale) + "	: " + "<blue>" + s.getLevelIncrease();
        configLines[13] = "<darkBlue>" + Language.getText("command.config.rules.lines_per_special", locale) + "	: " + "<blue>" + s.getLinesPerSpecial();
        configLines[14] = "<darkBlue>" + Language.getText("command.config.rules.special_added", locale) + "	: " + "<blue>" + s.getSpecialAdded();
        configLines[15] = "<darkBlue>" + Language.getText("command.config.rules.special_capacity", locale) + "	: " + "<blue>" + s.getSpecialCapacity();
        configLines[16] = "<darkBlue>" + Language.getText("command.config.rules.classic_rules", locale) + "	: " + "<blue>" + (s.getClassicRules()?Language.getText("common.yes", locale):Language.getText("common.no", locale));
        configLines[17] = "<darkBlue>" + Language.getText("command.config.rules.average_levels", locale) + "	: " + "<blue>" + (s.getAverageLevels()?Language.getText("common.yes", locale):Language.getText("common.no", locale));
        //configLines[18] = "";
        //configLines[19] = "<darkBlue>" + "<b>" + "Filters" + "<b>" + "  (type <red>/filter</red> for details)";
        //configLines[20] = "<darkBlue>" + "start, flood, amplifier";

        for (int i = 0; i < configLines.length; i++)
        {
            Message configMessage = new PlineMessage(configLines[i]);
            client.sendMessage(configMessage);
        }
    }
}
