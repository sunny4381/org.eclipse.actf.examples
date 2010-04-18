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
package org.eclipse.actf.ai.internal.ui.scripteditor;

//for save to Localization
import java.io.FileNotFoundException;
import java.net.URI;

import org.eclipse.actf.ai.scripteditor.data.ScriptData;
import org.eclipse.actf.ai.scripteditor.util.SoundMixer;
import org.eclipse.actf.ai.scripteditor.util.TempFileUtil;
import org.eclipse.actf.ai.ui.scripteditor.views.EditPanelView;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.ai.ui.scripteditor.views.ScriptListView;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.actf.examples.scripteditor.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class SelectWAVFileTab implements IUNIT {

	/**
	 * Local data
	 */
	Composite ownComposite;

	// Own instance
	static private SelectWAVFileTab ownInst = null;
	static private Shell instParentShell = null;

	// instance of ScriptData class
	private ScriptData instScriptData = null;
	// instance of SoundMixer class
	private SoundMixer instSoundMixer = null;

	// data of target description
	private int descriptionIndex = -1;
	private int descriptionStartTime = 0;
	private int descriptionEndTime = 0;
	private URI descriptionWavFile = null;
	// private String descriptionText = "";
	private float descriptionCompetitiveRatio = 1.0f;

	// Otherwise parameters
	private boolean currentStatAppend = true;
	private boolean currentModeAppend = true;
	private boolean currentStatDelete = true;
	private boolean currentStatPreview = true;
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

	// Parameter for multiple selection mode
	private boolean current_tab_mode = true;

	// TODO : dummy button?
	private Button buttonWavOpen;

	/**
	 * Constructor
	 */
	public SelectWAVFileTab(CTabFolder parent) {
		// store own instance
		ownInst = this;
		// initial setup own TabItem
		initTab(parent);
	}

	static public SelectWAVFileTab getInstance() {
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
	public void initTab(CTabFolder parent) {
		// Create own instance of Composite
		ownComposite = new Composite(parent, SWT.NONE);

		// Get instance of data class
		instScriptData = ScriptData.getInstance();
		// Get instance of sound mixer class
		instSoundMixer = SoundMixer.getInstance();

		// Get current Display
		IWorkbench workbench = PlatformUI.getWorkbench();
		Display display = workbench.getDisplay();

		// Get parent Shell
		instParentShell = display.getActiveShell();

		// Initialize application's GUI
		initGUI(display);
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		ownComposite.setFocus();
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
			textStartTime.setText("00 : 00 . 000");

			// Label : "End Time"
			Label labelEndTime = new Label(ownComposite, SWT.NONE);
			labelEndTime.setLayoutData(prepareFormData(48, 12, new int[] { 0,
					1000, 176 }, new int[] { 0, 1000, 11 }));
			labelEndTime.setText("End Time");
			// Text field : EndTime
			textEndTime = new Label(ownComposite, SWT.NONE);
			textEndTime.setLayoutData(prepareFormData(84, 12, new int[] { 0,
					1000, 240 }, new int[] { 0, 1000, 11 }));
			textEndTime.setText("00 : 00 . 000");

			// Label : WAV file information
			Label labelWFileInfo = new Label(ownComposite, SWT.NONE);
			labelWFileInfo.setLayoutData(prepareFormData(58, 12, new int[] { 0,
					1000, 5 }, new int[] { 0, 1000, 35 }));
			labelWFileInfo.setText("WAV File");

			// Text Area : Script data (Description)
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
			// Setup DnD listener
			initDnDProc(textAreaWavInfo);

			// CheckBox : "Play WAV"
			FormData chkboxPlayWavLData = new FormData();
			chkboxPlayWavLData.width = 13;
			chkboxPlayWavLData.height = 16;
			chkboxPlayWavLData.left = new FormAttachment(0, 1000, 501);
			chkboxPlayWavLData.top = new FormAttachment(0, 1000, 4);
			chkboxPlayWav = new Button(ownComposite, SWT.CHECK | SWT.LEFT);
			chkboxPlayWav.setLayoutData(chkboxPlayWavLData);
			// Default : check on
			chkboxPlayWav.setSelection(true);
			// Tracking mouse cursor listener
			chkboxPlayWav
					.addMouseTrackListener(new ButtonMouseCursorTrackAdapter());

			// Label : "Play WAV"
			FormData labelPlayWavLData = new FormData();
			labelPlayWavLData.left = new FormAttachment(0, 1000, 518);
			labelPlayWavLData.top = new FormAttachment(0, 1000, 6);
			labelPlayWav = new Label(ownComposite, SWT.NONE);
			labelPlayWav.setLayoutData(labelPlayWavLData);
			labelPlayWav.setText("Play WAV");

			// Label : each parameters of Scale
			FormData labelWSpeedLData = new FormData();
			labelWSpeedLData.left = new FormAttachment(0, 1000, 662);
			labelWSpeedLData.top = new FormAttachment(0, 1000, 22);
			labelWSpeed = new Label(ownComposite, SWT.NONE);
			labelWSpeed.setLayoutData(labelWSpeedLData);
			labelWSpeed.setText("Speed");

			// Scale : each parameters
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
			// Select Scale Listener
			scaleWavSpeed.addSelectionListener(new ScalePlaySpeedAdapter());
			// Tracking mouse cursor listener
			scaleWavSpeed
					.addMouseTrackListener(new ScaleMouseCursorTrackAdapter());

			// Button : script data Appended action
			FormData buttonWavAppendLData = new FormData();
			buttonWavAppendLData.width = 52;
			buttonWavAppendLData.height = 22;
			buttonWavAppendLData.left = new FormAttachment(0, 1000, 69);
			buttonWavAppendLData.top = new FormAttachment(0, 1000, 126);
			buttonWavAppend = new Button(ownComposite, SWT.PUSH | SWT.CENTER);
			buttonWavAppend.setLayoutData(buttonWavAppendLData);
			// default visible : disable
			setVisibleAppend(false, false);

			// Append event listener
			buttonWavAppend
					.addSelectionListener(new AppendScriptButtonAdapter());
			// Tracking mouse cursor listener
			buttonWavAppend
					.addMouseTrackListener(new ButtonMouseCursorTrackAdapter());

			// Button of script data Deleted action
			FormData buttonWavDeleteLData = new FormData();
			buttonWavDeleteLData.width = 52;
			buttonWavDeleteLData.height = 22;
			buttonWavDeleteLData.left = new FormAttachment(0, 1000, 125);
			buttonWavDeleteLData.top = new FormAttachment(0, 1000, 126);
			buttonWavDelete = new Button(ownComposite, SWT.PUSH | SWT.CENTER);
			buttonWavDelete.setLayoutData(buttonWavDeleteLData);
			Image imgDelete = Activator.getImageDescriptor("/icons/delete.jpg")
					.createImage();
			buttonWavDelete.setImage(imgDelete);
			// default visible : disable
			setVisibleDelete(false);

			// Append event listener
			buttonWavDelete
					.addSelectionListener(new DeleteScriptButtonAdapter());
			// Tracking mouse cursor listener
			buttonWavDelete
					.addMouseTrackListener(new ButtonMouseCursorTrackAdapter());

			// Button : "Preview"
			FormData buttonWavPreviewLData = new FormData();
			buttonWavPreviewLData.width = 52;
			buttonWavPreviewLData.height = 22;
			buttonWavPreviewLData.top = new FormAttachment(0, 1000, 68);
			buttonWavPreviewLData.left = new FormAttachment(0, 1000, 501);
			buttonWavPreview = new Button(ownComposite, SWT.PUSH | SWT.CENTER);
			buttonWavPreview.setLayoutData(buttonWavPreviewLData);
			Image imgPreview = Activator.getImageDescriptor(
					"/icons/preview.jpg").createImage();
			buttonWavPreview.setImage(imgPreview);

			// default visible : disable
			setVisiblePreview(false);

			// Append Preview event listener
			buttonWavPreview.addSelectionListener(new PreviewButtonAdapter());
			// Tracking mouse cursor listener
			buttonWavPreview
					.addMouseTrackListener(new ButtonMouseCursorTrackAdapter());

			// TODO : dummy button?
			// Button : "Open" for WAV file
			FormData buttonWavOpenLData = new FormData();
			buttonWavOpenLData.top = new FormAttachment(labelWFileInfo, 16);
			buttonWavOpenLData.left = new FormAttachment(0, 1000, 5);
			buttonWavOpen = new Button(ownComposite, SWT.PUSH | SWT.CENTER);
			buttonWavOpen.setLayoutData(buttonWavOpenLData);
			buttonWavOpen.setText("OPEN");
			// Append Preview event listener
			buttonWavOpen.addSelectionListener(new OpenWavButtonAdapter());

			// 1st Initialized current Window
			ownComposite.layout();
			ownComposite.pack();

		} catch (Exception e) {
			System.out.println("SelectWAVFileTab : Exception = " + e);
		}
	}

	/**
	 * Local method : initialize Select WAV file panel
	 */
	public void initDispSelWavFile() {
		// initial setup text to all item
		textStartTime.setText("00 : 00 . 000");
		textEndTime.setText("00 : 00 . 000");
		textAreaWavInfo
				.setText(Activator
						.getResourceString("scripteditor.tabitem.selwavfile.descstandby"));

		// default visible : disable
		setVisibleAppend(false, currentModeAppend);
		setVisibleDelete(false);
		setVisiblePreview(false);
	}

	/**
	 * Getter method : Get instance of own Composite
	 */
	public Composite getOwnComposite() {
		// return instance of own Composite
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
				initDescriptionStruct();
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

	/**
	 * Local method : setVisible button Add or Update
	 */
	private void setVisibleAppend(boolean stat, boolean mode) {
		// Check current status
		if (currentStatAppend != stat) {
			// update status
			currentStatAppend = stat;
			// only enable status
			if (stat) {
				// update mode
				currentModeAppend = mode;
				// create image button
				Image imgAppend;
				if (mode) {
					// Exist Script data
					imgAppend = Activator.getImageDescriptor(
							"/icons/update.jpg").createImage();
				} else {
					// New Script data
					imgAppend = Activator.getImageDescriptor("/icons/add.jpg")
							.createImage();
				}
				// set image button
				buttonWavAppend.setImage(imgAppend);
			} else {
				// Reset check box for Enabled WAV play
				chkboxPlayWav.setSelection(true);
				// Reset scale position
				scaleWavSpeed.setSelection(100);
				scaleWavSpeed.setToolTipText(String.valueOf(scaleWavSpeed
						.getSelection())
						+ "/200");
				// end of edit description
				currentSelWavFile = true;
			}
			// set visible button
			buttonWavAppend.setVisible(stat);
			// set visible check box
			chkboxPlayWav.setVisible(stat);
			labelPlayWav.setVisible(stat);
			// set visible scale
			scaleWavSpeed.setVisible(stat);
			labelWSpeed.setVisible(stat);
		}
	}

	/**
	 * Setter method : Enable control of "Preview" button
	 */
	public void setEnablePreview(int stat) {
		// Control enable of "Preview" button
		if (stat == 0) {
			// Set enable button (status is Play mode)
			buttonWavPreview.setEnabled(true);
		} else {
			// Set disable button (status is Pause or Idle mode)
			buttonWavPreview.setEnabled(false);
		}
	}

	/**
	 * @category Getter method : Get parent shell instance
	 * @return instance of parent shell
	 */
	public Shell getParentShell() {
		// return result
		return (instParentShell);
	}

	/**
	 * Local method : setVisible button Delete
	 */
	private void setVisibleDelete(boolean stat) {
		// check current status
		if (currentStatDelete != stat) {
			// update status
			currentStatDelete = stat;
			// set visible button
			buttonWavDelete.setVisible(stat);
		}
	}

	/**
	 * Local method : setVisible button Delete
	 */
	private void setVisiblePreview(boolean stat) {
		// check current status
		if (currentStatPreview != stat) {
			// update status
			currentStatPreview = stat;
			// set visible button
			buttonWavPreview.setVisible(stat);
		}
	}

	// **********************************************************
	// target Description control part
	//
	// **********************************************************
	/**
	 * Setter method : Initialize structure of target description
	 */
	public void initDescriptionStruct() {
		// Initialize all parameters
		descriptionIndex = -1;
		descriptionStartTime = 0;
		descriptionEndTime = 0;
		descriptionWavFile = null;
		// descriptionText = "";
		descriptionCompetitiveRatio = 1.0f;
	}

	/**
	 * Local method : Calculate competitive ratio of play WAV
	 */
	private float calCompetitiveRatioWav() {
		float compratio = 1.0f;

		// PickUP current value of speed scaler
		int nowValue = scaleWavSpeed.getSelection();

		// Exchange value from scale position to competitive ratio
		compratio = (float) nowValue / 100.0f;

		// return result
		return (compratio);
	}

	/**
	 * Local method : Calculate scale position of play WAV
	 * 
	 * @param compRatio
	 * @return scaler position(selection)
	 */
	private int calSpeedScale(float compRatio) {
		int newPos = 100;

		// Exchange value from competitive ratio to scale position
		newPos = (int) (compRatio * 100.0f);

		// return result
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

		// return result
		return (newDurationTime);
	}

	/**
	 * Setter method : Set parameters of target description
	 */
	public void startDescriptionStruct(int startTime, String strDescription) {
		// 1st of all, set up target description's information
		descriptionStartTime = startTime;
		// descriptionText = strDescription;
		descriptionCompetitiveRatio = calCompetitiveRatioWav();

		// Set text to own screen
		textStartTime.setText(instScriptData
				.makeFormatMMSSMS(descriptionStartTime));

		// Check exist data of current list
		descriptionIndex = instScriptData.getIndexWavList(descriptionStartTime);
		if (descriptionIndex >= 0) {
			// PickUP WAV file path
			descriptionWavFile = instScriptData
					.getFileNameWavList(descriptionIndex);
			try {
				// SetUP WAV header information
				instSoundMixer.storeWavHeader(descriptionWavFile);

				// additional display of exist data information
				textAreaWavInfo.setText(instSoundMixer
						.makeFormatWavInfo(descriptionWavFile.toString()));
				chkboxPlayWav.setSelection(instScriptData
						.getEnableWavList(descriptionIndex));
				descriptionCompetitiveRatio = instScriptData
						.getPlaySpeedWavList(descriptionIndex);
				descriptionEndTime = calDurationTimeWav(instSoundMixer
						.getDurationTimeWav(), descriptionCompetitiveRatio);
				textEndTime.setText(instScriptData
						.makeFormatMMSSMS(descriptionStartTime
								+ descriptionEndTime));
				scaleWavSpeed
						.setSelection(calSpeedScale(descriptionCompetitiveRatio));
				scaleWavSpeed.setToolTipText(String.valueOf(scaleWavSpeed
						.getSelection())
						+ "/200");
				// Enable setting
				chkboxPlayWav.setEnabled(true);
				scaleWavSpeed.setEnabled(true);

				// Visible all button control
				setVisibleAppend(true, true);
				setVisibleDelete(true);
				setVisiblePreview(true);

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
					strWavInfo = strFileName + descriptionWavFile
							+ strSeparator;
				}
				// additional display of INVALID message
				strWavInfo = strWavInfo + strNotice + strSeparator;
				// Not WAV format or crush data
				textAreaWavInfo.setText(strWavInfo);
				// Disable setting
				chkboxPlayWav.setEnabled(false);
				scaleWavSpeed.setEnabled(false);

				// Default setting otherwise contents
				descriptionCompetitiveRatio = instScriptData
						.getPlaySpeedWavList(descriptionIndex);
				descriptionEndTime = 0;
				textEndTime.setText(instScriptData
						.makeFormatMMSSMS(descriptionEndTime));

				// Visible all button control(Enable only delete button)
				setVisibleDelete(true);
				setVisibleAppend(false, currentModeAppend);
				setVisiblePreview(false);

				// Change status : Select WAV file mode
				currentSelWavFile = false;
			}
		} else {
			// next status : drag & drop WAV file
			textEndTime.setText("00 : 00 . 000");
			textAreaWavInfo
					.setText(Activator
							.getResourceString("scripteditor.tabitem.selwavfile.wavstandby"));

			// default visible : disable
			setVisibleAppend(false, currentModeAppend);
			setVisibleDelete(false);
			setVisiblePreview(false);

			// Change status : Not yet stand-by
			currentSelWavFile = true;
		}
	}

	/**
	 * Local method : Repaint WAV file TAB screen
	 */
	public void repaintDescriptionStruct(int index) {
		// search target start time
		int startTime = instScriptData.getScriptStartTime(index);
		String strDescription = instScriptData.getScriptData(index);

		// start WAV file mode
		startDescriptionStruct(startTime, strDescription);
	}

	/**
	 * @category refresh WAV file list : Main purpose is changed StartTime value
	 */
	public void refreshScriptData(int currentStartTime, int newStartTime,
			int newEndTime, boolean dspMode) {

		// Search index of target ScriptData
		int index = instScriptData.getIndexWavList(currentStartTime);
		if (index >= 0) {
			// PickUP info of target ScriptData
			URI currentWavFileName = instScriptData.getFileNameWavList(index);
			boolean currentEnableWav = instScriptData.getEnableWavList(index);
			float currentPlaySpeedWav = instScriptData
					.getPlaySpeedWavList(index);

			// Delete target information from List
			instScriptData.deleteIndexWavList(index);

			// Update current information
			descriptionStartTime = newStartTime;
			descriptionEndTime = newEndTime - newStartTime;

			// Append script data to Script List
			instScriptData.appendDataWavList(descriptionStartTime, newEndTime,
					currentWavFileName, currentEnableWav, currentPlaySpeedWav);

			// Check status
			if (dspMode) {
				// search index of parent ScriptData
				index = instScriptData.getIndexScriptData(newStartTime);
				// repaint WAV file TAB
				repaintDescriptionStruct(index);
			}
		}
	}

	// **********************************************************
	// Button event listener part
	//
	// **********************************************************
	/**
	 * Local Class implements ButtonListener
	 */
	class PreviewButtonAdapter extends SelectionAdapter {
		// Event of Button of Preview Script(Audio)
		public void widgetSelected(SelectionEvent e) {
			// Check current status
			if (currentSelWavFile) {
				// Preview WAV file
				instSoundMixer.startPlaySound(descriptionWavFile,
						descriptionCompetitiveRatio);
			}
		}
	}

	/**
	 * Local Class implements ButtonListener
	 */
	class AppendScriptButtonAdapter extends SelectionAdapter {
		// Event of Button of Append WAV file information to current list
		public void widgetSelected(SelectionEvent e) {
			// Check current status
			if (currentSelWavFile) {
				// PickUP current check box status
				boolean nowEnableWav = chkboxPlayWav.getSelection();
				float nowCompetitiveRatio = calCompetitiveRatioWav();

				// append target data to list
				instScriptData.appendDataWavList(descriptionStartTime,
						descriptionStartTime + descriptionEndTime,
						descriptionWavFile, nowEnableWav, nowCompetitiveRatio);

				// Update new end time by target data
				int index = ScriptData.getInstance().searchScriptData(
						descriptionStartTime);
				int newEndTime = descriptionStartTime + descriptionEndTime;
				if (index >= 0) {
					if (!nowEnableWav) {
						// Use end time of voice engine
						newEndTime = ScriptData.getInstance().getScriptEndTime(
								index);
					}
					// Repaint audio label by end time of WAV data
					EditPanelView.getInstance().getInstanceTabEditPanel()
							.reqUpdateEndTimeAudioLabel(index, newEndTime);

					// SetUP status to Edit start mode
					int stat = MB_STYLE_MODIFY;
					String filePath = TimeLineView.getInstance()
							.reqGetXMLFilePath();
					if (filePath != null)
						stat = MB_STYLE_OVERWR;
					instScriptData.setStatusSaveScripts(stat, true);

					// Enable setting
					chkboxPlayWav.setEnabled(true);
					scaleWavSpeed.setEnabled(true);
				}

				// initialize all parameters
				initDescriptionStruct();
				// initialize own screen
				initDispSelWavFile();
				// end process
				currentSelWavFile = true;

				// Re-draw ScriptList(Table area)
				ScriptListView.getInstance().getInstScriptList()
						.reloadScriptList();
			}
		}
	}

	/**
	 * Local Class implements ButtonListener
	 */
	class DeleteScriptButtonAdapter extends SelectionAdapter {
		// Event of Button of Delete WAV file information from current list
		public void widgetSelected(SelectionEvent e) {
			// Check current status
			if (currentSelWavFile) {
				// delete target data from current list
				instScriptData.deleteStartTimeWavList(descriptionStartTime);

				// Update new end time by target data
				int index = ScriptData.getInstance().searchScriptData(
						descriptionStartTime);
				if (index >= 0) {
					// Use end time of voice engine
					int newEndTime = ScriptData.getInstance().getScriptEndTime(
							index);
					// Repaint audio label by end time of WAV data
					EditPanelView.getInstance().getInstanceTabEditPanel()
							.reqUpdateEndTimeAudioLabel(index, newEndTime);

					// SetUP status to Edit start mode
					int stat = MB_STYLE_MODIFY;
					String filePath = TimeLineView.getInstance()
							.reqGetXMLFilePath();
					if (filePath != null)
						stat = MB_STYLE_OVERWR;
					instScriptData.setStatusSaveScripts(stat, true);
				}

				// initialize all parameters
				initDescriptionStruct();
				// initialize own screen
				initDispSelWavFile();
				// end process
				currentSelWavFile = true;

				// Re-draw ScriptList(Table area)
				ScriptListView.getInstance().getInstScriptList()
						.reloadScriptList();
			}
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
					if (instSoundMixer.isWavFormat(wavFileName)) {
						// PickUP file header from current WAV file
						descriptionWavFile = TempFileUtil
								.getResource(wavFileName);
						instSoundMixer.storeWavHeader(descriptionWavFile);

						// additional display of exist data information
						descriptionCompetitiveRatio = calCompetitiveRatioWav();
						descriptionEndTime = calDurationTimeWav(instSoundMixer
								.getDurationTimeWav(),
								descriptionCompetitiveRatio);
						textEndTime.setText(instScriptData
								.makeFormatMMSSMS(descriptionStartTime
										+ descriptionEndTime));
						textAreaWavInfo.setText(instSoundMixer
								.makeFormatWavInfo(descriptionWavFile
										.toString()));
						// Enable setting
						chkboxPlayWav.setEnabled(true);
						scaleWavSpeed.setEnabled(true);

						// Visible all button control
						setVisibleAppend(true, currentModeAppend);
						setVisibleDelete(true);
						setVisiblePreview(true);

						// Change status : Select WAV file mode
						currentSelWavFile = true;
					} else {
						// initialize all parameters
						initDescriptionStruct();
						// initialize own screen
						initDispSelWavFile();
						// end process
						currentSelWavFile = true;
						// no WAV format or crush data
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

	// **********************************************************
	// Mouse event listener part
	//
	// **********************************************************
	/**
	 * Local Class extends MouseTrackAdapter for Button
	 */
	class ButtonMouseCursorTrackAdapter extends MouseTrackAdapter {
		// mouse cursor enter into parent area
		public void mouseEnter(MouseEvent e) {
			// Changer Cursor image from ARROW type to HAND type
			Button parentButton = (Button) e.getSource();
			parentButton.setCursor(new Cursor(null, SWT.CURSOR_HAND));
		}

		// mouse cursor exit parent area
		public void mouseExit(MouseEvent e) {
			// Reset Cursor image to default type (ARROW)
			Button parentButton = (Button) e.getSource();
			parentButton.setCursor(new Cursor(null, SWT.CURSOR_ARROW));
		}
	}

	/**
	 * Local Class extends MouseTrackAdapter for Scale
	 */
	class ScaleMouseCursorTrackAdapter extends MouseTrackAdapter {
		// mouse cursor enter into parent area
		public void mouseEnter(MouseEvent e) {
			// Changer Cursor image from ARROW type to HAND type
			Scale parentScale = (Scale) e.getSource();
			parentScale.setCursor(new Cursor(null, SWT.CURSOR_HAND));
		}

		// mouse cursor exit parent area
		public void mouseExit(MouseEvent e) {
			// Reset Cursor image to default type (ARROW)
			Scale parentScale = (Scale) e.getSource();
			parentScale.setCursor(new Cursor(null, SWT.CURSOR_ARROW));
		}
	}

	/**
	 * Local method : SelectionAdapter for scale of WAV speed setting
	 * 
	 */
	class ScalePlaySpeedAdapter extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			// Get current scale data
			Scale scale = (Scale) e.widget;
			// Update ToolTip Text for Scale of Volume
			scale.setToolTipText(String.valueOf(scale.getSelection()) + "/200");
			// Update competitive ratio
			descriptionCompetitiveRatio = calCompetitiveRatioWav();

			// Update duration(end) time
			descriptionEndTime = calDurationTimeWav(instSoundMixer
					.getDurationTimeWav(), descriptionCompetitiveRatio);
			textEndTime
					.setText(instScriptData
							.makeFormatMMSSMS(descriptionStartTime
									+ descriptionEndTime));
		}
	}

	// **********************************************************
	// DnD part
	//
	// **********************************************************
	/**
	 * Setter method : Initial DnD target adapter
	 */
	private void initDnDProc(Text targetText) {
		// Initial setup DnD target control
		DropTarget targetDnD = new DropTarget(targetText, DND.DROP_DEFAULT
				| DND.DROP_COPY);
		targetDnD.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		targetDnD.addDropListener(new WavFileDropListener());
	}

	/**
	 * Local class : Drag & Drop WAV file
	 * 
	 */
	class WavFileDropListener extends DropTargetAdapter {
		// drag start event
		public void dragEnter(DropTargetEvent e) {
			e.detail = DND.DROP_COPY;
		}

		// drop to target event
		public void drop(DropTargetEvent e) {
			String[] files = (String[]) e.data;

			// Check multiple selection mode
			if (current_tab_mode) {
				try {
					if (files.length > 0) {
						// check file header
						if (instSoundMixer.isWavFormat(files[0])) {
							// PickUP file header from current WAV file
							descriptionWavFile = TempFileUtil
									.getResource(files[0]);
							instSoundMixer.storeWavHeader(descriptionWavFile);

							// additional display of exist data information
							descriptionCompetitiveRatio = calCompetitiveRatioWav();
							descriptionEndTime = calDurationTimeWav(
									instSoundMixer.getDurationTimeWav(),
									descriptionCompetitiveRatio);
							textEndTime.setText(instScriptData
									.makeFormatMMSSMS(descriptionStartTime
											+ descriptionEndTime));
							textAreaWavInfo.setText(instSoundMixer
									.makeFormatWavInfo(descriptionWavFile
											.toString()));
							// Enable setting
							chkboxPlayWav.setEnabled(true);
							scaleWavSpeed.setEnabled(true);

							// Visible all button control
							setVisibleAppend(true, currentModeAppend);
							setVisibleDelete(true);
							setVisiblePreview(true);

							// Change status : Select WAV file mode
							currentSelWavFile = true;
						} else {
							// initialize all parameters
							initDescriptionStruct();
							// initialize own screen
							initDispSelWavFile();
							// end process
							currentSelWavFile = true;
							// no WAV format or crush data
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
