<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html" indent="yes"/>

  <xsl:template match="item">
    <div class="news">
    <h2><xsl:value-of select="title"/></h2>
    <div class="newsdate"><xsl:value-of select="pubDate"/></div>
    <div class="newstext"><xsl:value-of select="description"/></div>
    </div>
  </xsl:template>

  <xsl:template match="text()"/>

</xsl:stylesheet>
