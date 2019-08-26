/**
 * Jetrix TetriNET Server
 * Copyright (C) 2001-2003  Emmanuel Bourg
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

package net.jetrix.servlets;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Filter redirecting the requests on .jsp files to the precompiled servlets.
 *
 * @author Emmanuel Bourg
 */
public class PrecompiledPagesFilter implements Filter
{
    public void init(FilterConfig filterConfig) throws ServletException { }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        String requestURI = ((HttpServletRequest) request).getRequestURI();
        String pageName = requestURI.substring(requestURI.lastIndexOf("/") + 1, requestURI.indexOf("."));
        RequestDispatcher dispatcher = request.getRequestDispatcher("/servlet/org.apache.jsp." + pageName + "_jsp");
        dispatcher.forward(request, response);
    }

    public void destroy() { }

}
