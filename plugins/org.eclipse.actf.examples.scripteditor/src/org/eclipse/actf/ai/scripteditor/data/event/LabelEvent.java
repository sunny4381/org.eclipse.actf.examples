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

public class LabelEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8216230604599825989L;

	public static final int ADD_PLAY_MARK = 0; //
	public static final int PUT_ALL_LABEL = 1; // create or
	public static final int PUT_LABEL = 2; // create or update label
	//
	public static final int DELETE_LABEL = 3;
	public static final int DELETE_PLAY_MARK = 4;
	public static final int DELETE_MARK = 5;
	public static final int CLEAR_LABEL = 6;

	private int eventType;
	private IScriptData data;

	public LabelEvent(int eventType, IScriptData data, Object source) {
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
