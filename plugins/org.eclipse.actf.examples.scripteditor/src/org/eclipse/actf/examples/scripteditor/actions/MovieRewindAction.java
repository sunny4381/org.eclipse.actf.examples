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

import org.eclipse.actf.ai.internal.ui.scripteditor.PreviewPanel;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class MovieRewindAction implements IWorkbenchWindowActionDelegate {

	/**
	 * Local data
	 */
	// instance of each ViewPart class
	private PreviewPanel instPreviewPanel = null;

	/**
	 * The constructor.
	 */
	public MovieRewindAction() {
	}

	/**
	 * Local method : PickUP instance of each ViewPart class
	 */
	private void pickupInstViewPart() {
		if (instPreviewPanel == null) {
			instPreviewPanel = PreviewPanel.getInstance();
		}
	}

	public void run(IAction action) {
		// Store instance of each ViewPart class
		pickupInstViewPart();

		// Rewind movie by Web Browser
		instPreviewPanel.rewindMedia();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}
}