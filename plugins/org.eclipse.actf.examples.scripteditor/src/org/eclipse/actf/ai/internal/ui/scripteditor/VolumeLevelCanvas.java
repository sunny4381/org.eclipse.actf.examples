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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.ArrayList;

import org.eclipse.actf.ai.internal.ui.scripteditor.event.EventManager;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.SyncTimeEvent;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.SyncTimeEventListener;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.TimerEvent;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.TimerEventListener;
import org.eclipse.actf.ai.scripteditor.preferences.CapturePreferenceUtil;
import org.eclipse.actf.ai.scripteditor.util.SoundMixer;
import org.eclipse.actf.ai.scripteditor.util.TempFileUtil;
import org.eclipse.actf.ai.scripteditor.util.VoicePlayerFactory;
import org.eclipse.actf.ai.ui.scripteditor.views.EditPanelView;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.actf.examples.scripteditor.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class VolumeLevelCanvas extends Canvas implements IUNIT,
		SyncTimeEventListener, TimerEventListener {

	// Sampling : 20msec
	private static final int SAMPLING_RATE = 20;
	private static URI savePathVolLvl = null;

	static private VolumeLevelCanvas ownInst = null;

	// Process Status flag
	// 0:Idle
	// 1:Clear Screen
	// 2:Draw current data(voice level)
	// 3:Re-Draw off-image on Screen
	private int currentProcStatus = 0;

	private Image offImage = null;

	private int borderLinePosX = 0;

	// sampling audio level data
	private ArrayList<Integer> sampleVolumeLevel;
	private ArrayList<Integer> startTimeCaptureAudio;
	private ArrayList<Integer> volumeLevelCaptureAudio;
	private int previewVoiceTotalTime = 0;
	private float currentVolLvlGain = 1.0f;

	// capture mode flag
	// TRUE : capture mode / FALSE : normal mode
	private boolean currentCaptureMode = true;
	// Sampling Timer Task
	private boolean samplingFlag = false; // sampling mode

	private int currentTimeLineLocation = 0;

	private int ownFreeRunTimeCount = 0;
	private int ownFreeRunLimitCount = 0;
	private int previousParentTimeLine = 0;

	// Parameters for draw captured data
	private int previousDrawTime = 0;
	private int previousDrawSize = 0;
	private Point previousCanvasSize;

	private TimeLineView instParentView;
	private EventManager eventManager = null;

	private VolumeLevelCanvas(Composite parent) {
		super(parent, SWT.BORDER);

		ownInst = this;
		eventManager = EventManager.getInstance();

		initializeCanvas(parent);

		instParentView = TimeLineView.getInstance();
		setCurrentVolLvlGain();

		// setup event listeners
		eventManager.addSyncTimeEventListener(ownInst);
		eventManager.addTimerEventListener(ownInst);
		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				eventManager.removeSyncTimeEventListener(ownInst);
				eventManager.removeTimerEventListener(ownInst);
			}
		});

	}

	static public VolumeLevelCanvas getInstance(Composite parent) {
		if (ownInst == null) {
			synchronized (VolumeLevelCanvas.class) {
				// 2nd check current instance
				if (ownInst == null) {
					ownInst = new VolumeLevelCanvas(parent);
				}
			}
		}
		return (ownInst);
	}

	static public VolumeLevelCanvas getInstance() {
		return (ownInst);
	}

	private void initializeCanvas(Composite parent) {
		try {
			// next status : Idle mode
			currentProcStatus = 0;

			// Initial location = (0, 0)
			setLocationBorderTimeLine(0);

			// Add PaintListener
			addPaintListener(new CanvasPaintListener());

			// Create ArrayList for sampling level data
			// (default rate = 20msec)
			sampleVolumeLevel = new ArrayList<Integer>();
			startTimeCaptureAudio = new ArrayList<Integer>();
			volumeLevelCaptureAudio = new ArrayList<Integer>();

			// Store current Canvas size
			previousCanvasSize = this.getSize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 */
	public void synchronizeTimeLine(int nowTime) {
		// Calculate current x-point
		int x = (nowTime - (currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE))
				/ TIME2PIXEL;
		// Update location
		setLocationBorderTimeLine(x);
	}

	/**
	 */
	private void refreshTimeLine(int nowCnt) {
		// Update counter of location of TimeLine
		currentTimeLineLocation = nowCnt;
		setStatusCanvasVolumeLevel(2);
	}

	/**
	 */
	public void setStatusCanvasVolumeLevel(int nextStatus) {
		currentProcStatus = nextStatus;
		redraw();
	}

	private void setLocationBorderTimeLine(int x) {
		// Update current time(position)
		borderLinePosX = x - 1;
		if (borderLinePosX < 0)
			borderLinePosX = 0;
		redraw();
	}

	/**
	 */
	public void clearSamplingLengthVolumeLevel() {
		previewVoiceTotalTime = 0;
	}

	/**
	 * 
	 * @return true if capture enabled
	 */
	public boolean isCaptureEnabled() {
		return (currentCaptureMode);
	}

	/**
	 * Enable/disable capture
	 */
	public void setCaptureEnabled(boolean stat) {
		currentCaptureMode = stat;
	}

	/**
	 * Set Gain of volume level based on settings in preference
	 */
	public void setCurrentVolLvlGain() {
		// Calculate new Gain value
		currentVolLvlGain = (float) CapturePreferenceUtil
				.getPreferenceVolLvlGain() / 100.0f;
	}

	/**
	 * clear audio level
	 */
	public void cleanupMovieAudioLevel() {
		startTimeCaptureAudio.clear();
		volumeLevelCaptureAudio.clear();
	}

	private int getSamplingLengthMovieAudioLevel() {
		int result = -1;
		if (!startTimeCaptureAudio.isEmpty()) {
			result = startTimeCaptureAudio.size();
		}
		return (result);
	}

	/**
	 * Local method : Initialize Canvas Status : 1
	 */
	private void initCanvasVolumeLevel(PaintEvent e) {
		// Max size of end TimeLine
		int endTimeLine = TL_DEF_ETIME;

		// Get current canvas size
		Point nowCanvas = this.getSize();
		// Exchange from Time data to Scale data(pixel)
		Point newCanvas = new Point(endTimeLine / TIME2PIXEL, nowCanvas.y);
		// Resize scaler
		if (e.width > newCanvas.x) {
			newCanvas = null;
			newCanvas = this.getSize();
		}

		// Dispose current off image
		if (offImage != null) {
			offImage.dispose();
			offImage = null;
		}
		// Initialize off image
		offImage = new Image(getDisplay(), newCanvas.x, newCanvas.y);
		// Create work Graphics Context
		GC wgc = new GC(offImage);

		// Resize GC
		Rectangle rectWGC = new Rectangle(0, 0, newCanvas.x, newCanvas.y);
		// Clear current Canvas
		wgc.setBackground(e.display.getSystemColor(SWT.COLOR_WHITE));
		wgc.fillRectangle(rectWGC);
		wgc.setForeground(e.display.getSystemColor(SWT.COLOR_BLUE));
		wgc.drawLine(0, newCanvas.y >> 1, newCanvas.x, newCanvas.y >> 1);

		// Initial draw TimeLine
		e.gc.drawImage(offImage, e.x, e.y, e.width, e.height, e.x, e.y,
				e.width, e.height);

		// CleanUP work GC
		wgc.dispose();

		// Adjust position of Label
		Point ownSize = ((Canvas) e.getSource()).getSize();
		instParentView.setPositionLabelVolumeLevel(ownSize.y >> 1);

		// Update Status : 4 (Redraw current off image mode)
		currentProcStatus = 4;
	}

	/**
	 * Local method : Draw Volume Level of Media's audio
	 */
	private void drawMediaAudioLevel(PaintEvent e) {
		// Max size of end TimeLine
		int endTimeLine = TL_DEF_ETIME;

		// Get current canvas size
		Point nowCanvas = this.getSize();
		// Exchange from Time data to Scale data(pixel)
		Point newCanvas = new Point(endTimeLine / TIME2PIXEL, nowCanvas.y);
		// Resize scaler
		if (e.width > newCanvas.x) {
			newCanvas = null;
			newCanvas = this.getSize();
		}
		// Get current Canvas size
		int ht = newCanvas.y;
		int cHt = ht >> 1;
		// int adjHt = (32767 / (cHt + 1)) + 1;
		int adjHt = 378;

		// Dispose current off image
		if (offImage != null) {
			offImage.dispose();
			offImage = null;
		}
		// Initialize off image
		offImage = new Image(getDisplay(), newCanvas.x, newCanvas.y);
		// Create work Graphics Context
		GC wgc = new GC(offImage);

		// Resize GC
		Rectangle rectWGC = new Rectangle(0, 0, newCanvas.x, newCanvas.y);
		// Clear current Canvas
		wgc.setBackground(e.display.getSystemColor(SWT.COLOR_WHITE));
		wgc.fillRectangle(rectWGC);
		wgc.setForeground(e.display.getSystemColor(SWT.COLOR_BLUE));
		wgc.drawLine(0, newCanvas.y >> 1, newCanvas.x, newCanvas.y >> 1);

		// Adjust position of Label
		instParentView.setPositionLabelVolumeLevel(cHt);

		// Set foreground color for Script data line.
		wgc.setForeground(new Color(e.display, 120, 150, 255));

		// Initialize audio level parameters
		int audioLevel = 0;
		int startTime = currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE;
		// initial Position-X by current Slider Position
		int dx = 0;

		// Get current buffer size
		int datasize = startTimeCaptureAudio.size();
		// Search index of start TimeLine
		int startIndex;
		for (startIndex = 0; startIndex < datasize; startIndex++) {
			// Get start time
			audioLevel = startTimeCaptureAudio.get(startIndex);
			// Check target start time
			if (audioLevel >= startTime)
				break;
		}

		// Draw sampling audio level data
		for (int i = startIndex; i < datasize; i++) {
			// pickup next level data
			audioLevel = volumeLevelCaptureAudio.get(i);
			// audioLevel = volumeLevelCaptureAudio.get(i)*4; // for TEST
			// System.out.println("drawMediaAudioLevel() : audioLevel="+audioLevel);
			// adjust level size by current window height
			audioLevel = audioLevel / adjHt;
			if (audioLevel < 0)
				audioLevel = -1 * audioLevel;

			// set gain to volume level
			float tempLevel = (float) audioLevel * currentVolLvlGain;
			audioLevel = (int) tempLevel;

			// put next sampling data on Canvas
			// ** 'dx' rate is 20msec.(default rate)
			if (audioLevel > 0) {
				// pickup next start time
				startTime = startTimeCaptureAudio.get(i);
				dx = (startTime - (currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE))
						/ TIME2PIXEL;
				wgc.drawLine(dx, cHt - audioLevel, dx, cHt + audioLevel);
			}
		}

		// Initial draw TimeLine
		// ///e.gc.drawImage(offImage, 0, 0);
		e.gc.drawImage(offImage, e.x, e.y, e.width, e.height, e.x, e.y,
				e.width, e.height);

		// CleanUP work GC
		wgc.dispose();

		// Initial setup each parameters by latest capture data info.
		previousDrawTime = startTime;
		previousDrawSize = datasize;

		// Set next paint mode (Check parent play mode)
		if (TL_STAT_PLAY == instParentView.getStatusTimeLine()) {
			// Check Capture audio mode
			if (isCaptureEnabled()) {
				// Update Status : 3 (Append Volume Level mode)
				currentProcStatus = 3;
			} else {
				// Update Status : 4 (Redraw current off-image)
				currentProcStatus = 4;
			}
		} else {
			// Update Status : 4 (Redraw current off-image)
			currentProcStatus = 4;
		}

	}

	/**
	 * Local method : Append Volume Level of Media's audio to off image & Redraw
	 * Canvas
	 */
	private void drawAppendMediaAudioLevel(PaintEvent e) {

		// Get current GC from off-image
		GC wgc = new GC(offImage);

		// Get current buffer size
		int datasize = startTimeCaptureAudio.size();

		// Check capture data length
		if ((datasize - previousDrawSize) > 0) {

			// Get current Canvas size
			Point nowCanvas = this.getSize();
			int ht = nowCanvas.y;
			int cHt = ht >> 1;
			// int adjHt = (32767 / (cHt + 1)) + 1;
			int adjHt = 378;

			// Set foreground color for Script data line.
			wgc.setForeground(new Color(e.display, 120, 150, 255));

			// Initialize audio level parameters
			int audioLevel = 0;
			int startTime = 0;

			// Search 1st index of capture data
			int initTime = startTimeCaptureAudio.indexOf(previousDrawTime);
			if (initTime < 0)
				initTime = 0;

			// initial Position-X by current Slider Position
			int dx = 0;

			// Draw sampling audio level data
			for (int i = initTime; i < datasize; i++) {
				// pickup next level data
				audioLevel = volumeLevelCaptureAudio.get(i);
				// adjust level size by current window height
				audioLevel = audioLevel / adjHt;
				if (audioLevel < 0)
					audioLevel = -1 * audioLevel;

				// set gain to volume level
				float tempLevel = (float) audioLevel * currentVolLvlGain;
				audioLevel = (int) tempLevel;

				// put next sampling data on Canvas
				// ** 'dx' rate is 20msec.(default rate)
				if (audioLevel > 0) {
					// pickup next start time
					startTime = startTimeCaptureAudio.get(i);
					dx = (startTime - (currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE))
							/ TIME2PIXEL;
					wgc.drawLine(dx, cHt - audioLevel, dx, cHt + audioLevel);
				}
			}

			// Update current parameters
			previousDrawSize = datasize;
			previousDrawTime = startTime;
		}

		// Redraw current off image
		e.gc.drawImage(offImage, e.x, e.y, e.width, e.height, e.x, e.y,
				e.width, e.height);
		// CleanUP work GC
		wgc.dispose();

	}

	/**
	 * Redraw Canvas by off image
	 */
	private void redrawCurrentOffImage(PaintEvent e) {

		// Redraw current off image
		if (offImage != null) {
			e.gc.drawImage(offImage, e.x, e.y, e.width, e.height, e.x, e.y,
					e.width, e.height);
		}
	}

	/**
	 * Local method : Draw Volume Level for Action of Preview status : 3
	 */
	private void drawPreviewDataVolumeLevel(PaintEvent e) {

		// Get current Canvas size
		Point nowCanvas = this.getSize();
		int ht = nowCanvas.y;
		int cHt = ht >> 1;
		int wd = nowCanvas.x;

		// Clear current Canvas
		e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_WHITE));
		e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLUE));
		e.gc.drawLine(0, cHt, wd, cHt);

		// Adjust position of Label
		instParentView.setPositionLabelVolumeLevel(cHt);

		// Set foreground color for Script data line.
		e.gc.setForeground(new Color(e.display, 120, 150, 255));

		// Initialize script data
		int audioLevel = 0;

		// initial Position-X by current Slider Position
		int dx = 0;

		// initialize start drawing point
		int startTimeLine = 0; // TimeLineView.getInstance().getStartTimeLine();

		// Draw sampling audio level data
		for (int nowStartTime = startTimeLine / IUNIT.MSEC; nowStartTime < sampleVolumeLevel
				.size(); nowStartTime++) {

			// pickup next level data
			audioLevel = sampleVolumeLevel.get(nowStartTime);
			// put next sampling data on Canvas
			// ** 'dx' rate is 20msec.(default rate)
			if (audioLevel > 0) {
				e.gc.drawLine(dx, cHt - audioLevel, dx, cHt + audioLevel);
			}
			// update dx for Paint
			dx++;
		}
	}

	/**
	 * PaintListener
	 */
	class CanvasPaintListener implements PaintListener {

		// Override paintControl()
		public void paintControl(PaintEvent e) {
			try {
				// null check
				if (offImage == null) {
					// re-initialize off image buffer
					currentProcStatus = 1;
				}
				// Check event of Resize Canvas
				else if ((previousCanvasSize.x != e.width)
						|| (previousCanvasSize.y != e.height)) {
					// Exchange status to Resize event status (1 or 2)
					if (currentProcStatus > 1)
						currentProcStatus = 2;
					// Store current Canvas size
					previousCanvasSize.x = e.width;
					previousCanvasSize.y = e.height;
				}

				// Check current status for request function
				// Status=1 : Clear Canvas
				if (currentProcStatus == 1) {
					// Clear Canvas
					initCanvasVolumeLevel(e);
				}
				// Status=2 : Draw Volume Level data from Captured audio of
				// Movie
				else if (currentProcStatus == 2) {
					drawMediaAudioLevel(e);
				}
				// Status=3 : Append Volume Level data to current off-image
				// buffer & Redraw Canvas
				else if (currentProcStatus == 3) {
					drawAppendMediaAudioLevel(e);
				}
				// Status=4 : Redraw Canvas by current off-image buffer
				else if (currentProcStatus == 4) {
					redrawCurrentOffImage(e);
				}
				// Status=11 : Draw Volume Level data for Preview's action
				else if (currentProcStatus == 11) {
					drawPreviewDataVolumeLevel(e);
				}

				// Draw border line(current TimeLine)
				Point nowCanvas = ownInst.getSize();
				e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_BLUE));
				e.gc.fillRectangle(borderLinePosX, 0, 2, nowCanvas.y);

				// release GC resource
				e.gc.dispose();
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
	}

	public void startSampling() {
		samplingFlag = true;
		previewVoiceTotalTime = 0; // Initialize total time.
	}

	public void stopSampling() {
		samplingFlag = false;
	}

	/**
	 * Local method : update own free running timer counter (for Capture audio)
	 */
	private void updateFreeRunCounter(int duration) {
		int nowParentTimeLine = instParentView.getCurrentTimeLine();
		if (previousParentTimeLine != nowParentTimeLine) {
			// Adjust own free-run counter by current TimeLine
			previousParentTimeLine = nowParentTimeLine;
			ownFreeRunTimeCount = nowParentTimeLine;
			// Reset limit counter
			ownFreeRunLimitCount = 0;
		} else {
			// Check free run limit counter (less than 100msec)
			if (ownFreeRunLimitCount <= SM_MAX_FREERUN_COUNT) {
				// Free running own counter
				ownFreeRunTimeCount = ownFreeRunTimeCount + duration;
				// Update limit counter
				ownFreeRunLimitCount++;
			}
		}
	}

	/**
	 * for Capture audio level
	 */
	private void updateCaptureAudio(int currentTime) {

		// pickup captured data each 20msec
		int audioLevel = SoundMixer.getInstance().pickupCaptureAudioLevel();
		audioLevel = audioLevel * TL_AUDIO_AMPLIFY_LEVEL; // amplify volume
															// level graph
		int quantumStartTime = currentTime - (currentTime % TIME2PIXEL);

		if (audioLevel >= 0) {
			// null check
			if (!(startTimeCaptureAudio.isEmpty())) {
				int index = startTimeCaptureAudio.indexOf(quantumStartTime);
				if (index >= 0) {
					// update raw audio data(exist data)
					volumeLevelCaptureAudio.set(index, audioLevel);
				} else {
					// append raw audio data(new data)
					startTimeCaptureAudio.add(quantumStartTime);
					volumeLevelCaptureAudio.add(audioLevel);
				}
			} else {
				// append raw audio data(new data)
				startTimeCaptureAudio.add(quantumStartTime);
				volumeLevelCaptureAudio.add(audioLevel);
			}
		}
	}

	/**
	 * @category Save volume level data to temporary file
	 */
	public void saveVolumeLevelTempFile() {
		try {
			String fpath = null;
			if (getSamplingLengthMovieAudioLevel() > 0) {
				if (VolumeLevelCanvas.getVolumeLevelFilePath() == null) {
					// Create new temporary file for volume level
					File fh = Activator.getDefault().createTempFile(
							"temp", ".lvl");
					if (fh != null) {
						// Get string of absolute file path(temporary file)
						fpath = fh.getAbsolutePath();
					}
					VolumeLevelCanvas.setVolumeLevelFilePath(fpath);
				} else {
					fpath = VolumeLevelCanvas.getVolumeLevelFilePath().getPath();
					fpath = fpath.replace("/", "\\");
				}

				if (fpath != null) {
					// Start write volume level data thread
					writeStreamTempFile(startTimeCaptureAudio,
							volumeLevelCaptureAudio, fpath);
				}
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}

	/**
	 * @category Check enable status for clear volume level data action
	 * @return status : TRUE:enable clear action, FALSE:disable clear action
	 */
	public boolean isEnableClearVolLvl() {
		boolean result = false;

		if (VolumeLevelCanvas.getVolumeLevelFilePath() == null) {
			result = true;
		}
		return (result);
	}

	/**
	 * @category Load volume level value from temporary file
	 * @param strPathVolLvl
	 *            : source path of temporary file
	 */
	public void loadVolumeLevelTempFile() {
		String fpath = VolumeLevelCanvas.getVolumeLevelFilePath().getPath();
		if (fpath != null) {
			fpath = fpath.replace("/", "\\");
			try {
				// CleanUP buffer of captured audio
				cleanupMovieAudioLevel();

				// load volume level data from temporary file
				if (openInputStreamTempFile(fpath)) {
					while (true) {
						// read start time value
						int startTime = readIntValueTempFile();
						if (startTime >= 0) {
							// read volume level value
							int volLvl = readIntValueTempFile();
							// append raw audio data(new data)
							startTimeCaptureAudio.add(startTime);
							volumeLevelCaptureAudio.add(volLvl);
						} else {
							// detect End of File
							closeInputStreamTempFile();
							break;
						}
					}
				}
			} catch (Exception ee) {
				System.out.println("loadVolumeLevelTempFile() : " + ee);
			}
		}
	}

	private void samplingTimerTask() {
		if (samplingFlag == false) {
			return;
		}
		if (VoicePlayerFactory.getInstance().getPlayVoiceStatus()) {
			previewVoiceTotalTime++; // TODO use actual time
		} else {
			if (samplingFlag == false) { // stopped
				return;
			}

			System.out.println("count:" + previewVoiceTotalTime);

			if (TL_STAT_EXTENDED != instParentView.getStatusTimeLine()) {
				// Request redraw Canvas
				final IWorkbench workbench = PlatformUI.getWorkbench();
				final Display display = workbench.getDisplay();
				display.asyncExec(new Runnable() {
					public void run() {
						// Repaint EndTime
						if (EditPanelView.getInstance() != null) {
							EditPanelView
									.getInstance()
									.getInstanceTabEditPanel()
									.updateEndTime(
											previewVoiceTotalTime
													* SAMPLING_RATE);
							samplingFlag = false;
						}
					}
				});
			}
		}
	}

	private void captureAudioTimerTask() {
		if (isCaptureEnabled()
				&& (TL_STAT_PLAY == instParentView.getStatusTimeLine())) {
			updateFreeRunCounter(SAMPLING_RATE);
			updateCaptureAudio(ownFreeRunTimeCount);
		}
	}

	public void handleSyncTimeEvent(SyncTimeEvent e) {
		// Synchronize TimeLine view
		if (e.getEventType() == SyncTimeEvent.SYNCHRONIZE_TIME_LINE) {
			synchronizeTimeLine(e.getCurrentTime());
			// } else if (e.getEventType() == SyncTimeEvent.ADJUST_TIME_LINE) {
			// synchronizeTimeLine(e.getCurrentTime());
		} else if (e.getEventType() == SyncTimeEvent.REFRESH_TIME_LINE) {
			refreshTimeLine(e.getCurrentTime());
		}
	}

	/**
	 * Timer Utility handling
	 */
	static long previousTimerUtilEventTime = -1L;

	public void handleTimerUtilEvent(TimerEvent e) {
		long time = e.getTime();
		// if(previousTimerUtilEventTime == -1L) {
		// previousTimerUtilEventTime = time;
		// }
		if ((time - previousTimerUtilEventTime) >= (SAMPLING_RATE - 4)) {
			samplingTimerTask();

			captureAudioTimerTask();
			previousTimerUtilEventTime = time;
		}
	}

	//-----------------------------------//
	
	// Local data
	private FileOutputStream fos = null;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private WriteStreamTempFileThread instWriteThread = null;
	private boolean statusActiveThread = false;
	private ArrayList<Integer> listStartTime = null;
	private ArrayList<Integer> listVolLvl = null;
	
	private void writeStreamTempFile(ArrayList<Integer> srcData1,
			ArrayList<Integer> srcData2, String desFilePath) {
		// Check current status of copy thread
		if (!statusActiveThread) {
			dos = null;
			try {
				dos = new DataOutputStream(new FileOutputStream(desFilePath));
				// Deep copy from source data list
				listStartTime = new ArrayList<Integer>(srcData1);
				listVolLvl = new ArrayList<Integer>(srcData2);
				// start write thread
				statusActiveThread = true;
				instWriteThread = new WriteStreamTempFileThread();
				instWriteThread.start();
			} catch (Exception ee) {
			}
		}
	}

	private boolean openInputStreamTempFile(String srcFilePath) {
		// Check current status of copy thread
		if (!statusActiveThread) {
			dis = null;
			try {
				dis = new DataInputStream(new FileInputStream(srcFilePath));
				if (dis != null) {
					statusActiveThread = true;
				}
			} catch (Exception ee) {
			}
		}
		return (statusActiveThread);
	}

	private int readIntValueTempFile() {
		int result = -1;
		try {
			result = dis.readInt();
		} catch (Exception e) {
		}
		return (result);
	}

	private void closeInputStreamTempFile() {
		try {
			dis.close();
		} catch (Exception e) {
		} finally {
			statusActiveThread = false;
		}
	}

	public static void setVolumeLevelFilePath(String fpath) {
		// Update URI value
		savePathVolLvl = null;
		if (fpath != null) {
			savePathVolLvl = TempFileUtil.getResource(fpath);
		}
	}

	public static URI getVolumeLevelFilePath() {
		return savePathVolLvl;
	}

	private class WriteStreamTempFileThread extends Thread {
		/**
		 * @category Thread{@link #run()}
		 */
		public void run() {
			int location = 0;
			int max = listStartTime.size();
			try {
				while (statusActiveThread) {
					location++;
					if (location < max) {
						dos.writeInt(listStartTime.get(location));// start time
						dos.writeInt(listVolLvl.get(location));// volume level
					} else {
						closeThread();
						break;
					}
					// Thread yield
					Thread.yield();
				}
			} catch (Exception ee) {
			}
		}

		/**
		 * @category Setter method : Close run() method
		 */
		private void closeThread() {
			// Reset status flag
			statusActiveThread = false;
			instWriteThread = null;
			// Close all file stream
			try {
				dos.close();
				fos.close();
				listStartTime.clear();
				listVolLvl.clear();
				listStartTime = null;
				listVolLvl = null;
			} catch (Exception ee) {

			}
		}
	}
	
}
