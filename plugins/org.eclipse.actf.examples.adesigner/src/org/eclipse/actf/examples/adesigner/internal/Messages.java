/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.examples.adesigner.internal;

import org.eclipse.osgi.util.NLS;



public class Messages extends NLS {
    private static final String BUNDLE_NAME = "messages"; //$NON-NLS-1$

    public static String GeneralPreferencePage_0;

	public static String GeneralPreferencePage_1;

	public static String GeneralPreferencePage_2;
    
    public static String DialogOpenURL_Open_URL;
    
    public static String ModeSwitchDialog_title;

    public static String ModeSwitchDialog_message;
    
    public static String ModeSwitchDialog_checkShowDialog;
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
