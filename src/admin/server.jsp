<%@ page import="net.jetrix.*"%>
<%@ page import="net.jetrix.commands.*"%>
<%@ page import="net.jetrix.config.*"%>
<%@ page import="net.jetrix.filter.*"%>
<%@ page import="java.text.*"%>
<%@ page import="java.util.*"%>

<%
    Server server = Server.getInstance();
    ServerConfig conf = server.getConfig();
    request.setAttribute("settings", conf.getDefaultSettings());
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <script type="text/javascript" src="javascript/tabpane/js/tabpane.js"></script>
  <link type="text/css" rel="stylesheet" href="javascript/tabpane/css/luna/tab.css">
  <link type="text/css" rel="stylesheet" href="style.css">
  <title>JetriX Admin - Server</title>
</head>
<body>

<div class="content" style="padding: 1em">

<h1>Server</h1>

<div class="tab-pane" id="tab-pane-2">

  <div class="tab-page" style="height: 400px">
    <h2 class="tab">General</h2>

    <form id="general" action="/servlet/net.jetrix.servlets.ServerAction">
    <input type="hidden" name="action" value="general">

    <table class="thin" style="width: 600px">
      <tr>
        <td width="20%">Version</td>
        <td width="80%"><%= ServerConfig.VERSION %></td>
      </tr>
      <tr>
        <td>Host</td>
        <td><input class="thin" type="text" value="<%= conf.getHost() != null ? conf.getHost().toString() : "*" %>"></td>
      </tr>
      <tr>
        <td>Max Players</td>
        <td><input class="thin" type="text" name="maxPlayers" value="<%= conf.getMaxPlayers() %>"></td>
      </tr>
      <tr>
        <td>Max Connections</td>
        <td><input class="thin" type="text" name="maxConnections" value="<%= conf.getMaxConnections() %>"></td>
      </tr>
      <tr>
        <td>Operator Password</td>
        <td><input class="thin" type="text" name="opPassword" value="<%= conf.getOpPassword() != null ? conf.getOpPassword() : "" %>"></td>
      </tr>
      <tr>
        <td>Locale</td>
        <td>
<%  Iterator locales = Language.getLocales().iterator(); %>
          <select class="thin" name="locale">
<%  while (locales.hasNext()) {
        Locale locale = (Locale) locales.next(); %>
            <option value="<%= locale.getLanguage() %>" <%= conf.getLocale().equals(locale) ? "selected" : "" %>>
              <%= locale.getLanguage().toUpperCase() %> - <%= locale.getDisplayLanguage() %>
            </option>
<%  } %>
          </select>
        </td>
      </tr>
        <tr>
          <td>Status</td>
          <td>
            <label><input type="radio" value="opened" name="status"> Opened</label>
            <label><input type="radio" value="closed" name="status"> Closed</label>
          </td>
        </tr>
      <tr>
        <td valign="top">Message of the day</td>
        <td><textarea class="thin" name="motd" cols="20" rows="5" style="width: 100%"><%= conf.getMessageOfTheDay() %></textarea></td>
      </tr>
    </table>

    <br>

    <input type="submit" value="Save Changes">
    <input type="button" value="Shutdown">

    </form>

    <br>

  </div>
  <div class="tab-page" style="height: 400px">
    <h2 class="tab">Clients</h2>

<%
    ClientRepository clientRepository = ClientRepository.getInstance();
    Iterator clients = clientRepository.getClients();
%>    

    <table class="thin" style="width: 500px">
      <tr>
        <th>Name</th>
        <th>Type</th>
        <th>Version</th>
        <th>Protocol</th>
        <th>IP</th>
        <th>Access Level</th>
        <th>Channel</th>
      </tr>
<%  while (clients.hasNext()) { 
        Client client = (Client) clients.next();
        User user = client.getUser(); %>
      <tr>
        <td><a href="user.jsp?name=<%= user.getName() %>"><%= user.getName() %></a></td>
        <td align="center"><%= client.getType() %></td>
        <td align="center"><%= client.getVersion() %></td>
        <td align="center"><%= client.getProtocol().getName() %></td>
        <td><%= client.getInetAddress().getHostName() %></td>
        <td align="center"><%= user.getAccessLevel() %></td>
        <td><a href="channel.jsp?name=<%= client.getChannel().getConfig().getName() %>"><%= client.getChannel().getConfig().getName() %></a></td>
      </tr>
<%  } %>
    </table>

  </div>
  <div class="tab-page" style="height: 400px">
    <h2 class="tab">Settings</h2>

    <jsp:include page="/servlet/org.apache.jsp.settings_jsp"/>

  </div>
  <div class="tab-page" style="height: 400px">
    <h2 class="tab">Filters</h2>

<%  Iterator filters = conf.getGlobalFilters(); %>

    <h2>Filters</h2>

    <table class="thin" style="width: 500px">
      <tr>
        <th>Name</th>
        <th>Version</th>
        <th>Description</th>
        <th></th>
      </tr>
<%  while (filters.hasNext()) {
        net.jetrix.config.FilterConfig filterConfig = (net.jetrix.config.FilterConfig) filters.next();  %>
      <tr>
        <td><%= filterConfig.getName() %></td>
        <td><%= filterConfig.getClassname() %></td>
        <td></td>
        <td><input type="image" src="images/delete16.png" value="remove" alt="Remove" title="Remove"></td>
      </tr>
<%  } %>
    </table>

    list, add, remove

  </div>
  <div class="tab-page" style="height: 400px; overflow: auto">
    <h2 class="tab">Commands</h2>

<%
    CommandManager commandManager = CommandManager.getInstance();
    Iterator commands = commandManager.getCommands(100);
%>

    <table class="thin">
      <tr>
        <th>Aliases</th>
        <th>Usage</th>
        <th>Description</th>
        <th>Access Level</th>
        <th></th>
      </tr>
<%  while (commands.hasNext()) {
        Command command = (Command) commands.next();
        String usage = command.getUsage(conf.getLocale());
        usage = usage.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

        StringBuffer aliases = new StringBuffer();
        for (int i = 0; i < command.getAliases().length; i++) {
            if (i > 0) { aliases.append(", "); }
            aliases.append(command.getAliases()[i]);
        } %>
      <tr>
        <td><%= aliases %></td>
        <td><%= usage %></td>
        <td><%= command.getDescription(conf.getLocale()) %></td>
        <td><%= command.getAccessLevel() %></td>
        <td><input type="image" src="images/delete16.png" value="remove" alt="Remove" title="Remove"></td>
      </tr>
<%  } %>
    </table>

    list, add, remove

  </div>
  <div class="tab-page" style="height: 400px">
    <h2 class="tab">Ban List</h2>

    <script type="text/javascript">
    function unban(value) {
        var form = document.forms["banlist.remove"];
        form["pattern"].value = value;
        form.submit();
    }
    </script>

    <form id="banlist.remove" action="/servlet/net.jetrix.servlets.ServerAction">
      <input type="hidden" name="action" value="banlist.remove">
      <input type="hidden" name="pattern" value="">
    </form>

    <form id="banlist" action="/servlet/net.jetrix.servlets.ServerAction">
    <input type="hidden" name="action" value="banlist.add">

<%  Iterator banlist = Banlist.getInstance().getBanlist(); %>

    <table class="thin" style="width: 400px">
      <tr>
        <th>Pattern</th>
        <th>Expires</th>
        <th>Type</th>
        <th></th>
      </tr>
<%  while (banlist.hasNext()) {
        Banlist.Entry entry = (Banlist.Entry) banlist.next(); %>
      <tr>
        <td><%= entry.pattern %></td>
        <td><%= entry.expiration != null ? entry.expiration.toString() : "" %></td>
        <td width="50" align="center">Host</td>
        <td width="50" align="center">
          <a href="javascript: unban('<%= entry.pattern %>')"><img src="images/delete16.png" alt="Remove" title="Remove"></a>
        </td>
      </tr>
<%  } %>
      <tr>
        <td><input type="text" name="pattern" style="thin"></td>
        <td><input type="text" name="expires" style="thin"></td>
        <td width="50" align="center">
          <select name="type">
            <option value="0">Host</option>
            <option value="1">Nickname</option>
            <option value="2">Team</option>
          </select>
        </td>
        <td width="50"><input type="submit" value="Add"></td>
      </tr>
    </table>

    </form>

  </div>
  <div class="tab-page" style="height: 400px">
    <h2 class="tab">Listeners</h2>

<%  Iterator listeners = conf.getListeners(); %>

    <table class="thin" style="width: 400px">
      <tr>
        <th>Name</th>
        <th>Port</th>
        <th></th>
      </tr>
<%  while (listeners.hasNext()) {
        Listener listener = (Listener) listeners.next();  %>
      <tr>
        <td><%= listener.getName() %></td>
        <td width="50" align="center"><%= listener.getPort() %></td>
        <td width="50"><input type="button" value="Stop"></td>
      </tr>
<%  } %>
    </table>

    <br>

    <input type="button" value="Add">

  </div>
  <div class="tab-page" style="height: 400px">
    <h2 class="tab">Statistics</h2>

    <table class="thin" style="width: 400px">
      <tr>
        <td width="30%">Uptime</td>
        <td>
<%
    // todo store the start time somewhere
    long startTime = System.currentTimeMillis();
    long elaspsed = System.currentTimeMillis() - startTime;
    long days = elaspsed / 24 * 3600 * 1000;
%>
          <%= days %> day<%= days > 1 ? "s" : "" %>
        </td>
      </tr>
      <tr>
        <td>Number of Connections</td>
        <td>1234</td>
      </tr>
      <tr>
        <td>Games Played</td>
        <td>1234</td>
      </tr>
    </table>

  </div>
  <div class="tab-page" style="height: 400px">
    <h2 class="tab">System</h2>

<%
    Runtime runtime = Runtime.getRuntime();
    DecimalFormat df = new DecimalFormat("0.##"); 
%>
    
    <h2>Environment</h2>

    <table class="thin" style="width: 600px">
      <tr>
        <td width="30%">Java Virtual Machine</td>
        <td>
          <%= System.getProperty("java.vm.name") %>
          (build <%= System.getProperty("java.vm.version") %>,
          <%= System.getProperty("java.vm.info") %>)
        </td>
      </tr>
      <tr>
        <td>Java Runtime Environment</td>
        <td>
          <%= System.getProperty("java.runtime.name") %>
          (build <%= System.getProperty("java.vm.version") %>)
        </td>
      </tr>
      <tr>
        <td>Operating System</td>
        <td>
          <%= System.getProperty("os.name") %>
          <%= System.getProperty("os.version") %>
          <%= System.getProperty("os.arch") %>
        </td>
      </tr>
      <tr>
        <td>Processor(s)</td>
        <td><%= runtime.availableProcessors() %></td>
      </tr>
    </table>
    
    
    <h2>Memory</h2>
    
    <table class="thin" style="width: 600px">
      <tr>
        <td width="30%">Total Memory</td>
        <td><%= df.format(runtime.totalMemory()/1024d/1024d) %> Mb</td>
      </tr>
      <tr>
        <td>Max Memory</td>
        <td><%= df.format(runtime.maxMemory()/1024d/1024d) %> Mb</td>
      </tr>
      <tr>
        <td>Free Memory</td>
        <td><%= df.format(runtime.freeMemory()/1024d/1024d) %> Mb</td>
      </tr>
    </table>

    <br>

    <form id="gc" action="/servlet/net.jetrix.servlets.ServerAction">
      <input type="hidden" name="action" value="gc">
      <input type="submit" value="Run the Garbage Collector">
    </form>

  </div>
</div>


</body>
</html>
