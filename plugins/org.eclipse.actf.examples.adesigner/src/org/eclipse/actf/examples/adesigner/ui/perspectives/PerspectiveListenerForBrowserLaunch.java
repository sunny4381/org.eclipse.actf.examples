/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.examples.adesigner.ui.perspectives;

import org.eclipse.actf.model.ui.editors.ie.editor.WebBrowserEditor;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

public class PerspectiveListenerForBrowserLaunch implements
		IPerspectiveListener {

	private String id;

	public PerspectiveListenerForBrowserLaunch(String id) {
		this.id = id;
	}

	public void perspectiveActivated(IWorkbenchPage page,
			IPerspectiveDescriptor perspective) {
		if (id.equals(perspective.getId())) {
			if (!ModelServiceUtils.activateEditorPart(WebBrowserEditor.ID)) {
				ModelServiceUtils
						.launch(
								PlatformUI
										.getWorkbench()
										.getHelpSystem()
										.resolve(
												"/org.eclipse.actf.examples.adesigner.doc/docs/index.html",
												true).toString(),
								WebBrowserEditor.ID);
			}
		}
	}

	public void perspectiveChanged(IWorkbenchPage page,
			IPerspectiveDescriptor perspective, String changeId) {
	}

}
