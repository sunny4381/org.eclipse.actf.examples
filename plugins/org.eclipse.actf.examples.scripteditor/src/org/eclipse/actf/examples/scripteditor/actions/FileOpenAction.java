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
package org.eclipse.actf.examples.scripteditor.actions;

import org.eclipse.actf.ai.internal.ui.scripteditor.EditPanelTab;
import org.eclipse.actf.ai.internal.ui.scripteditor.PreviewPanel;
import org.eclipse.actf.ai.internal.ui.scripteditor.VolumeLevelCanvas;
import org.eclipse.actf.ai.scripteditor.data.ScriptDataManager;
import org.eclipse.actf.ai.scripteditor.data.event.DataEventManager;
import org.eclipse.actf.ai.scripteditor.data.event.GuideListEvent;
import org.eclipse.actf.ai.scripteditor.data.event.LabelEvent;
import org.eclipse.actf.ai.scripteditor.reader.SAXReader;
import org.eclipse.actf.ai.scripteditor.util.WebBrowserFactory;
import org.eclipse.actf.ai.scripteditor.util.XMLFileMessageBox;
import org.eclipse.actf.ai.scripteditor.util.XMLFileSaveUtil;
import org.eclipse.actf.ai.ui.scripteditor.views.EditPanelView;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class FileOpenAction implements IWorkbenchWindowActionDelegate {

	private ScriptDataManager scriptManager = null;
	private DataEventManager dataEventManager = null;
	private EditPanelTab instEditPanelTab = null;
	private TimeLineView instTimeLine = null;

	private String[] EXTENSIONS = { "*.xml", "*" };
	private String saveFileName = "";
	private String currentURL = "about:blank";

	public FileOpenAction() {
	}

	private void init() {
		if (instEditPanelTab == null) {
			if (EditPanelView.getInstance() != null) {
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
		if (dataEventManager == null) {
			dataEventManager = DataEventManager.getInstance();
		}
	}

	private void preProcess() {
		VolumeLevelCanvas.setVolumeLevelFilePath(null);
		dataEventManager.fireLabelEvent(new LabelEvent(LabelEvent.CLEAR_LABEL,
				null, this)); // clear current data
		dataEventManager.fireGuideListEvent(new GuideListEvent(
				GuideListEvent.CLEAR_DATA, null, this));
	}

	private void postProcess() {
		dataEventManager.fireLabelEvent(new LabelEvent(
				LabelEvent.PUT_ALL_LABEL, null, this));

		if (instEditPanelTab != null) {
			instEditPanelTab.initDispEditPanel();
		}
		// initialize all parameters
		if (EditPanelView.getInstance() != null) {
			EditPanelView.getInstance().getInstanceTabSelWAVFile()
					.initDescriptionData();
			// initialize own screen
			EditPanelView.getInstance().getInstanceTabSelWAVFile()
					.initDispSelWavFile();
		}
		WebBrowserFactory.navigate(currentURL);
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
			loader = new SAXReader();
			loader.startSAXReader(fname);
			currentURL = loader.getUri();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			loader = null;
		}
	}

	public void run(IAction action) {
		boolean start_flg = true;

		init();

		// Check exist unsaved data Before Open file
		if (scriptManager.isSaveRequired() > 0) {
			XMLFileMessageBox confModifyMB = new XMLFileMessageBox(
					XMLFileMessageBox.MB_STYLE_MODIFY, null);
			int result = confModifyMB.open();
			if (result == SWT.YES) {
				XMLFileSaveUtil saveFH = XMLFileSaveUtil.getInstance();
				String filePath = saveFH.open();
				saveFH.save(filePath, true);
			} else if (result == SWT.CANCEL) {
				start_flg = false;
			}
		}
		if (start_flg) {
			FileDialog openDialog = new FileDialog(Display.getCurrent()
					.getActiveShell(), SWT.OPEN);
			openDialog.setFilterExtensions(EXTENSIONS);
			saveFileName = openDialog.open();
			if (saveFileName != null) {
				preProcess();
				loadFile(saveFileName);
				postProcess();
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