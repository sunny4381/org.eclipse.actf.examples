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

import org.eclipse.actf.ai.internal.ui.scripteditor.Messages;
import org.eclipse.actf.examples.scripteditor.Activator;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class CSVRulePreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	/**
	 * Local data
	 */
	private CSVRulePropertyChangeListener chgListener = null;

	/**
	 * Constructor
	 */
	public CSVRulePreferencePage() {
		// SetUP own Preference page
		super(GRID);
		setDescription(Messages.csvrule_description);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		// SetUP own ChangeListener
		chgListener = new CSVRulePropertyChangeListener();
		Activator.getDefault().getPreferenceStore()
				.addPropertyChangeListener(chgListener);
	}

	/**
	 * Override
	 */
	protected void createFieldEditors() {

		// Saved data rule : radio button group
		String[][] labelAndValue1 = new String[][] {
				new String[] { Messages.csvrule_save_insert,
						CSVRulePreferenceConstants.CSVRULE_SAVE_INSERT },
				new String[] { Messages.csvrule_save_renew,
						CSVRulePreferenceConstants.CSVRULE_SAVE_RENEWAL } };
		addField(new RadioGroupFieldEditor(
				CSVRulePreferenceConstants.CSVRULE_SAVE_GROUP_NAME,
				Messages.csvrule_save_title, 1, labelAndValue1,
				getFieldEditorParent()));

		// Saved WAV file path rule : radio button group
		String[][] labelAndValue2 = new String[][] {
				new String[] { Messages.csvrule_wav_drop,
						CSVRulePreferenceConstants.CSVRULE_WAV_DROP },
				new String[] { Messages.csvrule_wav_confirm,
						CSVRulePreferenceConstants.CSVRULE_WAV_CONFIRM },
				new String[] { Messages.csvrule_wav_through,
						CSVRulePreferenceConstants.CSVRULE_WAV_THROUGH } };
		addField(new RadioGroupFieldEditor(
				CSVRulePreferenceConstants.CSVRULE_WAV_GROUP_NAME,
				Messages.csvrule_wav_title, 1, labelAndValue2,
				getFieldEditorParent()));

		// Create Composite for otherwise widgets
		Composite compo = new Composite(getFieldEditorParent(), SWT.NONE);
		GridLayout layoutCompo = new GridLayout();
		layoutCompo.marginHeight = 0;
		layoutCompo.marginWidth = 10;
		compo.setLayout(layoutCompo);
		GridData layoutDataLabel = new GridData(GridData.HORIZONTAL_ALIGN_END);
		layoutDataLabel.horizontalSpan = 2;
		compo.setLayoutData(layoutDataLabel);

		// Pack all widgets into own Composite
		compo.layout();
		compo.pack();

		// TODO for Debug : PreferenceInitializer setting
		initializeDefaultPreferences();
	}

	/**
	 * Setter method : Dispose process
	 */
	public void dispose() {
		// dispose PreferencePage
		super.dispose();

		// dispose ChangeEventListener
		getPreferenceStore().removePropertyChangeListener(chgListener);
		// initialize variables
		chgListener = null;
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
	}

	/**
	 * Local class : Listener of property change event for each editor
	 */
	private class CSVRulePropertyChangeListener implements
			IPropertyChangeListener {
		/**
		 * Override
		 */
		public void propertyChange(PropertyChangeEvent eve) {
			// Check property name : Rule of saved data
			if (CSVRulePreferenceConstants.CSVRULE_SAVE_GROUP_NAME.equals(eve
					.getProperty())) {
				// No process
			}
			// Check property name : Rule of saved WAV file path
			if (CSVRulePreferenceConstants.CSVRULE_WAV_GROUP_NAME.equals(eve
					.getProperty())) {
				// No process
			}
		}
	}

	// TODO for Debug : PreferenceInitializer setting
	private void initializeDefaultPreferences() {
		IPreferenceStore store = getPreferenceStore();

		// Initialize values
		store.setDefault(CSVRulePreferenceConstants.CSVRULE_SAVE_GROUP_NAME,
				CSVRulePreferenceConstants.CSVRULE_SAVE_INSERT);
		store.setDefault(CSVRulePreferenceConstants.CSVRULE_WAV_GROUP_NAME,
				CSVRulePreferenceConstants.CSVRULE_WAV_DROP);
	}

}
