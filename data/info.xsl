<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xslutils="net.sourceforge.jvlt.XSLUtils">

	<xsl:template match="info">
		<xsl:variable name="font_family"
			select="xslutils:fontFamily('html_font')"/>
		<xsl:variable name="font_size"
			select="xslutils:fontSize('html_font')"/>
		<html>
		<body style="font-family:{$font_family}; font-size:{$font_size}pt;">
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
		<xsl:value-of select="name"/><br/>
		<xsl:value-of select="e-mail"/><br/>
		<br/>
	</xsl:template>
</xsl:stylesheet>

