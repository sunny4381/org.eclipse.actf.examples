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
package org.eclipse.actf.examples.scripteditor;

import org.eclipse.actf.ai.scripteditor.data.ScriptDataManager;
import org.eclipse.actf.ai.scripteditor.util.XMLFileMessageBox;
import org.eclipse.actf.ai.scripteditor.util.XMLFileSaveUtil;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;

public class ScriptEditorShutdownListener implements IWorkbenchListener {

	public boolean preShutdown(IWorkbench workbench, boolean forced) {
		return (reqConfirmSaveData());
	}

	public void postShutdown(IWorkbench workbench) {
	}

	/**
	 * @return result process : TRUE:disable quit event, FALSE:enable quit event
	 */
	private boolean reqConfirmSaveData() {
		boolean result = true;

		// Check exist unsaved script data
		int stat = ScriptDataManager.getInstance().isSaveRequired();
		if (stat > 0) {
			// Update status
			if (stat == XMLFileMessageBox.MB_STYLE_OVERWR)
				stat = XMLFileMessageBox.MB_STYLE_CONFIRM;

			XMLFileSaveUtil saveFH = XMLFileSaveUtil.getInstance();

			// Check exist opened file
			String filePath = saveFH.getFilePath();
			// Display confirmation message box
			XMLFileMessageBox confMB = new XMLFileMessageBox(stat, filePath);
			int ret = confMB.open();

			if (ret == SWT.YES) {
				String newFile = filePath;
				if (newFile == null) {
					// Select new file
					newFile = saveFH.open();
				}
				// Save current data to XML file
				saveFH.save(newFile, false);
			} else if (ret == SWT.CANCEL) {
				// Cancel quit event
				result = false;
			}
		}

		return (result);
	}

}
