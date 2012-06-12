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
package org.eclipse.actf.ai.scripteditor.data;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.eclipse.actf.ai.scripteditor.util.TimeFormatUtil;
import org.eclipse.actf.ai.scripteditor.util.XMLFileMessageBox;
import org.eclipse.actf.util.FileUtils;

/**
 * @category Script data manager class
 * 
 */
public class ScriptDataManager {

	public class scriptDataComparator implements Comparator<IScriptData> {

		public int compare(IScriptData o1, IScriptData o2) {
			if (o1.equals(o2)) {
				return 0;
			}

			int v1 = o1.getStartTime();
			int v2 = o2.getStartTime();

			if (v1 < v2) {
				return -1;
			} else if (v1 > v2) {
				return 1;
			}

			v1 = o1.isWavEnabled() ? o1.getWavEndTime() : o1.getEndTime();
			v2 = o2.isWavEnabled() ? o2.getWavEndTime() : o2.getEndTime();
			if (v1 < v2) {
				return -1;
			} else if (v1 > v2) {
				return 1;
			}

			String st1 = o1.getDescription();
			String st2 = o2.getDescription();
			int result = st1.compareTo(st2);
			if (result != 0) {
				return result;
			}
			// TODO
			return 0;
		}
	}

	private TreeSet<IScriptData> iData;
	private TreeSet<String> cData;

	/**
	 * static data
	 */
	static private ScriptDataManager ownInst = null;

	// Save status
	private boolean status_edit_scripts = false;
	private boolean status_import_csv = false;

	/**
	 * Constructor
	 */
	private ScriptDataManager() {
		iData = new TreeSet<IScriptData>(new scriptDataComparator());
		cData = new TreeSet<String>();
		ownInst = this;
	}

	static public ScriptDataManager getInstance() {
		if (ownInst == null) {
			ownInst = new ScriptDataManager();
		}
		return (ownInst);
	}

	public void clearData() {
		iData.clear();
		cData.clear();
	}

	@Deprecated
	public TreeSet<IScriptData> getDataSet() {
		// TODO replace with getDataList()
		return iData;
	}

	@Deprecated
	public void setScriptData(TreeSet<IScriptData> iData) {
		// TODO remove
		this.iData = iData;
	}

	public void trimCharacterDataSet() {
		cData.clear();
		for (IScriptData tmpData : iData) {
			String tmpCharacter = tmpData.getCharacter();
			if (tmpCharacter != null && tmpCharacter.length() > 0) {
				cData.add(tmpCharacter);
			}
		}
	}

	public List<IScriptData> getDataList() {
		// TODO cache
		ArrayList<IScriptData> rtnData = new ArrayList<IScriptData>(iData);
		return rtnData;
	}

	public List<IScriptData> getDataList(int type) {
		// TODO cache
		ArrayList<IScriptData> rtnData = new ArrayList<IScriptData>();
		for (IScriptData data : iData) {
			if (data.getType() == type) {
				rtnData.add(data);
			}
		}
		return rtnData;
	}

	public IScriptData[] getDataArray() {
		return iData.toArray(new IScriptData[iData.size()]);
	}

	public boolean add(IScriptData isData) {
		if (isData == null) {
			return false;
		}
		String tmpCharacter = isData.getCharacter();
		if (tmpCharacter != null && tmpCharacter.length() > 0) {
			cData.add(tmpCharacter);
		}
		return iData.add(isData);
	}

	public boolean contains(IScriptData data) {
		return iData.contains(data);
	}

	public boolean remove(IScriptData data) {
		return iData.remove(data);
	}

	@Deprecated
	public ScriptData getData(int datalocation) {
		IScriptData searchData = null;
		int pos = 0;
		if (iData.size() > datalocation) {
			for (IScriptData data : iData) {
				if (data instanceof ScriptData) {
					if (pos++ >= datalocation) {
						searchData = data;
						break;
					}
				}
			}
		}
		return (ScriptData) searchData; // return search data or null
	}

	public int size() {
		return iData.size();
	}

	public void setSaveRequired(int mode, boolean stat) {
		// TODO check
		if (!stat) {
			// do not need to save
			status_edit_scripts = stat;
			status_import_csv = stat;
		} else {
			if ((mode == XMLFileMessageBox.MB_STYLE_CONFIRM)
					|| (mode == XMLFileMessageBox.MB_STYLE_OVERWR)) {
				// XML file
				status_edit_scripts = stat;
			} else if (mode == XMLFileMessageBox.MB_STYLE_MODIFY) {
				// CSV data to XML file
				status_import_csv = stat;
			}
		}
	}

	/**
	 * Get current status of saved script data
	 * 
	 * @return true if data needs to save
	 */
	public int isSaveRequired() {
		int result = 0;

		// PickUP current status
		if (status_import_csv) {
			// modify mode
			result = XMLFileMessageBox.MB_STYLE_MODIFY;
		} else if ((status_edit_scripts)) {
			// overwrite mode
			result = XMLFileMessageBox.MB_STYLE_OVERWR;
		}

		// return result
		return (result);
	}

	public String toXMLfragment() {
		StringBuffer tmpSB = new StringBuffer();

		List<IScriptData> list = getDataList();
		for (IScriptData data : list) {
			if (data.isDataCommit() == false) {
				continue;
			}
			if (data.getType() == IScriptData.TYPE_AUDIO
					&& (data.getDescription() == null || data.getDescription()
							.trim().length() == 0)) {
				continue;
			}
			if (data.getType() == IScriptData.TYPE_SCENARIO
					&& (data.getScenario() == null || data.getScenario().trim()
							.length() == 0)) {
				continue;
			}
			if (data.getType() == IScriptData.TYPE_CAPTION
					&& (data.getCaption() == null || data.getCaption().trim()
							.length() == 0)) {
				continue;
			}
			int startTime = data.getStartTime();
			String strStartTime = TimeFormatUtil.makeFormatHH(startTime) + ":"
					+ TimeFormatUtil.makeFormatMM(startTime) + ":"
					+ TimeFormatUtil.makeFormatSS(startTime) + ":"
					+ TimeFormatUtil.makeFormatMS(startTime);
			int duration = data.getEndTime() - startTime;
			String strDuration = TimeFormatUtil.makeFormatHH(duration) + ":"
					+ TimeFormatUtil.makeFormatMM(duration) + ":"
					+ TimeFormatUtil.makeFormatSS(duration) + ":"
					+ TimeFormatUtil.makeFormatMS(duration);
			String strDesc = "";
			String strCaption = "";
			String strScenario = "";
			String strComment = null;
			if (data.getDescription() != null
					&& data.getDescription().length() > 0) {
				strDesc = data.getDescription();
			} else if (data.getCaption() != null
					&& data.getCaption().length() > 0) {
				strCaption = data.getCaption();
			}
			if (data.getScenario() != null && data.getScenario().length() > 0) {
				strScenario = data.getScenario();
			}
			if (data.getScriptComment() != null
					&& data.getScriptComment().length() > 0) {
				strComment = data.getScriptComment();
			}
			int speed = data.getVgPlaySpeed();

			String strSpeed = String.valueOf(speed);
			// boolean gender = getVgGender(i);
			boolean gender = data.getVgGender();
			String strGender = new String((gender ? "male" : "female"));
			String strExtended = new String(
					(data.isExtended() ? " extended=\"true\"" : ""));
			String strLang = data.getLang();

			// for WAV information
			String strServerUri = "";
			String strLocalUri = "";
			String strWavSpeed = "";

			String LINE_SEP = FileUtils.LINE_SEP;

			tmpSB.append("	<item importance=\"high\">" + LINE_SEP);
			tmpSB.append("\t  <start type=\"relTime\">" + strStartTime
					+ "</start>" + LINE_SEP);
			tmpSB.append("\t  <duration>" + strDuration + "</duration>"
					+ LINE_SEP);
			// if(strDesc != null) {
			if (data.getType() == IScriptData.TYPE_AUDIO) {
				tmpSB.append("\t  <description xml:lang=\"" + strLang + "\" "
						+ "speed=\"" + strSpeed + "\" " + "gender=\""
						+ strGender + "\"" + strExtended + ">" + strDesc
						+ "</description>" + LINE_SEP);
				// } else if (strCaption != null){
			} else if (data.getType() == IScriptData.TYPE_CAPTION) {
				tmpSB.append("\t  <caption>" + strCaption + "</caption>"
						+ LINE_SEP);
			}
			if (data.getType() == IScriptData.TYPE_SCENARIO) {
				tmpSB.append("\t  <scenario>" + strScenario + "</scenario>"
						+ LINE_SEP);
			}
			if (strComment != null) {
				tmpSB.append("\t  <comment>" + strComment + "</comment>"
						+ LINE_SEP);
			}
			if (data.getWavURI() != null) {
				try {
					strServerUri = data.getWavURI().toURL().toString();
					strLocalUri = data.getWavURI().toURL().toString();
					strWavSpeed = String
							.valueOf((int) (data.getWavPlaySpeed() * 100.0f));

					duration = data.getWavEndTime() - data.getStartTime();
					strDuration = TimeFormatUtil.makeFormatMM(duration) + ":"
							+ TimeFormatUtil.makeFormatSS(duration) + ":"
							+ TimeFormatUtil.makeFormatMS(duration);

					tmpSB.append("\t  <wave uri=\"" + strServerUri
							+ "\" local=\"" + strLocalUri + "\" duration=\""
							+ strDuration + "\" speed=\"" + strWavSpeed + "\"");

					if (!data.isWavEnabled()) {
						tmpSB.append(" enabled=\"false\"");
					}
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
	public String toCSVfragment(int outputType) {
		String LINE_SEP = FileUtils.LINE_SEP;
		String COLUMN_SEP = ",";
		String DQUOTE_CODE = "\"";
		StringBuffer tmpSB = new StringBuffer();
		List<IScriptData> list = getDataList();
		for (IScriptData data : list) {
			if (data.isDataCommit() == false) {
				continue;
			}
			if (data.getType() == IScriptData.TYPE_AUDIO
					&& (data.getDescription() == null || data.getDescription()
							.trim().length() == 0)) {
				continue;
			}
			if (data.getType() == IScriptData.TYPE_SCENARIO
					&& (data.getScenario() == null || data.getScenario().trim()
							.length() == 0)) {
				continue;
			}
			if (data.getType() == IScriptData.TYPE_CAPTION
					&& (data.getCaption() == null || data.getCaption().trim()
							.length() == 0)) {
				continue;
			}
			// Get next start time
			int startTime = data.getStartTime();
			String child = "";
			String dataType = "D";
			// switch (DataUtil.checkDataType(data)){
			switch (data.getType()) {
			case IScriptData.TYPE_AUDIO:
				dataType = "D";
				break;
			case IScriptData.TYPE_CAPTION:
				dataType = "C";
				break;
			case IScriptData.TYPE_SCENARIO:
				dataType = "S";
				break;
			}
			String strStartTime = TimeFormatUtil.makeFormatHH(startTime) + ":"
					+ TimeFormatUtil.makeFormatMM(startTime) + ":"
					+ TimeFormatUtil.makeFormatSS(startTime) + ":"
					+ TimeFormatUtil.makeFormatMS(startTime);
			int endTime = data.getEndTime();
			String strEndTime = "";
			if (endTime > 0) {
				strEndTime = TimeFormatUtil.makeFormatHH(endTime) + ":"
						+ TimeFormatUtil.makeFormatMM(endTime) + ":"
						+ TimeFormatUtil.makeFormatSS(endTime) + ":"
						+ TimeFormatUtil.makeFormatMS(endTime);
			}
			// for WAV information
			String wavLocalPath = "";
			String wavEnable = "";
			String wavSpeed = "";
			if (data.getWavURI() != null) {
				wavLocalPath = data.getWavURI().getPath();
				wavEnable = (data.isWavEnabled() == true) ? "1" : "0";
				wavSpeed = String.valueOf((int) (data.getWavPlaySpeed() * 100));
			}

			String strExtended = ((data.isExtended() == true) ? "1" : "0");
			String strGender = ((data.getVgGender() == true) ? "male"
					: "female");
			String strLang = data.getLang();
			String strSpeed = String.valueOf((int) (data.getVgPlaySpeed() * 1));
			String strPitch = String.valueOf(data.getVgPitch());
			String strVolume = String.valueOf(data.getVgVolume());

			// Get string of description
			String strDesc = data.getDescription();
			strDesc = strDesc.replaceAll("\"", "\"\"");
			String strScenario = data.getScenario();
			String strSpeaker = data.getCharacter();
			strSpeaker = strSpeaker.replaceAll("\"", "\"\"");
			String strCaption = data.getCaption();
			strCaption = strCaption.replaceAll("\"", "\"\"");
			String strComment = data.getScriptComment();
			strComment = strComment.replaceAll("\"", "\"\"");

			tmpSB.append(DQUOTE_CODE + strStartTime + DQUOTE_CODE + COLUMN_SEP); // startTime
			tmpSB.append(DQUOTE_CODE + strEndTime + DQUOTE_CODE + COLUMN_SEP); // endTime
			tmpSB.append(DQUOTE_CODE + strDesc + DQUOTE_CODE + COLUMN_SEP); // description
			tmpSB.append(DQUOTE_CODE + strScenario + DQUOTE_CODE + COLUMN_SEP); // scenario
			tmpSB.append(DQUOTE_CODE + strSpeaker + DQUOTE_CODE + COLUMN_SEP); // speaker
			tmpSB.append(DQUOTE_CODE + strCaption + DQUOTE_CODE + COLUMN_SEP); // caption
			tmpSB.append(DQUOTE_CODE + strComment + DQUOTE_CODE + COLUMN_SEP); // comment
			tmpSB.append(DQUOTE_CODE + dataType + DQUOTE_CODE + COLUMN_SEP); // dataType
			tmpSB.append(DQUOTE_CODE + child + DQUOTE_CODE + COLUMN_SEP); // child
			tmpSB.append(DQUOTE_CODE + wavLocalPath + DQUOTE_CODE + COLUMN_SEP); //
			tmpSB.append(DQUOTE_CODE + strExtended + DQUOTE_CODE + COLUMN_SEP);
			tmpSB.append(DQUOTE_CODE + strGender + DQUOTE_CODE + COLUMN_SEP);
			tmpSB.append(DQUOTE_CODE + strLang + DQUOTE_CODE + COLUMN_SEP);
			tmpSB.append(DQUOTE_CODE + strSpeed + DQUOTE_CODE + COLUMN_SEP);
			tmpSB.append(DQUOTE_CODE + strPitch + DQUOTE_CODE + COLUMN_SEP);
			tmpSB.append(DQUOTE_CODE + strVolume + DQUOTE_CODE + COLUMN_SEP);
			tmpSB.append(DQUOTE_CODE + wavEnable + DQUOTE_CODE + COLUMN_SEP);
			tmpSB.append(DQUOTE_CODE + wavSpeed + DQUOTE_CODE);

			tmpSB.append(LINE_SEP);
		}
		return (tmpSB.toString());
	}

	@Deprecated
	public int getEndTime(int index) {
		// TODO remove
		return getData(index).getEndTime();
	}

	@Deprecated
	public Integer getWavEndTime(int index) {
		// TODO remove
		return getData(index).getWavEndTime();
	}

	public List<String> getCharacterList() {
		// TODO cache
		ArrayList<String> characterList = new ArrayList<String>(cData);
		return characterList;
	}
}
