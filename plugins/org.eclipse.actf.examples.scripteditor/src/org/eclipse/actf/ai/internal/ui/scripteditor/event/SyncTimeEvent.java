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

import java.util.EventObject;

/**
 *
 */
public class SyncTimeEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5354476659198633807L;

	public static final int SYNCHRONIZE_TIME_LINE = 0;
	public static final int REFRESH_TIME_LINE = 1;
	public static final int ADJUST_TIME_LINE = 2;
	private int currentTime;
	private int eventType;

	/**
	 * @category Constructor
	 * 
	 *           for the synchronize time event.
	 * 
	 * @param currentTime
	 *            current time of the movie
	 * @param source
	 *            event source
	 */
	public SyncTimeEvent(int currentTime, Object source) {
		super(source);
		this.eventType = SYNCHRONIZE_TIME_LINE;
		this.currentTime = currentTime;
	}

	/**
	 * @category Constructor
	 * 
	 * @param type
	 *            event type
	 * @param currentTime
	 *            current movie time
	 * @param source
	 *            event source
	 */
	public SyncTimeEvent(int type, int currentTime, Object source) {
		super(source);
		this.eventType = type;
		this.currentTime = currentTime;
	}

	/**
	 * @category getter
	 * 
	 * @return current movie time
	 */
	public int getCurrentTime() {
		return currentTime;
	}

	/**
	 * @return type of event
	 */
	public int getEventType() {
		return eventType;
	}

}
