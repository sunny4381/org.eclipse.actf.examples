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
package org.eclipse.actf.ai.internal.ui.scripteditor;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "messages";//$NON-NLS-1$

	private Messages() {
		// Do not instantiate
	}

	// Messages of audio capture part
	public static String capture_description;
	public static String capture_samprate_title;
	public static String capture_samprate_fast;
	public static String capture_samprate_nom;
	public static String capture_samprate_late;
	public static String capture_vollvl_gain;

	// Messages of CSV rule preference part
	public static String csvrule_description;
	public static String csvrule_save_title;
	public static String csvrule_save_renew;
	public static String csvrule_save_insert;
	public static String csvrule_wav_title;
	public static String csvrule_wav_drop;
	public static String csvrule_wav_confirm;
	public static String csvrule_wav_through;

	// Messages of XML file dialog part
	public static String xml_dialog_title_confirm;
	public static String xml_dialog_save_confirm;
	public static String xml_dialog_title_overwrite;
	public static String xml_dialog_save_overwrite;
	public static String xml_dialog_title_error;
	public static String xml_dialog_script_nodesc;
	public static String xml_dialog_script_noexist;
	public static String xml_dialog_title_modify;
	public static String xml_dialog_save_modify;
	public static String xml_dialog_wav_confirm;
	public static String xml_dialog_access_denied;

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

}
