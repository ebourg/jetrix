/**
 * Jetrix TetriNET Server
 * Copyright (C) 2010  Emmanuel Bourg
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

package net.jetrix.listeners.interceptor;

import java.io.IOException;
import java.util.logging.Level;

import net.jetrix.Client;
import net.jetrix.Field;
import net.jetrix.Message;
import net.jetrix.Server;
import net.jetrix.config.Block;
import net.jetrix.config.ServerConfig;
import net.jetrix.config.Settings;
import net.jetrix.messages.channel.EndGameMessage;
import net.jetrix.messages.channel.FieldMessage;
import net.jetrix.messages.channel.GmsgMessage;
import net.jetrix.messages.channel.NewGameMessage;
import net.jetrix.messages.channel.PlayerLostMessage;

/**
 * Interceptor checking the delay between two successive pieces.
 *  
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 * @since 0.3
 */
public class SpeedCheckInterceptor extends InteractiveInterceptor
{
    /** The time the speed check was started */
    private long startTime;

    /** The cumulated delay between the piece drops (~83000 expected for a standard client, 17500 for a tetrifast client) */
    private long sum = -1;

    protected Field getField()
    {
        Field field = new Field();
        
        try
        {
            field.load("data/speedcheck.field");
        }
        catch (IOException e)
        {
            log.log(Level.WARNING, "Unable to load the speedcheck field", e);
        }
        
        return field;
    }

    public void process(Client client) throws ClientValidationException
    {
        ServerConfig config = Server.getInstance().getConfig();
        boolean enabled = "true".equals(config.getProperty("speedcheck.enabled"));

        // skip the test for tetrifast clients
        if (enabled && !"tetrifast".equals(client.getProtocol().getName()))
        {
            super.process(client);
        }
    }

    protected void prologue(Client client)
    {
        // start the game at high speed with squares only
        Settings settings = new Settings();
        settings.setOccurancy(Block.SQUARE, 100);
        settings.setStartingLevel(99);
        settings.setAverageLevels(false);
        
        client.send(new NewGameMessage(settings));
        
        FieldMessage fieldmessage = new FieldMessage(getField().getFieldString());
        fieldmessage.setSlot(1);
        client.send(fieldmessage);
        
        client.send(new GmsgMessage("*** Please wait, the server is checking your client..."));
        client.send(new GmsgMessage("*** Do not move, rotate or drop pieces."));
    }

    public void send(Message message)
    {
        Client client = (Client) message.getSource();
        
        if (message instanceof PlayerLostMessage)
        {
            // stop the game
            message.getSource().send(new EndGameMessage());
            
            if (isFast())
            {
                log.warning("Speed check failed for " + client);
                reject();
            }
            else
            {
                accept();
            }
        }
        else if (message instanceof FieldMessage)
        {
            FieldMessage msg = (FieldMessage) message;
            
            long now = System.currentTimeMillis();
            
            if (sum == -1 && isFirstPiece(msg))
            {
                // start the timer after the first drop
                startTime = now;
                sum = 0L;
            }
            
            if (sum >= 0)
            {
                sum += (now - startTime);
            }
        }
    }

    protected boolean isFirstPiece(FieldMessage msg)
    {
        Field field = new Field();
        field.update(msg);
        
        return field.getBlock(5, 0) == Field.BLOCK_YELLOW;
    }

    protected boolean isFast()
    {
        // the client must not be more than 10% faster        
        return sum < 83000 * 0.9;
    }

    protected int getTimeout()
    {
        return 30;
    }

    public boolean isValidating()
    {
        return true;
    }
}
