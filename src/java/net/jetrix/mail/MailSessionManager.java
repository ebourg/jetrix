/**
 * Jetrix TetriNET Server
 * Copyright (C) 2009  Emmanuel Bourg
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

package net.jetrix.mail;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;

import net.jetrix.config.MailSessionConfig;
import org.apache.commons.lang.StringUtils;

/**
 * Singleton holding the session to send mails.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 * @since 0.3
 */
public final class MailSessionManager
{
    private Logger log = Logger.getLogger(getClass().getName());

    private static MailSessionManager instance = new MailSessionManager();

    private Session session;

    private MailSessionManager()
    {
    }

    public static MailSessionManager getInstance()
    {
        return instance;
    }

    public Session getSession()
    {
        return session;
    }

    /**
     * Initialize the mail session from the specified configuration.
     */
    public void setConfiguration(final MailSessionConfig config)
    {        
        try
        {
            if (!StringUtils.isBlank(config.getHostname()))
            {
                Properties props = new Properties();
                props.setProperty("mail.transport.protocol", "smtp");
                props.setProperty("mail.smtp.host", config.getHostname());
                props.setProperty("mail.smtp.port", String.valueOf(config.getPort()));
                props.setProperty("mail.smtp.auth", String.valueOf(config.isAuth()));

                session = Session.getInstance(props, new Authenticator()
                {
                    protected PasswordAuthentication getPasswordAuthentication()
                    {
                        return new PasswordAuthentication(config.getUsername(), config.getPassword());
                    }
                });

                // enable the debug mode if requested
                if (config.isDebug())
                {
                    session.setDebug(true);
                }
            }
            else
            {
                log.warning("Unable to initialize the mail session, the hostname is missing");
            }
        }
        catch (Exception e)
        {
            log.log(Level.SEVERE, "Unable to initialize the mail session", e);
        }
    }

    /**
     * Check if the mail session is working properly.
     */
    public boolean checkSession()
    {
        boolean check = false;

        if (session != null)
        {
            try
            {
                Transport transport = session.getTransport();
                transport.connect();
                transport.close();

                check = true;
            }
            catch (MessagingException e)
            {
                log.warning("Unable to validate the mail session (" + e.getMessage() + ")");
            }
        }

        return check;
    }
}
