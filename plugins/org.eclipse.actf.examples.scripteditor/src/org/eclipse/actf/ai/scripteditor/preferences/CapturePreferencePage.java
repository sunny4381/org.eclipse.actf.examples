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
import org.eclipse.actf.ai.internal.ui.scripteditor.VolumeLevelCanvas;
import org.eclipse.actf.ai.scripteditor.util.SoundMixer;
import org.eclipse.actf.examples.scripteditor.Activator;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.ScaleFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class CapturePreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	/**
	 * Local data
	 */
	private ScaleFieldEditor gainEditor = null;
	private Label gainLabel = null;
	private CaptureAudioPropertyChangeListener chgListener = null;

	/**
	 * Constructor
	 */
	public CapturePreferencePage() {
		// SetUP own Preference page
		super(GRID);
		setDescription(Messages.capture_description);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		// SetUP own ChangeListener
		chgListener = new CaptureAudioPropertyChangeListener();
		Activator.getDefault().getPreferenceStore()
				.addPropertyChangeListener(chgListener);
	}

	/**
	 * Override
	 */
	protected void createFieldEditors() {

		// Sampling rate : radio button group
		String[][] labelAndValue = new String[][] {
				new String[] { Messages.capture_samprate_fast,
						CapturePreferenceConstants.SAMPRATE_FAST },
				new String[] { Messages.capture_samprate_nom,
						CapturePreferenceConstants.SAMPRATE_NOM },
				new String[] { Messages.capture_samprate_late,
						CapturePreferenceConstants.SAMPRATE_LATE } };
		addField(new RadioGroupFieldEditor(
				CapturePreferenceConstants.SAMPRATE_GROUP_NAME,
				Messages.capture_samprate_title, 1, labelAndValue,
				getFieldEditorParent()));

		// Gain for volume level : slider
		addField(gainEditor = new ScaleFieldEditor(
				CapturePreferenceConstants.GAIN_VOLLVL_NAME,
				Messages.capture_vollvl_gain, getFieldEditorParent(),
				CapturePreferenceConstants.GAIN_MIN,
				CapturePreferenceConstants.GAIN_MAX, 5, 25));

		// Create Composite for otherwise widgets
		Composite compo = new Composite(getFieldEditorParent(), SWT.NONE);
		GridLayout layoutCompo = new GridLayout();
		layoutCompo.marginHeight = 0;
		layoutCompo.marginWidth = 10;
		compo.setLayout(layoutCompo);
		GridData layoutDataLabel = new GridData(GridData.HORIZONTAL_ALIGN_END);
		layoutDataLabel.horizontalSpan = 2;
		compo.setLayoutData(layoutDataLabel);

		// Label : Display current slider value
		gainLabel = new Label(compo, SWT.NONE);
		gainLabel.setSize(80, 24);
		gainLabel.setText(makeupGainSliderValue(CapturePreferenceUtil
				.getPreferenceVolLvlGain()));

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
		// dispose all widgets
		gainLabel.dispose();
		gainEditor.dispose();
		// initialize variables
		chgListener = null;
		gainLabel = null;
		gainEditor = null;
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
	}

	/**
	 * Local method : MakeUP string of current slider value
	 * 
	 * @return
	 */
	private String makeupGainSliderValue(int newValue) {
		// MakeUP string of gain meter format
		String strMeter = String.valueOf(newValue) + "%";

		// return result
		return (strMeter);
	}

	/**
	 * Local class : Listener of property change event for each editor
	 */
	private class CaptureAudioPropertyChangeListener implements
			IPropertyChangeListener {
		/**
		 * Override
		 */
		public void propertyChange(PropertyChangeEvent eve) {
			// Check property name : Gain volume level
			if (CapturePreferenceConstants.GAIN_VOLLVL_NAME.equals(eve
					.getProperty())) {
				// update value by new preference settings
				int newValue = CapturePreferenceUtil.getPreferenceVolLvlGain();
				VolumeLevelCanvas.getInstance().setCurrentVolLvlGain(newValue);
				// Repaint own Composite cause of changed text of volume gain
				// value
				gainLabel.setText(makeupGainSliderValue(newValue));
			}
			// Check property name : Gain volume level
			if (CapturePreferenceConstants.SAMPRATE_GROUP_NAME.equals(eve
					.getProperty())) {
				// update value by new preference settings
				SoundMixer.getInstance().setSampleRateCaptureAudio(
						CapturePreferenceUtil.getPreferenceSampleRate());
			}
		}
	}

	// TODO for Debug : PreferenceInitializer setting
	private void initializeDefaultPreferences() {
		IPreferenceStore store = getPreferenceStore();

		// Initialize values
		store.setDefault(CapturePreferenceConstants.SAMPRATE_GROUP_NAME,
				CapturePreferenceConstants.SAMPRATE_NOM);
		store.setDefault(CapturePreferenceConstants.GAIN_VOLLVL_NAME,
				CapturePreferenceConstants.GAIN_DEF);
	}

}
