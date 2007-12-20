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
<fennec xmlns:loc="http://www.ibm.com/xmlns/prod/aiBrowser/fennec/xml-query"
	xmlns:flq="http://www.ibm.com/xmlns/prod/aiBrowser/fennec/flash-query"
	xmlns="http://www.ibm.com/xmlns/prod/aiBrowser/fennec">
  <meta xmlns="http://www.ibm.com/xmlns/prod/AcTF/aiBrowser/selector/1.0">
    <documentation>About Adobe</documentation>
    <targetSite uri="http://www.adobe.com/aboutadobe/"/>
    <!-- creation data or something -->
  </meta>
  
  <node>
    <altText>About Adobe</altText>

    <node loc:idrefs="site-menu">
      <h1/>
      <altText>Site Menu</altText>
      <node loc:path="./UL[1]/LI/SPAN[1]/A[1]">
	<altText><ref loc:path="."/></altText>
	<node loc:path="../../DL[1]/DT[1]">
	  <altText><ref loc:path="."/></altText>
	  <node loc:path="../DD[count(preceding-sibling::DT) = 1]">
	    <altText><ref loc:path="."/></altText>
	  </node>
	</node>
	<node loc:path="../../DL[1]/DT[2]">
	  <altText><ref loc:path="."/></altText>
	  <node loc:path="../DD[count(preceding-sibling::DT) = 2]">
	    <altText><ref loc:path="."/></altText>
	  </node>
	</node>
	<node loc:path="../../DL[1]/DT[3]">
	  <altText><ref loc:path="."/></altText>
	</node>
	<node loc:path="../../UL[1]/LI/A[1]">
	  <altText><ref loc:path="."/></altText>
	</node>
      </node>
    </node>

    <node loc:idrefs="site-search">
      <h1/>
      <altText>Search Bar</altText>
      <node loc:path="./P[1]/INPUT[2]"><altText/></node>
      <node loc:path="./P[1]/BUTTON[1]"><altText><ref loc:path="."/></altText></node>
    </node>
    
    <node loc:idrefs="user-menu">
      <h1/>
      <altText>User Menu</altText>
      <node loc:path="./UL[1]/LI[1]"><altText>Go to Cart</altText></node>
      <node loc:path="./UL[1]/LI[position() &gt; 2]"><altText><ref loc:path="."/></altText></node>
    </node>

    <node loc:idrefs="mymovie" flq:top="true" flq:base="_level0.mainContent_mc">
      <h1/>
      <altText>Movies <ref flq:targets="instance13.display_txt"/></altText>
      <node flq:targets="instance12.display_txt"><altText/></node>
      <node flq:targets="instance11.display_txt"><altText/></node>
      <node flq:targets="instance10">
	<altText><ref flq:targets=".label_mc.display_txt"/> All</altText>
      </node>
      <node flq:targets="mainNav_mc.label_0_mc.label_mc.display_txt">
	<altText>Play <ref flq:targets="."/></altText>
      </node>
      <node flq:targets="mainNav_mc.label_1_mc.label_mc">
	<altText>Play <ref flq:targets=".display_txt"/></altText>
      </node>
      <node flq:targets="mainNav_mc.label_2_mc.label_mc">
	<h3/>
	<altText>Open <ref flq:targets=".display_txt"/>Movies by click</altText>
	<node flq:targets="subsection_list_mc.button_*_mc">
	  <altText>Open <ref flq:targets=".label_mc.display_txt"/> by click</altText>
	  <attach trigger="click" auto="false" waitContents="true" flq:targets="subsection_pod_mc" flq:depth="-16379">
	    <altText><ref flq:targets="." flq:depth="-16383"/></altText>
	    <node flq:targets="." flq:depth="-16381">
	      <altText><ref flq:targets=".display_txt"/></altText>
	    </node>
	    <node flq:targets="." flq:depth="-16379">
	      <altText><ref flq:targets=".label_mc.display_txt"/></altText>
	    </node>
	  </attach>
	</node>
	<node flq:targets="controller.prevButton"><altText>Previous</altText></node>
	<node flq:targets="controller.nextButton"><altText>Next</altText></node>
      </node>
      <node flq:targets="mainNav_mc.label_3_mc.label_mc">
	<altText>Play <ref flq:targets=".display_txt"/></altText>
      </node>
      <node flq:targets="instance8"><altText>Learn More &gt;</altText></node>
      <node flq:targets="_level0.mainContent_mc" flq:depth="-16345">
	<altText><ref flq:targets=".display_txt"/></altText>
	<node flq:targets="_level0.mainContent_mc" flq:depth="-16347">
	  <altText><ref flq:targets=".display_txt"/></altText>
	</node>
	<node flq:targets="_level0.mainContent_mc" flq:depth="-16343">
	  <altText>There are 3 links but inaccessible. <ref flq:targets=".display_txt"/></altText>
	</node>
	<node flq:targets="_level0.mainContent_mc" flq:depth="-16341">
	  <altText>There are 3 links but inaccessible. <ref flq:targets=".display_txt"/></altText>
	</node>
	<node flq:targets="_level0.mainContent_mc" flq:depth="-16339">
	  <altText><ref flq:targets=".display_txt"/></altText>
	</node>
      </node>
    </node>

    <node loc:idrefs="C1-body">
      <h1/>
      <altText><ref loc:path="./H3[1]"/></altText>
      <node loc:path="./DIV[position() = 2 or position() = 3]/UL[1]/LI/A[1]">
	<altText><ref loc:path="."/></altText>
      </node>
    </node>

    <node loc:idrefs="C1-body">
      <h1/>
      <altText><ref loc:path="./H3[2]"/></altText>
      <node loc:path="./DIV[position() = 4 or position() = 5]/DIV/H4/A">
	<altText><ref loc:path="."/></altText>
	<node loc:path="../../P[2]"/>
      </node>
    </node>


    <node loc:path="id('C2')//DL[contains(@class, 'side-menu')]">
      <h1/>
      <altText>Side Menu</altText>
      <node loc:path="./child::*/A[1]"/>
    </node>

    <attach loc:idrefs="globalfooter">
      <h1/>
      <altText>Footer Links</altText>
    </attach>

  </node>
</fennec>