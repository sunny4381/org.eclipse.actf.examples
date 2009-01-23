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

package org.eclipse.actf.examples.adesigner;

import java.io.File;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.actf.ui.util.AbstractUIPluginACTF;
import org.eclipse.actf.util.logging.DebugPrintUtil;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;


public class ADesignerPlugin extends AbstractUIPluginACTF {
	public static final String PLUGIN_ID = "org.eclipse.actf.examples.adesigner";

	private static ADesignerPlugin plugin;

	private static String perspectiveID = null;

	private ResourceBundle _resourceBundle;

	public ADesignerPlugin() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);

		boolean flag = false;
		for (String arg : Platform.getApplicationArgs()) {
			// String tmpS = arg.trim().toLowerCase();
			DebugPrintUtil.devOrDebugPrintln(arg);

			if (arg.equalsIgnoreCase("-perspective")) {
				flag = true;
			} else if (flag) {
				flag = false;
				perspectiveID = arg;
			}

		}
		
//		ParamSystem.setAddLineNumber(IPreferenceConstants.CHECKER_ORG_DOM
//				.equals(getPluginPreferences().getString(
//						IPreferenceConstants.CHECKER_TARGET)));

	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	public static ADesignerPlugin getDefault() {
		return plugin;
	}

	public static String getResourceString(String key) {
		ResourceBundle bundle = ADesignerPlugin.getDefault()
				.getResourceBundle();
		try {
			return (null != bundle) ? bundle.getString(key) : key;
		} catch (MissingResourceException mre) {
			return "???" + key + "???";
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

	// TODO use creteTempFile
	public String getTempDirectoryS() {
		if (getTempDirectory() == null) {
			createTempDirectory();
		}
		return getTempDirectory().getAbsolutePath() + File.separator;
	}

    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path.
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
}
