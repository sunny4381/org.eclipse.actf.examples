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

package org.eclipse.actf.examples.aibrowser.launcher.bean;

import java.io.Serializable;

import org.eclipse.actf.examples.aibrowser.launcher.RequestBrokerProxy;
import org.eclipse.actf.examples.aibrowser.launcher.nvm3.BrowserUIService;
import org.eclipse.actf.examples.aibrowser.launcher.nvm3.Nvm3Service;
import org.eclipse.actf.examples.aibrowser.launcher.nvm3.Nvm3ServiceListener;
import org.eclipse.actf.examples.aibrowser.launcher.nvm3.impl.BrowserUIServiceImpl;
import org.eclipse.actf.examples.aibrowser.launcher.nvm3.impl.Nvm3ServiceProxy;

public class NavigatorUIBean implements Serializable {
    private static final long serialVersionUID = 5127765773726703672L;
    
    private Nvm3Service nvm3Service;

    public Nvm3Service getNvm3Service() {
        return nvm3Service;
    }

    private BrowserUIService browserUIService;

    public BrowserUIService getBrowserUIService() {
        return browserUIService;
    }
    
    public NavigatorUIBean() {
    	this.nvm3Service = new Nvm3ServiceProxy();
    	this.browserUIService = new BrowserUIServiceImpl();
    }

    public void addNvm3ServiceListner(Nvm3ServiceListener listener) {
        System.err.println("Add Listener:" + listener);
        RequestBrokerProxy.addNvm3ServiceListner(listener);
    }

    public void removeNvm3ServiceListner(Nvm3ServiceListener listener) {
        System.err.println("Remove Listener:" + listener);
        RequestBrokerProxy.removeNvm3ServiceListner(listener);
    }
}
