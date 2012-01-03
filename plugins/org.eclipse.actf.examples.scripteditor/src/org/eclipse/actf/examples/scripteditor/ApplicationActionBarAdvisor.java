/*******************************************************************************
 * Copyright (c) 2009, 2010 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.examples.scripteditor;

import org.eclipse.actf.ai.internal.ui.scripteditor.PreviewPanel;
import org.eclipse.actf.model.ui.editor.actions.FavoritesMenu;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Local data
	private static ApplicationActionBarAdvisor ownInst = null;
	private MenuManager fileMenu = null;

	// Action of Cool bar
	// private IWorkbenchAction coolbarAction;
	// Action of Favorites
	private FavoritesMenu favoritesMenu;

	private IWorkbenchAction _preferenceAction;

	private IWorkbenchAction _helpAction;

	private IWorkbenchAction _aboutAction;

	private IWorkbenchAction _quitAction;

	// private IWorkbenchAction _closeAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
		ownInst = this;
	}

	public static ApplicationActionBarAdvisor getInstance() {
		return (ownInst);
	}

	protected void makeActions(IWorkbenchWindow window) {
		favoritesMenu = new FavoritesMenu(window, true);
		/**
		 * introAction = ActionFactory.INTRO.create(window);
		 * register(introAction);
		 **/

		this._preferenceAction = ActionFactory.PREFERENCES.create(window);

		this._helpAction = ActionFactory.HELP_CONTENTS.create(window);

		this._aboutAction = ActionFactory.ABOUT.create(window);

		this._quitAction = ActionFactory.QUIT.create(window);

		// this._closeAction = ActionFactory.CLOSE.create(window);
	}

	protected void fillMenuBar(IMenuManager menuBar) {
		/**
		 * MenuManager helpMenu = new MenuManager("&Help",
		 * IWorkbenchActionConstants.M_HELP); menuBar.add(helpMenu);
		 * 
		 * // Help helpMenu.add(introAction);
		 **/

		// File Menu
		fileMenu = new MenuManager(
				Activator.getResourceString("scripteditor.menu.file"),
				IWorkbenchActionConstants.M_FILE);
		// Add menu item
		fileMenu.add(new Separator("fileOpenGroup"));
		fileMenu.add(new Separator());
		fileMenu.add(new Separator("fileCloseGroup"));
		fileMenu.add(new Separator());
		fileMenu.add(new Separator("fileSaveGroup"));
		fileMenu.add(new Separator());
		fileMenu.add(new Separator("movieGroup"));
		fileMenu.add(new Separator());
		fileMenu.add(new Separator("impExportGroup"));
		fileMenu.add(new Separator());
		fileMenu.add(_quitAction);
		menuBar.add(fileMenu);

		// Multimedia Menu
		MenuManager multimediaMenu = new MenuManager(
				Activator.getResourceString("scripteditor.menu.multimedia"),
				"multimedia");
		// Add menu item
		multimediaMenu.add(new Separator("mediaMovieGroup"));
		multimediaMenu.add(new Separator());
		multimediaMenu.add(new Separator("mediaAudioGroup"));
		multimediaMenu.add(new Separator());
		multimediaMenu.add(new Separator("mediaAudioCaptureGroup"));
		multimediaMenu.add(new Separator());
		menuBar.add(multimediaMenu);
		// Add sub menu item
		MenuManager captureModeSubMenu = new MenuManager(
				Activator
						.getResourceString("scripteditor.menu.multimedia.capturemode"),
				"capturemode");
		captureModeSubMenu.add(new Separator("captureAudioModeGroup"));
		captureModeSubMenu.add(new Separator());
		captureModeSubMenu.add(new Separator("captureAudioClearGroup"));
		captureModeSubMenu.add(new Separator());
		multimediaMenu.add(captureModeSubMenu);

		/* Favorite Menu */
		menuBar.add(favoritesMenu);

		// Window Menu
		MenuManager windowMenu = new MenuManager(
				Activator.getResourceString("scripteditor.menu.window"),
				IWorkbenchActionConstants.M_WINDOW);
		windowMenu.add(new Separator(IWorkbenchActionConstants.NAV_START));
		windowMenu.add(new Separator(IWorkbenchActionConstants.NAV_END));
		windowMenu.add(new Separator());
		windowMenu.add(_preferenceAction);
		menuBar.add(windowMenu);

		// Help Menu
		MenuManager helpMenu = new MenuManager(
				Activator.getResourceString("scripteditor.menu.help"),
				IWorkbenchActionConstants.M_HELP);
		helpMenu.add(new Separator(IWorkbenchActionConstants.HELP_START));
		helpMenu.add(_helpAction);
		helpMenu.add(new Separator(IWorkbenchActionConstants.HELP_END));
		helpMenu.add(new Separator());
		helpMenu.add(_aboutAction);
		menuBar.add(helpMenu);

		register(_preferenceAction);
		register(_helpAction);
		register(_aboutAction);
		register(_quitAction);
		// register(_closeAction);
	}

	/**
	 * SetUP FileMenu listener
	 */
	public void setFileMenuListener() {
		// SetUP MenuListener for file menu
		fileMenu.getMenu().addMenuListener(new FileMenuAdapter());
	}

	/**
	 * Local class : FileMenu Listener
	 * 
	 */
	private class FileMenuAdapter extends MenuAdapter {
		public void menuShown(MenuEvent e) {
			boolean newStat = PreviewPanel.getInstance()
					.getCurrentStatusMedia();
			Menu menu = (Menu) e.getSource();
			MenuItem[] items = menu.getItems();
			for (int i = 0; i < items.length; i++) {
				// Check index of file menu items
				String str = items[i].getText();
				if (str != "") {
					if (!Activator.getResourceString(
							"scripteditor.action.reload").equals(str)
							&& !Activator.getResourceString(
									"scripteditor.action.exit").equals(str)) {

						// SetUP new status to current item
						items[i].setEnabled(newStat);
					}
				}
			}
		}
	}

	@Override
	public void fillStatusLine(IStatusLineManager statusLine) {
		super.fillStatusLine(statusLine);
	}

	@Override
	protected void fillCoolBar(ICoolBarManager coolBar) {
		super.fillCoolBar(coolBar);
	}

}
