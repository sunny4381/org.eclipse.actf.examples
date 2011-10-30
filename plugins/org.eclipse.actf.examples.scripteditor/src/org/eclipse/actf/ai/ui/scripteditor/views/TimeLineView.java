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
package org.eclipse.actf.ai.ui.scripteditor.views;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.actf.ai.internal.ui.scripteditor.EventManager;
import org.eclipse.actf.ai.internal.ui.scripteditor.PreviewPanel;
import org.eclipse.actf.ai.internal.ui.scripteditor.ScriptAudioComposite;
import org.eclipse.actf.ai.internal.ui.scripteditor.SyncTimeEvent;
import org.eclipse.actf.ai.internal.ui.scripteditor.SyncTimeEventListener;
import org.eclipse.actf.ai.internal.ui.scripteditor.TimeLineCanvas;
import org.eclipse.actf.ai.internal.ui.scripteditor.VolumeLevelCanvas;
import org.eclipse.actf.ai.internal.ui.scripteditor.XMLFileMessageBox;
import org.eclipse.actf.ai.scripteditor.data.ScriptData;
import org.eclipse.actf.ai.scripteditor.data.XMLFileSaveUtil;
import org.eclipse.actf.ai.scripteditor.util.ScriptFileDropListener;
import org.eclipse.actf.ai.scripteditor.util.SoundMixer;
import org.eclipse.actf.ai.scripteditor.util.VoicePlayerFactory;
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
		SyncTimeEventListener {
	public static final String VIEW_ID = "org.eclipse.actf.examples.scripteditor.VolumeLevelView";

	/**
	 * Local data
	 */
	protected Composite parentComposite;
	protected Composite ownComposite;
	private ScrolledComposite parentSC;
	private ScrolledComposite ownSC;
	private Composite childComposite;

	// Own instance
	static private TimeLineView ownInst = null;
	private Display ownDisplay = null;

	// Slider of TimeLine
	private Slider sliderTimeLine;

	// instance of ScriptData
	protected ScriptData instScriptData = null;

	// for Voice Manager
	private VoicePlayerFactory voicePlayer = null;
	private boolean previousPlayerStatus = false;
	private boolean previewVoiceGender;
	private int previewVoiceSpeed;
	private int previewVoicePitch;
	private String previewVoiceDesc = new String();

	// TimeLine's Timer Task
	private TimeLineManager instTimerTimeLineManager = null;
	private ScheduledExecutorService schedulerTimeLineManager = null;
	private ScheduledFuture<?> futureTimeLineManager = null;

	// Instances of sub class
	private TimeLineCanvas canvasTimeLine = null;
	private VolumeLevelCanvas canvasVolumeLevel = null;
	private ScriptAudioComposite compositeScriptAudio = null;

	// Control parameters for TimeLine
	private int startTimeLine = 0;
	private int endTimeLine = TL_DEF_ETIME;
	private int currentTimeLine = 0;
	private int currentStatusTimeLine = TL_STAT_IDLE;
	private int movieEndTimeLine = 0;
	private int currentMovieTimeLine = 0;
	private int currentMovieStatus = -1;
	private int currentTimeLineLocation = 0;
	private int previousTimeLine = -1;
	private int pauseTimeLine = 0;

	// control end time updater
	private int indexCurrentScriptData = 0;
	private boolean currentVoiceEngineAction = false;
	private int countDurationVoice = -1;

	// For menu item
	private boolean currentEnableDescription = true;
	// For manage menu
	private String saveFileName = null;

	// other Widgets
	private Label labelVolumeLevel;
	private Label labelAudio1;
	private int current_lines_timeline = TL_DEF_LINES;

	// for FormLayout of TimeLine
	private FormData ParentSCTimeLineLData;
	private FormData compositeScriptAudioLData;
	private FormData labelAudio1LData;

	private static EventManager eventManager = null;

	/**
	 * Constructor
	 */
	public TimeLineView() {
		super();

		// SetUP Quit Listener for Workbench
		PlatformUI.getWorkbench().addWorkbenchListener(
				new ScriptEditorShutdownListener());

		// store own instance
		ownInst = this;
		// store event lister
		eventManager = EventManager.getInstance();
	}

	static public TimeLineView getInstance() {
		// return own instance
		return (ownInst);
	}

	/**
	 * @Override
	 */
	public void createPartControl(Composite parent) {

		// Store instance of parent composite
		parentComposite = parent;

		// Create instance of own Scrolled Composite
		parentSC = new ScrolledComposite(parent, SWT.V_SCROLL);

		// Create Data class
		instScriptData = ScriptData.getInstance();

		// Get instance of VoicePlayerFactory
		voicePlayer = new VoicePlayerFactory();

		// Get current Display
		ownDisplay = PlatformUI.getWorkbench().getDisplay();

		// Initialize application's GUI
		initGUI(ownDisplay);

		// Start Timer for TimeLine management
		startTimeLineManager();

		// Add listener for load meta file
		initDDListener(ownComposite);

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
		ownSC.setFocus();
		childComposite.setFocus();
		ownComposite.setFocus();
		canvasTimeLine.setFocus();
		compositeScriptAudio.setFocus();
		canvasVolumeLevel.setFocus();
	}

	/**
	 * Cleans up all resources created by this ViewPart.
	 */
	public void dispose() {
		// change status to DISPOSE
		setStatusTimeLine(TL_STAT_DISPOSE);

		// stop & close all process
		reqStopScriptAudio();
		reqStopCaptureAudio();

		// dispose thread of TimeLine
		shutdownTimeLineManager();

		eventManager.removeAllSyncTimeEventListener();
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
			ParentSCTimeLineLData.height = 118 + adj_height;
			ParentSCTimeLineLData.top = new FormAttachment(5, 1000, 0);
			ParentSCTimeLineLData.left = new FormAttachment(592, 1000, 0);
			ParentSCTimeLineLData.right = new FormAttachment(0, 1000, 441);
			// ParentSCTimeLineLData.bottom = new FormAttachment(1000, 1000,
			// -188);
			parentSC.setLayoutData(ParentSCTimeLineLData);

			// Spawn own Composite
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

			// Spawn & Initialize Preview Panel
			PreviewPanel.getInstance().initPreviewPanel(parentDisp,
					ownComposite);

			// Initial SetUP for own ScrolledComposite
			parentSC.setContent(ownComposite);
			parentSC.setSize(ownComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			parentSC.setMinHeight(118 + adj_height);
			parentSC.setExpandHorizontal(true);
			parentSC.setExpandVertical(true);

			// Slider : (Time Line) Create Slider
			FormData sliderTimeLineLayoutData = new FormData();
			sliderTimeLineLayoutData.height = 16;
			sliderTimeLineLayoutData.left = new FormAttachment(0, 1000, 80);
			sliderTimeLineLayoutData.right = new FormAttachment(1000, 1000, -2);
			sliderTimeLineLayoutData.bottom = new FormAttachment(1000, 1000, -2);
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
			// Layout : "TimeLine" child Composite into ScrolledComposite
			FormLayout SCTimeLineLayout = new FormLayout();
			ownSC.setLayout(SCTimeLineLayout);
			FormData SCTimeLineLData = new FormData();
			SCTimeLineLData.width = 479;
			// Initial setup own Composite width
			SCTimeLineLData.top = new FormAttachment(PreviewPanel.getInstance()
					.getPlayButton(), 2);
			SCTimeLineLData.left = new FormAttachment(0, 1000, 80);
			SCTimeLineLData.right = new FormAttachment(1000, 1000, -2);
			SCTimeLineLData.bottom = new FormAttachment(sliderTimeLine, 0);
			ownSC.setLayoutData(SCTimeLineLData);
			// Initial setup maximum size of slider of TimeLine
			reqSetMaximumSliderTimeLine(getMaxTimeLine());

			// Spawn child Composite now
			childComposite = new Composite(ownSC, SWT.NONE);
			// Layout : "TimeLine" child Composite into ScrolledComposite
			FormLayout childTimeLineLayout = new FormLayout();
			childComposite.setLayout(childTimeLineLayout);
			FormData childTimeLineLData = new FormData();
			// childTimeLineLData.width = 479;
			// childTimeLineLData.height = 190;
			childTimeLineLData.top = new FormAttachment(0, 1000, 0);
			childTimeLineLData.left = new FormAttachment(0, 1000, 0);
			childTimeLineLData.right = new FormAttachment(1000, 1000, 0);
			childTimeLineLData.bottom = new FormAttachment(1000, 1000, 0);
			childComposite.setLayoutData(childTimeLineLData);

			// 1)Canvas : Time Line
			canvasTimeLine = TimeLineCanvas.getInstance(childComposite);
			// Layout : TimeLine Canvas into ScrolledComposite
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
			// initial draw Canvas
			reqRedrawTimeLineCanvas(1);

			// 2)Composite : Audio Script
			compositeScriptAudio = ScriptAudioComposite
					.getInstance(childComposite);
			// Layout : Audio label Composite into ScrolledComposite
			FormLayout compositeScriptAudioLayout = new FormLayout();
			compositeScriptAudio.setLayout(compositeScriptAudioLayout);
			compositeScriptAudioLData = new FormData();
			// compositeScriptAudioLData.height = 100;
			compositeScriptAudioLData.height = TL_AUDIO1_SY + adj_height;
			// compositeScriptAudioLData.top = new
			// FormAttachment(canvasVolumeLevel, 0);
			compositeScriptAudioLData.left = new FormAttachment(0, 1000, 0);
			compositeScriptAudioLData.right = new FormAttachment(1000, 1000, 0);
			compositeScriptAudioLData.bottom = new FormAttachment(1000, 1000, 0);
			// initial setup
			compositeScriptAudio.setLayoutData(compositeScriptAudioLData);
			compositeScriptAudio.setBackground(new Color(parentDisp, 255, 239,
					215));
			compositeScriptAudio.pack();

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
			canvasVolumeLevelLData.bottom = new FormAttachment(
					compositeScriptAudio, 0);
			// initial setup
			canvasVolumeLevel.setLayoutData(canvasVolumeLevelLData);
			canvasVolumeLevel.setBackground(parentDisp
					.getSystemColor(SWT.COLOR_WHITE));
			canvasVolumeLevel.pack();
			// initial draw Canvas
			reqRedrawVolumeLevelCanvas(1);
			labelVolumeLevel.setVisible(true);

			// Label : Audio 1
			int bottom = TL_AUDIO1_SY + (TL_AUDIO1_MDRAG_SY * TL_DEF_LINES);
			labelAudio1LData = new FormData();
			labelAudio1LData.width = 70;
			// labelAudio1LData.height = 12;
			// labelAudio1LData.top = new FormAttachment(canvasVolumeLevel,
			// TL_AUDIO1_SY);
			labelAudio1LData.left = new FormAttachment(0, 1000, 4);
			labelAudio1LData.bottom = new FormAttachment(1000, 1000, -bottom);
			labelAudio1 = new Label(ownComposite, SWT.NONE);
			labelAudio1.setLayoutData(labelAudio1LData);
			labelAudio1.setText("Audio 1");
			labelAudio1.setAlignment(SWT.RIGHT);

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
	 * Update layout of Time Line view for adjust height of Audio Label
	 * Composite
	 * 
	 * @param lines
	 *            : new lines of audio labels for duplicated settings
	 */
	private void adjustLayoutTimeLine(int lines) {

		// check lower limit
		int nowLine = current_lines_timeline;
		current_lines_timeline = lines;
		if (current_lines_timeline < TL_DEF_LINES) {
			current_lines_timeline = TL_DEF_LINES;
		}
		// SetUP new height of Time Line view
		int adj_height = current_lines_timeline * TL_AUDIO1_MDRAG_SY;

		// Adjust layout for parent of parent scrolled composite
		ownComposite.layout(true, true);

		// Adjust layout for parent scrolled composite
		Point nowPon = parentSC.getSize();
		ParentSCTimeLineLData.height = (nowPon.y - (nowLine * TL_AUDIO1_MDRAG_SY))
				+ adj_height;
		parentSC.setLayoutData(ParentSCTimeLineLData);
		parentSC.layout(true, true);
		parentSC.pack();
		parentSC.setSize(nowPon.x, ParentSCTimeLineLData.height);
		parentSC.setMinHeight(ParentSCTimeLineLData.height + 1);

		// Adjust layout for audio label composite
		nowPon = compositeScriptAudio.getSize();
		compositeScriptAudioLData.height = TL_AUDIO1_SY + adj_height;
		compositeScriptAudio.setLayoutData(compositeScriptAudioLData);
		compositeScriptAudio.layout(true, true);
		compositeScriptAudio.setSize(nowPon.x, adj_height);

		// Adjust layout for text label of 'Audio 1'
		labelAudio1LData.bottom = new FormAttachment(1000, 1000, -adj_height);
		labelAudio1.setLayoutData(labelAudio1LData);

		// Adjust layout for child controls(label) in audio label composite

		// Adjust layout for parent of audio label composite
		childComposite.layout(true, true);
		ownSC.layout(true, true);
	}

	/**
	 * Initialize Drop&Drop Listener
	 */
	private void initDDListener(Composite parent) {
		// Initial setup DnD target control
		DropTarget targetDnD = new DropTarget(parent, DND.DROP_DEFAULT
				| DND.DROP_COPY);
		targetDnD.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		targetDnD.addDropListener(new ScriptFileDropListener());
	}

	/**
	 * Getter method : Get StartTime of TimeLine
	 */
	public int getStartTimeLine() {
		return (startTimeLine);
	}

	/**
	 * Getter method : Get StartTime of TimeLine
	 */
	public int getEndTimeLine() {
		return (endTimeLine);
	}

	/**
	 * Getter method : Get Movie's EndTime
	 */
	public int getMovieEndTimeLine() {
		return (movieEndTimeLine);
	}

	/**
	 * Getter method : Get StartTime of TimeLine
	 */
	public int getCurrentTimeLine() {
		return (currentTimeLine);
	}

	/**
	 * Getter method : Get current max scroll bar size of ScrolledComposite
	 */
	public int getMaxScrollBar() {
		// return current max scroll bar size of ScrolledComposite
		return (sliderTimeLine.getMaximum());
	}

	/**
	 * Getter method : Get current value scroll bar of ScrolledComposite
	 */
	public int getCurrentValueScrollBar() {
		// return current max scroll bar size of ScrolledComposite
		return (sliderTimeLine.getSelection());
	}

	/**
	 * Getter method : Get current size scroll bar of ScrolledComposite
	 */
	public Point getCurrentSizeScrollBar() {
		// return current scroll bar size of ScrolledComposite
		return (sliderTimeLine.getSize());
	}

	/**
	 * Getter method : Get current location of TimeLine Composite
	 */
	public int getCurrentLocation() {
		// return current location of TimeLine Composite
		return (currentTimeLineLocation);
	}

	/**
	 * Getter method : Get current size of (parent)ScrolledComposite
	 */
	public Point getSizeParentSC() {
		Point result = new Point(0, 0);

		// Check exist instance
		if (ownSC != null) {
			result = ownSC.getSize();
		}

		// return current size of (parent)ScrolledComposite
		return (result);
	}

	/**
	 * Getter method : Get current status of enabled play description
	 * 
	 * @return
	 */
	public boolean getEnableDescription() {
		// return current status of enabled play description
		return (currentEnableDescription);
	}

	/**
	 * @category Request Get current opened XML file path
	 * @return current XML file path
	 */
	public String reqGetXMLFilePath() {
		// return result
		return (saveFileName);
	}

	/**
	 * @category Request Store current opened XML file path
	 * 
	 */
	public void reqStoreXMLFilePath(String nowFileName) {
		// Store current file path
		saveFileName = nowFileName;
	}

	/**
	 * @category Request Store current opened XML file path
	 * 
	 */
	public void reqStoreVolLvlFilePath(String nowFileName) {
		// Store current file path
		canvasVolumeLevel.setSavePathVolLvl(nowFileName);
	}

	/**
	 * Request adjust layout(view height) of Time Line view for audio label's
	 * duplicated setting
	 * 
	 * @param lines
	 *            : lines of audio labels
	 */
	public void reqAdjustLayoutTimeLine(int lines) {
		// Update layout of Time Line view
		adjustLayoutTimeLine(lines);
	}

	/**
	 * Getter method : Request get current line no. of Audio Label Composite
	 * 
	 * @return current line no. (default=3lines)
	 */
	public int reqGetCurrentLineAudioLabel() {
		// return result
		return (current_lines_timeline - 1);
	}

	/**
	 * @category Request display Confirmation dialog of unsaved script data for
	 *           Quit action event
	 * @return result process : TRUE:disable quit event, FALSE:enable quit event
	 */
	public boolean reqConfirmSaveData() {
		boolean result = true;

		// Check exist unsaved script data
		int stat = instScriptData.getStatusSaveScripts();
		if (stat > 0) {
			// Update status
			if (stat == MB_STYLE_OVERWR)
				stat = MB_STYLE_CONFIRM;

			// Check exist opened file
			String filePath = reqGetXMLFilePath();
			// Display confirmation message box
			XMLFileMessageBox confMB = new XMLFileMessageBox(stat, filePath);
			int ret = confMB.open();

			// check result
			if (ret == SWT.YES) {
				// Create save process
				XMLFileSaveUtil saveFH = new XMLFileSaveUtil();
				// Check exist file path
				String newFile = filePath;
				if (newFile == null) {
					// Select new file
					newFile = saveFH.open();
				}
				// Save current data to XML file
				saveFH.save(newFile, false);
			} else if (ret == SWT.CANCEL) {
				// Cancel quit event
				result = false;
			}
		}

		// return result
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

		/*****
		 * // check even counter if(currentTimeLineLocation > 0){ if((nowEndTime
		 * % TL_DEF_ETIME) > 0){ // increment location counter
		 * currentTimeLineLocation = currentTimeLineLocation + 1; } }
		 *****/

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
	 * Setter method : Reset Composite size of TimeLine
	 */
	public void setResizeTimeLine(int maxTime) {
		// SetUP new width of TimeLine
		int newWidth = (maxTime / TIME2PIXEL);

		// Resize width(EndTime) of TimeLine
		ownSC.setMinWidth(newWidth);
	}

	/**
	 * Setter method : Set location of slider
	 */
	public void setLocationTimeLineSlider(int movieEndTime, int currentTime) {
		// SetUP location of TimeLine's slider
		sliderTimeLine.setSelection(currentTime);
		// ///sliderTimeLine.setMaximum(movieEndTime);
		reqSetMaximumSliderTimeLine(movieEndTime);
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
	 * Setter method : Set value scroll bar of ScrolledComposite
	 */
	public void setCurrentValueScrollBar(int value) {
		// set max scroll bar size of ScrolledComposite
		sliderTimeLine.setSelection(value);
	}

	/**
	 * Setter method : Set status of enabled play description
	 * 
	 * @return
	 */
	public void setEnableDescription(boolean stat) {
		// Update status of enabled play description
		currentEnableDescription = stat;
	}

	/**
	 * @category Request get volume level file path(temporary file)
	 * @return string of volume level file path
	 */
	public URI reqGetVolLvlPath() {
		// return result
		return (canvasVolumeLevel.getSavePathVolLvl());
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
	public void reqScrollHorizontalTimeLine(int nowLocation) {
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
	public void reqSetMaximumSliderTimeLine(int endTime) {
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
	public int isEndTimeLine(int nowLocation) {
		int result = TL_NO_EVENT;

		// Get end TimeLine
		// ///int nowEndTimeLine = getMaxTimeLine();
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
		// ///if((nowEndTime >= previousEndTime)&&(previousEndTime <
		// nowEndTimeLine)){
		if (nowEndTime >= previousEndTime) {
			// Detect out of max limit
			result = TL_OVER_MAX_LIMIT;
		}
		// Check min limit
		else if ((nowStartTime < previousStartTime) && (previousStartTime > 0)) {
			// Detect out of min limit
			result = TL_OVER_MIN_LIMIT;
		}

		// return result
		return (result);
	}

	/**
	 * Setter method : Request initialize location of ScrolledComposite
	 */
	public void reqInitLocationTimeLine() {
		// SetUP TimeLine's parameters by Media info.
		startTimeLine = 0;
		currentTimeLine = 0;
		movieEndTimeLine = PreviewPanel.getInstance().getVideoTotalTime();
		// SetUP End TimeLine
		endTimeLine = movieEndTimeLine;
		int lastIndex = instScriptData.getLengthScriptList() - 1;
		if (lastIndex >= 0) {
			// PickUP last ScriptData's EndTime
			int endTime = instScriptData.getScriptEndTime(lastIndex);
			// Adjust EndTimeLine
			if (endTime > endTimeLine)
				endTimeLine = endTime;
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
	public void reqSeekLocationTimeLine() {
		// SetUP TimeLine's parameters by Media info.
		startTimeLine = 0;
		movieEndTimeLine = PreviewPanel.getInstance().getVideoTotalTime();
		// SetUP End TimeLine
		endTimeLine = movieEndTimeLine;
		int lastIndex = instScriptData.getLengthScriptList() - 1;
		if (lastIndex >= 0) {
			// PickUP last ScriptData's EndTime
			int endTime = instScriptData.getScriptEndTime(lastIndex);
			// Adjust EndTimeLine
			if (endTime > endTimeLine)
				endTimeLine = endTime;
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
			currentTimeLine = PreviewPanel.getInstance()
					.getVideoCurrentPosition();
		}

		// Initial setup index of ScriptData
		if (instTimerTimeLineManager != null) {
			instTimerTimeLineManager.seekIndexTimeLine(currentTimeLine);
		}

		// Initial synchronized each TimeLine info.
		synchronizeAllTimeLine(currentTimeLine);
	}

	/**
	 * Setter method : Request Setup TimeLine(ScriptData) & Seek Location of
	 * TimeLine during PLAY status
	 */
	public void reqSetupTimeLine() {
		// Check current TimeLine's status
		if (currentStatusTimeLine > TL_STAT_IDLE) {
			// SetUP TimeLine's parameters by Media info.
			startTimeLine = 0;
			movieEndTimeLine = PreviewPanel.getInstance().getVideoTotalTime();

			// SetUP End TimeLine
			int lastIndex = instScriptData.getLengthScriptList() - 1;
			if (lastIndex >= 0) {
				// PickUP last ScriptData's EndTime
				int endTime = instScriptData.getScriptEndTime(lastIndex);
				// Adjust EndTimeLine
				if (endTime > endTimeLine)
					endTimeLine = endTime;
			} else {
				// Check EndTime
				if (endTimeLine == 0) {
					// Reset default initial value, Cause No media & No
					// ScriptData
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
				currentTimeLine = PreviewPanel.getInstance()
						.getVideoCurrentPosition();
			}

			// Initial setup index of ScriptData
			if (instTimerTimeLineManager != null) {
				instTimerTimeLineManager.seekIndexTimeLine(currentTimeLine);
			}

			// Check current TimeLine's status again
			if (currentStatusTimeLine == TL_STAT_PLAY) {
				// Initial synchronized each TimeLine info.
				synchronizeAllTimeLine(currentTimeLine);
			}
		}
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
		setLocationTimeLineSlider((intdev * TL_DEF_SCROL_COMP_SCALE), 0);
	}

	/**
	 * Setter method : Expand max size of TimeLine view
	 */
	public boolean reqExpandTimeLine() {
		boolean result = false;
		int index = -1;

		// Check Script data length
		index = instScriptData.getLengthScriptList() - 1;
		if (index >= 0) {
			// PickUP end time of last script data
			int endTime = instScriptData.getScriptEndTime(index);
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
		// return result
		return (result);
	}

	/**
	 * Setter method : Expand max size of TimeLine view
	 */
	public void reqExpandTimeLine(int newEndTime) {
		// PickUP current End TimeLine value
		int endTime = getMaxTimeLine();

		// Check current End TimeLine
		if (newEndTime > endTime) {
			// Expand TimeLine's Composite width to new End Time
			setMaxTimeLine(newEndTime);
			// Resize slider of TimeLine
			reqSetMaximumSliderTimeLine(getMaxTimeLine());

			/*****************
			 * // Request repaint mode=1(initial refresh)
			 * canvasTimeLine.setStatusCanvasTimeLine(1); // Repaint all
			 * AudioLabel in Composite of AudioLabel refreshScriptAudio();
			 *******************/

			// Synchronize Preview view
			PreviewPanel.getInstance()
					.synchronizeTimeLine(getCurrentTimeLine());
		}
	}

	/**
	 * Setter method : Request update location of ScrolledComposite
	 */
	public void reqUpdateLocationTimeLine(int index) {
		// Get target Start Time data by parameter(index)
		int nextStartTime = instScriptData.getScriptStartTime(index);

		// PickUP current size of scroll bar
		Point scPos = getCurrentSizeScrollBar();

		// Get start time by nextValue
		int sliderStartTime = (nextStartTime / TIME2PIXEL) - (scPos.x >> 1);
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
		int nextValue = (nextStartTime / TIME2PIXEL) - (scPos.x >> 1);
		if (nextValue < 0)
			nextValue = 0;
		// Update location of scroll bar
		setCurrentValueScrollBar(nextValue);

		// adjust next time line to offset time(times 10mins)
		int targetTime = nextStartTime
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

		// Update location of current Composite
		childComposite.setLocation(nextCentre, nowPon.y);
	}

	/**
	 * Setter method : Request increment location of ScrolledComposite
	 */
	public void updateLocationTimeLine(int nextTime) {
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
		setCurrentValueScrollBar(nextValue);

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
		setCurrentValueScrollBar(nextValue);
		// Update location of TimeLine Composite
		childComposite.setLocation(nextCentre, nowPon.y);
	}

	/**
	 * Setter method :
	 */
	public void repaintAllTimeLine() {
		// PickUP current location of TimeLine
		int nowCnt = getCurrentLocation();

		eventManager.fireSyncTimeEvent(new SyncTimeEvent(
				SyncTimeEvent.REFRESH_TIME_LINE, nowCnt));
	}

	/**
	 * @category Setter method
	 * @purpose Synchronize TimeLine manager
	 * 
	 */
	public void synchronizeAllTimeLine(int nowTime) {
		eventManager.fireSyncTimeEvent(new SyncTimeEvent(nowTime, this));
	}

	/**
	 * Local method : Synchronize TimeLine for TimeLine view
	 */
	private void synchronizeTimeLine(int nowTime) {
		// Synchronize TimeLine's components
		updateLocationTimeLine(nowTime);
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
	 * Setter method : Request CleanUP captured data of movie audio
	 */
	public void reqCleanupCaptureData() {
		// Call Setter method
		canvasVolumeLevel.cleanupMovieAudioLevel();
	}

	/**
	 * @category Setter method : Load value of volume level from temporary file
	 */
	public void reqLoadVolumeLevelData() {
		// Load value volume level from temporary file to buffer
		canvasVolumeLevel.loadVolumeLevelTempFile();
	}

	/**
	 * Request refresh ScriptList to EditPanelView
	 */
	public void reqRefreshScriptData(int currentStartTime, int newStartTime,
			int newEndTime, int newEndTimeWav, boolean dspMode) {
		// Request refresh ScriptData in EditPanel view
		EditPanelView
				.getInstance()
				.getInstanceTabEditPanel()
				.refreshScriptData(currentStartTime, newStartTime, newEndTime,
						dspMode);

		// Request refresh WAV file information in EditPanel view
		EditPanelView
				.getInstance()
				.getInstanceTabSelWAVFile()
				.refreshScriptData(currentStartTime, newStartTime,
						newEndTimeWav, dspMode);
	}

	/**
	 * Setter method : Play audio(voice or WAV)
	 */
	public void reqPlayAudio(int indexScriptData) {
		// Get audio status(voice or WAV file)
		int startTime = instScriptData.getScriptStartTime(indexScriptData);
		int wavno = instScriptData.getIndexWavList(startTime);
		boolean enaWav = instScriptData.getEnableWavList(wavno);

		// Case : play voice
		if (!enaWav || (wavno < 0)) {
			// check extended parameter
			if (instScriptData.getExtendExtended(indexScriptData)) {
				// Change extended play mode
				PreviewPanel.getInstance().controlExtendedPlay(true);
				setStatusTimeLine(TL_STAT_EXTENDED);
				// SetUP status
				// voicePlayer.setPlayVoiceStatus(1);
			}
			// SetUP status
			voicePlayer.setPlayVoiceStatus(1);
			// Play Voice, Now!
			reqStartVoicePlayer(indexScriptData);
		}
		// Case : play WAV file
		else {
			// check extended parameter
			if (instScriptData.getExtendExtended(indexScriptData)) {
				// Change extended play mode
				PreviewPanel.getInstance().controlExtendedPlay(true);
				setStatusTimeLine(TL_STAT_EXTENDED);
			}
			// Play WAV, Now!
			URI wavFName = instScriptData.getFileNameWavList(wavno);
			float wavCompetitiveRatio = instScriptData
					.getPlaySpeedWavList(wavno);
			SoundMixer.getInstance().startPlaySound(wavFName,
					wavCompetitiveRatio);
		}
	}

	/**
	 * Getter method : Check current audio status
	 */
	public boolean isRunningAudio() {
		// Check status of voice engine and play WAV file
		boolean result = isSamplingScriptAudio()
				| SoundMixer.getInstance().isRunningPlaySound();
		// return result
		return (result);
	}

	/**
	 * Checker method : Check current Sampling Timer Task status
	 */
	public Boolean isSamplingScriptAudio() {
		// Return status of current ProTalker engine
		return (voicePlayer.getPlayVoiceStatus());
	}

	/**
	 * Setter method : Request Play voice(Script Audio) by ProTalker
	 */
	public void reqSetupScriptAudio(String strGender, int speed, int pitch,
			int volume) {
		// SetUP extended parameters
		voicePlayer.setGender(strGender);
		voicePlayer.setSpeed(speed);
		voicePlayer.setPitch(pitch);
		voicePlayer.setVolume(volume);
	}

	/**
	 * Getter method : Check consistency Script data
	 */
	public boolean isConsistencyScriptData() {
		boolean result = false;

		// Check consistency data that store data and current widget value
		if (previewVoiceDesc.equals(EditPanelView.getInstance()
				.getInstanceTabEditPanel().getCurrentDescription())
				&& (previewVoiceGender == EditPanelView.getInstance()
						.getInstanceTabEditPanel().getCurrentGender())
				&& (previewVoiceSpeed == EditPanelView.getInstance()
						.getInstanceTabEditPanel().getCurrentSpeed())
				&& (previewVoicePitch == EditPanelView.getInstance()
						.getInstanceTabEditPanel().getCurrentPitch())) {

			// detect data matching
			result = true;
		}

		// return result
		return (result);
	}

	/**
	 * Setter method : Request Play voice(Script Audio) by ProTalker
	 */
	public void reqPlayScriptAudio(String currentScriptText) {
		// SetUP status
		voicePlayer.setPlayVoiceStatus(1);

		// Store current setting parameter of VoicePlay
		previewVoiceGender = EditPanelView.getInstance()
				.getInstanceTabEditPanel().getCurrentGender();
		previewVoiceSpeed = EditPanelView.getInstance()
				.getInstanceTabEditPanel().getCurrentSpeed();
		previewVoicePitch = EditPanelView.getInstance()
				.getInstanceTabEditPanel().getCurrentPitch();
		previewVoiceDesc = currentScriptText;

		// ** Comment Out for VoiceUtl
		// *******************************************
		/**/
		// Start timer for sampling Volume Level
		canvasVolumeLevel.startSamplingVolumeLevel();
		/**/
		// Dummy *************************
		// Repaint EndTime
		// // EditPanelView.getInstance().repaintTextEndTime();
		// ** Comment Out for VoiceUtl
		// *******************************************

		// Play voice
		voicePlayer.speak(currentScriptText);
	}

	/**
	 * Setter method : Request Stop & Dispose preview voice process
	 */
	public void reqStopScriptAudio() {
		// Stop ProTalker
		voicePlayer.stop();
		// Destroy current Thread
		canvasVolumeLevel.shutdownSamplingVolumeLevel();
	}

	/**
	 * Setter method : Request Stop & Dispose capture audio of movie
	 */
	public void reqStopCaptureAudio() {
		// Stop & Dispose SoundMixer
		SoundMixer.getInstance().stopCaptureSound();
		SoundMixer.getInstance().stopPlaySound();
		SoundMixer.getInstance().dispose();
		// Stop & Dispose TimeLine Thread
		canvasVolumeLevel.shutdownTimerCaptureAudio();
	}

	/**
	 * Request Start VoicePlayer
	 */
	public void reqStartVoicePlayer(String nowScriptText) {
		// Start ProTalker
		voicePlayer.speak(nowScriptText);
	}

	public void reqStartVoicePlayer(int index) {
		// SetUP current Script
		reqSetupScriptAudio((instScriptData.getExtendGender(index) ? "male"
				: "female"), instScriptData.getExtendSpeed(index),
				instScriptData.getExtendPitch(index),
				instScriptData.getExtendVolume(index));

		// Start ProTalker
		voicePlayer.speak(instScriptData.getScriptData(index));
	}

	/**
	 * Request Resume VoicePlayer
	 */
	public void reqResumeVoicePlayer() {
		// Resume ProTalker
		voicePlayer.resume();
	}

	/**
	 * Request Stop VoicePlayer
	 */
	public void reqStopVoicePlayer() {
		// Stop ProTalker
		voicePlayer.stop();
	}

	/**
	 * Request Pause VoicePlayer
	 */
	public void reqPauseVoicePlayer() {
		// Resume ProTalker
		voicePlayer.pause();
	}

	/**
	 * Setter method : Request refresh edit data
	 */
	public void reqSelectScriptData(int startTime) {
		// Search target index
		int index = instScriptData.searchScriptData(startTime);

		// Re-paint text of selected Script Data
		if (index >= 0) {
			// Get string of target description
			String strDesc = instScriptData.getScriptData(index);
			// Reset screen
			EditPanelView.getInstance().getInstanceTabEditPanel()
					.repaintTextScriptData(index);
			EditPanelView.getInstance().getInstanceTabSelWAVFile()
					.startDescriptionStruct(startTime, strDesc);
			// Set Focus on description's text area
			EditPanelView.getInstance().getInstanceTabEditPanel()
					.setFocusDescriptionTextArea();
		}
	}

	/**
	 * Setter method : Request refresh edit data
	 */
	public void reqDeleteScriptData(int startTime) {
		// Re-paint text of selected Script Data
		compositeScriptAudio.deleteAudioLabel(startTime);
		// Redraw all labels for exchange color
		compositeScriptAudio.redrawAudioLabelAll();
	}

	/**
	 * Update End Time of sampling data for Time Line
	 */
	public int setEndTimeVolumeLevel(String currentScript, int currentSpeed,
			int currentLang) {

		// get current Script data length (as Preview action)
		int lengthSample = VolumeLevelCanvas.getInstance()
				.getSamplingLengthVolumeLevel();
		int nowDuration;

		// Exist sampling data(by Preview action)
		if (isConsistencyScriptData() && (lengthSample > 0)) {
			// MakeUP duration time
			nowDuration = lengthSample * TL_DEF_SCALE;
		}
		// Calculate end time(duration time) of current description
		else {
			// Calculate current pitch(speed)
			nowDuration = sumMoraCount(currentScript, currentSpeed, currentLang);
		}

		// update End time Text field
		int newEndTime = EditPanelView.getInstance().getInstanceTabEditPanel()
				.setEndTimeScriptData(nowDuration);

		// return new end time
		return (newEndTime);

	}

	/**
	 * @category Getter method : Get counter of duration time of voice engine
	 * @return counter of duration time
	 */
	public int getCountDurationVoice() {
		// return result
		return (countDurationVoice);
	}

	/**
	 * @category Setter method : Update counter of duration time of voice engine
	 * @param newCount
	 *            : new counter value
	 */
	public void setCountDurationVoice(int newCount) {
		// Update new counter
		countDurationVoice = newCount;
	}

	/**
	 * @category Setter method : Increment counter of duration time of voice
	 *           engine
	 */
	public void incCountDurationVoice() {
		// Increment current counter
		countDurationVoice++;
	}

	/**
	 * Update End Time of sampling data for Time Line
	 */
	public void updateEndTimeVolumeLevel(int index) {

		// get current Script data length (during Play media)
		int lengthSample = getCountDurationVoice();

		// Exist sampling data(during Play media)
		if (lengthSample > 0) {
			// Get current start time
			int startTime = instScriptData.getScriptStartTime(index);
			// MakeUP duration time
			int nowDuration = lengthSample * TL_SYNC_MEDIA_TIME;
			// MakeUP new end time
			int newEndTime = startTime + nowDuration;

			// Merge end time to current script data
			instScriptData.updateScriptEndTime(startTime, newEndTime);
			// Repaint audio label by end time of WAV data
			EditPanelView.getInstance().getInstanceTabEditPanel()
					.reqUpdateEndTimeAudioLabel(index, newEndTime);
		}
	}

	/**
	 * @category Calculate MORA counter for description from CSV file
	 * @param strDesc
	 *            : string of description
	 * @return MORA counter
	 */
	public int sumMoraCount(String strDesc, int speed, int lang) {
		int duration = 0;

		// Calculate current pitch(speed)
		int nowPitch = (speed >= 50) ? (VE_TIME_MORA_EN - (speed - 50))
				: (VE_TIME_MORA_EN + (4 * (50 - speed)));

		// Count character
		if (lang == DESC_LANG_JA) {
			// Japanese
			duration = (int) ((float) nowPitch * voicePlayer
					.sumMoraCountJp(strDesc));
		} else {
			// English
			duration = (int) ((float) nowPitch * (int) voicePlayer
					.sumMoraCountEn(strDesc));
		}

		return (duration);
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
	public void setMaxTimeLine(int newEndTime) {
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
	 * Put on new Audio Label
	 */
	public void putScriptAudio(int index, int startTime, int endTime,
			String strAudio, boolean extended) {
		// Request put target data(Audio info.)
		compositeScriptAudio.putAudioLabel(index, startTime, endTime, strAudio,
				extended);

		// Check & Adjust TimeLine

	}

	/**
	 * Refresh all Script Audio Label from current ScriptList
	 */
	public void refreshScriptAudio() {

		// Reset script audio label on new time scale
		compositeScriptAudio.refreshScriptAudio();
	}

	public void repaintTimeLine() {
		// PickUP current video info.
		// startTimeLine = TL_DEF_STIME;
		// currentTimeLine = TL_DEF_STIME;
		movieEndTimeLine = PreviewPanel.getInstance().getVideoTotalTime();
		// SetUP End TimeLine
		endTimeLine = movieEndTimeLine;
		int lastIndex = instScriptData.getLengthScriptList() - 1;
		if (lastIndex >= 0) {
			// PickUP last ScriptData's EndTime
			int endTime = instScriptData.getScriptEndTime(lastIndex);
			// Adjust EndTimeLine
			if (endTime > endTimeLine)
				endTimeLine = endTime;
		} else {
			// Check EndTime
			if (endTimeLine == 0) {
				// SetUP default value
				endTimeLine = TL_DEF_ETIME;
			}
		}

		// Adjust end TimeLine by window scale size(5mins)
		int unitCount = endTimeLine / TL_DEF_SCROL_COMP_SCALE;
		if ((endTimeLine % TL_DEF_SCROL_COMP_SCALE) > 0)
			unitCount = unitCount + 1;
		endTimeLine = unitCount * TL_DEF_SCROL_COMP_SCALE;

		// Request clear info. of Canvas (reset clipping area)
		// VolumeLevelCanvas.getInstance().updateInfoTimeLineVolumeLevel(currentTimeLine);

	}

	/**
	 * Setter method : Adjust TimeLine for Audio Label
	 */
	public int adjustEndTimeLine() {
		int result = TL_NO_EVENT;

		// Check max limit TimeLine
		int nowLocation = getCurrentValueScrollBar();
		if (TL_NO_EVENT != isEndTimeLine(nowLocation)) {
			// Update scroll counter of TimeLine
			setCurrentLocation(nowLocation);
			// check expand time line
			repaintAllTimeLine();
			// horizontal scroll window
			reqScrollHorizontalTimeLine(nowLocation);
		}

		// return result
		return (result);
	}

	/**
	 * Local Class extends SliderSelectionAdapter for TimeLine
	 */
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
		// Override paintControl()
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

	// ********************************************************************
	// * Management TimeLine
	// *
	// ********************************************************************
	/**
	 * Switching action for TimeLine
	 */
	public void switchActionTimeLine(boolean sw) {
		// Play movie & Play Voice automatically
		if (sw) {
			// Seek location of TimeLine view
			reqSeekLocationTimeLine();
			// Resume VoicePlayer immediately
			if (previousPlayerStatus) {
				// Resume Play Voice, immediately
				// ///reqResumeVoicePlayer();
				// Update status
				currentStatusTimeLine = TL_STAT_PLAY;
				previousPlayerStatus = false;
			}
			// Start VoicePlayer normally
			else {
				currentStatusTimeLine = TL_STAT_PLAY;
			}
		}
		// Pause movie & Pause Voice immediately
		else {
			// PickUP current TimeLine to flag
			pauseTimeLine = currentTimeLine;
			// Stop VoicePlayer
			currentStatusTimeLine = TL_STAT_PAUSE;
			previousPlayerStatus = true;

			// Finish high-light for target index of ScriptList
			ScriptListView.getInstance().getInstScriptList()
					.clearHighLightScriptLine();
		}
	}

	/**
	 * Setter method : Rewind TimeLine
	 */
	public void rewindActionTimeLine() {
		// Rewind all parameters
		previousPlayerStatus = false;
		initParamTimeLine(0);
		// Seek location of TimeLine view
		reqInitLocationTimeLine();
	}

	/**
	 * Setter method : Rewind TimeLine
	 */
	public void resetTimeLine() {
		// Rewind all parameters
		previousPlayerStatus = false;
		initParamTimeLine(0);
		// Initial synchronized each TimeLine info.
		synchronizeAllTimeLine(currentTimeLine);
	}

	/*******************************************************************************
	 * Synchronize Movie position part
	 * 
	 *******************************************************************************/
	/**
	 * Background method : PickUP current Movie's status & position
	 */
	private void getCurrentMovieInfo() {
		// PickUP current video player status
		currentMovieStatus = PreviewPanel.getInstance().getVideoStatus();
		// PickUP current video position
		currentMovieTimeLine = PreviewPanel.getInstance()
				.getVideoCurrentPosition();
	}


	/*******************************************************************************
	 * Management TimeLine part
	 * 
	 *******************************************************************************/
	/**
	 * @category Start Timer for TimeLineManager
	 * 
	 */
	public Boolean startTimeLineManager() {
		// result own process
		Boolean result = true;

		// unable to duplicated spawn
		if (futureTimeLineManager == null) {
			// Initial setup Timer Task for sampling volume level data
			instTimerTimeLineManager = new TimeLineManager(this, instScriptData);
			schedulerTimeLineManager = Executors
					.newSingleThreadScheduledExecutor();
			// Start Timer Task
			futureTimeLineManager = schedulerTimeLineManager
					.scheduleAtFixedRate(instTimerTimeLineManager, 0,
							TL_SYNC_MEDIA_TIME, TimeUnit.MILLISECONDS);
		} else {
			// already spawn Thread
			result = false;
		}

		// return current status
		return (result);
	}

	public void shutdownTimeLineManager() {
		// check current instance
		if (futureTimeLineManager != null) {
			// Destroy Timer Task & Scheduler
			futureTimeLineManager.cancel(true);
			schedulerTimeLineManager.shutdownNow();
			// Request Garbage Collection
			futureTimeLineManager = null;
			instTimerTimeLineManager = null;
		}
	}

	/**
	 * Getter method : Check current TimeLine's status
	 */
	public int getStatusTimeLine() {
		// return current status of TimeLine
		return (currentStatusTimeLine);
	}

	/**
	 * Setter method : Check current TimeLine's status
	 */
	public void setStatusTimeLine(int nextStatus) {
		// update status of TimeLine
		currentStatusTimeLine = nextStatus;
	}

	/**
	 * Local method : initialize TimeLine's parameters
	 */
	private void initParamTimeLine(int nextStatus) {
		startTimeLine = TL_DEF_STIME;
		currentTimeLine = TL_DEF_STIME;
		previousTimeLine = -1;
		instTimerTimeLineManager.initIndexTimeLine(nextStatus);
	}

	/**
	 * @category TimeLine Manager
	 * 
	 */
	class TimeLineManager implements Runnable {
		// Instance of each class
		private TimeLineView instParentView = null;
		private ScriptData instScriptData = null;

		// Index pointer of ScriptList
		private int indexScriptData = 0;

		// Local parameters
		private int ownCurrentTimeLine = 0;
		private int ownPreviousTimeLine = 0;

		/**
		 * Constructor
		 */
		public TimeLineManager(TimeLineView instParentView,
				ScriptData instScriptData) {
			// Store instance of parent View
			this.instParentView = instParentView;
			// Store instance of ScriptData
			this.instScriptData = instScriptData;

			// initial parameters
			initIndexTimeLine(TL_STAT_IDLE);
		}

		/**
		 * Setter method : Initialize parameters
		 */
		public void initIndexTimeLine(int nextStatus) {
			// initial index of next script data
			indexScriptData = 0;
			ownCurrentTimeLine = currentTimeLine;
			ownPreviousTimeLine = previousTimeLine;
			if (instScriptData.getLengthScriptList() <= 0) {
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
			int len = instScriptData.getLengthScriptList();
			int previousTime = 0;

			// Exist data in ScriptList
			if (len > 0) {
				// Search index from ScriiptList
				for (index = 0; index < len; index++) {
					// PickUP current ScriptData & Check Time
					int startTime = instScriptData.getScriptStartTime(index);

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
				if (index >= instScriptData.getLengthScriptList()) {
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

			// return target index of ScriptList
			return (index);
		}

		/**
		 * Local method : Increment index of target ScriptData
		 */
		private void incrementIndexTimeLine() {
			// Increment index of target ScriptData
			indexScriptData++;

			// Limit check for index
			if (indexScriptData >= instScriptData.getLengthScriptList()) {
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

			// Check Limit of index
			if (indexScriptData > TL_EOL) {
				// PickUP StartTime as current index of ScriptList
				int startTime = instScriptData
						.getScriptStartTime(indexScriptData);
				int endTime = instScriptData.getScriptEndTime(indexScriptData);

				// search next index of description, cause past time line
				if (endTime <= ownCurrentTimeLine) {
					while (endTime <= ownCurrentTimeLine) {
						// Past TimeLine, then search next script data('s index)
						incrementIndexTimeLine();
						// Check End of List
						if (indexScriptData == TL_EOL)
							return (result);

						// PickUP StartTime as current index of ScriptList
						startTime = instScriptData
								.getScriptStartTime(indexScriptData);
						endTime = instScriptData
								.getScriptEndTime(indexScriptData);
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
		 * Setter method : Synchronize current TimeLine
		 */
		private boolean synchronizeCurrentTimeLine() {

			// PickUP current movie's
			getCurrentMovieInfo();

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

		/**
		 * @category Run method of Timer Task
		 */
		public void run() {
			try {
				// Management TimeLine
				ownDisplay.asyncExec(new Runnable() {
					public void run() {
						if (PreviewPanel.getInstance().isCurrentDragStatus() == true) {
							// It returns in dragging mode ,
							// because moveMouseDraggedEvent() of PreviewPanel
							// calls synchronizeAllTimeLine() method.
							return;
						}
						// Get current own status
						int nowStat = instParentView.getStatusTimeLine();
						if (synchronizeCurrentTimeLine() == false
								&& nowStat != TL_STAT_EXTENDED) {
							// Return run() method when currentTimeLine is no
							// changed and video status is not extend mode.
							return;
						}

						// 1)Check current position of media(movie)
						boolean result = checkCurrentTimeLine();
						// 2)control preview movie
						if (result) {
							// TL_STAT_PLAY : Status is Playing movie
							// (automatic)
							if (nowStat == TL_STAT_PLAY) {
								// Check next timing for play Voice
								boolean result2 = watchdocIndexTimeLine();
								if (result2) {
									// check status of enabled play description
									// flag
									if (instParentView.getEnableDescription()) {
										// Check current Player status
										if (instParentView
												.isSamplingScriptAudio()) {
											// Forced Stop VoicePlayer
											instParentView.reqStopScriptAudio();
										}

										// Start high-light for target index of
										// ScriptList
										ScriptListView
												.getInstance()
												.getInstScriptList()
												.updateHighLightScriptLine(
														indexScriptData);

										// Play Voice, Now!
										instParentView
												.reqPlayAudio(indexScriptData);
										// Set status flag
										indexCurrentScriptData = indexScriptData;
										setCountDurationVoice(0);
										currentVoiceEngineAction = true;
									}

									// Update index to next ScriptData
									incrementIndexTimeLine();
								}
								// For end time update
								else {
									// check status of enabled play description
									// flag
									if (currentVoiceEngineAction
											&& instParentView
													.getEnableDescription()) {
										// Check status of voice engine
										if (!(instParentView.isRunningAudio())) {
											// Reset status flag
											currentVoiceEngineAction = false;
											// Update end time of current
											// description
											updateEndTimeVolumeLevel(indexCurrentScriptData);
											// Finish high-light for target
											// index of ScriptList
											ScriptListView.getInstance()
													.getInstScriptList()
													.clearHighLightScriptLine();
										} else {
											// increment duration counter
											incCountDurationVoice();
										}
									}
								}

								// Synchronize TimeLine each views
								instParentView
										.synchronizeAllTimeLine(ownCurrentTimeLine);

								// Check current movie status
								if (currentMovieStatus != V_STAT_PLAY) {
									// Forced change own status(action)
									PreviewPanel.getInstance().playPauseMedia();
								}
							}
							// TL_STAT_PAUSE : Status is Pause action
							else if ((nowStat == TL_STAT_PAUSE)
									|| (nowStat == TL_STAT_IDLE)) {
								// Check TimeLine both Movie's position and
								// pause TimeLine
								if (currentMovieTimeLine != pauseTimeLine) {
									// Store current movie position as pause
									// time
									pauseTimeLine = currentMovieTimeLine;
									// Synchronize TimeLine each views
									instParentView
											.synchronizeAllTimeLine(pauseTimeLine);
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
									// Restart TimeLine
									PreviewPanel.getInstance()
											.controlExtendedPlay(false);
									instParentView
											.setStatusTimeLine(TL_STAT_PLAY);
									// Reset status flag
									currentVoiceEngineAction = false;
									// Update end time of current description
									updateEndTimeVolumeLevel(indexCurrentScriptData);
									// Finish high-light for target index of
									// ScriptList
									ScriptListView.getInstance()
											.getInstScriptList()
											.clearHighLightScriptLine();
								} else {
									// increment duration counter
									incCountDurationVoice();
								}
							}
							// TL_STAT_PAUSE : Status is Pause action
							else if ((nowStat == TL_STAT_PAUSE)
									|| (nowStat == TL_STAT_IDLE)) {
								// Check current movie status
								if (currentMovieStatus == V_STAT_PLAY) {
									// Forced change own status(action)
									PreviewPanel.getInstance().playPauseMedia();
								}
								// Reset status flag
								currentVoiceEngineAction = false;
							}
						}
					}
				});
			} catch (Exception e) {
				System.out.println("TimerTask::run() : Exception = " + e);
			}
		}

	} // End of Timer class

	public void handleSyncTimeEvent(SyncTimeEvent e) {
		// Synchronize TimeLine view
		if (e.getEventType() == SyncTimeEvent.SYNCHRONIZE_TIME_LINE) {
			synchronizeTimeLine(e.getCurrentTime());
		} else if (e.getEventType() == SyncTimeEvent.REFRESH_TIME_LINE) {

		}
	}
}
