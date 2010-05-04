<?
    if (preg_match("/MSIE/", $_SERVER['HTTP_USER_AGENT']) || preg_match("/google/", $_SERVER['HTTP_USER_AGENT'])) {
        header("Content-Type: text/html");
    } else {
        header("Content-Type: application/xhtml+xml");
        echo "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n";
    }
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <meta http-equiv="Content-Type" content="text/xml; charset=iso-8859-1" />
  <meta name="author" content="Emmanuel Bourg" />
  <meta name="email" content="smanux@lfjr.net" />
  <meta name="description" content="Jetrix is a new generation TetriNET server written in Java and designed for maximum scalability and extensibility." />
  <meta name="keywords" content="jetrix, tetrinet, server, tnet, tetrifast, tfast, tspec, tetris, tetrix, java" />
  <link rel="stylesheet" href="style.css" />
  <link rel="shortcut icon" type="image/x-icon" href="favicon.ico" />
  <link rel="alternate" type="application/rss+xml" title="RSS" href="http://sourceforge.net/export/rss2_projnews.php?group_id=52188&amp;rss_fulltext=1" />
  <title>Jetrix TetriNET Server</title>
</head>
<body>

<!-- header -->
<table width="100%" cellspacing="0">
<tbody>
  <tr>
    <td>
      <a href="http://jetrix.sourceforge.net/"><img src="images/jetrix-logo.png" alt="Jetrix" style="float: left" /></a>
    </td>
    <td align="right" style="padding-right: 0.5em">
      <a href="http://sourceforge.net"><img src="http://sourceforge.net/sflogo.php?group_id=52188&amp;type=2" width="125" height="37" alt="SourceForge.net Logo" /></a>
    </td>
  </tr>
</tbody>
</table>

<table width="100%" cellspacing="4">
<tbody>
  <tr>
    <td colspan="2">
      <div class="hr"></div>
    </td>
  </tr>
  <tr>
    <!-- left side menu -->
    <td style="width: 150px; white-space: nowrap" valign="top">
      <? include("menu.inc.php"); ?>
      
      <div align="center">
      <a href="http://www.opensource.org/"><img src="images/opensource-110x95.png" alt="Open Source" /></a><br/>
      
      <a href='http://www.ohloh.net/stack_entries/new?project_id=jetrix&amp;ref=WidgetProjectUsersLogo' onMouseOut="this.style.background = 'url(http://www.ohloh.net/images/stack/iusethis/static_logo.png) 0px 0px no-repeat'" onMouseOver="this.style.background = 'url(http://www.ohloh.net/images/stack/iusethis/static_logo.png) 0 -23px no-repeat'" style='border-bottom:none;text-decoration:none;display:block;background:url(http://www.ohloh.net/images/stack/iusethis/static_logo.png) 0px 0px no-repeat;width:73px;height:23px;' target='_top' title='Support Jetrix TetriNET Server by adding it to your stack at Ohloh'></a></div>
    </td>
    <td align="left" valign="top">
      <div class="content">
