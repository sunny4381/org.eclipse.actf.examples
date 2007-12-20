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

public interface Nvm3TableCell {
    int getColumn();
    String getColumnHeader();
    Nvm3Item getFirstItem();
    String getInfoString();
    int getRow();
    String getRowHeader();
    boolean isConnectedWithLeftCell(); 
    boolean isConnectedWithUpCell();
    boolean isHeader();
}
