/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Norimasa HAYASHIDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.examples.adesigner.ui.perspectives;

import org.eclipse.actf.visualization.gui.IGuiViewIDs;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;



public class GUIPerspective implements IPerspectiveFactory {

    public static final String ID = GUIPerspective.class.getName();
    
    public void createInitialLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible(false);
        
        IFolderLayout reportFolder = layout.createFolder("adesigner.report.folder", IPageLayout.BOTTOM,  0.7f, editorArea);
        IFolderLayout rightReportFolder = layout.createFolder("adesigner.report.left.folder", IPageLayout.RIGHT,  0.5f, "adesigner.report.folder");
        IFolderLayout jawsFolder = layout.createFolder("adesigner.jaws.folder", IPageLayout.LEFT, 1/3f, editorArea);
        IFolderLayout outlineFolder = layout.createFolder("adesigner.outline.folder", IPageLayout.RIGHT, 1f, editorArea);        
        IFolderLayout propertyFolder = layout.createFolder("adesigner.property.folder", IPageLayout.RIGHT,  0.5f, "adesigner.outline.folder");
        try {
            reportFolder.addView(IGuiViewIDs.ID_EVENTVIEW);
            rightReportFolder.addView(IGuiViewIDs.ID_REPORTVIEW);
            rightReportFolder.addView(IGuiViewIDs.ID_SIBLINGSVIEW);
            outlineFolder.addView(IGuiViewIDs.ID_OUTLINEVIEW);
            jawsFolder.addView(IGuiViewIDs.ID_SUMMARYVIEW);
            propertyFolder.addView(IGuiViewIDs.ID_PROPERTIESVIEW);
            layout.getViewLayout(IGuiViewIDs.ID_EVENTVIEW).setCloseable(false);
            layout.getViewLayout(IGuiViewIDs.ID_REPORTVIEW).setCloseable(false);
            layout.getViewLayout(IGuiViewIDs.ID_SIBLINGSVIEW).setCloseable(false);
            layout.getViewLayout(IGuiViewIDs.ID_OUTLINEVIEW).setCloseable(false);
            layout.getViewLayout(IGuiViewIDs.ID_SUMMARYVIEW).setCloseable(false);
            layout.getViewLayout(IGuiViewIDs.ID_PROPERTIESVIEW).setCloseable(false);
        }
        catch( Exception e ) {
        }        
    }
}
