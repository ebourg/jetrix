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

import java.util.*;

import net.jetrix.*;
import net.jetrix.winlist.*;
import net.jetrix.config.*;
import net.jetrix.messages.*;
import net.jetrix.messages.channel.*;
import net.jetrix.messages.channel.specials.*;
import org.apache.commons.lang.StringUtils;

/**
 * Protocol to communicate with TetriNET 1.13 compatible clients.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TetrinetProtocol extends AbstractProtocol
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

    /** Initialization token */
    public static final String INIT_TOKEN = "tetrisstart";

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

        // f <slot> <field>
        if ("f".equals(cmd))
        {
            FieldMessage field = new FieldMessage();
            field.setSlot(Integer.parseInt(st.nextToken()));
            field.setField((st.hasMoreTokens()) ? st.nextToken() : null);
            m = field;
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
        // team <slot> teamname
        else if ("team".equals(cmd))
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
            plineAct.setText(line.substring("plineact".length() + 3));
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
        // newgame
        else if ("newgame".equals(cmd))
        {
            m = new NewGameMessage();

            // todo parse the game settings
        }
        // endgame
        else if ("endgame".equals(cmd))
        {
            m = new EndGameMessage();
        }
        // ingame
        else if ("ingame".equals(cmd))
        {
            m = new IngameMessage();
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
        // clientinfo <name> <version>
        else if ("clientinfo".equals(cmd))
        {
            ClientInfoMessage info = new ClientInfoMessage();
            if (st.hasMoreTokens())
            {
                info.setName(st.nextToken());
            }
            if (st.hasMoreTokens())
            {
                info.setVersion(st.nextToken());
            }
            m = info;
        }
        // playernum <num>
        else if ("playernum".equals(cmd))
        {
            PlayerNumMessage num = new PlayerNumMessage();
            num.setSlot(Integer.parseInt(st.nextToken()));
            m = num;
        }
        // playerjoin <num> <name>
        else if ("playerjoin".equals(cmd))
        {
            JoinMessage join = new JoinMessage();
            join.setSlot(Integer.parseInt(st.nextToken()));
            join.setName(st.nextToken());
            join.setRawMessage(this, line);
            m = join;
        }
        // playerleave <num>
        else if ("playerleave".equals(cmd))
        {
            LeaveMessage leave = new LeaveMessage();
            leave.setSlot(Integer.parseInt(st.nextToken()));
            leave.setRawMessage(this, line);
            m = leave;
        }
        else if ("noconnecting".equals(cmd))
        {
            NoConnectingMessage noconnecting = new NoConnectingMessage();
            noconnecting.setText(line.substring(cmd.length() + 1));
            m = noconnecting;
        }
        else if ("winlist".equals(cmd))
        {
            WinlistMessage winlist = new WinlistMessage();
            winlist.setRawMessage(this, line);
            List<Score> scores = new ArrayList<Score>();
            String[] tokens = line.split(" ");
            for (int i = 1; i < tokens.length; i++)
            {
                String token = tokens[i];
                String[] values = token.split(";");

                Score score = new Score();
                score.setName(values[0].substring(1));
                score.setType(values[0].charAt(0) == 'p' ? Score.TYPE_PLAYER : Score.TYPE_TEAM);
                score.setScore(Long.parseLong(values[1]));

                scores.add(score);
            }
            winlist.setScores(scores);

            m = winlist;
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
        else if (m instanceof CommandMessage)       { return translate((CommandMessage) m); }
        else if (m instanceof ClientInfoMessage)    { return translate((ClientInfoMessage) m); }
        else
        {
            return null;
        }
    }

    public String translate(SpecialMessage m)
    {
        if (m instanceof LinesAddedMessage)          { return translate((LinesAddedMessage) m); }
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
        StringBuilder message = new StringBuilder();
        message.append("pline ");
        message.append(m.getSlot());
        message.append(" ");
        message.append(applyStyle(m.getText(locale)));
        return message.toString();
    }

    public String translate(PlineActMessage m, Locale locale)
    {
        StringBuilder message = new StringBuilder();
        message.append("plineact ");
        message.append(m.getSlot());
        message.append(" ");
        message.append(m.getText(locale));
        return message.toString();
    }

    public String translate(CommandMessage m)
    {
        StringBuilder message = new StringBuilder();
        message.append("pline ");
        message.append(m.getSlot());
        message.append(" /");
        message.append(m.getCommand());

        for (int i = 0; i < m.getParameterCount(); i++)
        {
            message.append(" ");
            message.append(m.getParameter(i));
        }
        return message.toString();
    }

    public String translate(TeamMessage m)
    {
        StringBuilder message = new StringBuilder();
        message.append("team ");
        message.append(m.getSlot());
        message.append(" ");
        if (m.getName() != null)
        {
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
            StringBuilder message = new StringBuilder();
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
            StringBuilder message = new StringBuilder();
            message.append("playerleave ");
            message.append(m.getSlot());
            return message.toString();
        }
    }

    public String translate(PlayerNumMessage m)
    {
        StringBuilder message = new StringBuilder();
        message.append("playernum ");
        message.append(m.getSlot());
        return message.toString();
    }

    public String translate(StartGameMessage m)
    {
        StringBuilder message = new StringBuilder();
        message.append("startgame 1 ");
        message.append(m.getSlot());
        return message.toString();
    }

    public String translate(StopGameMessage m)
    {
        StringBuilder message = new StringBuilder();
        message.append("startgame 0 ");
        message.append(m.getSlot());
        return message.toString();
    }

    public String translate(NewGameMessage m)
    {
        Settings s = m.getSettings();
        StringBuilder message = new StringBuilder();
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
                message.append(Integer.toString(block.ordinal() + 1));
            }
        }

        message.append(" ");

        // specials frequency
        for (Special special : Special.values())
        {
            for (int j = 0; j < s.getOccurancy(special); j++)
            {
                message.append(Integer.toString(special.ordinal() + 1));
            }
        }

        message.append(" ");
        message.append(s.getAverageLevels() ? "1" : "0");
        message.append(" ");
        message.append(s.getClassicRules() ? "1" : "0");

        // extended parameter for 1.14 clients
        if (s.getSameBlocks())
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
        StringBuilder message = new StringBuilder();
        message.append("gmsg ");
        message.append(stripStyle(m.getText(locale)));
        return message.toString();
    }

    public String translate(LevelMessage m)
    {
        StringBuilder message = new StringBuilder();
        message.append("lvl ");
        message.append(m.getSlot());
        message.append(" ");
        message.append(m.getLevel());
        return message.toString();
    }

    public String translate(FieldMessage m)
    {
        StringBuilder message = new StringBuilder();
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
        StringBuilder message = new StringBuilder();
        message.append("playerlost ");
        message.append(m.getSlot());
        return message.toString();
    }

    public String translate(PlayerWonMessage m)
    {
        StringBuilder message = new StringBuilder();
        message.append("playerwon ");
        message.append(m.getSlot());
        return message.toString();
    }

    public String translate(NoConnectingMessage m)
    {
        StringBuilder message = new StringBuilder();
        message.append("noconnecting ");
        message.append(m.getText());
        return message.toString();
    }

    public String translate(LinesAddedMessage m)
    {
        StringBuilder message = new StringBuilder();
        message.append("sb ");
        message.append(m.getSlot());
        message.append(" cs").append(m.getLinesAdded()).append(" ");
        message.append(m.getFromSlot());
        return message.toString();
    }

    public String translate(AddLineMessage m)
    {
        StringBuilder message = new StringBuilder();
        message.append("sb ");
        message.append(m.getSlot());
        message.append(" a ");
        message.append(m.getFromSlot());
        return message.toString();
    }

    public String translate(ClearLineMessage m)
    {
        StringBuilder message = new StringBuilder();
        message.append("sb ");
        message.append(m.getSlot());
        message.append(" c ");
        message.append(m.getFromSlot());
        return message.toString();
    }

    public String translate(NukeFieldMessage m)
    {
        StringBuilder message = new StringBuilder();
        message.append("sb ");
        message.append(m.getSlot());
        message.append(" n ");
        message.append(m.getFromSlot());
        return message.toString();
    }

    public String translate(RandomClearMessage m)
    {
        StringBuilder message = new StringBuilder();
        message.append("sb ");
        message.append(m.getSlot());
        message.append(" r ");
        message.append(m.getFromSlot());
        return message.toString();
    }

    public String translate(SwitchFieldsMessage m)
    {
        StringBuilder message = new StringBuilder();
        message.append("sb ");
        message.append(m.getSlot());
        message.append(" s ");
        message.append(m.getFromSlot());
        return message.toString();
    }

    public String translate(ClearSpecialsMessage m)
    {
        StringBuilder message = new StringBuilder();
        message.append("sb ");
        message.append(m.getSlot());
        message.append(" b ");
        message.append(m.getFromSlot());
        return message.toString();
    }

    public String translate(GravityMessage m)
    {
        StringBuilder message = new StringBuilder();
        message.append("sb ");
        message.append(m.getSlot());
        message.append(" g ");
        message.append(m.getFromSlot());
        return message.toString();
    }

    public String translate(BlockQuakeMessage m)
    {
        StringBuilder message = new StringBuilder();
        message.append("sb ");
        message.append(m.getSlot());
        message.append(" q ");
        message.append(m.getFromSlot());
        return message.toString();
    }

    public String translate(BlockBombMessage m)
    {
        StringBuilder message = new StringBuilder();
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
        StringBuilder message = new StringBuilder();
        String name = ((Client) m.getSource()).getUser().getName();

        PlineMessage pline = new PlineMessage();
        pline.setKey("channel.spectator.message", name, m.getText());
        message.append(translate(pline, locale));

        return message.toString();
    }

    public String translate(WinlistMessage m, Locale locale)
    {
        StringBuilder message = new StringBuilder();
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

    public String translate(ClientInfoMessage m)
    {
        StringBuilder message = new StringBuilder();
        message.append("clientinfo ");
        message.append(m.getName());
        message.append(" ");
        message.append(m.getVersion());
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

    /**
     * Removes the style tags from the specified text.
     */
    public String stripStyle(String text)
    {
        Map<String, String> styles = getStyles();
        if (styles == null) {
            return text;
        }

        for (String key : styles.keySet())
        {
            text = text.replaceAll("<" + key + ">", "");
            text = text.replaceAll("</" + key + ">", "");
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
            throw new IllegalArgumentException("Invalid initialization string, the length is not even (" + initString + ")");
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
            throw new IllegalArgumentException("Invalid initialization string, illegal characters found (" + initString + ")", e);
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
            throw new IllegalArgumentException("Invalid initialization string, unable to find the pattern (" + initString + ")");
        }

        // decode the string
        StringBuilder s = new StringBuilder();

        for (int i = 1; i < dec.length; i++)
        {
            s.append((char) (((dec[i] ^ pattern.charAt((i - 1) % pattern.length())) + 255 - dec[i - 1]) % 255));
        }

        return s.toString().replace((char) 0, (char) 255);
    }

    private static String findHashPattern(int[] dec, boolean tetrifast)
    {
        // the first characters from the decoded string
        char[] data = (tetrifast ? TetrifastProtocol.INIT_TOKEN : INIT_TOKEN).substring(0, 10).toCharArray();

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
        int p = 54 * (ip[0] & 0xFF) + 41 * (ip[1] & 0xFF) + 29 * (ip[2] & 0xFF) + 17 * (ip[3] & 0xFF);
        char[] pattern = String.valueOf(p).toCharArray();

        // build the string to encode
        char[] data = ((tetrifast ? TetrifastProtocol.INIT_TOKEN : INIT_TOKEN) + " " + nickname + " " + version).toCharArray();

        // build the encoded string
        StringBuilder result = new StringBuilder();
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

}
