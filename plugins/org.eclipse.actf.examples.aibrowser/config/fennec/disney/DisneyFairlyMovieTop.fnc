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
    <documentation>Disney Fairies Movie Top</documentation>
    <targetSite uri="http://disney.go.com/fairies/movies/movies.html*"/>
    <!-- creation data or something -->
  </meta>
  <!-- <attach loc:idrefs="chrome" changeless="true"/> -->
  <node loc:idrefs="disney_fairies" flq:top="true" flq:base="_level0">
    <h1/>
    <altText>Disney Fairy</altText>

	<node flq:targets="mcContent">
	  <h2/>
	  <altText>Content</altText>
	  <node flq:targets=".mcHeadPromo.item_1"><altText>View the Trailer: TinkerBell, Watch it now</altText></node>
	  <node flq:targets=".mcDVDBox.item_1"><altText><ref xmlns="http://www.ibm.com/xmlns/prod/aiBrowser/fennec" xmlns:x2="http://www.w3.org/TR/xhtml2" xmlns:wairole="http://www.w3.org/2005/01/wai-rdf/GUIRoleTaxonomy#" xmlns:state="http://www.w3.org/2005/07/aaa" flq:targets="mcContent.mcDVDBox.mcSubtitle.txLabel"/></altText></node>
	  <node flq:targets=".mcSectionPromo.item_1"><altText>Photo Gallery</altText></node>
	  <node flq:targets=".mcSectionPromo.item_2"><altText>The History: Faith, Trust, and Pixie Dust!</altText></node>
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
      <node flq:targets=".gallery.button"><altText>Photo Gallary</altText></node>
      <node flq:targets=".story.button"><altText>History</altText></node>
    </node>
  </node>
</fennec>