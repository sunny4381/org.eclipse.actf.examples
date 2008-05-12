/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.examples.aibrowser.actions;

import org.eclipse.actf.examples.aibrowser.ClientPlugin;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;




public class OpenHelpAction implements IWorkbenchWindowActionDelegate {

    private IWorkbenchWindow _window;

    public OpenHelpAction() {
    }

    public void run(IAction action) {
        String url = ClientPlugin.getDefault().getHelpFileURI("org.eclipse.actf.examples.aibrowser.doc", "docs/index.html");
        //String url = this._window.getWorkbench().getHelpSystem().resolve(
                //"/org.eclipse.actf.examples.aibrowser.doc/docs/index.html",true).toString();
        ModelServiceUtils.openInExistingEditor(url);
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }

    public void dispose() {
    }

    public void init(IWorkbenchWindow window) {
        this._window = window;
    }
}
