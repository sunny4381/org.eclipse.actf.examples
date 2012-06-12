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

import org.eclipse.actf.ai.internal.ui.scripteditor.VolumeLevelCanvas;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class MovieCaptureAudioClearDataAction implements
		IWorkbenchWindowActionDelegate {

	private VolumeLevelCanvas instParentView = null;

	public void run(IAction action) {
		if (instParentView == null) {
			instParentView = VolumeLevelCanvas.getInstance();
		}

		// Clear capture data
		instParentView.cleanupMovieAudioLevel();
		// Redraw Canvas
		instParentView.setStatusCanvasVolumeLevel(1);
	}

	public void init(IWorkbenchWindow window) {
	}

	public void dispose() {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
