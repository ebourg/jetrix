<?xml version="1.0"?>
<%@ page import="net.jetrix.*"%>
<%@ page import="java.util.*"%>
<%@ page contentType="text/xml" %>

<tree>
<%  for (Channel channel : ChannelManager.getInstance().channels()) {
        String channelName = channel.getConfig().getName(); %>
   <tree text="<%= channelName %>" 
         action="javascript:loadPage('channel.jsp?name=<%= channelName %>')" 
         src="navigationUsers.jsp?name=<%= channelName %>"
         icon="javascript/xloadtree/images/xp/folder.png" />
<%  } %>
</tree>
