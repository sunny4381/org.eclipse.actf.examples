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
    <documentation>Simplified You Tube</documentation>
    <targetSite uri="http://jp.youtube.com/"/>
    <targetSite uri="http://jp.youtube.com/index"/>
    <!-- creation data or something -->
  </meta>

  <attach loc:idrefs="search-form">
	<h1/>
	<altText>検索</altText>
  </attach>

  <node loc:idrefs="active_sharing" flq:top="true" flq:base="_level0.instance1">
	<h1/>
	<altText><ref flq:targets="title_mc.content_txt"/></altText>
	<node flq:targets="thumb00_mc thumb01_mc thumb02_mc thumb03_mc thumb04_mc thumb05_mc thumb06_mc thumb07_mc thumb08_mc thumb09_mc">
	  <altText><ref flq:targets=".title_mc.content_txt"/></altText>
	</node>
	<node flq:targets="thumb10_mc thumb11_mc thumb12_mc thumb13_mc thumb14_mc thumb15_mc thumb16_mc thumb17_mc thumb18_mc thumb19_mc">
	  <altText><ref flq:targets=".title_mc.content_txt"/></altText>
	</node>
	<node flq:targets="thumb20_mc thumb21_mc thumb22_mc thumb23_mc thumb24_mc thumb25_mc thumb26_mc thumb27_mc thumb28_mc thumb29_mc">
	  <altText><ref flq:targets=".title_mc.content_txt"/></altText>
	</node>
	<node flq:targets="thumb30_mc thumb31_mc thumb32_mc thumb33_mc thumb34_mc thumb35_mc thumb36_mc thumb37_mc thumb38_mc thumb39_mc">
	  <altText><ref flq:targets=".title_mc.content_txt"/></altText>
	</node>
	<node flq:targets="thumb40_mc thumb41_mc thumb42_mc thumb43_mc thumb44_mc thumb45_mc thumb46_mc thumb47_mc thumb48_mc thumb49_mc">
	  <altText><ref flq:targets=".title_mc.content_txt"/></altText>
	</node>
	<node flq:targets="thumb50_mc thumb51_mc thumb52_mc thumb53_mc thumb54_mc thumb55_mc thumb56_mc thumb57_mc thumb58_mc thumb59_mc">
	  <altText><ref flq:targets=".title_mc.content_txt"/></altText>
	</node>
	<node flq:targets="thumb60_mc thumb61_mc thumb62_mc thumb63_mc thumb64_mc thumb65_mc thumb66_mc thumb67_mc thumb68_mc thumb69_mc">
	  <altText><ref flq:targets=".title_mc.content_txt"/></altText>
	</node>
	<node flq:targets="thumb70_mc thumb71_mc thumb72_mc thumb73_mc thumb74_mc thumb75_mc thumb76_mc thumb77_mc thumb78_mc thumb79_mc">
	  <altText><ref flq:targets=".title_mc.content_txt"/></altText>
	</node>
	<node flq:targets="thumb80_mc thumb81_mc thumb82_mc thumb83_mc thumb84_mc thumb85_mc thumb86_mc thumb87_mc thumb88_mc thumb89_mc">
	  <altText><ref flq:targets=".title_mc.content_txt"/></altText>
	</node>
	<node flq:targets="thumb90_mc thumb91_mc thumb92_mc thumb93_mc thumb94_mc thumb95_mc thumb96_mc thumb97_mc thumb98_mc thumb99_mc">
	  <altText><ref flq:targets=".title_mc.content_txt"/></altText>
	</node>
  </node>

  <node loc:path="id('hpMainContent')/DIV[2]/DIV[2]">
    <h1/>
	<altText><ref loc:path="../DIV[1]"/></altText>
	<node loc:path="./child::DIV/DIV[2]/A[1]">
	  <altText>動画 <ref loc:path="."/></altText>
	  <node loc:path="../../DIV[3]/A[1]">
		<altText>ディレクター <ref loc:path="."/></altText>
	  </node>
	</node>
  </node>

  <node loc:idrefs="hpFeatured">
    <h1/>
	<altText><ref loc:path="id('hpFeaturedHeading')/H1"/></altText>
	<node loc:path="./DIV[@class='vEntry']/TABLE[1]/TBODY[1]/TR[1]/TD[2]/DIV[1]/A[1]">
	  <h3/>
	  <altText>動画 <ref loc:path="."/></altText>
	  <node loc:path="../../DIV[2]/SPAN[1]"/>
	  <node loc:path="../../../TD[3]/DIV[2]"/>
	  <node loc:path="../../../TD[3]/DIV[1]/A[1]">
		<altText><ref loc:path="preceding-sibling::*"/><ref loc:path="."/><ref loc:path="following-sibling::*"/></altText>
	  </node>
	  <node loc:path="../../../../TR[2]/TD[1]"/>
	  <node loc:path="../../../../TR[2]/TD[2]/A[1]">
		<altText><ref loc:path="preceding-sibling::*"/><ref loc:path="."/><ref loc:path="following-sibling::*"/></altText>
	  </node>
	</node>
	<node loc:path="id('hpFeaturedMoreTop')/A[1]">
	  <h2/>
	  <altText><ref loc:path="."/></altText>
	</node>
	<node loc:path="id('hpSmallTabsContainer')/LI[@class!='hilite']/A[1]"/>
	
  </node>
  
  <node loc:idrefs="hpPVA">
	<h1/>
	<altText>プロモーションビデオ</altText>
	<attach loc:idrefs="hpEmbedUnderBlock">
	  <node loc:path=".//IMG"><altText> </altText></node>
	</attach>
  </node>


  <node>
	<h1/>
	<altText>サイドコンテンツ</altText>
	<attach loc:path="id('hpSideContent')/DIV[position() > 2]">
	</attach>
  </node>

  <attach loc:idrefs="footer">
	<h1/>
	<altText>フッター</altText>
  </attach>
  
</fennec>