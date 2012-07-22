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

/**
 * A line break node is a HR or BR node,
 * which is used to separate two blocks.
 */
public class VipsLineBreak {
	/**
	 * Indicating whether a HR or BR node
	 */
	String tag;
	
	/**
	 * Index in which the line break node appears in parent node
	 */
	int index;
	
	public VipsLineBreak(String tag, int index){
		this.tag = tag;
		this.index = index;
	}

	/**************** Getters and Setters ***************/
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	/*********** End of Getters and Setters ***********/
}
