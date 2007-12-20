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
    <documentation>政府インターネットTV (チャンネル)</documentation>
    <targetSite uri="http://nettv.gov-online.go.jp/channel.html*"/>
    <!-- creation data or something -->
  </meta>

<!--
  <group  
   loc:path="id('movi_img')//OBJECT"
   flq:top="true" flq:base="_level0.MovieMain_mc">
-->
  <node loc:idrefs="ch000_mov01" flq:top="true" flq:base="_level0.MovieMain_mc">
    <h1/>
    <!-- <altText>ムービー</altText> -->
    <altText><ref loc:idrefs="inTitle"/></altText>
    <node flq:targets="Play_btn"><altText>再生</altText></node>
    <node flq:targets="Pause_btn"><altText>ポーズ</altText></node>
    <node flq:targets="Stop_btn"><altText>停止</altText></node>
    <!--
    <altText flq:targets="Prev_btn">Prev</altText>
    <altText flq:targets="Next_btn">Next</altText>
    -->
    <node flq:targets="MovieTime_mc"><altText>現在時間 <ref xmlns="urn:nvm3" xmlns:nvml="urn:nvm3" xmlns:x2="http://www.w3.org/TR/xhtml2" xmlns:wairole="http://www.w3.org/2005/01/wai-rdf/GUIRoleTaxonomy#" xmlns:state="http://www.w3.org/2005/07/aaa" flq:targets=".DurationTime_txt"/> 全体時間 <ref xmlns="urn:nvm3" xmlns:nvml="urn:nvm3" xmlns:x2="http://www.w3.org/TR/xhtml2" xmlns:wairole="http://www.w3.org/2005/01/wai-rdf/GUIRoleTaxonomy#" xmlns:state="http://www.w3.org/2005/07/aaa" flq:targets=".TotalTime_txt"/></altText></node>
    <attach loc:idrefs="movi_desc">
      <altText>番組の説明</altText>
      <h2/>
    </attach>
  </node>

  <node>
    <h1/>
    <altText>プログラム</altText>
    <attach loc:idrefs="program"/>
  </node>

  <node>
    <h1/>
    <altText>チャンネルリスト</altText>
    <attach loc:idrefs="chlist"/>
  </node>

  <!-- <attach loc:path="id('main_content')/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/A"/> -->
  <!-- attach loc:idrefs="header"/ -->
  <!-- attach loc:idrefs="channel_bottom_main"/ -->
</fennec>