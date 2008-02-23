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

import org.eclipse.actf.model.ModelServiceUtils;
import org.eclipse.actf.model.ui.editors.ooo.editor.OOoEditor;
import org.eclipse.actf.model.ui.editors.ooo.initializer.util.OOoEditorInitUtil;
import org.eclipse.actf.visualization.blind.ui.views.BlindView;
import org.eclipse.actf.visualization.lowvision.ui.views.LowVisionView;
import org.eclipse.actf.visualization.presentation.ui.views.RoomView;
import org.eclipse.actf.visualization.ui.report.views.DetailedReportView;
import org.eclipse.actf.visualization.ui.report.views.SummaryReportView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

public class ODFPerspective implements IPerspectiveFactory {

	public static final String ID = ODFPerspective.class.getName();

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);

		IFolderLayout reportFolder = layout.createFolder(
				"adesigner.odf.report.folder", IPageLayout.BOTTOM, 0.7f,
				editorArea);
		reportFolder.addView(SummaryReportView.ID);
		reportFolder.addView(DetailedReportView.ID);
		layout.getViewLayout(SummaryReportView.ID).setCloseable(false);
		layout.getViewLayout(DetailedReportView.ID).setCloseable(false);

		IFolderLayout simulatorFolder = layout.createFolder(
				"adesigner.odf.simulator.folder", IPageLayout.RIGHT, 0.5f,
				editorArea);
		simulatorFolder.addView(BlindView.ID);
		simulatorFolder.addView(LowVisionView.ID);
		layout.getViewLayout(BlindView.ID).setCloseable(false);
		layout.getViewLayout(LowVisionView.ID).setCloseable(false);
		// Add Room Simulator view
		simulatorFolder.addView(RoomView.ID);
		layout.getViewLayout(RoomView.ID).setCloseable(false);

		PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.addPerspectiveListener(new IPerspectiveListener() {
					public void perspectiveActivated(IWorkbenchPage page,
							IPerspectiveDescriptor perspective) {
						if (ID.equals(perspective.getId())) {
							if (OOoEditorInitUtil.isOOoInstalled(true)) {
								ModelServiceUtils.launch(null, OOoEditor.ID);
							}
						}
					}

					public void perspectiveChanged(IWorkbenchPage page,
							IPerspectiveDescriptor perspective, String changeId) {
					}
				});
	}
}
