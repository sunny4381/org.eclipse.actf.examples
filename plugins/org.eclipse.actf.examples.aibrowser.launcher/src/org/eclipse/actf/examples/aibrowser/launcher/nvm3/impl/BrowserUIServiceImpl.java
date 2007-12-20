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
package org.eclipse.actf.examples.aibrowser.launcher.nvm3.impl;

import org.eclipse.actf.examples.aibrowser.launcher.EclipseLauncher;
import org.eclipse.actf.examples.aibrowser.launcher.RequestBrokerProxy;
import org.eclipse.actf.examples.aibrowser.launcher.nvm3.BrowserUIService;


public class BrowserUIServiceImpl implements BrowserUIService {

    public void browserGoBack() {
        try {
            RequestBrokerProxy.invokeNavigator("goBackward");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void browserGoForward() {
        try {
            RequestBrokerProxy.invokeNavigator("goForward");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void browserRefresh() {
        try {
            RequestBrokerProxy.invokeNavigator("navigateRefresh");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gotoUrl(String url) {
        try {
            RequestBrokerProxy.invokeNavigator("gotoUrl", url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void quitBrowser() {
        // TODO Auto-generated method stub
		
    }

    private EclipseLauncher eclipseLauncher;

    public boolean startBrowser() {
        eclipseLauncher = new EclipseLauncher();
        eclipseLauncher.launch();
        return true;
    }

    public boolean startBrowserWithUrl(String url) {
        return false;
    }

}
