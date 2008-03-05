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
    <documentation>Simplified You Tube (Search Result) </documentation>
    <targetSite uri="http://jp.youtube.com/result*"/>
    <!-- creation data or something -->
  </meta>

  <node loc:idrefs="searchSectionHeader">
	<h1/>
    <altText><ref loc:path="./DIV[1]"/></altText>

	<node loc:idrefs="search-options-container">
	  <h2/>
	  <altText>表示順<ref loc:path=".//TD[@class='search-sort']//SPAN[@class='sort-by-selected']"/></altText>
	  <node loc:path=".//TD[@class='search-sort']//A"><altText>Change to <ref xmlns="urn:nvm3" xmlns:nvml="urn:nvm3" xmlns:x2="http://www.w3.org/TR/xhtml2" xmlns:wairole="http://www.w3.org/2005/01/wai-rdf/GUIRoleTaxonomy#" xmlns:state="http://www.w3.org/2005/07/aaa" loc:path="."/></altText></node>
	</node>
	<node loc:path="id('mainContent')//DIV[contains(@class,'vlentry')]//DIV[@class='vlshortTitle']//A[1]">
	  <h2/>
	  <altText><ref loc:path="ancestor::DIV[@class='vlshortTitle']"/></altText>
	  <node loc:path="ancestor::DIV[contains(@class,'vlentry')]">
	    <altText>詳細情報</altText>
   	    <node loc:path=".//DIV[@class='vldesc']/SPAN">
		  <altText><ref loc:path="."/></altText>
	    </node>
	    <node loc:path=".//DIV[@class='vlfacets']/text()[1]">
		  <altText>再生回数 <ref loc:path="."/></altText>
	    </node>
	    <node loc:path=".//DIV[@class='vlfacets']//SPAN[@class='vlfrom']/A[1]">
		  <altText>投稿者 <ref loc:path="."/></altText>
	    </node>
	    <node loc:path=".//DIV[@class='vlfacets']//DIV[@class='vladded']/text()">
		  <altText>投稿日 <ref loc:path="."/></altText>
	    </node>
	    <node loc:path=".//DIV[@class='vlfacets']//DIV[@class='runtime']">
		  <altText>時間 <ref loc:path="."/></altText>
	    </node>
	    <node loc:path=".//DIV[@class='vlfacets']//DIV[@class='vlcategory']/A[1]">
		  <altText>他の動画 <ref loc:path="."/></altText>
	    </node>
	  </node>
	</node>

	<attach loc:path="id('mainContent')/following-sibling::DIV[@class='searchFooterBox']"/>
  </node>
  <attach loc:idrefs="search-form"><altText>検索</altText><h1/></attach>


  <node>
	<h1/>
	<altText>サイドコンテンツ</altText>
	<attach loc:idrefs="sideContentWithPVA">
	</attach>
  </node>

  <attach loc:idrefs="footer">
	<h1/>
	<altText>フッター</altText>
  </attach>

</fennec>