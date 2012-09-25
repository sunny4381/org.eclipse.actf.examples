/*******************************************************************************
 * Copyright (c) 2012 Middle East Technical University Northern Cyprus Campus and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Elgin Akpinar (METU) - initial API and implementation
 *    Sukru Eraslan (METU NCC) - Eye Tracking Data Handling Implementation
 *******************************************************************************/

package org.eclipse.actf.examples.emine.vips;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.actf.examples.emine.vips.types.VipsLineBreak;
import org.eclipse.actf.examples.emine.vips.types.VipsNode;
import org.eclipse.actf.examples.emine.vips.types.VipsNodeTypes;
import org.eclipse.actf.model.dom.dombycom.IElementEx;
import org.eclipse.actf.model.dom.dombycom.IStyle;
import org.eclipse.actf.model.ui.editor.browser.ICurrentStyles;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DomStructureConstruction {
	private static final Logger logger = Logger.getLogger(Segmentation.class.getName());
	private VipsNode root;
	private Map<String, ICurrentStyles> styleMap;
	public static int windowSizeX, windowSizeY;
	private Map<String, VipsNode> nodePool;
	public static String htmlPath;
	
	public DomStructureConstruction(){
		logger.setLevel(Level.SEVERE);
		nodePool = new HashMap<String, VipsNode>();
	}
	
	/***************** Getters and Setters ******************/
	public VipsNode getRoot() {
		return root;
	}

	public void setRoot() {
		root = nodePool.get(htmlPath+"/BODY");
		root.setRootNode();
		root.setPath(htmlPath+"/BODY");
	}
	
	public Map<String, ICurrentStyles> getStyleMap() {
		return styleMap;
	}

	public void setStyleMap(Map<String, ICurrentStyles> styleMap) {
		this.styleMap = styleMap;
	}

	public Map<String, VipsNode> getNodePool() {
		return nodePool;
	}
	
	public static String getHtmlPath() {
		return htmlPath;
	}

	public void setHtmlPath() {
		htmlPath = "/HTML";
		if(!styleMap.containsKey(htmlPath)){
			for(int i = 2; !styleMap.containsKey(htmlPath);i++){
				htmlPath = "/HTML[" + i + "]";
			}
		}
	}
	/***************** End of Getters and Setters ******************/

	public void traverse(Node node, String path, int index) {
		if (node.getNodeName().equals("#COMMENT") || !isValidNode(node.getNodeName())) {
			return;
		}
		
		boolean isTextNode = false;
		String parent = path;
		VipsNode parentNode = nodePool.get(parent);
		String nodeName = node.getNodeName().toUpperCase(Locale.ENGLISH);
		
		VipsNode nodeElement = null;
		if(nodeName.matches("IMG")){
			/* Node is an image node */
			nodeElement = new VipsNode();
			nodeElement.setImage(true);
			parentNode.setContainsImage(true);
		} else if(VipsNodeTypes.LINEBREAK_TERMINAL_NODES.contains(nodeName)){
			/* Node is a line break terminal node */
			nodeElement = new VipsNode();
			nodeElement.setLineBreakObjet(true);
			parentNode.setContainsLineBreakObject(true);
		} else if(nodeName.matches("HR|BR")){
			/* Node is a separator */
			VipsLineBreak lineBreak = new VipsLineBreak(nodeName, parentNode.getChildren().size());
			parentNode.addLineBreak(lineBreak);
			return;
		} else if(nodeName.matches("#TEXT")){
			/* Node is a textual node */
			String content = node.getNodeValue().trim().toUpperCase(Locale.ENGLISH);
			/* If the content is null, it is omitted. */
			if(content.equals(""))
				return;
			
			/* Text nodes are evaluated in a more reduced way so that we need to mark the node as text node */
			isTextNode = true;
			nodeElement = new VipsNode();
			if(node.getParentNode().getChildNodes().getLength() == 1 || node.getNodeValue().trim().isEmpty()){
				nodeElement.setTextNode(true);
			}
		} else if (!nodeName.matches("#TEXT|LI|IMG") && node.getChildNodes().getLength() == 0) {
			/* If non-terminal node has no child, it is omitted. */
			return;
		} else {
			nodeElement = new VipsNode();
		}
		
		/* Path denotes the hierarchical place in DOM structure */
		if(parent.equals(""))
			path = htmlPath;
		else
			path = getPath(path, nodeName, index);
		
		ICurrentStyles nodeStyle = null;
		if (styleMap.containsKey(path)) {
			nodeStyle = styleMap.get(path);
		}
		
		/* If the node does not appear in the page visually, it is omitted. */
		if (!isDisplayable(nodeStyle)) {
			return;
		}		

		/* We do not need to get styles for text nodes, since text nodes form a block in normal way
		 * no matter what CSS they have. */
		if(!isTextNode){
			/*Background setting*/
			if(nodeStyle != null && nodeStyle.getBackgroundColor() != null)
				nodeElement.setBackground(nodeStyle.getBackgroundColor());
			
			try {
				/* Get CSS properties */
				IElementEx e = (IElementEx) node;
				IStyle style = e.getStyle();
				style.get("");
				nodeElement.setFontColor(style.get("color").toString());
				nodeElement.setFontWeight(style.get("fontWeight").toString());
				nodeElement.setFontSizeStr(style.get("fontSize").toString());
				nodeElement.setFloatStr(style.get("styleFloat").toString());
				nodeElement.setMarginLeft(style.get("marginLeft").toString());
				nodeElement.setMarginRight(style.get("marginRight").toString());
				nodeElement.setMarginTop(style.get("marginTop").toString());
				nodeElement.setMarginBottom(style.get("marginBottom").toString());
				nodeElement.setPosition(style.get("position").toString());
				nodeElement.setE(e);
			} catch (java.lang.ClassCastException e) {
				logger.severe(e.getMessage());
			}
		}

		if(!isTextNode){
			nodeElement.setFontSize(getFontSize(node));
		}
		
		nodeElement.setIndex((short) index);
		nodeElement.setPath(path);
		nodeElement.setTag(node.getNodeName());
		nodeElement.setParent(parentNode);
		nodeElement.setStyle(nodeStyle);
		
		if (nodePool.get(parent) != null){
			nodePool.get(parent).addChild(nodeElement);
		}
		nodePool.put(path, nodeElement);

		if (node.hasChildNodes()) {
			NodeList nodeList = node.getChildNodes();
			HashMap<String, Integer> childrenTags = new HashMap<String, Integer>();
			for (int i = 0; i < nodeList.getLength(); i++) {
				String childNodeName = nodeList.item(i).getNodeName().toUpperCase();
				if (!childrenTags.containsKey(childNodeName)) {
					childrenTags.put(childNodeName, 1);
				} else {
					childrenTags.put(childNodeName, childrenTags.get(childNodeName) + 1);
				}

				traverse(nodeList.item(i), path, childrenTags.get(childNodeName));
			}
			childrenTags = null;
			nodeList = null;
		}
	}
	
	public double getFontSize(Node node) {
		String size = getFontSizeStr(node);
		if (size.equals("xx-small")) {
			return 6.75; //9px
		} else if (size.equals("x-small")) {
			return 7.5; //10px
		} else if (size.equals("small")) {
			return 9.75; //13px
		} else if (size.equals("medium")) {
			return 12; //16px
		} else if (size.equals("large")) {
			return 13.5; //18px
		} else if (size.equals("x-large")) {
			return 18; //24px
		} else if (size.equals("xx-large")) {
			return 24; //32px
		} else if (size.equals("smaller")) {
			return 83.4 * getFontSize(node.getParentNode()) / 100;
		} else if (size.equals("larger")) {
			return 120 * getFontSize(node.getParentNode()) / 100;
		} else {
			try {
				if(size.contains("%")){
					double parent = getFontSize(node.getParentNode());
					String numericPart = size.replace("%", "").trim();
					double percentage = Double.parseDouble(numericPart);
					return parent*percentage/100;
				} else if(size.contains("pt")){
					String numericPart = size.substring(0, size.length() - 2).trim();
					double fontSize = Double.parseDouble(numericPart);
					return fontSize;
				} else if(size.contains("px")){
					String numericPart = size.substring(0, size.length() - 2).trim();
					double fontSize = Double.parseDouble(numericPart);
					return (fontSize/3.0)*4;
				} else if(size.contains("em")){
					String numericPart = size.substring(0, size.length() - 2).trim();
					double fontSize = Double.parseDouble(numericPart);
					return 12*fontSize;
				} else {
					double fontSize = Double.parseDouble(size);
					return fontSize;
				}
			} catch (NullPointerException e) {
				logger.severe(e.getMessage());
				return 12;
			}
		}
	}
	
	public String getFontSizeStr(Node node){
		try{
			if(node.getClass().getName().equals("org.eclipse.actf.model.dom.dombycom.impl.html.HTMLElementImpl")){
				IElementEx e = (IElementEx) node;
				IStyle style = e.getStyle();
				String size = style.get("fontSize").toString();
				return size;
			} else {
				return "12";
			}				
		} catch (java.lang.ClassCastException e){
			logger.severe(e.getMessage());
			return "12";
		}
	}
	
	public String getPath(String path, String nodeName, int index){
		if (index == 1)
			return path + "/" + nodeName;
		else
			return path + "/" + nodeName + "[" + index + "]";
	}
	
	public boolean isDisplayable(ICurrentStyles nodeStyle){
		if (nodeStyle != null){
			if(nodeStyle.getRectangle() != null && (/*nodeStyle.getRectangle().height == 0 ||*/ nodeStyle.getRectangle().width == 0)) {
				return false;
			} else if (nodeStyle.getDisplay() != null && nodeStyle.getDisplay().toLowerCase().equals("none")) {
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	public static boolean isValidNode(String nodeName) {
		if (VipsNodeTypes.INVALID_NODES.contains(nodeName.toUpperCase()))
			return false;
		else
			return true;
	}

	public static boolean isTextNode(Node node) {
		if (node.getNodeName().toUpperCase().equals("#TEXT")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void setWindowSizeX(int windowSize_X){
		windowSizeX = windowSize_X;
	}
	
	public static int getWindowSizeX() {
		return windowSizeX;
	}
	
	public static void setWindowSizeY(int windowSize_Y){
		windowSizeY = windowSize_Y;
	}

	public static int getWindowSizeY() {
		return windowSizeY;
	}

	public void print() {
		root.printNode("   ");
	}
}
