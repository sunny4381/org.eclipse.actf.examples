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

import org.eclipse.actf.model.dom.dombycom.IElementEx;
import org.eclipse.actf.model.dom.dombycom.IStyle;
import org.eclipse.actf.model.ui.IModelService;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserACTF;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserStyleInfo;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.eclipse.swt.graphics.Rectangle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Visualization Class:
 * 
 * Gives a heatmap of the page similar to an eye tracking heatmap. The eye
 * tracking heatmap uses colours to indicates the level of attention each area
 * receives based on the number of fixations that each area had. Fixations are
 * the number of times that participants looked at that area.
 * 
 * The visualization code will duplicate these results based on results derived
 * from eye tracking studies.
 * 
 * Red Areas of most attention: Images 2-3 First Items on Menus Yellow Green
 * Gray
 * 
 * 
 * The visualization code is based on the Complexity.java code. Since it is used
 * to identify the structural elements we will send information from there (:S)
 */
public class Visualization {

	private static Rectangle rectangle;

	public static void findElements() {
		IModelService modelService = ModelServiceUtils.getActiveModelService();
		if (modelService instanceof IWebBrowserACTF) {
			IWebBrowserACTF browser = (IWebBrowserACTF) modelService;
			IWebBrowserStyleInfo style = browser.getStyleInfo();
			// ModelServiceSizeInfo sizeInfo = style.getSizeInfo(true);

			// styleMap = style.getCurrentStyles();
			// for (String xpath : styleMap.keySet()) {
			// ICurrentStyles curStyle = styleMap.get(xpath);
			// rectangle = curStyle.getRectangle();
			// System.out.println("Rectangle - " + rectangle);
			// }
		}
		Document doc = modelService.getDocument();
		Document docLive = modelService.getLiveDocument();

		if (doc != null || docLive != null) {

			// get document element
			// identify tag name and node type

			Element docElement = doc.getDocumentElement();
			Element docLiveElement = docLive.getDocumentElement();
			// call methods that count all elements
			// and calculate final score for VCS and aesthetics
			identifyElements(docElement.getElementsByTagName("body").item(0));
		}

	}

	/*
	 * identifyElements: identifies what the node elements are
	 * (img/p/lists/menus etc)
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
			if (node instanceof IElementEx) {
				IStyle style = ((IElementEx) node).getStyle();
				rectangle = ((IElementEx) node).getLocation();
				System.out.println("Rectangle: " + rectangle);
			}

			if (nodeName.equalsIgnoreCase("a")) {

			}

			if (nodeName.equalsIgnoreCase("p")) {

			}

			if (nodeName.equalsIgnoreCase("img")) {
				// Get the location of the image
				System.out.println("Image Rectangle: " + rectangle);

			}

			if (nodeName.equalsIgnoreCase("form")) {

			}

			if (nodeName.equalsIgnoreCase("table")) {

			}

			if (nodeName.equalsIgnoreCase("ul")
					|| nodeName.equalsIgnoreCase("ol")) {

			}

			if (nodeName.equalsIgnoreCase("li")) {
				// Node parentNode = node.getParentNode();

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
	 * GetRedCoordinates: returns an array with the location of images and the
	 * 2/3 first items on menus or lists
	 */
	public static int[][] getRedCoordinates() {

		return null;

	}

}