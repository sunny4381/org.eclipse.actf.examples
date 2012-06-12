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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.actf.ai.scripteditor.util.TimeFormatUtil;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.jface.viewers.TreeViewer;

public class DataUtil {

	private static final String JA_KUTEN_CODE1 = "\uff61";
	private static final String JA_KUTEN_CODE2 = "\u3002";
	private static final String JA_TOUTEN_CODE1 = "\uff64";
	private static final String JA_TOUTEN_CODE2 = "\u3001";
	private static final int JA_KUTEN_COUNT = 5;
	private static final int JA_TOUTEN_COUNT = 2;

	// MORA
	private static final float JA_KANJI_MORA = 1.545f;
	private static final float JA_KANA_MORA = 0.988f;
	private static final float EN_CHAR_MORA = 0.347f;

	public static boolean[] isOverlap(List<IScriptData> list1,
			List<IScriptData> list2, IScriptData paramData) {

		int len = (list1 != null) ? list1.size() : 0;
		len += (list2 != null) ? list2.size() : 0;
		len += (paramData != null) ? 1 : 0;
		boolean[] result = new boolean[len]; // false

		List<IScriptData> list = new ArrayList<IScriptData>();
		if (list1 != null) {
			for (IScriptData d : list1) {
				list.add(d);
			}
		}
		if (paramData != null) {
			list.add(paramData);
		}
		if (list2 != null) {
			for (IScriptData d : list2) {
				list.add(d);
			}
		}

		int endTime = 0;
		for (int i = 0; i < (list.size() - 1); i++) {
			IScriptData data = list.get(i);

			int currentEndTime;
			if (data.isWavEnabled()) {
				currentEndTime = data.getWavEndTime();
			} else {
				currentEndTime = data.getEndTime();
			}

			int nextStartTime = list.get(i + 1).getStartTime();
			if (endTime > data.getStartTime()) {
				if (endTime < currentEndTime) {
					endTime = currentEndTime;
				}
			} else {
				endTime = currentEndTime;
			}
			if (!data.isExtended() && endTime > nextStartTime) {
				result[i] = true;
				result[i + 1] = true;
			}
		}
		return result;
	}

	/**
	 * Overlay check
	 * 
	 * @param list
	 *            array of IScript data.
	 * @return array of boolean true means overlapped data.
	 */
	public static boolean[] isOverlap(List<IScriptData> list) {
		boolean[] result = new boolean[list.size()]; // false

		int endTime = 0;
		for (int i = 0; i < (list.size() - 1); i++) {
			IScriptData data = list.get(i);
			;
			int currentEndTime;
			if (data.isWavEnabled()) {
				currentEndTime = data.getWavEndTime();
			} else {
				currentEndTime = data.getEndTime();
			}
			int nextStartTime = list.get(i + 1).getStartTime();
			if (endTime > nextStartTime) {
				if (endTime < currentEndTime) {
					endTime = currentEndTime;
				}
			} else {
				endTime = currentEndTime;
			}

			if (!data.isExtended() && endTime > nextStartTime) {
				result[i] = true;
				result[i + 1] = true;
			}
		}
		return result;
	}

	/**
	 * Duplicate check after ScritData
	 * 
	 * @param paramData
	 * @param type
	 * @return
	 */
	public static List<IScriptData> overlapCheckAfter(IScriptData paramData,
			int type) {
		List<IScriptData> result = new ArrayList<IScriptData>();
		List<IScriptData> list = ScriptDataManager.getInstance().getDataList(
				type);

		int paramStartTime = paramData.getStartTime();
		int paramEndTime;
		if (paramData.isWavEnabled() && paramData.getWavURI() != null) {
			paramEndTime = paramData.getWavEndTime();
		} else {
			paramEndTime = paramData.getEndTime();
		}
		for (IScriptData data : list) {
			if (data.isDataCommit() == false)
				continue;
			int endTime;
			if (data.isWavEnabled() && data.getWavURI() != null) {
				endTime = data.getWavEndTime();
			} else {
				endTime = data.getEndTime();
			}
			if (data.getStartTime() < paramStartTime
					|| data.isDataCommit() == false) {
				continue;
			} else if (data.getStartTime() == paramStartTime) {
				if (data.getEndTime() < paramEndTime) {
					continue;
				} else if (endTime == paramEndTime) {
					// TODO weight?
				}
			}
			if (data.getStartTime() > paramEndTime) {
				break;
			}

			if (data.equals(paramData)) {
				// same instance
				continue;
			}
			if (type == IScriptData.TYPE_AUDIO) {
				result.add(data);
			} else if (type == IScriptData.TYPE_CAPTION) {
				result.add(data);
			}
			if (endTime > paramEndTime) {
				paramEndTime = endTime;
			}
		}
		return result;
	}

	private static int getOverlayStartPos(int startTime, int pos,
			List<IScriptData> list) {
		boolean callFlag = false;
		int start = startTime;
		int min = startTime;
		int nextPos = 0;
		if (pos < 1) {
			return 0;
		}
		int i = pos - 1;
		for (; 0 <= i; i--) {
			IScriptData data = list.get(i);
			if (start < data.getStartTime() || data.isDataCommit() == false) {
				continue;
			}
			if (start > data.getStartTime()) {
				start = data.getStartTime();
			}
			if (data.getEndTime() > startTime) {
				callFlag = true;
				if (start < min) {
					min = start;
				}
				nextPos = i;
			}
		}
		if (callFlag == true) {
			return getOverlayStartPos(min, nextPos, list);
		}
		return pos;
	}

	/**
	 * Duplicate check before ScritData
	 * 
	 * @param paramData
	 *            duplicate check data source.
	 * @param changeWeight
	 *            true ... if same data which is startTime, endTime and weight
	 *            is same, change weight .
	 * @return
	 */
	public static List<IScriptData> overlapCheckBefore(IScriptData paramData,
			int type) {
		List<IScriptData> result = new ArrayList<IScriptData>();

		List<IScriptData> list = ScriptDataManager.getInstance().getDataList(
				type);
		int paramStartTime = paramData.getStartTime();

		int paramEndTime;
		if (paramData.isWavEnabled()) {
			paramEndTime = paramData.getWavEndTime();
		} else {
			paramEndTime = paramData.getEndTime();
		}

		for (int i = list.size() - 1; 0 <= i; i--) {
			IScriptData data = list.get(i);
			if (data.isDataCommit() == false || type != data.getType())
				continue;
			int endTime;
			if (data.isWavEnabled()) {
				endTime = data.getWavEndTime();
			} else {
				endTime = data.getEndTime();
			}
			if (data.getStartTime() > paramStartTime
					|| data.isDataCommit() == false) {
				continue;
			} else if (data.getStartTime() == paramStartTime) {
				if (endTime > paramEndTime) {
					continue;
				} else if (endTime == paramEndTime) {
					// TODO weight
				}
			}
			int curPos = i + 1;
			int startPos = getOverlayStartPos(paramStartTime, curPos, list);
			if (startPos < curPos) {
				for (int j = i; startPos <= j; j--) {
					result.add(list.get(j));
				}
			}
			break;
		}
		Collections.reverse(result);
		return result;
	}

	/**
	 * @category Calculate MORA count
	 * @param strDesc
	 *            target description
	 * @return MORA count
	 */
	public static int sumMoraCount(String strDesc, String lang) {
		int moraNum = 9; // TODO check
		return sumMoraCount(strDesc, lang, moraNum);
	}

	private static int sumMoraCount(String strDesc, String lang, int moraNum) {
		double duration = 0;

		// Count character
		if ("ja-JP".equalsIgnoreCase(lang)||"ja".equals(lang)) { //TODO
			// Japanese
			duration = (sumMoraCountJp(strDesc) / (double) moraNum);
		} else {
			// English
			duration = (sumMoraCountEn(strDesc) / (double) moraNum);
		}
		// double intDuration = duration*1000;
		BigDecimal bi = new BigDecimal(duration * 1000);
		duration = bi.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
		int intDuration = (int) (duration);

		// return result
		return intDuration;
	}

	private static double sumMoraCountJp(String strDesc) {
		// return code
		float mora = 0;
		// Count character
		String desc = strDesc.trim().replaceAll("^[\\s\u3000]*", "")
				.replaceAll("[\\s\u3000]*$", "");
		for (int i = 0; i < desc.length(); i++) {
			char c = strDesc.charAt(i);
			if ((c >= 0x20) && (c <= 0x7E)) {
				// ASCII code
				mora += EN_CHAR_MORA;
			} else if ((c >= 0xFF61) && (c <= 0xFF9F)) {
				String str = String.valueOf(c);
				if (JA_KUTEN_CODE1.equals(str)) {
					// Ku-ten
					mora += JA_KUTEN_COUNT;
				} else if (JA_TOUTEN_CODE1.equals(str)) {
					// Tou-ten
					mora += JA_TOUTEN_COUNT;
				} else {
					// JIS-KANA
					mora += JA_KANA_MORA;
				}
			} else {
				String str = String.valueOf(c);
				if (JA_KUTEN_CODE2.equals(str)) {
					// Ku-ten
					mora += JA_KUTEN_COUNT;
				} else if (JA_TOUTEN_CODE2.equals(str)) {
					// Tou-ten
					mora += JA_TOUTEN_COUNT;
				} else {
					if (c >= 0x4e9c) {
						// Kanji char
						mora += JA_KANJI_MORA;
					} else if (0x3041 <= c && c <= 0x30f6) {
						// Hiragana or Katakana
						mora += JA_KANA_MORA;
					} else {
						mora += EN_CHAR_MORA;
					}
				}
			}
		}
		return (mora);

	}

	/**
	 * Sum Mora's count of Description for English
	 */
	private static double sumMoraCountEn(String strDesc) {
		float mora = 0;
		// Sum count of Mora
		String desc = strDesc.trim().replaceAll("^[\\s\u3000]*", "")
				.replaceAll("[\\s\u3000]*$", "");
		mora = desc.length() * EN_CHAR_MORA;
		return (mora);
	}

	public static String makeToolTipInfo(IScriptData data) {

		String result = null;
		String strStartTime = null;
		String strEndTime = null;
		String strTrimAudio = null;

		// Convert from Integer to String
		strStartTime = TimeFormatUtil.makeFormatHHMMSSMS_short(data
				.getStartTime());
		if (data.isWavEnabled()) {
			strEndTime = TimeFormatUtil.makeFormatHHMMSSMS_short(data
					.getWavEndTime());
		} else {
			strEndTime = TimeFormatUtil.makeFormatHHMMSSMS_short(data
					.getEndTime());
		}
		// Trim data from AudioScript
		strTrimAudio = trimString(data.getDescription(), '\r');// trimString(strAudio,
																// '\r');
		result = "Start Time : " + strStartTime + "\n" + "  End Time : "
				+ strEndTime + "\n";

		// Append string from description or WAV file path
		if (data.isWavEnabled()) {
			result = result + "  WAV File : " + data.getWavURI().toString();
		} else {
			// Use voice engine
			result = result + "Desctiption: " + strTrimAudio;
		}
		return (result);
	}

	/**
	 * Local method : Trim character from target String
	 */
	protected static String trimString(String targetString, char TrimData) {
		StringBuffer tempString = new StringBuffer(256);
		for (int i = 0; i < targetString.length(); i++) {
			if (TrimData != targetString.charAt(i)) {
				tempString.append(targetString.charAt(i));
			}
		}
		return (tempString.toString());
	}

	static public void debug() {
		// ScriptDataManager scriptManager = ScriptDataManager.getInstance();
		// ArrayList<IScript> iData =
		// (ArrayList<IScript>)scriptManager.getScriptDataList();
		// TreeSet tree = scriptManager.getScriptData();
		// tree.size();
		// int pos = 0;
		// for (IScript d : iData) {
		// //
		// System.out.println("ScriptDataManager  : order="+pos+" : startTime="+d.getStartTime()+" : endTime="+d.getEndTime()+" : weight="+d.getWeight()+" : caption="+d.getCaption()+" : description="+d.getDescription()+" : comment="+d.getScriptComment()+" : scenario="+d.getScenario()+" : FileName="+d.getFileName());
		// System.out.println("ScriptDataManager  : order="+pos+" : commit="+d.isDataCommit()+" : type="+d.getType()+
		// " : dataKind=" +
		// d.getDataKind()+" : startTime="+d.getStartTime()+" : endTime="+d.getEndTime()+" : weight="+d.getWeight()+" : caption="+d.getCaption()+" : description="+d.getDescription()+" : comment="+d.getScriptComment()+" : scenario="+d.getScenario()+" : FileName="+d.getFileName()+" : gender="+d.getVgGender()+" : spealer="+d.getCharacter());
		// List<CaptionData> childList = d.getCaptionDataList();
		// if(childList != null && childList.size() > 0){
		// int childPos = 0;
		// for(CaptionData child : childList){
		// System.out.println("ScriptDataManager  child : order="+childPos+" : startTime="+child.getStartTime()+" : endTime="+child.getEndTime()+" : weight="+child.getWeight()+" : caption="+d.getCaption()+" : description="+d.getDescription()+" : comment="+d.getScriptComment()+" : scenario="+d.getScenario()+" : FileName="+d.getFileName());
		// childPos++;
		// }
		// }
		// pos++;
		// }
		// System.out.println("length="+iData.size()+" : Tree size=" +
		// tree.size());
	}

	static public void debug(TreeViewer instScriptListTreeViewer) {
		// if(instScriptListTreeViewer == null) {
		// return;
		// }
		// Tree tree = instScriptListTreeViewer.getTree() ;
		// TreeItem[] itemArray = tree.getItems();
		// int pos = 0;
		// for(TreeItem item : itemArray) {
		// Object dt = item.getData();
		// if(dt instanceof ScriptDataNew){
		// System.out.println("TreeView :  position="+position+": start="+((ScriptDataNew)dt).getStartTime()+" : end="+((ScriptDataNew)dt).getEndTime()
		// +" : weight="+((ScriptDataNew)dt).getWeight()+" : caption="+((ScriptDataNew)dt).getCaption()+" : description="+((ScriptDataNew)dt).getDescription()+" : comment="+((ScriptDataNew)dt).getScriptComment()+" : scenario="+((ScriptDataNew)dt).getScenario()+" : FileName="+((ScriptDataNew)dt).getFileName());
		// List<CaptionData> childList =
		// ((ScriptDataNew)dt).getCaptionDataList() ;
		// if(childList != null && childList.size() > 0){
		// int childPos = 0;
		// for(CaptionData child : childList){
		// System.out.println("TreeView child : position="+childPos+" : start="+child.getStartTime()
		// +" : end="+child.getEndTime()+" : weight="+((ScriptDataNew)dt).getWeight()+" : caption="+((ScriptDataNew)dt).getCaption()+" : description="+((ScriptDataNew)dt).getDescription()+" : comment="+((ScriptDataNew)dt).getScriptComment()+" : scenario="+((ScriptDataNew)dt).getScenario()+" : FileName="+((ScriptDataNew)dt).getFileName());
		// childPos++;
		// }
		// }
		// }
		// pos++;
		// }

	}
}