/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.examples.scripteditor.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.eclipse.actf.ai.internal.ui.scripteditor.PreviewPanel;
import org.eclipse.actf.ai.internal.ui.scripteditor.XMLFileMessageBox;
import org.eclipse.actf.ai.scripteditor.data.ScriptData;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.util.FileUtils;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ExportTTMLAction implements IWorkbenchWindowActionDelegate, IUNIT {

	/**
	 * Local data
	 */
	// instance of each ViewPart class
	private ScriptData instScriptData = null;
	private PreviewPanel instPreviewPanel = null;

	// private Shell instParentShell = null;

	// parameters
	private static final String[] NAMES = { "TTML format (*.xml)" };
	private static final String[] EXTENSIONS = { "*.xml" };
	private String saveFileName = "";

	/**
	 * Local method : PickUP instance of each ViewPart class
	 */
	private void pickupInstViewPart() {
		if (instScriptData == null) {
			instScriptData = ScriptData.getInstance();
		}
		if (instPreviewPanel == null) {
			instPreviewPanel = PreviewPanel.getInstance();
		}
	}

	/**
	 * Local method : Save data to target file(CSV)
	 */
	private void saveFile(String fname, boolean warnOverwrite) {
		PrintWriter writer = null;
		try {
			File file = new File(fname);

			// already file exist
			if (warnOverwrite && file.exists()) {
				// Warning : No Script data
				XMLFileMessageBox warningExistFile = new XMLFileMessageBox(
						MB_STYLE_OVERWR, fname);
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
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.println("<?xml-stylesheet href=\"ttml.css\" type=\"text/css\"?>");
			writer.println("<?access-control allow=\"*\"?>");
			writer.println("<tt ttp:profile='http://www.w3.org/ns/ttml/profile/dfxp-full'"
					+ LINE_SEP
					+ "    xmlns='http://www.w3.org/ns/ttml'"
					+ LINE_SEP
					+ "    xmlns:ttm='http://www.w3.org/ns/ttml#metadata'"
					+ LINE_SEP
					+ "    xmlns:tts='http://www.w3.org/ns/ttml#styling'"
					+ LINE_SEP
					+ "    xmlns:ttp='http://www.w3.org/ns/ttml#parameter'"
					+ LINE_SEP
					+ "    xmlns:actftvd='http://www.eclipse.org/actf/ai/tvd'>");
			writer.println(" <head>");
			writer.println("  <ttm:title>" + instPreviewPanel.getURLMovie()
					+ "</ttm:title>");// TODO

			writer.println(
			/*
			 * "  <styling>" + LINE_SEP +
			 * "    <style xml:id='ad1' tts:color='yellow' tts:fontFamily='proportionalSansSerif' tts:fontSize='16px' tts:textAlign='left'/>"
			 * + LINE_SEP + "  </styling>" + LINE_SEP + "  <layout>" + LINE_SEP
			 * + "   <region xml:id='descriptionArea'" + LINE_SEP +
			 * "           style='ad1'" + LINE_SEP +
			 * "           tts:origin='0% -15%'" + LINE_SEP +
			 * "           tts:extent='100% 20%'" + LINE_SEP +
			 * "           tts:padding='0px 0px'" + LINE_SEP +
			 * "           tts:backgroundColor='black'" + LINE_SEP +
			 * "           tts:opacity='0.75'" + LINE_SEP +
			 * "           tts:displayAlign='before'" + LINE_SEP +
			 * "           tts:showBackground='whenActive' />" + LINE_SEP +
			 * "  </layout>" + LINE_SEP +
			 */
			" </head>" + LINE_SEP + " <body>" + LINE_SEP
					+ "  <div role=\"narration\" region=\"descriptionArea\">");

			// Write all ScriptData
			for (int i = 0; i < instScriptData.getLengthScriptList(); i++) {
				int startTime = instScriptData.getScriptStartTime(i);
				int frame = (startTime % 1000) / (1000 / 30);
				String frameS = (frame > 9) ? Integer.toString(frame) : "0"
						+ frame;

				String strStartTime = instScriptData
						.makeFormatHHMMSS(startTime / 1000) + ":" + frameS;
				int endTime = instScriptData.getScriptEndTime(i) + 250; // TBD
				if (i < instScriptData.getLengthScriptList() - 1) {
					if (endTime > instScriptData.getScriptStartTime(i + 1)) {
						endTime = instScriptData.getScriptStartTime(i + 1) - 100;
					}
				}

				int duration = endTime - startTime;
				frame = (endTime % 1000) / (1000 / 30);
				frameS = (frame > 9) ? Integer.toString(frame) : "0" + frame;

				String StrEndTime = instScriptData
						.makeFormatHHMMSS(endTime / 1000) + ":" + frameS;

				frame = (duration % 1000) / (1000 / 30);
				frameS = (frame > 9) ? Integer.toString(frame) : "0" + frame;

				String strDuration = instScriptData
						.makeFormatHHMMSS(duration / 1000) + ":" + frameS;

				String strDesc = instScriptData.getScriptData(i);

				// TODO diff duration with next item
				/*
				 * writer.println("    <p xml:id=\"description"+i+
				 * "\" ttm:role=\"narration\" begin=\"" +
				 * strStartTime+"\" dur=\""
				 * +strDuration+"s\">"+canonicalize(strDesc )+"</p>");
				 */
				writer.print("    <p xml:id=\"description" + i
						+ "\" ttm:role=\"narration\" begin=\"" + strStartTime);
				if (instScriptData.getExtendExtended(i)) {
					writer.println("\" actftvd:extended='true' dur=\""
							+ strDuration + "\" >" + canonicalize(strDesc)
							+ "</p>");
				} else {
					writer.println("\" end=\"" + StrEndTime + "\">"
							+ canonicalize(strDesc) + "</p>");
				}
			}

			writer.println("  </div>" + LINE_SEP + " </body>" + LINE_SEP
					+ "</tt>");

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

	/**
	 * run() method
	 */
	public void run(IAction action) {
		// Request FileDialog (Choice open file name)
		FileDialog saveDialog = new FileDialog(Display.getCurrent()
				.getActiveShell(), SWT.SAVE);
		saveDialog.setFilterNames(NAMES);
		saveDialog.setFilterExtensions(EXTENSIONS);
		saveFileName = saveDialog.open();

		// Check null (file name)
		if (saveFileName != null) {
			// Store instance of each ViewPart class
			pickupInstViewPart();
			// instParentShell = instSelWavTab.getParentShell();

			// Save file
			saveFile(saveFileName, true);
		}
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	private String canonicalize(String targetS) {
		return (targetS.replaceAll("\\p{Cntrl}", "").replaceAll("&", "&amp;")
				.replaceAll("<", "&lt;").replaceAll(">", "&gt;")
				.replaceAll("\"", "&quot;").replaceAll("\'", "&apos;"));
	}
}
