<? include("header.inc.php") ?>
      
<h1>Changelog</h1>

<pre>
Changes in version 0.1.2 (2003-08-03)
-------------------------------------
- new commands : /move, /goto, /petition, /tmsg, /speclist
- new game mod : 7tetris
- basic web administration console available on port 8080 (read only)
- basic tspec support
- implemented the winlists
- implemented the tetrinetx query protocol
- the connection timeout is now enabled
- implemented the ban list (hosts only)
- italian translation contributed by Claudio Gargiulo
- german translation contributed by Mario Meuser

Changes in version 0.1.1 (2003-03-02)
-------------------------------------
- new commands : /start, /stop, /pause, /random, /reply
- "/start &lt;n&gt;" will run a countdown for n seconds
- the admin console can now use all /commands available to the clients
- the winner is announced at the end of the game
- any jar or class in the ./lib directory is now loaded at startup
- the number of concurrent connections from the same host can be limited (the default value is 2)
- channel access can now be restricted by access level
- channels can now be protected by a password
- the /join command now accepts channel numbers as argument

Changes in version 0.1.0 (2002-11-16)
-------------------------------------
- added support for tetrifast clients
- new commands : /teleport, /summon, /ping and /ip
- level change is now working
- full internationalization support
- added french support
- added dutch support (contributed by Tim Van Wassenhove)
- made the colors &amp; styles protocol independant

Changes in version 0.0.10 (2002-09-28)
--------------------------------------
- implemented the client repository
- nickname uniqueness is now checked on logging
- incomming clients are now rejected when the server is full
- implemented the pluggable command system
- implemented the /who command
- implemented the /tell command (/msg and /cmsg are aliases)
- implemented the /op command
- implemented the /kick command
- implemented the /broadcast command
- implemented the /time command
- implemented the /motd command
- implemented the /emote command
- commands can now be invoked using their partial name (/ver, /t, etc...)
- reduced server startup time
- clients are now properly disconnected on server shutdown

Changes in version 0.0.9 (2002-06-23)
-------------------------------------
- improved channel switching
- implemented the game pause
- the end of the game in now detected
- improved the configuration system
- implemented the channel filter system
- filter: spam blocker
- filter: game auto-start when players say "go"
- filter: special block multiplier
- added the /conf command to display the channel settings
- added server log files
- added a debug mode (run JetriX with the -Djetrix.debug=true parameter)
- now displaying a message upon player disconnection
- implemented a special block check to prevent forged messages to crash clients
- added the /version command
- added a source distribution
- added a more unix friendly .tar.gz distribution

Changes in version 0.0.8 (2002-03-26)
-------------------------------------
- added the configuration file config.xml
- implemented multi-channel
- added the /list and /join commands

</pre>

<? include("footer.inc.php") ?>