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

package net.jetrix.commands;

import static net.jetrix.GameState.*;

import java.util.*;

import net.jetrix.*;
import net.jetrix.messages.channel.StartGameMessage;
import net.jetrix.messages.channel.CommandMessage;
import net.jetrix.messages.channel.GmsgMessage;
import net.jetrix.messages.channel.PlineMessage;

/**
 * Start the game.
 *
 * @author Emmanuel Bourg
 */
public class StartCommand extends AbstractCommand
{
    public String getAlias()
    {
        return "start";
    }

    public String getUsage(Locale locale)
    {
        return "/start <" + Language.getText("command.params.seconds", locale) + ">";
    }

    public void execute(CommandMessage m)
    {
        Client client = (Client) m.getSource();
        Channel channel = client.getChannel();

        if (channel != null && channel.getGameState() == STOPPED)
        {
            // delay in seconds for the countdown
            int delay = 0;

            if (m.getParameterCount() > 0)
            {
                delay = m.getIntParameter(0, delay);
            }

            // the delay is capped at 20 seconds
            delay = Math.min(delay, 20);

            if (delay > 0)
            {
                // tell who started the game
                PlineMessage message = new PlineMessage();
                message.setKey("channel.game.started-by", client.getUser().getName());
                channel.send(message);

                (new StartCommand.CountDown(channel, delay)).start();
            }
            else
            {
                StartGameMessage start = new StartGameMessage();
                start.setSlot(channel.getClientSlot(client));
                start.setSource(client);
                channel.send(start);
            }
        }
    }

    /**
     * A countdown thread to delay the beginning of the game.
     */
    public static class CountDown extends Thread
    {
        private Channel channel;
        private int delay;
        /** */
        private static Map<Channel, CountDown> countdowns = new HashMap<Channel, CountDown>();

        /**
         * Construct a new game countdown.
         *
         * @param channel the channel where game will start
         * @param delay   the delay in seconds for this countdown
         */
        public CountDown(Channel channel, int delay)
        {
            this.channel = channel;
            this.delay = delay;
        }

        public void run()
        {
            // don't start the countdown is the game has already started
            if (channel.getGameState() != STOPPED) return;

            // don't start the countdown if another one is already running
            if (countdowns.get(channel) != null) return;
            countdowns.put(channel, this);

            PlineMessage getready1 = new PlineMessage();
            GmsgMessage getready2 = new GmsgMessage();
            getready1.setKey("command.start.get_ready");
            getready2.setKey("command.start.get_ready");
            channel.send(getready1);
            channel.send(getready2);

            // start the count down...
            for (int i = delay; i > 0; i--)
            {
                PlineMessage msg1 = new PlineMessage();
                GmsgMessage msg2 = new GmsgMessage();

                // plural or singular ? :)
                String key = "command.start.second" + (i > 1 ? "s" : "");
                msg1.setKey(key, i);
                msg2.setKey(key, i);
                
                channel.send(msg1);
                channel.send(msg2);
                try
                {
                    sleep(1000);
                }
                catch (InterruptedException e)
                {
                }

                // cancel the countdown if the game has started
                if (channel.getGameState() != STOPPED)
                {
                    countdowns.put(channel, null);
                    return;
                }
            }

            // announce "GO!"
            PlineMessage go1 = new PlineMessage();
            GmsgMessage go2 = new GmsgMessage();
            go1.setKey("command.start.go");
            go2.setKey("command.start.go");
            channel.send(go1);
            channel.send(go2);

            // start the game
            StartGameMessage start = new StartGameMessage();
            channel.send(start);

            countdowns.put(channel, null);
        }
    }
}
