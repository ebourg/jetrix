<? include("header.inc.php") ?>

<h1>Command guide - How to create a command</h1>

Jetrix has been designed to allow the addition of new commands like <tt>/who</tt>
or <tt>/list</tt> easily. This document will show you the steps to create a simple
command <tt>/hello</tt> that will just display the message <tt>"Hello World!"</tt>.

<h1>Write the command</h1>

Every command is represented by a Java class implementing the net.jetrix.commands.Command interface.
Let's create our class, HelloCommand :

<div class="code">
<span class="blue">import</span> java.util.*;
<span class="blue">import</span> net.jetrix.*;
<span class="blue">import</span> net.jetrix.messages.*;
<span class="blue">import</span> net.jetrix.commands.*;

<span class="blue">public class</span> HelloCommand <span class="blue">implements</span> <span class="red">Command</span>
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
        <span class="blue">return new</span> <span class="red">String</span>[] { <span class="grey">"hello"</span>, <span class="grey">"hi"</span> };
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
        <span class="blue">return</span> <span class="grey">"/hello"</span>;
    }

</div>

The <tt>getDescription(Locale locale)</tt> method is also used for the command
listing, it returns a short description of the command :

<div class="code">
    <span class="blue">public</span> <span class="red">String</span> getDescription(<span class="red">Locale</span> locale)
    {
        <span class="blue">return</span> <span class="grey">"Display 'Hello World!'"</span>;
    }

</div>

The <tt>getAccessLevel()</tt> defines the minimal access level required to use
the command. To allow everyone to use the command it should return 0, to restrict
it to operators only it would return 1.

<div class="code">
    <span class="blue">public int</span> getAccessLevel()
    {
        <span class="blue">return</span> <span class="red">0</span>;
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
        <span class="red">Message</span> hello = <span class="blue">new</span> <span class="red">PlineMessage</span>(<span class="grey">"Hello World!"</span>);
        message.getSource().sendMessage(hello);
    }

</div>

Our command is complete, let's put all the pieces together :

<div class="code">
<span class="blue">import</span> java.util.*;
<span class="blue">import</span> net.jetrix.*;
<span class="blue">import</span> net.jetrix.messages.*;
<span class="blue">import</span> net.jetrix.commands.*;

<span class="blue">public class</span> HelloCommand <span class="blue">implements</span> <span class="red">Command</span>
{
    <span class="blue">public</span> <span class="red">String</span>[] getAliases()
    {
        <span class="blue">return new</span> <span class="red">String</span>[] { <span class="grey">"hello"</span>, <span class="grey">"hi"</span> };
    }

    <span class="blue">public</span> <span class="red">String</span> getUsage(<span class="red">Locale</span> locale)
    {
        <span class="blue">return</span> <span class="grey">"/hello"</span>;
    }

    <span class="blue">public</span> <span class="red">String</span> getDescription(<span class="red">Locale</span> locale)
    {
        <span class="blue">return</span> <span class="grey">"Display 'Hello World!'"</span>;
    }

    <span class="blue">public int</span> getAccessLevel()
    {
        <span class="blue">return</span> <span class="red">0</span>;
    }

    <span class="blue">public void</span> execute(<span class="red">CommandMessage</span> message)
    {
        <span class="red">Message</span> hello = <span class="blue">new</span> <span class="red">PlineMessage</span>(<span class="grey">"Hello World!"</span>);
        message.getSource().sendMessage(hello);
    }
}

</div>


<h1>Compile the command</h1>

Save the code above in a <tt>HelloCommand.java</tt> file and copy the <tt>jetrix.jar</tt>
file in the same directory (this jar is in the <tt>jetrix/lib</tt> directory of
the jetrix distribution). Then compile the command with :

<div class="code">javac -classpath jetrix.jar HelloCommand.java</div>

<h1>Deploy the command</h1>

To make your class available to Jetrix just copy it into the <tt>jetrix/lib</tt>
directory. Starting with Jetrix 0.1.1 any <tt>.class</tt> or <tt>.jar</tt> file
in this directory is automatically loaded at startup. Then you need to declare
your command by editing the <tt>config.xml</tt> file, under the <tt>&lt;commands&gt;</tt>
element just add this :

<div class="code">
    &lt;<span class="blue">command</span> <span class="red">class</span>=<span class="grey">"HelloCommand"</span>/&gt;

</div>

<h1>Test the command !</h1>

Now we are ready to try the command ! Start Jetrix and log into the server. On
typing <tt>/help</tt> you'll notice that the new command is automatically listed :

<div style="margin: 1em"><img src="images/command-guide1.png" alt="HelloCommand - command listing" /></div>

To use the command just type <tt>/hello</tt> or <tt>/hi</tt>, you can also use
a partial name like <tt>/hel</tt> and mix upper and lower cases :

<div style="margin: 1em"><img src="images/command-guide2.png" alt="HelloCommand - usage" /></div>

Congratulations ! You have completed your first custom command :) If you create
a useful command feel free to submit it to the Jetrix project to make it available
to all Jetrix users, just send the code to <a href="mailto:smanux@lfjr.net">smanux@lfjr.net</a>.

<? include("footer.inc.php") ?>