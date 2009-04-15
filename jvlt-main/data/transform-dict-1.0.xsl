<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
<xsl:template match="dictionary">
	<dictionary>
	<xsl:attribute name="version">1.0</xsl:attribute>
	<xsl:attribute name="language">
		<xsl:value-of select="@language"/>
	</xsl:attribute>
	<xsl:apply-templates select="entry"/>
	<xsl:copy-of select="example"/>
	</dictionary>
</xsl:template>

<xsl:template match="entry">
	<entry>
	<xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
	<xsl:if test="details/child::*">
		<xsl:attribute name="class">
			<xsl:value-of select="name(details/child::*)"/>
		</xsl:attribute>
		<xsl:for-each select="details/child::*/descendant::*[not(child::*)]">
			<attr>
			<xsl:attribute name="name">
				<xsl:value-of select="name(current())"/>
			</xsl:attribute>
			<xsl:value-of select="current()"/>
			</attr>
		</xsl:for-each>
	</xsl:if>
	<xsl:if test="@class">
		<xsl:attribute name="class">
			<xsl:value-of select="@class"/>
		</xsl:attribute>
	</xsl:if>
	<xsl:copy-of select="orth"/>
	<xsl:copy-of select="pron"/>
	<xsl:copy-of select="category"/>
	<xsl:copy-of select="multimedia"/>
	<xsl:copy-of select="sense"/>
	<xsl:copy-of select="attr"/>
	</entry>
</xsl:template>
	
</xsl:stylesheet>

