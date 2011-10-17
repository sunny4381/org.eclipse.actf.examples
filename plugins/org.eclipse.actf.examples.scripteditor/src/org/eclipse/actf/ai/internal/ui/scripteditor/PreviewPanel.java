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

import org.eclipse.actf.ai.scripteditor.data.ScriptData;
import org.eclipse.actf.ai.scripteditor.util.SoundMixer;
import org.eclipse.actf.ai.scripteditor.util.WebBrowserFactory;
import org.eclipse.actf.ai.ui.scripteditor.views.EditPanelView;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.actf.examples.scripteditor.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;

public class PreviewPanel implements IUNIT {

	/**
	 * Local data
	 */
	// Parent instance
	static private TimeLineView instParentView = null;
	// Own instance
	static private PreviewPanel ownInst = null;

	// instance of ScriptData class
	static private ScriptData instScriptData = null;

	// action status(0:Idle(Stop/Pause), 1:Play)
	private int currentActionStatus = 0;
	private boolean currentSamplingAudioMode = false;
	private boolean currentStatusMedia = true;

	// Preview part
	private Button buttonRewind;
	private Button buttonPlay;

	// Preview Slider (ProgressBar)
	private Slider sliderPreview;
	private int previousEventSliderPreview = 0;
	private int previousMaxSlider = 0;
	private boolean stopExpandMaxSlider = false;
	private int storeCurrentTimeLineSlider = 0;

	// TimeLine control
	private Label timelinePreview;

	/**
	 * Constructor
	 */
	private PreviewPanel() {
		// store ScriptData class instance
		instScriptData = ScriptData.getInstance();
		// store parent class instance
		instParentView = TimeLineView.getInstance();
	}

	static public PreviewPanel getInstance() {
		// 1st check current Instance
		if (ownInst == null) {
			synchronized (PreviewPanel.class) {
				// 2nd check current instance
				if (ownInst == null) {
					// New own class at once
					ownInst = new PreviewPanel();
				}
			}
		}
		// return current Instance of own class
		return (ownInst);
	}

	/**
	 * Initialize own Panel
	 */
	public void initPreviewPanel(Display parentDisp, Composite parentComposite) {

		try {
			// Button : "Rewind"
			FormData buttonRewindLData = new FormData();
			buttonRewindLData.width = 25;
			buttonRewindLData.height = 23;
			buttonRewindLData.left = new FormAttachment(0, 1000, 4);
			buttonRewindLData.top = new FormAttachment(0, 1000, 2);
			// buttonRewindLData.bottom = new FormAttachment(1000, 1000, -4);
			buttonRewind = new Button(parentComposite, SWT.PUSH | SWT.CENTER);
			buttonRewind.setLayoutData(buttonRewindLData);
			// //buttonRewind.setText("Rewind");
			Image imgRewind = Activator.getImageDescriptor("/icons/Rewind.jpg")
					.createImage();
			buttonRewind.setImage(imgRewind);
			buttonRewind.setToolTipText("Rewind movie");

			// Rewind event listener
			buttonRewind.addSelectionListener(new RewindButtonAdapter());
			// Tracking mouse cursor listener
			buttonRewind
					.addMouseTrackListener(new ButtonMouseCursorTrackAdapter());

			// Button : "Play" or "Pause"
			FormData buttonPlayLData = new FormData();
			buttonPlayLData.width = 24;
			buttonPlayLData.height = 23;
			buttonPlayLData.left = new FormAttachment(0, 1000, 31);
			buttonPlayLData.top = new FormAttachment(0, 1000, 2);
			// buttonPlayLData.bottom = new FormAttachment(1000, 1000, -4);
			buttonPlay = new Button(parentComposite, SWT.PUSH | SWT.CENTER);
			buttonPlay.setLayoutData(buttonPlayLData);
			// Initial draw button image by current status
			redrawPlayButton(currentActionStatus);

			// Play event listener
			buttonPlay.addSelectionListener(new PlayButtonAdapter());
			// Tracking mouse cursor listener
			buttonPlay
					.addMouseTrackListener(new ButtonMouseCursorTrackAdapter());

			// Label : TimeLine for Preview's slider
			FormData labelTimeLinePreviewLData = new FormData();
			// labelTimeLinePreviewLData.width = 84;
			// labelTimeLinePreviewLData.height = 12;
			labelTimeLinePreviewLData.top = new FormAttachment(0, 1000, 8);
			// labelTimeLinePreviewLData.bottom = new FormAttachment(1000, 1000,
			// -8);
			labelTimeLinePreviewLData.right = new FormAttachment(1000, 1000, -4);
			timelinePreview = new Label(parentComposite, SWT.NONE);
			timelinePreview.setLayoutData(labelTimeLinePreviewLData);
			timelinePreview.setText("00:00/00:00");

			// Slider : (Time Line) Create Slider
			FormData sliderPreviewLayoutData = new FormData();
			// sliderPreviewLayoutData.width = 488;
			sliderPreviewLayoutData.height = 14;
			sliderPreviewLayoutData.left = new FormAttachment(0, 1000, 60);
			sliderPreviewLayoutData.top = new FormAttachment(0, 1000, 6);
			// sliderPreviewLayoutData.right = new FormAttachment(1000, 1000,
			// -4);
			sliderPreviewLayoutData.right = new FormAttachment(timelinePreview,
					-5);
			// sliderPreviewLayoutData.bottom = new FormAttachment(1000, 1000,
			// -8);
			sliderPreview = new Slider(parentComposite, SWT.HORIZONTAL);
			sliderPreview.setLayoutData(sliderPreviewLayoutData);
			// Initialize location of preview's slider
			sliderPreview.setMinimum(0);
			sliderPreview.setIncrement(TIME2PIXEL);
			setLocationPreviewSlider(TL_DEF_ETIME, 0);

			// Add SelectionListener
			sliderPreview
					.addSelectionListener(new SliderPreviewSelectionAdapter());
			// Tracking mouse cursor listener
			sliderPreview
					.addMouseTrackListener(new SliderMouseCursorTrackAdapter());

		} catch (Exception e) {
			System.out.println("PreviewPanelView() : Exception = " + e);
		}
	}

	/**
	 * Getter method : Get instance button of Play
	 */
	public Button getPlayButton() {
		return (buttonPlay);
	}

	/**
	 * Setter method : Set location of slider
	 */
	private void setLocationPreviewSlider(int movieEndTime, int currentTime) {
		// SetUP location of Preview's slider
		sliderPreview.setMaximum(movieEndTime);
		sliderPreview.setSelection(currentTime);
		sliderPreview.setToolTipText(getStringTimeLine(currentTime,
				movieEndTime));
	}

	/**
	 * Setter method : Enable control of "Play/Pause" button
	 * 
	 * @param stat
	 *            = true : Set enable button (status is Play mode) = false : Set
	 *            disable button (status is Pause or Idle mode)
	 */
	public void setEnablePlayPause(boolean stat) {
		// Control enable of "Play/Pause" button
		buttonPlay.setEnabled(stat);
	}

	/**
	 * @category Getter Method
	 * @purpose : Get current URL from Text field
	 */
	public String getURLMovie() {
		// return current URL of Preview movie
		return (WebBrowserFactory.getInstance().getUrlNavigaor());
	}

	/**
	 * @category Setter Method
	 * @purpose Set new URL to Text field
	 */
	public void setURLMovie(String newURL) {
		// Pop new URL to navigator
		WebBrowserFactory.getInstance().setUrlNavigaor(newURL);
	}

	/**
	 * @category Getter method
	 * @purpose Get current video status
	 * 
	 *          STATE_UNKNOWN STATE_PLAY STATE_STOP STATE_PAUSE
	 *          STATE_FASTFORWARD STATE_FASTREVERSE STATE_WAITING
	 */
	public int getVideoStatus() {
		// return current video status
		return (WebBrowserFactory.getInstance().getVideoStatus());
	}

	/**
	 * @category Getter method
	 * @purpose Get current video position(play time)
	 */
	public int getVideoCurrentPosition() {
		// return current video status
		return (WebBrowserFactory.getInstance().getCurrentPosition());
	}

	/**
	 * @category Getter method
	 * @purpose Get current video size(end time)
	 */
	public int getVideoTotalTime() {
		// return current video status
		return (WebBrowserFactory.getInstance().getTotalLength());
	}

	/**
	 * @category Setter Method
	 * @purpose Synchronized Time Line
	 */
	public void synchronizeTimeLine(int nowTime) {
		// PickUP End TimeLine
		int movieTotalTime = instParentView.getEndTimeLine();

		// Update Time Display of own view
		timelinePreview.setText(getStringTimeLine(nowTime, movieTotalTime));

		// Update slider's position of own view
		setLocationPreviewSlider(movieTotalTime, nowTime);
	}

	/**
	 * @category Local method
	 * @param nowTime
	 * @param endTime
	 * @return
	 */
	private String getStringTimeLine(int nowTime, int endTime) {

		// MakeUP string of current time line of Volume Level
		// 1)current time line
		String strCurrentTime = new String(instScriptData.makeFormatMM(nowTime)
				+ ":" + instScriptData.makeFormatSS(nowTime));
		// 2)end time line
		String strEndTime = new String(instScriptData.makeFormatMM(endTime)
				+ ":" + instScriptData.makeFormatSS(endTime));

		// Return string of current time line of VolumeLevel
		return (strCurrentTime + "/" + strEndTime);
	}

	/**
	 * 
	 * @param nextStatus
	 */
	private void redrawPlayButton(int nextStatus) {
		// SetUP next button image
		Image imgPlay = null;
		String textToolTip = null;

		// Check next status(action)
		if (nextStatus == 0) {
			// Configure Button
			imgPlay = Activator.getImageDescriptor("/icons/Play.jpg")
					.createImage();
			textToolTip = new String("Play movie");
		} else {
			// Configure
			imgPlay = Activator.getImageDescriptor("/icons/Pause.jpg")
					.createImage();
			textToolTip = new String("Pause movie");
		}

		// SetUP new button image
		buttonPlay.setImage(imgPlay);
		buttonPlay.setToolTipText(textToolTip);
	}

	/**
	 * Getter method : Check current status of Sampling Audio
	 */
	public boolean isSamplingAudioMode() {
		return (currentSamplingAudioMode);
	}

	/**
	 * Setter method : Set status of Sampling Audio
	 */
	public void setSamplingAudioMode(boolean stat) {
		currentSamplingAudioMode = stat;
	}

	/**
	 * Local Class implements SelectionAdapter
	 */
	class PlayButtonAdapter extends SelectionAdapter {
		// Event Play/Pause movie
		public void widgetSelected(SelectionEvent e) {
			playPauseMedia();
		}
	}

	/**
	 * Get current status for media play
	 * 
	 * @return current status (TRUE:now playing, FALSE:otherwise)
	 */
	public boolean getCurrentStatusMedia() {
		// return result
		return (currentStatusMedia);
	}

	/**
	 * Set new status for media play
	 * 
	 * @param newStat
	 *            : new status (TRUE:now playing, FALSE:otherwise)
	 */
	public void setCurrentStatusMedia(boolean newStat) {
		// Update status
		currentStatusMedia = newStat;
	}

	/**
	 * Play/Pause media
	 */
	public int playPauseMedia() {
		// Update status flag
		currentActionStatus = ~currentActionStatus;

		// Check next status(action)
		if (currentActionStatus == 0) {
			// Close captured voice module
			SoundMixer.getInstance().stopCaptureSound();
			SoundMixer.getInstance().stopPlaySound();

			// Pause MoviePlayer
			WebBrowserFactory.getInstance().pauseMedia();
			// Pause VoicePlayer
			instParentView.switchActionTimeLine(false);
			// Stop TTS engine
			instParentView.reqStopVoicePlayer();

			// redraw captured audio level to Canvas
			instParentView.reqRedrawVolumeLevelCanvas(4);
			// Set status "normal mode"
			setSamplingAudioMode(false);

			// SetUP enable status for File Menu
			setCurrentStatusMedia(true);
		} else {
			// Check current buffer status
			int paintStatus;
			if (VolumeLevelCanvas.getInstance().getCurrentCaptureMode()) {
				// Start captured voice module
				SoundMixer.getInstance().startCaptureSound(SM_PMODE_CAPTURE);
				// Set status "Sampling mode"
				setSamplingAudioMode(true);
				// Set paint mode = 2 : Redraw capture data
				paintStatus = 2;
			} else {
				// Set paint mode = 4 : Redraw current off image
				paintStatus = 4;
			}
			// Initial draw captured audio level to Canvas
			instParentView.reqRedrawVolumeLevelCanvas(paintStatus);

			// Start MoviePlayer
			WebBrowserFactory.getInstance().playMedia();
			// Start VoicePlayer
			instParentView.switchActionTimeLine(true);

			// SetUP disable status for File Menu
			setCurrentStatusMedia(false);
		}

		// SetUP new button image
		redrawPlayButton(currentActionStatus);

		// Control enabled "Preview" button of EditPanel view
		EditPanelView.getInstance().getInstanceTabEditPanel().setEnablePreview(
				currentActionStatus);
		EditPanelView.getInstance().getInstanceTabSelWAVFile()
				.setEnablePreview(currentActionStatus);

		// Return current status
		return (currentActionStatus);
	}

	/**
	 * Setter method : Request Extended text control
	 */
	public void controlExtendedPlay(boolean stat) {
		// TRUE : Start Extended voice, Suspend all time line
		if (stat) {
			// Pause MoviePlayer
			WebBrowserFactory.getInstance().pauseMedia();
		}
		// FALSE : Finish Extended voice, Resume all time line
		else {
			// Start MoviePlayer
			WebBrowserFactory.getInstance().playMedia();
		}
	}

	/**
	 * Local Class implements SelectionAdapter
	 */
	class RewindButtonAdapter extends SelectionAdapter {
		// Event Play/Pause movie
		public void widgetSelected(SelectionEvent e) {
			// Rewind media content
			rewindMedia();
			// SetUP enable status for File Menu
			setCurrentStatusMedia(true);
		}
	}

	public void rewindMedia() {
		// Set status "normal mode"
		setSamplingAudioMode(false);
		// Rewind VoicePlayer
		instParentView.switchActionTimeLine(false);
		instParentView.rewindActionTimeLine();
		currentActionStatus = 0;
		redrawPlayButton(currentActionStatus);
		sliderPreview.setSelection(0);

		// Control enabled "Preview" button of EditPanel view
		EditPanelView.getInstance().getInstanceTabEditPanel().setEnablePreview(
				currentActionStatus);
		EditPanelView.getInstance().getInstanceTabSelWAVFile()
				.setEnablePreview(currentActionStatus);

		// Close captured voice module
		SoundMixer.getInstance().stopCaptureSound();
		SoundMixer.getInstance().stopPlaySound();
		// Rewind movie
		WebBrowserFactory.getInstance().rewindMedia();
		// Stop TTS engine
		instParentView.reqStopVoicePlayer();

		// Rewind all time line
		instParentView.reqRewindTimeLine();

		// SetUP enable status for File Menu
		setCurrentStatusMedia(true);

		// Change paint mode : Redraw off image
		// ///instParentView.reqRedrawVolumeLevelCanvas(4);
	}

	/**
	 * Reload media
	 */
	public void reload() {

		// CleanUP buffer of captured audio
		instParentView.reqCleanupCaptureData();
		// redraw captured audio level to Canvas
		instParentView.reqRedrawVolumeLevelCanvas(1);

		// Reset all time line
		// ///imeLineView.getInstance().resetTimeLine();

		// reload media content
		WebBrowserFactory.getInstance().getInstWebBrowser().navigateRefresh();
		// Stop TTS engine
		instParentView.reqStopVoicePlayer();

		// SetUP enable status for File Menu
		setCurrentStatusMedia(true);
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
			parentButton.setCursor(new Cursor(null, SWT.CURSOR_ARROW));
		}
	}

	/**
	 * Local Class extends SliderSelectionAdapter for Slider
	 */
	class SliderPreviewSelectionAdapter extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			// get current location
			Slider ps = (Slider) e.getSource();
			int currentLocation = ps.getSelection();

			// check event of slider
			if ((e.detail == SWT.DRAG)
					&& (previousEventSliderPreview != SWT.DRAG)) {
				// store current event
				previousEventSliderPreview = e.detail;
				// store current max time line
				previousMaxSlider = ps.getMaximum();
			} else if ((e.detail == 0)
					&& (previousEventSliderPreview == SWT.DRAG)) {
				// store current event
				previousEventSliderPreview = e.detail;
				// check stop flag & end process
				if (stopExpandMaxSlider) {
					// reset stop flag
					stopExpandMaxSlider = false;
					// Update slider's position of own view
					setLocationPreviewSlider(ps.getMaximum(),
							storeCurrentTimeLineSlider);
				}
				// drop current event
				return;
			}
			// check single action for expand time line
			if (previousEventSliderPreview == SWT.DRAG) {
				// check stop max time line
				if (currentLocation >= previousMaxSlider) {
					// Set stop flag
					stopExpandMaxSlider = true;
					// store current time line
					storeCurrentTimeLineSlider = TimeLineCanvas.getInstance()
							.getCurrentPositionMarkerTimeLine();
				}
			}

			// check stop flag
			if (!stopExpandMaxSlider) {
				// SetUP new location to current TimeLine
				instParentView.reqSetTrackCurrentTimeLine(currentLocation);
				// Synchronize all TimeLine
				instParentView.synchronizeAllTimeLine(currentLocation);
				
				WebBrowserFactory.getInstance()
						.setCurrentPosition(currentLocation);					
			}
		}
	}

	/**
	 * Local Class extends MouseTrackAdapter for Slider
	 */
	class SliderMouseCursorTrackAdapter extends MouseTrackAdapter {
		// mouse cursor enter into parent area
		public void mouseEnter(MouseEvent e) {
			// Changer Cursor image from ARROW type to HAND type
			Slider parentSlider = (Slider) e.getSource();
			parentSlider.setCursor(new Cursor(null, SWT.CURSOR_HAND));
		}

		// mouse cursor exit parent area
		public void mouseExit(MouseEvent e) {
			// Reset Cursor image to default type (ARROW)
			Slider parentSlider = (Slider) e.getSource();
			parentSlider.setCursor(new Cursor(null, SWT.CURSOR_ARROW));
		}
	}

}
