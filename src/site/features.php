<? include("header.inc.php") ?>

<h1>Features</h1>

<div style="float: right; margin-right: 2em; margin-top: 1em">
<b>Legend:</b>

<ul>
  <li class="done">done</li>
  <li class="planned">planned</li>
  <li class="partial">in progress</li>
</ul>
</div>

<h2>Server</h2>

<ul>
  <li class="done"> Multiple channels</li>
  <li class="done"> 31 commands (<a href="user-guide.php#section4">reference</a>)</li>
  <li class="done"> Partial command invocation (<tt>/j</tt> for <tt>/join</tt>, <tt>/br</tt> for <tt>/broadcast</tt>, etc...)</li>  
  <li class="done"> Password protected channels</li>
  <li class="done"> Authentication level restricted channels</li>
  <li class="done"> Pluggable command system (<a href="dev-guide.php#section3-1">create your own /commands</a>)</li>
  <li class="done"> Customizable winlists</li>
  <li class="done"> Unlimited spectators per channel</li>
  <li class="done"> Database integration</li>
  <li class="partial"> 100 simultaneous clients supported</li>
  <li class="done"> Customizable channels with <a href="dev-guide.php#section3-2">filters</a></li>
  <li class="done"> Run on multiple platforms (tested on Linux, Windows &amp; Solaris)</li>
</ul>

<h2>Clients &amp; Protocols</h2>

<ul>
  <li class="done"> Multiple clients &amp; protocols supported</li>
  <li class="done"> TetriNET clients</li>
  <li class="done"> TetriFast clients</li>
  <li class="done"> Block synchronization protocol (1.14)</li>
  <li class="done"> TSpec clients</li>
  <!--<li class="planned"> Flash clients</li>-->
  <li class="done"> Query protocol</li>
  <li class="partial"> IRC clients</li>
</ul>

<h2>Gaming</h2>

<ul>
  <li class="done"> Game statistics (dropping rate, specials used, tetris...)</li>
  <li class="planned"> Individual statistics (games played, average skill, time logged, time played...)</li>
  <li class="planned"> Game recording and playback</li>
  <li class="done"> Multiplayer "7Tetris" channel</li>
  <li class="done"> Puzzle channels</li>
  <li class="done"> Survival channel</li>
  <li class="planned"> Quick channel</li>
  <li class="planned"> "All around" channel</li>
  <li class="done"> Sudden death</li>
</ul>

<h2>Anti cheating</h2>

<ul>
  <li class="done"> Specials blocked in pure mode</li>
  <li class="planned"> Piece droppping rate control</li>
  <li class="planned"> Forged messages detection</li>
  <li class="planned"> Block early "add to all" messages</li>
</ul>

<h2>Chatting</h2>

<ul>
  <li class="done"> Anti flood filter</li>
  <li class="planned"> Bad words filter</li>
  <li class="done"> Idle channel</li>
  <li class="partial"> Ignore list</li>
  <li class="planned"> Friends list</li>
</ul>

<h2>Administration</h2>

<ul>
  <li class="partial"> Web administration console</li>
  <li class="done"> XML based configuration files</li>
  <li class="partial"> Hot reconfiguration (no server restart needed)</li>
  <li class="partial"> Administration from the system shell</li>
</ul>

<h2>Internationalization</h2>

<ul>
  <li class="done"> Support for multiple languages</li>
  <li class="planned"> Language detection based on client's IP</li>
  <li class="done"> English support</li>
  <li class="done"> French support</li>
  <li class="done"> German support</li>
  <li class="done"> Dutch support</li>
  <li class="done"> Spanish support</li>
  <li class="done"> Portuguese support</li>
  <li class="partial"> Italian support</li>
</ul>

<h2>Security</h2>

<ul>
  <li class="planned"> Player authentication</li>
  <li class="planned"> Pluggable authentication modules (support for LDAP, Unix, NT or custom authentication systems)</li>
  <li class="done"> Limitation on simultaneous connections from the same host (default to 3)</li>
  <li class="planned"> Connection frequency check</li>
  <li class="done"> Host ban list</li>
  <li class="planned"> Host allow list</li>
</ul>

<h2>Networking</h2>

<ul>
  <li class="done"> IPv6 support</li>
  <li class="planned"> UPnP support</li>
</ul>

<h2>Misc</h2>

<ul>
  <li class="done"> Automatic advertisement to server directories (<a href="http://servers.tetrinet.fr">servers.tetrinet.fr</a>)</li>
  <li class="partial"> Automatic update</li>
  <li class="done"> Notification of new versions</li>
  <li class="planned"> Run as a service on Windows</li>
  <li class="planned"> Run as a daemon on Unix/Linux</li>
  <li class="done"> Windows installer</li>
  <li class="done"> Debian package</li>
  <li class="planned"> RPM package</li>
</ul>

<? include("footer.inc.php") ?>
