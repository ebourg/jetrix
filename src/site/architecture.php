<? include("header.inc.php") ?>
      
<h1>Architecture</h1>

Jetrix is designed around the concept of independant threads communicating asynchronously with messages. The three main entities are :
<ul>
  <li><b>client</b> - it listens for messages sent by the TetriNET client, it's coupled to a protocol that will translate incomming messages into server messages. The messages are then forwarded to the channel occupied by the client. There is one client thread for every client connected to the server.</li><br>
  <li><b>channel</b> - this is a game channel accepting playing and non-playing (spectators) clients. Messages go through a set of filters that can transform, delete or add messages to customize the behaviour of the channel. After a processing the game message are sent back to the clients. Non game related messages are forwarded to the main server thread.</li><br>
  <li><b>server</b> - it handles system commands such as shutdown and slash commands typed by clients (<tt>/who</tt>, <tt>/list</tt>, <tt>/join</tt>...)
</ul>

<div align="center"><img src="images/arch1.png" alt="Architecture - Message Flow"></div>

<? include("footer.inc.php") ?>