/*******************************************************************************
 * Copyright (c) 2009, 2012 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.examples.scripteditor;

import org.eclipse.actf.ai.ui.scripteditor.views.GuideListView;
import org.eclipse.actf.ai.ui.scripteditor.views.TimeLineView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective2 implements IPerspectiveFactory {

	public static String ID = "org.eclipse.actf.examples.scripteditor.perspective2";

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);

		layout.addStandaloneView(GuideListView.VIEW_ID, true,
				IPageLayout.BOTTOM, 0.5f, editorArea);

		layout.addStandaloneView(TimeLineView.VIEW_ID, true, IPageLayout.RIGHT,
				0.55f, editorArea);

	}
}
