/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.examples.adesigner.extension;

import org.eclipse.actf.model.ui.IModelService;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.eclipse.actf.visualization.gui.common.TargetWindowDataProvider;

public class ADesignerTargetWindowDataProvider extends TargetWindowDataProvider {

	public IModelService getActiveModelService() {
		return ModelServiceUtils.getActiveModelService();
	}

}
