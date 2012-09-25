/*******************************************************************************
 * Copyright (c) 2012 Middle East Technical University Northern Cyprus Campus and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Elgin Akpinar (METU) - initial API and implementation
 *    Sukru Eraslan (METU NCC) - Eye Tracking Data Handling Implementation
 *******************************************************************************/

package org.eclipse.actf.examples.emine.ui.internal;

import org.eclipse.swt.graphics.Point;
import java.util.ArrayList;

import org.eclipse.actf.examples.emine.vips.types.Fixation;
import org.eclipse.actf.examples.emine.vips.types.Recording;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PointsToolbar extends Composite {
	private static PointsToolbar toolbar;
	private Button nextPointButton;
	private Button prevPointButton;
	private Button nextRecordButton;
	private Button prevRecordButton;
	private Button pathRecordButton;
	
	private Button fileSelectorButton;
	private Text recText;
	private Text pointText;
	private Text levelText;
	private int recIndex, pointIndex, level = 3, numberOfRecordings = 0, numberOfPoints = 0;
	private Label coordinates;
	private Label recordingIdLabel;
	private Fixation currentFixation;
	private Recording currentRecording;
	ArrayList<Recording> map;
	Shell s;
	
	public static PointsToolbar getToolbar(){
		return toolbar;
	}
	
	public static PointsToolbar getToolbar(Composite parent, int style){
		if(toolbar == null){
			toolbar = new PointsToolbar(parent, style);
		}
		return toolbar;
	}
	
	public void addSelectionAdapter(SelectionAdapter adapter){
		prevPointButton.addSelectionListener(adapter);
	}
	
	private PointsToolbar(Composite parent, int style) {
		super(parent, style);
		initLayout(parent);
		recIndex = pointIndex = 1;
	}

	private void initLayout(Composite parent) {
		s = new Shell(this.getDisplay());
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginTop = 0;
		gridLayout.marginBottom = 0;
		gridLayout.marginHeight = gridLayout.marginWidth = 1;
		gridLayout.numColumns = 6;
		setLayout(gridLayout);
		
		GridData gridData = new GridData();
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		setLayoutData(gridData);
		
		Group fileGroup = new Group(this, SWT.NONE);
		gridLayout.numColumns = 2;
		fileGroup.setLayout(gridLayout);
		new Label(fileGroup, SWT.NONE).setText("File: ");
		
		fileSelectorButton = new Button(fileGroup,  SWT.CENTER);
		fileSelectorButton.setText("  Select  ");
		fileSelectorButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(s, SWT.OPEN);
		        fd.setText("Open");
		        fd.setFilterPath("C:/");
		        String[] filterExt = { "*.csv"};
		        fd.setFilterExtensions(filterExt);
		        String selected = fd.open();
		        map = CsvReader.readData(selected);
				numberOfRecordings = map.size();
				if(numberOfRecordings > 0){
					currentRecording = map.get(0);
					nextRecordButton.setEnabled(true);
					numberOfPoints = map.get(0).getPointCount();
					if(numberOfPoints > 0){
						retrievePoint(1,1);
						nextPointButton.setEnabled(true);
					}
					setRecordingIdLabel();
				}
			}
		});
		
		Group recordingGroup = new Group(this, SWT.NONE);
		gridLayout.numColumns = 5;
		recordingGroup.setLayout(gridLayout);
		
		new Label(recordingGroup, SWT.NONE).setText(" Recording: ");

		prevRecordButton = new Button(recordingGroup,  SWT.CENTER);
		prevRecordButton.setText("  <  ");
		prevRecordButton.setEnabled(false);
		
		recText = new Text(recordingGroup,  SWT.CENTER);
		recText.setText("  1  ");
		recText.addModifyListener(new ModifyListener() {			
			public void modifyText(ModifyEvent e) {
				try {
					recIndex = Integer.parseInt(recText.getText().trim());
				} catch (Exception ex){
					recText.setText("  " + Integer.toString(recIndex) + "  ");
				}
			}
		});
		
		nextRecordButton = new Button(recordingGroup,  SWT.CENTER);
		nextRecordButton.setText("  >  ");
		nextRecordButton.setEnabled(false);

		recordingIdLabel = new Label(recordingGroup, SWT.NONE);
		recordingIdLabel.setText(" (-) ");
		
		pathRecordButton = new Button(recordingGroup,  SWT.CENTER);
		pathRecordButton.setText("  ScanPath  ");
		
		
		Group pointGroup = new Group(this, SWT.NONE);
		gridLayout.numColumns = 6;
		pointGroup.setLayout(gridLayout);
		new Label(pointGroup, SWT.NONE).setText(" Point: "); //$NON-NLS-1$
		
		prevPointButton = new Button(pointGroup,  SWT.CENTER);
		prevPointButton.setText("  <  ");
		prevPointButton.setEnabled(false);
		
		pointText = new Text(pointGroup,  SWT.CENTER);
		pointText.setText("  1  ");
		pointText.addModifyListener(new ModifyListener() {			
			public void modifyText(ModifyEvent e) {
				try {
					pointIndex = Integer.parseInt(pointText.getText().trim());
				} catch (Exception ex){
					pointText.setText("  " + Integer.toString(pointIndex) + "  ");
				}
					
			}
		});
		
		nextPointButton = new Button(pointGroup,  SWT.CENTER);
		nextPointButton.setText("  >  ");
		nextPointButton.setEnabled(false);

		coordinates = new Label(pointGroup, SWT.CENTER);
		coordinates.setText(" (000, 000) ");
		
		Group levelGroup = new Group(this, SWT.RIGHT);
		gridLayout.numColumns = 2;
		levelGroup.setLayout(gridLayout);
		new Label(levelGroup, SWT.NONE).setText(" Max. Level: ");
		levelText = new Text(levelGroup,  SWT.RIGHT_TO_LEFT);
		levelText.setText("  3  ");
		levelText.addModifyListener(new ModifyListener() {			
			public void modifyText(ModifyEvent e) {
				try {
					level = Integer.parseInt(levelText.getText().trim());
				} catch (Exception ex){
					levelText.setText("  " + Integer.toString(level) + "  ");
				}	
			}
		});
		
		gridData = new GridData();
		gridLayout.numColumns = 6;
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
	}

	public void incrementRecordingIndex(){
		if(recIndex < numberOfRecordings){
			setRecIndex(recIndex + 1);
			setPointIndex(1);
			if(!prevRecordButton.isEnabled())
				prevRecordButton.setEnabled(true);
			prevPointButton.setEnabled(false);
			if(numberOfPoints > 1)
				nextPointButton.setEnabled(true);
			
			if(recIndex == numberOfRecordings)
				nextRecordButton.setEnabled(false);
			currentRecording = map.get(recIndex - 1);
			retrievePoint(recIndex, pointIndex);
		}
	}
	
	public void decrementRecordingIndex(){
		if(recIndex > 1){
			setRecIndex(recIndex - 1);
			setPointIndex(1);
			if(!nextRecordButton.isEnabled())
				nextRecordButton.setEnabled(true);
			if(recIndex == 1)
				prevRecordButton.setEnabled(false);
			prevPointButton.setEnabled(false);
			if(numberOfPoints > 1)
				nextPointButton.setEnabled(true);
			currentRecording = map.get(recIndex - 1);
			retrievePoint(recIndex, pointIndex);
		}
	}
	
	public void incrementPointIndex(){
		if(pointIndex < numberOfPoints){
			setPointIndex(pointIndex + 1);
			retrievePoint(recIndex, pointIndex);
			if(!prevPointButton.isEnabled())
				prevPointButton.setEnabled(true);
			if(pointIndex == numberOfPoints)
				nextPointButton.setEnabled(false);
		}
	}
	
	public void decrementPointIndex(){
		if(pointIndex > 1){
			setPointIndex(pointIndex - 1);
			retrievePoint(recIndex, pointIndex);
			if(!nextPointButton.isEnabled())
				nextPointButton.setEnabled(true);
			if(pointIndex == 1)
				prevPointButton.setEnabled(false);
		}
	}

	public void retrievePoint(int recIndex, int pointIndex){
		numberOfPoints = currentRecording.getPointCount();
		currentFixation = currentRecording.getPointAt(pointIndex - 1);
		Point currentPoint = currentFixation.getPoint();
		coordinates.setText("(" + Integer.toString(currentPoint.x) + ", " + Integer.toString(currentPoint.y) + ") ");	
	}
	
	public void setRecIndex(int index){
		recIndex = index;
		recText.setText("  " + Integer.toString(recIndex) + "  ");
	}
	
	public void setPointIndex(int index){
		pointIndex = index;
		pointText.setText("  " + Integer.toString(pointIndex) + "  ");
	}
	
	public Fixation getCurrentFixation(){
		return currentFixation;
	}
	
	public Recording getCurrentRecording(){
		return currentRecording;
	}
	
	public Button getScanPathButton() {
		return pathRecordButton;
	}
	
	public Button getNextPointButton() {
		return nextPointButton;
	}

	public Button getPrevPointButton() {
		return prevPointButton;
	}

	public Button getNextRecordButton() {
		return nextRecordButton;
	}

	public Button getPrevRecordButton() {
		return prevRecordButton;
	}

	public Text getRecText() {
		return recText;
	}

	public Text getPointText() {
		return pointText;
	}

	public int getRecIndex() {
		return recIndex;
	}

	public int getPointIndex() {
		return pointIndex;
	}

	public int getNumberOfRecordings() {
		return numberOfRecordings;
	}

	public Label getCoordinates() {
		return coordinates;
	}
	
	public int getLevel(){
		return level;
	}

	public void setRecordingIdLabel() {
		if(currentRecording != null)
			recordingIdLabel.setText("(" + currentRecording.getId() + ") ");
	}
}
