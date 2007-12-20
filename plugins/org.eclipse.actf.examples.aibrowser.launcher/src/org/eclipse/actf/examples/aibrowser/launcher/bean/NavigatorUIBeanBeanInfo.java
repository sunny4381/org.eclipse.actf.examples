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

import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.SimpleBeanInfo;

import org.eclipse.actf.examples.aibrowser.launcher.nvm3.Nvm3ServiceEvent;
import org.eclipse.actf.examples.aibrowser.launcher.nvm3.Nvm3ServiceListener;


public class NavigatorUIBeanBeanInfo extends SimpleBeanInfo {
    private final static Class sourceClass = NavigatorUIBean.class;
    private final static Class listenerClass = Nvm3ServiceListener.class;

    public EventSetDescriptor[] getEventSetDescriptors() {
        try {
            EventSetDescriptor esd;
            esd = new EventSetDescriptor("browserUIService",
                                         listenerClass,
                                         new MethodDescriptor[] {
                                             new MethodDescriptor(listenerClass.getMethod("handleServiceEvent", new Class[] { Nvm3ServiceEvent.class }))},
                                         sourceClass.getMethod("addNvm3ServiceListner", new Class[] { Nvm3ServiceListener.class }),
                                         sourceClass.getMethod("removeNvm3ServiceListner", new Class[] { Nvm3ServiceListener.class }));

            EventSetDescriptor[] esds = { esd };
            return esds;

        } catch (IntrospectionException ex) {
            throw new Error(ex.toString());
        } catch (NoSuchMethodException ex) {
            throw new Error(ex.toString());
        }
    }
	
}
