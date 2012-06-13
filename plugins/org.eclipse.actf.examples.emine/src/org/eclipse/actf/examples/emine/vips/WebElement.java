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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.actf.model.dom.dombycom.IElementEx;
import org.eclipse.actf.model.ui.editor.browser.ICurrentStyles;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

public class WebElement {
	private String tag;
	private short index;
	private int fontSize;
	private String path;
	private String id;
	private String className;
	private ICurrentStyles style;
	private WebElement parent;
	private ArrayList<WebElement> children;
	private String floatStr;
	private String position, posTop, posBottom, posLeft, posRight;
	private String marginLeft, marginRight, marginTop, marginBottom;

	private IElementEx e;

	protected static final Set<String> INVALID_NODES = new HashSet<String>(
			Arrays.asList(new String[] { "AREA", "BASE", "BASEFONT", "COL",
					"COLGROUP", "LINK", "MAP", "META", "PARAM", "SCRIPT",
					"STYLE", "TITLE", "!DOCTYPE" }));
	private static final Set<String> INLINE_NODES = new HashSet<String>(
			Arrays.asList(new String[] { "A", "ABBR", "ACRONYM", "B", "BDO",
					"BIG", "BUTTON", "CITE", "CODE", "DEL", "DFN", "EM",
					"FONT", "I", "IMG", "INPUT", "INS", "KBD", "LABEL",
					"OBJECT", "Q", "S", "SAMP", "SMALL", "SPAN", "STRIKE",
					"STRONG", "SUB", "SUP", "TT", "U", "VAR", "APPLET",
					"SELECT", "TEXTAREA" }));

	public WebElement() {
		children = new ArrayList<WebElement>();
		parent = null;
		style = null;
		path = "";
		tag = "";
		fontSize = -1;
		id = "";
		className = "";
		floatStr = "none";
	}

	public void highlight() {
		try {
			e.highlight();
		} catch (Exception e) {
			// TODO Handle pokemon code and implement catch
		}
	}

	public void unhighlight() {
		try {
			e.unhighlight();
		} catch (Exception e) {
			// TODO Handle pokemon code and implement catch
		}
	}

	public String getPosTop() {
		return posTop;
	}

	public void setPosTop(String posTop) {
		this.posTop = posTop;
	}

	public String getPosBottom() {
		return posBottom;
	}

	public void setPosBottom(String posBottom) {
		this.posBottom = posBottom;
	}

	public String getPosLeft() {
		return posLeft;
	}

	public void setPosLeft(String posLeft) {
		this.posLeft = posLeft;
	}

	public String getPosRight() {
		return posRight;
	}

	public void setPosRight(String posRight) {
		this.posRight = posRight;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public IElementEx getE() {
		return e;
	}

	public void setE(IElementEx e) {
		this.e = e;
	}

	public String getMarginLeft() {
		return marginLeft;
	}

	public void setMarginLeft(String marginLeft) {
		this.marginLeft = marginLeft;
	}

	public String getMarginRight() {
		return marginRight;
	}

	public void setMarginRight(String marginRight) {
		this.marginRight = marginRight;
	}

	public String getMarginTop() {
		return marginTop;
	}

	public void setMarginTop(String marginTop) {
		this.marginTop = marginTop;
	}

	public String getMarginBottom() {
		return marginBottom;
	}

	public void setMarginBottom(String marginBottom) {
		this.marginBottom = marginBottom;
	}

	public boolean hasChildren() {
		if (children.size() == 0)
			return false;
		else
			return true;
	}

	public String getFloatStr() {
		return floatStr;
	}

	public boolean containsImage() {
		if (getTag().equals("IMG"))
			return true;
		for (WebElement child : getChildren()) {
			if (child.getTag().equals("IMG") || child.containsImage())
				return true;
		}
		return false;
	}

	public void setFloatStr(String floatStr) {
		this.floatStr = floatStr;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public boolean hasValidChildren() {
		boolean state = isValid();
		if (children.size() != 0) {
			for (WebElement child : children) {
				state = state || child.hasValidChildren();
			}
		}
		return state;
	}

	public boolean areAllChildrenVirtualTextNodes() {
		boolean state = true;
		if (children.size() != 0) {
			for (WebElement child : children) {
				state = state
						&& (child.isVirtualTextNode() || child.isTextNode());
			}
		}
		return state;
	}

	public boolean isVirtualTextNode() {
		boolean state = (INLINE_NODES.contains(tag) || isTextNode()) ? true
				: false;
		if (getChildren().size() > 1)
			return false;
		// if(children.size() != 0){
		// for(WebElement child : children){
		// state = state && child.isVirtualTextNode();
		// }
		// }
		return state;
	}

	public boolean isVirtualTextNode(boolean condition) {
		boolean state = (INLINE_NODES.contains(tag) || isTextNode()) ? true
				: false;
		if (condition && getChildren().size() > 1)
			return false;
		return state;
	}

	public boolean isValid() {
		if (INVALID_NODES.contains(tag))
			return false;
		else if (style == null)
			return false;
		else if (style.getRectangle().width == 0
				|| style.getRectangle().height == 0)
			return false;
		else if (children.size() == 0) {
			return false;
		} else
			return true;
	}

	public boolean isTextNode() {
		if (getTag().equals("#TEXT"))
			return true;
		else
			return false;
	}

	public void drawNode(GC gc) {
		if (!isTextNode() && style != null && isValid()) {
			gc.setForeground(new Color(gc.getDevice(), 0, 0, 255));
			gc.drawRectangle(style.getRectangle());
			gc.setForeground(new Color(gc.getDevice(), 0, 0, 0));
			// for(WebElement child: children){
			// child.drawNode(gc);
			// }
		}
	}

	public void detectBordersFromChildren() {
		for (WebElement tempChild : getChildren()) {
			if (tempChild.getStyle() != null
					&& tempChild.getStyle().getRectangle() != null
					&& tempChild != getChildren().get(0)) {
				getStyle().getRectangle().union(
						tempChild.getStyle().getRectangle());
				getStyle().getRectangle().height += tempChild.getStyle()
						.getRectangle().height;
			}
		}
	}

	public boolean containsBR() {
		for (WebElement child : getChildren()) {
			if (child.getTag().equals("BR"))
				return true;
		}
		return false;
	}

	public boolean containsHR() {
		for (WebElement child : getChildren()) {
			if (child.getTag().equals("HR"))
				return true;
		}
		return false;
	}

	public boolean hasSibling() {
		if (parent.getChildren().size() == 1) {
			return false;
		} else {
			return true;
		}
	}

	public boolean hasPrevSibling() {
		if (parent.getChildren().indexOf(this) == 0) {
			return false;
		} else {
			return true;
		}
	}

	public WebElement getPrevSibling() {
		if (parent.getChildren().indexOf(this) == 0) {
			return null;
		} else {
			return parent.getChildren().get(
					parent.getChildren().indexOf(this) - 1);
		}
	}

	public boolean isLineBreakNode() {
		if (isValid() && !isVirtualTextNode() && !isTextNode()) {
			return true;
		} else if (isVirtualTextNode() && getChildren().size() > 1) {
			System.out.println("yeah");
			return true;
		} else {
			return false;
		}
	}

	public boolean includesALineBreakChild() {
		for (WebElement child : children) {
			if (child.isValid() && !child.isVirtualTextNode()
					&& !child.isTextNode()) {
				return true;
			}
		}
		return false;
	}

	public void addChild(WebElement child) {
		children.add(child);
	}

	public void removeChild(int index) {
		children.remove(index);
	}

	public WebElement getParent() {
		return parent;
	}

	public void setParent(WebElement parent) {
		this.parent = parent;
	}

	public ArrayList<WebElement> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<WebElement> children) {
		this.children = children;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag.toUpperCase();
	}

	public short getIndex() {
		return index;
	}

	public void setIndex(short index) {
		this.index = index;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public ICurrentStyles getStyle() {
		return style;
	}

	public void setStyle(ICurrentStyles style) {
		this.style = style;
	}

	public boolean hasDifferentFloatInChildren(int i) {
		String float_prop = getChildren().get(i).getFloatStr();
		for (; i < getChildren().size(); i++) {
			WebElement child = getChildren().get(i);
			String childFloat = child.getFloatStr();
			if (!childFloat.equals(float_prop)) {
				return true;
			}
			float_prop = childFloat;
		}
		return false;
	}

	public boolean hasDifferentPositionInChildren() {
		for (int i = 0; i < getChildren().size(); i++) {
			WebElement child = getChildren().get(i);
			String childPosition = child.getPosition();
			if (childPosition != null && !childPosition.equals("static"))
				return true;
		}
		return false;
	}

	public boolean hasDifferentMarginInChildren(int i) {
		for (; i < getChildren().size(); i++) {
			WebElement child = getChildren().get(i);
			String childMarginTop = child.getMarginTop();
			String childMarginBottom = child.getMarginBottom();
			if (isNonZeroMargin(childMarginTop) && i != 0) {
				return true;
			} else if (isNonZeroMargin(childMarginBottom)
					&& i != getChildren().size() - 1) {
				return true;
			}
		}
		return false;
	}

	private boolean isNonZeroMargin(String margin) {
		if (margin == null || margin.equals("0px") || margin.equals("auto"))
			return false;
		else
			return true;
	}

	public int getCountOfChildrenWithMaxFontSize() {
		int maxFontSize = getMaxFontSizeInChildren();
		int count = 0;
		for (int i = 0; i < getChildren().size(); i++) {
			WebElement child = getChildren().get(i);
			int childFontSize = child.getFontSize();
			if (childFontSize == maxFontSize)
				count++;
		}
		return count;
	}

	public boolean areAllMaxFontSizeChildrenAtFront() {
		int count = getCountOfChildrenWithMaxFontSize();
		int maxFontSize = getMaxFontSizeInChildren();

		for (int i = 0; i < count; i++) {
			WebElement child = getChildren().get(i);
			if (child.getFontSize() != maxFontSize)
				return false;
		}

		return true;
	}

	public int getMaxFontSizeInChildren() {
		int maxFontSize = 0;
		for (int i = 0; i < getChildren().size(); i++) {
			WebElement child = getChildren().get(i);
			int childFontSize = child.getFontSize();
			if (childFontSize > maxFontSize)
				maxFontSize = childFontSize;
		}
		return maxFontSize;
	}

	public boolean hasDifferentFontSizeInChildren(int i) {
		int fontSize = getChildren().get(i).getFontSize();
		for (; i < getChildren().size(); i++) {
			WebElement child = getChildren().get(i);
			int childFontSize = child.getFontSize();
			if (childFontSize != fontSize) {
				return true;
			}
			fontSize = childFontSize;
		}
		return false;
	}

	public int getFontSize() {
		if (fontSize != -1)
			return fontSize;

		if (this == null)
			return 16; // since medium is the default font-size
		else if (getStyle() == null || getStyle().getFontSize() == null) {
			if (getParent() == null)
				return 16;
			return getParent().getFontSize();
		}

		String fontSizeString = getStyle().getFontSize().trim();

		if (fontSizeString.equals("xx-small")) {
			return 9;
		} else if (fontSizeString.equals("x-small")) {
			return 10;
		} else if (fontSizeString.equals("small")) {
			return 13;
		} else if (fontSizeString.equals("medium")) {
			return 16;
		} else if (fontSizeString.equals("large")) {
			return 18;
		} else if (fontSizeString.equals("x-large")) {
			return 24;
		} else if (fontSizeString.equals("xx-large")) {
			return 32;
		} else if (fontSizeString.equals("smaller")) {
			return 84 * getParent().getFontSize() / 100;
		} else if (fontSizeString.equals("larger")) {
			return 120 * getParent().getFontSize() / 100;
		} else {
			try {
				if (fontSizeString.substring(fontSizeString.length() - 1,
						fontSizeString.length()).equals("%")) {
					String numericPart = fontSizeString.substring(0,
							fontSizeString.length() - 1);
					int fontSize = Integer.parseInt(numericPart);
					return fontSize;
				} else if (fontSizeString.substring(
						fontSizeString.length() - 2, fontSizeString.length())
						.equals("px")) {
					String numericPart = fontSizeString.substring(0,
							fontSizeString.length() - 2).trim();
					int fontSize = Integer.parseInt(numericPart);
					return fontSize;
				} else
					return 16;
			} catch (Exception e) {
				// TODO handle pokemon code!
				return 16;
			}
		}
	}

	public boolean hasDivGroups() {
		int divCount = 0;
		int lineBreakCount = 0;
		for (WebElement child : getChildren()) {
			if (child.getTag().equals("DIV")) {
				divCount++;
			} else if (!child.isVirtualTextNode(false) && child.isValid()) {
				lineBreakCount++;
			}
		}

		if (divCount > 0 && lineBreakCount > 0)
			return true;
		else
			return false;
	}

	public boolean containsEmptyListItem() {
		if (getTag().equals("UL")) {
			for (WebElement child : getChildren()) {
				if (child.getTag().equals("LI") && child.hasValidChildren())
					return true;
			}
		}
		return false;
	}

	public boolean hasChildContainingImage() {
		for (WebElement child : getChildren()) {
			if (child.containsImage())
				return true;
		}
		return false;
	}

}
