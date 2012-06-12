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

import org.eclipse.actf.ai.internal.ui.scripteditor.event.EventManager;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.MouseDragEvent;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.MouseDragEventListener;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.PlayerControlEvent;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.PlayerControlEventListener;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.SyncTimeEvent;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.SyncTimeEventListener;
import org.eclipse.actf.ai.scripteditor.util.SoundMixer;
import org.eclipse.actf.ai.scripteditor.util.TimeFormatUtil;
import org.eclipse.actf.ai.scripteditor.util.WebBrowserFactory;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.actf.examples.scripteditor.Activator;
import org.eclipse.actf.model.ui.IModelService;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserACTF;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;

public class PreviewPanel implements IUNIT, SyncTimeEventListener,
		MouseDragEventListener, PlayerControlEventListener {

	static private TimeLineView instParentView = null;
	static private PreviewPanel ownInst = null;

	// action status(0:Idle(Stop/Pause), 1:Play)
	private int currentActionStatus = 0;
	private boolean isInSampling = false;
	private boolean currentStatusMedia = true;

	// for timeline drag
	private boolean currentDragStatus = false; // status for dragging
	private int saveCurrentActionStatus = currentActionStatus; // save
																// currentActionStatus

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

	private static EventManager eventManager = null;

	/**
	 * Constructor
	 */
	private PreviewPanel() {
		instParentView = TimeLineView.getInstance();
		eventManager = EventManager.getInstance();
	}

	static public PreviewPanel getInstance() {
		if (ownInst == null) {
			synchronized (PreviewPanel.class) { // TODO check
				// 2nd check current instance
				if (ownInst == null) {
					ownInst = new PreviewPanel();
				}
			}
		}
		return (ownInst);
	}

	/**
	 * Initialize own Panel
	 */
	public void initPreviewPanel(Display parentDisp, Composite parentComposite) {

		try {
			FormData buttonRewindLData = new FormData();
			buttonRewindLData.width = 25;
			buttonRewindLData.height = 23;
			buttonRewindLData.left = new FormAttachment(0, 1000, 4);
			buttonRewindLData.top = new FormAttachment(0, 1000, 2);
			buttonRewind = new Button(parentComposite, SWT.PUSH | SWT.CENTER);
			buttonRewind.setLayoutData(buttonRewindLData);
			// //buttonRewind.setText("Rewind");
			Image imgRewind = Activator.getImageDescriptor("/icons/Rewind.jpg")
					.createImage();
			buttonRewind.setImage(imgRewind);
			buttonRewind.setToolTipText("Rewind movie");

			// Rewind event listener
			buttonRewind.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					rewindMedia();
					currentStatusMedia = true;
				}
			});
			buttonRewind.addMouseTrackListener(new MouseCursorTrackAdapter());

			FormData buttonPlayLData = new FormData();
			buttonPlayLData.width = 24;
			buttonPlayLData.height = 23;
			buttonPlayLData.left = new FormAttachment(0, 1000, 31);
			buttonPlayLData.top = new FormAttachment(0, 1000, 2);
			buttonPlay = new Button(parentComposite, SWT.PUSH | SWT.CENTER);
			buttonPlay.setLayoutData(buttonPlayLData);
			// Initial draw button image by current status
			redrawPlayButton(currentActionStatus);

			// Play event listener
			buttonPlay.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					playPauseMedia();
				}
			});
			buttonPlay.addMouseTrackListener(new MouseCursorTrackAdapter());

			FormData labelTimeLinePreviewLData = new FormData();
			labelTimeLinePreviewLData.top = new FormAttachment(0, 1000, 8);
			labelTimeLinePreviewLData.right = new FormAttachment(1000, 1000, -4);
			timelinePreview = new Label(parentComposite, SWT.NONE);
			timelinePreview.setLayoutData(labelTimeLinePreviewLData);
			timelinePreview.setText("00:00/00:00");

			FormData sliderPreviewLayoutData = new FormData();
			sliderPreviewLayoutData.height = 14;
			sliderPreviewLayoutData.left = new FormAttachment(0, 1000, 60);
			sliderPreviewLayoutData.top = new FormAttachment(0, 1000, 6);
			sliderPreviewLayoutData.right = new FormAttachment(timelinePreview,
					-5);
			sliderPreview = new Slider(parentComposite, SWT.HORIZONTAL);
			sliderPreview.setLayoutData(sliderPreviewLayoutData);
			sliderPreview.setMinimum(0);
			sliderPreview.setIncrement(TIME2PIXEL);
			setLocationPreviewSlider(TL_DEF_ETIME, 0);

			sliderPreview
					.addSelectionListener(new SliderPreviewSelectionAdapter());
			sliderPreview.addMouseTrackListener(new MouseCursorTrackAdapter());

			// initialize event listeners
			eventManager.addSyncTimeEventListener(this);
			eventManager.addMouseDragEventListener(this);
			eventManager.addPlayerControlEvenListener(this);
			parentComposite.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent e) {
					// remove EventListener
					eventManager.removeSyncTimeEventListener(ownInst);
					eventManager.removeMouseDragEventListener(ownInst);
					eventManager.removePlayerControlEventListener(ownInst);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get Play button
	 */
	public Button getPlayButton() {
		return (buttonPlay);
	}

	private void setLocationPreviewSlider(int movieEndTime, int currentTime) {
		// SetUP location of Preview's slider
		sliderPreview.setMaximum(movieEndTime);
		sliderPreview.setSelection(currentTime);
		sliderPreview.setToolTipText(getStringTimeLine(currentTime,
				movieEndTime));
	}

	/**
	 * @category Setter Method
	 * @purpose Synchronized Time Line
	 */
	public void synchronizeTimeLine(int nowTime) {
		// PickUP End TimeLine
		int movieTotalTime = WebBrowserFactory.getInstance().getTotalLength();

		// Update Time Display of own view
		timelinePreview.setText(getStringTimeLine(nowTime, movieTotalTime));

		// Update slider's position of own view
		setLocationPreviewSlider(movieTotalTime, nowTime);
	}

	private String getStringTimeLine(int nowTime, int endTime) {
		String strCurrentTime = new String(TimeFormatUtil.makeFormatMM(nowTime)
				+ ":" + TimeFormatUtil.makeFormatSS(nowTime));
		String strEndTime = new String(TimeFormatUtil.makeFormatMM(endTime)
				+ ":" + TimeFormatUtil.makeFormatSS(endTime));
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
	 * Get current status for media play
	 * 
	 * @return current status (TRUE:now playing, FALSE:otherwise)
	 */
	public boolean getCurrentStatusMedia() {
		// TODO use WebBrowserFactory
		return (currentStatusMedia);
	}

	/**
	 * Play/Pause media
	 */
	public int playPauseMedia() {
		// Update status flag
		currentActionStatus = ~currentActionStatus;

		if (currentActionStatus == 0) {
			SoundMixer.getInstance().stopCaptureSound();
			SoundMixer.getInstance().stopPlaySound();

			// Pause media
			WebBrowserFactory.getInstance().pauseMedia();
			instParentView.switchActionTimeLine(false);
			instParentView.reqStopVoicePlayer();

			// redraw captured audio level
			instParentView.reqRedrawVolumeLevelCanvas(4);
			// Set normal mode
			isInSampling = false;
			// SetUP enable status for File Menu
			currentStatusMedia = true;
		} else {
			// Check current buffer status
			int paintStatus;
			if (VolumeLevelCanvas.getInstance().isCaptureEnabled()) {
				// Start
				SoundMixer.getInstance().startCaptureSound(SM_PMODE_CAPTURE);

				// Set sampling mode
				isInSampling = true;
				// Set paint mode = 2 : Redraw capture data
				paintStatus = 2;
			} else {
				// Set paint mode = 4 : Redraw current off image
				paintStatus = 4;
			}
			instParentView.reqRedrawVolumeLevelCanvas(paintStatus);

			// Start media
			WebBrowserFactory.getInstance().playMedia();
			instParentView.switchActionTimeLine(true);
			// SetUP disable status for File Menu
			currentStatusMedia = false;
		}
		redrawPlayButton(currentActionStatus);

		// TODO disable preview while playing movie
		// EditPanelView panelView = EditPanelView.getInstance();
		// if (panelView != null) {
		// panelView.getInstanceTabEditPanel().setEnablePreview(
		// currentActionStatus);
		// panelView.getInstanceTabSelWAVFile().setEnablePreview(
		// currentActionStatus);
		// }

		return (currentActionStatus);
	}

	public void rewindMedia() {
		// Set status "normal mode"
		isInSampling = false;
		// Rewind VoicePlayer
		instParentView.switchActionTimeLine(false);
		instParentView.rewindActionTimeLine();
		currentActionStatus = 0;
		redrawPlayButton(currentActionStatus);
		sliderPreview.setSelection(0);

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
		currentStatusMedia = true;

		// Change paint mode : Redraw off image
		// ///instParentView.reqRedrawVolumeLevelCanvas(4);
	}

	/**
	 * Reload media
	 */
	public void reload() {
		instParentView.cleanupCaptureData();
		instParentView.reqRedrawVolumeLevelCanvas(1);

		// reload media content
		IModelService model = ModelServiceUtils.getActiveModelService();
		if (model instanceof IWebBrowserACTF) {
			((IWebBrowserACTF) model).navigateRefresh();
		}
		// Stop TTS engine
		instParentView.reqStopVoicePlayer();
		// SetUP enable status for File Menu
		currentStatusMedia = true;
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

				pauseForDargging();

				eventManager.fireMouseDragEvent(new MouseDragEvent(
						MouseDragEvent.MOUSE_SET_DRAG_STATUS,
						currentDragStatus, this));

			} else if ((e.detail == 0)
					&& (previousEventSliderPreview == SWT.DRAG)) {
				// store current event
				previousEventSliderPreview = e.detail;

				resumeAfterDragging();

				eventManager.fireMouseDragEvent(new MouseDragEvent(
						MouseDragEvent.MOUSE_SET_DRAG_STATUS,
						currentDragStatus, this));

				// check stop flag & end process
				if (stopExpandMaxSlider) {
					// reset stop flag
					stopExpandMaxSlider = false;
					// Update slider's position of own view
					setLocationPreviewSlider(ps.getMaximum(),
							storeCurrentTimeLineSlider);
				}

				WebBrowserFactory.getInstance().showCurrentImage();

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

				// // Synchronize all TimeLine
				eventManager.fireSyncTimeEvent(new SyncTimeEvent(
						currentLocation, this));

				WebBrowserFactory.getInstance().setCurrentPosition(
						currentLocation);
			}
		}
	}

	void pauseForDargging() {
		if (currentDragStatus == true) {
			return;
		}
		currentDragStatus = true;
		saveCurrentActionStatus = currentActionStatus;
		if (saveCurrentActionStatus != 0) { // (0:Idle(Stop/Pause), 1:Play)
			playPauseMedia(); // pause media
		}
	}

	void resumeAfterDragging() {
		if (currentDragStatus == false) {
			return;
		}
		if (saveCurrentActionStatus != 0) { // (0:Idle(Stop/Pause), 1:Play)
			playPauseMedia(); // restart playing media
		}
		currentDragStatus = false;
	}

	public void handleSyncTimeEvent(SyncTimeEvent e) {
		// Synchronize TimeLine view
		if (e.getEventType() == SyncTimeEvent.SYNCHRONIZE_TIME_LINE) {
			synchronizeTimeLine(e.getCurrentTime());
		}
	}

	public void handleMouseDragEvent(MouseDragEvent e) {
		//
		switch (e.getEventType()) {
		case MouseDragEvent.MOUSE_DRAG_START:
			pauseForDargging();
			break;
		case MouseDragEvent.MOUSE_SET_DRAG_STATUS:
			this.currentDragStatus = e.isStatus();
			break;
		case MouseDragEvent.MOUSE_DRAG_END:
			resumeAfterDragging();
			break;
		}
	}

	public void handlePlayPauseEvent(PlayerControlEvent e) {
		playPauseMedia();
	}

}
