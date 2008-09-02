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
package org.eclipse.actf.examples.simplevisualizer.ui.views;

import org.eclipse.actf.examples.simplevisualizer.ui.internal.PartControlSimpleVisualizer;
import org.eclipse.actf.mediator.MediatorEvent;
import org.eclipse.actf.visualization.ui.IVisualizationView;
import org.eclipse.actf.visualization.ui.VisualizationStatusLineContributionItem;
import org.eclipse.actf.visualization.ui.report.table.ResultTableLabelProviderLV;
import org.eclipse.actf.visualization.ui.report.table.ResultTableSorterLV;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

public class SimpleVisualizerView extends ViewPart implements IVisualizationView {

	public static final String ID = SimpleVisualizerView.class.getName();

	private IBaseLabelProvider baseLabelProvider = new ResultTableLabelProviderLV();

	private ViewerSorter viewerSorter = new ResultTableSorterLV();

	private PartControlSimpleVisualizer partControl;

	public SimpleVisualizerView() {
		super();
	}

	public void init(IViewSite site) throws PartInitException {
		setSite(site);
		setStatusLine();
	}

	public void createPartControl(Composite parent) {
		partControl = new PartControlSimpleVisualizer(this, parent);
	}

	public void setFocus() {
	}

	public void setStatusMessage(String statusMessage) {
		IContributionItem[] items = getViewSite().getActionBars()
				.getStatusLineManager().getItems();
		for (int i = 0; i < items.length; i++) {
			if (null != items[i]
					&& items[i].getId().equals(
							VisualizationStatusLineContributionItem.ID + ID)) {
				((VisualizationStatusLineContributionItem) items[i])
						.setStatusMessage(statusMessage);
			}
		}
	}

	public void setInfoMessage(String infoMessage) {
		IContributionItem[] items = getViewSite().getActionBars()
				.getStatusLineManager().getItems();
		for (int i = 0; i < items.length; i++) {
			if (null != items[i]
					&& items[i].getId().equals(
							VisualizationStatusLineContributionItem.ID + ID)) {
				((VisualizationStatusLineContributionItem) items[i])
						.setInfoMessage(infoMessage);
			}
		}
	}

	private void setStatusLine() {
		getViewSite().getActionBars().getStatusLineManager().add(
				new VisualizationStatusLineContributionItem(ID));
	}

	public IBaseLabelProvider getLabelProvider() {
		return baseLabelProvider;
	}

	public ViewerSorter getTableSorter() {
		return viewerSorter;
	}

	public int getResultTableMode() {
		return MODE_LOWVISION;
	}

	public void doVisualize() {
		partControl.doVisualize(true);
	}

	public void modelserviceChanged(MediatorEvent event) {
		partControl.setCurrentModelService(event.getModelServiceHolder()
				.getModelService());
	}

	public void modelserviceInputChanged(MediatorEvent event) {
		partControl.setCurrentModelService(event.getModelServiceHolder()
				.getModelService());
	}

	public void reportChanged(MediatorEvent event) {
	}

	public void reportGeneratorChanged(MediatorEvent event) {
	}

	public void setVisualizeMode(int mode) {
		//do nothing
	}

}
