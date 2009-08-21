/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation, University of Manchester and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *    Eleni Michailidou - initial API and implementation     
 *******************************************************************************/
package org.eclipse.actf.examples.simplevisualizer.ui.internal;

import java.io.File;
import java.util.List;

import org.eclipse.actf.examples.simplevisualizer.SimpleVisualizerPlugin;
import org.eclipse.actf.examples.simplevisualizer.vicramtest.Complexity;
import org.eclipse.actf.examples.simplevisualizer.vicramtest.Visualization;
import org.eclipse.actf.mediator.Mediator;
import org.eclipse.actf.model.ui.IModelService;
import org.eclipse.actf.model.ui.ModelServiceImageCreator;
import org.eclipse.actf.model.ui.ModelServiceSizeInfo;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserACTF;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserStyleInfo;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.eclipse.actf.visualization.IVisualizationConst;
import org.eclipse.actf.visualization.eval.EvaluationResultImpl;
import org.eclipse.actf.visualization.ui.IPositionSize;
import org.eclipse.actf.visualization.ui.IVisualizationView;
import org.eclipse.actf.visualization.ui.VisualizationCanvas;
import org.eclipse.actf.visualization.util.ImageOverlayUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;

public class ComplexityVisualizationController implements IVisualizationConst {

	private static final EvaluationResultImpl dummyResult = new EvaluationResultImpl();

	private Shell shell;
	private IVisualizationView vizView;
	private VisualizationCanvas vizCanvas;
	private AlphaValueToolbar alphaBar;
	private Action overlayAction;

	private Mediator mediator = Mediator.getInstance();

	private boolean isInVisualize;
	private EvaluationResultImpl evalResult;
	private String screenshotFile, reportFile;

	public ComplexityVisualizationController(IVisualizationView vizView,
			Composite parent) {

		this.vizView = vizView;
		this.shell = parent.getShell();

		initComposite(parent);
		prepareActions();

		isInVisualize = false;

		try {
			File dumpImgFile = SimpleVisualizerPlugin.getDefault()
					.createTempFile(PREFIX_SCREENSHOT, SUFFIX_BMP);
			screenshotFile = dumpImgFile.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			File htmlFile = SimpleVisualizerPlugin.getDefault().createTempFile(
					PREFIX_REPORT, SUFFIX_HTML);
			reportFile = htmlFile.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void prepareActions() {

		overlayAction = new Action("Complexity Visualization",
				SimpleVisualizerPlugin.imageDescriptorFromPlugin(
						SimpleVisualizerPlugin.PLUGIN_ID,
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
		GridData gridData;

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = gridLayout.verticalSpacing = 0;
		parent.setLayout(gridLayout);

		alphaBar = new AlphaValueToolbar(parent, SWT.BORDER);

		Composite compositeLowVisionHalf2 = new Composite(parent, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		compositeLowVisionHalf2.setLayoutData(gridData);

		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = gridLayout.verticalSpacing = 0;
		compositeLowVisionHalf2.setLayout(gridLayout);

		// Canvas to show the image.
		this.vizCanvas = new VisualizationCanvas(compositeLowVisionHalf2);
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

		// remove results
		mediator.setReport(vizView, dummyResult);
		evalResult = new EvaluationResultImpl();
		vizCanvas.clear();
		shell.getDisplay().update();

		vizView.setStatusMessage("Capturing screenshot.");
		// obtain active Model Service (browser, etc.)
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
		
		//EM - Call Visualization Code
		//
		Visualization.findElements();
		int[][] redPixels = Visualization.getRedCoordinates();
		
		

		// prepare overlay image data (rainbow)
		Rectangle size = baseImage.getBounds();
		int[][] overlayPixels = new int[size.height][size.width];
		int xMax = size.width;
		int yMax = size.height;

		for (int y = 0; y < yMax; y++) {
			for (int x = 0; x < xMax; x++) {
				overlayPixels[y][x] = 0xC0C0C0;
			}
		}

		ImageOverlayUtil.overlay(baseImage, overlayPixels, alphaBar.getAlpha());

		// set image to canvas
		vizCanvas.showImage(baseImage.getImageData(), modelService);

		if (modelService instanceof IWebBrowserACTF) {
			IWebBrowserACTF browser = (IWebBrowserACTF) modelService;
			vizView.setStatusMessage("Getting styleInfo from Live DOM.");

			IWebBrowserStyleInfo style = browser.getStyleInfo();
			ModelServiceSizeInfo sizeInfo = style.getSizeInfo(true);
			StringBuffer tmpSB = new StringBuffer(4096);
			//EM - Call calculate method
			tmpSB.append(Complexity.calculate());
			//tmpSB.append(Complexity.getTotalWords());

			// set styleInfo as a summary report
			evalResult.setSummaryReportText(tmpSB.toString());
			// set summary of the page as a report
			evalResult.setSummaryReportUrl(reportFile);

		}

		Mediator.getInstance().setReport(vizView, evalResult);

		vizView.setStatusMessage("Visualization is over.");
		shell.setCursor(null);
		isInVisualize = false;

	}

	public void setHighlightPositions(List<IPositionSize> infoPositionSizeList) {
		vizCanvas.highlight(infoPositionSizeList);
	}

	public void setCurrentModelService(IModelService modelService) {
		vizCanvas.setCurrentModelService(modelService);
	}

}
