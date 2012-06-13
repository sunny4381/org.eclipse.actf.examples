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

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class WavUtil {

	/**
	 * @param file
	 *            target WAV file
	 * @return length of WAV file in Microsecond
	 */
	public static long getMicrosecondLength(File file) {
		long result = -1;
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(file);
			Clip clp = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class,
					ais.getFormat()));
			clp.open(ais);
			result = clp.getMicrosecondLength();
			ais.close();
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * @param file
	 *            target WAV file
	 * @return length of WAV file in Millisecond
	 */
	public static double getMillisecondLength(File file) {
		long result = getMicrosecondLength(file);
		if (result == -1) {
			return -1;
		}
		return result / (double) 1000;
	}

	/**
	 * @param file
	 *            target WAV file
	 * @return length of WAV file in Second
	 */
	public static double getLength(File file) {
		long result = getMicrosecondLength(file);
		if (result == -1) {
			return -1;
		}
		return result / (double) 1000000;
	}

	/**
	 * Check WAV file format
	 * 
	 * @param fname
	 *            target file
	 * @return true if target file is WAV
	 */
	public static boolean isWavFormat(String fname) {
		boolean result = false;
		File fh;

		try {
			fh = new File(fname);
			AudioFileFormat aff = AudioSystem.getAudioFileFormat(fh);
			if (AudioFileFormat.Type.WAVE == aff.getType()) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (result);
	}

}
