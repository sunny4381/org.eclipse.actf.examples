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
package org.eclipse.actf.ai.internal.ui.scripteditor.event;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.actf.ai.internal.ui.scripteditor.ScriptEditorTimerUtil;

/**
 *
 */
public class EventManager {
	private static EventManager ownInst = null;

	/**
	 * Get instance of EventManager
	 * 
	 * @return instance
	 */
	public static synchronized EventManager getInstance() {
		if (ownInst == null) {
			ownInst = new EventManager();
		}
		return ownInst;
	}

	private EventManager() {
		ScriptEditorTimerUtil.getInstance();
	}

	/**
	 * Synchronize Timer Event Listeners
	 */
	private static List<SyncTimeEventListener> SyncListeners = new ArrayList<SyncTimeEventListener>();

	/**
	 * Add Synchronize Timer Event Listener
	 * 
	 * @param listener
	 */
	public synchronized void addSyncTimeEventListener(
			SyncTimeEventListener listener) {
		SyncListeners.add(listener);
	}

	/**
	 * Remove Synchronize Timer Event Listener
	 * 
	 * @param listener
	 */
	public synchronized void removeSyncTimeEventListener(
			SyncTimeEventListener listener) {
		SyncListeners.remove(listener);
	}

	/**
	 * fire Synchronize Timer Event
	 * 
	 * @param event
	 */
	public synchronized void fireSyncTimeEvent(SyncTimeEvent event) {
		for (SyncTimeEventListener listener : SyncListeners) {
			listener.handleSyncTimeEvent(event);
			// System.out.println(listener);
		}
		// System.out.println("----------");
	}

	// ----------------- Mouse Drag Event -----------------------
	/**
	 * Mouse Drag Event Listeners
	 */
	private static List<MouseDragEventListener> DragListeners = new ArrayList<MouseDragEventListener>();

	/**
	 * Add Mouse Drag Event Listener
	 * 
	 * @param listener
	 */
	public synchronized void addMouseDragEventListener(
			MouseDragEventListener listener) {
		DragListeners.add(listener);
	}

	/**
	 * Remove Mouse Drag Event Listener
	 * 
	 * @param listener
	 */
	public synchronized void removeMouseDragEventListener(
			MouseDragEventListener listener) {
		DragListeners.remove(listener);
	}

	/**
	 * fire Mouse Drag Event
	 * 
	 * @param event
	 */
	public synchronized void fireMouseDragEvent(MouseDragEvent event) {
		for (MouseDragEventListener listener : DragListeners) {
			listener.handleMouseDragEvent(event);
		}
	}

	// ----------------- Play Pause Event -----------------------
	/**
	 * Player Control Event Listeners
	 */
	private static List<PlayerControlEventListener> PlayerControlEventListeners = new ArrayList<PlayerControlEventListener>();

	/**
	 * Add Player Control Event Listener
	 * 
	 * @param listener
	 */
	public synchronized void addPlayerControlEvenListener(
			PlayerControlEventListener listener) {
		PlayerControlEventListeners.add(listener);
	}

	/**
	 * Remove Player Control Event Listener
	 * 
	 * @param listener
	 */
	public synchronized void removePlayerControlEventListener(
			PlayerControlEventListener listener) {
		PlayerControlEventListeners.remove(listener);
	}

	/**
	 * fire Player Control Event
	 * 
	 * @param event
	 */
	public synchronized void firePlayerControlEvent(PlayerControlEvent event) {
		for (PlayerControlEventListener listener : PlayerControlEventListeners) {
			listener.handlePlayPauseEvent(event);
		}
	}

	// ----------------- Timer Event -----------------------
	/**
	 * Time Line Event Listeners
	 */
	private static List<TimerEventListener> TimerEventListeners = new ArrayList<TimerEventListener>();

	/**
	 * Add Time Line Event Listener
	 * 
	 * @param listener
	 */
	public synchronized void addTimerEventListener(TimerEventListener listener) {
		TimerEventListeners.add(listener);
	}

	/**
	 * Remove Time Line Event Listener
	 * 
	 * @param listener
	 */
	public synchronized void removeTimerEventListener(
			TimerEventListener listener) {
		TimerEventListeners.remove(listener);
	}

	/**
	 * fire Timer Event
	 * 
	 * @param event
	 */
	public synchronized void fireTimerEvent(TimerEvent event) {
		for (TimerEventListener listener : TimerEventListeners) {
			listener.handleTimerUtilEvent(event);
		}
	}

}
