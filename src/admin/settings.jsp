<%@ page import="net.jetrix.config.*"%>

<%
    Settings settings = (Settings) request.getAttribute("settings");
    String channelName = (String) request.getAttribute("channel.name");
%>

<script type="text/javascript" src="/javascript/settings.js"></script>

<form id="settings" action="/servlet/net.jetrix.servlets.SettingsAction">

<%  if (channelName != null) { %>
  <input type="hidden" name="channel" value="<%= channelName %>">
<%  } %>

<table cellspacing="0" cellpadding="0" width="100%">
  <tr>
    <td width="30%" valign="top">

      <h2>Specials</h2>

      <table class="thin" style="width: 100%">
        <tr>
          <th width="70%" colspan="2">Special</th>
          <th width="30%">Occurancy</th>
        </tr>
        <tr>
          <td align="center" style="border-right: 0px" width="10%"><img src="/images/specials/a.png" alt="A"></td>
          <td>Add Line</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getSpecialOccurancy(Settings.SPECIAL_ADDLINE) %>" name="addLine" style="width: 30px" onchange="updateSpecials()"> %</td>
        </tr>
        <tr>
          <td align="center" style="border-right: 0px" width="10%"><img src="/images/specials/c.png" alt="C"></td>
          <td>Clear Line</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getSpecialOccurancy(Settings.SPECIAL_CLEARLINE) %>" name="clearLine" style="width: 30px" onchange="updateSpecials()"> %</td>
        </tr>
        <tr>
          <td align="center" style="border-right: 0px" width="10%"><img src="/images/specials/n.png" alt="N"></td>
          <td>Nuke Field</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getSpecialOccurancy(Settings.SPECIAL_NUKEFIELD) %>" name="nukeField" style="width: 30px" onchange="updateSpecials()"> %</td>
        </tr>
        <tr>
          <td align="center" style="border-right: 0px" width="10%"><img src="/images/specials/r.png" alt="R"></td>
          <td>Random Clear</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getSpecialOccurancy(Settings.SPECIAL_RANDOMCLEAR) %>" name="randomClear" style="width: 30px" onchange="updateSpecials()"> %</td>
        </tr>
        <tr>
          <td align="center" style="border-right: 0px" width="10%"><img src="/images/specials/s.png" alt="S"></td>
          <td>Switch Field</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getSpecialOccurancy(Settings.SPECIAL_SWITCHFIELD) %>" name="switchField" style="width: 30px" onchange="updateSpecials()"> %</td>
        </tr>
        <tr>
          <td align="center" style="border-right: 0px" width="10%"><img src="/images/specials/b.png" alt="B"></td>
          <td>Clear Specials</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getSpecialOccurancy(Settings.SPECIAL_CLEARSPECIAL) %>" name="clearSpecial" style="width: 30px" onchange="updateSpecials()"> %</td>
        </tr>
        <tr>
          <td align="center" style="border-right: 0px" width="10%"><img src="/images/specials/g.png" alt="G"></td>
          <td>Gravity</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getSpecialOccurancy(Settings.SPECIAL_GRAVITY) %>" name="gravity" style="width: 30px" onchange="updateSpecials()"> %</td>
        </tr>
        <tr>
          <td align="center" style="border-right: 0px" width="10%"><img src="/images/specials/q.png" alt="Q"></td>
          <td>Quake Field</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getSpecialOccurancy(Settings.SPECIAL_QUAKEFIELD) %>" name="quakeField" style="width: 30px" onchange="updateSpecials()"> %</td>
        </tr>        
        <tr>
          <td align="center" style="border-right: 0px" width="10%"><img src="/images/specials/o.png" alt="O"></td>
          <td>Block Bomb</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getSpecialOccurancy(Settings.SPECIAL_BLOCKBOMB) %>" name="blockBomb" style="width: 30px" onchange="updateSpecials()"> %</td>
        </tr>
        <tr>
          <td colspan="2" align="right"><b>Total :</b> </td>
          <td align="center"><span id="specials.total">100</span> %</td>
        </tr>        
      </table>

    </td>
    <td width="5%"></td>
    <td width="30%" valign="top">

      <h2>Blocks</h2>

      <table class="thin" style="width: 100%">
        <tr>
          <th width="70%">Special</th>
          <th width="30%">Occurancy</th>
        </tr>
        <tr>
          <td align="center" width="10%"><img src="/images/blocks/line.png" alt="Line"></td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getBlockOccurancy(Settings.BLOCK_LINE) %>" name="line" style="width: 30px" onchange="updateBlocks()"> %</td>
        </tr>
        <tr>
          <td align="center" width="10%"><img src="/images/blocks/square.png" alt="Square"></td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getBlockOccurancy(Settings.BLOCK_SQUARE) %>" name="square" style="width: 30px" onchange="updateBlocks()"> %</td>
        </tr>
        <tr>
          <td align="center" width="10%"><img src="/images/blocks/leftl.png" alt="Left L"></td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getBlockOccurancy(Settings.BLOCK_LEFTL) %>" name="leftL" style="width: 30px" onchange="updateBlocks()"> %</td>
        </tr>
        <tr>
          <td align="center" width="10%"><img src="/images/blocks/rightl.png" alt="Right L"></td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getBlockOccurancy(Settings.BLOCK_RIGHTL) %>" name="rightL" style="width: 30px" onchange="updateBlocks()"> %</td>
        </tr>
        <tr>
          <td align="center" width="10%"><img src="/images/blocks/leftz.png" alt="Left Z"></td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getBlockOccurancy(Settings.BLOCK_LEFTZ) %>" name="leftZ" style="width: 30px" onchange="updateBlocks()"> %</td>
        </tr>
        <tr>
          <td align="center" width="10%"><img src="/images/blocks/rightz.png" alt="Right Z"></td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getBlockOccurancy(Settings.BLOCK_RIGHTZ) %>" name="rightZ" style="width: 30px" onchange="updateBlocks()"> %</td>
        </tr>
        <tr>
          <td align="center" width="10%"><img src="/images/blocks/halfcross.png" alt="Half Cross"></td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getBlockOccurancy(Settings.BLOCK_HALFCROSS) %>" name="halfcross" style="width: 30px" onchange="updateBlocks()"> %</td>
        </tr>
        <tr>
          <td align="right"><b>Total :</b> </td>
          <td align="center"><span id="blocks.total">100</span> %</td>
        </tr>        
      </table>

    </td>
    <td width="5%"></td>
    <td width="30%" valign="top">

      <h2>Rules</h2>
     
      <table class="thin" style="width: 100%">
        <tr>
          <th width="60%">Rule</th>
          <th width="40%">Value</th>
        </tr>
        <tr>
          <td>Starting Level</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getStartingLevel() %>" name="startingLevel" style="width: 30px"></td>
        </tr>
        <tr>
          <td>Stack Height</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getStackHeight() %>" name="stackHeight" style="width: 30px"></td>
        </tr>
        <tr>
          <td>Lines per Level</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getLinesPerLevel() %>" name="linesPerLevel" style="width: 30px"></td>
        </tr>
        <tr>
          <td>Lines per Special</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getLinesPerSpecial() %>" name="linesPerSpecial" style="width: 30px"></td>
        </tr>
        <tr>
          <td>Level Increase</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getLevelIncrease() %>" name="levelIncrease" style="width: 30px"></td>
        </tr>
        <tr>
          <td>Specials Added</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getSpecialAdded() %>" name="specialAdded" style="width: 30px"></td>
        </tr>
        <tr>
          <td>Specials Capacity</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getSpecialCapacity() %>" name="specialCapacity" style="width: 30px"></td>
        </tr>
        <tr>
          <td>Average Levels</td>
          <td align="center">
            <label><input type="radio" value="true"  name="averageLevels" <%= settings.getAverageLevels() ? "checked" : "" %>> Yes</label>
            <label><input type="radio" value="false" name="averageLevels" <%= settings.getAverageLevels() ? "" : "checked" %>> No</label>
          </td>
        </tr>
        <tr>
          <td>Classic Rules</td>
          <td align="center">
            <label><input type="radio" value="true"  name="classicRules" <%= settings.getClassicRules() ? "checked" : "" %>> Yes</label>
            <label><input type="radio" value="false" name="classicRules" <%= settings.getClassicRules() ? "" : "checked" %>> No</label>
          </td>
        </tr>
        <tr>
          <td>Same Blocks</td>
          <td align="center">
            <label><input type="radio" value="true"  name="sameBlocks" <%= settings.getSameBlocks() ? "checked" : "" %>> Yes</label>
            <label><input type="radio" value="false" name="sameBlocks" <%= settings.getSameBlocks() ? "" : "checked" %>> No</label>
          </td>
        </tr>
      </table>


    </td>
  </tr>
</table>
    
<br>
    
<input type="submit" value="Save Changes">

</form>
