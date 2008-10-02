<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xslutils="net.sourceforge.jvlt.XSLUtils">

<xsl:variable name="orth_font_family"
	select="xslutils:fontFamily('orth_font')"/>
<xsl:variable name="orth_font_size"
	select="xslutils:fontSize('orth_font', 24)"/>
<xsl:variable name="pron_font_family"
	select="xslutils:fontFamily('pron_font')"/>
<xsl:variable name="pron_font_size"
	select="xslutils:fontSize('pron_font')"/>
<xsl:variable name="font_family"
	select="xslutils:fontFamily('html_font')"/>
<xsl:variable name="font_size"
	select="xslutils:fontSize('html_font')"/>

<xsl:template match="Dict">
	<html>
	
	<head>
	<link href="style.css" rel="stylesheet" type="text/css"/>
	</head>
	
	<body class="gradient"
		style="font-family:{$font_family}; font-size:{$font_size}pt;">
	
	<div class="hbar"/>
	<table width="100%" class="box1">
	<tr>
	<td valign="top">
	<!--
	This td element contains original, pronunciation, categories and class
	of the word.
	-->
	<xsl:if test="string(Entry/Orthography)">
		<font style="font-family:{$orth_font_family}; font-size:{$orth_font_size}pt;">
		<xsl:value-of disable-output-escaping="yes" select="Entry/Orthography"/>
		</font>
	</xsl:if>
	<xsl:if test="Entry/Pronunciations/item">
		<font style="font-family:{$pron_font_family}; font-size:{$pron_font_size}pt;">
		[<xsl:call-template name="process-item-list">
			<xsl:with-param name="item-list"
				select="Entry/Pronunciations/item"/>
		</xsl:call-template>]
		</font>
	</xsl:if>
	<div style="margin-top:10;">
	<table cellspacing="2" cellpadding="1">
	<xsl:apply-templates select="Entry/Lesson"/>
	<xsl:apply-templates select="Entry/Categories"/>
	<xsl:apply-templates select="Entry/EntryClass"/>
	</table>
	</div>
	</td>

	<td align="right" valign="top">
	<!-- This td element contains the word statistics. -->
	<xsl:if test="string(Entry/NumQueried) or string(Entry/NumMistakes) or
		string(Entry/LastQueried) or string(Entry/Batch) or
		string(Entry/ExpireDate)">
		<xsl:call-template name="process-stats"/>
	</xsl:if>
	</td>
	</tr>
	
	<xsl:variable name="non_empty_attrs" select="Entry/Attribute[string(.)]"/>
	<xsl:if test="count($non_empty_attrs)>0">
		<tr>
		<td colspan="2">
		<xsl:call-template name="process-attributes">
			<xsl:with-param name="non_empty_attrs" select="$non_empty_attrs"/>
		</xsl:call-template>
		</td>
		</tr>
	</xsl:if>

	<xsl:if test="Entry/MultimediaFiles/item">
		<tr>
		<td colspan="2">
		<xsl:apply-templates select="Entry/MultimediaFiles"/>
		</td>
		</tr>
	</xsl:if>
	</table>
	<div class="hbar"/>
	
	<xsl:apply-templates select="Entry/Senses/Sense"/>

	</body>
	</html>
</xsl:template>

<xsl:template name="process-stats">
	<table cellspacing="2" cellpadding="1">
	<xsl:if test="string(Entry/NumQueried) and string(Entry/NumMistakes)">
		<tr>
		<td><i>
		<xsl:value-of select="xslutils:i18nString('known_quizzed')"/>:
		</i></td>
		<td>
		<xsl:variable name="known"
			select="Entry/NumQueried - Entry/NumMistakes"/>
		<xsl:variable name="quizzed" select="Entry/NumQueried"/>
		<xsl:value-of select="$known"/>/
		<xsl:value-of select="$quizzed"/> 
		<xsl:if test="$quizzed>0">
			(<xsl:value-of
				select="format-number($known div $quizzed,'#%')"/>)
		</xsl:if>
		</td>
		</tr>
	</xsl:if>
	<xsl:if test="string(Entry/LastQueried)">
		<tr>
		<td><i>
		<xsl:value-of select="xslutils:i18nString('last_quizzed')"/>:
		</i></td>
		<td>
		<xsl:choose>
			<xsl:when test="Entry/NumQueried > 0 and string(Entry/LastQueried)">
				<xsl:value-of select="xslutils:formatDate(Entry/LastQueried)"/>
			</xsl:when>
			<xsl:otherwise>-</xsl:otherwise>
		</xsl:choose>
		</td>
		</tr>
	</xsl:if>
	<xsl:if test="string(Entry/Batch)">
		<tr>
		<td><i>
		<xsl:value-of select="xslutils:i18nString('batch')"/>:
		</i></td>
		<td>
		<xsl:value-of select="Entry/Batch"/>
		</td>
		</tr>
	</xsl:if>
	<xsl:if test="string(Entry/ExpireDate)">
		<tr>
		<td>
		<xsl:choose>
			<xsl:when test="xslutils:expired(Entry/ExpireDate)='true'">
				<i>
				<xsl:value-of select="xslutils:i18nString('expired_on')"/>:
				</i>
			</xsl:when>
			<xsl:otherwise>
				<i>
				<xsl:value-of select="xslutils:i18nString('expires_on')"/>:
				</i>
			</xsl:otherwise>
		</xsl:choose>
		</td>
		<td>
		<xsl:value-of select="xslutils:formatDate(Entry/ExpireDate)"/>
		</td>
		</tr>
	</xsl:if>
	</table>
</xsl:template>

<xsl:template name="process-attributes">
	<xsl:param name="non_empty_attrs"/>
	<xsl:choose>
		<xsl:when test="count($non_empty_attrs)>1">
			<!-- Use two columns if we have many attributes. -->
			<xsl:variable name="attr_rows"
				select="ceiling(count($non_empty_attrs) div 2)"/>
			<table cellspacing="0" cellpadding="0">
			<tr>
			<td valign="top">
			<table cellspacing="2" cellpadding="1">
			<xsl:apply-templates
				select="$non_empty_attrs[position() &lt;= $attr_rows]"/>
			</table>
			</td>
			<td valign="center" class="vbar"/>
			<td valign="top">
			<table cellspacing="2" cellpadding="1">
			<xsl:apply-templates
				select="$non_empty_attrs[position() &gt; $attr_rows]"/>
			</table>
			</td>
			</tr>
			</table>
		</xsl:when>
		<xsl:otherwise>
			<table cellspacing="2" cellpadding="1">
			<xsl:apply-templates select="Entry/Attribute"/>
			</table>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="Sense">
	<div style="margin-top:5pt;">
	<xsl:if test="count(../Sense)>1">
		<xsl:value-of select="position()"/>.
	</xsl:if>
	<xsl:if test="string(Definition)">
		<i>(<xsl:value-of disable-output-escaping="yes"
			select="Definition"/>)</i>
		<xsl:if test="string(Translation)">, </xsl:if>
	</xsl:if>
	<xsl:value-of disable-output-escaping="yes" select="Translation"/>
	<xsl:variable name="entry-id" select="../../ID"/>
	<xsl:variable name="sense-id" select="ID"/>
	<xsl:for-each select="/Dict/Example[TextFragments/Fragment/Link=$sense-id]">
		<xsl:call-template name="process-example">
			<xsl:with-param name="example-node" select="current()"/>
			<xsl:with-param name="sense-id" select="$sense-id"/>
		</xsl:call-template>
	</xsl:for-each>
	</div>
</xsl:template>
	
<xsl:template name="process-example">
	<xsl:param name="example-node"/>
	<xsl:param name="sense-id"/>
	<table cellpadding="0" cellspacing="0"
		style="margin-top:2pt; font-family:{$orth_font_family}">
	<tr>
	<td width="25" valign="top">
	<img src="/images/bullet.png" width="16" height="16"/>
	</td>
	<td>
	<xsl:for-each select="$example-node/TextFragments/Fragment">
		<xsl:choose>
			<xsl:when test="string(Link)">
				<xsl:choose>
					<xsl:when test="$sense-id=string(current()/Link)">
					<a href="{Link}" class="selflink"><xsl:value-of
						disable-output-escaping="yes" select="Text"/></a>
					</xsl:when>
					<xsl:otherwise>
					<a href="{Link}" class="link"><xsl:value-of
						disable-output-escaping="yes" select="Text"/></a>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="Text/child::node()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:for-each>
	</td>
	<xsl:if test="string($example-node/Translation)">
		<td valign="center" class="vbar"/>
		<td>
		<xsl:copy-of select="$example-node/Translation/child::node()"/>
		</td>
	</xsl:if>
	<td style="white-space:nowrap;">
	[<a href="{$example-node/ID}" class="link">
		<xsl:value-of select="xslutils:i18nString('edit')"/></a>]
	</td>
	</tr>
	</table>
</xsl:template>

<xsl:template name="process-item-list">
	<xsl:param name="item-list"/>
	<xsl:for-each select="$item-list">
		<xsl:if test="position()>1">, </xsl:if>
		<xsl:value-of disable-output-escaping="yes" select="."/>
	</xsl:for-each>
</xsl:template>

<xsl:template match="Lesson">
	<xsl:if test="string(.)">
		<tr>
		<td>
		<xsl:value-of select="xslutils:i18nString('lesson')"/>:
		</td>
		<td>
		<xsl:value-of select="."/>
		</td>
		</tr>
	</xsl:if>
</xsl:template>

<xsl:template match="Categories">
	<xsl:if test="count(item)>0">
		<tr>
		<td>
		<xsl:choose>
			<xsl:when test="count(item)=1">
				<i>
				<xsl:value-of select="xslutils:i18nString('category')"/>:
				</i>
			</xsl:when>
			<xsl:otherwise>
				<i>
				<xsl:value-of select="xslutils:i18nString('categories')"/>:
				</i>
			</xsl:otherwise>
		</xsl:choose>
		</td>
		<td>
			<xsl:call-template name="process-item-list">
				<xsl:with-param name="item-list" select="./item"/>
			</xsl:call-template>
		</td>
		</tr>
	</xsl:if>
</xsl:template>

<xsl:template match="EntryClass">
	<xsl:if test="string(.)">
		<tr>
		<td><i>
		<xsl:value-of select="xslutils:i18nString('class')"/>:
		</i></td>
		<td>
		<xsl:value-of select="xslutils:translate(.)"/>
		</td>
		</tr>
	</xsl:if>
</xsl:template>

<xsl:template match="Attribute">
	<xsl:if test="string(.)">
		<tr>
		<td><i>
		<xsl:value-of select="xslutils:translate(@name)"/>:
		</i></td>
		<td>
		<xsl:value-of select="."/>
		</td>
		</tr>
	</xsl:if>
</xsl:template>

<xsl:template match="MultimediaFiles">
	<xsl:for-each select="item">
		<xsl:variable name="image" select="xslutils:mimeTypeImage(.)"/>
		<xsl:variable name="file" select="."/>
		<a href="mm:{$file}"><img src="{$image}" border="0"/></a>
	</xsl:for-each>
</xsl:template>

</xsl:stylesheet>

