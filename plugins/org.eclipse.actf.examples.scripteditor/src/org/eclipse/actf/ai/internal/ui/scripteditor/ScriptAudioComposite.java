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

import java.util.ArrayList;

import org.eclipse.actf.ai.scripteditor.data.ScriptData;
import org.eclipse.actf.ai.ui.scripteditor.views.EditPanelView;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.actf.examples.scripteditor.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ScriptAudioComposite extends Composite implements IUNIT {

	// Local class
	class AudioLabelInfo {
		// Local data
		private int index = -1;
		private int startTime = 0;
		private int endTime = 0;
		private String strAudioLabel = null;
		private boolean extended = false;
		private int color = SWT.COLOR_GREEN;

		// Constructor
		public AudioLabelInfo(int index, int startTime, int endTime,
				String strAudioLabel, boolean extended, int color) {
			// Store current data
			this.index = index;
			this.startTime = startTime;
			this.endTime = endTime;
			this.strAudioLabel = new String(strAudioLabel);
			this.extended = extended;
			this.color = color;
		}

		/**
		 * @category Getter methods
		 */
		public int getIndex() {
			return (this.index);
		}

		public int getStartTime() {
			return (this.startTime);
		}

		public int getEndTime() {
			return (this.endTime);
		}

		public String getStrAudioLabel() {
			return (this.strAudioLabel);
		}

		public boolean getExtended() {
			return (this.extended);
		}

		public int getColor() {
			return (this.color);
		}

		/**
		 * @category Setter methods
		 */
		public void setIndex(int index) {
			this.index = index;
		}

		public void setStartTime(int startTime) {
			this.startTime = startTime;
		}

		public void setEndTime(int endTime) {
			this.endTime = endTime;
		}

		public void setStrAudioLabel(String strAudioLabel) {
			this.strAudioLabel = strAudioLabel;
		}

		public void setExtended(boolean extended) {
			this.extended = extended;
		}

		public void setColor(int color) {
			this.color = color;
		}
	}

	// instance of own class
	static private ScriptAudioComposite ownInst = null;
	private ScriptData instScriptData = null;

	// current location of TimeLine
	private int currentTimeLineLocation = 0;

	// static data for Mouse event
	private boolean statusMouseDragged = false;
	private boolean execDataConvMouseDragged = false;
	private int startTimeMouseDragged = -1;
	private String strAudioMouseDragged = null;
	private int startXMouseDragged = 0;
	private int previousXMouseDragged = 0;
	private int deltaMouseDragged = 0;

	// private label object
	private Label dragAudioLabel;
	private Label labelBorderTimeLine;

	// parent view info.
	private TimeLineView instParentView;

	// Audio Label data List
	private ArrayList<AudioLabelInfo> audioLabelInfo;
	private ArrayList<Integer> audioStartTimeList;
	private ArrayList<Label> audioLabelList;

	// for mouse drag action
	private Composite parentInst = null;
	private int minXDragTimeLine = 0;
	private int maxXDragTimeLine = 0;
	private int currentWidthTimeLine = 0;
	private long timePushMouseLButton = 0;
	private boolean currentDragStatus = false;
	private boolean editMode = false;

	// for Label duplicated setting
	private boolean stat_duplicate_label = true;

	/**
	 * @category Constructor
	 */
	public ScriptAudioComposite(Composite parent) {
		super(parent, SWT.BORDER);

		// store parent instance
		parentInst = parent;
		// store own instance
		ownInst = this;

		// Initialize Composite
		initializeComposite(parent);

		// Store TimeLine view instance
		instParentView = TimeLineView.getInstance();
		instScriptData = ScriptData.getInstance();
	}

	static public ScriptAudioComposite getInstance(Composite parent) {

		// 1st check current Instance
		if (ownInst == null) {
			synchronized (ScriptAudioComposite.class) {
				// 2nd check current instance
				if (ownInst == null) {
					// New own class at once
					ownInst = new ScriptAudioComposite(parent);
				}
			}
		}
		// return current Instance of Script Audio Composite
		return (ownInst);
	}

	static public ScriptAudioComposite getInstance() {
		// return current Instance of Script Audio Composite
		return (ownInst);
	}

	// Initialize for Canvas class & object
	private void initializeComposite(Composite parent) {
		try {
			// Create HashMap for Audio Label List
			audioLabelInfo = new ArrayList<AudioLabelInfo>();
			audioStartTimeList = new ArrayList<Integer>();
			audioLabelList = new ArrayList<Label>();

			// Initialize border image of TimeLine (1st priority in own
			// Composite)
			labelBorderTimeLine = new Label(ownInst, SWT.NONE);
			// Set background color
			labelBorderTimeLine.setBackground(getDisplay().getSystemColor(
					SWT.COLOR_BLUE));
			// Initial location = (0, 0)
			setLocationBorderTimeLine(0);

			// Create new Label (2nd priority in own Composite)
			dragAudioLabel = new Label((Composite) ownInst, SWT.TOP);
			setVisibleDragAudioLabel(false);

		} catch (Exception ef) {
			System.out.println("Exception : Front processer=" + ef);
		}
	}

	/**
	 * Setter method : Set visible Audio Label for Mouse Drag action
	 */
	private void setVisibleDragAudioLabel(boolean stat) {
		// Set visible Audio Label for Mouse Drag action
		dragAudioLabel.setVisible(stat);
	}

	public void clearAudioLabel() {
		// Dispose all Label on Composite
		for (int i = 0; i < audioStartTimeList.size(); i++) {
			// Delete current Label
			audioLabelList.get(i).dispose();
		}

		// Clear all AudioLabel List
		audioLabelInfo.clear();
		audioStartTimeList.clear();
		audioLabelList.clear();
	}

	// Search target data by index
	private int searchScriptData(int startTime) {
		// if index is '-1', then no data
		int index;

		// search start time from current ScriptList
		index = audioStartTimeList.indexOf(startTime);

		// return index of target Script data
		return (index);
	}

	// Search new data index
	private int indexScriptData(int startTime) {
		// if index is '-1', then no data
		int index = -1;

		// ScriptList empty is always 1st index
		if (audioStartTimeList.isEmpty()) {
			index = 0;
		} else {
			// search start time from current ScriptList
			int i;
			for (i = 0; i < audioStartTimeList.size(); i++) {
				// exist data?
				if (startTime < audioStartTimeList.get(i)) {
					// exist data.
					break;
				}
			}
			// update index
			index = i;
		}

		// return index of target Script data
		return (index);
	}

	private void setLocationBorderTimeLine(int x) {
		// calculate start X position
		int sx = x - 1;
		if (sx < 0)
			sx = 0;
		// Update Layout parameters for resize parent Composite
		FormData labelBorderLData = new FormData();
		labelBorderLData.width = 2;
		labelBorderLData.top = new FormAttachment(0, 1000, 0);
		labelBorderLData.left = new FormAttachment(0, 1000, sx);
		labelBorderLData.bottom = new FormAttachment(1000, 1000, 0);
		labelBorderTimeLine.setLayoutData(labelBorderLData);

		// Update location for border of TimeLine as Pixel
		labelBorderTimeLine.setLocation(sx, 0);
	}

	/**
	 * Refresh all Script Audio Label from current ScriptList
	 */
	public void refreshScriptAudio() {
		// Clear current all Audio Label List
		clearAudioLabel();

		// Check current length of ScriptList
		int len = instScriptData.getLengthScriptList();
		if (len > 0) {
			int startTime = 0;
			int endTime = 0;
			for (int i = 0; i < len; i++) {
				// SetUP parameters
				startTime = instScriptData.getScriptStartTime(i);
				String strAudio = instScriptData.getScriptData(i);
				boolean extended = instScriptData.getExtendExtended(i);
				// Check WAV status
				int index = instScriptData.getIndexWavList(startTime);
				if ((index >= 0) && instScriptData.getEnableWavList(index)) {
					// Use end time of WAV information
					endTime = instScriptData.getEndTimeWavList(index);
				} else {
					// Use end time of voice engine
					endTime = instScriptData.getScriptEndTime(i);
				}

				// Refresh all Script Audio Label from current ScriptList
				putAudioLabel(i, startTime, endTime, strAudio, extended);
			}
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

		// Redraw own Composite of audio label
		refreshScriptAudio();
	}

	/**
	 * Local method : Create new Audio Label
	 */
	private void createMouseDragLabel(int startTime, int endTime,
			String strAudio) {

		// Calculate target Label position & size
		int sX = (startTime - (currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE))
				/ TIME2PIXEL;
		int eX = (endTime - (currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE))
				/ TIME2PIXEL;
		int sY = TL_AUDIO1_MDRAG_SY;
		int size = eX - sX;

		// Get rectangle of parent label
		int index = audioStartTimeList.indexOf(startTime);
		if (index >= 0) {
			Rectangle parentRect = audioLabelList.get(index).getBounds();
			sY = (parentRect.y - TL_AUDIO1_SY) + TL_AUDIO1_MDRAG_SY;
		}

		// Adjust Label width
		if (size < TL_MIN_SCALESIZE) {
			size = TL_MIN_SCALESIZE;
			eX = sX + size;
		}

		// Create new Label
		// /// dragAudioLabel = new Label((Composite) ownInst, SWT.TOP);
		// SetUP MouseTrackListener
		dragAudioLabel
				.addMouseTrackListener(new LabelMouseCursorTrackAdapter());
		// Create ToolTip
		dragAudioLabel.setToolTipText(makeToolTipInfo(startTime, endTime,
				strAudio));

		// Layout new Label
		dragAudioLabel.setBounds(sX, sY, size, TL_LABEL_MDRAG_HEIGHT);
		dragAudioLabel.setBackground(getDisplay().getSystemColor(
				SWT.COLOR_YELLOW));

		// Set visible blue label
		setVisibleDragAudioLabel(true);
	}

	/**
	 * Local method : Move drag mouse
	 */
	private int moveMouseDragLabel(int startTime, int endTime, int deltaX,
			String strAudio) {

		// Result code(adjust delta X Position)
		int newDeltaX = deltaX;

		// Calculate new TimeLine
		int newStartTime = startTime + (deltaX * TIME2PIXEL);
		int newEndTime = endTime + (deltaX * TIME2PIXEL);

		// Get current Point value
		Rectangle nowRect = dragAudioLabel.getBounds();

		// Calculate target Label position & size
		int sX = (newStartTime - (currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE))
				/ TIME2PIXEL;
		int eX = (newEndTime - (currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE))
				/ TIME2PIXEL;
		int sY = nowRect.y;
		int size = eX - sX;

		// Adjust blue Label width
		if (size < TL_MIN_SCALESIZE) {
			size = TL_MIN_SCALESIZE;
			eX = sX + size;
		}

		// Check minimum time
		if (newStartTime < 0) {
			// Adjust parameters
			newStartTime = 0;
			newEndTime = size * TIME2PIXEL;
			newDeltaX = -1 * (startTime / TIME2PIXEL);
			// Calculate target Label position & size
			sX = newStartTime / TIME2PIXEL;
			eX = newEndTime / TIME2PIXEL;
		}

		// Redraw blue Label
		dragAudioLabel.setBounds(sX, sY, size, TL_LABEL_MDRAG_HEIGHT);

		// Update ToolTip for Parent Label
		String newText = new String(makeToolTipInfo(newStartTime, newEndTime,
				strAudio));
		Label parentLabel = audioLabelList.get(audioStartTimeList
				.indexOf(startTimeMouseDragged));
		parentLabel.setToolTipText(newText);

		// Return result
		return (newDeltaX);
	}

	/**
	 * Local method : Create new Audio Label
	 */
	private void createAudioLabel(int index, int parentIndex, int startTime,
			int endTime, String strAudio, boolean extended, int color) {

		// Calculate target Label position & size
		int intdev = currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE;
		int sX = (startTime - intdev) / TIME2PIXEL;
		int eX = (endTime - intdev) / TIME2PIXEL;
		int sY = TL_AUDIO1_SY;
		int size = eX - sX;
		Color col = getDisplay().getSystemColor(color);

		// Adjust Label width
		if (size < TL_MIN_SCALESIZE) {
			size = TL_MIN_SCALESIZE;
			eX = sX + size;
		}

		// Create new Label
		Label newAudio = new Label((Composite) ownInst, SWT.BORDER);
		// Store instance of current Label
		audioLabelList.add(index, newAudio);

		/**********
		 * // Check Extended parameter if( extended ){ // Change Extended
		 * color(=YELLOW) col = getDisplay().getSystemColor(SWT.COLOR_BLUE); }
		 * // Check next Script data if ((index + 1) <
		 * audioStartTimeList.size()) { if (endTime >
		 * audioStartTimeList.get(index + 1)) { // Change warning color(= RED)
		 * col = getDisplay().getSystemColor(SWT.COLOR_RED); } }
		 ***********/

		// Adjust label's height by duplicated setting
		if (getStatDuplicateLabel()) {
			// Check own label's color
			if (color == SWT.COLOR_RED) {
				// PickUP own line no.
				int line = getOwnLineAudioLabel(index);
				// Adjust own height
				sY = sY + (line * TL_AUDIO1_MDRAG_SY);
			}
		}

		// Layout Audio Label
		FormData newAudioLData = new FormData();
		newAudioLData.width = size;
		newAudioLData.height = TL_LABEL_HEIGHT;
		newAudioLData.top = new FormAttachment(0, 1000, sY);
		newAudioLData.left = new FormAttachment(0, 1000, sX);
		newAudio.setLayoutData(newAudioLData);
		newAudio.setBounds(sX, sY, size, TL_LABEL_HEIGHT);
		newAudio.setBackground(col);
		newAudio.setForeground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		// Create ToolTip
		newAudio.setToolTipText(makeToolTipInfo(startTime, endTime, strAudio));

		// SetUP label's image for WAV enabled
		int wavIndex = instScriptData.getIndexWavList(startTime);
		// SetUP label's text for Extended
		if (extended) {
			// Check enable status
			if ((wavIndex >= 0) && (instScriptData.getEnableWavList(wavIndex))) {
				// Display 'WAV' image
				Image imgWavOn = Activator.getImageDescriptor(
						"/icons/wave_bar_ex.gif").createImage();
				newAudio.setImage(imgWavOn);
			} else {
				// Display 'Extended' text
				newAudio.setText(" Extended ");
			}
		} else {
			// Check enable status
			if ((wavIndex >= 0) && (instScriptData.getEnableWavList(wavIndex))) {
				// Display 'WAV' image
				Image imgWavOn = Activator.getImageDescriptor(
						"/icons/wave_bar.gif").createImage();
				newAudio.setImage(imgWavOn);
			} else {
				// Display blank text
				newAudio.setText("");
			}
		}

		// MakeUP MouseListener & MouseMoveListener
		newAudio.addMouseListener(new AudioLabelMouseAdapter(startTime));
		newAudio
				.addMouseMoveListener(new AudioLabelMouseMoveListener(startTime));
		// SetUP MouseTrackListener
		newAudio.addMouseTrackListener(new LabelMouseCursorTrackAdapter());
	}

	/**
	 * Local method : Create new Audio Label
	 */
	private void updateAudioLabel(int index, int startTime, int endTime,
			String strAudio, boolean extended, int color) {

		// Calculate target Label position & size
		int intdev = currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE;
		int sX = (startTime - intdev) / TIME2PIXEL;
		int eX = (endTime - intdev) / TIME2PIXEL;
		int sY = TL_AUDIO1_SY;
		int size = eX - sX;
		Color col = getDisplay().getSystemColor(color);

		// Adjust Label width
		if (size < TL_MIN_SCALESIZE) {
			size = TL_MIN_SCALESIZE;
			eX = sX + size;
		}

		// Update target Label
		Label targetLabel = audioLabelList.get(index);

		/*************
		 * // Check Extended parameter if( extended ){ // Change Extended
		 * color(=YELLOW) col = getDisplay().getSystemColor(SWT.COLOR_BLUE); }
		 * // Check previous Script data if (index > 0) { // PickUP previous
		 * AudioLabel info. AudioLabelInfo previousInfo =
		 * audioLabelInfo.get(index - 1); if (startTime <=
		 * previousInfo.getEndTime()) { // Change warning color(= RED) col =
		 * getDisplay().getSystemColor(SWT.COLOR_RED); } } // Check next Script
		 * data else if ((index + 1) < audioStartTimeList.size()) { // PickUP
		 * next AudioLabel info. AudioLabelInfo nextInfo =
		 * audioLabelInfo.get(index + 1); if (endTime > nextInfo.getStartTime())
		 * { // Change warning color(= RED) col =
		 * getDisplay().getSystemColor(SWT.COLOR_RED); } }
		 ***************/

		// Adjust label's height by duplicated setting
		if (getStatDuplicateLabel()) {
			// Check own label's color
			if (color == SWT.COLOR_RED) {
				// PickUP own line no.
				int line = getOwnLineAudioLabel(index);
				// Adjust own height
				sY = sY + (line * TL_AUDIO1_MDRAG_SY);
			}
		}

		// SetUP label's image for WAV enabled
		int wavIndex = instScriptData.getIndexWavList(startTime);
		// SetUP label's text for Extended
		if (extended) {
			// Check enable status
			if ((wavIndex >= 0) && (instScriptData.getEnableWavList(wavIndex))) {
				// Display 'WAV' image
				Image imgWavOn = Activator.getImageDescriptor(
						"/icons/wave_bar_ex.gif").createImage();
				targetLabel.setImage(imgWavOn);
			} else {
				// Display 'Extended' text
				targetLabel.setText(" Extended ");
			}
		} else {
			// Check enable status
			if ((wavIndex >= 0) && (instScriptData.getEnableWavList(wavIndex))) {
				// Display 'WAV' image
				Image imgWavOn = Activator.getImageDescriptor(
						"/icons/wave_bar.gif").createImage();
				targetLabel.setImage(imgWavOn);
			} else {
				// Display blank text
				targetLabel.setText("");
			}
		}

		// Redraw Audio Label
		targetLabel.setBounds(sX, sY, size, TL_LABEL_HEIGHT);
		targetLabel.setBackground(col);

		// Update ToolTip
		targetLabel
				.setToolTipText(makeToolTipInfo(startTime, endTime, strAudio));
	}

	/**
	 * Get line no of own label
	 * 
	 * @param index
	 *            : own index of label list
	 * @return own line no
	 */
	private int getOwnLineAudioLabel(int index) {
		int line = 0;
		if (index > 0) {
			// Check duplicated setting
			if (getStatDuplicateLabel()) {
				// Check own color
				if (audioLabelInfo.get(index).getColor() == SWT.COLOR_RED) {
					// Search own position
					int startTime = audioLabelInfo.get(index).getStartTime();
					for (int i = (index - 1); i >= 0; i--) {
						// Check collision previous audio label
						int previousEndTime = audioLabelInfo.get(i)
								.getEndTime();
						if (startTime >= previousEndTime) {
							// End of duplicated lines
							break;
						}
						// Update line no.
						line++;
						startTime = audioLabelInfo.get(i).getStartTime();
					}
				}
			}
		}
		// return result
		return (line);
	}

	/**
	 * Redraw all audio labels for exchange color setting
	 * 
	 * @param indexs
	 *            : target label's list
	 */
	public void redrawAudioLabelAll() {
		// Store max line no.
		int maxLine = 0;
		// Check collision of all labels
		Integer collisions[] = getCollisionsTimeLine();
		if ((collisions != null) && (collisions.length > 0)) {
			// Redraw all audio label
			for (int i = 0; i < collisions.length; i++) {
				// PickUP target label's data
				int tempColor = audioLabelInfo.get(collisions[i]).getColor();
				Label tempLabel = audioLabelList.get(collisions[i]);
				Color newColor = getDisplay().getSystemColor(tempColor);

				// Re-adjust color for extended setting
				if (tempColor == SWT.COLOR_RED) {
					boolean extended = audioLabelInfo.get(collisions[i])
							.getExtended();
					if (extended) {
						// Check collision both own end time and next
						// description's start time
						if (collisions[i] < (instScriptData
								.getLengthScriptList() - 1)) {
							int ownEndTime = audioLabelInfo.get(collisions[i])
									.getEndTime();
							int nextStartTime = audioLabelInfo.get(
									collisions[i] + 1).getStartTime();
							if (ownEndTime > nextStartTime) {
								// and more collision check both own start time
								// and previous description's end time
								if (collisions[i] > 0) {
									extended = audioLabelInfo.get(
											collisions[i] - 1).getExtended();
									if (extended) {
										int ownStartTime = audioLabelInfo.get(
												collisions[i]).getStartTime();
										int previousEndTime = audioLabelInfo
												.get(collisions[i] - 1)
												.getEndTime();
										if (previousEndTime > ownStartTime) {
											// ignore collision(reset to
											// extended color)
											newColor = getDisplay()
													.getSystemColor(
															SWT.COLOR_BLUE);
										}
									}
								} else {
									// ignore collision(reset to extended color)
									newColor = getDisplay().getSystemColor(
											SWT.COLOR_BLUE);
								}
							}
						} else {
							// and more collision check both own start time and
							// previous description's end time
							if (collisions[i] > 0) {
								extended = audioLabelInfo
										.get(collisions[i] - 1).getExtended();
								if (extended) {
									int ownStartTime = audioLabelInfo.get(
											collisions[i]).getStartTime();
									int previousEndTime = audioLabelInfo.get(
											collisions[i] - 1).getEndTime();
									if (previousEndTime > ownStartTime) {
										// ignore collision(reset to extended
										// color)
										newColor = getDisplay().getSystemColor(
												SWT.COLOR_BLUE);
									}
								}
							}
						}
					} else {
						// Check collision both own end time and next
						// description's start time
						if (collisions[i] > 0) {
							int ownStartTime = audioLabelInfo
									.get(collisions[i]).getStartTime();
							int previousEndTime = audioLabelInfo.get(
									collisions[i] - 1).getEndTime();
							extended = audioLabelInfo.get(collisions[i] - 1)
									.getExtended();
							if (extended) {
								if (previousEndTime > ownStartTime) {
									// and more collision check both own end
									// time and next description's start time
									if (collisions[i] < (instScriptData
											.getLengthScriptList() - 1)) {
										int ownEndTime = audioLabelInfo.get(
												collisions[i]).getEndTime();
										int nextStartTime = audioLabelInfo.get(
												collisions[i] + 1)
												.getStartTime();
										if (ownEndTime <= nextStartTime) {
											// ignore collision(reset to normal
											// color)
											newColor = getDisplay()
													.getSystemColor(
															SWT.COLOR_GREEN);
										}
									} else {
										// ignore collision(reset to normal
										// color)
										newColor = getDisplay().getSystemColor(
												SWT.COLOR_GREEN);
									}
								}
							}
						}
					}
				}

				// Update color to target label
				tempLabel.setBackground(newColor);
				// Adjust height by current color(status)
				Rectangle nowRect = tempLabel.getBounds();
				int line = getOwnLineAudioLabel(collisions[i]) % TL_DEF_LINES;
				nowRect.y = TL_AUDIO1_SY + (line * TL_AUDIO1_MDRAG_SY);
				tempLabel.setBounds(nowRect);

				// Check max limit
				if (maxLine < line) {
					maxLine = line;
				}
			}

			// Adjust height of Time Line view
			if (++maxLine != instParentView.reqGetCurrentLineAudioLabel()) {
				// TODO
				// ///instParentView.reqAdjustLayoutTimeLine(maxLine);
			}

		}
	}

	/**
	 * Pickup new color of target audio label
	 * 
	 * @param startTime
	 *            : start time of target audio label
	 * @param endTime
	 *            : end time of target audio label
	 * @param extended
	 *            : extended status of target audio label
	 * @return new color id
	 */
	private int pickupColorAudioLabel(int startTime, int endTime,
			boolean extended) {
		int newCol = SWT.COLOR_GREEN;

		// Check collision on time line
		if (isCollisionTimeLine(startTime, endTime)) {
			// Set collision color to own label
			newCol = SWT.COLOR_RED;
		} else {
			// no detect collision on time line
			if (extended) {
				// Set extended color
				newCol = SWT.COLOR_BLUE;
			}
		}

		// return result
		return (newCol);
	}

	/**
	 * Check collision on time line
	 * 
	 * @param startTime
	 *            : start time of target audio label
	 * @param endTime
	 *            : end time of target audio label
	 * @return check result (TRUE:detect collision, FALSE:no collision)
	 */
	private boolean isCollisionTimeLine(int startTime, int endTime) {
		boolean result = false;
		int wrkStartTime = 0;
		int wrkEndTime = 0;

		if (!audioStartTimeList.isEmpty()) {
			// Get index of target audio label
			int index = audioStartTimeList.indexOf(startTime);
			if (index >= 0) {
				// Check collision time line by current script list
				if (index > 0) {
					// check collision both own label and previous label
					wrkEndTime = audioLabelInfo.get(index - 1).getEndTime();
					if (startTime < wrkEndTime) {
						// Detect collision
						result = true;
					}
				}
				if (index < (audioStartTimeList.size() - 1)) {
					// check collision both own label and next label
					wrkStartTime = audioStartTimeList.get(index + 1);
					if (endTime > wrkStartTime) {
						// Detect collision
						result = true;
					}
				}
			}
		}

		// return result
		return (result);
	}

	/**
	 * Get index of all collision labels on time line
	 * 
	 * @param startTime
	 *            : start time of target label
	 * @param endTime
	 *            : end time of target label
	 * @return buffer of collisions list
	 */
	private Integer[] getCollisionsTimeLine() {
		Integer[] result = null;
		ArrayList<Integer> collisions = new ArrayList<Integer>();

		// Check collision index of all audio labels
		if (!audioStartTimeList.isEmpty()) {
			// Get current list size
			int max = audioStartTimeList.size();
			for (int i = 0; i < max; i++) {
				// PickUP target data from current audio label list
				AudioLabelInfo tempList = audioLabelInfo.get(i);
				int startTime = tempList.getStartTime();
				int endTime = tempList.getEndTime();
				boolean extended = tempList.getExtended();
				int newCol = pickupColorAudioLabel(startTime, endTime, extended);

				// Check different color setting
				if ((newCol != tempList.getColor())
						|| (newCol == SWT.COLOR_RED)) {
					// Update color
					tempList.setColor(newCol);
					// Update audio label list
					audioLabelInfo.set(i, tempList);
					// Store target index to array list
					collisions.add(i);
				}
			}
		}
		// deep copy
		if (collisions != null) {
			result = new Integer[collisions.size()];
			for (int i = 0; i < collisions.size(); i++) {
				result[i] = collisions.get(i);
			}
		}
		// dispose resources
		collisions.clear();
		collisions = null;

		// return result
		return (result);
	}

	/**
	 * Local method : Make String of ToolTip for Audio Info.
	 */
	private String makeToolTipInfo(int startTime, int endTime, String strAudio) {
		String result = null;
		String strStartTime = null;
		String strEndTime = null;
		String strTrimAudio = null;

		// Convert from Integer to String
		strStartTime = makeFormatMMSSMS(startTime);
		strEndTime = makeFormatMMSSMS(endTime);
		// Trim data from AudioScript
		strTrimAudio = trimString(strAudio, '\r');
		// WAV file path
		boolean enableWav = false;
		int index = instScriptData.getIndexWavList(startTime);
		if (index >= 0) {
			enableWav = instScriptData.getEnableWavList(index);
		}

		// MakeUP new String of ToolTip
		result = "Start Time ： " + strStartTime + "\n" + "  End Time ： "
				+ strEndTime + "\n";

		// Append string from description or WAV file path
		if (enableWav) {
			// Use WAV file
			result = result + "  WAV File ： "
					+ instScriptData.getFileNameWavList(index).toString();
		} else {
			// Use voice engine
			result = result + "Desctiption： " + strTrimAudio;
		}

		// return String of ToolTip
		return (result);
	}

	/**
	 * Local method : Trim character from target String
	 */
	private String trimString(String targetString, char TrimData) {

		// initialize
		StringBuffer tempString = new StringBuffer(256);

		// Trim data
		for (int i = 0; i < targetString.length(); i++) {
			// Check Not trim data
			if (TrimData != targetString.charAt(i)) {
				// copy data to StringBuffer
				tempString.append(targetString.charAt(i));
			}
		}

		// return new String
		return (tempString.toString());
	}

	/**
	 * Getter method : Get current status of duplicated label setting
	 * 
	 * @return current setting (TRUE:Duplicated label mode, FALSE:Singleton
	 *         label mode)
	 */
	public boolean getStatDuplicateLabel() {
		// return result
		return (stat_duplicate_label);
	}

	/**
	 * Setter method : Set new status of duplicated label setting
	 * 
	 * @param newStat
	 *            : new status of Preference setting (TRUE:Duplicated label
	 *            mode, FALSE:Singleton label mode)
	 */
	public void setStatDuplicateLabel(boolean newStat) {
		// Update new status
		stat_duplicate_label = newStat;
	}

	/**
	 * Put on new Audio Label on Composite
	 */
	public boolean putAudioLabel(int index, int startTime, int endTime,
			String strAudio, boolean extended) {

		boolean result = true;
		int idx = -1;

		// Check exist target data from current List
		idx = searchScriptData(startTime);
		if (idx >= 0) {
			// exist data (update current script data)
			// *(caution)* : No need changed start time area, cause same date.
			audioStartTimeList.set(idx, startTime);
			AudioLabelInfo tempList = audioLabelInfo.get(idx);
			tempList.setIndex(index);
			tempList.setStartTime(startTime);
			tempList.setEndTime(endTime);
			tempList.setStrAudioLabel(strAudio);
			tempList.setExtended(extended);

			// PickUP label color(collision check in time line)
			int newCol = pickupColorAudioLabel(startTime, endTime, extended);
			tempList.setColor(newCol);

			// Update audio label list
			audioLabelInfo.set(idx, tempList);
			// update Label List(adjust position)
			updateAudioLabel(index, startTime, endTime, strAudio, extended,
					newCol);

			// Redraw all labels for exchange color
			redrawAudioLabelAll();
		} else {
			// no data (insert new script data)
			idx = indexScriptData(startTime);
			if (idx >= 0) {
				// PickUP label color(collision check in time line)
				int newCol = pickupColorAudioLabel(startTime, endTime, extended);

				// insert new script data
				audioStartTimeList.add(idx, startTime);
				AudioLabelInfo tempList = new AudioLabelInfo(index, startTime,
						endTime, strAudio, extended, newCol);
				audioLabelInfo.add(idx, tempList);
				// update Label List
				createAudioLabel(idx, index, startTime, endTime, strAudio,
						extended, newCol);

				// Redraw all labels for exchange color
				redrawAudioLabelAll();
			} else {
				// ** May be, invalid start time **********
				result = false;
			}
		}

		// return result status
		return (result);
	}

	/**
	 * Local method : Delete Audio Label
	 */
	public void deleteAudioLabel(int startTime) {
		// Search target Label
		for (int i = 0; i < audioStartTimeList.size(); i++) {
			// PickUP Label info.
			if (startTime == audioStartTimeList.get(i)) {
				// Delete current Label
				audioLabelList.get(i).dispose();
				audioStartTimeList.remove(i);
				audioLabelInfo.remove(i);
				audioLabelList.remove(i);
			}
		}
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
			// Milli Sec
			Integer ms100 = ms / 10;
			if (ms100 < 10)
				formTime = formTime + "0" + ms100.toString();
			else
				formTime = formTime + ms100.toString();
		}

		// return String data
		return (formTime);
	}

	/**
	 * Local Class extends MouseTrackAdapter for Label
	 */
	class LabelMouseCursorTrackAdapter extends MouseTrackAdapter {
		// mouse cursor enter into parent area
		public void mouseEnter(MouseEvent e) {
			// Changer Cursor image from ARROW type to HAND type
			Label parentLabel = (Label) e.getSource();
			parentLabel.setCursor(new Cursor(null, SWT.CURSOR_HAND));
		}

		// mouse cursor exit parent area
		public void mouseExit(MouseEvent e) {
			// Reset Cursor image to default type (ARROW)
			Label parentLabel = (Label) e.getSource();
			parentLabel.setCursor(new Cursor(null, SWT.CURSOR_HAND));
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
		// Reset flag
		editMode = false;
	}

	/**
	 * Local method : Start Mouse drag event
	 */
	private void startMouseDraggedEvent(MouseEvent e, int startTime) {

		// StartUP Mouse Drag action
		statusMouseDragged = true;
		startTimeMouseDragged = startTime;
		AudioLabelInfo tempInfo = audioLabelInfo.get(audioStartTimeList
				.indexOf(startTime));
		int endTime = tempInfo.getEndTime();
		strAudioMouseDragged = tempInfo.getStrAudioLabel();

		// Store current Mouse info.
		startXMouseDragged = e.x;
		previousXMouseDragged = startXMouseDragged;
		deltaMouseDragged = 0;

		// Create Mouse Drag's Label(blue)
		createMouseDragLabel(startTimeMouseDragged, endTime,
				strAudioMouseDragged);

		// Check status of current EditPanel
		int startTimeEditPanel = EditPanelView.getInstance()
				.getInstanceTabEditPanel().getStarTimeEditPanel();
		if (startTime == startTimeEditPanel) {
			// synchronize data
			editMode = true;
		}

	}

	/**
	 * Local method : Move Mouse drag position
	 */
	private void moveMouseDraggedEvent(MouseEvent e) {

		// PickUP current position of blue label
		Rectangle pos = dragAudioLabel.getBounds();
		// Calculate blue label's time
		int startPosBlueLabel = pos.x;
		int endPosBlueLabel = pos.x + pos.width;

		// Max limit check
		int nowEndTime = (instParentView.getMaxTimeLine() - (currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE))
				/ TIME2PIXEL;
		// Max limit check (not expand time line)
		if (endPosBlueLabel > nowEndTime) {
			// Adjust point of blue label
			// //endPosBlueLabel = nowEndTime;
			// //startPosBlueLabel = endPosBlueLabel - (currentWidthTimeLine *
			// TIME2PIXEL);
			// ///maxXDragTimeLine = nowEndTime - 1;
			// ///minXDragTimeLine = maxXDragTimeLine - currentWidthTimeLine;
			// Adjust End time
			pos.x = maxXDragTimeLine - pos.width;
			dragAudioLabel.setBounds(pos);

			// non update position & repaint of yellow bar
			return;
		}

		// Update X move distance(pixel)
		deltaMouseDragged = deltaMouseDragged + (e.x - previousXMouseDragged);

		// PickUP Mouse Drag action
		AudioLabelInfo tempInfo = audioLabelInfo.get(audioStartTimeList
				.indexOf(startTimeMouseDragged));
		int endTime = tempInfo.getEndTime();

		// Move Mouse Drag's Label(blue)
		int newDeltaX = moveMouseDragLabel(startTimeMouseDragged, endTime,
				deltaMouseDragged, strAudioMouseDragged);

		// Check result code
		if (newDeltaX != deltaMouseDragged) {
			// Adjust delta X position
			deltaMouseDragged = newDeltaX;
		} else {
			// Update current X-Y position
			previousXMouseDragged = e.x;
		}

		// Check min x position
		if (startPosBlueLabel < minXDragTimeLine) {
			// Update mix & max location
			minXDragTimeLine -= TL_MIN_SCALESIZE;
			if (minXDragTimeLine < 0)
				minXDragTimeLine = 0;
			maxXDragTimeLine = minXDragTimeLine + currentWidthTimeLine;

			// Scroll toward left(one scale size)
			instParentView.resetLocationTimeLine(minXDragTimeLine * TIME2PIXEL,
					endPosBlueLabel * TIME2PIXEL);
		}
		// Check max x position
		else if (endPosBlueLabel > maxXDragTimeLine) {
			// Update mix & max location
			maxXDragTimeLine += TL_MIN_SCALESIZE;
			minXDragTimeLine += TL_MIN_SCALESIZE;
			// check max limit
			int delta = nowEndTime - maxXDragTimeLine;
			if (delta < 0) {
				// adjust end time line to max limit
				maxXDragTimeLine = nowEndTime;
				minXDragTimeLine = maxXDragTimeLine - currentWidthTimeLine;
			}
			// Update blue label X position
			endPosBlueLabel = maxXDragTimeLine;

			// Adjust End time
			pos.x = endPosBlueLabel - pos.width;
			dragAudioLabel.setBounds(pos);

			// Scroll toward right(one scale size)
			instParentView.resetLocationTimeLine(minXDragTimeLine * TIME2PIXEL,
					endPosBlueLabel * TIME2PIXEL);
		}

	}

	/**
	 * Local method : Finish Mouse drag action
	 */
	private void endMouseDraggedEvent(MouseEvent e) {

		// Start Data Convert
		execDataConvMouseDragged = true;
		// Clear status flag
		statusMouseDragged = false;

		// Remove blue Label
		// ///dragAudioLabel.dispose();
		// Set status=false of visible blue label
		setVisibleDragAudioLabel(false);

		// PickUP current Audio info. & Calculate new Audio info.
		int newStartTime = startTimeMouseDragged
				+ (deltaMouseDragged * TIME2PIXEL);
		AudioLabelInfo tempInfo = audioLabelInfo.get(audioStartTimeList
				.indexOf(startTimeMouseDragged));
		String newStrAudioLabel = tempInfo.getStrAudioLabel();
		boolean newExtended = tempInfo.getExtended();

		// Use end time of voice engine
		int newEndTime = 0;
		int index = instScriptData.getIndexScriptData(startTimeMouseDragged);
		if (index >= 0) {
			newEndTime = instScriptData.getScriptEndTime(index)
					+ (deltaMouseDragged * TIME2PIXEL);
		}
		// Use end time of WAV information
		int newEndTimeWav = 0;
		index = instScriptData.getIndexWavList(startTimeMouseDragged);
		// Check WAV status
		if ((index >= 0) && instScriptData.getEnableWavList(index)) {
			newEndTimeWav = instScriptData.getEndTimeWavList(index)
					+ (deltaMouseDragged * TIME2PIXEL);
		}

		// current min-max time line's value
		int nowMaxTimeLine = instParentView.getMaxTimeLine();
		int nowMinTimeLine = currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE;

		// Adjust max time line for new position of audio label
		if (newEndTime > nowMaxTimeLine) {
			// adjust X-Y position
			newStartTime = nowMaxTimeLine
					- (tempInfo.getEndTime() - startTimeMouseDragged);
			newEndTime = nowMaxTimeLine;
		}
		// Adjust min time line for new position of audio label
		else if (newStartTime < nowMinTimeLine) {
			// adjust X-Y position
			newStartTime = nowMinTimeLine;
			newEndTime = newStartTime
					+ (tempInfo.getEndTime() - startTimeMouseDragged);
		}

		// Request Refresh ScriptList to parent View
		instParentView.reqRefreshScriptData(startTimeMouseDragged,
				newStartTime, newEndTime, newEndTimeWav, editMode);
		// Check target end time
		if ((index >= 0) && instScriptData.getEnableWavList(index)) {
			// Exchange end time to WAV time
			newEndTime = newEndTimeWav;
		}

		// Delete current Audio Label
		deleteAudioLabel(startTimeMouseDragged);
		// Reset position for target Audio Label
		putAudioLabel(0, newStartTime, newEndTime, newStrAudioLabel,
				newExtended);

		// Redraw all labels for exchange color
		redrawAudioLabelAll();

		// Expand Composite of TimeLine
		// ///instParentView.reqExpandTimeLine();
		instParentView.adjustEndTimeLine();

		// End of Process
		execDataConvMouseDragged = false;
	}

	/**
	 * Getter method : Get current mouse drag
	 */
	public boolean getStatusDragAudioLabel() {
		// return current drag status
		return (statusMouseDragged);
	}

	/**
	 * Local Class extends MouseTrackAdapter for Button, Composite
	 */
	class AudioLabelMouseAdapter extends MouseAdapter {
		// Local data
		private int ownStartTime = -1;

		// Constructor
		public AudioLabelMouseAdapter(int parentStartTime) {
			super();
			ownStartTime = parentStartTime;
		}

		// Mouse event : Clicked
		public void mouseDown(MouseEvent e) {
			// Check mouse 'Left' button
			if (!execDataConvMouseDragged && !statusMouseDragged
					&& (e.button == 1)) {

				// Precheck Mouse drag action
				precheckMouseDraggedEvent(e);

				// Forced end process of Multiple selection mode
				EditPanelView.getInstance().getInstanceTabEditPanel()
						.endSelectMultiItems();
			}

		}

		// Mouse event : Purge
		public void mouseUp(MouseEvent e) {
			// Check mouse 'Left' button
			if (!execDataConvMouseDragged && !statusMouseDragged
					&& (e.button == 1) && currentDragStatus) {

				// Get current System time
				long timeReleaseMouseLButton = e.time;
				// Check short single click action
				if ((timeReleaseMouseLButton - timePushMouseLButton) < AL_SINGLE_CLICK_TIME) {
					// goto Edit description process
					instParentView.reqSelectScriptData(ownStartTime);
				}
				// Reset status flag
				currentDragStatus = false;
			}
			// Check mouse 'Left' button
			else if (!execDataConvMouseDragged && statusMouseDragged
					&& (e.button == 1)) {

				// Finish Mouse drag action & Refresh target Audio Label info.
				endMouseDraggedEvent(e);
			}
		}
	}

	/**
	 * Local Class extends MouseTrackAdapter for AudioLabel
	 */
	class AudioLabelMouseMoveListener implements MouseMoveListener {
		// own StartTime(description)
		private int parentStartTime = -1;

		// Constructor
		public AudioLabelMouseMoveListener(int parentStartTime) {
			super();

			// store own start time
			this.parentStartTime = parentStartTime;
		}

		// Mouse event : Dragged
		public void mouseMove(MouseEvent e) {

			// Check mouse 'Left' button
			if (!execDataConvMouseDragged && statusMouseDragged) {
				// Move blue label for Drag Mouse
				moveMouseDraggedEvent(e);
			}
			// Check mouse 'Left' button
			else if (!execDataConvMouseDragged && !statusMouseDragged
					&& currentDragStatus) {

				// Start Mouse drag action
				startMouseDraggedEvent(e, parentStartTime);
				currentDragStatus = false;

				// Get each current parameters
				Point wsiz = instParentView.getCurrentSizeScrollBar();
				currentWidthTimeLine = wsiz.x;
				Rectangle comp = parentInst.getBounds();

				// Calculate minimum x position of current TimeLine window's
				// size
				minXDragTimeLine = -1 * comp.x;
				// Calculate maximum x position of current TimeLine window's
				// size
				maxXDragTimeLine = minXDragTimeLine + currentWidthTimeLine;
			}
		}
	}

}
