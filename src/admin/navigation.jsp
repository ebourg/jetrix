<%@ page import="net.jetrix.*"%>
<%@ page import="net.jetrix.config.*"%>
<%@ page import="net.jetrix.filter.*"%>
<%@ page import="java.util.*"%>

<%
    ChannelManager channelManager = ChannelManager.getInstance();
    
    
    
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <script type="text/javascript" src="javascript/xloadtree/xtree.js"></script>
  <script type="text/javascript" src="javascript/xloadtree/xmlextras.js"></script>
  <script type="text/javascript" src="javascript/xloadtree/xloadtree.js"></script>
  <link type="text/css" rel="stylesheet" href="javascript/xloadtree/xtree.css" />
  <link type="text/css" rel="stylesheet" href="style.css">
  <title>JetriX Admin - Navigation</title>
</head>
<body style="margin: 1em">

<button onclick="document.location.reload()">Refresh</button>

<br><br>

<script type="text/javascript">

function loadPage(page)
{
    parent.frames['content'].location=page;
}

// XP Look
webFXTreeConfig.rootIcon		= "javascript/xloadtree/images/xp/folder.png";
webFXTreeConfig.openRootIcon	= "javascript/xloadtree/images/xp/openfolder.png";
webFXTreeConfig.folderIcon		= "javascript/xloadtree/images/xp/folder.png";
webFXTreeConfig.openFolderIcon	= "javascript/xloadtree/images/xp/openfolder.png";
webFXTreeConfig.fileIcon		= "javascript/xloadtree/images/xp/file.png";
webFXTreeConfig.lMinusIcon		= "javascript/xloadtree/images/xp/Lminus.png";
webFXTreeConfig.lPlusIcon		= "javascript/xloadtree/images/xp/Lplus.png";
webFXTreeConfig.tMinusIcon		= "javascript/xloadtree/images/xp/Tminus.png";
webFXTreeConfig.tPlusIcon		= "javascript/xloadtree/images/xp/Tplus.png";
webFXTreeConfig.iIcon			= "javascript/xloadtree/images/xp/I.png";
webFXTreeConfig.lIcon			= "javascript/xloadtree/images/xp/L.png";
webFXTreeConfig.tIcon			= "javascript/xloadtree/images/xp/T.png";

var tree = new WebFXLoadTree("Server", "navigationChannels.jsp", "javascript:loadPage('server.jsp')");
document.write(tree);

</script>


</body>
</html>
