<%@ page import="net.jetrix.config.*,
                 java.util.Locale"%>

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
<%  for (Special special : Special.values()) { %>
        <tr>
          <td align="center" style="border-right: 0px" width="10%"><img src="/images/specials/<%= special.getLetter() %>.png" alt="<%= String.valueOf(special.getLetter()).toUpperCase() %>"></td>
          <td><%= special.getName(Locale.ENGLISH) %></td>
          <td align="center"><input class="thin <%= settings.isDefaultSpecialOccurancy() ? "default" : "" %>" type="text" value="<%= settings.getOccurancy(special) %>" name="<%= special.getCode() %>" style="width: 30px" onchange="updateSpecials()"> %</td>
        </tr>
<%  } %>
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
          <th width="70%">Block</th>
          <th width="30%">Occurancy</th>
        </tr>
<%  for (Block block : Block.values()) { %>
        <tr>
          <td align="center" width="10%"><img src="/images/blocks/<%= block.getCode() %>.png" alt="<%= block.getName(Locale.ENGLISH) %>"></td>
          <td align="center"><input class="thin <%= settings.isDefaultBlockOccurancy() ? "default" : "" %>" type="text" value="<%= settings.getOccurancy(block) %>" name="<%= block.getCode() %>" style="width: 30px" onchange="updateBlocks()"> %</td>
        </tr>
<%  } %>
        <tr>
          <td align="right"><b>Total :</b> </td>
          <td align="center"><span id="blocks.total">100</span> %</td>
        </tr>
      </table>

      <h2>Sudden Death</h2>

      <table class="thin" style="width: 100%">
        <tr>
          <th width="30%">Property</th>
          <th width="70%">Value</th>
        </tr>
        <tr>
          <td>Time (seconds)</td>
          <td align="center"><input class="thin <%= settings.isDefaultSuddenDeathTime() ? "default" : "" %>" type="text" value="<%= settings.getSuddenDeathTime() %>" name="suddenDeathTime" style="width: 95%"></td>
        </tr>
        <tr>
          <td>Message</td>
          <td align="center"><input class="thin <%= settings.isDefaultSuddenDeathMessage() ? "default" : "" %>" type="text" value="<%= settings.getSuddenDeathMessage() %>" name="suddenDeathMessage" style="width: 95%"></td>
        </tr>
        <tr>
          <td>Delay (seconds)</td>
          <td align="center"><input class="thin <%= settings.isDefaultSuddenDeathDelay() ? "default" : "" %>" type="text" value="<%= settings.getSuddenDeathDelay() %>" name="suddenDeathDelay" style="width: 95%"></td>
        </tr>
        <tr>
          <td>Lines Added</td>
          <td align="center"><input class="thin <%= settings.isDefaultSuddenDeathLinesAdded() ? "default" : "" %>" type="text" value="<%= settings.getSuddenDeathLinesAdded() %>" name="suddenDeathLinesAdded" style="width: 95%"></td>
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
          <td align="center"><input class="thin <%= settings.isDefaultStartingLevel() ? "default" : "" %>" type="text" value="<%= settings.getStartingLevel() %>" name="startingLevel" style="width: 30px"></td>
        </tr>
        <tr>
          <td>Stack Height</td>
          <td align="center"><input class="thin <%= settings.isDefaultStackHeight() ? "default" : "" %>" type="text" value="<%= settings.getStackHeight() %>" name="stackHeight" style="width: 30px"></td>
        </tr>
        <tr>
          <td>Lines per Level</td>
          <td align="center"><input class="thin <%= settings.isDefaultLinesPerLevel() ? "default" : "" %>" type="text" value="<%= settings.getLinesPerLevel() %>" name="linesPerLevel" style="width: 30px"></td>
        </tr>
        <tr>
          <td>Lines per Special</td>
          <td align="center"><input class="thin <%= settings.isDefaultLinesPerSpecial() ? "default" : "" %>" type="text" value="<%= settings.getLinesPerSpecial() %>" name="linesPerSpecial" style="width: 30px"></td>
        </tr>
        <tr>
          <td>Level Increase</td>
          <td align="center"><input class="thin <%= settings.isDefaultLevelIncrease() ? "default" : "" %>" type="text" value="<%= settings.getLevelIncrease() %>" name="levelIncrease" style="width: 30px"></td>
        </tr>
        <tr>
          <td>Specials Added</td>
          <td align="center"><input class="thin <%= settings.isDefaultSpecialAdded() ? "default" : "" %>" type="text" value="<%= settings.getSpecialAdded() %>" name="specialAdded" style="width: 30px"></td>
        </tr>
        <tr>
          <td>Specials Capacity</td>
          <td align="center"><input class="thin <%= settings.isDefaultSpecialCapacity() ? "default" : "" %>" type="text" value="<%= settings.getSpecialCapacity() %>" name="specialCapacity" style="width: 30px"></td>
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
