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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.eclipse.actf.ai.internal.ui.scripteditor.XMLFileMessageBox;
import org.eclipse.actf.ai.scripteditor.data.ScriptData;
import org.eclipse.actf.ai.scripteditor.preferences.CSVRulePreferenceUtil;
import org.eclipse.actf.ai.scripteditor.util.SoundMixer;
import org.eclipse.actf.ai.ui.scripteditor.views.EditPanelView;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.ai.ui.scripteditor.views.ScriptListView;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

public class CSVReader implements IUNIT {

	// process status
	private static final int CSV_PROC_IDLE = 0; // 0 : idle mode
	private static final int CSV_PROC_LOAD = 1; // 1 : now loading CSV file (as
												// String data)
	private static final int CSV_PROC_ANALYZE = 2; // 2 : now analyzing loaded
													// data
	private static final int CSV_PROC_SAVE = 3; // 3 : now saving to ScriptList
	private static final int CSV_SUB_PROC_CAT = 11; // 11 : Sub mode : now cat
													// string mode

	// sub status of analyze mode
	private static final int CSV_ANA_IDLE = 0; // 0 : idle mode
	private static final int CSV_ANA_STIME = 1; // 1 : now analyzing start time
												// value
	private static final int CSV_ANA_WAV = 2; // 2 : now analyzing URI value of
												// WAV file path
	private static final int CSV_ANA_WAIT_CONFIRM = 3; // 3 : now waiting
														// confirm message box
														// process
	private static final int CSV_ANA_EXT_ENA = 4; // 4 : now analyzing enable
													// status of Extend
	private static final int CSV_ANA_EXT_GEN = 5; // 5 : now analyzing gender of
													// Extend
	private static final int CSV_ANA_EXT_LANG = 6; // 6 : now analyzing language
													// of Extend
	private static final int CSV_ANA_EXT_SPEED = 7; // 7 : now analyzing speed
													// of Extend
	private static final int CSV_ANA_EXT_PITCH = 8; // 8 : now analyzing pitch
													// of Extend
	private static final int CSV_ANA_EXT_VOL = 9; // 9 : now analyzing volume of
													// Extend
	private static final int CSV_ANA_EXT_WENA = 10; // 10 : now analyzing WAV
													// enable status of Extend
	private static final int CSV_ANA_EXT_WSPEED = 11; // 11 : now analyzing WAV
														// speed of Extend
	private static final int CSV_ANA_DESC = 12; // 12 : now analyzing
												// description value

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
	private String bkup_description = null;
	private URI bkup_wavUri = null;
	private int bkup_wavDuration = -1;
	// variables for extended information
	private boolean bkup_ext_extended = false;
	private boolean bkup_ext_gender = true;
	private int bkup_ext_lang = 0;
	private int bkup_ext_speed = 50;
	private int bkup_ext_pitch = 50;
	private int bkup_ext_volume = 50;
	private boolean bkup_ext_wav_enable = false;
	private float bkup_ext_wav_speed = 1.0f;

	// variables for save process
	private ArrayList<Integer> list_startTime;
	private ArrayList<String> list_description;
	private ArrayList<URI> list_wavUri;
	private ArrayList<Integer> list_wavStartTime;
	private ArrayList<Integer> list_wavDuration;
	// variables for extend information
	private ArrayList<Boolean> list_ext_extended;
	private ArrayList<Boolean> list_ext_gender;
	private ArrayList<Integer> list_ext_lang;
	private ArrayList<Integer> list_ext_speed;
	private ArrayList<Integer> list_ext_pitch;
	private ArrayList<Integer> list_ext_volume;
	private ArrayList<Boolean> list_ext_wav_enable;
	private ArrayList<Float> list_ext_wav_speed;

	// input streams
	private InputStream inCsvStream;
	private BufferedReader bufCsvReader;
	private ArrayList<String> rawCsvDataList = null;
	private StringBuilder rawCsvCatString = null;
	private int maxAnalyzeData = 0;
	private int currentAnalyzeData = 0;
	// private boolean nowExceptionNoWavFile = false;

	// Thread of file reader
	private ThreadCSVReader thCsvRd = null;
	private boolean currentActive = false;
	private int currentProcess = CSV_PROC_IDLE;

	/**
	 * Constructor
	 */
	public CSVReader() {
		// Allocate array list for load process
		rawCsvDataList = new ArrayList<String>();
		// Allocate array list for save process
		list_startTime = new ArrayList<Integer>();
		list_description = new ArrayList<String>();
		list_wavUri = new ArrayList<URI>();
		list_wavStartTime = new ArrayList<Integer>();
		list_wavDuration = new ArrayList<Integer>();
		// Allocate array list for extend information
		list_ext_extended = new ArrayList<Boolean>();
		list_ext_gender = new ArrayList<Boolean>();
		list_ext_lang = new ArrayList<Integer>();
		list_ext_speed = new ArrayList<Integer>();
		list_ext_pitch = new ArrayList<Integer>();
		list_ext_volume = new ArrayList<Integer>();
		list_ext_wav_enable = new ArrayList<Boolean>();
		list_ext_wav_speed = new ArrayList<Float>();
		// Initialize all status flag
		currentProcess = CSV_PROC_IDLE;
		currentStatus = CSV_ANA_IDLE;
		currentActive = false;
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
			list_description.clear();
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
			bkup_description = null;
			bkup_wavUri = null;
			bkup_wavDuration = -1;
			// reset all extend variables
			bkup_ext_extended = false;
			bkup_ext_gender = true;
			bkup_ext_lang = 0;
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
				// Store line string to temporary buffer
				String[] tempLineData = rawLineData.split(",");
				// Store splitting string with trimming blank code
				for (int i = 0; i < tempLineData.length; i++) {
					// trim blank code of current string
					String trimLineData = tempLineData[i].trim();
					if (trimLineData.length() == 0) {
						// undo parent data (may be, all blank code)
						trimLineData = tempLineData[i];
					}
					// Check double quotation code for cat string
					int index = trimLineData.indexOf("\"");
					if (index >= 0) {
						// Trim double quotation code from current string
						String trimLineData2 = trimLineData
								.replaceAll("\"", "");
						// Initialize StringBuilder for cat string
						rawCsvCatString = new StringBuilder(trimLineData2);
						// Blanch to cat string mode
						currentProcess = CSV_SUB_PROC_CAT;
					} else {
						// append all splitting string to array list
						rawCsvDataList.add(trimLineData);
					}
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
	 * @category Local method : Cat string during load line data from CSV file
	 * @return result process : TRUE:Finished process, FALSE:Active process
	 * @throws IOException
	 */
	private boolean catStringCSVData() throws IOException {
		boolean result = false;
		if (bufCsvReader != null) {
			// load current line from CSV file
			String rawLineData = bufCsvReader.readLine();
			if (rawLineData != null) {
				// Store line string to temporary buffer
				String[] tempLineData = rawLineData.split(",");
				// Store splitting string with trimming blank code
				for (int i = 0; i < tempLineData.length; i++) {
					// trim blank code of current string
					String trimLineData = tempLineData[i].trim();
					if (trimLineData.length() == 0) {
						// undo parent data (may be, all blank code)
						trimLineData = tempLineData[i];
					}

					// Check cat string mode
					if (currentProcess == CSV_SUB_PROC_CAT) {
						// Check double quotation code for cat string
						int index = trimLineData.indexOf("\"");
						if (index >= 0) {
							// Trim double quotation code from current string
							String trimLineData2 = trimLineData.replaceAll(
									"\"", "");
							rawCsvCatString.append(trimLineData2);
							// append all splitting string to array list
							rawCsvDataList.add(rawCsvCatString.toString());
							rawCsvCatString = null;
							// Recovery status to loading mode
							currentProcess = CSV_PROC_LOAD;
							// End of current process
							result = true;
						} else {
							// Cat string with line separator
							rawCsvCatString.append(trimLineData);
							rawCsvCatString.append(System
									.getProperty("line.separator"));
						}
					} else {
						// append all splitting string to array list
						rawCsvDataList.add(trimLineData);
					}
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
			if (currentStatus >= CSV_ANA_STIME) {
				// exchange null data to blank code
				if (bkup_description == null)
					bkup_description = DEF_STR_DESC;

				// store current data as script data to own temporary buffer
				list_startTime.add(bkup_startTime);
				list_description.add(bkup_description);
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
				bkup_ext_gender = true;
				bkup_ext_lang = EditPanelView.getInstance()
						.getInstanceTabEditPanel().getLangDescription();
				bkup_ext_speed = 50;
				bkup_ext_pitch = 50;
				bkup_ext_volume = 50;
				bkup_ext_wav_enable = false;
				bkup_ext_wav_speed = 1.0f;
			}

			// Exchange data format to ScriptData
			bkup_startTime = parseIntStartTime(nowStr);
			// Check result
			if (bkup_startTime >= 0) {
				// Change mode own process
				currentStatus = CSV_ANA_STIME;
			} else {
				// illegal data
				currentStatus = CSV_ANA_IDLE;
			}
		}
		// Status 2 : Check format is WAV file path
		else if (currentStatus == CSV_ANA_STIME) {
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
											+ ScriptData.getInstance()
													.makeFormatMMSSMS(
															bkup_startTime)
											+ LN_SEPARATOR
											+ "    WAV file   : "
											+ targetWavFile + LN_SEPARATOR;

									// Display confirmation message box
									XMLFileMessageBox wavMB = new XMLFileMessageBox(
											MB_STYLE_WAV_CONFIRM, wavInfo);
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
		// Status 4 : PickUP enable status of Extend
		else if (currentStatus == CSV_ANA_WAV) {
			bkup_ext_extended = false;
			if ((nowStr != null) && ("1".equals(nowStr))) {
				// Set enable status
				bkup_ext_extended = true;
			}
			// Change mode to own process
			currentStatus = CSV_ANA_EXT_ENA;
		}
		// Status 5 : PickUP gender of Extend
		else if (currentStatus == CSV_ANA_EXT_ENA) {
			bkup_ext_gender = true;
			if ((nowStr != null) && ("female".equals(nowStr))) {
				// Set female
				bkup_ext_gender = false;
			}
			// Change mode to own process
			currentStatus = CSV_ANA_EXT_GEN;
		}
		// Status 6 : PickUP language of Extend
		else if (currentStatus == CSV_ANA_EXT_GEN) {
			bkup_ext_lang = EditPanelView.getInstance()
					.getInstanceTabEditPanel().getLangDescription();
			if (nowStr != null) {
				// Check limit
				if ("ja".equals(nowStr) || "en".equals(nowStr)) {
					// Set language
					bkup_ext_lang = ("ja".equals(nowStr) ? DESC_LANG_JA
							: DESC_LANG_EN);
				}
			}
			// Change mode to own process
			currentStatus = CSV_ANA_EXT_LANG;
		}
		// Status 7 : PickUP Speed of Extend
		else if (currentStatus == CSV_ANA_EXT_LANG) {
			bkup_ext_speed = 50;
			if (nowStr != null) {
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
			if (nowStr != null) {
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
			if (nowStr != null) {
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
				if (nowStr != null) {
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
		// Status 12 : PickUP string of description
		else if (currentStatus == CSV_ANA_EXT_WSPEED) {
			// Check null string
			if (!nowStr.equals("")) {
				// This is description string
				bkup_description = nowStr;
			}
			// Change mode to own process
			currentStatus = CSV_ANA_DESC;
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

					// store current data as script data to own temporary buffer
					list_startTime.add(bkup_startTime);
					list_description.add(bkup_description);
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
			// System.out.println("isFormatStartTime() : " +pe);
		}
		// return result
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
	 * @category Check format is Local path
	 * @param strUrl
	 *            : string of URL
	 * @return result : TRUE:local path, FALSE:otherwise format
	 */
	private boolean isFormatLocalUri(String strOrg) {
		boolean result = false;

		try {
			// trimming blank code
			String strUri = strOrg.trim();
			// MakeUP URI data
			URI uri = getResource(strUri);
			// Check current string format is URL
			if (uri != null) {
				// Check current URL is local path
				int index = uri.toString().indexOf("file:/");
				if (index >= 0) {
					// this is local path
					result = true;
				}
			}
		} catch (Exception ue) {
			// System.out.println("isFormatLocalUri() : " +ue);
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
				// Check WAV file
				int index = strWavPath.indexOf(".wav");
				if (index >= 0) {
					// Check WAV header information
					if (SoundMixer.getInstance().isWavFormat(strWavPath)) {
						// this is WAV file path
						result = true;
					}
				}
			}
		} catch (FileNotFoundException fnfe) {
			// Catch File not found Exception
		} catch (Exception ue) {
			// System.out.println("isFormatWavPath() : " +ue);
		}
		// return result
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
			if (SoundMixer.getInstance().isWavFormat(strWavPath)) {
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
			// Request FileDialog (Choice open file name)
			FileDialog openDialog = new FileDialog(Display.getCurrent()
					.getActiveShell(), SWT.OPEN);
			openDialog.setFilterExtensions(EXTENSIONS);
			wavFileName = openDialog.open();

			// Check null (file name)
			if (wavFileName != null) {
				// check file header
				if (!SoundMixer.getInstance().isWavFormat(wavFileName)) {
					// invalid file again!
					wavFileName = null;
				}
			}
		} catch (Exception we) {
		}

		// return result
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
	private int parseIntStartTime(String strStartTime) {
		int startTime = -1;

		// Check pattern format of StartTime (SS:MM:mmm)
		if (isFormatStartTime(strStartTime, FORMAT_STIME_MMSSmmm)) {
			// split time code
			String[] splitStr = strStartTime.split(":");
			// MakeUP integer time data
			if (splitStr.length == 3) {
				int mm = Integer.parseInt(splitStr[0].trim());
				int ss = Integer.parseInt(splitStr[1].trim());
				int msec = Integer.parseInt(splitStr[2].trim());
				startTime = (((mm * 60) + ss) * MSEC) + msec;
			}
		}
		// Check pattern format of StartTime (SS:MM)
		else if (isFormatStartTime(strStartTime, FORMAT_STIME_MMSS)) {
			// split time code
			String[] splitStr = strStartTime.split(":");
			// MakeUP integer time data
			if (splitStr.length == 2) {
				int mm = Integer.parseInt(splitStr[0].trim());
				int ss = Integer.parseInt(splitStr[1].trim());
				startTime = ((mm * 60) + ss) * MSEC;
			}
		}
		// Check pattern format of StartTime (HH:SS:MM:mmm)
		else if (isFormatStartTime(strStartTime, FORMAT_STIME_HHMMSSmmm)) {
			// split time code
			String[] splitStr = strStartTime.split(":");
			// MakeUP integer time data
			if (splitStr.length == 4) {
				int hh = Integer.parseInt(splitStr[0].trim());
				int mm = Integer.parseInt(splitStr[1].trim());
				int ss = Integer.parseInt(splitStr[2].trim());
				int msec = Integer.parseInt(splitStr[3].trim());
				startTime = (((hh * 3600) + (mm * 60) + ss) * MSEC) + msec;
			}
		}

		// return result
		return (startTime);
	}

	/**
	 * @category Check exist all elements for script data
	 * @return result : TRUE:exist all elements, FALSE:no yet
	 */
	private boolean isExistAllElements() {
		boolean result = false;

		// Check exist all elements for script data
		if ((bkup_startTime >= 0) && (bkup_description != null)) {
			// exist all elements
			result = true;
		}
		// return result
		return (result);
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
			// clear current script & WAV list
			ScriptData.getInstance().clearScriptData();
			ScriptData.getInstance().cleanupWavList();
		}

		// restore list data to script & WAV list
		for (int i = 0; i < list_startTime.size(); i++) {
			// PickUP current data from temporary list
			int startTime = list_startTime.get(i);
			String description = list_description.get(i);
			// PickUP current Extend data from temporary list
			boolean extended = list_ext_extended.get(i);
			boolean gender = list_ext_gender.get(i);
			int lang = list_ext_lang.get(i);
			int speed = list_ext_speed.get(i);
			int pitch = list_ext_pitch.get(i);
			int volume = list_ext_volume.get(i);

			// Update ScriptList
			EditPanelView
					.getInstance()
					.getInstanceTabEditPanel()
					.appendScriptData(startTime, description, extended, gender,
							lang, speed, pitch, volume);

			// Check exist WAV file
			int indexWav = list_wavStartTime.indexOf(startTime);
			if (indexWav >= 0) {
				// PickUP target WAV information from temporary list
				URI wavUri = list_wavUri.get(indexWav);
				int wavDuration = list_wavDuration.get(indexWav);
				// PickUP target WAV Extend information from temporary list
				boolean wavEnable = list_ext_wav_enable.get(indexWav);
				float wavSpeed = list_ext_wav_speed.get(indexWav);

				// Update WAV file list
				EditPanelView
						.getInstance()
						.getInstanceTabEditPanel()
						.appendDataWavList(startTime, wavDuration, wavUri,
								wavEnable, wavSpeed);
			}
		}
		// finish own process
		result = true;

		// return result
		return (result);
	}

	/**
	 * @category Local method : post process for CSV file reader
	 */
	private void postCSVReader() {
		// Repaint Script List
		ScriptListView.getInstance().getInstScriptList().reloadScriptList();
		// Initialize Edit Panel contents
		EditPanelView.getInstance().getInstanceTabEditPanel()
				.initDispEditPanel();
		// initialize all parameters
		EditPanelView.getInstance().getInstanceTabSelWAVFile()
				.initDescriptionStruct();
		// initialize own screen
		EditPanelView.getInstance().getInstanceTabSelWAVFile()
				.initDispSelWavFile();
		// Expand Composite of TimeLine
		TimeLineView.getInstance().reqExpandTimeLine();
		// Repaint image of TimeLine Scale
		TimeLineView.getInstance().reqRedrawTimeLineCanvas(1);
		// Repaint TimeLine's Audio Label
		TimeLineView.getInstance().refreshScriptAudio();
		// Reset location of TimeLine
		TimeLineView.getInstance().rewindActionTimeLine();
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
					} else if (currentProcess == CSV_SUB_PROC_CAT) {
						// Cat string mode for splitting a few line
						catStringCSVData();
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
											// Repaint all screen by new Script
											// list
											postCSVReader();
										}
									});

							// Close process of file reader
							closeCSVReader();
						}
					}
					// idle own thread
					Thread.yield();
				}
			} catch (Exception e) {
				// forced call post process
				closeCSVReader();
			}
		}
	}

}
