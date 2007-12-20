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

package org.eclipse.actf.examples.aibrowser.launcher;

import org.eclipse.core.launcher.Main;

public class EclipseLauncher extends Thread {
    private Main eclipseMain;
    // private boolean launched;

    public void registerRequestBroker(Object o) {
        RequestBrokerProxy.registerRequestBroker(o);
    }

    public void sendEvent(int id, Object param) {
        RequestBrokerProxy.handleEvent(id, param);
    }
    
    public void run() {
        String[] args = new String[2];
        args[0] = "-debug";
        args[1] = "-osgi";
        try {
            System.setSecurityManager(null);

            synchronized (this) {
                eclipseMain.run(args);
                // int r = eclipseMain.run(args);
                // System.err.println("Result: " + r);
                // launched = true;
                notify();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public synchronized void launch() {
        start();
        /*
        while (!launched) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        */
    }

    public EclipseLauncher() {
        super("EclipseMainThread");
        this.eclipseMain = new Main();
        // this.launched = false;
    }
    
}
