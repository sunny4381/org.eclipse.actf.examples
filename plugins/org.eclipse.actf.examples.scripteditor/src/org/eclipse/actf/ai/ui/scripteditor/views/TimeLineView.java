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

import java.net.URI;
import java.util.Calendar;
import java.util.List;

import org.eclipse.actf.ai.internal.ui.scripteditor.AudioComposite;
import org.eclipse.actf.ai.internal.ui.scripteditor.CaptionComposite;
import org.eclipse.actf.ai.internal.ui.scripteditor.PreviewPanel;
import org.eclipse.actf.ai.internal.ui.scripteditor.TimeLineCanvas;
import org.eclipse.actf.ai.internal.ui.scripteditor.VolumeLevelCanvas;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.EventManager;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.MouseDragEvent;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.MouseDragEventListener;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.SyncTimeEvent;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.SyncTimeEventListener;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.TimerEvent;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.TimerEventListener;
import org.eclipse.actf.ai.scripteditor.data.IScriptData;
import org.eclipse.actf.ai.scripteditor.data.ScriptDataManager;
import org.eclipse.actf.ai.scripteditor.data.event.DataEventManager;
import org.eclipse.actf.ai.scripteditor.data.event.GuideListEvent;
import org.eclipse.actf.ai.scripteditor.data.event.LabelEvent;
import org.eclipse.actf.ai.scripteditor.util.ScriptFileDropListener;
import org.eclipse.actf.ai.scripteditor.util.SoundMixer;
import org.eclipse.actf.ai.scripteditor.util.VoicePlayerFactory;
import org.eclipse.actf.ai.scripteditor.util.WebBrowserFactory;
import org.eclipse.actf.examples.scripteditor.ScriptEditorShutdownListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class TimeLineView extends ViewPart implements IUNIT,
		SyncTimeEventListener, MouseDragEventListener, TimerEventListener {

	public static final String VIEW_ID = "org.eclipse.actf.examples.scripteditor.VolumeLevelView";

	static private TimeLineView ownInst = null;

	protected Composite parentComposite;
	protected Composite ownComposite;
	private ScrolledComposite parentSC;
	private ScrolledComposite ownSC;
	private Composite childComposite;

	// Slider of TimeLine
	private Slider sliderTimeLine;

	// for Voice Manager
	private VoicePlayerFactory voicePlayer = null;
	private boolean previousPlayerStatus = false;

	// TimeLine's Timer Task
	private TimeLineManager instTimerTimeLineManager = null;
	// private SynchronizeTimeLineTimer instTimerSynchronizeTimeLine = null;

	// Instances of sub class
	private TimeLineCanvas canvasTimeLine = null;
	private VolumeLevelCanvas canvasVolumeLevel = null;
	private AudioComposite compositeAudio = null;

	private CaptionComposite compositeCaption = null;

	// Control parameters for TimeLine
	// private int startTimeLine = 0;
	private int endTimeLine = TL_DEF_ETIME;
	private int currentTimeLine = 0;
	private int currentStatusTimeLine = TL_STAT_IDLE;
	private int movieEndTimeLine = 0;
	private int currentMovieTimeLine = 0;
	private int currentMovieStatus = -1;
	private int previousMovieStatus = -1;
	private int currentTimeLineLocation = 0;
	private int previousTimeLine = -1;
	private int pauseTimeLine = 0;

	// control end time updater
	private int indexCurrentScriptData = 0;
	private boolean currentVoiceEngineAction = false;
	private long startDurationVoice;
	private long endDurationVoice;
	// For menu item
	private boolean currentEnableDescription = true;

	// other Widgets
	private Label labelVolumeLevel;
	private Label labelAudio1;

	// for FormLayout of TimeLine
	private FormData ParentSCTimeLineLData;
	private FormData labelAudio1LData;

	private boolean currentDragStatus = false; // status for dragging

	// for Event Managing
	private static EventManager eventManager = null;
	private ScriptDataManager scriptManager = null;
	private DataEventManager dataEventManager = null;

	private int captionStartTimeLine = 0;

	private boolean adjustTimeLine = false;

	/**
	 * Constructor
	 */
	public TimeLineView() {
		super();

		// SetUP Quit Listener for Workbench
		PlatformUI.getWorkbench().addWorkbenchListener(
				new ScriptEditorShutdownListener());

		ownInst = this;

		eventManager = EventManager.getInstance();
		scriptManager = ScriptDataManager.getInstance();
		dataEventManager = DataEventManager.getInstance();
	}

	static public TimeLineView getInstance() {
		return (ownInst);
	}

	/**
	 * @Override
	 */
	public void createPartControl(Composite parent) {

		parentComposite = parent;
		parentSC = new ScrolledComposite(parent, SWT.V_SCROLL);

		voicePlayer = VoicePlayerFactory.getInstance();

		initGUI(PlatformUI.getWorkbench().getDisplay());

		// Start Timer for Synchronize TimeLine
		// instTimerSynchronizeTimeLine = new SynchronizeTimeLineTimer();
		// Start Timer for TimeLine management
		instTimerTimeLineManager = new TimeLineManager(this, scriptManager);

		eventManager.addSyncTimeEventListener(this);
		eventManager.addMouseDragEventListener(this);
		eventManager.addTimerEventListener(this);

		parent.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				// TODO other components
				eventManager.removeSyncTimeEventListener(ownInst);
				eventManager.removeMouseDragEventListener(ownInst);
				eventManager.removeTimerEventListener(ownInst);
			}
		});

		// Initial setup DnD target control
		DropTarget targetDnD = new DropTarget(ownComposite, DND.DROP_DEFAULT
				| DND.DROP_COPY);
		targetDnD.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		targetDnD.addDropListener(new ScriptFileDropListener());
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		ownSC.setFocus();
		childComposite.setFocus();
		ownComposite.setFocus();
		canvasTimeLine.setFocus();
		// compositeScriptAudio.setFocus();
		canvasVolumeLevel.setFocus();
	}

	/**
	 * Cleans up all resources created by this ViewPart.
	 */
	public void dispose() {

		// change status to DISPOSE
		currentStatusTimeLine = TL_STAT_DISPOSE;

		// stop & close all process
		reqStopScriptAudio();
		reqStopCaptureAudio();

		// instTimerSynchronizeTimeLine = null;

		eventManager = null;
		// dispose own ViewPart
		super.dispose();
	}

	/**
	 * Initialize Screen
	 */
	private void initGUI(Display parentDisp) {

		try {
			// adjust height by duplicated setting
			int adj_height = TL_AUDIO1_MDRAG_SY * TL_DEF_LINES;

			// Layout : Parent composite's layout as ScrolledComposite
			FormLayout ParentSCTimeLineLayout = new FormLayout();
			parentSC.setLayout(ParentSCTimeLineLayout);
			ParentSCTimeLineLData = new FormData();
			ParentSCTimeLineLData.width = 547;
			ParentSCTimeLineLData.height = 168 + adj_height;
			ParentSCTimeLineLData.top = new FormAttachment(5, 1000, 0);
			ParentSCTimeLineLData.left = new FormAttachment(592, 1000, 0);
			ParentSCTimeLineLData.right = new FormAttachment(0, 1000, 441);
			parentSC.setLayoutData(ParentSCTimeLineLData);

			ownComposite = new Composite(parentSC, SWT.NONE);
			// Layout : "TimeLine" parent Composite
			FormLayout viewTimeLinelLayout = new FormLayout();
			ownComposite.setLayout(viewTimeLinelLayout);
			FormData viewTimeLineLData = new FormData();
			viewTimeLineLData.top = new FormAttachment(0, 1000, 0);
			viewTimeLineLData.left = new FormAttachment(0, 1000, 0);
			viewTimeLineLData.right = new FormAttachment(1000, 1000, 0);
			viewTimeLineLData.bottom = new FormAttachment(1000, 1000, 0);
			ownComposite.setLayoutData(viewTimeLineLData);

			// Initialize Preview Panel
			PreviewPanel.getInstance().initPreviewPanel(parentDisp,
					ownComposite);

			parentSC.setContent(ownComposite);
			parentSC.setSize(ownComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			// parentSC.setMinHeight(168 + adj_height);
			parentSC.setMinHeight(168 + adj_height);
			parentSC.setExpandHorizontal(true);
			parentSC.setExpandVertical(true);

			// Slider : (Time Line) Create Slider
			FormData sliderTimeLineLayoutData = new FormData();
			sliderTimeLineLayoutData.height = 16;
			sliderTimeLineLayoutData.left = new FormAttachment(0, 1000, 80);
			sliderTimeLineLayoutData.right = new FormAttachment(1000, 1000, -2);
			sliderTimeLineLayoutData.bottom = new FormAttachment(870, 1000, -2);
			sliderTimeLine = new Slider(ownComposite, SWT.HORIZONTAL);
			sliderTimeLine.setLayoutData(sliderTimeLineLayoutData);
			// Initialize location of preview's slider
			sliderTimeLine.setIncrement(1);
			sliderTimeLine.setMinimum(1);
			// Add SelectionListener
			sliderTimeLine
					.addSelectionListener(new SliderTimeLineSelectionAdapter());
			// Initial setup location of TimeLine
			// ///setLocationTimeLineSlider(TL_DEF_ETIME, 0);

			// Create ScrolledComposite
			ownSC = new ScrolledComposite(ownComposite, SWT.BORDER);
			FormLayout SCTimeLineLayout = new FormLayout();
			ownSC.setLayout(SCTimeLineLayout);
			FormData SCTimeLineLData = new FormData();
			SCTimeLineLData.width = 479;
			SCTimeLineLData.top = new FormAttachment(PreviewPanel.getInstance()
					.getPlayButton(), 2);
			SCTimeLineLData.left = new FormAttachment(0, 1000, 80);
			SCTimeLineLData.right = new FormAttachment(1000, 1000, -2);
			SCTimeLineLData.bottom = new FormAttachment(sliderTimeLine, 0);
			ownSC.setLayoutData(SCTimeLineLData);
			// Initial setup maximum size of slider of TimeLine
			// reqSetMaximumSliderTimeLine(getMaxTimeLine());

			// child composite
			childComposite = new Composite(ownSC, SWT.NONE);
			FormLayout childTimeLineLayout = new FormLayout();
			childComposite.setLayout(childTimeLineLayout);
			FormData childTimeLineLData = new FormData();
			childTimeLineLData.top = new FormAttachment(0, 1000, 0);
			childTimeLineLData.left = new FormAttachment(0, 1000, 0);
			childTimeLineLData.right = new FormAttachment(1000, 1000, 0);
			childTimeLineLData.bottom = new FormAttachment(1000, 1000, 0);
			childComposite.setLayoutData(childTimeLineLData);

			// 1)Canvas : Time Line
			canvasTimeLine = TimeLineCanvas.getInstance(childComposite);
			FormLayout canvasTimeLineLayout = new FormLayout();
			canvasTimeLine.setLayout(canvasTimeLineLayout);
			FormData canvasTimeLineLData = new FormData();
			canvasTimeLineLData.height = 20;
			canvasTimeLineLData.top = new FormAttachment(0, 1000, 0);
			canvasTimeLineLData.left = new FormAttachment(0, 1000, 0);
			canvasTimeLineLData.right = new FormAttachment(1000, 1000, 0);
			// initial setup
			canvasTimeLine.setLayoutData(canvasTimeLineLData);
			canvasTimeLine.setBackground(parentDisp
					.getSystemColor(SWT.COLOR_WHITE));
			canvasTimeLine.pack();
			reqRedrawTimeLineCanvas(1);

			// 2)Composite : Audio Script
			compositeAudio = new AudioComposite(childComposite);
			FormLayout compositeScriptAudioLayout3 = new FormLayout();
			compositeAudio.setLayout(compositeScriptAudioLayout3);
			FormData compositeScriptAudioLData3 = new FormData();
			compositeScriptAudioLData3.height = TL_AUDIO1_SY + adj_height;
			compositeScriptAudioLData3.left = new FormAttachment(0, 1000, 0);
			compositeScriptAudioLData3.right = new FormAttachment(1000, 1000, 0);
			// TODO layout for with caption
			// compositeScriptAudioLData3.bottom = new FormAttachment(1000,
			// 1000,
			// -(TL_AUDIO1_SY + adj_height));
			compositeScriptAudioLData3.bottom = new FormAttachment(1000, 1000,
					0);

			compositeAudio.setLayoutData(compositeScriptAudioLData3);
			compositeAudio.setBackground(new Color(parentDisp, 255, 239, 215));
			compositeAudio.pack();

			// 2)Composite : Caption Script
			// compositeCaption = new CaptionComposite(childComposite);
			// FormLayout compositeScriptAudioLayout2 = new FormLayout();
			// compositeCaption.setLayout(compositeScriptAudioLayout2);
			// FormData compositeScriptAudioLData2 = new FormData();
			// compositeScriptAudioLData2.height = TL_AUDIO1_SY + adj_height;
			// compositeScriptAudioLData2.left = new FormAttachment(0, 1000, 0);
			// compositeScriptAudioLData2.right = new FormAttachment(1000, 1000,
			// 0);
			// compositeScriptAudioLData2.bottom = new FormAttachment(1000,
			// 1000,
			// 0);
			// compositeCaption.setLayoutData(compositeScriptAudioLData2);
			// compositeCaption
			// .setBackground(new Color(parentDisp, 255, 239, 215));
			// compositeCaption.pack();

			// Label : Volume Level's caption
			labelVolumeLevel = new Label(ownComposite, SWT.NONE);
			labelVolumeLevel.setVisible(false);
			labelVolumeLevel.setText("Volume Level");

			// 3)Canvas : Volume Level
			canvasVolumeLevel = VolumeLevelCanvas.getInstance(childComposite);
			// Layout : Volume Level Canvas into ScrolledComposite
			FormLayout canvasVolumeLevelLayout = new FormLayout();
			canvasVolumeLevel.setLayout(canvasVolumeLevelLayout);
			FormData canvasVolumeLevelLData = new FormData();
			// canvasVolumeLevelLData.height = 100;
			canvasVolumeLevelLData.top = new FormAttachment(canvasTimeLine, 0);
			canvasVolumeLevelLData.left = new FormAttachment(0, 1000, 0);
			canvasVolumeLevelLData.right = new FormAttachment(1000, 1000, 0);
			canvasVolumeLevelLData.bottom = new FormAttachment(compositeAudio,
					0);
			// initial setup
			canvasVolumeLevel.setLayoutData(canvasVolumeLevelLData);
			canvasVolumeLevel.setBackground(parentDisp
					.getSystemColor(SWT.COLOR_WHITE));
			canvasVolumeLevel.pack();
			// initial draw Canvas
			reqRedrawVolumeLevelCanvas(1);
			labelVolumeLevel.setVisible(true);

			labelAudio1LData = new FormData();
			labelAudio1LData.width = 70;
			labelAudio1LData.left = new FormAttachment(0, 1000, 4);
			// TODO layout for with Caption
			// labelAudio1LData.bottom = new FormAttachment(920, 1000,
			// -((TL_AUDIO1_SY + adj_height) + (TL_AUDIO1_SY + adj_height)
			// / 2 + 55));
			labelAudio1LData.bottom = new FormAttachment(1000, 1000,
					-(TL_AUDIO1_SY + (TL_AUDIO1_MDRAG_SY * TL_DEF_LINES)));
			labelAudio1 = new Label(ownComposite, SWT.NONE);
			labelAudio1.setLayoutData(labelAudio1LData);
			labelAudio1.setText("Audio Description");
			labelAudio1.setAlignment(SWT.RIGHT);

			// TODO recover later
			// labelAudio1LData = new FormData();
			// labelAudio1LData.width = 70;
			// labelAudio1 = new Label(ownComposite, SWT.NONE);
			// labelAudio1.setLayoutData(labelAudio1LData);
			// labelAudio1.setText("Caption");
			// labelAudio1LData.left = new FormAttachment(0, 1000, 4);
			// labelAudio1LData.bottom = new FormAttachment(920, 1000,
			// -((TL_AUDIO1_SY + adj_height) / 2 + 55));
			// labelAudio1.setLayoutData(labelAudio1LData);

			/*
			 * TODO recover later : buttons to mark labelAudio1LData = new
			 * FormData(); labelAudio1LData.width = 100; labelAudio1LData.left =
			 * new FormAttachment(0, 1000, 80); labelAudio1LData.bottom = new
			 * FormAttachment(920, 1000, +15);
			 * 
			 * 
			 * Button button = new Button(ownComposite, SWT.PUSH);
			 * button.setText("Audio Description");
			 * button.setLayoutData(labelAudio1LData);
			 * button.addSelectionListener(new SelectionListener() {
			 * 
			 * public void widgetSelected(SelectionEvent e) { int currentTime =
			 * canvasTimeLine .getCurrentPositionMarkerTimeLine(); IScriptData
			 * data = ScriptDataFactory.createNewData();
			 * data.setType(IScriptData.TYPE_AUDIO); data.setMark(PLAY_MARK);
			 * data.setStartTime(currentTime); data.setEndTime(0);
			 * data.setVgLang(DESC_LANG_JA);
			 * dataEventManager.fireGuideListEvent(new GuideListEvent(
			 * GuideListEvent.ADD_DATA, data, this));
			 * 
			 * dataEventManager.fireLabelEvent(new LabelEvent(
			 * LabelEvent.ADD_PLAY_MARK, data, this));
			 * 
			 * dataEventManager.fireGuideListEvent(new GuideListEvent(
			 * GuideListEvent.SET_DATA, data, this));
			 * 
			 * }
			 * 
			 * public void widgetDefaultSelected(SelectionEvent e) { } });
			 * 
			 * labelAudio1.setAlignment(SWT.RIGHT);
			 * 
			 * labelAudio1LData = new FormData(); labelAudio1LData.width = 100;
			 * labelAudio1LData.left = new FormAttachment(0, 1000, 30 + 85 +
			 * 80); labelAudio1LData.bottom = new FormAttachment(920, 1000,
			 * +15); Button button2 = new Button(ownComposite, SWT.TOGGLE);
			 * 
			 * button2.setText("Caption");
			 * button2.setLayoutData(labelAudio1LData);
			 * 
			 * button2.addMouseListener(new MouseAdapter() { public void
			 * mouseDown(MouseEvent e) { if (getStatusTimeLine() !=
			 * TL_STAT_PLAY) { MessageDialog.openError(Display.getCurrent()
			 * .getActiveShell(), "Error",
			 * "can not use without playing movie."); return; }
			 * captionStartTimeLine = canvasTimeLine
			 * .getCurrentPositionMarkerTimeLine(); }
			 * 
			 * public void mouseUp(MouseEvent e) { if (getStatusTimeLine() !=
			 * TL_STAT_PLAY) { return; } int end =
			 * canvasTimeLine.getCurrentPositionMarkerTimeLine();
			 * 
			 * IScriptData data = ScriptDataFactory.createNewData();
			 * data.setType(IScriptData.TYPE_CAPTION);
			 * data.setMark(CAPTION_MARK);
			 * data.setStartTime(captionStartTimeLine); data.setEndTime(end);
			 * data.setDataCommit(true); // data.setCaption(" ");
			 * dataEventManager.fireGuideListEvent(new GuideListEvent(
			 * GuideListEvent.ADD_DATA, data, this));
			 * dataEventManager.fireLabelEvent(new LabelEvent(
			 * LabelEvent.PUT_LABEL, data, this));
			 * dataEventManager.fireGuideListEvent(new GuideListEvent(
			 * GuideListEvent.SET_DATA, data, this)); } });
			 * 
			 * labelAudio1.setAlignment(SWT.RIGHT);
			 */

			// Initial SetUP for own ScrolledComposite
			ownSC.setContent(childComposite);
			ownSC.setSize(childComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			ownSC.setMinWidth((endTimeLine / TIME2PIXEL));
			ownSC.setExpandHorizontal(true);
			ownSC.setExpandVertical(true);
			// Add PaintListener for Resize action
			ownSC.addPaintListener(new ResizeScrolledCompositeListener());

			// Update View window
			childComposite.layout();
			childComposite.pack();
			ownComposite.layout();
			ownComposite.pack();

		} catch (Exception e) {
			System.out.println("TimeLineView() : Exception = " + e);
		}
	}

	/**
	 * Getter method : Get StartTime of TimeLine
	 */
	public int getCurrentTimeLine() {
		return (currentTimeLine);
	}

	/**
	 * Returns current scroll bar size
	 */
	public Point getCurrentSizeScrollBar() {
		return (sliderTimeLine.getSize());
	}

	/**
	 * Getter method : Get current size of (parent)ScrolledComposite
	 */
	public Point getSizeParentSC() {
		Point result = new Point(0, 0);
		if (ownSC != null) {
			result = ownSC.getSize();
		}
		return (result);
	}

	/**
	 * Setter method : Get current location of TimeLine Composite
	 */
	public void setCurrentLocation(int nowLocation) {
		// PickUP current size of Scrolled Composite
		Point nowSize = ownSC.getSize();

		// Calculate current end time line
		int nowEndTime = (nowLocation + nowSize.x) * TIME2PIXEL;
		// Calculate new index of location
		currentTimeLineLocation = (nowEndTime / TL_DEF_SCROL_COMP_SCALE) - 1;
		if (currentTimeLineLocation < 0) {
			// minimum limit
			currentTimeLineLocation = 0;
		}

		// check end time limit
		if (currentTimeLineLocation >= TL_MAX_ENDTIME_COUNT) {
			// adjust max value
			currentTimeLineLocation = TL_MAX_ENDTIME_COUNT - 1;
		}
		// check start time limit
		else if (currentTimeLineLocation < 0) {
			// adjust min value
			currentTimeLineLocation = 0;
		}
	}

	/**
	 * Setter method : Adjust Label position of VolumeLevel Canvas's caption
	 */
	public void setPositionLabelVolumeLevel(int centreY) {
		// Check target label's instance
		if (labelVolumeLevel != null) {
			// Next Y position
			int nextY = centreY + 20 + 2 + 23;
			// Adjust label position by center position of VolumeLevel's Canvas
			FormData labelVolumeLevelLData = new FormData();
			labelVolumeLevelLData.width = 70;
			labelVolumeLevelLData.top = new FormAttachment(0, 1000, nextY);
			labelVolumeLevelLData.left = new FormAttachment(0, 1000, 4);
			labelVolumeLevel.setLayoutData(labelVolumeLevelLData);
			labelVolumeLevel.setAlignment(SWT.RIGHT);
			Point nowPos = labelVolumeLevel.getLocation();
			nowPos.y = nextY;
			labelVolumeLevel.setLocation(nowPos);
		}
	}

	/**
	 * Setter method : Set status of enabled play description
	 * 
	 * @return
	 */
	public void setEnableDescription(boolean stat) {
		currentEnableDescription = stat;
	}

	/**
	 * @category Request save volume level data to temporary file
	 */
	public void reqSaveVolumeLevelTempFile() {
		// call save volume level data process
		canvasVolumeLevel.saveVolumeLevelTempFile();
	}

	/**
	 * Setter method : Request scroll slider of ScrolledComposite
	 */
	private void reqScrollHorizontalTimeLine(int nowLocation) {
		// new location(X-Position)
		int newLocation = nowLocation;
		// PickUP current Composite size
		Point nowPon = ownSC.getSize();

		// check max limit
		int posEndTime = (getMaxTimeLine() / TIME2PIXEL);
		if (newLocation > (posEndTime - nowPon.x)) {
			// adjust end location
			newLocation = posEndTime - nowPon.x;
		}

		// adjust location counter(each 5mins)
		newLocation = newLocation
				- ((currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE) / TIME2PIXEL);

		// setup current composite position
		Point newPon = childComposite.getLocation();
		newPon.x = -newLocation;

		// update location of all parent Composite
		childComposite.setLocation(newPon);
	}

	/**
	 * Setter method : Request resize slider of ScrolledComposite
	 */
	private void reqSetMaximumSliderTimeLine(int endTime) {
		// Exchange from Time to Pixel
		int maxWidth = (endTime / TIME2PIXEL);

		// PickUP current Composite size
		Point nowPon = ownSC.getSize();
		// adjust width
		int sliderMaxWidth = maxWidth - nowPon.x;
		// update maximum size of slider
		sliderTimeLine.setMaximum(sliderMaxWidth);
	}

	/**
	 * Getter method : Check max limit of End TimeLine
	 * 
	 * @return -1 : backward TimeLine 1 : forward TimeLine 0 : no event
	 */
	private int isEndTimeLine(int nowLocation) {
		int result = TL_NO_EVENT;

		// PickUP current size of Scrolled Composite
		Point nowSize = ownSC.getSize();
		// Calculate current end time line
		int nowEndTime = (nowLocation + nowSize.x) * TIME2PIXEL;
		int nowStartTime = nowLocation * TIME2PIXEL;

		// Calculate previous end time line
		int previousEndTime = (currentTimeLineLocation + 2)
				* TL_DEF_SCROL_COMP_SCALE;
		int previousStartTime = currentTimeLineLocation
				* TL_DEF_SCROL_COMP_SCALE;

		// Check max limit of (End)TimeLine
		if (nowEndTime >= previousEndTime) {
			// Detect out of max limit
			result = TL_OVER_MAX_LIMIT;
		}
		// Check min limit
		else if ((nowStartTime < previousStartTime) && (previousStartTime > 0)) {
			// Detect out of min limit
			result = TL_OVER_MIN_LIMIT;
		}
		return (result);
	}

	/**
	 * Setter method : Request initialize location of ScrolledComposite
	 */
	private void reqInitLocationTimeLine() {
		// SetUP TimeLine's parameters by Media info.
		currentTimeLine = 0;
		movieEndTimeLine = WebBrowserFactory.getInstance().getTotalLength();
		// SetUP End TimeLine
		endTimeLine = movieEndTimeLine;
		int lastIndex = scriptManager.size() - 1;

		if (lastIndex >= 0) {
			// PickUP last ScriptData's EndTime
			int vgEndTime = scriptManager.getEndTime(lastIndex);
			int wavEndTime = scriptManager.getWavEndTime(lastIndex);
			// Adjust EndTimeLine
			if (vgEndTime > endTimeLine)
				endTimeLine = vgEndTime;
			if (wavEndTime > endTimeLine)
				endTimeLine = wavEndTime;
		} else {
			// Check EndTime
			if (endTimeLine == 0) {
				// Reset default initial value, Cause No media & No ScriptData
				endTimeLine = TL_DEF_ETIME;
			}
		}

		// Adjust end TimeLine by window scale size(5mins)
		int unitCount = endTimeLine / TL_DEF_SCROL_COMP_SCALE;
		if ((endTimeLine % TL_DEF_SCROL_COMP_SCALE) > 0)
			unitCount = unitCount + 1;
		endTimeLine = unitCount * TL_DEF_SCROL_COMP_SCALE;
		setCurrentLocation(0);

		// Initial setup index of ScriptData
		if (instTimerTimeLineManager != null) {
			instTimerTimeLineManager.seekIndexTimeLine(currentTimeLine);
		}
		// Initial synchronized each TimeLine info.
		synchronizeAllTimeLine(currentTimeLine);
	}

	/**
	 * Setter method : Request seek location of ScrolledComposite
	 */
	private void reqSeekLocationTimeLine() {
		// SetUP TimeLine's parameters by Media info.
		movieEndTimeLine = WebBrowserFactory.getInstance().getTotalLength();
		// SetUP End TimeLine
		endTimeLine = movieEndTimeLine;
		int lastIndex = scriptManager.size() - 1;
		if (lastIndex >= 0) {
			IScriptData data = scriptManager.getDataList().get(lastIndex);
			// PickUP last ScriptData's EndTime
			int vgEndTime = data.getEndTime();
			int wavEndTime = data.getWavEndTime();

			// Adjust EndTimeLine
			if (vgEndTime > endTimeLine)
				endTimeLine = vgEndTime;
			if (wavEndTime > endTimeLine)
				endTimeLine = wavEndTime;
		} else {
			// Check EndTime
			if (endTimeLine == 0) {
				// Reset default initial value, Cause No media & No ScriptData
				endTimeLine = TL_DEF_ETIME;
			}
		}

		// Adjust end TimeLine by window scale size(5mins)
		int unitCount = endTimeLine / TL_DEF_SCROL_COMP_SCALE;
		if ((endTimeLine % TL_DEF_SCROL_COMP_SCALE) > 0)
			unitCount = unitCount + 1;
		endTimeLine = unitCount * TL_DEF_SCROL_COMP_SCALE;

		// SetUP Current Time
		if (currentTimeLine < movieEndTimeLine) {
			currentTimeLine = WebBrowserFactory.getInstance()
					.getCurrentPosition();
		}
		// Initial setup index of ScriptData
		if (instTimerTimeLineManager != null) {
			instTimerTimeLineManager.seekIndexTimeLine(currentTimeLine);
		}
		// Initial synchronized each TimeLine info.
		synchronizeAllTimeLine(currentTimeLine);
	}

	/**
	 * @category Setter method
	 * @purpose Request SetUP current TimeLine by Preview's slider
	 */
	public void reqSetTrackCurrentTimeLine(int newTime) {
		// forced setup new TimeLine
		currentTimeLine = newTime;

		// Initial setup index of ScriptData
		if (instTimerTimeLineManager != null) {
			instTimerTimeLineManager.seekIndexTimeLine(currentTimeLine);
		}

	}

	/**
	 * Setter method : Request rewind time line
	 */
	public void reqRewindTimeLine() {
		// Rewind all time line
		setCurrentLocation(0);
		repaintAllTimeLine();

		// Rewind slider of ScrolledComposite
		int intdev = getMaxTimeLine() / TL_DEF_SCROL_COMP_SCALE;
		if ((getMaxTimeLine() % TL_DEF_SCROL_COMP_SCALE) > 0) {
			// increment counter
			intdev++;
		}
		sliderTimeLine.setSelection(0);
		reqSetMaximumSliderTimeLine((intdev * TL_DEF_SCROL_COMP_SCALE));
	}

	/**
	 * Setter method : Expand max size of TimeLine view
	 */
	public boolean reqExpandTimeLine() {
		boolean result = false;
		int index = -1;

		// Check Script data length
		index = scriptManager.size() - 1;// .getScriptSize() - 1; //

		if (index >= 0) {
			IScriptData data = scriptManager.getDataList().get(index);
			// PickUP end time of last script data
			int endTime = data.getEndTime();
			int wavEndTime = data.getWavEndTime();//
			if (endTime < wavEndTime)
				endTime = wavEndTime;
			// PickUP current size of Composite of TimeLine
			Point nowPon = childComposite.getSize();
			int maxTimeComp = nowPon.x * TIME2PIXEL;
			if (maxTimeComp < 0)
				maxTimeComp = 0;

			// Check current End TimeLine
			if ((endTime > TL_DEF_ETIME) && (endTime > maxTimeComp)) {
				// Expand TimeLine's Composite width to new End Time
				setMaxTimeLine(endTime);
				// Resize slider of TimeLine
				reqSetMaximumSliderTimeLine(getMaxTimeLine());

				// Synchronize Preview view
				PreviewPanel.getInstance().synchronizeTimeLine(
						getCurrentTimeLine());
				result = true;
			}
		}
		return (result);
	}

	/**
	 * Setter method : Expand max size of TimeLine view
	 */
	private void reqExpandTimeLine(int newEndTime) {
		// PickUP current End TimeLine value
		int endTime = getMaxTimeLine();

		// Check current End TimeLine
		if (newEndTime > endTime) {
			// Expand TimeLine's Composite width to new End Time
			setMaxTimeLine(newEndTime);
			// Resize slider of TimeLine
			reqSetMaximumSliderTimeLine(getMaxTimeLine());

			// Synchronize Preview view
			PreviewPanel.getInstance()
					.synchronizeTimeLine(getCurrentTimeLine());
		}
	}

	/**
	 * Setter method : Request increment location of ScrolledComposite
	 */
	private void updateLocationTimeLine(int nextTime) {
		// PickUP current size of scroll bar
		Point scPos = getCurrentSizeScrollBar();

		// Get start time by nextValue
		int sliderStartTime = (nextTime / TIME2PIXEL) - (scPos.x >> 1);
		// Check max limit TimeLine
		if (TL_NO_EVENT != isEndTimeLine(sliderStartTime)) {
			// Update scroll counter of TimeLine
			setCurrentLocation(sliderStartTime);
			// Expand end TimeLine
			int newEndTime = (currentTimeLineLocation + 2)
					* TL_DEF_SCROL_COMP_SCALE;
			if (newEndTime > getMaxTimeLine()) {
				// Expand TimeLine's Composite width to new End Time
				setMaxTimeLine(newEndTime);
				// Resize slider of TimeLine
				reqSetMaximumSliderTimeLine(getMaxTimeLine());
				// Synchronize Preview view
				PreviewPanel.getInstance().synchronizeTimeLine(
						getCurrentTimeLine());
			}
			// check expand time line
			repaintAllTimeLine();
		}

		// MakeUP next position
		int nextValue = (nextTime / TIME2PIXEL) - (scPos.x >> 1);
		if (nextValue < 0)
			nextValue = 0;
		// Update location of scroll bar
		// set max scroll bar size of ScrolledComposite
		sliderTimeLine.setSelection(nextValue);

		// adjust next time line to offset time(times 10mins)
		int targetTime = nextTime
				- (currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE);
		if (targetTime < 0)
			targetTime = 0;

		// MakeUP next position
		nextValue = (targetTime / TIME2PIXEL) - (scPos.x >> 1);
		if (nextValue < 0)
			nextValue = 0;

		// setup current composite position
		Rectangle nowPon = childComposite.getBounds();
		int nextCentre = -nextValue;

		// check max limit
		if ((-nextCentre + scPos.x) > nowPon.width) {
			// adjust next location
			nextValue = nowPon.width - scPos.x;
			nextCentre = -nextValue;
		}
		// check min limit
		else if (nextCentre > 0) {
			nextValue = 0;
			nextCentre = 0;
		}

		// Update location of TimeLine Composite
		childComposite.setLocation(nextCentre, nowPon.y);
	}

	/**
	 * Setter method : Request increment location of ScrolledComposite
	 */
	public void resetLocationTimeLine(int startTime, int labelEndTime) {
		// MakeUP next position
		int nextValue = startTime / TIME2PIXEL;

		// setup current composite position
		Rectangle nowPon = childComposite.getBounds();
		int nextCentre = -nextValue;

		// Update repaint status before access TimeLine's contexts
		reqExpandTimeLine(labelEndTime);
		// ///canvasTimeLine.adjustMaxSizeTimeLine(labelEndTime);

		// Adjust next position
		nextValue = nextValue
				+ (currentTimeLineLocation * TL_DEF_SCROL_COMP_T2P);

		// Update location of scroll bar
		// set max scroll bar size of ScrolledComposite
		sliderTimeLine.setSelection(nextValue);
		// Update location of TimeLine Composite
		childComposite.setLocation(nextCentre, nowPon.y);
	}

	private void repaintAllTimeLine() {
		// PickUP current location of TimeLine
		int nowCnt = currentTimeLineLocation;
		eventManager.fireSyncTimeEvent(new SyncTimeEvent(
				SyncTimeEvent.REFRESH_TIME_LINE, nowCnt, this));
	}

	/**
	 * @category Setter method
	 * @purpose Synchronize TimeLine manager
	 * 
	 */
	public void synchronizeAllTimeLine(int nowTime) {
		// PickUP current location of TimeLine
		eventManager.fireSyncTimeEvent(new SyncTimeEvent(
				SyncTimeEvent.SYNCHRONIZE_TIME_LINE, nowTime, this));
	}

	/**
	 * Setter method : Request repaint Canvas via this.view
	 */
	public void reqRedrawTimeLineCanvas(int stat) {
		// Request component access
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.asyncExec(new Runnable() {
			public void run() {
				// Request redraw Canvas
				canvasTimeLine.initPositionMarkerTimeLine();
			}
		});
	}

	/**
	 * Setter method : Request repaint Canvas via this.view
	 */
	public void reqRedrawVolumeLevelCanvas(int stat) {
		// Call Setter method
		canvasVolumeLevel.setStatusCanvasVolumeLevel(stat);
	}

	/**
	 * Cleanup captured data of movie audio
	 */
	public void cleanupCaptureData() {
		canvasVolumeLevel.cleanupMovieAudioLevel();
	}

	/**
	 * Load volume level data from data file
	 */
	public void reqLoadVolumeLevelData() {
		canvasVolumeLevel.loadVolumeLevelTempFile();
	}

	private void reqPlayCaption(IScriptData data) {
		// TODO need to implement caption part
	}

	/**
	 * Setter method : Play audio(voice or WAV)
	 */
	public void reqPlayAudio(IScriptData data) {
		// Case : play voice
		if (!(data.isWavEnabled() && data.getWavURI() != null)) {

			// check extended parameter
			if (data.isExtended()) {
				// Change extended play mode
				WebBrowserFactory.getInstance().pauseMedia();
				currentStatusTimeLine = TL_STAT_EXTENDED;
			}
			reqStartVoicePlayer(data);
		} else { // Case : play WAV file
			if (data.isExtended()) {
				WebBrowserFactory.getInstance().pauseMedia();
				currentStatusTimeLine = TL_STAT_EXTENDED;
			}
			// Play WAV, Now!
			URI wavFName = data.getWavURI();
			// TODO if not exist, use TTS
			float wavCompetitiveRatio = data.getWavPlaySpeed();
			SoundMixer.getInstance().startPlaySound(wavFName,
					wavCompetitiveRatio);
		}
	}

	/**
	 * Returns current audio status
	 */
	private boolean isRunningAudio() {
		// Check status of voice engine and play WAV file
		boolean result = voicePlayer.getPlayVoiceStatus()
				| SoundMixer.getInstance().isRunningPlaySound();
		return (result);
	}

	/**
	 * Setter method : Request Stop & Dispose preview voice process
	 */
	public void reqStopScriptAudio() {
		voicePlayer.stop();
		if (canvasVolumeLevel != null) {
			canvasVolumeLevel.stopSampling();
		}
	}

	/**
	 * Setter method : Request Stop & Dispose capture audio of movie
	 */
	private void reqStopCaptureAudio() {
		// Stop & Dispose SoundMixer
		SoundMixer.getInstance().stopCaptureSound();
		SoundMixer.getInstance().stopPlaySound();
		SoundMixer.getInstance().dispose();
	}

	private void reqStartVoicePlayer(IScriptData data) {
		voicePlayer.speak(data);
		voicePlayer.setPlayVoiceStatus(1); // for sampling
	}

	/**
	 * Request Stop VoicePlayer
	 */
	public void reqStopVoicePlayer() {
		// Stop ProTalker
		voicePlayer.stop();
		voicePlayer.setPlayVoiceStatus(-1); //
		startDurationVoice = -1; // reset duration.
	}

	/**
	 * update label
	 * 
	 * @param data
	 */
	private void updateEndTimeVolumeLabel(IScriptData data) {

		if (startDurationVoice == -1) {
			return;
		}

		int startTime = data.getStartTime();// scriptManager.getStartTime(index);
		int nowDuration = (int) (endDurationVoice - startDurationVoice);
		if (nowDuration > 0) {

			// MakeUP new end time
			int newEndTime = startTime + nowDuration;
			if (data.isWavEnabled()) {
				if (newEndTime == data.getWavEndTime()) {
					// no change return
					return;
				}
			} else {
				if (data.isEndTimeAccurate() || newEndTime == data.getEndTime()) {
					// no change return
					return;
				}
			}

			// TODO check order change
			if (data.isWavEnabled()) {
				data.setWavEndTime(newEndTime);
			} else {
				data.setEndTime(newEndTime);
			}
			// replace label and data.
			dataEventManager.fireLabelEvent(new LabelEvent(
					LabelEvent.PUT_LABEL, data, this));
			dataEventManager.fireGuideListEvent(new GuideListEvent(
					GuideListEvent.REPALCE_DATA, data, this));

		}
	}

	/**
	 * Getter method : Get Max TimeLine
	 */
	public int getMaxTimeLine() {
		// default(initial) size
		int result = TL_DEF_ETIME;

		// Check current Movie's end time(length)
		if (result < movieEndTimeLine) {
			result = movieEndTimeLine;
		}
		// Check current ScriptList's end time(last data's end time)
		if (result < endTimeLine) {
			result = endTimeLine;
		}

		// Return Max TimeLine
		return (result);
	}

	/**
	 * Setter method : Set Max TimeLine
	 */
	private void setMaxTimeLine(int newEndTime) {
		// calculate scale's counter
		int intdev = newEndTime / TL_DEF_SCROL_COMP_SCALE;
		if ((newEndTime % TL_DEF_SCROL_COMP_SCALE) > 0)
			intdev = intdev + 1;

		// expand end TimeLine
		endTimeLine = intdev * TL_DEF_SCROL_COMP_SCALE;

		// check max limit
		if (endTimeLine > TL_MAX_ENDTIME_MSEC) {
			endTimeLine = TL_MAX_ENDTIME_MSEC;
		}
		// check min limit
		else if (endTimeLine < TL_DEF_ETIME) {
			endTimeLine = TL_DEF_ETIME;
		}
	}

	/**
	 * Refresh all Script Audio Label from current ScriptList
	 */
	public void refreshScriptAudio() {

		List<IScriptData> list = scriptManager.getDataList();
		// Reset script audio label on new time scale
		for (int i = 0; i < list.size(); i++) {
			IScriptData data = list.get(i);
			if (data.getType() == IScriptData.TYPE_AUDIO) {
				compositeAudio.putLabel(data);
			} else if (data.getType() == IScriptData.TYPE_CAPTION) {
				// TODO recover later
				// compositeCaption.putLabel(data, MODE_PUT);
			}
		}
	}

	/**
	 * Setter method : Adjust TimeLine for Audio Label
	 */
	public int adjustEndTimeLine() {
		int result = TL_NO_EVENT;

		// Check max limit TimeLine
		int nowLocation = (sliderTimeLine.getSelection());
		if (TL_NO_EVENT != isEndTimeLine(nowLocation)) {
			// Update scroll counter of TimeLine
			setCurrentLocation(nowLocation);
			// check expand time line
			repaintAllTimeLine();
			// horizontal scroll window
			reqScrollHorizontalTimeLine(nowLocation);
		}
		return (result);
	}

	class SliderTimeLineSelectionAdapter extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			// get current location
			Slider ps = (Slider) e.getSource();
			int nowLocation = ps.getSelection();

			// Check max limit TimeLine
			if (TL_NO_EVENT != isEndTimeLine(nowLocation)) {
				// Update scroll counter of TimeLine
				setCurrentLocation(nowLocation);
				// check expand time line
				repaintAllTimeLine();
			}

			// horizontal scroll window
			reqScrollHorizontalTimeLine(nowLocation);
		}
	}

	/**
	 * PaintListener for ScrolledComposite of TimeLine
	 */
	class ResizeScrolledCompositeListener implements PaintListener {
		public void paintControl(PaintEvent e) {
			// expand width of own Composite
			if (e.width > 0) {
				// right shift X position as Resize action
				Point newPon = childComposite.getLocation();
				// update location of all parent Composite
				childComposite.setLocation((newPon.x + e.width), newPon.y);
			}
		}
	}

	/**
	 * Switching action for TimeLine
	 */
	public void switchActionTimeLine(boolean sw) {
		// Play movie & Play Voice automatically
		if (sw) {
			reqSeekLocationTimeLine();
			if (previousPlayerStatus) {
				currentStatusTimeLine = TL_STAT_PLAY;
				previousPlayerStatus = false;
			} else {
				currentStatusTimeLine = TL_STAT_PLAY;
			}
		} else { // Pause movie & Pause Voice immediately
			pauseTimeLine = currentTimeLine;

			currentStatusTimeLine = TL_STAT_PAUSE;
			previousPlayerStatus = true;

		}
	}

	/**
	 * Setter method : Rewind TimeLine
	 */
	public void rewindActionTimeLine() {
		// Rewind all parameters
		previousPlayerStatus = false;
		currentTimeLine = TL_DEF_STIME;
		previousTimeLine = -1;
		instTimerTimeLineManager.initIndexTimeLine(0);
		// Seek location of TimeLine view
		reqInitLocationTimeLine();
	}

	// class SynchronizeTimeLineTimer {

	private boolean synchronizeCurrentTimeLine() {

		previousMovieStatus = currentMovieStatus;
		currentMovieStatus = WebBrowserFactory.getInstance().getVideoStatus();
		currentMovieTimeLine = WebBrowserFactory.getInstance()
				.getCurrentPosition();

		// Synchronized running TimeLine
		if (currentMovieStatus != V_STAT_NOMEDIA) {
			// SetUP Current Time
			if (currentTimeLine != currentMovieTimeLine) {
				// Store current video position
				previousTimeLine = currentTimeLine;
				currentTimeLine = currentMovieTimeLine;
				return true;
			}
			return false;
		}
		return false;
	}

	// }

	/**
	 * returns current status of TimeLine
	 */
	public int getStatusTimeLine() {
		return (currentStatusTimeLine);
	}

	class TimeLineManager {
		private TimeLineView instParentView = null;
		private ScriptDataManager localScriptManager = null;

		// Index pointer of ScriptList
		private int indexScriptData = 0;

		private int currentScriptDataIndex = -1; // sampling data index
		private IScriptData currentSamplingData = null;

		private int ownCurrentTimeLine = 0;
		private int ownPreviousTimeLine = 0;

		public TimeLineManager(TimeLineView instParentView,
				ScriptDataManager scriptManager) {

			this.instParentView = instParentView;
			this.localScriptManager = scriptManager;

			// initial parameters
			initIndexTimeLine(TL_STAT_IDLE);
		}

		public void initIndexTimeLine(int nextStatus) {
			// initial index of next script data
			indexScriptData = 0;
			while (true) {
				IScriptData data = scriptManager.getData(indexScriptData);
				if (data == null || data.isDataCommit() == true
						&& data.getType() == IScriptData.TYPE_AUDIO)
					break;
				indexScriptData++;
			}
			ownCurrentTimeLine = currentTimeLine;
			ownPreviousTimeLine = previousTimeLine;
			if (scriptManager.size() <= 0) {
				// End of Data
				indexScriptData = TL_EOL;
			}
		}

		/**
		 * Local method : Seek index of Script pointer & Refresh inner
		 * parameters
		 */
		public int seekIndexTimeLine(int targetTime) {
			// If index is -1, then no exist data(currentTime)
			int index;
			int len = scriptManager.size();

			int previousTime = 0;

			if (len > 0) {
				for (index = 0; index < len; index++) {
					IScriptData data = localScriptManager.getData(index);
					if (data.getType() != IScriptData.TYPE_AUDIO) {
						continue;
					}
					int startTime = data.getStartTime();

					// Check limit
					if ((previousTime <= startTime)
							&& (targetTime <= startTime)) {
						// current i value is next Script's index
						break;
					}
					// Check Start Time '00:00.000'
					else if ((startTime <= TL_MARGIN_STARTTIME)
							&& ((startTime + TL_MARGIN_STARTTIME) >= targetTime)) {
						// current i value is next Script's index
						break;
					}

					// Update previous time
					previousTime = startTime;
				}
				// Limit check for index
				if (index >= localScriptManager.size()) {
					// Limit Over, then reset no exist data
					index = TL_EOL;
				}
			}
			// Nothing script data
			else {
				index = TL_NODATA;
			}

			// Reset index of next ScriptData
			indexScriptData = index;
			ownPreviousTimeLine = previousTime;

			return (index);
		}

		/**
		 * Local method : Increment index of target ScriptData
		 */
		private void incrementIndexTimeLine() {
			IScriptData prevData = null;
			IScriptData data = null;
			while (true) {
				while (true) {
					prevData = scriptManager.getData(indexScriptData++);
					if (prevData == null
							|| (prevData.getType() == IScriptData.TYPE_AUDIO && prevData
									.isDataCommit() == true)) {
						break;
					}
				}
				if (prevData == null)
					break;
				int i = 0;
				while (true) {
					data = scriptManager.getData(indexScriptData + i);
					if (data == null
							|| (data.getType() == IScriptData.TYPE_AUDIO && data
									.isDataCommit() == true)) {
						break;
					}
					indexScriptData++;
				}
				if (data == null) {
					break;
				}
				if (data.getStartTime() == prevData.getStartTime()) {
					i++;
					continue;
				}
				break;
			}
			if (indexScriptData >= localScriptManager.size()) {
				// Limit Over, then reset as End of List(-1)
				indexScriptData = TL_EOL;
			}
		}

		/**
		 * Setter method : Check TimeLine modify
		 */
		public boolean checkCurrentTimeLine() {
			boolean result = false;

			// Synchronized running TimeLine
			if (currentMovieStatus != V_STAT_NOMEDIA) {
				// SetUP Current Time
				if (ownCurrentTimeLine != currentTimeLine) {
					// Store current video position
					ownPreviousTimeLine = ownCurrentTimeLine;
					ownCurrentTimeLine = currentTimeLine;
					// check previous track control
					if (ownPreviousTimeLine > ownCurrentTimeLine) {
						// search index of description
						seekIndexTimeLine(ownCurrentTimeLine);
					}
					// Set result
					result = true;
				}
			}
			// return result
			return (result);
		}

		/**
		 * Checker method : Check current index of ScriptList
		 */
		private boolean watchdocIndexTimeLine() {
			// Result is true, then immediately access VoicePlayer as current
			// index
			boolean result = false;
			// VoiceGuideData scriptData =
			// ScriptManager.getVoiceGuidData(indexScriptData);

			// Check Limit of index
			if (indexScriptData > TL_EOL) {
				// IScript data = localScriptManager.getScriptDataList().get(
				// indexScriptData);
				IScriptData data = localScriptManager.getData(indexScriptData);
				if (!data.isDataCommit()
						|| data.getType() != IScriptData.TYPE_AUDIO) {
					// incrementIndexTimeLine();
					// skip not AUDIO label data.
					while (true) {
						IScriptData prevData = scriptManager
								.getData(indexScriptData);
						if (prevData == null
								|| (prevData.getType() == IScriptData.TYPE_AUDIO && prevData
										.isDataCommit() == true)) {
							break;
						}
						indexScriptData++;
					}
					return result;
				}

				// PickUP StartTime as current index of ScriptList
				int startTime = data.getStartTime();
				int endTime = 0;
				if (data.isWavEnabled() && data.getWavURI() != null) {
					endTime = data.getWavEndTime();
				} else {
					endTime = data.getEndTime();
				}

				// search next index of description, cause past time line
				if (endTime <= ownCurrentTimeLine) {
					while (endTime <= ownCurrentTimeLine) {
						// Past TimeLine, then search next script data('s index)
						incrementIndexTimeLine();
						// Check End of List
						if (indexScriptData == TL_EOL)
							return (result);
						data = localScriptManager.getData(indexScriptData);
						startTime = data.getStartTime();// localScriptManager
						if (data.isWavEnabled() && data.getWavURI() != null) {
							endTime = data.getWavEndTime();
						} else {
							endTime = data.getEndTime();
						}
					}
				}

				// Check Start Time
				if ((ownPreviousTimeLine < startTime)
						&& (startTime <= ownCurrentTimeLine)) {
					// Now Play voice timing by current index's Script data
					result = true;
				}
				// Check Start Time for 00:00.000
				else if ((startTime <= TL_MARGIN_STARTTIME)
						&& (endTime >= ownCurrentTimeLine)) {
					// Delayed Play voice timing by current index's Script data
					result = true;
				}
			}

			// return result
			return (result);
		}

		/**
		 * @category Run method of Timer Task
		 */
		public void timerTask() {
			if (currentDragStatus == true) {
				return;
			}

			// Get current own status
			int nowStat = getStatusTimeLine();
			if (synchronizeCurrentTimeLine() == false
					&& nowStat != TL_STAT_EXTENDED
					&& previousMovieStatus == currentMovieStatus) {
				return;
			}

			boolean result = checkCurrentTimeLine();
			if (result) {
				if (nowStat == TL_STAT_PLAY) {
					// Check next timing for play Voice
					boolean result2 = watchdocIndexTimeLine();
					if (result2) {
						IScriptData data = localScriptManager.getDataList()
								.get(indexScriptData);
						if (currentScriptDataIndex != -1
								&& currentScriptDataIndex != indexScriptData) {
							endDurationVoice = Calendar.getInstance()
									.getTimeInMillis();
							updateEndTimeVolumeLabel(currentSamplingData);
							currentScriptDataIndex = -1;
						}
						currentScriptDataIndex = indexScriptData;
						currentSamplingData = (IScriptData) data;
						// TODO check data is stable or not(isDataCommit?)

						if (data.getType() == IScriptData.TYPE_CAPTION) {
							dataEventManager
									.fireGuideListEvent(new GuideListEvent(
											GuideListEvent.PLAY_LABEL, data,
											this));

							instParentView.reqPlayCaption(data);

						} else {
							if (currentEnableDescription) {
								// Check current Player status
								if (instParentView.voicePlayer
										.getPlayVoiceStatus()) {
									// Forced Stop VoicePlayer
									instParentView.reqStopScriptAudio();
								}

								dataEventManager
										.fireGuideListEvent(new GuideListEvent(
												GuideListEvent.PLAY_LABEL,
												data, this));

								// Play Voice, Now!
								instParentView.reqPlayAudio(data);
								if (!(data.isWavEnabled() == true && data
										.getWavURI() != null)) {
									voicePlayer.setPlayVoiceStatus(1);
								}
								// Set status flag
								indexCurrentScriptData = indexScriptData;
								startDurationVoice = Calendar.getInstance()
										.getTimeInMillis();
								currentVoiceEngineAction = true;

							}

						}
						incrementIndexTimeLine();
					} else { // For end time update
						IScriptData data = null;

						if (localScriptManager.getDataSet().size() > indexCurrentScriptData) {
							data = localScriptManager
									.getData(indexCurrentScriptData);
						}

						if (currentVoiceEngineAction
								&& currentEnableDescription) {
							if (!(instParentView.isRunningAudio())) {
								// Reset status flag
								currentVoiceEngineAction = false;
								// Update end time of current
								// description
								if (data != null) {
									updateEndTimeVolumeLabel(data);
									currentScriptDataIndex = -1;

									// Finish high-light for target
									dataEventManager
											.fireGuideListEvent(new GuideListEvent(
													GuideListEvent.DESELECT_DATA,
													data, this));
								}
							} else {
								// increment duration counter
								endDurationVoice = Calendar.getInstance()
										.getTimeInMillis();
							}
						}
					}

					// Synchronize TimeLine each views
					instParentView.synchronizeAllTimeLine(ownCurrentTimeLine);

					// Check current movie status
					if (currentMovieStatus != V_STAT_PLAY) {
						// Forced change own status(action)
						PreviewPanel.getInstance().playPauseMedia();
					}
				}
				// TL_STAT_PAUSE : Status is Pause action
				else if ((nowStat == TL_STAT_PAUSE)
						|| (nowStat == TL_STAT_IDLE)) {
					if (adjustTimeLine == true) {
						// Adjust TimeLineView location when a TreeViewer line
						// is selected,
						pauseTimeLine = currentMovieTimeLine;
						// Reset status flag
						currentVoiceEngineAction = false;
						adjustTimeLine = false;
						return;
					}
					// Check TimeLine both Movie's position and
					// pause TimeLine
					if (currentMovieTimeLine != pauseTimeLine) {
						pauseTimeLine = currentMovieTimeLine;
						instParentView.synchronizeAllTimeLine(pauseTimeLine);
					}
					// Reset status flag
					currentVoiceEngineAction = false;

					// Check current movie status
					if (currentMovieStatus == V_STAT_PLAY) {
						// Forced change own status(action)
						PreviewPanel.getInstance().playPauseMedia();
					}
				}
			} else {
				// TL_STAT_EXTENDED : Status is Play extended text,
				// All TimeLine suspends
				if (nowStat == TL_STAT_EXTENDED) {
					// Check status of voice engine
					if (!(instParentView.isRunningAudio())) {
						IScriptData data = localScriptManager.getDataList()
								.get(indexCurrentScriptData);
						// Restart TimeLine
						WebBrowserFactory.getInstance().playMedia();
						instParentView.currentStatusTimeLine = TL_STAT_PLAY;
						// Reset status flag
						currentVoiceEngineAction = false;
						// Update end time of current description
						updateEndTimeVolumeLabel(data);
						currentScriptDataIndex = -1;

					} else {
						// increment duration counter
						endDurationVoice = Calendar.getInstance()
								.getTimeInMillis();
					}
				} else if ((nowStat == TL_STAT_PAUSE)
						|| (nowStat == TL_STAT_IDLE)) { // Pause

					if (currentMovieStatus == V_STAT_PLAY) {
						// Forced change own status(action)
						PreviewPanel.getInstance().playPauseMedia();
					}
					// Reset status flag
					currentVoiceEngineAction = false;
				}
			}
		}

	} // End of Timer class

	/**
	 * 
	 */
	public void handleSyncTimeEvent(SyncTimeEvent e) {
		// Synchronize TimeLine view
		if (e.getEventType() == SyncTimeEvent.SYNCHRONIZE_TIME_LINE) {
			updateLocationTimeLine(e.getCurrentTime());
		} else if (e.getEventType() == SyncTimeEvent.ADJUST_TIME_LINE) {
			adjustTimeLine = true;

			int nowTime = e.getCurrentTime();
			updateLocationTimeLine(nowTime);

			compositeAudio.synchronizeTimeLine(nowTime);
			// TODO recover later
			// compositeCaption.synchronizeTimeLine(nowTime);
			canvasVolumeLevel.synchronizeTimeLine(nowTime);
		}
	}

	public void handleMouseDragEvent(MouseDragEvent e) {
		// Synchronize TimeLine view
		switch (e.getEventType()) {
		case MouseDragEvent.MOUSE_DRAG_START:
			currentDragStatus = true;
			break;
		case MouseDragEvent.MOUSE_DRAGGING:
			currentDragStatus = true;
			break;
		case MouseDragEvent.MOUSE_DRAG_END:
			currentDragStatus = false;
			break;
		case MouseDragEvent.MOUSE_SET_DRAG_STATUS:
			currentDragStatus = e.isStatus();
			break;
		}
	}

	static long previousTimerUtilEventTime = -1L;

	public void handleTimerUtilEvent(TimerEvent e) {
		long time = e.getTime();
		if ((time - previousTimerUtilEventTime) >= (TL_SYNC_MEDIA_TIME - 5)) {
			if (instTimerTimeLineManager != null) {
				instTimerTimeLineManager.timerTask();
			}
			previousTimerUtilEventTime = time;
		}
	}

}