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

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * A simple mail message. This class intends to abstract the complexity
 * of the JavaMail API for simple usages. It relies on the MailSessionManager
 * to provide a valid mail session.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 * @since 0.3
 */
public class MailMessage
{
    private static final String DEFAULT_CHARSET = "iso-8859-1";

    private CharSequence subject;
    private CharSequence body;

    private InternetAddress from;

    private Set<InternetAddress> recipients = new HashSet<InternetAddress>();
    private Set<InternetAddress> recipientsCC = new HashSet<InternetAddress>();
    private Set<InternetAddress> recipientsBCC = new HashSet<InternetAddress>();

    private Set<DataSource> attachments = new HashSet<DataSource>();

    public MailMessage()
    {
    }

    public void setSubject(CharSequence subject)
    {
        this.subject = subject;
    }

    public void setBody(CharSequence body)
    {
        this.body = body;
    }

    public void setFrom(InternetAddress from)
    {
        this.from = from;
    }

    public void setFrom(String from) throws AddressException
    {
        this.from = new InternetAddress(from, false);
    }

    public void addRecipient(String address) throws AddressException
    {
        recipients.add(new InternetAddress(address));
    }

    public void addRecipientCC(String address) throws AddressException
    {
        recipientsCC.add(new InternetAddress(address));
    }

    public void addRecipientBCC(String address) throws AddressException
    {
        recipientsBCC.add(new InternetAddress(address));
    }

    public void addRecipient(InternetAddress address)
    {
        recipients.add(address);
    }

    public void addRecipientCC(InternetAddress address)
    {
        recipientsCC.add(address);
    }

    public void addRecipientBCC(InternetAddress address)
    {
        recipientsBCC.add(address);
    }

    public void addAttachment(File file)
    {
        attachments.add(new FileDataSource(file));
    }

    public void addAttachment(DataSource datasource)
    {
        attachments.add(datasource);
    }

    /**
     * Send the message, and if requested, asynchronously in a separate thread.
     *
     * @param asynchronous send the message in a separate thread
     */
    public void send(boolean asynchronous) throws MessagingException
    {
        if (!asynchronous)
        {
            send();
        }
        else
        {
            new Thread("MailMessage.send()")
            {
                public void run()
                {
                    try
                    {
                        send();
                    }
                    catch (MessagingException e)
                    {
                        throw new RuntimeException("An error occured when sending the mail", e);
                    }
                }
            }.start();
        }
    }

    /**
     * Send the message.
     */
    public void send() throws MessagingException
    {
        Session session = MailSessionManager.getInstance().getSession();

        // build the message
        MimeMessage message = new MimeMessage(session);
        message.setSubject(subject.toString(), DEFAULT_CHARSET);
        message.setFrom(from);

        message.addRecipients(Message.RecipientType.TO, recipients.toArray(new Address[recipients.size()]));
        message.addRecipients(Message.RecipientType.CC, recipientsCC.toArray(new Address[recipientsCC.size()]));
        message.addRecipients(Message.RecipientType.BCC, recipientsBCC.toArray(new Address[recipientsBCC.size()]));

        message.setHeader("Content-Transfer-Encoding", "8bit");
        message.setSentDate(new Date());

        MimeBodyPart part1 = new MimeBodyPart();
        if (body.toString().toLowerCase().trim().startsWith("<html") || body.toString().toLowerCase().trim().startsWith("<!doctype html"))
        {
            part1.setContent(body.toString(), "text/html; charset=" + DEFAULT_CHARSET);
        }
        else
        {
            part1.setText(body.toString(), DEFAULT_CHARSET);
        }
        part1.setHeader("Content-Transfer-Encoding", "8bit");

        Multipart content = new MimeMultipart();
        content.addBodyPart(part1);

        // add attachments
        for (DataSource attachment : attachments)
        {
            MimeBodyPart part2 = new MimeBodyPart();
            part2.setDataHandler(new DataHandler(attachment));
            part2.setFileName(attachment.getName());

            content.addBodyPart(part2);
        }

        message.setContent(content);
        message.saveChanges();

        // send the message
        Transport transport = session.getTransport();

        try
        {
            transport.connect();
            transport.sendMessage(message, message.getAllRecipients());
        }
        finally
        {
            if (transport.isConnected())
            {
                transport.close();
            }
        }
    }
}
