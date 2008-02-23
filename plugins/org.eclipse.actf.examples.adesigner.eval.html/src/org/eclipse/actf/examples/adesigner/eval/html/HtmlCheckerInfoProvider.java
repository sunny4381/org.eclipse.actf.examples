/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.examples.adesigner.eval.html;

import java.io.InputStream;
import java.util.ResourceBundle;

import org.eclipse.actf.visualization.eval.ICheckerInfoProvider;

public class HtmlCheckerInfoProvider implements ICheckerInfoProvider {

	private static final String BUNDLE_NAME = "org.eclipse.actf.examples.adesigner.eval.html.resources.description"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	public InputStream[] getCheckItemInputStreams() {
		InputStream is = HtmlCheckerInfoProvider.class
				.getResourceAsStream("resources/checkitem.xml");
		if (null != is) {
			return new InputStream[] { is };
		}
		return new InputStream[0];
	}

	public ResourceBundle getDescriptionRB() {
		return RESOURCE_BUNDLE;
	}

	public InputStream[] getGuidelineInputStreams() {
		return new InputStream[0];
	}

}
