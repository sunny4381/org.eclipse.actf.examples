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
    <documentation>Disney Fairies Top</documentation>
    <targetSite uri="http://disney.go.com/fairies/index.html*"/>
    <targetSite uri="http://disney.go.com/fairies/"/>
    <!-- creation data or something -->
  </meta>
  <!-- <attach loc:idrefs="chrome" changeless="true"/> -->
  <node loc:idrefs="disney_fairies" flq:top="true" flq:base="_level0">
    <h1/>
    <altText>Disney Fairy</altText>

    <node flq:targets="mcContent">
	  <h2/>
      <altText>Fairies</altText>
      <node flq:targets=".mcTinkerBell"><altText>Tinker Bell</altText></node>
      <node flq:targets=".mcSilvermist"><altText>Silvermist</altText></node>
      <node flq:targets=".mcIridessa"><altText>Iridessa</altText></node>
      <node flq:targets=".mcFawn"><altText>Fawn</altText></node>
      <node flq:targets=".mcRosetta"><altText>Rosetta</altText></node>
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

	<node flq:targets="mcContent.mcGamesPromo">
	  <h2/>
	  <altText>Games</altText>
	  <node flq:targets=".item_1"><altText><ref xmlns="http://www.ibm.com/xmlns/prod/aiBrowser/fennec" xmlns:x2="http://www.w3.org/TR/xhtml2" xmlns:wairole="http://www.w3.org/2005/01/wai-rdf/GUIRoleTaxonomy#" xmlns:state="http://www.w3.org/2005/07/aaa" flq:targets=".placeholder"/></altText></node>
	  <node flq:targets=".item_2"><altText><ref xmlns="http://www.ibm.com/xmlns/prod/aiBrowser/fennec" xmlns:x2="http://www.w3.org/TR/xhtml2" xmlns:wairole="http://www.w3.org/2005/01/wai-rdf/GUIRoleTaxonomy#" xmlns:state="http://www.w3.org/2005/07/aaa" flq:targets=".placeholder"/></altText></node>
	  <node flq:targets="mcContent.btAllGames"><altText><ref xmlns="http://www.ibm.com/xmlns/prod/aiBrowser/fennec" xmlns:x2="http://www.w3.org/TR/xhtml2" xmlns:wairole="http://www.w3.org/2005/01/wai-rdf/GUIRoleTaxonomy#" xmlns:state="http://www.w3.org/2005/07/aaa" flq:targets=".txLabel"/></altText></node>
	</node>

	<node flq:targets="mcContent.mcMeet">
	  <h2/>
	  <altText>Meet the Fairies</altText>
	  <node flq:targets=".btMeetHome"><altText>Home</altText></node>
	  
	  <node flq:targets=".container.item_1.button.placeholder"><altText>Queen Clarion</altText></node>
	  <node flq:targets=".container.item_2.button.placeholder"><altText>Tinker Bell</altText></node>
	  <node flq:targets=".container.item_3.button.placeholder"><altText>Silvermist</altText></node>
	  <node flq:targets=".container.item_4.button.placeholder"><altText>Iridessa</altText></node>
	  <node flq:targets=".container.item_5.button.placeholder"><altText>Rosetta</altText></node>
	  <node flq:targets=".container.item_6.button.placeholder"><altText>Fawn</altText></node>
	  <node flq:targets=".container.item_7.button.placeholder"><altText>Fairy Mary</altText></node>
	  <node flq:targets=".container.item_8.button.placeholder"><altText>Bess</altText></node>
	  <node flq:targets=".container.item_9.button.placeholder"><altText>Fira</altText></node>
	  <node flq:targets=".container.item_10.button.placeholder"><altText>Lily</altText></node>
	  <node flq:targets=".container.item_11.button.placeholder"><altText>Prilla</altText></node>
	  <node flq:targets=".container.item_12.button.placeholder"><altText>Vidia</altText></node>
	  <node flq:targets=".container.item_13.button.placeholder"><altText>Rani</altText></node>
	  <node flq:targets=".container.item_14.button.placeholder"><altText>Beck</altText></node>
	</node>
  </node>
</fennec>