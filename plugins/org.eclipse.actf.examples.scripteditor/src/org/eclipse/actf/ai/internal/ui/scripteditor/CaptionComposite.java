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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.actf.ai.internal.ui.scripteditor.event.EventManager;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.SyncTimeEvent;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.SyncTimeEventListener;
import org.eclipse.actf.ai.scripteditor.data.DataUtil;
import org.eclipse.actf.ai.scripteditor.data.IScriptData;
import org.eclipse.actf.ai.scripteditor.data.ScriptDataManager;
import org.eclipse.actf.ai.scripteditor.data.event.DataEventManager;
import org.eclipse.actf.ai.scripteditor.data.event.GuideListEvent;
import org.eclipse.actf.ai.scripteditor.data.event.LabelEvent;
import org.eclipse.actf.ai.scripteditor.data.event.LabelEventListener;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.actf.examples.scripteditor.Activator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class CaptionComposite extends Composite implements IUNIT,
		SyncTimeEventListener, LabelEventListener {

	// instance of own class
	static private CaptionComposite ownInst = null;

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
	private Label dragLabel;
	private Label labelBorderTimeLine;

	// parent view info.
	private TimeLineView instParentView;

	// for mouse drag action
	private Composite parentInst = null;
	private int minXDragTimeLine = 0;
	private int maxXDragTimeLine = 0;
	private int currentWidthTimeLine = 0;
	private boolean currentDragStatus = false;

	private static EventManager eventManager = null;
	private static DataEventManager dataEventManager = null;
	protected ScriptDataManager scriptManager = null;

	protected Label selectLabel = null;
	protected Map<IScriptData, Label> labelMap = new HashMap<IScriptData, Label>();
	protected Map<IScriptData, Label> guideMarkMap = new HashMap<IScriptData, Label>();

	protected List<IScriptData> resultListUp = new ArrayList<IScriptData>();
	protected List<IScriptData> resultListDown = new ArrayList<IScriptData>();
	protected List<IScriptData> resultListMoveUp = new ArrayList<IScriptData>();
	protected List<IScriptData> resultListMoveDown = new ArrayList<IScriptData>();

	/**
	 * @category Constructor
	 */
	public CaptionComposite(Composite parent) {
		super(parent, SWT.BORDER);
		// store parent instance
		parentInst = parent;
		// store own instance
		ownInst = this;

		// Initialize Composite
		initializeComposite(parent);

		// Store TimeLine view instance
		instParentView = TimeLineView.getInstance();

		scriptManager = ScriptDataManager.getInstance();
		eventManager = EventManager.getInstance();
		eventManager.addSyncTimeEventListener(ownInst);
		dataEventManager = DataEventManager.getInstance();
		dataEventManager.addLabelEventListener(ownInst);
		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				eventManager.removeSyncTimeEventListener(ownInst);
				dataEventManager.removeLabelEventListener(ownInst);
			}
		});

	}

	// Initialize for Canvas class & object
	private void initializeComposite(Composite parent) {
		try {
			// Initialize border image of TimeLine (1st priority in own
			// Composite)
			labelBorderTimeLine = new Label(ownInst, SWT.NONE);
			// Set background color
			labelBorderTimeLine.setBackground(getDisplay().getSystemColor(
					SWT.COLOR_BLUE));
			// Initial location = (0, 0)
			setLocationBorderTimeLine(0);

			// Create new Label (2nd priority in own Composite)
			dragLabel = new Label((Composite) ownInst, SWT.TOP);
			setVisibleDragAudioLabel(false);

		} catch (Exception ef) {
			System.out.println("Exception : Front processer=" + ef);
		}
	}

	/**
	 * Setter method : Set visible Audio Label for Mouse Drag action
	 */
	protected void setVisibleDragAudioLabel(boolean stat) {
		// Set visible Audio Label for Mouse Drag action
		dragLabel.setVisible(stat);
	}

	public void clearLabel(int type) {

		// clean up label objects
		for (IScriptData data : scriptManager.getDataList()) {
			if (data.getType() != type)
				continue;
			Label label = labelMap.get(data);
			if (label != null) {
				label.dispose();
				label = null;
				labelMap.remove(data);
			} else {
				Label guide = guideMarkMap.get(data);
				if (guide != null) {
					guide.dispose();
					guide = null;
					guideMarkMap.remove(data);
				}
			}
		}

		// make label map empty.
		labelMap.clear();

	}

	protected void setLocationBorderTimeLine(int x) {
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
	 * Local method : Create new Audio Label
	 */
	protected void createMouseDragLabel(IScriptData data, int startTime,
			int endTime, String strAudio) {
		// Calculate target Label position & size
		int sX = (startTime - (currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE))
				/ TIME2PIXEL;
		int eX = (endTime - (currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE))
				/ TIME2PIXEL;
		int sY = TL_AUDIO1_MDRAG_SY;
		int size = eX - sX;

		Label label = labelMap.get(data);
		if (label != null) {
			Rectangle parentRect = label.getBounds();
			sY = (parentRect.y - 6/* TL_AUDIO1_SY */) + TL_AUDIO1_MDRAG_SY;
		}

		// Adjust Label width
		if (size < TL_MIN_SCALESIZE) {
			size = TL_MIN_SCALESIZE;
			eX = sX + size;
		}

		dragLabel.addMouseTrackListener(new MouseCursorTrackAdapter());
		dragLabel.setToolTipText(DataUtil.makeToolTipInfo(data));

		// Layout new Label
		dragLabel.setBounds(sX, sY, size, TL_LABEL_MDRAG_HEIGHT);
		dragLabel.setBackground(getDisplay().getSystemColor(SWT.COLOR_YELLOW));

		// Set visible blue label
		setVisibleDragAudioLabel(true);
	}

	/**
	 * Local method : Move drag mouse
	 */
	protected int moveMouseDragLabel(IScriptData data, int startTime,
			int endTime, int deltaX, String strAudio) {
		// Result code(adjust delta X Position)
		int newDeltaX = deltaX;

		// Calculate new TimeLine
		int newStartTime = startTime + (deltaX * TIME2PIXEL);
		int newEndTime = endTime + (deltaX * TIME2PIXEL);

		// Get current Point value
		Rectangle nowRect = dragLabel.getBounds();

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
		dragLabel.setBounds(sX, sY, size, TL_LABEL_MDRAG_HEIGHT);

		// Update ToolTip for Parent Label
		String newText = new String(DataUtil.makeToolTipInfo(data));
		Label parentLabel = labelMap.get(data);
		parentLabel.setToolTipText(newText);

		// Return result
		return (newDeltaX);
	}

	public void deleteImage() {
		guideMarkMap.clear();
	}

	public boolean deleteGuideMark(IScriptData data) {
		Label label = guideMarkMap.get(data);
		if (label != null) {
			label.dispose();
			guideMarkMap.remove(data);
			return true;
		}
		return false;
	}

	protected void reDisplayLabelforDelete(IScriptData data) {
		checkDuplicateAll(data);

		boolean[] overlay = DataUtil.isOverlap(resultListUp, resultListDown,
				null);
		// Redisplay labels which overlaid moved label.
		int pos = 0;
		for (int i = 0; i < resultListUp.size(); i++, pos++) {
			updateLabel(resultListUp.get(i),
					getColor(overlay[i], resultListUp.get(i)), i);

		}
		// Redisplay labels which overlaid moved label.
		for (int i = 0; i < resultListDown.size(); i++, pos++) {
			updateLabel(resultListDown.get(i),
					getColor(overlay[pos], resultListDown.get(i)), pos + 1);
		}
	}

	public void deleteLabel(IScriptData data) {
		reDisplayLabelforDelete(data);

		Label label = (Label) labelMap.get(data);
		if (label != null) {
			label.dispose();
			labelMap.remove(data);
		}
	}

	/**
	 * Local method : Create new Audio Label
	 */
	public void createGuideMark(IScriptData data) {
		// Calculate target Label position & size
		int intdev = currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE;
		int sX = (data.getStartTime() - intdev) / TIME2PIXEL;
		// int eX = (endTime - intdev) / TIME2PIXEL;
		int sY = TL_AUDIO1_SY + 35;
		int size = 16;// eX - sX;

		// Create new Label
		Label newLabel = new Label((CaptionComposite) ownInst, SWT.BORDER);
		newLabel.setData(data);
		// Store instance of current Label
		// audioLabelList.add(index, newAudio);

		// Layout Audio Label
		FormData newAudioLData = new FormData();
		newAudioLData.width = size;
		newAudioLData.height = 16;// TL_LABEL_HEIGHT;
		newAudioLData.top = new FormAttachment(0, 1000, sY);
		newAudioLData.left = new FormAttachment(0, 1000, sX);
		newLabel.setLayoutData(newAudioLData);
		newLabel.setBounds(sX, sY, size, TL_LABEL_HEIGHT);
		ImageDescriptor button1d = Activator
				.getImageDescriptor("/icons/mark1.bmp");
		newLabel.setImage(button1d.createImage());
		// newAudio.setBackground(col);
		newLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		guideMarkMap.put(data, newLabel);

	}

	/**
	 * Local method : Create new Audio Label
	 */
	protected void createLabel(IScriptData data, Color col, int lineNo) {
		// Calculate target Label position & size
		int eX = 0;
		int intdev = currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE;
		int sX = (data.getStartTime() - intdev) / TIME2PIXEL;
		if (data.isWavEnabled() && data.getWavURI() != null) {
			eX = (data.getWavEndTime() - intdev) / TIME2PIXEL;
		} else {
			eX = (data.getEndTime() - intdev) / TIME2PIXEL;
		}

		int sY = TL_DEFAULT_POINT_SY;// TL_AUDIO1_SY;
		int size = eX - sX;
		// Color col = getDisplay().getSystemColor(SWT.COLOR_GREEN);

		// Adjust Label width
		if (size < TL_MIN_SCALESIZE) {
			size = TL_MIN_SCALESIZE;
			eX = sX + size;
		}

		// Create new Label
		Label newLabel = new Label((Composite) ownInst, SWT.BORDER);
		newLabel.setData(data);
		labelMap.put(data, newLabel);

		// Adjust label's height by duplicated setting

		sY = sY + (lineNo * TL_AUDIO1_MDRAG_SY);

		// Layout Audio Label
		FormData newAudioLData = new FormData();
		newAudioLData.width = size;
		newAudioLData.height = TL_LABEL_HEIGHT;
		newAudioLData.top = new FormAttachment(0, 1000, sY);
		newAudioLData.left = new FormAttachment(0, 1000, sX);
		newLabel.setLayoutData(newAudioLData);
		newLabel.setBounds(sX, sY, size, TL_LABEL_HEIGHT);
		newLabel.setBackground(col);
		newLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		// Create ToolTip
		newLabel.setToolTipText(DataUtil.makeToolTipInfo(data));
		if (data.isExtended()) {
			if (data.getWavURI() != null && data.isWavEnabled()) {
				Image imgWavOn = Activator.getImageDescriptor(
						"/icons/wave_bar_ex.gif").createImage();
				newLabel.setImage(imgWavOn);
			} else {
				newLabel.setText(" Extended ");

			}
		} else {
			if (data.getWavURI() != null && data.isWavEnabled()) {
				Image imgWavOn = Activator.getImageDescriptor(
						"/icons/wave_bar.gif").createImage();
				newLabel.setImage(imgWavOn);
			} else {
				// Display blank text
				newLabel.setText("");

			}
		}

		// MakeUP MouseListener & MouseMoveListener
		newLabel.addMouseListener(new LabelMouseAdapter());
		newLabel.addMouseMoveListener(new LabelMouseMoveListener(data
				.getStartTime()));
		// SetUP MouseTrackListener
		newLabel.addMouseTrackListener(new MouseCursorTrackAdapter());
	}

	public boolean checkDuplicateAll(IScriptData data) {
		resultListUp = DataUtil.overlapCheckBefore(data,
				IScriptData.TYPE_CAPTION);
		resultListDown = DataUtil.overlapCheckAfter(data,
				IScriptData.TYPE_CAPTION);

		if (resultListUp.size() > 0 || resultListDown.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param data
	 * @return
	 */
	private boolean checkDuplicateMoveAll(IScriptData data) {

		resultListMoveUp = DataUtil.overlapCheckBefore(data,
				IScriptData.TYPE_CAPTION);
		resultListMoveDown = DataUtil.overlapCheckAfter(data,
				IScriptData.TYPE_CAPTION);

		if (resultListMoveUp.size() > 0 || resultListMoveDown.size() > 0) {
			return true;
		} else {
			return false;
		}

	}

	private void updateLabel(IScriptData data, /* int cola */Color col,
			int lineNo) {
		Label targetLabel = labelMap.get(data);
		if (targetLabel == null) {
			return; //
		}

		// Calculate target Label position & size
		int intdev = currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE;
		int sX = (data.getStartTime() - intdev) / TIME2PIXEL;
		int eX = 0;
		if (data.isWavEnabled() && data.getWavURI() != null) {
			eX = (data.getWavEndTime() - intdev) / TIME2PIXEL;
		} else {
			eX = (data.getEndTime() - intdev) / TIME2PIXEL;
		}
		int sY = TL_DEFAULT_POINT_SY;// TL_AUDIO1_SY;
		int size = eX - sX;
		if (size < TL_MIN_SCALESIZE) {
			size = TL_MIN_SCALESIZE;
			eX = sX + size;
		}

		sY = sY + (lineNo * TL_AUDIO1_MDRAG_SY);

		if (data.isExtended()) {

			// Check enable status
			if (data.getWavURI() != null && data.isWavEnabled()) {
				Image imgWavOn = Activator.getImageDescriptor(
						"/icons/wave_bar_ex.gif").createImage();
				targetLabel.setImage(imgWavOn);
			} else {
				Image imgWavOn = null;
				targetLabel.setImage(imgWavOn);
				Font font = targetLabel.getFont();// .setFont(font)
				FontData[] fd = font.getFontData();
				fd[0].setHeight(7);
				targetLabel.setFont(new Font(Display.getCurrent(), fd[0]));
				targetLabel.setText(" dummy "); // to change text string is need
												// redraw. (Why?)
				targetLabel.setText(" Extended ");
			}
			// }
		} else {
			if (data.isWavEnabled() && data.getWavURI() != null) {
				// Display 'WAV' image
				Image imgWavOn = Activator.getImageDescriptor(
						"/icons/wave_bar.gif").createImage();
				targetLabel.setImage(imgWavOn);
			} else {
				// Display blank text
				Image imgWavOn = null;

				// TODO check
				targetLabel.setImage(imgWavOn);
				targetLabel.setText("");
			}
		}

		// Redraw Audio Label
		targetLabel.setBackground(col);
		targetLabel.setBounds(sX, sY, size, TL_LABEL_HEIGHT);
		targetLabel.setToolTipText(DataUtil.makeToolTipInfo(data));
		targetLabel.redraw();
		targetLabel.update();
	}

	public void putLabel(IScriptData data) {
		putLabel(data, MODE_PUT);
	}

	/**
	 * create all labels
	 */
	private void createAllLabel(int type) {
		List<IScriptData> list = scriptManager.getDataList(type);
		if (list.size() == 0) {
			return;
		}
		int[] allColors = new int[list.size()];
		int[] allLines = new int[list.size()];

		// initialize parameter
		for (int i = 0; i < list.size(); i++) {
			allColors[i] = SWT.COLOR_GREEN;
			allLines[i] = 0;
		}

		// get overlay parameter
		int endTime = 0;
		int lineNo = 0;
		for (int i = 0; i < (list.size() - 1); i++) {
			IScriptData data = list.get(i);

			int currentEndTime;
			if (data.isDataCommit() == false || data.getType() != type) {
				continue;
			}
			if (data.isWavEnabled() && data.getWavURI() != null) {
				currentEndTime = data.getWavEndTime();
			} else {
				currentEndTime = data.getEndTime();
			}

			int nextStartTime = list.get(i + 1).getStartTime();
			if (endTime < currentEndTime) {
				endTime = currentEndTime;
			}
			if (endTime > nextStartTime) {
				allColors[i] = SWT.COLOR_RED;
				allLines[i] = lineNo++;
				allColors[i + 1] = SWT.COLOR_RED;
				// allLines[i + 1] = lineNo++;
				allLines[i + 1] = lineNo;
			} else {
				if (data.isExtended() == true) {
					allColors[i] = SWT.COLOR_BLUE;
				}
				lineNo = 0;
			}
		}

		// create all labels
		for (int i = 0; i < list.size(); i++) {
			IScriptData data = list.get(i);
			if (data.isDataCommit() == false || data.getType() != type) {
				continue;
			}
			createLabel(data, getDisplay().getSystemColor(allColors[i]),
					allLines[i]);
		}
	}

	private Color getColor(boolean flag, IScriptData data) {
		int col = (flag == true) ? SWT.COLOR_RED
				: (data.isExtended() == true) ? SWT.COLOR_BLUE
						: SWT.COLOR_GREEN;
		return getDisplay().getSystemColor(col);
	}

	/**
	 * Put on new Audio Label on Composite
	 */
	public boolean putLabel(IScriptData data, int mode) {
		boolean result = true;

		// Check exist target data from current List
		// Move label by dragging.
		if (mode == MODE_MOVE) {
			resultListMoveDown.clear();
			resultListMoveDown.clear();
			if (checkDuplicateMoveAll(data)) {
				boolean[] overlay = DataUtil.isOverlap(resultListMoveUp,
						resultListMoveDown, data);
				int pos = 0;
				for (int i = 0; i < resultListMoveUp.size(); i++, pos++) {
					updateLabel(resultListMoveUp.get(i),
							getColor(overlay[i], resultListMoveUp.get(i)), i);
				}
				createLabel(data, getColor(overlay[pos], data), pos);
				pos++;
				// Redisplay labels which overlaid moved label.
				for (int i = 0; i < resultListMoveDown.size(); i++, pos++) {
					updateLabel(resultListMoveDown.get(i),
							getColor(overlay[pos], resultListMoveDown.get(i)),
							pos);
				}
			} else {
				Color col = getDisplay().getSystemColor(
						(data.isExtended() == true) ? SWT.COLOR_BLUE
								: SWT.COLOR_GREEN);
				createLabel(data, col, 0);
			}

		} else if (labelMap.containsKey(data)) {
			// not dragging

			resultListUp.clear();
			resultListDown.clear();

			if (checkDuplicateAll(data)) {
				boolean[] overlay = DataUtil.isOverlap(resultListUp,
						resultListDown, data);
				// int[] verticalPos = DataUtil.getVerticalPos(resultListUp,
				// resultListDown, data);
				// Redisplay labels which overlaid moved label.
				int pos = 0;
				for (int i = 0; i < resultListUp.size(); i++, pos++) {
					updateLabel(resultListUp.get(i),
							getColor(overlay[i], resultListUp.get(i)), i);

				}
				// Redisplay moved label.
				updateLabel(data, getColor(overlay[pos], data), pos);
				pos++;
				// Redisplay labels which overlaid moved label.
				for (int i = 0; i < resultListDown.size(); i++, pos++) {
					updateLabel(resultListDown.get(i),
							getColor(overlay[pos], resultListDown.get(i)), pos);
				}

			} else {
				// Redisplay moved label which has no overlaid label.
				updateLabel(
						data,
						getDisplay().getSystemColor(
								(data.isExtended() == false) ? SWT.COLOR_GREEN
										: SWT.COLOR_BLUE), 0);

			}
		} else { // Create new label

			if (checkDuplicateAll(data)) {
				boolean[] overlay = DataUtil.isOverlap(resultListUp,
						resultListDown, data);
				int pos = 0;
				for (int i = 0; i < resultListUp.size(); i++, pos++) {
					updateLabel(resultListUp.get(i),
							getColor(overlay[i], resultListUp.get(i)), i);
				}
				createLabel(data, getColor(overlay[pos], data), pos);
				pos++;
				// Redisplay labels which overlaid moved label.
				for (int i = 0; i < resultListDown.size(); i++) {
					updateLabel(resultListDown.get(i),
							getColor(overlay[pos], resultListDown.get(i)), pos);
					pos++;
				}
				deleteGuideMark(data); // delete mark
			} else {
				createLabel(
						data,
						getDisplay().getSystemColor(
								(data.isExtended() == false) ? SWT.COLOR_GREEN
										: SWT.COLOR_BLUE), 0);
				deleteGuideMark(data); // delete mark
			}
		}
		return (result);
	}

	/**
	 * Local method : Start Mouse drag event
	 */
	private void startMouseDraggedEvent(MouseEvent e, int startTime) {
		IScriptData data = (IScriptData) e.widget.getData();

		// StartUP Mouse Drag action
		statusMouseDragged = true;
		startTimeMouseDragged = startTime;
		int endTime = 0;
		// AudioLabelInfo tempInfo = audioLabelInfo.get(audioStartTimeList
		// .indexOf(startTime));
		if (data.isWavEnabled()) {
			endTime = data.getWavEndTime();
		} else {
			endTime = data.getEndTime();// tempInfo.getEndTime();
		}
		strAudioMouseDragged = data.getDescription();// tempInfo.getStrAudioLabel();

		// Store current Mouse info.
		startXMouseDragged = e.x;
		previousXMouseDragged = startXMouseDragged;
		deltaMouseDragged = 0;

		createMouseDragLabel(data, startTimeMouseDragged, endTime,
				strAudioMouseDragged);

	}

	/**
	 * Local method : Move Mouse drag position
	 */
	private void moveMouseDraggedEvent(MouseEvent e) {
		IScriptData data = (IScriptData) e.widget.getData();
		// PickUP current position of blue label
		Rectangle pos = dragLabel.getBounds();
		// Calculate blue label's time
		int startPosBlueLabel = pos.x;
		int endPosBlueLabel = pos.x + pos.width;

		// Max limit check
		int nowEndTime = (instParentView.getMaxTimeLine() - (currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE))
				/ TIME2PIXEL;
		// Max limit check (not expand time line)
		if (endPosBlueLabel > nowEndTime) {
			pos.x = maxXDragTimeLine - pos.width;
			dragLabel.setBounds(pos);

			// non update position & repaint of yellow bar
			return;
		}

		// Update X move distance(pixel)
		deltaMouseDragged = deltaMouseDragged + (e.x - previousXMouseDragged);

		// PickUP Mouse Drag action
		int endTime = data.getEndTime();// tempInfo.getEndTime();

		// Move Mouse Drag's Label(blue)
		int newDeltaX = moveMouseDragLabel(data, startTimeMouseDragged,
				endTime, deltaMouseDragged, strAudioMouseDragged);

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
			dragLabel.setBounds(pos);

			instParentView.resetLocationTimeLine(minXDragTimeLine * TIME2PIXEL,
					endPosBlueLabel * TIME2PIXEL);
		}

	}

	/**
	 * Local method : Finish Mouse drag action
	 */
	private void endMouseDraggedEvent(MouseEvent e) {
		// Label data is ScriptDataManager
		IScriptData data = (IScriptData) e.widget.getData();

		// Start Data Convert
		execDataConvMouseDragged = true;
		// Clear status flag
		statusMouseDragged = false;

		// Remove blue Label
		setVisibleDragAudioLabel(false);

		// PickUP current Audio info. & Calculate new Audio info.
		int newStartTime = startTimeMouseDragged
				+ (deltaMouseDragged * TIME2PIXEL);

		int newEndTimeVg = 0;
		newEndTimeVg = data.getEndTime();
		newEndTimeVg = newEndTimeVg + (deltaMouseDragged * TIME2PIXEL);

		// Use end time of WAV information
		int newEndTimeWav = 0;
		if (data.isWavEnabled()) {
			newEndTimeWav = data.getWavEndTime()
					+ (deltaMouseDragged * TIME2PIXEL);
		}

		int newEndTime = newEndTimeVg;
		if ((newEndTimeVg < newEndTimeWav) && data.isWavEnabled()) {
			newEndTime = newEndTimeWav;
		}

		// current min-max time line's value
		int nowMaxTimeLine = instParentView.getMaxTimeLine();
		int nowMinTimeLine = currentTimeLineLocation * TL_DEF_SCROL_COMP_SCALE;

		// Adjust max time line for new position of audio label
		if (newEndTime > nowMaxTimeLine) {
			// adjust X-Y position
			newStartTime = nowMaxTimeLine
					- (data.getEndTime() - startTimeMouseDragged);
			newEndTime = nowMaxTimeLine;
		}
		// Adjust min time line for new position of audio label
		else if (newStartTime < nowMinTimeLine) {
			// adjust X-Y position
			newStartTime = nowMinTimeLine;
			newEndTime = newStartTime
					+ (data.getEndTime() - startTimeMouseDragged);
		}
		checkDuplicateAll(data);
		deleteLabel(data);

		// delete old data, then add new data //TODO check
		dataEventManager.fireGuideListEvent(new GuideListEvent(
				GuideListEvent.DELETE_DATA, data, this));

		data.setStartTime(newStartTime);
		if (data.getWavURI() != null && data.isWavEnabled()) {
			newEndTime = newEndTimeWav;
			data.setWavEndTime(newEndTime);
		} else {
			data.setEndTime(newEndTime);
		}

		putLabel(data, MODE_MOVE);

		instParentView.adjustEndTimeLine();
		dataEventManager.fireGuideListEvent(new GuideListEvent(
				GuideListEvent.SET_DATA, data, this));
		dataEventManager.fireGuideListEvent(new GuideListEvent(
				GuideListEvent.ADD_DATA, data, this));
		// End of Process
		execDataConvMouseDragged = false;
	}

	private class LabelMouseAdapter extends MouseAdapter {

		public void mouseDown(MouseEvent e) {
			if (!execDataConvMouseDragged && !statusMouseDragged
					&& (e.button == 1)) {
				currentDragStatus = true;
			}

		}

		public void mouseUp(MouseEvent e) {
			IScriptData data = (IScriptData) e.widget.getData();
			if (!execDataConvMouseDragged && !statusMouseDragged
					&& (e.button == 1) && currentDragStatus) {

				if (e.widget instanceof Label) {
					selectLabel = (Label) e.widget;
					dataEventManager.fireGuideListEvent(new GuideListEvent(
							GuideListEvent.SET_DATA, data, this));
				}
				currentDragStatus = false;
			} else if (!execDataConvMouseDragged && statusMouseDragged
					&& (e.button == 1)) {
				endMouseDraggedEvent(e);
			}
		}
	}

	/**
	 * Local Class extends MouseTrackAdapter for AudioLabel
	 */
	private class LabelMouseMoveListener implements MouseMoveListener {
		// own StartTime(description)
		protected int parentStartTime = -1;

		// Constructor
		public LabelMouseMoveListener(int parentStartTime) {
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

	public IScriptData getSelectLabelData() {
		if (selectLabel != null) {
			return (IScriptData) selectLabel.getData();
		}
		return null;
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
		clearLabel(IScriptData.TYPE_CAPTION);

		int len = scriptManager.size();
		if (len > 0) {
			List<IScriptData> list = scriptManager.getDataList();
			for (int i = 0; i < list.size(); i++) {
				IScriptData data = list.get(i);
				if (data.getType() != IScriptData.TYPE_CAPTION
						|| data.isDataCommit() == false)
					continue;
				putLabel(data, MODE_PUT);
			}
		}
	}

	public void handleSyncTimeEvent(SyncTimeEvent e) {
		// Synchronize TimeLine view
		if (e.getEventType() == SyncTimeEvent.SYNCHRONIZE_TIME_LINE) {
			synchronizeTimeLine(e.getCurrentTime());
			// } else if(e.getEventType() == SyncTimeEvent.ADJUST_TIME_LINE){
			// synchronizeTimeLine(e.getCurrentTime());
		} else if (e.getEventType() == SyncTimeEvent.REFRESH_TIME_LINE) {
			refreshTimeLine(e.getCurrentTime());
		}
	}

	public void handleLabelEvent(LabelEvent e) {
		switch (e.getEventType()) {
		case LabelEvent.PUT_ALL_LABEL:
			createAllLabel(IScriptData.TYPE_CAPTION);
			break;
		case LabelEvent.PUT_LABEL:
			if (e.getData().getType() != IScriptData.TYPE_CAPTION) {
				return;
			}
			putLabel(e.getData());
			break;
		case LabelEvent.DELETE_LABEL:
			if (e.getData().getType() != IScriptData.TYPE_CAPTION) {
				return;
			}
			deleteLabel(e.getData());
			break;
		case LabelEvent.DELETE_PLAY_MARK:
			if (e.getData().getType() != IScriptData.TYPE_CAPTION) {
				return;
			}
			deleteGuideMark(e.getData());
			break;
		case LabelEvent.CLEAR_LABEL:
			clearLabel(IScriptData.TYPE_CAPTION);
			break;
		}
	}

}
