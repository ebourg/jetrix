<? include("header.inc.php") ?>

<h1>User Guide</h1>

<h2>Table of Contents</h2>

<ol>
  <li><a href="#section1">Installation</a></li>
  <li><a href="#section2">Configuration</a>
    <ul>
      <li><a href="#section2-1">Listeners</a></li>
      <li><a href="#section2-2">Channels</a></li>
      <li><a href="#section2-3">Commands</a></li>
      <li><a href="#section2-4">Filters</a></li>
      <li><a href="#section2-5">Banlist</a></li>
    </ul>
  </li>
  <li><a href="#section3">Web Administration</a></li>
  <li><a href="#section4">Command Reference</a>
    <ul>
      <li><a href="#section4-broadcast">Broadcast</a></li>
      <li><a href="#section4-config">Config</a></li>
      <li><a href="#section4-emote">Emote</a></li>
      <li><a href="#section4-goto">Goto</a></li>
      <li><a href="#section4-help">Help</a></li>
      <li><a href="#section4-ip">Ip</a></li>
      <li><a href="#section4-join">Join</a></li>
      <li><a href="#section4-kick">Kick</a></li>
      <li><a href="#section4-language">Language</a></li>
      <li><a href="#section4-list">List</a></li>
      <li><a href="#section4-motd">Motd</a></li>
      <li><a href="#section4-move">Move</a></li>
      <li><a href="#section4-operator">Operator</a></li>
      <li><a href="#section4-pause">Pause</a></li>
      <li><a href="#section4-ping">Ping</a></li>
      <li><a href="#section4-random">Random</a></li>
      <li><a href="#section4-reply">Reply</a></li>
      <li><a href="#section4-start">Start</a></li>
      <li><a href="#section4-stop">Stop</a></li>
      <li><a href="#section4-summon">Summon</a></li>
      <li><a href="#section4-teleport">Teleport</a></li>
      <li><a href="#section4-tell">Tell</a></li>
      <li><a href="#section4-time">Time</a></li>
      <li><a href="#section4-version">Version</a></li>
      <li><a href="#section4-who">Who</a></li>
    </ul>
  </li>
</ol>

<h1><a id="section1"></a>Installation</h1>

<h3>System Requirements</h3>

<ul>
  <li>Linux, Windows, Solaris or MacOS X</li>
  <li>Java Runtime Environnement 1.4.x or higher</li>
  <li>32 Mb RAM</li>
  <li>5 Mb hard drive space</li>
</ul>

<h3>Running &amp; Upgrading</h3>

<p>You need a JRE 1.4 or higher installed on your server to run JetriX. You can
download it here :</p>

<a href="http://java.sun.com/j2se/1.4/download.html">http://java.sun.com/j2se/1.4/download.html</a>

<p>Make sure the <tt>JAVA_HOME</tt> environnement variable is properly set !
Unzip the JetriX archive to the installation directory and run the following
commands in the jetrix directory:</p>

<ul>
  <li>To start the server, <tt>./jetrix</tt> on Unix or <tt>jetrix</tt> on Windows</li>
  <li>To download the latest release and upgrade your version, <tt>./update</tt> on Unix or <tt>update</tt> on Windows</li>
</ul>


<h1><a id="section2"></a>Configuration</h1>

<h2><a id="section2-1"></a>Listeners</h2>

<h2><a id="section2-2"></a>Channels</h2>

<h2><a id="section2-3"></a>Commands</h2>

<h2><a id="section2-4"></a>Filters</h2>

<h2><a id="section2-5"></a>Banlist</h2>


<h1><a id="section3"></a>Web Administration</h1>

<p>An administration console is integrated to Jetrix, you can use it with any
modern browser to change most of the configuration without restarting the 
server. The console is available on the port 8080 of your server. You'll be 
prompted for a username and a password, just type in <tt>operator</tt> and the 
operator password to enter (<tt>jetrixpass</tt> by default).</p>

<p>NOTE: This is still a prototype, the changes made to the configuration will 
not survive a server restart with the current version (0.1.3). The full 
persistence of the configuration is planned for a future release.</p>

<h1><a id="section4"></a>Command Reference</h1>

<h2><a id="section4-broadcast"></a>Broadcast</h2>

<h2><a id="section4-config"></a>Config</h2>

<h2><a id="section4-emote"></a>Emote</h2>

<h2><a id="section4-goto"></a>Goto</h2>

<h2><a id="section4-help"></a>Help</h2>

<h2><a id="section4-ip"></a>Ip</h2>

<h2><a id="section4-join"></a>Join</h2>

<h2><a id="section4-kick"></a>Kick</h2>

<h2><a id="section4-language"></a>Language</h2>

<h2><a id="section4-list"></a>List</h2>

<h2><a id="section4-motd"></a>Motd</h2>

<h2><a id="section4-move"></a>Move</h2>

<h2><a id="section4-operator"></a>Operator</h2>

<h2><a id="section4-pause"></a>Pause</h2>

<h2><a id="section4-ping"></a>Ping</h2>

<h2><a id="section4-random"></a>Random</h2>

<h2><a id="section4-reply"></a>Reply</h2>

<h2><a id="section4-start"></a>Start</h2>

<h2><a id="section4-stop"></a>Stop</h2>

<h2><a id="section4-summon"></a>Summon</h2>

<h2><a id="section4-teleport"></a>Teleport</h2>

<h2><a id="section4-tell"></a>Tell</h2>

<h2><a id="section4-time"></a>Time</h2>

<h2><a id="section4-version"></a>Version</h2>

<h2><a id="section4-who"></a>Who</h2>


<? include("footer.inc.php") ?>
