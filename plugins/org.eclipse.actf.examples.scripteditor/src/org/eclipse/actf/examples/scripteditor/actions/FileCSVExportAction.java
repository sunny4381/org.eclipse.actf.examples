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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.eclipse.actf.ai.internal.ui.scripteditor.SelectWAVFileTab;
import org.eclipse.actf.ai.internal.ui.scripteditor.XMLFileMessageBox;
import org.eclipse.actf.ai.scripteditor.data.ScriptData;
import org.eclipse.actf.ai.ui.scripteditor.views.EditPanelView;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class FileCSVExportAction implements IWorkbenchWindowActionDelegate,
		IUNIT {

	/**
	 * Local data
	 */
	// instance of each ViewPart class
	private ScriptData instScriptData = null;
	private SelectWAVFileTab instSelWavTab = null;
	// private Shell instParentShell = null;

	// parameters
	private String[] EXTENSIONS = { "*.csv", "*" };
	private String saveFileName = "";

	/**
	 * Local method : PickUP instance of each ViewPart class
	 */
	private void pickupInstViewPart() {
		if (instScriptData == null) {
			instScriptData = ScriptData.getInstance();
		}
		if (instSelWavTab == null) {
			instSelWavTab = EditPanelView.getInstance()
					.getInstanceTabSelWAVFile();
		}
	}

	/**
	 * Local method : Save data to target file(CSV)
	 */
	private void saveFile(String fname, boolean warnOverwrite) {
		PrintWriter writer = null;
		try {
			File file = new File(fname);

			// already file exist
			if (warnOverwrite && file.exists()) {
				// Warning : No Script data
				XMLFileMessageBox warningExistFile = new XMLFileMessageBox(
						MB_STYLE_OVERWR, fname);
				// Check answer
				int ret = warningExistFile.open();
				if (ret != SWT.YES)
					return;
			}

			// Open file
			writer = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(fname)));

			// Write all ScriptData to CSV file
			writer.write(instScriptData.toCSVfragment());

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
		// Request FileDialog (Choice open file name)
		FileDialog saveDialog = new FileDialog(Display.getCurrent()
				.getActiveShell(), SWT.SAVE);
		saveDialog.setFilterExtensions(EXTENSIONS);
		saveFileName = saveDialog.open();

		// Check null (file name)
		if (saveFileName != null) {
			// Store instance of each ViewPart class
			pickupInstViewPart();
			// instParentShell = instSelWavTab.getParentShell();

			// Save file
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
