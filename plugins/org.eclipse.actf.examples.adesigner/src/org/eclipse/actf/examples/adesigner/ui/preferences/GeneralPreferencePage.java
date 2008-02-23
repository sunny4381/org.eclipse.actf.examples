/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Norimasa HAYASHIDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.examples.adesigner.ui.preferences;

import org.eclipse.actf.examples.adesigner.ADesignerPlugin;
import org.eclipse.actf.examples.adesigner.internal.Messages;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;




public class GeneralPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public GeneralPreferencePage() {
        super(GRID);
        setPreferenceStore(ADesignerPlugin.getDefault().getPreferenceStore());
    }

    public void init(IWorkbench workbench) {
    }

    protected void createFieldEditors() {

        addField(new RadioGroupFieldEditor(IPreferenceConstants.STARTUP_OPTION_ID,
                Messages.GeneralPreferencePage_0, 1, new String[][] {
                        { Messages.GeneralPreferencePage_1,
                                IPreferenceConstants.CHOICE_SHOW_MODE_DIALOG },
                        { Messages.GeneralPreferencePage_2,
                                IPreferenceConstants.CHOICE_DONOT_SHOW_MODE_DIALOG } }, getFieldEditorParent()));

    }

}
