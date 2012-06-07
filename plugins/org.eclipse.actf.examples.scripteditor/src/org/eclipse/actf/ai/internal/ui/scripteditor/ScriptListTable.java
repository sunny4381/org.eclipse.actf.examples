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

import java.util.ArrayList;

import org.eclipse.actf.ai.scripteditor.data.ScriptData;
import org.eclipse.actf.ai.ui.scripteditor.views.EditPanelView;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * 
 */
public class ScriptListTable {

	// ++ local data +++
	// Instance of parent Composite
	private EditPanelView instParentView;
	// Instance of parent Group
	private Composite instParentComposite;
	// Instance of Script data class
	private ScriptData instScriptData;

	// Instance of TableViewer
	private TableViewer instScriptListTblViewer;
	// Instance of Table
	private Table instScriptListTbl;

	// parameters during Preview movie
	private TableItem[] instTableItems;
	private int previousIndexScript = 0;
	private int currentIndexScript = 0;
	private Color onHighLight;
	private Color offHighLight;

	// parameters during multiple selection mode
	private boolean current_select_mode = false;

	/**
	 * Constructor
	 */
	public ScriptListTable(Composite parentComposite) {
		// Store instance of parent
		instParentView = EditPanelView.getInstance();
		instParentComposite = parentComposite;

		// Get current ScriptData
		instScriptData = ScriptData.getInstance();

		// Create new TableViewer for ScriptList
		instScriptListTblViewer = new TableViewer(instParentComposite,
				SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
		instScriptListTbl = instScriptListTblViewer.getTable();

		// Initialize Table
		initTable();

	}

	private void initTable() {

		// Set Layout for ScriptList Table
		FormData ScriptTableLData = new FormData();
		ScriptTableLData.top = new FormAttachment(0, 1000, 2);
		ScriptTableLData.left = new FormAttachment(0, 1000, 2);
		ScriptTableLData.right = new FormAttachment(1000, 1000, -2);
		ScriptTableLData.bottom = new FormAttachment(1000, 1000, -2);
		instScriptListTbl.setLayoutData(ScriptTableLData);

		// Initialize title of column of Table
		TableColumn col1 = new TableColumn(instScriptListTbl, SWT.CENTER);
		col1.setText("WAV");
		col1.setWidth(40);

		TableColumn col2 = new TableColumn(instScriptListTbl, SWT.CENTER);
		col2.setText("Extended");
		col2.setWidth(70);

		TableColumn col3 = new TableColumn(instScriptListTbl, SWT.LEFT);
		col3.setText("Start Time");
		col3.setWidth(78);

		TableColumn col4 = new TableColumn(instScriptListTbl, SWT.LEFT);
		col4.setText("Description");
		col4.setWidth(415);

		TableColumn col5 = new TableColumn(instScriptListTbl, SWT.LEFT);
		col5.setText("Comment");
		col5.setWidth(415);

		// Set visible table column
		instScriptListTbl.setHeaderVisible(true);
		instScriptListTbl.setLinesVisible(true);

		// Initialize Cell editor for Comment column
		String[] columnProperties = new String[] { "wav", "extended", "stime",
				"description", "comment" };
		instScriptListTblViewer.setColumnProperties(columnProperties);
		CellEditor[] cellEditors = new CellEditor[] {
				new TextCellEditor(instScriptListTbl),
				new TextCellEditor(instScriptListTbl),
				new TextCellEditor(instScriptListTbl),
				new TextCellEditor(instScriptListTbl),
				new TextCellEditor(instScriptListTbl) };
		// SetUP CellEditor into TableViewer
		instScriptListTblViewer.setCellEditors(cellEditors);
		// SetUP CellModifier into TableViewer
		instScriptListTblViewer.setCellModifier(new ScriptListCellModifier(
				instScriptListTblViewer));

		// Initialize ContentProvider Listener
		instScriptListTblViewer.setContentProvider(new ArrayContentProvider());
		instScriptListTblViewer.setLabelProvider(new ScriptListLabelProvider());

		// Initialize TableView Listeners
		instScriptListTblViewer
				.addSelectionChangedListener(new ScriptListSlectionChangedListener());

		// SetUP high-light color
		onHighLight = Display.getCurrent().getSystemColor(
				SWT.COLOR_TITLE_INACTIVE_BACKGROUND);
		offHighLight = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	}

	public void reloadScriptList() {

		// temporary items
		ArrayList<ScriptData> currentScriptList = new ArrayList<ScriptData>();
		// re-draw Script List
		ScriptData columnScriptData = null;

		// CleanUP current TableItem list
		if (instTableItems != null) {
			for (int i = 0; i < instTableItems.length; i++) {
				// TODO : Clear issue of disposing image resource.
				// ///instTableItems[i].dispose();
				instTableItems[i] = null;
			}
		}

		// re-load ScriptData list & re-draw Table
		int maxData = instScriptData.getLengthScriptList();
		// 1)Exist data(ScliptData)
		if (maxData > 0) {
			for (int i = 0; i < maxData; i++) {
				// temporary column data
				columnScriptData = new ScriptData();

				// PickUP Script Data from current ScriptData object.
				String nowScriptData = new String(
						instScriptData.getScriptData(i));
				int intScriptStartTime = instScriptData.getScriptStartTime(i);
				int intScriptEndTime = instScriptData.getScriptEndTime(i);

				// PickUP comment of description
				String strScriptComment = instScriptData.getScriptComment(i);

				// Append data for column
				columnScriptData.appendScriptData(nowScriptData,
						intScriptStartTime, intScriptEndTime, strScriptComment);
				// Append data to List for Table
				currentScriptList.add(columnScriptData);
			}
			// Input current elements
			instScriptListTblViewer.setInput(currentScriptList);
		}
		// 2)No data
		else {
			// Input null data
			instScriptListTblViewer.setInput(currentScriptList);
		}

		// SetUP new TableItem list
		instTableItems = instScriptListTbl.getItems();
	}

	public void setCurrentSelectionMode(boolean newMode) {
		// Update new selection mode
		current_select_mode = newMode;
	}

	/**
	 * @category Clear all high-light line
	 * 
	 */
	public void clearHighLightScriptLine() {
		// Clear default background color for all lines
		setBackgroundColorScriptLine(previousIndexScript, false);
		setBackgroundColorScriptLine(currentIndexScript, false);
		// Clear all index
		previousIndexScript = 0;
		currentIndexScript = 0;
	}

	/**
	 * @category SetUP high-light setting for next line
	 * @param nextIndex
	 *            : next index of current script list
	 */
	public void updateHighLightScriptLine(int nextIndex) {
		// Clear default background color for all lines
		setBackgroundColorScriptLine(currentIndexScript, false);
		// SetUP high-light setting for next line
		setCurrentScriptIndex(nextIndex);
		setBackgroundColorScriptLine(currentIndexScript, true);
	}

	/**
	 * @category Setter method : Update current high-light line of ScriptList
	 *           for Preview movie
	 * @param nextIndex
	 *            : next index of current script list
	 */
	private void setCurrentScriptIndex(int nextIndex) {
		// Check limit of current script list
		if ((nextIndex >= 0)
				&& (nextIndex < instScriptData.getLengthScriptList())) {
			// Store current value for erase current high-light
			previousIndexScript = currentIndexScript;
			// Update status of preview movie
			currentIndexScript = nextIndex;
		}
	}

	/**
	 * @category Set background color for target table line
	 * @param index
	 *            : index of target line
	 * @param swHighLight
	 *            : switch of high-light (TRUE:on, FALSE:off)
	 */
	private void setBackgroundColorScriptLine(int index, boolean swHighLight) {
		if (instTableItems != null && instTableItems.length > index) {
			// Change background color of target table item
			instTableItems[index].setBackground((swHighLight ? onHighLight
					: offHighLight));
		}
	}

	/**
	 * SelectionChangedListener
	 */
	class ScriptListSlectionChangedListener implements
			ISelectionChangedListener {
		/**
		 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
		 */
		public void selectionChanged(SelectionChangedEvent e) {
			// Store event data(Script Data)
			IStructuredSelection sel = (IStructuredSelection) e.getSelection();
			Object[] objs = sel.toArray();

			// Exist ScriptData?
			if (objs.length == 1) {
				// Singleton item selection mode
				selectSingleItem(e, (ScriptData) objs[0]);
			} else if (objs.length > 1) {
				// Multiple items selection mode
				selectMultiItems(objs);
			}
		}

		/**
		 * Selection single item from current table list
		 * 
		 * @param targetData
		 *            : instance of ScriptData of selection item
		 */
		private void selectSingleItem(SelectionChangedEvent e,
				ScriptData targetData) {
			// Search index of current ScriptData from ScriptList
			int index = instScriptData.searchScriptData(targetData
					.getScriptStartTime(0));
			// Exist ScriptData?
			if (index >= 0) {
				// Clear multiple selection mode
				if (current_select_mode) {
					current_select_mode = false;
					instParentView.getInstanceTabEditPanel()
							.endSelectMultiItems();
				}

				// Re-paint text of selected Script Data
				instParentView.getInstanceTabEditPanel().repaintTextScriptData(
						index);
				instParentView.getInstanceTabSelWAVFile()
						.repaintDescriptionStruct(index);
				// Setup TimeLine location
				TimeLineView.getInstance().reqUpdateLocationTimeLine(index);

				// TODO
				// If column is 'Comment', then not changed focus
				// if(e.get){
				{
					// Set Focus on description's text area
					// instParentView.getInstanceTabEditPanel().setFocusDescriptionTextArea();
				}
			}
		}

		/**
		 * Selection multiple items from current table list
		 * 
		 * @param targetDatas
		 *            [] : instance of ScriptData of selection items
		 */
		private void selectMultiItems(Object[] targetDatas) {
			// Check current mode flag
			if (!current_select_mode) {
				// SetUP multiple selection mode
				current_select_mode = true;
				// Exchange multiple items selection mode
				instParentView.getInstanceTabEditPanel().startSelectMultiItems(
						targetDatas);
			} else {
				// Append(Modify) selection items
				instParentView.getInstanceTabEditPanel()
						.appendSelectMultiItems(targetDatas);
			}
		}
	}

}
