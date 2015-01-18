/*******************************************************************************
 * Copyright (c) 2006, 2015 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.examples.simplevisualizer.ui.perspectives;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.actf.examples.simplevisualizer.ui.views.SimpleVisualizerView;
import org.eclipse.actf.model.ui.util.PerspectiveListenerForBrowserLaunch;
import org.eclipse.actf.visualization.ui.IVisualizationView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.PlatformUI;

public class HTMLPerspective implements IPerspectiveFactory {

	public static final String ID = HTMLPerspective.class.getName();

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);

		IFolderLayout reportFolder = layout.createFolder(
				"simplevizualizer.html.report.folder", //$NON-NLS-1$
				IPageLayout.BOTTOM, 0.7f, editorArea);
		reportFolder.addView(IVisualizationView.SUMMARY_REPORT_VIEW_ID);
		reportFolder.addView(IVisualizationView.DETAILED_REPROT_VIEW_ID);
		layout.getViewLayout(IVisualizationView.SUMMARY_REPORT_VIEW_ID)
				.setCloseable(false);
		layout.getViewLayout(IVisualizationView.DETAILED_REPROT_VIEW_ID)
				.setCloseable(false);

		IFolderLayout simulatorFolder = layout.createFolder(
				"simplevizualizer.html.simulator.folder", //$NON-NLS-1$
				IPageLayout.RIGHT, 0.5f, editorArea);
		simulatorFolder.addView(SimpleVisualizerView.ID);
		layout.getViewLayout(SimpleVisualizerView.ID).setCloseable(false);

		try {
			PerspectiveListenerForBrowserLaunch.setTargetUrl(new URL(
					"https://www.eclipse.org/actf"));
		} catch (MalformedURLException e) {
		}
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.addPerspectiveListener(
						new PerspectiveListenerForBrowserLaunch(ID));
	}
}
