<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
<xsl:template match="stats">
	<stats version="{@version}">
	<xsl:copy-of select="entry-info"/>
	</stats>
</xsl:template>
	
</xsl:stylesheet>
