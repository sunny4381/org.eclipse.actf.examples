/*******************************************************************************
 * Copyright (c) 2006, 2012 IBM Corporation, Middle East Technical University
 * Northern Cyprus Campus and Others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA (IBM) - initial API and implementation
 *    Elgin Akpinar (METU) - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.examples.emine;

import java.io.File;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.actf.ui.util.AbstractUIPluginACTF;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class EminePlugin extends AbstractUIPluginACTF {
	public static final String PLUGIN_ID = "org.eclipse.actf.examples.emine"; //$NON-NLS-1$

	private static EminePlugin plugin;

	private static String perspectiveID = null;

	private ResourceBundle _resourceBundle;

	public EminePlugin() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	public static EminePlugin getDefault() {
		return plugin;
	}

	public static String getResourceString(String key) {
		ResourceBundle bundle = EminePlugin.getDefault()
				.getResourceBundle();
		try {
			return (null != bundle) ? bundle.getString(key) : key;
		} catch (MissingResourceException mre) {
			return ""; //$NON-NLS-1$
		}
	}

	public ResourceBundle getResourceBundle() {
		if (null == _resourceBundle) {
			Bundle bundle = getBundle();
			if (null != bundle) {
				_resourceBundle = Platform.getResourceBundle(bundle);
			}
		}

		return _resourceBundle;
	}

	public static String getPerspectiveID() {
		return perspectiveID;
	}

	public String getTempDirectoryS() {
		if (getTempDirectory() == null) {
			createTempDirectory();
		}
		return getTempDirectory().getAbsolutePath() + File.separator;
	}

}
