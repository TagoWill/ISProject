
<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://sitedapixmania.pt">

  <xsl:template match="/">
    <html>
      <body>
        <h2>Smartphones</h2>
        <table border="1">
          <tr bgcolor="#9acd32">
            <th>brand</th>
            <th>name</th>
            <th>price</th>
          </tr>
          <xsl:for-each select="listofthings/info">
            <tr>
              <td><xsl:value-of select="brand"/></td>
              <td><xsl:value-of select="name"/></td>
              <td><xsl:value-of select="price"/></td>
            </tr>
          </xsl:for-each>
        </table>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet> 