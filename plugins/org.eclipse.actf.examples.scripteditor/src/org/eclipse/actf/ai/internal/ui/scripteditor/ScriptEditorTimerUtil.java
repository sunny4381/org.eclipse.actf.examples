/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.ai.internal.ui.scripteditor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.actf.ai.internal.ui.scripteditor.event.EventManager;
import org.eclipse.actf.ai.internal.ui.scripteditor.event.TimerEvent;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.eclipse.actf.ui.util.PlatformUIUtil;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class ScriptEditorTimerUtil implements Runnable, IUNIT{

	private static ScriptEditorTimerUtil ownInst = null;

	// private SynchronizeTimeLineTimer instTimerSynchronizeTimeLine = null;
	private static ScheduledExecutorService schedulerSynchronizeTimeLine = null;
	private static ScheduledFuture<?> futureSynchronizeTimeLine = null;

	// for Event Managing
	private static EventManager eventManager = null;

	public static final int TL_SYNC_TIME_BASE = 10;
	
	private static Display display = null;

	/**
	 * @category Get instance of SynchronizeTimeLineTimer
	 * @return instance
	 */
	public static synchronized ScriptEditorTimerUtil getInstance() {
		if (display == null) {
			display = PlatformUI.getWorkbench().getDisplay();
		}
		if (ownInst == null && display != null) {
			ownInst = new ScriptEditorTimerUtil();
			eventManager = EventManager.getInstance();

			// Initial setup (Timer Task for sampling volume level data)
			schedulerSynchronizeTimeLine = Executors
					.newSingleThreadScheduledExecutor();
			// Start Timer Task
			futureSynchronizeTimeLine = schedulerSynchronizeTimeLine
					.scheduleAtFixedRate(ownInst, 0, TL_SYNC_TIME_BASE,
							TimeUnit.MILLISECONDS);

			return ownInst;
		}
		return ownInst;
	}
	
	private ScriptEditorTimerUtil() {
		PlatformUIUtil.getActiveWindow().getShell()
		.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				dispose();
			}
		});
	}
	
	@Override
	public void run() {
		try {
			// Synchronize TimeLine
			display.asyncExec(new Runnable() {
				public void run() {
					// run() was executed few times after cancel timer,
					// therefore eventManager object must be checked
					if (eventManager != null) {
						eventManager.fireTimerEvent(new TimerEvent(this));
					}
				}
			});

		} catch (Exception e) {
			System.out.println("SynchronizeTimeLineTimer::run() : Exception = "
					+ e);
		}
	}

	/**
	 * dispose timer
	 */
	private void dispose() {
		//TODO
		if (futureSynchronizeTimeLine != null) {
			// Destroy Timer Task & Scheduler
			futureSynchronizeTimeLine.cancel(true);
			schedulerSynchronizeTimeLine.shutdownNow();
			// Request Garbage Collection
			futureSynchronizeTimeLine = null;
		}
		eventManager = null;
		ownInst = null;
	}

}
