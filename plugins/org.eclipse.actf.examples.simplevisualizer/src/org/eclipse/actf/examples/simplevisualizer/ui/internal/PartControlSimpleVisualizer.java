/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.examples.simplevisualizer.ui.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.eclipse.actf.examples.simplevisualizer.SimpleVisualizerPlugin;
import org.eclipse.actf.mediator.Mediator;
import org.eclipse.actf.model.ui.IModelService;
import org.eclipse.actf.model.ui.ModelServiceImageCreator;
import org.eclipse.actf.model.ui.ModelServiceSizeInfo;
import org.eclipse.actf.model.ui.editor.browser.ICurrentStyles;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserACTF;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserStyleInfo;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.eclipse.actf.util.FileUtils;
import org.eclipse.actf.util.dom.DomPrintUtil;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PartControlSimpleVisualizer implements IVisualizationConst {

	private static final EvaluationResultImpl dummyResult = new EvaluationResultImpl();

	private static final String REPORT_HTML_PRE = "<html><head><title>ACTF report sample</title>"
			+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
			+ "</head><body><pre>";
	private static final String REPORT_HTML_POST = "</pre></body></html>";

	private Shell shell;
	private IVisualizationView vizView;
	private VisualizationCanvas vizCanvas;
	private AlphaValueToolbar alphaBar;
	private Action visualizeAction, overlayAction;

	private Mediator mediator = Mediator.getInstance();

	private boolean isInVisualize;
	private EvaluationResultImpl evalResult;
	private String screenshotFile, reportFile;

	public PartControlSimpleVisualizer(IVisualizationView vizView,
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

		overlayAction = new Action("Visualize (live DOM)",
				SimpleVisualizerPlugin.imageDescriptorFromPlugin(
						SimpleVisualizerPlugin.PLUGIN_ID,
						"/icons/action16/overlay16.gif")) {
			public void run() {
				doVisualize(true);
			}
		};

		visualizeAction = new Action("Visualize (original DOM)",
				SimpleVisualizerPlugin.imageDescriptorFromPlugin(
						SimpleVisualizerPlugin.PLUGIN_ID,
						"/icons/action16/simulation16.gif")) {
			public void run() {
				doVisualize(false);
			}
		};

		IActionBars bars = vizView.getViewSite().getActionBars();
		// IMenuManager menuManager = bars.getMenuManager();
		IToolBarManager toolbarManager = bars.getToolBarManager();
		toolbarManager.add(overlayAction);
		toolbarManager.add(visualizeAction);
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

	public void doVisualize(boolean flag) {
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

		// prepare overlay image data (rainbow)
		Rectangle size = baseImage.getBounds();
		int[][] overlayPixels = new int[size.height][size.width];
		int xMax = flag ? size.width : size.height;
		int yMax = flag ? size.height : size.width;
		for (int y = 0; y < yMax; y++) {
			int i = (y / 50) % 7;
			int color;
			switch (i) {
			case 0:
				color = 0x3D1AED;
				break;
			case 1:
				color = 0x4CB7FF;
				break;
			case 2:
				color = 0x00D4FF;
				break;
			case 3:
				color = 0x008000;
				break;
			case 4:
				color = 0xD69A00;
				break;
			case 5:
				color = 0x74540F;
				break;
			case 6:
				color = 0xA857A7;
				break;
			default:
				color = 0xFFFFFF;
			}

			for (int x = 0; x < xMax; x++) {
				if (flag) {
					overlayPixels[y][x] = color;
				} else {
					overlayPixels[x][y] = color;
				}
			}
		}

		ImageOverlayUtil.overlay(baseImage, overlayPixels, alphaBar.getAlpha());

		// set image to canvas
		vizCanvas.showImage(baseImage.getImageData(), modelService);

		// example: obtain DOM, curentStyle
		if (modelService instanceof IWebBrowserACTF) {
			IWebBrowserACTF browser = (IWebBrowserACTF) modelService;
			vizView.setStatusMessage("Getting styleInfo from Live DOM.");

			IWebBrowserStyleInfo style = browser.getStyleInfo();
			ModelServiceSizeInfo sizeInfo = style.getSizeInfo(true);
			StringBuffer tmpSB = new StringBuffer(4096);
			tmpSB.append("Web page size: [" + sizeInfo.toString() + "]"
					+ FileUtils.LINE_SEP + FileUtils.LINE_SEP);

			Map<String, ICurrentStyles> styleMap = style.getCurrentStyles();
			for (String xpath : styleMap.keySet()) {
				ICurrentStyles curStyle = styleMap.get(xpath);
				tmpSB.append(xpath + " : (" + curStyle.getRectangle() + ")"
						+ FileUtils.LINE_SEP + "  display: "
						+ curStyle.getDisplay() + "  backgroundColor: "
						+ curStyle.getBackgroundColor() + FileUtils.LINE_SEP
						+ FileUtils.LINE_SEP);
			}

			// set styleInfo as a summary report
			evalResult.setSummaryReportText(tmpSB.toString());

			try {
				PrintWriter tmpPW = new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(reportFile), "UTF-8"));
				tmpPW.println(REPORT_HTML_PRE);
				DomPrintUtil dpu;
				if (flag) {
					vizView.setStatusMessage("Copying Live DOM.");
					tmpPW.println("---Live DOM--- ");
					tmpPW.println();

					Document doc = modelService.getLiveDocument();

					dpu = new DomPrintUtil(doc);
					// Escape tag bracket('<' -> '%lt;') to print out in <pre>
					dpu.setEscapeTagBracket(true);
					// attribute filter to remove unnecessary attributes
					dpu.setAttrFilter(new DomPrintUtil.AttributeFilter() {
						public boolean acceptNode(Element element, Node attr) {
							String name = attr.getNodeName();
							return element.hasAttribute(name);
						}
					});

					// TODO recover DOCTYPE
					// DOCTYPE is handled as a Comment node (first/last 2
					// chars are
					// lost) in IE.

				} else {
					vizView.setStatusMessage("Parsing and copying source DOM.");

					tmpPW.println("---Source DOM--- ");
					tmpPW.println();

					Document doc = modelService.getDocument();
					dpu = new DomPrintUtil(doc);
					// Escape tag bracket('<' -> '%lt;') to print out in <pre>
					dpu.setEscapeTagBracket(true);
					System.out.println(doc);
				}

				tmpPW.println(dpu.toXMLString());

				tmpPW.println(REPORT_HTML_POST);
				tmpPW.flush();
				tmpPW.close();

				// set summary of the page as a report
				evalResult.setSummaryReportUrl(reportFile);

			} catch (Exception e) {
				e.printStackTrace();
			}

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
