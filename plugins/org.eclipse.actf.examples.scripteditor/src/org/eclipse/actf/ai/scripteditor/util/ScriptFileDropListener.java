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

import org.eclipse.actf.ai.internal.ui.scripteditor.PreviewPanel;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;

public class ScriptFileDropListener extends DropTargetAdapter {

	// drag start event
	public void dragEnter(DropTargetEvent e) {
		e.detail = DND.DROP_COPY;
	}

	// drop to target event
	public void drop(DropTargetEvent e) {
		// Check current movie status
		if (PreviewPanel.getInstance().getCurrentStatusMedia()) {
			String[] files = (String[]) e.data;
			int mode = -1;
			try {
				if (files.length > 0) {
					// Check file
					int idxXML = files[0].indexOf(".xml");
					int idxCSV = files[0].indexOf(".csv");
					if (idxXML >= 0) {
						// Load XML file
						mode = IUNIT.LD_FTYPE_XML;
					} else if (idxCSV >= 0) {
						// Load CSV file
						mode = IUNIT.LD_FTYPE_CSV;
					}

					// Launch file reader
					if (mode >= IUNIT.LD_FTYPE_XML) {
						// Create file reader
						DragDropFileReader reader = new DragDropFileReader(
								mode, files[0]);
						// Load data
						reader.load();
						// post process
						reader = null;
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			// Display notice message box
			XMLFileMessageBox noticeMB = new XMLFileMessageBox(
					XMLFileMessageBox.MB_STYLE_ACCESS_DENIED, null);
			noticeMB.open();
		}
	}
}
