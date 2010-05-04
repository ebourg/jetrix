<? include("header.inc.php") ?>

<h1>Developer Guide</h1>

<h2>Table of Contents</h2>

<ol>
  <!--<li><a href="#section0">Developing with Jetrix</a></li>-->
  <li><a href="#section1">Architecture</a></li>
  <li><a href="#section2">Protocols</a>
    <ul>
      <li><a href="#section2-1">TetriNET 1.13</a></li>
      <li><a href="#section2-2">TetriNET 1.14</a></li>
      <li><a href="#section2-3">TetriFast</a></li>
      <li><a href="#section2-4">TSpec</a></li>
      <li><a href="#section2-5">Query</a></li>
    </ul>
  </li>
  <li><a href="#section3">Customization</a>
    <ul>
      <li><a href="#section3-1">Commands</a></li>
      <li><a href="#section3-2">Filters</a></li>
      <li><a href="#section3-3">Winlists</a></li>
    </ul>
  </li>
</ol>

<!--
<h1><a id="section0"></a>Developing with Jetrix</h1>

@todo

installing the JDK

installing &amp; using Apache Ant
-->

<h1><a id="section1"></a>Architecture</h1>

Jetrix is designed around the concept of independant threads communicating asynchronously with messages. The three main entities are :
<ul>
  <li style="margin-bottom: 1em"><b>client</b> - it listens for messages sent by the TetriNET client, it's coupled to a protocol that will translate incomming messages into server messages. The messages are then forwarded to the channel occupied by the client. There is one client thread for every client connected to the server.</li>
  <li style="margin-bottom: 1em"><b>channel</b> - this is a game channel accepting playing and non-playing (spectators) clients. Messages go through a set of filters that can transform, delete or add messages to customize the behaviour of the channel. After a processing the game message are sent back to the clients. Non game related messages are forwarded to the main server thread.</li>
  <li style="margin-bottom: 1em"><b>server</b> - it handles system commands such as shutdown and slash commands typed by clients (<tt>/who</tt>, <tt>/list</tt>, <tt>/join</tt>...)</li>
</ul>

<div style="text-align: center"><img src="images/arch1.png" alt="Architecture - Message Flow" /></div>


<h1><a id="section2"></a>Protocols</h1>


<h2><a id="section2-1"></a>TetriNET 1.13</h2>

External references:

<ul>
  <li><a href="http://knarf2.cheztaz.com/tetrinet.html">Description du protocole TetriNET par knarf2 (french)</a></li>
  <li><a href="http://web.archive.org/web/20040413185025/home.planetinternet.be/~m0217000/tsrv/tetrinetproto.htm">Tetrinet protocol on TSRV.COM (archived)</a></li>
</ul>


<h2><a id="section2-2"></a>TetriNET 1.14</h2>

The version 1.14 of the TetriNET protocol is an extension introduced by 
Olivier Vidal in August 2003 allowing players to get the same sequence of
blocks. This protocol was first integrated to a server in Jetrix 0.1.3. It
works by adding an extra parameter at the end of the newgame command:

<div class="code">newgame 0 1 2 1 2 1 18 &lt;blocks frequency array&gt; &lt;specials frequency array&gt; 1 1 <b>2A1C21B6</b></div>

The parameter <tt>2A1C21B6</tt> is actually the hexadecimal representation of a 
32 bits integer. It determines the seed of the random number generator used by
TetriNET to generate the blocks (see bellow). The number must be zero padded
to 8 digits and use the little endian format. For example <tt>0x123</tt> will
be added as <tt>00000123</tt>.


<h3>TetriNET Pseudo Random Number Generator</h3>

<p>The original TetriNET client designed by St0rmCat uses the standard random
function available in Delphi. It's basically a <a href="http://en.wikipedia.org/wiki/Linear_congruential_generator">linear congruential generator</a>,
the recursive sequence is defined by:</p>

<div class="code">s<sub>n+1</sub> = (a.s<sub>n</sub> + c) mod M</div>

<p>with the following parameters:</p>

<div class="code">a = 0x08088405
c = 1
M = 2<sup>32</sup>
</div>

<p>The seed s<sub><small>0</small></sub> is determined by the last parameter of 
the <tt>newgame</tt> message. This sequence gives pseudo random integers between 
0 and 2<sup><small>32</small></sup> - 1, but an integer in the [0, 100[
range is required for selecting the block, and an integer in the [0, 4[ range
for the orientation of the block. So the result is multiplied by the length of 
the range and divided by 2<sup><small>32</small></sup>:</p>

<div class="code">I<sub>n</sub> = s<sub>n</sub> * L / 2<sup><small>32</small></sup></div>

<p>Where L=100 to select the block and L=4 to select its orientation. The
sequence s<sub><small>n</small></sub> is evaluated twice to determine each block, first for the block 
and then for its orientation.</p>

<p>The following table defines the orientations for each type of block :</p>

<table class="thin">
  <tr>
    <th width="100">Orientation</th>
    <th width="75">0</th>
    <th width="75">1</th>
    <th width="75">2</th>
    <th width="75">3</th>
  </tr>
  <tr>
    <th>Line</th>
    <td><img src="images/blocks/line1.png" alt="Line"/></td>
    <td><img src="images/blocks/line2.png" alt="Line"/></td>
    <td><img src="images/blocks/line1.png" alt="Line"/></td>
    <td><img src="images/blocks/line2.png" alt="Line"/></td>
  </tr>
  <tr>
    <th>Square</th>
    <td><img src="images/blocks/square.png" alt="Square"/></td>
    <td><img src="images/blocks/square.png" alt="Square"/></td>
    <td><img src="images/blocks/square.png" alt="Square"/></td>
    <td><img src="images/blocks/square.png" alt="Square"/></td>
  </tr>
  <tr>
    <th>Left L</th>
    <td><img src="images/blocks/leftl1.png" alt="Left L"/></td>
    <td><img src="images/blocks/leftl2.png" alt="Left L"/></td>
    <td><img src="images/blocks/leftl3.png" alt="Left L"/></td>
    <td><img src="images/blocks/leftl4.png" alt="Left L"/></td>
  </tr>
  <tr>
    <th>Right L</th>
    <td><img src="images/blocks/rightl1.png" alt="Right L"/></td>
    <td><img src="images/blocks/rightl2.png" alt="Right L"/></td>
    <td><img src="images/blocks/rightl3.png" alt="Right L"/></td>
    <td><img src="images/blocks/rightl4.png" alt="Right L"/></td>
  </tr>
  <tr>
    <th>Left Z</th>
    <td><img src="images/blocks/leftz1.png" alt="Left Z"/></td>
    <td><img src="images/blocks/leftz2.png" alt="Left Z"/></td>
    <td><img src="images/blocks/leftz1.png" alt="Left Z"/></td>
    <td><img src="images/blocks/leftz2.png" alt="Left Z"/></td>
  </tr>
  <tr>
    <th>Right Z</th>
    <td><img src="images/blocks/rightz1.png" alt="Right Z"/></td>
    <td><img src="images/blocks/rightz2.png" alt="Right Z"/></td>
    <td><img src="images/blocks/rightz1.png" alt="Right Z"/></td>
    <td><img src="images/blocks/rightz2.png" alt="Right Z"/></td>
  </tr>
  <tr>
    <th>Half Cross</th>
    <td><img src="images/blocks/halfcross1.png" alt="Half Cross"/></td>
    <td><img src="images/blocks/halfcross2.png" alt="Half Cross"/></td>
    <td><img src="images/blocks/halfcross3.png" alt="Half Cross"/></td>
    <td><img src="images/blocks/halfcross4.png" alt="Half Cross"/></td>
  </tr> 
</table>



<h3>Examples</h3>

<p>Here are some examples of block sequences for different seed values. This might 
help client programmers to validate their random number generator. The frequencies
for these examples are :</p>

<table class="thin" style="width: 300px">
  <tr>
    <th align="center" colspan="2">Block</th>
    <th align="center" width="30%">Frequency</th>
  </tr>
  <tr>
    <td align="center" width="10%">1</td>
    <td align="center"><img src="images/blocks/small/line.png" alt="Line" /></td>
    <td align="center">15 %</td>
  </tr>
  <tr>
    <td align="center">2</td>
    <td align="center"><img src="images/blocks/small/square.png" alt="Square" /></td>
    <td align="center">15 %</td>
  </tr>
  <tr>
    <td align="center">3</td>
    <td align="center"><img src="images/blocks/small/leftl.png" alt="Left L" /></td>
    <td align="center">14 %</td>
  </tr>
  <tr>
    <td align="center">4</td>
    <td align="center"><img src="images/blocks/small/rightl.png" alt="Right L" /></td>
    <td align="center">14 %</td>
  </tr>
  <tr>
    <td align="center">5</td>
    <td align="center"><img src="images/blocks/small/leftz.png" alt="Left Z" /></td>
    <td align="center">14 %</td>
  </tr>
  <tr>
    <td align="center">6</td>
    <td align="center"><img src="images/blocks/small/rightz.png" alt="Right Z" /></td>
    <td align="center">14 %</td>
  </tr>
  <tr>
    <td align="center">7</td>
    <td align="center"><img src="images/blocks/small/halfcross.png" alt="Half Cross" /></td>
    <td align="center">14 %</td>
  </tr>     
</table>

<br />

The corresponding frequency table is :

<div class="code">F = [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1, 
     2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
     3,3,3,3,3,3,3,3,3,3,3,3,3,3,
     4,4,4,4,4,4,4,4,4,4,4,4,4,4,
     5,5,5,5,5,5,5,5,5,5,5,5,5,5,
     6,6,6,6,6,6,6,6,6,6,6,6,6,6,
     7,7,7,7,7,7,7,7,7,7,7,7,7,7]</div>

<p>For this example the value of the seed is 0.</p>

<table class="thin">
  <tr>
    <th width="100">Blocks</th>
    <th width="150">Sequence</th>
    <th width="100">Index</th>
    <th width="100">Result</th>       
  </tr>
  <tr>
    <td rowspan="2" valign="middle">Block 1</td>
    <td>s1 = 0x00000001</td>
    <td>0</td>
    <td rowspan="2" valign="middle"><img src="images/blocks/line1.png" alt="Line"/></td>
  </tr>
  <tr>
    <td>s2 = 0x08088406</td>
    <td>0</td>
  </tr>
  <tr>
    <td rowspan="2" valign="middle">Block 2</td>
    <td>s3 = 0xDC6DAC1F</td>
    <td>86</td>
    <td rowspan="2" valign="middle"><img src="images/blocks/halfcross1.png" alt="Half Cross"/></td>
  </tr>
  <tr>
    <td>s4 = 0x33DC589C</td>
    <td>0</td>
  </tr>
  <tr>
    <td rowspan="2" valign="middle">Block 3</td>
    <td>s5 = 0x45DE2B0D</td>
    <td>27</td>
    <td rowspan="2" valign="middle"><img src="images/blocks/square.png" alt="Square"/></td>
  </tr>
  <tr>
    <td>s6 = 0xABF18B42</td>
    <td>2</td>
  </tr>

  <tr>
    <td rowspan="2" valign="middle">Block 4</td>
    <td>s7 = 0x5195C04B</td>
    <td>31</td>
    <td rowspan="2" valign="middle"><img src="images/blocks/leftl1.png" alt="Left L"/></td>
  </tr>
  <tr>
    <td>s8 = 0x296B6D78</td>
    <td>0</td>
  </tr>
</table>


<p>Here are the 10 first blocks for different seed values:</p>

<table class="thin" cellspacing="1">
  <tbody>
    <tr>
      <th>Seed</th>
      <th>Sequence</th>
    </tr>
    <tr>
      <td>0x00000000</td>
      <td>
        <img src="images/blocks/line1.png" alt="Line"/>
        <img src="images/blocks/halfcross1.png" alt="Half Cross"/>
        <img src="images/blocks/square.png" alt="Square"/>
        <img src="images/blocks/leftl1.png" alt="Left L"/>
        <img src="images/blocks/leftl4.png" alt="Left L"/>
        <img src="images/blocks/line2.png" alt="Line"/>
        <img src="images/blocks/line2.png" alt="Line"/>
        <img src="images/blocks/line2.png" alt="Line"/>
        <img src="images/blocks/halfcross4.png" alt="Half Cross"/>
        <img src="images/blocks/leftz2.png" alt="Left Z"/>
      </td>
    </tr>
    <tr>
      <td>0xAABBCCDD</td>
      <td>
        <img src="images/blocks/rightl4.png" alt="Right L"/>
        <img src="images/blocks/rightl4.png" alt="Right L"/>
        <img src="images/blocks/leftl1.png" alt="Left L"/>
        <img src="images/blocks/leftl3.png" alt="Left L"/>
        <img src="images/blocks/square.png" alt="Square"/>
        <img src="images/blocks/rightz2.png" alt="Right Z"/>
        <img src="images/blocks/square.png" alt="Square"/>
        <img src="images/blocks/rightl4.png" alt="Right L"/>
        <img src="images/blocks/halfcross3.png" alt="Half Cross"/>
        <img src="images/blocks/rightl1.png" alt="Right L"/>
      </td>
    </tr>
    <tr>
      <td>0x12345678</td>
      <td>
        <img src="images/blocks/leftz2.png" alt="Left Z"/>
        <img src="images/blocks/leftz2.png" alt="Left Z"/>
        <img src="images/blocks/halfcross2.png" alt="Half Cross"/>
        <img src="images/blocks/rightz2.png" alt="Right Z"/>
        <img src="images/blocks/leftl2.png" alt="Left L"/>
        <img src="images/blocks/rightz1.png" alt="Right Z"/>
        <img src="images/blocks/rightz2.png" alt="Right Z"/>
        <img src="images/blocks/halfcross1.png" alt="Half Cross"/>
        <img src="images/blocks/line2.png" alt="Line"/>
        <img src="images/blocks/rightl1.png" alt="Right L"/>
      </td>
    </tr>
  </tbody>
</table>


<h2><a id="section2-3"></a>TetriFast</h2>

TetriFast is a modified TetriNET client that removes the delay between the 
block fall and the showing of next block. The TetriFast protocol is quite   
similar to the TetriNET protocol, it has just been slightly modified to prevent   
TetriFast clients to connect and cheat on regular TetriNET servers.

<h3>Login</h3>

TetriFast clients connect on the same port as TetriNET client, that's 31457. The
only difference is the initialization string, a TetriFast client will use
<tt>tetrifaster</tt> instead of <tt>tetrisstart</tt>.

<h3>Messages</h3>

The TetriFast protocol use two different messages :

<ul>
  <li>the message <tt>playernum</tt> has been changed to <tt>)#)(!@(*3</tt></li>
  <li>the message <tt>newgame</tt> has been changed to <tt>*******</tt></li>
</ul>




<h2><a id="section2-4"></a>TSpec</h2>


<h2><a id="section2-5"></a>Query</h2>

Tetrix first introduced a query protocol to get easily a list of players and
channels on TetriNET servers. This protocol consists in 4 commands :
<tt>playerquery</tt>, <tt>listchan</tt>, <tt>listuser</tt> and <tt>version</tt>.
These commands are sent through the standard tetrinet port 31457 (TCP) and must be
terminated by the <tt>0xFF</tt> character. The line terminator for the response
is a line feed <tt>0x0A</tt>.<br />


<br /><div><b>playerquery</b></div>

Returns the number of players connected to the server. The output format is :

<div class="code">Number of players logged in: &lt;playernum&gt;</div>

<br /><div><b>listchan</b></div>

Returns the list of available channels on the server. The output format is :

<div class="code">"&lt;name&gt;" "&lt;description&gt;" &lt;playernum&gt; &lt;playermax&gt; &lt;priority&gt; &lt;status&gt;</div>
<pre>
name         the name of the channel (without the leading #)
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



<h1><a id="section3"></a>Customization</h1>


<h2><a id="section3-1"></a>Commands</h2>

Jetrix has been designed to allow the addition of new commands like <tt>/who</tt>
or <tt>/list</tt> easily. This section will show you the steps to follow in 
order to create a simple <tt>/hello</tt> command that will just display the 
message <tt>"Hello World!"</tt>.

<h3>Write the command</h3>

Every command is represented by a Java class implementing the 
<a class="api" href="/api/@version.stable@/net/jetrix/commands/Command.html">Command</a> 
interface. Let's create our class, <tt>HelloCommand</tt>, it'll be based on the 
<a class="api" href="/api/@version.stable@/net/jetrix/commands/AbstractCommand.html">AbstractCommand</a> 
class implementing part of the <tt>Command</tt> interface methods :

<div class="code">
<span class="blue">import</span> java.util.*;
<span class="blue">import</span> net.jetrix.*;
<span class="blue">import</span> net.jetrix.messages.channel.*;
<span class="blue">import</span> net.jetrix.commands.*;

<span class="blue">public class</span> HelloCommand <span class="blue">extends</span> <span class="red">AbstractCommand</span>
{

}

</div>


The <tt>Command</tt> interface defines the required methods that any command has
to implements. We will look at these methods one by one. The first one is the
<tt>getAliases()</tt> method, it returns an array of Strings containing the names
used to invoke the command. For our command we will use two names, <tt>"hello"</tt>
and <tt>"hi"</tt>. The first alias in the array is the default name that will be
displayed in the <tt>/help</tt> list.

<div class="code">
    <span class="blue">public</span> <span class="red">String</span>[] getAliases()
    {
        <span class="blue">return new</span> <span class="red">String</span>[] { <span class="gray">"hello"</span>, <span class="gray">"hi"</span> };
    }

</div>

The next method is <tt>getUsage(Locale locale)</tt>, it returns a String
describing the usage of the command. It is used when displaying the list of
commands available on the server with <tt>/help</tt>. You can ignore the
<tt>Locale</tt> parameter for now, it's used for internationalization purposes.
 Our command has no parameter so it's pretty straight forward :

<div class="code">
    <span class="blue">public</span> <span class="red">String</span> getUsage(<span class="red">Locale</span> locale)
    {
        <span class="blue">return</span> <span class="gray">"/hello"</span>;
    }

</div>

The <tt>getDescription(Locale locale)</tt> method is also used for the command
listing, it returns a short description of the command :

<div class="code">
    <span class="blue">public</span> <span class="red">String</span> getDescription(<span class="red">Locale</span> locale)
    {
        <span class="blue">return</span> <span class="gray">"Display 'Hello World!'"</span>;
    }

</div>

The <tt>getAccessLevel()</tt> defines the minimal access level required to use
the command. To allow everyone to use the command it should return 
<tt>AccessLevel.PLAYER</tt>, to restrict it to operators only it would return 
<tt>AccessLevel.OPERATOR</tt>. The <tt>AbstractCommand</tt> class provides a
default implementation for this method returning the player access level, so
you don't have to worry about it. But I you want your command to be used only
by an operator, change the access level in the constructor :

<div class="code">
    <span class="blue">public </span> HelloCommand()
    {
        setAccessLevel(<span class="red">AccessLevel</span>.OPERATOR);
    }

</div>

Now we reach the main part of the command, the <tt>execute(CommandMessage message)</tt>
method. This method is called when the command is executed. The message parameter
contains all the relevant information needed to process the command, that's the
source of the message (i.e. the user that issued the command) and the list of
parameters. Our Hello command will just create a text message and send it back to
the user :

<div class="code">
    <span class="blue">public void</span> execute(<span class="red">CommandMessage</span> message)
    {
        <span class="red">Message</span> hello = <span class="blue">new</span> <span class="red">PlineMessage</span>(<span class="gray">"Hello World!"</span>);
        message.getSource().sendMessage(hello);
    }

</div>

Our command is complete, let's put all the pieces together :

<div class="code">
<span class="blue">import</span> java.util.*;
<span class="blue">import</span> net.jetrix.*;
<span class="blue">import</span> net.jetrix.messages.channel.*;
<span class="blue">import</span> net.jetrix.commands.*;

<span class="blue">public class</span> HelloCommand <span class="blue">extends</span> <span class="red">AbstractCommand</span>
{
    <span class="blue">public</span> <span class="red">String</span>[] getAliases()
    {
        <span class="blue">return new</span> <span class="red">String</span>[] { <span class="gray">"hello"</span>, <span class="gray">"hi"</span> };
    }

    <span class="blue">public</span> <span class="red">String</span> getUsage(<span class="red">Locale</span> locale)
    {
        <span class="blue">return</span> <span class="gray">"/hello"</span>;
    }

    <span class="blue">public</span> <span class="red">String</span> getDescription(<span class="red">Locale</span> locale)
    {
        <span class="blue">return</span> <span class="gray">"Display 'Hello World!'"</span>;
    }

    <span class="blue">public void</span> execute(<span class="red">CommandMessage</span> message)
    {
        <span class="red">Message</span> hello = <span class="blue">new</span> <span class="red">PlineMessage</span>(<span class="gray">"Hello World!"</span>);
        message.getSource().sendMessage(hello);
    }
}

</div>


<h3>Compile the command</h3>

Save the code above in a <tt>HelloCommand.java</tt> file and copy the <tt>jetrix.jar</tt>
file in the same directory (this jar is in the <tt>jetrix/lib</tt> directory of
the jetrix distribution). Then compile the command with :

<div class="code">javac -classpath jetrix.jar HelloCommand.java</div>

<h3>Deploy the command</h3>

To make your class available to Jetrix just copy it into the <tt>jetrix/lib</tt>
directory. Starting with Jetrix 0.1.1 any <tt>.class</tt> or <tt>.jar</tt> file
in this directory is automatically loaded at startup. Then you need to declare
your command by editing the <tt>config.xml</tt> file, under the <tt>&lt;commands&gt;</tt>
element just add this :

<div class="code">
    &lt;<span class="blue">command</span> <span class="red">class</span>=<span class="gray">"HelloCommand"</span>/&gt;

</div>

<h3>Test the command !</h3>

Now we are ready to try the command ! Start Jetrix and log into the server. On
typing <tt>/help</tt> you'll notice that the new command is automatically listed :

<div style="margin: 1em"><img src="images/command-guide1.png" alt="HelloCommand - command listing" /></div>

To use the command just type <tt>/hello</tt> or <tt>/hi</tt>, you can also use
a partial name like <tt>/hel</tt> and mix upper and lower cases :

<div style="margin: 1em"><img src="images/command-guide2.png" alt="HelloCommand - usage" /></div>

Congratulations ! You have completed your first custom command :) If you create
a useful command feel free to submit it to the Jetrix project to make it available
to all Jetrix users, just send the code to <a href="mailto:smanux@lfjr.net">smanux@lfjr.net</a>.



<h2><a id="section3-2"></a>Filters</h2>

<p>One feature of Jetrix is to make it easy to change the behaviour of the server by
modifying its response to the messages sent by the clients. This is done by
implementing filters. A filter is a small class that can watch and modify all
messages sent to a channel. It can serve various purposes, like modifying the
game, displaying additional informations at the end of the game, blocking a flood
of messages from a player, or implementing a bot responding to the players.</p>

<p>This guide will show you how to create a simple game modification using a filter.
In this mod the first player to complete 7 tetris win.</p>


<h3>Write the filter</h3>

<p>Filters extend the base class <a class="api" href="/api/@version.stable@/net/jetrix/filter/MessageFilter.html">MessageFilter</a>, 
this class defines a <tt>process(Message m, List out)</tt> method that must be 
overridden to implement the behaviour of the filter. A higher level filter 
<a class="api" href="/api/@version.stable@/net/jetrix/filter/GenericFilter.html">GenericFilter</a> 
with process methods for all message types is provided, we will use it for our 
filter.</p>

<p>Let's create our class :</p>

<div class="code"><span class="blue">import</span> java.util.*;
<span class="blue">import</span> net.jetrix.*;
<span class="blue">import</span> net.jetrix.messages.*;
<span class="blue">import</span> net.jetrix.filter.*;

<span class="blue">public class</span> TetrisFilter <span class="blue">extends</span> <span class="red">GenericFilter</span>
{

}
</div>

We have to count the number of tetris performed by each player during the game,
we will put this in an array initialized when the game starts :

<div class="code">
    <span class="blue">private int</span>[] tetrisCount = <span class="blue">new int</span>[<span class="red">6</span>];

    <span class="blue">public void</span> onMessage(<span class="red">StartGameMessage</span> m, <span class="red">List</span> out)
    {
        <span class="red">Arrays</span>.fill(tetrisCount, <span class="red">0</span>);
        out.add(m);
    }

</div>

<p>Every channel has a chain of filters, the messages go through the filters before
reaching the channel. A filter has the responsability to decide what messages
the next filter in the chain will get. The filter could simply eat the message
and forward nothing to the next filter, pass the message unchanged, transform
the message or generate several messages. This is determined by what is put in
the out list of the process method.</p>

<p>Our first <tt>onMessage</tt> method just put the message in the out list with
<tt>out.add(m)</tt>, this is mandatory or the game would never start.</p>

<p>The next <tt>onMessage</tt> method will watch <tt>FourLinesAddedMessage</tt>
messages, this is the message sent when a player performs a tetris. This method
will increase the tetris count of the player and test if the limit of 7 tetris
has been reached. If so the game is stopped and the winner is announced.</p>

<div class="code">
    <span class="blue">public void</span> onMessage(<span class="red">FourLinesAddedMessage</span> m, <span class="red">List</span> out)
    {
        <span class="green">// get the slot number of the player</span>
        <span class="blue">int</span> from = m.getFromSlot();

        <span class="green">// increase its tetris count</span>
        tetrisCount[from]++;

        <span class="green">// pass the message unchanged</span>
        out.add(m);

        <span class="green">// test the tetris limit</span>
        <span class="blue">if</span> (tetrisCount[from] >= <span class="red">7</span>)
        {
            <span class="green">// stop the game</span>
            out.add(<span class="blue">new</span> <span class="red">StopGameMessage</span>());

            <span class="green">// announce the winner</span>
            <span class="red">User</span> winner = getChannel().getPlayer(from);
            <span class="red">PlineMessage</span> announce = <span class="blue">new</span> <span class="red">PlineMessage</span>();
            announce.setKey(<span class="gray">"channel.player_won"</span>,
                            <span class="blue">new</span> <span class="red">Object</span>[] { winner.getName() });
            out.add(announce);
        }
    }

</div>

<p>Our filter is complete, now we have to compile and deploy it in the server.</p>


<h3>Compile the filter</h3>

<p>Save the code above in a <tt>TetrisFilter.java</tt> file and copy the
<tt>jetrix.jar</tt> file in the same directory (this jar is in the <tt>jetrix/lib</tt>
directory of the jetrix distribution). Then compile the command with :</p>

<div class="code">
javac -classpath jetrix.jar TetrisFilter.java

</div>

<h3>Deploy the filter</h3>

<p>To make the filter available to Jetrix just copy the <tt>TetrisFilter.class</tt>
file into the <tt>jetrix/lib</tt> directory. Then you need to declare your
filter by editing the <tt>config.xml</tt> file, under the <tt>&lt;filter-definitions&gt;</tt>
element add this line:</p>

<div class="code">
    &lt;<span class="blue">alias</span> <span class="red">name</span>=<span class="gray">"7tetris"</span> <span class="red">class</span>=<span class="gray">"TetrisFilter"</span>/&gt;

</div>

<p>Then the filter has to be associated to a channel, we will create a new channel
dedicated to this mod :</p>

<div class="code">
    &lt;<span class="blue">channel</span> <span class="red">name</span>=<span class="gray">"7tetris"</span>&gt;
      &lt;<span class="blue">description</span>&gt;7 tetris to win!&lt;/<span class="blue">description</span>&gt;
      &lt;<span class="blue">filters</span>&gt;
        &lt;<span class="blue">filter</span> <span class="red">name</span>=<span class="gray">"7tetris"</span>/&gt;
      &lt;/<span class="blue">filters</span>&gt;
    &lt;/<span class="blue">channel</span>&gt;

</div>

<p>You can also declare a global filter under the <tt>default-filters</tt>
element that will be applied to all channels.</p>

<h3>Test the filter !</h3>

You can now try the game modification, start Jetrix, bring some friends, join
the <tt>7tetris</tt> channel and show who is the fastest ! This filter could be
improved in many ways, for example it could announce who has taken the
lead, how many tetris are left, it could send attacks such as a quakefield or a
random clear to the leader, etc... The only limit is your imagination :)



<h2><a id="section3-3"></a>Winlists</h2>


<? include("footer.inc.php") ?>
