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

import org.eclipse.actf.ai.internal.ui.scripteditor.guidelist.GuideListTree;
import org.eclipse.actf.ai.internal.ui.scripteditor.guidelist.IGuideListConstants;
import org.eclipse.actf.ai.scripteditor.util.ScriptFileDropListener;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class GuideListView extends ViewPart {

	public static final String VIEW_ID = "org.eclipse.actf.examples.scripteditor.GuideListView";

	public static final int ALL_MODE = 0;
	public static final int AUDIODESCRIPTION_MODE = 1;
	public static final int CAPTION_MODE = 2;
	public static final int SCRIPT_MODE = 3;

	private static GuideListView ownInst = null;

	ScrolledComposite ownComposite;
	private GuideListTree instScriptList;

	public GuideListView() {
		super();
		ownInst = this;
	}

	static public GuideListView getInstance() {
		return (ownInst);
	}

	/**
	 * @Override
	 */
	public void createPartControl(Composite parent) {
		ownComposite = new ScrolledComposite(parent, SWT.VERTICAL
				| SWT.H_SCROLL);
		ownComposite.setExpandHorizontal(true);
		ownComposite.setExpandVertical(true);

		instScriptList = new GuideListTree(ownComposite);

		initDDListener(ownComposite);

		createActions();
		// TODO recover
		// IToolBarManager tbm =
		// getViewSite().getActionBars().getToolBarManager();
		// tbm.add(copyToCaptionAction);

		// IMenuManager mgr = getViewSite().getActionBars().getMenuManager();
		// mgr.add(allAction);
		// mgr.add(voiceAllAction);
		// mgr.add(captionAllAction);
		// mgr.add(scriptAllAction);
		// mgr.add(new Separator());
		// mgr.add(sTimeAction);
		// mgr.add(eTimeAction);
		// mgr.add(characterAction);
		// mgr.add(scenarioAction);
		// mgr.add(captionAction);
		// mgr.add(wavAction);
		// mgr.add(extendsAction);
		// mgr.add(descriptionAction);
		// mgr.add(commentAction);

	}

	Action addItemAction = null;

	Action copyToCaptionAction = null;

	Action allAction = null;
	Action voiceAllAction = null;
	Action captionAllAction = null;
	Action scriptAllAction = null;

	Action sTimeAction = null;
	Action eTimeAction = null;
	Action characterAction = null;
	Action scenarioAction = null;
	Action captionAction = null;
	Action wavAction = null;
	Action extendsAction = null;
	Action descriptionAction = null;
	Action commentAction = null;

	private void createActions() {

		copyToCaptionAction = new Action("Copy To Caption") {
			public void run() {
				instScriptList.copyCaption();
			}
		};

		allAction = new Action("Show all items") {
			public void run() {
				instScriptList.showColumn(IGuideListConstants.START_TIME);
				sTimeAction.setChecked(true);
				instScriptList.showColumn(IGuideListConstants.END_TIME);
				eTimeAction.setChecked(true);
				instScriptList.showColumn(IGuideListConstants.CHARACTER);
				characterAction.setChecked(true);
				instScriptList.showColumn(IGuideListConstants.SCENARIO);
				scenarioAction.setChecked(true);
				instScriptList.showColumn(IGuideListConstants.CAPTION);
				captionAction.setChecked(true);
				instScriptList.showColumn(IGuideListConstants.WAV);
				wavAction.setChecked(true);
				instScriptList.showColumn(IGuideListConstants.EXTENDS);
				extendsAction.setChecked(true);
				instScriptList.showColumn(IGuideListConstants.DESCRIPTION);
				descriptionAction.setChecked(true);
				instScriptList.showColumn(IGuideListConstants.COMMENT);
				commentAction.setChecked(true);
			}
		};

		voiceAllAction = new Action("Audio Description mode") {
			public void run() {
				instScriptList.showColumn(IGuideListConstants.START_TIME);
				sTimeAction.setChecked(true);
				instScriptList.hideColumn(IGuideListConstants.END_TIME);
				eTimeAction.setChecked(false);
				instScriptList.hideColumn(IGuideListConstants.CHARACTER);
				characterAction.setChecked(false);
				instScriptList.hideColumn(IGuideListConstants.SCENARIO);
				scenarioAction.setChecked(false);
				instScriptList.hideColumn(IGuideListConstants.CAPTION);
				captionAction.setChecked(false);
				instScriptList.showColumn(IGuideListConstants.WAV);
				wavAction.setChecked(true);
				instScriptList.showColumn(IGuideListConstants.EXTENDS);
				extendsAction.setChecked(true);
				instScriptList.showColumn(IGuideListConstants.DESCRIPTION);
				descriptionAction.setChecked(true);
				instScriptList.showColumn(IGuideListConstants.COMMENT);
				commentAction.setChecked(true);
			}
		};

		captionAllAction = new Action("Caption mode") {
			public void run() {
				instScriptList.showColumn(IGuideListConstants.START_TIME);
				sTimeAction.setChecked(true);
				instScriptList.showColumn(IGuideListConstants.END_TIME);
				eTimeAction.setChecked(true);
				instScriptList.showColumn(IGuideListConstants.CHARACTER);
				characterAction.setChecked(true);
				instScriptList.hideColumn(IGuideListConstants.SCENARIO);
				scenarioAction.setChecked(false);
				instScriptList.showColumn(IGuideListConstants.CAPTION);
				captionAction.setChecked(true);
				instScriptList.hideColumn(IGuideListConstants.WAV);
				wavAction.setChecked(false);
				instScriptList.hideColumn(IGuideListConstants.EXTENDS);
				extendsAction.setChecked(false);
				instScriptList.hideColumn(IGuideListConstants.DESCRIPTION);
				descriptionAction.setChecked(false);
				instScriptList.showColumn(IGuideListConstants.COMMENT);
				commentAction.setChecked(true);
			}
		};

		scriptAllAction = new Action("Script mode") {
			public void run() {
				instScriptList.showColumn(IGuideListConstants.START_TIME);
				sTimeAction.setChecked(true);
				instScriptList.showColumn(IGuideListConstants.END_TIME);
				eTimeAction.setChecked(true);
				instScriptList.showColumn(IGuideListConstants.CHARACTER);
				characterAction.setChecked(true);
				instScriptList.showColumn(IGuideListConstants.SCENARIO);
				scenarioAction.setChecked(true);
				instScriptList.hideColumn(IGuideListConstants.CAPTION);
				captionAction.setChecked(false);
				instScriptList.hideColumn(IGuideListConstants.WAV);
				wavAction.setChecked(false);
				instScriptList.hideColumn(IGuideListConstants.EXTENDS);
				extendsAction.setChecked(false);
				instScriptList.hideColumn(IGuideListConstants.DESCRIPTION);
				descriptionAction.setChecked(false);
				instScriptList.hideColumn(IGuideListConstants.COMMENT);
				commentAction.setChecked(false);
			}
		};

		sTimeAction = createAction("Start Time", IGuideListConstants.START_TIME);
		eTimeAction = createAction("End Time", IGuideListConstants.END_TIME);
		characterAction = createAction("Character",
				IGuideListConstants.CHARACTER);
		scenarioAction = createAction("Scenario", IGuideListConstants.SCENARIO);
		captionAction = createAction("Caption", IGuideListConstants.CAPTION);
		wavAction = createAction("Wav", IGuideListConstants.WAV);
		extendsAction = createAction("Extends", IGuideListConstants.EXTENDS);
		descriptionAction = createAction("Description",
				IGuideListConstants.DESCRIPTION);
		commentAction = createAction("Comment", IGuideListConstants.COMMENT);

	}

	private Action createAction(final String columnName,
			final String propertyKeyName) {
		Action action = new Action(columnName) {
			public void run() {

				if (!this.isChecked()) {
					instScriptList.hideColumn(propertyKeyName);
				} else {
					instScriptList.showColumn(propertyKeyName);
				}
				super.run();
			}
		};
		action.setChecked(true);
		return action;
	}

	public void setFocus() {
	}

	private void initDDListener(Composite parent) {
		// Initial setup DnD target control
		DropTarget targetDnD = new DropTarget(parent, DND.DROP_DEFAULT
				| DND.DROP_COPY);
		targetDnD.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		targetDnD.addDropListener(new ScriptFileDropListener());
	}

	public void setLayout(int type) {
		switch (type) {
		case ALL_MODE:
			allAction.run();
			break;
		case AUDIODESCRIPTION_MODE:
			voiceAllAction.run();
			break;
		case CAPTION_MODE:
			captionAllAction.run();
			break;
		case SCRIPT_MODE:
			scriptAllAction.run();
			break;
		default:
			// do nothing
		}
	}
}
