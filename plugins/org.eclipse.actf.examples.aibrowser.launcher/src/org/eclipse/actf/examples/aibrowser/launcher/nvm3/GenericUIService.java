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

public interface GenericUIService {
    int TARGET_BMLBROWSER = 1;
    int TARGET_CC = 2;
    int TARGET_EPG = 3;

    void addServiceListener(Nvm3ServiceListener cb); 
    void changeControlTarget(int targetId);
    void dispose();
    void removeServiceListener(Nvm3ServiceListener cb);
}
