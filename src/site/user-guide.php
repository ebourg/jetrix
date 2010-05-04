<? include("header.inc.php") ?>

<h1>User Guide</h1>

<h2>Table of Contents</h2>

<ol>
  <li><a href="#section1">Installation</a></li>
  <li><a href="#section2">Configuration</a>
    <ul>
      <li><a href="#section2-1">Listeners</a></li>
      <li><a href="#section2-2">Services</a></li>
      <li><a href="#section2-3">Channels</a></li>
      <li><a href="#section2-4">Commands</a></li>
      <li><a href="#section2-5">Filters</a></li>
      <li><a href="#section2-6">Banlist</a></li>
    </ul>
  </li>
  <li><a href="#section3">Web Administration</a></li>
  <li><a href="#section4">Command Reference</a></li>
</ol>

<h1><a id="section1"></a>Installation</h1>

<h3>System Requirements</h3>

<ul>
  <li>Linux, Windows, Solaris or MacOS X</li>
  <li>Java 6 or higher</li>
  <li>32 Mb RAM</li>
  <li>5 Mb hard drive space</li>
  <li>If you have a firewall, open the ports 31456, 31457 and 31458</li>
</ul>

<h3>Running &amp; Upgrading</h3>

<p>You need Java 6 or higher installed on your server to run Jetrix. You can
download it on <a href="http://java.com">http://java.com</a></p>


<h4>Unix</h4>

<p>Once the Java Runtime Environnement is installed, you'll need a JAVA_HOME
environnement variable pointing to your Java directory. For example on
Linux you can add this line to your /etc/profile file:</p>

<code>export JAVA_HOME=/usr/java/jre_1.6.0</code>

<p>Then decompress the Jetrix archive to the installation directory:</p>

<code>tar -jxvf jetrix-x.y.z.tar.bz2</code>

<p>To start the server, type the following commands in the jetrix directory:</p>

<code>./jetrix</code>

<p>To start the server as a background process, type:</p>

<code>nohup ./jetrix &amp;</code>

<p>Or even better if the screen tool is available on your system, type:</p>

<code>screen ./jetrix</code>

<p>To download the latest release and upgrade your version (experimental), type:</p>

<code>./update</code>

<h4>Windows</h4>

<p>Install the Java Runtime Environnement and then run the Jetrix installer (<tt>jetrix-x.y.z-installer.exe</tt>).
This will create a "Jetrix TetriNET Server" group in your Start menu, just click on "Jetrix TetriNET Server" to
start Jetrix, an icon will appear in the system tray:</p>

<center><img src="images/systray.png"/></center>

<p>You can click on this icon to open a menu and stop the server or open the administration console.</p>


<h1><a id="section2"></a>Configuration</h1>

<h2><a id="section2-1"></a>Listeners</h2>

A listener is a component listening for network connections. It waits for connections on a specific port, for example
the listener waiting for tspec clients listens on the port 31458. The listeners are declared in the server configuration
under the <tt>&lt;listeners&gt;</tt> element:

<div class="code">
  &lt;!-- Client listeners --&gt;
  &lt;listeners>
    &lt;listener class="net.jetrix.listeners.TetrinetListener"/&gt;
    &lt;listener class="net.jetrix.listeners.TSpecListener"/&gt;
    &lt;listener class="net.jetrix.listeners.IRCListener" port="31456"/&gt;
    &lt;listener class="net.jetrix.listeners.HttpListener" port="31460"/&gt;
  &lt;/listeners&gt;

</div>



<h2><a id="section2-2"></a>Services</h2>

<h2><a id="section2-3"></a>Channels</h2>

<h2><a id="section2-4"></a>Commands</h2>

<h2><a id="section2-5"></a>Filters</h2>

<h2><a id="section2-6"></a>Banlist</h2>


<h1><a id="section3"></a>Web Administration</h1>

<p>An administration console is integrated to Jetrix, you can use it with any
modern browser to change most of the configuration without restarting the 
server. The console is available on the port 31460 of your server. You'll be 
prompted for a username and a password, just type in <tt>admin</tt> and the
administrator password to enter (<tt>adminpass</tt> by default).</p>

<h1><a id="section4"></a>Command Reference</h1>

<? include("commands.html") ?>

<? include("footer.inc.php") ?>
