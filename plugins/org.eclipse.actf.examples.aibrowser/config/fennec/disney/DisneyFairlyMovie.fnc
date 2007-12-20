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
<fennec xmlns:nvm3="urn:nvm3" xmlns:f="http://www.ibm.com/xmlns/prod/aiBrowser/fennec" xmlns:loc="http://www.ibm.com/xmlns/prod/aiBrowser/fennec/xml-query" xmlns:flq="http://www.ibm.com/xmlns/prod/aiBrowser/fennec/flash-query" xmlns="http://www.ibm.com/xmlns/prod/aiBrowser/fennec">
  <meta xmlns="http://www.ibm.com/xmlns/prod/AcTF/aiBrowser/selector/1.0" xmlns:nvml="urn:nvm3" xmlns:x2="http://www.w3.org/TR/xhtml2" xmlns:wairole="http://www.w3.org/2005/01/wai-rdf/GUIRoleTaxonomy#" xmlns:state="http://www.w3.org/2005/07/aaa">
    <documentation>Disney Fairies Movie</documentation>
    <targetSite uri="http://disney.go.com/fairies/movies/videos.html"/>
    <!-- creation data or something -->
  </meta>
  <!-- <attach loc:idrefs="chrome" changeless="true"/> -->
  <node loc:idrefs="disney_fairies" flq:top="true" flq:base="_level0">
    <h1/>
    <altText>Disney Fairy, Coming Fall 2008!</altText>
    <node flq:targets="mcContent.VideoPlayer.VideoPlayerControls">
	  <h2/>
      <altText>Video Control</altText>
      <node flq:targets=".Play"><altText>Play</altText></node>
      <node flq:targets=".Pause"><altText>Pause</altText></node>
      <node flq:targets=".Rewind"><altText>Rewind</altText></node>
    </node>
    <node flq:targets="mcMenu">
	  <h2/>
      <altText>Menu</altText>
      <node flq:targets=".fairies"><altText>Meet the Fairies</altText></node>
      <node flq:targets=".books"><altText>Books</altText></node>
      <node flq:targets=".movies"><altText>Movies</altText></node>
      <node flq:targets=".games"><altText>Games and Activities</altText></node>
      <node flq:targets=".createafairy"><altText>Create a Fairy</altText></node>
      <node flq:targets=".parents"><altText>Parents</altText></node>
      <node flq:targets=".home"><altText>Home</altText></node>
    </node>

    <node flq:targets="mcContent.mcSubNav">
	  <h2/>
      <altText>Navigation</altText>
      <node flq:targets=".videos.button"><altText>Videos</altText></node>
      <node flq:targets=".gallery.button"><altText>Photo Gallery</altText></node>
      <node flq:targets=".story.button"><altText>History</altText></node>
	  <node flq:targets="mcFooterPromos.item_1"><altText>Play Fairies in Disney XD</altText></node>
	  <node flq:targets="mcFooterPromos.item_2"><altText>Download the Pixie Dust Tree</altText></node>
	  <node flq:targets="mcFooterPromos.item_3"><altText>Take the Hopeful's Quest</altText></node>
    </node>

	<node>
	  <h2/>
	  <altText>HTML Contents</altText>
	  <node loc:idrefs="chrome">
		<altText>Links</altText>
		<node loc:path="./FORM[1]/TABLE[1]//TABLE//A[1]">
		  <altText><ref loc:path="."/></altText>
		</node>
	  </node>
	  <attach loc:path="id('chrome')/FORM[1]/TABLE[1]/TBODY[1]/TR[1]/TD[3]">
		<altText>Search Disney.com</altText>
	  </attach>
	  <attach loc:idrefs="footer">
		<altText>Footer Links</altText>
	  </attach>
	</node>
  </node>
</fennec>