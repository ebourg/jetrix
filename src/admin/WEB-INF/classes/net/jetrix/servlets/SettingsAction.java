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

package net.jetrix.servlets;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import net.jetrix.*;
import net.jetrix.config.*;

/**
 * Action Servlet handling the server and channels settings changes.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class SettingsAction extends HttpServlet
{

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Collection errors = new ArrayList();
        Settings settings = new Settings();

        // validation
        settings.setSpecialOccurancy(Settings.SPECIAL_ADDLINE, Integer.parseInt(request.getParameter("addLine")));
        settings.setSpecialOccurancy(Settings.SPECIAL_CLEARLINE, Integer.parseInt(request.getParameter("clearLine")));
        settings.setSpecialOccurancy(Settings.SPECIAL_NUKEFIELD, Integer.parseInt(request.getParameter("nukeField")));
        settings.setSpecialOccurancy(Settings.SPECIAL_RANDOMCLEAR, Integer.parseInt(request.getParameter("randomClear")));
        settings.setSpecialOccurancy(Settings.SPECIAL_SWITCHFIELD, Integer.parseInt(request.getParameter("switchField")));
        settings.setSpecialOccurancy(Settings.SPECIAL_CLEARSPECIAL, Integer.parseInt(request.getParameter("clearSpecial")));
        settings.setSpecialOccurancy(Settings.SPECIAL_GRAVITY, Integer.parseInt(request.getParameter("gravity")));
        settings.setSpecialOccurancy(Settings.SPECIAL_QUAKEFIELD, Integer.parseInt(request.getParameter("quakeField")));
        settings.setSpecialOccurancy(Settings.SPECIAL_BLOCKBOMB, Integer.parseInt(request.getParameter("blockBomb")));

        settings.setBlockOccurancy(Settings.BLOCK_LINE, Integer.parseInt(request.getParameter("line")));
        settings.setBlockOccurancy(Settings.BLOCK_SQUARE, Integer.parseInt(request.getParameter("square")));
        settings.setBlockOccurancy(Settings.BLOCK_LEFTL,  Integer.parseInt(request.getParameter("leftL")));
        settings.setBlockOccurancy(Settings.BLOCK_RIGHTL, Integer.parseInt(request.getParameter("rightL")));
        settings.setBlockOccurancy(Settings.BLOCK_LEFTZ, Integer.parseInt(request.getParameter("leftZ")));
        settings.setBlockOccurancy(Settings.BLOCK_RIGHTZ, Integer.parseInt(request.getParameter("rightZ")));
        settings.setBlockOccurancy(Settings.BLOCK_HALFCROSS, Integer.parseInt(request.getParameter("halfcross")));

        settings.setStartingLevel(Integer.parseInt(request.getParameter("startingLevel")));
        settings.setStartingLevel(Integer.parseInt(request.getParameter("stackHeight")));
        settings.setLinesPerLevel(Integer.parseInt(request.getParameter("linesPerLevel")));
        settings.setLinesPerSpecial(Integer.parseInt(request.getParameter("linesPerSpecial")));
        settings.setLevelIncrease(Integer.parseInt(request.getParameter("levelIncrease")));
        settings.setSpecialAdded(Integer.parseInt(request.getParameter("specialAdded")));
        settings.setSpecialCapacity(Integer.parseInt(request.getParameter("specialCapacity")));
        settings.setAverageLevels(Boolean.valueOf(request.getParameter("averageLevels")).booleanValue());
        settings.setClassicRules(Boolean.valueOf(request.getParameter("classicRules")).booleanValue());
        settings.setSameBlocks(Boolean.valueOf(request.getParameter("sameBlocks")).booleanValue());


        if (errors.isEmpty())
        {
            settings.normalizeBlockOccurancy();
            settings.normalizeSpecialOccurancy();

            // update the settings
            String channelName = request.getParameter("channel");
            if (channelName != null)
            {
                Channel channel = ChannelManager.getInstance().getChannel(channelName);
                channel.getConfig().setSettings(settings);
                response.sendRedirect("/channel.jsp?name=" + channelName);
            }
            else
            {
                Settings.setDefaultSettings(settings);
                response.sendRedirect("/server.jsp");
            }
        }
    }
}
