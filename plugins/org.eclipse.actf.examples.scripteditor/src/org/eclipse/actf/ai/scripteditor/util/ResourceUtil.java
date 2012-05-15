/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.ai.scripteditor.util;

import org.eclipse.actf.examples.scripteditor.Activator;
import org.eclipse.actf.util.FileUtils;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class ResourceUtil {

	/**
	 * Save resource files into target path
	 * 
	 * @param path
	 *            target path
	 */
	@SuppressWarnings("nls")
	public static void saveResources(String path) {
		Bundle bundleBlind = Platform.getBundle(Activator.PLUGIN_ID);

		FileUtils.copyFile(bundleBlind, new Path(
				"resources/scripts/vd-compat-ie.js"), path + "vd-compat-ie.js",
				true);
		FileUtils.copyFile(bundleBlind, new Path(
				"resources/scripts/vd-compat.js"), path + "vd-compat.js", true);
		FileUtils.copyFile(bundleBlind, new Path(
				"resources/scripts/vd-player-en.js"), path + "vd-player-en.js",
				true);
		FileUtils.copyFile(bundleBlind, new Path(
				"resources/scripts/vd-player-ja.js"), path + "vd-player-ja.js",
				true);

		FileUtils.copyFile(bundleBlind, new Path(
				"resources/css/vd-player-ie.css"), path + "vd-player-ie.css",
				true);

		FileUtils.copyFile(bundleBlind, new Path(
				"resources/css/vd-player.css"), path + "vd-player.css",
				true);

	}
}
