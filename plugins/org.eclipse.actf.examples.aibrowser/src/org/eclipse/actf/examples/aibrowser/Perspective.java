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
package org.eclipse.actf.examples.aibrowser;

import org.eclipse.actf.ai.navigator.views.NavigatorTreeView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


public class Perspective implements IPerspectiveFactory {

    public static final String ID = "org.eclipse.actf.examples.aibrowser.DefaultPerspective";

    public void createInitialLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible(true);
        layout.addStandaloneView(NavigatorTreeView.ID, true, IPageLayout.LEFT, 0.2f, editorArea);
        layout.getViewLayout(NavigatorTreeView.ID).setCloseable(false);
        // layout.addStandaloneView(WebBrowserView.ID, true, IPageLayout.LEFT, 0.7f, editorArea);
        // layout.getViewLayout(WebBrowserView.ID).setCloseable(false);
        // layout.addStandaloneView(SampleView.ID, true, IPageLayout.LEFT, 0.2f, editorArea);
        // layout.addStandaloneView(PanelUIView.ID, true, IPageLayout.BOTTOM, 0.7f, SampleView.ID);
        
    }
}
