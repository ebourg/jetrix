<%@ page import="net.jetrix.config.*"%>

<%
    Settings settings = (Settings) request.getAttribute("settings");    
%>

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
          <td align="center"><input class="thin" type="text" value="<%= settings.getSpecialOccurancy(Settings.SPECIAL_ADDLINE) %>" name="addline" style="width: 30px"> %</td>
        </tr>
        <tr>
          <td align="center" style="border-right: 0px" width="10%"><img src="/images/specials/c.png" alt="C"></td>
          <td>Clear Line</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getSpecialOccurancy(Settings.SPECIAL_CLEARLINE) %>" name="clearline" style="width: 30px"> %</td>
        </tr>
        <tr>
          <td align="center" style="border-right: 0px" width="10%"><img src="/images/specials/n.png" alt="N"></td>
          <td>Nuke Field</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getSpecialOccurancy(Settings.SPECIAL_NUKEFIELD) %>" name="clearline" style="width: 30px"> %</td>
        </tr>
        <tr>
          <td align="center" style="border-right: 0px" width="10%"><img src="/images/specials/r.png" alt="R"></td>
          <td>Random Clear</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getSpecialOccurancy(Settings.SPECIAL_RANDOMCLEAR) %>" name="clearline" style="width: 30px"> %</td>
        </tr>
        <tr>
          <td align="center" style="border-right: 0px" width="10%"><img src="/images/specials/s.png" alt="S"></td>
          <td>Switch Field</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getSpecialOccurancy(Settings.SPECIAL_SWITCHFIELD) %>" name="clearline" style="width: 30px"> %</td>
        </tr>
        <tr>
          <td align="center" style="border-right: 0px" width="10%"><img src="/images/specials/b.png" alt="B"></td>
          <td>Clear Specials</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getSpecialOccurancy(Settings.SPECIAL_CLEARSPECIAL) %>" name="clearline" style="width: 30px"> %</td>
        </tr>
        <tr>
          <td align="center" style="border-right: 0px" width="10%"><img src="/images/specials/g.png" alt="G"></td>
          <td>Gravity</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getSpecialOccurancy(Settings.SPECIAL_GRAVITY) %>" name="clearline" style="width: 30px"> %</td>
        </tr>
        <tr>
          <td align="center" style="border-right: 0px" width="10%"><img src="/images/specials/o.png" alt="O"></td>
          <td>Quake Field</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getSpecialOccurancy(Settings.SPECIAL_QUAKEFIELD) %>" name="clearline" style="width: 30px"> %</td>
        </tr>        
        <tr>
          <td align="center" style="border-right: 0px" width="10%"><img src="/images/specials/q.png" alt="Q"></td>
          <td>Block Bomb</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getSpecialOccurancy(Settings.SPECIAL_BLOCKBOMB) %>" name="clearline" style="width: 30px"> %</td>
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
          <td align="center"><input class="thin" type="text" value="<%= settings.getBlockOccurancy(Settings.BLOCK_LINE) %>" name="" style="width: 30px"> %</td>
        </tr>
        <tr>
          <td align="center" width="10%"><img src="/images/blocks/square.png" alt="Square"></td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getBlockOccurancy(Settings.BLOCK_SQUARE) %>" name="" style="width: 30px"> %</td>
        </tr>
        <tr>
          <td align="center" width="10%"><img src="/images/blocks/leftl.png" alt="Left L"></td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getBlockOccurancy(Settings.BLOCK_LEFTL) %>" name="" style="width: 30px"> %</td>
        </tr>
        <tr>
          <td align="center" width="10%"><img src="/images/blocks/rightl.png" alt="Right L"></td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getBlockOccurancy(Settings.BLOCK_RIGHTL) %>" name="" style="width: 30px"> %</td>
        </tr>
        <tr>
          <td align="center" width="10%"><img src="/images/blocks/leftz.png" alt="Left Z"></td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getBlockOccurancy(Settings.BLOCK_LEFTZ) %>" name="" style="width: 30px"> %</td>
        </tr>
        <tr>
          <td align="center" width="10%"><img src="/images/blocks/rightz.png" alt="Right Z"></td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getBlockOccurancy(Settings.BLOCK_RIGHTZ) %>" name="" style="width: 30px"> %</td>
        </tr>
        <tr>
          <td align="center" width="10%"><img src="/images/blocks/halfcross.png" alt="Half Cross"></td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getBlockOccurancy(Settings.BLOCK_HALFCROSS) %>" name="" style="width: 30px"> %</td>
        </tr>
        <tr>
          <td align="right"><b>Total :</b> </td>
          <td align="center"><span id="specials.total">100</span> %</td>
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
          <td align="center"><input class="thin" type="text" value="<%= settings.getStartingLevel() %>" name="" style="width: 30px"></td>
        </tr>
        <tr>
          <td>Stack Height</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getStackHeight() %>" name="" style="width: 30px"></td>
        </tr>
        <tr>
          <td>Lines per Level</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getLinesPerLevel() %>" name="" style="width: 30px"></td>
        </tr>
        <tr>
          <td>Lines per Special</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getLinesPerSpecial() %>" name="" style="width: 30px"></td>
        </tr>
        <tr>
          <td>Level Increase</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getLevelIncrease() %>" name="" style="width: 30px"></td>
        </tr>
        <tr>
          <td>Specials Added</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getSpecialAdded() %>" name="" style="width: 30px"></td>
        </tr>
        <tr>
          <td>Specials Capacity</td>
          <td align="center"><input class="thin" type="text" value="<%= settings.getSpecialCapacity() %>" name="" style="width: 30px"></td>
        </tr>
        <tr>
          <td>Average Levels</td>
          <td align="center">
            <label><input type="radio" value="true"  name="averageLevels"> Yes</label>
            <label><input type="radio" value="false" name="averageLevels"> No</label>
          </td>
        </tr>
        <tr>
          <td>Classic Rules</td>
          <td align="center">
            <label><input type="radio" value="true"  name="classicRules"> Yes</label>
            <label><input type="radio" value="false" name="classicRules"> No</label>
          </td>
        </tr>
      </table>


    </td>
  </tr>
</table>
    
<br>
    
<input type="button" value="Save Changes">
