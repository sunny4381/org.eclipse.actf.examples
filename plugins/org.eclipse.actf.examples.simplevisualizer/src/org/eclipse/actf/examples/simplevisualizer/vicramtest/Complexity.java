/*******************************************************************************
 * Copyright (c) 2009 University of Manchester and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eleni Michailidou - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.examples.simplevisualizer.vicramtest;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;


import org.eclipse.actf.model.dom.dombycom.IElementEx;
import org.eclipse.actf.model.dom.dombycom.INodeEx;
import org.eclipse.actf.model.dom.dombycom.IStyle;
import org.eclipse.actf.model.ui.IModelService;
import org.eclipse.actf.model.ui.ModelServiceSizeInfo;
import org.eclipse.actf.model.ui.editor.browser.ICurrentStyles;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserACTF;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserStyleInfo;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.eclipse.actf.util.FileUtils;
import org.eclipse.swt.graphics.Rectangle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Complexity {

	
	 public static int links, 
		images, 
		tables,
		paragraphs,
		forms,
		linkedWords,
		unlinkedWords,
		listWords,
		rowWords,
		colWords,
		rows,
		columns,
		lists,
		listItems,
		wordCount,
		blocks, TLC;
	 public static String words;
	 public static boolean insideLink = false;
	 public static boolean insideList = false;
	 public static boolean insideTableRow = false;
	 public static boolean insideTableCol = false;
	       
	 public static String linkedString, unlinkedString;
	private static int tableStyle;
	private static String backgroundColor;
	private static String display;
	private static String backgroundColorParent;
	private static int TLC1;
	private static int TLC2;
	private static int layoutTable;
	private static int div;
	
	private static boolean findName = true;
	private static boolean isLayout;
	private static int dataTables;
	private static boolean isTLC = false;
	private static boolean backgroundDif;
	private static String backgroundColorGrandParent;
	private static boolean blockChild;
	private static boolean headingTLC;
	private static boolean singlesChildren;
	private static boolean lastIsImg;
	private static int len;
	private static String borderWidth;
	private static int borderLen;
	private static boolean visibleBorder;
	private static boolean isPx;
	private static Rectangle rectangle;
	private static Map<String, ICurrentStyles> styleMap;
	private static Object xpath;
	private static boolean veryComplex;
/*

	1. Calculates the level of complexity and aesthetics of Web pages
	Equations:
	VisualComplexity = 1.743 + 0.097 (TLC) + 0.053 (Words) + 0.003 (Images)
	THE FINAL Visual Complexity Score is equal to VisualComplexity/10. If the score is > 10 then VC =10*, and * denotes extreme complexity 
																		due to length of page, a lot of text, a large number of images etc.
	Clean = 8.342 - 0.094 (TLC) - 0.004 (Words) + 1.069 (Familiarity) - 0.060 (Images)
	     = 8.307 - 0.115 (TLC) - 0.003 (Words)
	Interesting = 4.955 + 1.560 (Familiarity)
	           = 5.342 + 0.026 (Links) - 0.003 (Words)
	Organisation = 7.459 -0.004 (Words) + 1.354 (Familiarity) - 0.064 (Images)
	            = 7.297 - 0.003 (Words)
	Clear = 7.365 -0.005 (Words) + 1.417 (Familiarity) - 0.082 (Images)
	     = 7.172 -0.082 (TLC)
	Beautiful = 5.358 + 1.267 (Familiarity) - 0.064 (TLC)
	
	Aesthetic scores output will not show as an output. For more on the aesthetics please look at the EiVAA project


	Variables that need to identify:
	TLC, WordCount, Images, Links

	Variables that will identify:
	TLC, WordCount, Images, Tables, Links, Paragraphs, Lists

*/	 
	public static  String calculate(){
		//calculate method is called from PartControlSimpleVisualizer
		//is used to call the appropriate methods that count the page elements 
		//and calculates complexity and aesthetics
		
		IModelService modelService = ModelServiceUtils.getActiveModelService();
		
		if (modelService instanceof IWebBrowserACTF) {
			IWebBrowserACTF browser = (IWebBrowserACTF) modelService;
			IWebBrowserStyleInfo style = browser.getStyleInfo();
			//ModelServiceSizeInfo sizeInfo = style.getSizeInfo(true);
			 
			styleMap = style.getCurrentStyles();
			//for (String xpath : styleMap.keySet()) {
				//ICurrentStyles curStyle = styleMap.get(xpath);
				//rectangle =  curStyle.getRectangle();
				//System.out.println("Rectangle - " + rectangle);
			//}
		}
		Document doc = modelService.getDocument();
		Document docLive = modelService.getLiveDocument();
		
		if (doc==null || docLive==null){
			return "doc is null";
		}
		else{
		//get document element
		//identify tag name and node type 
			
			Element docElement = doc.getDocumentElement();
			Element docLiveElement = docLive.getDocumentElement();
			//test
			//String element = docElement.getElementsByTagName("body").item(0).getNodeName();
			
			//initialize all variables/counters
		    // reset them to zero
		    //for example: var = links/images/words/TLC/tables/lists/paragraphs/div/
			links = 0;
			lists = 0;
			images = 0;
			wordCount = 0;
			blocks = 0;
			TLC = 0;
			TLC1 = 0;
			TLC2 = 0;
			tables=0;
			layoutTable=0;
			div = 0;
			dataTables=0;
			
			isTLC = false;
			headingTLC = false;
			singlesChildren = false;
			lastIsImg = false;
			visibleBorder = false;
			isPx = false;
			
			
			//test
			//String initial = "Links - " + links + "\nImages - " + images + "\nWords - "+ wordCount + "\n";
			
			//call methods that count all elements
		    //and calculate final score for VCS and aesthetics
			countElements(docElement.getElementsByTagName("body").item(0));
			
			Node node = docLiveElement.getElementsByTagName("body").item(0);
			//System.out.println("name of begining - " + node.getNodeName());
			NodeList NodeChildren = node.getChildNodes();
	           if (NodeChildren != null) {
	               int len = NodeChildren.getLength();
	               for (int i = 0; i < len; i++) {
	            	   //System.out.println(" ----- NEW NODE ---");
	            	   findName = true;
	            	   singlesChildren = false;
	            	   isTLC = false;
	            	   headingTLC=false;
	            	   countTLC(NodeChildren.item(i));
	               }
	           }    
	       
	      // countTLC(docLiveElement.getElementsByTagName("body").item(0));

	       //calculate equation results
	       //VCS/clean/interesting/clear/organised/beautiful
	       //return equation results  as a string      
		
	       String counters = "\nCounters:" + 
			"\nLinks - " + links + "\nLists - " + lists + "\nImages - " + images + "\nWords - "+ wordCount + "\nBlocks - " + blocks +
			"\nTables - " + tables + "\nTableStyles - " + tableStyle + "\nTLC - " + TLC + "\nTLC1 - TLC2 " + TLC1 + TLC2 +
			"\nDataTables - " + dataTables + "\nLayout table - " + layoutTable + "\nDiv - " + div + "\n";
	       
	       System.out.println(counters);
	       /*
	        * Visual Complexity Score
	        * Within the tested (selected and randomly while browsing) pages the scores mostly ranged from 0 - 100. Very few pages
	        * generated complexity score more than 100 and that was due to long page (=> big number of word count etc). So, the score below
	        * will be divided by 10 and if the score is more than 10 it will display a **. A description will added explaining the case of **.
	        */
	       double VCS = (1.743 + 0.097 * (TLC) + 0.053 *(wordCount) + 0.003 * (images))/10;
	       if(VCS > 10){
	    	   veryComplex = true;
	       }
	       else
	    	   veryComplex = false;
	       /*
	   	   //double Clean = 8.342 - 0.094 * (TLC) - 0.004 * (wordCount) + 1.069 * (Familiarity) - 0.060 * (images);
	       double Clean  = 8.307 - 0.115 * (TLC) - 0.003 * (wordCount);
	   	   //Interesting = 4.955 + 1.560 (Familiarity)
	       double Interesting  = 5.342 + 0.026 * (links) - 0.003 *(wordCount);
	   	   //double Organisation = 7.459 -0.004 * (wordCount) + 1.354 (Familiarity) - 0.064 (Images)
	       double Organisation = 7.297 - 0.003 * (wordCount);
	   	   //Clear = 7.365 -0.005 (wordCount) + 1.417 (Familiarity) - 0.082 (Images)
	   	   double Clear  = 7.172 -0.082 * (TLC);
	   	//Beautiful = 5.358 + 1.267 (Familiarity) - 0.064 (TLC)
	   	 * */
	   	 
	   	   String results = "";
	   	   String resultsA = "";
	   	   String resultsB ="";
	   	   String resultsC ="";
	   	   if(veryComplex==true){
	   		   VCS = 10.0;
	   		   resultsA = "VCS = " + VCS + " **";
	   	   }
	   	   else{
	   		resultsA = "VCS = " + VCS;
	   	   }
	   	   resultsB = "The Visual Complexity Score (VCS) ranges from 0 to 10, with 0 being very visually simple and 10 very visually complex. For more details please visit the ViCRAM Project Webpages at http://hcw.cs.manchester.ac.uk/research/vicram/" +
			"\n\nNOTE: **(Two stars) after the VCS, signifies that the page just tested was ranked with a score bigger than 10 which is the maximum of our scale. This could happen because the page is very long which means that the page has a large number of structural elements";
			/*
	   	   //Aesthetic Scores are not to be published at this stage. From now, Aesthetics are part of EiVAA project
	       resultsC ="Aesthetic Scores:"+ "\nClean = " + Clean + "\nInteresting = " + Interesting + "\nOrganisation = " + Organisation + "\nClear = " + Clear;
		   */
	       results = "=============\n\n"+ resultsA + "\n"+resultsB + "\n\n=============";
	       System.out.println(results);
			return results + "\n\n";// + counters;
			
		}
	}
	

	
	/*
	 * countElements is a recursive method that performs DOM analysis
	 * counts the page elements by recursively going through the node using DOM parser 
	 */
	public static  void countElements(Node node){
		
		if (node == null) 
            return;

		
		int type = node.getNodeType();
		//System.out.print("Type: " +type + "Name: " + node.getNodeName());
		if (type == Node.DOCUMENT_NODE){
			countElements(((Document) node).getDocumentElement());
		}
		if (type == Node.ELEMENT_NODE){
			//checks and counts the type of element
			String nodeName = node.getNodeName();
            if (nodeName.equalsIgnoreCase("a"))
     	        links++;
            if (nodeName.equalsIgnoreCase("p"))
	        	paragraphs++;
	        if (nodeName.equalsIgnoreCase("img"))
	        	images++;
	        if (nodeName.equalsIgnoreCase("form"))
	        	forms++;
	        if (nodeName.equalsIgnoreCase("div"))
	        	div++;
	        
	        //to find how many tables are used for layout we check for tables with:
	        // 1 col + multiple rows OR multiple cols + 1 row
	       if (nodeName.equalsIgnoreCase("table")){
	        	
	    	   tables++;
	        	//tableCellLayout(node);
	        	
	        }
	        	
	        
	        if (nodeName.equalsIgnoreCase("ul") || nodeName.equalsIgnoreCase("ol")){
	        
	        	lists++;   
	        	insideList = true;
	        }
	     
	        
	        if (nodeName.equalsIgnoreCase("li")){
	        	//Node parentNode = node.getParentNode();	
	        	listItems++;
	        }
	        
	        //recurse to find the rest of the counters
	        NodeList children = node.getChildNodes();
            if (children != null) {
                int len = children.getLength();
                for (int i = 0; i < len; i++) {
                	countElements(children.item(i));
                }
            }
	
	}//ends if (type == Node.ELEMENT_NODE)
	//Get the word count	
		if (type == Node.TEXT_NODE){
        	//where the word count begins
        	
        	String string = node.getNodeValue();
        	words = " "+ string;
        	if (words == null)
               	wordCount = 0;
            else
            {
            	StringTokenizer total = new StringTokenizer(words, "'?!@#$&*/-,:.<>()~;=_");
            	//int count2 = 0;
            	while (total.hasMoreTokens() == true)
            	{
            		StringTokenizer token = new StringTokenizer(total.nextToken());
            		wordCount += token.countTokens();
            	}
             }
        	
            	
        	
        }//ends if (type == Node.TEXT_NODE)

	
      
	}//ends countElements
	
	
/*
 * CountTLC(node) identifies the block level elements
  recursively by starting with the basic rules.
  I will add more rules later during testing
  
  boolean isTLC - to avoid TLC recognised within TLCs that are basically the same (i.e. tables within tables)
  
  NOTE: During coding, the TLC that the algorithm detected was highlighted on the page. Now, the highlight command is commended out.
  If one would like to see the TLCs that the algorithm detects please remove the second line comments that start as: 
  	//TLC - highlight
  	//((INodeEx) node).highlight();
 */

public static void countTLC(Node node){	
	
	if (node == null) 
        return;
	
	int type = node.getNodeType();
	
	//System.out.println("type - "+ type + " name - "+ node.getNodeName());
	
	if (type == Node.DOCUMENT_NODE){
		countTLC(((Document) node).getDocumentElement());
	}
	
	if (type == Node.ELEMENT_NODE){
		//System.out.println(node.getNodeName() + " length " + node.getChildNodes().getLength());
		
		/*ICurrentStyles curStyle = styleMap.get(xpath);
		rectangle =  curStyle.getRectangle();
		System.out.println("Rectangle" + rectangle);
		//rectangle = ((ICurrentStyles)node).getRectangle();*/
		if(node instanceof IElementEx) {
			IStyle style = ((IElementEx)node).getStyle();				
			display = (String) style.get("display");
		//	System.out.println("display - " + display);
			borderWidth = (String)style.get("borderWidth");
			//if(node.getChildNodes().getLength() >0){
				//ICurrentStyles rectangleStyle = (()node).getRectangle();
				//rectangle = ((ICurrentStyles)node).getRectangle();
				//System.out.println("Rectangle" + rectangle);
			//}
				
			//get border information if node is a div
			
			
			if(node.getNodeName().equalsIgnoreCase("div")){
				borderLen = borderWidth.length();
				//borderWidth returns medium or Npx (N=number)
				//need to check if the borderWidth is a number and is >0
				isPx = borderWidth.contains("px");
				
				//System.out.println("Border - "+ borderWidth + "isPx - " + isPx);
				int px = 0;
				if(isPx == true){
					//need to use StringTokenizer to get the string that contains the string part with the px string in it
					//some elements have different px for left/right etc (e.g - borderWidth = medium medium 5px)
					StringTokenizer borderToken = new StringTokenizer(borderWidth, " ");
	            	String pixels = "";
	            	String borderPx = "";
					while (borderToken.hasMoreTokens() == true)
	            	{
	            		String token = borderToken.nextToken();
	            		if(token.contains("px")==true){
	            			//get the number in front of px
	            			int tokenLength = token.length();
	            			borderPx  = token.substring(0, tokenLength-2);
	            			
	            		}
	            		
	            	}
	            	//System.out.println(pixels);
	            	px  = Integer.parseInt(borderPx);						
					if(px > 0){
						visibleBorder = true;
					}	
					else
						visibleBorder = false;
					
					
				}					
				
			}
			
			
		     
		}
		if(display == null)
			{display = "";}
		
		
		//Case1: the element is displayed as a Block and has no block level children
		//this is for elements such as standalone images 
		//node = block && has no block children
		NodeList children = node.getChildNodes();
		//len = children.getLength();
		len=0;
		for(int i=0; i< children.getLength(); i++){
			Node child = children.item(i);
			int childType = child.getNodeType();
			if(childType == 1)
				len++;
		}
		
	//	System.out.println("Length -" + len);
		
		
		blockChild = false;
		lastIsImg=false;
		
		//headingTLC = false;
		String nodeName = node.getNodeName();
		
	    if(len==1){
       	 
       	 for(int i=0; i< children.getLength(); i++){
			Node child = children.item(i);
			//System.out.println("Last Child: "+ child.getNodeName());
			//int childType = child.getNodeType();
			
			//last child might be not of type 1 -it could be a text or comment
			//so need to check that if is not of type 1 then take the previousSibling
   			/*if(child.getNodeType() != 1){
   				Node pvSibling = child.getPreviousSibling();
   				System.out.println("Previous Sibling "+ pvSibling.getNodeName());
   				if(pvSibling.getNodeName().equalsIgnoreCase("img")){
   					lastIsImg = true; 	            		
   		    		System.out.println("LstIsIMG - " + lastIsImg);
   				}
   			}
			
   			else*/ if(child.getNodeName().equalsIgnoreCase("img")){
	    		//if the last child is an img and isTLC = false
	    		lastIsImg = true; 	            		
	    		//System.out.println("LstIsIMG - " + lastIsImg + " isTLC - " + isTLC);
	    	}
    			 
    			 
    			
    		}
       	
       	 
       }
	    
	  /*  if(lastIsImg==true && isTLC==false){
	 		  
			TLC ++;
		 	isTLC=true;
			((INodeEx) node).highlight();
			System.out.println("lastIsIMG TLC");
		 } */
		
		//this needs to be visited only once per node so we use a boolean flag 
		//findName which needs to be reset to true on the main method
	    if (findName == true){
	        if (children != null) {	            
	            Node childNode = children.item(0);
	           // System.out.println("Children Length = "+len);
	          
	            //need to check for Nodes with only one child per child node. OR zero node 
             	//If that is the case, then it is a TLC - have a boolean flag: singleChildren
	            //need to check that the node's name is not table or tbody
	            singleChildren(node,len);            
	           
	            for (int i = 0; i < len; i++){
	            	//need to check each child's display attribute
	             	//insert a flag - if there is at least one block level element child then flag as true
	            	
	            	childNode = children.item(i);
	            	NodeList childNodeList = childNode.getChildNodes();
	            	int length = childNodeList.getLength();
	            	singleChildren(node,len);
	            	
	             	if (childNode instanceof IElementEx){
	             		IStyle childStyle = ((IElementEx)childNode).getStyle();	
		             	String displayChild = (String) childStyle.get("display");
		             	if (displayChild.equalsIgnoreCase("block")|| display.equalsIgnoreCase("table"))
		             		blockChild = true;
	             	}      	
	             		               	
	         //    	System.out.println("blockChild - " + blockChild + ", node name - "+childNode.getNodeName());
	            }//end for-loop
	        }//end if not null children       	
	        
	     
	    	   if(display.equalsIgnoreCase("block") && blockChild == false){
		   			TLC ++;
		   			isTLC=true;
		   			//TLC - highlight
		   			//((INodeEx) node).highlight();
		   			TLC1 ++;
		//   			System.out.println("tlc1 - case1");
	   			}
	    	   
	    	   else if(singlesChildren == true && isTLC==false){
	    		   //if the last child is an image then it is a TLC
	    		   //but then might have multiple tlcs
	    		   //have to check that the img is the ONLY children and that the tag is a series of singles children
	    	//	   System.out.println("singleChildren - " + singlesChildren);
	    		   TLC ++;
	    		  isTLC=true;
	    		//TLC - highlight
	    		 //  ((INodeEx) node).highlight();
	    		  
	    		   //System.out.println("singleChildren TLC");
		   			
	    	   } 	   
	    	   
	    	   
	    	   
	    		   
	    	   findName = false;
       }
		
	   else if(nodeName.equalsIgnoreCase("div")){
	    	
			
			//System.out.println("Border - "+ borderWidth + ", Len - " + borderLen + ", isPx - "+ isPx + ", viible -" + visibleBorder);
			if (visibleBorder == true){
				
				TLC++;
				isTLC = true;
				//TLC - highlight
				//((INodeEx) node).highlight();
				
				//System.out.println("DIV TLC 3");
			}	
			
			/*else if(children.getLength() > 0)
			{
				//check the div element children if it is only one children of type=text
				//if(isTLC==false){
					System.out.println("First Child of a DIV: " + node.getFirstChild().getNodeName());
					
					if(children.getLength()==1 ){
						if(node.getLastChild().getNodeType()==3){
							TLC++;
							isTLC = true;
							((INodeEx) node).highlight();
							System.out.println("DIV TLC 4");
						}
						
					}
					
					else if(node.getFirstChild().getNodeName().equalsIgnoreCase("h1")
							|| node.getFirstChild().getNodeName().equalsIgnoreCase("h2")
							|| node.getFirstChild().getNodeName().equalsIgnoreCase("h3")){
						
						TLC++;
						isTLC = true;
						((INodeEx) node).highlight();
						System.out.println("DIV TLC 5 - heading");
						
					}
					
				//}
				
			}*/
			}
		//}				 
		
		//CASE2: If a block element has block-displayed children 
		//this case leads to a set of subcases (2a-2c) and it is recursive only for some cases (2b and 2c)
	 
		else if (display.equalsIgnoreCase("block") || display.equalsIgnoreCase("table") || display.startsWith("table")){
			//&& blockChild == true
			//System.out.println("node name - "+ node.getNodeName()+ ", Display - "+display + ", isTLC - "+ isTLC ); 
					//", headingTLC - "+ headingTLC);
			
			//CASE 2a: if the node is a heading then we count the number of headings
			//		within that node with the  level 1/2/3
		//	if(isTLC == false){	
				//need to put the case the the node is a DIV element with a visible border (>0px)
				if(nodeName.equalsIgnoreCase("div")){
									
					//System.out.println("Border - "+ borderWidth + ", Len - " + borderLen + ", isPx - "+ isPx+ "pixels -" + borderPx + " - " + px + visibleBorder);
					if (visibleBorder == true && isLayout==false){
						
						TLC++;
						isTLC = true;
						//TLC - highlight
						//((INodeEx) node).highlight();
						
						//System.out.println("DIV TLC");
					}				
				}
			
			/*	if(lastIsImg==true){
		 	 		  
		   			TLC ++;
		   		 	isTLC=true;
		   			((INodeEx) node).highlight();
		   			System.out.println("lastIsIMG TLC");
		   		 }*/
			
			else if(nodeName.equalsIgnoreCase("h1") || nodeName.equalsIgnoreCase("h2")){					
					//get all heading elements under that node
					//need to check which heading level are under the node. If h1 and h2 and h3 exist, then count h1 and h2 only.
					//if h3 exist only, then we count h3
					
					//NodeList headingChild = node.getChildNodes();
					//for(int k=0; k < headingChild.getLength(); k++){
					//	Node child = headingChild.item(k);
					//	String childName = child.getNodeName();
					//	if (childName.equalsIgnoreCase("h1")||childName.equalsIgnoreCase("h2")||childName.equalsIgnoreCase("h3")){
						//if (isTLC == false){
							TLC++;
							isTLC = true;
							headingTLC = true;
							//TLC - highlight
							//((INodeEx) node).highlight();
							
							//System.out.println("Heading12 TLC");
						//}
						//}
						
					//}//ends for k-loop
				}//ends if case 2 (if nodeName equals h1 or h2)
				
				else if(headingTLC == false && nodeName.equalsIgnoreCase("h3")){
					TLC++;
					isTLC = true;
					//headingTLC = true;
					//TLC - highlight
					//((INodeEx) node).highlight();
					
					//System.out.println("Heading3 TLC");
				}
				
				else if(headingTLC == false && nodeName.equalsIgnoreCase("h4")){
					TLC++;
					isTLC = true;
					//headingTLC=true;
					//TLC - highlight
					//((INodeEx) node).highlight();
					//System.out.println("Heading4 TLC");
				}
				
				
			//}//ends if tlc is false
	//	}
			
			
			//CASE 2c: If the node is a table and has visible border
			// if the table has a caption or a theading => then it would be a data table which we count as one TLC
			// if the table has only visible border for now we count it as a TLC	
			else if(nodeName.equalsIgnoreCase("table")||display.contains("table")){
				//(nodeName.equalsIgnoreCase("table"))
				//System.out.println("IN TABLE");
				//case A: check if the table has a head or caption 
				boolean dataTable = false;
				boolean blockChilNodes = false;
				NodeList tchildren = node.getChildNodes();
		           if (tchildren != null) {
		               int len = tchildren.getLength();
		               for (int i = 0; i < len; i++) {
		            	   //need to check if the table's children are thead or caption
		            	   String tchildName = tchildren.item(i).getNodeName();
		            	   if (tchildName.equalsIgnoreCase("thead") || tchildName.equalsIgnoreCase("caption")){
		            		   dataTable = true;
		            		   dataTables++;
		            	   }
		            	   //check if there are block level child nodes
		            	   if (tchildren.item(i) instanceof IElementEx){
			             		IStyle childStyle = ((IElementEx)tchildren.item(i)).getStyle();	
				             	String displayChild = (String) childStyle.get("display");
				             	if (displayChild.equalsIgnoreCase("block")|| display.equalsIgnoreCase("table"))
				             		blockChilNodes = true;
			             	}
		            	   
		            	
		               }//ends for-loop
		           	}
		           
		           if(isTLC == false && dataTable == true){
		        	   TLC++;
		        	   isTLC = true;
		        	 //TLC - highlight
		        	   //((INodeEx) node).highlight();
		        	   
		        	   //System.out.println("DATATable TLC");
		           }		
		           
		           else if(dataTable==false){
		        	 //count the tables used for layouts and are not already identified for TLC before
		        	 //call tableCellLayout
		        	   tableCellLayout(node);
		        	   //System.out.println("Layout Table - " + layoutTable + ", isLayout - "+ isLayout+ ", isTLC -" + isTLC);
		        	   if (isLayout == true){
		        		   //if it already was identified as TLC then need to check further layout spec about the table
		        		   //what though???
		        		   if (isTLC == false){
		        			   TLC++;
			        		   isTLC = true;
			        		 //TLC - highlight
							//	((INodeEx) node).highlight();
								//System.out.println("LayoutTable TLC");		        			   
		        		   }
		        		//   else if(blockChild ==true){
		        		//	   TLC++;
			        	//	   isTLC = true;
						//		((INodeEx) node).highlight();
						//		System.out.println("LayoutTable-BlockChild TLC");	
		        		//   }
		        			   
		        		  /* else{
		        			  // System.out.println("LayoutTable is already TLC");	
		        			   //need to check background difference
		        			   //tried this but does not work as it counts within tlc as well
		        			   backgroundCheck(node);
		        			   if(backgroundDif == true){
		        					TLC++;
		        					isTLC = true;
		        					((INodeEx) node).highlight();
		        					System.out.println("background tlc");
		        				}
		        			 
		        			   
		        			   
		        		   }*/
		        		   
		        	   }//ends if isLayout=true 
		        	   
		        	  else if (isLayout == false && blockChilNodes==true){
		        		   //count if there are block level child nodes
		        		    TLC++;
		        		    isTLC = true;
		        		  //TLC - highlight
							//((INodeEx) node).highlight();
							//System.out.println("blockChilNodes TLC");
		        		   
		        	   }
		        	   
		        	   //check if the div element has only one child 
						else if(nodeName.equalsIgnoreCase("div")){
							//&& node.getChildNodes().getLength()==1){
								TLC++;
								isTLC = true;
								//TLC - highlight
								//((INodeEx) node).highlight();
								//System.out.println("DIV TLC 2");
						} 
		        	   
		        	  
		           }//ends if dataTable=false	
		          
					
		           
			}//ends else-if table
				
		//	}//ends if isTLC=false
				
				

	
				

			
			
			
	}//ends else-if block
		
		
		
		
	       NodeList NodeChildren = node.getChildNodes();
           if (NodeChildren != null) {
               int len = NodeChildren.getLength();
               for (int i = 0; i < len; i++) {
            	   visibleBorder = false;
            	   countTLC(NodeChildren.item(i));
               }
           }    
		
	
			
			
	
	}//end if element node
		
}//end getBlockCount

public static void singleChildren(Node node, int length){
	
	int NodeLength = length;
	
    //need to check for Nodes with only one child per child node. OR zero node 
   	//If that is the case, then it is a TLC - have a boolean flag: singleChildren
      //need to check that the node's name is not table or tbody
	
	//need to check all the node/tree
      if(NodeLength == 1||NodeLength == 0){
    	//System.out.println("Children Length = "+ NodeLength + "Node name = " + node.getNodeName());
      	if(!node.getNodeName().equalsIgnoreCase("table") && !node.getNodeName().equalsIgnoreCase("tbody")){
      		
      		//if singlesChildren == true then need to check if the display is block if its zero 
          	if(NodeLength == 0 && display.equalsIgnoreCase("block"))
                	singlesChildren = true;
          	else if (NodeLength ==1)
          		singlesChildren = true;   
          	else if(NodeLength == 0 && node.getNodeName().equalsIgnoreCase("img"))
          		singlesChildren = true;
          	else
          		singlesChildren = false;
	              		
      	}
       }
}




public static void backgroundCheck(Node node){
	//this method determines a background color difference between a node and it's parent node
	//CASE 2b: If the node's background colour is different from the parents				
	backgroundDif = false;
	 boolean transparent = false;
	if(node instanceof IElementEx) {
		//get node's background colour
		IStyle style = ((IElementEx)node).getStyle();				
		backgroundColor = (String)style.get("backgroundColor");
		//get parent's background colour
		Node parentNode = node.getParentNode();
        IStyle style2 = ((IElementEx)parentNode).getStyle();	
        backgroundColorParent = (String)style2.get("backgroundColor");	
        if(backgroundColorParent.equalsIgnoreCase("transparent")){
        	Node grandParent = parentNode.getParentNode();
        	IStyle style3 = ((IElementEx)grandParent).getStyle();	
        	//grandParent's color could still be transparent
        	//need a for-loop to reach the non-transparent parent
            backgroundColorGrandParent = (String)style3.get("backgroundColor");	
            transparent = true;
        }
        //System.out.println(backgroundColor + "-"+ backgroundColorParent + " - grandparent: " + backgroundColorGrandParent);
	}
	
	if(transparent == false){
	}
	
		
	if(!backgroundColor.equalsIgnoreCase(backgroundColorParent) 
			&& !backgroundColor.equalsIgnoreCase("transparent") 
			&& !backgroundColorParent.equalsIgnoreCase("transparent")){
		
		backgroundDif = true;
	}
		
}//ends backgroundCheck


public static void tableCellLayout(Node node){
	//this method determines the number of columns and rows a node that identified as table has
	//returns true if the table is used for layout
	isLayout = false;
	int tableRows = 0;
    int tableCols = 0;
    //table-->tbody-->tr-->td
    //get the children nodes of the tbody
    Node tbody = node.getFirstChild();
    NodeList tbodyNodes = tbody.getChildNodes();
    //System.out.println(childNodes);
    for (int i = 0; i < tbodyNodes.getLength(); i++) {
    	Node trnode = tbodyNodes.item(i);
    	String name = trnode.getNodeName();
    	if(name.equalsIgnoreCase("tr")){
    		rows++;
    		tableRows++;
    	}//ends if	        			
     			
     	//get the children of TR to find TD count
 		NodeList trChildNodes = trnode.getChildNodes();
 		for (int j = 0; j < trChildNodes.getLength(); j++){
 			Node tdnode = trChildNodes.item(j);
        		String name2 = tdnode.getNodeName();
 			if(name2.equalsIgnoreCase("td")){
 				tableCols++;
 				columns++;
 			}
        			
     	}//ends j-for    
     		
     		//System.out.println("table rows - cols: " + tableRows + " - " + tableCols);
     }//ends i-for
     	
     	if (tableRows ==1 || tableCols == 1){     		
     		layoutTable++;
     		isLayout = true;
     }
     	else if(tableRows == tableCols) {
     		if(tableRows !=0 && tableCols !=0){
     		layoutTable++;
     		isLayout = true;
     		}
     }
     //	else
     //		tables++;
	
	//return isLayout;
	
}//ends tableCellLayout

}




	
	

