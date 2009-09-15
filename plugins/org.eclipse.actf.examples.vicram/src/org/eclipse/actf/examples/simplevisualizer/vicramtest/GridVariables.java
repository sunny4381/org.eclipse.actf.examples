/*******************************************************************************
 * Copyright (c) 2009 University of Manchester and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eleni Michailidou - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.examples.simplevisualizer.vicramtest;

/*
 * GridVarables is a helper method that stores the variables for each grid defined in Visualization.java
 * The method also implements compareTo method that sorts an array based on a ascending order of TLC, images and then wordCount
 */
public class GridVariables implements Comparable {
	public int TLC, images;
	double wordCount;
	public int row = -1;
	public int column = -1;

	// public double gridVCS;

	public GridVariables(int TLC, int images, double wordCount) {
		this.images = images;
		this.TLC = TLC;
		this.wordCount = wordCount;
	}

	public double getGridVCS() {
		return (1.743 + 0.097 * (TLC) + 0.053 * (wordCount) + 0.003 * (images)) / 10;
	}

	public void setRow(int newRow) {
		this.row = newRow;
	}

	public void setCol(int newCol) {
		this.column = newCol;
	}

	// this allows java to srt the objects
	public int compareTo(Object anotherGridVariables) throws ClassCastException {
		if (!(anotherGridVariables instanceof GridVariables))
			throw new ClassCastException("A GridVariables object expected.");
		int anotherGridTLC = ((GridVariables) anotherGridVariables).TLC;
		int anotherGridImg = ((GridVariables) anotherGridVariables).images;
		double anotherGridWC = ((GridVariables) anotherGridVariables).wordCount;
		if (this.TLC == 0 && anotherGridTLC == 0) {
			if (this.images == 0 && anotherGridImg == 0) {
				if (this.wordCount >= anotherGridWC) {
					return -1;
				} else
					return 1;
			} else if (this.images >= anotherGridImg) {
				return -1;
			} else
				return 1;
		}
		else if (this.TLC >= anotherGridTLC) {
			return -1;
		} else
			return 1;
	}
}