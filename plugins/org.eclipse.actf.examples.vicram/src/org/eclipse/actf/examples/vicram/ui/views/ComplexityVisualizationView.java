/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.examples.vicram.ui.views;

import org.eclipse.actf.examples.vicram.ui.internal.ComplexityVisualizationController;
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

public class ComplexityVisualizationView extends ViewPart implements IVisualizationView {

	public static final String ID = ComplexityVisualizationView.class.getName();

	private IBaseLabelProvider baseLabelProvider = new ResultTableLabelProviderLV();

	private ViewerSorter viewerSorter = new ResultTableSorterLV();

	private ComplexityVisualizationController partControl;

	public ComplexityVisualizationView() {
		super();
	}

	public void init(IViewSite site) throws PartInitException {
		setSite(site);
		setStatusLine();
	}

	public void createPartControl(Composite parent) {
		partControl = new ComplexityVisualizationController(this, parent);
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
		partControl.doVisualize();
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
