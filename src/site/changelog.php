<? include("header.inc.php") ?>
      
<h1>Changelog</h1>

<pre>
Changes in version 0.1.3 (2004-01-20)
-------------------------------------
- fixed the query protocol (thanks to ekn for the debugging info)
- tspec clients can now speak on channels
- the list of available languages can be displayed with /lang
- the new score &amp; rank of the winner is now announced in the channel
- fields are now updated on joining a channel (bug 808507)
- Jetrix can now read and write tetrinetx winlists
- winlists now accept initialization parameters like filters
- implemented the new tetrinet 1.14 block synchronization protocol
- the /join command now accepts a partial channel name as argument
- reduced the size of the jetrix distribution by 25%
- web admin: channel settings editing implemented
- web admin: basic server parameters implemented
- web admin: kick/ban implemented
- web admin: added a field tab in the channel view
- added a topic to the channels
- added a "port" attribute to the "listener" element in config.xml
- spanish translation contributed by Julian Mesa Llopis &amp; Bryan Reynaert

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
- added a debug mode (run Jetrix with the -Djetrix.debug=true parameter)
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