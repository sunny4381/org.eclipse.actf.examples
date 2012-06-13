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
package org.eclipse.actf.ai.scripteditor.util;

import java.io.File;
import java.net.URI;

public class TempFileUtil {

	/**
	 * Getter method : Get resource URL string
	 */
	static public URI getResource(String fpath) {
		URI result = null;

		// exchange type from String to URI
		fpath = fpath.replace("file:/", "");
		File fh = new File(fpath);
		result = fh.toURI();

		// return result
		return (result);
	}

}
