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
  <meta xmlns="http://www.ibm.com/xmlns/prod/AcTF/aiBrowser/selector/1.0" 
		xmlns:x2="http://www.w3.org/TR/xhtml2" 
		xmlns:wairole="http://www.w3.org/2005/01/wai-rdf/GUIRoleTaxonomy#" 
		xmlns:state="http://www.w3.org/2005/07/aaa">
    <documentation>Simplified You Tube (Video)</documentation>
    <targetSite uri="http://jp.youtube.com/watch*"/>
    <!-- creation data or something -->
  </meta>
  <node loc:idrefs="movie_player" flq:top="true" flq:base="_level0.player.controller">
	<h1/>
    <altText><ref loc:idrefs="video_title"/><ref loc:idrefs="vidTitle"/></altText>
    <node flq:targets="play_button"><altText>再生</altText></node>
    <node flq:targets="pause_button"><altText>一時停止</altText></node>
    <node flq:targets="stop_button"><altText>停止</altText></node>
  </node>
  <attach loc:idrefs="search-form">
	<altText>検索</altText>
	<h1/>
  </attach>

  <node>
	<altText>この動画について</altText>
	<h1/>
	<attach loc:path="id('channelVidsTop')/TABLE[1]/TBODY[1]/TR[1]/TD[2]/DIV[1]"/>
	<attach loc:idrefs="userInfoDiv"/>
	<node>
	  <altText><ref loc:path="id('ratingAndStatsDiv')/TABLE[1]//DIV[@class='viewsDiv']/SPAN"/>
	  <ref loc:path="id('actionsAndStatsDiv')/DIV[6]"/>
	  </altText>
	</node>
	<attach loc:path="id('actionsAndStatsDiv')/DIV[2]">
	  <node loc:path=".//IMG"><altText/></node>
	</attach>	  
	<attach loc:path="id('actionsAndStatsDiv')/DIV[3]">
	  <node loc:path=".//IMG"><altText/></node>
	</attach>	  
	<attach loc:path="id('actionsAndStatsDiv')/DIV[4]">
	  <node loc:path=".//IMG"><altText/></node>
	</attach>	  
	<attach loc:path="id('actionsAreaDiv')/DIV[1]">
	  <node loc:path=".//IMG"><altText/></node>
	</attach>
	<attach loc:idrefs="vidFacetsTable">
	</attach>
	<attach loc:idrefs="embedDiv">
	</attach>
  </node>

  <node>
	<h1/>
	<altText>コメントと動画レスポンス</altText>
	<node loc:idrefs="vResponseDiv">
	  <altText><ref loc:path="./DIV[1]/TABLE[1]/TBODY[1]/TR[1]/TD[1]/B[1]"/></altText>
	  <node loc:path="./DIV[2]/DIV[1]/TABLE[1]/TBODY[1]/TR[1]/TD[2]/TABLE[1]/TBODY[1]/TR[1]/TD[1]/DIV/CENTER[1]/DIV[1]/A"/>
	  <node loc:path="./DIV[2]/DIV[1]/TABLE[1]/TBODY[1]/TR[1]/TD[3]/IMG">
	  	<altText>次</altText>
	  </node>
	  <node loc:path="./DIV[1]/TABLE[1]/TBODY[1]/TR[1]/TD[1]//A"/>
	  <node loc:path="./DIV[1]/TABLE[1]/TBODY[1]/TR[1]/TD[2]//A"/>
	</node>

	<node loc:path="id('recent_comments')/DIV/DIV/DIV[@class='commentHead']/DIV[1]/B[1]/A[1]">
	  <altText><ref loc:path="."/></altText>
	  <node loc:path="../../../../DIV[2]/DIV[2]"/>
	  <node loc:path="../../../DIV[2]//B[1]">
	    <altText>スコア: <ref loc:path="."/></altText>
	  </node>
	  <node loc:path="../../../DIV[2]//A"/>
	  <node loc:path="../../../../DIV[2]/DIV[1]/DIV[2]/A"/>
	</node>
	<attach loc:path="id('recent_comments')/DIV[1]"/>
	<attach loc:idrefs="commentPostDiv"/>
  </node>

  <node loc:path="id('exploreDiv')/MAP[1]/AREA[1]">
	<h1/>
	<altText>関連動画タブ</altText>
	<attach loc:idrefs="exRelatedDiv">
	  <node loc:path=".//IMG"><altText/></node>
	</attach>
  </node>

  <node loc:idrefs="otherVidsDiv">
	<altText/>
	<node loc:path="id('channel_videos_more')/B[1]/A[1]">
	  <h1/>
	  <altText>このチャンネルのその他の動画</altText>
	  <attach loc:idrefs="more_channel_videos">
	  </attach>
	</node>
	<node>
	  <h1/>
	  <altText>関連動画</altText>
	  <attach loc:idrefs="relatedVidsBody">
	  </attach>
	</node>
  </node>

  <node loc:path="id('exploreDiv')/MAP[1]/AREA[2]">
	<h1/>
	<altText>このチャンネルのその他の動画タブ</altText>
	<attach loc:idrefs="exPlaylistDiv">
	  <node loc:path=".//IMG"><altText/></node>
	</attach>
  </node>
  <node loc:path="id('exploreDiv')/MAP[1]/AREA[3]">
	<h1/>
	<altText>再生リストタブ</altText>
	<attach loc:idrefs="exUserDiv">
	  <node loc:path=".//IMG"><altText/></node>
	</attach>
  </node>


  <attach loc:idrefs="promotedVidsContainer">
	<altText>ディレクター動画</altText>
	<h1/>
	<node loc:path=".//IMG"><altText/></node>
  </attach>

  <attach loc:idrefs="dVidsDiv">
	<h1/>
	<node loc:path=".//IMG"><altText/></node>
  </attach>

  <attach loc:idrefs="footer">
	<h1/>
	<altText>フッター</altText>
  </attach>
</fennec>