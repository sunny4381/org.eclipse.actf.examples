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
package org.eclipse.actf.examples.aibrowser.launcher.nvm3.impl;

import java.lang.reflect.Array;

import org.eclipse.actf.examples.aibrowser.launcher.RequestBrokerProxy;
import org.eclipse.actf.examples.aibrowser.launcher.nvm3.Nvm3Item;


public class Nvm3ItemProxy implements Nvm3Item {
    private Object item;

    public Object getTarget() {
        return item;
    }

    Nvm3ItemProxy(Object item) {
        this.item = item;
    }

    public void focus() {
        // TODO Auto-generated method stub
    }

    public int getChildItemCount() {
        try {
            Object a = RequestBrokerProxy.invokeITreeItem(item, "getChildItems",
                                                          RequestBrokerProxy.EMPTY_ARG);
            return  Array.getLength(a);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Nvm3Item[] getChildItems() {
        try {
            Object a = RequestBrokerProxy.invokeITreeItem(item, "getChildItems",
                                                          RequestBrokerProxy.EMPTY_ARG);
            int size = Array.getLength(a);
            Nvm3Item[] r = new Nvm3Item[size];
            for (int i = 0; i < size; i++) {
                Object o = Array.get(a, i);
                r[i] = new Nvm3ItemProxy(o);
            }
            return r;
        } catch (Exception e) {
            e.printStackTrace();
            return new Nvm3ItemProxy[0];
        }
    }

    public String getContentString() {
        try {
            return (String) RequestBrokerProxy.invokeITreeItem(item, "getUIString",
                                                               RequestBrokerProxy.EMPTY_ARG);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getDebugString() {
        try {
            return (String) RequestBrokerProxy.invokeITreeItem(item, "getNodeString",
                                                               RequestBrokerProxy.EMPTY_ARG);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getDescription() {
        try {
            return (String) RequestBrokerProxy.invokeITreeItem(item, "getDescription",
                                                               RequestBrokerProxy.EMPTY_ARG);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getLinkURI() {
        try {
            return (String) RequestBrokerProxy.invokeITreeItem(item, "getLinkURI",
                                                               RequestBrokerProxy.EMPTY_ARG);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public int getNavIndex() {
        // Deprecated.  Always returns 0.
        return 0;
    }

    public Nvm3Item getParent() {
        try {
            Object o = RequestBrokerProxy.invokeITreeItem(item, "getParent",
                                                          RequestBrokerProxy.EMPTY_ARG);
            if (o == null) return null;
            return new Nvm3ItemProxy(o);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getPosition() {
        try {
            Integer iObj = (Integer) RequestBrokerProxy.invokeITreeItem(item, "getNth",
                                                                        RequestBrokerProxy.EMPTY_ARG);
            return iObj.intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Object getProperty(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] getPropertyList() {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] getStillPictureData() {
        try {
            String[] ret = (String[]) RequestBrokerProxy.invokeITreeItem(item, "getStillPictureData",
                                                                          RequestBrokerProxy.EMPTY_ARG);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isClickable() {
        try {
            Boolean bObj = (Boolean) RequestBrokerProxy.invokeITreeItem(item, "isClickable",
                                                                        RequestBrokerProxy.EMPTY_ARG);
            return bObj.booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isInTable() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isSelection() {
        // Deprecated.
        return false;
    }

    public boolean isStillPicture() {
        try {
            Boolean bObj = (Boolean) RequestBrokerProxy.invokeITreeItem(item, "isImage",
                                                                        RequestBrokerProxy.EMPTY_ARG);
            return bObj.booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isTable() {
        return false;
    }

    public boolean isTextInputable() {
        try {
            Boolean bObj = (Boolean) RequestBrokerProxy.invokeITreeItem(item, "isInputable",
                                                                        RequestBrokerProxy.EMPTY_ARG);
            return bObj.booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
