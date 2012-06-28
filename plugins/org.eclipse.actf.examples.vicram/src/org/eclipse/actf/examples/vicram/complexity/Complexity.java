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
package org.eclipse.actf.examples.vicram.complexity;

import java.util.StringTokenizer;

import org.eclipse.actf.model.dom.dombycom.IElementEx;
import org.eclipse.actf.model.dom.dombycom.INodeEx;
import org.eclipse.actf.model.dom.dombycom.IStyle;
import org.eclipse.actf.model.ui.IModelService;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Complexity {

	public static int links, images, tables, paragraphs, forms, linkedWords,
			unlinkedWords, listWords, rowWords, colWords, rows, columns, lists,
			listItems, wordCount, blocks, TLC;
	public static String words;
	public static boolean insideLink = false;
	public static boolean insideList = false;
	public static boolean insideTableRow = false;
	public static boolean insideTableCol = false;
	public static String linkedString, unlinkedString;
	private static String backgroundColor;
	private static String display;
	private static String backgroundColorParent;
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
	//private static Map<String, ICurrentStyles> styleMap;
	private static boolean veryComplex;
	public static double VCS;

	/*
	 * 
	 * 1. Calculates the level of complexity and aesthetics of Web pages
	 * Equations: VisualComplexity = 1.743 + 0.097 (TLC) + 0.053 (Words) + 0.003
	 * (Images)
	 * 
	 * THE FINAL Visual Complexity Score is equal to: VisualComplexity/10. If
	 * the score is > 10 then VC =10, and denotes extreme complexity due to
	 * length of page, a lot of text, a large number of images etc.
	 * 
	 * For more details: ViCRAM Webpage: http://vicram.cs.manchester.ac.uk
	 */
	public static String calculate() {
		/*
		 * calculate method is called from PartControlSimpleVisualizer This
		 * method calls the appropriate methods that analyze the DOM structure
		 * of the page, counts the appropriate page elements and based on the
		 * complexity formula gives the score (VCS) by returning an appropriate
		 * string
		 */

		// initialize modelService and get the style information based on IE
		// Current Styles method
		IModelService modelService = ModelServiceUtils.getActiveModelService();
		// if (modelService instanceof IWebBrowserACTF) {
		// IWebBrowserACTF browser = (IWebBrowserACTF) modelService;
		// IWebBrowserStyleInfo style = browser.getStyleInfo();
		// // ModelServiceSizeInfo sizeInfo = style.getSizeInfo(true);
		// styleMap = style.getCurrentStyles();
		// }

		/*
		 * A. initialize Document based on DOM and LiveDOM in order to identify
		 * the number of blocks the page has (TLC) the liveDoc will be used.
		 * LiveDoc returns the style information as it is currently presented by
		 * the browser
		 * 
		 * B. if the documents are not empty then get respective elements, reset
		 * variables used as counters and help counters, initialize the body
		 * Node and recursively pass the docElement and docLiveElement to the
		 * countElements(node) and countTLC(node) respectively
		 */
		Document doc = modelService.getDocument();
		Document docLive = modelService.getLiveDocument();

		if (doc == null || docLive == null) {
			return "doc is null";
		} else {

			Element docElement = doc.getDocumentElement();
			Element docLiveElement = docLive.getDocumentElement();

			// reset variables to zero/false appropriately
			VCS = 0;
			links = 0;
			lists = 0;
			images = 0;
			wordCount = 0;
			blocks = 0;
			TLC = 0;
			tables = 0;
			layoutTable = 0;
			div = 0;
			dataTables = 0;
			isTLC = false;
			headingTLC = false;
			singlesChildren = false;
			lastIsImg = false;
			visibleBorder = false;
			isPx = false;
			// Elements Counter
			countElements(docElement.getElementsByTagName("body").item(0));
			// Block Counter
			Node node = docLiveElement.getElementsByTagName("body").item(0);
			NodeList NodeChildren = node.getChildNodes();
			if (NodeChildren != null) {
				int len = NodeChildren.getLength();
				for (int i = 0; i < len; i++) {
					findName = true;
					singlesChildren = false;
					isTLC = false;
					headingTLC = false;
					countTLC(NodeChildren.item(i));
				}
			}
			/*
			 * Visual Complexity Score calculation
			 */
			VCS = (1.743 + 0.097 * (TLC) + 0.053 * (wordCount) + 0.003 * (images)) / 10;
			if (VCS > 10) {
				veryComplex = true;
			} else
				veryComplex = false;

			String results = "";
			String resultsA = "";
			String resultsB = "";
			String resultsC = "";
			String resultsD = "";
			if (veryComplex == true) {
				VCS = 10.0;
				resultsA = "VCS = " + VCS + " **";
				resultsC = "NOTE: **(Two stars) after the VCS, signifies that the page just tested was ranked with a score bigger than 10 which is the maximum of our scale.";
			} else {
				resultsA = "VCS = " + VCS;
				resultsC = "";
			}
			resultsB = "The Visual Complexity Score (VCS) ranges from 0 to 10, with 0 being very visually simple and 10 very visually complex. ";
			resultsD = "The highlighted green boxes are the identified TLC, which is one of the main complexity factors. For more details please visit the ViCRAM Project Webpages at http://hcw.cs.manchester.ac.uk/research/vicram/";
			results = "======= Web Page Visual Complexity =======\n\n"
					+ resultsA + "\n\n" + resultsB + "\n\n" + resultsC + "\n"
					+ resultsD;
			return results + "\n\n";

		}
	}

	/*
	 * countElements is a recursive method that performs DOM analysis counts the
	 * page elements by recursively going through the node using DOM parser some
	 * counters are not used in the final equation but are used as part of the
	 * discussion in the report and overall structure of the page
	 */
	public static void countElements(Node node) {

		if (node == null)
			return;

		int type = node.getNodeType();

		if (type == Node.DOCUMENT_NODE) {
			countElements(((Document) node).getDocumentElement());
		}
		if (type == Node.ELEMENT_NODE) {
			// checks and counts the type of element
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
			if (nodeName.equalsIgnoreCase("table")) {
				tables++;
			}
			if (nodeName.equalsIgnoreCase("ul")
					|| nodeName.equalsIgnoreCase("ol")) {
				lists++;
				insideList = true;
			}
			if (nodeName.equalsIgnoreCase("li")) {
				listItems++;
			}

			// recurse through the node to find the rest of the counters
			NodeList children = node.getChildNodes();
			if (children != null) {
				int len = children.getLength();
				for (int i = 0; i < len; i++) {
					countElements(children.item(i));
				}
			}
		}// ends if (type == Node.ELEMENT_NODE)
			// Get the word count
		if (type == Node.TEXT_NODE) {
			String string = node.getNodeValue();
			words = " " + string;
			if (words == null)
				wordCount = 0;
			else {
				StringTokenizer total = new StringTokenizer(words,
						"'?!@#$&*/-,:.<>()~;=_|");
				while (total.hasMoreTokens() == true) {
					StringTokenizer token = new StringTokenizer(
							total.nextToken());
					wordCount += token.countTokens();
				}
			}
		}// ends if (type == Node.TEXT_NODE)
	}// ends countElements

	/*
	 * CountTLC(node) identifies the number of blocks the page is groupped into
	 * The method is based on the results from the evaluation described in the
	 * technical report ADD REPORT The block structure algorithm is based on a
	 * series of heuristic descibred individually below
	 * 
	 * boolean isTLC - to avoid TLC recognised within TLCs that are basically
	 * the same (i.e. tables within tables)
	 * 
	 * NOTE: The TLCs that the algorithm detects are highlighted on the page
	 * using the following code: //TLC - highlight ((INodeEx) node).highlight();
	 */

	public static int countTLC(Node node) {

		if (node == null)
			return 0;

		int type = node.getNodeType();
		if (type == Node.DOCUMENT_NODE) {
			countTLC(((Document) node).getDocumentElement());
		}

		if (type == Node.ELEMENT_NODE) {

			if (node instanceof IElementEx) {
				IStyle style = ((IElementEx) node).getStyle();
				display = (String) style.get("display");
				borderWidth = (String) style.get("borderWidth");

				/*
				 * STEP 1. <div> elements If the node is a <div> element & has a
				 * visible border => we flag that the node has a visibleBorder:
				 * 1. Get border attributes: borderWidth returns medium or Npx
				 * (N=number) need to check if the borderWidth is a number and
				 * is >0
				 * 
				 * 2. If border width contains a number of pixels as Npx, we use
				 * StringTokenizer to get the string that contains the string
				 * part with the px string in it some elements have different px
				 * for left/right etc (e.g - borderWidth = medium medium 5px)
				 */

				if (node.getNodeName().equalsIgnoreCase("div")) {
					borderLen = borderWidth.length();
					isPx = borderWidth.contains("px");
					int px = 0;
					if (isPx == true) {
						StringTokenizer borderToken = new StringTokenizer(
								borderWidth, " ");
						String borderPx = "";
						while (borderToken.hasMoreTokens() == true) {
							String token = borderToken.nextToken();
							if (token.contains("px") == true) {
								int tokenLength = token.length();
								borderPx = token.substring(0, tokenLength - 2);
							}
						}
						px = Integer.parseInt(borderPx);
						if (px > 0) {
							visibleBorder = true;
						} else
							visibleBorder = false;
					}
				}
			}// ends style info extraction
			if (display == null) {
				display = "";
			}

			/*
			 * STEP 2. Node is display=block && has no block children (this step
			 * is to flag elements such as standaline images) => lastIsImg flag
			 * 1. Get the NodeList of the current node and find the number of
			 * children that are type=1 ONLY
			 * 
			 * 2. If there is only one type 1 child, we check if it is an <img>
			 * and we flag as true
			 */

			NodeList children = node.getChildNodes();
			len = 0;
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				int childType = child.getNodeType();
				if (childType == 1)
					len++;
			}

			blockChild = false;
			lastIsImg = false;
			String nodeName = node.getNodeName();
			if (len == 1) {
				for (int i = 0; i < children.getLength(); i++) {
					Node child = children.item(i);
					if (child.getNodeName().equalsIgnoreCase("img")) {
						lastIsImg = true;
					}
				}
			}// ends if len==1

			/*
			 * STEP 3. blockChild: Determine if the node has only one child
			 * (singleChildren - method) and if it is displayed as block or
			 * table:
			 * 
			 * 1. Determine if it is a singleChildren (see respective method)
			 * 
			 * 2. Determine if it is a blockChild, that is displayed as a block
			 * or table no matter of the output of singleChildren
			 * 
			 * 3. If display=block && blockChild==false => TLC
			 * 
			 * 4. else If singleChild==true && isTLC==false => TLC
			 * 
			 * We need to follow these steps because if the last child is an
			 * image then it is a TLC BUT then might have multiple TLCs! So, we
			 * need to check that the img is the ONLY children and that the tag
			 * is a series of singles children
			 * 
			 * NOTE: this needs to be visited only once per node so we use a
			 * boolean flag findName which needs to be reset to true on the main
			 * method.
			 * 
			 * Also, isTLC is used to make sure that a node is only once
			 * identified as TLC and avoid duplicates
			 */

			if (findName == true) {
				if (children != null) {
					Node childNode = children.item(0);
					singleChildren(node, len);
					for (int i = 0; i < len; i++) {
						// need to check each child's display attribute and
						// whether is a singleChildren
						// insert a flag - if there is at least one block level
						// element child then flag as true
						// blockChild are blocks!
						childNode = children.item(i);
						//NodeList childNodeList = childNode.getChildNodes();
						//int length = childNodeList.getLength();
						singleChildren(node, len);
						if (childNode instanceof IElementEx) {
							IStyle childStyle = ((IElementEx) childNode)
									.getStyle();
							String displayChild = (String) childStyle
									.get("display");
							if (displayChild.equalsIgnoreCase("block")
									|| display.equalsIgnoreCase("table"))
								blockChild = true;
						}
					}// end for-loop
				}// end if not null children

				if (display.equalsIgnoreCase("block") && blockChild == false) {
					TLC++;
					isTLC = true;
					// TLC - highlight
					((INodeEx) node).highlight();
				}

				else if (singlesChildren == true && isTLC == false) {
					TLC++;
					isTLC = true;
					// TLC - highlight
					((INodeEx) node).highlight();
				}
				findName = false;
			}// ends if findName == true

			/*
			 * STEP 4. <div> element and a visible border => TLC We run this
			 * step here and not earlier to avoid duplicates. A <div> with
			 * visible border could contain an img as a singleChildren or is
			 * displayed as block element (see Step 3)
			 */
			else if (nodeName.equalsIgnoreCase("div")) {
				if (visibleBorder == true) {
					TLC++;
					isTLC = true;
					// TLC - highlight
					((INodeEx) node).highlight();
				}
			}// ends if <div> and visible border

			/*
			 * STEP 5. If a block displayed element has block-displayed children
			 * THis step leads to a set of substeps described where appropriate
			 * (5a-5c).
			 * 
			 * Step 5 is also recursive for some substeps (5c and 5c):
			 * 
			 * (i). Node is displayed as block/table or display starts with
			 * table
			 * 
			 * (ii). If the node is a <div> element, has visible border and is
			 * not used for Layout => TLC
			 * 
			 * (iii). else if the node is a heading <h1> or <h2> => TLC && flag
			 * that is identified as heading
			 * 
			 * (iv). else if <h3> && headingTLC==false => TLC
			 * 
			 * (v). else if <h4> && headintTLC==false => TLC
			 * 
			 * (vi). else if the node is a table and has visible border need to
			 * make sure if the table is used for data or layout if the table
			 * has a caption or a theading => then it would be a data table
			 * which we count as one TLC if the table has only visible border
			 * for now we count it as a TLC TLC++ if (one of those else if
			 * statements): a. dataTable==true && isTLC==false
			 * 
			 * b. dataTalbe==false && isLayout==true (table is used for layout
			 * see respective method)
			 * 
			 * c. isLayout == false && blockChilNodes==true
			 * 
			 * d. nodeName.equalsIgnoreCase("div")
			 */

			else if (display.equalsIgnoreCase("block")
					|| display.equalsIgnoreCase("table")
					|| display.startsWith("table")) {
				// step 5(ii)
				if (nodeName.equalsIgnoreCase("div")) {
					if (visibleBorder == true && isLayout == false) {
						TLC++;
						isTLC = true;
						// TLC - highlight
						((INodeEx) node).highlight();
					}
				}
				// step 5(iii) --flag that already identified TLC based on
				// headings
				else if (nodeName.equalsIgnoreCase("h1")
						|| nodeName.equalsIgnoreCase("h2")) {
					TLC++;
					isTLC = true;
					headingTLC = true;
					// TLC - highlight
					((INodeEx) node).highlight();
				}
				// step 5(iv)
				else if (headingTLC == false && nodeName.equalsIgnoreCase("h3")) {
					TLC++;
					isTLC = true;
					// headingTLC=true;
					// TLC - highlight
					((INodeEx) node).highlight();
				}
				// step 5(v)
				else if (headingTLC == false && nodeName.equalsIgnoreCase("h4")) {
					TLC++;
					isTLC = true;
					// headingTLC=true;
					// TLC - highlight
					((INodeEx) node).highlight();
				}

				// step 5(vi)
				else if (nodeName.equalsIgnoreCase("table")
						|| display.contains("table")) {
					boolean dataTable = false;
					boolean blockChilNodes = false;
					NodeList tchildren = node.getChildNodes();
					if (tchildren != null) {
						int len = tchildren.getLength();
						for (int i = 0; i < len; i++) {
							// need to check if the table's children are thead
							// or caption
							String tchildName = tchildren.item(i).getNodeName();
							if (tchildName.equalsIgnoreCase("thead")
									|| tchildName.equalsIgnoreCase("caption")) {
								dataTable = true;
								dataTables++;
							}
							// check if there are block level child nodes
							if (tchildren.item(i) instanceof IElementEx) {
								IStyle childStyle = ((IElementEx) tchildren
										.item(i)).getStyle();
								String displayChild = (String) childStyle
										.get("display");
								if (displayChild.equalsIgnoreCase("block")
										|| display.equalsIgnoreCase("table"))
									blockChilNodes = true;
							}
						}// ends for-loop
					}

					if (isTLC == false && dataTable == true) {
						TLC++;
						isTLC = true;
						// TLC - highlight
						((INodeEx) node).highlight();
					}

					else if (dataTable == false) {
						tableCellLayout(node);
						if (isLayout == true) {
							if (isTLC == false) {
								TLC++;
								isTLC = true;
								// TLC - highlight
								((INodeEx) node).highlight();
							}
						}// ends if isLayout=true

						else if (isLayout == false && blockChilNodes == true) {
							TLC++;
							isTLC = true;
							// TLC - highlight
							((INodeEx) node).highlight();
						} else if (nodeName.equalsIgnoreCase("div")) {
							TLC++;
							isTLC = true;
							// TLC - highlight
							((INodeEx) node).highlight();
						}
					}// ends if dataTable=false
				}// ends else-if table
			}// ends else-if block

			// Recurse through the rest of the childrenNodes
			NodeList NodeChildren = node.getChildNodes();
			if (NodeChildren != null) {
				int len = NodeChildren.getLength();
				for (int i = 0; i < len; i++) {
					visibleBorder = false;
					countTLC(NodeChildren.item(i));
				}
			}
		}// end if element node

		return TLC;
	}// end getBlockCount

	/*
	 * boolean singleChildren(node, length) This method is a help method for
	 * calculateTLC(). It checks for Nodes with only one child per child node OR
	 * zero nodes If that is the case, then we flag as singleChildren IFF the
	 * node's name is not table or tbody
	 */

	public static boolean singleChildren(Node node, int length) {
		int NodeLength = length;
		// need to check all the node/tree
		if (NodeLength == 1 || NodeLength == 0) {
			if (!node.getNodeName().equalsIgnoreCase("table")
					&& !node.getNodeName().equalsIgnoreCase("tbody")) {
				// if singlesChildren == true then need to check if the display
				// is block if its zero
				if (NodeLength == 0 && display.equalsIgnoreCase("block"))
					singlesChildren = true;
				else if (NodeLength == 1)
					singlesChildren = true;
				else if (NodeLength == 0
						&& node.getNodeName().equalsIgnoreCase("img"))
					singlesChildren = true;
				else
					singlesChildren = false;
			}
		}
		return singlesChildren;
	}

	/*
	 * backgroundCheck(node) This method is a help method for calculateTLC(). It
	 * determines a background color difference between a node and its parent
	 * node. THIS METHOD IS NOT USED
	 */

	public static void backgroundCheck(Node node) {
		backgroundDif = false;
		boolean transparent = false;
		if (node instanceof IElementEx) {
			// get node's background colour
			IStyle style = ((IElementEx) node).getStyle();
			backgroundColor = (String) style.get("backgroundColor");
			// get parent's background colour
			Node parentNode = node.getParentNode();
			IStyle style2 = ((IElementEx) parentNode).getStyle();
			backgroundColorParent = (String) style2.get("backgroundColor");
			if (backgroundColorParent.equalsIgnoreCase("transparent")) {
				Node grandParent = parentNode.getParentNode();
				IStyle style3 = ((IElementEx) grandParent).getStyle();
				// grandParent's color could still be transparent
				// need a for-loop to reach the non-transparent parent
				backgroundColorGrandParent = (String) style3
						.get("backgroundColor");
				transparent = true;
			}
		}
		if (transparent == false) {
		}

		if (!backgroundColor.equalsIgnoreCase(backgroundColorParent)
				&& !backgroundColor.equalsIgnoreCase("transparent")
				&& !backgroundColorParent.equalsIgnoreCase("transparent")) {
			backgroundDif = true;
		}

	}// ends backgroundCheck

	/*
	 * boolean tableCellLayout(node) This method is a help method for
	 * calculateTLC(). It determines the number of column and rows a node that
	 * identidied as table has and returns true if the table is used for layout
	 */

	public static boolean tableCellLayout(Node node) {
		isLayout = false;
		int tableRows = 0;
		int tableCols = 0;
		// table-->tbody-->tr-->td
		// get the children nodes of the tbody
		Node tbody = node.getFirstChild();
		if (tbody != null) {
			NodeList tbodyNodes = tbody.getChildNodes();
			// System.out.println(childNodes);
			for (int i = 0; i < tbodyNodes.getLength(); i++) {
				Node trnode = tbodyNodes.item(i);
				String name = trnode.getNodeName();
				if (name.equalsIgnoreCase("tr")) {
					rows++;
					tableRows++;
				}// ends if
					// get the children of TR to find TD count
				NodeList trChildNodes = trnode.getChildNodes();
				for (int j = 0; j < trChildNodes.getLength(); j++) {
					Node tdnode = trChildNodes.item(j);
					String name2 = tdnode.getNodeName();
					if (name2.equalsIgnoreCase("td")) {
						tableCols++;
						columns++;
					}
				}// ends j-for
			}// ends i-for
		}

		if (tableRows == 1 || tableCols == 1) {
			layoutTable++;
			isLayout = true;
		} else if (tableRows == tableCols) {
			if (tableRows != 0 && tableCols != 0) {
				layoutTable++;
				isLayout = true;
			}
		}
		return isLayout;
	}// ends tableCellLayout
}// ends class

