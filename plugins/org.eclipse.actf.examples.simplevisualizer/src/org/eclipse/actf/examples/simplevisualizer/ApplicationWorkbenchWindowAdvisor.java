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
package org.eclipse.actf.examples.simplevisualizer;

import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.WorkbenchWindow;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {

		PlatformUI.getPreferenceStore().setValue(
				IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS,
				false);

		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setShowCoolBar(true);
		configurer.setShowMenuBar(true);
		configurer.setShowStatusLine(true);
		configurer.setTitle(SimpleVisualizerPlugin
				.getResourceString("simplevizualizer.window.title")); //$NON-NLS-1$
	}

	public void postWindowOpen() {
		// remove search and run menus
		IMenuManager menuManager = getWindowConfigurer()
				.getActionBarConfigurer().getMenuManager();
		IContributionItem[] items = menuManager.getItems();
		for (int i = 0; i < items.length; i++) {
			if (null != items[i].getId()
					&& (items[i].getId().equals("org.eclipse.search.menu") || items[i] //$NON-NLS-1$
							.getId().equals("org.eclipse.ui.run"))) { //$NON-NLS-1$
				items[i].dispose();
			}
		}

		// hide quick access (for Eclipse 4.2.x)
		try {
			IWorkbenchWindow window = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			MWindow model = ((WorkbenchWindow) window).getModel();
			EModelService modelService = model.getContext().get(
					EModelService.class);
			modelService.find("SearchField", model).setToBeRendered(false);
		} catch (Exception e) {

		}
	}

}
