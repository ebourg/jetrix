<%@ page import="net.jetrix.*"%>
<%@ page import="java.util.*"%>

<%
    ClientRepository clientRepository = ClientRepository.getInstance();
    Client client = clientRepository.getClient(request.getParameter("name"));
    User user = client.getUser();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <script type="text/javascript" src="javascript/tabpane/js/tabpane.js"></script>
  <link type="text/css" rel="stylesheet" href="javascript/tabpane/css/luna/tab.css">
  <link type="text/css" rel="stylesheet" href="style.css">
  <title>JetriX Admin - User - <%= user.getName() %></title>
</head>
<body>

<div class="content" style="padding: 1em">

  <h1>User - <%= user.getName() %></h1>

<div class="tab-pane" id="tab-pane-3">

  <div class="tab-page" style="height: 400px">
    <h2 class="tab">Profile</h2>

    <h2>User</h2>
    
    <table class="thin" style="width: 500px">
      <tr>
        <td width="30%">Name</td>
        <td><%= user.getName() %></td>
      </tr>
      <tr>
        <td>Access Level</td>
        <td><%= user.getAccessLevel() %></td>
      </tr>
      <tr>
        <td>Channel</td>
        <td><a href="channel.jsp?name=<%= client.getChannel().getConfig().getName() %>"><%= client.getChannel().getConfig().getName() %></a></td>
      </tr>
    </table>

    <h2>Client</h2>
    
    <table class="thin" style="width: 500px">
      <tr>
        <td width="30%">Type</td>
        <td><%= client.getType() %></td>
      </tr>
      <tr>
        <td>Version</td>
        <td><%= client.getVersion() %></td>
      </tr>
      <tr>
        <td>Protocol</td>
        <td><%= client.getProtocol().getName() %></td>
      </tr>
      <tr>
        <td>IP</td>
        <td><%= client.getInetAddress().getHostName() %></td>
      </tr>
    </table>
    
    <br>
    
    <input type="button" value="Kick">
    <input type="button" value="Ban">
  
  </div>
  <div class="tab-page" style="height: 400px">
    <h2 class="tab">Statistics</h2>

    <table class="thin" style="width: 400px">
      <tr>
        <td width="30%">Connection Time</td>
        <td><%= client.getConnectionTime() %></td>
      </tr>
      <tr>
        <td>Games Played</td>
        <td>1234</td>
      </tr>
    </table>
  
  </div>
</div>

</div>

</body>
</html>
