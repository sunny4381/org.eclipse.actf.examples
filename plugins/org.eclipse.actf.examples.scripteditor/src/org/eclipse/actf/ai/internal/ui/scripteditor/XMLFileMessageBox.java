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

import java.io.File;

import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.examples.scripteditor.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

public class XMLFileMessageBox implements IUNIT {

	// Local data
	private MessageBox msgBox = null;

	/**
	 * @category Constructor
	 * @param parent
	 *            : parent shell's instance
	 */
	public XMLFileMessageBox(int mode, String filePath) {
		// Check message mode
		if (msgBox == null) {
			// Initial setup for MessageBox
			int style = SWT.YES | SWT.NO | SWT.CLOSE;
			String strTitle = "";
			String strMessage = "";

			// MakeUP style of MessageBox
			if (mode == MB_STYLE_CONFIRM) {
				// Confirmation message box
				strMessage = getFileName(filePath);
				style = style | SWT.ICON_WARNING | SWT.CANCEL;
				strTitle = Messages.xml_dialog_title_confirm;
				strMessage += Messages.xml_dialog_save_confirm;
			} else if (mode == MB_STYLE_OVERWR) {
				// Overwrite message box
				strMessage = getFileName(filePath);
				style = style | SWT.ICON_WARNING;
				strTitle = Messages.xml_dialog_title_overwrite;
				strMessage += Messages.xml_dialog_save_overwrite;
			} else if (mode == MB_STYLE_NODESC) {
				// No exist description message box
				style = SWT.ICON_ERROR | SWT.OK;
				strTitle = Messages.xml_dialog_title_error;
				strMessage = Messages.xml_dialog_script_nodesc;
			} else if (mode == MB_STYLE_NOEXIST) {
				// No exist script message box
				style = SWT.ICON_ERROR | SWT.OK;
				strTitle = Messages.xml_dialog_title_error;
				strMessage = Messages.xml_dialog_script_noexist;
			} else if (mode == MB_STYLE_MODIFY) {
				// Modify script data message box
				style = style | SWT.ICON_WARNING | SWT.CANCEL;
				strTitle = Messages.xml_dialog_title_modify;
				strMessage = Messages.xml_dialog_save_modify;
			} else if (mode == MB_STYLE_WAV_CONFIRM) {
				// Modify script data message box
				style = SWT.ICON_WARNING | SWT.YES | SWT.NO;
				strTitle = Messages.xml_dialog_title_confirm;
				strMessage = Messages.xml_dialog_wav_confirm + filePath;
			} else if (mode == MB_STYLE_ACCESS_DENIED) {
				// No exist script message box
				style = SWT.ICON_WARNING | SWT.OK;
				strTitle = Messages.xml_dialog_title_confirm;
				strMessage = Messages.xml_dialog_access_denied;
			}

			try {
				// Display confirm message box
				// msgBox = new
				// MessageBox(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
				// style);
				msgBox = new MessageBox(Activator.getParentShell(), style);
				msgBox.setText(strTitle);
				msgBox.setMessage(strMessage);
			} catch (Exception ee) {
			}
		}
	}

	/**
	 * @category Getter method : Get result from own MessageBox
	 * @return result : Yes or No or Cancel
	 */
	public int open() {
		int result = -1;
		if (msgBox != null) {
			// Open MessageBox & Wait response
			result = msgBox.open();
			// dispose own component
			msgBox = null;
		}
		// return result
		return (result);
	}

	/**
	 * @category Local method : PickUP prefix and suffix from target file path
	 * @param filePath
	 *            : String of target file path
	 * @return file name as <prefix>.<suffix>
	 */
	private String getFileName(String filePath) {
		String result = "";

		// Get File instance
		try {
			if (filePath != null) {
				File fh = new File(filePath);
				if (fh != null) {
					// Get file name
					result = "\'" + fh.getName() + "\' ";
				}
			}
		} catch (Exception ee) {
		}

		// return result
		return (result);
	}

}
