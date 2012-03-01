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

import org.eclipse.actf.ai.internal.ui.scripteditor.event.EventManager;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.MouseDragEvent;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.SyncTimeEvent;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.SyncTimeEventListener;
import org.eclipse.actf.ai.scripteditor.util.WebBrowserFactory;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.actf.examples.scripteditor.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class TimeLineCanvas extends Canvas implements IUNIT,
		SyncTimeEventListener {

	// instance of own class
	static private TimeLineCanvas ownInst = null;

	// parent view info.
	private TimeLineView instParentView;

	// Process Status flag
	// 0:Idle
	// 1:Play movie
	private int currentProcStatus = 0;

	// Off Image
	private Image offImage = null;

	// current time of TimeLine
	private int currentTime = 0;
	// private int previousTime = 0;
	private int maxTime = 0;

	// current location of TimeLine
	private int currentTimeLineLocation = 0;

	// Marker image of TimeLine
	static final private Image imgMarkerTimeLine = Activator
			.getImageDescriptor("/icons/marker.gif").createImage();
	private Image imgMarkerAlpha = null;
	private int markerWidth = 0;
	// private int markerHeight = 0;

	// mouse drag control for TimeLine
	private boolean statusMouseDragged = false;
	// for mouse drag action
	private long timePushMouseLButton = 0;
	private boolean currentDragStatus = false;

	private static EventManager eventManager = null;

	/**
	 * @category Constructor
	 */
	public TimeLineCanvas(Composite parent) {
		super(parent, SWT.NONE);

		// store own instance
		ownInst = this;

		// Initialize Canvas & Create Graphics Context.
		initializeCanvas(parent);

		// Store TimeLine view instance
		instParentView = TimeLineView.getInstance();
		// store event lister
		eventManager = EventManager.getInstance();
		// Add synchronized TimeEvent Listener
		eventManager.addSyncTimeEventListener(this);
		parent.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				// TODO other components
				eventManager.removeSyncTimeEventListener(ownInst);
			}
		});
	}

	static public TimeLineCanvas getInstance(Composite parent) {

		// 1st check current Instance
		if (ownInst == null) {
			synchronized (TimeLineCanvas.class) {
				// 2nd check current instance
				if (ownInst == null) {
					// New own class at once
					ownInst = new TimeLineCanvas(parent);
				}
			}
		}
		// return current Instance of VoluemLevel Canvas
		return (ownInst);
	}

	static public TimeLineCanvas getInstance() {
		// return current Instance of VoluemLevel Canvas
		return (ownInst);
	}

	// Initialize for Canvas class & object
	private void initializeCanvas(Composite parent) {
		try {
			// next status : Idle mode
			currentProcStatus = 0;

			// SetUP alpha blend to marker of TimeLine
			Image temp = Activator.getImageDescriptor("/icons/marker.bmp")
					.createImage();
			ImageData imgData = temp.getImageData();
			imgData.alpha = 70;
			imgMarkerAlpha = new Image(getDisplay(), imgData);

			// PickUP size marker of TimeLine
			Rectangle rect = imgMarkerTimeLine.getBounds();
			markerWidth = rect.width - 1;
			// markerHeight = rect.height - 1;

			// Add PaintListener for repaint event
			addPaintListener(new CanvasPaintListener());

			// MakeUP MouseListener for Adjusting TimeLine position
			addMouseListener(new TimeLineMouseDragAdapter());

			// SetUP MouseTrackListener for Mouse Cursor type
			addMouseTrackListener(new TimeLineMouseTrackAdapter());

			// SetUP MouseMoveListener
			addMouseMoveListener(new TimeLineMouseMoveListener());

			// SetUP ToolTip for mouse cursor
			displayTimeLineToolTip(0);

		} catch (Exception ef) {
			System.out.println("initializeCanvas() : Exception = " + ef);
		}
	}

	/**
	 * @category Setter Method
	 * @purpose Synchronized Time Line
	 */
	public void synchronizeTimeLine(int nowTime) {
		// Redraw marker on TimeLIne
		updatePositionMarkerTimeLine(nowTime);
	}

	/**
	 * Setter method : Set current location of TimeLine
	 */
	public void refreshTimeLine(int nowCnt) {
		// Update counter of location of TimeLine
		currentTimeLineLocation = nowCnt;

		// Repaint Canvas
		setStatusCanvasTimeLine(1);
		this.redraw();
	}

	/**
	 * Local method : SetUP & Display tool tip of TimeLine
	 */
	private void displayTimeLineToolTip(int nowPosition) {
		// Calculate time from now cursor position
		int nowTime = (nowPosition * TIME2PIXEL)
				+ (currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE);
		if (nowTime < 0)
			nowTime = 0;

		// MakeUP string of TimeLine
		String strToolTip = "TimeLine : " + makeFormatMMSSMS(nowTime) + " ";

		// Update ToolTip
		ownInst.setToolTipText(strToolTip);
	}

	/**
	 * @category Local method : Make String of TimeLine
	 * @param totalSec
	 * @return
	 */
	private String makeFormatMMSSMS(int totalSec) {

		String formTime = new String();
		Integer mm;
		Integer ss;
		Integer ms;

		// Make format "MM:SS.MS"
		if (totalSec <= 0) {
			// default
			formTime = "00:00.00";
		} else {
			// Integer to String
			mm = (totalSec / 1000) / 60;
			ss = (totalSec / 1000) % 60;
			ms = totalSec % 1000;
			formTime = "";

			// MM
			if (mm < 10)
				formTime = formTime + "0";
			formTime = formTime + mm.toString();
			// separator
			formTime += ":";
			// SS
			if (ss < 10)
				formTime = formTime + "0";
			formTime = formTime + ss.toString();
			// separator
			formTime += ".";
			// millisecond
			Integer ms100 = ms / 10;
			if (ms < 10)
				formTime = formTime + "00";
			else if ((ms < 100) && (ms >= 10))
				formTime = formTime + "0" + ms.toString();
			else
				formTime = formTime + ms100.toString();
		}

		// return String data
		return (formTime);
	}

	/**
	 * Update marker position of TimeLine
	 */
	private void updatePositionMarkerTimeLine(int currentTime) {

		// Store current time(adjust scale refresh)
		// previousTime = this.currentTime;
		this.currentTime = currentTime
				- (currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE);
		if (this.currentTime < 0)
			this.currentTime = 0;

		// Change mode & Request repaint Canvas
		// ////setStatusCanvasTimeLine(2);
		this.redraw();
	}

	/**
	 * Getter method : Get current time line marker X position
	 */
	public int getCurrentPositionMarkerTimeLine() {
		// return current position of time line marker X position
		return (currentTime + (currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE));
	}

	/**
	 * Initialize marker position of TimeLine
	 */
	public void initPositionMarkerTimeLine() {
		// Initialize time parameters
		// previousTime = 0;
		currentTime = 0;

		// Initialize mode & Request repaint Canvas
		setStatusCanvasTimeLine(1);
		this.redraw();
	}

	/**
	 * Adjust max size of TimeLine
	 */
	public void adjustMaxSizeTimeLine(int maxTime) {
		// Adjust max time parameters
		this.maxTime = maxTime;

		// Adjust size mode & Request repaint Canvas
		setStatusCanvasTimeLine(3);
		this.redraw();
	}

	/**
	 * Setter methods : Set Status to internal flag
	 */
	public void setStatusCanvasTimeLine(int nextStatus) {
		// Store next status
		currentProcStatus = nextStatus;
	}

	/**
	 * Draw marker of TimeLine on Canvas
	 */
	private void drawMarkerTimeLine(PaintEvent e) {
		// MakeUP new & previous position as pixel
		int newX = currentTime / TIME2PIXEL;

		// draw marker image as new position
		e.gc.drawImage(imgMarkerAlpha, (newX - (markerWidth >> 1)), 1);
		e.gc.drawImage(imgMarkerTimeLine, (newX - (markerWidth >> 1)), 1);
	}

	/**
	 * @category Local method : Initialize Canvas of TimeLine
	 * 
	 */
	private void initCanvasTimeLine(PaintEvent e) {
		// PickUP current size of Canvas
		Point nowCanvas = instParentView.getSizeParentSC();

		// PickUP TimeLine info.
		int startTimeLine = currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE;
		if (startTimeLine < 0)
			startTimeLine = 0;

		// Exchange from Time data to Scale data(pixel)
		Point newCanvas = new Point(TL_DEF_ETIME / TIME2PIXEL, nowCanvas.y);

		// Initialize off image & Create work Graphics Context
		if (offImage != null) {
			offImage.dispose();
			offImage = null;
		}
		offImage = new Image(getDisplay(), newCanvas.x, newCanvas.y);
		// Get Graphic context from off-image
		GC wgc = new GC(offImage);

		// Clear current Canvas
		wgc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));

		// Draw Scaler by current TimeLine
		wgc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		wgc.drawRectangle(0, 0, newCanvas.x, newCanvas.y);

		// Initial SetUP Time text
		int nextTimeCount = 0;
		int timeWidth = 18;
		wgc.drawString(this.makeFormatMMSSMS(startTimeLine), -timeWidth, 5);
		for (int i = 0; i <= newCanvas.x; i += TL_DEF_SCALESIZE) {
			for (int j = 0; j < TL_DEF_SCALESIZE; j += 5) {
				// draw scale line
				wgc.drawLine(i + j, 0, i + j, 2);
			}
			// draw scale line
			wgc.drawLine(i + TL_DEF_SCALESIZE, 0, i + TL_DEF_SCALESIZE, 5);

			// Update title counter
			nextTimeCount++;
			if ((nextTimeCount % 2) == 0) {
				// Draw Time Text by current TimeLine
				int nowTime = startTimeLine + (nextTimeCount * MSEC);
				wgc.drawString(this.makeFormatMMSSMS(nowTime), i
						+ TL_DEF_SCALESIZE - timeWidth, 5);
			}
		}

		// Initial draw TimeLine
		e.gc.drawImage(offImage, e.x, e.y, e.width, e.height, e.x, e.y,
				e.width, e.height);

		// CleanUP work GC
		wgc.dispose();

		// Update Marker of TimeLine
		drawMarkerTimeLine(e);

	}

	/**
	 * @category Local method : Adjust max size of Canvas of TimeLine
	 * 
	 */
	private void adjustSizeCanvasTimeLine(PaintEvent e) {
		// PickUP TimeLine info.
		int startTimeLine = instParentView.getStartTimeLine();
		int endTimeLine = instParentView.getMaxTimeLine();

		// Check max size & adjust parameter
		if (maxTime > endTimeLine) {
			// Reset current max size(may be, size of blue label)
			endTimeLine = maxTime;
		}

		// Exchange from Time data to Scale data(pixel)
		Canvas src = (Canvas) e.getSource();
		Point nowCanvas = new Point(endTimeLine / TIME2PIXEL, src.getSize().y);
		// Resize scaler
		if (e.width > nowCanvas.x) {
			nowCanvas = null;
			nowCanvas = src.getSize();
		}

		// Initialize off image & Create work Graphics Context
		if (offImage != null)
			offImage.dispose();
		offImage = new Image(getDisplay(), nowCanvas.x, nowCanvas.y);
		GC wgc = new GC(offImage);

		// Clear current Canvas
		wgc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));

		// Draw Scaler by current TimeLine
		wgc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		wgc.drawRectangle(0, 0, nowCanvas.x, nowCanvas.y);

		// Initial SetUP Time text
		int nextTimeCount = 0;
		int timeWidth = 18;
		wgc.drawString(this.makeFormatMMSSMS(startTimeLine), -timeWidth, 5);
		for (int i = 0; i <= nowCanvas.x; i += TL_DEF_SCALESIZE) {
			for (int j = 0; j < TL_DEF_SCALESIZE; j += 5) {
				// draw scale line
				wgc.drawLine(i + j, 0, i + j, 2);
			}
			// draw scale line
			wgc.drawLine(i + TL_DEF_SCALESIZE, 0, i + TL_DEF_SCALESIZE, 5);

			// Update title counter
			nextTimeCount++;
			if ((nextTimeCount % 2) == 0) {
				// Draw Time Text by current TimeLine
				int nowTime = (i + TL_DEF_SCALESIZE) * TIME2PIXEL;
				wgc.drawString(this.makeFormatMMSSMS(nowTime), i
						+ TL_DEF_SCALESIZE - timeWidth, 5);
			}
		}

		// Initial draw TimeLine
		e.gc.drawImage(offImage, e.x, e.y, e.width, e.height, e.x, e.y,
				e.width, e.height);

		// CleanUP work GC
		wgc.dispose();

		// Update Marker of TimeLine
		drawMarkerTimeLine(e);

	}

	/**
	 * Redraw Canvas (update position marker of TimeLine)
	 */
	private void redrawCanvasTimeLine(PaintEvent e) {

		// Initial draw TimeLine
		if (offImage != null) {
			e.gc.drawImage(offImage, e.x, e.y, e.width, e.height, e.x, e.y,
					e.width, e.height);

			// Draw marker of TimeLine
			drawMarkerTimeLine(e);
		}
	}

	/**
	 * PaintListener
	 */
	class CanvasPaintListener implements PaintListener {

		// Override paintControl()
		public void paintControl(PaintEvent e) {
			// null check
			if (offImage == null) {
				// re-initialize off image buffer
				currentProcStatus = 1;
			}

			// Check current status for request function
			// Status=1 : Clear Canvas
			if (currentProcStatus == 1) {
				// Clear Canvas
				initCanvasTimeLine(e);
				// Use off image buffer next time
				currentProcStatus = 2;
			}
			// Status=2 : Refresh Canvas by current time line
			else if (currentProcStatus == 2) {
				// Repaint Canvas(= Redraw marker of TimeLine)
				redrawCanvasTimeLine(e);
			}
			// Status=3 : Adjust max size of Canvas
			else if (currentProcStatus == 3) {
				// Clear Canvas
				adjustSizeCanvasTimeLine(e);
				// Use off image buffer next time
				currentProcStatus = 2;
			}

			// release GC resource
			e.gc.dispose();
		}
	}

	/**
	 * Local Class extends MouseTrackAdapter for TimeLine
	 */
	class TimeLineMouseTrackAdapter extends MouseTrackAdapter {
		// mouse cursor enter into parent area
		public void mouseEnter(MouseEvent e) {
			// Changer Cursor image from ARROW type to HAND type
			Canvas parentCanvas = (Canvas) e.getSource();
			parentCanvas.setCursor(new Cursor(null, SWT.CURSOR_HAND));
		}

		// mouse cursor exit parent area
		public void mouseExit(MouseEvent e) {
			// Reset Cursor image to default type (ARROW)
			Canvas parentCanvas = (Canvas) e.getSource();
			parentCanvas.setCursor(new Cursor(null, SWT.CURSOR_ARROW));
		}
	}

	/**
	 * Local method : Pre check Mouse drag event
	 */
	private void precheckMouseDraggedEvent(MouseEvent e) {
		// Get current System time
		timePushMouseLButton = e.time;
		// Drag start
		currentDragStatus = true;
	}

	/**
	 * Local method : Move Mouse drag position
	 */
	private void moveMouseDraggedEvent(MouseEvent e) {
		// Calculate current time line
		int nowTime = (e.x * TIME2PIXEL)
				+ (currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE);
		int maxTime = instParentView.getMaxTimeLine();

		// check min limit of time line
		if (nowTime < 0) {
			// adjust minimum value of time line
			nowTime = 0;
		}
		// check max limit of time line
		else if (nowTime > maxTime) {
			// adjust maximum value of time line
			nowTime = instParentView.getMaxTimeLine();
		}

		// SetUP new location to current TimeLine
		instParentView.reqSetTrackCurrentTimeLine(nowTime);

		// Synchronize all TimeLine
		eventManager.fireSyncTimeEvent(new SyncTimeEvent(nowTime, this));

		WebBrowserFactory.getInstance().setCurrentPosition(nowTime);

	}

	/**
	 * Local Class extends MouseTrackAdapter for Button, Composite
	 */
	class TimeLineMouseDragAdapter extends MouseAdapter {

		// Mouse event : Clicked
		public void mouseDown(MouseEvent e) {
			// Check mouse 'Left' button
			if (!statusMouseDragged && (e.button == 1)) {
				// pre-check Mouse drag action
				precheckMouseDraggedEvent(e);

				eventManager.fireMouseDragEvent(new MouseDragEvent(
						MouseDragEvent.MOUSE_DRAG_START, this));
			}
		}

		// Mouse event : Purge
		public void mouseUp(MouseEvent e) {
			// Check mouse 'Left' button
			if (!statusMouseDragged && (e.button == 1) && currentDragStatus) {
				// Get current System time
				long timeReleaseMouseLButton = e.time;
				// Check short single click action
				if ((timeReleaseMouseLButton - timePushMouseLButton) < TL_SINGLE_CLICK_TIME) {
					// goto single click button action
					moveMouseDraggedEvent(e);
				}
				// Reset status flag
				currentDragStatus = false;

				eventManager.fireMouseDragEvent(new MouseDragEvent(
						MouseDragEvent.MOUSE_SET_DRAG_STATUS,
						currentDragStatus, this));
			}
			// Check mouse 'Left' button
			else if (statusMouseDragged && (e.button == 1)) {
				// Finish Mouse drag action
				statusMouseDragged = false;
				eventManager.fireMouseDragEvent(new MouseDragEvent(
						MouseDragEvent.MOUSE_DRAG_END, this));
			}
			WebBrowserFactory.getInstance().showCurrentImage();
		}
	}

	/**
	 * Local Class extends MouseMoveListener for mouse cursor
	 */
	class TimeLineMouseMoveListener implements MouseMoveListener {
		// Mouse event : Drag control or Update ToolTip of TimeLine(current
		// time)
		public void mouseMove(MouseEvent e) {
			// Check mouse 'Left' button
			if (statusMouseDragged) {
				// Move marker image of TimeLine
				moveMouseDraggedEvent(e);
			}
			// Check mouse 'Left' button
			else if (!statusMouseDragged && currentDragStatus) {
				// Start Mouse drag action
				statusMouseDragged = true;
				currentDragStatus = false;
			}

			// Update ToolTip of TimeLine
			displayTimeLineToolTip(e.x);
		}
	}

	public void handleSyncTimeEvent(SyncTimeEvent e) {
		// Synchronize TimeLine view
		if (e.getEventType() == SyncTimeEvent.SYNCHRONIZE_TIME_LINE) {
			synchronizeTimeLine(e.getCurrentTime());
		} else if (e.getEventType() == SyncTimeEvent.REFRESH_TIME_LINE) {
			refreshTimeLine(e.getCurrentTime());
		}
	}

}
