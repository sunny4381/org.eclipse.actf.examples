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

package org.eclipse.actf.examples.aibrowser.launcher.nvm3;

public interface BrowserUIService {
    boolean startBrowser();
    boolean startBrowserWithUrl(String url);
    void gotoUrl(String url);
	
    void browserRefresh();
	
    void browserGoBack();
	
    void browserGoForward();
	
    // double getContentScale();
	
    // double getFontScale();
	
    // void enlargeContent();
	
    // void ensmallContent();
	
    // void enlargeFont();
	
    // void ensmallFont();
	
    // void setTranscodingMode(boolean mode);
	
    // boolean isTranscodingOn();
	
    void quitBrowser();
}
