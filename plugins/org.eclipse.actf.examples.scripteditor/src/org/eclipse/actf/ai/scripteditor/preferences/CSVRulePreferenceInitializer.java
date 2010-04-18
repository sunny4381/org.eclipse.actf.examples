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

import org.eclipse.actf.examples.scripteditor.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class CSVRulePreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		// Initialize values
		store.setDefault(CSVRulePreferenceConstants.CSVRULE_SAVE_GROUP_NAME, CSVRulePreferenceConstants.CSVRULE_SAVE_INSERT);
		store.setDefault(CSVRulePreferenceConstants.CSVRULE_WAV_GROUP_NAME, CSVRulePreferenceConstants.CSVRULE_WAV_DROP);
	}

}
