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

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.actf.ai.scripteditor.preferences.CapturePreferenceUtil;
import org.eclipse.actf.ai.scripteditor.util.SoundMixer;
import org.eclipse.actf.ai.scripteditor.util.TempFileUtil;
import org.eclipse.actf.ai.ui.scripteditor.views.EditPanelView;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.swt.SWT;
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

//========================================
// Canvas class for Volume Level content
public class VolumeLevelCanvas extends Canvas implements IUNIT {

	// instance of own class
	static private VolumeLevelCanvas ownInst = null;

	// Process Status flag
	// 0:Idle
	// 1:Clear Screen
	// 2:Draw current data(voice level)
	// 3:Re-Draw off-image on Screen
	private int currentProcStatus = 0;

	// Off Image
	private Image offImage = null;

	// private label object
	// /private Label labelBorderTimeLine;
	private int borderLinePosX = 0;
	//private int previousBorderLinePosX = 0;

	// sampling audio level data
	private ArrayList<Integer> sampleVolumeLevel;
	private ArrayList<Integer> startTimeCaptureAudio;
	private ArrayList<Integer> volumeLevelCaptureAudio;
	private int previewVoiceTotalTime = 0;
	private float currentVolLvlGain = 1.0f;
	private URI savePathVolLvl = null;

	// capture mode flag
	// TRUE : capture audio mode
	// FALSE : normal mode
	private boolean currentCaptureMode = true;

	// current location of TimeLine
	private int currentTimeLineLocation = 0;

	// Sampling Timer Task
	private SamplingTimerTask instTimerTaskSampling = null;
	private ScheduledExecutorService schedulerSamplingLevel = null;
	private ScheduledFuture<?> futureSamplingLevel = null;

	// Capture Audio Timer Task
	private CaptureAudioTimerTask instTimerTaskCaptureAudio = null;
	private ScheduledExecutorService schedulerCaptureAudio = null;
	private ScheduledFuture<?> futureCaptureAudio = null;
	private int ownFreeRunTimeCount = 0;
	private int ownFreeRunLimitCount = 0;
	private int previousParentTimeLine = 0;

	// Parameters for draw captured data
	private int previousDrawTime = 0;
	private int previousDrawSize = 0;
	private Point previousCanvasSize;

	// parent view info.
	private TimeLineView instParentView;

	/**
	 * @category Constructor
	 */
	public VolumeLevelCanvas(Composite parent) {
		super(parent, SWT.BORDER);

		// store own instance
		ownInst = this;

		// Initialize Canvas & Create Graphics Context.
		initializeCanvas(parent);

		// Store TimeLine view instance
		instParentView = TimeLineView.getInstance();

		// Stand-by Capture timer
		if (getCurrentCaptureMode()) {
			// Start Timer
			startTimerCaptureAudio();
			// Set status : Capture Audio mode
			setCurrentCaptureMode(true);
		}

		// Initialize value by Preference setting
		setCurrentVolLvlGain(CapturePreferenceUtil.getPreferenceVolLvlGain());

	}

	static public VolumeLevelCanvas getInstance(Composite parent) {

		// 1st check current Instance
		if (ownInst == null) {
			synchronized (VolumeLevelCanvas.class) {
				// 2nd check current instance
				if (ownInst == null) {
					// New own class at once
					ownInst = new VolumeLevelCanvas(parent);
				}
			}
		}
		// return current Instance of VoluemLevel Canvas
		return (ownInst);
	}

	static public VolumeLevelCanvas getInstance() {
		// return current Instance of VoluemLevel Canvas
		return (ownInst);
	}

	// Initialize for Canvas class & object
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
		} catch (Exception ef) {
			System.out.println("initializeCanvas : Exception = " + ef);
		}
	}

	/**
	 * @category Setter Method
	 * @purpose Synchronized Time Line
	 */
	public void synchronizeTimeLine(int nowTime) {
		// Calculate current x-point by now TimeLine
		int x = (nowTime - (currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE))
				/ TIME2PIXEL;

		// Update location for border of TimeLine
		setLocationBorderTimeLine(x);
	}

	/**
	 * Setter method : Set current location of TimeLine
	 */
	public void refreshTimeLine(int nowCnt) {
		// Update counter of location of TimeLine
		currentTimeLineLocation = nowCnt;

		// Repaint Canvas
		setStatusCanvasVolumeLevel(2);
	}

	/**
	 * Setter methods : Set Status to internal flag
	 */
	public void setStatusCanvasVolumeLevel(int nextStatus) {
		// Store next status
		currentProcStatus = nextStatus;
		redraw();
	}

	private void setLocationBorderTimeLine(int x) {
		// Get current Canvas size
		// Point nowCanvas = this.getSize();

		// Store current time
		//previousBorderLinePosX = borderLinePosX;

		// Update current time(position)
		borderLinePosX = x - 1;
		if (borderLinePosX < 0)
			borderLinePosX = 0;
		// Paint event
		// ///redraw(previousBorderLinePosX, 0, 4, nowCanvas.y, true);
		redraw();
	}

	/**
	 * Getter method : Get current sampling data length
	 */
	public int getSamplingLengthVolumeLevel() {
		// Return current sampling data length
		// ///return (sampleVolumeLevel.size());
		return (previewVoiceTotalTime);
	}

	/**
	 * Setter method : Reset sampling data length
	 */
	public void clearSamplingLengthVolumeLevel() {
		// Return current sampling data length
		previewVoiceTotalTime = 0;
	}

	/**
	 * Getter method : Get current capture audio mode
	 * 
	 * @return
	 */
	public boolean getCurrentCaptureMode() {
		// return current capture mode
		return (currentCaptureMode);
	}

	/**
	 * Getter method : Get current capture audio mode
	 * 
	 * @return
	 */
	public void setCurrentCaptureMode(boolean stat) {
		// update capture mode
		currentCaptureMode = stat;
	}

	/**
	 * Setter method : Set Gain of volume level by Preference setting
	 * 
	 * @param newGain
	 *            : new Gain value by Preference setting
	 */
	public void setCurrentVolLvlGain(int newGain) {
		// Calculate new Gain value
		currentVolLvlGain = (float) newGain / 100.0f;
	}

	/**
	 * Setter method : initialize ArrayList of captured audio level
	 */
	public void cleanupMovieAudioLevel() {
		// CleanUP ArrayList of captured audio level
		startTimeCaptureAudio.clear();
		volumeLevelCaptureAudio.clear();
	}

	public int getSamplingLengthMovieAudioLevel() {
		int result = -1;
		if (!startTimeCaptureAudio.isEmpty()) {
			// Get current size
			result = startTimeCaptureAudio.size();
		}
		// Return current sampling data length
		return (result);
	}

	/**
	 * Local method : clear previous border line area
	 * 
	 * @param e
	 */
	// private void clearPreviousBorderLine(PaintEvent e) {
	// // Get current Canvas size
	// Point nowCanvas = this.getSize();
	// // Clear previous border line area by off-image(original data)
	// e.gc.drawImage(offImage, previousBorderLinePosX, 0, 2, nowCanvas.y,
	// previousBorderLinePosX, 0, 2, nowCanvas.y);
	// }

	/**
	 * Local method : draw border line
	 */
	private void drawBorderLine(PaintEvent e) {
		// Get current Canvas size
		Point nowCanvas = this.getSize();
		// Draw border line(current TimeLine)
		e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_BLUE));
		e.gc.fillRectangle(borderLinePosX, 0, 2, nowCanvas.y);
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
		// ///e.gc.drawImage(offImage, 0, 0);
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
		int adjHt = (32767 / (cHt + 1)) + 1;

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
			if (getCurrentCaptureMode()) {
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
			int adjHt = (32767 / (cHt + 1)) + 1;

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
		int startTimeLine = TimeLineView.getInstance().getStartTimeLine();

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

				// Clear previous border line area
				// ///clearPreviousBorderLine(e);
				// Draw border line(current TimeLine)
				drawBorderLine(e);

				// release GC resource
				e.gc.dispose();
			} catch (Exception ee) {
				System.out.println(">>VolumeCanvas::paintControl() : " + ee);
			}
		}
	}

	public Boolean startSamplingVolumeLevel() {

		Boolean result = true;

		// unable to duplicated spawn
		if (futureSamplingLevel == null) {
			// Clear sampling buffer
			previewVoiceTotalTime = 0;

			// Initial setup Timer Task for sampling volume level data
			instTimerTaskSampling = new SamplingTimerTask();
			schedulerSamplingLevel = Executors
					.newSingleThreadScheduledExecutor();
			// Start Timer Task
			futureSamplingLevel = schedulerSamplingLevel.scheduleAtFixedRate(
					instTimerTaskSampling, 0, TL_AUDIO_SAMPLE_TIME,
					TimeUnit.MILLISECONDS);

		} else {
			// already spawn Thread
			result = false;
		}

		// return current status
		return (result);
	}

	public void shutdownSamplingVolumeLevel() {

		// check current instance
		if (futureSamplingLevel != null) {
			// Destroy Timer Task & Scheduler
			futureSamplingLevel.cancel(true);
			schedulerSamplingLevel.shutdownNow();
			// Request Garbage Collection
			futureSamplingLevel = null;
			instTimerTaskSampling = null;
		}
	}

	public Boolean isTimerTaskSampling() {
		// return current status of TimerTask
		return ((futureSamplingLevel == null) ? false : true);
	}

	/**
	 * @category Sampling Volume Level
	 * 
	 * 
	 */
	class SamplingTimerTask implements Runnable {

		/**
		 * @category Run method of Timer Task
		 */
		public void run() {
			// Check status of Audio preview
			if (instParentView.isSamplingScriptAudio()) {
				try {
					// Sampling level data
					// // audioLevel =
					// SoundMixer.getInstance().getCompoundVolumeLevel();
					// Append sampling level data
					// ///sampleVolumeLevel.add(audioLevel);
					previewVoiceTotalTime++;
				} catch (Exception e) {
					System.out.println("Catch Exception : " + e);
				}
			} else {
				// Stop & Destroy Timer Task
				shutdownSamplingVolumeLevel();

				// Request draw Canvas
				// ///setStatusCanvasVolumeLevel(11);

				// Check current status(Not Play Extended text)
				if (TL_STAT_EXTENDED != instParentView.getStatusTimeLine()) {
					// Request redraw Canvas
					final IWorkbench workbench = PlatformUI.getWorkbench();
					final Display display = workbench.getDisplay();
					display.asyncExec(new Runnable() {
						public void run() {
							// Control disabled "Play/Pause" button
							// ///PreviewPanelView.getInstance().setEnablePlayPause(true);
							// GUI access
							// // redraw();

							// Repaint EndTime
							EditPanelView.getInstance()
									.getInstanceTabEditPanel()
									.repaintTextEndTime();
						}
					});
				}
			}
		}
	}

	public Boolean startTimerCaptureAudio() {

		Boolean result = true;

		// unable to duplicated spawn
		if (futureCaptureAudio == null) {
			// Initialize each parameters
			previousParentTimeLine = instParentView.getCurrentTimeLine();
			ownFreeRunTimeCount = previousParentTimeLine;
			ownFreeRunLimitCount = 0;

			// Initial setup Timer Task for sampling volume level data
			instTimerTaskCaptureAudio = new CaptureAudioTimerTask();
			schedulerCaptureAudio = Executors
					.newSingleThreadScheduledExecutor();
			// Start Timer Task
			futureCaptureAudio = schedulerCaptureAudio.scheduleAtFixedRate(
					instTimerTaskCaptureAudio, 0, TL_AUDIO_SAMPLE_TIME,
					TimeUnit.MILLISECONDS);

		} else {
			// already spawn Thread
			result = false;
		}

		// return current status
		return (result);
	}

	public void shutdownTimerCaptureAudio() {
		// check current instance
		if (futureCaptureAudio != null) {
			// Destroy Timer Task & Scheduler
			futureCaptureAudio.cancel(true);
			schedulerCaptureAudio.shutdownNow();
			// Request Garbage Collection
			futureCaptureAudio = null;
			instTimerTaskCaptureAudio = null;
		}
	}

	/**
	 * Local method : update own free running timer counter (for Capture audio)
	 */
	private void updateFreeRunCounter(int duration) {
		// pickup current TimeLine
		int nowParentTimeLine = instParentView.getCurrentTimeLine();
		// check modified TimeLine
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
		// Calculate Quantization start time
		int quantumStartTime = currentTime - (currentTime % TIME2PIXEL);

		// check data
		if (audioLevel >= 0) {
			// null check
			if (!(startTimeCaptureAudio.isEmpty())) {
				// Search target data(StartTime)
				int index = startTimeCaptureAudio.indexOf(quantumStartTime);
				// exist data
				if (index >= 0) {
					// update raw audio data(exist data)
					volumeLevelCaptureAudio.set(index, audioLevel);
				}
				// new data
				else {
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
	 * @category Sampling Volume Level
	 * 
	 * 
	 */
	class CaptureAudioTimerTask implements Runnable {

		/**
		 * @category Run method of Timer Task
		 */
		public void run() {
			// check current status
			if (getCurrentCaptureMode()
					&& (TL_STAT_PLAY == instParentView.getStatusTimeLine())) {
				// Adjust free run counter
				updateFreeRunCounter(TL_AUDIO_SAMPLE_TIME);

				// Store data of Captured audio level
				updateCaptureAudio(ownFreeRunTimeCount);
			}
			Thread.yield();
		}
	}

	/*********************************************************
	 * File in/out management
	 * 
	 ********************************************************/
	/**
	 * @category Save volume level data to temporary file
	 */
	public void saveVolumeLevelTempFile() {
		try {
			String fpath = null;
			// Check exist data
			if (getSamplingLengthMovieAudioLevel() > 0) {
				// Check exit target file
				if (savePathVolLvl == null) {
					// Create new temporary file for volume level
					File fh = TempFileUtil.createTempFile(DIR_TEMP_VOLLVL,
							FILE_TEMP_VOLLVL_PREFIX, FILE_TEMP_VOLLVL_SUFFIX);
					if (fh != null) {
						// Get string of absolute file path(temporary file)
						fpath = fh.getAbsolutePath();
					}
				} else {
					// PickUP target file path
					fpath = savePathVolLvl.getPath();
					fpath = fpath.replace("/", "\\");
				}

				if (fpath != null) {
					// Start write volume level data thread
					TempFileUtil.writeStreamTempFile(startTimeCaptureAudio,
							volumeLevelCaptureAudio, fpath);
					savePathVolLvl = TempFileUtil.getResource(fpath);
				}
			}
		} catch (Exception ee) {
			System.out.println("saveVolumeLevelTempFile() : " + ee);
		}
	}

	/**
	 * @category Getter method : Get save path of volume level(temporary file)
	 * @return
	 */
	public URI getSavePathVolLvl() {
		// return result
		return (savePathVolLvl);
	}

	/**
	 * @category Setter method : Set save path of volume level(temporary file)
	 */
	public void setSavePathVolLvl(String fpath) {
		// Update URI value
		savePathVolLvl = null;
		if (fpath != null) {
			savePathVolLvl = TempFileUtil.getResource(fpath);
		}
	}

	/**
	 * @category Check enable status for clear volume level data action
	 * @return status : TRUE:enable clear action, FALSE:disable clear action
	 */
	public boolean isEnableClearVolLvl() {
		boolean result = false;

		// Check volume level file path
		if (savePathVolLvl == null) {
			// enable clear action
			result = true;
		}

		// return result
		return (result);
	}

	/**
	 * @category Load volume level value from temporary file
	 * @param strPathVolLvl
	 *            : source path of temporary file
	 */
	public void loadVolumeLevelTempFile() {
		if (savePathVolLvl != null) {
			// trim URI string
			String fpath = savePathVolLvl.getPath();
			fpath = fpath.replace("/", "\\");
			try {
				// CleanUP buffer of captured audio
				cleanupMovieAudioLevel();

				// load volume level data from temporary file
				if (TempFileUtil.openInputStreamTempFile(fpath)) {
					while (true) {
						// read start time value
						int startTime = TempFileUtil.readIntValueTempFile();
						if (startTime >= 0) {
							// read volume level value
							int volLvl = TempFileUtil.readIntValueTempFile();

							// append raw audio data(new data)
							startTimeCaptureAudio.add(startTime);
							volumeLevelCaptureAudio.add(volLvl);
						} else {
							// detect End of File
							TempFileUtil.closeInputStreamTempFile();
							break;
						}
					}
				}
			} catch (Exception ee) {
				System.out.println("loadVolumeLevelTempFile() : " + ee);
			}
		}
	}

}
