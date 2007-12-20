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
<fennec xmlns:loc="http://www.ibm.com/xmlns/prod/aiBrowser/fennec/xml-query"
	xmlns:flq="http://www.ibm.com/xmlns/prod/aiBrowser/fennec/flash-query"
	xmlns="http://www.ibm.com/xmlns/prod/aiBrowser/fennec">
  <meta xmlns="http://www.ibm.com/xmlns/prod/AcTF/aiBrowser/selector/1.0">
    <documentation>IBM TV</documentation>
    <targetSite uri="http://www-306.ibm.com/software/info/television/index.jsp*"/>
    <!-- creation data or something -->
  </meta>
  <node loc:idrefs="shell_popup" flq:top="true" flq:base="_level0.mcLoad">
    <h1/>
    <altText>IBM TV</altText>
    <node flq:base="mcVideo">
      <h2/>
      <altText>Now Playing</altText>
      <node flq:targets="video_description"/>
      <node flq:targets="mcVidControlsBkg.my_pausbttn"><altText>Pause</altText></node>
      <node flq:targets="mcVidControlsBkg.my_plybttn"><altText>Play</altText></node>
      <node flq:targets="selectbandwidth">
	<node flq:targets="mcLow.selectlow"/>
	<node flq:targets="mcHigh.selecthigh"/>
      </node>
    </node>
    <node flq:targets="c_availableMediaWindow">
      <h2/>
      <altText><ref flq:targets="dt_availableMedia"/></altText>
      <node flq:targets=".spContentHolder.mc_mediaContentContainer.dynamicTF_container*">
	<altText><ref flq:targets=".title"/></altText>
	<node flq:targets=".description"><altText/></node>
	<node flq:targets=".select_bandwidth">
	  <altText><ref flq:targets=".selectBandwithText"/></altText>
	  <node flq:targets=".lowbandwidth_button"><altText>Low</altText></node>
	  <node flq:targets=".highbandwidth_button"><altText>High</altText></node>
	</node>
      </node>
    </node>

    <node flq:targets="mcOfferings">
      <h2/>
      <altText><ref flq:targets="dt_relatedOfferings"/></altText>
      <node flq:targets=".mcOffering*"/>
    </node>
    
    <node flq:base="mc_menuNav" flq:targets="mc_mediaNavButton.mainNavButton">
      <h2/>
      <altText><ref flq:targets=".titleTextfield"/></altText>
      <node flq:targets="mc_mediaNavMenu.menu0.lineItem*">
	<altText>a<ref flq:targets=".linkTextfield"/></altText>
      </node>
    </node>
  </node>
</fennec>