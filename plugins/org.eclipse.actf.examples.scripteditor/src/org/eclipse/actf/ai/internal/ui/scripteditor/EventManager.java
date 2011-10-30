/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.ai.internal.ui.scripteditor;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class EventManager {
	private static EventManager ownInst = null;

	/**
	 * @category Get instance of EventManager
	 * @return instance
	 */
	public static EventManager getInstance() {
		if (ownInst == null) {
			ownInst = new EventManager();
		}
		return ownInst;
	}

	// Synchronize Timer Event Listeners
	private static List<SyncTimeEventListener> SyncListeners = new ArrayList<SyncTimeEventListener>();

	/**
	 * @category Add Synchronize Timer Event Listener
	 * @param listener
	 */
	public synchronized void addSyncTimeEventListener(
			SyncTimeEventListener listener) {
		SyncListeners.add(listener);
	}

	/**
	 * @category Remove Synchronize Timer Event Listener
	 * @param listener
	 */
	public synchronized void removeSyncTimeEventListener(
			SyncTimeEventListener listener) {
		SyncListeners.remove(listener);
	}

	/**
	 * @category clear all Synchronize Timer Event Listeners
	 */
	public synchronized void removeAllSyncTimeEventListener() {
		SyncListeners.clear();
	}

	/**
	 * @category fire Synchronize Timer Event
	 * @param event
	 */
	public synchronized void fireSyncTimeEvent(SyncTimeEvent event) {
		for (SyncTimeEventListener listener : SyncListeners) {
			listener.handleSyncTimeEvent(event);
			// System.out.println(listener);
		}
		// System.out.println("----------");
	}

}
