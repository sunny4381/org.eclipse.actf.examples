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
import java.util.Map;

import javax.swing.JOptionPane;

import org.eclipse.actf.examples.emine.ui.internal.PointsToolbar;
import org.eclipse.actf.examples.emine.vips.types.Fixation;
import org.eclipse.actf.examples.emine.vips.types.Recording;
import org.eclipse.actf.examples.emine.vips.types.VipsBlock;
import org.eclipse.actf.examples.emine.vips.types.VipsNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class ContentStructureConstruction {
	private Tree tableTree;
	private ArrayList<VipsNode> highlighted;
	private Map<String, VipsNode> nodePool;
	private VipsBlock bodyBlock;
	
	public ContentStructureConstruction(Tree tree){
		this.tableTree = tree;
		highlighted = new ArrayList<VipsNode>();
		
		for (VipsNode node : highlighted) {
			node.unhighlight();
		}
		
		SelectionAdapter prevPointSelectionAdapter = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PointsToolbar.getToolbar().decrementPointIndex();
				highlightFixation();
			}
		};
		
		SelectionAdapter nextPointSelectionAdapter = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PointsToolbar.getToolbar().incrementPointIndex();
				highlightFixation();
			}
		};
		
		SelectionAdapter prevRecordSelectionAdapter = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PointsToolbar.getToolbar().decrementRecordingIndex();
				initRecording();
				highlightFixation();
			}
		};
		
		SelectionAdapter nextRecordSelectionAdapter = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PointsToolbar.getToolbar().incrementRecordingIndex();
				initRecording();
				highlightFixation();
			}
		};
		
		PointsToolbar.getToolbar().getPrevPointButton().addSelectionListener(prevPointSelectionAdapter);
		PointsToolbar.getToolbar().getNextPointButton().addSelectionListener(nextPointSelectionAdapter);
		PointsToolbar.getToolbar().getPrevRecordButton().addSelectionListener(prevRecordSelectionAdapter);
		PointsToolbar.getToolbar().getNextRecordButton().addSelectionListener(nextRecordSelectionAdapter);
		PointsToolbar.getToolbar().getScanPathButton().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(PointsToolbar.getToolbar().getCurrentRecording() != null){
					initRecording();
					String scanPath = PointsToolbar.getToolbar().getCurrentRecording().getScanPath();
					JOptionPane.showMessageDialog(null, scanPath);
				}
			}
		});
		
		addListener();
	}
	
	public void initRecording(){
		Recording rec = PointsToolbar.getToolbar().getCurrentRecording();
		PointsToolbar.getToolbar().setRecordingIdLabel();
		if(rec != null && !rec.isScanPathDetected()){
			for(Fixation fix : rec.getFixations()){
				fix.setBlock(detectPoint(fix.getPoint(), bodyBlock, 0));
			}
		}
	}
	
	public void highlightFixation(){
		Fixation fix = PointsToolbar.getToolbar().getCurrentFixation();
		for (VipsNode node : highlighted) {
			node.unhighlight();
		}
		
		if(fix.getBlock() == null){
			fix.setBlock(detectPoint(fix.getPoint(), bodyBlock, 0));
		}
		
		if(fix.getBlock() != null){
			highlightNode(fix.getBlock().getTreeItem());
		}
	}
	
	public void setBodyBlock(VipsBlock block){
		this.bodyBlock = block;
		initRecording();
	}
	
	public void setTree(Tree tree) {
		this.tableTree = tree;
	}
	
	public Tree getTree(){
		return tableTree;
	}
	
	public void addListener(){
		tableTree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				for (VipsNode node : highlighted) {
					node.unhighlight();
				}
				
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
		for (VipsNode node : highlighted) {
			node.unhighlight();
		}
		highlighted.clear();
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
	
	/**
	 * here, we detect the block which contains a given point, starting searching from a 
	 * given block, and highlight the block.
	 * 
	 * 
	 * @param p
	 * @param block
	 */
	public VipsBlock detectPoint(Point p, VipsBlock block, int level){
		if(level >= PointsToolbar.getToolbar().getLevel())
			return block;
		
		boolean childrenContain = false;	
		VipsBlock container = null;
		
		if(block.containsPoint(p) && block.getChildren().isEmpty()){
			/* block contains the point and it is terminal,
			 * highlight it. */
			return block;
		} else {
			/* block may contain the point but it may not be terminal,
			 * or simply block does not contain the point,
			 * check for children  */
			for(VipsBlock child : block.getChildren()){
				/* if we find a children which contains the point,
				 * recursively call the function with the child. */
				if(child.containsPoint(p)){
					childrenContain = true;
					container = detectPoint(p, child, level + 1);
				}
			}
		}
		
		if(block.containsPoint(p) && !childrenContain){
			/* if this argument is true, it means that,
			 * the block contains the point, but its children does not.
			 * This means that, the point is inside the gaps between children.
			 * here we should highlight the block. */
			return block;
		}
		
		return container;
	}
}
