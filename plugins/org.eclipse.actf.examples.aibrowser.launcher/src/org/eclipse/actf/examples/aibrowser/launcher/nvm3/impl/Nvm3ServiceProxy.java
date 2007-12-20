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
import org.eclipse.actf.examples.aibrowser.launcher.nvm3.Nvm3KeyDescription;
import org.eclipse.actf.examples.aibrowser.launcher.nvm3.Nvm3Service;
import org.eclipse.actf.examples.aibrowser.launcher.nvm3.Nvm3Table;
import org.eclipse.actf.examples.aibrowser.launcher.nvm3.Nvm3TableCell;


public class Nvm3ServiceProxy implements Nvm3Service {
    private Nvm3Item pseudoActiveItem;

    public Nvm3ServiceProxy() {
    }

    public void cancel() {
        // Deprecated
    }

    public int click() {
        // pseudoActiveItem = null;
    	try {
            Nvm3Item item = getActiveItem();
            Integer iObj = (Integer) RequestBrokerProxy.invokeITreeItem(((Nvm3ItemProxy) item).getTarget(), "doClick",
                                                                        RequestBrokerProxy.EMPTY_ARG);
            return iObj.intValue();
    	} catch (Exception e) {
            e.printStackTrace();
            return 0;
    	}
    }

    public int enterNavigationMode(int mode) {
        return 0;
    }

    public void functionKeyType(int keyId) {
        // TODO Auto-generated method stub
    }

    public Nvm3Item getActiveItem() {
        if (pseudoActiveItem != null) return pseudoActiveItem;
    	try {
            Object item = RequestBrokerProxy.invokeTreeManager("getActiveItem",
                                                               RequestBrokerProxy.EMPTY_ARG);
            return new Nvm3ItemProxy(item);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }

    public Nvm3Table getActiveTable() {
        // TODO Auto-generated method stub
        return null;
    }

    public Nvm3TableCell getActiveTableCell() {
        // TODO Auto-generated method stub
        return null;
    }

    public Nvm3Table getCurrentWholeTable() {
        // TODO Auto-generated method stub
        return null;
    }

    public Nvm3TableCell[][] getCurrentWholeTableAsArray() {
        // Deprecated
        return null;
    }

    public Nvm3Item getCurrentWholeTree() {
    	try {
            Object item = RequestBrokerProxy.invokeTreeManager("expandWholeTree",
                                                               RequestBrokerProxy.EMPTY_ARG);
            return new Nvm3ItemProxy(item);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }

    public String getInputText() {
        // TODO Auto-generated method stub
        return null;
    }

    public Nvm3KeyDescription[] getKeyDescriptions() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getLevel() {
    	try {
            Integer iObj = (Integer) RequestBrokerProxy.invokeTreeManager("getLevel",
                                                                          RequestBrokerProxy.EMPTY_ARG);
            return iObj.intValue();
    	} catch (Exception e) {
            e.printStackTrace();
            return 0;
    	}
    }

    public int getNavigationMode() {
        // TODO Auto-generated method stub
        return 0;
    }

    public Nvm3Item[] getSiblings() {
        try {
            Object a = RequestBrokerProxy.invokeTreeManager("getSiblings",
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

    public int gotoDownCell() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int gotoEndOfSiblings() {
        pseudoActiveItem = null;
    	try {
            Integer iObj = (Integer) RequestBrokerProxy.invokeTreeManager("gotoEndOfSiblings",
                                                                          RequestBrokerProxy.EMPTY_ARG);
            return iObj.intValue();
    	} catch (Exception e) {
            e.printStackTrace();
            return 0;
    	}
    }

    public int gotoFirstChild() {
        pseudoActiveItem = null;
    	try {
            Integer iObj = (Integer) RequestBrokerProxy.invokeTreeManager("gotoFirstChild",
                                                                          RequestBrokerProxy.EMPTY_ARG);
            return iObj.intValue();
    	} catch (Exception e) {
            e.printStackTrace();
            return 0;
    	}
    }

    public int gotoLeftCell() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int gotoNextSibling() {
        pseudoActiveItem = null;
    	try {
            Integer iObj = (Integer) RequestBrokerProxy.invokeTreeManager("gotoNextSibling",
                                                                          RequestBrokerProxy.EMPTY_ARG);
            return iObj.intValue();
    	} catch (Exception e) {
            e.printStackTrace();
            return 0;
    	}
    }

    public int gotoParent() {
        pseudoActiveItem = null;
    	try {
            Integer iObj = (Integer) RequestBrokerProxy.invokeTreeManager("gotoParent",
                                                                          RequestBrokerProxy.EMPTY_ARG);
            return iObj.intValue();
    	} catch (Exception e) {
            e.printStackTrace();
            return 0;
    	}
    }

    public int gotoPreviousSibling() {
        pseudoActiveItem = null;
    	try {
            Integer iObj = (Integer) RequestBrokerProxy.invokeTreeManager("gotoPreviousSibling",
                                                                          RequestBrokerProxy.EMPTY_ARG);
            return iObj.intValue();
    	} catch (Exception e) {
            e.printStackTrace();
            return 0;
    	}
    }

    public int gotoRightCell() {
        pseudoActiveItem = null;
        // TODO Auto-generated method stub
        return 0;
    }

    public int gotoStartOfSiblings() {
        pseudoActiveItem = null;
        // TODO Auto-generated method stub
        return 0;
    }

    public int gotoUpCell() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void keyType(char keyChar) {
        // TODO Auto-generated method stub

    }

    public int leaveNavigationMode() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int moveTo(Nvm3Item dest) {
        pseudoActiveItem = dest;
        return 0;
    }

    public void setInputText() {
        // TODO Auto-generated method stub

    }

    public int traverse(boolean forward) {
        pseudoActiveItem = null;
    	try {
            Integer iObj = (Integer) RequestBrokerProxy.invokeTreeManager("traverse", forward);
            return iObj.intValue();
    	} catch (Exception e) {
            e.printStackTrace();
            return 0;
    	}
    }

}
