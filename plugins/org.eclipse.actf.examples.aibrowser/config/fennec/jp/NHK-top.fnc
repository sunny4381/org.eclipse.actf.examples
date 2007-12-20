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
<fennec xmlns:nvm3="urn:nvm3" 
	xmlns:f="http://www.ibm.com/xmlns/prod/aiBrowser/fennec" 
	xmlns:loc="http://www.ibm.com/xmlns/prod/aiBrowser/fennec/xml-query" 
	xmlns:flq="http://www.ibm.com/xmlns/prod/aiBrowser/fennec/flash-query" 
	xmlns="http://www.ibm.com/xmlns/prod/aiBrowser/fennec">
  <meta xmlns="http://www.ibm.com/xmlns/prod/AcTF/aiBrowser/selector/1.0">
    <documentation>NHK Top Page</documentation>
    <targetSite uri="http://www.nhk.or.jp/"/>
    <!-- creation data or something -->
  </meta>

  <node loc:idrefs="header">
	<h1/>
	<altText><ref loc:path="./H1[1]/IMG[1]"/> ヘッダー</altText>
	<node loc:idrefs="navExtra">
	  <altText>ページナビゲーション</altText>
	  <node loc:path="./LI/A[1]"/>
	</node>
	<node loc:idrefs="searchArea">
	  <altText>検索ボックス</altText>
	  <node loc:path="./INPUT"/>
	</node>
	<node loc:idrefs="navInfo">
	  <altText>ＮＨＫインフォメーション</altText>
	  <node loc:path="id('navManagement')/LI[1]/A[1]"/>
	  <node loc:path="./LI/A[1]"/>
	</node>
	<node loc:idrefs="navSub">
	  <altText>番組リンク</altText>
	  <node loc:path="./LI/A[1]"/>
	</node>
	<node loc:idrefs="navGlobal">
	  <h2/>
	  <altText>ジャンルリンク</altText>
	  <node loc:path="./LI/A[1]"/>
	</node>
  </node>

  <node loc:idrefs="content">
	<h1/>
	<altText>コンテンツ</altText>
	<node loc:idrefs="news">
	  <h2/>
	  <altText>ニュース <ref loc:idrefs="date"/></altText>
	  <node loc:path="id('topics')/UL/LI/A">
	    <!-- <altText><ref loc:path="."/></altText> -->
	  </node>
	  <node loc:idrefs="newsCategory">
		<altText>ニュースカテゴリー</altText>
		<node loc:path="./UL/LI/A[1]"/>
	  </node>
	  <node loc:idrefs="newsSpecial">
		<altText>ニュース特集</altText>
		<node loc:path="./UL/LI/A[1]"/>
	  </node>
	  <node loc:idrefs="newsExtra">
		<altText>その他のニュース</altText>
		<node loc:path="./UL/LI/A[1]"/>
	  </node>
	  <node loc:path="id('info')/DIV[1]/H2[1]">
	  	<node loc:path="../..//A[1]"/>
	  </node>
	</node>
	<node loc:idrefs="ticker" flq:top="true" flq:base="_level0">
	  <h2/>
	  <altText>ただいま放送中の番組</altText>
	  <node flq:targets="tick_txt_box.tick_txt"><altText><ref xmlns="urn:nvm3" xmlns:nvml="urn:nvm3" xmlns:x2="http://www.w3.org/TR/xhtml2" xmlns:wairole="http://www.w3.org/2005/01/wai-rdf/GUIRoleTaxonomy#" xmlns:state="http://www.w3.org/2005/07/aaa" flq:targets="."/></altText></node>
	</node>
	<node loc:idrefs="nhk_online" flq:top="true" flq:base="_level0">
	  <h2/>
	  <altText><ref loc:path="preceding::IMG[1]"/></altText>
	</node>

	<node loc:idrefs="editorial">
	  <h2/>
	  <altText><ref loc:path="./H2[1]"/></altText>
	  <node loc:path="./UL/LI/A[1]">
	  	<altText><ref loc:path="../IMG"/> <ref loc:path="."/></altText>
	  </node>
	</node>

	<node loc:idrefs="invite">
	  <h2/>
	  <altText><ref loc:path="./H2[1]"/></altText>
	  <node loc:path="./UL/LI/A[1]">
		<altText><ref loc:path="../IMG"/> <ref loc:path="."/></altText>
		<node loc:path="../text()"/>
	  </node>
	  <node loc:path="./P[1]/A[1]"/>
	</node>

	<node loc:idrefs="spotlight">
	  <h2/>
	  <altText><ref loc:path="./H2[1]"/></altText>
	  <node loc:path="./DIV[1]/UL[1]/LI[1]/A[1]"><altText>NHKデジタル教材</altText></node>
	  <node loc:path="./DIV[1]/UL[1]/LI[2]/A[1]"><altText>NHKアニメワールド</altText></node>
	  <node loc:path="./DIV[1]/UL[1]/LI[3]/A[1]"><altText>NHKオンライン「ラボブログ」</altText></node>
	  <node loc:path="./DIV[1]/UL[1]/LI[4]/A[1]"><altText>連続テレビ小説「どんど晴れ」</altText></node>
	  <node loc:path="./DIV[1]/UL[1]/LI[5]/A[1]"><altText>KIDS WORLD</altText></node>
	  <node loc:path="./DIV[1]/UL[1]/LI[6]/A[1]"><altText>名曲アルバム</altText></node>
	  <node loc:path="./DIV[1]/UL[1]/LI[7]/A[1]"><altText>NHK甲子園2007</altText></node>
	  <node loc:path="./DIV[1]/UL[1]/LI[8]/A[1]"><altText>にっぽん釣りの旅</altText></node>
	  <node loc:path="./DIV[1]/UL[1]/LI[9]/A[1]"><altText>新日曜美術館</altText></node>
	  <node loc:path="./DIV[1]/UL[1]/LI[10]/A[1]"><altText>ドラマ「ハゲタカ」</altText></node>
	  <node loc:path="id('review')/DIV/DIV/P[2]/A">
		<altText><ref loc:path="."/></altText>
		<node loc:path="../text()"/>
	  </node>
	</node>

	<node loc:idrefs="rsscontent">
	  <h2/>
	  <altText><ref loc:path="./H2[1]"/></altText>
	  <node loc:path="./P[1]/A[1]"><altText><ref xmlns="urn:nvm3" xmlns:nvml="urn:nvm3" xmlns:x2="http://www.w3.org/TR/xhtml2" xmlns:wairole="http://www.w3.org/2005/01/wai-rdf/GUIRoleTaxonomy#" xmlns:state="http://www.w3.org/2005/07/aaa" loc:path="."/></altText></node>
	  <node loc:path="./UL/LI/A[1]">
		<altText><ref loc:path="."/></altText>
		<node loc:path="./following::A[1]"><altText><ref xmlns="urn:nvm3" xmlns:nvml="urn:nvm3" xmlns:x2="http://www.w3.org/TR/xhtml2" xmlns:wairole="http://www.w3.org/2005/01/wai-rdf/GUIRoleTaxonomy#" xmlns:state="http://www.w3.org/2005/07/aaa" loc:path="."/></altText></node>
	  </node>
	</node>

	<node loc:path="id('rss')/IFRAME[2]/HTML[1]/BODY[1]">
	  <h2/>
	  <altText><ref loc:path="./H2[1]"/></altText>
	  <node loc:path="./P[1]/A[1]"/>
	  <node loc:path="./P[2]"/>
	  <node loc:path="./UL/LI/A[1]"/>
	</node>

	<node loc:idrefs="bannerArea">
	  <h2/>
	  <altText>バナーリンク</altText>
	  <node loc:path="./UL/LI/A[1]"/>
	</node>
  </node>

  <node loc:idrefs="other">
	<h1/>
	<altText>フッター</altText>
	<node loc:idrefs="institution receive about">
	  <altText><ref loc:path="./H2[1]"/></altText>
	  <node loc:path="./UL/LI/A[1]"><altText><ref xmlns="urn:nvm3" xmlns:nvml="urn:nvm3" xmlns:x2="http://www.w3.org/TR/xhtml2" xmlns:wairole="http://www.w3.org/2005/01/wai-rdf/GUIRoleTaxonomy#" xmlns:state="http://www.w3.org/2005/07/aaa" loc:path="."/></altText></node>
	</node>
	<node loc:idrefs="plugin">
	  <altText><ref loc:path="./P[1]"/></altText>
	  <node loc:path="./DIV[1]/P[1]/A[1]"><altText><ref xmlns="urn:nvm3" xmlns:nvml="urn:nvm3" xmlns:x2="http://www.w3.org/TR/xhtml2" xmlns:wairole="http://www.w3.org/2005/01/wai-rdf/GUIRoleTaxonomy#" xmlns:state="http://www.w3.org/2005/07/aaa" loc:path="."/></altText></node>
	</node>
	<node loc:path="id('footer')/P[2]"><altText><ref xmlns="urn:nvm3" xmlns:nvml="urn:nvm3" xmlns:x2="http://www.w3.org/TR/xhtml2" xmlns:wairole="http://www.w3.org/2005/01/wai-rdf/GUIRoleTaxonomy#" xmlns:state="http://www.w3.org/2005/07/aaa" loc:path="."/></altText></node>
  </node>
</fennec>