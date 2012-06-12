/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.ai.internal.ui.scripteditor.guidelist;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * Viewer sorter implementation for IScriptData
 * 
 */
public class ScriptSorter extends ViewerSorter {

	private boolean inverse = false;
	private int curColumn = -1;

	private GuildListComparator defaultComparator = new GuildListComparator();

	public ScriptSorter() {
		super();
	}

	public int compare(Viewer arg0, Object arg1, Object arg2) {
		int result = 0;

		switch (curColumn) {
		default:
			result = defaultComparator.compare(arg0, arg1, arg2);
		}

		if (inverse) {
			return (-result);
		} else {
			return (result);
		}
	}

	public void setCurColumn(int curColumn) {
		if (this.curColumn == curColumn) {
			inverse = !inverse;
		} else {
			inverse = false;
			this.curColumn = curColumn;
		}
	}

	public void reset() {
		curColumn = -1;
		inverse = false;
	}

}
