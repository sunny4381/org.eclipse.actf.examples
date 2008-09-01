/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.examples.aibrowser;

import org.eclipse.actf.model.flash.proxy.cache.FlashCacheUtil;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;


public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    @Override
    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    @Override
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        // configurer.setInitialSize(new Point(400, 300));
        configurer.setShowCoolBar(true);
        // configurer.setShowPerspectiveBar(true);
        configurer.setShowMenuBar(true);
        configurer.setShowStatusLine(true);
        
        FlashCacheUtil.clearCacheForStartup();
    }
    
    @Override
    public void postWindowOpen() {
        // remove search and run menus
        IMenuManager menuManager = getWindowConfigurer().getActionBarConfigurer().getMenuManager();
        IContributionItem[] items = menuManager.getItems();
        for (int i = 0; i < items.length; i++) {
            if (null != items[i].getId()
                && (items[i].getId().equals("org.eclipse.search.menu")
                    || items[i].getId().equals("org.eclipse.ui.run"))) {
                items[i].dispose();
            }
        }   
        try {
            String startURI;
            startURI = ClientPlugin.getDefault().getHelpFileURI("org.eclipse.actf.examples.aibrowser.doc", "docs/index.html");
            ModelServiceUtils.launch(startURI);
        } catch (Throwable t) {
        	t.printStackTrace();
        }
        // WebBrowserEditorManager.launch("about:blank");
        
        FlashCacheUtil.checkCache();
    }
}
