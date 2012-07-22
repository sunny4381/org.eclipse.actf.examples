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
import java.util.Map;

import org.eclipse.actf.examples.emine.vips.types.VipsBlock;
import org.eclipse.actf.examples.emine.vips.types.VipsNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class ContentStructureConstruction {
	private Tree tableTree;
	private ArrayList<VipsNode> highlighted;
	private Map<String, VipsNode> nodePool;
		
	public ContentStructureConstruction(Tree tree){
		this.tableTree = tree;
		addListener();
	}
	
	public void setTree(Tree tree) {
		this.tableTree = tree;
	}
	
	public Tree getTree(){
		return tableTree;
	}
	
	public void addListener(){
		highlighted = new ArrayList<VipsNode>();
		tableTree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				for (int i = 0; i < highlighted.size(); i++) {
					highlighted.get(i).unhighlight();
				}
				highlighted = new ArrayList<VipsNode>();
				TreeItem[] selection = tableTree.getSelection();
				highlightNode(selection[0]);
			}
		});
	}
	
	private void highlightNode(TreeItem selected){
		VipsNode highlightedElement = nodePool.get(selected.getText(4));
		if (highlightedElement != null) {
			highlighted.add(highlightedElement);
			highlightedElement.highlight();
		} else {
			TreeItem[] items = selected.getItems();
			for (int i = 0; i < items.length; i++) {
				highlightNode(items[i]);
			}
		}
	}
	
	public void setNodePool(Map<String, VipsNode> nodePool) {
		this.nodePool = nodePool;
	}
	
	public void removeItems(){
		tableTree.removeAll();
	}
	
	public void addBodyBlock(VipsBlock block){
		block.setTreeItem(new TreeItem(tableTree, SWT.ARROW_LEFT));
		block.getTreeItem().setText(new String[] { "VB.1", "BODY", "", "", DomStructureConstruction.getHtmlPath() +"/BODY", "Body"});
	}
	
	@SuppressWarnings("unchecked")
	public void constructTree(VipsBlock block) {
		block.addToTree();
		ArrayList<VipsBlock> children = (ArrayList<VipsBlock>) block.getChildren().clone();
		for(VipsBlock child : children){
			child.setParent(block);
			constructTree(child);
		}
	}
}
