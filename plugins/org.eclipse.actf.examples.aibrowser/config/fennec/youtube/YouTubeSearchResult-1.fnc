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
    <targetSite uri="http://www.youtube.com/result*"/>
    <targetSite uri="http://youtube.com/result*"/>
    <!--targetSite uri="http://jp.youtube.com/result*"/-->
    <!-- creation data or something -->
  </meta>

  <node loc:idrefs="searchSectionHeader">
	<h1/>
    <altText><ref loc:path="./DIV[2]"/> <ref loc:path="./DIV[1]"/></altText>
	<attach loc:path="id('mainContent')/DIV[@class='marT10']"/>

	<node loc:idrefs="searchSortContainer">
	  <h2/>
	  <altText>Sorted by <ref loc:path="./A[contains(@class, 'selected')]"/></altText>
	  <node loc:path="./A[not(contains(@class, 'selected'))]"><altText><ref xmlns="urn:nvm3" xmlns:nvml="urn:nvm3" xmlns:x2="http://www.w3.org/TR/xhtml2" xmlns:wairole="http://www.w3.org/2005/01/wai-rdf/GUIRoleTaxonomy#" xmlns:state="http://www.w3.org/2005/07/aaa" loc:path="."/></altText></node>
	  <node loc:idrefs="searchViews">
		<altText><ref loc:path="./SPAN[1]"/><ref loc:path="./IMG[1]"/></altText>
		<node loc:path="./A[1]"><altText>Change to <ref xmlns="urn:nvm3" xmlns:nvml="urn:nvm3" xmlns:x2="http://www.w3.org/TR/xhtml2" xmlns:wairole="http://www.w3.org/2005/01/wai-rdf/GUIRoleTaxonomy#" xmlns:state="http://www.w3.org/2005/07/aaa" loc:path="."/></altText></node>
	  </node>
	</node>
	<node loc:path="id('mainContent')/DIV[@class='']/DIV[contains(@class,'vEntry')]/TABLE[1]/TBODY[1]/TR[1]/TD[2]/DIV[1]/A[1]">
	  <h2/>
	  <altText><ref loc:path="."/></altText>
	  <node loc:path="../../DIV[2]">
		<altText><ref loc:path="."/></altText>
	  </node>
	  <node loc:path="../../../TD[3]/child::text()[2]">
		<altText>Views <ref loc:path="."/></altText>
	  </node>
	  <node loc:path="../../../TD[3]/A[1]">
		<altText>From <ref loc:path="."/></altText>
	  </node>
	  <node loc:path="../../../TD[3]/child::text()[3]">
		<altText>Added <ref loc:path="."/></altText>
	  </node>
	  <node loc:path="../../../../TR[2]/TD[1]/SPAN[1]">
		<altText>Time <ref loc:path="./following::SPAN[1]"/></altText>
	  </node>
	  <node loc:path="../../../../TR[2]/TD[1]/DIV[1]">
		<altText>Tags</altText>
		<node loc:path="./A"/>
	  </node>
	  <node loc:path="../../../../TR[2]/TD[2]/A[1]">
		<altText>More in <ref loc:path="."/></altText>
	  </node>
	</node>

	<node loc:path="id('mainContent')/TABLE[1]">
	  <h2/>
	  <altText>List</altText>
	  <node loc:path="./TBODY[1]/TR/TD/DIV[1]/A[1]">
		<altText><ref loc:path="."/>. Time <ref loc:path="../../DIV[2]/SPAN[1]"/></altText>
	  </node>
	</node>
	<attach loc:path="id('mainContent')/following-sibling::DIV[@class='searchFooterBox']"/>
  </node>
  <attach loc:idrefs="searchDiv"><altText>Search</altText><h1/></attach>


  <node>
	<h1/>
	<altText>Navigation Links</altText>
	
	<attach loc:idrefs="utilDiv">
	  <node loc:idrefs="iconMail"><altText>mail</altText></node>
	</attach>

	<attach loc:idrefs="gNavDiv">
	  <node loc:path="id('gNavDiv')/DIV[1]/H3[1]">
		<h-/>
	  </node>
	  <node loc:path=".//IMG">
		<altText> </altText>
	  </node>
	</attach>
  </node>

  <node>
	<h1/>
	<altText>Side Content</altText>
	<attach loc:idrefs="sideContentWithPVA">
	</attach>
  </node>

  <attach loc:idrefs="footerDiv">
	<h1/>
	<altText>Footer</altText>
  </attach>

</fennec>