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
package org.eclipse.actf.ai.scripteditor.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.eclipse.actf.ai.internal.ui.scripteditor.FileInfoStore;
import org.eclipse.actf.ai.scripteditor.data.ScriptDataManager;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.actf.model.ui.IModelService;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserACTF;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.eclipse.actf.util.FileUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

public class XMLFileSaveUtil {

	private static XMLFileSaveUtil instance;

	private String[] EXTENSIONS = { "*.xml", "*" };
	private ScriptDataManager scriptManager = null;
	private TimeLineView instTimeLine = null;
	private String filePath = null;

	public static XMLFileSaveUtil getInstance() {
		if (instance == null) {
			instance = new XMLFileSaveUtil();
		}
		return instance;
	}

	private XMLFileSaveUtil() {
		if (scriptManager == null) {
			scriptManager = ScriptDataManager.getInstance();
		}
		if (instTimeLine == null) {
			instTimeLine = TimeLineView.getInstance();
		}
	}

	/**
	 * @category Check existence of target file
	 * @param filePath
	 *            target file path
	 * @return true if target file exists and can write
	 */
	static public boolean exists(String filePath) {
		boolean result = false;

		if (filePath != null) {
			try {
				File fh = new File(filePath);
				if (fh.exists() && fh.isFile() && fh.canWrite()) {
					result = true;
				}
			} catch (Exception e) {
			}
		}
		return (result);
	}

	/**
	 * @category Display OpenFile dialog for save XML file
	 * @return XML file path in String
	 */
	public String open() {
		String filePath = null;
		FileDialog saveDialog = new FileDialog(Display.getCurrent()
				.getActiveShell(), SWT.SAVE);
		saveDialog.setFilterExtensions(EXTENSIONS);
		filePath = saveDialog.open();
		return (filePath);
	}

	/**
	 * @category Save all data into XML file
	 * @param filePath
	 * @return true if data is successfully saved
	 */
	public boolean save(String filePath, boolean ovwr) {
		boolean result = false;

		if (filePath != null) {
			// Save volume level data
			instTimeLine.reqSaveVolumeLevelTempFile();
			saveFile(filePath, ovwr);
			this.filePath = filePath;
			scriptManager.setSaveRequired(XMLFileMessageBox.MB_STYLE_MODIFY,
					false);
			result = true;
		}
		return (result);
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	private void saveFile(String fname, boolean warnOverwrite) {
		PrintWriter writer = null;
		try {
			File file = new File(fname);
			if (warnOverwrite && file.exists()) {
				XMLFileMessageBox warningExistFile = new XMLFileMessageBox(
						XMLFileMessageBox.MB_STYLE_OVERWR, fname);
				int ret = warningExistFile.open();
				if (ret != SWT.YES)
					return;
			}

			writer = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(fname), "UTF-8"));

			String LINE_SEP = FileUtils.LINE_SEP;

			// Write Header & Comments
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ LINE_SEP);
			writer.write("<puits xmlns=\"urn:puits\">" + LINE_SEP);
			writer.write("  <meta xmlns=\"http://www.ibm.com/xmlns/prod/AcTF/aiBrowser/selector/1.0\">"
					+ LINE_SEP);

			// Write URL with encode to UTF-8
			String strURL = getTargetURL();

			writer.write("\t<targetSite uri=\"" + strURL + "\">" + LINE_SEP);
			writer.write("\t  <targetContent key=\"*\"/>" + LINE_SEP);
			writer.write("\t</targetSite>" + LINE_SEP);
			writer.write("  </meta>" + LINE_SEP);
			writer.write("  " + LINE_SEP);
			writer.write("  <alternative type=\"audio-description\">"
					+ LINE_SEP);

			writer.write(scriptManager.toXMLfragment());

			writer.write("  </alternative>" + LINE_SEP);

			if (FileInfoStore.getVolumeLevelFilePath() != null) {
				String strPath = FileInfoStore.getVolumeLevelFilePath()
						.getPath();
				writer.write("  " + LINE_SEP);
				writer.write("  <volumeLevel local=\"" + strPath + "\"/>"
						+ LINE_SEP);
			}

			writer.write("</puits>" + LINE_SEP);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (Exception e) {
				}
			}
		}
	}

	private String getTargetURL() {
		IModelService model = ModelServiceUtils.getActiveModelService();
		if (model instanceof IWebBrowserACTF) {
			return model.getURL();
		}
		return "about:blank";
	}

}