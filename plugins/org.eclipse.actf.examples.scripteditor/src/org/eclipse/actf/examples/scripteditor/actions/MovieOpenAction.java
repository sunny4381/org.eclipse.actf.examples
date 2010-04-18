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

import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class MovieOpenAction implements IWorkbenchWindowActionDelegate, IUNIT {

	/**
	 * Local data
	 */
	// parameters
	private String[] EXTENSIONS = { "*.html", "*" };
	private String openFileName = "";

	/**
	 * The constructor.
	 */
	public MovieOpenAction() {
	}

	public void run(IAction action) {
		// Request FileDialog (Choice open file name)
		FileDialog openDialog = new FileDialog(Display.getCurrent()
				.getActiveShell(), SWT.OPEN);
		openDialog.setFilterExtensions(EXTENSIONS);
		openFileName = openDialog.open();

		// Check null (file name)
		if (openFileName != null) {
			ModelServiceUtils.launch(openFileName);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}
}