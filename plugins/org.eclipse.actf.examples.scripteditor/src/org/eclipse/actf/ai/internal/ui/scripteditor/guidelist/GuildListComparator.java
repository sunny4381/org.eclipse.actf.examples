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
package org.eclipse.actf.ai.internal.ui.scripteditor.guidelist;

import org.eclipse.actf.ai.scripteditor.data.IScriptData;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public class GuildListComparator extends ViewerComparator {
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (viewer == null || (viewer instanceof TreeViewer)) {
			if (e1 instanceof IScriptData && e2 instanceof IScriptData) {
				if (e1.equals(e2)) {
					return 0;
				}

				IScriptData o1 = (IScriptData) e1;
				IScriptData o2 = (IScriptData) e1;

				int v1 = o1.getStartTime();
				int v2 = o2.getStartTime();

				if (v1 < v2) {
					return -1;
				} else if (v1 > v2) {
					return 1;
				}

				v1 = o1.isWavEnabled() ? o1.getWavEndTime() : o1.getEndTime();
				v2 = o2.isWavEnabled() ? o2.getWavEndTime() : o2.getEndTime();
				if (v1 < v2) {
					return -1;
				} else if (v1 > v2) {
					return 1;
				}

				String st1 = o1.getDescription();
				String st2 = o2.getDescription();
				int result = st1.compareTo(st2);
				if (result != 0) {
					return result;
				}

				// TODO

				return 0;
			}
		}
		return 0;
	}

}
