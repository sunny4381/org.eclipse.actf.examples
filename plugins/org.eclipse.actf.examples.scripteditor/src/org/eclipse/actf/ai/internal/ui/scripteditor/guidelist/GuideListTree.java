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
package org.eclipse.actf.ai.internal.ui.scripteditor.guidelist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.actf.ai.internal.ui.scripteditor.ComboBoxCellEditorEx;
import org.eclipse.actf.ai.internal.ui.scripteditor.EditPanelTab;
import org.eclipse.actf.ai.internal.ui.scripteditor.Validator;
import org.eclipse.actf.ai.internal.ui.scripteditor.VolumeLevelCanvas;
import org.eclipse.actf.ai.internal.ui.scripteditor.WavInputDialog;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.EventManager;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.SyncTimeEvent;
import org.eclipse.actf.ai.scripteditor.data.DataUtil;
import org.eclipse.actf.ai.scripteditor.data.IScriptData;
import org.eclipse.actf.ai.scripteditor.data.ScriptDataFactory;
import org.eclipse.actf.ai.scripteditor.data.ScriptDataManager;
import org.eclipse.actf.ai.scripteditor.data.event.DataEventManager;
import org.eclipse.actf.ai.scripteditor.data.event.GuideListEvent;
import org.eclipse.actf.ai.scripteditor.data.event.GuideListEventListener;
import org.eclipse.actf.ai.scripteditor.data.event.LabelEvent;
import org.eclipse.actf.ai.scripteditor.util.VoicePlayerFactory;
import org.eclipse.actf.ai.scripteditor.util.WebBrowserFactory;
import org.eclipse.actf.ai.scripteditor.util.XMLFileMessageBox;
import org.eclipse.actf.ai.scripteditor.util.XMLFileSaveUtil;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * 
 */
public class GuideListTree implements IUNIT, GuideListEventListener {

	private static GuideListTree ownInst = null;

	private ScrolledComposite parentComp;
	private TreeViewer treeViewer;
	private Tree instTree;

	private boolean isUser = true;

	// data manager
	private ScriptDataManager scriptManager = null;
	private TreeColumn column0 = null; // check
	private TreeColumn column1 = null; // mark
	private TreeColumn column2 = null; // Start Time
	private TreeColumn column3 = null; // End Time
	private TreeColumn column4 = null; // character
	private TreeColumn column5 = null; // Scenario
	private TreeColumn column6 = null; // Description
	private TreeColumn column7 = null; // Caption
	private TreeColumn column8 = null; // Caption
	private TreeColumn column9 = null; // Wav
	private TreeColumn column10 = null; // Comment

	private ComboBoxCellEditorEx characterComboCell = null;

	boolean headerSelectFlag = false;

	private EventManager eventManager = null;
	private DataEventManager dataEventManager = null;
	private ScriptSorter sorter = new ScriptSorter();

	private class ColumnSelectionAdapter extends SelectionAdapter {
		private int column;

		public ColumnSelectionAdapter(int column) {
			this.column = column;
		}

		public void widgetSelected(SelectionEvent arg0) {
			treeViewer.setSelection(null);
			if (sorter instanceof ScriptSorter) {
				((ScriptSorter) sorter).setCurColumn(column);
			}
			treeViewer.refresh();
		}
	}

	/**
	 * Constructor
	 */
	public GuideListTree(ScrolledComposite parentComposite) {
		ownInst = this;
		parentComp = parentComposite;

		treeViewer = new TreeViewer(parentComp, SWT.FULL_SELECTION
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER); // TODO | SWT.MULTI
		instTree = treeViewer.getTree();// Table();
		instTree.setLinesVisible(true);
		treeViewer.setComparator(new GuildListComparator());
		treeViewer.setSorter(sorter);

		eventManager = EventManager.getInstance();
		scriptManager = ScriptDataManager.getInstance();

		dataEventManager = DataEventManager.getInstance();
		dataEventManager.addGuideListEventListener(ownInst);
		parentComposite.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				// TODO other components
				dataEventManager.removeGuideListEventListener(ownInst);
			}
		});
		initTable();
	}

	private void selectAllColumn(TreeColumn column, int style) {
		instTree.setSortColumn(column);
		instTree.setSortDirection(style);
	}

	private void selectGuideLineItemProcess(IScriptData paramData) {
		IScriptData data = paramData;

		TimeLineView timeLineView = TimeLineView.getInstance();
		if (timeLineView != null
				&& timeLineView.getStatusTimeLine() == TL_STAT_PAUSE) {
			// Synchronize all TimeLine
			eventManager.fireSyncTimeEvent(new SyncTimeEvent(
					SyncTimeEvent.ADJUST_TIME_LINE, data.getStartTime(), this));
		}
		dataEventManager.fireGuideListEvent(new GuideListEvent(
				GuideListEvent.SET_DATA, data, this));

		// Initialize sampling duration.
		VolumeLevelCanvas.getInstance().clearSamplingLengthVolumeLevel();
	}

	private void initTable() {

		FormData scriptTreeData = new FormData();
		scriptTreeData.top = new FormAttachment(0, 1000, 2);
		scriptTreeData.left = new FormAttachment(0, 1000, 2);
		scriptTreeData.right = new FormAttachment(1000, 1000, -2);
		scriptTreeData.bottom = new FormAttachment(1000, 1000, 0);
		instTree.setLayoutData(scriptTreeData);
		instTree.setLinesVisible(true);
		instTree.setHeaderVisible(true);

		column0 = new TreeColumn(instTree, SWT.LEFT);// dummy
		column0.setText("");
		column0.setWidth(0);
		column0.setResizable(false);

		column1 = new TreeColumn(instTree, SWT.LEFT);
		column1.setText("");
		column1.setWidth(20);
		column1.setResizable(false);
		column1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (instTree.getItemCount() != 0) {
					headerSelectFlag = true;

					selectAllColumn(column1, SWT.UP);
				}
			}
		});

		// Start Time
		column2 = new TreeColumn(instTree, SWT.NONE);
		column2.setText("Start Time");
		column2.setWidth(130);

		// End Time
		column3 = new TreeColumn(instTree, SWT.NONE);
		column3.setText("End Time");
		column3.setWidth(130);

		// character
		column4 = new TreeColumn(instTree, SWT.NONE);
		column4.setText("Speaker");
		column4.setWidth(120);

		// Scenario
		column5 = new TreeColumn(instTree, SWT.NONE);
		column5.setText("Scenario");
		column5.setWidth(215);

		// Description
		column6 = new TreeColumn(instTree, SWT.NONE);
		column6.setText("Audio Description");
		column6.setWidth(400);
		column6.setResizable(true);

		// Caption
		column7 = new TreeColumn(instTree, SWT.NONE);
		column7.setText("Caption");
		column7.setWidth(215);

		// Extended
		column8 = new TreeColumn(instTree, SWT.NONE);
		column8.setText("Extended");
		column8.setWidth(78);

		// Wav
		column9 = new TreeColumn(instTree, SWT.NONE);
		column9.setText("Wav");
		column9.setWidth(98);

		// Comment
		column10 = new TreeColumn(instTree, SWT.NONE);
		column10.setText("Comment");
		column10.setWidth(215);

		// GuideLabelProvider
		String[] properties = new String[] { "dummy", IGuideListConstants.MARK,
				IGuideListConstants.START_TIME, IGuideListConstants.END_TIME,
				IGuideListConstants.CHARACTER, IGuideListConstants.SCENARIO,
				IGuideListConstants.DESCRIPTION, IGuideListConstants.CAPTION,
				IGuideListConstants.EXTENDS, IGuideListConstants.WAV,
				IGuideListConstants.COMMENT };

		treeViewer.setColumnProperties(properties);

		// StartTime
		final TextCellEditor startTimeTextCell = new TextCellEditor(instTree);
		// EndTime
		final TextCellEditorEx endTimeTextCell = new TextCellEditorEx(instTree);

		// Speaker
		characterComboCell = new ComboBoxCellEditorEx(instTree, scriptManager
				.getCharacterList().toArray(new String[0]));
		((CCombo) characterComboCell.getControl())
				.addFocusListener(new FocusAdapter() {
					String beforeCharacter = "";
					IScriptData data = null;

					public void focusGained(FocusEvent e) {
						TreeSelection selection = (TreeSelection) treeViewer
								.getSelection();
						data = (IScriptData) selection.getFirstElement();

						beforeCharacter = ((CCombo) (e.widget)).getText();
						selectGuideLineItemProcess(data);
						if (data.getType() == IScriptData.TYPE_AUDIO) {
							Display.getCurrent().asyncExec(new Runnable() {
								public void run() {
									characterComboCell.deactivate();
								}
							});
							return;
						}
					}

					public void focusLost(FocusEvent e) {
						String character = ((CCombo) (e.widget)).getText();

						if (beforeCharacter.equals(character)) {
							return;
						}

						data.setCharacter(character);
						data.setDataCommit(true);
						dataEventManager.fireGuideListEvent(new GuideListEvent(
								GuideListEvent.REPALCE_DATA, data, this));
					}
				});

		// Scenario
		final TextCellEditorEx scenarioTextCell = new TextCellEditorEx(instTree);
		((Text) scenarioTextCell.getControl())
				.addFocusListener(new FocusAdapter() {
					String beforeScenario = "";
					IScriptData data = null;

					public void focusGained(FocusEvent e) {
						TreeSelection selection = (TreeSelection) treeViewer
								.getSelection();
						data = (IScriptData) selection.getFirstElement();
						selectGuideLineItemProcess(data);

						if (data.getType() != IScriptData.TYPE_SCENARIO) {
							Display.getCurrent().asyncExec(new Runnable() {
								public void run() {
									scenarioTextCell.deactivate();
								}
							});
							return;
						}
						beforeScenario = ((Text) (e.widget)).getText();
					}

					public void focusLost(FocusEvent e) {
						String scenario = ((Text) (e.widget)).getText();
						if (beforeScenario.equals(scenario)) {
							return;
						}
						data.setScenario(scenario);
						data.setDataCommit(true);
						dataEventManager.fireGuideListEvent(new GuideListEvent(
								GuideListEvent.REPALCE_DATA, data, this));

					}
				});

		// Description
		final TextCellEditorEx descriptionTextCell = new TextCellEditorEx(
				instTree);
		((Text) descriptionTextCell.getControl())
				.addFocusListener(new FocusAdapter() {
					String beforeDescription = "";
					IScriptData data = null;

					public void focusGained(FocusEvent e) {
						Display.getCurrent().asyncExec(new Runnable() {
							public void run() {
								descriptionTextCell.activate();
							}
						});
						TreeSelection selection = (TreeSelection) treeViewer
								.getSelection();
						data = (IScriptData) selection.getFirstElement();
						beforeDescription = ((Text) (e.widget)).getText();

						selectGuideLineItemProcess(data);

						if (data.getType() != IScriptData.TYPE_AUDIO
								&& !(data.getType() == IScriptData.TYPE_SCENARIO && data
										.isDataCommit() == false)) {
							Display.getCurrent().asyncExec(new Runnable() {
								public void run() {
									descriptionTextCell.deactivate();
								}
							});
							return;
						}
					}

					public void focusLost(FocusEvent e) {
						String description = ((Text) (e.widget)).getText();
						if (beforeDescription.equals(description)) {
							return;
						}
						dataEventManager.fireLabelEvent(new LabelEvent(
								LabelEvent.DELETE_LABEL, data, this));
						dataEventManager.fireGuideListEvent(new GuideListEvent(
								GuideListEvent.DELETE_DATA, data, this));

						data.setType(IScriptData.TYPE_AUDIO);
						data.setMark(NO_MARK);
						data.setDescription(description);

						int newEndTime;
						int length = VoicePlayerFactory.getInstance()
								.getSpeakLength(data);
						if (length > 0) {
							newEndTime = data.getStartTime() + length;
							data.setEndTimeAccurate(true);
						} else {
							newEndTime = data.getStartTime()
									+ DataUtil.sumMoraCount(
											data.getDescription(),
											data.getLang());
							data.setEndTimeAccurate(false);
						}

						data.setEndTime(newEndTime);
						data.setDataCommit(true);

						dataEventManager.fireLabelEvent(new LabelEvent(
								LabelEvent.PUT_LABEL, data, this));
						dataEventManager.fireGuideListEvent(new GuideListEvent(
								GuideListEvent.ADD_DATA, data, this));

					}
				});

		//
		// Caption
		// -------------------------------------------------------------------------------
		//
		final TextCellEditorEx captionTextCell = new TextCellEditorEx(instTree);
		((Text) captionTextCell.getControl())
				.addFocusListener(new FocusAdapter() {
					String beforeCaption = "";
					IScriptData data = null;
					TreeItem[] item;

					public void focusGained(FocusEvent e) {
						TreeSelection selection = (TreeSelection) treeViewer
								.getSelection();
						data = (IScriptData) selection.getFirstElement();
						item = instTree.getSelection();
						beforeCaption = ((Text) (e.widget)).getText();

						selectGuideLineItemProcess(data);

						if (data.getType() != IScriptData.TYPE_CAPTION
								&& !(data.getType() == IScriptData.TYPE_SCENARIO && data
										.isDataCommit() == false)) {
							Display.getCurrent().asyncExec(new Runnable() {
								public void run() {
									captionTextCell.deactivate();
								}
							});
							return;
						}
					}

					public void focusLost(FocusEvent e) {
						String caption = ((Text) (e.widget)).getText();
						if (beforeCaption.equals(caption)) {
							return;
						}

						dataEventManager.fireLabelEvent(new LabelEvent(
								LabelEvent.DELETE_LABEL, data, this));
						dataEventManager.fireGuideListEvent(new GuideListEvent(
								GuideListEvent.DELETE_DATA, data, this));

						data.setType(IScriptData.TYPE_CAPTION);
						data.setMark(NO_MARK);
						data.setCaption(caption);
						if (data.getEndTime() <= data.getStartTime()) {
							// TODO need input from user
							int endTime = DataUtil.sumMoraCount(
									data.getCaption(), data.getLang());
							data.setEndTime(data.getStartTime() + endTime);
						}

						data.setDataCommit(true);

						dataEventManager.fireLabelEvent(new LabelEvent(
								LabelEvent.PUT_LABEL, data, this));
						dataEventManager.fireGuideListEvent(new GuideListEvent(
								GuideListEvent.ADD_DATA, data, this));

					}
				});
		//
		// Extended
		// -------------------------------------------------------------------------------
		//
		final ComboBoxCellEditor extendsComboCell = new ComboBoxCellEditor(
				instTree, new String[] { "Extended", "Normal" });
		((CCombo) extendsComboCell.getControl())
				.addFocusListener(new FocusAdapter() {
					String beforeExtends = "";
					IScriptData data = null;

					public void focusGained(FocusEvent e) {
						TreeSelection selection = (TreeSelection) treeViewer
								.getSelection();
						data = (IScriptData) selection.getFirstElement();

						beforeExtends = ((CCombo) (e.widget)).getText();
						selectGuideLineItemProcess(data);
						if (data.getType() != IScriptData.TYPE_AUDIO) {
							Display.getCurrent().asyncExec(new Runnable() {
								public void run() {
									extendsComboCell.deactivate();
								}
							});
							return;
						}
					}

					public void focusLost(FocusEvent e) {
						String extended = ((CCombo) (e.widget)).getText();
						if (beforeExtends.equals(extended)) {
							return;
						}

						if (data.getType() == IScriptData.TYPE_AUDIO) {
							data.setExtended("Extended".equals(extended));
							dataEventManager.fireLabelEvent(new LabelEvent(
									LabelEvent.PUT_LABEL, data, this));
						}
					}
				});

		//
		// Wav
		// -------------------------------------------------------------------------------
		//
		final TextCellEditorEx wavTextCell = new TextCellEditorEx(instTree);
		((Text) wavTextCell.getControl()).addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				Display.getCurrent().asyncExec(new Runnable() {
					public void run() {
						endTimeTextCell.activate();
					}
				});

				try {
					Thread.sleep(100);
					TreeSelection selection = (TreeSelection) treeViewer
							.getSelection();
					final IScriptData data = (IScriptData) selection
							.getFirstElement();
					selectGuideLineItemProcess(data);
					if (data.getType() != IScriptData.TYPE_AUDIO) {
						return;
					}
					if (data.isDataCommit() == false) {
						return;
					}
					// make WAV input Dialog.
					final WavInputDialog dialog = new WavInputDialog(Display
							.getCurrent().getActiveShell(), data);
					//
					Display.getCurrent().asyncExec(new Runnable() {
						public void run() {
							dialog.open();
							if (dialog.getReturnCode() == WavInputDialog.OK) {
								if (data.getType() == IScriptData.TYPE_SCENARIO) {
									updateLine(dialog.getData());
									return;
								}

								if (!Validator.checkNull(data
										.getStartTimeString())) {

									dataEventManager
											.fireGuideListEvent(new GuideListEvent(
													GuideListEvent.DELETE_DATA,
													data, this));

									dataEventManager
											.fireLabelEvent(new LabelEvent(
													LabelEvent.DELETE_LABEL,
													data, this));
									data.setDataCommit(true);

									dataEventManager
											.fireGuideListEvent(new GuideListEvent(
													GuideListEvent.ADD_DATA,
													data, this));

									dataEventManager
											.fireLabelEvent(new LabelEvent(
													LabelEvent.PUT_LABEL, data,
													this));

								}

							} else if (dialog.getReturnCode() == WavInputDialog.DELETE) {
								dataEventManager
										.fireGuideListEvent(new GuideListEvent(
												GuideListEvent.DELETE_DATA,
												data, this));

								dataEventManager.fireLabelEvent(new LabelEvent(
										LabelEvent.DELETE_LABEL, data, this));

								dataEventManager
										.fireGuideListEvent(new GuideListEvent(
												GuideListEvent.ADD_DATA, data,
												this));
								dataEventManager.fireLabelEvent(new LabelEvent(
										LabelEvent.PUT_LABEL, data, this));

							}
						}
					});

				} catch (Exception ee) {

				}

			}

			public void focusLost(FocusEvent e) {
				wavTextCell.focusLost();
			}
		});

		//
		// Comment
		// -------------------------------------------------------------------------------
		//
		TextCellEditorEx commentTextCell = new TextCellEditorEx(instTree);
		((Text) commentTextCell.getControl())
				.addFocusListener(new FocusAdapter() {
					String beforeComment = "";
					IScriptData data = null;

					public void focusGained(FocusEvent e) {
						TreeSelection selection = (TreeSelection) treeViewer
								.getSelection();
						data = (IScriptData) selection.getFirstElement();

						beforeComment = ((Text) (e.widget)).getText();
						selectGuideLineItemProcess(data);
					}

					public void focusLost(FocusEvent e) {
						String comment = ((Text) (e.widget)).getText();
						if (beforeComment.equals(comment)) {
							return;
						}
						data.setScriptComment(comment);
						dataEventManager.fireGuideListEvent(new GuideListEvent(
								GuideListEvent.REPALCE_DATA, data, this));
						DataUtil.debug();
					}
				});

		CellEditor[] editors = new CellEditor[] { new TextCellEditor(instTree),
				new TextCellEditor(instTree), startTimeTextCell,
				endTimeTextCell, characterComboCell, scenarioTextCell,
				descriptionTextCell, captionTextCell, extendsComboCell,
				wavTextCell, commentTextCell };

		treeViewer.setCellEditors(editors);
		treeViewer.setCellModifier(new GuideCellModifier(treeViewer,
				scriptManager));
		treeViewer.setContentProvider(new GuideListContentProvider());

		treeViewer.setLabelProvider(new GuideLabelProvider());
		createContextMenu(instTree);
		treeViewer
				.addSelectionChangedListener(new ScriptListSlectionChangedListener());

		treeViewer.expandAll();

		parentComp.setContent(instTree);
		final int clientWidth = parentComp.getClientArea().width;
		int prefHeight = instTree.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		instTree.setSize(instTree.computeSize(clientWidth, prefHeight));

		List<IScriptData> lists = new ArrayList<IScriptData>();
		IScriptData data1 = ScriptDataFactory.createNewData();
		data1.setType(IScriptData.TYPE_AUDIO);
		lists.add(data1);
		treeViewer.setInput(lists);
		treeViewer.remove(lists, 0);

	}

	void createContextMenu(Control control) {
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);

		menuManager.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager menu) {
				if (headerSelectFlag) {
					headerSelectFlag = false;
					menu.add(new InsertDownAction());

				} else if (treeViewer.getSelection().isEmpty()
						&& instTree.getItemCount() == 0) {
					// menu.add(new InsertUpAction());
					// TODO recover
					menu.add(new InsertDownAction());

				} else if (!treeViewer.getSelection().isEmpty()) {
					if (instTree.getSelection().length < 2) {
						// menu.add(new InsertUpAction());
						// TODO recover
						// menu.add(new InsertDownAction());
					}
					menu.add(new DeleteAction());
				}
			}
		});

		Menu menu = menuManager.createContextMenu(control);
		control.setMenu(menu);
	}

	private void updateLine(IScriptData data) {

		treeViewer.refresh();

		// SetUP status to Edit start mode
		int stat = XMLFileMessageBox.MB_STYLE_MODIFY;
		String filePath = XMLFileSaveUtil.getInstance().getFilePath();
		if (filePath != null) {
			stat = XMLFileMessageBox.MB_STYLE_OVERWR;
		}
		scriptManager.setSaveRequired(stat, true);
	}

	private void addLine(IScriptData data) {
		scriptManager.add(data); // TODO move to other
		updateLine(data);
	}

	private void deleteLine(IScriptData data) {
		scriptManager.remove(data); // TODO move to other
		updateLine(data);
	}

	class InsertDownAction extends Action {
		InsertDownAction() {
			setText("Insert");
		}

		public void run() {
			// if (dataList != null && dataList.size() != 0) {
			if (instTree.getItemCount() != 0) {
				TreeItem[] items = instTree.getSelection();

				IScriptData data = null;
				data = ScriptDataFactory.createNewData();
				data.setType(IScriptData.TYPE_SCENARIO);
				data.setStartTimeString(((IScriptData) items[0].getData())
						.getStartTimeString());
				data.setEndTimeString("");
				data.setWavEndTimeString("");
				data.setMark(NO_MARK); // TOP ITEM NO MARK
				data.setDataCommit(false);

				dataEventManager.fireGuideListEvent(new GuideListEvent(
						GuideListEvent.ADD_DATA, data, this));

				treeViewer.setSelection(new StructuredSelection(data), true);

			} else {
				IScriptData data = null;
				data = ScriptDataFactory.createNewData();
				data.setType(IScriptData.TYPE_SCENARIO);
				data.setEndTimeString("");
				data.setWavEndTimeString("");
				data.setMark(NO_MARK);
				//
				dataEventManager.fireGuideListEvent(new GuideListEvent(
						GuideListEvent.ADD_DATA, data, this));
			}
		}
	}

	/**
	 * 
	 */
	public void copyCaption() {
		if (TimeLineView.getInstance() != null) {
			if (TimeLineView.getInstance().getStatusTimeLine() == TL_STAT_PLAY) {
				Display.getCurrent().asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openError(Display.getCurrent()
								.getActiveShell(), "Error",
								"can not copy whene playing movie");
					}
				});
				return;
			}
		}
		List<IScriptData> list = scriptManager
				.getDataList(IScriptData.TYPE_SCENARIO);
		if (list.size() == 0) {
			return;
		}

		dataEventManager.fireLabelEvent(new LabelEvent(LabelEvent.CLEAR_LABEL,
				null, this));
		for (IScriptData data : list) {
			if (data.isDataCommit() == true
					&& !Validator.checkNull(data.getScenario().trim())) {

				dataEventManager.fireGuideListEvent(new GuideListEvent(
						GuideListEvent.DELETE_DATA, data, this));
				data.setType(IScriptData.TYPE_CAPTION);
				data.setCaption(data.getScenario());
				data.setScenario("");
				data.setMark(NO_MARK);
				if (data.getEndTime() < data.getStartTime()) {
					int endTime = DataUtil.sumMoraCount(data.getCaption(),
							data.getLang());
					data.setEndTime(data.getStartTime() + endTime);
				}
				// ADD_AUDIO_DATA Event make clone data, therefore you do not
				// need change clone.
				dataEventManager.fireGuideListEvent(new GuideListEvent(
						GuideListEvent.ADD_DATA, data, this));
				// TODO : for child process
			}
		}
		dataEventManager.fireLabelEvent(new LabelEvent(
				LabelEvent.PUT_ALL_LABEL, null, this));
	}

	class DeleteAction extends Action implements IUNIT {
		int result = -1;

		DeleteAction() {
			setText("Delete");
		}

		public void run() {
			final TreeItem[] items = instTree.getSelection();

			Display.getCurrent().asyncExec(new Runnable() {
				public void run() {
					for (int i = 0; i < items.length; i++) {
						if (items[i].isDisposed()) {
							continue;
						}

						IScriptData data = (IScriptData) items[i].getData();
						dataEventManager.fireGuideListEvent(new GuideListEvent(
								GuideListEvent.DELETE_DATA, data, this));
						dataEventManager.fireLabelEvent(new LabelEvent(
								LabelEvent.DELETE_LABEL, data, this));

					}
				}
			});
		}

	}

	private TreeColumn getTableColumn(String columnName) {
		if (columnName.equals(IGuideListConstants.MARK)) {
			return column1;
		} else if (columnName.equals(IGuideListConstants.START_TIME)) {
			return column2;
		} else if (columnName.equals(IGuideListConstants.END_TIME)) {
			return column3;
		} else if (columnName.equals(IGuideListConstants.CHARACTER)) {
			return column4;
		} else if (columnName.equals(IGuideListConstants.SCENARIO)) {
			return column5;
		} else if (columnName.equals(IGuideListConstants.DESCRIPTION)) {
			return column6;
		} else if (columnName.equals(IGuideListConstants.CAPTION)) {
			return column7;
		} else if (columnName.equals(IGuideListConstants.EXTENDS)) {
			return column8;
		} else if (columnName.equals(IGuideListConstants.WAV)) {
			return column9;
		} else if (columnName.equals(IGuideListConstants.COMMENT)) {
			return column10;
		}
		return null;

	}

	public void hideColumn(String columnName) {
		TreeColumn column = getTableColumn(columnName);
		column.setWidth(0);
		column.setResizable(false);

	}

	public void showColumn(String columnName) {
		showColumn(columnName, 300);
	}

	public void showColumn(String columnName, int maxWidth) {
		if (maxWidth < 0) {
			maxWidth = 0;
		}
		TreeColumn column = getTableColumn(columnName);
		column.pack();
		if (column.getWidth() > maxWidth) {
			column.setWidth(maxWidth);
		}
		// TODO
		if (columnName.equals(IGuideListConstants.START_TIME)
				|| columnName.equals(IGuideListConstants.END_TIME)) {
			if (column.getWidth() < 100) {
				column.setWidth(100);
			}
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

			VolumeLevelCanvas.getInstance().clearSamplingLengthVolumeLevel();

			// Store event data(Script Data)
			IStructuredSelection sel = (IStructuredSelection) e.getSelection();
			@SuppressWarnings("unchecked")
			List<IScriptData> list = sel.toList();

			// Exist ScriptData?
			if (list.size() == 1) {
				selectAllColumn(column2, SWT.NONE);
			} else if (list.size() > 1) {
				selectAllColumn(column2, SWT.NONE);
				if (EditPanelTab.getInstance() != null) {
					EditPanelTab.getInstance().setMultiSelectMode(true);
					EditPanelTab.getInstance().setCurrentListData(list);
				}
			} else {
				return;
			}
			if (isUser && e.getSource() != this) {
				int newPosition = list.iterator().next().getStartTime() - 10;
				if (newPosition < 0) {
					newPosition = 0;
				}
				WebBrowserFactory.getInstance().pauseMedia();
				WebBrowserFactory.getInstance().setCurrentPosition(newPosition);
			}
		}
	}

	public void handleGuideListEvent(GuideListEvent e) {
		switch (e.getEventType()) {
		case GuideListEvent.SET_DATA:
		case GuideListEvent.PLAY_LABEL:
			isUser = false;
			IScriptData data = e.getData();
			treeViewer.setSelection(new StructuredSelection(data), true);
			isUser = true;
			break;
		case GuideListEvent.DESELECT_DATA:
			treeViewer.setSelection(null, false);
			break;
		case GuideListEvent.ADD_DATA:
			addLine(e.getData());
			break;
		case GuideListEvent.REPALCE_DATA:
			updateLine(e.getData());
			break;
		case GuideListEvent.DELETE_DATA:
			deleteLine(e.getData());
			break;
		case GuideListEvent.CLEAR_DATA:
			treeViewer.getTree().removeAll();
			scriptManager.clearData();
			break;
		}

	}
}
