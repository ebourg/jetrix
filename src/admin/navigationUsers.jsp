<?xml version="1.0"?>
<%@ page import="net.jetrix.*"%>
<%@ page import="java.util.*"%>
<%@ page contentType="text/xml" %>

<%
    ChannelManager channelManager = ChannelManager.getInstance();
    Channel channel = channelManager.getChannel(request.getParameter("name"));       
    
    Iterator clients = (channel != null) ? channel.getPlayers() : (new ArrayList()).iterator();
%>

<tree>
<%  while (clients.hasNext()) { 
        Client client = (Client) clients.next();
        if (client != null) {
            String userName = client.getUser().getName(); %>
   <tree text="<%= userName %>" action="javascript:loadPage('user.jsp?name=<%= userName %>')"/>
<%      } %>   
<%  } %>
</tree>
