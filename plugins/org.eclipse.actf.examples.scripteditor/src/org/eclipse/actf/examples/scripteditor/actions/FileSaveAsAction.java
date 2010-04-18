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
package org.eclipse.actf.examples.scripteditor.actions;

import org.eclipse.actf.ai.scripteditor.data.XMLFileSaveUtil;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class FileSaveAsAction implements IWorkbenchWindowActionDelegate, IUNIT {

	/**
	 * Local data
	 */
	// parameters
	private String saveFileName = "";

	/**
	 * The constructor.
	 */
	public FileSaveAsAction() {
	}

	public void run(IAction action) {
		// Request FileDialog (Choice open file name)
		XMLFileSaveUtil saveFH = new XMLFileSaveUtil();
		saveFileName = saveFH.open();

		// Check null (file name)
		if (saveFileName != null) {
			// Save script data to XML file
			saveFH.save(saveFileName, true);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}
}