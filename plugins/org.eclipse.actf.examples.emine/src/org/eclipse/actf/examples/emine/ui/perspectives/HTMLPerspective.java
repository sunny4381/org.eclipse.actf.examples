/*******************************************************************************
 * Copyright (c) 2006, 2012 IBM Corporation, Middle East Technical University
 * Northern Cyprus Campus and Others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *    Elgin Akpinar -  VIPS implementation
 *******************************************************************************/

package org.eclipse.actf.examples.emine.ui.perspectives;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.actf.examples.emine.ui.views.VIPSVisualizerView;
import org.eclipse.actf.model.ui.util.PerspectiveListenerForBrowserLaunch;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.PlatformUI;

public class HTMLPerspective implements IPerspectiveFactory {

	public static final String ID = HTMLPerspective.class.getName();

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);

		IFolderLayout simulatorFolder = layout.createFolder(
				"emine.html.simulator.folder", //$NON-NLS-1$
				IPageLayout.BOTTOM, 0.5f, editorArea);
		simulatorFolder.addView(VIPSVisualizerView.ID);
		layout.getViewLayout(VIPSVisualizerView.ID).setCloseable(false);

		try {
			PerspectiveListenerForBrowserLaunch.setTargetUrl(new URL(
					"http://www.eclipse.org/actf"));
		} catch (MalformedURLException e) {
		}

		PlatformUI
				.getWorkbench()
				.getActiveWorkbenchWindow()
				.addPerspectiveListener(
						new PerspectiveListenerForBrowserLaunch(ID));
	}
}
