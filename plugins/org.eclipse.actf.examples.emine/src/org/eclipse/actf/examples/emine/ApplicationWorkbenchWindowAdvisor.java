/*******************************************************************************
 * Copyright (c) 2006, 2012 IBM Corporation, Middle East Technical University
 * Northern Cyprus Campus and Others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA (IBM) - initial API and implementation
 *    Elgin Akpinar (METU) - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.examples.emine;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

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
		configurer.setTitle(EminePlugin
				.getResourceString("emine.window.title")); //$NON-NLS-1$
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
	}

}
