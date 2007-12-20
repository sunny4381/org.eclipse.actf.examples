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

public interface Nvm3Item {
    void focus();

    int getChildItemCount();

    Nvm3Item[] getChildItems();

    String getContentString();

    String getDebugString(); 

    String getDescription();

    // int getLevel();

    String getLinkURI();

    int getNavIndex();

    Nvm3Item getParent();

    int getPosition();

    Object getProperty(String name);

    String[] getPropertyList(); 

    String[] getStillPictureData();

    boolean isClickable();

    boolean isSelection();

    boolean isStillPicture();

    boolean isTable();

    boolean isInTable();

    boolean isTextInputable();
}
