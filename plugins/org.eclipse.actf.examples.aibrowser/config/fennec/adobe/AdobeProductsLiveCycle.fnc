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
	<documentation>Adobe Livecycle</documentation>
	<targetSite uri="http://www.adobe.com/products/livecycle/"/>
	<!-- creation data or something -->
  </meta>

  <node>
	<altText>Adobe Livecycle</altText>
	<!-- (1) TOP MENU BAR -->
	<node loc:idrefs="site-menu">
	  <h1/>
	  <altText>Top Menu</altText>
	  <node loc:path="./UL[1]/LI/SPAN[1]">		  
		<h2/>
		<altText><ref loc:path="."/></altText>
		<node loc:path="../DL[1]/DT[1]">
		  <altText><ref loc:path="."/></altText>
		  <attach loc:path="following-sibling::DD[count(preceding-sibling::DT)=1]">
		  </attach>
		</node>
		<node loc:path="../DL[1]/DT[2]">
		  <altText><ref loc:path="."/></altText>
		  <attach loc:path="following-sibling::DD[count(preceding-sibling::DT)=2]">
		  </attach>
		</node>
		<node loc:path="../DL[1]/DT[3]"><altText><ref xmlns="urn:nvm3" xmlns:nvml="urn:nvm3" xmlns:x2="http://www.w3.org/TR/xhtml2" xmlns:wairole="http://www.w3.org/2005/01/wai-rdf/GUIRoleTaxonomy#" xmlns:state="http://www.w3.org/2005/07/aaa" loc:path="."/></altText></node>
		<node loc:path="../UL">
		  <attach loc:path="child::LI">
		  </attach>		
		</node>
	  </node>
	</node>


	<!-- (2) SEARCH BAR -->
	<node loc:idrefs="site-search">
	  <h1/>
	  <altText>Search Bar</altText>
	  <node loc:path="./P[1]/INPUT[2]"><altText/></node>
	  <node loc:path="./P[1]/BUTTON[1]"><altText><ref xmlns="urn:nvm3" xmlns:nvml="urn:nvm3" xmlns:x2="http://www.w3.org/TR/xhtml2" xmlns:wairole="http://www.w3.org/2005/01/wai-rdf/GUIRoleTaxonomy#" xmlns:state="http://www.w3.org/2005/07/aaa" loc:path="."/></altText></node>
	</node>

	<!-- (3) USER MENU -->
	<node loc:idrefs="user-menu">
	  <h1/>
	  <altText>User Menu</altText>
	  <attach loc:path="UL/LI[position()!=2]">
	  </attach>
	</node>

	<!-- (4) FLASH MOVIE -->          
	<node loc:idrefs="livecycleesmovie" flq:top="true" flq:base="_level0">
	  <h1/>
	  <altText>Industry Solutions Links</altText>
	  <node flq:targets="btn1_mc btn2_mc btn3_mc btn4_mc">
		<altText><ref flq:targets=".btnText_tf"/></altText>
	  </node>
	  <node flq:targets="videoEnd_mc.replay_mc"><altText><ref xmlns="urn:nvm3" xmlns:nvml="urn:nvm3" xmlns:x2="http://www.w3.org/TR/xhtml2" xmlns:wairole="http://www.w3.org/2005/01/wai-rdf/GUIRoleTaxonomy#" xmlns:state="http://www.w3.org/2005/07/aaa" flq:targets=".replay_tf"/></altText></node>
	</node>
	
	<!-- (5) TEXT 1 -->
	<node loc:path="id('L0C1-body')/H2[1]">
	  <h1/>
	  <altText><ref loc:path="."/></altText>
	  <node loc:path="following-sibling::DIV[1]">
		<altText><ref loc:path="."/></altText>
	  </node>
	</node>

	<!-- (6) AJAX -->
	<node loc:path="id('L0C1-body')/DIV[3]//H3[1]">
	  <h1/>
	  <altText><ref loc:path="."/></altText>
	  <node loc:path="../DIV[1]/H4[1]/A[1]">
		<altText><ref loc:path="."/> <ref loc:path="../../../DL[1]/DT[1]"/></altText>
		<node loc:path="../../BLOCKQUOTE[1]/P[1]"/>
		<node loc:path="../../../DL[1]/DD[2]"/>
		<node loc:path="../../../DL[1]/DD[1]"/>
	  </node>

	</node>

	<!-- (7) TAB 1 -->
	<node loc:idrefs="tab_business">
	  <h1/>
	  <altText><ref loc:path="."/></altText>
	  <attach loc:path="id('showMyTab')/DIV[1]"/>
	</node>

	<!-- (8) TAB 2 -->
	
	<node loc:idrefs="tab_technology">
	  <h1/>
	  <altText><ref loc:path="."/></altText>
	  <attach loc:path="id('showMyTab')/DIV[2]"/>
	</node>

	<!-- (9) NEXT STEPS -->
	<attach loc:path="id('L0C2')/DIV[1]">
	  <h1/>
	</attach>

	<!-- (10) SIDE MENU 1 -->
	<attach loc:path="id('L0C1')/following::DL[1]">
	  <h1/>
	  <altText>Side Menu 1</altText>
	</attach>

	<!-- (11) SIDE MENU 2 -->
	<node loc:path="id('L0C1')/following::DL[2]">
	  <h1/>
	  <altText>Side Menu 2</altText>
	  <node loc:path="DT[1]">
		<h2/>
		<altText><ref loc:path="."/></altText>
		<attach loc:path="following-sibling::DD[count(preceding-sibling::DT)=1]">
		</attach>
	  </node>
	  <node loc:path="DT[2]">
		<h2/>
		<altText><ref loc:path="."/></altText>
		<attach loc:path="following-sibling::DD[count(preceding-sibling::DT)=2]">
		</attach>
	  </node>
	  <node loc:path="DT[3]">
		<h2/>
		<altText><ref loc:path="."/></altText>
		<attach loc:path="following-sibling::DD[count(preceding-sibling::DT)=3]">
		</attach>
	  </node>
	</node>

	<!-- (12) FOOTER -->
	<attach loc:idrefs="globalfooter">
	  <h1/>
	  <altText>Footer</altText>
	</attach>

  </node>
</fennec>