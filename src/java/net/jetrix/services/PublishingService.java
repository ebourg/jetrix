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

package net.jetrix.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jetrix.Server;

/**
 * A service publishing the address of the server on public server lists
 * (tetrinet.org, tfast.org and tsrv.com). The <tt>host</tt> parameter can be
 * specified if not explicitely defined in the config.xml file (with the host
 * attribute on the tetrinet-server element), if no hostname is provided the
 * service will try to guess the address automatically. The name of the server
 * is used as the description. The address is published once a day by default.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 * @since 0.2
 */
public class PublishingService extends ScheduledService
{

    private Logger log = Logger.getLogger("net.jetrix");

    private String host;

    public PublishingService()
    {
        setDelay(1000);
        setPeriod(24 * 3600 * 1000);
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public String getHost()
    {
        return host;
    }

    public String getName()
    {
        return "Publishing service";
    }

    protected void run()
    {
        // get the server address
        String host = getPublishedAddress();

        if (host == null)
        {
            log.warning("The server address cannot be published, please specify the hostname in the server configuration.");
            return;
        }

        log.info("Publishing server address to online directories... (" + host + ")");

        // publishing to tfast.org
        try
        {
            String url = "http://www.tfast.org/en/add.php";
            Map<String, String> params = new HashMap<String, String>();
            params.put("ip", host);
            params.put("desc", Server.getInstance().getConfig().getName());
            post(url, params);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // publishing to tetrinet.org
        try
        {
            String url = "http://slummy.tetrinet.org/grav/slummy_addsvr.pl";
            Map<String, String> params = new HashMap<String, String>();
            params.put("qname", host);
            params.put("submit_bt", "Add Server");
            post(url, params);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // publishing to tsrv.com
        try
        {
            String url = "http://dieterdhoker.mine.nu:8280/cgi-bin/TSRV/submitserver.pl";
            Map<String, String> params = new HashMap<String, String>();
            params.put("hostname", host);
            params.put("description", Server.getInstance().getConfig().getName());
            post(url, params);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Send a POST http request to the specified URL.
     *
     * @param url        the URL
     * @param parameters the parameters of the request
     */
    protected void post(String url, Map<String, String> parameters) throws IOException
    {
        if (log.isLoggable(Level.FINE))
        {
            log.fine("posting to: " + url);
        }

        URL location = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) location.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");

        // prepare the request body
        StringBuffer params = new StringBuffer();
        Iterator<String> keys = parameters.keySet().iterator();
        while (keys.hasNext())
        {
            String key = keys.next();
            params.append(key);
            params.append("=");
            params.append(parameters.get(key));
            if (keys.hasNext())
            {
                params.append("&");
            }
        }

        // prepare the request header
        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.addRequestProperty("Content-Length", String.valueOf(params.length()));

        // write the request body
        Writer out = new OutputStreamWriter(conn.getOutputStream());
        out.write(params.toString());
        out.flush();
        out.close();

        try
        {
            // send the request
            conn.connect();

            if (log.isLoggable(Level.FINE))
            {
                log.fine("Response: " + conn.getResponseCode() + " - " + conn.getResponseMessage());

                // read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;
                while ((line = in.readLine()) != null)
                {
                    log.fine(line);
                }

                in.close();
            }

        }
        finally
        {
            conn.disconnect();
        }
    }

    /**
     * Find the address to publish by looking at the service host parameter, the
     * server host attribute in the config.xml file, and the IP associated to
     * the network interfaces.
     */
    protected String getPublishedAddress()
    {
        // try the service host parameter
        if (host != null)
        {
            return host;
        }

        // try the server host parameter
        InetAddress address = Server.getInstance().getConfig().getHost();
        if (address != null)
        {
            return address.getHostName();
        }

        // search through the network interfaces
        address = findInetAddress();
        if (address != null)
        {
            return address.getHostName();
        }

        return null;
    }

    /**
     * Search through the available network interfaces and return the first
     * global internet address found.
     */
    protected InetAddress findInetAddress()
    {
        InetAddress address = null;

        try
        {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements() && address == null)
            {
                NetworkInterface network = interfaces.nextElement();

                Enumeration<InetAddress> addresses = network.getInetAddresses();
                while (addresses.hasMoreElements() && address == null)
                {
                    InetAddress addr = addresses.nextElement();
                    if (!addr.isLoopbackAddress()
                            && !addr.isLinkLocalAddress()
                            && !addr.isSiteLocalAddress())
                    {
                        address = addr;
                    }
                }
            }
        }
        catch (SocketException e)
        {
            log.log(Level.WARNING, e.getMessage(), e);
        }

        return address;
    }

}
