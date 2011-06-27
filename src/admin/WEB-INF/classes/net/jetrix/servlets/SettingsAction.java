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

package net.jetrix.servlets;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import net.jetrix.Channel;
import net.jetrix.ChannelManager;
import net.jetrix.Server;
import net.jetrix.config.Block;
import net.jetrix.config.Occurancy;
import net.jetrix.config.Settings;
import net.jetrix.config.Special;

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

        // todo: validation

        // get the settings to update
        String channelName = request.getParameter("channel");
        Settings settings = null;

        if (errors.isEmpty())
        {
            if (channelName != null)
            {
                // channel settings
                Channel channel = ChannelManager.getInstance().getChannel(channelName);
                settings = channel.getConfig().getSettings();
            }
            else
            {
                // server settings
                settings = Settings.getDefaultSettings();
            }
        }
        
        // update the special occurancies
        boolean resetSpecials = true;
        Occurancy<Special> specialOccurancy = settings.getSpecialOccurancy().clone();
        for (Special special : Special.values())
        {
            String value = request.getParameter(special.getCode());
            resetSpecials = resetSpecials && StringUtils.isBlank(value);
            
            if (StringUtils.isNotBlank(value))
            {
                specialOccurancy.setOccurancy(special, Integer.parseInt(value));
            }
        }
        
        if (!specialOccurancy.equals(settings.getSpecialOccurancy()))
        {
            specialOccurancy.normalize();
            settings.setSpecialOccurancy(specialOccurancy);
        }
        else if (resetSpecials)
        {
            settings.setDefaultSpecialOccurancy(true);
        }
        
        
        // update the block occurancies
        boolean resetBlocks = true;
        Occurancy<Block> blockOccurancy = settings.getBlockOccurancy().clone();
        for (Block block : Block.values())
        {
            String value = request.getParameter(block.getCode());
            resetBlocks = resetBlocks && StringUtils.isBlank(value);
            
            if (StringUtils.isNotBlank(value)) 
            {
                blockOccurancy.setOccurancy(block, Integer.parseInt(value));
            }
        }
        
        if (!blockOccurancy.equals(settings.getBlockOccurancy()))
        {
            blockOccurancy.normalize();
            settings.setBlockOccurancy(blockOccurancy);
        }
        else if (resetBlocks)
        {
            settings.setDefaultBlockOccurancy(true);
        }
        
        
        // update the game settings
        updateSettingsField(settings, "startingLevel", request);
        updateSettingsField(settings, "stackHeight", request);
        updateSettingsField(settings, "linesPerLevel", request);
        updateSettingsField(settings, "linesPerSpecial", request);
        updateSettingsField(settings, "levelIncrease", request);
        updateSettingsField(settings, "specialAdded", request);
        updateSettingsField(settings, "specialCapacity", request);
        updateSettingsField(settings, "averageLevels", request);
        updateSettingsField(settings, "classicRules", request);
        updateSettingsField(settings, "sameBlocks", request);

        // update the sudden death settings
        updateSettingsField(settings, "suddenDeathTime", request);
        updateSettingsField(settings, "suddenDeathMessage", request);
        updateSettingsField(settings, "suddenDeathDelay", request);
        updateSettingsField(settings, "suddenDeathLinesAdded", request);


        // redirection
        if (settings != Settings.getDefaultSettings())
        {
            // channel settings
            response.sendRedirect("/channel.jsp?name=" + channelName);
        }
        else
        {
            // server settings
            response.sendRedirect("/server.jsp");
        }

        // save the server configuration
        Server.getInstance().getConfig().save();
    }

    /**
     * Update the settings field with the value in the request. The field is
     * set only if the value in the request is different from the current value.
     * If the value is empty the field is reset to the default value.
     *
     * @param settings the Settings object to update
     * @param field    the name of the field to update
     * @param request  the request containing the field value
     */
    private void updateSettingsField(Settings settings, String field, HttpServletRequest request)
    {
        String value = request.getParameter(field);

        field = field.substring(0, 1).toUpperCase() + field.substring(1);

        try
        {
            if (value == null || "".equals(value.trim()))
            {
                // reset the field to the default value
                Method method = Settings.class.getMethod("setDefault" + field, Boolean.TYPE);
                method.invoke(settings, Boolean.TRUE);
            }
            else
            {
                // update the field if necessary
                Method getter = Settings.class.getMethod("get" + field);
                Object oldValue = getter.invoke(settings);

                // find the type of the field
                Class type = null;

                Object newValue = null;
                if (oldValue instanceof String)
                {
                    newValue = value.trim();
                    type = String.class;
                }
                else if (oldValue instanceof Integer)
                {
                    newValue = Integer.parseInt(value.trim());
                    type = Integer.TYPE;
                }
                else if (oldValue instanceof Boolean)
                {
                    newValue = Boolean.valueOf(value);
                    type = Boolean.TYPE;
                }

                if (!oldValue.equals(newValue))
                {
                    // set the new value
                    Method setter = Settings.class.getMethod("set" + field, type);
                    setter.invoke(settings, newValue);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
