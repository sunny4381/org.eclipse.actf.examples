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
<fennec 
	xmlns:f="http://www.ibm.com/xmlns/prod/aiBrowser/fennec" 
	xmlns:loc="http://www.ibm.com/xmlns/prod/aiBrowser/fennec/xml-query" 
	xmlns:flq="http://www.ibm.com/xmlns/prod/aiBrowser/fennec/flash-query" 
	xmlns="http://www.ibm.com/xmlns/prod/aiBrowser/fennec">
  <meta xmlns="http://www.ibm.com/xmlns/prod/AcTF/aiBrowser/selector/1.0">
    <documentation>NHK Sport</documentation>
    <targetSite uri="http://www.nhk.or.jp/sports/"/>
    <!-- creation data or something -->
  </meta>

  <node loc:idrefs="header">
	<h1/>
	<altText>ＮＨＫメニュー</altText>
	<node loc:path="DIV[2]">
	  <altText>メニュー１</altText>
	  <node loc:path="../DIV[1]/A[1]"/>
	  <node loc:path="./DIV[1]/A[1]"/>
	  <node loc:path="./DIV[1]/DIV[1]/UL[1]/LI/A[1]"/>
	</node>
	<node loc:path="DIV[2]/UL[1]">
	  <altText>メニュー２</altText>
	  <node loc:path="./LI/A[1]"/>
	</node>
	<node loc:idrefs="genre_area">
	  <altText>メニュー３</altText>
	  <node loc:path="UL[1]/LI/A[1]"/>
	</node>
  </node>

  <node loc:path="id('mainFrame')/HTML[1]/BODY[1]/TABLE[1]/TBODY[1]/TR[2]/TD[1]/OBJECT[1]" flq:top="true" flq:base="_level0.screen">
	<h1/>
	<altText>スポーツオンライン</altText>
	<node flq:targets="yakyu" flq:base="_level0.screen">
	  <altText>野球</altText>
	  <node flq:targets="yakyu_b.yakyu_ama">
		<altText>アマ野球</altText>
	  </node>
	  <node flq:targets="yakyu_b.yakyu_pro">
		<altText>プロ野球</altText>
	  </node>
	  <node flq:targets="yakyu_b.yakyu_mlb">
		<altText>ＭＬＢ</altText>
	  </node>
	</node>
	<node flq:targets="sumo_b">
	  <altText>相撲</altText>
	</node>
	<node flq:targets="soccer_b">
	  <altText>サッカー</altText>
	</node>
	<node flq:targets="golf" flq:base="_level0.screen">
	  <altText>ゴルフ</altText>
	  <node flq:targets="golf_b.golf_pga">
		<altText>ＰＧＡツアー</altText>
	  </node>
	  <node flq:targets="golf_b.golf_oth">
		<altText>その他</altText>
	  </node>
	</node>
	<node flq:targets="amefoot" flq:base="_level0.screen">
	  <altText>アメリカンフットボール</altText>
	  <node flq:targets="amefoot_b.amefoot_nfl">
		<altText>ＮＦＬ</altText>
	  </node>
	</node>
	<node flq:targets="games_b">
	  <altText>総合大会　Games</altText>
	</node>
	<node flq:targets="keiba_b">
	  <altText>競馬・馬術</altText>
	</node>
	<node flq:targets="rugby_b">
	  <altText>ラグビー</altText>
	</node>
	<node flq:targets="gym_b">
	  <altText>体操・新体操</altText>
	</node>
	<node flq:targets="others_b">
	  <altText>この他の注目種目</altText>
	</node>
	<node flq:targets="xgames_b">
	  <altText>Xゲーム</altText>
	</node>
	<node flq:targets="usa" flq:base="_level0.screen">
	  <altText>アメリカンメジャースポーツ</altText>
	  <node flq:targets="usa_b.yakyu_mlb">
		<altText>ＭＬＢ</altText>
	  </node>
	  <node flq:targets="usa_b.golf_pga">
		<altText>ＰＧＡツアー</altText>
	  </node>
	  <node flq:targets="usa_b.amefoot_nfl">
		<altText>ＮＦＬ</altText>
	  </node>
	</node>	
  </node>   

  <node loc:path="id('mainFrame')/HTML[1]/BODY[1]/TABLE[2]/TBODY[1]/TR[1]/TD[1]/TABLE[1]/TBODY[1]/TR[1]/TD[2]/TABLE[1]">
	<h1/>
	<altText>おすすめページ</altText>
	<node loc:path="./TBODY[1]/TR[1]/TD[1]/A[1]"/>
	<node loc:path="./TBODY[1]/TR[1]/TD[2]/TABLE[1]//A"/>
  </node>

  <node loc:path="id('mainFrame')/HTML[1]/BODY[1]/TABLE[2]/TBODY[1]/TR[1]/TD[1]/TABLE[1]/TBODY[1]/TR[2]/TD[1]/TABLE[1]">
	<h1/>
	<altText>今週の注目！</altText>
	<node loc:path=".//A"/>
  </node>

  <node loc:path="id('mainFrame')/HTML[1]/BODY[1]/TABLE[2]/TBODY[1]/TR[1]/TD[1]/TABLE[1]/TBODY[1]/TR[2]/TD[3]/TABLE[1]">
	<h1/>
	<altText>スポーツトピックス</altText>
	<node loc:path=".//A"/>
  </node>

  <node loc:path="id('mainFrame')/HTML[1]/BODY[1]/TABLE[2]/TBODY[1]/TR[1]/TD[1]/TABLE[2]">
	<h1/>
	<altText>リンク</altText>
	<node loc:path=".//TD/child::*"/>
  </node>
</fennec>