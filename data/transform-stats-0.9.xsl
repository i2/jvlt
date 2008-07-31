<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
<xsl:template match="stats">
	<stats>
	<xsl:attribute name="version">1.0</xsl:attribute>
	<xsl:apply-templates select="entry-info"/>
	</stats>
</xsl:template>

<xsl:template match="entry-info">
	<entry-info>
	<xsl:attribute name="entry-id">
		<xsl:value-of select="@entry-id"/>
	</xsl:attribute>
	<xsl:attribute name="queried">
		<xsl:value-of select="@queried"/>
	</xsl:attribute>
	<xsl:attribute name="mistakes">
		<xsl:value-of select="@mistakes"/>
	</xsl:attribute>
	<xsl:attribute name="batch">
		<xsl:value-of select="@batch"/>
	</xsl:attribute>
	<xsl:if test="@last-queried">
		<xsl:attribute name="last-queried">
			<xsl:value-of select="@last-queried"/> 00:00</xsl:attribute>
	</xsl:if>
	</entry-info>
</xsl:template>
	
</xsl:stylesheet>
