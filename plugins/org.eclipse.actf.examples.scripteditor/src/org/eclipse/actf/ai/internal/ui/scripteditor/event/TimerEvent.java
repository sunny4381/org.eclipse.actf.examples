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
public class TimerEvent extends EventObject {

	private static final long serialVersionUID = 6939507005105789334L;

	/**
	 * @category Constructor
	 * 
	 *           for the timer event.
	 * 
	 * @param source
	 *            event source
	 */
	public TimerEvent(Object source) {
		super(source);
	}

}
