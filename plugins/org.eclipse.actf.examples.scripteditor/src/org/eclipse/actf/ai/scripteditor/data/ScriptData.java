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

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;

import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.util.FileUtils;

/**
 * @category Script data class
 * 
 */
public class ScriptData implements IUNIT {

	/**
	 * Structured Unit of ScriptData
	 */
	private class StructExtendData {

		// 0)Index of Extend data (Start Time)
		private int StartTime;

		// 1)Extend : CheckBox of Extended
		private Boolean Extended;
		// 2)Extend : RadioButton of Gender
		private Boolean Gender;
		// 3)Extend : Speed of Machine Voice
		private int Speed;
		// 4)Extend : Pitch of Machine Voice
		private int Pitch;
		// 5)Extend : Volume of Machine Voice
		private int Volume;

		// Special)Extend : Language of Description
		private int Lang;

		/**
		 * Constructor
		 */
		public StructExtendData(int starttime, boolean extended,
				boolean gender, int speed, int pitch, int volume, int lang) {
			// Store index(Start Time)
			StartTime = starttime;
			// Store Extended data
			Extended = extended;
			Gender = gender;
			Speed = speed;
			Pitch = pitch;
			Volume = volume;
			Lang = lang;
		}

		/**
		 * Getter method : Get Start Time
		 */
		public int getStartTime() {
			// return current Start Time
			return (StartTime);
		}

		/**
		 * Getter method : Get CheckBox of Extended
		 */
		public Boolean getExtended() {
			// return current CheckBox of Extended
			return (Extended);
		}

		/**
		 * Getter method : Get RadioButton of Gender
		 */
		public Boolean getGender() {
			// return current RadioButton of Gender
			return (Gender);
		}

		/**
		 * Getter method : Get Speed of Voice
		 */
		public int getSpeed() {
			// return current Speed of Voice
			return (Speed);
		}

		/**
		 * Getter method : Get Pitch of Voice
		 */
		public int getPitch() {
			// return current Pitch of Voice
			return (Pitch);
		}

		/**
		 * Getter method : Get Volume of Voice
		 */
		public int getVolume() {
			// return current Volume of Voice
			return (Volume);
		}

		/**
		 * Getter method : Get Language of Description
		 */
		public int getLang() {
			// return current Language of Description
			return (Lang);
		}

		/**
		 * Setter method : Set Start Time
		 */
		public void setStartTime(int starttime) {
			// update current StartTime
			StartTime = starttime;
		}

		/**
		 * Setter method : Set CheckBox of Extended
		 */
		public void setExtended(Boolean extended) {
			// update current Extended
			Extended = extended;
		}

		/**
		 * Setter method : Set RadioButton of Gender
		 */
		public void setGender(Boolean gender) {
			// update current Gender
			Gender = gender;
		}

		/**
		 * Setter method : Set Speed of Voice
		 */
		public void setSpeed(int speed) {
			// update current Speed
			Speed = speed;
		}

		/**
		 * Setter method : Set Pitch of Voice
		 */
		public void setPitch(int pitch) {
			// update current Pitch
			Pitch = pitch;
		}

		/**
		 * Setter method : Set Volume of Voice
		 */
		public void setVolume(int volume) {
			// update current Volume of Voice
			Volume = volume;
		}

		/**
		 * Setter method : Set Language of Description
		 */
		public void setLang(int lang) {
			// update current Language of Description
			Lang = lang;
		}

	}

	/**
	 * Local class for structured script comment list
	 * 
	 */
	private class StructScriptComment {

		// Start time (relationship ScriptData's start time)
		private int startTime;

		// Comment
		private String comment;

		/**
		 * Constructor
		 * 
		 * @param startTime
		 *            : start time (relationship ScriptData's start time)
		 * @param comment
		 *            : comment of current description
		 */
		public StructScriptComment(int startTime, String comment) {
			// Store comment data
			this.startTime = startTime;
			this.comment = new String(comment);
		}

		/**
		 * Getter method : Get current start time
		 * 
		 * @return start time
		 */
		public int getStartTime() {
			return (startTime);
		}

		/**
		 * Getter method : Get current comment string
		 * 
		 * @return comment string
		 */
		public String getScriptComment() {
			return (comment);
		}

		/**
		 * Setter method : Set start time
		 * 
		 * @param startTime
		 *            : new start time value
		 */
		public void setStartTime(int startTime) {
			// store new value
			this.startTime = startTime;
		}

		/**
		 * Setter method : Set comment string
		 * 
		 * @param comment
		 *            : new comment string
		 */
		public void setScriptComment(String comment) {
			// store new value
			this.comment = comment;
		}
	}

	/**
	 * static data
	 */
	static private ScriptData ownInst = null;

	/**
	 * Private data
	 */
	// Script data List
	private ArrayList<String> ScriptList;
	// Script Start Time
	private ArrayList<Integer> ScriptStartTime;
	// Script End Time
	private ArrayList<Integer> ScriptEndTime;
	// Script Comment
	private ArrayList<StructScriptComment> ScriptComment;

	// Extended data List
	private ArrayList<StructExtendData> ExtendList;

	// WAV file list
	private ArrayList<Integer> listStartTimeWav = null;
	private ArrayList<Integer> listEndTimeWav = null;
	private ArrayList<URI> listFileNameWav = null;
	private ArrayList<Boolean> listEnableWav = null;
	private ArrayList<Float> listPlaySpeedWav = null;

	// Save status
	private boolean status_edit_scripts = false;
	private boolean status_import_csv = false;

	/**
     * 
     */
	static public ScriptData getInstance() {

		// 1st check current Own Instance
		if (ownInst == null) {
			synchronized (ScriptData.class) {
				// 2nd check current Own instance
				if (ownInst == null) {
					// New own class at once
					ownInst = new ScriptData();
				}
			}
		}
		// return current Instance of ScriptData
		return (ownInst);
	}

	/**
	 * Constructor
	 */
	public ScriptData() {
		// Initialize Script data list
		initializeScriptData();
		// Initialize WAV data list
		initializeWavData();
	}

	/**
	 * Local method : Initialize Script data list
	 */
	private void initializeScriptData() {
		// Allocate array data
		ScriptList = new ArrayList<String>();
		ScriptStartTime = new ArrayList<Integer>();
		ScriptEndTime = new ArrayList<Integer>();
		ExtendList = new ArrayList<StructExtendData>();
		ScriptComment = new ArrayList<StructScriptComment>();
	}

	/**
	 * Local method : Initialize WAV data list
	 */
	private void initializeWavData() {
		// Create ArrayList for control WAV file
		listStartTimeWav = new ArrayList<Integer>();
		listEndTimeWav = new ArrayList<Integer>();
		listFileNameWav = new ArrayList<URI>();
		listEnableWav = new ArrayList<Boolean>();
		listPlaySpeedWav = new ArrayList<Float>();
	}

	public void clearScriptData() {
		// Clear all ArrayList
		ScriptList.clear();
		ScriptStartTime.clear();
		ScriptEndTime.clear();
		ScriptComment.clear();
		ExtendList.clear();
	}

	private int indexScriptData(int startTime) {
		// if index is '-1', then no data
		int index = -1;

		// ScriptList empty is always 1st index
		if (ScriptStartTime.isEmpty()) {
			index = 0;
		} else {
			// search start time from current ScriptList
			int i;
			for (i = 0; i < ScriptStartTime.size(); i++) {
				// exist data?
				if (startTime < ScriptStartTime.get(i)) {
					// exist data.
					break;
				}
			}
			// update index
			index = i;
		}

		// return index of target Script data
		return (index);
	}

	/**
	 * Getter methods : Get current Script List length
	 */
	public int getLengthScriptList() {
		// return length of current Script List
		return (ScriptList.size());
	}

	/**
	 * Setter methods : Append Script data to current Script List
	 */
	public int setScriptData(String data, int startTime, int endTime) {
		// Alloc new List
		ScriptList.add(data);
		ScriptStartTime.add(startTime);
		ScriptEndTime.add(endTime);

		// return length of current Script List
		return (ScriptList.size());
	}

	public int searchScriptData(int startTime) {
		// if index is '-1', then no data
		int index;

		// search start time from current ScriptList
		index = ScriptStartTime.indexOf(startTime);

		// return index of target Script data
		return (index);
	}

	public int parseIntStartTime(String MM, String SS, String MS) {

		int startTime;

		// casting start time String to Integer
		startTime = (Integer.parseInt(MM) * 60) + Integer.parseInt(SS);
		startTime = (startTime * 1000) + Integer.parseInt(MS);

		// return StarTime at parsed Integer
		return (startTime);
	}

	public int parseIntStartTime(String MMSSMS) {

		int startTime;

		// casting start time String to Integer
		String[] eachTime = MMSSMS.split(":", 0);
		startTime = (Integer.parseInt(eachTime[0]) * 60)
				+ Integer.parseInt(eachTime[1]);
		startTime = (startTime * 1000) + Integer.parseInt(eachTime[2]);

		// return StarTime at parsed Integer
		return (startTime);
	}

	/**
	 * @category Getter method : Get current status of saved script data
	 * @return saved status : FALSE:finished current script data, TRUE:Not yet.
	 */
	public int getStatusSaveScripts() {
		int result = 0;

		// PickUP current status
		if (getStatusImportCsv()) {
			// modify mode
			result = MB_STYLE_MODIFY;
		} else if (getStatusEditingScripts()) {
			// overwrite mode
			result = MB_STYLE_OVERWR;
		}

		// return result
		return (result);
	}

	/**
	 * @category Getter method : Get current status of saved script data
	 * @return saved status : FALSE:finished current script data, TRUE:Not yet.
	 */
	public boolean getStatusEditingScripts() {
		// return result
		return (status_edit_scripts);
	}

	/**
	 * @category Getter method : Get current status of saved script data
	 * @return saved status : FALSE:finished current script data, TRUE:Not yet.
	 */
	public boolean getStatusImportCsv() {
		// return result
		return (status_import_csv);
	}

	/**
	 * @category Setter method : Set new status of editing script data
	 * @param stat
	 *            : new status
	 */
	public void setStatusSaveScripts(int mode, boolean stat) {
		// Check status
		if (!stat) {
			// Drop all status flag
			setStatusEditingScripts(stat);
			setStatusImportCsv(stat);
		} else {
			// Update new status
			if ((mode == MB_STYLE_CONFIRM) || (mode == MB_STYLE_OVERWR)) {
				// Save edited data to XML file
				setStatusEditingScripts(stat);
			} else if (mode == MB_STYLE_MODIFY) {
				// Save CSV data to XML file
				setStatusImportCsv(stat);
			}
		}
	}

	/**
	 * @category Setter method : Set new status of editing script data
	 * @param stat
	 *            : new status
	 */
	public void setStatusEditingScripts(boolean stat) {
		// Update new status
		status_edit_scripts = stat;
	}

	/**
	 * @category Setter method : Set new status of importing CSV data
	 * @param stat
	 *            : new status
	 */
	public void setStatusImportCsv(boolean stat) {
		// Update new status
		status_import_csv = stat;
	}

	/**
	 * Stub methods
	 */
	public String getScriptData(int index) {

		String currentScriptData = new String();

		// PickUP Script Data by index
		if ((index >= 0) && (index <= (ScriptList.size() - 1))) {
			// PickUP script data by index
			currentScriptData = ScriptList.get(index).toString();
		}
		// index error
		else {
			currentScriptData = "";
		}

		// return script data by index
		return (currentScriptData);
	}

	public int getScriptStartTime(int index) {

		int currentScriptStartTime = -1;

		// PickUP Script Start time by index
		if ((index >= 0) && (index <= (ScriptStartTime.size() - 1))) {
			// PickUP script start time by index
			currentScriptStartTime = ScriptStartTime.get(index);
		}

		// return script start time by index
		return (currentScriptStartTime);
	}

	public int getScriptEndTime(int index) {

		int currentScriptEndTime = -1;

		// PickUP Script Start time by index
		if ((index >= 0) && (index <= (ScriptEndTime.size() - 1))) {
			// PickUP script start time by index
			currentScriptEndTime = ScriptEndTime.get(index);
		}

		// return script start time by index
		return (currentScriptEndTime);
	}

	public int getIndexScriptData(String startMM, String startSS, String startMS) {
		// if index is '-1', then no data
		int index;
		int startTime = 0;

		// casting start time String to Integer
		startTime = parseIntStartTime(startMM, startSS, startMS);

		// search start time from current ScriptList
		index = searchScriptData(startTime);

		// return index of target Script data
		return (index);
	}

	public int getIndexScriptData(int startTime) {
		// if index is '-1', then no data
		int index;

		// search start time from current ScriptList
		index = searchScriptData(startTime);

		// return index of target Script data
		return (index);
	}

	public int updateScriptEndTime(int startTime, int endTime) {
		// if index is '-1', then no data
		int index;

		// search start time from current ScriptList
		index = ScriptStartTime.indexOf(startTime);
		if (index >= 0) {
			// Update EndTime List
			ScriptEndTime.set(index, endTime);
		}

		// return index of target Script data
		return (index);
	}

	public boolean appendScriptData(String scriptData, int startTime,
			int endTime) {

		boolean result = true;
		int index = -1;

		// check exist data
		if (scriptData.isEmpty()) {
			// No script data
			result = false;
		} else {
			index = searchScriptData(startTime);
			if (index >= 0) {
				// exist data (update current script data)
				// *(caution)* : No need changed start time area, cause same
				// date.
				ScriptList.set(index, scriptData);
				ScriptEndTime.set(index, endTime);
			} else {
				// no data (insert new script data)
				index = indexScriptData(startTime);
				if (index >= 0) {
					// insert new script data
					ScriptList.add(index, scriptData);
					ScriptStartTime.add(index, startTime);
					ScriptEndTime.add(index, endTime);
				} else {
					// ** May be, invalid start time **********
					result = false;
				}
			}
		}

		// return result status
		return (result);
	}

	public boolean appendScriptData(String scriptData, int startTime,
			int endTime, String comment) {

		boolean result = true;

		// Append script data
		result = appendScriptData(scriptData, startTime, endTime);
		if (result) {
			// If success append data, then append comment string
			setScriptComment(startTime, comment);
		}

		// return result status
		return (result);
	}

	public boolean appendScriptData(String scriptData, String startMM,
			String startSS, String startMS, int endTime) {

		boolean result = true;
		int startTime = 0;
		int index = -1;

		// casting start time String to Integer
		startTime = parseIntStartTime(startMM, startSS, startMS);

		// check exist data
		if (scriptData.isEmpty()) {
			// No script data
			result = false;
		} else {
			index = searchScriptData(startTime);
			if (index >= 0) {
				// exist data (update current script data)
				// *(caution)* : No need changed start time area, cause same
				// date.
				ScriptList.set(index, scriptData);
				ScriptEndTime.set(index, endTime);
			} else {
				// no data (insert new script data)
				index = indexScriptData(startTime);
				if (index >= 0) {
					// insert new script data
					ScriptList.add(index, scriptData);
					ScriptStartTime.add(index, startTime);
					ScriptEndTime.add(index, endTime);
				} else {
					// ** May be, invalid start time **********
					result = false;
				}
			}
		}

		// return result status
		return (result);
	}

	public boolean deleteScriptData(String startMM, String startSS,
			String startMS) {

		boolean result = true;
		int startTime = 0;
		int index = -1;

		// casting start time String to Integer
		startTime = parseIntStartTime(startMM, startSS, startMS);

		// check exist data
		index = searchScriptData(startTime);
		if (index >= 0) {
			// exist data (remove current script data)
			ScriptList.remove(index);
			ScriptStartTime.remove(index);
			ScriptEndTime.remove(index);
		} else {
			// No exist data
			result = false;
		}

		// return result status
		return (result);
	}

	public boolean deleteScriptData(int index) {
		boolean result = true;

		try {
			// exist data (remove current script data)
			ScriptList.remove(index);
			ScriptStartTime.remove(index);
			ScriptEndTime.remove(index);
		} catch (Exception e) {
			System.out.println("deleteScriptData() : Exception = " + e);
			result = false;
		}

		// return result
		return (result);
	}

	/**
	 * @category Getter method : get Selection status of CheckBox
	 * @param index
	 * @return Selection of CheckBox
	 */
	public Boolean getExtendExtended(int index) {
		// Get Structure of target Extended data
		StructExtendData currentExtendData = (StructExtendData) ExtendList
				.get(index);
		// return current Extended selection
		return (currentExtendData.getExtended());
	}

	/**
	 * @category Getter method : get Selection status of RadioButton
	 * @param index
	 * @return Selection of RadioButton
	 */
	public Boolean getExtendGender(int index) {
		// Get Structure of target Extended data
		StructExtendData currentExtendData = (StructExtendData) ExtendList
				.get(index);
		// return current Gender selection
		return (currentExtendData.getGender());
	}

	/**
	 * @category Getter method : get Scale data of Speed
	 * @param index
	 * @return Scale data
	 */
	public int getExtendSpeed(int index) {
		// Get Structure of target Extended data
		StructExtendData currentExtendData = (StructExtendData) ExtendList
				.get(index);
		// return current scale data of Speed
		return (currentExtendData.getSpeed());
	}

	/**
	 * @category Getter method : get Scale data of Pitch
	 * @param index
	 * @return Scale data
	 */
	public int getExtendPitch(int index) {
		// Get Structure of target Extended data
		StructExtendData currentExtendData = (StructExtendData) ExtendList
				.get(index);
		// return current scale data of Pitch
		return (currentExtendData.getPitch());
	}

	/**
	 * @category Getter method : get Scale data of Volume
	 * @param index
	 * @return Scale data
	 */
	public int getExtendVolume(int index) {
		// Get Structure of target Extended data
		StructExtendData currentExtendData = (StructExtendData) ExtendList
				.get(index);
		// return current scale data of Volume
		return (currentExtendData.getVolume());
	}

	/**
	 * @category Getter method : get Language index of Description
	 * @param index
	 * @return Index of Language
	 */
	public int getExtendLang(int index) {
		// Get Structure of target Extended data
		StructExtendData currentExtendData = (StructExtendData) ExtendList
				.get(index);
		// return current index of Language of Description
		return (currentExtendData.getLang());
	}

	public void appendExtendData(int index, int starttime,
			Boolean extendExtended, Boolean extendSex, int extendSpeed,
			int extendPitch, int extendVolume, int extendLang) {

		// Create target Struct(Extended data)
		StructExtendData newExtendData = new StructExtendData(starttime,
				extendExtended, extendSex, extendSpeed, extendPitch,
				extendVolume, extendLang);

		// Check current List length
		if (ExtendList.isEmpty()) {
			// No exist data
			ExtendList.add(0, newExtendData);
		} else {
			// Check current List length
			if (index >= ExtendList.size()) {
				// Append data cause Out of bounds
				// New Data(Insert)
				ExtendList.add(index, newExtendData);
			} else {
				try {
					// Get Structure of target Extended data
					StructExtendData currentExtendData = (StructExtendData) ExtendList
							.get(index);
					// Compare index(StartTime)
					if (starttime != currentExtendData.getStartTime()) {
						// New Data(Insert)
						ExtendList.add(index, newExtendData);
					} else {
						// Exist Data(Update)
						ExtendList.set(index, newExtendData);
					}
				} catch (Exception e) {
					// May be, catch IndexOutOfBoundsException
					System.out.println("appendExtendData() : Exception = " + e);
				}
			}
		}
	}

	public void deleteExtendData(int starttime) {

		// Check exist data
		if (!ExtendList.isEmpty()) {
			// Search target data
			for (int index = 0; index < ExtendList.size(); index++) {
				// Get Structure of target Extended data
				StructExtendData currentExtendData = (StructExtendData) ExtendList
						.get(index);
				// Compare index(StartTime)
				if (starttime == currentExtendData.getStartTime()) {
					// Remove target data from ArrayList
					ExtendList.remove(index);
					break;
				}
			}
		}
	}

	public String makeFormatHHMMSS(int totalSec) {

		String formTime = new String();
		Integer hh;
		Integer mm;
		Integer ss;

		// Make format "HH:MM:SS"
		if (totalSec <= 0) {
			// default
			formTime = "00 : 00 : 00";
		} else {
			// Integer to String
			hh = totalSec / 3600;
			mm = (totalSec / 60) - (hh * 60);
			ss = totalSec % 60;
			formTime = "";

			// HH
			if (hh < 10)
				formTime = formTime + "0";
			formTime = formTime + hh.toString();
			// separator
			formTime += " : ";
			// MM
			if (mm < 10)
				formTime = formTime + "0";
			formTime = formTime + mm.toString();
			// separator
			formTime += " : ";
			// SS
			if (ss < 10)
				formTime = formTime + "0";
			formTime = formTime + ss.toString();
		}

		// return String data
		return (formTime);
	}

	public String makeFormatMMSSMS(int totalSec) {

		String formTime = new String();
		Integer mm;
		Integer ss;
		Integer ms;

		// Make format "MM:SS.MSec"
		if (totalSec <= 0) {
			// default
			formTime = "00 : 00 . 000";
		} else {
			// Integer to String
			mm = (totalSec / 1000) / 60;
			ss = (totalSec / 1000) % 60;
			ms = totalSec % 1000;
			formTime = "";

			// MM
			if (mm < 10)
				formTime = formTime + "0";
			formTime = formTime + mm.toString();
			// separator
			formTime += " : ";
			// SS
			if (ss < 10)
				formTime = formTime + "0";
			formTime = formTime + ss.toString();
			// separator
			formTime += " . ";
			// Milli Sec
			if (ms < 10)
				formTime = formTime + "00";
			else if ((ms < 100) && (ms >= 10))
				formTime = formTime + "0";
			formTime = formTime + ms.toString();
		}

		// return String data
		return (formTime);
	}

	public String makeFormatHH(int totalSec) {

		String formTime = new String();
		Integer hh;

		// Make format "HH:MM:SS"
		if (totalSec <= 0) {
			// default
			formTime = "00";
		} else {
			// Integer to String
			hh = totalSec / 3600;
			formTime = "";

			// HH
			if (hh < 10)
				formTime = formTime + "0";
			formTime = formTime + hh.toString();
		}

		// return String data
		return (formTime);
	}

	public String makeFormatMM(int totalSec) {

		String formTime = new String();
		Integer tm, mm;

		// Make format "MM"
		if (totalSec <= 0) {
			// default
			formTime = "00";
		} else {
			tm = totalSec / 1000;
			// Integer to String
			// // mm = (totalSec / 60) - ((totalSec / 3600) * 60);
			mm = tm / 60;
			formTime = "";

			// MM
			if (mm < 10)
				formTime = formTime + "0";
			formTime = formTime + mm.toString();
		}

		// return String data
		return (formTime);
	}

	public String makeFormatSS(int totalSec) {

		String formTime = new String();
		Integer ss;

		// Make format "SS"
		if (totalSec <= 0) {
			// default
			formTime = "00";
		} else {
			// Integer to String
			ss = (totalSec / 1000) % 60;
			formTime = "";

			// SS
			if (ss < 10)
				formTime = formTime + "0";
			formTime = formTime + ss.toString();
		}

		// return String data
		return (formTime);
	}

	public String makeFormatMS(int totalSec) {

		String formTime = new String();
		Integer ms;

		// Make format "Milli Sec"
		if (totalSec <= 0) {
			// default
			formTime = "000";
		} else {
			// Integer to String
			ms = totalSec % 1000;
			formTime = "";

			// Milli Sec
			if (ms < 10)
				formTime = "00";
			else if ((ms < 100) && (ms >= 10))
				formTime = "0";

			formTime = formTime + ms.toString();
		}

		// return String data
		return (formTime);
	}

	public String toXMLfragment() {
		StringBuffer tmpSB = new StringBuffer();
		for (int i = 0; i < getLengthScriptList(); i++) {
			int startTime = getScriptStartTime(i);
			String strStartTime = makeFormatMM(startTime) + ":"
					+ makeFormatSS(startTime) + ":" + makeFormatMS(startTime);
			int duration = getScriptEndTime(i) - startTime;
			String strDuration = makeFormatMM(duration) + ":"
					+ makeFormatSS(duration) + ":" + makeFormatMS(duration);
			String strDesc = getScriptData(i);
			int speed = getExtendSpeed(i);
			String strSpeed = String.valueOf(speed);
			boolean gender = getExtendGender(i);
			String strGender = new String((gender ? "male" : "female"));
			String strExtended = new String(
					(getExtendExtended(i) ? " extended=\"true\"" : ""));
			String strLang = new String(((getExtendLang(i) == 1) ? "ja" : "en"));

			// for WAV information
			String strServerUri = "";
			String strLocalUri = "";
			String strWavSpeed = "";
			int wavNo = getIndexWavList(startTime);
			boolean wavEnable = false;
			if (wavNo >= 0) {
				// PickUP WAV enable status
				wavEnable = getEnableWavList(wavNo);
			}

			String LINE_SEP = FileUtils.LINE_SEP;

			tmpSB.append("	<item importance=\"high\">" + LINE_SEP);
			tmpSB.append("\t  <start type=\"relTime\">" + strStartTime
					+ "</start>" + LINE_SEP);
			tmpSB.append("\t  <duration>" + strDuration + "</duration>"
					+ LINE_SEP);
			tmpSB.append("\t  <description xml:lang=\"" + strLang + "\" "
					+ "speed=\"" + strSpeed + "\" " + "gender=\"" + strGender
					+ "\"" + strExtended + ">" + strDesc + "</description>"
					+ LINE_SEP);

			// Check exist WAV information
			if (wavNo >= 0) {
				// PickUP current WAV information
				try {
					// URL encode to UTF-8
					strServerUri = getFileNameWavList(wavNo).toURL().toString();
					strLocalUri = getFileNameWavList(wavNo).toURL().toString();
					strWavSpeed = String
							.valueOf((int) (getPlaySpeedWavList(wavNo) * 100.0f));

					// Calculate duration time of WAV data
					duration = getEndTimeWavList(wavNo)
							- getStartTimeWavList(wavNo);
					strDuration = makeFormatMM(duration) + ":"
							+ makeFormatSS(duration) + ":"
							+ makeFormatMS(duration);

					// Append WAV information
					tmpSB.append("\t  <wave uri=\"" + strServerUri
							+ "\" local=\"" + strLocalUri + "\" duration=\""
							+ strDuration + "\" speed=\"" + strWavSpeed + "\"");

					// Check enable status
					if (!wavEnable) {
						// Append enable status(true)
						// default true
						tmpSB.append(" enabled=\"false\"");
					}

					// End of Line
					tmpSB.append("/>" + LINE_SEP);
				} catch (MalformedURLException e) {
					System.out.println("toXMLfragment() : " + e);
				} catch (Exception ee) {

				}
			}

			tmpSB.append("\t</item>" + LINE_SEP);
		}

		return tmpSB.toString();
	}

	/**
	 * @category Save script data to CSV format(file)
	 * @return string of script data
	 */
	public String toCSVfragment() {
		String LINE_SEP = FileUtils.LINE_SEP;
		String COLUMN_SEP = ",";
		String DQUOTE_CODE = "\"";
		StringBuffer tmpSB = new StringBuffer();

		for (int i = 0; i < getLengthScriptList(); i++) {
			// Get next start time
			int startTime = getScriptStartTime(i);
			String strStartTime = makeFormatMM(startTime) + ":"
					+ makeFormatSS(startTime) + ":" + makeFormatMS(startTime);

			// for WAV information
			String wavLocalPath = "";
			String wavEnable = "";
			String wavSpeed = "";
			int wavNo = getIndexWavList(startTime);
			if (wavNo >= 0) {
				// PickUP local WAV file path
				wavLocalPath = getFileNameWavList(wavNo).getPath().replace("/",
						"\\");
				// PickUP WAV play status
				wavEnable = (getEnableWavList(wavNo) ? "1" : "0");
				// PickUP WAV play speed
				wavSpeed = String
						.valueOf((int) (getPlaySpeedWavList(wavNo) * 100.0f));
			}

			// Get current index of script list
			int index = getIndexScriptData(startTime);
			// Get extended status
			String strExtended = (getExtendExtended(index) ? "1" : "0");
			// Get gender
			String strGender = (getExtendGender(index) ? "male" : "female");
			// Get language of description
			String strLang = ((getExtendLang(index) == DESC_LANG_JA) ? "ja"
					: "en");
			// Get play voice speed
			String strSpeed = String.valueOf(getExtendSpeed(index));
			// Get play voice pitch
			String strPitch = String.valueOf(getExtendPitch(index));
			// Get play voice volume
			String strVolume = String.valueOf(getExtendVolume(index));

			// Get string of description
			String strDesc = getScriptData(i);
			// Check exist CR/LF code
			if ((strDesc.indexOf("\n") >= 0) || (strDesc.indexOf("\r") >= 0)) {
				// append double quote code
				strDesc = DQUOTE_CODE + strDesc + DQUOTE_CODE;
			}

			// MakeUP line string
			tmpSB.append(strStartTime + COLUMN_SEP);
			tmpSB.append(wavLocalPath + COLUMN_SEP);
			tmpSB.append(strExtended + COLUMN_SEP);
			tmpSB.append(strGender + COLUMN_SEP);
			tmpSB.append(strLang + COLUMN_SEP);
			tmpSB.append(strSpeed + COLUMN_SEP);
			tmpSB.append(strPitch + COLUMN_SEP);
			tmpSB.append(strVolume + COLUMN_SEP);
			tmpSB.append(wavEnable + COLUMN_SEP);
			tmpSB.append(wavSpeed + COLUMN_SEP);
			tmpSB.append(strDesc + COLUMN_SEP);
			tmpSB.append(LINE_SEP);
		}
		// return result
		return (tmpSB.toString());
	}

	// **********************************************************
	// Comment of description control part
	//
	// **********************************************************
	public int getIndexScriptComment(int startTime) {
		int index = -1;

		// Check comment list
		if (!ScriptComment.isEmpty()) {
			// search target index by start time
			for (int i = 0; i < ScriptComment.size(); i++) {
				// Get Structure of target Comment list
				StructScriptComment currentScriptComment = (StructScriptComment) ScriptComment
						.get(i);
				// compare start time
				if (startTime == currentScriptComment.getStartTime()) {
					// detect target data
					index = i;
				}
			}
		}

		// return result
		return (index);
	}

	public String getScriptComment(int index) {
		String comment = "";

		// Check comment list
		if (!ScriptComment.isEmpty()) {
			// check limit index
			if ((index >= 0) && (index < ScriptComment.size())) {
				// Get Structure of target Comment list
				StructScriptComment currentScriptComment = (StructScriptComment) ScriptComment
						.get(index);
				comment = currentScriptComment.getScriptComment();
			}
		}
		// return current Comment string
		return (comment);
	}

	public void setScriptComment(int startTime, String comment) {
		// check exist target start time
		int index = getIndexScriptComment(startTime);
		if (index >= 0) {
			// Update exist comment data
			StructScriptComment targetComment = ScriptComment.get(index);
			targetComment.setScriptComment(comment);
		} else {
			// Store new comment data
			StructScriptComment newComment = new StructScriptComment(startTime,
					comment);
			ScriptComment.add(newComment);
		}
	}

	// **********************************************************
	// WAV file control part
	//
	// **********************************************************
	/**
	 * Setter method : initialize ArrayList of control WAV file
	 */
	public void cleanupWavList() {
		// CleanUP ArrayList of control WAV file
		listStartTimeWav.clear();
		listEndTimeWav.clear();
		listFileNameWav.clear();
		listEnableWav.clear();
		listPlaySpeedWav.clear();
	}

	/**
	 * Getter method : Get current list size
	 * 
	 * @return
	 */
	public int getLengthWavList() {
		int len = -1;

		// check list size
		if (!listStartTimeWav.isEmpty()) {
			// Get current list size
			len = listStartTimeWav.size();
		}
		// Return current list size
		return (len);
	}

	/**
	 * Getter method : Search index for target start time
	 */
	public int getIndexWavList(int startTime) {
		int index = -1;

		// check list size
		int len = getLengthWavList();
		if (len > 0) {
			// search target start time from current list
			index = listStartTimeWav.indexOf(startTime);
		}

		// return result
		return (index);
	}

	/**
	 * Getter method : Search insert index for target start time
	 */
	public int searchInsertIndexWavList(int startTime) {
		int index = -1;

		// ScriptList empty is always 1st index
		if (listStartTimeWav.isEmpty()) {
			index = 0;
		} else {
			// search start time from current ScriptList
			int i;
			for (i = 0; i < listStartTimeWav.size(); i++) {
				// exist data?
				if (startTime < listStartTimeWav.get(i)) {
					// exist data.
					break;
				}
			}
			// update index
			index = i;
		}

		// return result
		return (index);
	}

	/**
	 * Getter method : Get target start time data from list
	 */
	public int getStartTimeWavList(int index) {
		int result = -1;

		// check out of list size
		int len = getLengthWavList();
		if ((len > 0) && (index >= 0) && (index < len)) {
			// get target data
			result = listStartTimeWav.get(index);
		}
		// return result
		return (result);
	}

	/**
	 * Getter method : Get target end time data from list
	 */
	public int getEndTimeWavList(int index) {
		int result = -1;

		// check out of list size
		int len = getLengthWavList();
		if ((len > 0) && (index >= 0) && (index < len)) {
			// get target data
			result = listEndTimeWav.get(index);
		}
		// return result
		return (result);
	}

	/**
	 * Getter method : Get target file name string from list
	 */
	public URI getFileNameWavList(int index) {
		URI result = null;

		// check out of list size
		int len = getLengthWavList();
		if ((len > 0) && (index >= 0) && (index < len)) {
			// get target data
			result = listFileNameWav.get(index);
		}
		// return result
		return (result);
	}

	/**
	 * Getter method : Get target file enable status from list
	 */
	public boolean getEnableWavList(int index) {
		boolean result = false;

		// check out of list size
		int len = getLengthWavList();
		if ((len > 0) && (index >= 0) && (index < len)) {
			// get target data
			result = listEnableWav.get(index);
		}
		// return result
		return (result);
	}

	/**
	 * Getter method : Get competitive ratio as play sound
	 */
	public float getPlaySpeedWavList(int index) {
		float result = -1.0f;

		// check out of list size
		int len = getLengthWavList();
		if ((len > 0) && (index >= 0) && (index < len)) {
			// get target data
			result = listPlaySpeedWav.get(index);
		}
		// return result
		return (result);
	}

	/**
	 * Setter method : Delete target data from current list by index
	 */
	public void deleteIndexWavList(int index) {
		// check index
		if (0 < getStartTimeWavList(index)) {
			// remove target data from list
			listStartTimeWav.remove(index);
			listEndTimeWav.remove(index);
			listFileNameWav.remove(index);
			listEnableWav.remove(index);
			listPlaySpeedWav.remove(index);
		}
	}

	/**
	 * Setter method : Delete target data from current list by start time
	 */
	public void deleteStartTimeWavList(int startTime) {
		// check index
		int index = getIndexWavList(startTime);
		if (index >= 0) {
			// remove target data from list
			listStartTimeWav.remove(index);
			listEndTimeWav.remove(index);
			listFileNameWav.remove(index);
			listEnableWav.remove(index);
			listPlaySpeedWav.remove(index);
		}
	}

	/**
	 * Setter method : append(update) target data to current list
	 */
	public int appendDataWavList(int startTime, int endTime,
			URI strWavFileName, boolean statEnaWav, float competitiveRatioWav) {
		int index = -1;

		// search target data from current list
		index = getIndexWavList(startTime);
		// Exist target data
		if (index >= 0) {
			// update target data(only file name string)
			listEndTimeWav.set(index, endTime);
			listFileNameWav.set(index, strWavFileName);
			listEnableWav.set(index, statEnaWav);
			listPlaySpeedWav.set(index, competitiveRatioWav);
		}
		// New data
		else {
			// search insert index
			index = searchInsertIndexWavList(startTime);
			if (index >= 0) {
				// append new data to current list
				listStartTimeWav.add(index, startTime);
				listEndTimeWav.add(index, endTime);
				listFileNameWav.add(index, strWavFileName);
				listEnableWav.add(index, statEnaWav);
				listPlaySpeedWav.add(index, competitiveRatioWav);
			}
		}

		// return index of target data
		return (index);
	}

}
