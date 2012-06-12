/*******************************************************************************
 * Copyright (c) 2009, 2012 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.ai.scripteditor.util;

import java.util.HashMap;

import org.eclipse.actf.ai.internal.ui.scripteditor.event.EventManager;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.SyncTimeEvent;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.SyncTimeEventListener;
import org.eclipse.actf.model.dom.dombycom.AnalyzedResult;
import org.eclipse.actf.model.dom.dombycom.INodeEx;
import org.eclipse.actf.model.dom.dombycom.INodeExVideo;
import org.eclipse.actf.model.dom.dombycom.INodeExVideo.VideoState;
import org.eclipse.actf.model.ui.IModelService;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserACTF;
import org.eclipse.actf.model.ui.editor.browser.WebBrowserEventUtil;
import org.eclipse.actf.model.ui.editors.ie.WebBrowserEditor;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.w3c.dom.Node;

/**
 * 
 * @category Factory class for FlashPlayer
 * 
 */
public class WebBrowserFactory extends WebBrowserEditor implements
		SyncTimeEventListener {

	// Media sample unit time(100msec) to Local unit time(1msec)
	private static final int SEC2MSEC = 1000;

	// Local data
	private static WebBrowserFactory ownInst = null;

	// for Browser (dummy)
	private IWebBrowserACTF curBrowser = null;
	private MediaInfo curMediaInfo = null;

	private static EventManager eventManager = null;

	private class MediaInfo {
		Node curRoot = null;
		INodeExVideo[] videos = new INodeExVideo[0];
		boolean checkFlag = false;
		int counter = 0;
	};

	private HashMap<IWebBrowserACTF, MediaInfo> mediaMap = new HashMap<IWebBrowserACTF, WebBrowserFactory.MediaInfo>();

	public static void navigate(String url) {
		IModelService model = ModelServiceUtils.getActiveModelService();
		if (model instanceof IWebBrowserACTF) {
			((IWebBrowserACTF) model).navigate(url);
		} else {
			ModelServiceUtils.launch(url, WebBrowserEditor.ID);
		}
	}

	/**
	 * Creates a new MediaController.
	 */
	private WebBrowserFactory() {
		eventManager = EventManager.getInstance();
		eventManager.addSyncTimeEventListener(this);
	}

	public static WebBrowserFactory getInstance() {
		if (ownInst == null) {
			ownInst = new WebBrowserFactory();
		}
		return (ownInst);
	}

	public void setCurrentWebBrowser(IWebBrowserACTF webBrowser) {
		curBrowser = webBrowser;
		curMediaInfo = getMediaInfo(webBrowser);
	}

	public void removeWebBrowser(IWebBrowserACTF webBrowser) {
		mediaMap.remove(webBrowser);
		curBrowser = null;
		curMediaInfo = null;
	}

	public void mediaSearchRequest(IWebBrowserACTF webBrowser) {
		getMediaInfo(webBrowser).checkFlag = true;
	}

	private MediaInfo getMediaInfo(IWebBrowserACTF webBrowser) {
		if (mediaMap.containsKey(webBrowser)) {
			return mediaMap.get(webBrowser);
		}

		MediaInfo tmp = new MediaInfo();
		mediaMap.put(webBrowser, tmp);
		return tmp;
	}

	/*
	 * @override
	 */
	public void dispose() {
		if (curBrowser == null) {
			WebBrowserEventUtil.browserDisposed(curBrowser, getPartName());
			// TODO need to confirm
		}
		eventManager.removeSyncTimeEventListener(this);
	}

	private void checkVideo() {
		// TODO support diff (dombycom)

		if (curBrowser == null) {
			IModelService model = ModelServiceUtils.getActiveModelService();
			if (model instanceof IWebBrowserACTF) {
				setCurrentWebBrowser((IWebBrowserACTF) model);
			} else {
				return;
			}
		}

		if (curMediaInfo.checkFlag) {
			searchVideo();
		} else if (curMediaInfo.videos.length == 0) {
			curMediaInfo.counter++;
			if (curMediaInfo.counter >= 50) {
				curMediaInfo.checkFlag = true;
				searchVideo();
			}
		}
	}

	public void searchVideo() {
		// for cache
		// checkFlag becomes true when mediaSearchRequest() is called.
		if (curBrowser == null) {
			IModelService model = ModelServiceUtils.getActiveModelService();
			if (model instanceof IWebBrowserACTF) {
				setCurrentWebBrowser((IWebBrowserACTF) model);
			} else {
				return;
			}
		}

		if (curMediaInfo.checkFlag == false) {
			return;
		}

		AnalyzedResult analyzedResult = new AnalyzedResult();
		curMediaInfo.curRoot = curBrowser.getLiveDocument()
				.getDocumentElement();
		if (curMediaInfo.curRoot instanceof INodeEx) {
			analyzedResult = ((INodeEx) curMediaInfo.curRoot)
					.analyze(analyzedResult);
		}
		curMediaInfo.videos = analyzedResult.getVideoNodes();
		curMediaInfo.checkFlag = false;
		curMediaInfo.counter = 0;
	}

	public boolean pauseMedia() {
		searchVideo();
		boolean r = true;
		if (curMediaInfo == null) {
			return r;
		}
		for (int i = 0; i < curMediaInfo.videos.length; i++) {
			r &= curMediaInfo.videos[i].pauseMedia();
		}
		return r;
	}

	public boolean playMedia() {
		searchVideo();
		boolean r = true;
		if (curMediaInfo == null) {
			return r;
		}
		for (int i = 0; i < curMediaInfo.videos.length; i++) {
			r &= curMediaInfo.videos[i].playMedia();
		}
		return r;
	}

	public boolean rewindMedia() {
		searchVideo();
		boolean r = true;
		if (curMediaInfo == null) {
			return r;
		}
		for (int i = 0; i < curMediaInfo.videos.length; i++) {
			// r &= videos[i].fastReverse();
			r &= curMediaInfo.videos[i].stopMedia();
		}
		return r;
	}

	public int getCurrentPosition() {
		checkVideo();// TODO cache
		if (curMediaInfo != null && curMediaInfo.videos.length > 0) {
			int localTime = 0;
			for (int i = 0; i < curMediaInfo.videos.length; i++) {
				double realTime = curMediaInfo.videos[i].getCurrentPosition();
				localTime = (int) (realTime * SEC2MSEC);
			}
			return (localTime);
		} else {
			return (0);
		}
	}

	public boolean setCurrentPosition(int pos) {
		checkVideo();
		boolean result = true;
		if (curMediaInfo != null && curMediaInfo.videos.length > 0) {
			double readTime = pos / SEC2MSEC;
			for (int i = 0; i < curMediaInfo.videos.length; i++) {
				result = curMediaInfo.videos[i].setCurrentPosition(readTime)
						& result;
			}
			return result;
		} else {
			return false;
		}
	}

	public void showCurrentImage() {
		int status = getVideoStatus();
		if (status == 2 || status == 3) {
			playMedia();
			pauseMedia();
		}
	}

	public String getVideoURL() {
		// TODO cache
		checkVideo();
		if (curMediaInfo != null && curMediaInfo.videos.length > 0) {
			// TODO
			return curMediaInfo.videos[0].getVideoURL();
		}
		return null;
	}

	public int getTotalLength() {

		// TODO cache
		checkVideo();
		if (curMediaInfo != null && curMediaInfo.videos.length > 0) {
			double max = 0;
			double tmp = 0;
			for (int i = 0; i < curMediaInfo.videos.length; i++) {
				tmp = curMediaInfo.videos[i].getTotalLength();
				if (tmp > max) {
					max = tmp;
				}
				// System.out.println(curMediaInfo.videos[i].getVideoURL());
			}
			int length = (int) Math.ceil(max * 1000);

			// TODO
			if (length == 0) {
				length = 600000;
			}
			return (length);
		} else {
			return (0);
		}

	}

	public int getVideoStatus() {
		checkVideo();// TODO cache
		if (curMediaInfo != null && curMediaInfo.videos.length > 0) {
			int currentStatus = 0;
			for (int i = 0; i < curMediaInfo.videos.length; i++) {
				// PickUP current status
				VideoState vs = curMediaInfo.videos[i].getCurrentState();
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

	public void handleSyncTimeEvent(SyncTimeEvent e) {
		// Synchronize TimeLine view
		if (e.getEventType() == SyncTimeEvent.ADJUST_TIME_LINE) {
			boolean result = setCurrentPosition(e.getCurrentTime());
			if (result == false) {
				System.out
						.println("WebBrowserFactory.setCurrentPosition method error");
			}
			playMedia();
			try {
				Thread.sleep(1);
			} catch (Exception ee) {
			}
			pauseMedia();
		}
	}

}
