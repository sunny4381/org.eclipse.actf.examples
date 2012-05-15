/*******************************************************************************
 * Copyright (c) 2010, 2012 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.examples.scripteditor.actions;

import org.eclipse.actf.ai.scripteditor.util.TTMLUtil;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ExportTTMLAction implements IWorkbenchWindowActionDelegate {

	// parameters
	private static final String[] NAMES = { "TTML format (*.xml)" };
	private static final String[] EXTENSIONS = { "*.xml" };

	public void run(IAction action) {
		FileDialog saveDialog = new FileDialog(Display.getCurrent()
				.getActiveShell(), SWT.SAVE);
		saveDialog.setFilterNames(NAMES);
		saveDialog.setFilterExtensions(EXTENSIONS);
		String filepath = saveDialog.open();

		if (filepath != null) {
			TTMLUtil.exportTTML(filepath, true);
		}
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
}
