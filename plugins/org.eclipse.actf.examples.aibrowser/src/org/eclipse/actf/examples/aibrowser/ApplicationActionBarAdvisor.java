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
package org.eclipse.actf.examples.aibrowser;

import org.eclipse.actf.ai.navigator.NavigatorPlugin;
import org.eclipse.actf.ai.navigator.ui.ModeContribution;
import org.eclipse.actf.model.ui.editor.actions.FavoritesMenu;
import org.eclipse.actf.util.ui.ProgressContribution;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;


public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    private FavoritesMenu _favoritesMenu;
    
    //private IWorkbenchAction _closeAction;
    
    //private IWorkbenchAction _closeAllAction;

    private IWorkbenchAction _quitAction;

    private IWorkbenchAction _preferencesAction;

    private IWorkbenchAction _aboutAction;

    @Override
    protected void makeActions(IWorkbenchWindow window) {
        this._favoritesMenu = new FavoritesMenu(window, true);
        
        //this._closeAction = ActionFactory.CLOSE.create(window);
        
        //this._closeAllAction = ActionFactory.CLOSE_ALL.create(window);

        this._quitAction = ActionFactory.QUIT.create(window);

        this._preferencesAction = ActionFactory.PREFERENCES.create(window);

        this._aboutAction = ActionFactory.ABOUT.create(window);
        
    }

    @Override
    protected void fillMenuBar(IMenuManager menuBar) {
        super.fillMenuBar(menuBar);

        /* File Menu */
        MenuManager fileMenu = new MenuManager(ClientPlugin.getResourceString("Menu.&File"),
                IWorkbenchActionConstants.M_FILE);
        fileMenu.add(new Separator("fileGroup"));
        fileMenu.add(new Separator());
        fileMenu.add(new Separator("closeGroup"));
        //fileMenu.add(_closeAction);
        //fileMenu.add(_closeAllAction);
        fileMenu.add(new Separator());
        fileMenu.add(new Separator("importGroup"));
        fileMenu.add(new Separator());
        fileMenu.add(new Separator("cacheGroup"));
        fileMenu.add(new Separator());
        fileMenu.add(new Separator("exitGroup"));
        fileMenu.add(_quitAction);
        menuBar.add(fileMenu);

        /* Favorite Menu */
        menuBar.add(this._favoritesMenu);

        /* Navigate Menu */
        MenuManager navMenu = new MenuManager(ClientPlugin.getResourceString("Menu.&Navigate"),
                IWorkbenchActionConstants.M_NAVIGATE);
        navMenu.add(new Separator("navigation"));
        navMenu.add(new Separator("search"));
        navMenu.add(new Separator("jumping1"));
        navMenu.add(new Separator("jumping2"));
        navMenu.add(new Separator("etc"));
        menuBar.add(navMenu);
        
        /* Jumping Menu */
        MenuManager jumpMenu = new MenuManager(ClientPlugin.getResourceString("Menu.&Jump"),
                "org.eclipse.actf.examples.aibrowser.jumping");
        jumpMenu.add(new Separator("jumping1"));
        jumpMenu.add(new Separator("jumping2"));
        jumpMenu.add(new Separator("jumping3"));
        jumpMenu.add(new Separator("jumping4"));
        menuBar.add(jumpMenu);
        
        /* Multimedia Menu */
        MenuManager mulMenu = new MenuManager(ClientPlugin.getResourceString("Menu.&Multimedia"),
                "org.eclipse.actf.examples.aibrowser.multimedia");
        mulMenu.add(new Separator("basic"));
        mulMenu.add(new Separator("volume"));
        mulMenu.add(new Separator("advance"));
        mulMenu.add(new Separator("etc"));
        menuBar.add(mulMenu);

        /* Annotation Menu */
        MenuManager annotMenu = new MenuManager(ClientPlugin.getResourceString("Menu.&Annotation"),
                "org.eclipse.actf.examples.aibrowser.annotation");
        annotMenu.add(new Separator("memo"));
        annotMenu.add(new Separator("file"));
        menuBar.add(annotMenu);
        
        /* Window Menu */
        MenuManager windowMenu = new MenuManager(ClientPlugin.getResourceString("Menu.&Window"),
                IWorkbenchActionConstants.M_WINDOW);
        windowMenu.add(new Separator(IWorkbenchActionConstants.NAV_START));
        windowMenu.add(new Separator(IWorkbenchActionConstants.NAV_END));
        windowMenu.add(new Separator());
        windowMenu.add(_preferencesAction);
        menuBar.add(windowMenu);
        
        /* Help Menu */
        MenuManager helpMenu = new MenuManager(ClientPlugin.getResourceString("Menu.&Help"),
                IWorkbenchActionConstants.M_HELP);
        helpMenu.add(new Separator(IWorkbenchActionConstants.HELP_START));
        helpMenu.add(new Separator(IWorkbenchActionConstants.HELP_END));
        helpMenu.add(new Separator());
        helpMenu.add(_aboutAction);
        menuBar.add(helpMenu);
        
        NavigatorPlugin.menuManager = menuBar;

        //register(_closeAction);
        //register(_closeAllAction);
        register(_quitAction);
        register(_preferencesAction);
        register(_aboutAction);
    }

    @Override
    public void fillStatusLine(IStatusLineManager statusLine) {
        super.fillStatusLine(statusLine);
        // StatusLineContributionItem statusModeItem = new StatusLineContributionItem("mode");
        // statusLine.add(statusModeItem);
        
        ModeContribution mc = new ModeContribution();
        statusLine.add(mc);
        
        ProgressContribution pc = new ProgressContribution(ProgressContribution.PROGRESS_CONTRIBUTION_ID);
        pc.setVisible(false);
        statusLine.add(pc);

        statusLine.update(true);
    }

    @Override
    protected void fillCoolBar(ICoolBarManager coolBar) {
    }
}
