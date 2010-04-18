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
package org.eclipse.actf.ai.scripteditor.util;

import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.ai.voice.IVoice;
import org.eclipse.actf.ai.voice.IVoiceEventListener;
import org.eclipse.actf.ai.voice.VoiceUtil;

/**
 * 
 * @category Voice Player
 * 
 */
public class VoicePlayerFactory implements IUNIT {

	// for Voice Engine
	private static IVoice voice = VoiceUtil.getVoice();
	private int currentVoicePlayerStatus = -1;

	// Local data
	static private VoicePlayerFactory ownInst = null;

	// Define
	private static final int VP_EVENT_FIN_SPEAK = 99;
	private static final int TTSFLAG_DEFAULT = 0;
	private static final String JA_KUTEN_CODE1 = "\uff61";
	private static final String JA_KUTEN_CODE2 = "\u3002";
	private static final String JA_TOUTEN_CODE1 = "\uff64";
	private static final String JA_TOUTEN_CODE2 = "\u3001";
	private static final int JA_KUTEN_COUNT = 5;
	private static final int JA_TOUTEN_COUNT = 2;

	/**
	 * Constructor
	 */
	public VoicePlayerFactory() {
		// Store own instance
		ownInst = this;
		// SetUP Voice engine event listener
		voice.setEventListener(new MyVoiceEventListener());
	}

	static public VoicePlayerFactory getInstance() {
		// return current Instance of VoicePlayerFactory
		return (ownInst);
	}

	private void addSpeakIndex(int index) {
		if (voice != null) {
			voice.getTTSEngine().speak("", TTSFLAG_DEFAULT, index);
		}
	}

	/**
	 * @category Speak voice
	 */
	public void speak(String voiceMessage) {
		// speak motion with flush(TRUE)
		voice.speak(voiceMessage, true);
		addSpeakIndex(VP_EVENT_FIN_SPEAK);
	}

	public void stop() {
		// voice.stop();
		voice.speak("", TTSFLAG_FLUSH);
	}

	public void pause() {
	}

	public void resume() {
	}

	/**
	 * Getter methods
	 */
	public int getSpeed() {
		// Get Voice Speed param.
		return (voice.getSpeed());
	}

	public int getPitch() {
		// Get Voice Speed param.
		return (VE_NOSUPPORT);
	}

	public int getVolume() {
		// Get Voice Speed param.
		return (VE_NOSUPPORT);
	}

	/**
	 * Setter methods
	 */
	public void setGender(String gender) {
		voice.getTTSEngine().setGender(gender);
	}

	public void setSpeed(int speed) {
		// Set Voice Speed param.
		voice.setSpeed(speed);
	}

	public void setPitch(int pitch) {
	}

	public void setVolume(int volume) {
	}

	public void setPlayVoiceStatus(int stat) {
		// Set next status
		currentVoicePlayerStatus = stat;
	}

	public boolean getPlayVoiceStatus() {
		// Return status of current ProTalker engine
		return (((currentVoicePlayerStatus > -1) ? true : false));
	}

	/**
	 * Sum Mora's count of Description for English
	 */
	public int sumMoraCountEn(String strDesc) {
		// return code
		int mora = 0;
		// check valid
		String validChara = "aiueoAIUEO";

		// Sum count of Mora
		for (int i = 0; i < (strDesc.length() - 1); i++) {
			// check 1st character code
			if (validChara.indexOf(strDesc.charAt(i)) == -1) {
				// Count 1Mora
				mora++;
				// check 2nd character code
				if (validChara.indexOf(strDesc.charAt(i + 1)) >= 0) {
					// Count 1Mora with together previous character
					i++;
					continue;
				}
			} else {
				// Count 1Mora
				mora++;
			}
		}

		// Trim count of blank code
		String trimBlank = strDesc.replace(" ", "");
		int delta = strDesc.length() - trimBlank.length();
		// real count
		mora = mora - delta;

		// return current Mora's counter
		return (mora);
	}

	/**
	 * Sum Mora's count of Description for Japanese
	 */
	public int sumMoraCountJp(String strDesc) {
		// return code
		int mora = 0;
		// Count character
		for (int i = 0; i < strDesc.length(); i++) {
			char c = strDesc.charAt(i);
			if ((c >= 0x20) && (c <= 0x7E)) {
				// ASCII code
				mora++;
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
					mora++;
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
					// Bai-kaku
					mora++;
				}
			}
		}

		// Trim count of blank code
		String trimBlank = strDesc.replace(" ", "");
		int delta = strDesc.length() - trimBlank.length();
		// real count
		mora = mora - delta;

		// return current Mora's counter
		return (mora);
	}

	/**
	 * Local class implements IVoiceEventListener
	 */
	class MyVoiceEventListener implements IVoiceEventListener {
		// Check event method
		public void indexReceived(final int index) {
			// 1 : Start Play
			if ((index == -1) && (currentVoicePlayerStatus == 0)) {
				// Set status flag
				setPlayVoiceStatus(1);
			}
			// 99 : Finish Play
			else if ((index == VP_EVENT_FIN_SPEAK)
					&& (currentVoicePlayerStatus == 1)) {
				// Reset status flag
				setPlayVoiceStatus(-1);
			}
			// ? : other Event
			else {
				// System.out.printf("IVoice::indexReceived() : index=%d, status=%d \n",
				// index, currentVoicePlayerStatus);
			}
		}
	}
}
