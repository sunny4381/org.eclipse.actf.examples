/*******************************************************************************
 * Copyright (c) 2011, 2012 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.ai.internal.ui.scripteditor.guidelist;

import java.io.File;
import java.net.URI;

import org.eclipse.actf.ai.internal.ui.scripteditor.ComboBoxCellEditorEx;
import org.eclipse.actf.ai.internal.ui.scripteditor.Validator;
import org.eclipse.actf.ai.scripteditor.data.IScriptData;
import org.eclipse.actf.ai.scripteditor.data.ScriptDataManager;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Item;

public class GuideCellModifier implements ICellModifier {
	private TreeViewer viewer;

	private ScriptDataManager scriptManager;

	public GuideCellModifier(TreeViewer viewer, ScriptDataManager scriptManager) {
		this.viewer = viewer;
		this.scriptManager = scriptManager;
	}

	public boolean canModify(Object element, String property) {
		// TODO change by type

		if (property == IGuideListConstants.START_TIME)
			return false;
		if (property == IGuideListConstants.END_TIME)
			return false; // TODO true for caption, script or prepare editor
		if (property == IGuideListConstants.CHARACTER)
			return true;
		if (property == IGuideListConstants.SCENARIO)
			return true;
		if (property == IGuideListConstants.CAPTION)
			return true;
		if (property == IGuideListConstants.WAV)
			return true;
		if (property == IGuideListConstants.EXTENDS)
			return true;
		if (property == IGuideListConstants.DESCRIPTION)
			return true;
		if (property == IGuideListConstants.COMMENT)
			return true;

		return false;
	}

	public Object getValue(Object element, String property) {
		IScriptData item = (IScriptData) element;

		if (property == IGuideListConstants.START_TIME) {
			return item.getStartTimeString();
		}
		if (property == IGuideListConstants.END_TIME) {
			if (item.isWavEnabled()) {
				return item.getWavEndTimeString();
			} else {
				return item.getEndTimeString();
			}
		}
		if (property == IGuideListConstants.CHARACTER) {
			int index = 0;

			index = scriptManager.getCharacterList().indexOf(
					item.getCharacter());
			if (item.getComboIndex() == -1) {
				return 0;
			} else if (ComboBoxCellEditorEx.updateFlag) {
				return new Integer(ComboBoxCellEditorEx.COMBO_ITEM.length);
			}
			return new Integer(index);
		}
		if (property == IGuideListConstants.SCENARIO) {
			return item.getScenario();
		}
		if (property == IGuideListConstants.CAPTION) {
			return item.getCaption();
		}
		if (property == IGuideListConstants.WAV) {
			if (item.getWavURI() != null) {
				return new File(item.getWavURI()).getName();
			} else {
				return "";
			}
		}
		if (property == IGuideListConstants.EXTENDS) {
			if (item.isExtended()) {
				return new Integer(0);
			} else {
				return new Integer(1);
			}
		}
		if (property == IGuideListConstants.DESCRIPTION) {
			return item.getDescription();
		}
		if (property == IGuideListConstants.COMMENT) {
			return item.getScriptComment();
		}
		return null;
	}

	public void modify(Object element, String property, Object value) {
		if (element instanceof Item) {
			element = ((Item) element).getData();
		}
		IScriptData item = (IScriptData) element;

		if (property == IGuideListConstants.START_TIME) {
			String data = Validator.format((String) value);
			if (Validator.checkTimeFormat(data)) {
				item.setStartTimeString((String) value);
			}

		}
		if (property == IGuideListConstants.END_TIME) {
			// TODO check can't modify manually
			if (item.isWavEnabled()) {
				item.setWavEndTimeString((String) value);
			} else {
				item.setEndTimeString((String) value);
			}
		}
		if (property == IGuideListConstants.CHARACTER) {
			if (((Integer) value).intValue() == -1) {
				item.setComboIndex(0);
				item.setCharacter(ComboBoxCellEditorEx.COMBO_ITEM[0]);

			} else if (ComboBoxCellEditorEx.updateFlag) {
				ComboBoxCellEditorEx.updateFlag = false;

				item.setComboIndex(ComboBoxCellEditorEx.COMBO_ITEM.length - 1);
				item.setCharacter(ComboBoxCellEditorEx.COMBO_ITEM[ComboBoxCellEditorEx.COMBO_ITEM.length - 1]);

			} else {
				item.setComboIndex(((Integer) value).intValue());
				item.setCharacter(ComboBoxCellEditorEx.COMBO_ITEM[((Integer) value)
						.intValue()]);
			}
		}
		if (property == IGuideListConstants.SCENARIO) {
			item.setScenario((String) value);
		}
		if (property == IGuideListConstants.CAPTION) {
			item.setCaption((String) value);
		}
		if (property == IGuideListConstants.WAV) {
			try {
				// TODO check instance, file can read
				if (((String) value).length() != 0) {
					if (new URI((String) value).isAbsolute()) {
						item.setWavEnabled(new Boolean(true));
						item.setWavURI(new URI((String) value));
					}
				} else {
					item.setWavEnabled(new Boolean(false));
				}
			} catch (Exception e) {

			}

		}

		if (property == IGuideListConstants.EXTENDS) {
			if (((Integer) value) == 0) {
				item.setExtended(new Boolean(true));
			} else {
				item.setExtended(new Boolean(false));
			}

		}
		if (property == IGuideListConstants.DESCRIPTION) {
			item.setDescription((String) value);
		}

		if (property == IGuideListConstants.COMMENT) {
			item.setScriptComment((String) value);

		}
		viewer.update(item, null);
	}
}
