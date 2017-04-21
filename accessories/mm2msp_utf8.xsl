<?xml version="1.0" encoding="UTF-8"?>
<!--
    (c) by Naoki Nose, 2006, and Eric Lavarde, 2008
    This code is licensed under the GPLv2 or later.
    (http://www.gnu.org/copyleft/gpl.html)
    Check 'mm2msp_utf8_TEMPLATE.mm' for detailed instructions on how to use
    this sheet.
--> 
<xsl:stylesheet version="1.0"
	xmlns="http://schemas.microsoft.com/project"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes" encoding="UTF-8" standalone="yes"/>

<xsl:key name="deps" match="arrowlink" use="@DESTINATION"/>

<xsl:template match="/">
<Project>
	<xsl:apply-templates/>
</Project>
</xsl:template>

<xsl:template match="//map">
	<Title><xsl:value-of select="node/@TEXT"/></Title>
	<xsl:apply-templates select="node/attribute">
		<xsl:with-param name="prefix" select="'prj'"/>
	</xsl:apply-templates>
	<Tasks>
		<xsl:apply-templates select="node" mode="tasks"/>
	</Tasks>
</xsl:template>

<xsl:template match="node" mode="tasks">
<xsl:param name="level" select="0"/>
	<Task>
		<UID><xsl:if test="$level > 0">
			<xsl:number level="any" count="//map/node//node"
				format="1"/></xsl:if>
			<xsl:if test="$level = 0">0</xsl:if></UID>
		<xsl:call-template name="output-node-text-as-name"/>
		<xsl:call-template name="output-note-text-as-notes"/>
		<OutlineLevel><xsl:value-of select="$level"/></OutlineLevel>
		<xsl:if test="not(attribute[@NAME = 'tsk-FixedCostAccrual'])">
		<FixedCostAccrual>1</FixedCostAccrual>
		</xsl:if>
		<xsl:apply-templates select="attribute">
			<xsl:with-param name="prefix" select="'tsk'"/>
		</xsl:apply-templates>
		<xsl:for-each select="key('deps',@ID)">
			<xsl:call-template name="output-arrow-as-predecessor">
				<xsl:with-param name="level" select="$level"/>
			</xsl:call-template>
		</xsl:for-each>
	</Task>
	<xsl:apply-templates mode="tasks">
		<xsl:with-param name="level" select="$level + 1"/>
	</xsl:apply-templates>
</xsl:template>

<xsl:template name="output-node-text-as-name">
	<Name><xsl:choose>
	<xsl:when test="@TEXT">
		<xsl:value-of select="normalize-space(@TEXT)" />
	</xsl:when>
	<xsl:when test="richcontent[@TYPE='NODE']">
		<xsl:value-of
		select="normalize-space(richcontent[@TYPE='NODE']/html/body)" />
	</xsl:when>
	<xsl:otherwise>
		<xsl:text></xsl:text>
	</xsl:otherwise>
	</xsl:choose></Name>
</xsl:template>

<xsl:template name="output-note-text-as-notes">
	<xsl:if test="richcontent[@TYPE='NOTE']">
		<Notes><xsl:value-of
			select="string(richcontent[@TYPE='NOTE']/html/body)" />
		</Notes>
	</xsl:if>
</xsl:template>

<xsl:template name="output-arrow-as-predecessor">
<xsl:param name="level" select="0"/>
<PredecessorLink>
	<PredecessorUID><xsl:if test="$level > 0">
		<xsl:number level="any" count="//map/node//node" format="1"/>
		</xsl:if>
	<xsl:if test="$level = 0">0</xsl:if></PredecessorUID>
	<Type><xsl:choose>
		<xsl:when test="@ENDARROW = 'Default'">
			<xsl:choose>
			<xsl:when test="@STARTARROW = 'Default'">3</xsl:when>
			<xsl:otherwise>1</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:otherwise>
			<xsl:choose>
			<xsl:when test="@STARTARROW = 'Default'">2</xsl:when>
			<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:otherwise>
	</xsl:choose></Type> 
</PredecessorLink>
</xsl:template>

<xsl:template match="attribute">
	<xsl:param name="prefix" />
	<xsl:if test="starts-with(@NAME,concat($prefix,'-'))">
		<xsl:element name="{substring-after(@NAME,concat($prefix,'-'))}">
			<xsl:value-of select="@VALUE"/>
		</xsl:element>
	</xsl:if>
</xsl:template>

<!-- required to _not_ output other things than nodes -->
<xsl:template match="*" mode="tasks"></xsl:template>

</xsl:stylesheet>

 	  	 
