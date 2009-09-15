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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.eclipse.actf.model.dom.dombycom.IElementEx;
import org.eclipse.actf.model.dom.dombycom.INodeEx;
import org.eclipse.actf.model.dom.dombycom.IStyle;
import org.eclipse.actf.model.ui.IModelService;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.eclipse.swt.graphics.Rectangle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * Visualization Algorithm 
 * Divides the page in x*y grids and the complexity for each grid is calculated
 * Colours for each grid is assigned based on the complexity score of each grid and the VCS of the overall page
 * This class uses the Complexity.java methods while identifying each grid variables
 * 
 *  One could change the number of: rows, columns, and the colour array list defined in the class
 */
public class Visualization {

	private static int[][] newOverlayPixels;
	private static int webPageHeight, webPageWidth;
	private static IModelService modelService;
	private static int TLC, wordCount, images;
	private static Element docLiveElement;
	private static ArrayList[][] nodeGridArray;
	public static int gridSize = 200;
	public static int rows = 10;
	public static int columns = 10;
	private static GridVariables[][] gridVarArray;
	private static int gridColumn;
	private static int gridRow;
	private static boolean isTLC, visibleBorder;
	private static boolean findName;
	private static boolean singlesChildren;
	private static boolean headingTLC;
	private static boolean isLayout;
	private static Rectangle TLCRec;
	private static double[][] gridVCSarray;
	private static double gridVCS;
	private static double visVCS;
	private static String words;
	private static int gridTLC;
	private static boolean flag;
	private static GridVariables[][] calcVCS;
	private static int gridHeight;
	private static int gridWidth;
	public static String gridDescription;

	/*
	 * int[][] findElements() returns an int array with the new overlay pixels
	 * that the PartControlVisualizer uses to draw the overlay on top of the
	 * screenshot. 
	 * This method: 
	 * 1. Runs the Complexity.calculate() to get the overall VCS of the page 
	 * 
	 * 2. Resets variables used in the analysis 
	 * 
	 * 3. Determine the gridCellWidth and height in order to get coordinates of
	 * each grid based on the rows and columns count 
	 * 
	 * 4. Initialises Model and Document (based on LiveDom) and GridVariablesArray 
	 * 
	 * 5. Uses GridVariable.java to create Objects that stores the data for each grid
	 * based on the row and column location
	 */
	public static int[][] findElements(int[][] overlayPixels,
			Rectangle imageSize, boolean flag) {
		// will need some variables from the complexity algorithm so we run it first here
		Complexity.calculate();
		newOverlayPixels = overlayPixels;
		webPageHeight = imageSize.height;
		webPageWidth = imageSize.width;
		isTLC = false;
		headingTLC = false;
		singlesChildren = false;
		visibleBorder = false;
		gridTLC = 0;
		gridVCS = 0;
		visVCS = 0;
		gridWidth = webPageWidth / columns;
		gridHeight = webPageHeight / rows;
		modelService = ModelServiceUtils.getActiveModelService();
		Document docLive = modelService.getLiveDocument();
		if (docLive != null) {
			docLiveElement = docLive.getDocumentElement();
			gridVarArray = new GridVariables[rows][columns];
			gridVCSarray = new double[rows][columns];
			// and initialize arrays
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++) {
					gridVarArray[i][j] = new GridVariables(0, 0, 0);
				}
			}
			Node node = docLiveElement.getElementsByTagName("body").item(0);
			NodeList NodeChildren = node.getChildNodes();
			if (NodeChildren != null) {
				int len = NodeChildren.getLength();
				for (int i = 0; i < len; i++) {
					findTypeLoc(NodeChildren.item(i));
					findName = true;
					singlesChildren = false;
					isTLC = false;
					headingTLC = false;
					gridTLC = 0;
					findTLCLoc(NodeChildren.item(i));
				}
			}
		}
		String gridInfo = "";
		gridDescription = "----- Complexity Visualization View -----"
				+ "\nColour range: Red - Orange - Yellow - YellowGreen - Green - DarkGreen\n	"
				+ "\nThe more to the red colour, the more visually complex the grid is."
				+ " The colours  depend on the overall visual complexity of the page. "
				+ "The grids with the highest complexity score reflect the overall page's VCS; "
				+ "the more to the red the more visually complex the page is.\n"
				+ "\n ----- Grid Description: -----\n";
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				// sets the row and column in the gridVarArray
				gridVarArray[i][j].setRow(i);
				gridVarArray[i][j].setCol(j);
				gridInfo = "Grid (row-column): " + i + "-" + j + " | Images: "
						+ gridVarArray[i][j].images + " | TLC: "
						+ gridVarArray[i][j].TLC + " | Word Count: "
						+ gridVarArray[i][j].wordCount + " | VCS = "
						+ gridVarArray[i][j].getGridVCS();
				gridDescription += "\n" + gridInfo;
			}
		}
		determineColour();
		// create the grid lines
		drawGridLines();
		return newOverlayPixels;
	}

	/*
	 * findTypeLoc(node) - finds the type of the node and the location of the
	 * interested elements then it assigns the element by incrementing the
	 * appropriate counter in the appropriate grid
	 */
	public static void findTypeLoc(Node node) {
		if (node == null)
			return;
		int type = node.getNodeType();
		if (type == Node.DOCUMENT_NODE) {
			findTypeLoc(((Document) node).getDocumentElement());
		}
		if (type == Node.ELEMENT_NODE) {
			// checks and counts the type of element
			String nodeName = node.getNodeName();
			if (nodeName.equalsIgnoreCase("img")) {
				// find location of the image and assign to correct object and
				// array position (grid array)
				Rectangle imageRec = findGrid(node);
				if (imageRec != null) {
					if (imageRec.x >= 0
							&& imageRec.y >= 0
							&& (imageRec.x <= webPageWidth && imageRec.y <= webPageHeight)) {
						if ((imageRec.height > gridHeight || imageRec.width > gridWidth)) {
							// place and increament
							gridVarArray[gridRow][gridColumn].images++;
						}
					}
				}
			}// ends node img

			// recurse to find the rest of the counters
			NodeList children = node.getChildNodes();
			if (children != null) {
				int len = children.getLength();
				for (int i = 0; i < len; i++) {
					findTypeLoc(children.item(i));
				}
			}
		}// ends if (type == Node.ELEMENT_NODE)
		// Get the word count

		if (type == Node.TEXT_NODE) {
			// where the word count begins -need to get the number of rows and
			// columns that the text node spans for (see below)

			String strWord = node.getNodeValue();
			int words = 0;
			if (strWord != null) {
				StringTokenizer total = new StringTokenizer(strWord,
						"'?!@#$&*/-,:.<>()~;=_|");
				while (total.hasMoreTokens() == true) {
					StringTokenizer token = new StringTokenizer(total
							.nextToken());
					words += token.countTokens();
					if (words > 0) {
						Rectangle textNodeRec = findGrid(node);
						// System.out.println(textNodeRec);
						if (textNodeRec != null) {
							int spanCol = 0;
							int spanRow = 0;
							double wordsPerGrid = 0;
							boolean spanWords = false;
							// find the range of the words span: columns:
							// words/width of rectangle, rows: words/height of rectangle
							// need to check that the x and yth coordinate of the rectangle falls within the webpage height and width
							if (textNodeRec.x >= 0
									&& textNodeRec.y >= 0
									&& (textNodeRec.x <= webPageWidth && textNodeRec.y <= webPageHeight)) {
								if ((textNodeRec.height > gridHeight || textNodeRec.width > gridWidth)
										&& words > 1) {
									spanWords = true;
									spanCol = textNodeRec.width / gridWidth;
									spanRow = textNodeRec.height / gridHeight;
									int spanCol2 = spanCol;
									int spanRow2 = spanRow;
									if (spanCol == 0)
										spanCol2 = 1;
									if (spanRow == 0)
										spanRow2 = 1;
									if (words > 1)
										wordsPerGrid = ((double) words / ((double) spanCol2 * (double) spanRow2));
									else if (words > 0 && words <= 1)
										wordsPerGrid = words;
								}
								if (spanWords == true) {
									int maxCol = gridColumn + spanCol;
									int maxRow = gridRow + spanRow;	
									if (maxCol >= columns)
										maxCol = columns - 1;
									if (maxRow >= rows)
										maxRow = rows - 1;
									if (spanRow == 0) {
										for (int j = gridColumn; j <= maxCol; j++) {
											gridVarArray[gridRow][j].wordCount += wordsPerGrid;
										}
									}
									if (spanCol == 0) {
										for (int i = gridRow; i <= maxRow; i++) {
											gridVarArray[i][gridColumn].wordCount += wordsPerGrid;
										}
									} else if (spanCol > 0 && spanRow > 0) {
										for (int i = gridRow; i <= maxRow; i++) {
											for (int j = gridColumn; j <= maxCol; j++) {
												gridVarArray[i][j].wordCount += wordsPerGrid;
											}
										}
									}
								}// ends if span = true
								else {
									gridVarArray[gridRow][gridColumn].wordCount += words;
								}
							}
						}// end if rec not null
					}// ends if words>0
				}// ends while loop
			}
		}// ends if (type == Node.TEXT_NODE)
	}

	/*
	 * Rectangle findGrid(node) - returns the rectangle that the node's top left
	 * point fits within in this method is used to find the row and column of
	 * the grid
	 */
	public static Rectangle findGrid(Node node) {

		gridColumn = 0;
		gridRow = 0;
		Rectangle nodeRectangle = null;
		if (node instanceof IElementEx) {
			nodeRectangle = ((IElementEx) node).getLocation();
			// if width or height are zero = return null
			if (nodeRectangle.height == 0 || nodeRectangle.width == 0
					|| nodeRectangle.x < 0 || nodeRectangle.x > webPageWidth
					|| nodeRectangle.y < 0 || nodeRectangle.y > webPageHeight) {
				nodeRectangle = null;
			}

			// need to check the x and y point and respectively assign to the
			// appropriate grid's list

			else {
				if (nodeRectangle.x < 1 && nodeRectangle.x >= 0
						&& nodeRectangle.y > 0) {
					gridColumn = 0;
					gridRow = (nodeRectangle.y) / gridHeight;
				} else if (nodeRectangle.y < 1 && nodeRectangle.y >= 0
						&& nodeRectangle.x > 0) {
					gridRow = 0;
					gridColumn = (nodeRectangle.x) / gridWidth;
				} else {
					gridRow = (nodeRectangle.y) / gridHeight;
					gridColumn = (nodeRectangle.x) / gridWidth;
				}
			}
		}// ends if node an element
		else if (node instanceof INodeEx) {
			// for text nodes
			/*
			 * Should calculate the word count 2. Find the width of the node and
			 * calculate the span range 3. by calculating the wordCount/
			 */
			nodeRectangle = ((INodeEx) node).getLocation();
			if (nodeRectangle.height == 0 || nodeRectangle.width == 0
					|| nodeRectangle.x < 0 || nodeRectangle.x > webPageWidth
					|| nodeRectangle.y < 0 || nodeRectangle.y > webPageHeight) {
				nodeRectangle = null;
			}
			// need to check the x and y point and respectively assign to the
			// appropriate grid's list
			else {
				if (nodeRectangle.x < 1 && nodeRectangle.x >= 0
						&& nodeRectangle.y > 0) {
					gridColumn = 0;
					gridRow = (nodeRectangle.y) / gridHeight;
				} else if (nodeRectangle.y < 1 && nodeRectangle.y >= 0
						&& nodeRectangle.x > 0) {
					gridRow = 0;
					gridColumn = (nodeRectangle.x) / gridWidth;
				} else {
					gridRow = (nodeRectangle.y) / gridHeight;
					gridColumn = (nodeRectangle.x) / gridWidth;
				}
			}
		}// ends if node a TEXT_node
		// need to check that the assign gridRow and gridColumn does not exceed
		// the gridVarArray size
		if (gridRow >= rows)
			gridRow = rows - 1;
		if (gridColumn >= columns)
			gridColumn = columns - 1;
		return nodeRectangle;
	}

	/*
	 * findTLCLoc(node) This method mirrors the countTLC() method from
	 * Complexity.java. The addition is that we determine the grid location that
	 * the TLC belongs into FOR DESCRIPTION ON ALGORITHM SEE COMMENTS ON
	 * COMPLEXITY.JAVA' REPSECTIVE METHOD
	 */
	public static void findTLCLoc(Node node) {

		String display = null;
		String borderWidth = null;
		if (node == null)
			return;
		int type = node.getNodeType();
		if (type == Node.DOCUMENT_NODE) {
			findTLCLoc(((Document) node).getDocumentElement());
		}
		if (type == Node.ELEMENT_NODE) {
			if (node instanceof IElementEx) {
				IStyle style = ((IElementEx) node).getStyle();
				display = (String) style.get("display");
				borderWidth = (String) style.get("borderWidth");
				if (node.getNodeName().equalsIgnoreCase("div")) {
					int borderLen = borderWidth.length();
					boolean isPx = borderWidth.contains("px");
					int px = 0;
					if (isPx == true) {
						StringTokenizer borderToken = new StringTokenizer(
								borderWidth, " ");
						String pixels = "";
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
			}
			if (display == null) {
				display = "";
			}
			NodeList children = node.getChildNodes();
			int len = 0;
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				int childType = child.getNodeType();
				if (childType == 1)
					len++;
			}
			boolean blockChild = false;
			boolean lastIsImg = false;
			String nodeName = node.getNodeName();
			if (len == 1) {
				for (int i = 0; i < children.getLength(); i++) {
					Node child = children.item(i);
					if (child.getNodeName().equalsIgnoreCase("img")) {
						lastIsImg = true;
					}
				}
			}
			if (findName == true) {
				if (children != null) {
					Node childNode = children.item(0);
					singlesChildren = Complexity.singleChildren(node, len);
					for (int i = 0; i < len; i++) {
						childNode = children.item(i);
						NodeList childNodeList = childNode.getChildNodes();
						int length = childNodeList.getLength();
						singlesChildren = Complexity.singleChildren(node, len);
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
					TLCRec = findGrid(node);
					if (TLCRec != null) {
						gridVarArray[gridRow][gridColumn].TLC++;
					}
					isTLC = true;
				}
				else if (singlesChildren == true && isTLC == false) {
					TLCRec = findGrid(node);
					if (TLCRec != null) {
						gridVarArray[gridRow][gridColumn].TLC++;
					}
					isTLC = true;
				}
				findName = false;
			}
			else if (nodeName.equalsIgnoreCase("div")) {
				if (visibleBorder == true) {
					TLCRec = findGrid(node);
					if (TLCRec != null) {
						// place and increament
						gridVarArray[gridRow][gridColumn].TLC++;
					}
					isTLC = true;
				}
			}
			else if (display.equalsIgnoreCase("block")
					|| display.equalsIgnoreCase("table")
					|| display.startsWith("table")) {
				if (nodeName.equalsIgnoreCase("div")) {
					if (visibleBorder == true && isLayout == false) {
						TLCRec = findGrid(node);
						if (TLCRec != null) {
							// place and increment
							gridVarArray[gridRow][gridColumn].TLC++;
						}
						isTLC = true;
					}
				}
				else if (nodeName.equalsIgnoreCase("h1")
						|| nodeName.equalsIgnoreCase("h2")) {
					TLCRec = findGrid(node);
					if (TLCRec != null) {
						// place and increment
						gridVarArray[gridRow][gridColumn].TLC++;
					}
					isTLC = true;
					headingTLC = true;
				}// ends if case 2 (if nodeName equals h1 or h2)

				else if (headingTLC == false && nodeName.equalsIgnoreCase("h3")) {
					TLCRec = findGrid(node);
					if (TLCRec != null) {
						// place and increment
						gridVarArray[gridRow][gridColumn].TLC++;
					}
					isTLC = true;
				}
				else if (headingTLC == false && nodeName.equalsIgnoreCase("h4")) {
					TLCRec = findGrid(node);
					if (TLCRec != null) {
						// place and increment
						gridVarArray[gridRow][gridColumn].TLC++;
					}
					isTLC = true;
				}
				else if (nodeName.equalsIgnoreCase("table")
						|| display.contains("table")) {
					boolean dataTable = false;
					boolean blockChilNodes = false;
					NodeList tchildren = node.getChildNodes();
					if (tchildren != null) {
						len = tchildren.getLength();
						for (int i = 0; i < len; i++) {
							String tchildName = tchildren.item(i).getNodeName();
							if (tchildName.equalsIgnoreCase("thead")
									|| tchildName.equalsIgnoreCase("caption")) {
								dataTable = true;
							}
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
						TLCRec = findGrid(node);
						if (TLCRec != null) {
							// place and increament
							gridVarArray[gridRow][gridColumn].TLC++;
						}
						isTLC = true;
					}
					else if (dataTable == false) {
						isLayout = Complexity.tableCellLayout(node);
						if (isLayout == true) {
							if (isTLC == false) {
								TLCRec = findGrid(node);
								if (TLCRec != null) {
									// place and increament
									gridVarArray[gridRow][gridColumn].TLC++;
								}
								isTLC = true;
							}
						}// ends if isLayout=true
						else if (isLayout == false && blockChilNodes == true) {
							// count if there are block level child nodes
							TLCRec = findGrid(node);
							if (TLCRec != null) {
								// place and increament
								gridVarArray[gridRow][gridColumn].TLC++;
							}
							isTLC = true;
						} else if (nodeName.equalsIgnoreCase("div")) {
							TLCRec = findGrid(node);
							if (TLCRec != null) {
								// place and increament
								gridVarArray[gridRow][gridColumn].TLC++;
							}
							isTLC = true;
						}
					}// ends if dataTable=false
				}// ends else-if table
			}// ends else-if block
			NodeList NodeChildren = node.getChildNodes();
			if (NodeChildren != null) {
				len = NodeChildren.getLength();
				for (int i = 0; i < len; i++) {
					visibleBorder = false;
					findTLCLoc(NodeChildren.item(i));
				}
			}
		}// end if element node
	}// ends findTLCLoc

	/*
	 * determineColour this method identifies the colour that each grid will be
	 * assigned with
	 * 
	 * the colour depends on the ratio of gridVCS/complexity.VCS 
	 * 
	 * first we need to know the colour assigned overall based on the complexity.VCS:
	 *  0-3.5 Simple yellow 
	 *  
	 *  3.5 - 6.5 Medium green 
	 *  
	 *  6.5 - 10 Complex red 
	 *  
	 *  then sort the gridVCSarray based on ascending order. The 5 grids with the highest score
	 * are assigned with the page's complexity VCS colour then the rest based on
	 * the colour scale
	 */
	public static void determineColour() {
		// get complexity score of the whole page
		double compVCS = Complexity.VCS;
		String complexity = "";
		int startColour = 0xC0C0C0;		
		// determine page's complexity level array and starting colour
		// colours: http://www.webmonkey.com/reference/Color_Charts
		// colours array:red/orange/gold/yellow/yellowgreen/lime/green/darkgreen
		// //{0xFF0000, 0xFFA500,0xFFD700,0xFFFF00,0x9ACD32,0x00FF00,0x008000,
		// 0x006400};
		// darkgreen = the minScoreColour
		int[] colours = { 0x3D1AED, 0x4CB7FF, 0x00D4FF, 0x00ff77, 0x00C000 };
		int minScoreColour = 0x006400;// for scores <=0.1743
		int colourLen = colours.length;
		// if the length is even or odd
		boolean colourLenEven = false;
		if (colourLen % 2 == 0)
			colourLenEven = true;
		int pageColour = -1;
		// assign initial colour of page based on its overall VCS complexity
		if (compVCS < 3) {
			complexity = "simple";
			if (colourLenEven)
				pageColour = ((colourLen / 3) * 2) - 1;
			else
				pageColour = ((colourLen / 3) * 2);
			startColour = colours[pageColour];
		} else if (compVCS >= 3 && compVCS < 6) {
			complexity = "medium";
			if (colourLenEven)
				pageColour = (colourLen / 3) - 1;
			else
				pageColour = (colourLen / 3);
			startColour = colours[pageColour];
		} else if (compVCS >= 6) {
			complexity = "complex";
			pageColour = 0;// always the first index
			startColour = colours[pageColour];
		}

		double[][] sortVCSarray = new double[rows][columns];

		// Create a single array now that is the size of the doubel array
		GridVariables[] sortVCS = new GridVariables[rows * columns];
		// now populate by going through the double array and extracting the
		// Data
		// HERE we can find the number of grids that have the minimum value of
		// VCS (<=0.1743) which
		// will help later for the calculation of gridsPerColour. The dark green
		// is only assigned to those
		int counter = 0;
		int minScore = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				sortVCS[counter] = gridVarArray[i][j];
				counter++;
				if (gridVarArray[i][j].getGridVCS() <= 0.1743)
					minScore++;
			}
		}
		// sort the array - based on the sort method extended in
		// GridVariables.java
		Arrays.sort(sortVCS);
		/*
		 * The following code decides the color for each grid based on the colorIndex of the page and the number of grids the colorIndex is
		 * calculated based on the VCS score of the page and is the starting color of the page then the number of grids per color depends on the
		 * location of the colorIndex on the colourArray colors[]
		 * numGridsPerColor = (numGrids/(color.length - ColorIndex - 1))
		 */
		int numGrids = rows * columns;
		int coloursBasedScore = colourLen - pageColour;
		int gridsPerColour = (numGrids - minScore) / coloursBasedScore;
		int counter2 = 0;
		int counterMaxIndex = 0;// this can go up to 3 inclusive
		int gridColour = pageColour;
		// System.out.println(colourLen+"gridsPerColour= "+ gridsPerColour +
		// ", coloursBasedScore = "+ coloursBasedScore+ ", page colour="+
		// pageColour);
		for (int j = 0; j < sortVCS.length; j++) {
			if (sortVCS[j].getGridVCS() <= 0.1743) {
				// System.out.println("in <=0.1743");
				gridColour = minScoreColour;
				colour(sortVCS[j].row, sortVCS[j].column, minScoreColour);
			} else {
				// System.out.println("in else");
				if (counter2 == gridsPerColour) {
					counter2 = 0;
					gridColour++;
				}
				if (gridColour == colourLen) {
					gridColour = colourLen - 1;// the max colour
				}
				colour(sortVCS[j].row, sortVCS[j].column, colours[gridColour]);
			}
			counter2++;
		}
	}

	/*
	 * Colours the nodes based on the coordinates and colour
	 */
	public static void colour(int gRow, int gCol, int colour) {
		//
		// get the row and column of the grid
		// gridRow
		// colour = 0x00D4FF;
		int x1 = gCol * gridWidth;
		int y1 = gRow * gridHeight;
		int x2 = gridWidth + x1;
		int y2 = gridHeight + y1;
		// System.out.println("Coordinates: x1 - "+ x1+ ", y1 - "+y1+",x2 - " + x2+", y2 - "+y2);
		for (int b = y1; b < (y2); b++) {
			for (int a = x1; a < (x2); a++) {
				if (flag) {
					newOverlayPixels[a][b] = colour;
				} else {
					newOverlayPixels[b][a] = colour;
				}
			}
		}
	}
	/*
	 * drawGridLines() draws lines around the grids to easily differentiate
	 * between rectangles/grids
	 */
	public static void drawGridLines() {
		// horizontal lines
		int x1 = 0;
		int x2 = webPageWidth;
		int y1 = 0;
		int y2 = webPageHeight;
		for (int b = y1; b < rows; b++) {
			for (int a = x1; a < (x2); a++) {
				if (flag) {
					newOverlayPixels[a][b * gridHeight] = 0x000000;
				} else {
					newOverlayPixels[b * gridHeight][a] = 0x000000;
				}
			}
		}
		// vertical lines
		x2 = 0;
		// b-Y, a - X
		for (int b = y1; b < y2; b++) {
			for (int a = x1; a < columns; a++) {
				if (flag) {
					newOverlayPixels[a * gridWidth][b] = 0x000000;
				} else {
					newOverlayPixels[b][a * gridWidth] = 0x000000;
				}
			}
		}
	}
}
