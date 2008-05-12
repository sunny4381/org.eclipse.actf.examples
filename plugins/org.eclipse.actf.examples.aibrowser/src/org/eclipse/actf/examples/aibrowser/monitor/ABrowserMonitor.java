/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.examples.aibrowser.monitor;

import org.eclipse.actf.model.flash.proxy.ProxyPlugin;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.eclipse.actf.util.win32.COPYDATASTRUCT;
import org.eclipse.actf.util.win32.WMCMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;


/**
 *
 *
 */
public class ABrowserMonitor implements IStartup {

	private static final IWorkbench workbench = PlatformUI.getWorkbench();
	private static final String CACHE = "cache:"; //$NON-NLS-1$
	private static final String TEST = "test:"; //$NON-NLS-1$
	private static final String OK = "ok:"; //$NON-NLS-1$
	private static final String CACHE_BACKGROUND= "background"; //$NON-NLS-1$
	private static final String CACHE_SILENT = "silent"; //$NON-NLS-1$
	private static final String CACHE_CLEAR = "clear"; //$NON-NLS-1$


	public void earlyStartup() {
		final Display display = Display.getDefault(); 
        display.asyncExec(new Runnable(){
            public void run() {
                new WMCMonitor() {
					@Override
                    public int onCopyData(final int hwndTo, final int hwndFrom, COPYDATASTRUCT cds) {
						final String strData = cds.getStringData();
						display.asyncExec(new Runnable(){
							public void run() {
								if( strData.startsWith(CACHE) ) {
									clearCache(strData.substring(CACHE.length())); //$NON-NLS-1$
								}
								else if( strData.startsWith(TEST) ) {
									// NOP
								}
								else {
									if( needCacheClear() ) {
										clearCache(CACHE_CLEAR);
									}
						            openURL(strData);
								}
								if( 0 != hwndFrom ) {
									// Echo back for receipt
									COPYDATASTRUCT replyData = new COPYDATASTRUCT(0,OK+strData);
									replyData.sendMessage(hwndFrom, hwndTo);
								}
							}
						});
			            return 1;
					}
                };
            }
        });
	}

	/*
	 * Open web page on the embedded browser 
	 */
	private void openURL(String url) {
        ModelServiceUtils.launch(url);
	}
	
	/*
	 * Clear internet cache
	 */
	private void clearCache(String mode) {
		boolean background = CACHE_BACKGROUND.equals(mode);
		boolean silent = CACHE_SILENT.equals(mode);
		IWorkbenchWindow window = (silent||background) ? null : workbench.getActiveWorkbenchWindow();
		ProxyPlugin.getDefault().clearCache(background,window);
	}
	
	/*
	 * Check if current perspective need cache clear 
	 */
	private boolean needCacheClear() {
		return true;
	}
}
