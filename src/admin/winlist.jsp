<%@ page import="net.jetrix.*,
                 net.jetrix.winlist.WinlistManager,
                 net.jetrix.winlist.Winlist,
                 net.jetrix.winlist.Score"%>
<%@ page import="net.jetrix.config.*"%>
<%@ page import="net.jetrix.filter.*"%>
<%@ page import="java.util.*"%>

<%
    WinlistManager manager = WinlistManager.getInstance();
    Winlist winlist = manager.getWinlist(request.getParameter("id"));
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <script type="text/javascript" src="javascript/tabpane/js/tabpane.js"></script>
  <link type="text/css" rel="stylesheet" href="javascript/tabpane/css/luna/tab.css">
  <link type="text/css" rel="stylesheet" href="style.css">
  <title>Jetrix Administration - Winlist - <%= winlist.getId() %></title>
</head>
<body>

<div class="content" style="padding: 1em">

<h1>Winlist - <%= winlist.getId() %></h1>

<div class="tab-pane" id="tab-pane-1">

  <div class="tab-page" style="height: 400px">
    <h2 class="tab">Scores</h2>

    <form id="parameters" action="/servlet/net.jetrix.servlets.WinlistAction">
      <input type="hidden" name="id" value="<%= winlist.getId() %>">

      <table class="thin" style="width: 600px">
        <tr>
          <th width="50%">Name</th>
          <th width="50%">Type</th>
          <th width="50%">Score</th>
        </tr>
<%  for (Score score : winlist.getScores(0, winlist.size())) { %>
        <tr>
          <td><%= score.getName() %></td>
          <td><%= score.getScore()%></td>
        </tr>
<%  } %>
      </table>

      <br>

      <input type="submit" value="Clear">

    </form>

  </div>
</div>

<script type="text/javascript">setupAllTabs();</script>

</div>

</body>
</html>
