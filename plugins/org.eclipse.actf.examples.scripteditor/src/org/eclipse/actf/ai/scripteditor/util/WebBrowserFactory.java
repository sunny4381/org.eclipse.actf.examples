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
package org.eclipse.actf.ai.scripteditor.util;

import org.eclipse.actf.model.dom.dombycom.AnalyzedResult;
import org.eclipse.actf.model.dom.dombycom.INodeEx;
import org.eclipse.actf.model.dom.dombycom.INodeExVideo;
import org.eclipse.actf.model.dom.dombycom.INodeExVideo.VideoState;
import org.eclipse.actf.model.ui.IModelService;
import org.eclipse.actf.model.ui.IModelServiceHolder;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserACTF;
import org.eclipse.actf.model.ui.editor.browser.WebBrowserEventUtil;
import org.eclipse.actf.model.ui.editors.ie.WebBrowserEditor;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.w3c.dom.Node;

/**
 * 
 * @category Factory class for FlashPlayer
 * 
 */
public class WebBrowserFactory extends WebBrowserEditor implements
		IModelServiceHolder {

	// Media sample unit time(100msec) to Local unit time(1msec)
	private static final int SEC2MSEC = 1000;

	// Local data
	private static WebBrowserFactory ownInst = null;

	// for Browser (dummy)
	private IWebBrowserACTF browserPreview = null;

	/**
	 * Creates a new Internet Explorer Editor.
	 */
	private WebBrowserFactory(String targetURL) {
		// Spawn WebBrowser
		initFactory(targetURL);
	}

	private void initFactory(String targetURL) {
		// Launch WebBrowser
		ModelServiceUtils.launch(targetURL, WebBrowserEditor.ID); //$NON-NLS-1$

		// TODO check by using instanceof
		browserPreview = (IWebBrowserACTF) ModelServiceUtils
				.getActiveModelService();
	}

	/**
	 * @category Getter method : Get own Instance
	 */
	static public WebBrowserFactory getInstance(String targetURL) {
		// 1st check current Own Instance
		if (ownInst == null) {
			synchronized (WebBrowserFactory.class) {
				// 2nd check current Own instance
				if (ownInst == null) {
					// New own class at once
					ownInst = new WebBrowserFactory(targetURL);
				}
			}
		}
		// return own instance
		return (ownInst);
	}

	static public WebBrowserFactory getInstance() {
		// return own instance
		// TODO need to create instance if onwInst is null
		return (ownInst);
	}

	/**
	 * @category Getter method : Get Instance of Web Browser
	 */
	public IWebBrowserACTF getInstWebBrowser() {
		// return instance of Web Browser
		return (browserPreview);
	}

	/**
	 * Dummy ********************************************************* /**
	 * 
	 * @category Getter method : Get current URL from navigator
	 */
	public String getUrlNavigaor() {
		// return current URL from navigator
		return (browserPreview.getURL());
	}

	/**
	 * @category Setter method : Set URL to navigator
	 */
	public void setUrlNavigaor(String nextUrl) {
		try {
			// return current URL from navigator
			browserPreview.navigate(nextUrl);
		} catch (Exception e) {
			System.out.println("setUrlNavigaor() : Exception = " + e);
		}
	}

	/**
	 * Dummy *********************************************************
	 * 
	 * 
	 * /**
	 * 
	 * @override
	 */
	public void dispose() {
		WebBrowserEventUtil.browserDisposed(browserPreview, getPartName());
	}

	public void setFocus() {
		// TODO
		super.setFocus();
	}

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		// TODO
		super.init(site, input);
	}

	public IModelService getModelService() {
		return (this.browserPreview);
	}

	public IEditorPart getEditorPart() {
		// TODO Auto-generated method stub
		return (this);
	}

	public void setEditorTitle(String title) {
		// TODO Auto-generated method stub
		setPartName(title);
	}

	private Node curRoot = null;
	private INodeExVideo[] videos = new INodeExVideo[0];
	private boolean checkFlag = false;
	private int counter = 0;

	public void mediaSearchRequest() {
		checkFlag = true;
	}

	private void checkVideo() {
		// TODO support diff (dombycom)
		// Node root = browserPreview.getLiveDocument().getDocumentElement();
		// if(curRoot != root){
		// searchVideo();
		// }

		if (checkFlag) {
			searchVideo();
		} else if (videos.length == 0) {
			counter++;
			if (counter >= 50) {
				searchVideo();
			}
		}
	}

	public void searchVideo() {
		AnalyzedResult analyzedResult = new AnalyzedResult();
		curRoot = browserPreview.getLiveDocument().getDocumentElement();
		if (curRoot instanceof INodeEx) {
			analyzedResult = ((INodeEx) curRoot).analyze(analyzedResult);
		}
		videos = analyzedResult.getVideoNodes();
		checkFlag = false;
		counter = 0;
	}

	public boolean pauseMedia() {
		searchVideo();
		boolean r = true;
		for (int i = 0; i < videos.length; i++) {
			r &= videos[i].pauseMedia();
		}
		return r;
	}

	public boolean playMedia() {
		searchVideo();
		boolean r = true;
		for (int i = 0; i < videos.length; i++) {
			r &= videos[i].playMedia();
		}
		return r;
	}

	public boolean rewindMedia() {
		searchVideo();
		boolean r = true;
		for (int i = 0; i < videos.length; i++) {
			// r &= videos[i].fastReverse();
			r &= videos[i].stopMedia();
		}
		return r;
	}

	public int getCurrentPosition() {
		checkVideo();// TODO cache
		if (videos.length > 0) {
			int localTime = 0;
			for (int i = 0; i < videos.length; i++) {
				double realTime = videos[i].getCurrentPosition();
				localTime = (int) (realTime * SEC2MSEC);
			}
			return (localTime);
		} else {
			return (0);
		}
	}

	public int getTotalLength() {

		/**
		 * Pending *************************************** analyze(); // TODO
		 * cache INodeExVideo[] videos = analyzedResult.getVideoNodes();
		 * if(videos.length > 0){ int localTime = 0; for(int i = 0; i <
		 * videos.length; i++){ double realTime = videos[0].getTotalLength();
		 * localTime = (int)(realTime * SEC2MSEC); } return(localTime); } else {
		 * return(0); } Pending
		 ***************************************/

		// ** Dummy **** Movie time (EndTime) ********************
		// TODO cache
		checkVideo();
		if (videos.length > 0) {
			return (600000);
		} else {
			return (0);
		}
		// ** Dummy **** max value(EndTime) = 10min ********************

	}

	public int getVideoStatus() {
		checkVideo();// TODO cache
		if (videos.length > 0) {
			int currentStatus = 0;
			for (int i = 0; i < videos.length; i++) {
				// PickUP current status
				VideoState vs = videos[i].getCurrentState();
				// exchange data type
				switch (vs) {
				case STATE_PLAY:
				case STATE_FASTFORWARD:
				case STATE_FASTREVERSE:
					currentStatus = 1;
					break;
				case STATE_STOP:
					currentStatus = 2;
					break;
				case STATE_PAUSE:
				case STATE_WAITING:
					currentStatus = 3;
					break;
				default:
					break;
				}
			}
			// return current Video status
			return (currentStatus);
		} else {
			// unknown(or No including media)
			return (-1);
		}
	}

}
