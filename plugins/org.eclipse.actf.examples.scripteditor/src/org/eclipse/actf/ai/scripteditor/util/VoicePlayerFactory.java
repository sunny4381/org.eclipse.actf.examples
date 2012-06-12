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
package org.eclipse.actf.ai.scripteditor.util;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.actf.ai.scripteditor.data.IScriptData;
import org.eclipse.actf.ai.tts.ISAPIEngine;
import org.eclipse.actf.ai.tts.ITTSEngine;
import org.eclipse.actf.ai.voice.IVoice;
import org.eclipse.actf.ai.voice.IVoiceEventListener;
import org.eclipse.actf.ai.voice.VoiceUtil;
import org.eclipse.actf.examples.scripteditor.Activator;

/**
 * 
 * @category Voice Player
 * 
 */
public class VoicePlayerFactory {

	public static final int NOT_SUPPORTED = -1;
	public static Set<String> langSet = new TreeSet<String>(
			ISAPIEngine.LANGID_MAP.keySet()); // TODO

	private static final int VP_EVENT_FIN_SPEAK = 99;
	private static final int TTSFLAG_DEFAULT = 0;

	private static IVoice voice = VoiceUtil.getVoice();
	private static VoicePlayerFactory ownInst = null;

	private int currentVoicePlayerStatus = -1;
	private File tmpFile;

	private VoicePlayerFactory() {
		ownInst = this;
		voice.setEventListener(new MyVoiceEventListener());
	}

	public static VoicePlayerFactory getInstance() {
		if (ownInst == null) {
			ownInst = new VoicePlayerFactory();
		}
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
		voice.speak(voiceMessage, true);
		addSpeakIndex(VP_EVENT_FIN_SPEAK);
	}

	public void stop() {
		voice.speak("", true);
	}

	/**
	 * Getter methods
	 */
	public int getSpeed() {
		return (voice.getSpeed());
	}

	public int getPitch() {
		return (NOT_SUPPORTED);
	}

	public int getVolume() {
		return (NOT_SUPPORTED);
	}

	public void setLang(String language) {
		voice.getTTSEngine().setLanguage(language);
	}

	/**
	 * Setter methods
	 */
	public void setGender(String gender) {
		voice.getTTSEngine().setGender(gender);
	}

	public void setSpeed(int speed) {
		voice.setSpeed(speed);
	}

	public void setPitch(int pitch) {
	}

	public void setVolume(int volume) {
	}

	public void setPlayVoiceStatus(int stat) {
		currentVoicePlayerStatus = stat;
	}

	public boolean getPlayVoiceStatus() {
		return (((currentVoicePlayerStatus > -1) ? true : false));
	}

	public boolean canSpeakToFile() {
		return voice.getTTSEngine().canSpeakToFile();
	}

	public boolean speakToFile(String target, File targetFile) {
		ITTSEngine engine = voice.getTTSEngine();
		if (engine.canSpeakToFile()) {
			return (engine.speakToFile(target, targetFile));
		}
		return false;
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

	public void speak(IScriptData data) {
		setLang(data.getLang());
		setGender((data.getVgGender() ? "male" : "female"));
		setSpeed(data.getVgPlaySpeed());
		setPitch(data.getVgPitch());
		setVolume(data.getVgVolume());

		speak(data.getDescription());
	}

	public int getSpeakLength(IScriptData data) {
		int length = -1;
		setLang(data.getLang());
		setGender((data.getVgGender() ? "male" : "female"));
		setSpeed(data.getVgPlaySpeed());
		setPitch(data.getVgPitch());
		setVolume(data.getVgVolume());

		if (canSpeakToFile()) {
			if (tmpFile == null) {
				try {
					tmpFile = Activator.getDefault().createTempFile("test",
							".wav");
				} catch (Exception e) {
					return length;
				}
			}
			speakToFile(data.getDescription(), tmpFile);
			SoundMixer sm = SoundMixer.getInstance();
			try {
				sm.storeWavHeader(tmpFile.toURI());
				length = sm.getDurationTimeWav();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return length;
	}

}
