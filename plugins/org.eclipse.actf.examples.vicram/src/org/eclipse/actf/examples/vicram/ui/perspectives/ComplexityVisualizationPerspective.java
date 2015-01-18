/*******************************************************************************
 * Copyright (c) 2009, 2015 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.examples.vicram.ui.perspectives;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.actf.model.ui.util.PerspectiveListenerForBrowserLaunch;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.PlatformUI;

public class ComplexityVisualizationPerspective implements IPerspectiveFactory {

	public static final String ID = ComplexityVisualizationPerspective.class.getName();

	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(true);

		try {
			PerspectiveListenerForBrowserLaunch.setTargetUrl(new URL(
					"https://projects.eclipse.org/projects/technology.actf/"));
		} catch (MalformedURLException e) {
		}
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.addPerspectiveListener(
						new PerspectiveListenerForBrowserLaunch(ID));
	}
}
