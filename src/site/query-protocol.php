<? include("header.inc.php") ?>
      
<h1>Query Protocol</h1>

Tetrix first introduced a query protocol to get easily a list of players and channels on TetriNET servers. This protocol consists in 4 commands : <tt>playerquery</tt>, <tt>listchan</tt>, <tt>listuser</tt> and <tt>version</tt>. These commands are sent through the standard tetrinet port 31457 and must be terminated by the <tt>0xFF</tt> character. The line terminator for the response is a line feed <tt>0x0A</tt>. This protocol is not yet supported by Jetrix.<br />


<br /><div><b>playerquery</b></div>

Returns the number of players connected to the server. The output format is :

<div class="code">Number of players logged in: &lt;playernum&gt;</div>

<br /><div><b>listchan</b></div>

Returns the list of available channels on the server. The output format is :

<div class="code">"&lt;name&gt;" "&lt;description&gt;" &lt;playernum&gt; &lt;playermax&gt; &lt;priority&gt; &lt;status&gt;</div>
<pre>
name         the name of the channel
description  the description or topic of the channel
playernum    the number of players in this channel
playermax    the maximum number of players
priority     the priority of the channel
status       the game state (1: stopped, 2: started, 3: paused)
</pre>

The response is terminated by the string "<tt>+OK</tt>".<br /><br />

<br /><div><b>listuser</b></div>

Returns the list of players connected to the server. The output format is :<br />

<div class="code">"&lt;nick&gt;" "&lt;team&gt;" "&lt;version&gt;" &lt;slot&gt; &lt;state&gt; &lt;auth&gt; "&lt;chname&gt;"</div>
<pre>
nick         the name of the player
team         the team name of the player
version      the version of the client used, usually "1.13"
slot         the slot used in the channel (1-6)
state        the state of the player (0: not playing, 1: playing, 2: lost)
auth         the authentication level (1: normal, 2: channel operator, 3: operator)
channel      the name of the channel
</pre>
The response is terminated by the string "<tt>+OK</tt>".<br /><br />

<br /><div><b>version</b></div>

Returns the version of the server. The response is terminated by the string "<tt>+OK</tt>".

<br /><br />

<? include("footer.inc.php") ?>