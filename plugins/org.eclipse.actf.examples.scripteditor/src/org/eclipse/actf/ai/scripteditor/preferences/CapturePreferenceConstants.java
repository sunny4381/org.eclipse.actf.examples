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

public class CapturePreferenceConstants {

	/**
	 * Capture sampling rate values
	 */
	public static final String SAMPRATE_GROUP_NAME = "CaptureSamplingRate";
	public static final String SAMPRATE_FAST = "SAMPRATE_FAST";
	public static final String SAMPRATE_NOM = "SAMPRATE_NOM";
	public static final String SAMPRATE_LATE = "SAMPRATE_LATE";

	/**
	 * Capture volume level gain values
	 */
	public static final String GAIN_VOLLVL_NAME = "GainVolumeLevel";
	public static final int GAIN_MIN = 50;
	public static final int GAIN_MAX = 500;
	public static final int GAIN_DEF = 100;

}
