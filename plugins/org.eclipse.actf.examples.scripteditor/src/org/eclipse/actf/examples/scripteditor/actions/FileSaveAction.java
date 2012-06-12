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

import org.eclipse.actf.ai.scripteditor.util.XMLFileSaveUtil;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class FileSaveAction implements IWorkbenchWindowActionDelegate {

	private String saveFileName = "";

	public FileSaveAction() {
	}

	public void run(IAction action) {

		try {
			XMLFileSaveUtil saveFH = XMLFileSaveUtil.getInstance();
			saveFileName = saveFH.getFilePath();
			if (!XMLFileSaveUtil.exists(saveFileName)) {
				saveFileName = saveFH.open();
			}

			if (saveFileName != null) {
				saveFH.save(saveFileName, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}
}
