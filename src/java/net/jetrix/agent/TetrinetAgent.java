/**
 * Jetrix TetriNET Server
 * Copyright (C) 2008  Emmanuel Bourg
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

package net.jetrix.agent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.Locale;

import net.jetrix.Message;
import net.jetrix.Protocol;
import net.jetrix.protocols.TetrinetProtocol;
import net.jetrix.messages.*;
import net.jetrix.messages.channel.*;
import net.jetrix.messages.channel.specials.*;

/**
 * Tetrinet agent to log on a server as a player.
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TetrinetAgent implements Agent
{
    private String name;
    private String hostname;

    private int slot;

    private Socket socket;
    private BufferedReader in;
    private Writer out;
    
    protected Protocol protocol = new TetrinetProtocol();
    private boolean running;

    public TetrinetAgent(String name)
    {
        this.name = name;
    }

    public String getHostname()
    {
        return hostname;
    }

    public void connect(String hostname) throws IOException
    {
        connect(hostname, 31457, "1.13");
    }

    protected void connect(String hostname, int port, String version) throws IOException
    {
        if (running)
        {
            return;
        }

        this.hostname = hostname;

        socket = new Socket(hostname, port);
        socket.setSoTimeout(15000);
        
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "ISO-8859-1"));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "ISO-8859-1"));

        send(TetrinetProtocol.encode(name, version, socket.getInetAddress().getAddress(), false));

        // block until the playernum message is received
        String line = protocol.readLine(in);
        Message message = protocol.getMessage(line);
        receive(message);

        if (message instanceof NoConnectingMessage)
        {
            throw new IOException("Connexion rejected (noconnecting) : " + ((NoConnectingMessage) message).getText());
        }

        running = true;
        
        socket.setSoTimeout(0);

        // start the message listener
        new MessageListener().start();
    }

    private class MessageListener extends Thread
    {
        private MessageListener()
        {
            setName("listener");
        }

        public void run()
        {
            try
            {
                while (running)
                {
                    String line = protocol.readLine(in);

                    Message message = protocol.getMessage(line);

                    receive(message);
                }
            }
            catch (IOException e)
            {
                if (running)
                {
                    e.printStackTrace();
                    running = false;
                }
            }
        }
    }

    public void disconnect() throws IOException
    {
        if (socket != null)
        {
            running = false;
            socket.close();
        }
    }

    protected void send(String message) throws IOException
    {
        out.write(message);
        out.write(protocol.getEOL());
        out.flush();
    }

    public void send(Message message) throws IOException
    {
        // set the slot for channel messages
        if (message instanceof ChannelMessage)
        {
            ((ChannelMessage) message).setSlot(slot);
        }

        send(protocol.translate(message, Locale.getDefault()));
    }

    /**
     * Join the specified channel
     * 
     * @param channel
     */
    public void join(String channel) throws IOException
    {
        send(new PlineMessage("/join " + channel));
    }

    public final void receive(Message m)
    {
        // overwritable pre processing
        onMessage(m);

        // message dispatching
        if (m instanceof SpecialMessage)           { onMessage((SpecialMessage) m); }
        else if (m instanceof FieldMessage)        { onMessage((FieldMessage) m); }
        else if (m instanceof CommandMessage)      { onMessage((CommandMessage) m); }
        else if (m instanceof PlineMessage)        { onMessage((PlineMessage) m); }
        else if (m instanceof LevelMessage)        { onMessage((LevelMessage) m); }
        else if (m instanceof PlayerLostMessage)   { onMessage((PlayerLostMessage) m); }
        else if (m instanceof PlineActMessage)     { onMessage((PlineActMessage) m); }
        else if (m instanceof TeamMessage)         { onMessage((TeamMessage) m); }
        else if (m instanceof JoinMessage)         { onMessage((JoinMessage) m); }
        else if (m instanceof LeaveMessage)        { onMessage((LeaveMessage) m); }
        else if (m instanceof PlayerNumMessage)    { onMessage((PlayerNumMessage) m); }
        else if (m instanceof StartGameMessage)    { onMessage((StartGameMessage) m); }
        else if (m instanceof StopGameMessage)     { onMessage((StopGameMessage) m); }
        else if (m instanceof NewGameMessage)      { onMessage((NewGameMessage) m); }
        else if (m instanceof EndGameMessage)      { onMessage((EndGameMessage) m); }
        else if (m instanceof PauseMessage)        { onMessage((PauseMessage) m); }
        else if (m instanceof ResumeMessage)       { onMessage((ResumeMessage) m); }
        else if (m instanceof GmsgMessage)         { onMessage((GmsgMessage) m); }
        else if (m instanceof PlayerWonMessage)    { onMessage((PlayerWonMessage) m); }
        else if (m instanceof NoConnectingMessage) { onMessage((NoConnectingMessage) m); }
        else
        {
            // nothing, log an error ?
        }

        // todo add onMessage(DisconnectedMessage)
    }

    /**
     * Message pre-processing. This method is called at the beginning of the
     * <tt>process(Message m, List out)</tt> method and allow custom
     * processing for all filtered messages.
     */
    public void onMessage(Message m) { }

    public void onMessage(PlineMessage m) { }

    public void onMessage(PlineActMessage m) { }

    public void onMessage(NoConnectingMessage m) { }

    public void onMessage(TeamMessage m) { }

    public void onMessage(JoinMessage m) { }

    public void onMessage(LeaveMessage m) { }

    public void onMessage(PlayerNumMessage m)
    {
        this.slot = m.getSlot();
    }

    public void onMessage(StartGameMessage m) { }

    public void onMessage(StopGameMessage m) { }

    public void onMessage(NewGameMessage m) { }

    public void onMessage(EndGameMessage m) { }

    public void onMessage(PauseMessage m) { }

    public void onMessage(ResumeMessage m) { }

    public void onMessage(GmsgMessage m) { }

    private void onMessage(SpecialMessage m)
    {
        // message pre-processing
        onSpecial(m);

        // message dispatching
        if (m instanceof LinesAddedMessage)        { onMessage((LinesAddedMessage) m); }
        else if (m instanceof AddLineMessage)        { onMessage((AddLineMessage) m); }
        else if (m instanceof ClearLineMessage)      { onMessage((ClearLineMessage) m); }
        else if (m instanceof ClearSpecialsMessage)  { onMessage((ClearSpecialsMessage) m); }
        else if (m instanceof RandomClearMessage)    { onMessage((RandomClearMessage) m); }
        else if (m instanceof BlockQuakeMessage)     { onMessage((BlockQuakeMessage) m); }
        else if (m instanceof BlockBombMessage)      { onMessage((BlockBombMessage) m); }
        else if (m instanceof GravityMessage)        { onMessage((GravityMessage) m); }
        else if (m instanceof NukeFieldMessage)      { onMessage((NukeFieldMessage) m); }
        else if (m instanceof SwitchFieldsMessage)   { onMessage((SwitchFieldsMessage) m); }
    }

    /**
     * Special block message pre-processing. This method is called for all
     * specials filtered and allow custom processing for all specials
     * (lines added, blockbomb switchs, etc...).
     */
    public void onSpecial(SpecialMessage m) { }

    public void onMessage(LevelMessage m) { }

    public void onMessage(FieldMessage m) { }

    public void onMessage(PlayerLostMessage m) { }

    public void onMessage(PlayerWonMessage m) { }

    public void onMessage(CommandMessage m) { }

    public void onMessage(LinesAddedMessage m)
    {
        if (m instanceof OneLineAddedMessage)        { onMessage((OneLineAddedMessage) m); }
        else if (m instanceof TwoLinesAddedMessage)  { onMessage((TwoLinesAddedMessage) m); }
        else if (m instanceof FourLinesAddedMessage) { onMessage((FourLinesAddedMessage) m); }
    }

    public void onMessage(OneLineAddedMessage m) { }

    public void onMessage(TwoLinesAddedMessage m) { }

    public void onMessage(FourLinesAddedMessage m) { }

    public void onMessage(AddLineMessage m) { }

    public void onMessage(ClearLineMessage m) { }

    public void onMessage(NukeFieldMessage m) { }

    public void onMessage(RandomClearMessage m) { }

    public void onMessage(SwitchFieldsMessage m) { }

    public void onMessage(ClearSpecialsMessage m) { }

    public void onMessage(GravityMessage m) { }

    public void onMessage(BlockQuakeMessage m) { }

    public void onMessage(BlockBombMessage m) { }

}
