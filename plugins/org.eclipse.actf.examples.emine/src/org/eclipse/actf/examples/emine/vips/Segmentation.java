/*******************************************************************************
 * Copyright (c) 2012 Middle East Technical University Northern Cyprus Campus and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Elgin Akpinar (METU) - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.examples.emine.vips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.actf.model.dom.dombycom.IElementEx;
import org.eclipse.actf.model.dom.dombycom.IStyle;
import org.eclipse.actf.model.ui.IModelService;
import org.eclipse.actf.model.ui.editor.browser.ICurrentStyles;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserACTF;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserStyleInfo;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Segmentation {

	private Map<String, ICurrentStyles> styleMap;
	private Map<String, WebElement> nodePool;
	private ArrayList<WebElement> highlighted;
	private Map<VIPSBlock, WebElement> blockPool;
	private WebElement root;
	SeparatorDetection detector;
	VisualBlockExtraction extraction;
	IWebBrowserACTF browser;
	IWebBrowserStyleInfo style;
	private GC gc;
	private Tree tableTree;

	public void setGC(GC gc) {
		this.gc = gc;
	}

	public void setTree(Tree tree) {
		this.tableTree = tree;
	}

	public Segmentation() {

	}

	public void calculate() {
		IModelService modelService = ModelServiceUtils.getActiveModelService();
		if (modelService instanceof IWebBrowserACTF) {
			browser = (IWebBrowserACTF) modelService;

			style = browser.getStyleInfo();
			styleMap = style.getCurrentStyles();
			nodePool = new HashMap<String, WebElement>();
		}

		// browser.open(browser.getURL());
		Document doc = modelService.getDocument();
		Document docLive = modelService.getLiveDocument();

		if (doc == null || docLive == null) {
			System.out.println("doc is null");
		} else {
			// Element docElement = doc.getDocumentElement();
			Element docLiveElement = docLive.getDocumentElement();
			traverse(docLiveElement, "", "", 1);
		}

		highlighted = new ArrayList<WebElement>();

		tableTree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				for (int i = 0; i < highlighted.size(); i++) {
					highlighted.get(i).unhighlight();
				}
				highlighted = new ArrayList<WebElement>();

				TreeItem[] selection = tableTree.getSelection();
				WebElement highlightedElement = nodePool.get(selection[0]
						.getText(4));

				if (highlightedElement != null) {
					highlighted.add(highlightedElement);
					highlightedElement.highlight();
				} else {
					for (int i = 0; i < selection[0].getItems().length; i++) {
						WebElement highlightedChild = nodePool.get(selection[0]
								.getItems()[i].getText(4));
						if (highlightedChild != null) {
							highlighted.add(highlightedChild);
							highlightedChild.highlight();
						}
					}
				}
			}
		});

		tableTree.removeAll();

		root = nodePool.get("/HTML/BODY");
		blockPool = new HashMap<VIPSBlock, WebElement>();

		detector = new SeparatorDetection();
		detector.setBlockPool(blockPool);
		detector.setGc(gc);

		VIPSBlock bodyBlock = new VIPSBlock();
		bodyBlock.setTreeItem(new TreeItem(tableTree, SWT.ARROW_LEFT));
		bodyBlock.getTreeItem().setText(
				new String[] { "VB1", "BODY", "", "", "/HTML/BODY" });
		bodyBlock.setBlockName("VB1");
		blockPool.put(bodyBlock, root);
		extraction = new VisualBlockExtraction(blockPool, detector, gc,
				tableTree);
		// try {
		extraction.blockExtraction(bodyBlock, root, 1);
		// } catch (NullPointerException e) {
		// System.out.println(e.getCause());
		// }

		// printBlock(bodyBlock, "");
		// detector.seperatorDetection(bodyBlock);
		// bodyBlock.drawSeparators(gc);

		// nodePool = null;
		styleMap = null;
		// browser.navigateRefresh();
	}

	public void printNode(Node node, String indent) {
		System.out.println(indent + node.getNodeName());
		if (node.hasChildNodes()) {
			NodeList nodeList = node.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				printNode(nodeList.item(i), indent + "  ");
			}
		}
	}

	public void traverse(Node node, String prefix, String path, int index) {
		if (node.getNodeName().equals("#COMMENT") || !isValidNode(node)) {
			return;
		} else if (!node.getNodeName().toUpperCase()
				.matches("#TEXT|HR|BR|LI|IMG")
				&& node.getChildNodes().getLength() == 0) {
			return;
		}

		String parent = path;
		if (index == 1)
			path = path + "/" + node.getNodeName().toUpperCase();
		else
			path = path + "/" + node.getNodeName().toUpperCase() + "[" + index
					+ "]";

		WebElement nodeElement = new WebElement();
		ICurrentStyles nodeStyle = null;
		if (styleMap.containsKey(path)) {
			nodeStyle = styleMap.get(path);
			if (nodeStyle == null)
				nodeStyle = styleMap.get(parent);
		}

		try {
			IElementEx e = (IElementEx) node;
			IStyle style = e.getStyle();

			nodeElement.setFloatStr(style.get("styleFloat").toString());
			nodeElement.setMarginLeft(style.get("marginLeft").toString());
			nodeElement.setMarginRight(style.get("marginRight").toString());
			nodeElement.setMarginTop(style.get("marginTop").toString());
			nodeElement.setMarginBottom(style.get("marginBottom").toString());
			nodeElement.setPosition(style.get("position").toString());
			nodeElement.setE(e);
		} catch (java.lang.ClassCastException e) {

		}

		if (nodeStyle != null && nodeStyle.getDisplay() != null
				&& nodeStyle.getDisplay().toLowerCase().equals("none")) {
			return;
		}

		nodeElement.setIndex((short) index);
		nodeElement.setPath(path);
		nodeElement.setTag(node.getNodeName());
		nodeElement.setParent(nodePool.get(parent));
		nodeElement.setStyle(nodeStyle);
		nodeElement.setId(getAttribute(node, "id"));
		nodeElement.setClassName(getAttribute(node, "class"));

		if (!node.getNodeName().toUpperCase().matches("HR|BR")
				&& nodeStyle != null && nodeStyle.getRectangle() != null) {
			if (nodeStyle.getRectangle().height == 0
					|| nodeStyle.getRectangle().width == 0) {
				return;
			}
		}

		if (nodePool.get(parent) != null)
			nodePool.get(parent).addChild(nodeElement);
		nodePool.put(path, nodeElement);

		if (node.hasChildNodes()) {
			NodeList nodeList = node.getChildNodes();
			HashMap<String, Integer> childrenTags = new HashMap<String, Integer>();
			for (int i = 0; i < nodeList.getLength(); i++) {
				if (!childrenTags.containsKey(nodeList.item(i).getNodeName()
						.toUpperCase())) {
					childrenTags.put(nodeList.item(i).getNodeName()
							.toUpperCase(), 1);
				} else {
					childrenTags.put(
							nodeList.item(i).getNodeName().toUpperCase(),
							childrenTags.get(nodeList.item(i).getNodeName()
									.toUpperCase()) + 1);
				}

				traverse(
						nodeList.item(i),
						prefix + "  ",
						path,
						childrenTags.get(nodeList.item(i).getNodeName()
								.toUpperCase()));
			}
			childrenTags = null;
			nodeList = null;
		}
	}

	public void printBlock(VIPSBlock block, String indent) {
		System.out.println(indent + blockPool.get(block).getTag() + " "
				+ blockPool.get(block).getId() + " "
				+ blockPool.get(block).getClassName() + " "
				+ blockPool.get(block).getChildren().size() + " "
				+ block.getDoc() + " " + blockPool.get(block).getPath() + " "
				+ blockPool.get(block).getFontSize());
		for (VIPSBlock child : block.getChildren()) {
			printBlock(child, indent + "    ");
		}
	}

	public static String getAttribute(Node node, String attribute) {
		try {
			NamedNodeMap attr = node.getAttributes();
			for (int i = 0; i < attr.getLength(); i++) {
				Node att = attr.item(i);
				if (att.getNodeName().equals(attribute)) {
					return att.getNodeValue();
				}
			}
			return "";
		} catch (NullPointerException e) {
			return "";
		} catch (java.lang.UnsupportedOperationException e) {
			return "";
		}
	}

	public static void printAttributes(Node node, String attribute) {
		try {
			NamedNodeMap attr = node.getAttributes();
			for (int i = 0; i < attr.getLength(); i++) {
				Node att = attr.item(i);
				if (att.getNodeName().equals(attribute)) {
					System.out.println(att.getNodeName());
					System.out.println(att.getNodeType());
					System.out.println(att.getNodeValue());
					System.out.println(att.getBaseURI());
					System.out.println(att.getPrefix());
					System.out.println();
				}
			}

		} catch (NullPointerException e) {

		} catch (java.lang.UnsupportedOperationException e) {

		}
	}

	public static void printDetails(Node node) {
		if (!isTextNode(node)) {
			System.out.print(node.getNodeName().toLowerCase());
			if (node.getNodeValue() != null)
				System.out.print(node.getNodeValue() + " ");
		} else {
			System.out.print("Text: ");
			if (node.getNodeValue() != null)
				System.out.print(node.getNodeValue() + " ");
		}
		if (node.hasAttributes()) {
			NamedNodeMap attributes = node.getAttributes();
			for (int i = 0; i < attributes.getLength(); i++) {
				Node attrNode = attributes.item(i);
				System.out.println(attrNode.getNodeName() + " = "
						+ attrNode.getNodeValue());
			}
		}
		System.out.println();
	}

	public static boolean isValidNode(Node node) {
		if (WebElement.INVALID_NODES.contains(node.getNodeName().toUpperCase()))
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
}
