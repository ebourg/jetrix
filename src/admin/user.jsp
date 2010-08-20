<%@ page import="net.jetrix.*"%>

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
  <title>Jetrix Administration - User - <%= user.getName() %></title>
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
        <td>Team</td>
        <td><%= user.getTeam() != null ? user.getTeam() : "" %></td>
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
        <td width="30%">Agent</td>
        <td><%= client.getAgent() %> <%= client.getVersion() %></td>
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

    <form id="kick" action="/servlet/net.jetrix.servlets.UserAction" style="display: inline">
      <input type="hidden" name="action" value="kick">
      <input type="hidden" name="name" value="<%= user.getName() %>">
      <input type="submit" value="Kick">
    </form>

    <form id="ban" action="/servlet/net.jetrix.servlets.UserAction" style="display: inline">
      <input type="hidden" name="action" value="ban">
      <input type="hidden" name="name" value="<%= user.getName() %>">
      <input type="submit" value="Ban">
    </form>

  </div>
  <div class="tab-page" style="height: 400px">
    <h2 class="tab">Statistics</h2>

    <table class="thin" style="width: 400px">
      <tr>
        <td width="30%">Connection Time</td>
        <td><%= client.getConnectionTime() %></td>
      </tr>
      <tr>
        <td>Idle Time</td>
        <td><%= client.getIdleTime() / 1000 %> second<%= client.getIdleTime() > 2000 ? "s" : "" %></td>
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
