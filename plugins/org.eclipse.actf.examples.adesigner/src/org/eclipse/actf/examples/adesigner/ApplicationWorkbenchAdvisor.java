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
package org.eclipse.actf.examples.adesigner;

import org.eclipse.actf.examples.adesigner.ui.preferences.IPreferenceConstants;
import org.eclipse.actf.visualization.ui.IVisualizationPerspective;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public String getInitialWindowPerspectiveId() {
		if (ADesignerPlugin.getPerspectiveID() == null) {
			return ADesignerPlugin.getDefault().getPluginPreferences()
					.getString(
							IPreferenceConstants.SELECTED_MODE_PERSPECTIVE_ID);
		} else {
			String pID = ADesignerPlugin.getPerspectiveID();
			if (PlatformUI.getWorkbench().getPerspectiveRegistry()
					.findPerspectiveWithId(pID) != null) {
				return pID;
			}else{
				return IVisualizationPerspective.ID_HTML_PERSPECTIVE;
			}
		}
	}

}
