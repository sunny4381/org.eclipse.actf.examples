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
package org.eclipse.actf.ai.scripteditor.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.eclipse.actf.ai.internal.ui.scripteditor.PreviewPanel;
import org.eclipse.actf.ai.internal.ui.scripteditor.XMLFileMessageBox;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.actf.util.FileUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

public class XMLFileSaveUtil implements IUNIT {

	// parameters
	private String[] EXTENSIONS = { "*.xml", "*" };

	// instance of each ViewPart class
	private ScriptData instScriptData = null;
	private PreviewPanel instPreviewPanel = null;
	private TimeLineView instTimeLine = null;


	/**
	 * @category Constructor
	 * @param filePath : String of target XML file path
	 */
	public XMLFileSaveUtil() {
		// Store instance of parent view part, and so on..
		pickupInstViewPart();
	}

	/**
	 * @category Check exist target file
	 * @param filePath : target file path
	 * @return result status : TRUE:exist file, FALSE:not exist
	 */
	static public boolean exists(String filePath) {
		boolean result = false;

		// check exit file
		if(filePath != null){
			try {
				// Check enable data
				File fh = new File(filePath);
				if( fh.exists() ){
					// exist target file
					result = true;
				}
			}
			catch(Exception ee){
			}
		}
		// return result
		return(result);
	}

	/**
	 * @category Display OpenFile dialog for save XML file
	 * @return String of XML file path
	 */
	public String open() {
		String filePath = null;
		
		// Request FileDialog (Choice open file name)
		FileDialog saveDialog = new FileDialog(Display.getCurrent().getActiveShell(),
												SWT.SAVE);
		saveDialog.setFilterExtensions(EXTENSIONS);
		filePath = saveDialog.open();

		// return result
		return(filePath);
	}

	/**
	 * @category Save all script data to XML file
	 * @param filePath
	 * @return result process : TRUE:Success, FALSE:failed
	 */
	public boolean save(String filePath, boolean ovwr) {
		boolean result = false;

		// Check null (file name)
		if(filePath != null){
			// Save volume level data
			instTimeLine.reqSaveVolumeLevelTempFile();
			// Save file
			saveFile(filePath, ovwr);
			// Store current opened XML file path
			instTimeLine.reqStoreXMLFilePath(filePath);
			// Clear status for saved data
			instScriptData.setStatusSaveScripts(MB_STYLE_MODIFY, false);
			// success process
			result = true;
		}

		// return result
		return(result);
	}

	/**
	 * Local method : PickUP instance of each ViewPart class
	 */
	private void pickupInstViewPart() {
		if(instPreviewPanel == null){
			instPreviewPanel = PreviewPanel.getInstance();
		}
		if(instScriptData == null){
			instScriptData = ScriptData.getInstance();
		}
		if(instTimeLine == null){
			instTimeLine = TimeLineView.getInstance();
		}
	}

	/**
	 * Local method : Save data to target file(XML)
	 */
	private void saveFile(String fname, boolean warnOverwrite) {
		PrintWriter writer = null;
		try {
			// already file exist
			File file = new File(fname);
			if (warnOverwrite && file.exists()) {
				// Warning : No Script data
				XMLFileMessageBox warningExistFile = new XMLFileMessageBox(MB_STYLE_OVERWR, fname);
				// Check answer
				int ret = warningExistFile.open();
				if (ret != SWT.YES)
					return;
			}

			// Open file
			writer = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(fname), "UTF-8"));

			String LINE_SEP = FileUtils.LINE_SEP;

			// Write Header & Comments
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ LINE_SEP);
			writer.write("<puits xmlns=\"urn:puits\">" + LINE_SEP);
			writer
					.write("  <meta xmlns=\"http://www.ibm.com/xmlns/prod/AcTF/aiBrowser/selector/1.0\">"
							+ LINE_SEP);

			// Write URL with encode to UTF-8
			String strURL = instPreviewPanel.getURLMovie();
			writer.write("\t<targetSite uri=\"" + strURL + "\">" + LINE_SEP);
			writer.write("\t  <targetContent key=\"*\"/>" + LINE_SEP);
			writer.write("\t</targetSite>" + LINE_SEP);
			writer.write("  </meta>" + LINE_SEP);
			writer.write("  " + LINE_SEP);
			writer.write("  <alternative type=\"audio-description\">"
					+ LINE_SEP);
			// Write all ScriptData
			writer.write(instScriptData.toXMLfragment());

			writer.write("  </alternative>" + LINE_SEP);

			// Write path of volume level file(temporary file)
			if(instTimeLine.reqGetVolLvlPath() != null){
				String strPath = instTimeLine.reqGetVolLvlPath().toString();
				writer.write("  " + LINE_SEP);
				writer.write("  <volumeLevel local=\"" + strPath + "\"/>" + LINE_SEP);
			}

			writer.write("</puits>" + LINE_SEP);
		} catch (IOException e) {
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

}
