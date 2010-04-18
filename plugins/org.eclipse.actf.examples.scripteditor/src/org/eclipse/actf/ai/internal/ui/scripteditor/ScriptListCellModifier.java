/*******************************************************************************
 * Copyright (c) 2009, 2010 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.ai.internal.ui.scripteditor;

import org.eclipse.actf.ai.scripteditor.data.ScriptData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Item;

public class ScriptListCellModifier implements ICellModifier {

	/**
	 * Local data
	 */
	private TableViewer instParentTableViewer = null;
	private ScriptData instScriptData = null;

	/**
	 * Constructor
	 */
	public ScriptListCellModifier(TableViewer parent) {
		// Store parent TableViewer class's instance
		instParentTableViewer = parent;
		instScriptData = ScriptData.getInstance();
	}

	public void modify(Object element, String property, Object value) {
		if (element instanceof Item) {
			element = ((Item) element).getData();
		}
		// Get data of current column from input param(element).
		ScriptData item = (ScriptData) element;

		// PickUP start time & index from target item
		int startTime = item.getScriptStartTime(0);

		// Check target property
		if (property == "comment") {
			// Update string comment to ScriptData class & ScriptList
			item.setScriptComment(startTime, (String) value);
			instScriptData.setScriptComment(startTime, (String) value);
		} else if (property == "wav") {
			// no process
		} else if (property == "extended") {
			// no process
		} else if (property == "stime") {
			// no process
		} else if (property == "description") {
			// no process
		}

		// Update parent TableViewer
		instParentTableViewer.update(element, null);
	}

	public Object getValue(Object element, String property) {
		// Get data of current column from input param(element).
		ScriptData item = (ScriptData) element;

		// Check target property
		if (property == "comment") {
			// PickUP comment string from target item
			return (item.getScriptComment(0));
		} else if (property == "wav") {
			// no process
		} else if (property == "extended") {
			// no process
		} else if (property == "stime") {
			// no process
		} else if (property == "description") {
			// no process
		}

		// no target property
		return (null);
	}

	public boolean canModify(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

}
