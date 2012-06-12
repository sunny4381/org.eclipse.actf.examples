/*******************************************************************************
 * Copyright (c) 2009, 2011 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.ai.scripteditor.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.actf.ai.internal.ui.scripteditor.event.EventManager;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.SyncTimeEvent;
import org.eclipse.actf.ai.scripteditor.data.DataUtil;
import org.eclipse.actf.ai.scripteditor.data.IScriptData;
import org.eclipse.actf.ai.scripteditor.data.ScriptDataFactory;
import org.eclipse.actf.ai.scripteditor.data.event.DataEventManager;
import org.eclipse.actf.ai.scripteditor.data.event.GuideListEvent;
import org.eclipse.actf.ai.scripteditor.data.event.LabelEvent;
import org.eclipse.actf.ai.scripteditor.preferences.CSVRulePreferenceUtil;
import org.eclipse.actf.ai.scripteditor.util.SoundMixer;
import org.eclipse.actf.ai.scripteditor.util.TimeFormatUtil;
import org.eclipse.actf.ai.scripteditor.util.VoicePlayerFactory;
import org.eclipse.actf.ai.scripteditor.util.WavUtil;
import org.eclipse.actf.ai.scripteditor.util.WebBrowserFactory;
import org.eclipse.actf.ai.scripteditor.util.XMLFileMessageBox;
import org.eclipse.actf.ai.ui.scripteditor.views.EditPanelView;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

public class CSVReader implements IUNIT {

	// process status
	private static final int CSV_PROC_IDLE = 0; // idle mode
	private static final int CSV_PROC_LOAD = 1; // loading CSV file (as String
												// data)
	private static final int CSV_PROC_ANALYZE = 2; // analyzing loaded data
	private static final int CSV_PROC_SAVE = 3; // saving to ScriptList

	// private static final int CSV_SUB_PROC_CAT = 11;
	// Sub mode : now cat string mode

	// sub status of analyze mode
	// 0 : idle mode
	private static final int CSV_ANA_IDLE = 0;
	// 1 : now analyzing start time
	private static final int CSV_ANA_STIME = 1;
	// 2 : now analyzing end time
	private static final int CSV_ANA_ETIME = 2;
	// 3 : now analyzing description
	private static final int CSV_ANA_DESC = 3;
	// 4 : now analyzing scenario
	private static final int CSV_ANA_SCENARIO = 4;
	// 5 : now analyzing speaker
	private static final int CSV_ANA_SPEKER = 5;
	// 6 : now analyzing caption
	private static final int CSV_ANA_CAPTION = 6;
	// 7 : now analyzing caption
	private static final int CSV_ANA_COMMENT = 7;
	// 8 : now analyzing data type
	private static final int CSV_ANA_TYPE = 8;
	// 9 : now analyzing data type
	private static final int CSV_ANA_CHILD = 9;
	// 10 : now analyzing URI value of WAV file path
	private static final int CSV_ANA_WAV = 10;
	// 11 : now waiting confirm message box process
	private static final int CSV_ANA_WAIT_CONFIRM = 11;
	// 12 : now analyzing enable status of Extend
	private static final int CSV_ANA_EXT_ENA = 12;
	// 13 : now analyzing gender of Extend
	private static final int CSV_ANA_EXT_GEN = 13;
	// 14 : now analyzing language of Extend
	private static final int CSV_ANA_EXT_LANG = 14;
	// 15 : now analyzing speed of Extend
	private static final int CSV_ANA_EXT_SPEED = 15;
	// 16 : now analyzing pitch of Extend
	private static final int CSV_ANA_EXT_PITCH = 16;
	// 17 : now analyzing volume of Extend
	private static final int CSV_ANA_EXT_VOL = 17;
	// 18 : now analyzing WAV enable status of Extend
	private static final int CSV_ANA_EXT_WENA = 18;
	// 19 : now analyzing WAV speed of Extend
	private static final int CSV_ANA_EXT_WSPEED = 19;

	// parse of TIME format
	private static final String FORMAT_STIME_MMSSmmm = "mm:ss:mmm";
	private static final String FORMAT_STIME_MMSS = "mm:ss";
	private static final String FORMAT_STIME_HHMMSSmmm = "hh:mm:ss:mmm";

	// define literal value
	private static final String LN_SEPARATOR = "\n";

	// default data for save process
	private static final String DEF_STR_DESC = " ";

	// own mode
	private int currentStatus = CSV_ANA_IDLE;
	private int currentWavRule = CSV_WAV_RULE_DROP;
	private boolean currentWavWait = false;

	// variables for analyze process
	private int bkup_startTime = -1;
	private int bkup_endTime = -1;
	private String bkup_description = null;
	private String bkup_scenario = null;
	private String bkup_speaker = null;
	private String bkup_caption = null;
	private String bkup_comment = null;
	private String bkup_dataType = null;
	private String bkup_child = null;
	private URI bkup_wavUri = null;
	private int bkup_wavDuration = -1;
	// variables for extended information
	private boolean bkup_ext_extended = false;
	private boolean bkup_ext_gender = true;
	private String bkup_ext_lang = "en-US"; // TODO use locale
	private int bkup_ext_speed = 50;
	private int bkup_ext_pitch = 50;
	private int bkup_ext_volume = 50;
	private boolean bkup_ext_wav_enable = false;
	private float bkup_ext_wav_speed = 1.0f;

	// variables for save process
	private ArrayList<Integer> list_startTime;
	private ArrayList<Integer> list_endTime;
	private ArrayList<String> list_description;
	private ArrayList<String> list_scenario;
	private ArrayList<String> list_speaker;
	private ArrayList<String> list_capton;
	private ArrayList<String> list_comment;
	private ArrayList<String> list_dataType;
	private ArrayList<String> list_child;
	private ArrayList<URI> list_wavUri;
	private ArrayList<Integer> list_wavStartTime;
	private ArrayList<Integer> list_wavDuration;
	// variables for extend information
	private ArrayList<Boolean> list_ext_extended;
	private ArrayList<Boolean> list_ext_gender;
	private ArrayList<String> list_ext_lang;
	private ArrayList<Integer> list_ext_speed;
	private ArrayList<Integer> list_ext_pitch;
	private ArrayList<Integer> list_ext_volume;
	private ArrayList<Boolean> list_ext_wav_enable;
	private ArrayList<Float> list_ext_wav_speed;

	// input streams
	private InputStream inCsvStream;
	private BufferedReader bufCsvReader;
	private ArrayList<String> rawCsvDataList = null;
	private int maxAnalyzeData = 0;
	private int currentAnalyzeData = 0;
	// private boolean nowExceptionNoWavFile = false;

	// Thread of file reader
	private ThreadCSVReader thCsvRd = null;
	private boolean currentActive = false;
	private int currentProcess = CSV_PROC_IDLE;

	private DataEventManager dataEventManager = null;
	private EventManager eventManager = null;
	static List<IScriptData> dataList = new ArrayList<IScriptData>();

	/**
	 * Constructor
	 */
	public CSVReader() {
		// Allocate array list for load process
		rawCsvDataList = new ArrayList<String>();
		// Allocate array list for save process
		list_startTime = new ArrayList<Integer>();
		list_endTime = new ArrayList<Integer>();
		list_description = new ArrayList<String>();
		list_scenario = new ArrayList<String>();
		list_speaker = new ArrayList<String>();
		list_capton = new ArrayList<String>();
		list_comment = new ArrayList<String>();
		list_dataType = new ArrayList<String>();
		list_child = new ArrayList<String>();
		list_wavUri = new ArrayList<URI>();
		list_wavStartTime = new ArrayList<Integer>();
		list_wavDuration = new ArrayList<Integer>();
		// Allocate array list for extend information
		list_ext_extended = new ArrayList<Boolean>();
		list_ext_gender = new ArrayList<Boolean>();
		list_ext_lang = new ArrayList<String>();
		list_ext_speed = new ArrayList<Integer>();
		list_ext_pitch = new ArrayList<Integer>();
		list_ext_volume = new ArrayList<Integer>();
		list_ext_wav_enable = new ArrayList<Boolean>();
		list_ext_wav_speed = new ArrayList<Float>();
		// Initialize all status flag
		currentProcess = CSV_PROC_IDLE;
		currentStatus = CSV_ANA_IDLE;
		currentActive = false;

		dataEventManager = DataEventManager.getInstance();
		eventManager = EventManager.getInstance();
	}

	/**
	 * @throws IOException
	 * @category Start Loading CSV file by FileInputStream
	 */
	public void startCSVReader(String fname) {
		try {
			// Check status of own thread
			if (!currentActive) {
				// Check exist target CSV file
				File fh = new File(fname);
				if (fh.exists()) {
					// SetUP input stream buffer
					inCsvStream = new FileInputStream(fname);
					bufCsvReader = new BufferedReader(new InputStreamReader(
							inCsvStream));
					// Get current preference setting
					currentWavRule = CSVRulePreferenceUtil
							.getPreferenceCsvWavRule();

					// Run file reader thread
					currentProcess = CSV_PROC_LOAD;
					currentActive = true;
					thCsvRd = new ThreadCSVReader();
					thCsvRd.start();
				}
			}
		} catch (Exception e) {
			System.out.println("startCSVReader : " + e);
		}
	}

	/**
	 * @category Local method : Close process after finished reading CSV file
	 * @throws IOException
	 */
	private void closeCSVReader() {
		try {
			// drop status flag
			currentActive = false;
			thCsvRd = null;
			// dispose all input stream buffer
			inCsvStream.close();
			bufCsvReader.close();
			// clear array list
			rawCsvDataList.clear();
			list_startTime.clear();
			list_endTime.clear();
			list_description.clear();
			list_scenario.clear();
			list_speaker.clear();
			list_capton.clear();
			list_comment.clear();
			list_dataType.clear();
			list_child.clear();
			list_wavUri.clear();
			list_wavStartTime.clear();
			list_wavDuration.clear();
			// clear array list
			list_ext_extended.clear();
			list_ext_gender.clear();
			list_ext_lang.clear();
			list_ext_speed.clear();
			list_ext_pitch.clear();
			list_ext_volume.clear();
			list_ext_wav_enable.clear();
			list_ext_wav_speed.clear();
			// reset all variables
			bkup_startTime = -1;
			bkup_endTime = -1;
			bkup_description = null;
			bkup_scenario = null;
			bkup_speaker = null;
			bkup_caption = null;
			bkup_comment = null;
			bkup_dataType = null;
			bkup_child = null;
			bkup_wavUri = null;
			bkup_wavDuration = -1;
			// reset all extend variables
			bkup_ext_extended = false;
			bkup_ext_gender = true;
			bkup_ext_lang = "en-US"; // TODO use locale
			bkup_ext_speed = 50;
			bkup_ext_pitch = 50;
			bkup_ext_volume = 50;
			bkup_ext_wav_enable = false;
			bkup_ext_wav_speed = 1.0f;
			// reset status flag
			currentProcess = CSV_PROC_IDLE;
			currentStatus = CSV_ANA_IDLE;
		} catch (IOException ioe) {
			System.out.println("closeCSVReader() : " + ioe);
		}
	}

	/**
	 * @category Check current status of CSV file reader process
	 * @return current status (TRUE:now processing, FALSE:no action)
	 */
	public boolean isActiveCSVReader() {
		// return result : If value is TRUE, then now processing.
		return (currentActive);
	}

	/**
	 * @category Local method : Load line data from CSV file
	 * @return result process : TRUE:Finished process, FALSE:Active process
	 * @throws IOException
	 */
	private boolean loadCSVData() throws IOException {
		boolean result = false;
		if (bufCsvReader != null) {
			// load current line from CSV file
			String rawLineData = bufCsvReader.readLine();
			if (rawLineData != null) {
				String[] tempLineData = null;
				// CSV_SUB_PROC_CAT mode is not used.
				while (true) {
					String tmp = rawLineData.trim().replaceAll("&", "")
							.replaceAll("\"\"", "&");
					if (tmp.endsWith("\"") || tmp.endsWith(",&")) {
						break;
					}
					String tmpRaw = bufCsvReader.readLine();
					if (tmpRaw == null) {
						break;
					}
					rawLineData += (System.getProperty("line.separator") + tmpRaw);
				}
				// Store line string to temporary buffer
				// TODO use regexp
				tempLineData = rawLineData.split("\",\"", -1);
				// Store splitting string with trimming blank code
				for (int i = 0; i < tempLineData.length; i++) {
					// trim blank code of current string
					String trimLineData = tempLineData[i]
							.replaceAll("^[\\s\"]?+", "")
							.replaceAll("[\\s\"]?+$", "")
							.replaceAll("\"\"", "\"");
					trimLineData = trimLineData.replaceAll("&2c", ",")
							.replaceAll("&amp;", "&");
					if (trimLineData.trim().length() > 0) {
						// undo parent data (may be, all blank code)
						trimLineData = trimLineData.trim();
					}
					// Trim double quotation code from current string
					rawCsvDataList.add(trimLineData);
				}
			} else {
				// End of File
				result = true;
			}
		} else {
			// forced exit
			result = true;
		}
		// return result
		return (result);
	}

	/**
	 * @category Local method : Analyze line data for conversion data to
	 *           description
	 * @return result process : TRUE:Finished process, FALSE:Active process
	 * @throws IOException
	 */
	private boolean analyzeCSVData() {
		boolean result = false;

		// PickUP next string
		String nowStr = rawCsvDataList.get(currentAnalyzeData);
		// Status 1 : Check format is StartTime(3pattern match)
		if (isFormatStartTime(nowStr, FORMAT_STIME_MMSSmmm)
				|| isFormatStartTime(nowStr, FORMAT_STIME_MMSS)
				|| isFormatStartTime(nowStr, FORMAT_STIME_HHMMSSmmm)) {

			// check current status
			if (currentStatus >= CSV_ANA_ETIME) {
				if (bkup_description == null) {
					bkup_description = DEF_STR_DESC;
				}
				if (bkup_scenario == null) {
					bkup_scenario = "";
				}
				if (bkup_speaker == null) {
					bkup_speaker = "";
				}
				if (bkup_caption == null) {
					bkup_caption = "";
				}
				if (bkup_comment == null) {
					bkup_comment = "";
				}

				// store current data as script data to own temporary buffer
				list_startTime.add(bkup_startTime);
				list_endTime.add(bkup_endTime);
				list_description.add(bkup_description);
				list_scenario.add(bkup_scenario);
				list_speaker.add(bkup_speaker);
				list_capton.add(bkup_caption);
				list_comment.add(bkup_comment);
				list_dataType.add(bkup_dataType);
				list_child.add(bkup_child);
				// store current data as WAV information to own temporary buffer
				if (bkup_wavUri != null) {
					list_wavUri.add(bkup_wavUri);
					list_wavStartTime.add(bkup_startTime);
					list_wavDuration.add(bkup_wavDuration);
					list_ext_wav_enable.add(bkup_ext_wav_enable);
					list_ext_wav_speed.add(bkup_ext_wav_speed);
				}

				// store current Extend data as script data to own temporary
				// buffer
				list_ext_extended.add(bkup_ext_extended);
				list_ext_gender.add(bkup_ext_gender);
				list_ext_lang.add(bkup_ext_lang);
				list_ext_speed.add(bkup_ext_speed);
				list_ext_pitch.add(bkup_ext_pitch);
				list_ext_volume.add(bkup_ext_volume);

				// Initialize area for next start time
				bkup_description = null;
				bkup_wavUri = null;
				bkup_wavDuration = -1;
				// Initialize Extended area for next start time
				bkup_ext_extended = false;
				bkup_ext_speed = 50;
				bkup_ext_pitch = 50;
				bkup_ext_volume = 50;
				bkup_ext_wav_enable = false;
				bkup_ext_wav_speed = 1.0f;
				// TODO gender, lang
			}

			if (currentStatus != CSV_ANA_STIME) {
				bkup_startTime = parseIntStartEndTime(nowStr);
				if (bkup_startTime >= 0) {
					currentStatus = CSV_ANA_STIME;
				} else {
					currentStatus = CSV_ANA_IDLE;
				}
			} else {
				bkup_endTime = parseIntStartEndTime(nowStr);
				if (bkup_endTime >= 0) {
					currentStatus = CSV_ANA_ETIME;
				} else {
					currentStatus = CSV_ANA_IDLE;
				}
			}
		} else if (currentStatus == CSV_ANA_STIME) {
			if (nowStr.trim().length() == 0) {
				currentStatus = CSV_ANA_ETIME;
			}
		} else if (currentStatus == CSV_ANA_ETIME) {
			bkup_description = nowStr;
			currentStatus = CSV_ANA_DESC;
		} else if (currentStatus == CSV_ANA_DESC) {
			bkup_scenario = nowStr;
			currentStatus = CSV_ANA_SCENARIO;
		} else if (currentStatus == CSV_ANA_SCENARIO) {
			bkup_speaker = nowStr;
			currentStatus = CSV_ANA_SPEKER;
		} else if (currentStatus == CSV_ANA_SPEKER) {
			bkup_caption = nowStr;
			currentStatus = CSV_ANA_CAPTION;
		} else if (currentStatus == CSV_ANA_CAPTION) {
			bkup_comment = nowStr;
			currentStatus = CSV_ANA_COMMENT;
		} else if (currentStatus == CSV_ANA_COMMENT) {
			bkup_dataType = nowStr;
			currentStatus = CSV_ANA_TYPE;
		} else if (currentStatus == CSV_ANA_TYPE) {
			bkup_child = nowStr;
			currentStatus = CSV_ANA_CHILD;
		} else if (currentStatus == CSV_ANA_CHILD) {
			// Check format is WAV file path
			if (isFormatWavPath(nowStr)) {
				// Exchange data format to URI
				bkup_wavUri = getResource(nowStr);
				// Calculate duration time
				bkup_wavDuration = getDurationTimeWavData(nowStr);
			}
			// Check preference setting
			else {
				// Display confirmation dialog mode
				if (currentWavRule == CSV_WAV_RULE_CONFIRM) {
					// Pre process before Display confirm message box
					currentStatus = CSV_ANA_WAIT_CONFIRM;
					currentWavWait = true;
					final String targetWavFile = nowStr;

					// Display confirm message box
					PlatformUI.getWorkbench().getDisplay()
							.asyncExec(new Runnable() {
								public void run() {
									// MakeUP WAV information
									String wavInfo = LN_SEPARATOR
											+ LN_SEPARATOR
											+ "    Start Time : "
											+ TimeFormatUtil
													.makeFormatHHMMSSMS_short(bkup_startTime)
											+ LN_SEPARATOR
											+ "    WAV file   : "
											+ targetWavFile + LN_SEPARATOR;

									// Display confirmation message box
									XMLFileMessageBox wavMB = new XMLFileMessageBox(
											XMLFileMessageBox.MB_STYLE_WAV_CONFIRM,
											wavInfo);
									int mode = wavMB.open();
									// Check result
									if (mode == SWT.YES) {
										// Select a new file
										String newFilePath = openWavFileProc();
										if (newFilePath != null) {
											// Exchange data format to URI
											bkup_wavUri = getResource(newFilePath);
											// Calculate duration time
											bkup_wavDuration = getDurationTimeWavData(newFilePath);
										}
									}
									// Post process after Display confirm
									// message box
									currentWavWait = false;
								}
							});
				}
				// Save original(invalid) data mode
				else if (currentWavRule == CSV_WAV_RULE_IGNORE) {
					if (nowStr != null) {
						// Forced exchange data from string to URI
						bkup_wavUri = getResource(nowStr);
						// Set invalid data(-1)
						bkup_wavDuration = WAV_STAT_INVALID;
					}
				}
			}
			// Check wait mode
			if (currentStatus != CSV_ANA_WAIT_CONFIRM) {
				// Change mode to own process
				currentStatus = CSV_ANA_WAV;
			}
		}
		// Status 4 : Extended
		else if (currentStatus == CSV_ANA_WAV) {
			bkup_ext_extended = false;
			if ((nowStr != null) && ("1".equals(nowStr))) {
				// Set enable status
				bkup_ext_extended = true;
			}
			// Change mode to own process
			currentStatus = CSV_ANA_EXT_ENA;
		}
		// Status 5 : gender
		else if (currentStatus == CSV_ANA_EXT_ENA) {
			bkup_ext_gender = true;
			if ((nowStr != null) && ("female".equals(nowStr))) {
				// Set female
				bkup_ext_gender = false;
			}
			currentStatus = CSV_ANA_EXT_GEN;
		} else if (currentStatus == CSV_ANA_EXT_GEN) {
			bkup_ext_lang = "en-US"; // TODO
			if (nowStr != null) {
				// Check limit
				if ("ja".equals(nowStr) || "en".equals(nowStr)) {
					// Set language
					bkup_ext_lang = ("ja".equals(nowStr) ? "ja-JP" : "en-US");
				} else {
					bkup_ext_lang = nowStr;
				}

			}
			// Change mode to own process
			currentStatus = CSV_ANA_EXT_LANG;
		}
		// Status 7 : PickUP Speed of Extend
		else if (currentStatus == CSV_ANA_EXT_LANG) {
			bkup_ext_speed = 50;
			if (nowStr != null && nowStr.length() > 0) {
				// Set speed
				bkup_ext_speed = Integer.parseInt(nowStr);
				// Check limit
				if (bkup_ext_speed < 0)
					bkup_ext_speed = 0;
				else if (bkup_ext_speed > 100)
					bkup_ext_speed = 100;
			}
			// Change mode to own process
			currentStatus = CSV_ANA_EXT_SPEED;
		}
		// Status 8 : PickUP Pitch of Extend
		else if (currentStatus == CSV_ANA_EXT_SPEED) {
			bkup_ext_pitch = 50;
			if (nowStr != null && nowStr.length() > 0) {
				// Set pitch
				bkup_ext_pitch = Integer.parseInt(nowStr);
				// Check limit
				if (bkup_ext_pitch < 0)
					bkup_ext_pitch = 0;
				else if (bkup_ext_pitch > 100)
					bkup_ext_pitch = 100;
			}
			// Change mode to own process
			currentStatus = CSV_ANA_EXT_PITCH;
		}
		// Status 9 : PickUP Volume of Extend
		else if (currentStatus == CSV_ANA_EXT_PITCH) {
			bkup_ext_volume = 50;
			if (nowStr != null && nowStr.length() > 0) {
				// Set volume
				bkup_ext_volume = Integer.parseInt(nowStr);
				// Check limit
				if (bkup_ext_volume < 0)
					bkup_ext_volume = 0;
				else if (bkup_ext_volume > 100)
					bkup_ext_volume = 100;
			}
			// Change mode to own process
			currentStatus = CSV_ANA_EXT_VOL;
		}
		// Status 10 : PickUP WAV enable status of Extend
		else if (currentStatus == CSV_ANA_EXT_VOL) {
			// Check exist WAV file
			if (bkup_wavUri != null) {
				bkup_ext_wav_enable = true;
				if ((nowStr != null) && ("0".equals(nowStr))) {
					// Set disable status
					bkup_ext_wav_enable = false;
				}
			} else {
				bkup_ext_wav_enable = false;
			}
			// Change mode to own process
			currentStatus = CSV_ANA_EXT_WENA;
		}
		// Status 11 : PickUP WAV enable status of Extend
		else if (currentStatus == CSV_ANA_EXT_WENA) {
			bkup_ext_wav_speed = 1.0f;
			// Check exist WAV file
			if (bkup_wavUri != null) {
				if (nowStr != null && nowStr.length() > 0) {
					// Set WAV speed
					bkup_ext_wav_speed = Float.valueOf(nowStr) / 100.0f;
					// Check limit
					if (bkup_ext_wav_speed < 0.5f)
						bkup_ext_wav_speed = 0.5f;
					else if (bkup_ext_wav_speed > 2.0f)
						bkup_ext_wav_speed = 2.0f;
				}
			}
			// Change mode to own process
			currentStatus = CSV_ANA_EXT_WSPEED;
		}
		// Status 5 : Wait confirm message box
		else if (currentStatus == CSV_ANA_WAIT_CONFIRM) {
			// Check finish process of confirm message box
			if (!currentWavWait) {
				currentStatus = CSV_ANA_WAV;
			}
		}

		// Check wait status
		if (currentStatus != CSV_ANA_WAIT_CONFIRM) {
			// Update index counter
			currentAnalyzeData++;
			// Check end of data
			if (currentAnalyzeData >= maxAnalyzeData) {
				// Flush latest data
				if (currentStatus >= CSV_ANA_STIME) {
					// exchange null data to blank code
					if (bkup_description == null)
						bkup_description = DEF_STR_DESC;
					if (bkup_description == null)
						bkup_description = DEF_STR_DESC;
					if (bkup_scenario == null)
						bkup_scenario = DEF_STR_DESC;
					if (bkup_speaker == null)
						bkup_speaker = DEF_STR_DESC;
					if (bkup_caption == null)
						bkup_caption = DEF_STR_DESC;
					if (bkup_comment == null)
						bkup_comment = DEF_STR_DESC;
					if (bkup_dataType == null)
						bkup_comment = "D";
					if (bkup_child == null)
						bkup_child = "";

					// store current data as script data to own temporary buffer
					list_startTime.add(bkup_startTime);
					list_endTime.add(bkup_endTime);
					list_description.add(bkup_description);
					list_scenario.add(bkup_scenario);
					list_speaker.add(bkup_speaker);
					list_capton.add(bkup_caption);
					list_comment.add(bkup_comment);
					list_dataType.add(bkup_dataType);
					list_child.add(bkup_child);
					// store current data as WAV information to own temporary
					// buffer
					if (bkup_wavUri != null) {
						list_wavUri.add(bkup_wavUri);
						list_wavStartTime.add(bkup_startTime);
						list_wavDuration.add(bkup_wavDuration);
						list_ext_wav_enable.add(bkup_ext_wav_enable);
						list_ext_wav_speed.add(bkup_ext_wav_speed);
					}

					// store current Extend data as script data to own temporary
					// buffer
					list_ext_extended.add(bkup_ext_extended);
					list_ext_gender.add(bkup_ext_gender);
					list_ext_lang.add(bkup_ext_lang);
					list_ext_speed.add(bkup_ext_speed);
					list_ext_pitch.add(bkup_ext_pitch);
					list_ext_volume.add(bkup_ext_volume);
				}
				// End of process
				result = true;
			}
		}

		// return result
		return (result);
	}

	/**
	 * @category Check format is StartTime
	 * @param strStartTime
	 *            : string of StartTime
	 * @return result : TRUE:Time format, FALSE:otherwise format
	 */
	private boolean isFormatStartTime(String strStartTime, String strParseFormat) {
		boolean result = false;
		try {
			// MakeUP Date(Time) format
			DateFormat df = new SimpleDateFormat(strParseFormat);
			Date date = df.parse(strStartTime);
			// Check current string format is StartTime
			if (date != null) {
				// this is time format
				result = true;
			}
		} catch (ParseException pe) {
		}
		return (result);
	}

	/**
	 * @category Check format is URL
	 * @param strUrl
	 *            : string of URL
	 * @return result : TRUE:URL format, FALSE:otherwise format
	 */
	private boolean isFormatUrl(String strOrg) {
		boolean result = false;

		try {
			// trimming blank code
			String strUri = strOrg.trim();
			// MakeUP URI data
			URI uri = getResource(strUri);
			// Check current string format is URL
			if (uri != null) {
				// this is URL format
				result = true;
			}
		} catch (Exception ue) {
			// System.out.println("isFormatUrl() : " +ue);
		}
		// return result
		return (result);
	}

	/**
	 * @category Check format is WAV file path
	 * @param strUrl
	 *            : string of URL
	 * @return result : TRUE:WAV file path, FALSE:otherwise format
	 */
	private boolean isFormatWavPath(String strWavPath) {
		boolean result = false;
		try {
			// Check URL format
			if (isFormatUrl(strWavPath)) {
				if (strWavPath.trim().toLowerCase().endsWith(".wav") == true) {
					if (WavUtil.isWavFormat(strWavPath)) {
						result = true;
					}
				}
			}
		} catch (Exception ue) {
		}
		return (result);
	}

	/**
	 * @category Calculate duration time of WAV file
	 * @param strUrl
	 *            : string of URL
	 * @return duration time
	 */
	private int getDurationTimeWavData(String strWavPath) {
		int duration = -1;

		try {
			// Check WAV header information
			if (WavUtil.isWavFormat(strWavPath)) {
				// PickUP file header from current WAV file
				SoundMixer.getInstance()
						.storeWavHeader(getResource(strWavPath));
				// Calculate duration time of WAV file
				duration = SoundMixer.getInstance().getDurationTimeWav();
			}
		} catch (Exception ue) {
			// System.out.println("getWavDurationTime() : " +ue);
		}
		// return result : -1 is not WAV format
		return (duration);
	}

	/**
	 * @category Open WAV file process
	 * @param filePath
	 *            : target file path
	 */
	public String openWavFileProc() {
		String[] EXTENSIONS = { "*.wav", "*" };
		String wavFileName = null;

		try {
			// TODO : "Display.getCurrent().getActiveShell()" is null
			FileDialog openDialog = new FileDialog(Display.getCurrent()
					.getActiveShell(), SWT.OPEN);
			openDialog.setFilterExtensions(EXTENSIONS);
			wavFileName = openDialog.open();
			if (wavFileName != null) {
				if (!WavUtil.isWavFormat(wavFileName)) {
					wavFileName = null;
				}
			}
		} catch (Exception we) {
			we.printStackTrace();
		}
		return (wavFileName);
	}

	/**
	 * Getter method : Get resource URL string
	 */
	private URI getResource(String fpath) {
		URI result = null;

		try {
			// exchange type from String to URI
			File fh = new File(fpath);
			result = fh.toURI();
		} catch (Exception ee) {
			// System.out.println("getResource() : " +ee);
		}
		// return result
		return (result);
	}

	/**
	 * @category Local method : Parse integer data from string data
	 * @param strStartTime
	 *            : string time data
	 * @return integer time data(millisecond)
	 */
	private int parseIntStartEndTime(String strStartTime) {
		int startTime = -1;

		// split time code
		String[] splitStr = strStartTime.split(":");
		// Check pattern format of StartTime (SS:MM:mmm)
		if (splitStr.length == 3
				&& isFormatStartTime(strStartTime, FORMAT_STIME_MMSSmmm)) {
			// MakeUP integer time data
			int mm = Integer.parseInt(splitStr[0].trim());
			int ss = Integer.parseInt(splitStr[1].trim());
			int msec = Integer.parseInt(splitStr[2].trim());
			startTime = (((mm * 60) + ss) * MSEC) + msec;
		}
		// Check pattern format of StartTime (SS:MM)
		else if (splitStr.length == 2
				&& isFormatStartTime(strStartTime, FORMAT_STIME_MMSS)) {
			// MakeUP integer time data
			int mm = Integer.parseInt(splitStr[0].trim());
			int ss = Integer.parseInt(splitStr[1].trim());
			startTime = ((mm * 60) + ss) * MSEC;
		}
		// Check pattern format of StartTime (HH:SS:MM:mmm)
		else if (splitStr.length == 4
				&& isFormatStartTime(strStartTime, FORMAT_STIME_HHMMSSmmm)) {
			int hh = Integer.parseInt(splitStr[0].trim());
			int mm = Integer.parseInt(splitStr[1].trim());
			int ss = Integer.parseInt(splitStr[2].trim());
			int msec = Integer.parseInt(splitStr[3].trim());
			startTime = (((hh * 3600) + (mm * 60) + ss) * MSEC) + msec;
		}
		return (startTime);
	}

	/**
	 * @category Local method : Save description data to ScriptList
	 * @return result process : TRUE:Finished process, FALSE:Active process
	 * @throws IOException
	 */
	private boolean saveCSVData() throws IOException {
		boolean result = false;

		// Check preference setting of CSV save rule
		if (CSV_SAVE_RULE_RENEWAL == CSVRulePreferenceUtil
				.getPreferenceCsvSaveRule()) {
			// TODO check
			// clear current script & WAV list
			// ScriptDataOld.getInstance().clearScriptData();
			// ScriptDataOld.getInstance().cleanupWavList();
			//
			// scriptManager.clearScriptData();
		}

		dataList.clear();
		// restore list data to script & WAV list
		for (int i = 0; i < list_startTime.size(); i++) {
			// PickUP current data from temporary list
			int startTime = list_startTime.get(i);
			int endTime = list_endTime.get(i);
			String description = list_description.get(i);
			String scenario = list_scenario.get(i);
			String speaker = list_speaker.get(i);
			String capton = list_capton.get(i);
			String comment = list_comment.get(i);
			String dataType = list_dataType.get(i);

			// String child = list_child.get(i);
			// PickUP current Extend data from temporary list
			boolean extended = list_ext_extended.get(i);
			boolean gender = list_ext_gender.get(i);
			String lang = list_ext_lang.get(i);
			int speed = list_ext_speed.get(i).intValue();
			int pitch = list_ext_pitch.get(i);
			int volume = list_ext_volume.get(i);
			IScriptData data = ScriptDataFactory.createNewData();
			data.setStartTime(startTime);

			if ("D".equalsIgnoreCase(dataType) == true) {
				data.setType(IScriptData.TYPE_AUDIO);
				data.setDescription(description);
			} else if ("C".equalsIgnoreCase(dataType) == true) {
				data.setType(IScriptData.TYPE_CAPTION);
				data.setCaption(capton);
			} else {
				data.setType(IScriptData.TYPE_SCENARIO);
				data.setScenario(scenario);
			}
			data.setCharacter(speaker);
			data.setScriptComment(comment);
			data.setVgGender(gender);
			data.setExtended(extended);
			data.setVgPlaySpeed(speed);
			data.setVgPitch(pitch);
			data.setVgVolume(volume);
			data.setLang(lang);
			data.setMark(NO_MARK);
			data.setDataCommit(true);
			// Check exist WAV file
			int indexWav = list_wavStartTime.indexOf(startTime);
			if (indexWav >= 0) {
				// PickUP target WAV information from temporary list
				URI wavUri = list_wavUri.get(indexWav);
				int wavDuration = list_wavDuration.get(indexWav);
				// PickUP target WAV Extend information from temporary list
				boolean wavEnable = list_ext_wav_enable.get(indexWav);
				float wavSpeed = list_ext_wav_speed.get(indexWav);
				data.setWavURI(wavUri);
				data.setWavEnabled(wavEnable);
				data.setWavPlaySpeed(wavSpeed);
				data.setWavEndTime(data.getStartTime() + wavDuration);
				// TODO : Update WAV file list
				// // EditPanelView.getInstance().getInstanceTabEditPanel()
				// // .appendDataWavList(startTime, wavDuration, wavUri,
				// // wavEnable, wavSpeed);
			}

			int length = VoicePlayerFactory.getInstance().getSpeakLength(data);
			if (length > 0) {
				data.setEndTime(data.getStartTime() + length);
				data.setEndTimeAccurate(true);
			} else {
				if (endTime > startTime) {
					data.setEndTime(endTime);
				} else if ((description.trim().length() > 0)) {
					data.setEndTime(startTime
							+ DataUtil.sumMoraCount(description, lang));
				}
			}

			dataList.add(data);

		}
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				// Repaint all screen by new Script
				// list
				TimeLineView.getInstance().synchronizeAllTimeLine(0);
				eventManager.fireSyncTimeEvent(new SyncTimeEvent(0, this)); // Rewind
				WebBrowserFactory.getInstance().setCurrentPosition(0); // Rewind
																		// low
																		// level
																		// function
				dataEventManager.fireLabelEvent(new LabelEvent(
						LabelEvent.CLEAR_LABEL, null, this)); // clear exists

				if (CSV_SAVE_RULE_RENEWAL == CSVRulePreferenceUtil
						.getPreferenceCsvSaveRule()) {
					dataEventManager.fireGuideListEvent(new GuideListEvent(
							GuideListEvent.CLEAR_DATA, null, this)); // clear
																		// exists
				}

				for (IScriptData data : dataList) {
					dataEventManager.fireGuideListEvent(new GuideListEvent(
							GuideListEvent.ADD_DATA, data, this));
				}
				// AudioEvent.PUT_AUDIO_LABEL check overlay each times , so it
				// takes long time.
				// Therefore, must use PUT_ALL_LABEL which check all labels
				// overlay at once.
				dataEventManager.fireLabelEvent(new LabelEvent(
						LabelEvent.PUT_ALL_LABEL, null, this));
			}
		});

		// Close process of file reader
		// finish own process
		result = true;

		// return result
		return (result);
	}

	/**
	 * @category Local method : post process for CSV file reader
	 */
	private void postCSVReader() {
		if (EditPanelView.getInstance() != null) {
			if (EditPanelView.getInstance().getInstanceTabEditPanel() != null) {
				EditPanelView.getInstance().getInstanceTabEditPanel()
						.initDispEditPanel();
			}
			if (EditPanelView.getInstance().getInstanceTabSelWAVFile() != null) {
				// initialize all parameters
				EditPanelView.getInstance().getInstanceTabSelWAVFile()
						.initDescriptionData();
				EditPanelView.getInstance().getInstanceTabSelWAVFile()
						.initDispSelWavFile();
			}
		}
		// initialize own screen
		if (TimeLineView.getInstance() != null) {
			TimeLineView.getInstance().reqExpandTimeLine();
			TimeLineView.getInstance().reqRedrawTimeLineCanvas(1);
			TimeLineView.getInstance().rewindActionTimeLine();
		}
	}

	/**
	 * @category Local Thread class : CSV file reader thread
	 */
	private class ThreadCSVReader extends Thread {
		/**
		 * @category Override {@link Thread#run()}
		 */
		public void run() {
			try {
				boolean ret;
				while (currentActive) {
					// Check current process status
					if (currentProcess == CSV_PROC_LOAD) {
						// Loading string data from CSV file
						ret = loadCSVData();
						// check end of loading
						if (ret) {
							// Initial variables
							currentAnalyzeData = 0;
							maxAnalyzeData = rawCsvDataList.size();
							// change next mode
							currentProcess++;
						}
						// } else if (currentProcess == CSV_SUB_PROC_CAT) {
						// // Cat string mode for splitting a few line
						// catStringCSVData();
					} else if (currentProcess == CSV_PROC_ANALYZE) {
						// Analyzing string data
						boolean result = analyzeCSVData();
						// check end of analyzing
						if (result) {
							// change next mode
							currentProcess++;
						}
					} else if (currentProcess == CSV_PROC_SAVE) {
						// Saving data to ScriptList
						ret = saveCSVData();
						// check end of saving
						if (ret) {
							// Post process for Repaint all screen
							PlatformUI.getWorkbench().getDisplay()
									.asyncExec(new Runnable() {
										public void run() {
											postCSVReader();
										}
									});
							closeCSVReader();
						}
					}
					// Thread.yield();
				}
			} catch (Exception e) {
				e.printStackTrace();
				closeCSVReader();
			}
		}
	}

}
