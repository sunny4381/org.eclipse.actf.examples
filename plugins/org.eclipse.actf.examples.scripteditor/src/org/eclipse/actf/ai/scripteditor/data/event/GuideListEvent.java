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

import java.util.EventObject;

import org.eclipse.actf.ai.scripteditor.data.IScriptData;

public class GuideListEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1860351762555451972L;

	public static final int CLEAR_DATA = 0;
	public static final int SET_DATA = 1;
	public static final int DESELECT_DATA = 2;
	// public static final int CLEAR_LABEL = 3;
	public static final int PLAY_LABEL = 4;

	// Migrate from AudioEvent
	public static final int ADD_DATA = 5;
	public static final int REPALCE_DATA = 6;
	public static final int DELETE_DATA = 7;

	private int eventType;
	private IScriptData data;

	public GuideListEvent(int eventType, IScriptData data, Object source) {
		super(source);
		this.eventType = eventType;
		this.data = data;
	}

	public int getEventType() {
		return eventType;
	}

	public IScriptData getData() {
		return data;
	}

}
