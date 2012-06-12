/*******************************************************************************
 * Copyright (c) 2010, 2012 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.ai.internal.ui.scripteditor;

import java.text.MessageFormat;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class ComboBoxCellEditorEx extends ComboBoxCellEditor {

	public static String[] COMBO_ITEM;
	public static boolean updateFlag = false;
	CCombo comboBox = null;
	int selection = 0;

	public ComboBoxCellEditorEx(Composite parent, String[] items) {
		super(parent, items, SWT.NONE);
		COMBO_ITEM = items;
	}

	public void setItems(String[] items) {
		super.setItems(items);
		COMBO_ITEM = items;
		comboBox = (CCombo) getControl();
		if (comboBox != null && items != null) {
			comboBox.removeAll();
			for (int i = 0; i < items.length; i++) {
				comboBox.add(items[i], i);
			}

			setValueValid(true);
			selection = 0;
		}

	}

	protected Control createControl(Composite parent) {
		comboBox = new CCombo(parent, getStyle());
		comboBox.setFont(parent.getFont());

		comboBox.addKeyListener(new KeyAdapter() {
			// hook key pressed - see PR 14201
			public void keyPressed(KeyEvent e) {
				keyReleaseOccured(e);
			}
		});

		comboBox.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent event) {
				// applyEditorValueAndDeactivate();
			}

			public void widgetSelected(SelectionEvent event) {
				selection = comboBox.getSelectionIndex();
			}
		});

		comboBox.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE
						|| e.detail == SWT.TRAVERSE_RETURN) {
					e.doit = false;
					ComboBoxCellEditorEx.this.focusLost();
				}
			}
		});

		comboBox.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				ComboBoxCellEditorEx.this.focusLost();
			}
		});
		return comboBox;

	}

	protected void doSetFocus() {
		if (comboBox == null) {
			comboBox = (CCombo) getControl();
		}
		comboBox.setFocus();
	}

	protected Object doGetValue() {
		if (comboBox == null) {
			comboBox = (CCombo) getControl();
		}
		String[] oldItems = comboBox.getItems();

		// new value
		if (!(comboBox.getText() == null || comboBox.getText().length() == 0)
				&& isNewKey(comboBox.getText(), oldItems)) {
			String[] newItems = new String[oldItems.length + 1];
			for (int i = 0; i < oldItems.length; i++) {
				newItems[i] = oldItems[i];
			}
			// add into last position
			newItems[oldItems.length] = comboBox.getText();
			// add into combo box
			setItems(newItems);
			COMBO_ITEM = newItems;

			// select new item
			comboBox.select(oldItems.length);

			updateFlag = true;
			return new Integer(COMBO_ITEM.length);
		} else if (updateFlag) {
		} else {
			updateFlag = false;
		}
		return comboBox.getSelectionIndex();
	}

	protected void doSetValue(Object value) {
		if (comboBox == null) {
			comboBox = (CCombo) getControl();
		}
		int selection = ((Integer) value).intValue();
		String[] oldItems = comboBox.getItems();
		if (!(comboBox.getText() == null || comboBox.getText().length() == 0)
				&& isNewKey(comboBox.getText(), oldItems)) {
			comboBox.select(oldItems.length);
		} else {
			comboBox.select(selection);

		}
	}

	// TODO check
	void applyEditorValueAndDeactivate() {
		// must set the selection before getting value
		if (comboBox == null) {
			comboBox = (CCombo) getControl();
		}

		selection = comboBox.getSelectionIndex();
		Object newValue = doGetValue();
		markDirty();
		boolean isValid = isCorrect(newValue);
		setValueValid(isValid);

		if (!isValid) {
			if (COMBO_ITEM.length > 0 && selection >= 0
					&& selection < COMBO_ITEM.length) {
				setErrorMessage(MessageFormat.format(getErrorMessage(),
						new Object[] { COMBO_ITEM[selection] }));
			} else {
				setErrorMessage(MessageFormat.format(getErrorMessage(),
						new Object[] { comboBox.getText() }));
			}
		}

		fireApplyEditorValue();
		deactivate();
	}

	protected void focusLost() {
		// TODO marge with doGetValue()
		if (comboBox == null) {
			comboBox = (CCombo) getControl();
		}
		String[] oldItems = comboBox.getItems();

		// new value
		if (!(comboBox.getText() == null || comboBox.getText().length() == 0)
				&& isNewKey(comboBox.getText(), oldItems)) {
			String[] newItems = new String[oldItems.length + 1];
			for (int i = 0; i < oldItems.length; i++) {
				newItems[i] = oldItems[i];
			}
			// add into last position
			newItems[oldItems.length] = comboBox.getText();
			// add into combo box
			setItems(newItems);
			COMBO_ITEM = newItems;

			// select new item
			comboBox.select(oldItems.length);

			updateFlag = true;
		} else if (updateFlag) {
		} else {
			updateFlag = false;
		}

		if (isActivated()) {
			applyEditorValueAndDeactivate();
		}
		// super.focusLost();
	}

	/**
	 * 
	 * @param key
	 * @param oldItems
	 * @return
	 */
	private boolean isNewKey(String key, String[] oldItems) {
		for (int i = 0; i < oldItems.length; i++) {
			if (oldItems[i].equals(key)) {
				return false;
			}
		}
		return true;
	}
}