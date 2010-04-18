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

import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class MovieCaptureAudioScriptModeAction implements
		IWorkbenchWindowActionDelegate {

	/**
	 * Local data
	 */
	// instance of each ViewPart class
	private TimeLineView instParentView = null;

	/**
	 * Local method : PickUP instance of parent Canvas class
	 */
	private void pickupInstViewPart() {
		if (instParentView == null) {
			instParentView = TimeLineView.getInstance();
		}
	}

	/**
	 * Management PopUP window for preference of capture audio (non-Javadoc)
	 */

	/**
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		// Store instance of parent Canvas class
		pickupInstViewPart();

		// Check current action(toggle)
		if (action.isChecked()) {
			// Set status : Enable play description mode
			instParentView.setEnableDescription(true);
		} else {
			// Set status : Disable play description mode
			instParentView.setEnableDescription(false);
		}
	}

	public void init(IWorkbenchWindow window) {
	}

	public void dispose() {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
