<?xml version="1.0"?>
<%@ page import="net.jetrix.*"%>
<%@ page import="java.util.*"%>
<%@ page contentType="text/xml" %>

<%
    ChannelManager channelManager = ChannelManager.getInstance();
    Iterator channels = channelManager.channels();    
%>

<tree>
<%  while (channels.hasNext()) { 
        String channelName = ((Channel) channels.next()).getConfig().getName(); %>
   <tree text="<%= channelName %>" 
         action="javascript:loadPage('channel.jsp?name=<%= channelName %>')" 
         src="navigationUsers.jsp?name=<%= channelName %>"
         icon="javascript/xloadtree/images/xp/folder.png" />
<%  } %>
</tree>
