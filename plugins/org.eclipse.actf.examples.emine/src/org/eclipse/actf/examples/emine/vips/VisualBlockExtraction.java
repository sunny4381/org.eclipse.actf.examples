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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.actf.examples.emine.vips.types.VipsBlock;
import org.eclipse.actf.examples.emine.vips.types.VipsCompositeNode;
import org.eclipse.actf.examples.emine.vips.types.VipsLineBreak;
import org.eclipse.actf.examples.emine.vips.types.VipsNode;

public class VisualBlockExtraction {
	private VipsBlock bodyBlock;
	private Map<VipsBlock, VipsNode> blockPool;
	private boolean control = true;
	private static final Logger logger = Logger.getLogger(VisualBlockExtraction.class.getName());
	VipsNode root;
	
	
	public VipsBlock getBodyBlock() {
		return bodyBlock;
	}

	public void setBodyBlock(VipsBlock bodyBlock) {
		this.bodyBlock = bodyBlock;
	}

	public Map<VipsBlock, VipsNode> getBlockPool() {
		return blockPool;
	}

	public void setBlockPool(Map<VipsBlock, VipsNode> blockPool) {
		this.blockPool = blockPool;
	}

	public VisualBlockExtraction(VipsNode root,
			SeparatorDetection detector) {
		this.root = root;
		blockPool = new HashMap<VipsBlock, VipsNode>();
		bodyBlock = new VipsBlock();
		bodyBlock.setElement(root);
		bodyBlock.setBlockName("VB.1");
		blockPool.put(bodyBlock, root);
		logger.setLevel(Level.OFF);
	}

	public void start(){
		blockExtraction(bodyBlock, root, 1);
	}
	
	public void blockExtraction(VipsBlock block, VipsNode element, int doc) {
		String blockName = block.getBlockName();
		
		if(element == null){
			return;
		} else if (element.isTextNode() || !element.isValid()) {
			logger.info(blockName + " has no block");
			// no block
		} else if (element.getChildren().size() == 1) {
			logger.info(blockName + " has only one child");
			VipsNode child = element.getChildren().get(0);
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
				logger.info("All children of " + blockName + " are virtual text node");
				putIntoPool(block, element, 9);
			} else if(element.childrenHaveColumns()){ 
				logger.info(blockName + " children has columns");
				handleColumnsAtChildren(block, element, 10);
			} else if(element.rowsHaveDifferentBgColor()){
				logger.info(blockName + " children has different background colors");
				handleDifferentBgColorAtChildren(block, element, 10);
			} else if (element.hasDifferentFontSizeInChildren(0)) {
				block.setDoc(8);
				logger.info(blockName + " has different font size in children");
				// control = false;
				handleDifferentFontSize(block, element, 10);
			} else if (element.containsLineBreak()){
				logger.info(blockName + " contains line break");
				handleLineBreaks(block, element, 6);
			} else if(element.containsEmptyListItem()) {
				logger.info(blockName + " contains an empty list item");
				handleEmptyListItem(block, element, 6);
			} else if (element.hasDivGroups() && control) {
				logger.info(blockName + " has different groups of divs in children");
				handleDivGroups(block, element, 7);
			} else if (element.hasDifferentFloatInChildren(0)) {
				logger.info(blockName + " has different float in children");
				handleDifferentFloat(block, element, 6);
			} else if (element.hasDifferentMarginInChildren(0)) {
				block.setDoc(8);
				logger.info(blockName + " has different margin in children");
				handleDifferentMargin(block, element, 8);
			} else if (element.doesContainImage()) {
				logger.severe(blockName + " has at least one image child");
				handleImageInChildren(block, element, 8);
			} else if (element.doesContainLineBreakObject()) {			
				logger.severe(blockName + " has at least one object child");
				handleObjectInChildren(block, element, 8);
			} else {
				logger.info(blockName + " has no property in children");
				handleNormalForm(block, element, 8);
			}
		}
	}

	/**
	 * If node is a table and some of its columns have different background color than the
	 * others, divide the table into the number saparate columns and construct a block for
	 * each piece.
	 * 
	 * @param block
	 * @param element
	 * @param doc
	 */
	private void handleDifferentBgColorAtChildren(VipsBlock block, VipsNode element, int doc) {
		String bgColor = "";
		ArrayList<VipsNode> rows = getClonedChildren(element);
		VipsNode firstRow = rows.get(0);
		ArrayList<VipsCompositeNode> composites = new ArrayList<VipsCompositeNode>();
		/* According to the first row, define the columns with respect to the background colors */
		for(VipsNode column: firstRow.getChildren()){
			if(!column.getBackground().equals(bgColor)){
				/* Add a new composite node, when different bg color found */
				VipsCompositeNode tmpNode = new VipsCompositeNode();
				composites.add(tmpNode);
				bgColor = column.getBackground();
			}
			composites.get(composites.size()-1).addChild(column);
		}
		
		/* For the other rows after the first one */
		for(int i = 1; i < rows.size(); i++){
			int j = -1;
			bgColor = "";
			VipsNode row = rows.get(i);
			for(VipsNode column: row.getChildren()){
				if(!column.getBackground().equals(bgColor)){
					j++;
					bgColor = column.getBackground();
				}
				composites.get(j).addChild(column);
			}
		}
		
		for(VipsCompositeNode composite : composites){
			createCompositeBlock(block, composite, doc);
		}
	}

	
	/**
	 * According to the VIPS Algorithm, if there are some children of a node and two
	 * or more but not all of these nodes are in the same horizontal line, the nodes
	 * in the same line must be grouped to form a composite block before block
	 * extraction.
	 *  
	 * @param block
	 * @param element
	 * @param doc
	 */
	private void handleColumnsAtChildren(VipsBlock block, VipsNode element, int doc) {
		block.setDoc(6);
		ArrayList<VipsNode> children = getClonedChildren(element);
		ArrayList<VipsNode> tmpList = new ArrayList<VipsNode>();
		VipsCompositeNode tempCompositeNode = new VipsCompositeNode();
		boolean isWide = false;
		for(VipsNode child : children){
			if(child.getStyle() != null && 
					child.getStyle().getRectangle() != null &&
					child.getStyle().getRectangle().width < DomStructureConstruction.getWindowSizeX()){
				if(isWide){
					if(tempCompositeNode.hasChildren()){
						tmpList.add(tempCompositeNode);
					}
					tempCompositeNode = new VipsCompositeNode();
				}
				tempCompositeNode.addChild(child);
				isWide = false;
			} else {
				if(!isWide){
					if(tempCompositeNode.hasChildren()){
						tmpList.add(tempCompositeNode);
					}
					tempCompositeNode = new VipsCompositeNode();
				}
				tempCompositeNode.addChild(child);
				isWide = true;
			}
		}
		
		if(tempCompositeNode.hasChildren()){
			tmpList.add(tempCompositeNode);
		}
		
		processTempList(block, tmpList, doc);
		tmpList = null;
		tempCompositeNode = null;
	}

	/**
	 * Children have no special form
	 * 
	 * @param block
	 * @param element
	 * @param doc
	 */
	private void handleNormalForm(VipsBlock block, VipsNode element, int doc){
		VipsCompositeNode tempCompositeNode = new VipsCompositeNode();
		ArrayList<VipsNode> children = getClonedChildren(element);
		ArrayList<VipsNode> tmpList = new ArrayList<VipsNode>();
		for (VipsNode child : children) {
			if (child.isLineBreakNode() || child.isImage() || child.isLineBreakObjet()) {
				if(tempCompositeNode.hasChildren()){
					tmpList.add(tempCompositeNode);
				}
				putIntoPool(block, child, 11);
				tempCompositeNode = new VipsCompositeNode();
			} else {
				tempCompositeNode.addChild(child);
			}
		}
		if(tempCompositeNode.hasChildren()){
			tmpList.add(tempCompositeNode);
		}
		processTempList(block, tmpList, doc);
		tmpList = null;
		tempCompositeNode = null;
	}

	/**
	 * If a node contains a child whose tag is HR, BR, then the node is divided into two as
	 * the nodes before the separator and after the separator. For each side of the separator, 
	 * two new blocks are created and children nodes are put under these blocks. Note that,
	 * separator does not extract a block under the main block, it just serves to extract
	 * two blocks which other nodes are put into.
	 * 
	 * @param block Parent block for the new blocks
	 * @param element Parent of the VipsNode objects which construct new blocks
	 * @param doc DoC value of the new blocks
	 */
	private void handleLineBreaks(VipsBlock block, VipsNode element, int doc){
		block.setDoc(6);
		ArrayList<VipsNode> children = getClonedChildren(element);
		ArrayList<VipsNode> tmpList = new ArrayList<VipsNode>();
		VipsCompositeNode tempCompositeNode = new VipsCompositeNode();
		ArrayList<VipsLineBreak> lineBreaks = element.getLineBreaks();
		int itr = 0;
		for(VipsLineBreak lineBreak : lineBreaks){
			for(; itr < lineBreak.getIndex(); itr++){
				tempCompositeNode.addChild(children.get(itr));
			}
			if(tempCompositeNode.hasChildren()){
				tmpList.add(tempCompositeNode);
			}
			tempCompositeNode = new VipsCompositeNode();
		}
		for(; itr < children.size(); itr++){
			tempCompositeNode.addChild(children.get(itr));
		}
		
		if(tempCompositeNode.hasChildren()){
			tmpList.add(tempCompositeNode);
		}
		processTempList(block, tmpList, doc);
		tmpList = null;
		tempCompositeNode = null;
	}
	
	/**
	 * If a node contains an empty list item, then the node is divided into two as
	 * the nodes before the separator and after the separator. For each side of the separator, 
	 * two new blocks are created and children nodes are put under these blocks. Note that,
	 * separator does not extract a block under the main block, it just serves to extract
	 * two blocks which other nodes are put into.
	 * 
	 * @param block Parent block for the new blocks
	 * @param element Parent of the VipsNode objects which construct new blocks
	 * @param doc DoC value of the new blocks
	 */
	private void handleEmptyListItem(VipsBlock block, VipsNode element, int doc){
		block.setDoc(6);
		VipsCompositeNode tempCompositeNode = new VipsCompositeNode();
		ArrayList<VipsNode> children = getClonedChildren(element);
		ArrayList<VipsNode> tmpList = new ArrayList<VipsNode>();
		
		for (int i = 0; i < children.size(); i++) {
			VipsNode child = children.get(i);
			if (child.isEmptyListItem()) {
				if(tempCompositeNode.hasChildren()){
					tmpList.add(tempCompositeNode);
				}
				tempCompositeNode = new VipsCompositeNode();
			} else {
				tempCompositeNode.addChild(child);
			}
		}
		
		if(tempCompositeNode.hasChildren()){
			tmpList.add(tempCompositeNode);
		}
		processTempList(block, tmpList, doc);
		tmpList = null;
		tempCompositeNode = null;
	}
	
	/**
	 * 
	 * @param block
	 * @param element
	 * @param doc
	 */
	private void handleDivGroups(VipsBlock block, VipsNode element, int doc) {
		VipsCompositeNode tempCompositeNode = new VipsCompositeNode();
		ArrayList<VipsNode> children = getClonedChildren(element);
		ArrayList<VipsNode> tmpList = new ArrayList<VipsNode>();
		for (int i = 0; i < children.size(); i++) {
			VipsNode child = children.get(i);
			if (child.getTag().equals("DIV") || child.isVirtualTextNode(false)) {
				tempCompositeNode.addChild(child);
			} else {
				if(tempCompositeNode.hasChildren()){
					tmpList.add(tempCompositeNode);
				}
				tempCompositeNode = new VipsCompositeNode();
				putIntoPool(block, child, 11);
			}
		}
		children = null;
		if(tempCompositeNode.hasChildren()){
			tmpList.add(tempCompositeNode);
		}
		processTempList(block, tmpList, doc);
		tmpList = null;
		tempCompositeNode = null;
	}
	
	/**
	 * 
	 * @param block
	 * @param element
	 * @param doc
	 */
	private void handleImageInChildren(VipsBlock block, VipsNode element, int doc){
		int count = element.getImageCount();
		if(count == 1 && element.getChildren().get(0).isImage()){
			handleNormalForm(block, element, doc);
		} else {
			VipsCompositeNode tempCompositeNode = new VipsCompositeNode();
			ArrayList<VipsNode> children = getClonedChildren(element);
			ArrayList<VipsNode> tmpList = new ArrayList<VipsNode>();
			for(VipsNode child : children){
				if(child.isImage()){
					if(tempCompositeNode.hasChildren()){
						tmpList.add(tempCompositeNode);
					}
					tempCompositeNode = new VipsCompositeNode();
					tempCompositeNode.addChild(child);
				} else {
					tempCompositeNode.addChild(child);
				}
			}
			if(tempCompositeNode.hasChildren()){
				tmpList.add(tempCompositeNode);
			}
			processTempList(block, tmpList, doc);
			tmpList = null;
			tempCompositeNode = null;
		}
	}
	
	/**
	 * 
	 * @param block
	 * @param element
	 * @param doc
	 */
	private void handleObjectInChildren(VipsBlock block, VipsNode element, int doc){
		int count = element.getLineBreakObjectCount();
		if(count == 1 && element.getChildren().get(0).isLineBreakObjet()){
			handleNormalForm(block, element, doc);
		} else {
			VipsCompositeNode tempCompositeNode = new VipsCompositeNode();
			ArrayList<VipsNode> children = getClonedChildren(element);
			ArrayList<VipsNode> tmpList = new ArrayList<VipsNode>();
			for(VipsNode child : children){
				if(child.isLineBreakObjet()){
					if(tempCompositeNode.hasChildren()){
						tmpList.add(tempCompositeNode);
					}
					tempCompositeNode = new VipsCompositeNode();
					tempCompositeNode.addChild(child);
				} else {
					tempCompositeNode.addChild(child);
				}
			}
			
			if(tempCompositeNode.hasChildren()){
				tmpList.add(tempCompositeNode);
			}
			processTempList(block, tmpList, doc);
			tmpList = null;
			tempCompositeNode = null;
		}
	}

	/**
	 * If a node has a child, whose at least one of margin-top and margin-bottom values are
	 * nonzero, divide this node into two blocks. Put the sibling nodes before the node with
	 * nonzero margin into the first block and put the siblings after the node with nonzero
	 * margin into the second block.
	 * <ol> 
	 * <li>If child has only nonzero margin-top, put the child into second block.</li>
	 * <li>If child has only nonzero margin-bottom, put the child into first block.</li>
	 * <li>If child has both nonzero margin-top and nonzero margin-bottom, create a third
	 * block and put it between two blocks.</li>
	 * </ol>
	 * 
	 * @param block
	 * @param element
	 * @param doc
	 */
	private void handleDifferentMargin(VipsBlock block, VipsNode element, int doc) {
		VipsCompositeNode tempCompositeNode = new VipsCompositeNode();

		ArrayList<VipsNode> children = getClonedChildren(element);
		ArrayList<VipsNode> tmpList = new ArrayList<VipsNode>();
		for (int i = 0; i < children.size(); i++) {
			VipsNode child = children.get(i);
			String childMarginTop = child.getMarginTop();
			String childMarginBottom = child.getMarginBottom();
			if (isNonZeroMargin(childMarginTop)
					&& isNonZeroMargin(childMarginBottom)) {
				if(tempCompositeNode.hasChildren()){
					tmpList.add(tempCompositeNode);
				}
				tempCompositeNode = new VipsCompositeNode();
				putIntoPool(block, child, doc);
			} else if (isNonZeroMargin(childMarginTop)) {
				if(tempCompositeNode.hasChildren()){
					tmpList.add(tempCompositeNode);
				}
				tempCompositeNode = new VipsCompositeNode();
				tempCompositeNode.addChild(child);
			} else if (isNonZeroMargin(childMarginBottom)) {
				tempCompositeNode.addChild(child);
				if(tempCompositeNode.hasChildren()){
					tmpList.add(tempCompositeNode);
				}
				tempCompositeNode = new VipsCompositeNode();
			} else {
				tempCompositeNode.addChild(child);
			}
		}
		children = null;
		if(tempCompositeNode.hasChildren()){
			tmpList.add(tempCompositeNode);
		}
		processTempList(block, tmpList, doc);
		tmpList = null;
		tempCompositeNode = null;
	}

	/**
	 * 
	 * @param margin
	 * @return
	 */
	private boolean isNonZeroMargin(String margin) {
		if (margin != null && (margin.equals("0px") || margin.equals("auto")))
			return false;
		else
			return true;
	}

	/**
	 * If one of the child node has bigger font size than its previous sibling, divide node into
	 * two blocks. Put the nodes before the child node with bigger font size into the first
	 * block, and put the remaining nodes to the second block.
	 * 
	 * If the first child of the node has bigger font size than the remaining children, extract
	 * two blocks, one of which is the first child with bigger font size, and the other contains
	 * remaining children.
	 * 
	 * @param block
	 * @param vipsNode
	 * @param doc
	 */
	public void handleDifferentFontSize(VipsBlock block, VipsNode vipsNode, int doc) {
		double maxFontSize = vipsNode.getMaxFontSizeInChildren();
		VipsCompositeNode tempCompositeNode = new VipsCompositeNode();
		ArrayList<VipsNode> children = getClonedChildren(vipsNode);
		int numberOfChildren = children.size();
		if (children.get(0).getMaxFontSize() == maxFontSize) {
			/* First child has the maximum font size. */
			int count = vipsNode.getCountOfChildrenWithMaxFontSize();
			if (count == 1) {
				/* There is only one child with maximum font size 
				 * Put the first child into pool and create a composite
				 * node for the remaining */
				putIntoPool(block, children.get(0), 11);
				for (int i = 1; i < numberOfChildren; i++) {
					tempCompositeNode.addChild(children.get(i));
				}
				createCompositeBlock(block, tempCompositeNode, 10);
			} else if (vipsNode.areAllMaxFontSizeChildrenAtFront()) {
				/* First n children have the maximum font size
				 * where n is equal to the number of children 
				 * with maximum font size. */
				VipsCompositeNode tempCompositeNode2 = new VipsCompositeNode();
				VipsCompositeNode tempCompositeNode3 = new VipsCompositeNode();
				/* Create a composite node for the children with max. font size. */
				for (int i = 0; i < count; i++) {
					tempCompositeNode2.addChild(children.get(i));
				}
				/* Create a composite node for the others. */				
				for (int i = count; i < children.size(); i++) {
					tempCompositeNode3.addChild(children.get(i));
				}
				createCompositeBlock(block, tempCompositeNode2, 10);
				createCompositeBlock(block, tempCompositeNode3, 10);
			} else {
				/* The first child has maximum font size and there are some
				 * other children which have max. font size. */
				boolean flag = true;
				for (int i = 0; i < numberOfChildren; i++) {
					VipsNode child = children.get(i);
					double childFontSize = child.getMaxFontSize();
					if (childFontSize == maxFontSize && flag) {
						createCompositeBlock(block, tempCompositeNode, 8);
						tempCompositeNode = new VipsCompositeNode();
						tempCompositeNode.addChild(child);
						flag = false;
					} else {
						tempCompositeNode.addChild(child);
						if (childFontSize != maxFontSize)
							flag = true;
					}
				}
				createCompositeBlock(block, tempCompositeNode, 10);
			}
		} else {
			/* First child does not have the maximum font size*/
			boolean flag = true;
			for (int i = 0; i < numberOfChildren; i++) {
				VipsNode child = children.get(i);
				double childFontSize = child.getMaxFontSize();
				
				if (childFontSize == maxFontSize && flag) {
					createCompositeBlock(block, tempCompositeNode, 10);
					tempCompositeNode = new VipsCompositeNode();
					tempCompositeNode.addChild(child);
					flag = false;
				} else {
					tempCompositeNode.addChild(child);
					if (childFontSize != maxFontSize)
						flag = true;
				}
			}
			createCompositeBlock(block, tempCompositeNode, 8);
		}
		children = null;
	}

	/**
	 * If node has at least one child with float value ”left” or ”right”, create three blocks.
	 * For each children,
	 * <ol> 
	 * <li> If child is left float, put it into the first block.</li>
	 * <li>If child is right float, put it into the second block.</li>
	 * <li>If child is not both left and right float, put it into the third block. If first block
	 * or second block have children, create new blocks for them. Also, create a new
	 * block for the child without float.</li>
	 * </ol>
	 * 
	 * @param block
	 * @param vipsNode
	 * @param doc
	 */
	public void handleDifferentFloat(VipsBlock block, VipsNode vipsNode, int doc) {
		VipsCompositeNode tempCompositeNode = new VipsCompositeNode();
		VipsCompositeNode tempCompositeNodeForLeft 	  = new VipsCompositeNode();
		VipsCompositeNode tempCompositeNodeForRight   = new VipsCompositeNode();
		ArrayList<VipsNode> tmpList   = new ArrayList<VipsNode>();
		
		ArrayList<VipsNode> children = getClonedChildren(vipsNode);
		
		for (int i = 0; i < children.size(); i++) {
			VipsNode child = children.get(i);
			String childFloat = child.getFloatStr();
			if (childFloat.equals("left")) {
				tempCompositeNodeForLeft.setTag(child.getTag());
				tempCompositeNodeForLeft.setFloatStr("left");
				tempCompositeNodeForLeft.addChild(child);
			} else if (childFloat.equals("right")) {
				tempCompositeNodeForRight.setTag(child.getTag());
				tempCompositeNodeForRight.setFloatStr("right");
				tempCompositeNodeForRight.addChild(child);
			} else {
				if (tempCompositeNodeForLeft.hasChildren()){
					tempCompositeNode.addChild(tempCompositeNodeForLeft);
				}
				
				if (tempCompositeNodeForRight.hasChildren()){
					tempCompositeNode.addChild(tempCompositeNodeForRight);
				}
				
				tempCompositeNode.addChild(child);
				if (!tempCompositeNodeForLeft.hasChildren() && !tempCompositeNodeForRight.hasChildren()){
					putIntoPool(block, child, 11);
				} else {
					if(tempCompositeNode.getChildren().size() != 0){
						tempCompositeNode.setExceptional(true);
						tmpList.add(tempCompositeNode);
					}
				}
				
				tempCompositeNodeForLeft = new VipsCompositeNode();
				tempCompositeNodeForRight = new VipsCompositeNode();
				tempCompositeNode = new VipsCompositeNode();
			}
		}

		if (tempCompositeNodeForLeft.hasChildren())
			tempCompositeNode.addChild(tempCompositeNodeForLeft);
		if (tempCompositeNodeForRight.hasChildren())
			tempCompositeNode.addChild(tempCompositeNodeForRight);
		children = null;
		if(tempCompositeNode.getChildren().size() != 0){
			tempCompositeNode.setExceptional(true);
			tmpList.add(tempCompositeNode);
		}
		
		processTempList(block, tmpList, doc);
		tmpList = null;
		tempCompositeNodeForLeft = null;
		tempCompositeNodeForRight = null;
	}

	/**
	 * Composite blocks which does not appear in DOM structure but generated
	 * for some reason in visual block extraction process causes unnecessarily nested
	 * blocks, if the parent block has only one composite block as a child.
	 * 
	 * Therefore, composite blocks are collected in an ArrayList and then constructed
	 * at the end of the process.
	 * 
	 * @param block
	 * @param nodeList
	 * @param doc
	 */
	private void processTempList(VipsBlock block, ArrayList<VipsNode> nodeList, int doc){
		if(nodeList.size() == 1){
			VipsNode vipsNode = nodeList.get(0);
			for(VipsNode child : vipsNode.getChildren()){
				if(child.isCompositeNode()){
					if(child.isExceptional())
						createCompositeBlockWithException(block, child, doc);
					else
						createCompositeBlock(block, child, doc);
				} else
					putIntoPool(block, child, 11);
			}
		} else {
			for(VipsNode child : nodeList){
				if(child.isExceptional())
					createCompositeBlockWithException(block, child, doc);
				else
					createCompositeBlock(block, child, doc);
			}
		}
	}
	
	/**
	 * 
	 * @param block
	 * @param vipsNode
	 * @param doc
	 * 
	 * @see VisualBlockExtraction#handleDifferentFloat(VipsBlock, VipsNode, int)
	 */
	public void createCompositeBlockWithException(VipsBlock block, VipsNode vipsNode, int doc) {
		if (vipsNode.getChildren().isEmpty()) {
			return;
		} else if (vipsNode.getChildren().size() == 1) {
			createCompositeBlock(block, vipsNode.getChildren().get(0), doc);
		} else {
			ArrayList<VipsNode> children = getClonedChildren(vipsNode);
			VipsBlock newBlock = putIntoPool(block, vipsNode, doc);
			
			for (int i = 0; i < children.size(); i++) {
				VipsNode child = children.get(i);
				if (child.getFloatStr().equals("none"))
					putIntoPool(newBlock, child, 11);
				else
					createCompositeBlock(newBlock, child, 11);
			}

//			vipsNode.setPath(children.get(0).getPath());
			for (int i = 0; i < children.size(); i++) {
				if (children.get(i).getStyle() != null) {
					vipsNode.setStyle(children.get(i).getStyle());
					break;
				}
			}
			children = null;
		}
	}

	/**
	 * 
	 * @param block
	 * @param tempCompositeNode
	 * @param doc
	 * 
	 * @see VipsCompositeNode
	 */
	public void createCompositeBlock(VipsBlock block, VipsNode tempCompositeNode, int doc) {
		ArrayList<VipsNode> children = getClonedChildren(tempCompositeNode);
		if (!children.isEmpty()) {
			/* Prevent unnecessarily nested composite nodes */
			while(tempCompositeNode.isCompositeNode() && children.size() == 1){
				tempCompositeNode = children.get(0);
				children = getClonedChildren(tempCompositeNode);
			}
			
			/*for (int i = 0; i < children.size(); i++) {
				if (children.get(i).getStyle() != null) {
					tempCompositeNode.setStyle(children.get(i).getStyle());
					break;
				}
			}*/
			tempCompositeNode.detectBordersFromChildren();
			
			for(VipsNode child : children){
				child.setParent(tempCompositeNode);
			}
			
			children = null;
			putIntoPool(block, tempCompositeNode, doc);
		}
	}

	/**
	 * Constructs a new block and put into the block pool
	 * 
	 * @param parent
	 * @param vipsNode
	 * @param doc
	 * @return
	 */
	public VipsBlock putIntoPool(VipsBlock parent, VipsNode vipsNode, int doc) {
		/* If node is a textual node or already added in the pool, do not continue */
		if (vipsNode.isTextNode() || isInBlockPool(vipsNode))
			return null;
		
		/* For some tags, if node has only one child, put the child into the pool */
		if (vipsNode.getTag().matches("TR|UL") 
				&& vipsNode.getChildren().size() == 1
				&& vipsNode.getChildren().get(0).getTag().matches("TD|LI")
				&& vipsNode.getChildren().get(0).isValid())
			vipsNode = vipsNode.getChildren().get(0);
		if(vipsNode.isCompositeNode()){
			vipsNode.setPath(parent.getElement().getPath() + "/COMPOSITE");
		}
		VipsBlock block = new VipsBlock();
		blockPool.put(block, vipsNode);
		parent.addChild(block);
		block.setParent(parent);
		block.setDoc(doc);
		block.setBlockName(parent.getBlockName() + "." + parent.getChildren().size());
		block.setElement(vipsNode);
		
		/* Parent set */
		if(vipsNode.getParent() != null){
			vipsNode.getParent().removeChild(vipsNode);
		}
		
		if(vipsNode.getStyle() == null || vipsNode.getStyle().getRectangle() == null ||
				vipsNode.getStyle().getRectangle().height == 0){
			vipsNode.detectBordersFromChildren();
		}
		
		/* Recursive call for children */
		blockExtraction(block, vipsNode, doc);
		return block;
	}

	public boolean isInBlockPool(VipsNode vipsNode) {
		if (blockPool.values().contains(vipsNode)) {
			return true;
		} else {
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<VipsNode> getClonedChildren(VipsNode vipsNode){
		ArrayList<VipsNode> children = null;
		Object cloneObject = vipsNode.getChildren().clone();
		if(cloneObject instanceof ArrayList){
			children = (ArrayList<VipsNode>) cloneObject;
		} else {
			children = new ArrayList<VipsNode>();
		}
		return children;
	}
}
