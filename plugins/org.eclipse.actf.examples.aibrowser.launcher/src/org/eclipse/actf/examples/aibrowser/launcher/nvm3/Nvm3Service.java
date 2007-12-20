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

public interface Nvm3Service {

    int KEY_DTV_BACK = 101;
    int KEY_DTV_BLUE = 102;
    int KEY_DTV_DATA = 103;
    int KEY_DTV_GREEN = 104;
    int KEY_DTV_RED = 105;
    int KEY_DTV_YELLOW = 106;

    int MODE_SELECT = 1;
    int MODE_TABLE = 2;
    int MODE_TEXTINPUT = 3;
    int MODE_TREE = 4;
    int MODE_UNSPECIFIED = 5;

    int STATUS_NOACTION = 0;
    int STATUS_MOVED = 1 << 0;
    int STATUS_LEVEL_CHANGED = 1 << 1;
    int STATUS_TRANSFERRED = 1 << 2;
    int STATUS_CLICKED = 1 << 3;
    int STATUS_CHANGED = 1 << 4;
    int STATUS_PARENT_CHANGED = 1 << 5;
    int STATUS_FOUND = 1 << 6;
    int STATUS_UNDONE = 1 << 8;
    int STATUS_ERROR = 1 << 16;

    void cancel(); // Deprecated

    int click();

    int enterNavigationMode(int mode);
            
    void functionKeyType(int keyId);

    Nvm3Item getActiveItem();

    Nvm3Table getActiveTable();

    Nvm3TableCell getActiveTableCell();

    Nvm3Table getCurrentWholeTable();

    Nvm3TableCell[][] getCurrentWholeTableAsArray(); // Deprecated

    Nvm3Item getCurrentWholeTree();

    String getInputText();

    Nvm3KeyDescription[] getKeyDescriptions(); 

    int getLevel(); 

    int getNavigationMode();  // Deprecated.

    Nvm3Item[] getSiblings(); //Deprecated.

    int gotoDownCell();

    int gotoEndOfSiblings();

    int gotoFirstChild();

    int gotoLeftCell();

    int gotoNextSibling();

    int gotoParent();

    int gotoPreviousSibling();

    int gotoRightCell();

    int gotoStartOfSiblings();

    int gotoUpCell();

    void keyType(char keyChar);

    int leaveNavigationMode();

    int moveTo(Nvm3Item dest);

    void setInputText();

    int traverse(boolean forward);
}
