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

package org.eclipse.actf.examples.emine.vips.types;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.TreeItem;


/**
 * A block is a semantic part in the web page.
 */
public class VipsBlock {
	private int doc;
	private String blockName;
	private ArrayList<VipsBlock> children;
	private TreeItem treeItem;
	private VipsNode element;
	private VipsBlock parent;
	private ArrayList<VipsSeparator> separators;
	
	public VipsBlock() {
		doc = 0;
		blockName = "";
		children = new ArrayList<VipsBlock>();
		//separators = new ArrayList<VipsSeparator>();
	}
	
	/****************** Getters and Setters ********************/
	public VipsBlock getParent() {
		return parent;
	}

	public void setParent(VipsBlock parent) {
		this.parent = parent;
	}

	public VipsNode getElement() {
		return element;
	}

	public String getBlockName() {
		return blockName;
	}

	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}

	public TreeItem getTreeItem() {
		return treeItem;
	}

	public void setTreeItem(TreeItem treeItem) {
		this.treeItem = treeItem;
	}

	public void addChild(VipsBlock child) {
		children.add(child);
	}

	public void setElement(VipsNode element) {
		this.element = element;
	}
	
	public ArrayList<VipsSeparator> getSeparators() {
		return separators;
	}

	public void setSeparators(ArrayList<VipsSeparator> separators) {
		this.separators = separators;
	}
	
	public int getDoc() {
		return doc;
	}

	public void setDoc(int doc) {
		this.doc = doc;
	}

	public ArrayList<VipsBlock> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<VipsBlock> children) {
		this.children = children;
	}

	public String getTag() {
		if(element == null)
			return null;
		else 
			return element.getTag();
	}
	
	public String getId(){
		if(element == null)
			return null;
		else
			return element.getId();
	}
	
	public String getClassName(){
		if(element == null)
			return null;
		else
			return element.getClassName();
	}

	/************** End of Getters and Setters ****************/
	
	public void drawSeparators(GC gc) {
		for (VipsSeparator separator : getSeparators()) {
			separator.drawSeparator(gc);
		}
	}

	public void addSeparator(VipsSeparator separator) {
		separators.add(separator);
	}
	
	public void addToTree(){
		try {
			treeItem = new TreeItem(parent.getTreeItem(), SWT.ARROW_LEFT);
			treeItem.setText(new String[] { getBlockName(), element.getTag(),
					Integer.toString(doc),
					Double.toString(element.getFontSize()), element.getPath() });
			if(element.getFontSize() == 0)
				treeItem.setText(3, "");
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
	}
	
	public void printBlock(String indent) {
		System.out.print(indent);
		System.out.print(getBlockName());
		System.out.println();
		for (VipsBlock child : getChildren()) {
			child.printBlock(indent + "  ");
		}
	}
}
