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
    <documentation>BBC News (Video &amp; Audio) </documentation>
    <targetSite uri="http://news.bbc.co.uk/2/hi/video_and_audio/default.stm"/>
    <!-- creation data or something -->
  </meta>
  <!-- seems problematic -->
  <node loc:path="./BODY[1]/TABLE[3]/TBODY/TR/TD[2]">
	<node loc:path="./TABLE[2]">
	  <h1/>
	  <altText>Most Popular Stories</altText>
	  <node loc:path=".//DIV/A">
		<altText><ref loc:path="."/></altText>
	  </node>
	</node>
	<node loc:path="./TABLE[3]">
	  <h1/>
	  <altText>Video and Audio Choice</altText>
	  <node loc:path=".//DIV[@class='miitb']/A">
		<altText><ref loc:path="."/></altText>
	  </node>
	</node>
	<node loc:path="./TABLE[4]">
	  <h1/>
	  <altText>Other Top Stories</altText>
	  <node loc:path=".//TD[1]//A[child::B]">
		<altText><ref loc:path="."/></altText>
	  </node>
	  <node loc:path="../TABLE[5]//TD[1]//A[child::B]">
		<altText><ref loc:path="."/></altText>
	  </node>
	</node>
	<node loc:path="./TABLE[4]">
	  <h1/>
	  <altText>From Programmes</altText>
	  <node loc:path=".//TD[3]//A[child::B]">
		<altText><ref loc:path="."/></altText>
	  </node>
	  <node loc:path="../TABLE[5]//TD[3]//A[child::B]">
		<altText><ref loc:path="."/></altText>
	  </node>
	</node>
	<node loc:path="./TABLE[5]">
	  <h1/>
	  <altText>More Video and Audio News</altText>
	  <node loc:path=".//DIV[@class='nlp']/A">
		<altText>Category : <ref loc:path="."/></altText>
		<node loc:path="../..//DIV[@class='miitb']/A">
		  <altText><ref loc:path="."/></altText>
		</node>
	  </node>
	</node>
  </node>
</fennec>