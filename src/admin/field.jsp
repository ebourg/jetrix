<%@ page import="net.jetrix.*,
                 net.jetrix.messages.*"%>
<%
    String name = request.getParameter("name");
    int num = Integer.parseInt(request.getParameter("num"));

    ChannelManager channelManager = ChannelManager.getInstance();
    Channel channel = channelManager.getChannel(name);

    Field field = channel.getField(num);
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link rel="stylesheet" href="style.css">
  <link rel="shortcut icon" href="http://jetrix.sourceforge.net/images/favicon.gif">
  <title>JetriX Admin - Field <%= num + 1 %> in #<%= name %></title>
</head>
<body style="background-image: url(images/tiny/background.png); background-repeat: no-repeat; margin: 0">

<%  if (field != null) { %>
<table cellspacing="0" cellpadding="0">
<%      for (int j = Field.HEIGHT - 1; j >= 0; j--) { %>
  <tr>
<%          for (int i = 0; i < Field.WIDTH; i++) { %>
    <td><img src="images/tiny/<%= (char) field.getBlock(i, j) %>.png" width="8" height="8" alt="<%= (char) field.getBlock(i, j) %>"></td>
<%          } %>
  </tr>
<%      } %>
</table>
<%  } %>

</body>
</html>
