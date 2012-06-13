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

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class VisualBlockExtraction {

	private Map<VIPSBlock, WebElement> blockPool;

	private SeparatorDetection detector;
	private GC gc;
	private Tree tree;
	private boolean control = true;

	public VisualBlockExtraction(Map<VIPSBlock, WebElement> blockPool,
			SeparatorDetection detector, GC gc, Tree tree) {
		this.blockPool = blockPool;
		this.detector = detector;
		this.gc = gc;
		this.tree = tree;
	}

	public void blockExtraction(VIPSBlock block, WebElement element, int doc) {

		if (element.getTag().equals("#TEXT") || !element.isValid()) {
			// no block
		} else if (element.getChildren().size() == 1) {
			WebElement child = element.getChildren().get(0);
			if (child.isTextNode()) {
				block.setDoc(11);
				return;
			}
			blockExtraction(block, child, 11);
		} else { // block has more than one children
			// (a) if all of the children are virtual text nodes, the node will
			// be a block

			if (element.areAllChildrenVirtualTextNodes()) {
				// the node will be a block
				putIntoPool2(block, element, 9);
			} else if (element.containsBR() || element.containsHR()
					|| element.containsEmptyListItem()) {
				block.setDoc(6);
				WebElement tempElement = new WebElement();
				for (int i = 0; i < element.getChildren().size(); i++) {
					WebElement child = element.getChildren().get(i);
					if (child.getTag().matches("HR|BR")) {
						createTempElement(block, tempElement, 9);
						tempElement = new WebElement();
					} else if (child.getTag().matches("LI")
							&& !child.hasChildren()) {
						createTempElement(block, tempElement, 9);
						tempElement = new WebElement();
					} else {
						tempElement.addChild(child);
					}
				}
				createTempElement(block, tempElement, 9);
			} else if (element.hasDifferentFontSizeInChildren(0)) {
				block.setDoc(8);
				System.out.println(block.getBlockName()
						+ " has different font size");
				// control = false;
				handleDifferentFontSize2(block, element, 10);
			} else if (element.hasDivGroups() && control) {
				handleDivGroups(block, element, 7);
			} else if (element.hasDifferentFloatInChildren(0)) {
				System.out.println(block.getBlockName()
						+ " has different float");
				handleDifferentFloat(block, element, 6);
			} else if (element.hasDifferentMarginInChildren(0)) {
				block.setDoc(8);
				System.out.println(block.getBlockName()
						+ " has different margin");
				handleDifferentMargin(block, element, 8);
			} else if (element.hasChildContainingImage()) {

			} else {
				WebElement tempElement = new WebElement();
				for (int i = 0; i < element.getChildren().size(); i++) {
					WebElement child = element.getChildren().get(i);
					if (child.isLineBreakNode()) {
						createTempElement(block, tempElement, 8);
						putIntoPool2(block, child, 11);
						tempElement = new WebElement();
					} else {
						tempElement.addChild(child);
					}
				}
				createTempElement(block, tempElement, 8);
			}
		}
	}

	private void handleDivGroups(VIPSBlock block, WebElement element, int doc) {
		WebElement tempElement = new WebElement();

		for (int i = 0; i < element.getChildren().size(); i++) {
			WebElement child = element.getChildren().get(i);
			if (child.getTag().equals("DIV") || child.isVirtualTextNode(false)) {
				tempElement.addChild(child);
			} else {
				createTempElement(block, tempElement, doc);
				tempElement = new WebElement();
				putIntoPool2(block, child, 11);
			}

		}
		createTempElement(block, tempElement, doc);
	}

	private void handleDifferentMargin(VIPSBlock block, WebElement element,
			int doc) {
		WebElement tempElement = new WebElement();

		for (int i = 0; i < element.getChildren().size(); i++) {
			WebElement child = element.getChildren().get(i);
			String childMarginTop = child.getMarginTop();
			String childMarginBottom = child.getMarginBottom();
			if (isNonZeroMargin(childMarginTop)
					&& isNonZeroMargin(childMarginBottom)) {
				createTempElement(block, tempElement, doc);
				tempElement = new WebElement();
				putIntoPool2(block, child, doc);
			} else if (isNonZeroMargin(childMarginTop)) {
				createTempElement(block, tempElement, doc);
				tempElement = new WebElement();
				tempElement.addChild(child);
			} else if (isNonZeroMargin(childMarginBottom)) {
				tempElement.addChild(child);
				createTempElement(block, tempElement, doc);
				tempElement = new WebElement();
			} else {
				tempElement.addChild(child);
			}

		}
		createTempElement(block, tempElement, doc);
	}

	private boolean isNonZeroMargin(String margin) {
		if (margin != null && (margin.equals("0px") || margin.equals("auto")))
			return false;
		else
			return true;
	}

	public void handleDifferentFontSize2(VIPSBlock block, WebElement element,
			int doc) {
		int maxFontSize = element.getMaxFontSizeInChildren();
		WebElement tempElement = new WebElement();

		if (element.getChildren().get(0).getFontSize() == maxFontSize) {
			int count = element.getCountOfChildrenWithMaxFontSize();
			if (count == 1) {
				putIntoPool2(block, element.getChildren().get(0), 11);
				for (int i = 1; i < element.getChildren().size(); i++) {
					tempElement.addChild(element.getChildren().get(i));
				}
				createTempElement(block, tempElement, 10);
			} else if (element.areAllMaxFontSizeChildrenAtFront()) {
				WebElement tempElement2 = new WebElement();
				WebElement tempElement3 = new WebElement();

				for (int i = 0; i < count; i++) {
					tempElement2.addChild(element.getChildren().get(i));
				}
				for (int i = count; i < element.getChildren().size(); i++) {
					tempElement3.addChild(element.getChildren().get(i));
				}

				createTempElement(block, tempElement2, 10);
				createTempElement(block, tempElement3, 10);
			} else {
				boolean flag = true;
				for (int i = 0; i < element.getChildren().size(); i++) {
					WebElement child = element.getChildren().get(i);
					int childFontSize = child.getFontSize();
					if (childFontSize == maxFontSize && flag) {
						createTempElement(block, tempElement, 8);
						tempElement = new WebElement();
						tempElement.addChild(child);
						flag = false;
					} else {
						tempElement.addChild(child);
						if (childFontSize != maxFontSize)
							flag = true;
					}
				}
				createTempElement(block, tempElement, 10);
			}
		} else {
			boolean flag = true;
			for (int i = 0; i < element.getChildren().size(); i++) {
				WebElement child = element.getChildren().get(i);
				int childFontSize = child.getFontSize();
				if (childFontSize == maxFontSize && flag) {
					createTempElement(block, tempElement, 10);
					tempElement = new WebElement();
					tempElement.addChild(child);
					flag = false;
				} else {
					tempElement.addChild(child);
					if (childFontSize != maxFontSize)
						flag = true;
				}
			}
			createTempElement(block, tempElement, 8);
		}
	}

	public void handleDifferentFontSize(VIPSBlock block, WebElement element,
			int doc) {
		int fontSize = element.getChildren().get(0).getFontSize();
		boolean flag = true;
		WebElement tempElement = new WebElement();

		if (!element.hasDifferentFontSizeInChildren(1)
				&& element.getChildren().get(0).getFontSize() > element
						.getChildren().get(1).getFontSize()) {
			// first one has bigger font size and the others are all the same
			putIntoPool2(block, element.getChildren().get(0), doc);
			for (int i = 1; i < element.getChildren().size(); i++) {
				tempElement.addChild(element.getChildren().get(i));
			}
			createTempElement(block, tempElement, doc);
		} else {
			for (int i = 0; i < element.getChildren().size(); i++) {
				WebElement child = element.getChildren().get(i);
				int childFontSize = child.getFontSize();
				if (child.isLineBreakNode() && childFontSize != fontSize
						&& flag) {
					if (childFontSize > fontSize) {
						flag = false;
						createTempElement(block, tempElement, doc);
						tempElement = new WebElement();
						tempElement.addChild(child);
					} else {
						tempElement.addChild(child);
					}
				} else {
					tempElement.addChild(child);
				}
				fontSize = childFontSize;
			}
			// if(element.getChildren().size() !=
			// tempElement.getChildren().size())
			createTempElement(block, tempElement, doc);
		}
	}

	public void handleDifferentFloat(VIPSBlock block, WebElement element,
			int doc) {
		WebElement tempElement = new WebElement();
		WebElement tempLeft = new WebElement();
		WebElement tempRight = new WebElement();
		for (int i = 0; i < element.getChildren().size(); i++) {
			WebElement child = element.getChildren().get(i);
			String childFloat = child.getFloatStr();
			if (childFloat.equals("left")) {
				tempLeft.setTag(child.getTag());
				tempLeft.setFloatStr("left");
				tempLeft.addChild(child);
			} else if (childFloat.equals("right")) {
				tempRight.setTag(child.getTag());
				tempRight.setFloatStr("right");
				tempRight.addChild(child);
			} else {
				if (tempLeft.hasChildren())
					tempElement.addChild(tempLeft);
				if (tempRight.hasChildren())
					tempElement.addChild(tempRight);
				tempElement.addChild(child);
				if (!tempLeft.hasChildren() && !tempRight.hasChildren())
					putIntoPool2(block, child, 11);
				else
					createTempElement2(block, tempElement, doc);
				tempLeft = new WebElement();
				tempRight = new WebElement();
				tempElement = new WebElement();
			}
		}

		if (tempLeft.hasChildren())
			tempElement.addChild(tempLeft);
		if (tempRight.hasChildren())
			tempElement.addChild(tempRight);
		createTempElement2(block, tempElement, doc);
		tempLeft = null;
		tempRight = null;
	}

	public void createTempElement2(VIPSBlock block, WebElement tempElement,
			int doc) {
		if (tempElement.getChildren().isEmpty()) {
			return;
		} else if (tempElement.getChildren().size() == 1) {
			createTempElement(block, tempElement.getChildren().get(0), doc);
		} else {
			VIPSBlock newBlock = putIntoPool2(block, tempElement, doc);
			for (int i = 0; i < tempElement.getChildren().size(); i++) {
				WebElement child = tempElement.getChildren().get(i);
				if (child.getFloatStr().equals("none"))
					putIntoPool2(newBlock, child, 11);
				else
					createTempElement(newBlock, child, 11);
			}

			tempElement.setTag(tempElement.getChildren().get(0).getTag());
			tempElement.setPath(tempElement.getChildren().get(0).getPath());
			for (int i = 0; i < tempElement.getChildren().size(); i++) {
				if (tempElement.getChildren().get(i).getStyle() != null) {
					tempElement.setStyle(tempElement.getChildren().get(i)
							.getStyle());
					break;
				}
			}
			tempElement.detectBordersFromChildren();
		}
	}

	public void createTempElement(VIPSBlock block, WebElement tempElement,
			int doc) {
		if (!tempElement.getChildren().isEmpty()) {
			if (tempElement.getChildren().size() == 1) {
				tempElement = tempElement.getChildren().get(0);
			} else {
				tempElement.setTag(tempElement.getChildren().get(0).getTag());
				for (int i = 0; i < tempElement.getChildren().size(); i++) {
					if (tempElement.getChildren().get(i).getStyle() != null) {
						tempElement.setStyle(tempElement.getChildren().get(i)
								.getStyle());
						break;
					}
				}
				tempElement.detectBordersFromChildren();
			}
			putIntoPool2(block, tempElement, doc);
		}
	}

	public VIPSBlock putIntoPool2(VIPSBlock parent, WebElement element, int doc) {
		if (element.getTag().equals("#TEXT") || isInBlockPool(element))
			return null;

		if (element.getTag().matches("TR|UL")
				&& element.getChildren().size() == 1
				&& element.getChildren().get(0).getTag().matches("TD|LI")
				&& element.getChildren().get(0).isValid())
			element = element.getChildren().get(0);

		VIPSBlock block = new VIPSBlock();
		blockPool.put(block, element);
		parent.addChild(block);
		block.setDoc(doc);
		block.setBlockName(parent.getBlockName() + "."
				+ parent.getChildren().size());

		try {
			TreeItem item = new TreeItem(parent.getTreeItem(), SWT.ARROW_LEFT);
			item.setText(new String[] { block.getBlockName(), element.getTag(),
					Integer.toString(doc),
					Integer.toString(element.getFontSize()), element.getPath() });
			block.setTreeItem(item);
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}

		blockExtraction(block, element, doc);

		// try{
		// detector.seperatorDetection(block);
		// } catch(NullPointerException e){
		// System.out.println(element.getPath());
		// }
		//
		// block.drawSeparators(gc);
		return block;
	}

	public boolean isInBlockPool(WebElement element) {
		if (blockPool.values().contains(element)) {
			return true;
		} else {
			return false;
		}
	}

	public void printBlock(VIPSBlock block, String indent) {
		System.out.println(indent + blockPool.get(block).getTag() + " "
				+ blockPool.get(block).getChildren().size() + " "
				+ block.getDoc() + " " + blockPool.get(block).getPath() + " "
				+ blockPool.get(block).getFontSize());
		for (VIPSBlock child : block.getChildren()) {
			printBlock(child, indent + "    ");
		}
	}
}
