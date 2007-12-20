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

package org.eclipse.actf.examples.aibrowser.launcher.nvm3;

public class Nvm3ServiceEvent {
    public static final int SHOW_STATUS = 100;
	
    public static final int EVENT_NEW_URL = 101;

    public static final int EVENT_NEWPAGE_READY = 1000;
	
    public static final int EVENT_LOAD_STARTING = 1001;
	
    public static final int EVENT_WAIT_FOR_PROCESSING = 1200;
	
    public static final int EVENT_INFORMATION_UPDATED = 1201;

    public static final int EVENT_TREE_MODIFIED = 1202;

    public static final int EVENT_NOTIFICATION = 1203;
	
    public static final int EVENT_ALERT_MODAL = 1204;
	
    public static final int EVENT_AUTOMATIC_TRANSITION = 1205;
	
    private final int id;
    private final Object param;

    public int getId() {
    	return id;
    }

    public Object getParam() {
    	return param;
    }

    public Nvm3ServiceEvent(int id, Object param) {
        this.id = id;
        this.param = param;
    }
}
