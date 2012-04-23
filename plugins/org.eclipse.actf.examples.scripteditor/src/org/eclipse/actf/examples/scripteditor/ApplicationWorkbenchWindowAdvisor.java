/*******************************************************************************
 * Copyright (c) 2009, 2010 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.examples.scripteditor;

import java.net.URL;

import org.eclipse.actf.ai.internal.ui.scripteditor.PreviewPanel;
import org.eclipse.actf.ai.scripteditor.util.WebBrowserFactory;
import org.eclipse.actf.model.flash.proxy.FlashCacheUtil;
import org.eclipse.actf.model.flash.proxy.ProxySettingUtil;
import org.eclipse.actf.model.ui.editors.ie.WebBrowserEditor;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.eclipse.swt.graphics.Point;
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
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(1000, 768));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);
		configurer.setTitle("ACTF ScriptEditor");
	}

	@Override
	public void postWindowOpen() {
		super.postWindowOpen();
		FlashCacheUtil.checkCache();
		
		ProxySettingUtil.setCurrentMode(ProxySettingUtil.PROXY_NONE);
		
		// Launch Web Browser
		String strUrl = "about:blank";
		URL helpUrl = PlatformUI
				.getWorkbench()
				.getHelpSystem()
				.resolve(
						"/org.eclipse.actf.examples.scripteditor.doc/docs/index.html", //$NON-NLS-1$
						true);

		if (helpUrl != null) {
			strUrl = helpUrl.toString();
		}

		WebBrowserFactory.getInstance();
		ModelServiceUtils.launch(strUrl, WebBrowserEditor.ID);
		PreviewPanel.getInstance().setURLMovie(strUrl);

		// Store parent shell instance
		// Activator.setParentShell();

		// SetUP FileMenu listener
		ApplicationActionBarAdvisor.getInstance().setFileMenuListener();
	}

}
