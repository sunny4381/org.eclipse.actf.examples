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
    <documentation>政府インターネットTV</documentation>
    <targetSite uri="http://nettv.gov-online.go.jp/"/>
    <!-- creation data or something -->
  </meta>

  <attach loc:idrefs="upper lower"/>

  <node loc:idrefs="memu">
    <altText>チャンネルリスト</altText>
    <h1/>
    <node loc:path="./TBODY/TR/TD[2]">
      <altText><ref loc:path="../TD[1]/IMG"/> <ref loc:path="../TD[2]/SPAN/text()"/></altText>
      <description><ref loc:idrefs="frame_text2"/></description>
    </node>
  </node>

  <node loc:idrefs="program_top">
    <altText>プログラム</altText>
    <h1/>
    <node>
      <altText><ref loc:path="./UL/LI[@class='osusume']"/></altText>
      <attach loc:idrefs="program_main"/>
    </node>
    <!--
    <group loc:path="./UL/LI[@class='new']">
      <altText><ref loc:path="."/></altText>
    </group>
    <group loc:path="./UL/LI[@class='ranking']">
      <altText><ref loc:path="."/></altText>
    </group>
    -->
  </node>

  <!-- <attach loc:path="id('main_content')/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/A"/> -->
  <attach loc:idrefs="channel_bottom_main footer">
    <h1/>
  </attach>
</fennec>