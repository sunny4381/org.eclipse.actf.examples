/*******************************************************************************
 * Copyright (c) 2009, 2010 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.ai.ui.scripteditor.views;

import org.eclipse.actf.ai.internal.ui.scripteditor.ScriptListTable;
import org.eclipse.actf.ai.scripteditor.data.ScriptData;
import org.eclipse.actf.ai.scripteditor.util.ScriptFileDropListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class ScriptListView extends ViewPart {
	public static final String VIEW_ID = "org.eclipse.actf.examples.scripteditor.ScriptListView";

	/**
	 * Local data
	 */
	Composite ownComposite;

	// Own instance
	static private ScriptListView ownInst = null;

	// instance of ScriptData
	private ScriptData instScriptData = null;
	private ScriptListTable instScriptList;

	// Script List Part
	private TableColumn tableColumnStartTime;
	private TableViewer tableScriptTableViewer;
	private Table tableScriptTable;

	/**
	 * Constructor
	 */
	public ScriptListView() {
		super();

		// store own instance
		ownInst = this;
	}

	static public ScriptListView getInstance() {
		// return own instance
		return (ownInst);
	}

	public ScriptListTable getInstScriptList() {
		// return current instance of ScriptList Table
		return (instScriptList);
	}

	/**
	 * @Override
	 */
	public void createPartControl(Composite parent) {
		// Create own instance of Composite
		ownComposite = new Composite(parent, SWT.NONE);

		// Create Data class
		instScriptData = ScriptData.getInstance();
		// Create class of ScriptList Table
		instScriptList = new ScriptListTable(ownComposite);

		// Get current Display
		IWorkbench workbench = PlatformUI.getWorkbench();
		Display display = workbench.getDisplay();

		// Initialize application's GUI
		initGUI(display);

		// Add listener for load meta file
		initDDListener(ownComposite);
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
	}

	/**
	 * Cleans up all resources created by this ViewPart.
	 */
	public void dispose() {
		super.dispose();
	}

	/**
	 * Initialize Screen
	 */
	private void initGUI(Display parentDisp) {

		try {
			// Layout : "ScriptList" view
			FormLayout viewScriptListLayout = new FormLayout();
			ownComposite.setLayout(viewScriptListLayout);
			FormData viewScriptListLData = new FormData();
			viewScriptListLData.width = 364;
			viewScriptListLData.height = 611; // 651
			viewScriptListLData.top = new FormAttachment(0, 1000, 25);
			viewScriptListLData.left = new FormAttachment(0, 1000, 566);
			viewScriptListLData.right = new FormAttachment(993, 1000, 0);
			viewScriptListLData.bottom = new FormAttachment(1000, 1000, -196);
			ownComposite.setLayoutData(viewScriptListLData);

			// Update View window
			ownComposite.layout();
			ownComposite.pack();

		} catch (Exception e) {
			System.out.println("ScriptListView() : Exception = " + e);
		}
	}

	/**
	 * Initialize Drop&Drop Listener
	 */
	private void initDDListener(Composite parent) {
		// Initial setup DnD target control
		DropTarget targetDnD = new DropTarget(parent, DND.DROP_DEFAULT
				| DND.DROP_COPY);
		targetDnD.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		targetDnD.addDropListener(new ScriptFileDropListener());
	}

}
