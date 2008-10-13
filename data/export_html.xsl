<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html" indent="yes"/>

<xsl:template match="dictionary">
	<html>
	<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1"/>
	</head>
	<body>
	<xsl:apply-templates select="entry"/>
	</body>
	</html>
</xsl:template>

<xsl:template match="entry">
	<p>
	<b><xsl:value-of select="orth"/></b>
	<xsl:text> </xsl:text>
	<xsl:if test="string(pron)">
		[<xsl:value-of select="."/>]
		<xsl:text> </xsl:text>
	</xsl:if>
	<xsl:apply-templates select="sense"/>
	</p>
</xsl:template>

<xsl:template match="sense">
	<xsl:if test="count(../sense)>1">
		<xsl:value-of select="position()"/>.
		<xsl:text> </xsl:text>
	</xsl:if>
	<xsl:if test="string(def)">
		<i>(<xsl:value-of select="def"/>)</i>
		<xsl:text> </xsl:text>
	</xsl:if>
	<xsl:if test="string(trans)">
		<xsl:value-of select="trans"/>
		<xsl:text> </xsl:text>
	</xsl:if>

	<xsl:variable name="sense-id" select="@id"/>
	<xsl:for-each select="/dictionary/example[ex/link/@sid=$sense-id]">
		<i>
		<span style="font-size:small;">
		<xsl:apply-templates select="ex/*|ex/text()">
	        <xsl:with-param name="sense-id" select="$sense-id"/>
		</xsl:apply-templates>

		<xsl:if test="string(tr)">
			- <xsl:value-of select="tr"/>
		</xsl:if>
		</span>
		</i>
		<xsl:text> </xsl:text>
	</xsl:for-each>
</xsl:template>

<xsl:template match="link">
	<xsl:param name="sense-id"/>
	<xsl:choose>
		<xsl:when test="@sid=$sense-id">
			<b><xsl:value-of select="."/></b>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="."/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>
