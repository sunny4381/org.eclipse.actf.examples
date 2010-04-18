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

import org.eclipse.actf.ai.internal.ui.scripteditor.XMLFileMessageBox;
import org.eclipse.actf.ai.scripteditor.data.ScriptData;
import org.eclipse.actf.ai.scripteditor.data.XMLFileSaveUtil;
import org.eclipse.actf.ai.scripteditor.reader.CSVReader;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;


public class FileCSVImportAction implements IWorkbenchWindowActionDelegate, IUNIT {

	// parameters
	private String[] EXTENSIONS = { "*.csv", "*" };


	public void run(IAction action) {
		boolean start_flg = true;
		
		// Store instance of each ViewPart class
		ScriptData instScriptData = ScriptData.getInstance();

		// Check exist unsaved data Before Open file
		if(instScriptData.getStatusSaveScripts() > 0){
			// Display confirmation message box
			XMLFileMessageBox confModifyMB = new XMLFileMessageBox(MB_STYLE_MODIFY, null);
			int result = confModifyMB.open();
			// Check result
			if(result == SWT.YES){
				// Save current data to XML file
				XMLFileSaveUtil saveFH = new XMLFileSaveUtil();
				String filePath = saveFH.open();
				saveFH.save(filePath, true);
			}
			else if(result == SWT.CANCEL){
				// cancel close action
				start_flg = false;
			}
		}
		// Check status
		if( start_flg ){
			// Request FileDialog (Choice open file name)
			FileDialog openDialog = new FileDialog(	Display.getCurrent().getActiveShell(),
													SWT.OPEN);
			openDialog.setFilterExtensions(EXTENSIONS);
			String csvFileName = openDialog.open();

			// Check null (file name)
			if(csvFileName != null){
				// Clear XML file path
				TimeLineView.getInstance().reqStoreXMLFilePath(null);

				// Start CSV reader thread
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
