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

import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;

public class ScriptEditorShutdownListener implements IWorkbenchListener {

	public boolean preShutdown(IWorkbench workbench, boolean forced) {

		// Check exist unsaved data
		boolean result = TimeLineView.getInstance().reqConfirmSaveData();

		// return result
		return (result);
	}

	public void postShutdown(IWorkbench workbench) {
		// TODO Auto-generated method stub
	}

}
