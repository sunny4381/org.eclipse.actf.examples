/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.examples.adesigner.ui.perspectives;

import org.eclipse.actf.model.flash.proxy.ProxyPlugin;
import org.eclipse.actf.visualization.gui.IGuiViewIDs;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;


public class FlashPerspective implements IPerspectiveFactory {

    public static final String ID = FlashPerspective.class.getName();

    public void createInitialLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible(true);

        IFolderLayout reportFolder = layout.createFolder("adesigner.flash.report.folder",
                IPageLayout.BOTTOM, 0.7f, editorArea);
        IFolderLayout rightReportFolder = layout.createFolder(
                "adesigner.flash.report.left.folder", IPageLayout.RIGHT, 0.5f,
                "adesigner.flash.report.folder");
        IFolderLayout simulatorFolder = layout.createFolder("adesigner.flash.simulator.folder",
                IPageLayout.RIGHT, 0.5f, editorArea);
        IFolderLayout outlineFolder = layout.createFolder("adesigner.flash.outline.folder",
                IPageLayout.RIGHT, 0.5f, "adesigner.flash.simulator.folder");
        IFolderLayout flashDomFolder = layout.createFolder("adesigner.flash.flashdom.folder",
                IPageLayout.BOTTOM, 0.5f, "adesigner.flash.outline.folder");

        reportFolder.addView(IGuiViewIDs.ID_EVENTVIEW);
        rightReportFolder.addView(IGuiViewIDs.ID_PROPERTIESVIEW);
        rightReportFolder.addView(IGuiViewIDs.ID_REPORTVIEW);
        rightReportFolder.addView(IGuiViewIDs.ID_SIBLINGSVIEW);
        simulatorFolder.addView(IGuiViewIDs.ID_SUMMARYVIEW);
        outlineFolder.addView(IGuiViewIDs.ID_OUTLINEVIEW);
        flashDomFolder.addView(IGuiViewIDs.ID_FLASHDOMVIEW);

        layout.getViewLayout(IGuiViewIDs.ID_REPORTVIEW).setCloseable(false);
        layout.getViewLayout(IGuiViewIDs.ID_SIBLINGSVIEW).setCloseable(false);
        layout.getViewLayout(IGuiViewIDs.ID_PROPERTIESVIEW).setCloseable(false);
        layout.getViewLayout(IGuiViewIDs.ID_EVENTVIEW).setCloseable(false);
        layout.getViewLayout(IGuiViewIDs.ID_SUMMARYVIEW).setCloseable(false);
        layout.getViewLayout(IGuiViewIDs.ID_OUTLINEVIEW).setCloseable(false);
        layout.getViewLayout(IGuiViewIDs.ID_FLASHDOMVIEW).setCloseable(false);

        PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(
                new PerspectiveListenerForBrowserLaunch(ID));
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(new IPerspectiveListener() {
            public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
                if (ID.equals(perspective.getId())) {
                    // tentative code for avoiding isWorkbenchRunning()'s bug
                    if (((Workbench) (PlatformUI.getWorkbench())).isStarting()) {
                        //do nothing
                    } else {
                        ProxyPlugin.getDefault().checkCache();
                    }
                }
            }

            public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
            }
        });

    }
}
