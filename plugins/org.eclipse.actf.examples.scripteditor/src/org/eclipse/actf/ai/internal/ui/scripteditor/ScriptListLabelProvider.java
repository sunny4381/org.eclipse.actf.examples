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

import java.net.URLDecoder;

import org.eclipse.actf.ai.scripteditor.data.ScriptData;
import org.eclipse.actf.ai.scripteditor.util.SoundMixer;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.examples.scripteditor.Activator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class ScriptListLabelProvider extends LabelProvider implements
		ITableLabelProvider, IUNIT {

	// Local data
	private static final String LABEL_EXTENDED_ENABLE = "Ex";
	private static final String LABEL_EXTENDED_DISABLE = "";

	public Image getColumnImage(Object element, int columnIndex) {

		// Get data of current column from input param(element).
		ScriptData item = (ScriptData) element;
		Image result = null;

		if (columnIndex == 0) {
			// PickUP index of current script
			int index = ScriptData.getInstance().getIndexWavList(
					item.getScriptStartTime(0));
			if (index >= 0) {
				// PickUP source images from resource area
				Image imgWavOn = Activator.getImageDescriptor(
						"/icons/wave_on.gif").createImage();
				Image imgWavOff = Activator.getImageDescriptor(
						"/icons/wave_off.gif").createImage();
				// PickUP WAV enabled setting
				result = (ScriptData.getInstance().getEnableWavList(index) ? imgWavOn
						: imgWavOff);

				// Check invalid status of WAV file
				if (ScriptData.getInstance().getEndTimeWavList(index) == WAV_STAT_INVALID) {
					// Change INVALID image
					result = imgWavOff;
				}
				// Check WAV header's format
				try {
					String strWavPath = ScriptData.getInstance()
							.getFileNameWavList(index).toString().replace(
									"file:/", "");
					if (!SoundMixer.getInstance().isWavFormat(strWavPath)) {
						// Change INVALID image
						strWavPath = URLDecoder.decode(
								ScriptData.getInstance().getFileNameWavList(
										index).toString(), "UTF-8").replace(
								"file:/", "");
						if (!SoundMixer.getInstance().isWavFormat(strWavPath)) {
							result = imgWavOff;
						}
					}
				} catch (Exception ee) {
					// Change INVALID image
					result = imgWavOff;
				}

				// Use system icon image
				// Program p = Program.findProgram(".wav");
				// ImageData dat = p.getImageData();
				// result = new Image(PlatformUI.getWorkbench().getDisplay(),
				// dat);
			}
		}

		// return result
		return (result);
	}

	public String getColumnText(Object element, int columnIndex) {

		// Get data of current column from input param(element).
		ScriptData item = (ScriptData) element;
		String result = "";

		// PickUP text of data for current column.
		// **Caution**
		// 1)Each element(instance of ScriptData class) has one script data
		// only.
		if (columnIndex == 1) {
			// PickUP text of Extended
			int startTime = item.getScriptStartTime(0);
			int index = ScriptData.getInstance().getIndexScriptData(startTime);
			if (index >= 0) {
				boolean stat = ScriptData.getInstance()
						.getExtendExtended(index);
				result = new String((stat ? LABEL_EXTENDED_ENABLE
						: LABEL_EXTENDED_DISABLE));
			}
		} else if (columnIndex == 2) {
			// PickUP text of Start Time
			int intScriptStartTime = item.getScriptStartTime(0);
			result = new String(item.makeFormatMMSSMS(intScriptStartTime));
		} else if (columnIndex == 3) {
			// PickUP string of Description
			result = item.getScriptData(0);
		} else if (columnIndex == 4) {
			// PickUP string of Comment
			result = item.getScriptComment(0);
		}

		// return text of column
		return (result);
	}

	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

}
