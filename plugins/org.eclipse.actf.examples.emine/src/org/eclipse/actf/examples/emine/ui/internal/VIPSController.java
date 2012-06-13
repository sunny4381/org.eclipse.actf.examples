/*******************************************************************************
 * Copyright (c) 2008, 2012 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *    Elgin Akpinar -  VIPS implementation
 *******************************************************************************/
package org.eclipse.actf.examples.emine.ui.internal;

import java.io.File;
import java.util.List;

import org.eclipse.actf.examples.emine.EminePlugin;
import org.eclipse.actf.examples.emine.vips.Segmentation;
import org.eclipse.actf.model.ui.IModelService;
import org.eclipse.actf.model.ui.ModelServiceImageCreator;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.eclipse.actf.visualization.IVisualizationConst;
import org.eclipse.actf.visualization.ui.IPositionSize;
import org.eclipse.actf.visualization.ui.IVisualizationView;
import org.eclipse.actf.visualization.ui.VisualizationCanvas;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;

public class VIPSController implements IVisualizationConst {

	private Shell shell;
	private IVisualizationView vizView;
	private VisualizationCanvas vizCanvas;
	// private AlphaValueToolbar alphaBar;
	private Tree tableTree;
	private Action overlayAction;

	// private Mediator mediator = Mediator.getInstance();

	private boolean isInVisualize;
	private String screenshotFile;

	public VIPSController(IVisualizationView vizView,
			Composite parent) {

		this.vizView = vizView;
		this.shell = parent.getShell();

		initComposite(parent);
		prepareActions();

		isInVisualize = false;

		try {
			File dumpImgFile = EminePlugin.getDefault()
					.createTempFile(PREFIX_SCREENSHOT, SUFFIX_BMP);
			screenshotFile = dumpImgFile.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void prepareActions() {
		overlayAction = new Action("Segmentation",
				EminePlugin.imageDescriptorFromPlugin(
						EminePlugin.PLUGIN_ID,
						"/icons/action16/overlay16.gif")) {
			public void run() {
				doVisualize();
			}
		};

		IActionBars bars = vizView.getViewSite().getActionBars();
		// IMenuManager menuManager = bars.getMenuManager();
		IToolBarManager toolbarManager = bars.getToolBarManager();
		toolbarManager.add(overlayAction);
		toolbarManager
				.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void initComposite(Composite parent) {
		parent.setToolTipText("Visual Segmentation");
		GridData gridData;

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = gridLayout.verticalSpacing = 0;
		parent.setLayout(gridLayout);

		tableTree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tableTree.setHeaderVisible(true);
		TreeColumn column1 = new TreeColumn(tableTree, SWT.LEFT);
		column1.setText("Block");
		column1.setWidth(200);
		TreeColumn column5 = new TreeColumn(tableTree, SWT.RIGHT);
		column5.setText("Tag");
		column5.setWidth(100);
		TreeColumn column2 = new TreeColumn(tableTree, SWT.RIGHT);
		column2.setText("DoC");
		column2.setWidth(50);
		TreeColumn column3 = new TreeColumn(tableTree, SWT.RIGHT);
		column3.setText("Font Size");
		column3.setWidth(50);
		TreeColumn column4 = new TreeColumn(tableTree, SWT.RIGHT);
		column4.setText("Path");
		column4.setWidth(200);

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;

		tableTree.setLayoutData(gridData);

		this.vizCanvas = new VisualizationCanvas(tableTree);

		gridData = new GridData();
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.vizCanvas.setLayoutData(gridData);
	}

	public void doVisualize() {
		if (isInVisualize) {
			return;
		}

		isInVisualize = true;
		shell.setCursor(new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT));

		vizCanvas.clear();
		shell.getDisplay().update();

		vizView.setStatusMessage("Capturing screenshot.");
		IModelService modelService = ModelServiceUtils.getActiveModelService();
		if (modelService == null) {
			return;
		}

		// generate screenshot and read it as Image
		ModelServiceImageCreator imgCreator = new ModelServiceImageCreator(
				modelService);
		imgCreator.getScreenImageAsBMP(screenshotFile, true);
		Image baseImage = new Image(shell.getDisplay(), screenshotFile);

		vizView.setStatusMessage("Processing overlay.");

		GC gc = new GC(baseImage);
		gc.setAlpha(100);
		Segmentation seg = new Segmentation();
		seg.setGC(gc);
		seg.setTree(tableTree);
		seg.calculate();

		vizView.setStatusMessage("Segmentation is over.");
		shell.setCursor(null);
		isInVisualize = false;
	}

	public void setHighlightPositions(List<IPositionSize> infoPositionSizeList) {
		vizCanvas.highlight(infoPositionSizeList);
	}

	public void setCurrentModelService(IModelService modelService) {
		vizCanvas.setCurrentModelService(modelService);
	}

	// public void getStyleInfo(IModelService modelService) {
	// if (modelService instanceof IWebBrowserACTF) {
	// IWebBrowserACTF browser = (IWebBrowserACTF) modelService;
	// vizView.setStatusMessage("Getting styleInfo from Live DOM.");
	// IWebBrowserStyleInfo style = browser.getStyleInfo();
	// ModelServiceSizeInfo sizeInfo = style.getSizeInfo(true);
	// StringBuffer tmpSB = new StringBuffer(4096);
	// }
	// }

}
