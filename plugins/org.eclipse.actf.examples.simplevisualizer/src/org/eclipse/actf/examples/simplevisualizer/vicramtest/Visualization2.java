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
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import org.eclipse.actf.model.dom.dombycom.IElementEx;
import org.eclipse.actf.model.dom.dombycom.INodeEx;
import org.eclipse.actf.model.ui.IModelService;
import org.eclipse.actf.model.ui.editor.browser.ICurrentStyles;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.eclipse.swt.graphics.Rectangle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * Visualization Class:
 * Gives a heatmap of the page similar to an eye tracking heatmap. The eye tracking heatmap uses colours to indicates the level
 * of attention each area receives based on the number of fixations that each area had. Fixations are the number of times that participants looked 
 * at that area.
 * The visualization code will duplicate these results based on results derived from eye tracking studies. 
 * Red Areas of most attention: Images 
 * 								2-3 First Items on Menus
 * Yellow
 * Green
 * Gray
 * 
 * 
 * The visualization code is based on the Complexity.java code. Since it is used to identify the structural elements we will send information from there (:S)
 */

public class Visualization2 {

	private static Rectangle rectangle;
	private static IModelService modelService;
	private static Map<String, ICurrentStyles> styleMap;
	private static int[][] newOverlayPixels;
	private static boolean flag;
	private static int webPageHeight;
	private static int webPageWidth;
	private static ArrayList<Rectangle> listItems, images, redCoordList,
			yellowCoordList, greenCoordList, redCoordImg, yellowCoordImg,
			greenCoordImg;

	public static int[][] findElements(int[][] overlayPixels,
			Rectangle imageSize, boolean flag) {

		newOverlayPixels = overlayPixels;
		webPageHeight = imageSize.height;
		webPageWidth = imageSize.width;
		System.out.println("Image Size: Height " + webPageHeight + "width "
				+ webPageWidth);
		modelService = ModelServiceUtils.getActiveModelService();
		// listItems = new Rectangle[];

		Document doc = modelService.getDocument();
		Document docLive = modelService.getLiveDocument();

		listItems = new ArrayList<Rectangle>();
		images = new ArrayList<Rectangle>();
		redCoordList = new ArrayList<Rectangle>();
		yellowCoordList = new ArrayList<Rectangle>();
		greenCoordList = new ArrayList<Rectangle>();
		redCoordImg = new ArrayList<Rectangle>();
		yellowCoordImg = new ArrayList<Rectangle>();
		greenCoordImg = new ArrayList<Rectangle>();

		if (doc != null || docLive != null) {

			// get document element
			// identify tag name and node type

			// Element docElement = doc.getDocumentElement();
			Element docLiveElement = docLive.getDocumentElement();
			// call methods that identify the elements that will be highlighted
			// for attention
			// and calculate final score for VCS and aesthetics
			Node node = docLiveElement.getElementsByTagName("body").item(0);
			identifyElements(node);
			determineColour();
			// identifyElements(docElement.getElementsByTagName("body").item(0));
		}

		return newOverlayPixels;

	}

	/*
	 * identifyElements: identifies what the node elements are
	 * (img/p/lists/menus etc) finds the coordinates of the interested elements
	 * and assignes them in the appropriate coordinate Array (or Object)
	 */
	public static void identifyElements(Node node) {
		if (node == null)
			return;

		int type = node.getNodeType();
		// System.out.print("Type: " +type + "Name: " + node.getNodeName());
		if (type == Node.DOCUMENT_NODE) {
			identifyElements(((Document) node).getDocumentElement());
		}
		if (type == Node.ELEMENT_NODE) {
			// checks and counts the type of element
			String nodeName = node.getNodeName();
			System.out.println("NodeName: " + nodeName);

			if (nodeName.equalsIgnoreCase("a")) {

			}

			if (nodeName.equalsIgnoreCase("p")) {

			}

			if (nodeName.equalsIgnoreCase("img")) {
				// Get the location of the image

				if (node instanceof IElementEx) {
					// IStyle style = ((IElementEx)node).getStyle();
					// TO DO: FIND SIZE OF IMAGE - THE BIGGEST IMAGE GETS THE
					// RED COLOR
					// THE LOCATION OF THE IMAGE WITH RESPECT TO THE SIZE OF THE
					// PAGE MATTERS
					/*
					 * Put the image found in an array and sort it based on its
					 * width and height size. The image with the largest size
					 * and if it is located within the top quearter of the page
					 * is assigned the red color.
					 */
					rectangle = ((IElementEx) node).getLocation();
					System.out.println("Rectangle: " + rectangle);
					// highlight on the left for testing purposes
					// ((INodeEx) node).highlight();
					// colour if the (x2,y2) point of the node is in the top
					// half of the page
					// check for first quarter since colourNode does not work
					// for points on the very right hand side of the page
					// int pointX2 = rectangle.width + rectangle.x;
					// int pointY2 = rectangle.height + rectangle.y;
					if (rectangle.y < (webPageHeight / 2)) { // && pointX2 <
																// (webPageWidth/2)){
						// if(rectangle.height > 90 && rectangle.width >90){
						if (rectangle.height > 5 && rectangle.width > 5) {
							images.add(rectangle);
							// colourNodes(rectangle,0x3D1AED);
							((INodeEx) node).highlight();
						}

					}

				}

				// System.out.println("Image Rectangle: "+ rectangle);

			}

			if (nodeName.equalsIgnoreCase("form")) {

			}

			if (nodeName.equalsIgnoreCase("table")) {

			}

			if (nodeName.equalsIgnoreCase("ul")
					|| nodeName.equalsIgnoreCase("ol")) {

				if (node instanceof IElementEx) {
					// IStyle style = ((IElementEx)node).getStyle();
					/*
					 * THE LOCATION OF THE list WITH RESPECT TO THE SIZE OF THE
					 * PAGE MATTERS -- Y1 point needs to be in the top half of
					 * the page -- need to find location of the first 3 items of
					 * the menu which will be coloured
					 */
					// location of the list
					rectangle = ((IElementEx) node).getLocation();
					//					       
					/*
					 * If listItems <=3, then color the whole list node else
					 * color the first three list items (find location of the
					 * first three <li> nodes) OR send to colour the first three
					 * <li> elements
					 * 
					 * CASE: Multiple lists <ol>/<ul> under the same column
					 * Solution attempt: to put all list items in an array. Then
					 * colour the 3 first items with the same x1 (and/or x2)
					 * point see following method
					 */
					if (rectangle.y < (webPageHeight / 2)) {

						// create array that will put the list items with the
						// same X1 point

						// recurse to find the number of items of the list
						NodeList listChildren = node.getChildNodes();
						int nodelistItems = 0;
						if (listChildren != null) {
							int len = listChildren.getLength();
							for (int i = 0; i < len; i++) {
								// System.out.println("List Rectangle: "+
								// rectangle + " List Items: "+ nodelistItems);
								if (listChildren.item(i).getNodeName()
										.equalsIgnoreCase("li")) {
									nodelistItems++;
									rectangle = ((IElementEx) listChildren
											.item(i)).getLocation();
									int listItemsLen = listItems.size();
									System.out.println("length" + listItemsLen);
									listItems.add(rectangle);

								}
							}

						}
						// test
						for (int k = 0; k < listItems.size(); k++)
							System.out.println("list item " + k + "- "
									+ listItems.get(k));

					}

				}

			}

			// recurse to find the rest of the counters
			NodeList children = node.getChildNodes();
			if (children != null) {
				int len = children.getLength();
				for (int i = 0; i < len; i++) {
					identifyElements(children.item(i));
				}
			}
		}// ends if (type == Node.ELEMENT_NODE)

		if (type == Node.TEXT_NODE) {
			// where the word count begins

			// String string = node.getNodeValue();
			// words = " "+ string;
			// if (words == null)
			// wordCount = 0;
			// else
			// {
			// StringTokenizer total = new StringTokenizer(words,
			// "'?!@#$&*/-,:.<>()~;=_");
			// int count2 = 0;
			// while (total.hasMoreTokens() == true)
			// {
			// StringTokenizer token = new StringTokenizer(total.nextToken());
			// wordCount += token.countTokens();
			// }
			// }

		}// ends if (type == Node.TEXT_NODE)
	}

	/*
	 * DetermineColour This method works on the arrays created in
	 * identifyElements and determines the colour for each of the elements This
	 * method is called from the main method since need to finish the whole
	 * page's element identification before colouring For example: listItems[] -
	 * need to call the first 3 items with the same X point to red colour, then
	 * 1 with yellow and 1 with green
	 */
	public static void determineColour() {
		/*
		 * do for images what did for lists (put them in a list and red the two
		 * with the largest area )
		 */
		if (images.size() > 0) {
			ArrayList<Rectangle> imagesByArea = images;
			Collections.sort(imagesByArea, new Comparator<Rectangle>() {

				public int compare(Rectangle o1, Rectangle o2) {
					if ((o1.width * o1.height) > (o2.width * o2.height)) {
						return -1;
					} else {
						return 1;
					}
				}
			});
			// display elements of ArrayList
			System.out
					.println("ArrayList elements after sorting in descending order of Area : ");
			for (int i = 0; i < imagesByArea.size(); i++)
				System.out.println(imagesByArea.get(i));
			/*
			 * Need to call the first 3 items to red colour, then 4 with yellow
			 * and 1 with green
			 */
			int counter = 0;
			for (int i = 0; i < imagesByArea.size(); i++) {
				if (counter <= 3) {
					redCoordList.add(imagesByArea.get(i));
				} else if (counter > 3 && counter <= 6) {
					yellowCoordList.add(imagesByArea.get(i));
				} else if (counter == 7) {
					greenCoordList.add(imagesByArea.get(i));
				}

				counter++;
			}

		}

		// color Red = 0x3D1AED, the 3 first list items with the same x1

		if (listItems.size() > 0) {

			/*
			 * To sort an ArrayList object, use Collection.sort and implement
			 * Comparator method.
			 */
			// arrayList by the X coordinate
			ArrayList<Rectangle> listItemsByX = listItems;
			Collections.sort(listItemsByX, new Comparator<Rectangle>() {

				public int compare(Rectangle o1, Rectangle o2) {
					if (o1.x <= o2.x) {
						return -1;
					} else {
						return 1;
					}
				}
			});
			/*
			 * TESTING ARRAY Sort
			 */
			// display elements of ArrayList
			System.out
					.println("ArrayList elements after sorting in ascending order of Point X : ");
			for (int i = 0; i < listItemsByX.size(); i++)
				System.out.println(listItemsByX.get(i));

			/*
			 * Need to call the first 3 items with the same X point to red
			 * colour, then 1 with yellow and 1 with green
			 */
			int counterX = 0;
			int pointX = listItemsByX.get(0).x;
			System.out.println("Point X: " + pointX);
			// redColour
			redCoordList.add(listItemsByX.get(0));
			for (int i = 1; i < listItemsByX.size(); i++) {
				if (pointX == listItemsByX.get(i).x && counterX < 3) {
					redCoordList.add(listItemsByX.get(i));
				} else if (pointX == listItemsByX.get(i).x
						&& (counterX == 3 || counterX == 4)) {
					yellowCoordList.add(listItemsByX.get(i));
				} else if (pointX == listItemsByX.get(i).x && counterX == 5) {
					greenCoordList.add(listItemsByX.get(i));
				} else if (pointX != listItemsByX.get(i).x) {
					counterX = 0;
					pointX = listItemsByX.get(i).x;
				}
				counterX++;
			}
			// do the same for horizontal lists
			// arrayList by the Y coordinate - for horizontal menu
			ArrayList<Rectangle> listItemsByY = listItems;
			Collections.sort(listItemsByY, new Comparator<Rectangle>() {

				public int compare(Rectangle o1, Rectangle o2) {
					if (o1.y <= o2.y) {
						return -1;
					} else {
						return 1;
					}
				}
			});
			int counterY = 0;
			int pointY = listItemsByY.get(0).y;
			System.out.println("Point Y: " + pointY);
			// redColour
			redCoordList.add(listItemsByY.get(0));
			for (int i = 1; i < listItemsByY.size(); i++) {
				if (pointY == listItemsByY.get(i).y && counterY < 3) {
					redCoordList.add(listItemsByY.get(i));
				} else if (pointY == listItemsByY.get(i).y
						&& (counterY == 3 || counterY == 4)) {
					yellowCoordList.add(listItemsByY.get(i));
				} else if (pointY == listItemsByY.get(i).y && counterY == 5) {
					greenCoordList.add(listItemsByY.get(i));
				} else if (pointY != listItemsByY.get(i).y) {
					counterY = 0;
					pointY = listItemsByY.get(i).y;
				}
				counterY++;
			}

			/*
			 * TESTING ARRAY Sort
			 */
			// System.out.println("ArrayList elements after sorting in ascending order of Point Y : ");
			// for(int i=0; i<listItemsByY.size(); i++)
			// System.out.println(listItemsByY.get(i));
			// End testing

			// send to colour
			for (int i = 0; i < redCoordList.size(); i++)
				colourNodes(redCoordList.get(i), 0x3D1AED);
			for (int i = 0; i < yellowCoordList.size(); i++)
				colourNodes(yellowCoordList.get(i), 0x00D4FF);
			for (int i = 0; i < greenCoordList.size(); i++)
				colourNodes(greenCoordList.get(i), 0x008000);

		}
	}

	/*
	 * Colours the nodes based on the coordinates and colour
	 */
	public static void colourNodes(Rectangle coordinates, int colour) {
		// colour = 0x00D4FF;
		int x1 = coordinates.x;
		int y1 = coordinates.y;
		int x2 = (coordinates.width) + x1;
		int y2 = (coordinates.height) + y1;
		System.out.println("Coordinates: x1 - " + x1 + ", y1 - " + y1
				+ ",x2 - " + x2 + ", y2 - " + y2);

		// colour test
		/*
		 * for (int b = 40; b < 400; b++) { for (int a = 50; a < 600; a++) { if
		 * (flag) { newOverlayPixels[b][a] = colour; } else {
		 * newOverlayPixels[a][b] = colour; } } }
		 */

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

}
