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

import java.util.EventListener;

public interface GuideListEventListener extends EventListener {
	public abstract void handleGuideListEvent(GuideListEvent e);
}
