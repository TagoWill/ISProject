<?xml version="1.0" encoding="UTF-8"?>
<html xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsl:version="1.0">
  <!-- encoding="ISO-8859-9" -->
  <head><title>Smartphone INFO</title>
  </head>
  <body>
    <h2>Smartphones</h2>
      <table border="1">
        <tr bgcolor="#FF8C00">
          <th>Brand</th>
          <th>Name</th>
          <th>Price</th>
          <th>Operating System</th>
          <th>Processor</th>
          <th>Screen</th>
          <th>Cameras</th>
          <th>Internal Memory</th>
          <th>Battery</th>
          <th>Weight</th>
        </tr>
        <xsl:for-each select="listofsmartphones/smartphone">
          <tr>
            <td><xsl:value-of select="brand"/></td>
            <td><xsl:value-of select="name"/></td>
            <td><xsl:value-of select="price"/></td>
            <td><xsl:value-of select="extrainfo[@category='sistema operativo']/@description"/></td>
            <td><xsl:value-of select="extrainfo[@category='processador']/@description"/></td>
            <td><xsl:value-of select="extrainfo[@category='tamanho do ecrã']/@description"/></td>
            <td><xsl:value-of select="extrainfo[@category='resolução máxima (em pixeis)']/@description"/></td>
            <td><xsl:value-of select="extrainfo[@category='memória interna']/@description"/></td>
            <td><xsl:value-of select="extrainfo[@category='bateria']/@description"/></td>
            <td><xsl:value-of select="extrainfo[@category='peso']/@description"/></td>
          </tr>
        </xsl:for-each>
      </table>
    </body>
</html>