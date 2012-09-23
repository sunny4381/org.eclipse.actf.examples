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

import org.eclipse.actf.examples.emine.vips.VisualBlockExtraction;
import org.eclipse.actf.model.dom.dombycom.IElementEx;

/**
 * A composite node does not appear in the DOM structure of the page,
 * but they are created and used as needed in Visual Block Extraction
 * for several reasons.
 * 
 * While all VipsNode objects are created in DOM structure construction part,
 * VipsCompositeNode objects are created in visual block extraction.
 * 
 * @see VisualBlockExtraction#createCompositeBlock(VipsBlock, VipsNode, int)
 */
public class VipsCompositeNode extends VipsNode {
	private static final String COMPOSITE = "COMPOSITE";
	
	public VipsCompositeNode(){
		super();
	}
	
	public boolean isCompositeNode() {
		return true;
	}
	
	public double getFontSize() {
		return 0;
	}
	
	public boolean isContentNode(){
		return false;
	}
	
	public String getPath(){	
		return path;
	}
	
	public String getTag(){
		return COMPOSITE;
	}
	
	/**
	 * Since composite blocks do not appear in the DOM structure,
	 * IElementEx must be null;
	 */
	public IElementEx getE() {
		return null;
	}
}
