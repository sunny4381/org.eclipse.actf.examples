/*******************************************************************************
 * Copyright (c) 2009, 2011 IBM Corporation and Others
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
import org.eclipse.actf.ai.internal.ui.scripteditor.event.EventManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class MovieCaptureAudioCaptureModeAction implements
		IWorkbenchWindowActionDelegate {

	/**
	 * Local data
	 */
	// instance of each ViewPart class
	private VolumeLevelCanvas instParentView = null;
	
	// for Event Managing
	private static EventManager eventManager = null;

	private static boolean movieCapture = false;

	/**
	 * Local method : PickUP instance of parent Canvas class
	 */
	private void pickupInstViewPart() {
		if (instParentView == null) {
			instParentView = VolumeLevelCanvas.getInstance();
		}
	}
	/**
	 * Local method : Check Event Manager instance
	 */
	private void preCheckEventManager() {
		if(eventManager == null) {
			eventManager = EventManager.getInstance(); 			
		}
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		// Store instance of parent Canvas class
		pickupInstViewPart();
		// Check event Manager instance
		preCheckEventManager();
		// Check current action(toggle)
		movieCapture = action.isChecked();
		// Set status : Capture Audio mode
		instParentView.setCurrentCaptureMode(movieCapture);
	}

	public void init(IWorkbenchWindow window) {
	}

	public void dispose() {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
