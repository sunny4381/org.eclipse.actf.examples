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
import org.eclipse.actf.ai.internal.ui.scripteditor.XMLFileMessageBox;
import org.eclipse.actf.ai.scripteditor.data.ScriptData;
import org.eclipse.actf.ai.scripteditor.data.XMLFileSaveUtil;
import org.eclipse.actf.ai.scripteditor.reader.SAXReader;
import org.eclipse.actf.ai.ui.scripteditor.views.EditPanelView;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.ai.ui.scripteditor.views.ScriptListView;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class FileOpenAction implements IWorkbenchWindowActionDelegate, IUNIT {

	/**
	 * Local data
	 */
	// instance of each ViewPart class
	private ScriptData instScriptData = null;
	private EditPanelTab instEditPanelTab = null;
	private TimeLineView instTimeLine = null;
	private PreviewPanel instPreviewPanel = null;
	private ScriptListView instScriptList = null;

	// parameters
	private String[] EXTENSIONS = { "*.xml", "*" };
	private String saveFileName = "";
	private String currentURLMovie = "about:blank";

	/**
	 * The constructor.
	 */
	public FileOpenAction() {
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
	// * Private method : Pre-Process for Load data
	// *
	private void preProcessLoadFile() {
		// Clear ScriptData class
		instScriptData.clearScriptData();
		// Clear volume level file path
		instTimeLine.reqStoreVolLvlFilePath(null);
	}

	// **
	// * Private method : Post-Process for Load data
	// *
	private void postProcessLoadFile() {
		// Repaint Script List
		instScriptList.getInstScriptList().reloadScriptList();
		// Initialize Edit Panel contents
		instEditPanelTab.initDispEditPanel();
		// initialize all parameters
		EditPanelView.getInstance().getInstanceTabSelWAVFile()
				.initDescriptionStruct();
		// initialize own screen
		EditPanelView.getInstance().getInstanceTabSelWAVFile()
				.initDispSelWavFile();
		// Reset URL for Preview Movie
		instPreviewPanel.setURLMovie(currentURLMovie);
		// Store current opened XML file path
		instTimeLine.reqStoreXMLFilePath(saveFileName);
		// Expand Composite of TimeLine
		instTimeLine.reqExpandTimeLine();
		// Repaint image of TimeLine Scale
		instTimeLine.reqRedrawTimeLineCanvas(1);
		// Load volume level value to buffer
		instTimeLine.reqLoadVolumeLevelData();
		// Repaint image of TimeLine Scale
		instTimeLine.reqRedrawVolumeLevelCanvas(2);
		// Repaint TimeLine's Audio Label
		instTimeLine.refreshScriptAudio();
		// Reset location of TimeLine
		instTimeLine.rewindActionTimeLine();
	}

	// **
	// * Private method : Load data from Open file.
	// *
	private void loadFile(String fname) {
		SAXReader loader = null;

		try {
			// Load XML file by DefaultHandler
			loader = new SAXReader();
			loader.startSAXReader(fname, instEditPanelTab);
			// PickUP URI String
			currentURLMovie = loader.getUri();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			loader = null;
		}
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		boolean start_flg = true;

		// Store instance of each ViewPart class
		pickupInstViewPart();

		// Check exist unsaved data Before Open file
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
			} else if (result == SWT.CANCEL) {
				// cancel close action
				start_flg = false;
			}
		}
		// Check status
		if (start_flg) {
			// Request FileDialog (Choice open file name)
			FileDialog openDialog = new FileDialog(Display.getCurrent()
					.getActiveShell(), SWT.OPEN);
			openDialog.setFilterExtensions(EXTENSIONS);
			saveFileName = openDialog.open();

			// Check null (file name)
			if (saveFileName != null) {
				// Pre-Process for loading
				preProcessLoadFile();
				// Load file(XML format)
				loadFile(saveFileName);
				// Post-Process for loading
				postProcessLoadFile();
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}
}