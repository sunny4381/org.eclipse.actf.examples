/*******************************************************************************
 * Copyright (c) 2009, 2011 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.ai.ui.scripteditor.views;

import org.eclipse.actf.ai.internal.ui.scripteditor.EditPanelTab;
import org.eclipse.actf.ai.internal.ui.scripteditor.SelectWAVFileTab;
import org.eclipse.actf.ai.scripteditor.util.ScriptFileDropListener;
import org.eclipse.actf.examples.scripteditor.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class EditPanelView extends ViewPart implements IUNIT {
	public static final String VIEW_ID = "org.eclipse.actf.examples.scripteditor.EditPanelView";

	/**
	 * Local data
	 */
	// Own class instance
	static private EditPanelView ownInst = null;
	private CTabFolder ownTabFolder;

	// Child class instance
	private EditPanelTab instEditPanelTab = null;
	private SelectWAVFileTab instSelectWAVFileTab = null;

	// Tab Item instance
	private CTabItem instEditPanelTabItem = null;
	private CTabItem instSelectWAVFileTabItem = null;

	/**
     * 
     */
	public EditPanelView() {
		super();

		// store own instance
		ownInst = this;
	}

	static public EditPanelView getInstance() {
		// return current own instance
		return (ownInst);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createPartControl(Composite parent) {
		// Create own instance of Composite
		ownTabFolder = new CTabFolder(parent, SWT.NONE);

		// Initialize application's GUI
		initTabFolder();

		// Add listener for load meta file
		initDDListener(ownTabFolder);
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		ownTabFolder.setFocus();
	}

	/**
	 * Initialize Screen
	 */
	private void initTabFolder() {

		try {
			// current display
			Display display = PlatformUI.getWorkbench().getDisplay();

			// **<Edit panel>***********************************
			FormLayout editPanelLayout = new FormLayout();
			ownTabFolder.setLayout(editPanelLayout);
			FormData editPanelLData = new FormData(925, 128);
			editPanelLData.top = new FormAttachment(0, 1000, 609);
			editPanelLData.left = new FormAttachment(0, 1000, 5);
			editPanelLData.right = new FormAttachment(1000, 1000, -7);
			editPanelLData.bottom = new FormAttachment(1000, 1000, -31);
			ownTabFolder.setLayoutData(editPanelLData);
			// otherwise setting
			ownTabFolder.setSimple(false);
			ownTabFolder.setMaximizeVisible(false);// do not display maximize
													// button
			ownTabFolder.setMinimizeVisible(false);// do not display minimize
													// button
			ownTabFolder
					.setSelectionBackground(new Color[] {
							new Color(display, 216, 228, 251),
							new Color(display, 153, 186, 243) },
							new int[] { 80 }, true);

			// create tab items
			instEditPanelTabItem = new CTabItem(ownTabFolder, SWT.NONE);
			instSelectWAVFileTabItem = new CTabItem(ownTabFolder, SWT.NONE);

			// setup title of tab item
			instEditPanelTabItem.setText(Activator
					.getResourceString("scripteditor.tabitem.editpanel"));
			instSelectWAVFileTabItem.setText(Activator
					.getResourceString("scripteditor.tabitem.selwavfile"));
			instEditPanelTabItem.setImage(Activator.getImageDescriptor(
					"/icons/micx16.gif").createImage());
			instSelectWAVFileTabItem.setImage(Activator.getImageDescriptor(
					"/icons/micx16.gif").createImage());

			// spawn child class
			instEditPanelTab = new EditPanelTab(ownTabFolder);
			instSelectWAVFileTab = new SelectWAVFileTab(ownTabFolder);

			// setup control of tab item
			instEditPanelTabItem.setControl(instEditPanelTab.getOwnComposite());
			instSelectWAVFileTabItem.setControl(instSelectWAVFileTab
					.getOwnComposite());

			// 1st Initialized current Window
			ownTabFolder.layout();
			ownTabFolder.pack();

		} catch (Exception e) {
			System.out.println("EditPanelView : Exception = " + e);
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

	/**
	 * Getter method : Get instance of tab item : EditPanel
	 */
	public EditPanelTab getInstanceTabEditPanel() {
		// return instance of tab item : EditPanel
		return (instEditPanelTab);
	}

	/**
	 * Getter method : Get instance of tab item : SelectWAVFile
	 */
	public SelectWAVFileTab getInstanceTabSelWAVFile() {
		// return instance of tab item : SelectWAVFile
		return (instSelectWAVFileTab);
	}

	/**
	 * Initialize selection multiple items mode
	 */
	public void setSelectMultiItemsMode(boolean sw) {
		// Check switch flag
		if (sw) {
			// Forced select Edit tab
			ownTabFolder.setSelection(instEditPanelTabItem);
			// exchange mode to multiple items selection to WAV file select tab
			instSelectWAVFileTab.setEnabledTab(false);
		} else {
			// exchange mode to normal to WAV file select tab
			instSelectWAVFileTab.setEnabledTab(true);
		}
	}

}
