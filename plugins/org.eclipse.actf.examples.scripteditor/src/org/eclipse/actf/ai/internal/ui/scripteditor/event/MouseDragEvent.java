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

public class MouseDragEvent extends EventObject {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -264642209345581317L;
	
	public static final int MOUSE_DRAG_START = 0;
	public static final int MOUSE_DRAGGING = 1;
	public static final int MOUSE_DRAG_END = 2;
	public static final int MOUSE_SET_DRAG_STATUS = 3;
	private boolean status;
	private int eventType;


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
	public MouseDragEvent(int type, Object source) {
		super(source);
		this.eventType = type;
		if(type == MOUSE_DRAG_START) {
			status = true;
		} else if (type == MOUSE_DRAG_END){
			status = false;
		}
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
	public MouseDragEvent(int type, boolean status, Object source) {
		super(source);
		this.eventType = type;
		this.status = status;
	}

	/**
	 * @category getter
	 * 
	 * @return current status
	 */
	public boolean isStatus() {
		return status;
	}


	/**
	 * @category getter
	 * @return type of event
	 */
	public int getEventType() {
		return eventType;
	}

}
