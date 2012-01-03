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

	// MORA
	private static final float JA_KANJI_MORA = 1.545f;
	private static final float JA_KANA_MORA = 0.988f;
	private static final float EN_CHAR_MORA = 0.347f;

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
	public float sumMoraCountEn(String strDesc) {
		// return code
		float mora = 0;
		// Sum count of Mora
		String desc = strDesc.trim().replaceAll("^[\\s　]*", "")
				.replaceAll("[\\s　]*$", "");
		mora = desc.length() * EN_CHAR_MORA;
		// return current Mora's counter
		return (mora);
	}

	/**
	 * Sum Mora's count of Description for Japanese
	 */
	public float sumMoraCountJp(String strDesc) {
		// return code
		float mora = 0;
		// Count character
		String desc = strDesc.trim().replaceAll("^[\\s　]*", "")
				.replaceAll("[\\s　]*$", "");
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
