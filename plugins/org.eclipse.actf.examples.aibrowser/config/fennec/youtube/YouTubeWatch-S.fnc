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
  <meta xmlns="http://www.ibm.com/xmlns/prod/AcTF/aiBrowser/selector/1.0" 
		xmlns:nvml="urn:nvm3" xmlns:x2="http://www.w3.org/TR/xhtml2" 
		xmlns:wairole="http://www.w3.org/2005/01/wai-rdf/GUIRoleTaxonomy#" 
		xmlns:state="http://www.w3.org/2005/07/aaa">
    <documentation>Simplified You Tube (Video)</documentation>
    <targetSite uri="http://www.youtube.com/watch*"/>
    <targetSite uri="http://youtube.com/watch*"/>
    <!--targetSite uri="http://jp.youtube.com/watch*"/-->
    <!-- creation data or something -->
  </meta>
  <node loc:idrefs="movie_player" flq:top="true" flq:base="_level0.player.controller">
	<h1/>
    <altText><ref loc:idrefs="video_title"/><ref loc:idrefs="vidTitle"/></altText>
    <node flq:targets="play_button"><altText>Play</altText></node>
    <node flq:targets="pause_button"><altText>Pause</altText></node>
    <node flq:targets="stop_button"><altText>Stop</altText></node>
  </node>
  <attach loc:idrefs="searchDiv">
	<altText>Search</altText>
	<h1/>
  </attach>

  <node>
	<altText>About This Video</altText>
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
	<altText>Comments &amp; Responses</altText>
	<node loc:idrefs="vResponseDiv">
	  <altText><ref loc:path="./DIV[1]/TABLE[1]/TBODY[1]/TR[1]/TD[1]/B[1]"/></altText>
	  <node loc:path="./DIV[2]/DIV[1]/TABLE[1]/TBODY[1]/TR[1]/TD[2]/TABLE[1]/TBODY[1]/TR[1]/TD[1]/DIV/CENTER[1]/DIV[1]/A"/>
	  <node loc:path="./DIV[2]/DIV[1]/TABLE[1]/TBODY[1]/TR[1]/TD[3]/IMG">
	  	<altText>Next</altText>
	  </node>
	  <node loc:path="./DIV[1]/TABLE[1]/TBODY[1]/TR[1]/TD[1]//A"/>
	  <node loc:path="./DIV[1]/TABLE[1]/TBODY[1]/TR[1]/TD[2]//A"/>
	</node>

	<node loc:path="id('recent_comments')/DIV/DIV/DIV[@class='commentHead']/DIV[1]/B[1]/A[1]">
	  <altText><ref loc:path="."/></altText>
	  <node loc:path="../../../../DIV[2]/DIV[2]"/>
	  <node loc:path="../../../DIV[2]//B[1]">
	    <altText>Score: <ref loc:path="."/></altText>
	  </node>
	  <node loc:path="../../../DIV[2]//A"/>
	  <node loc:path="../../../../DIV[2]/DIV[1]/DIV[2]/A"/>
	</node>
	<attach loc:path="id('recent_comments')/DIV[1]"/>
	<attach loc:idrefs="commentPostDiv"/>
  </node>

  <node loc:path="id('exploreDiv')/MAP[1]/AREA[1]">
	<h1/>
	<altText>Tab Related Video</altText>
	<attach loc:idrefs="exRelatedDiv">
	  <node loc:path=".//IMG"><altText/></node>
	</attach>
  </node>

  <node loc:idrefs="otherVidsDiv">
	<altText/>
	<node loc:path="id('channel_videos_more')/B[1]/A[1]">
	  <h1/>
	  <altText>More Videos From This Channel</altText>
	  <attach loc:idrefs="more_channel_videos">
	  </attach>
	</node>
	<node>
	  <h1/>
	  <altText>Related Video</altText>
	  <attach loc:idrefs="relatedVidsBody">
	  </attach>
	</node>
  </node>

  <node loc:path="id('exploreDiv')/MAP[1]/AREA[2]">
	<h1/>
	<altText>Tab More from this user</altText>
	<attach loc:idrefs="exPlaylistDiv">
	  <node loc:path=".//IMG"><altText/></node>
	</attach>
  </node>
  <node loc:path="id('exploreDiv')/MAP[1]/AREA[3]">
	<h1/>
	<altText>Tab Playlists</altText>
	<attach loc:idrefs="exUserDiv">
	  <node loc:path=".//IMG"><altText/></node>
	</attach>
  </node>


  <attach loc:idrefs="promotedVidsContainer">
	<altText>Promoted Videos</altText>
	<h1/>
	<node loc:path=".//IMG"><altText/></node>
  </attach>

  <attach loc:idrefs="dVidsDiv">
	<h1/>
	<node loc:path=".//IMG"><altText/></node>
  </attach>




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


  <attach loc:idrefs="footerDiv">
	<h1/>
	<altText>Footer</altText>
  </attach>
</fennec>