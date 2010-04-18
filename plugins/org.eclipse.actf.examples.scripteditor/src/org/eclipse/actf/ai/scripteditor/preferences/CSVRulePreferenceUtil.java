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

public class CSVRulePreferenceUtil implements IUNIT {

	/**
	 * @category Getter method : Get current preference value
	 * @return CSV saved rule : 0:insert data, 1:renewal data
	 */
	static public int getPreferenceCsvSaveRule() {
		// insert data mode
		int result = CSV_SAVE_RULE_INSERT;

		// PickUP current stored string of rule of saved CSV data
		String strCsvSaveRule = Activator.getDefault().getPreferenceStore()
				.getString(CSVRulePreferenceConstants.CSVRULE_SAVE_GROUP_NAME);

		// Check current selection
		if (CSVRulePreferenceConstants.CSVRULE_SAVE_RENEWAL
				.equals(strCsvSaveRule)) {
			// Update status to renewal data mode
			result = CSV_SAVE_RULE_RENEWAL;
		}

		// return result
		return (result);
	}

	/**
	 * @category Getter method : Get current preference value
	 * @return CSV WAV file path saved rule : 0:save only description, 1:display
	 *         confirmation dialog 2:save original data
	 */
	static public int getPreferenceCsvWavRule() {
		// drop WAV data(save only description) mode
		int result = CSV_WAV_RULE_DROP;

		// PickUP current stored string of rule of saved WAV file path
		String strCsvWavRule = Activator.getDefault().getPreferenceStore()
				.getString(CSVRulePreferenceConstants.CSVRULE_WAV_GROUP_NAME);

		// Check current selection
		if (CSVRulePreferenceConstants.CSVRULE_WAV_CONFIRM
				.equals(strCsvWavRule)) {
			// Update status to display confirmation dialog mode
			result = CSV_WAV_RULE_CONFIRM;
		} else if (CSVRulePreferenceConstants.CSVRULE_WAV_THROUGH
				.equals(strCsvWavRule)) {
			// Update status to save original data
			result = CSV_WAV_RULE_IGNORE;
		}

		// return result
		return (result);
	}

}
