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
package org.eclipse.actf.examples.scripteditor.actions;

import org.eclipse.actf.ai.internal.ui.scripteditor.EditPanelTab;
import org.eclipse.actf.ai.internal.ui.scripteditor.PreviewPanel;
import org.eclipse.actf.ai.internal.ui.scripteditor.SelectWAVFileTab;
import org.eclipse.actf.ai.internal.ui.scripteditor.XMLFileMessageBox;
import org.eclipse.actf.ai.scripteditor.data.ScriptData;
import org.eclipse.actf.ai.scripteditor.data.XMLFileSaveUtil;
import org.eclipse.actf.ai.ui.scripteditor.views.EditPanelView;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.ai.ui.scripteditor.views.ScriptListView;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class FileCloseAction implements IWorkbenchWindowActionDelegate, IUNIT {

	// private IWorkbenchWindow window;

	/**
	 * Local data
	 */
	// instance of each ViewPart class
	private ScriptData instScriptData = null;
	private EditPanelTab instEditPanelTab = null;
	private SelectWAVFileTab instSelWavTab = null;
	private TimeLineView instTimeLine = null;
	private PreviewPanel instPreviewPanel = null;
	private ScriptListView instScriptList = null;

	/**
	 * The constructor.
	 */
	public FileCloseAction() {
	}

	/**
	 * Local method : PickUP instance of each ViewPart class
	 */
	private void pickupInstViewPart() {
		// Check each instance of parent Class
		if (instEditPanelTab == null) {
			instEditPanelTab = EditPanelView.getInstance()
					.getInstanceTabEditPanel();
		}
		if (instSelWavTab == null) {
			instSelWavTab = EditPanelView.getInstance()
					.getInstanceTabSelWAVFile();
		}
		if (instTimeLine == null) {
			instTimeLine = TimeLineView.getInstance();
		}
		if (instPreviewPanel == null) {
			instPreviewPanel = PreviewPanel.getInstance();
		}
		if (instScriptList == null) {
			instScriptList = ScriptListView.getInstance();
		}
		if (instScriptData == null) {
			instScriptData = ScriptData.getInstance();
		}
	}

	// **
	// * Private method : Close file process (clear all data & screen)
	// *
	private void closeFile() {

		// Clear ScriptData & WAV list
		instScriptData.clearScriptData();
		instScriptData.cleanupWavList();

		// Repaint Script List
		instScriptList.getInstScriptList().reloadScriptList();
		// Initialize Edit Panel contents
		instEditPanelTab.initDispEditPanel();
		// initialize all parameters
		instSelWavTab.initDescriptionStruct();
		// initialize own screen
		instSelWavTab.initDispSelWavFile();
		// Clear XML file path
		instTimeLine.reqStoreXMLFilePath(null);
		// Clear volume level file path
		instTimeLine.reqStoreVolLvlFilePath(null);
		// Expand Composite of TimeLine
		instTimeLine.reqExpandTimeLine();
		// Repaint image of TimeLine Scale
		instTimeLine.reqRedrawTimeLineCanvas(1);
		// Repaint TimeLine's Audio Label
		instTimeLine.refreshScriptAudio();
		// Reset location of TimeLine
		instTimeLine.rewindActionTimeLine();
		// Reset all time line display
		instTimeLine.reqRewindTimeLine();
	}

	public void run(IAction action) {
		boolean start_flg = true;

		// Store instance of each ViewPart class
		pickupInstViewPart();

		// Check exist edit data
		if (instScriptData.getStatusSaveScripts() > 0) {
			// Display confirmation message box
			XMLFileMessageBox confModifyMB = new XMLFileMessageBox(
					MB_STYLE_MODIFY, null);
			int result = confModifyMB.open();
			// Check result
			if (result == SWT.YES) {
				// Save current data to XML file
				XMLFileSaveUtil saveFH = new XMLFileSaveUtil();
				String filePath = saveFH.open();
				saveFH.save(filePath, true);
				// start close action
				start_flg = true;
			} else if (result == SWT.CANCEL) {
				// cancel close action
				start_flg = false;
				return;
			}
		}
		// Check process status
		if (start_flg) {
			// Close file process
			closeFile();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		// this.window = window;
	}

}
