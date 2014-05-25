/*******************************************************************************
 * Copyright (c) 2006, 2014 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Norimasa HAYASHIDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.examples.adesigner;

import org.eclipse.actf.examples.adesigner.ui.actions.SwitchModeAction;
import org.eclipse.actf.examples.adesigner.ui.preferences.IPreferenceConstants;
import org.eclipse.actf.model.flash.proxy.FlashCacheUtil;
import org.eclipse.actf.model.ui.util.PerspectiveListenerForBrowserLaunch;
import org.eclipse.actf.visualization.ui.IVisualizationPerspective;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
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
		configurer.setTitle(ADesignerPlugin
				.getResourceString("adesigner.window.title")); //$NON-NLS-1$

		// Show perspective name on title
		configurer.getWindow().addPerspectiveListener(
				new IPerspectiveListener() {
					public void perspectiveActivated(IWorkbenchPage page,
							IPerspectiveDescriptor perspective) {
						getWindowConfigurer()
								.setTitle(
										perspective.getLabel()
												+ " - " + ADesignerPlugin.getResourceString("adesigner.window.title")); //$NON-NLS-1$ //$NON-NLS-2$
					}

					public void perspectiveChanged(IWorkbenchPage page,
							IPerspectiveDescriptor perspective, String changeId) {
					}
				});

		FlashCacheUtil.clearCacheForStartup();

		PerspectiveListenerForBrowserLaunch
				.setTargetUrl(PlatformUI
						.getWorkbench()
						.getHelpSystem()
						.resolve(
								"/org.eclipse.actf.examples.adesigner.doc/docs/index.html", //$NON-NLS-1$
								true));
	}

	private void checkChache(String id) {
		if (IVisualizationPerspective.ID_FLASH_PERSPECTIVE.equals(id)) {
			FlashCacheUtil.checkCache();
		}
	}

	@SuppressWarnings({ "nls" })
	public void postWindowOpen() {
		// remove search and run menus
		IMenuManager menuManager = getWindowConfigurer()
				.getActionBarConfigurer().getMenuManager();
		IContributionItem[] items = menuManager.getItems();
		for (int i = 0; i < items.length; i++) {
			if (null != items[i].getId()
					&& (items[i].getId().equals("org.eclipse.search.menu") || items[i]
							.getId().equals("org.eclipse.ui.run"))) {
				items[i].dispose();
			}
		}
		
		//hide quick access (for Eclipse 4.2.x)
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		MWindow model = ((WorkbenchWindow) window).getModel();
		EModelService modelService = model.getContext()
				.get(EModelService.class);
		modelService.find("SearchField", model).setToBeRendered(false);
						
		PreferenceManager prefManager = getWindowConfigurer()
				.getWorkbenchConfigurer().getWorkbench().getPreferenceManager();
		for (IPreferenceNode node : prefManager.getRootSubNodes()) {
			if ("org.eclipse.actf.ui.preferences.RootPreferencePage"
					.equals(node.getId())) {
				node
						.remove("org.eclipse.actf.util.vocab.preferences.VocabPreferencePage");
			}
		}

		Preferences prefStore = ADesignerPlugin.getDefault()
				.getPluginPreferences();

		if (ADesignerPlugin.getPerspectiveID() == null
				&& prefStore.getString(IPreferenceConstants.STARTUP_OPTION_ID)
						.equals(IPreferenceConstants.CHOICE_SHOW_MODE_DIALOG)) {
			SwitchModeAction.openModeSwitchDialog(getWindowConfigurer()
					.getWindow());
		}
		IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		checkChache(activePage.getPerspective().getId());
	}

}
