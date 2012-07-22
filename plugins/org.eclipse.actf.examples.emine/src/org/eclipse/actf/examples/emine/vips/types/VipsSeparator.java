/*******************************************************************************
 * Copyright (c) 2012 Middle East Technical University Northern Cyprus Campus and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Elgin Akpinar (METU) - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.examples.emine.vips.types;

import org.eclipse.actf.model.ui.editor.browser.ICurrentStyles;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Separators are horizontal or vertical lines in a web page that visually cross with no
 * blocks and can be used to discriminate different semantics within the page.
 */
public class VipsSeparator {
	public static final String VERTICAL = "V";
	public static final String HORIZONTAL = "H";
	public static final int WEIGHTADDITION = 2;
	private VipsNode leftElement;
	private VipsNode rightElement;
	private Point startPixel;
	private Point endPixel;
	private String type;
	private int weight = 0;

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public void addToWeight(int addition) {
		this.weight += addition;
	}

	public void subtractFromWeight(int subtraction) {
		this.weight -= subtraction;
	}

	public VipsNode getLeftElement() {
		return leftElement;
	}

	public void setLeftElement(VipsNode leftElement) {
		this.leftElement = leftElement;
	}

	public VipsNode getRightElement() {
		return rightElement;
	}

	public void setRightElement(VipsNode rightElement) {
		this.rightElement = rightElement;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Point getStartPixel() {
		return startPixel;
	}

	public void setStartPixel(Point startPixel) {
		this.startPixel = startPixel;
	}

	public Point getEndPixel() {
		return endPixel;
	}

	public void setEndPixel(Point endPixel) {
		this.endPixel = endPixel;
	}

	public boolean containsRectangle(Rectangle rect) {
		if (startPixel.x <= rect.x && startPixel.y <= rect.y
				&& endPixel.x >= rect.x + rect.width
				&& endPixel.y >= rect.y + rect.height) {
			return true;
		} else {
			return false;
		}
	}

	public void detectWeight() {

	}

	public void drawSeparator(GC gc) {
		gc.setForeground(new Color(gc.getDevice(), 255, 0, 0));
		gc.setBackground(new Color(gc.getDevice(), 255, 0, 0));
		Rectangle rect = new Rectangle(startPixel.x, startPixel.y, endPixel.x
				- startPixel.x, endPixel.y - startPixel.y);
		// gc.drawRectangle(rect);
		gc.fillRectangle(rect);
		gc.setForeground(new Color(gc.getDevice(), 0, 0, 0));
		gc.setBackground(new Color(gc.getDevice(), 0, 0, 0));
	}

	public void detectWeightForVertical() {
		addToWeight(getEndPixel().x - getStartPixel().x);
		ICurrentStyles leftStyle = getLeftElement().getStyle();
		ICurrentStyles rightStyle = getRightElement().getStyle();
		// TODO check if overlapped with hr
		if (!leftStyle.getBackgroundColor().equals(
				rightStyle.getBackgroundColor())) {
			addToWeight(WEIGHTADDITION);
		}

	}

	public void detectWeightForHorizontal() {
		// calculate weight
		addToWeight(getEndPixel().y - getStartPixel().y);

		ICurrentStyles leftStyle = getLeftElement().getStyle();
		ICurrentStyles rightStyle = getRightElement().getStyle();
		// TODO check if overlapped with hr
		if (!leftStyle.getBackgroundColor().equals(
				rightStyle.getBackgroundColor())) {
			addToWeight(WEIGHTADDITION);
		}

		if (getLeftElement().getFontSize() < getRightElement().getFontSize()) {
			addToWeight(WEIGHTADDITION);
		}
		// TODO compare structure

	}
}
