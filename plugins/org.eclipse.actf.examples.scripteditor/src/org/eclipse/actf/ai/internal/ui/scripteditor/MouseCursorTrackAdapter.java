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
package org.eclipse.actf.ai.internal.ui.scripteditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Control;

public class MouseCursorTrackAdapter extends MouseTrackAdapter {
	public void mouseEnter(MouseEvent e) {
		// Cursor image (from ARROW to HAND)
		Control target = (Control) e.getSource();
		target.setCursor(new Cursor(null, SWT.CURSOR_HAND));
	}

	public void mouseExit(MouseEvent e) {
		// Reset Cursor image
		Control parentButton = (Control) e.getSource();
		parentButton.setCursor(new Cursor(null, SWT.CURSOR_ARROW));
	}
}
