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
package org.eclipse.actf.ai.ui.scripteditor.views;

import org.eclipse.actf.ai.internal.ui.scripteditor.EditPanelTab;
import org.eclipse.actf.ai.internal.ui.scripteditor.SelectWAVFileTab;
import org.eclipse.actf.ai.scripteditor.data.IScriptData;
import org.eclipse.actf.ai.scripteditor.data.event.DataEventManager;
import org.eclipse.actf.ai.scripteditor.data.event.GuideListEvent;
import org.eclipse.actf.ai.scripteditor.data.event.GuideListEventListener;
import org.eclipse.actf.ai.scripteditor.util.ScriptFileDropListener;
import org.eclipse.actf.examples.scripteditor.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class EditPanelView extends ViewPart implements GuideListEventListener {
	public static final String VIEW_ID = "org.eclipse.actf.examples.scripteditor.EditPanelView";

	static private EditPanelView ownInst = null;

	private CTabFolder ownTabFolder;

	// Child class instance
	private EditPanelTab instEditPanelTab = null;
	private SelectWAVFileTab instSelectWAVFileTab = null;

	// Tab Item instance
	private CTabItem instEditPanelTabItem = null;
	private CTabItem instSelectWAVFileTabItem = null;
	//
	private DataEventManager dataEventManager = null;

	/**
     * 
     */
	public EditPanelView() {
		super();
		ownInst = this;
		dataEventManager = DataEventManager.getInstance();
	}

	static public EditPanelView getInstance() {
		return (ownInst);
	}

	public void createPartControl(Composite parent) {
		// Create own instance of Composite
		ownTabFolder = new CTabFolder(parent, SWT.NONE);

		// Initialize application's GUI
		initTabFolder();

		// Add listener for load meta file
		initDDListener(ownTabFolder);

		dataEventManager.addGuideListEventListener(ownInst);
		parent.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				// TODO other components
				dataEventManager.removeGuideListEventListener(ownInst);
			}
		});
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		ownTabFolder.setFocus();
	}

	/**
	 * Cleans up all resources created by this ViewPart.
	 */
	public void dispose() {
		super.dispose();
		ownInst = null;
	}

	/**
	 * Initialize Screen
	 */
	private void initTabFolder() {

		try {
			Display display = PlatformUI.getWorkbench().getDisplay();

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
			ownTabFolder.setMaximizeVisible(false);
			ownTabFolder.setMinimizeVisible(false);

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

			instEditPanelTab = new EditPanelTab(ownTabFolder);
			instSelectWAVFileTab = new SelectWAVFileTab(ownTabFolder);

			// setup control of tab item
			instEditPanelTabItem.setControl(instEditPanelTab.getOwnComposite());
			instSelectWAVFileTabItem.setControl(instSelectWAVFileTab
					.getOwnComposite());

			// 1st Initialized current Window
			ownTabFolder.setSelection(0);
			ownTabFolder.layout();
			ownTabFolder.pack();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize Drop&Drop Listener
	 */
	private void initDDListener(Composite parent) {
		DropTarget targetDnD = new DropTarget(parent, DND.DROP_DEFAULT
				| DND.DROP_COPY);
		targetDnD.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		targetDnD.addDropListener(new ScriptFileDropListener());
	}

	public EditPanelTab getInstanceTabEditPanel() {
		return (instEditPanelTab);
	}

	public SelectWAVFileTab getInstanceTabSelWAVFile() {
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
			// change mode to multiple items selection to WAV file select tab
			instSelectWAVFileTab.setEnabledTab(false);
		} else {
			// change mode to normal to WAV file select tab
			instSelectWAVFileTab.setEnabledTab(true);
		}
	}

	public void handleGuideListEvent(GuideListEvent e) {
		if (e.getEventType() == GuideListEvent.SET_DATA) {
			IScriptData data = e.getData();
			instEditPanelTab.setMultiSelectMode(false);
			instEditPanelTab.repaintTextScriptData(data);
			instSelectWAVFileTab.setScreenData(data);
			if (data.isWavEnabled()) {
				ownTabFolder.setSelection(1);
			} else {
				ownTabFolder.setSelection(0);
			}
		} else if (e.getEventType() == GuideListEvent.DESELECT_DATA) {
			instEditPanelTab.initDispEditPanel();
		}

	}
}
