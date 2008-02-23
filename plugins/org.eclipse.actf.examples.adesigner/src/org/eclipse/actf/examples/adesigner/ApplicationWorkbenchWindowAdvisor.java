/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and Others
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
import org.eclipse.actf.examples.adesigner.ui.perspectives.FlashPerspective;
import org.eclipse.actf.examples.adesigner.ui.preferences.IPreferenceConstants;
import org.eclipse.actf.model.flash.proxy.ProxyPlugin;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;



public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }

    public void preWindowOpen() {

        PlatformUI.getPreferenceStore().setValue( IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS , false );

        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setShowCoolBar(true);
        configurer.setShowMenuBar(true);
        configurer.setShowStatusLine(true);
        configurer.setTitle(ADesignerPlugin.getResourceString("adesigner.window.title"));
        
        // Show perspective name on title
        configurer.getWindow().addPerspectiveListener(new IPerspectiveListener() {
            public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
                getWindowConfigurer().setTitle(
                        perspective.getLabel()
                                + " - " + ADesignerPlugin.getResourceString("adesigner.window.title")); //$NON-NLS-1$ //$NON-NLS-2$
            }

            public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
            }
        });      

        ProxyPlugin.getDefault().clearCacheWithCheck();
    }
    
    private void checkChache(String id){
        if(FlashPerspective.ID.equals(id)){
            ProxyPlugin.getDefault().checkCache();
        }
    }

    public void postWindowOpen() {
        // remove search and run menus
        IMenuManager menuManager = getWindowConfigurer().getActionBarConfigurer().getMenuManager();
        IContributionItem[] items = menuManager.getItems();
        for (int i = 0; i < items.length; i++) {
            if (null != items[i].getId() && (items[i].getId().equals("org.eclipse.search.menu") || items[i].getId().equals("org.eclipse.ui.run"))) {
                items[i].dispose();
            }
        }        
             
        Preferences prefStore = ADesignerPlugin.getDefault().getPluginPreferences();

        if(ADesignerPlugin.getPerspectiveID()==null && prefStore.getString(IPreferenceConstants.STARTUP_OPTION_ID).equals(IPreferenceConstants.CHOICE_SHOW_MODE_DIALOG)) {
            SwitchModeAction.openModeSwitchDialog(getWindowConfigurer().getWindow());
        }
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        checkChache(activePage.getPerspective().getId());
    }
        
}
