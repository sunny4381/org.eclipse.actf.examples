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
import org.eclipse.actf.ai.internal.ui.scripteditor.SelectWAVFileTab;
import org.eclipse.actf.ai.internal.ui.scripteditor.VolumeLevelCanvas;
import org.eclipse.actf.ai.scripteditor.data.ScriptDataManager;
import org.eclipse.actf.ai.scripteditor.data.event.DataEventManager;
import org.eclipse.actf.ai.scripteditor.data.event.GuideListEvent;
import org.eclipse.actf.ai.scripteditor.data.event.LabelEvent;
import org.eclipse.actf.ai.scripteditor.util.XMLFileMessageBox;
import org.eclipse.actf.ai.scripteditor.util.XMLFileSaveUtil;
import org.eclipse.actf.ai.ui.scripteditor.views.EditPanelView;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ClearAction implements IWorkbenchWindowActionDelegate {

	private ScriptDataManager scriptManager = null;
	private EditPanelTab instEditPanelTab = null;
	private SelectWAVFileTab instSelWavTab = null;
	private TimeLineView instTimeLine = null;

	private DataEventManager dataEventManager = null;

	public ClearAction() {
	}

	private void init() {
		if (instEditPanelTab == null) {
			if (EditPanelView.getInstance() != null) {
				instEditPanelTab = EditPanelView.getInstance()
						.getInstanceTabEditPanel();
			}
		}
		if (instSelWavTab == null) {
			if (EditPanelView.getInstance() != null) {
				instSelWavTab = EditPanelView.getInstance()
						.getInstanceTabSelWAVFile();
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

	private void closeFile() {

		dataEventManager.fireLabelEvent(new LabelEvent(LabelEvent.CLEAR_LABEL,
				null, this));
		dataEventManager.fireGuideListEvent(new GuideListEvent(
				GuideListEvent.CLEAR_DATA, null, this));

		if (instEditPanelTab != null) {
			instEditPanelTab.initDispEditPanel();
		}
		if (instSelWavTab != null) {
			instSelWavTab.initDescriptionData();
			instSelWavTab.initDispSelWavFile();
		}
		XMLFileSaveUtil.getInstance().setFilePath(null);
		VolumeLevelCanvas.setVolumeLevelFilePath(null);

		instTimeLine.reqExpandTimeLine();
		instTimeLine.reqRedrawTimeLineCanvas(1);
		instTimeLine.refreshScriptAudio();
		instTimeLine.rewindActionTimeLine();
		instTimeLine.reqRewindTimeLine();
	}

	public void run(IAction action) {
		boolean start_flg = true;

		init();

		if (scriptManager.isSaveRequired() > 0) {
			XMLFileMessageBox confModifyMB = new XMLFileMessageBox(
					XMLFileMessageBox.MB_STYLE_MODIFY, null);
			int result = confModifyMB.open();
			if (result == SWT.YES) {
				XMLFileSaveUtil saveFH = XMLFileSaveUtil.getInstance();
				String filePath = saveFH.open();
				saveFH.save(filePath, true);
				start_flg = true;
			} else if (result == SWT.CANCEL) {
				start_flg = false;
				return;
			}
		}
		if (start_flg) {
			closeFile();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

}
