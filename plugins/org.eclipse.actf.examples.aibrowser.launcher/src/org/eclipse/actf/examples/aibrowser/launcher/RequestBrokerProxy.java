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

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.actf.examples.aibrowser.launcher.nvm3.Nvm3ServiceEvent;
import org.eclipse.actf.examples.aibrowser.launcher.nvm3.Nvm3ServiceListener;
import org.eclipse.actf.examples.aibrowser.launcher.reflect.Mirror;


public class RequestBrokerProxy {
    public static final Object[] EMPTY_ARG = new Object[0];

    private static Mirror requestBrokerMirror;
    private static Method invokeNavigatorMethod;
    private static Method invokeTreeManagerMethod;
    private static Method invokeITreeItemMethod;
    
    public static Object invokeNavigator(String method, Object... args) throws Exception {
    	return invokeNavigatorMethod.invoke(requestBrokerMirror.getObject(), method, args);
    }

    private static Set<Nvm3ServiceListener> eventListeners = new HashSet<Nvm3ServiceListener>();

    public static void addNvm3ServiceListner(Nvm3ServiceListener listener) {
        eventListeners.add(listener);
    }

    public static void removeNvm3ServiceListner(Nvm3ServiceListener listener) {
        eventListeners.remove(listener);
    }

    public static void handleEvent(int id, Object param) {
        System.err.println("Event:" + id + " Param:" + param);
        Nvm3ServiceEvent ev = new Nvm3ServiceEvent(id, param);
        Iterator<Nvm3ServiceListener> it = eventListeners.iterator();
        while (it.hasNext()) {
            Nvm3ServiceListener listener = it.next();
            listener.handleServiceEvent(ev);
        }
    }

    public static Object invokeTreeManager(String method, Object... args) throws Exception {
    	return invokeTreeManagerMethod.invoke(requestBrokerMirror.getObject(), method, args);
    }

    public static Object invokeITreeItem(Object item, String method, Object... args)
        throws Exception {
    	return invokeITreeItemMethod.invoke(requestBrokerMirror.getObject(), item, method, args);
    }

    public static void registerRequestBroker(Object requestBroker) {
        System.err.println("Registered!");
        requestBrokerMirror = new Mirror(requestBroker);
        invokeNavigatorMethod = requestBrokerMirror.getMethod("invokeNavigator");
        invokeTreeManagerMethod = requestBrokerMirror.getMethod("invokeTreeManager");
        invokeITreeItemMethod = requestBrokerMirror.getMethod("invokeITreeItem");
    }
}
