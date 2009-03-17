/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.examples.simplevisualizer.ui.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;

public class AlphaValueToolbar extends Composite {

	private Slider alphaSlider;

	public AlphaValueToolbar(Composite parent, int style) {
		super(parent, style);
		initLayout(parent);
	}

	private void initLayout(Composite parent) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginTop = 4;
		gridLayout.marginBottom = 4;
		gridLayout.marginHeight = gridLayout.marginWidth = 1;
		gridLayout.numColumns = 5;
		setLayout(gridLayout);

		GridData gridData = new GridData();
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		setLayoutData(gridData);

		new Label(this, SWT.NONE).setText("alpha value:");

		final Label valueL = new Label(this, SWT.CENTER);
		valueL.setText("127"); //$NON-NLS-1$

		new Label(this, SWT.NONE).setText("     0"); //$NON-NLS-1$
		alphaSlider = new Slider(this, SWT.CENTER);
		alphaSlider.setValues(128, 0, 275, 20, 1, 25);
		alphaSlider.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				valueL.setText(Integer.toString(alphaSlider.getSelection()));
			}
		});

		gridData = new GridData();
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		alphaSlider.setLayoutData(gridData);

		new Label(this, SWT.NONE).setText("255"); //$NON-NLS-1$

	}

	protected int getAlpha() {
		return alphaSlider.getSelection();
	}

}
