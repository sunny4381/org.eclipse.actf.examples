/*******************************************************************************
 * Copyright (c) 2011, 2012 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.ai.internal.ui.scripteditor;

import java.net.URI;

import org.eclipse.actf.ai.scripteditor.util.TempFileUtil;

public class FileInfoStore {

	// TODO move to XMLFileSaveUtil

	private static URI savePathVolLvl = null;

	public static void setVolumeLevelFilePath(String fpath) {
		// Update URI value
		savePathVolLvl = null;
		if (fpath != null) {
			savePathVolLvl = TempFileUtil.getResource(fpath);
		}
	}

	public static URI getVolumeLevelFilePath() {
		return savePathVolLvl;
	}

}
