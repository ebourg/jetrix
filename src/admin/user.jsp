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
  <link type="text/css" rel="stylesheet" href="javascript/tabpane/css/luna/tab.css" />
  <link type="text/css" rel="stylesheet" href="style.css">
  <title>JetriX Admin - User - <%= user.getName() %></title>
</head>
<body>

<div class="content" style="padding: 1em">

<h1>User - <%= user.getName() %></h1>




</body>
</html>
