/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2005  Emmanuel Bourg
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

import java.io.*;
import java.util.*;

import net.jetrix.*;
import net.jetrix.winlist.*;
import net.jetrix.config.*;
import net.jetrix.messages.*;
import org.apache.commons.lang.StringUtils;

/**
 * Protocol to communicate with TetriNET 1.13 compatible clients.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TetrinetProtocol implements Protocol
{
    private static Map<String, String> styles = new HashMap<String, String>();

    static
    {
        styles.put("red", "\u0014");
        styles.put("black", "\u0004");
        styles.put("green", "\u000c");
        styles.put("lightGreen", "\u000e");
        styles.put("darkBlue", "\u0011");
        styles.put("blue", "\u0005");
        styles.put("cyan", "\u0003");
        styles.put("aqua", "\u0017");
        styles.put("yellow", "\u0019");
        styles.put("kaki", "\u0012");
        styles.put("brown", "\u0010");
        styles.put("lightGray", "\u000f");
        styles.put("gray", "\u0006");
        styles.put("magenta", "\u0008");
        styles.put("purple", "\u0013");
        styles.put("b", "\u0002");
        styles.put("i", "\u0016");
        styles.put("u", "\u001f");
        styles.put("white", "\u0018");
    }

    private static Map<String, Class> specials = new TreeMap<String, Class>();

    static
    {
        specials.put("cs1", OneLineAddedMessage .class);
        specials.put("cs2", TwoLinesAddedMessage.class);
        specials.put("cs4", FourLinesAddedMessage.class);
        specials.put("a", AddLineMessage.class);
        specials.put("c", ClearLineMessage.class);
        specials.put("n", NukeFieldMessage.class);
        specials.put("r", RandomClearMessage.class);
        specials.put("s", SwitchFieldsMessage.class);
        specials.put("b", ClearSpecialsMessage.class);
        specials.put("g", GravityMessage.class);
        specials.put("q", BlockQuakeMessage.class);
        specials.put("o", BlockBombMessage.class);
    }

    /**
     * Return the name of this protocol
     */
    public String getName()
    {
        return "tetrinet";
    }

    /**
     * Parse the specified string and return the corresponding server
     * message for this protocol.
     */
    public Message getMessage(String line)
    {
        StringTokenizer st = new StringTokenizer(line);
        if (!st.hasMoreTokens())
        {
            // skip empty lines
            return null;
        }

        String cmd = st.nextToken();
        Message m = null;

        // team <slot> teamname
        if ("team".equals(cmd))
        {
            TeamMessage team = new TeamMessage();
            team.setSlot(Integer.parseInt(st.nextToken()));
            team.setName(st.hasMoreTokens() ? st.nextToken() : null);
            m = team;
            m.setRawMessage(this, line);
        }
        // pline <slot> text
        else if ("pline".equals(cmd))
        {
            if (st.hasMoreTokens())
            {
                String slot = st.nextToken();
                if (st.hasMoreTokens())
                {
                    String firstWord = st.nextToken();

                    if (firstWord.startsWith("/") && !firstWord.startsWith("//"))
                    {
                        CommandMessage command = new CommandMessage();
                        command.setSlot(Integer.parseInt(slot));
                        command.setCommand(firstWord.substring(1));
                        command.setText(line.substring(line.indexOf(" ", 9) + 1));
                        while (st.hasMoreTokens())
                        {
                            command.addParameter(st.nextToken());
                        }
                        m = command;
                    }
                    else
                    {
                        PlineMessage pline = new PlineMessage();
                        pline.setSlot(Integer.parseInt(slot));
                        pline.setText(line.substring("pline".length() + 3));
                        m = pline;
                        m.setRawMessage(this, line);
                    }
                }
            }
        }
        // gmsg <playername> text
        else if ("gmsg".equals(cmd))
        {
            GmsgMessage gmsg = new GmsgMessage();
            gmsg.setText(line.substring(line.indexOf(" ") + 1));
            m = gmsg;
            m.setRawMessage(this, line);
        }
        // plineact <slot> emote
        else if ("plineact".equals(cmd))
        {
            PlineActMessage plineAct = new PlineActMessage();
            plineAct.setSlot(Integer.parseInt(st.nextToken()));
            plineAct.setText(line.substring(line.indexOf(" ")));
            m = plineAct;
            m.setRawMessage(this, line);
        }
        // startgame <0 = stop | 1 = start> <playernumber>
        else if ("startgame".equals(cmd))
        {
            if ("1".equals(st.nextToken()))
            {
                StartGameMessage start = new StartGameMessage();
                start.setSlot(Integer.parseInt(st.nextToken()));
                m = start;
            }
            else
            {
                StopGameMessage stop = new StopGameMessage();
                stop.setSlot(Integer.parseInt(st.nextToken()));
                m = stop;
            }
        }
        // pause <0 = resume | 1 = pause> <playernumber>
        else if ("pause".equals(cmd))
        {
            ChannelMessage msg;
            if ("1".equals(st.nextToken()))
            {
                msg = new PauseMessage();
            }
            else
            {
                msg = new ResumeMessage();
            }
            m = msg;
        }
        // playerlost <slot>
        else if ("playerlost".equals(cmd))
        {
            PlayerLostMessage lost = new PlayerLostMessage();
            lost.setSlot(Integer.parseInt(st.nextToken()));
            m = lost;
            m.setRawMessage(this, line);
        }
        // lvl <playernumber> <level>
        else if ("lvl".equals(cmd))
        {
            LevelMessage level = new LevelMessage();
            level.setSlot(Integer.parseInt(st.nextToken()));
            level.setLevel(Integer.parseInt(st.nextToken()));
            m = level;
            m.setRawMessage(this, line);
        }
        // sb <to> <bonus> <from>
        else if ("sb".equals(cmd))
        {
            int to = Integer.parseInt(st.nextToken());
            String special = st.nextToken();
            int from = Integer.parseInt(st.nextToken());

            Class cls = specials.get(special);

            SpecialMessage spmsg = null;
            if (specials.keySet().contains(special))
            {
                try
                {
                    spmsg = (SpecialMessage) cls.newInstance();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                throw new IllegalArgumentException("Forged special detected from " + this);
            }

            spmsg.setSlot(to);
            spmsg.setFromSlot(from);
            m = spmsg;
            m.setRawMessage(this, line);
        }
        // f <slot> <field>
        else if ("f".equals(cmd))
        {
            FieldMessage field = new FieldMessage();
            field.setSlot(Integer.parseInt(st.nextToken()));
            field.setField((st.hasMoreTokens()) ? st.nextToken() : null);
            m = field;
            m.setRawMessage(this, line);
        }

        return m;
    }


    /**
     * Translate the specified message into a string that will be sent
     * to a client using this protocol.
     */
    public String translate(Message m, Locale locale)
    {
        if (m instanceof SpecialMessage)            { return translate((SpecialMessage) m); }
        else if (m instanceof FieldMessage)         { return translate((FieldMessage) m); }
        else if (m instanceof PlineMessage)         { return translate((PlineMessage) m, locale); }
        else if (m instanceof LevelMessage)         { return translate((LevelMessage) m); }
        else if (m instanceof PlayerLostMessage)    { return translate((PlayerLostMessage) m); }
        else if (m instanceof PlineActMessage)      { return translate((PlineActMessage) m, locale); }
        else if (m instanceof TeamMessage)          { return translate((TeamMessage) m); }
        else if (m instanceof JoinMessage)          { return translate((JoinMessage) m, locale); }
        else if (m instanceof LeaveMessage)         { return translate((LeaveMessage) m, locale); }
        else if (m instanceof PlayerNumMessage)     { return translate((PlayerNumMessage) m); }
        else if (m instanceof StartGameMessage)     { return translate((StartGameMessage) m); }
        else if (m instanceof StopGameMessage)      { return translate((StopGameMessage) m); }
        else if (m instanceof NewGameMessage)       { return translate((NewGameMessage) m); }
        else if (m instanceof EndGameMessage)       { return translate((EndGameMessage) m); }
        else if (m instanceof PauseMessage)         { return translate((PauseMessage) m); }
        else if (m instanceof ResumeMessage)        { return translate((ResumeMessage) m); }
        else if (m instanceof IngameMessage)        { return translate((IngameMessage) m); }
        else if (m instanceof GmsgMessage)          { return translate((GmsgMessage) m, locale); }
        else if (m instanceof PlayerWonMessage)     { return translate((PlayerWonMessage) m); }
        else if (m instanceof NoConnectingMessage)  { return translate((NoConnectingMessage) m); }
        else if (m instanceof SpectatorListMessage) { return translate((SpectatorListMessage) m, locale); }
        else if (m instanceof SmsgMessage)          { return translate((SmsgMessage) m, locale); }
        else if (m instanceof WinlistMessage)       { return translate((WinlistMessage) m, locale); }
        else if (m instanceof NoopMessage)          { return translate((NoopMessage) m); }
        else
        {
            return null;
        }
    }

    public String translate(SpecialMessage m)
    {
        if (m instanceof OneLineAddedMessage)        { return translate((OneLineAddedMessage) m); }
        else if (m instanceof TwoLinesAddedMessage)  { return translate((TwoLinesAddedMessage) m); }
        else if (m instanceof FourLinesAddedMessage) { return translate((FourLinesAddedMessage) m); }
        else if (m instanceof AddLineMessage)        { return translate((AddLineMessage) m); }
        else if (m instanceof ClearLineMessage)      { return translate((ClearLineMessage) m); }
        else if (m instanceof ClearSpecialsMessage)  { return translate((ClearSpecialsMessage) m); }
        else if (m instanceof RandomClearMessage)    { return translate((RandomClearMessage) m); }
        else if (m instanceof BlockQuakeMessage)     { return translate((BlockQuakeMessage) m); }
        else if (m instanceof BlockBombMessage)      { return translate((BlockBombMessage) m); }
        else if (m instanceof GravityMessage)        { return translate((GravityMessage) m); }
        else if (m instanceof NukeFieldMessage)      { return translate((NukeFieldMessage) m); }
        else if (m instanceof SwitchFieldsMessage)   { return translate((SwitchFieldsMessage) m); }
        else
        {
            return null;
        }
    }

    public String translate(PlineMessage m, Locale locale)
    {
        StringBuffer message = new StringBuffer();
        message.append("pline ");
        message.append(m.getSlot());
        message.append(" ");
        message.append(applyStyle(m.getText(locale)));
        return message.toString();
    }

    public String translate(PlineActMessage m, Locale locale)
    {
        StringBuffer message = new StringBuffer();
        message.append("plineact ");
        message.append(m.getSlot());
        message.append(" ");
        message.append(m.getText(locale));
        return message.toString();
    }

    public String translate(TeamMessage m)
    {
        StringBuffer message = new StringBuffer();
        message.append("team ");
        message.append(m.getSlot());
        if (m.getName() != null)
        {
            message.append(" ");
            message.append(m.getName());
        }
        return message.toString();
    }

    public String translate(JoinMessage m, Locale locale)
    {
        if (m.getSlot() == 0)
        {
            // spectator joining
            PlineMessage announce = new PlineMessage();
            announce.setKey("channel.spectator.join", m.getName());
            return translate(announce, locale);
        }
        else
        {
            StringBuffer message = new StringBuffer();
            message.append("playerjoin ");
            message.append(m.getSlot());
            message.append(" ");
            message.append(m.getName());
            return message.toString();
        }
    }

    public String translate(LeaveMessage m, Locale locale)
    {
        if (m.getSlot() == 0)
        {
            // spectator leaving
            PlineMessage announce = new PlineMessage();
            announce.setKey("channel.spectator.leave", m.getName());
            return translate(announce, locale);
        }
        else
        {
            StringBuffer message = new StringBuffer();
            message.append("playerleave ");
            message.append(m.getSlot());
            return message.toString();
        }
    }

    public String translate(PlayerNumMessage m)
    {
        StringBuffer message = new StringBuffer();
        message.append("playernum ");
        message.append(m.getSlot());
        return message.toString();
    }

    public String translate(StartGameMessage m)
    {
        StringBuffer message = new StringBuffer();
        message.append("startgame 1 ");
        message.append(m.getSlot());
        return message.toString();
    }

    public String translate(StopGameMessage m)
    {
        StringBuffer message = new StringBuffer();
        message.append("startgame 0 ");
        message.append(m.getSlot());
        return message.toString();
    }

    public String translate(NewGameMessage m)
    {
        Settings s = m.getSettings();
        StringBuffer message = new StringBuffer();
        message.append("newgame ");
        message.append(s.getStackHeight());
        message.append(" ");
        message.append(s.getStartingLevel());
        message.append(" ");
        message.append(s.getLinesPerLevel());
        message.append(" ");
        message.append(s.getLevelIncrease());
        message.append(" ");
        message.append(s.getLinesPerSpecial());
        message.append(" ");
        message.append(s.getSpecialAdded());
        message.append(" ");
        message.append(s.getSpecialCapacity());
        message.append(" ");

        // blocks frequency
        for (Block block : Block.values())
        {
            for (int j = 0; j < s.getOccurancy(block); j++)
            {
                message.append(Integer.toString(block.getValue() + 1));
            }
        }

        message.append(" ");

        // specials frequency
        for (Special special : Special.values())
        {
            for (int j = 0; j < s.getOccurancy(special); j++)
            {
                message.append(Integer.toString(special.getValue() + 1));
            }
        }

        message.append(" ");
        message.append(s.getAverageLevels() ? "1" : "0");
        message.append(" ");
        message.append(s.getClassicRules() ? "1" : "0");

        // extended parameter for 1.14 clients
        if (s.getSameBlocks() == true)
        {
            message.append(" ");
            String hexstring = Integer.toHexString(m.getSeed()).toUpperCase();
            // padding to 8 digits
            for (int i = hexstring.length(); i < 8; i++)
            {
                message.append("0");
            }

            message.append(hexstring);
        }

        return message.toString();
    }

    public String translate(EndGameMessage m)
    {
        return "endgame";
    }

    public String translate(PauseMessage m)
    {
        return "pause 1 ";
    }

    public String translate(ResumeMessage m)
    {
        return "pause 0 ";
    }

    public String translate(IngameMessage m)
    {
        return "ingame";
    }

    public String translate(GmsgMessage m, Locale locale)
    {
        StringBuffer message = new StringBuffer();
        message.append("gmsg ");
        message.append(m.getText(locale));
        return message.toString();
    }

    public String translate(LevelMessage m)
    {
        StringBuffer message = new StringBuffer();
        message.append("lvl ");
        message.append(m.getSlot());
        message.append(" ");
        message.append(m.getLevel());
        return message.toString();
    }

    public String translate(FieldMessage m)
    {
        StringBuffer message = new StringBuffer();
        message.append("f ");
        message.append(m.getSlot());
        if (m.getField() != null)
        {
            message.append(" ");
            message.append(m.getField());
        }
        return message.toString();
    }

    public String translate(PlayerLostMessage m)
    {
        StringBuffer message = new StringBuffer();
        message.append("playerlost ");
        message.append(m.getSlot());
        return message.toString();
    }

    public String translate(PlayerWonMessage m)
    {
        StringBuffer message = new StringBuffer();
        message.append("playerwon ");
        message.append(m.getSlot());
        return message.toString();
    }

    public String translate(NoConnectingMessage m)
    {
        StringBuffer message = new StringBuffer();
        message.append("noconnecting ");
        message.append(m.getText());
        return message.toString();
    }

    public String translate(OneLineAddedMessage m)
    {
        StringBuffer message = new StringBuffer();
        message.append("sb ");
        message.append(m.getSlot());
        message.append(" cs1 ");
        message.append(m.getFromSlot());
        return message.toString();
    }

    public String translate(TwoLinesAddedMessage m)
    {
        StringBuffer message = new StringBuffer();
        message.append("sb ");
        message.append(m.getSlot());
        message.append(" cs2 ");
        message.append(m.getFromSlot());
        return message.toString();
    }

    public String translate(FourLinesAddedMessage m)
    {
        StringBuffer message = new StringBuffer();
        message.append("sb ");
        message.append(m.getSlot());
        message.append(" cs4 ");
        message.append(m.getFromSlot());
        return message.toString();
    }

    public String translate(AddLineMessage m)
    {
        StringBuffer message = new StringBuffer();
        message.append("sb ");
        message.append(m.getSlot());
        message.append(" a ");
        message.append(m.getFromSlot());
        return message.toString();
    }

    public String translate(ClearLineMessage m)
    {
        StringBuffer message = new StringBuffer();
        message.append("sb ");
        message.append(m.getSlot());
        message.append(" c ");
        message.append(m.getFromSlot());
        return message.toString();
    }

    public String translate(NukeFieldMessage m)
    {
        StringBuffer message = new StringBuffer();
        message.append("sb ");
        message.append(m.getSlot());
        message.append(" n ");
        message.append(m.getFromSlot());
        return message.toString();
    }

    public String translate(RandomClearMessage m)
    {
        StringBuffer message = new StringBuffer();
        message.append("sb ");
        message.append(m.getSlot());
        message.append(" r ");
        message.append(m.getFromSlot());
        return message.toString();
    }

    public String translate(SwitchFieldsMessage m)
    {
        StringBuffer message = new StringBuffer();
        message.append("sb ");
        message.append(m.getSlot());
        message.append(" s ");
        message.append(m.getFromSlot());
        return message.toString();
    }

    public String translate(ClearSpecialsMessage m)
    {
        StringBuffer message = new StringBuffer();
        message.append("sb ");
        message.append(m.getSlot());
        message.append(" b ");
        message.append(m.getFromSlot());
        return message.toString();
    }

    public String translate(GravityMessage m)
    {
        StringBuffer message = new StringBuffer();
        message.append("sb ");
        message.append(m.getSlot());
        message.append(" g ");
        message.append(m.getFromSlot());
        return message.toString();
    }

    public String translate(BlockQuakeMessage m)
    {
        StringBuffer message = new StringBuffer();
        message.append("sb ");
        message.append(m.getSlot());
        message.append(" q ");
        message.append(m.getFromSlot());
        return message.toString();
    }

    public String translate(BlockBombMessage m)
    {
        StringBuffer message = new StringBuffer();
        message.append("sb ");
        message.append(m.getSlot());
        message.append(" o ");
        message.append(m.getFromSlot());
        return message.toString();
    }

    public String translate(SpectatorListMessage m, Locale locale)
    {
        PlineMessage pline = new PlineMessage();
        pline.setKey("command.speclist.format", StringUtils.join(m.getSpectators().iterator(), ", "));

        return translate(pline, locale);
    }

    public String translate(SmsgMessage m, Locale locale)
    {
        StringBuffer message = new StringBuffer();
        String name = ((Client) m.getSource()).getUser().getName();

        PlineMessage pline = new PlineMessage();
        pline.setKey("channel.spectator.message", name, m.getText());
        message.append(translate(pline, locale));

        return message.toString();
    }

    public String translate(WinlistMessage m, Locale locale)
    {
        StringBuffer message = new StringBuffer();
        message.append("winlist");

        for (Score score : m.getScores())
        {           
            message.append(" ");
            message.append(score.getType() == Score.TYPE_PLAYER ? "p" : "t");
            message.append(score.getName());
            message.append(";");
            message.append(score.getScore());
        }
        return message.toString();
    }

    public String translate(NoopMessage m)
    {
        return "";
    }

    /**
     * Return the map of the color and style codes for this protocol.
     */
    public Map<String, String> getStyles()
    {
        return styles;
    }

    public String applyStyle(String text)
    {
        // to be optimized later
        Map<String, String> styles = getStyles();
        if (styles == null) {
            return text;
        }

        for (String key : styles.keySet())
        {
            String value = styles.get(key);
            if (value == null)
            {
                value = "";
            }
            text = text.replaceAll("<" + key + ">", value);
            text = text.replaceAll("</" + key + ">", value);
        }
        return text;
    }

    public char getEOL()
    {
        return 0xFF;
    }


    /**
     * Decodes TetriNET client initialization string
     *
     * @param initString initialization string
     * @return decoded string
     * @throws IllegalArgumentException thrown if the string can't be decoded
     */
    public static String decode(String initString)
    {
        // check the size of the init string
        if (initString.length() % 2 != 0)
        {
            throw new IllegalArgumentException("Invalid initialization string, the length is not even");
        }

        // parse the hex values from the init string
        int[] dec = new int[initString.length() / 2];

        try
        {
            for (int i = 0; i < dec.length; i++)
            {
                dec[i] = Integer.parseInt(initString.substring(i * 2, i * 2 + 2), 16);
            }
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Invalid initialization string, illegal characters found", e);
        }

        // find the hash pattern for a tetrinet client
        String pattern = findHashPattern(dec, false);

        // find the hash pattern for a tetrifast client
        if (pattern.length() == 0)
        {
            pattern = findHashPattern(dec, true);
        }

        // check the size of the pattern found
        if (pattern.length() == 0)
        {
            throw new IllegalArgumentException("Invalid initialization string, unable to find the pattern");
        }

        // decode the string
        StringBuffer s = new StringBuffer();

        for (int i = 1; i < dec.length; i++)
        {
            s.append((char) (((dec[i] ^ pattern.charAt((i - 1) % pattern.length())) + 255 - dec[i - 1]) % 255));
        }

        return s.toString().replace((char) 0, (char) 255);
    }

    private static String findHashPattern(int[] dec, boolean tetrifast)
    {
        // the first characters from the decoded string
        char[] data = (tetrifast ? "tetrifaste" : "tetrisstar").toCharArray();

        // compute the full hash
        int[] hash = new int[data.length];

        for (int i = 0; i < data.length; i++)
        {
            hash[i] = ((data[i] + dec[i]) % 255) ^ dec[i + 1];
        }

        // find the length of the hash
        int length = 5;

        for (int i = 5; i == length && i > 0; i--)
        {
            for (int j = 0; j < data.length - length; j++)
            {
                if (hash[j] != hash[j + length])
                {
                    length--;
                }
            }
        }

        return new String(hash, 0, length);
    }

    /**
     * Return the initialization string for the specified user.
     *
     * @param nickname  the nickname of the client
     * @param version   the version of the client
     * @param ip        the IP of the server
     * @param tetrifast is this a tetrifast client ?
     */
    public static String encode(String nickname, String version, byte[] ip, boolean tetrifast)
    {
        // compute the pattern
        int p = 54 * ip[0] + 41 * ip[1] + 29 * ip[2] + 17 * ip[3];
        char[] pattern = String.valueOf(p).toCharArray();

        // build the string to encode
        char[] data = ((tetrifast ? "tetrifaster " : "tetrisstart ") + nickname + " " + version).toCharArray();

        // build the encoded string
        StringBuffer result = new StringBuffer();
        char offset = 0x80;
        result.append(toHex(offset));

        char previous = offset;

        for (int i = 0; i < data.length; i++)
        {
            char current = (char) (((previous + data[i]) % 255) ^ pattern[i % pattern.length]);
            result.append(toHex(current));
            previous = current;
        }

        return result.toString().toUpperCase();
    }

    /**
     * Return the hex value of the specified byte on 2 digits.
     */
    private static String toHex(char c)
    {
        String h = Integer.toHexString(c);

        return h.length() > 1 ? h : "0" + h;
    }

    /**
     * Read a line as defined in the TetriNET protocol (that's ending with a
     * 0xFF character). 0xOA and 0xOD are also accepted as EOL characters.
     *
     * @since 0.2.1
     *
     * @param in the stream to be read
     * @throws IOException thrown if the stream is closed
     */
    public static String readLine(Reader in) throws IOException
    {
        StringBuffer input = new StringBuffer();

        int readChar;
        while ((readChar = in.read()) != -1 && readChar != 0xFF && readChar != 0x0A && readChar != 0x0D)
        {
            if (readChar != 0x0A && readChar != 0x0D)
            {
                input.append((char) readChar);
            }
        }

        if (readChar == -1)
        {
            throw new IOException("End of stream");
        }

        return input.toString();
    }

    public String toString()
    {
        return "[Protocol name=" + getName() + "]";
    }

}
