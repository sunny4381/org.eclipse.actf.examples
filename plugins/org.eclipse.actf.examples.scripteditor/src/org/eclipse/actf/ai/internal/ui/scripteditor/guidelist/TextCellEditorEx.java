/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.ai.internal.ui.scripteditor.guidelist;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class TextCellEditorEx extends TextCellEditor {

	Composite parent;

	public TextCellEditorEx(Composite parent) {
		super(parent, SWT.NONE);
	}

	public void fireApplyEditorValue() {
		super.fireApplyEditorValue();
	}

	protected Control createControl(Composite parent) {
		text = new Text(parent, getStyle());
		text.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				handleDefaultSelection(e);
			}
		});
		text.addKeyListener(new KeyAdapter() {
			// hook key pressed - see PR 14201
			public void keyPressed(KeyEvent e) {
				keyReleaseOccured(e);

				if ((getControl() == null) || getControl().isDisposed()) {
					return;
				}
			}
		});
		text.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE
						|| e.detail == SWT.TRAVERSE_RETURN) {
					e.doit = false;
				}
			}
		});
		text.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
			}
		});

		text.setFont(parent.getFont());
		text.setBackground(parent.getBackground());
		return text;
	}

	protected void focusLost() {
		super.focusLost();
	}
}
