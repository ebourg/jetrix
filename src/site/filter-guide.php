<? include("header.inc.php") ?>

<h1>Filter guide - How to create a filter</h1>

<p>One feature of Jetrix is to make it easy to change the behaviour of the server by
modifying its response to the messages sent by the clients. This is done by
implementing filters. A filter is a small class that can watch and modify all
messages sent to a channel. It can serve various purposes, like modifying the
game, displaying additional informations at the end of the game, blocking a flood
of messages from a player, or implementing a bot responding to the players.</p>

<p>This guide will show you how to create a simple game modification using a filter.
In this mod the first player to complete 7 tetris win.</p>


<h1>Write the filter</h1>

<p>Filters extend the base class <tt>MessageFilter</tt>, this class defines a
<tt>process(Message m, List out)</tt> method that must be overridden to implement
the behaviour of the filter. A higher level filter <tt>GenericFilter</tt> with
process methods for all message types is provided, we will use it for our filter.</p>

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
            announce.setKey(<span class="grey">"channel.player_won"</span>,
                            <span class="blue">new</span> <span class="red">Object</span>[] { winner.getName() });
            out.add(announce);
        }
    }

</div>

<p>Our filter is complete, now we have to compile and deploy it in the server.</p>


<h1>Compile the filter</h1>

<p>Save the code above in a <tt>TetrisFilter.java</tt> file and copy the
<tt>jetrix.jar</tt> file in the same directory (this jar is in the <tt>jetrix/lib</tt>
directory of the jetrix distribution). Then compile the command with :</p>

<div class="code">
javac -cp jetrix.jar TetrisFilter.java

</div>

<h1>Deploy the filter</h1>

<p>To make the filter available to Jetrix just copy the <tt>TetrisFilter.class</tt>
file into the <tt>jetrix/lib</tt> directory. Then you need to declare your
filter by editing the <tt>config.xml</tt> file, under the <tt>&lt;filter-definitions&gt;</tt>
element add this line:</p>

<div class="code">
    &lt;<span class="blue">alias</span> <span class="red">name</span>=<span class="grey">"7tetris"</span> <span class="red">class</span>=<span class="grey">"TetrisFilter"</span>/&gt;

</div>

<p>Then the filter has to be associated to a channel, we will create a new channel
dedicated to this mod :</p>

<div class="code">
    &lt;<span class="blue">channel</span> <span class="red">name</span>=<span class="grey">"7tetris"</span>&gt;
      &lt;<span class="blue">description</span>&gt;7 tetris to win!&lt;/<span class="blue">description</span>&gt;
      &lt;<span class="blue">filters</span>&gt;
        &lt;<span class="blue">filter</span> <span class="red">name</span>=<span class="grey">"7tetris"</span>/&gt;
      &lt;/<span class="blue">filters</span>&gt;
    &lt;/<span class="blue">channel</span>&gt;

</div>

<p>You can also declare a global filter under the <tt>default-filters</tt>
element that will be applied to all channels.</p>

<h1>Test the filter !</h1>

You can now try the game modification, start Jetrix, bring some friends, join
the <tt>7tetris</tt> channel and show who is the fastest ! This filter could be
improved in many ways, for example it could announce who has taken the
lead, how many tetris are left, it could send attacks such as a quakefield or a
random clear to the leader, etc... The only limit is your imagination :)

<? include("footer.inc.php") ?>