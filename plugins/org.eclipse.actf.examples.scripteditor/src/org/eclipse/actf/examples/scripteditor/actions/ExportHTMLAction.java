/*******************************************************************************
 * Copyright (c) 2010, 2012 IBM Corporation and Others
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

import org.eclipse.actf.ai.internal.ui.scripteditor.XMLFileMessageBox;
import org.eclipse.actf.ai.scripteditor.util.ResourceUtil;
import org.eclipse.actf.ai.scripteditor.util.TTMLUtil;
import org.eclipse.actf.ai.scripteditor.util.WebBrowserFactory;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ExportHTMLAction implements IWorkbenchWindowActionDelegate {

	// parameters
	private static final String[] NAMES = { "HTML (*.HTML)" };
	private static final String[] EXTENSIONS = { "*.html" };

	/**
	 * Export HTML template
	 */
	private void exportHTML(String filepath, String adFilename,
			boolean warnOverwrite) {
		PrintWriter writer = null;
		try {
			File file = new File(filepath);

			if (warnOverwrite && file.exists()) {
				XMLFileMessageBox warningExistFile = new XMLFileMessageBox(
						IUNIT.MB_STYLE_OVERWR, filepath);
				int ret = warningExistFile.open();
				if (ret != SWT.YES)
					return;
			}

			writer = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(filepath), "UTF-8"));

			String url = WebBrowserFactory.getInstance().getVideoURL();

			if (url == null) {
				return;
			}

			int index1 = url.lastIndexOf(".");
			int index2 = url.lastIndexOf("/");
			int index3 = url.lastIndexOf("\\");

			if (index1 > 0 && (index2 > 0 && index2 < index1)
					|| (index3 > 0 && index3 < index1)) {
				url = url.substring(0, index1);
			}
			if (url.startsWith("http")) {
				if (index1 > 0 && (index2 > 7 && index2 < index1)) {
					url = url.substring(0, index1);
				}
			} else {
				if (index1 > 0 && (index2 > 0 && index2 < index1)
						|| (index3 > 0 && index3 < index1)) {
					url = url.substring(0, index1);
				}
				url = "file://" + url;
			}

			writer.println("<!DOCTYPE html>");
			writer.println("<html lang=\"en\">");
			writer.println("<head>");
			writer.println("<meta content=\"text/html; charset=utf-8\" http-equiv=content-type>");
			writer.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
			writer.println("");
			writer.println("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=EmulateIE7; IE=EmulateIE9\">");
			writer.println("");
			writer.println("<title>Audio Description Template</title>");
			writer.println("");
			writer.println("<!-- HTML5 movie header start -->");
			writer.println("    <!--[if lt IE 9]>");
			writer.println("    <link rel=\"stylesheet\" href=\"resource/vd-player-ie.css\">");
			writer.println("    <script src=\"resource/vd-compat-ie.js\" defer></script>");
			writer.println("    <![endif]-->");
			writer.println("    <link rel=\"stylesheet\" href=\"resource/vd-player.css\">");
			writer.println("    <script src=\"resource/vd-compat.js\" defer></script>");
			writer.println("    <script src=\"resource/vd-player-en.js\" defer></script>");
			writer.println("<!-- HTML5 movie header end -->");
			writer.println("");
			writer.println("</head>");
			writer.println("<body>");
			writer.println("<div>");
			writer.println("");
			writer.println("<!-- HTML5 movie main start-->");
			writer.println("    <video width=\"320\" height=\"240\">");
			writer.println("        <source type=\"video/ogg\" src=\"" + url
					+ ".ogv\">");
			writer.println("        <source type=\"video/mp4\" src=\"" + url
					+ ".mp4\">");
			writer.println("        <source type=\"video/x-ms-wmv\" src=\""
					+ url + ".wmv\">");
			writer.println("        <track kind=\"metadata\" data-kind=\"descriptions\" src=\""
					+ adFilename
					+ "\" srclang=\"en\" label=\"Audio Description\" default>"); // TODO
																					// lang
			writer.println("    </video>");
			writer.println("<!-- HTML5 movie main end-->");
			writer.println("");
			writer.println("</div>");
			writer.println("</body>");
			writer.println("</html>");

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

	public void run(IAction action) {
		// Request FileDialog (Choice open file name)
		FileDialog saveDialog = new FileDialog(Display.getCurrent()
				.getActiveShell(), SWT.SAVE);
		saveDialog.setFilterNames(NAMES);
		saveDialog.setFilterExtensions(EXTENSIONS);
		saveDialog.setFileName("audio_description_template.html");
		String saveFilepath = saveDialog.open();

		if (saveFilepath != null) {

			int last = saveFilepath.lastIndexOf(".html");
			String preRecordedFilepath;
			if (last > 0) {
				preRecordedFilepath = saveFilepath.substring(0, last)
						+ "_p.html";
			} else {
				preRecordedFilepath = saveFilepath + "_p.html";
			}

			// TODO
			int index = saveFilepath.lastIndexOf(File.separator);
			String dirName = saveFilepath.substring(0, index + 1);
			String fileName;
			if (last > 0) {
				fileName = saveFilepath.substring(index + 1, last);
			} else {
				fileName = saveFilepath.substring(index + 1);
			}

			exportHTML(saveFilepath, fileName + "_ad.xml", true);
			exportHTML(preRecordedFilepath, fileName + "_ad_p.xml", false);

			File resourceDir = new File(dirName + "resource");
			resourceDir.mkdir();
			ResourceUtil.saveResources(dirName + "resource" + File.separator);

			File adFilesDir = new File(dirName + "ad_files");
			adFilesDir.mkdir();

			String adFilepath = dirName + fileName + "_ad.xml";
			TTMLUtil.exportTTML(adFilepath, false);

			adFilepath = dirName + fileName + "_ad_p.xml";
			TTMLUtil.exportTTMLandWAV(adFilepath, dirName + "ad_files"
					+ File.separator, fileName, null, false);
		}
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
}
