<?xml version="1.0" encoding="UTF-8"?>
<!--
 Copyright (c) 2007 IBM Corporation and others.
 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html

 Contributors:
     IBM Corporation - initial API and implementation
-->
<fennec xmlns:f="http://www.ibm.com/xmlns/prod/aiBrowser/fennec" xmlns:loc="http://www.ibm.com/xmlns/prod/aiBrowser/fennec/xml-query" xmlns:flq="http://www.ibm.com/xmlns/prod/aiBrowser/fennec/flash-query" xmlns="http://www.ibm.com/xmlns/prod/aiBrowser/fennec">
  <meta xmlns="http://www.ibm.com/xmlns/prod/AcTF/aiBrowser/selector/1.0" xmlns:x2="http://www.w3.org/TR/xhtml2" xmlns:wairole="http://www.w3.org/2005/01/wai-rdf/GUIRoleTaxonomy#" xmlns:state="http://www.w3.org/2005/07/aaa">
    <documentation>CBS News (Video) </documentation>
    <targetSite uri="http://www.cbsnews.com/sections/i_video/*"/>
    <!-- creation data or something -->
  </meta>
  <node>
	<attach loc:path="./BODY/TABLE[1]/TBODY[1]/TR[1]/TD[1]/DIV[1]/DIV[1]/DIV[1]">
	  <h1/>
	  <altText>Search Video</altText>
	</attach>
	<attach loc:idrefs="tab_content_np">
	  <h1/>
	  <altText>Now Playing</altText>
	</attach>
	<node loc:idrefs="IFrame1">
	  <h1/>
	  <altText>Play List / Search Result</altText>
	  <node loc:path=".//A[child::B]">
		<altText><ref loc:path="./B"/></altText>
		<description><ref loc:path="./following-sibling::DIV/A[2]/text()"/></description>
	  </node>
	</node>
	<attach loc:path="id('channelPane')/DIV[2]/DIV[1]">
	  <h1/>
	  <altText>Channels</altText>
	</attach>
  </node>
</fennec>