/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.examples.adesigner.eval.odf;

import java.io.InputStream;
import java.util.ResourceBundle;

import org.eclipse.actf.visualization.eval.ICheckerInfoProvider;

public class OdfCheckerInfoProvider implements ICheckerInfoProvider {
	private static final String BUNDLE_NAME = "org.eclipse.actf.examples.adesigner.eval.odf.resources.description"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	public InputStream[] getCheckItemInputStreams() {
		InputStream is = OdfChecker.class
				.getResourceAsStream("resources/ODFcheckitem.xml");
		return new InputStream[] { is };
	}

	public InputStream[] getGuidelineInputStreams() {
		InputStream is = OdfChecker.class
				.getResourceAsStream("resources/ODFGuide.xml");
		return new InputStream[] { is };
	}

	public ResourceBundle getDescriptionRB() {
		return RESOURCE_BUNDLE;
	}

}
