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
import net.jetrix.messages.*;

/**
 * Start the game.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class StartCommand implements Command
{
    private int accessLevel = 0;

    public String[] getAliases()
    {
        return (new String[] { "start" });
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsage(Locale locale)
    {
        return "/start <" + Language.getText("command.params.seconds", locale) + ">";
    }

    public String getDescription(Locale locale)
    {
        return Language.getText("command.start.description", locale);
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client)m.getSource();
        Channel channel = client.getChannel();

        if (channel != null)
        {
            // delay in seconds for the countdown
            int delay = 0;

            if (m.getParameterCount() > 0)
            {
                try { delay = Integer.parseInt(m.getParameter(0)); } catch (Exception e) { };
            }

            // the delay is capped at 20 seconds
            delay = Math.min(delay, 20);

            if (delay > 0)
            {
                (new StartCommand.CountDown(channel, delay)).start();
            }
            else
            {
                StartGameMessage start = new StartGameMessage();
                start.setSlot(channel.getClientSlot(client));
                start.setSource(client);
                channel.sendMessage(start);
            }
        }
    }

    /**
     * A countdown thread to delay the beginning of the game.
     */
    private static class CountDown extends Thread
    {
        private Channel channel;
        private int delay;
        /** */
        private static Map countdowns = new HashMap();

        /**
         * Construct a new game countdown.
         *
         * @param channel the channel where game will start
         * @param delay the delay in seconds for this countdown
         */
        public CountDown(Channel channel, int delay)
        {
            this.channel = channel;
            this.delay = delay;
        }

        public void run()
        {
            // don't start the countdown is the game has already started
            if (channel.getGameState() != Channel.GAME_STATE_STOPPED) return;

            // don't start the countdown if another one is already running
            if (countdowns.get(channel) != null) return;
            countdowns.put(channel, this);

            PlineMessage getready1 = new PlineMessage();
            GmsgMessage  getready2 = new GmsgMessage();
            getready1.setKey("command.start.get_ready");
            getready2.setKey("command.start.get_ready.gmsg");
            channel.sendMessage(getready1);
            channel.sendMessage(getready2);

            // start the count down...
            for (int i = delay; i > 0; i--)
            {
                PlineMessage msg1 = new PlineMessage();
                GmsgMessage  msg2 = new GmsgMessage();

                // plural or singular ? :)
                if (i > 1)
                {
                    Object[] params = new Object[] { new Integer(i)};
                    msg1.setKey("command.start.seconds", params);
                    msg2.setKey("command.start.seconds.gmsg", params);
                }
                else
                {
                    msg1.setKey("command.start.second");
                    msg2.setKey("command.start.second.gmsg");
                }

                channel.sendMessage(msg1);
                channel.sendMessage(msg2);
                try { sleep(1000); } catch(InterruptedException e) { }

                // cancel the countdown if the game has started
                if (channel.getGameState() != Channel.GAME_STATE_STOPPED)
                {
                    countdowns.put(channel, null);
                    return;
                }
            }

            // announce "GO!"
            PlineMessage go1 = new PlineMessage();
            GmsgMessage  go2 = new GmsgMessage();
            go1.setKey("command.start.go");
            go2.setKey("command.start.go.gmsg");
            channel.sendMessage(go1);
            channel.sendMessage(go2);

            // start the game
            StartGameMessage start = new StartGameMessage();
            channel.sendMessage(start);

            countdowns.put(channel, null);
        }
    }


}
