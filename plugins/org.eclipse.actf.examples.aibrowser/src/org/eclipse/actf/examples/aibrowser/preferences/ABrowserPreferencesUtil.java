/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.examples.aibrowser.preferences;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;



public class ABrowserPreferencesUtil {
    private PreferenceDialog dialog;

    public static ABrowserPreferencesUtil newInstance(String pageId) {
        PreferenceDialog d = PreferencesUtil.createPreferenceDialogOn(null, pageId,
                                                                      null, null);
        return new ABrowserPreferencesUtil(d);
    }

    public static ABrowserPreferencesUtil newInstance() {
        return newInstance(null);
    }

    public void open() {
        dialog.open();
    }

    public void close() {
        dialog.close();
    }

    private ABrowserPreferencesUtil(PreferenceDialog dialog) {
        this.dialog = dialog;
    }
}
