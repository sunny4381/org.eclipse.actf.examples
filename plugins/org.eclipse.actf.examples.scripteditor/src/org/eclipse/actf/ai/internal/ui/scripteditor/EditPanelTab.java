/*******************************************************************************
 * Copyright (c) 2009, 2011 IBM Corporation and Others
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
import java.net.URI;
import java.util.Locale;

import org.eclipse.actf.ai.internal.ui.scripteditor.event.EventManager;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.SyncTimeEvent;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.SyncTimeEventListener;
import org.eclipse.actf.ai.scripteditor.data.ScriptData;
import org.eclipse.actf.ai.scripteditor.preferences.CSVRulePreferenceUtil;
import org.eclipse.actf.ai.ui.scripteditor.views.EditPanelView;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.ai.ui.scripteditor.views.ScriptListView;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.actf.examples.scripteditor.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class EditPanelTab implements IUNIT, SyncTimeEventListener {

	/**
	 * Local data
	 */
	Composite ownComposite;

	// Own instance
	static private EditPanelTab ownInst = null;

	// instance of ScriptData class
	private ScriptData instScriptData;

	// for Audio Label
	private boolean makeupAudioLabelStatus = false;
	private int indexAudioLabel = -1;
	private String strAudioLabel = null;

	// Other parameters
	private int currentEndTime = 0;
	private boolean currentStatAppend = true;
	private boolean currentModeAppend = true;
	private boolean currentStatDelete = true;
	private boolean currentStatCancel = true;
	private boolean currentStatPreview = true;
	private boolean currentEditDescription = false;

	// for Multiple selection mode
	private boolean currentMultiSelection = false;
	private Object[] storeObjs = null;
	private boolean modifyMultiExtended = false;
	private boolean modifyMultiGender = false;
	private boolean modifyMultiLang = false;
	private boolean modifyMultiSpeed = false;
	private boolean modifyMultiPitch = false;
	private boolean modifyMultiVolume = false;

	// store start time for current edit script
	private int updateScriptStartTime = 0;

	// Language of description
	private int currentDescLang = DESC_LANG_EN;

	// Edit Panel part
	private Label labelVPitch;
	private Label labelVVolume;
	private Label labelVSpeed;
	private Scale scaleVoiceVolume;
	private Scale scaleVoicePitch;
	private Scale scaleVoiceSpeed;
	private Label labelCaution;
	private Label labelImageSpeaker;
	private Button buttonVoicePreview;
	private Label labelFemale;
	private Button rButtonFemale;
	private Label labelMale;
	private Button rButtonMale;
	private Label labelExtended;
	private Button chkBoxExtended;
	private Text textAreaDescription;
	private Label labelDescription;
	private Label textEndTime;
	private Label labelEndTime;
	private Label labelStartTime;
	private Button buttonVoiceAppend;
	private Button buttonVoiceDelete;
	private Button buttonVoiceCancel;
	private Label labelLang;
	private Combo comboLang;

	private Text textStartTimeMM;
	private Label labelStartTimeMM;
	private Text textStartTimeSS;
	private Label labelStartTimeSS;
	private Text textStartTimeMS;

	private static EventManager eventManager = null;

	/**
	 * Constructor
	 */
	public EditPanelTab(CTabFolder parent) {
		// store own instance
		ownInst = this;
		// store event lister
		eventManager = EventManager.getInstance();
		// initial setup
		initTab(parent);
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

		// Create Data class
		instScriptData = ScriptData.getInstance();

		// Get current Display
		IWorkbench workbench = PlatformUI.getWorkbench();
		Display display = workbench.getDisplay();

		// Initialize application's GUI
		initGUI(display);
		// Add eventListener
		eventManager.addSyncTimeEventListener(this);

		parent.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				// TODO other components
				eventManager.removeSyncTimeEventListener(ownInst);
			}
		});
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
		FormData tmpData = new FormData(SWT.DEFAULT, SWT.DEFAULT);// TODO:
																	// temporary
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
			// **<Edit panel>***********************************
			FormLayout groupEditPanelLayout = new FormLayout();
			FormData groupEditPanelLData = new FormData(925, 128);
			groupEditPanelLData.top = new FormAttachment(0, 1000, 0);
			groupEditPanelLData.left = new FormAttachment(0, 1000, 0);
			groupEditPanelLData.right = new FormAttachment(1000, 1000, 0);
			groupEditPanelLData.bottom = new FormAttachment(1000, 1000, 0);
			ownComposite.setLayoutData(groupEditPanelLData);
			ownComposite.setLayout(groupEditPanelLayout);

			// 1)Time scale of current script
			// MM
			textStartTimeMM = new Text(ownComposite, SWT.BORDER);
			textStartTimeMM.setLayoutData(prepareFormData(12, 12, new int[] {
					0, 1000, 78 }, new int[] { 0, 1000, 9 }));
			textStartTimeMM.setTextLimit(2);

			labelStartTimeMM = new Label(ownComposite, SWT.NONE);
			labelStartTimeMM.setLayoutData(prepareFormData(4, 12, new int[] {
					0, 1000, 100 }, new int[] { 0, 1000, 11 }));
			labelStartTimeMM.setText(":");

			// SS
			textStartTimeSS = new Text(ownComposite, SWT.BORDER);
			textStartTimeSS.setLayoutData(prepareFormData(12, 12, new int[] {
					0, 1000, 104 }, new int[] { 0, 1000, 9 }));
			textStartTimeSS.setTextLimit(2);

			labelStartTimeSS = new Label(ownComposite, SWT.NONE);
			labelStartTimeSS.setLayoutData(prepareFormData(4, 12, new int[] {
					0, 1000, 126 }, new int[] { 0, 1000, 11 }));
			labelStartTimeSS.setText(".");

			// millisecond
			textStartTimeMS = new Text(ownComposite, SWT.BORDER);
			textStartTimeMS.setLayoutData(prepareFormData(18, 12, new int[] {
					0, 1000, 130 }, new int[] { 0, 1000, 9 }));
			textStartTimeMS.setTextLimit(3);

			// initial Text
			textStartTimeMM.setText("00");
			textStartTimeSS.setText("00");
			textStartTimeMS.setText("000");

			// Append SelectionListener
			textStartTimeMM.addListener(SWT.Verify,
					new DefaultNumCheckListener());
			textStartTimeSS.addListener(SWT.Verify, new TimeSSNumCheck());
			textStartTimeMS.addListener(SWT.Verify,
					new DefaultNumCheckListener());

			// Label : "Start Time"
			labelStartTime = new Label(ownComposite, SWT.NONE);
			labelStartTime.setLayoutData(prepareFormData(54, 12, new int[] { 0,
					1000, 5 }, new int[] { 0, 1000, 11 }));
			labelStartTime.setText("Start Time");

			// Label : "End Time"
			textEndTime = new Label(ownComposite, SWT.NONE);
			textEndTime.setLayoutData(prepareFormData(84, 12, new int[] { 0,
					1000, 240 }, new int[] { 0, 1000, 11 }));
			textEndTime.setText("00 : 00 . 000");

			labelEndTime = new Label(ownComposite, SWT.NONE);
			labelEndTime.setLayoutData(prepareFormData(48, 12, new int[] { 0,
					1000, 176 }, new int[] { 0, 1000, 11 }));
			labelEndTime.setText("End Time");

			// Label : Description
			labelDescription = new Label(ownComposite, SWT.NONE);
			labelDescription.setLayoutData(prepareFormData(58, 12, new int[] {
					0, 1000, 2 }, new int[] { 0, 1000, 35 }));
			labelDescription.setText("Description");

			// Text Area : Script data (Description)
			int scrollbarWidth = 17;
			FormData textAreaDescriptionLData = new FormData();
			textAreaDescriptionLData.width = 415 - scrollbarWidth;
			textAreaDescriptionLData.height = 80;
			textAreaDescriptionLData.left = new FormAttachment(0, 1000, 79);
			textAreaDescriptionLData.top = new FormAttachment(0, 1000, 35);
			textAreaDescription = new Text(ownComposite, SWT.MULTI | SWT.WRAP
					| SWT.BORDER | SWT.V_SCROLL);
			textAreaDescription.setLayoutData(textAreaDescriptionLData);

			// Append SelectionListener
			textAreaDescription.addListener(SWT.Modify,
					new descriptionModifyListener());

			// Button : script data Appended action
			FormData buttonVoiceAppendLData = new FormData();
			buttonVoiceAppendLData.width = 52;
			buttonVoiceAppendLData.height = 22;
			buttonVoiceAppendLData.left = new FormAttachment(0, 1000, 69);
			buttonVoiceAppendLData.top = new FormAttachment(0, 1000, 126);
			buttonVoiceAppend = new Button(ownComposite, SWT.PUSH | SWT.CENTER);
			buttonVoiceAppend.setLayoutData(buttonVoiceAppendLData);
			// default visible : disable
			setVisibleAppend(false, false);

			// Append event listener
			buttonVoiceAppend
					.addSelectionListener(new AppendScriptButtonAdapter());
			// Tracking mouse cursor listener
			buttonVoiceAppend
					.addMouseTrackListener(new ButtonMouseCursorTrackAdapter());

			// Button of script data Deleted action
			FormData buttonVoiceDeleteLData = new FormData();
			buttonVoiceDeleteLData.width = 52;
			buttonVoiceDeleteLData.height = 22;
			buttonVoiceDeleteLData.left = new FormAttachment(0, 1000, 125);
			buttonVoiceDeleteLData.top = new FormAttachment(0, 1000, 126);
			buttonVoiceDelete = new Button(ownComposite, SWT.PUSH | SWT.CENTER);
			buttonVoiceDelete.setLayoutData(buttonVoiceDeleteLData);
			Image imgDelete = Activator.getImageDescriptor("/icons/delete.jpg")
					.createImage();
			buttonVoiceDelete.setImage(imgDelete);
			// default visible : disable
			setVisibleDelete(false);

			// Append event listener
			buttonVoiceDelete
					.addSelectionListener(new DeleteScriptButtonAdapter());
			// Tracking mouse cursor listener
			buttonVoiceDelete
					.addMouseTrackListener(new ButtonMouseCursorTrackAdapter());

			// Button of script data Canceled action
			FormData buttonVoiceCancelLData = new FormData();
			buttonVoiceCancelLData.width = 52;
			buttonVoiceCancelLData.height = 22;
			buttonVoiceCancelLData.left = new FormAttachment(0, 1000, 181);
			buttonVoiceCancelLData.top = new FormAttachment(0, 1000, 126);
			buttonVoiceCancel = new Button(ownComposite, SWT.PUSH | SWT.CENTER);
			buttonVoiceCancel.setLayoutData(buttonVoiceCancelLData);
			Image imgCancel = Activator.getImageDescriptor("/icons/cancel.jpg")
					.createImage();
			buttonVoiceCancel.setImage(imgCancel);
			// default visible : disable
			setVisibleCancel(false);

			// Append event listener
			buttonVoiceCancel
					.addSelectionListener(new CancelScriptButtonAdapter());
			// Tracking mouse cursor listener
			buttonVoiceCancel
					.addMouseTrackListener(new ButtonMouseCursorTrackAdapter());

			// Label : description of "Preview" button
			FormData labelCautionLData = new FormData();
			labelCautionLData.left = new FormAttachment(0, 1000, 504);
			labelCautionLData.top = new FormAttachment(0, 1000, 94);
			labelCaution = new Label(ownComposite, SWT.NONE);
			labelCaution.setLayoutData(labelCautionLData);
			labelCaution.setText("Play Audio Description");

			// ++ Speaker Image
			FormData labelImageSpeakerLData = new FormData();
			labelImageSpeakerLData.width = 16;
			labelImageSpeakerLData.height = 20;
			labelImageSpeakerLData.left = new FormAttachment(0, 1000, 622);
			labelImageSpeakerLData.top = new FormAttachment(0, 1000, 90);
			labelImageSpeaker = new Label(ownComposite, SWT.NONE);
			labelImageSpeaker.setLayoutData(labelImageSpeakerLData);
			Image imgSpeaker = Activator.getImageDescriptor(
					"/icons/speaker.bmp").createImage();
			labelImageSpeaker.setImage(imgSpeaker);

			// Button : "Preview"
			FormData buttonVoicePreviewLData = new FormData();
			buttonVoicePreviewLData.width = 52;
			buttonVoicePreviewLData.height = 22;
			buttonVoicePreviewLData.left = new FormAttachment(0, 1000, 501);
			buttonVoicePreviewLData.top = new FormAttachment(0, 1000, 68);
			buttonVoicePreview = new Button(ownComposite, SWT.PUSH | SWT.CENTER);
			buttonVoicePreview.setLayoutData(buttonVoicePreviewLData);
			// buttonVoicePreview.setText("Preview");
			Image imgPreview = Activator.getImageDescriptor(
					"/icons/preview.jpg").createImage();
			buttonVoicePreview.setImage(imgPreview);

			// default visible : disable
			setVisiblePreview(false);

			// Append Preview event listener
			buttonVoicePreview.addSelectionListener(new PreviewButtonAdapter());
			// Tracking mouse cursor listener
			buttonVoicePreview
					.addMouseTrackListener(new ButtonMouseCursorTrackAdapter());

			// **<Extended>***********************************************:
			// CheckBox : "Extended"
			FormData labelExtendedLData = new FormData();
			labelExtendedLData.left = new FormAttachment(0, 1000, 518);
			labelExtendedLData.top = new FormAttachment(0, 1000, 6);
			labelExtended = new Label(ownComposite, SWT.NONE);
			labelExtended.setLayoutData(labelExtendedLData);
			labelExtended.setText("Extended");
			labelExtended.setVisible(true);

			FormData chkBoxExtendedLData = new FormData();
			chkBoxExtendedLData.width = 13;
			chkBoxExtendedLData.height = 16;
			chkBoxExtendedLData.left = new FormAttachment(0, 1000, 501);
			chkBoxExtendedLData.top = new FormAttachment(0, 1000, 4);
			chkBoxExtended = new Button(ownComposite, SWT.CHECK | SWT.LEFT);
			chkBoxExtended.setLayoutData(chkBoxExtendedLData);

			// Default : no check
			chkBoxExtended.setSelection(false);
			// Tracking mouse cursor listener
			chkBoxExtended
					.addMouseTrackListener(new ButtonMouseCursorTrackAdapter());
			// SetUP selection listener for multiple selection mode
			chkBoxExtended.addListener(SWT.Selection,
					new ExtendExtendedListener());
			chkBoxExtended.setVisible(true);

			// Label : "Male"
			FormData labelMaleLData = new FormData();
			labelMaleLData.left = new FormAttachment(0, 1000, 518);
			labelMaleLData.top = new FormAttachment(0, 1000, 28);
			labelMale = new Label(ownComposite, SWT.NONE);
			labelMale.setLayoutData(labelMaleLData);
			labelMale.setText("Male");

			FormData rButtonMaleLData = new FormData();
			rButtonMaleLData.left = new FormAttachment(0, 1000, 501);
			rButtonMaleLData.top = new FormAttachment(0, 1000, 26);
			rButtonMale = new Button(ownComposite, SWT.RADIO | SWT.LEFT);
			rButtonMale.setLayoutData(rButtonMaleLData);

			// Default : Male selected
			rButtonMale.setSelection(true);
			// Tracking mouse cursor listener
			rButtonMale
					.addMouseTrackListener(new ButtonMouseCursorTrackAdapter());
			// SetUP selection listener for multiple selection mode
			rButtonMale.addListener(SWT.Selection, new ExtendGenderListener());

			// Label : "Female"
			FormData labelFemaleLData = new FormData();
			labelFemaleLData.left = new FormAttachment(0, 1000, 572);
			labelFemaleLData.top = new FormAttachment(0, 1000, 28);
			labelFemale = new Label(ownComposite, SWT.NONE);
			labelFemale.setLayoutData(labelFemaleLData);
			labelFemale.setText("Female");

			FormData rButtonFemaleLData = new FormData();
			rButtonFemaleLData.left = new FormAttachment(0, 1000, 555);
			rButtonFemaleLData.top = new FormAttachment(0, 1000, 26);
			rButtonFemale = new Button(ownComposite, SWT.RADIO | SWT.LEFT);
			rButtonFemale.setLayoutData(rButtonFemaleLData);

			// Tracking mouse cursor listener
			rButtonFemale
					.addMouseTrackListener(new ButtonMouseCursorTrackAdapter());
			// SetUP selection listener for multiple selection mode
			rButtonFemale
					.addListener(SWT.Selection, new ExtendGenderListener());

			// Language of Description
			// **Label**
			FormData labelLangLData = new FormData();
			labelLangLData.left = new FormAttachment(0, 1000, 501);
			labelLangLData.top = new FormAttachment(textAreaDescription, -4);
			labelLang = new Label(ownComposite, SWT.NONE);
			labelLang.setLayoutData(labelLangLData);
			labelLang.setText("Language");
			// **ComboBox**
			FormData comboLangLData = new FormData();
			comboLangLData.left = new FormAttachment(0, 1000, 501);
			comboLangLData.top = new FormAttachment(labelLang, 2);
			comboLang = new Combo(ownComposite, SWT.DROP_DOWN);
			comboLang.setLayoutData(comboLangLData);
			comboLang.setItems(itemLang);
			if (Locale.getDefault().toString().startsWith("ja")) {
				currentDescLang = DESC_LANG_JA;
			} else {
				// Default : English selected
				currentDescLang = DESC_LANG_EN;
			}
			comboLang.select(currentDescLang);
			// Add EventListener
			comboLang.addListener(SWT.Selection, new DescLangListener());

			// Label : each parameters of Scale
			FormData labelVSpeedLData = new FormData();
			labelVSpeedLData.left = new FormAttachment(0, 1000, 662);
			labelVSpeedLData.top = new FormAttachment(0, 1000, 22);
			labelVSpeed = new Label(ownComposite, SWT.NONE);
			labelVSpeed.setLayoutData(labelVSpeedLData);
			labelVSpeed.setText("Speed");

			FormData labelVPitchLData = new FormData();
			labelVPitchLData.left = new FormAttachment(0, 1000, 667);
			labelVPitchLData.top = new FormAttachment(0, 1000, 68);
			labelVPitch = new Label(ownComposite, SWT.NONE);
			labelVPitch.setLayoutData(labelVPitchLData);
			labelVPitch.setText("Pitch");
			labelVPitch.setVisible(false);

			FormData labelVVolumeLData = new FormData();
			labelVVolumeLData.left = new FormAttachment(0, 1000, 656);
			labelVVolumeLData.top = new FormAttachment(0, 1000, 118);
			labelVVolume = new Label(ownComposite, SWT.NONE);
			labelVVolume.setLayoutData(labelVVolumeLData);
			labelVVolume.setText("Volume");
			labelVVolume.setVisible(false);

			// Scale : each parameters
			FormData scaleVoiceSpeedLData = new FormData();
			scaleVoiceSpeedLData.width = 219;
			scaleVoiceSpeedLData.height = 42;
			scaleVoiceSpeedLData.left = new FormAttachment(0, 1000, 700);
			scaleVoiceSpeedLData.top = new FormAttachment(0, 1000, 6);
			scaleVoiceSpeed = new Scale(ownComposite, SWT.HORIZONTAL);
			scaleVoiceSpeed.setLayoutData(scaleVoiceSpeedLData);
			scaleVoiceSpeed.setMinimum(0);
			scaleVoiceSpeed.setMaximum(100);
			scaleVoiceSpeed.setIncrement(10);
			scaleVoiceSpeed.setSelection(50);
			scaleVoiceSpeed.setToolTipText("50/100");

			// Select Scale Listener
			scaleVoiceSpeed.addSelectionListener(new ScaleSelectionAdapter());
			// Tracking mouse cursor listener
			scaleVoiceSpeed
					.addMouseTrackListener(new ScaleMouseCursorTrackAdapter());

			FormData scaleVoicePitchLData = new FormData();
			scaleVoicePitchLData.width = 219;
			scaleVoicePitchLData.height = 42;
			scaleVoicePitchLData.left = new FormAttachment(0, 1000, 700);
			scaleVoicePitchLData.top = new FormAttachment(0, 1000, 54);
			scaleVoicePitch = new Scale(ownComposite, SWT.NONE);
			scaleVoicePitch.setLayoutData(scaleVoicePitchLData);
			scaleVoicePitch.setMinimum(0);
			scaleVoicePitch.setMaximum(100);
			scaleVoicePitch.setIncrement(10);
			scaleVoicePitch.setSelection(50);
			scaleVoicePitch.setToolTipText("50/100");
			scaleVoicePitch.setVisible(false);

			// Select Scale Listener
			scaleVoicePitch.addSelectionListener(new ScaleSelectionAdapter());
			// Tracking mouse cursor listener
			scaleVoicePitch
					.addMouseTrackListener(new ScaleMouseCursorTrackAdapter());

			FormData scaleVoiceVolumeLData = new FormData();
			scaleVoiceVolumeLData.width = 219;
			scaleVoiceVolumeLData.height = 42;
			scaleVoiceVolumeLData.left = new FormAttachment(0, 1000, 700);
			scaleVoiceVolumeLData.top = new FormAttachment(0, 1000, 102);
			scaleVoiceVolume = new Scale(ownComposite, SWT.NONE);
			scaleVoiceVolume.setLayoutData(scaleVoiceVolumeLData);
			scaleVoiceVolume.setMinimum(0);
			scaleVoiceVolume.setMaximum(100);
			scaleVoiceVolume.setIncrement(10);
			scaleVoiceVolume.setSelection(50);
			scaleVoiceVolume.setToolTipText("50/100");
			scaleVoiceVolume.setVisible(false);

			// Select Scale Listener
			scaleVoiceVolume.addSelectionListener(new ScaleSelectionAdapter());
			// Tracking mouse cursor listener
			scaleVoiceVolume
					.addMouseTrackListener(new ScaleMouseCursorTrackAdapter());

			// 1st Initialized current Window
			ownComposite.layout();
			ownComposite.pack();

		} catch (Exception e) {
			System.out.println("EditPanelTab : Exception = " + e);
		}
	}

	/**
	 * Getter method : Get instance of own Composite
	 */
	public Composite getOwnComposite() {
		// return instance of own Composite
		return (ownComposite);
	}

	/**
	 * Local method : PopUP error message box
	 */
	public void popupWarningNoScriptData() {
		// PopUP Error Message Box
		XMLFileMessageBox noDescMB = new XMLFileMessageBox(MB_STYLE_NODESC,
				null);
		noDescMB.open();
	}

	public void popupWarningNoExistData() {
		// PopUP Error Message Box
		XMLFileMessageBox noExistMB = new XMLFileMessageBox(MB_STYLE_NOEXIST,
				null);
		noExistMB.open();
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
				buttonVoiceAppend.setImage(imgAppend);
			} else {
				// end of edit description
				currentEditDescription = false;
			}
			// set visible button
			buttonVoiceAppend.setVisible(stat);
		}
	}

	/**
	 * Setter method : Enable control of "Preview" button
	 */
	public void setEnablePreview(int stat) {
		// Control enable of "Preview" button
		if (stat == 0) {
			// Set enable button (status is Play mode)
			buttonVoicePreview.setEnabled(true);
		} else {
			// Set disable button (status is Pause or Idle mode)
			buttonVoicePreview.setEnabled(false);
		}
	}

	/**
	 * Setter method : Set Focus text area (use edit description)
	 */
	public void setFocusDescriptionTextArea() {
		// Set Focus own Composite
		this.setFocus();
		// Set Focus text area
		textAreaDescription.setFocus();
	}

	/**
	 * Getter method : Get current StartTime of Text area
	 */
	public int getStarTimeEditPanel() {
		// Get index(StartTime)
		int startTime = instScriptData.parseIntStartTime(
				textStartTimeMM.getText(), textStartTimeSS.getText(),
				textStartTimeMS.getText());

		// return result
		return (startTime);
	}

	/**
	 * Getter method : Get current Language of Description
	 */
	public int getLangDescription() {
		return (currentDescLang);
	}

	/**
	 * Setter method : Set current Language of Description
	 */
	public void setLangDescription(int lindex) {
		currentDescLang = lindex;
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
			buttonVoiceDelete.setVisible(stat);
		}
	}

	/**
	 * Local method : setVisible button Cancel
	 */
	private void setVisibleCancel(boolean stat) {
		// check current status
		if (currentStatCancel != stat) {
			// update status
			currentStatCancel = stat;
			// set visible button
			buttonVoiceCancel.setVisible(stat);
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
			buttonVoicePreview.setVisible(stat);
			// set otherwise contents
			labelCaution.setVisible(stat);
			labelImageSpeaker.setVisible(stat);
		}
	}

	/**
	 * Getter method : Get current value of each widget
	 */
	public boolean getCurrentGender() {
		return (rButtonMale.getSelection());
	}

	public int getCurrentSpeed() {
		return (scaleVoiceSpeed.getSelection());
	}

	public int getCurrentPitch() {
		return (scaleVoicePitch.getSelection());
	}

	public int getCurrentVolume() {
		return (scaleVoiceVolume.getSelection());
	}

	public String getCurrentDescription() {
		return (textAreaDescription.getText());
	}

	/**
	 * Method for Edit Panel's components
	 */
	public void repaintTextScriptData(int index) {
		// Get target Start Time data by parameter(index)
		int intScriptStartTime = instScriptData.getScriptStartTime(index);

		// Set new text data for Script Data
		textStartTimeMM
				.setText(instScriptData.makeFormatMM(intScriptStartTime));
		textStartTimeSS
				.setText(instScriptData.makeFormatSS(intScriptStartTime));
		textStartTimeMS
				.setText(instScriptData.makeFormatMS(intScriptStartTime));

		// Set new selections for Extended components.
		chkBoxExtended.setSelection(instScriptData.getExtendExtended(index));
		if (instScriptData.getExtendGender(index)) {
			// select Male
			rButtonFemale.setSelection(false);
			rButtonMale.setSelection(true);
		} else {
			// select Female
			rButtonMale.setSelection(false);
			rButtonFemale.setSelection(true);
		}
		scaleVoiceSpeed.setSelection(instScriptData.getExtendSpeed(index));
		scaleVoicePitch.setSelection(instScriptData.getExtendPitch(index));
		scaleVoiceVolume.setSelection(instScriptData.getExtendVolume(index));
		// Update ToolTip Text for Scale of Speed
		scaleVoiceSpeed.setToolTipText(String.valueOf(scaleVoiceSpeed
				.getSelection()) + "/100");
		scaleVoicePitch.setToolTipText(String.valueOf(scaleVoicePitch
				.getSelection()) + "/100");
		scaleVoiceVolume.setToolTipText(String.valueOf(scaleVoiceVolume
				.getSelection()) + "/100");

		// Clear position of Slider
		TimeLineView.getInstance().repaintTimeLine();
		// Clear Canvas
		// /// TimeLineView.getInstance().reqRedrawVolumeLevelCanvas(1);

		// get index(StartTime) to End Time(initial)
		// // int idx =
		// instScriptData.getIndexScriptData(textStartTimeMM.getText(),
		// textStartTimeSS.getText(), textStartTimeMS.getText());
		// // int idx = instScriptData.searchScriptData(intScriptStartTime);
		int endTime = instScriptData.getScriptEndTime(index);
		textEndTime.setText(instScriptData.makeFormatMMSSMS(endTime));

		// Set Language of Description
		currentDescLang = instScriptData.getExtendLang(index);
		comboLang.select(currentDescLang);

		// Set visible button
		setVisibleAppend(true, true);
		setVisibleDelete(true);
		setVisibleCancel(true);
		setVisiblePreview(true);

		// SetUP Description
		textAreaDescription.setText(instScriptData.getScriptData(index));

		// BackUP current Start Time value for Update action
		updateScriptStartTime = intScriptStartTime;
	}

	/**
	 * Start multiple items selection mode
	 * 
	 * @param targetDatas
	 *            [] : instance of ScriptData of multiple items
	 */
	public void startSelectMultiItems(Object[] targetObjs) {
		// Store selection items
		storeObjs = targetObjs;

		// Cancel current action
		initDispEditPanel();
		VolumeLevelCanvas.getInstance().clearSamplingLengthVolumeLevel();

		// Disable WAV file selection TAB
		EditPanelView.getInstance().setSelectMultiItemsMode(true);

		// Exchange screen to multiple items selection mode
		setMultiSelectMode(true);

		// Set visible button without Preview button
		setVisibleAppend(true, true);
		setVisibleDelete(true);
		setVisibleCancel(true);
	}

	/**
	 * Append(modify) selection items object
	 * 
	 * @param targetDatas
	 *            [] : instance of ScriptData of multiple items
	 */
	public void appendSelectMultiItems(Object[] targetObjs) {
		// Store selection items
		storeObjs = targetObjs;
	}

	/**
	 * End multiple items selection mode
	 */
	public void endSelectMultiItems() {
		// Disable WAV file selection TAB
		EditPanelView.getInstance().setSelectMultiItemsMode(false);

		// Exchange screen to normal mode
		setMultiSelectMode(false);
		storeObjs = null;
		modifyMultiExtended = false;
		modifyMultiGender = false;
		modifyMultiLang = false;
		modifyMultiSpeed = false;
		modifyMultiPitch = false;
		modifyMultiVolume = false;

		// default visible : disable
		setVisibleAppend(false, currentModeAppend);
		setVisibleDelete(false);
		setVisibleCancel(false);
	}

	/**
	 * Setter method : Set new mode to own tab's controls
	 * 
	 * @param newMode
	 *            : Selection mode (TRUE:Multiple selection mode,
	 *            FALSE:Singleton selection mode)
	 */
	public void setMultiSelectMode(boolean newMode) {
		// Check new mode
		if (currentMultiSelection != newMode) {
			// Store new mode
			currentMultiSelection = newMode;
			// SetUP new mode to all own controls
			if (currentMultiSelection) {
				// Disable one of controls
				textStartTimeMM.setEnabled(false);
				textStartTimeSS.setEnabled(false);
				textStartTimeMS.setEnabled(false);
				textAreaDescription.setEnabled(false);
				// Set gray color setting for all items
				setGrayedExtendParam(true);
			} else {
				// Reset selection mode of ScriptList Table
				ScriptListView.getInstance().getInstScriptList()
						.setCurrentSelectionMode(false);
				// Enable one of controls
				textStartTimeMM.setEnabled(true);
				textStartTimeSS.setEnabled(true);
				textStartTimeMS.setEnabled(true);
				textAreaDescription.setEnabled(true);
				// Reset gray color setting for all items
				setGrayedExtendParam(false);
			}
		}
	}

	// TODO
	/**
	 * Set grayed color setting for multiple selection items
	 * 
	 * @param sw
	 *            : setup mode(TRUE:set grayed color, FALSE:set normal color)
	 */
	private void setGrayedExtendParam(boolean sw) {
		// Check new status
		if (sw) {
			// PickUP 1st script's data
			ScriptData firstData = (ScriptData) storeObjs[0];
			int index = instScriptData.getIndexScriptData(firstData
					.getScriptStartTime(0));
			boolean extended = instScriptData.getExtendExtended(index);
			boolean gender = instScriptData.getExtendGender(index);
			int lang = instScriptData.getExtendLang(index);
			int speed = instScriptData.getExtendSpeed(index);
			int pitch = instScriptData.getExtendPitch(index);
			int volume = instScriptData.getExtendVolume(index);

			// Set grayed color when different current setting
			if (isDiffExtended()) {
				chkBoxExtended.setSelection(true);
				chkBoxExtended.setGrayed(true);
			} else {
				chkBoxExtended.setSelection(extended);
			}
			if (isDiffGender()) {
				rButtonMale.setGrayed(true);
				rButtonFemale.setGrayed(true);
			}
			if (gender) {
				rButtonMale.setSelection(true);
			} else {
				rButtonFemale.setSelection(true);
			}
			if (isDiffSpeed()) {
				scaleVoiceSpeed.setBackground(PlatformUI.getWorkbench()
						.getDisplay()
						.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
			}
			scaleVoiceSpeed.setSelection(speed);
			if (isDiffPitch()) {
				scaleVoicePitch.setBackground(PlatformUI.getWorkbench()
						.getDisplay()
						.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
			}
			scaleVoicePitch.setSelection(pitch);
			if (isDiffVolume()) {
				scaleVoiceVolume.setBackground(PlatformUI.getWorkbench()
						.getDisplay()
						.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
			}
			scaleVoiceVolume.setSelection(volume);
			if (isDiffLang()) {
				comboLang.setForeground(PlatformUI.getWorkbench().getDisplay()
						.getSystemColor(SWT.COLOR_GRAY));
				comboLang.setBackground(PlatformUI.getWorkbench().getDisplay()
						.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			}
			comboLang.select(lang);
		} else {
			// Reset grayed color setting
			chkBoxExtended.setGrayed(false);
			rButtonMale.setGrayed(false);
			rButtonFemale.setGrayed(false);
			scaleVoiceSpeed.setBackground(PlatformUI.getWorkbench()
					.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			scaleVoicePitch.setBackground(PlatformUI.getWorkbench()
					.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			scaleVoiceVolume.setBackground(PlatformUI.getWorkbench()
					.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			comboLang.setForeground(PlatformUI.getWorkbench().getDisplay()
					.getSystemColor(SWT.COLOR_BLACK));
			comboLang.setBackground(PlatformUI.getWorkbench().getDisplay()
					.getSystemColor(SWT.COLOR_WHITE));
		}
	}

	private boolean isDiffExtended() {
		boolean result = false;
		boolean firstData;

		// PickUP 1st data
		ScriptData wrkData = (ScriptData) storeObjs[0];
		int index = instScriptData.getIndexScriptData(wrkData
				.getScriptStartTime(0));
		firstData = instScriptData.getExtendExtended(index);

		// Check different value all items
		for (int i = 1; i < storeObjs.length; i++) {
			// PickUP 1st data
			wrkData = (ScriptData) storeObjs[i];
			index = instScriptData.getIndexScriptData(wrkData
					.getScriptStartTime(0));
			boolean nextData = instScriptData.getExtendExtended(index);
			// Check value
			if (firstData != nextData) {
				// detect different value
				result = true;
				break;
			}
		}
		// return result
		return (result);
	}

	private boolean isDiffGender() {
		boolean result = false;
		boolean firstData;

		// PickUP 1st data
		ScriptData wrkData = (ScriptData) storeObjs[0];
		int index = instScriptData.getIndexScriptData(wrkData
				.getScriptStartTime(0));
		firstData = instScriptData.getExtendGender(index);

		// Check different value all items
		for (int i = 1; i < storeObjs.length; i++) {
			// PickUP 1st data
			wrkData = (ScriptData) storeObjs[i];
			index = instScriptData.getIndexScriptData(wrkData
					.getScriptStartTime(0));
			boolean nextData = instScriptData.getExtendGender(index);
			// Check value
			if (firstData != nextData) {
				// detect different value
				result = true;
				break;
			}
		}
		// return result
		return (result);
	}

	private boolean isDiffLang() {
		boolean result = false;
		int firstData;

		// PickUP 1st data
		ScriptData wrkData = (ScriptData) storeObjs[0];
		int index = instScriptData.getIndexScriptData(wrkData
				.getScriptStartTime(0));
		firstData = instScriptData.getExtendLang(index);

		// Check different value all items
		for (int i = 1; i < storeObjs.length; i++) {
			// PickUP 1st data
			wrkData = (ScriptData) storeObjs[i];
			index = instScriptData.getIndexScriptData(wrkData
					.getScriptStartTime(0));
			int nextData = instScriptData.getExtendLang(index);
			// Check value
			if (firstData != nextData) {
				// detect different value
				result = true;
				break;
			}
		}
		// return result
		return (result);
	}

	private boolean isDiffSpeed() {
		boolean result = false;
		int firstData;

		// PickUP 1st data
		ScriptData wrkData = (ScriptData) storeObjs[0];
		int index = instScriptData.getIndexScriptData(wrkData
				.getScriptStartTime(0));
		firstData = instScriptData.getExtendSpeed(index);

		// Check different value all items
		for (int i = 1; i < storeObjs.length; i++) {
			// PickUP 1st data
			wrkData = (ScriptData) storeObjs[i];
			index = instScriptData.getIndexScriptData(wrkData
					.getScriptStartTime(0));
			int nextData = instScriptData.getExtendSpeed(index);
			// Check value
			if (firstData != nextData) {
				// detect different value
				result = true;
				break;
			}
		}
		// return result
		return (result);
	}

	private boolean isDiffPitch() {
		boolean result = false;
		int firstData;

		// PickUP 1st data
		ScriptData wrkData = (ScriptData) storeObjs[0];
		int index = instScriptData.getIndexScriptData(wrkData
				.getScriptStartTime(0));
		firstData = instScriptData.getExtendPitch(index);

		// Check different value all items
		for (int i = 1; i < storeObjs.length; i++) {
			// PickUP 1st data
			wrkData = (ScriptData) storeObjs[i];
			index = instScriptData.getIndexScriptData(wrkData
					.getScriptStartTime(0));
			int nextData = instScriptData.getExtendPitch(index);
			// Check value
			if (firstData != nextData) {
				// detect different value
				result = true;
				break;
			}
		}
		// return result
		return (result);
	}

	private boolean isDiffVolume() {
		boolean result = false;
		int firstData;

		// PickUP 1st data
		ScriptData wrkData = (ScriptData) storeObjs[0];
		int index = instScriptData.getIndexScriptData(wrkData
				.getScriptStartTime(0));
		firstData = instScriptData.getExtendVolume(index);

		// Check different value all items
		for (int i = 1; i < storeObjs.length; i++) {
			// PickUP 1st data
			wrkData = (ScriptData) storeObjs[i];
			index = instScriptData.getIndexScriptData(wrkData
					.getScriptStartTime(0));
			int nextData = instScriptData.getExtendVolume(index);
			// Check value
			if (firstData != nextData) {
				// detect different value
				result = true;
				break;
			}
		}
		// return result
		return (result);
	}

	/**
	 * Check modified all voice parameters
	 * 
	 * @return check result (TRUE:Modified parameters, FALSE:Not Modified)
	 */
	private boolean isModifiedVoiceParam() {
		// return result
		return (modifyMultiExtended || modifyMultiGender || modifyMultiLang
				|| modifyMultiSpeed || modifyMultiPitch || modifyMultiVolume);

	}

	/**
	 * Setter method : Repaint EndTime of Edit view
	 */
	public void repaintTextEndTime() {
		// PickUP current text
		if(scaleVoiceSpeed.isDisposed()){
			return;
		}
		int extendSpeed = scaleVoiceSpeed.getSelection();
		int extendLang = currentDescLang;
		String currentDesc = textAreaDescription.getText();
		// PickUP EndTime of current description
		int newEndTime = TimeLineView.getInstance().setEndTimeVolumeLevel(
				currentDesc, extendSpeed, extendLang);
		// Set text EndTime of current description
		textEndTime.setText(instScriptData.makeFormatMMSSMS(newEndTime));
	}

	/**
	 * Setter Method : Set new "End Time" by "Start Time" & Process Time of
	 * VoicePlayer
	 */
	public int setEndTimeScriptData(int lenData) {

		// Get index(StartTime)
		int startTime = instScriptData.parseIntStartTime(
				textStartTimeMM.getText(), textStartTimeSS.getText(),
				textStartTimeMS.getText());

		// Calc. new End Time & Set string to Text field
		int newEndTime = startTime + lenData;
		textEndTime.setText(instScriptData.makeFormatMMSSMS(newEndTime));

		// SetUP new Audio Label
		makeupAudioLabel(newEndTime);

		// return end time
		return (newEndTime);
	}

	/**
	 * Setter method : start save data within Preview Voice for EndTime
	 */
	private void preMakeupAudioLabel(int index, String strAudio) {
		// Pre process for MakeUP Audio Label
		makeupAudioLabelStatus = true;
		// Store target index of ScriptList
		indexAudioLabel = index;
		// Store target text of Audio
		strAudioLabel = strAudio;
	}

	/**
	 * Setter method : start save data within Preview Voice for EndTime
	 */
	private void makeupAudioLabel(int newEndTime) {
		// Check current status
		if (isMakeupAudioLabel()) {
			// PickUP StartTime & Audio length
			int startTime = instScriptData.getScriptStartTime(indexAudioLabel);
			boolean extended = instScriptData
					.getExtendExtended(indexAudioLabel);

			// Check status of choose WAV
			boolean enableWav = false;
			int index = instScriptData.getIndexWavList(startTime);
			// Exist target data
			if (index >= 0) {
				// Only chosen WAV
				enableWav = instScriptData.getEnableWavList(index);
			}
			// Only use voice engine
			if (!enableWav) {
				// Create new Label & Put on Composite
				TimeLineView.getInstance().putScriptAudio(indexAudioLabel,
						startTime, newEndTime, strAudioLabel, extended);
			}

			// post process
			postMakeupAudioLabel();
		}
	}

	/**
	 * Setter method : start save data within Preview Voice for EndTime
	 */
	private void postMakeupAudioLabel() {
		// Pre process for MakeUP Audio Label
		makeupAudioLabelStatus = false;
		indexAudioLabel = -1;
		strAudioLabel = null;
	}

	/**
	 * Checker method : Check current action status for MakeUP Audio Label
	 */
	private boolean isMakeupAudioLabel() {
		// Return current action status
		return (makeupAudioLabelStatus);
	}

	/**
	 * Setter method : Request update end time of target audio label
	 * 
	 * @param index
	 * @param newEndTime
	 */
	public void reqUpdateEndTimeAudioLabel(int index, int newEndTime) {
		// PickUP StartTime & Audio length
		int startTime = instScriptData.getScriptStartTime(index);
		String strAudioLabel = instScriptData.getScriptData(index);
		boolean extended = instScriptData.getExtendExtended(index);

		// Create new Label & Put on Composite
		TimeLineView.getInstance().putScriptAudio(index, startTime, newEndTime,
				strAudioLabel, extended);
	}

	/**
	 * @category Setter method
	 * @purpose Synchronize TimeLine for EditPanel view
	 * 
	 */
	public void synchronizeTimeLine(int nowTime) {
		// Check current Edit status
		if (!buttonVoiceAppend.getVisible()) {
			// Set new text data for Script Data
			textStartTimeMM.setText(instScriptData.makeFormatMM(nowTime));
			textStartTimeSS.setText(instScriptData.makeFormatSS(nowTime));
			textStartTimeMS.setText(instScriptData.makeFormatMS(nowTime));
		}
	}

	/**
	 * Local method : Play Voice for Save Script to ScriptList
	 */
	private void playPreviewDescription(String currentScriptText) {
		// Check current Voice Manager
		if (TimeLineView.getInstance().isSamplingScriptAudio()) {
			// Stop Voice Manager
			TimeLineView.getInstance().reqStopScriptAudio();
		}

		// Clear Canvas
		// ////TimeLineView.getInstance().reqRedrawVolumeLevelCanvas(1);

		// Call Voice Manager
		try {
			// Get current Extended status & SetUP Voice Manager
			String strGender = rButtonMale.getSelection() ? "male" : "female";
			int speed = scaleVoiceSpeed.getSelection();
			int pitch = scaleVoicePitch.getSelection();
			int volume = scaleVoiceVolume.getSelection();

			// SetUP Voice Manage
			TimeLineView.getInstance().reqSetupScriptAudio(strGender, speed,
					pitch, volume);
			// Play voice(Script Audio)
			TimeLineView.getInstance().reqPlayScriptAudio(currentScriptText);

			// Control disabled "Play/Pause" button
			// ///PreviewPanelView.getInstance().setEnablePlayPause(false);

		} catch (Exception ee) {
			System.out.println("playPreviewDescription() : Exception = " + ee);
		}
	}

	/**
	 * Local Class implements ButtonListener
	 */
	class PreviewButtonAdapter extends SelectionAdapter {
		// Event of Button of Preview Script(Audio)
		public void widgetSelected(SelectionEvent e) {
			// Get current Script text from Text Area
			String currentScriptText = new String(textAreaDescription.getText());

			// Check length of String
			if (!currentScriptText.isEmpty()) {
				// Preview Voice
				playPreviewDescription(currentScriptText);
			}
		}
	}

	/**
	 * 
	 * @category SelectionAdapter of Append button
	 * 
	 */
	class AppendScriptButtonAdapter extends SelectionAdapter {
		// Event of Button of Append Script(to ScriptList)
		public void widgetSelected(SelectionEvent e) {
			// Check current selection mode
			if (!currentMultiSelection) {
				// Singleton mode
				appendSingleScript();
			} else {
				// Check modified status
				if (isModifiedVoiceParam()) {
					// Multiple mode
					updateMultiScripts();
				}
				// Change normal mode
				endSelectMultiItems();
			}
		}

		/**
		 * Append, Update singleton script
		 */
		private void appendSingleScript() {

			// local parameters
			boolean result = false;
			int index = -1;
			String strDesc = textAreaDescription.getText();
			int startTime = instScriptData.parseIntStartTime(
					textStartTimeMM.getText(), textStartTimeSS.getText(),
					textStartTimeMS.getText());
			int newEndTime = currentEndTime;

			// status of update(start time) mode flag
			boolean update_mode_flg = false;

			// Extended parameters
			Boolean extendExtended = chkBoxExtended.getSelection();
			Boolean extendSex = rButtonMale.getSelection() ? true : false;
			int extendSpeed = scaleVoiceSpeed.getSelection();
			int extendPitch = scaleVoicePitch.getSelection();
			int extendVolume = scaleVoiceVolume.getSelection();
			int extendLang = currentDescLang;

			// Check Update button mode
			if (currentModeAppend && (startTime != updateScriptStartTime)) {
				// Check exist script data
				index = instScriptData
						.getIndexScriptData(updateScriptStartTime);
				if (index >= 0) {
					// exist data
					result = true;
					/**
					 * // Compare to new description and old description if(
					 * strDesc.equals(instScriptData.getScriptData(index)) ){
					 **/
					// Calculate new WAV end time
					int startTimeWav = 0;
					int endTimeWav = 0;
					int indexWav = instScriptData
							.getIndexWavList(updateScriptStartTime);
					if (indexWav >= 0) {
						startTimeWav = instScriptData
								.getStartTimeWavList(indexWav);
						endTimeWav = instScriptData.getEndTimeWavList(indexWav);
						endTimeWav = (endTimeWav - startTimeWav) + startTime;
						if (ScriptData.getInstance().getEnableWavList(indexWav)) {
							newEndTime = endTimeWav;
						}
					}

					// Request Refresh ScriptList to parent View
					TimeLineView.getInstance().reqRefreshScriptData(
							updateScriptStartTime, startTime, currentEndTime,
							endTimeWav, false);
					// SetUP update status flag
					update_mode_flg = true;

					/**
					 * // Delete current Audio Label
					 * ScriptAudioComposite.getInstance
					 * ().deleteAudioLabel(updateScriptStartTime); // Reset
					 * position for target Audio Label
					 * ScriptAudioComposite.getInstance().putAudioLabel(index,
					 * startTime, newEndTime, strDesc, extendExtended); } // New
					 * data (add data) else { // Add script data to Script List
					 * result = instScriptData.appendScriptData(strDesc,
					 * textStartTimeMM.getText(), textStartTimeSS.getText(),
					 * textStartTimeMS.getText(), currentEndTime); }
					 **/
				}
			}
			// Add button mode
			else {
				// Add script data to Script List
				result = instScriptData.appendScriptData(strDesc,
						textStartTimeMM.getText(), textStartTimeSS.getText(),
						textStartTimeMS.getText(), currentEndTime);
			}

			// check result status
			if (result) {
				// If result status is SUCCESS, then repaint End Time
				index = instScriptData.getIndexScriptData(startTime);
				// check index
				if (index >= 0) {
					// update Extended data
					instScriptData.appendExtendData(index, startTime,
							extendExtended, extendSex, extendSpeed,
							extendPitch, extendVolume, extendLang);

					// MakeUP new Audio Label
					preMakeupAudioLabel(index, strDesc);

					// Update VoluemLevel's View
					newEndTime = TimeLineView.getInstance()
							.setEndTimeVolumeLevel(strDesc, extendSpeed,
									extendLang);

					// Merge end time to current script data
					instScriptData.updateScriptEndTime(startTime, newEndTime);

					// SetUP status to Edit start mode
					int stat = MB_STYLE_MODIFY;
					String filePath = TimeLineView.getInstance()
							.reqGetXMLFilePath();
					if (filePath != null)
						stat = MB_STYLE_OVERWR;
					instScriptData.setStatusSaveScripts(stat, true);

					// Expand Composite of TimeLine
					TimeLineView.getInstance().reqExpandTimeLine();

					// Seek location of TimeLine view
					TimeLineView.getInstance().reqSetupTimeLine();

					// Reset buffer of preview description
					VolumeLevelCanvas.getInstance()
							.clearSamplingLengthVolumeLevel();
				}
			}
			// No data
			else {
				popupWarningNoScriptData();
			}

			// Reset end time for display Label
			int indexWav = instScriptData.getIndexWavList(startTime);
			if (indexWav >= 0) {
				if (ScriptData.getInstance().getEnableWavList(indexWav)) {
					// PickUP WAV's end time
					newEndTime = instScriptData.getEndTimeWavList(indexWav);
				}
			}

			// Check status flag
			if (update_mode_flg) {
				// Delete current Audio Label
				ScriptAudioComposite.getInstance().deleteAudioLabel(
						updateScriptStartTime);
			}
			// Reset position for target Audio Label
			ScriptAudioComposite.getInstance().putAudioLabel(index, startTime,
					newEndTime, strDesc, extendExtended);
			// Redraw all labels for exchange color
			ScriptAudioComposite.getInstance().redrawAudioLabelAll();

			// Re-draw ScriptList(Table area)
			ScriptListView.getInstance().getInstScriptList().reloadScriptList();

			// Initialize Edit Panel contents
			initDispEditPanel();

			// initialize all parameters
			EditPanelView.getInstance().getInstanceTabSelWAVFile()
					.initDescriptionStruct();
			// initialize own screen
			EditPanelView.getInstance().getInstanceTabSelWAVFile()
					.initDispSelWavFile();
		}

		/**
		 * Update multiple scripts Note : not multiple append mode
		 */
		private void updateMultiScripts() {

			// Check exist data list
			if (storeObjs != null) {
				// Reset buffer of preview description
				VolumeLevelCanvas.getInstance()
						.clearSamplingLengthVolumeLevel();

				// PickUP extend parameters from edit tab
				Boolean extendExtended = chkBoxExtended.getSelection();
				Boolean extendGender = rButtonMale.getSelection() ? true
						: false;
				int extendSpeed = scaleVoiceSpeed.getSelection();
				int extendPitch = scaleVoicePitch.getSelection();
				int extendVolume = scaleVoiceVolume.getSelection();
				int extendLang = currentDescLang;

				// Start delete multiple data
				for (int i = 0; i < storeObjs.length; i++) {
					// Get next script data
					ScriptData tempScriptData = (ScriptData) storeObjs[i];
					int startTime = tempScriptData.getScriptStartTime(0);
					int newEndTime = tempScriptData.getScriptEndTime(0);
					String strDesc = tempScriptData.getScriptData(0);
					int index = instScriptData.searchScriptData(startTime);
					// SetUP start time text for end time
					textStartTimeMM.setText(instScriptData
							.makeFormatMM(startTime));
					textStartTimeSS.setText(instScriptData
							.makeFormatSS(startTime));
					textStartTimeMS.setText(instScriptData
							.makeFormatMS(startTime));

					// PickUP not modified parameters
					if (!modifyMultiExtended) {
						// current extended
						extendExtended = instScriptData
								.getExtendExtended(index);
					}
					if (!modifyMultiGender) {
						// current extended
						extendGender = instScriptData.getExtendGender(index);
					}
					if (!modifyMultiLang) {
						// current extended
						extendLang = instScriptData.getExtendLang(index);
					}
					if (!modifyMultiSpeed) {
						// current extended
						extendSpeed = instScriptData.getExtendSpeed(index);
					}
					if (!modifyMultiPitch) {
						// current extended
						extendPitch = instScriptData.getExtendPitch(index);
					}
					if (!modifyMultiVolume) {
						// current extended
						extendVolume = instScriptData.getExtendVolume(index);
					}

					// Update extend parameters to target script
					if (index >= 0) {
						// update Extended data
						instScriptData.appendExtendData(index, startTime,
								extendExtended, extendGender, extendSpeed,
								extendPitch, extendVolume, extendLang);

						// Update VoluemLevel's View
						newEndTime = TimeLineView.getInstance()
								.setEndTimeVolumeLevel(strDesc, extendSpeed,
										extendLang);

						// MakeUP new Audio Label
						preMakeupAudioLabel(index, strDesc);

						// Merge end time to current script data
						instScriptData.updateScriptEndTime(startTime,
								newEndTime);

						// Calculate new WAV end time
						int endTimeWav = 0;
						int indexWav = instScriptData
								.getIndexWavList(startTime);
						if (indexWav >= 0) {
							endTimeWav = instScriptData
									.getEndTimeWavList(indexWav);
							if (ScriptData.getInstance().getEnableWavList(
									indexWav)) {
								newEndTime = endTimeWav;
							}
						}

						// Delete current Audio Label
						ScriptAudioComposite.getInstance().deleteAudioLabel(
								startTime);
						// Reset position for target Audio Label
						ScriptAudioComposite.getInstance().putAudioLabel(index,
								startTime, newEndTime, strDesc, extendExtended);
					}
				}

				// SetUP status to Edit start mode
				int stat = MB_STYLE_MODIFY;
				String filePath = TimeLineView.getInstance()
						.reqGetXMLFilePath();
				if (filePath != null)
					stat = MB_STYLE_OVERWR;
				instScriptData.setStatusSaveScripts(stat, true);

				// Expand Composite of TimeLine
				TimeLineView.getInstance().reqExpandTimeLine();
				// Seek location of TimeLine view
				TimeLineView.getInstance().reqSetupTimeLine();

				// Redraw all labels for exchange color
				ScriptAudioComposite.getInstance().redrawAudioLabelAll();
				// Re-draw ScriptList(Table area)
				ScriptListView.getInstance().getInstScriptList()
						.reloadScriptList();
			}
		}
	}

	/**
	 * @category Append ScriptData from XML file
	 * @param strStart
	 * @param strDuration
	 * @param strDescription
	 * @param extended
	 * @param extGender
	 * @param extSpeed
	 * @param extPitch
	 * @param extVolume
	 * @param extLang
	 */
	public void appendScriptData(String strStart, String strDuration,
			String strDescription, String extended, String extGender,
			String extSpeed, String extPitch, String extVolume, String extLang) {

		// MakeUP StartTime
		int startTime = instScriptData.parseIntStartTime(strStart);
		// MakeUP EndTime
		int duration = instScriptData.parseIntStartTime(strDuration);
		int endTime = startTime + duration;

		// Append script data to Script List
		boolean result = instScriptData.appendScriptData(strDescription,
				startTime, endTime);

		// check result status
		if (result) {
			// If result status is SUCCESS, then repaint End Time
			int index = instScriptData.getIndexScriptData(startTime);
			// check index
			if (index >= 0) {
				// Extended parameters
				Boolean extendExtended = ("true".equals(extended)) ? true
						: false;
				Boolean extendSex = ("male".equals(extGender)) ? true : false;
				int extendSpeed = Integer.parseInt(extSpeed);
				int extendPitch = Integer.parseInt(extPitch);
				int extendVolume = Integer.parseInt(extVolume);
				int extendLang = "ja".equals(extLang) ? DESC_LANG_JA
						: DESC_LANG_EN;

				// update Extended data
				instScriptData.appendExtendData(index, startTime,
						extendExtended, extendSex, extendSpeed, extendPitch,
						extendVolume, extendLang);
			}
		}
	}

	/**
	 * @category Append ScriptData from CSV file
	 * @param strStart
	 * @param strDescription
	 * @return result process : TRUE:success process, FALSE:faile process
	 */
	public boolean appendScriptData(int startTime, String strDescription,
			boolean extended, boolean gender, int lang, int speed, int pitch,
			int volume) {

		// Extended parameters
		Boolean extendExtended = extended;
		Boolean extendGender = gender;
		int extendSpeed = speed;
		int extendPitch = pitch;
		int extendVolume = volume;
		int extendLang = lang;
		int index = -1;

		// Check preference setting of CSV save rule
		if (CSV_SAVE_RULE_INSERT == CSVRulePreferenceUtil
				.getPreferenceCsvSaveRule()) {
			// If current rule is insert mode, then pickup current parameters.
			index = instScriptData.getIndexScriptData(startTime);
			if (index >= 0) {
				extendExtended = instScriptData.getExtendExtended(index);
				extendGender = instScriptData.getExtendGender(index);
				extendSpeed = instScriptData.getExtendSpeed(index);
				extendPitch = instScriptData.getExtendPitch(index);
				extendVolume = instScriptData.getExtendVolume(index);
				extendLang = instScriptData.getExtendLang(index);
			}
		}

		// MakeUP EndTime
		int endTime = startTime
				+ TimeLineView.getInstance().sumMoraCount(strDescription,
						extendSpeed, extendLang);

		// Append script data to Script List
		boolean result = instScriptData.appendScriptData(strDescription,
				startTime, endTime);

		// check result status
		if (result) {
			// If result status is SUCCESS, then repaint End Time
			index = instScriptData.getIndexScriptData(startTime);
			// check index
			if (index >= 0) {
				// update Extended data
				instScriptData.appendExtendData(index, startTime,
						extendExtended, extendGender, extendSpeed, extendPitch,
						extendVolume, extendLang);

				// SetUP status to Edit start mode
				instScriptData.setStatusSaveScripts(MB_STYLE_MODIFY, true);
			}
		}

		// return result
		return (result);
	}

	/**
	 * Setter method : Update WAV information to WAV file list
	 * 
	 * @param strStartTime
	 * @param strEndTime
	 * @param strLocalUri
	 * @param strSpeed
	 */
	public void appendDataWavList(String strStartTime, String strDurationTime,
			String strLocalUri, String strSpeed, boolean enable) {
		int wavStartTime;
		int wavEndTime;
		URI wavFileUri;
		float wavCompRatio;

		// Convert target type from read parameters
		wavStartTime = instScriptData.parseIntStartTime(strStartTime);
		wavEndTime = wavStartTime
				+ instScriptData.parseIntStartTime(strDurationTime);
		wavFileUri = URI.create(strLocalUri);
		// Salvage previous format version(float type data)
		wavCompRatio = Float.parseFloat(strSpeed);
		if ((wavCompRatio >= 50.0f) && (wavCompRatio <= 200.0f)) {
			// Exchange ratio to float type
			wavCompRatio = wavCompRatio / 100.0f;
		}

		// Update WAV list
		instScriptData.appendDataWavList(wavStartTime, wavEndTime, wavFileUri,
				enable, wavCompRatio);
	}

	/**
	 * Setter method : Update WAV information from CSV file
	 * 
	 * @param startTime
	 * @param endTime
	 * @param strLocalUri
	 * @param speed
	 */
	public void appendDataWavList(int startTime, int duration, URI localUri,
			boolean enable, float speed) {
		int wavEndTime;
		float wavCompRatio;
		boolean wavEnabled;

		// Convert target type from read parameters
		wavEndTime = startTime + duration;
		wavCompRatio = speed;
		wavEnabled = ((enable && (duration != WAV_STAT_INVALID)) ? true : false);

		// Update WAV list
		instScriptData.appendDataWavList(startTime, wavEndTime, localUri,
				wavEnabled, wavCompRatio);
	}

	/**
	 * Local method : initialize Edit area
	 */
	public void initDispEditPanel() {

		// If result is SUCCESS(true), then clear text area
		textAreaDescription.setText("");

		// Seek to current time line marker's position
		int nowTimeLine = TimeLineCanvas.getInstance()
				.getCurrentPositionMarkerTimeLine();
		textStartTimeMM.setText(instScriptData.makeFormatMM(nowTimeLine));
		textStartTimeSS.setText(instScriptData.makeFormatSS(nowTimeLine));
		textStartTimeMS.setText(instScriptData.makeFormatMS(nowTimeLine));
		textEndTime.setText(instScriptData.makeFormatMMSSMS(nowTimeLine));

		// If result is SUCCESS(true), then reset Extended selections
		chkBoxExtended.setSelection(false);
		scaleVoiceSpeed.setSelection(50);
		scaleVoicePitch.setSelection(50);
		scaleVoiceVolume.setSelection(50);
		// Update ToolTip Text for Scale of Speed
		scaleVoiceSpeed.setToolTipText(String.valueOf(scaleVoiceSpeed
				.getSelection()) + "/100");
		scaleVoicePitch.setToolTipText(String.valueOf(scaleVoicePitch
				.getSelection()) + "/100");
		scaleVoiceVolume.setToolTipText(String.valueOf(scaleVoiceVolume
				.getSelection()) + "/100");

		/**
		 * Keep current setting ************************* // Set Gender
		 * selection rButtonFemale.setSelection(false);
		 * rButtonMale.setSelection(true); // Set Language of Description
		 * currentDescLang = ((Locale.getDefault().toString().startsWith("ja"))
		 * ? DESC_LANG_JA : DESC_LANG_EN); comboLang.select(currentDescLang);
		 * Keep current setting
		 *************************/

		// default visible : disable
		setVisibleAppend(false, currentModeAppend);
		setVisibleDelete(false);
		setVisibleCancel(false);
		setVisiblePreview(false);
	}

	/**
	 * @category refresh ScriptData : Main purpose is changed StartTime value
	 */
	public void refreshScriptData(int currentStartTime, int newStartTime,
			int newEndTime, boolean dspMode) {

		// Search index of target ScriptData
		int index = instScriptData.searchScriptData(currentStartTime);
		if (index >= 0) {
			// PickUP info of target ScriptData
			String currentScriptData = instScriptData.getScriptData(index);
			boolean currentExtended = instScriptData.getExtendExtended(index);
			boolean currentGender = instScriptData.getExtendGender(index);
			int currentSpeed = instScriptData.getExtendSpeed(index);
			int currentPitch = instScriptData.getExtendPitch(index);
			int currentVolume = instScriptData.getExtendVolume(index);
			int currentLang = instScriptData.getExtendLang(index);

			// Delete target ScriptData from List
			instScriptData.deleteScriptData(index);
			// delete Extended data
			instScriptData.deleteExtendData(currentStartTime);

			// Update EndTime
			currentEndTime = newEndTime;
			// Append script data to Script List
			boolean result = instScriptData.appendScriptData(currentScriptData,
					newStartTime, newEndTime);

			// check result status
			if (result) {
				// If result status is SUCCESS, then repaint End Time
				index = instScriptData.getIndexScriptData(newStartTime);

				// check index
				if (index >= 0) {
					// update Extended data
					instScriptData.appendExtendData(index, newStartTime,
							currentExtended, currentGender, currentSpeed,
							currentPitch, currentVolume, currentLang);

					// Check status
					if (dspMode) {
						// Re-draw EditPanel
						repaintTextScriptData(index);
					}
				}
			}
			// No data
			else {
				popupWarningNoScriptData();
			}

			// Re-draw ScriptList(Table area)
			ScriptListView.getInstance().getInstScriptList().reloadScriptList();
		}
	}

	class DeleteScriptButtonAdapter extends SelectionAdapter {
		// Event of Button of Append Script(to ScriptList)
		public void widgetSelected(SelectionEvent e) {
			// Check current selection mode
			if (!currentMultiSelection) {
				// Singleton mode
				deleteSingleScript();
			} else {
				// Multiple mode
				deleteMultiScripts();
				// Change normal mode
				endSelectMultiItems();
			}
		}

		/**
		 * Delete script data as Singleton selection mode
		 */
		private void deleteSingleScript() {
			// Append script data to Script List
			boolean result = instScriptData.deleteScriptData(
					textStartTimeMM.getText(), textStartTimeSS.getText(),
					textStartTimeMS.getText());

			// check result status
			if (result) {
				// get index(StartTime)
				int startTime = instScriptData.parseIntStartTime(
						textStartTimeMM.getText(), textStartTimeSS.getText(),
						textStartTimeMS.getText());
				// delete Extended data
				instScriptData.deleteExtendData(startTime);

				// If result is SUCCESS(true), then clear Canvas
				// ////TimeLineView.getInstance().reqRedrawVolumeLevelCanvas(1);

				// Initialize EditPanel contents
				initDispEditPanel();

				// initialize all parameters
				EditPanelView.getInstance().getInstanceTabSelWAVFile()
						.initDescriptionStruct();
				// initialize own screen
				EditPanelView.getInstance().getInstanceTabSelWAVFile()
						.initDispSelWavFile();

				// If result is SUCCESS(true), then redraw ScriptList(Table
				// area)
				ScriptListView.getInstance().getInstScriptList()
						.reloadScriptList();
				// If result is SUCCESS(true), then delete Audio Label
				TimeLineView.getInstance().reqDeleteScriptData(startTime);

				// SetUP status to Edit start mode
				int stat = MB_STYLE_MODIFY;
				String filePath = TimeLineView.getInstance()
						.reqGetXMLFilePath();
				if (filePath != null)
					stat = MB_STYLE_OVERWR;
				instScriptData.setStatusSaveScripts(stat, true);

				// Seek location of TimeLine view
				TimeLineView.getInstance().reqSetupTimeLine();

				// Reset buffer of preview description
				VolumeLevelCanvas.getInstance()
						.clearSamplingLengthVolumeLevel();
			}
			// No data
			else {
				popupWarningNoExistData();
			}
		}

		/**
		 * Delete script data as multiple selection mode
		 */
		private void deleteMultiScripts() {
			// Check exist data list
			if (storeObjs != null) {
				// Start delete multiple data
				for (int i = 0; i < storeObjs.length; i++) {
					// Get next script data
					ScriptData tempScriptData = (ScriptData) storeObjs[i];
					int startTime = tempScriptData.getScriptStartTime(0);
					int index = instScriptData.searchScriptData(startTime);

					// Append script data to Script List
					boolean result = instScriptData.deleteScriptData(index);

					// check result status
					if (result) {
						// delete Extended data
						instScriptData.deleteExtendData(startTime);

						// If result is SUCCESS(true), then delete Audio Label
						TimeLineView.getInstance().reqDeleteScriptData(
								startTime);
					}
				}

				// If result is SUCCESS(true), then redraw ScriptList(Table
				// area)
				ScriptListView.getInstance().getInstScriptList()
						.reloadScriptList();

				// SetUP status to Edit start mode
				int stat = MB_STYLE_MODIFY;
				String filePath = TimeLineView.getInstance()
						.reqGetXMLFilePath();
				if (filePath != null)
					stat = MB_STYLE_OVERWR;
				instScriptData.setStatusSaveScripts(stat, true);

				// Seek location of TimeLine view
				TimeLineView.getInstance().reqSetupTimeLine();

				// Reset buffer of preview description
				VolumeLevelCanvas.getInstance()
						.clearSamplingLengthVolumeLevel();
			}
		}
	}

	class CancelScriptButtonAdapter extends SelectionAdapter {
		// Event of Button of Append Script(to ScriptList)
		public void widgetSelected(SelectionEvent e) {
			// Initialize EditPanel contents
			initDispEditPanel();

			// initialize all parameters
			EditPanelView.getInstance().getInstanceTabSelWAVFile()
					.initDescriptionStruct();
			// initialize own screen
			EditPanelView.getInstance().getInstanceTabSelWAVFile()
					.initDispSelWavFile();

			// Reset buffer of preview description
			VolumeLevelCanvas.getInstance().clearSamplingLengthVolumeLevel();

			// Reset selection mode to normal mode
			endSelectMultiItems();
		}
	}

	/**
	 * Scale Selection Adapter for Speed/Pitch/Volume slider
	 * 
	 */
	class ScaleSelectionAdapter extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			// Get current scale data
			Scale scale = (Scale) e.widget;
			// Update ToolTip Text for Scale of Volume
			scale.setToolTipText(String.valueOf(scale.getSelection()) + "/100");

			// SetUP enabled status for multiple selection mode
			if (currentMultiSelection) {
				// Who am i?
				if (scaleVoiceSpeed.equals(scale)) {
					// i am Speed slider.
					modifyMultiSpeed = true;
					// Reset grayed color setting
					scaleVoiceSpeed.setBackground(PlatformUI.getWorkbench()
							.getDisplay()
							.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
				} else if (scaleVoicePitch.equals(scale)) {
					// i am Pitch slider.
					modifyMultiPitch = true;
					// Reset grayed color setting
					scaleVoicePitch.setBackground(PlatformUI.getWorkbench()
							.getDisplay()
							.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
				} else if (scaleVoiceVolume.equals(scale)) {
					// i am Volume slider
					modifyMultiVolume = true;
					// Reset grayed color setting
					scaleVoiceVolume.setBackground(PlatformUI.getWorkbench()
							.getDisplay()
							.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
				}
			}
		}
	}

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
			parentButton.setCursor(new Cursor(null, SWT.CURSOR_HAND));
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

	abstract class AbstructNumCheckListener implements Listener {
		public void handleEvent(Event e) {
			int val = -1;
			if (null != e.text && !"".equals(e.text)) {
				try {
					val = Integer.parseInt(e.text);
				} catch (Exception e1) {
					e.doit = false;
				}
				if (e.doit) {
					additionalCheck(e, val);
				}
			}
		}

		abstract void additionalCheck(Event e, int val);

	}

	class DefaultNumCheckListener extends AbstructNumCheckListener {
		@Override
		void additionalCheck(Event e, int val) {
			// do nothing
		}
	}

	class TimeSSNumCheck extends AbstructNumCheckListener {
		// TODO
		void additionalCheck(Event e, int val) {
			if (e.start == 0) {
				// digit limit
				int limit = 5;
				// check input length
				if (e.end >= 1) {
					// change limit
					limit = 59;
				}

				// limit check
				if (textStartTimeSS.getText().length() > 0 && val > limit) {
					e.doit = false;
				}
			} else if (e.start == 1) {
				if (Integer.parseInt(textStartTimeSS.getText()) > 5) {
					e.doit = false;
				}
			}
		}
	}

	/**
	 * TextArea Listener : check modify text area
	 */
	class descriptionModifyListener implements Listener {
		public void handleEvent(Event e) {
			// check current status
			if (!currentEditDescription && !currentStatAppend) {
				// start edit description
				currentEditDescription = true;
				// set visible button to "enable"
				setVisibleAppend(true, false);
				setVisibleDelete(false);
				setVisibleCancel(true);
				setVisiblePreview(true);
			}
		}
	}

	/**
	 * @category ComboBox EventListener : Select Language of Description
	 * 
	 */
	class DescLangListener implements Listener {
		public void handleEvent(Event e) {
			// PickUP selection item index of Language of Description
			Combo combo = (Combo) e.widget;
			currentDescLang = combo.getSelectionIndex();

			// SetUP enabled status for multiple selection mode
			if (currentMultiSelection) {
				modifyMultiLang = true;
				// Reset grayed color setting
				comboLang.setForeground(PlatformUI.getWorkbench().getDisplay()
						.getSystemColor(SWT.COLOR_BLACK));
				comboLang.setBackground(PlatformUI.getWorkbench().getDisplay()
						.getSystemColor(SWT.COLOR_WHITE));
			}
		}
	}

	/**
	 * @category RadioBox Group EventListener : Select gender of voice engine
	 * 
	 */
	class ExtendGenderListener implements Listener {
		public void handleEvent(Event e) {
			// SetUP enabled status for multiple selection mode
			if (currentMultiSelection) {
				modifyMultiGender = true;
				// Reset grayed color setting
				rButtonMale.setGrayed(false);
				rButtonFemale.setGrayed(false);
			}
		}
	}

	/**
	 * @category CheckBox EventListener : CheckUP Extended of description
	 * 
	 */
	class ExtendExtendedListener implements Listener {
		public void handleEvent(Event e) {
			// SetUP enabled status for multiple selection mode
			if (currentMultiSelection) {
				modifyMultiExtended = true;
				// Reset grayed color setting
				chkBoxExtended.setGrayed(false);
			}
		}
	}

	public void handleSyncTimeEvent(SyncTimeEvent e) {
		// Synchronize TimeLine view
		if (e.getEventType() == SyncTimeEvent.SYNCHRONIZE_TIME_LINE) {
			synchronizeTimeLine(e.getCurrentTime());
		} else if (e.getEventType() == SyncTimeEvent.REFRESH_TIME_LINE) {

		}
	}

}
