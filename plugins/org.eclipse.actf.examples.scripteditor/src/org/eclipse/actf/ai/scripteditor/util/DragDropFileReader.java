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
package org.eclipse.actf.ai.scripteditor.util;

import org.eclipse.actf.ai.internal.ui.scripteditor.EditPanelTab;
import org.eclipse.actf.ai.internal.ui.scripteditor.VolumeLevelCanvas;
import org.eclipse.actf.ai.scripteditor.data.ScriptDataManager;
import org.eclipse.actf.ai.scripteditor.reader.CSVReader;
import org.eclipse.actf.ai.scripteditor.reader.SAXReader;
import org.eclipse.actf.ai.ui.scripteditor.views.EditPanelView;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.swt.SWT;

public class DragDropFileReader {

	private ScriptDataManager scriptManager = null;

	private EditPanelTab instEditPanelTab = null;
	private TimeLineView instTimeLine = null;

	// parameters
	private int modeFile = 0;
	private String saveFileName = "";
	private String currentURLMovie = "about:blank";

	/**
	 * @category Constructor
	 * @param mode
	 *            : file type (0:XML file, 1:CSV file)
	 * @param fname
	 *            : file path
	 */
	public DragDropFileReader(int mode, String fname) {
		modeFile = mode;
		saveFileName = fname;

		pickupInstViewPart();
	}

	/**
	 * @category Start load data from target meta file
	 */
	public void load() {
		// Load data from target meta file
		if (modeFile == IUNIT.LD_FTYPE_XML) {
			// XML file
			loadXMLFile();
		} else if (modeFile == IUNIT.LD_FTYPE_CSV) {
			// CSV file
			loadCSVFile();
		}
	}

	/**
	 * Local method : PickUP instance of each ViewPart class
	 */
	private void pickupInstViewPart() {
		if (instEditPanelTab == null) {
			if (EditPanelView.getInstance() != null
					&& EditPanelView.getInstance().getInstanceTabEditPanel() != null) {
				instEditPanelTab = EditPanelView.getInstance()
						.getInstanceTabEditPanel();
			}
		}
		if (instTimeLine == null) {
			instTimeLine = TimeLineView.getInstance();
		}
		if (scriptManager == null) {
			scriptManager = ScriptDataManager.getInstance();
		}
	}

	/*************************************************************************
	 * Drag & Drop XML file reader
	 * 
	 ************************************************************************/
	// **
	// * Private method : Pre-Process for Load data
	// *
	private void preProcessLoadFile() {
		scriptManager.clearData();
		VolumeLevelCanvas.setVolumeLevelFilePath(null);
	}

	private void postProcessLoadFile() {
		instEditPanelTab.initDispEditPanel();
		if (EditPanelView.getInstance() != null
				&& EditPanelView.getInstance().getInstanceTabSelWAVFile() != null) {
			EditPanelView.getInstance().getInstanceTabSelWAVFile()
					.initDescriptionData();
			EditPanelView.getInstance().getInstanceTabSelWAVFile()
					.initDispSelWavFile();
		}
		WebBrowserFactory.navigate(currentURLMovie);
		XMLFileSaveUtil.getInstance().setFilePath(saveFileName);
		instTimeLine.reqExpandTimeLine();
		instTimeLine.reqRedrawTimeLineCanvas(1);
		instTimeLine.reqLoadVolumeLevelData();
		instTimeLine.reqRedrawVolumeLevelCanvas(2);
		instTimeLine.refreshScriptAudio();
		instTimeLine.rewindActionTimeLine();
	}

	private void loadFile(String fname) {
		SAXReader loader = null;

		try {
			// Load XML file by DefaultHandler
			loader = new SAXReader();
			loader.startSAXReader(fname);
			currentURLMovie = loader.getUri();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			loader = null;
		}
	}

	/**
	 * @category Local method : Main method of Load XML file
	 */
	private void loadXMLFile() {
		boolean start_flg = true;
		// Check exist unsaved data Before Open file
		if (scriptManager.size() > 0) {
			// Display confirmation message box
			XMLFileMessageBox confModifyMB = new XMLFileMessageBox(
					XMLFileMessageBox.MB_STYLE_MODIFY, null);
			int result = confModifyMB.open();
			// Check result
			if (result == SWT.YES) {
				// Save current data to XML file
				XMLFileSaveUtil saveFH = XMLFileSaveUtil.getInstance();
				String filePath = saveFH.open();
				saveFH.save(filePath, true);
			} else if (result == SWT.CANCEL) {
				// cancel close action
				start_flg = false;
			}
		}
		// Check status
		if (start_flg) {
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

	/*************************************************************************
	 * Drag & Drop CSV file reader
	 * 
	 ************************************************************************/
	/**
	 * @category Local method : Main method of Load CSV file
	 */
	private void loadCSVFile() {
		boolean start_flg = true;

		// Check exist unsaved data Before Open file
		if (scriptManager.size() > 0) {
			// Display confirmation message box
			XMLFileMessageBox confModifyMB = new XMLFileMessageBox(
					XMLFileMessageBox.MB_STYLE_MODIFY, null);
			int result = confModifyMB.open();
			// Check result
			if (result == SWT.YES) {
				// Save current data to XML file
				XMLFileSaveUtil saveFH = XMLFileSaveUtil.getInstance();
				String filePath = saveFH.open();
				saveFH.save(filePath, true);
			} else if (result == SWT.CANCEL) {
				// cancel close action
				start_flg = false;
			}
		}
		// Check status
		if (start_flg) {
			// Check null (file name)
			if (saveFileName != null) {
				// Clear XML file path
				XMLFileSaveUtil.getInstance().setFilePath(null);

				// Start CSV reader thread
				CSVReader csvReader = new CSVReader();
				csvReader.startCSVReader(saveFileName);
			}
		}
	}

}
