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

package org.eclipse.actf.examples.emine.vips;

import java.util.Map;

import org.eclipse.actf.examples.emine.vips.types.VipsBlock;
import org.eclipse.actf.examples.emine.vips.types.VipsSeparator;
import org.eclipse.actf.examples.emine.vips.types.VipsNode;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class SeparatorDetection {
	private Map<VipsBlock, VipsNode> blockPool;
	private GC gc;

	public void detect() {

	}

	public Map<VipsBlock, VipsNode> getBlockPool() {
		return blockPool;
	}

	public void setBlockPool(Map<VipsBlock, VipsNode> blockPool) {
		this.blockPool = blockPool;
	}

	public GC getGc() {
		return gc;
	}

	public void setGc(GC gc) {
		this.gc = gc;
	}

	public void seperatorDetection(VipsBlock block) {
		VipsNode blockElement = blockPool.get(block);
		VipsSeparator blockBorder = new VipsSeparator();
		blockBorder.setStartPixel(new Point(blockElement.getStyle()
				.getRectangle().x, blockElement.getStyle().getRectangle().y));
		blockBorder.setEndPixel(new Point(blockElement.getStyle()
				.getRectangle().x
				+ blockElement.getStyle().getRectangle().width, blockElement
				.getStyle().getRectangle().y
				+ blockElement.getStyle().getRectangle().height));
		blockBorder.setType(VipsSeparator.HORIZONTAL);
		blockBorder.setLeftElement(null);
		blockBorder.setRightElement(null);
		block.addSeparator(blockBorder);

		for (VipsBlock childBlock : block.getChildren()) {
			Rectangle blockRect = blockPool.get(childBlock).getStyle()
					.getRectangle();
			for (int i = 0; i < block.getSeparators().size(); i++) {
				VipsSeparator separator = block.getSeparators().get(i);

				Rectangle separatorRect = new Rectangle(
						separator.getStartPixel().x,
						separator.getStartPixel().y, separator.getEndPixel().x
								- separator.getStartPixel().x,
						separator.getEndPixel().y - separator.getStartPixel().y);

				if (separatorRect.intersects(blockRect)) {
					Point s1 = separator.getStartPixel();
					Point s2 = separator.getEndPixel();

					if (blockRect.contains(s1) && blockRect.contains(s2)) {
						// block covers the separator, then remove the separator
						block.getSeparators().remove(separator);
					} else if (separator.containsRectangle(blockRect)) {
						// the block is contained in the separator, split the
						// separator
						VipsSeparator above = new VipsSeparator();
						VipsSeparator below = new VipsSeparator();
						VipsSeparator left = new VipsSeparator();
						VipsSeparator right = new VipsSeparator();

						above.setStartPixel(s1);
						above.setEndPixel(new Point(s2.x, blockRect.y));
						above.setType(VipsSeparator.HORIZONTAL);
						above.setLeftElement(separator.getLeftElement());
						above.setRightElement(blockPool.get(childBlock));

						below.setStartPixel(new Point(s1.x, blockRect.y
								+ blockRect.height));
						below.setEndPixel(s2);
						below.setType(VipsSeparator.HORIZONTAL);
						below.setLeftElement(blockPool.get(childBlock));
						below.setRightElement(separator.getRightElement());

						left.setStartPixel(s1);
						left.setEndPixel(new Point(blockRect.x, s2.y));
						left.setType(VipsSeparator.VERTICAL);
						left.setLeftElement(separator.getLeftElement());
						left.setRightElement(blockPool.get(childBlock));

						right.setStartPixel(new Point(blockRect.x
								+ blockRect.width, s1.y));
						right.setEndPixel(s2);
						right.setType(VipsSeparator.VERTICAL);
						right.setLeftElement(blockPool.get(childBlock));
						right.setRightElement(separator.getRightElement());

						block.getSeparators().remove(separator);
						block.getSeparators().add(above);
						block.getSeparators().add(below);
						block.getSeparators().add(left);
						block.getSeparators().add(right);
						// break;
					} else {
						// block crosses with the separator
						Rectangle intersectionRect = blockRect
								.intersection(separatorRect);

						VipsSeparator above = new VipsSeparator();
						VipsSeparator below = new VipsSeparator();
						VipsSeparator left = new VipsSeparator();
						VipsSeparator right = new VipsSeparator();

						if (isRectangleValid(s1.x, s1.y,
								separator.getEndPixel().x, intersectionRect.y)) {
							above.setStartPixel(s1);
							above.setEndPixel(new Point(s2.x,
									intersectionRect.y));
							above.setType(VipsSeparator.HORIZONTAL);
							above.setLeftElement(separator.getLeftElement());
							above.setRightElement(blockPool.get(childBlock));
							block.getSeparators().add(above);
						}

						if (isRectangleValid(s1.x, intersectionRect.y
								+ intersectionRect.height, s2.x, s2.y)) {
							below.setStartPixel(new Point(s1.x,
									intersectionRect.y
											+ intersectionRect.height));
							below.setEndPixel(s2);
							below.setType(VipsSeparator.HORIZONTAL);
							below.setLeftElement(blockPool.get(childBlock));
							below.setRightElement(separator.getRightElement());
							block.getSeparators().add(below);
						}

						if (isRectangleValid(s1.x, s1.y, intersectionRect.x,
								s2.y)) {
							left.setStartPixel(s1);
							left.setEndPixel(new Point(intersectionRect.x, s2.y));
							left.setType(VipsSeparator.VERTICAL);
							left.setLeftElement(separator.getLeftElement());
							left.setRightElement(blockPool.get(childBlock));
							block.getSeparators().add(left);
						}

						if (isRectangleValid(intersectionRect.x
								+ intersectionRect.width, s1.y, s2.x, s2.y)) {
							right.setStartPixel(new Point(intersectionRect.x
									+ intersectionRect.width, s1.y));
							right.setEndPixel(s2);
							right.setType(VipsSeparator.VERTICAL);
							right.setLeftElement(blockPool.get(childBlock));
							right.setRightElement(separator.getRightElement());
							block.getSeparators().add(right);
						}

						block.getSeparators().remove(separator);
						// break;
					}
				}
			}
		}

		// remove the separators that stand at the border of the pool
		for (int i = 0; i < block.getSeparators().size();) {
			VipsSeparator separator = block.getSeparators().get(i);
			if (separator.getType().equals(VipsSeparator.HORIZONTAL)) {
				if (separator.getStartPixel().y == blockBorder.getStartPixel().y) {
					block.getSeparators().remove(separator);
				} else if (separator.getEndPixel().y == blockBorder
						.getEndPixel().y) {
					block.getSeparators().remove(separator);
				} else {
					i++;
					// separator.detectWeightForHorizontal();
				}
			} else {
				if (separator.getStartPixel().x == blockBorder.getStartPixel().x) {
					block.getSeparators().remove(separator);
				} else if (separator.getEndPixel().x == blockBorder
						.getEndPixel().x) {
					block.getSeparators().remove(separator);
				} else {
					i++;
					// separator.detectWeightForVertical();
				}
			}
		}

		// for (int i = 0; i < block.getSeparators().size(); i++) {
		// VIPSSeparator separator = block.getSeparators().get(i);
		// // System.out.println(separator.getWeight());
		//
		// separator.drawSeparator(gc);
		// }
	}

	public boolean isRectangleValid(int x1, int y1, int x2, int y2) {
		if ((x2 - x1) * (y2 - y1) == 0)
			return false;
		else
			return true;
	}
}
