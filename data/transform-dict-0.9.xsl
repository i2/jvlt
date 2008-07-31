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
	<xsl:apply-templates select="example"/>
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
	<xsl:copy-of select="orth"/>
	<xsl:copy-of select="pron"/>
	<xsl:copy-of select="category"/>
	<xsl:copy-of select="multimedia"/>
	<xsl:apply-templates select="sense">
		<xsl:with-param name="entry_id" select="@id"/>
	</xsl:apply-templates>
	</entry>
</xsl:template>

<xsl:template match="sense">
	<xsl:param name="entry_id"/>
	<sense>
	<xsl:attribute name="id">
		<xsl:value-of select="$entry_id"/>-s<xsl:value-of select="position()"/>
	</xsl:attribute>
	<xsl:copy-of select="trans"/>
	<xsl:copy-of select="def"/>
	</sense>
</xsl:template>

<xsl:template match="example">
	<example>
	<xsl:attribute name="id">x<xsl:value-of select="substring(@id,2)"/>
	</xsl:attribute>
	<ex>
	<xsl:apply-templates select="ex"/>
	</ex>
	<xsl:copy-of select="tr"/>
	</example>
</xsl:template>

<xsl:template match="link">
	<link>
	<xsl:attribute name="sid">
		<xsl:value-of select="@eid"/>-<xsl:value-of select="@sid"/>
	</xsl:attribute>
	<xsl:value-of select="."/>
	</link>
</xsl:template>
	
</xsl:stylesheet>

