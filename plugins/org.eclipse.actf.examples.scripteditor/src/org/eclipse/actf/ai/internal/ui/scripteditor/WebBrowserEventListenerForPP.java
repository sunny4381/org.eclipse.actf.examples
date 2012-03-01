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
package org.eclipse.actf.ai.internal.ui.scripteditor;

import org.eclipse.actf.ai.scripteditor.util.WebBrowserFactory;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserACTF;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserACTFEventListener;

public class WebBrowserEventListenerForPP implements
		IWebBrowserACTFEventListener {

	public void beforeNavigate(IWebBrowserACTF webBrowser, String url,
			String targetFrameName, boolean isInNavigation) {
		mediaSearchRequest(webBrowser);
	}

	public void browserDisposed(IWebBrowserACTF webBrowser, String title) {
		//System.out.println("dispose: "+webBrowser);
		mediaController.removeWebBrowser(webBrowser);
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void focusGainedOfAddressText(IWebBrowserACTF webBrowser) {
		// TODO Auto-generated method stub

	}

	public void focusLostOfAddressText(IWebBrowserACTF webBrowser) {
		// TODO Auto-generated method stub

	}

	public void getFocus(IWebBrowserACTF webBrowser) {
		changeCurrentWebBrowser(webBrowser);
	}

	public void navigateComplete(IWebBrowserACTF webBrowser, String url) {
		mediaSearchRequest(webBrowser);
	}

	public void navigateStop(IWebBrowserACTF webBrowser) {
		mediaSearchRequest(webBrowser);
	}

	public void newWindow(IWebBrowserACTF webBrowser) {
		System.out.println("new window: "+webBrowser);
		changeCurrentWebBrowser(webBrowser);
	}

	public void progressChange(IWebBrowserACTF webBrowser, int progress,
			int progressMax) {
		// TODO Auto-generated method stub
		// System.out.println("progress:" +progress+"/"+progressMax);

	}

	public void refreshComplete(IWebBrowserACTF webBrowser) {
		mediaSearchRequest(webBrowser);
	}

	public void refreshStart(IWebBrowserACTF webBrowser) {
		mediaSearchRequest(webBrowser);
	}

	public void rootDocumentComplete(IWebBrowserACTF webBrowser) {
		// TODO null check

		// Check load status
		if (VolumeLevelCanvas.getInstance().isEnableClearVolLvl()) {
			// CleanUP buffer of captured audio
			VolumeLevelCanvas.getInstance().cleanupMovieAudioLevel();
			// redraw captured audio level to Canvas
			VolumeLevelCanvas.getInstance().setStatusCanvasVolumeLevel(1);
		}

		mediaSearchRequest(webBrowser);
	}

	public void titleChange(IWebBrowserACTF webBrowser, String title) {
		// for YouTube
		mediaSearchRequest(webBrowser);
	}

	WebBrowserFactory mediaController = WebBrowserFactory.getInstance();
	
	private void mediaSearchRequest(IWebBrowserACTF webBrowser) {
		mediaController.mediaSearchRequest(webBrowser);
	}

	private void changeCurrentWebBrowser(IWebBrowserACTF webBrowser){
		mediaController.setCurrentWebBrowser(webBrowser);
	}
}
