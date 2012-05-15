/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.ai.scripteditor.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.eclipse.actf.ai.internal.ui.scripteditor.XMLFileMessageBox;
import org.eclipse.actf.ai.scripteditor.data.ScriptData;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.util.FileUtils;
import org.eclipse.swt.SWT;

public class TTMLUtil {

	private static ScriptData instScriptData = ScriptData.getInstance();

	/**
	 * Export AudioDescription data in TTML format
	 */
	public static void exportTTML(String ttmlPathname, boolean warnOverwrite) {
		exportTTML(ttmlPathname, false, null, null, null, warnOverwrite);
	}

	/**
	 * Export AudioDescription data in TTML format and pre-recorded
	 * AudioDescription(AD) WAV files
	 */
	public static void exportTTMLandWAV(String ttmlPathname,
			String adWavDirpath, String adWavFilename, String adUrlBase,
			boolean warnOverwrite) {
		exportTTML(ttmlPathname, true, adWavDirpath, adWavFilename, adUrlBase,
				warnOverwrite);
	}

	/**
	 * Export AudioDescription data in TTML format and pre-recorded
	 * AudioDescription(AD) WAV files
	 */
	private static void exportTTML(String ttmlPathname, boolean isPrerecorded,
			String adWavDirpath, String adWavFilename, String adUrlBase,
			boolean warnOverwrite) {
		PrintWriter writer = null;

		VoicePlayerFactory voice = VoicePlayerFactory.getInstance();

		try {
			File file = new File(ttmlPathname);

			if (warnOverwrite && file.exists()) {
				XMLFileMessageBox warningExistFile = new XMLFileMessageBox(
						IUNIT.MB_STYLE_OVERWR, ttmlPathname);
				int ret = warningExistFile.open();
				if (ret != SWT.YES)
					return;
			}

			writer = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(ttmlPathname), "UTF-8"));

			String LINE_SEP = FileUtils.LINE_SEP;

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
					+ "    xmlns:tvd='http://www.eclipse.org/actf/ai/tvd'>");
			writer.println(" <head>");
			writer.println("  <metadata>");
			writer.println("   <ttm:title>TextTrack (Audio Description)</ttm:title>");// TODO
			writer.println("  </metadata>");

			writer.println("  <styling>" + LINE_SEP
					+ "   <style xml:id=\"style1\"/>" + LINE_SEP
					+ "  </styling>" + LINE_SEP + "  <layout>" + LINE_SEP
					+ "   <region xml:id=\"region1\" style=\"style1\"/>"
					+ LINE_SEP + "  </layout>" + LINE_SEP
					+
					//
					" </head>" + LINE_SEP + " <body>" + LINE_SEP
					+ "  <div role=\"narration\" region=\"region1\">");

			if (isPrerecorded) {
				File readmeFile = new File(adWavDirpath + "1st_Readme.txt");
				try {
					PrintWriter pw = new PrintWriter(readmeFile, "UTF-8");
					if (voice.canSpeakToFile()) {
						pw.println("Please convert wav files into wma/oga/m4a formats.");
						pw.println(" wma: for IE8 or older");
						pw.println(" oga: for Chrome, Firefox, Opera");
						pw.println(" m4a: for IE9 or later, Safari");
						pw.println();
						pw.println("After converting files, please deploy these files to your Web server,");
						pw.println("and adjust URLs of \"tvd:external\" attribute in "
								+ file.getName());
						pw.flush();
						pw.close();
					} else {
						pw.println("This TTS engine can't create wav files.");
						pw.println("Please prepare audio descriptions in wma/oga/m4a formats.");
						pw.println(" wma: for IE8 or older");
						pw.println(" oga: for Chrome, Firefox, Opera");
						pw.println(" m4a: for IE9 or later, Safari");
						pw.println();
						pw.println("After preparing files, please deploy these files to your Web server,");
						pw.println("and adjust URLs of \"tvd:external\" attribute in "
								+ file.getName());
						pw.flush();
						pw.close();
					}
				} catch (Exception e) {
				}
			}

			// Write all ScriptData
			for (int i = 0; i < instScriptData.getLengthScriptList(); i++) {
				int startTime = instScriptData.getScriptStartTime(i);
				int frame = (startTime % 1000) / (1000 / 30);
				String frameS = (frame > 9) ? Integer.toString(frame) : "0"
						+ frame;

				String strStartTime = instScriptData
						.makeFormatHHMMSS(startTime / 1000) + ":" + frameS;
				int endTime = instScriptData.getScriptEndTime(i);

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
				writer.print("    <p xml:id=\"description" + i + "\" begin=\""
						+ strStartTime);
				if (instScriptData.getExtendExtended(i)) {
					writer.print("\" tvd:extended=\"true\" dur=\""
							+ strDuration + "\"");
				} else {
					writer.print("\" tvd:extended=\"false\" end=\""
							+ StrEndTime + "\"");
				}

				if (isPrerecorded) {
					int number = i + 1;
					String ad_file;
					if (number < 10) {
						ad_file = adWavFilename + "_0000" + number;
					} else if (number < 100) {
						ad_file = adWavFilename + "_000" + number;
					} else if (number < 1000) {
						ad_file = adWavFilename + "_00" + number;
					} else if (number < 10000) {
						ad_file = adWavFilename + "_0" + number;
					} else {
						ad_file = adWavFilename + number;
					}
					if (adUrlBase == null || adUrlBase.isEmpty()) {
						writer.print(" tvd:external=\"file://" + adWavDirpath
								+ ad_file + ".*\"");
					} else {
						writer.print(" tvd:external=\"" + adUrlBase + ad_file
								+ ".*\"");
					}
					if (voice.canSpeakToFile()) {
						String strGender = instScriptData.getExtendGender(i) ? "male"
								: "female";
						voice.setGender(strGender);
						voice.setSpeed(instScriptData.getExtendSpeed(i));
						voice.speakToFile(instScriptData.getScriptData(i),
								new File(adWavDirpath + ad_file + ".wav"));
					}
				}
				writer.println(">" + canonicalize(strDesc) + "</p>");
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

	private static String canonicalize(String targetS) {
		return (targetS.replaceAll("\\p{Cntrl}", "").replaceAll("&", "&amp;")
				.replaceAll("<", "&lt;").replaceAll(">", "&gt;")
				.replaceAll("\"", "&quot;").replaceAll("\'", "&apos;"));
	}

}
