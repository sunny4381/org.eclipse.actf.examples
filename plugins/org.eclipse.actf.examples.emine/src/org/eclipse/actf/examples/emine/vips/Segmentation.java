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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.actf.examples.emine.vips.types.VipsBlock;
import org.eclipse.actf.model.ui.IModelService;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserACTF;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserStyleInfo;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.eclipse.swt.widgets.Tree;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Segmentation {
	private static final Logger logger = Logger.getLogger(Segmentation.class.getName());
	private VisualBlockExtraction extraction;
	private ContentStructureConstruction contentConstruction;
	private DomStructureConstruction domConstruction;

	public Segmentation() {
		logger.setLevel(Level.SEVERE);
	}

	/**
	 * DOM structure construction.
	 * This part is a preparation step and is not a part of VIPS Algorithm.
	 * Since VIPS Algorithm depends on the DOM structure of the page,
	 * we need an appropriate structure to use in block extraction.
	 * 
	 * @see DomStructureConstruction
	 */
	public void segmentPage() {
		domConstruction = new DomStructureConstruction();
		
		IModelService modelService = ModelServiceUtils.getActiveModelService();
		if (modelService instanceof IWebBrowserACTF) {
			IWebBrowserACTF browser = (IWebBrowserACTF) modelService;
			IWebBrowserStyleInfo style = browser.getStyleInfo();
			
			DomStructureConstruction.setWindowSizeX(style.getSizeInfo(true).getWholeSizeX());
			DomStructureConstruction.setWindowSizeY(style.getSizeInfo(true).getWholeSizeY());
			domConstruction.setStyleMap(style.getCurrentStyles());
			domConstruction.setHtmlPath();
		}
		
		Document doc = modelService.getDocument();
		Document docLive = modelService.getLiveDocument();

		if (doc == null || docLive == null) {
			System.out.println("doc is null");
		} else {
//			Element docLiveElement = doc.getDocumentElement();
			Element docLiveElement = docLive.getDocumentElement();
			domConstruction.traverse(docLiveElement, "", 1);
			domConstruction.setRoot();
//			domConstruction.print();
		}
	}
	
	/**
	 * Visual block extraction part of the VIPS Algorithm.
	 * The aim is to find all appropriate visual blocks contained in the web page.
	 * @see VisualBlockExtraction
	 */
	public void extractVisualBlocks(){
		extraction = new VisualBlockExtraction(domConstruction.getRoot(), null);
		extraction.start();
	}
	
	/**
	 * Content Structure Construction part of the VIPS Algorithm.
	 * After finding all visual blocks in the page, we put them in a hierarchical
	 * structure. 
	 * 
	 * @param tree
	 * @see ContentStructureConstruction
	 */
	public void constructContentStructure(Tree tree){
		contentConstruction = new ContentStructureConstruction(tree);
		contentConstruction.removeItems();
		contentConstruction.addBodyBlock(extraction.getBodyBlock());
		contentConstruction.setNodePool(domConstruction.getNodePool());
		contentConstruction.setBodyBlock(extraction.getBodyBlock());
		for(VipsBlock child : extraction.getBodyBlock().getChildren())
			contentConstruction.constructTree(child);
//		contentConstruction.detectPoint(new Point(100, 100), extraction.getBodyBlock());
	}
}
