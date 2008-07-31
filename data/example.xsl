<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xslutils="net.sourceforge.jvlt.XSLUtils">

	<xsl:variable name="orth_font_family"
		select="xslutils:fontFamily('orth_font')"/>
	<xsl:variable name="pron_font_family"
		select="xslutils:fontFamily('pron_font')"/>
	<xsl:variable name="font_family"
		select="xslutils:fontFamily('html_font')"/>
	<xsl:variable name="font_size"
		select="xslutils:fontSize('html_font')"/>

	<xsl:template match="Dict">
		<html>
		
		<head>
		<link href="style.css" rel="stylesheet" type="text/css"/>
		</head>

		<body style="font-family:{$font_family}; font-size:{$font_size}pt;">
		
		<div class="hbar"/>
		<table width="100%" class="box1">
		<tr>
		<td valign="top">
		<table cellpadding="0" cellspacing="0">
		<tr>
		<td>
		<xsl:apply-templates select="Example/TextFragments/Fragment"/>
		</td>
		<xsl:if test="string(Example/Translation)">
			<td valign="center" class="vbar"/>
			<td>
			<xsl:copy-of select="Example/Translation/child::node()"/>
			</td>
		</xsl:if>
		</tr>
		</table>
		</td>
		</tr>
		</table>
		<div class="hbar"/>

		<div style="margin-top:5pt;">
		<xsl:apply-templates select="Example/TextFragments/Fragment/Link"/>
		</div>
		
		</body>
		</html>
	</xsl:template>
	
	<xsl:template match="Fragment">
		<xsl:choose>
			<xsl:when test="Link">
				<a href="{Link}" class="link">
				<font style="font-family:{$orth_font_family};">
				<xsl:value-of disable-output-escaping="yes" select="Text"/>
				</font>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<font style="font-family:{$orth_font_family};">
				<xsl:copy-of select="Text/child::node()"/>
				</font>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="Link">
		<xsl:variable name="sid" select="."/>
		<xsl:variable name="entry" select="/Dict/Entry[Senses/Sense/ID=$sid]"/>
		<div style="margin-top:2pt;">
		<a href="{$sid}" class="link">
		<font style="font-family:{$orth_font_family};">
		<xsl:value-of disable-output-escaping="yes"
			select="$entry/Orthography"/>
		</font>
		</a>
		<xsl:if test="$entry/Pronunciations/item">
			<font style="font-family:{$pron_font_family};">
			[<xsl:call-template name="process-item-list">
				<xsl:with-param name="item-list"
					select="$entry/Pronunciations/item"/>
			</xsl:call-template>]
			</font>
		</xsl:if>
		- <xsl:apply-templates select="$entry/Senses/Sense[ID=$sid]"/>
		</div>
	</xsl:template>
	
	<xsl:template match="Sense">
		<xsl:if test="string(Definition)">
			<i>(<xsl:value-of disable-output-escaping="yes"
				select="Definition"/>)</i>
			<xsl:if test="string(Translation)">, </xsl:if>
		</xsl:if>
		<xsl:value-of disable-output-escaping="yes" select="Translation"/>
	</xsl:template>

	<xsl:template name="process-item-list">
		<xsl:param name="item-list"/>
		<xsl:for-each select="$item-list">
			<xsl:if test="position()>1">, </xsl:if>
			<xsl:value-of disable-output-escaping="yes" select="."/>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>

