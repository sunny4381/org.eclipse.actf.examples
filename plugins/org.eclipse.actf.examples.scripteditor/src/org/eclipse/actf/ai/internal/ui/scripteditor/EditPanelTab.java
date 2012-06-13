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

import java.util.List;
import java.util.Locale;

import org.eclipse.actf.ai.internal.ui.scripteditor.event.EventManager;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.SyncTimeEvent;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.SyncTimeEventListener;
import org.eclipse.actf.ai.scripteditor.data.DataUtil;
import org.eclipse.actf.ai.scripteditor.data.IScriptData;
import org.eclipse.actf.ai.scripteditor.data.ScriptDataFactory;
import org.eclipse.actf.ai.scripteditor.data.ScriptDataManager;
import org.eclipse.actf.ai.scripteditor.data.event.DataEventManager;
import org.eclipse.actf.ai.scripteditor.data.event.GuideListEvent;
import org.eclipse.actf.ai.scripteditor.data.event.LabelEvent;
import org.eclipse.actf.ai.scripteditor.util.TimeFormatUtil;
import org.eclipse.actf.ai.scripteditor.util.VoicePlayerFactory;
import org.eclipse.actf.ai.scripteditor.util.XMLFileMessageBox;
import org.eclipse.actf.ai.scripteditor.util.XMLFileSaveUtil;
import org.eclipse.actf.ai.tts.ITTSEngine;
import org.eclipse.actf.ai.ui.scripteditor.views.EditPanelView;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.actf.examples.scripteditor.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

	private static EditPanelTab ownInst = null;

	private static EventManager eventManager = null;
	private static DataEventManager dataEventManager = null;

	private VoicePlayerFactory voice = VoicePlayerFactory.getInstance();

	private String[] langList = ITTSEngine.LANGSET
			.toArray(new String[ITTSEngine.LANGSET.size()]);

	ScrolledComposite ownComposite;

	// Other parameters
	private int currentEndTime = 0;
	private boolean currentModeAppend = true;
	private boolean currentStatPreview = true;
	private boolean currentEditDescription = false;
	// private boolean currentStatAppend = true;
	// private boolean currentStatDelete = true;
	// private boolean currentStatCancel = true;
	// private boolean currentSetData = false;

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
	private String currentDescLang = "en-US"; // TODO

	// Edit Panel part
	private Label labelVPitch;
	private Label labelVPitchMax;
	private Label labelVPitchMin;
	private Label labelVVolume;
	private Label labelVSpeed;
	private Label labelVSpeedMax;
	private Label labelVSpeedMin;
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

	private Text textStartTimeHH;
	private Label labelStartTimeHH;
	private Text textStartTimeMM;
	private Label labelStartTimeMM;
	private Text textStartTimeSS;
	private Label labelStartTimeSS;
	private Text textStartTimeMS;

	private IScriptData currentData = null;
	private List<IScriptData> currentListData = null;

	public void setCurrentListData(List<IScriptData> currentListData) {
		this.currentListData = currentListData;
	}

	// data manager
	private ScriptDataManager scriptManager = null;

	/**
	 * Constructor
	 */
	public EditPanelTab(CTabFolder parent) {
		ownInst = this;
		eventManager = EventManager.getInstance();
		dataEventManager = DataEventManager.getInstance();
		initTab(parent);

		scriptManager = ScriptDataManager.getInstance();

		// TODO check SyncTimeEventListener
	}

	static public EditPanelTab getInstance() {
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
		ownComposite = new ScrolledComposite(parent, SWT.DEFAULT);

		// Get current Display
		IWorkbench workbench = PlatformUI.getWorkbench();
		Display display = workbench.getDisplay();

		// Initialize application's GUI
		initGUI(display);
		// Add eventListener
		eventManager.addSyncTimeEventListener(ownInst);
		parent.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				// TODO other components
				eventManager.removeSyncTimeEventListener(ownInst);
			}
		});
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
			// **<Edit panel>***********************************
			FormLayout groupEditPanelLayout = new FormLayout();
			FormData groupEditPanelLData = new FormData(925, 128);
			groupEditPanelLData.top = new FormAttachment(0, 1000, 0);
			groupEditPanelLData.left = new FormAttachment(0, 1000, 0);
			groupEditPanelLData.right = new FormAttachment(1000, 1000, 0);
			groupEditPanelLData.bottom = new FormAttachment(1000, 1000, 0);
			Composite formComposite = new Composite(ownComposite, SWT.NULL);
			formComposite.setLayoutData(groupEditPanelLData);
			formComposite.setLayout(groupEditPanelLayout);

			// 1)Time scale of current script
			textStartTimeHH = new Text(formComposite, SWT.BORDER);
			textStartTimeHH.setLayoutData(prepareFormData(12, 12, new int[] {

			0, 1000, 78 }, new int[] { 0, 1000, 9 }));

			textStartTimeHH.setTextLimit(2);

			labelStartTimeHH = new Label(formComposite, SWT.NONE);
			labelStartTimeHH.setLayoutData(prepareFormData(4, 12, new int[] {

			0, 1000, 100 }, new int[] { 0, 1000, 11 }));

			labelStartTimeHH.setText(":");

			// MM
			textStartTimeMM = new Text(formComposite, SWT.BORDER);
			// textStartTimeMM.setLayoutData(prepareFormData(12, 12, new int[] {
			// 0, 1000, 78 }, new int[] { 0, 1000, 9 })); del L01
			textStartTimeMM.setLayoutData(prepareFormData(12, 12, new int[] {
					0, 1000, 104 }, new int[] { 0, 1000, 9 }));
			textStartTimeMM.setTextLimit(2);

			labelStartTimeMM = new Label(formComposite, SWT.NONE);
			// labelStartTimeMM.setLayoutData(prepareFormData(4, 12, new int[] {
			// 0, 1000, 100 }, new int[] { 0, 1000, 11 })); del L01
			labelStartTimeMM.setLayoutData(prepareFormData(4, 12, new int[] {
					0, 1000, 126 }, new int[] { 0, 1000, 11 }));

			labelStartTimeMM.setText(":");

			// SS
			textStartTimeSS = new Text(formComposite, SWT.BORDER);
			// textStartTimeSS.setLayoutData(prepareFormData(12, 12, new int[] {
			// 0, 1000, 104 }, new int[] { 0, 1000, 9 })); del L01
			textStartTimeSS.setLayoutData(prepareFormData(12, 12, new int[] {
					0, 1000, 130 }, new int[] { 0, 1000, 9 }));

			textStartTimeSS.setTextLimit(2);

			labelStartTimeSS = new Label(formComposite, SWT.NONE);
			// labelStartTimeSS.setLayoutData(prepareFormData(4, 12, new int[] {
			// 0, 1000, 126 }, new int[] { 0, 1000, 11 })); del L01
			labelStartTimeSS.setLayoutData(prepareFormData(4, 12, new int[] {
					0, 1000, 152 }, new int[] { 0, 1000, 11 }));

			labelStartTimeSS.setText(".");

			// millisecond
			textStartTimeMS = new Text(formComposite, SWT.BORDER);
			// textStartTimeMS.setLayoutData(prepareFormData(18, 12, new int[] {
			// 0, 1000, 130 }, new int[] { 0, 1000, 9 })); del L01
			textStartTimeMS.setLayoutData(prepareFormData(18, 12, new int[] {
					0, 1000, 156 }, new int[] { 0, 1000, 9 }));

			textStartTimeMS.setTextLimit(3);

			// initial Text
			textStartTimeHH.setText("00");
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
			labelStartTime = new Label(formComposite, SWT.NONE);
			labelStartTime.setLayoutData(prepareFormData(54, 12, new int[] { 0,
					1000, 5 }, new int[] { 0, 1000, 11 }));
			labelStartTime.setText("Start Time");

			// Label : "End Time"
			textEndTime = new Label(formComposite, SWT.NONE);
			// textEndTime.setLayoutData(prepareFormData(84, 12, new int[] { 0,
			// 1000, 240 }, new int[] { 0, 1000, 11 })); del L01
			textEndTime.setLayoutData(prepareFormData(84, 12, new int[] { 0,

			1000, 300 }, new int[] { 0, 1000, 11 }));
			// textEndTime.setText("00 : 00 . 000");
			textEndTime.setText("00 : 00 : 00 . 000");

			labelEndTime = new Label(formComposite, SWT.NONE);
			// labelEndTime.setLayoutData(prepareFormData(48, 12, new int[] { 0,
			// 1000, 176 }, new int[] { 0, 1000, 11 })); del L01
			labelEndTime.setLayoutData(prepareFormData(48, 12, new int[] { 0,

			1000, 236 }, new int[] { 0, 1000, 11 }));
			labelEndTime.setText("End Time");

			// Label : Description
			labelDescription = new Label(formComposite, SWT.NONE);
			labelDescription.setLayoutData(prepareFormData(58, 12, new int[] {
					0, 1000, 2 }, new int[] { 0, 1000, 35 }));
			labelDescription.setText("Description");

			// Text Area : Script data (Description)
			int scrollbarWidth = 17;
			FormData textAreaDescriptionLData = new FormData();
			textAreaDescriptionLData.width = 415 - scrollbarWidth;
			textAreaDescriptionLData.height = 40;
			textAreaDescriptionLData.left = new FormAttachment(0, 1000, 79);
			textAreaDescriptionLData.top = new FormAttachment(0, 1000, 35);
			textAreaDescription = new Text(formComposite, SWT.MULTI | SWT.WRAP
					| SWT.BORDER | SWT.V_SCROLL);
			textAreaDescription.setLayoutData(textAreaDescriptionLData);

			// Append SelectionListener
			textAreaDescription.addListener(SWT.Modify, new Listener() {
				public void handleEvent(Event e) {
					if (currentData == null) {
						setVisibleAppendButton(true, false);
						buttonVoiceDelete.setVisible(false);
						buttonVoiceCancel.setVisible(true);
						setVisiblePreviewButton(true);
					} else {
						setVisibleAppendButton(true, true);
						buttonVoiceDelete.setVisible(true);
						buttonVoiceCancel.setVisible(true);
						setVisiblePreviewButton(true);
					}
				}
			});

			// Button : script data Appended action
			FormData buttonVoiceAppendLData = new FormData();
			buttonVoiceAppendLData.width = 52;
			buttonVoiceAppendLData.height = 22;
			buttonVoiceAppendLData.left = new FormAttachment(0, 1000, 69);
			buttonVoiceAppendLData.top = new FormAttachment(0, 1000, 86);
			buttonVoiceAppend = new Button(formComposite, SWT.PUSH | SWT.CENTER);
			buttonVoiceAppend.setLayoutData(buttonVoiceAppendLData);

			// default visible : disable
			setVisibleAppendButton(false, false);

			// Append event listener
			buttonVoiceAppend
					.addSelectionListener(new AppendScriptButtonAdapter());
			// Tracking mouse cursor listener
			buttonVoiceAppend
					.addMouseTrackListener(new MouseCursorTrackAdapter());

			// Button of script data Deleted action
			FormData buttonVoiceDeleteLData = new FormData();
			buttonVoiceDeleteLData.width = 52;
			buttonVoiceDeleteLData.height = 22;
			buttonVoiceDeleteLData.left = new FormAttachment(0, 1000, 125);
			buttonVoiceDeleteLData.top = new FormAttachment(0, 1000, 86);
			buttonVoiceDelete = new Button(formComposite, SWT.PUSH | SWT.CENTER);
			buttonVoiceDelete.setLayoutData(buttonVoiceDeleteLData);
			// Image imgDelete =
			// Activator.getImageDescriptor("/icons/delete.jpg") del L01
			// .createImage(); del L01
			// buttonVoiceDelete.setImage(imgDelete); del L01
			buttonVoiceDelete.setText("Delete");
			// default visible : disable
			buttonVoiceDelete.setVisible(false);

			// Append event listener
			buttonVoiceDelete
					.addSelectionListener(new DeleteScriptButtonAdapter());
			// Tracking mouse cursor listener
			buttonVoiceDelete
					.addMouseTrackListener(new MouseCursorTrackAdapter());

			// Button of script data Canceled action
			FormData buttonVoiceCancelLData = new FormData();
			buttonVoiceCancelLData.width = 52;
			buttonVoiceCancelLData.height = 22;
			buttonVoiceCancelLData.left = new FormAttachment(0, 1000, 181);
			buttonVoiceCancelLData.top = new FormAttachment(0, 1000, 86);
			buttonVoiceCancel = new Button(formComposite, SWT.PUSH | SWT.CENTER);
			buttonVoiceCancel.setLayoutData(buttonVoiceCancelLData);
			buttonVoiceCancel.setText("Cancel");
			buttonVoiceCancel.setVisible(false);

			// Append event listener
			buttonVoiceCancel
					.addSelectionListener(new CancelScriptButtonAdapter());
			// Tracking mouse cursor listener
			buttonVoiceCancel
					.addMouseTrackListener(new MouseCursorTrackAdapter());

			// Label : description of "Preview" button
			FormData labelCautionLData = new FormData();
			// labelCautionLData.left = new FormAttachment(0, 1000, 504);
			// labelCautionLData.top = new FormAttachment(0, 1000, 94);
			labelCautionLData.left = new FormAttachment(0, 1000, 294);
			labelCautionLData.top = new FormAttachment(0, 1000, 112);
			labelCaution = new Label(formComposite, SWT.NONE);
			labelCaution.setLayoutData(labelCautionLData);
			labelCaution.setText("Play Audio Description");
			labelCaution.setVisible(false);

			// ++ Speaker Image
			FormData labelImageSpeakerLData = new FormData();
			labelImageSpeakerLData.width = 16;
			labelImageSpeakerLData.height = 20;
			// labelImageSpeakerLData.left = new FormAttachment(0, 1000, 622);
			// labelImageSpeakerLData.top = new FormAttachment(0, 1000, 90);
			labelImageSpeakerLData.left = new FormAttachment(0, 1000, 354); // 412
			labelImageSpeakerLData.top = new FormAttachment(0, 1000, 87); // 148
			labelImageSpeaker = new Label(formComposite, SWT.NONE);
			labelImageSpeaker.setLayoutData(labelImageSpeakerLData);
			Image imgSpeaker = Activator.getImageDescriptor(
					"/icons/speaker.bmp").createImage();
			labelImageSpeaker.setImage(imgSpeaker);

			// Button : "Preview"
			FormData buttonVoicePreviewLData = new FormData();
			buttonVoicePreviewLData.width = 62;
			buttonVoicePreviewLData.height = 22;
			// buttonVoicePreviewLData.left = new FormAttachment(0, 1000, 501);
			// buttonVoicePreviewLData.top = new FormAttachment(0, 1000, 68);
			buttonVoicePreviewLData.left = new FormAttachment(0, 1000, 290);
			buttonVoicePreviewLData.top = new FormAttachment(0, 1000, 86);
			buttonVoicePreview = new Button(formComposite, SWT.PUSH
					| SWT.CENTER);
			buttonVoicePreview.setLayoutData(buttonVoicePreviewLData);
			buttonVoicePreview.setText("Preview");

			// default visible : disable
			setVisiblePreviewButton(false);

			// Append Preview event listener
			buttonVoicePreview.addSelectionListener(new PreviewButtonAdapter());
			// Tracking mouse cursor listener
			buttonVoicePreview
					.addMouseTrackListener(new MouseCursorTrackAdapter());

			// **<Extended>***********************************************:
			// CheckBox : "Extended"
			FormData labelExtendedLData = new FormData();
			labelExtendedLData.left = new FormAttachment(0, 1000, 530);//
			// 518);
			labelExtendedLData.top = new FormAttachment(0, 1000, 6);
			// labelExtendedLData.left = new FormAttachment(0, 1000, 19);
			// labelExtendedLData.top = new FormAttachment(0, 1000, 162);
			labelExtended = new Label(formComposite, SWT.NONE);
			labelExtended.setLayoutData(labelExtendedLData);
			labelExtended.setText("Extended");
			labelExtended.setVisible(true);

			FormData chkBoxExtendedLData = new FormData();
			chkBoxExtendedLData.width = 13;
			chkBoxExtendedLData.height = 16;
			chkBoxExtendedLData.left = new FormAttachment(0, 1000, 513);//
			// 501);
			chkBoxExtendedLData.top = new FormAttachment(0, 1000, 4);
			// chkBoxExtendedLData.left = new FormAttachment(0, 1000, 5);
			// chkBoxExtendedLData.top = new FormAttachment(0, 1000, 160);
			chkBoxExtended = new Button(formComposite, SWT.CHECK | SWT.LEFT);
			chkBoxExtended.setLayoutData(chkBoxExtendedLData);
			chkBoxExtended.setSelection(false);
			chkBoxExtended.addMouseTrackListener(new MouseCursorTrackAdapter());
			// SetUP selection listener for multiple selection mode
			chkBoxExtended.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					// SetUP enabled status for multiple selection mode
					if (currentMultiSelection) {
						modifyMultiExtended = true;
						// Reset grayed color setting
						chkBoxExtended.setGrayed(false);
					}
				}
			});
			chkBoxExtended.setVisible(true);

			// Label : "Male"
			FormData labelMaleLData = new FormData();
			labelMaleLData.left = new FormAttachment(0, 1000, 530);// 518);
			labelMaleLData.top = new FormAttachment(0, 1000, 28);
			// labelMaleLData.left = new FormAttachment(0, 1000, 22);
			// labelMaleLData.top = new FormAttachment(0, 1000, 201);
			labelMale = new Label(formComposite, SWT.NONE);
			labelMale.setLayoutData(labelMaleLData);
			labelMale.setText("Male");

			FormData rButtonMaleLData = new FormData();
			rButtonMaleLData.left = new FormAttachment(0, 1000, 513);// 501);
			rButtonMaleLData.top = new FormAttachment(0, 1000, 26);
			// rButtonMaleLData.left = new FormAttachment(0, 1000, 5);
			// rButtonMaleLData.top = new FormAttachment(0, 1000, 199);
			rButtonMale = new Button(formComposite, SWT.RADIO | SWT.LEFT);
			rButtonMale.setLayoutData(rButtonMaleLData);
			rButtonMale.setSelection(false);
			rButtonMale.addMouseTrackListener(new MouseCursorTrackAdapter());
			// SetUP selection listener for multiple selection mode
			rButtonMale.addListener(SWT.Selection, new GenderListener());

			// Label : "Female"
			FormData labelFemaleLData = new FormData();
			labelFemaleLData.left = new FormAttachment(0, 1000, 584);// 572);
			labelFemaleLData.top = new FormAttachment(0, 1000, 28);
			// labelFemaleLData.left = new FormAttachment(0, 1000, 76);
			// labelFemaleLData.top = new FormAttachment(0, 1000, 201);
			labelFemale = new Label(formComposite, SWT.NONE);
			labelFemale.setLayoutData(labelFemaleLData);
			labelFemale.setText("Female");

			FormData rButtonFemaleLData = new FormData();
			rButtonFemaleLData.left = new FormAttachment(0, 1000, 567);//
			// 555);
			rButtonFemaleLData.top = new FormAttachment(0, 1000, 26);
			// rButtonFemaleLData.left = new FormAttachment(0, 1000, 59);
			// rButtonFemaleLData.top = new FormAttachment(0, 1000, 199);
			rButtonFemale = new Button(formComposite, SWT.RADIO | SWT.LEFT);
			rButtonFemale.setLayoutData(rButtonFemaleLData);
			rButtonFemale.addMouseTrackListener(new MouseCursorTrackAdapter());
			// SetUP selection listener for multiple selection mode
			rButtonFemale.addListener(SWT.Selection, new GenderListener());
			rButtonFemale.setSelection(true);

			// Language of Description
			// **Label**
			FormData labelLangLData = new FormData();
			labelLangLData.left = new FormAttachment(0, 1000, 513);// 501);
			labelLangLData.top = new FormAttachment(textAreaDescription, -4);
			// labelLangLData.left = new FormAttachment(0, 1000, 5);
			// labelLangLData.top = new FormAttachment(0, 1000, 240);
			labelLang = new Label(formComposite, SWT.NONE);
			labelLang.setLayoutData(labelLangLData);
			labelLang.setText("Language");
			FormData comboLangLData = new FormData();
			comboLangLData.left = new FormAttachment(0, 1000, 513);// 501);
			comboLangLData.top = new FormAttachment(labelLang, 2);
			// comboLangLData.left = new FormAttachment(0, 1000, 58);// 501);
			// comboLangLData.top = new FormAttachment(labelLang, -14);
			comboLang = new Combo(formComposite, SWT.DROP_DOWN);
			comboLang.setLayoutData(comboLangLData);
			comboLang.setItems(langList);
			if (Locale.getDefault().toString().startsWith("ja")) {
				for (int i = 0; i < langList.length; i++) {
					if ("ja-JP".equals(langList[i])) {
						comboLang.select(i);
						break;
					}
				}
			} else {
				// TODO
				for (int i = 0; i < langList.length; i++) {
					if ("en-US".equals(langList[i])) {
						comboLang.select(i);
						break;
					}
				}
			}
			// Add EventListener
			comboLang.addListener(SWT.Selection, new DescLangListener());

			// Label : each parameters of Scale
			FormData labelVSpeedLData = new FormData();
			labelVSpeedLData.left = new FormAttachment(0, 1000, 662);
			labelVSpeedLData.top = new FormAttachment(0, 1000, 22);
			// labelVSpeedLData.left = new FormAttachment(0, 1000, 170);
			// labelVSpeedLData.top = new FormAttachment(0, 1000, 192);
			labelVSpeed = new Label(formComposite, SWT.NONE);
			labelVSpeed.setLayoutData(labelVSpeedLData);
			labelVSpeed.setText("Speed");

			FormData labelVSpeedMaxLData = new FormData();
			labelVSpeedMaxLData.left = new FormAttachment(0, 1000, 711); //
			labelVSpeedMaxLData.top = new FormAttachment(0, 1000, 38);
			// labelVSpeedMaxLData.left = new FormAttachment(0, 1000, 219);
			// labelVSpeedMaxLData.top = new FormAttachment(0, 1000, 220);
			labelVSpeedMax = new Label(formComposite, SWT.NONE);
			labelVSpeedMax.setLayoutData(labelVSpeedMaxLData);
			labelVSpeedMax.setText("0");
			labelVSpeedMax.setVisible(false);

			FormData labelVSpeedMinLData = new FormData();
			labelVSpeedMinLData.left = new FormAttachment(0, 1000, 898); //
			labelVSpeedMinLData.top = new FormAttachment(0, 1000, 38);
			// labelVSpeedMinLData.left = new FormAttachment(0, 1000, 406);
			// labelVSpeedMinLData.top = new FormAttachment(0, 1000, 210);
			labelVSpeedMin = new Label(formComposite, SWT.NONE);
			labelVSpeedMin.setLayoutData(labelVSpeedMinLData);
			labelVSpeedMin.setText("100");
			labelVSpeedMin.setVisible(false);

			FormData labelVPitchLData = new FormData();
			labelVPitchLData.left = new FormAttachment(0, 1000, 667);
			labelVPitchLData.top = new FormAttachment(0, 1000, 68);
			// labelVPitchLData.left = new FormAttachment(0, 1000, 175);
			// labelVPitchLData.top = new FormAttachment(0, 1000, 240);
			labelVPitch = new Label(formComposite, SWT.NONE);
			labelVPitch.setLayoutData(labelVPitchLData);
			labelVPitch.setText("Pitch");
			labelVPitch.setVisible(false);

			FormData labelVPitchMaxLData = new FormData();
			// labelVPitchMaxLData.left = new FormAttachment(0, 1000, 711); //
			// labelVPitchMaxLData.top = new FormAttachment(0, 1000, 86);
			labelVPitchMaxLData.left = new FormAttachment(0, 1000, 219);
			// labelVPitchMaxLData.top = new FormAttachment(0, 1000, 218+40); //
			labelVPitchMaxLData.top = new FormAttachment(0, 1000, 258 + 40);
			labelVPitchMax = new Label(formComposite, SWT.NONE);
			labelVPitchMax.setLayoutData(labelVPitchMaxLData);
			labelVPitchMax.setText("0");
			labelVPitchMax.setVisible(false);

			FormData labelVPitchMinLData = new FormData();
			// labelVPitchMinLData.left = new FormAttachment(0, 1000, 898); //
			// labelVPitchMinLData.top = new FormAttachment(0, 1000, 86);
			labelVPitchMinLData.left = new FormAttachment(0, 1000, 406);
			// labelVPitchMinLData.top = new FormAttachment(0, 1000, 219+40); //
			labelVPitchMinLData.top = new FormAttachment(0, 1000, 259 + 40);
			labelVPitchMin = new Label(formComposite, SWT.NONE);
			labelVPitchMin.setLayoutData(labelVPitchMinLData);
			labelVPitchMin.setText("100");
			labelVPitchMin.setVisible(false);

			FormData labelVVolumeLData = new FormData();
			// labelVVolumeLData.left = new FormAttachment(0, 1000, 656);
			// labelVVolumeLData.top = new FormAttachment(0, 1000, 118);
			labelVVolumeLData.left = new FormAttachment(0, 1000, 164);
			// labelVVolumeLData.top = new FormAttachment(0, 1000, 250+40);
			labelVVolumeLData.top = new FormAttachment(0, 1000, 290 + 40);
			labelVVolume = new Label(formComposite, SWT.NONE);
			labelVVolume.setLayoutData(labelVVolumeLData);
			labelVVolume.setText("Volume");
			// labelVVolume.setVisible(false);
			labelVVolume.setVisible(false);

			// Scale : each parameters
			FormData scaleVoiceSpeedLData = new FormData();
			scaleVoiceSpeedLData.width = 219;
			scaleVoiceSpeedLData.height = 42;
			scaleVoiceSpeedLData.left = new FormAttachment(0, 1000, 700);
			scaleVoiceSpeedLData.top = new FormAttachment(0, 1000, 6);
			// scaleVoiceSpeedLData.left = new FormAttachment(0, 1000, 208);
			// scaleVoiceSpeedLData.top = new FormAttachment(0, 1000, 178);
			scaleVoiceSpeed = new Scale(formComposite, SWT.HORIZONTAL);
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
					.addMouseTrackListener(new MouseCursorTrackAdapter());

			FormData scaleVoicePitchLData = new FormData();
			scaleVoicePitchLData.width = 219;
			scaleVoicePitchLData.height = 42;
			// scaleVoicePitchLData.left = new FormAttachment(0, 1000, 700);
			// scaleVoicePitchLData.top = new FormAttachment(0, 1000, 54);
			scaleVoicePitchLData.left = new FormAttachment(0, 1000, 208);
			// scaleVoicePitchLData.top = new FormAttachment(0, 1000, 186+40);
			scaleVoicePitchLData.top = new FormAttachment(0, 1000, 226);
			scaleVoicePitch = new Scale(formComposite, SWT.NONE);
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
					.addMouseTrackListener(new MouseCursorTrackAdapter());

			FormData scaleVoiceVolumeLData = new FormData();
			scaleVoiceVolumeLData.width = 219;
			scaleVoiceVolumeLData.height = 42;
			// scaleVoiceVolumeLData.left = new FormAttachment(0, 1000, 700);
			// scaleVoiceVolumeLData.top = new FormAttachment(0, 1000, 102);
			scaleVoiceVolumeLData.left = new FormAttachment(0, 1000, 208);
			// scaleVoiceVolumeLData.top = new FormAttachment(0, 1000, 234+40);
			scaleVoiceVolumeLData.top = new FormAttachment(0, 1000, 274);
			scaleVoiceVolume = new Scale(formComposite, SWT.NONE);
			scaleVoiceVolume.setLayoutData(scaleVoiceVolumeLData);
			scaleVoiceVolume.setMinimum(0);
			scaleVoiceVolume.setMaximum(100);
			scaleVoiceVolume.setIncrement(10);
			scaleVoiceVolume.setSelection(50);
			scaleVoiceVolume.setToolTipText("50/100");
			// scaleVoiceVolume.setVisible(false);
			scaleVoiceVolume.setVisible(false);

			// Select Scale Listener
			scaleVoiceVolume.addSelectionListener(new ScaleSelectionAdapter());
			// Tracking mouse cursor listener
			scaleVoiceVolume
					.addMouseTrackListener(new MouseCursorTrackAdapter());

			// 1st Initialized current Window
			formComposite.layout();
			formComposite.pack();
			ownComposite.setContent(formComposite);

		} catch (Exception e) {
			System.out.println("EditPanelTab : Exception = " + e);
		}
	}

	public Composite getOwnComposite() {
		return (ownComposite);
	}

	private void setVisibleAppendButton(boolean stat, boolean mode) {
		if (stat) {
			currentModeAppend = mode;
			if (mode) {
				buttonVoiceAppend.setText("Update");
			} else {
				buttonVoiceAppend.setText("Add");

			}
		} else {
			currentEditDescription = false;
		}
		buttonVoiceAppend.setVisible(stat);
	}

	// }

	private int getStarTimeFromEditPanel() {
		int startTime = TimeFormatUtil.parseIntStartTime(
				textStartTimeHH.getText(), textStartTimeMM.getText(),
				textStartTimeSS.getText(), textStartTimeMS.getText());

		return (startTime);
	}

	private void setStartTimeToEditPanel(int startTime) {
		textStartTimeHH.setText(TimeFormatUtil.makeFormatHH(startTime));
		textStartTimeMM.setText(TimeFormatUtil.makeFormatMM(startTime));
		textStartTimeSS.setText(TimeFormatUtil.makeFormatSS(startTime));
		textStartTimeMS.setText(TimeFormatUtil.makeFormatMS(startTime));

	}

	private void setVisiblePreviewButton(boolean stat) {
		if (currentStatPreview != stat) {
			currentStatPreview = stat;
			buttonVoicePreview.setVisible(stat);
			labelImageSpeaker.setVisible(stat);
		}
	}

	public void repaintTextScriptData(IScriptData data) {
		this.currentData = data;

		setStartTimeToEditPanel(data.getStartTime());

		// Set new selections for Extended components.
		chkBoxExtended.setSelection(data.isExtended());
		if (data.getVgGender()) {
			rButtonFemale.setSelection(false);
			rButtonMale.setSelection(true);
		} else {
			rButtonMale.setSelection(false);
			rButtonFemale.setSelection(true);
		}
		scaleVoiceSpeed.setSelection(data.getVgPlaySpeed());
		scaleVoicePitch.setSelection(data.getVgPitch());
		scaleVoiceVolume.setSelection(data.getVgVolume());

		// Update ToolTip Text for Scale of Speed
		scaleVoiceSpeed.setToolTipText(String.valueOf(scaleVoiceSpeed
				.getSelection()) + "/100");
		scaleVoicePitch.setToolTipText(String.valueOf(scaleVoicePitch
				.getSelection()) + "/100");
		scaleVoiceVolume.setToolTipText(String.valueOf(scaleVoiceVolume
				.getSelection()) + "/100");

		// Clear position of Slider
		// TimeLineView.getInstance().repaintTimeLine();
		int size = 0;
		if (scriptManager.getDataList().size() != 0) {
			size = scriptManager.getDataList().size() - 1;
		}

		textEndTime.setText(data.getEndTimeString());

		// Set Language of Description
		currentDescLang = data.getLang();
		for (int i = 0; i < langList.length; i++) {
			if (langList[i].equals(currentDescLang)) {
				comboLang.select(i);
			}
		}// TODO set default

		// SetUP Description
		textAreaDescription.setText(data.getDescription());

		// BackUP current Start Time value for Update action
		updateScriptStartTime = data.getStartTime();
		// Set visible button
		if (!Validator.checkNull(data.getDescription())) {
			setVisibleAppendButton(true, true);
			buttonVoiceDelete.setVisible(true);
			buttonVoiceCancel.setVisible(true);
			setVisiblePreviewButton(true);
		} else {
			setVisibleAppendButton(false, false);
			setVisibleAppendButton(false, false);
			buttonVoiceCancel.setVisible(false);
			buttonVoiceCancel.setVisible(false);
		}
	}

	public void startSelectMultiItems(Object[] targetObjs) {
		// TODO recover
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
		setVisibleAppendButton(true, true);
		buttonVoiceDelete.setVisible(true);
		buttonVoiceCancel.setVisible(true);
	}

	/**
	 * End multiple items selection mode
	 */
	public void endSelectMultiItems() {
		// Disable WAV file selection TAB
		EditPanelView.getInstance().setSelectMultiItemsMode(false);

		setMultiSelectMode(false);
		storeObjs = null;
		modifyMultiExtended = false;
		modifyMultiGender = false;
		modifyMultiLang = false;
		modifyMultiSpeed = false;
		modifyMultiPitch = false;
		modifyMultiVolume = false;

		// default visible : disable
		setVisibleAppendButton(false, currentModeAppend);
		buttonVoiceDelete.setVisible(false);
		buttonVoiceCancel.setVisible(false);
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
				textStartTimeHH.setEnabled(false);
				textStartTimeMM.setEnabled(false);
				textStartTimeSS.setEnabled(false);
				textStartTimeMS.setEnabled(false);
				textAreaDescription.setEnabled(false);
				// Set gray color setting for all items
				// setGrayedExtendParam(true);
			} else {
				// Reset selection mode of ScriptList Table
				// ScriptListView.getInstance().getInstScriptList()
				// .setCurrentSelectionMode(false);
				// Enable one of controls
				textStartTimeHH.setEnabled(true);
				textStartTimeMM.setEnabled(true);
				textStartTimeSS.setEnabled(true);
				textStartTimeMS.setEnabled(true);
				textAreaDescription.setEnabled(true);
				// Reset gray color setting for all items
				// setGrayedExtendParam(false);
			}
		}
	}

	/**
	 * Repaint EndTime of Edit view
	 */
	public void updateEndTime(int samplingDuration) {
		textEndTime
				.setText(TimeFormatUtil.makeFormatHHMMSSMS(samplingDuration));
	}

	/**
	 * Local method : Play Voice for Save Script to ScriptList
	 */
	private void playPreviewDescription(String currentScriptText) {
		if (voice.getPlayVoiceStatus()) {
			TimeLineView.getInstance().reqStopScriptAudio();
		}
		// Clear Canvas
		TimeLineView.getInstance().reqRedrawVolumeLevelCanvas(1);

		try {
			// data is temporary.
			IScriptData data = getScriptData(null);

			int length = voice.getSpeakLength(data);
			if (length > 0) {
				textEndTime.setText(TimeFormatUtil.makeFormatHHMMSSMS(length));
			} else { // can't save to file
				VolumeLevelCanvas.getInstance().startSampling();
			}
			TimeLineView.getInstance().reqPlayAudio(data);
		} catch (Exception ee) {
			ee.printStackTrace();
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
			if (currentScriptText.length() > 0) {
				// Preview Voice
				playPreviewDescription(currentScriptText);
			}
		}
	}

	class AppendScriptButtonAdapter extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			if (!currentMultiSelection) {
				int newEndTime = currentEndTime;

				// status of update(start time) mode flag
				boolean update_mode_flg = false;

				// check result status
				if (currentData != null) {
					// SetUP status to Edit start mode
					int stat = XMLFileMessageBox.MB_STYLE_MODIFY;
					String filePath = XMLFileSaveUtil.getInstance()
							.getFilePath();
					if (filePath != null)
						stat = XMLFileMessageBox.MB_STYLE_OVERWR;

					update_mode_flg = true;
				}

				if (update_mode_flg) {
					// for change order, delete and add data.
					dataEventManager.fireLabelEvent(new LabelEvent(
							LabelEvent.DELETE_LABEL, currentData, this));
					dataEventManager.fireGuideListEvent(new GuideListEvent(
							GuideListEvent.DELETE_DATA, currentData, this));
				}

				currentData = getScriptData(currentData);

				int length = voice.getSpeakLength(currentData);
				if (length > 0) {
					newEndTime = currentData.getStartTime() + length;
					currentData.setEndTimeAccurate(true);
				} else {
					newEndTime = currentData.getStartTime()
							+ DataUtil.sumMoraCount(
									currentData.getDescription(),
									currentData.getLang());
					currentData.setEndTimeAccurate(false);
				}

				currentData.setEndTime(newEndTime);
				currentData.setType(IScriptData.TYPE_AUDIO);
				currentData.setMark(NO_MARK);
				currentData.setDataCommit(true);

				dataEventManager.fireLabelEvent(new LabelEvent(
						LabelEvent.PUT_LABEL, currentData, this));
				dataEventManager.fireGuideListEvent(new GuideListEvent(
						GuideListEvent.ADD_DATA, currentData, this));

				DataUtil.debug();

				currentData = null;
				// Initialize Edit Panel contents
				initDispEditPanel();

				// initialize all parameters
				EditPanelView.getInstance().getInstanceTabSelWAVFile()
						.initDescriptionData();
				// initialize own screen
				EditPanelView.getInstance().getInstanceTabSelWAVFile()
						.initDispSelWavFile();
			} else {
				updateMultiScripts();
			}
		}

		/**
		 * Update multiple scripts Note : not multiple append mode
		 */
		private void updateMultiScripts() {
			for (IScriptData data : currentListData) {
				dataEventManager.fireLabelEvent(new LabelEvent(
						LabelEvent.DELETE_LABEL, data, this));

				data.setExtended(chkBoxExtended.getSelection());
				data.setVgGender(rButtonMale.getSelection() ? true : false);
				data.setLang(currentDescLang);
				data.setVgPlaySpeed(scaleVoiceSpeed.getSelection());
				data.setVgPitch(scaleVoicePitch.getSelection());
				data.setVgVolume(scaleVoiceVolume.getSelection());

				dataEventManager.fireGuideListEvent(new GuideListEvent(
						GuideListEvent.REPALCE_DATA, data, this));
				dataEventManager.fireLabelEvent(new LabelEvent(
						LabelEvent.PUT_LABEL, data, this));
			}

			endSelectMultiItems();
			initDispEditPanel();

		}
	}

	private IScriptData getScriptData(IScriptData data) {
		if (data == null) {
			data = ScriptDataFactory.createNewData();
			data.setType(IScriptData.TYPE_AUDIO);
		}
		// If result is SUCCESS(true), then clear text area
		data.setDescription(textAreaDescription.getText());// SelectionText().setText(data.getScriptText());

		// Seek to current time line marker's position
		int startTime = getStarTimeFromEditPanel();

		data.setStartTime(startTime);
		data.setEndTimeString(textEndTime.getText());
		data.setVgPlaySpeed(scaleVoiceSpeed.getSelection());
		data.setExtended(chkBoxExtended.getSelection());
		data.setVgGender(rButtonMale.getSelection());

		data.setVgPitch(scaleVoicePitch.getSelection());
		data.setVgVolume(scaleVoiceVolume.getSelection());
		data.setLang(langList[comboLang.getSelectionIndex()]);

		return data;
	}

	/**
	 * Local method : initialize Edit area
	 */
	public void initDispEditPanel() {

		textAreaDescription.setText("");

		int nowTimeLine = TimeLineCanvas.getInstance()
				.getCurrentPositionMarkerTimeLine();

		setStartTimeToEditPanel(nowTimeLine);
		textEndTime.setText(TimeFormatUtil.makeFormatHHMMSSMS(nowTimeLine));

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
		setVisibleAppendButton(false, currentModeAppend);
		buttonVoiceDelete.setVisible(false);
		buttonVoiceCancel.setVisible(false);
		setVisiblePreviewButton(false);

		this.currentData = null;
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

			dataEventManager.fireLabelEvent(new LabelEvent(
					LabelEvent.DELETE_LABEL, currentData, this));
			dataEventManager.fireGuideListEvent(new GuideListEvent(
					GuideListEvent.DELETE_DATA, currentData, this));

			initDispEditPanel();

			EditPanelView.getInstance().getInstanceTabSelWAVFile()
					.initDescriptionData();
			EditPanelView.getInstance().getInstanceTabSelWAVFile()
					.initDispSelWavFile();

		}

		/**
		 * Delete script data as multiple selection mode
		 */
		private void deleteMultiScripts() {
			for (IScriptData data : currentListData) {
				dataEventManager.fireLabelEvent(new LabelEvent(
						LabelEvent.DELETE_LABEL, data, this));
				// dataEventManager.fireAudioEvent(new AudioEvent(
				// AudioEvent.DELETE_DATA, data, this));
				dataEventManager.fireGuideListEvent(new GuideListEvent(
						GuideListEvent.DELETE_DATA, data, this));
			}
			dataEventManager.fireGuideListEvent(new GuideListEvent(
					GuideListEvent.DESELECT_DATA, null, this));

			initDispEditPanel();

		}
	}

	class CancelScriptButtonAdapter extends SelectionAdapter {
		// Event of Button of Append Script(to ScriptList)
		public void widgetSelected(SelectionEvent e) {
			// Initialize EditPanel contents
			initDispEditPanel();

			// initialize all parameters
			EditPanelView.getInstance().getInstanceTabSelWAVFile()
					.initDescriptionData();
			// initialize own screen
			EditPanelView.getInstance().getInstanceTabSelWAVFile()
					.initDispSelWavFile();

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
	 * @category ComboBox EventListener : Select Language of Description
	 * 
	 */
	class DescLangListener implements Listener {
		public void handleEvent(Event e) {
			// PickUP selection item index of Language of Description
			Combo combo = (Combo) e.widget;
			currentDescLang = langList[combo.getSelectionIndex()];

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

	class GenderListener implements Listener {
		public void handleEvent(Event e) {
			if (currentMultiSelection) {
				modifyMultiGender = true;
				// Reset grayed color setting
				rButtonMale.setGrayed(false);
				rButtonFemale.setGrayed(false);
			}
		}
	}

	public void handleSyncTimeEvent(SyncTimeEvent e) {
		if (e.getEventType() == SyncTimeEvent.SYNCHRONIZE_TIME_LINE) {
			if (!buttonVoiceAppend.getVisible()) {
				setStartTimeToEditPanel(e.getCurrentTime());
			}
		} else if (e.getEventType() == SyncTimeEvent.REFRESH_TIME_LINE) {

		}
	}
}
