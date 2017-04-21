<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <!--
/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2006  Christian Foltin <christianfoltin@users.sourceforge.net>
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
      
  -->

  <xsl:output method="xml" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" 
    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"/>
 
<!-- fc, 20.10.2004: The following parameter is set by freemind. -->
<xsl:param name="destination_dir">./</xsl:param>
<xsl:param name="area_code"></xsl:param>
<xsl:param name="folding_type">html_export_no_folding</xsl:param>
	<!-- possible values: 
		html_export_fold_all, 
		html_export_no_folding, 
		html_export_fold_currently_folded, 
		html_export_based_on_headings: this means, that approx. five levels are given, more deeper nodes are folded.
		As of the time being, this parameter is not used.
		-->
<!--
    
    -->
  <xsl:template match="/">
    <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
      <head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title><xsl:call-template name="output-title" /></title>
        <xsl:element name="script">
            <xsl:attribute name="type">text/javascript</xsl:attribute>
            <xsl:attribute name="src">./<xsl:value-of select="$destination_dir"/>flashobject.js</xsl:attribute>
			<xsl:text> </xsl:text><!-- this space is a trick, such that firefox displays the flash...  :( -->
		</xsl:element>
<style type="text/css">
	
	/* hide from ie on mac \*/
	html {
		height: 100%;
		overflow: hidden;
	}
	
	#flashcontent {
		height: 100%;
	}
	/* end hide */

	body {
		height: 100%;
		margin: 0;
		padding: 0;
		background-color: #9999ff;
	}

</style>
      </head>
		<body>
	<div id="flashcontent">
		 Flash plugin or Javascript are turned off.
		 Activate both  and reload to view the mindmap
	</div>
	
	<script type="text/javascript">
		var fo = new FlashObject("./<xsl:value-of select="$destination_dir"/>visorFreemind.swf", "visorFreeMind", "100%", "100%", 8, "#9999ff");
		fo.addParam("quality", "high");
		fo.addParam("bgcolor", "#ffffff");
		fo.addParam("allowScriptAccess", "true");
		fo.addVariable("openUrl", "_blank");
		fo.addVariable("initLoadFile", "./<xsl:value-of select="$destination_dir"/>map.mm");
		fo.addVariable("startCollapsedToLevel","5");
		fo.write("flashcontent");
	</script>
   		</body>
    </html>
  </xsl:template>

<!-- 
	fc, 22.3.2013: tracing doesn't work. If you want to try, replace in the flash build.xml
	-trace off by -trace Flashout.traceReplacer 
		fo.addVariable("FLASHOUT_ENABLE", "TRUE");
		fo.addVariable("FLASHOUT_PORT", "1024");
 -->

<!-- from toxhtml.xsl -->

<xsl:template name="output-title">
	<!-- look if there is any node inside the map (there should never be
		none, but who knows?) and take its text as the title -->
	<xsl:choose>
	<xsl:when test="/map/node/@TEXT">
		<xsl:value-of select="/map/node/@TEXT" />
	</xsl:when>
	<xsl:when test="/map/node/richcontent[@TYPE='NODE']">
		<xsl:apply-templates select="/map/node/richcontent[@TYPE='NODE']/html/body" mode="strip-tags" />
	</xsl:when>
	<xsl:otherwise>
		<xsl:text>FreeMind2HTML Mindmap</xsl:text>
	</xsl:otherwise>
	</xsl:choose>
</xsl:template>

	
	<xsl:template match="text()|@*"  mode="strip-tags">
		  <xsl:value-of select="string(.)"/>
	</xsl:template>


</xsl:stylesheet>
