/*******************************************************************************
 * Copyright (c) 2011, 2012 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.ai.scripteditor.data.event;

import java.util.ArrayList;
import java.util.List;

public class DataEventManager {
	private static DataEventManager ownInst = null;

	/**
	 * Get instance of EventManager
	 * 
	 * @return instance
	 */
	public static DataEventManager getInstance() {
		if (ownInst == null) {
			ownInst = new DataEventManager();
		}
		return ownInst;
	}

	// ----------------- AUDIO Event -----------------------
	/**
	 * Play Mark Event Listeners
	 */
	private static List<LabelEventListener> labelEventListeners = new ArrayList<LabelEventListener>();

	/**
	 * Add Play Mark Event Listener
	 * 
	 * @param listener
	 */
	public synchronized void addLabelEventListener(LabelEventListener listener) {
		labelEventListeners.add(listener);
	}

	/**
	 * Remove Play Mark Event Listener
	 * 
	 * @param listener
	 */
	public synchronized void removeLabelEventListener(
			LabelEventListener listener) {
		labelEventListeners.remove(listener);
	}

	/**
	 * fire Play Mark Event
	 * 
	 * @param event
	 */
	public synchronized void fireLabelEvent(LabelEvent event) {
		for (LabelEventListener listener : labelEventListeners) {
			listener.handleLabelEvent(event);
			// System.out.println(listener);
		}
		// System.out.println("----------");
	}

	// ----------------- GuideList Event -----------------------
	/**
	 * Play Mark Event Listeners
	 */
	private static List<GuideListEventListener> iScriptEventListener = new ArrayList<GuideListEventListener>();

	/**
	 * Add Play Mark Event Listener
	 * 
	 * @param listener
	 */
	public synchronized void addGuideListEventListener(
			GuideListEventListener listener) {
		iScriptEventListener.add(listener);
	}

	/**
	 * Remove Play Mark Event Listener
	 * 
	 * @param listener
	 */
	public synchronized void removeGuideListEventListener(
			GuideListEventListener listener) {
		iScriptEventListener.remove(listener);
	}

	/**
	 * fire Play Mark Event
	 * 
	 * @param event
	 */
	public synchronized void fireGuideListEvent(GuideListEvent event) {
		for (GuideListEventListener listener : iScriptEventListener) {
			listener.handleGuideListEvent(event);
		}
	}
}
