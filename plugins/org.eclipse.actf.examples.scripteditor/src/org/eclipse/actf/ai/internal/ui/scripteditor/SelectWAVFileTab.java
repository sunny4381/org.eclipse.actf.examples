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
package org.eclipse.actf.ai.internal.ui.scripteditor;

import java.io.FileNotFoundException;
import java.net.URI;

import org.eclipse.actf.ai.scripteditor.data.IScriptData;
import org.eclipse.actf.ai.scripteditor.data.ScriptDataFactory;
import org.eclipse.actf.ai.scripteditor.data.event.DataEventManager;
import org.eclipse.actf.ai.scripteditor.data.event.GuideListEvent;
import org.eclipse.actf.ai.scripteditor.data.event.LabelEvent;
import org.eclipse.actf.ai.scripteditor.util.SoundMixer;
import org.eclipse.actf.ai.scripteditor.util.TempFileUtil;
import org.eclipse.actf.ai.scripteditor.util.TimeFormatUtil;
import org.eclipse.actf.ai.scripteditor.util.WavUtil;
import org.eclipse.actf.examples.scripteditor.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public class SelectWAVFileTab {

	Composite ownComposite;

	private SoundMixer instSoundMixer = null;

	// data of target description
	// private int descriptionIndex = -1;
	private int descriptionStartTime = 0;
	private int descriptionEndTime = 0;
	private URI descriptionWavFile = null;
	// private String descriptionText = "";
	private float descriptionCompetitiveRatio = 1.0f;

	// Otherwise parameters
	// private boolean currentStatAppend = true;
	private boolean currentModeAppend = true;
	private boolean currentSelWavFile = true;

	// each widget parameters
	private Label textStartTime;
	private Label textEndTime;
	private Text textAreaWavInfo;
	private Button buttonWavAppend;
	private Button buttonWavDelete;
	private Button buttonWavPreview;
	private Button chkboxPlayWav;
	private Label labelPlayWav;
	private Label labelWSpeed;
	private Scale scaleWavSpeed;
	private Label labelWavSpeedMax;
	private Label labelWavSpeedMid;
	private Label labelWavSpeedMin;

	// Parameter for multiple selection mode
	private boolean current_tab_mode = true;

	private DataEventManager dataEventManager = null;

	private Button buttonWavOpen;

	private IScriptData currentData = null;

	/**
	 * Constructor
	 */
	public SelectWAVFileTab(CTabFolder parent) {
		dataEventManager = DataEventManager.getInstance();
		// Create own instance of Composite
		ownComposite = new Composite(parent, SWT.NONE);

		// Get instance of data class
		instSoundMixer = SoundMixer.getInstance();

		Display display = PlatformUI.getWorkbench().getDisplay();
		initGUI(display);
	}

	private FormData prepareFormData(int width, int height, int[] left,
			int[] top) {
		FormData tmpData = new FormData(SWT.DEFAULT, SWT.DEFAULT);// TODO:tmp
		tmpData.left = new FormAttachment(left[0], left[1], left[2]);
		tmpData.top = new FormAttachment(top[0], top[1], top[2]);
		return tmpData;
	}

	/**
	 * Initialize Screen
	 */
	private void initGUI(Display parentDisp) {

		try {
			// **<Select WAV file>***********************************
			FormLayout groupSelWAVFileLayout = new FormLayout();
			FormData groupSelWAVFileLData = new FormData(925, 128);
			groupSelWAVFileLData.top = new FormAttachment(0, 1000, 0);
			groupSelWAVFileLData.left = new FormAttachment(0, 1000, 0);
			groupSelWAVFileLData.right = new FormAttachment(1000, 1000, 0);
			groupSelWAVFileLData.bottom = new FormAttachment(1000, 1000, 0);
			ownComposite.setLayoutData(groupSelWAVFileLData);
			ownComposite.setLayout(groupSelWAVFileLayout);

			// Label : "Start Time"
			Label labelStartTime = new Label(ownComposite, SWT.NONE);
			labelStartTime.setLayoutData(prepareFormData(54, 12, new int[] { 0,
					1000, 5 }, new int[] { 0, 1000, 11 }));
			labelStartTime.setText("Start Time");
			// Text field : StartTime
			textStartTime = new Label(ownComposite, SWT.NONE);
			textStartTime.setLayoutData(prepareFormData(84, 12, new int[] { 0,
					1000, 78 }, new int[] { 0, 1000, 11 }));
			textStartTime.setText("00 : 00 : 00 . 000");

			// Label : "End Time"
			Label labelEndTime = new Label(ownComposite, SWT.NONE);
			labelEndTime.setLayoutData(prepareFormData(48, 12, new int[] { 0,
					1000, 236 }, new int[] { 0, 1000, 11 }));
			labelEndTime.setText("End Time");
			// Text field : EndTime
			textEndTime = new Label(ownComposite, SWT.NONE);
			textEndTime.setLayoutData(prepareFormData(84, 12, new int[] { 0,
					1000, 300 }, new int[] { 0, 1000, 11 }));
			textEndTime.setText("00 : 00 : 00 . 000");

			// Label : WAV file information
			Label labelWFileInfo = new Label(ownComposite, SWT.NONE);
			labelWFileInfo.setLayoutData(prepareFormData(58, 12, new int[] { 0,
					1000, 5 }, new int[] { 0, 1000, 35 }));
			labelWFileInfo.setText("WAV File");

			// Text Area : Script data (Description)
			FormData textAreaWFileInfoLData = new FormData();
			textAreaWFileInfoLData.width = 415;
			textAreaWFileInfoLData.height = 40;
			textAreaWFileInfoLData.left = new FormAttachment(0, 1000, 69);
			textAreaWFileInfoLData.top = new FormAttachment(0, 1000, 35);
			textAreaWavInfo = new Text(ownComposite, SWT.MULTI | SWT.WRAP
					| SWT.BORDER | SWT.READ_ONLY);
			textAreaWavInfo.setLayoutData(textAreaWFileInfoLData);
			textAreaWavInfo
					.setText(Activator
							.getResourceString("scripteditor.tabitem.selwavfile.descstandby"));
			// Setup DnD listener
			initDnDProc(textAreaWavInfo);

			// CheckBox : "Play WAV"
			FormData chkboxPlayWavLData = new FormData();
			chkboxPlayWavLData.width = 13;
			chkboxPlayWavLData.height = 16;
			chkboxPlayWavLData.left = new FormAttachment(0, 1000, 275);
			chkboxPlayWavLData.top = new FormAttachment(0, 1000, 90);
			chkboxPlayWav = new Button(ownComposite, SWT.CHECK | SWT.LEFT);
			chkboxPlayWav.setLayoutData(chkboxPlayWavLData);
			chkboxPlayWav.setSelection(true);
			chkboxPlayWav.addMouseTrackListener(new MouseCursorTrackAdapter());

			// Label : "Play WAV"
			FormData labelPlayWavLData = new FormData();
			labelPlayWavLData.left = new FormAttachment(0, 1000, 290);
			labelPlayWavLData.top = new FormAttachment(0, 1000, 90);
			labelPlayWav = new Label(ownComposite, SWT.NONE);
			labelPlayWav.setLayoutData(labelPlayWavLData);
			labelPlayWav.setText("Play WAV");

			// Label : each parameters of Scale
			FormData labelWSpeedLData = new FormData();
			labelWSpeedLData.left = new FormAttachment(0, 1000, 500);
			labelWSpeedLData.top = new FormAttachment(0, 1000, 40);
			labelWSpeed = new Label(ownComposite, SWT.NONE);
			labelWSpeed.setLayoutData(labelWSpeedLData);
			labelWSpeed.setText("Speed");

			// Scale : each parameters
			FormData scaleWavSpeedLData = new FormData();
			scaleWavSpeedLData.width = 219;
			scaleWavSpeedLData.height = 42;
			scaleWavSpeedLData.left = new FormAttachment(0, 1000, 550); // -650
			scaleWavSpeedLData.top = new FormAttachment(0, 1000, 20); // +200
			scaleWavSpeed = new Scale(ownComposite, SWT.HORIZONTAL);
			scaleWavSpeed.setLayoutData(scaleWavSpeedLData);
			scaleWavSpeed.setMinimum(50);
			scaleWavSpeed.setMaximum(200);
			scaleWavSpeed.setIncrement(10);
			scaleWavSpeed.setSelection(100);
			scaleWavSpeed.setToolTipText("100/200");
			// Select Scale Listener
			scaleWavSpeed.addSelectionListener(new ScalePlaySpeedAdapter());
			// Tracking mouse cursor listener
			scaleWavSpeed.addMouseTrackListener(new MouseCursorTrackAdapter());

			FormData labelWavSpeedMaxLData = new FormData();
			labelWavSpeedMaxLData.left = new FormAttachment(0, 1000, 552);
			labelWavSpeedMaxLData.top = new FormAttachment(0, 1000, 65);
			labelWavSpeedMax = new Label(ownComposite, SWT.NONE);
			labelWavSpeedMax.setLayoutData(labelWavSpeedMaxLData);
			labelWavSpeedMax.setText("1/2");

			FormData labelWavSpeedMidLData = new FormData();
			labelWavSpeedMidLData.left = new FormAttachment(0, 1000, 625);
			labelWavSpeedMidLData.top = new FormAttachment(0, 1000, 65);
			labelWavSpeedMid = new Label(ownComposite, SWT.NONE);
			labelWavSpeedMid.setLayoutData(labelWavSpeedMidLData);
			labelWavSpeedMid.setText("1");

			FormData labelWavSpeedMinLData = new FormData();
			labelWavSpeedMinLData.left = new FormAttachment(0, 1000, 752);
			labelWavSpeedMinLData.top = new FormAttachment(0, 1000, 65);
			labelWavSpeedMin = new Label(ownComposite, SWT.NONE);
			labelWavSpeedMin.setLayoutData(labelWavSpeedMinLData);
			labelWavSpeedMin.setText("2");

			// Button : script data Appended action
			FormData buttonWavAppendLData = new FormData();
			buttonWavAppendLData.width = 52;
			buttonWavAppendLData.height = 22;
			buttonWavAppendLData.left = new FormAttachment(0, 1000, 69);
			buttonWavAppendLData.top = new FormAttachment(0, 1000, 86);
			buttonWavAppend = new Button(ownComposite, SWT.PUSH | SWT.CENTER);
			buttonWavAppend.setLayoutData(buttonWavAppendLData);
			// default visible : disable
			setVisibleAppendButton(false, false);

			// Append event listener
			buttonWavAppend
					.addSelectionListener(new AppendScriptButtonAdapter());
			// Tracking mouse cursor listener
			buttonWavAppend
					.addMouseTrackListener(new MouseCursorTrackAdapter());

			// Button of script data Deleted action
			FormData buttonWavDeleteLData = new FormData();
			buttonWavDeleteLData.width = 52;
			buttonWavDeleteLData.height = 22;
			buttonWavDeleteLData.left = new FormAttachment(0, 1000, 125);
			buttonWavDeleteLData.top = new FormAttachment(0, 1000, 86);
			buttonWavDelete = new Button(ownComposite, SWT.PUSH | SWT.CENTER);
			buttonWavDelete.setLayoutData(buttonWavDeleteLData);
			buttonWavDelete.setText("Delete");
			setVisibleDeleteButton(false);

			// Append event listener
			buttonWavDelete
					.addSelectionListener(new DeleteScriptButtonAdapter());
			// Tracking mouse cursor listener
			buttonWavDelete
					.addMouseTrackListener(new MouseCursorTrackAdapter());

			// Button : "Preview"
			FormData buttonWavPreviewLData = new FormData();
			buttonWavPreviewLData.width = 52;
			buttonWavPreviewLData.height = 22;
			buttonWavPreviewLData.top = new FormAttachment(0, 1000, 86);
			buttonWavPreviewLData.left = new FormAttachment(0, 1000, 201);
			buttonWavPreview = new Button(ownComposite, SWT.PUSH | SWT.CENTER);
			buttonWavPreview.setLayoutData(buttonWavPreviewLData);
			buttonWavPreview.setText("Preview");

			// default visible : disable
			setVisiblePreviewButton(false);

			// Append Preview event listener
			buttonWavPreview.addSelectionListener(new PreviewButtonAdapter());
			// Tracking mouse cursor listener
			buttonWavPreview
					.addMouseTrackListener(new MouseCursorTrackAdapter());

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

			// 1st Initialized current Window
			ownComposite.layout();
			ownComposite.pack();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Local method : initialize Select WAV file panel
	 */
	public void initDispSelWavFile() {
		// initial setup text to all item
		textStartTime.setText("00 : 00 : 00 . 000");
		textEndTime.setText("00 : 00 : 00 . 000");
		textAreaWavInfo
				.setText(Activator
						.getResourceString("scripteditor.tabitem.selwavfile.descstandby"));

		// default visible : disable
		setVisibleAppendButton(false, currentModeAppend);
		setVisibleDeleteButton(false);
		setVisiblePreviewButton(false);
	}

	/**
	 * Getter method : Get instance of own Composite
	 */
	public Composite getOwnComposite() {
		return (ownComposite);
	}

	/**
	 * Setter method : Set new mode to own tab's controls
	 * 
	 * @param newMode
	 *            : Enabled mode (TRUE:enable control, FALSE:disable control)
	 */
	public void setEnabledTab(boolean newMode) {
		// Check new mode
		if (current_tab_mode != newMode) {
			// Store new mode
			current_tab_mode = newMode;
			// SetUP new mode to all own controls
			if (current_tab_mode) {
				// Display enabled(normal) message
				textAreaWavInfo
						.setText(Activator
								.getResourceString("scripteditor.tabitem.selwavfile.descstandby"));

				// TODO : dummy button?
				buttonWavOpen.setEnabled(true);

			} else {
				// initialize all parameters
				initDescriptionData();
				// initialize own screen
				initDispSelWavFile();
				// end process
				currentSelWavFile = true;

				// Display disabled message
				textAreaWavInfo
						.setText(Activator
								.getResourceString("scripteditor.tabitem.selwavfile.cannotcontrol"));

				// TODO : dummy button?
				buttonWavOpen.setEnabled(false);

			}
		}
	}

	private void setVisibleAppendButton(boolean stat, boolean mode) {
		if (stat) {
			// update mode
			currentModeAppend = mode;
			if (mode) {
				buttonWavAppend.setText("Update");
			} else {
				buttonWavAppend.setText("Add");
			}
		} else {
			chkboxPlayWav.setSelection(true);
			scaleWavSpeed.setSelection(100);
			scaleWavSpeed.setToolTipText(String.valueOf(scaleWavSpeed
					.getSelection()) + "/200");
			currentSelWavFile = true;
		}
		buttonWavAppend.setVisible(stat);
		chkboxPlayWav.setVisible(stat);
		labelPlayWav.setVisible(stat);
		scaleWavSpeed.setVisible(stat);
		labelWSpeed.setVisible(stat);
		labelWavSpeedMax.setVisible(stat);
		labelWavSpeedMid.setVisible(stat);
		labelWavSpeedMin.setVisible(stat);
	}

	private void setVisibleDeleteButton(boolean stat) {
		buttonWavDelete.setVisible(stat);
	}

	private void setVisiblePreviewButton(boolean stat) {
		buttonWavPreview.setVisible(stat);
	}

	/**
	 * Initialize new description
	 */
	public void initDescriptionData() {
		// Initialize all parameters
		descriptionStartTime = 0;
		descriptionEndTime = 0;
		descriptionWavFile = null;
		// descriptionText = "";
		descriptionCompetitiveRatio = 1.0f;
	}

	private float calCompetitiveRatioWav() {
		float compratio = 1.0f;
		int nowValue = scaleWavSpeed.getSelection();
		compratio = (float) nowValue / 100.0f;
		return (compratio);
	}

	private int calSpeedScale(float compRatio) {
		int newPos = 100;
		newPos = (int) (compRatio * 100.0f);
		return (newPos);
	}

	/**
	 * Local method : Calculate end(duration) time of WAV
	 */
	private int calDurationTimeWav(int nowDurationTime,
			float nowCompetitiveRatio) {
		int newDurationTime = nowDurationTime;

		// Calculate duration time by new competitive ratio
		float tempDurationTime = (float) nowDurationTime / nowCompetitiveRatio;
		newDurationTime = (int) tempDurationTime;

		return (newDurationTime);
	}

	/**
	 * Setter method : Set parameters of target description
	 */
	public void setScreenData(IScriptData data) {
		this.currentData = data;

		// 1st of all, set up target description's information
		descriptionStartTime = data.getStartTime();// startTime;
		// descriptionText = strDescription;
		descriptionCompetitiveRatio = data.getWavPlaySpeed();// calCompetitiveRatioWav();

		// Set text to own screen
		textStartTime.setText(TimeFormatUtil
				.makeFormatHHMMSSMS(descriptionStartTime));

		descriptionWavFile = data.getWavURI();

		try {
			// SetUP WAV header information
			instSoundMixer.storeWavHeader(descriptionWavFile);

			textAreaWavInfo.setText(instSoundMixer
					.makeFormatWavInfo(descriptionWavFile.toString()));
			chkboxPlayWav.setSelection(data.isWavEnabled());
			descriptionCompetitiveRatio = data.getWavPlaySpeed();
			descriptionEndTime = calDurationTimeWav(
					instSoundMixer.getDurationTimeWav(),
					descriptionCompetitiveRatio);
			textEndTime.setText(TimeFormatUtil
					.makeFormatHHMMSSMS(descriptionStartTime
							+ descriptionEndTime));
			scaleWavSpeed
					.setSelection(calSpeedScale(descriptionCompetitiveRatio));
			scaleWavSpeed.setToolTipText(String.valueOf(scaleWavSpeed
					.getSelection()) + "/200");
			// Enable setting
			chkboxPlayWav.setEnabled(true);
			scaleWavSpeed.setEnabled(true);

			// Show all buttons
			setVisibleAppendButton(true, true);
			setVisibleDeleteButton(true);
			setVisiblePreviewButton(true);

			// Change status : Select WAV file mode
			currentSelWavFile = true;
		} catch (Exception we) {
			// local parameters
			String strWavInfo = "";
			String strSeparator = "\n\r";
			String strFileName = "WAV file name : ";
			String strNotice = "Notice : "
					+ Activator
							.getResourceString("scripteditor.tabitem.selwavfile.wavformerr");

			// WAV file path
			if (descriptionWavFile != null) {
				// additional display of invalidate WAV file path]
				strWavInfo = strFileName + descriptionWavFile + strSeparator;
			}
			// additional display of INVALID message
			strWavInfo = strWavInfo + strNotice + strSeparator;
			// Not WAV format or crush data
			textAreaWavInfo.setText(strWavInfo);
			// Disable setting
			chkboxPlayWav.setEnabled(false);
			scaleWavSpeed.setEnabled(false);

			descriptionCompetitiveRatio = data.getWavPlaySpeed();//
			descriptionEndTime = 0;
			textEndTime.setText(TimeFormatUtil
					.makeFormatHHMMSSMS(descriptionEndTime));

			// Show delete button
			setVisibleDeleteButton(true);
			setVisibleAppendButton(false, currentModeAppend);
			setVisiblePreviewButton(false);

			// Change status : Select WAV file mode
			currentSelWavFile = false;
		}
	}

	class PreviewButtonAdapter extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			if (currentSelWavFile) {
				instSoundMixer.startPlaySound(descriptionWavFile,
						descriptionCompetitiveRatio);
			}
		}
	}

	private IScriptData appendScriptData(IScriptData data) {
		if (data == null) {
			data = ScriptDataFactory.createNewData();
		}
		data.setType(IScriptData.TYPE_AUDIO);

		boolean nowEnableWav = chkboxPlayWav.getSelection();
		float nowCompetitiveRatio = calCompetitiveRatioWav();

		data.setStartTime(descriptionStartTime);
		data.setWavEndTime(descriptionStartTime + descriptionEndTime);
		data.setWavPlaySpeed(nowCompetitiveRatio);
		data.setWavURI(descriptionWavFile);
		data.setWavEnabled(nowEnableWav);

		return data;
	}

	class AppendScriptButtonAdapter extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			if (currentSelWavFile) {
				if (currentData != null) {

					dataEventManager.fireGuideListEvent(new GuideListEvent(
							GuideListEvent.DELETE_DATA, currentData, this));
					dataEventManager.fireLabelEvent(new LabelEvent(
							LabelEvent.DELETE_LABEL, currentData, this));

					currentData = appendScriptData(currentData);

					dataEventManager.fireGuideListEvent(new GuideListEvent(
							GuideListEvent.ADD_DATA, currentData, this));
					dataEventManager.fireLabelEvent(new LabelEvent(
							LabelEvent.PUT_LABEL, currentData, this));

					// TODO ???
					// SetUP status to Edit start mode
					// int stat = XMLFileMessageBox.MB_STYLE_MODIFY;
					// String filePath = FileInfoStore.getXmlFilePath();
					// if (filePath != null)
					// stat = XMLFileMessageBox.MB_STYLE_OVERWR;

					// Enable setting
					chkboxPlayWav.setEnabled(true);
					scaleWavSpeed.setEnabled(true);
				} else {
					// TODO : add proc
				}

				currentData = null;

				initDescriptionData();
				initDispSelWavFile();

				currentSelWavFile = true;

				// Re-draw ScriptList(Table area)
				// ScriptListView.getInstance().getInstScriptList()
				// .reloadScriptList();
			}

		}
	}

	class DeleteScriptButtonAdapter extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {

			if (currentSelWavFile) {

				// TODO recover delete all function
				// boolean deleteAll = true;
				// boolean rtn = MessageDialog
				// .openQuestion(
				// Display.getCurrent().getActiveShell(),
				// "Delete WAV information",
				// "Delete entire Audio Description information?");
				// deleteAll = rtn;

				boolean deleteAll = false;

				if (!deleteAll) {
					dataEventManager.fireGuideListEvent(new GuideListEvent(
							GuideListEvent.DELETE_DATA, currentData, this));
					dataEventManager.fireLabelEvent(new LabelEvent(
							LabelEvent.DELETE_LABEL, currentData, this));

					currentData.setWavURI(null);
					currentData.setWavEnabled(false);

					dataEventManager.fireGuideListEvent(new GuideListEvent(
							GuideListEvent.ADD_DATA, currentData, this));
					dataEventManager.fireLabelEvent(new LabelEvent(
							LabelEvent.PUT_LABEL, currentData, this));

				} else {
					dataEventManager.fireGuideListEvent(new GuideListEvent(
							GuideListEvent.DELETE_DATA, currentData, this));
					dataEventManager.fireLabelEvent(new LabelEvent(
							LabelEvent.DELETE_LABEL, currentData, this));
				}

				initDescriptionData();
				initDispSelWavFile();

				currentSelWavFile = true;
			}
		}
	}

	/**
	 * Local Class implements ButtonListener
	 */
	class OpenWavButtonAdapter extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			String[] EXTENSIONS = { "*.wav", "*" };
			String wavFileName = "";

			try {
				FileDialog openDialog = new FileDialog(Display.getCurrent()
						.getActiveShell(), SWT.OPEN);
				openDialog.setFilterExtensions(EXTENSIONS);
				wavFileName = openDialog.open();

				if (wavFileName != null) {
					// check file header
					if (WavUtil.isWavFormat(wavFileName)) {
						descriptionWavFile = TempFileUtil
								.getResource(wavFileName);
						instSoundMixer.storeWavHeader(descriptionWavFile);

						descriptionCompetitiveRatio = calCompetitiveRatioWav();
						descriptionEndTime = calDurationTimeWav(
								instSoundMixer.getDurationTimeWav(),
								descriptionCompetitiveRatio);
						textEndTime.setText(TimeFormatUtil
								.makeFormatHHMMSSMS(descriptionStartTime
										+ descriptionEndTime));
						textAreaWavInfo.setText(instSoundMixer
								.makeFormatWavInfo(descriptionWavFile
										.toString()));
						// Enable setting
						chkboxPlayWav.setEnabled(true);
						scaleWavSpeed.setEnabled(true);

						// Show all buttons
						setVisibleAppendButton(true, currentModeAppend);
						setVisibleDeleteButton(true);
						setVisiblePreviewButton(true);

						currentSelWavFile = true;
					} else {
						initDescriptionData();
						initDispSelWavFile();

						currentSelWavFile = true;
						textAreaWavInfo
								.setText(Activator
										.getResourceString("scripteditor.tabitem.selwavfile.wavformerr"));
					}
				}
			} catch (FileNotFoundException fnfe) {
			} catch (Exception we) {
			}
		}
	}

	class ScalePlaySpeedAdapter extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			Scale scale = (Scale) e.widget;
			scale.setToolTipText(String.valueOf(scale.getSelection()) + "/200");
			descriptionCompetitiveRatio = calCompetitiveRatioWav();

			descriptionEndTime = calDurationTimeWav(
					instSoundMixer.getDurationTimeWav(),
					descriptionCompetitiveRatio);
			textEndTime.setText(TimeFormatUtil
					.makeFormatHHMMSSMS(descriptionStartTime
							+ descriptionEndTime));
		}
	}

	private void initDnDProc(Text targetText) {
		// Initial setup DnD target control
		DropTarget targetDnD = new DropTarget(targetText, DND.DROP_DEFAULT
				| DND.DROP_COPY);
		targetDnD.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		targetDnD.addDropListener(new WavFileDropListener());
	}

	class WavFileDropListener extends DropTargetAdapter {
		public void dragEnter(DropTargetEvent e) {
			e.detail = DND.DROP_COPY;
		}

		public void drop(DropTargetEvent e) {
			String[] files = (String[]) e.data;

			if (current_tab_mode) {
				try {
					if (files.length > 0) {
						if (WavUtil.isWavFormat(files[0])) {
							descriptionWavFile = TempFileUtil
									.getResource(files[0]);
							instSoundMixer.storeWavHeader(descriptionWavFile);

							descriptionCompetitiveRatio = calCompetitiveRatioWav();
							descriptionEndTime = calDurationTimeWav(
									instSoundMixer.getDurationTimeWav(),
									descriptionCompetitiveRatio);
							textEndTime.setText(TimeFormatUtil
									.makeFormatHHMMSSMS(descriptionStartTime
											+ descriptionEndTime));
							textAreaWavInfo.setText(instSoundMixer
									.makeFormatWavInfo(descriptionWavFile
											.toString()));
							// Enable setting
							chkboxPlayWav.setEnabled(true);
							scaleWavSpeed.setEnabled(true);

							// Show all buttons
							setVisibleAppendButton(true, currentModeAppend);
							setVisibleDeleteButton(true);
							setVisiblePreviewButton(true);

							// Change status : Select WAV file mode
							currentSelWavFile = true;
						} else {
							initDescriptionData();
							initDispSelWavFile();

							currentSelWavFile = true;
							textAreaWavInfo
									.setText(Activator
											.getResourceString("scripteditor.tabitem.selwavfile.wavformerr"));
						}
					}
				} catch (FileNotFoundException fnfe) {
				} catch (Exception we) {
				}
			}
		}
	}
}