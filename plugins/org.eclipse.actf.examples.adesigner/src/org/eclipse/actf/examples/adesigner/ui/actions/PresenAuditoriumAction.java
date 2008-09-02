/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daisuke SATO - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.examples.adesigner.ui.actions;

import org.eclipse.actf.visualization.presentation.IPresentationVisualizationModes;
import org.eclipse.actf.visualization.ui.IVisualizationView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;

public class PresenAuditoriumAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow _window;

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this._window = window;
	}

	public void run(IAction action) {
		try {
			IViewPart viewPart = this._window.getActivePage().showView(
					IVisualizationView.ID_PRESENTATIONVIEW);
			if (viewPart != null) {
				IVisualizationView roomView = ((IVisualizationView) viewPart);
				roomView.setVisualizeMode(IPresentationVisualizationModes.AUDITORIUM);
				roomView.doVisualize();
			}

		} catch (PartInitException pie) {
			pie.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
