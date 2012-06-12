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

import org.eclipse.actf.ai.scripteditor.data.ScriptDataManager;
import org.eclipse.actf.ai.scripteditor.reader.CSVReader;
import org.eclipse.actf.ai.scripteditor.util.XMLFileMessageBox;
import org.eclipse.actf.ai.scripteditor.util.XMLFileSaveUtil;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class FileCSVImportAction implements IWorkbenchWindowActionDelegate {

	private String[] EXTENSIONS = { "*.csv", "*" };

	private ScriptDataManager scriptManager;

	public void run(IAction action) {
		boolean start_flg = true;
		scriptManager = ScriptDataManager.getInstance();
		if (scriptManager.isSaveRequired() > 0) {
			XMLFileMessageBox confModifyMB = new XMLFileMessageBox(
					XMLFileMessageBox.MB_STYLE_MODIFY, null);
			int result = confModifyMB.open();
			if (result == SWT.YES) {
				XMLFileSaveUtil saveFH = XMLFileSaveUtil.getInstance();
				String filePath = saveFH.open();
				saveFH.save(filePath, true);
			} else if (result == SWT.CANCEL) {
				start_flg = false;
			}
		}
		// Check status
		if (start_flg) {
			FileDialog openDialog = new FileDialog(Display.getCurrent()
					.getActiveShell(), SWT.OPEN);
			openDialog.setFilterExtensions(EXTENSIONS);
			String csvFileName = openDialog.open();

			if (csvFileName != null) {
				XMLFileSaveUtil.getInstance().setFilePath(null);

				CSVReader csvReader = new CSVReader();
				csvReader.startCSVReader(csvFileName);
			}
		}
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
}
