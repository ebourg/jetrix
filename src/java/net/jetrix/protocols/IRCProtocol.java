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

package net.jetrix.protocols;

import java.util.*;
import net.jetrix.*;
import net.jetrix.messages.*;
import net.jetrix.messages.channel.*;
import net.jetrix.messages.channel.specials.*;
import org.apache.commons.lang.*;

/**
 * Protocol to communicate with IRC clients.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class IRCProtocol extends AbstractProtocol
{
    private static Map<String, String> styles = new HashMap<String, String>();

    static
    {
        styles.put("red", "\u000304");
        styles.put("black", "\u000301");
        styles.put("green", "\u000303");
        styles.put("lightGreen", "\u000309");
        styles.put("darkBlue", "\u000302");
        styles.put("blue", "\u000312");
        styles.put("cyan", "\u000310");
        styles.put("aqua", "\u000311");
        styles.put("yellow", "\u000308");
        styles.put("kaki", "\u000307");
        styles.put("brown", "\u000305");
        styles.put("lightGray", "\u000315");
        styles.put("gray", "\u000314");
        styles.put("magenta", "\u000313");
        styles.put("purple", "\u000306");
        styles.put("b", "\u0002");
        styles.put("i", "\u0016");
        styles.put("u", "\u0037");
        styles.put("white", "\u000300");
    }

    /**
     * Return the name of this protocol
     */
    public String getName()
    {
        return "IRC";
    }

    /**
     * Parse the specified string and return the corresponding server
     * message for this protocol.
     */
    public Message getMessage(String line)
    {
        IRCMessage msg = IRCMessage.parse(line);

        if (msg.isCommand(IRCCommand.JOIN))
        {
            AddPlayerMessage message = new AddPlayerMessage();
            message.setDestination(ChannelManager.getInstance().getChannel(msg.getParameter(0)));

            return message;
        }
        else if (msg.isCommand(IRCCommand.PART))
        {
            LeaveMessage message = new LeaveMessage();
            message.setDestination(ChannelManager.getInstance().getChannel(msg.getParameter(0)));

            return message;
        }
        else if (msg.isCommand(IRCCommand.PRIVMSG))
        {
            // todo : check for emotes

            SmsgMessage message = new SmsgMessage();
            message.setDestination(ChannelManager.getInstance().getChannel(msg.getParameter(0)));
            message.setText(msg.getParameter(1));
            message.setPrivate(false);

            return message;
        }
        else
        {
            return null;
        }
    }

    /**
     * Translate the specified message into a string that will be sent
     * to a client using this protocol.
     */
    public String translate(Message m, Locale locale)
    {
        if (m instanceof PlineMessage)         { return translate((PlineMessage) m, locale); }
        else if (m instanceof PlayerLostMessage)    { return translate((PlayerLostMessage) m); }
        else if (m instanceof PlineActMessage)      { return translate((PlineActMessage) m, locale); }
        else if (m instanceof TeamMessage)          { return translate((TeamMessage) m, locale); }
        else if (m instanceof JoinMessage)          { return translate((JoinMessage) m, locale); }
        else if (m instanceof LeaveMessage)         { return translate((LeaveMessage) m, locale); }
        else if (m instanceof NewGameMessage)       { return translate((NewGameMessage) m, locale); }
        else if (m instanceof EndGameMessage)       { return translate((EndGameMessage) m, locale); }
        else if (m instanceof IngameMessage)       { return translate((IngameMessage) m, locale); }
        else if (m instanceof PauseMessage)         { return translate((PauseMessage) m); }
        else if (m instanceof ResumeMessage)        { return translate((ResumeMessage) m); }
        else if (m instanceof GmsgMessage)          { return translate((GmsgMessage) m, locale); }
        else if (m instanceof SpectatorListMessage) { return translate((SpectatorListMessage) m, locale); }
        //else if (m instanceof SmsgMessage)          { return translate((SmsgMessage) m, locale); }
        else
        {
            return null;
        }
    }

    public String translate(PlineMessage m, Locale locale)
    {
        IRCMessage message = new IRCMessage(IRCCommand.PRIVMSG);

        Destination source = m.getSource();
        if (source != null && source instanceof Client)
        {
            message.setNick(((Client) source).getUser().getName());
        }
        else
        {
            message.setNick("jetrix");
        }

        String name = m.getChannelName() == null ? "#jetrix" : "#" + m.getChannelName();

        message.addParameter(name);
        message.addParameter(applyStyle(m.getText(locale)));

        return message.toString();
    }

    public String translate(PlineActMessage m, Locale locale)
    {
        IRCMessage message = new IRCMessage(IRCCommand.PRIVMSG);

        Destination source = m.getSource();
        if (source != null && source instanceof Client)
        {
            message.setNick(((Client) source).getUser().getName());
        }
        else
        {
            message.setNick("jetrix");
        }

        String name = m.getChannelName() == null ? "#jetrix" : "#" + m.getChannelName();

        message.addParameter(name);
        message.addParameter(applyStyle("\u0001ACTION " + m.getText(locale) + "\u0001"));

        return message.toString();
    }

    public String translate(GmsgMessage m, Locale locale)
    {
        IRCMessage message = new IRCMessage(IRCCommand.PRIVMSG);

        Destination source = m.getSource();
        if (source != null && source instanceof Client)
        {
            message.setNick(((Client) source).getUser().getName());
        }
        else
        {
            message.setNick("jetrix");
        }

        String name = m.getChannelName() == null ? "#jetrix" : "#" + m.getChannelName();

        // todo remove the <name> of the player at the beginning of the message

        message.addParameter(name);
        message.addParameter(applyStyle("<gray>" + m.getText(locale)));

        return message.toString();
    }

    public String translate(SpectatorListMessage m, Locale locale)
    {
        IRCMessage message1 = new IRCMessage(IRCReply.RPL_NAMREPLY);
        message1.setNick("jetrix");
        message1.addParameter(((Client) m.getDestination()).getUser().getName());
        message1.addParameter("=");
        message1.addParameter("#" + m.getChannel());

        Collection<String> spectators = m.getSpectators();
        message1.addParameter(StringUtils.join(spectators.iterator(), " "));

        IRCMessage message2 = new IRCMessage(IRCReply.RPL_ENDOFNAMES);
        message2.setNick("jetrix");
        message2.addParameter(((Client) m.getDestination()).getUser().getName());
        message2.addParameter("#" + m.getChannel());
        message2.addParameter("End of /NAMES list");

        return message1.toString() + getEOL() + message2;
    }

    public String translate(TeamMessage m, Locale locale)
    {
        Client client = (Client) m.getSource();

        IRCMessage message = new IRCMessage(IRCCommand.PRIVMSG);
        message.setNick("jetrix");
        message.addParameter("#" + m.getChannelName());

        String messageKey = m.getName() == null ? "channel.team.none" : "channel.team.new";
        Object[] params = new Object[] { client.getUser().getName(), m.getName() };
        message.addParameter(applyStyle(Language.getText(messageKey, locale, params)));

        return message.toString();
    }

    public String translate(JoinMessage m, Locale locale)
    {
        IRCMessage message = new IRCMessage(IRCCommand.JOIN);
        message.setNick(m.getName());
        message.addParameter("#" + m.getChannelName());

        return message.toString();
    }

    public String translate(LeaveMessage m, Locale locale)
    {
        IRCMessage message = new IRCMessage(IRCCommand.PART);
        message.setNick(m.getName());
        message.addParameter("#" + m.getChannelName());

        return message.toString();
    }

    public String translate(NewGameMessage m, Locale locale)
    {
        IRCMessage message = new IRCMessage(IRCCommand.PRIVMSG);
        message.setNick("jetrix");
        message.addParameter("#" + m.getChannelName());
        message.addParameter(applyStyle(Language.getText("channel.game.start", locale)));

        return message.toString();
    }

    public String translate(EndGameMessage m, Locale locale)
    {
        IRCMessage message = new IRCMessage(IRCCommand.PRIVMSG);
        message.setNick("jetrix");
        message.addParameter("#" + m.getChannelName());
        message.addParameter(applyStyle(Language.getText("channel.game.stop", locale)));

        return message.toString();
    }

    public String translate(IngameMessage m, Locale locale)
    {
        IRCMessage message = new IRCMessage(IRCCommand.PRIVMSG);
        message.setNick("jetrix");
        message.addParameter("#" + m.getChannelName());
        message.addParameter(applyStyle(Language.getText("channel.game.running", locale)));

        return message.toString();
    }

    public String translate(PauseMessage m)
    {
        return null;
    }

    public String translate(ResumeMessage m)
    {
        return null;
    }

    public String translate(LevelMessage m)
    {
        return null;
    }

    public String translate(FieldMessage m)
    {
        return null;
    }

    public String translate(PlayerLostMessage m)
    {
        return null;
    }

    public String translate(DisconnectedMessage m)
    {
        return null;
    }

    public String translate(CommandMessage m)
    {
        return null;
    }

    public String translate(LinesAddedMessage m)
    {
        return null;
    }

    public String translate(AddLineMessage m)
    {
        return null;
    }

    public String translate(ClearLineMessage m)
    {
        return null;
    }

    public String translate(NukeFieldMessage m)
    {
        return null;
    }

    public String translate(RandomClearMessage m)
    {
        return null;
    }

    public String translate(SwitchFieldsMessage m)
    {
        return null;
    }

    public String translate(ClearSpecialsMessage m)
    {
        return null;
    }

    public String translate(GravityMessage m)
    {
        return null;
    }

    public String translate(BlockQuakeMessage m)
    {
        return null;
    }

    public String translate(BlockBombMessage m)
    {
        return null;
    }

    public Map<String,String> getStyles()
    {
        return styles;
    }

    public String applyStyle(String text)
    {
        // todo to be optimized later
        Map<String,String> styles = getStyles();
        if (styles == null) return text;

        for (String key : styles.keySet())
        {
            String value = styles.get(key);
            if (value == null) { value = ""; }
            text = text.replaceAll("<" + key + ">", value);
            text = text.replaceAll("</" + key + ">", value);
        }
        return text;
    }

    public char getEOL()
    {
        return '\n';
    }

}
