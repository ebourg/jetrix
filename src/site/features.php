<? include("header.inc.php") ?>

<style type="text/css">
.done    { list-style-image: url("images/done.png") }
.planned { list-style-image: url("images/planned.png") }
.partial { list-style-image: url("images/partial.png") }
</style>

<h1>Features</h1>

<b>Legend:</b>

<ul>
  <li class="done">done</li>
  <li class="planned">planned</li>
  <li class="partial">in progress</li>
</ul>


<h2>Server</h2>

<ul>
  <li class="done"> multiple channels</li>
  <li class="done"> 31 commands (<a href="user-guide.php#section4">reference</a>)</li>
  <li class="done"> partial command invocation (<tt>/j</tt> for <tt>/join</tt>, <tt>/br</tt> for <tt>/broadcast</tt>, etc...)</li>  
  <li class="done"> password protected channels</li>
  <li class="done"> authentication level restricted channels</li>
  <li class="done"> pluggable command system (<a href="dev-guide.php#section3-1">create your own /commands</a>)</li>
  <li class="done"> customizable winlists</li>
  <li class="done"> unlimited spectators per channel</li>
  <li class="planned"> database integration</li>
  <li class="partial"> 100 simultaneous clients supported</li>
  <li class="done"> customizable channels with <a href="dev-guide.php#section3-2">filters</a></li>
  <li class="done"> run on multiple platforms (tested on Linux, Windows &amp; Solaris)</li>
</ul>

<h2>Clients &amp; Protocols</h2>

<ul>
  <li class="done"> multiple clients &amp; protocols supported</li>
  <li class="done"> TetriNET clients</li>
  <li class="done"> TetriFast clients</li>
  <li class="done"> Block synchronization protocol (1.14)</li>
  <li class="done"> TSpec clients</li>
  <li class="planned"> Flash clients</li>
  <li class="done"> Query protocol</li>
  <li class="partial"> IRC clients</li>
</ul>

<h2>Gaming</h2>

<ul>
  <li class="done"> game statistics (dropping rate, specials used, tetris...)</li>
  <li class="planned"> individual statistics (games played, average skill, time logged, time played...)</li>
  <li class="planned"> game recording and playback</li>
  <li class="done"> multiplayer "7Tetris" channel</li>
  <li class="planned"> puzzle channels</li>
  <li class="done"> survival channel</li>
  <li class="planned"> quick channel</li>
  <li class="planned"> "all around" channel</li>
  <li class="done"> sudden death</li>
</ul>

<h2>Anti cheating</h2>

<ul>
  <li class="done"> specials blocked in pure mode</li>
  <li class="planned"> piece droppping rate control</li>
  <li class="planned"> forged messages detection</li>
  <li class="planned"> block early "add to all" messages</li>
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
  <li class="partial"> web administration console</li>
  <li class="done"> XML based configuration files</li>
  <li class="partial"> hot reconfiguration (no server restart needed)</li>
  <li class="partial"> administration from the system shell</li>
</ul>

<h2>Internationalization</h2>

<ul>
  <li class="done"> support for multiple languages</li>
  <li class="planned"> language detection based on client's IP</li>
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
  <li class="planned"> player authentication</li>
  <li class="planned"> pluggable authentication modules (support for LDAP, Unix, NT or custom authentication systems)</li>
  <li class="done"> limitation on simultaneous connections from the same host (default to 3)</li>
  <li class="planned"> connection frequency check</li>
  <li class="done"> host ban list</li>
  <li class="planned"> host allow list</li>
</ul>

<h2>Networking</h2>

<ul>
  <li class="done"> IPv6 support (testing needed)</li>
</ul>

<h2>Misc</h2>

<ul>
  <li class="done"> automatic advertisement to server directories (tetrinet.org, tsrv.com)</li>
  <li class="partial"> automatic update</li>
  <li class="done"> notification of new versions</li>
  <li class="planned"> run as a service on Windows</li>
  <li class="planned"> run as a daemon on Unix/Linux</li>
  <li class="done"> Windows installer</li>
</ul>

<? include("footer.inc.php") ?>
