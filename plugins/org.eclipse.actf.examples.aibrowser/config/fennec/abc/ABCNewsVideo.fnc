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
<fennec  xmlns="http://www.ibm.com/xmlns/prod/aiBrowser/fennec"
	 xmlns:loc="http://www.ibm.com/xmlns/prod/aiBrowser/fennec/xml-query"
	 xmlns:flq="http://www.ibm.com/xmlns/prod/aiBrowser/fennec/flash-query">

  <meta xmlns="http://www.ibm.com/xmlns/prod/AcTF/aiBrowser/selector/1.0">
    <documentation>ABC News Video</documentation>
    <targetSite uri="http://abcnews.go.com/Video/playerIndex?id=*"/>
    <!-- creation data or something -->
  </meta>
  
  <node>
	<altText>ABC News Video</altText>

	<node loc:idrefs="fspContainer" flq:top="true" flq:base="_level0">
	  <h2/>
	  <altText>Controller</altText>
	  <node>
		<altText>Now Playing</altText>
		<attach loc:idrefs="nowplaying"/>
	  </node>
	  <node flq:targets="cv.playBut">
		<altText>Play / Pause</altText>
	  </node>
	  <node flq:targets="cv.getURLBut">
		<altText>Get URL, click to copy the URL to your clipboard</altText>
	  </node>
	  <node flq:targets="cv.emailVideoBut">
		<altText>E-mail Video</altText>
		<attach loc:idrefs="SendToFriend"/>
	  </node>
	</node>
	
	<node loc:idrefs="Guide" flq:top="true" flq:base="_level0.guide.container">
	  <h2/>
	  <altText>Guide</altText>
	  <node>
		<node flq:targets="item_0"><node flq:targets="subItemContainer_item_0.subItem_*"/></node>
		<node flq:targets="item_1"><node flq:targets="subItemContainer_item_1.subItem_*"/></node>
		<node flq:targets="item_2"><node flq:targets="subItemContainer_item_2.subItem_*"/></node>
		<node flq:targets="item_3"><node flq:targets="subItemContainer_item_3.subItem_*"/></node>
		<node flq:targets="item_4"><node flq:targets="subItemContainer_item_4.subItem_*"/></node>
	  </node>
	  <node>
		<node flq:targets="item_5"><node flq:targets="subItemContainer_item_5.subItem_*"/></node>
		<node flq:targets="item_6"><node flq:targets="subItemContainer_item_6.subItem_*"/></node>
		<node flq:targets="item_7"><node flq:targets="subItemContainer_item_7.subItem_*"/></node>
		<node flq:targets="item_8"><node flq:targets="subItemContainer_item_8.subItem_*"/></node>
		<node flq:targets="item_9"><node flq:targets="subItemContainer_item_9.subItem_*"/></node>
		<node flq:targets="item_10"><node flq:targets="subItemContainer_item_10.subItem_*"/></node>
	  </node>
	  <node>
		<h2/>
		<altText>Guide 2</altText>
		<node flq:targets="item_11"><node flq:targets="subItemContainer_item_11.subItem_*"/></node>
		<node flq:targets="item_12"><node flq:targets="subItemContainer_item_12.subItem_*"/></node>
		<node flq:targets="item_13"><node flq:targets="subItemContainer_item_13.subItem_*"/></node>
		<node flq:targets="item_14"><node flq:targets="subItemContainer_item_14.subItem_*"/></node>
		<node flq:targets="item_15"><node flq:targets="subItemContainer_item_15.subItem_*"/></node>
	  </node>
	  <node>
		<node flq:targets="item_16"><node flq:targets="subItemContainer_item_16.subItem_*"/></node>
		<node flq:targets="item_17"><node flq:targets="subItemContainer_item_17.subItem_*"/></node>
		<node flq:targets="item_18"><node flq:targets="subItemContainer_item_18.subItem_*"/></node>
		<node flq:targets="item_19"><node flq:targets="subItemContainer_item_19.subItem_*"/></node>
		<node flq:targets="item_20"><node flq:targets="subItemContainer_item_20.subItem_*"/></node>
	  </node>
	  <node>
		<node flq:targets="item_21"><node flq:targets="subItemContainer_item_21.subItem_*"/></node>
		<node flq:targets="item_22"><node flq:targets="subItemContainer_item_22.subItem_*"/></node>
		<node flq:targets="item_23"><node flq:targets="subItemContainer_item_23.subItem_*"/></node>
		<node flq:targets="item_24"><node flq:targets="subItemContainer_item_24.subItem_*"/></node>
	  </node>
	</node>

	<node loc:idrefs="browselist">
	  <h2/>
	  <altText>Play list</altText>
	  <node loc:path="./DIV/DIV/DIV[1]/P[1]">
		<altText>Link <ref loc:path="."/></altText>
	  </node>
	</node>

	<node loc:idrefs="Featured" flq:top="true" flq:base="_level0.instance1.container">
	  <h2/>
	  <altText>Featured Videos</altText>
	  <node flq:targets="pane_0">
		<altText><ref flq:targets=".instance2"/></altText>
		<node flq:targets=".clip_0"><altText><ref flq:targets=".clipTitle"/></altText></node>
		<node flq:targets=".clip_1"><altText><ref flq:targets=".clipTitle"/></altText></node>
	  </node>
	  <node flq:targets="pane_1">
		<altText><ref flq:targets=".instance6"/></altText>
		<node flq:targets=".clip_0"><altText><ref flq:targets=".clipTitle"/></altText></node>
		<node flq:targets=".clip_1"><altText><ref flq:targets=".clipTitle"/></altText></node>
	  </node>
	  <node flq:targets="pane_2">
		<altText><ref flq:targets=".instance10"/></altText>
		<node flq:targets=".clip_0"><altText><ref flq:targets=".clipTitle"/></altText></node>
		<node flq:targets=".clip_1"><altText><ref flq:targets=".clipTitle"/></altText></node>
	  </node>
	</node>
  </node>
</fennec>