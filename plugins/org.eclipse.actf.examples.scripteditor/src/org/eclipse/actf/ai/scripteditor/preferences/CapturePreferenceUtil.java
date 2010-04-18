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
package org.eclipse.actf.ai.scripteditor.preferences;

import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.examples.scripteditor.Activator;

public class CapturePreferenceUtil implements IUNIT {

	/**
	 * Getter method : Get current preference string
	 * 
	 * @return
	 */
	public static float getPreferenceSampleRate() {
		float realSampleRate = SM_CAP_RATE_NOM;

		// PickUP current stored string of sampling rate
		String strSampleRate = Activator.getDefault().getPreferenceStore()
				.getString(CapturePreferenceConstants.SAMPRATE_GROUP_NAME);
		// Exchange real data of sampling rate
		if (CapturePreferenceConstants.SAMPRATE_FAST.equals(strSampleRate)) {
			// Select Fast mode(44100Hz)
			realSampleRate = SM_CAP_RATE_FAST;
		} else if (CapturePreferenceConstants.SAMPRATE_LATE
				.equals(strSampleRate)) {
			// Select Late mode(11025Hz)
			realSampleRate = SM_CAP_RATE_LATE;
		}

		// return result
		return (realSampleRate);
	}

	/**
	 * Getter method : Get current preference string
	 * 
	 * @return
	 */
	public static int getPreferenceVolLvlGain() {
		// PickUP current stored string of sampling rate
		int gain = Activator.getDefault().getPreferenceStore().getInt(
				CapturePreferenceConstants.GAIN_VOLLVL_NAME);

		// TODO : Exchange null code
		if (gain == 0) {
			// reset 100%
			gain = 100;
		}

		// return result
		return (gain);
	}

}
