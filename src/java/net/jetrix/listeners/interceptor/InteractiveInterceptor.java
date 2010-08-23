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
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jetrix.Client;
import net.jetrix.Destination;
import net.jetrix.Message;
import net.jetrix.clients.QueryClient;
import net.jetrix.clients.TetrinetClient;
import net.jetrix.messages.channel.PlayerNumMessage;

/**
 * Interceptor expecting a response from the client (a password, a speed check, etc).
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 * @since 0.3
 */
public abstract class InteractiveInterceptor implements ClientInterceptor, Destination
{
    protected Logger log = Logger.getLogger("net.jetrix");
    
    private BlockingQueue<Boolean> queue = new ArrayBlockingQueue<Boolean>(1);
    
    private boolean running;
    
    public void process(Client client) throws ClientValidationException
    {
        if (client instanceof QueryClient)
        {
            return;
        }
        
        // adjust the read timeout for the client
        if (client instanceof TetrinetClient)
        {
            TetrinetClient c = (TetrinetClient) client;
            try
            {
                c.getSocket().setSoTimeout(2 * getTimeout() * 1000);
            }
            catch (SocketException e)
            {
                e.printStackTrace();
            }
        }
        
        // start listening the messages sent by the client
        MessageReader messageReader = new MessageReader(client);
        messageReader.start();
        
        // initiates the dialogue
        client.send(new PlayerNumMessage(1));
        prologue(client);
        
        // wait until the client is processed
        try
        {
            Boolean accepted = queue.poll(getTimeout(), TimeUnit.SECONDS);
            if ((accepted == null || accepted == Boolean.FALSE) && isValidating())
            {
                log.info("Rejecting " + client);
                client.disconnect();
                throw new ClientValidationException();
            }
            else
            {
                log.info("Accepting: " + client);
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();            
        }
    }

    /**
     * Returns the time to wait for a response from the client.
     */
    protected abstract int getTimeout();

    /**
     * Initiates the interaction with the client (i.e. display a message)
     */
    protected abstract void prologue(Client client);

    /**
     * Process a message received from the client.
     * 
     * @param message
     */
    public abstract void send(Message message);

    
    protected void accept()
    {
        try
        {
            running = false;
            queue.put(Boolean.TRUE);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    protected void reject()
    {
        try
        {
            running = false;
            queue.put(Boolean.FALSE);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private class MessageReader extends Thread
    {
        private Client client;

        private MessageReader(Client client)
        {
            setDaemon(true);
            this.client = client;
        }

        public void run()
        {
            running = true;
            
            try
            {
                log.finer("Message reader started (" + this + ")");
                Message message;
                while (running && (message = client.receive()) != null)
                {
                    send(message);
                }
            }
            catch (IOException e)
            {
                log.log(Level.WARNING, "Error reading client message during the interception", e);
            }
            finally
            {
                log.finer("Message reader stopped (" + this + ")");
            }
        }
    }
}
