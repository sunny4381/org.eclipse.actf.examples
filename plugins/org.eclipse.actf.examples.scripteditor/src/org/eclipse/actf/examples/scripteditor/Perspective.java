/*******************************************************************************
 * Copyright (c) 2009, 2011 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.examples.scripteditor;

import org.eclipse.actf.ai.ui.scripteditor.views.EditPanelView;
import org.eclipse.actf.ai.ui.scripteditor.views.GuideListView;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {
	public static String ID = "org.eclipse.actf.examples.scripteditor.perspective";

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(true);

		// Create each ViewPart
		layout.addStandaloneView(EditPanelView.VIEW_ID, false,
				IPageLayout.BOTTOM, 0.8f, editorArea);
		layout.addStandaloneView(GuideListView.VIEW_ID, true,
				IPageLayout.RIGHT, 0.63f, editorArea);
		layout.addStandaloneView(TimeLineView.VIEW_ID, true,
				IPageLayout.BOTTOM, 0.65f, editorArea);
	}
}
