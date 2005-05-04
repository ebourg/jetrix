<%@ page import="net.jetrix.*,
                 net.jetrix.winlist.WinlistManager,
                 net.jetrix.winlist.Winlist"%>
<%@ page import="net.jetrix.servlets.*"%>
<%@ page import="net.jetrix.config.*"%>
<%@ page import="net.jetrix.filter.*"%>
<%@ page import="java.util.*"%>

<%
    ChannelManager channelManager = ChannelManager.getInstance();
    Channel channel = channelManager.getChannel(request.getParameter("name"));

    ChannelConfig conf = channel.getConfig();
    request.setAttribute("settings", conf.getSettings());
    request.setAttribute("channel.name", conf.getName());
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <script type="text/javascript" src="javascript/tabpane/js/tabpane.js"></script>
  <link type="text/css" rel="stylesheet" href="javascript/tabpane/css/luna/tab.css">
  <link type="text/css" rel="stylesheet" href="style.css">
  <title>Jetrix Administration - Channel - <%= conf.getName() %></title>
</head>
<body>

<div class="content" style="padding: 1em">

<h1>Channel - <%= conf.getName() %></h1>

<div class="tab-pane" id="tab-pane-1">

  <div class="tab-page" style="height: 400px">
    <h2 class="tab">Parameters</h2>

    <form id="parameters" action="/servlet/<%= ChannelAction.class.getName() %>">
      <input type="hidden" name="name" value="<%= conf.getName() %>">

      <table class="thin" style="width: 600px">
        <tr>
          <th width="20%">Parameter</th>
          <th width="80%">Value</th>
        </tr>
        <tr>
          <td>Description</td>
          <td><input class="thin" type="text" name="description" value="<%= conf.getDescription() %>"></td>
        </tr>
        <tr>
          <td>Access Level</td>
          <td><input class="thin" type="text" name="accessLevel" value="<%= conf.getAccessLevel() %>"></td>
        </tr>
        <tr>
          <td>Password</td>
          <td><input class="thin" type="text" name="password" value="<%= conf.getPassword() == null ? "" : conf.getPassword() %>"></td>
        </tr>        
        <tr>
          <td>Max Players</td>
          <td><input class="thin" type="text" name="maxPlayers" value="<%= conf.getMaxPlayers() %>"></td>
        </tr>
        <tr>
          <td>Max Spectators</td>
          <td><input class="thin" type="text" name="maxSpectators" value="<%= conf.getMaxSpectators() %>"></td>
        </tr>
        <tr>
          <td>Spectator Comments</td>
          <td>
            <label><input type="radio" value="true"  name="spectalk"> Yes</label>
            <label><input type="radio" value="false" name="spectalk"> No</label>
          </td>
        </tr>
        <tr>
          <td>Status</td>
          <td>
            <label><input type="radio" value="true"  name="open"> Opened</label>
            <label><input type="radio" value="false" name="open"> Closed</label>
          </td>
        </tr>
        <tr>
          <td>Persistent</td>
          <td>
            <label><input type="radio" value="true"  name="persistent" <%= conf.isPersistent() ? "checked" : "" %>> Yes</label>
            <label><input type="radio" value="false" name="persistent" <%= conf.isPersistent() ? "" : "checked" %>> No</label>
          </td>
        </tr>
        <tr>
          <td>Visible</td>
          <td>
            <label><input type="radio" value="true"  name="visible" <%= conf.isVisible() ? "checked" : "" %>> Yes</label>
            <label><input type="radio" value="false" name="visible" <%= conf.isVisible() ? "" : "checked" %>> No</label>
          </td>
        </tr>
        <tr>
          <td>Idle Allowed</td>
          <td>
            <label><input type="radio" value="true"  name="idle" <%= conf.isIdleAllowed() ? "checked" : "" %>> Yes</label>
            <label><input type="radio" value="false" name="idle" <%= conf.isIdleAllowed() ? "" : "checked" %>> No</label>
          </td>
        </tr>
        <tr>
          <td>Winlist</td>
          <td>
            <select name="winlist">
              <option value="">-- none --</option>
<%  for (Winlist winlist : WinlistManager.getInstance().getWinlists()) { %>
              <option <%= winlist.getId().equals(conf.getWinlistId()) ? "selected" : "" %>><%= winlist.getId() %></option>
<%  } %>
            </select>
          </td>
        </tr>
        <tr>
          <td valign="top">Topic</td>
          <td><textarea class="thin" name="topic" rows="5" cols="20" style="width: 100%"><%= conf.getTopic() == null ? "" : conf.getTopic() %></textarea></td>
        </tr>
      </table>

      <br>

      <input type="submit" value="Save Changes">

    </form>

  </div>
  <div class="tab-page" style="height: 400px">
    <h2 class="tab">Settings</h2>

    <jsp:include page="/servlet/org.apache.jsp.settings_jsp"/>

  </div>
  <div class="tab-page" style="height: 400px">
    <h2 class="tab">Filters</h2>

<%  Iterator<MessageFilter> filters = channel.getFilters(); %>

	<h2>Filters</h2>

	<table class="thin" style="width: 600px">
	  <tr>
	    <th>Name</th>
	    <th>Version</th>
	    <th>Description</th>
        <th>Global</th>
	    <th></th>
	  </tr>
<%  while (filters.hasNext()) {
        MessageFilter filter = filters.next();  %>
	  <tr>
	    <td><%= filter.getName() %></td>
	    <td><%= filter.getVersion() %></td>
	    <td><%= filter.getDescription() %></td>
        <td align="center"><%= filter.getConfig().isGlobal() ? "yes" : "no" %></td>
	    <td>
<%      if (!filter.getConfig().isGlobal()) { %>
          <input type="image" src="images/delete16.png" value="remove" alt="Remove" title="Remove">
<%      } %>
        </td>
	  </tr>
<%  } %>
	</table>

  </div>
  <div class="tab-page" style="height: 400px">
    <h2 class="tab">Fields</h2>

    <table class="thin" cellpadding="3">
      <tr>
<%  for (int i = 0; i < 6; i++) { %>
        <td>
          <%= i + 1 %>:
<%      if (channel.getPlayer(i + 1) != null) { %>
          <a href="user.jsp?name=<%= channel.getPlayer(i + 1).getName() %>"><%= channel.getPlayer(i + 1).getName() %></a>
<%      } else { %>
          (empty)
<%      } %>
        </td>
<%  } %>
      </tr>
      <tr>
<%  for (int i = 0; i < 6; i++) { %>
        <td><iframe src="field.jsp?name=<%= conf.getName() %>&num=<%= i %>" width="96" height="176" frameborder="no"></iframe></td>
<%  } %>
      </tr>
    </table>

  </div>
</div>

<script type="text/javascript">setupAllTabs();</script>

</div>

</body>
</html>
