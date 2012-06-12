/*******************************************************************************
 * Copyright (c) 2011, 2012 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.ai.internal.ui.scripteditor;

import java.net.URI;

import org.eclipse.actf.ai.scripteditor.data.IScriptData;
import org.eclipse.actf.ai.scripteditor.util.SoundMixer;
import org.eclipse.actf.ai.scripteditor.util.TempFileUtil;
import org.eclipse.actf.ai.scripteditor.util.TimeFormatUtil;
import org.eclipse.actf.ai.scripteditor.util.WavUtil;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.examples.scripteditor.Activator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class WavInputDialog extends Dialog implements IUNIT {

	// public static final int OK_ID = 0;
	public static final int DELETE = 2;
	// public static final int CANCEL_ID = 2;

	private IScriptData data = null;

	private int descriptionEndTime = 0;
	private URI descriptionWavFile = null;
	private float descriptionCompetitiveRatio = 1.0f;

	// each widget parameters
	private Label textStartTime;
	private Label textEndTime;
	private Text textAreaWavInfo;
	private Button buttonWavPreview;
	private Button chkboxPlayWav;
	private Label labelPlayWav;
	private Label labelWSpeed;
	private Scale scaleWavSpeed;
	private Label labelWavSpeedMax;
	private Label labelWavSpeedMid;
	private Label labelWavSpeedMin;

	// TODO : dummy button?
	private Button buttonWavOpen;

	private SoundMixer instSoundMixer = null;

	public WavInputDialog(Shell parent, IScriptData data) {
		super(parent);
		this.data = data;
		instSoundMixer = SoundMixer.getInstance();
	}

	protected Point getInitialSize() {
		return new Point(1025, 260);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Input WAV Dialog");
	}

	private FormData prepareFormData(int width, int height, int[] left,
			int[] top) {
		// FormData tmpData = new FormData(width, height);
		FormData tmpData = new FormData(SWT.DEFAULT, SWT.DEFAULT);// TODO:temporary
																	// fix
		tmpData.left = new FormAttachment(left[0], left[1], left[2]);
		tmpData.top = new FormAttachment(top[0], top[1], top[2]);
		return tmpData;
	}

	protected Control createDialogArea(Composite parent) {
		Composite ownComposite1 = (Composite) super.createDialogArea(parent);
		Composite ownComposite = new Composite(ownComposite1, SWT.NONE);
		ownComposite.setLayout(new FormLayout()); // TODO Grid Layout

		Label labelStartTime = new Label(ownComposite, SWT.NONE);
		labelStartTime.setLayoutData(prepareFormData(54, 12, new int[] { 0,
				1000, 5 }, new int[] { 0, 1000, 11 }));
		labelStartTime.setText("Start Time");
		textStartTime = new Label(ownComposite, SWT.NONE);
		textStartTime.setLayoutData(prepareFormData(84, 12, new int[] { 0,
				1000, 78 }, new int[] { 0, 1000, 11 }));
		textStartTime.setText("00 : 00 : 00 . 000");

		Label labelEndTime = new Label(ownComposite, SWT.NONE);
		labelEndTime.setLayoutData(prepareFormData(48, 12, new int[] { 0, 1000,
				236 }, new int[] { 0, 1000, 11 }));
		labelEndTime.setText("End Time");
		textEndTime = new Label(ownComposite, SWT.NONE);
		textEndTime.setLayoutData(prepareFormData(84, 12, new int[] { 0, 1000,
				300 }, new int[] { 0, 1000, 11 }));
		textEndTime.setText("00 : 00 : 00 . 000");

		Label labelWFileInfo = new Label(ownComposite, SWT.NONE);
		labelWFileInfo.setLayoutData(prepareFormData(58, 12, new int[] { 0,
				1000, 5 }, new int[] { 0, 1000, 35 }));
		labelWFileInfo.setText("WAV File");

		FormData textAreaWFileInfoLData = new FormData();
		textAreaWFileInfoLData.width = 415;
		textAreaWFileInfoLData.height = 80;
		textAreaWFileInfoLData.left = new FormAttachment(0, 1000, 69);
		textAreaWFileInfoLData.top = new FormAttachment(0, 1000, 35);
		textAreaWavInfo = new Text(ownComposite, SWT.MULTI | SWT.WRAP
				| SWT.BORDER | SWT.READ_ONLY);
		textAreaWavInfo.setLayoutData(textAreaWFileInfoLData);
		textAreaWavInfo
				.setText(Activator
						.getResourceString("scripteditor.tabitem.selwavfile.descstandby"));
		// TODO Setup DnD listener
		// initDnDProc(textAreaWavInfo);

		FormData chkboxPlayWavLData = new FormData();
		chkboxPlayWavLData.width = 13;
		chkboxPlayWavLData.height = 16;
		chkboxPlayWavLData.left = new FormAttachment(0, 1000, 501);
		chkboxPlayWavLData.top = new FormAttachment(0, 1000, 4);
		chkboxPlayWav = new Button(ownComposite, SWT.CHECK | SWT.LEFT);
		chkboxPlayWav.setLayoutData(chkboxPlayWavLData);
		chkboxPlayWav.setSelection(true);
		// TODO Tracking mouse cursor listener

		FormData labelPlayWavLData = new FormData();
		labelPlayWavLData.left = new FormAttachment(0, 1000, 518);
		labelPlayWavLData.top = new FormAttachment(0, 1000, 6);
		labelPlayWav = new Label(ownComposite, SWT.NONE);
		labelPlayWav.setLayoutData(labelPlayWavLData);
		labelPlayWav.setText("Play WAV");

		FormData labelWSpeedLData = new FormData();
		labelWSpeedLData.left = new FormAttachment(0, 1000, 662);
		labelWSpeedLData.top = new FormAttachment(0, 1000, 22);
		labelWSpeed = new Label(ownComposite, SWT.NONE);
		labelWSpeed.setLayoutData(labelWSpeedLData);
		labelWSpeed.setText("Speed");

		FormData scaleWavSpeedLData = new FormData();
		scaleWavSpeedLData.width = 219;
		scaleWavSpeedLData.height = 42;
		scaleWavSpeedLData.left = new FormAttachment(0, 1000, 697);
		scaleWavSpeedLData.top = new FormAttachment(0, 1000, 6);
		scaleWavSpeed = new Scale(ownComposite, SWT.HORIZONTAL);
		scaleWavSpeed.setLayoutData(scaleWavSpeedLData);
		scaleWavSpeed.setMinimum(50);
		scaleWavSpeed.setMaximum(200);
		scaleWavSpeed.setIncrement(10);
		scaleWavSpeed.setSelection(100);
		scaleWavSpeed.setToolTipText("100/200");

		FormData labelWavSpeedMaxLData = new FormData();
		labelWavSpeedMaxLData.left = new FormAttachment(0, 1000, 702);

		labelWavSpeedMaxLData.top = new FormAttachment(0, 1000, 47);
		labelWavSpeedMax = new Label(ownComposite, SWT.NONE);
		labelWavSpeedMax.setLayoutData(labelWavSpeedMaxLData);
		labelWavSpeedMax.setText("1/2");

		FormData labelWavSpeedMidLData = new FormData();
		labelWavSpeedMidLData.left = new FormAttachment(0, 1000, 772);

		labelWavSpeedMidLData.top = new FormAttachment(0, 1000, 47);
		labelWavSpeedMid = new Label(ownComposite, SWT.NONE);
		labelWavSpeedMid.setLayoutData(labelWavSpeedMidLData);
		labelWavSpeedMid.setText("1");

		FormData labelWavSpeedMinLData = new FormData();
		labelWavSpeedMinLData.left = new FormAttachment(0, 1000, 900);

		labelWavSpeedMinLData.top = new FormAttachment(0, 1000, 47);
		labelWavSpeedMin = new Label(ownComposite, SWT.NONE);
		labelWavSpeedMin.setLayoutData(labelWavSpeedMinLData);
		labelWavSpeedMin.setText("2");

		// Button : "Preview"
		FormData buttonWavPreviewLData = new FormData();
		buttonWavPreviewLData.width = 52;
		buttonWavPreviewLData.height = 22;
		buttonWavPreviewLData.top = new FormAttachment(0, 1000, 68);
		buttonWavPreviewLData.left = new FormAttachment(0, 1000, 501);
		buttonWavPreview = new Button(ownComposite, SWT.PUSH | SWT.CENTER);
		buttonWavPreview.setLayoutData(buttonWavPreviewLData);
		buttonWavPreview.setText("Preview");

		// TODO : dummy button?
		// Button : "Open" for WAV file
		FormData buttonWavOpenLData = new FormData();
		buttonWavOpenLData.top = new FormAttachment(labelWFileInfo, 16);
		buttonWavOpenLData.left = new FormAttachment(0, 1000, 5);
		buttonWavOpen = new Button(ownComposite, SWT.PUSH | SWT.CENTER);
		buttonWavOpen.setLayoutData(buttonWavOpenLData);
		buttonWavOpen.setText("Open");
		// Append Preview event listener
		buttonWavOpen.addSelectionListener(new OpenWavButtonAdapter());

		ownComposite.layout();
		ownComposite.pack();
		setData();
		return ownComposite;
	}

	private void setData() {
		try {
			textStartTime.setText(this.data.getStartTimeString());
			// SetUP WAV header information
			instSoundMixer.storeWavHeader(this.data.getWavURI());

			textAreaWavInfo.setText(instSoundMixer.makeFormatWavInfo(this.data
					.getWavURI().toString()));
			chkboxPlayWav.setSelection(this.data.isWavEnabled());
			descriptionCompetitiveRatio = this.data.getWavPlaySpeed();
			descriptionEndTime = calDurationTimeWav(
					instSoundMixer.getDurationTimeWav(),
					descriptionCompetitiveRatio);
			textEndTime.setText(TimeFormatUtil.makeFormatHHMMSSMS(this.data
					.getStartTime() + descriptionEndTime));
			scaleWavSpeed
					.setSelection(calSpeedScale(descriptionCompetitiveRatio));
			scaleWavSpeed.setToolTipText(String.valueOf(scaleWavSpeed
					.getSelection()) + "/200");
		} catch (Exception we) {
		}

	}

	private int calDurationTimeWav(int nowDurationTime,
			float nowCompetitiveRatio) {
		int newDurationTime = nowDurationTime;

		float tempDurationTime = (float) nowDurationTime / nowCompetitiveRatio;
		newDurationTime = (int) tempDurationTime;

		return (newDurationTime);
	}

	private int calSpeedScale(float compRatio) {
		int newPos = 100;
		newPos = (int) (compRatio * 100.0f);
		return (newPos);
	}

	protected void okPressed() {
		applyData();
		setReturnCode(OK);
		close();
	}

	private void applyData() {
		this.data.setStartTimeString(textStartTime.getText());
		this.data.setWavEndTimeString(textEndTime.getText());
		if (descriptionWavFile != null) {
			this.data.setWavURI(descriptionWavFile);
		}
		this.data.setWavEnabled(chkboxPlayWav.getSelection());
		this.data.setWavPlaySpeed(calCompetitiveRatioWav());
	}

	private void deleteData() {
		this.data.setStartTimeString(textStartTime.getText());
		this.data.setWavEndTimeString("");
		this.data.setWavURI(null);
		this.data.setWavEnabled(false);
		this.data.setWavPlaySpeed(new Float(0));
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, OK, "OK", true);
		if (this.data.getWavURI() != null) {
			createButton(parent, DELETE, "Delete", true);
		}
		createButton(parent, CANCEL, "Cancel", true);

	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == OK) {
			applyData();
			setReturnCode(OK);
			close();
		} else if (buttonId == DELETE) {
			deleteData();
			setReturnCode(DELETE);
			close();
		} else if (buttonId == CANCEL) {
			setReturnCode(CANCEL);
			close();
		}
	}

	/**
	 * Local Class implements ButtonListener
	 */
	class OpenWavButtonAdapter extends SelectionAdapter {
		// Event of Button of Open WAV file (file dialog)
		public void widgetSelected(SelectionEvent e) {
			String[] EXTENSIONS = { "*.wav", "*" };
			String wavFileName = "";

			try {
				// Request FileDialog (Choice open file name)
				FileDialog openDialog = new FileDialog(Display.getCurrent()
						.getActiveShell(), SWT.OPEN);
				openDialog.setFilterExtensions(EXTENSIONS);
				wavFileName = openDialog.open();

				// Check null (file name)
				if (wavFileName != null) {
					// check file header
					if (WavUtil.isWavFormat(wavFileName)) {
						// PickUP file header from current WAV file
						descriptionWavFile = TempFileUtil
								.getResource(wavFileName);
						instSoundMixer.storeWavHeader(descriptionWavFile);

						descriptionCompetitiveRatio = calCompetitiveRatioWav();
						descriptionEndTime = calDurationTimeWav(
								instSoundMixer.getDurationTimeWav(),
								descriptionCompetitiveRatio);
						textEndTime.setText(TimeFormatUtil
								.makeFormatHHMMSSMS(data.getStartTime()

								+ descriptionEndTime));
						textAreaWavInfo.setText(instSoundMixer
								.makeFormatWavInfo(descriptionWavFile
										.toString()));
					} else {
						// TODO
					}
				}
			} catch (Exception we) {
			}
		}
	}

	/**
	 * Local method : Calculate competitive ratio of play WAV
	 */
	private float calCompetitiveRatioWav() {
		float compratio = 1.0f;

		int nowValue = scaleWavSpeed.getSelection();
		compratio = (float) nowValue / 100.0f;
		return (compratio);
	}

	public IScriptData getData() {
		return data;
	}

}
