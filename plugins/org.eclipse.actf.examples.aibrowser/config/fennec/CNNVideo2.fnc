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
    <documentation>CNN Video</documentation>
    <targetSite uri="http://edition.cnn.com/video/player/player.html*"/>
    <!-- creation data or something -->
  </meta>
  <node loc:idrefs="cnnVdPlyrPlaySearchBox">
    <h1/>hh
    <altText>Search Video</altText>
	<node loc:path=".//TR[2]/TD[1]/INPUT[1]">
	  <altText><ref loc:path="."/></altText>
	</node>
	<node loc:path=".//TR[1]/TD[2]/INPUT[1]">
	  <altText>Go <ref loc:path="."/></altText>
	</node>
  </node>
	  <node>
    <altText>Now Playing</altText>
    <h1/>
    <attach loc:idrefs="cnnVdPlyrHeadline duration"/>
  </node>
  <node>
    <altText>Playlist</altText>
    <h1/>
	<node loc:idrefs="cnnPlayListRelatedPane">
	  <altText>Related</altText>
	  <node loc:path="./DIV//A[contains(string(@id), 'cnnPlayListLinkValue')]">
		<altText><ref loc:path="."/></altText>
		<node loc:path="../../../DIV[2]">
		  <altText><ref loc:path=".//P[1]"/></altText>
		</node>
	  </node>
	</node>
	<node loc:idrefs="cnnVdPlyrtopVideo">
	  <node loc:path="./DIV/H1/A">
		<altText><ref loc:path="."/></altText>
		<node loc:path="../child::node()[position()=2]">
		  <altText>Time is <ref loc:path="."/></altText>
		</node>
	  </node>
	</node>
  </node>
  <node>
	<altText>Channels</altText>
	<h1/>
	<node loc:path="id('picksMap')/AREA[2]">
	  <altText>Related Video</altText>
	</node>
	<node loc:path="id('relatedMap')/AREA[2]">
	  <altText>Top Video</altText>
	</node>
	<node loc:path="id('relatedMap')/AREA[1]">
	  <altText>Picks Video</altText>
	</node>
  </node>
</fennec>