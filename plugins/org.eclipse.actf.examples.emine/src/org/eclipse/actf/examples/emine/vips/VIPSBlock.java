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

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.TreeItem;

public class VIPSBlock {
	private int doc = 0;
	private String blockName = "";
	private boolean isDivided = false;
	private ArrayList<VIPSBlock> children;
	private ArrayList<VIPSSeparator> separators;
	private TreeItem treeItem;

	public VIPSBlock() {
		children = new ArrayList<VIPSBlock>();
		separators = new ArrayList<VIPSSeparator>();
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

	public void addChild(VIPSBlock child) {
		children.add(child);
	}

	public ArrayList<VIPSSeparator> getSeparators() {
		return separators;
	}

	public void setSeparators(ArrayList<VIPSSeparator> separators) {
		this.separators = separators;
	}

	public void drawSeparators(GC gc) {
		for (VIPSSeparator separator : getSeparators()) {
			separator.drawSeparator(gc);
		}
	}

	public void addSeparator(VIPSSeparator separator) {
		separators.add(separator);
	}

	public int getDoc() {
		return doc;
	}

	public void setDoc(int doc) {
		this.doc = doc;
	}

	public boolean isDivided() {
		return isDivided;
	}

	public void setDivided(boolean isDivided) {
		this.isDivided = isDivided;
	}

	public ArrayList<VIPSBlock> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<VIPSBlock> children) {
		this.children = children;
	}
}
