<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xslutils="net.sourceforge.jvlt.XSLUtils">

	<xsl:template match="info">
		<html>
		<body style="xslutils:fontStyle('html_font')">
		<div align="center">
		<img src="jvlt.png" alt="jVLT logo" width="128" height="128"/>
		</div>
		<p align="center" style="font-size:large;">
		<xsl:value-of select="name"/>
		</p>
		<p align="center">
		<xsl:value-of select="xslutils:i18nString('version')"/>:
		<xsl:value-of select="version"/>
		</p>
		<p align="center">
		<xsl:apply-templates select="author"/>
		</p>
		</body>
		</html>
	</xsl:template>

	<xsl:template match="author">
		<xsl:value-of select="name"/>
		<xsl:text> (</xsl:text>
			<xsl:value-of select="role"/>
		<xsl:text>)</xsl:text>
		<br/>
		<xsl:value-of select="e-mail/local-part"/>
		<xsl:text>@</xsl:text>
		<xsl:value-of select="e-mail/domain"/>
		<br/>
		<br/>
	</xsl:template>
</xsl:stylesheet>

