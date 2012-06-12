/*******************************************************************************
 * Copyright (c) 2009, 2012 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.examples.scripteditor.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.eclipse.actf.ai.scripteditor.data.ScriptDataManager;
import org.eclipse.actf.ai.scripteditor.util.XMLFileMessageBox;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class FileCSVExportAction implements IWorkbenchWindowActionDelegate {

	private String[] EXTENSIONS = { "*.csv", "*" };
	private String saveFileName = "";

	private void saveFile(String fname, boolean warnOverwrite) {
		PrintWriter writer = null;
		try {
			File file = new File(fname);

			if (warnOverwrite && file.exists()) {
				XMLFileMessageBox warningExistFile = new XMLFileMessageBox(
						XMLFileMessageBox.MB_STYLE_OVERWR, fname);
				int ret = warningExistFile.open();
				if (ret != SWT.YES)
					return;
			}

			writer = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(fname)));

			// Write all ScriptData to CSV file
			writer.write(ScriptDataManager.getInstance().toCSVfragment(
					IUNIT.OUTPUT_CSV_TYPE_VG));

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * run() method
	 */
	public void run(IAction action) {
		FileDialog saveDialog = new FileDialog(Display.getCurrent()
				.getActiveShell(), SWT.SAVE);
		saveDialog.setFilterExtensions(EXTENSIONS);
		saveFileName = saveDialog.open();

		if (saveFileName != null) {
			saveFile(saveFileName, true);
		}
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
}
