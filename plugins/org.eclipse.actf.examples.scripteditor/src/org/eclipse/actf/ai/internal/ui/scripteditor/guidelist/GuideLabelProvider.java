/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and Others
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

import org.eclipse.actf.ai.scripteditor.data.IScriptData;
import org.eclipse.actf.ai.scripteditor.data.ScriptData;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.examples.scripteditor.Activator;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class GuideLabelProvider extends LabelProvider implements
		ITableLabelProvider, IUNIT {
	static Image mark1 = Activator.getImageDescriptor("/icons/mark1.bmp")
			.createImage();
	static Image mark2 = Activator.getImageDescriptor("/icons/mark2.bmp")
			.createImage();
	static Image c_no_mark = Activator.getImageDescriptor(
			"/icons/c_no_mark.bmp").createImage();
	static Image c_caption_mark = Activator.getImageDescriptor(
			"/icons/c_caption_mark.bmp").createImage();

	public Image getColumnImage(Object element, int columnIndex) {

		IScriptData item = (IScriptData) element;

		Image result = null;
		switch (columnIndex) {
		case 1:
			int mark = item.getMark();
			if (item instanceof ScriptData) {
				if (mark == PLAY_MARK) {
					result = mark1;
				} else if (mark == CAPTION_MARK) {
					result = mark2;
				}
			} else {
				if (mark == NO_MARK) {
					result = c_no_mark;
				} else if (mark == CAPTION_MARK) {
					result = c_caption_mark;
				}
			}
			break;
		}

		return result;
	}

	public String getColumnText(Object element, int columnIndex) {
		IScriptData item = (IScriptData) element;
		String result = "";

		switch (columnIndex) {
		case 0:
		case 1:
			break;
		case 2:
			result = item.getStartTimeString();
			break;
		case 3:
			if (item.isWavEnabled()) {
				result = item.getWavEndTimeString();
			} else {
				result = item.getEndTimeString();
			}
			break;
		case 4:
			result = item.getCharacter();
			break;
		case 5:
			result = item.getScenario();
			break;
		case 6:
			result = item.getDescription();
			break;
		case 7:
			result = item.getCaption();
			break;
		case 8:
			if (item.isExtended()) {
				result = "Extended";
			} else {
				result = "";
			}
			break;
		case 9:
			if (item.getWavURI() != null) {
				result = new File(item.getWavURI()).getName();
			} else {
				result = "";
			}
			break;
		case 10:
			result = item.getScriptComment();
			break;
		default:
			break;
		}
		return result;
	}
}