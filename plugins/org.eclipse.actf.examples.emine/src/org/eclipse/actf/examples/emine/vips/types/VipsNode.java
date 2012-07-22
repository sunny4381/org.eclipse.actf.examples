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

import java.util.ArrayList;
import java.util.HashMap;
import org.eclipse.actf.examples.emine.vips.DomStructureConstruction;
import org.eclipse.actf.model.dom.dombycom.IElementEx;
import org.eclipse.actf.model.ui.editor.browser.ICurrentStyles;

public class VipsNode {
	
	protected String tag;
	protected short index;
	protected double fontSize;
	protected String path;
	protected String id;
	protected String className;
	protected ICurrentStyles style;
	protected VipsNode parent;
	protected ArrayList<VipsNode> children;
	protected ArrayList<VipsLineBreak> lineBreakList;
	protected HashMap<String, String> styleMap;
	protected boolean isImage;
	protected boolean isLineBreakObjet;
	protected boolean isInlineObjet;
	protected int containsImage;
	protected int containsLineBreakObject;
	protected int containsInlineObject;
	protected boolean isTextNode;
	protected boolean isRootNode;
	protected boolean isExceptional;
	private IElementEx e;
	
	/**
	 * Constructor definition
	 */
	public VipsNode() {
		children = new ArrayList<VipsNode>();
		lineBreakList = new ArrayList<VipsLineBreak>();
		parent = null;
		containsImage = 0;
		containsLineBreakObject = 0;
		style = null;
		path = "";
		tag = "";
		fontSize = -1;
		id = "";
		className = "";
		isTextNode = false;
		isRootNode = false;
		isExceptional = false;
		styleMap = new HashMap<String, String>();
	}
	
	/* Getters and setters*/
	
	public void setRootNode(){
		isRootNode = true;
	}
	
	public boolean isExceptional() {
		return isExceptional;
	}

	public void setExceptional(boolean isExceptional) {
		this.isExceptional = isExceptional;
	}

	public boolean isTextNode(){
		return isTextNode;
	}
	
	public void setTextNode(boolean isTextNode) {
		this.isTextNode = isTextNode;
	}

	public IElementEx getE() {
		return e;
	}

	public void setE(IElementEx e) {
		this.e = e;
	}
	
	public boolean isInlineObjet() {
		return isInlineObjet;
	}

	public void setInlineObjet(boolean isInlineObjet) {
		this.isInlineObjet = isInlineObjet;
	}

	public boolean doesContainInlineObject() {
		if(containsInlineObject == 0)
			return false;
		else
			return true;
	}

	public void setContainsInlineObject(boolean containsInlineObject) {
		if(containsInlineObject)
			this.containsInlineObject += 1;
	}
	
	public int getInlineObjectCount(){
		return containsInlineObject;
	}

	public boolean isImage() {
		return isImage;
	}

	public void setImage(boolean isImage) {
		this.isImage = isImage;
	}

	public boolean isLineBreakObjet() {
		return isLineBreakObjet;
	}

	public void setLineBreakObjet(boolean isLineBreakObjet) {
		this.isLineBreakObjet = isLineBreakObjet;
	}
	
	public boolean doesContainImage() {
		if(containsImage == 0)
			return false;
		else
			return true;
	}
	
	public int getImageCount(){
		return containsImage;
	}

	public void setContainsImage(boolean containsImage) {
		if(containsImage)
			this.containsImage  += 1;
	}

	public boolean doesContainLineBreakObject() {
		if(containsLineBreakObject == 0)
			return false;
		else
			return true;
	}
	
	public int getLineBreakObjectCount(){
		return containsLineBreakObject;
	}
	
	public void setContainsLineBreakObject(boolean containsLineBreakObject) {
		if(containsLineBreakObject)
			this.containsLineBreakObject += 1;
	}

	public boolean isCompositeNode() {
		return false;
	}
	
	public void setBackground(String background){
		if(!background.equals("transparent"))
			styleMap.put("background", background);
	}
	
	public String getBackground() {
		if(styleMap.containsKey("background"))
			return styleMap.get("background");
		else
			return "transparent";
	}

	public String getPosition() {
		if(styleMap.containsKey("position"))
			return styleMap.get("position");
		else
			return "static";
	}

	public void setPosition(String position) {
		if(!position.equals("static"))
			styleMap.put("position", position);
	}

	public String getMarginLeft() {
		if(styleMap.containsKey("marginLeft"))
			return styleMap.get("marginLeft");
		else
			return "auto";
	}

	public void setMarginLeft(String marginLeft) {
		if(!marginLeft.equals("auto"))
			styleMap.put("marginLeft", marginLeft);
	}

	public String getMarginRight() {
		if(styleMap.containsKey("marginRight"))
			return styleMap.get("marginRight");
		else
			return "auto";
	}

	public void setMarginRight(String marginRight) {
		if(!marginRight.equals("auto"))
			styleMap.put("marginRight", marginRight);
	}

	public String getMarginTop() {
		if(styleMap.containsKey("marginTop"))
			return styleMap.get("marginTop");
		else
			return "auto";
	}

	public void setMarginTop(String marginTop) {
		if(!marginTop.equals("auto"))
			styleMap.put("marginTop", marginTop);
	}

	public String getMarginBottom() {
		if(styleMap.containsKey("marginBottom"))
			return styleMap.get("marginBottom");
		else
			return "auto";
	}

	public void setMarginBottom(String marginBottom) {
		if(!marginBottom.equals("auto"))
			styleMap.put("marginBottom", marginBottom);
	}
	
	public String getFontColor() {
		if(styleMap.containsKey("fontColor"))
			return styleMap.get("fontColor");
		else
			return "#000";
	}

	public void setFontColor(String fontColor) {
		if(!fontColor.equals("#000"))
			styleMap.put("fontColor", fontColor);
	}

	public String getFontWeight() {
		if(styleMap.containsKey("fontWeight"))
			return styleMap.get("fontWeight");
		else
			return "400";
	}

	public void setFontWeight(String fontWeight) {
		if(!fontWeight.equals("400"))
			styleMap.put("fontWeight", fontWeight);
	}

	public double getFontSize() {
		return fontSize;
	}
	
	public void setFontSize(double fontSize) {
		this.fontSize = fontSize;
	}

	public boolean hasChildren() {
		if (children.size() == 0)
			return false;
		else
			return true;
	}

	public String getFloatStr() {
		if(styleMap.containsKey("float"))
			return styleMap.get("float");
		else
			return "none";
	}

	public void setFloatStr(String floatValue) {
		if(!floatValue.equals("none"))
			styleMap.put("float", floatValue);
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
	
	public String getFontSizeStr() {
		if(styleMap.containsKey("fontSize"))
			return styleMap.get("fontSize");
		else
			return "medium";
	}

	public void setFontSizeStr(String fontSize) {
		if(!fontSize.equals("medium"))
			styleMap.put("fontSize", fontSize);
	}
	
	public VipsNode getParent() {
		return parent;
	}

	public void setParent(VipsNode parent) {
		this.parent = parent;
	}

	public ArrayList<VipsNode> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<VipsNode> children) {
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
	/*************** End of getters and setters *****************/
	
	public void addLineBreak(VipsLineBreak lineBreak){
		lineBreakList.add(lineBreak);
	}
	
	public ArrayList<VipsLineBreak> getLineBreaks(){
		return lineBreakList;
	}
	
	public boolean containsLineBreak(){
		if(lineBreakList == null || lineBreakList.size() == 0)
			return false;
		else
			return true;
	}
	
	public void addChild(VipsNode child) {
		children.add(child);
	}

	public void removeChild(int index) {
		children.remove(index);
	}

	public void removeChild(VipsNode child){
		children.remove(child);
	}
	
	public boolean hasValidChildren() {
		boolean state = isValid();
		if (children.size() != 0) {
			for (VipsNode child : children) {
				state = state || child.hasValidChildren();
			}
		}
		return state;
	}

	public boolean areAllChildrenVirtualTextNodes() {
		boolean state = true;
		if (children.size() != 0) {
			for (VipsNode child : children) {
				state = state
						&& (child.isVirtualTextNode() || child.isTextNode());
			}
		}
		return state;
	}

	public boolean isVirtualTextNode() {
		boolean state = (VipsNodeTypes.INLINE_NODES.contains(tag) || isTextNode() || getTag().equals("#TEXT")) ? true : false;
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
		boolean state = (VipsNodeTypes.INLINE_NODES.contains(tag) || isTextNode() || getTag().equals("#TEXT")) ? true
				: false;
		if (condition && getChildren().size() > 1)
			return false;
		return state;
	}

	public boolean isValid() {
		if (VipsNodeTypes.INVALID_NODES.contains(tag)){
			return false;
		} else if(!isRootNode && style != null && (style.getRectangle() == null || 
								  style.getRectangle().width == 0 || style.getRectangle().height == 0)){
			return false;
		} else if (children == null || children.size() == 0) {
			return false;
		} else
			return true;
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

	public VipsNode getPrevSibling() {
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
			return true;
		} else {
			return false;
		}
	}

	public boolean includesALineBreakChild() {
		for (VipsNode child : children) {
			if (child.isValid() && !child.isVirtualTextNode()
					&& !child.isTextNode()) {
				return true;
			}
		}
		return false;
	}

	public boolean hasDifferentFloatInChildren(int i) {
		String float_prop = getChildren().get(i).getFloatStr();
		for (; i < getChildren().size(); i++) {
			VipsNode child = getChildren().get(i);
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
			VipsNode child = getChildren().get(i);
			String childPosition = child.getPosition();
			if (childPosition != null && !childPosition.equals("static"))
				return true;
		}
		return false;
	}

	public boolean hasDifferentMarginInChildren(int i) {
		for (; i < getChildren().size(); i++) {
			VipsNode child = getChildren().get(i);
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

	protected boolean isNonZeroMargin(String margin) {
		if (margin == null || margin.equals("0px") || margin.equals("auto"))
			return false;
		else
			return true;
	}

	public int getCountOfChildrenWithMaxFontSize() {
		double maxFontSize = getMaxFontSizeInChildren();
		int count = 0;
		for (int i = 0; i < getChildren().size(); i++) {
			VipsNode child = getChildren().get(i);
			double childFontSize = child.getMaxFontSize();
			if (childFontSize == maxFontSize)
				count++;
		}
		return count;
	}

	public boolean areAllMaxFontSizeChildrenAtFront() {
		int count = getCountOfChildrenWithMaxFontSize();
		double maxFontSize = getMaxFontSizeInChildren();

		for (int i = 0; i < count; i++) {
			VipsNode child = getChildren().get(i);
			if (child.getMaxFontSize() != maxFontSize)
				return false;
		}

		return true;
	}

	public double getMaxFontSizeInChildren() {
		double maxFontSize = 0;
		for (int i = 0; i < getChildren().size(); i++) {
			VipsNode child = getChildren().get(i);
			double childFontSize = child.getMaxFontSize();
			maxFontSize = Math.max(childFontSize, maxFontSize);
		}
		return maxFontSize;
	}

	public boolean hasDifferentFontSizeInChildren(int i) {
		double fontSize = getChildren().get(i).getMaxFontSize();
		for (; i < getChildren().size(); i++) {
			VipsNode child = getChildren().get(i);
			double childFontSize = child.getMaxFontSize();
			if (childFontSize != fontSize) {
				return true;
			}
			fontSize = childFontSize;
		}
		return false;
	}

	public boolean hasDivGroups() {
		int divCount = 0;
		int lineBreakCount = 0;
		for (VipsNode child : getChildren()) {
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
		int count = getChildren().size();
		if (getTag().equals("UL")) {
			for (VipsNode child : getChildren()) {
				int index = getChildren().indexOf(child);
				if (child.isEmptyListItem() && index != count - 1)
					return true;
			}
		}
		return false;
	}
	
	public boolean childrenHaveColumns(){
		try {
			if(getChildren().size() <= 1)
				return false;
			int width = 0;
			int screenWidth = DomStructureConstruction.getWindowSizeX();
			if(getStyle() != null  && getStyle().getRectangle() != null){
				width = getStyle().getRectangle().width;
			
				if(width < screenWidth)
					return false;
			}
			
			DomStructureConstruction.getWindowSizeX();
			for(VipsNode child : getChildren()){
				int childWidth = 0;
				if(child.getStyle() != null  && child.getStyle().getRectangle() != null)
					childWidth = child.getStyle().getRectangle().width;
				if(screenWidth != childWidth)
					return true;
			}
		} catch(NullPointerException e){
			return false;
		}
		
		return false;
	}
	
	public int getMaxChildSize(){
		int max = 0;
		for(VipsNode child : getChildren()){
			try {
				int tmp = child.getStyle().getRectangle().width;
				max = Math.max(max, tmp);
			} catch (NullPointerException e){
				
			}
		}
		return max;
	}

	public boolean isEmptyListItem(){
		if (getTag().equals("LI") && !hasValidChildren())
			return true;
		else
			return false;
	}
	
	public double getMaxFontSize(){
		double childFont = -1;
		for(VipsNode child : getChildren()){
			childFont = Math.max(child.getMaxFontSize(), childFont);
		}
		return Math.max(childFont, getFontSize());
	}
	
	public boolean rowsHaveDifferentBgColor() {
		if(tag.matches("TBODY|TABLE")){
			for(VipsNode child : getChildren()){
				if(child.columnsHaveDifferentBgColor())
					return true;
			}
		}
		return false;
	}
	
	public boolean columnsHaveDifferentBgColor(){
		if(tag.matches("TR")){
			String bgColor = "transparent";
			for(VipsNode child : getChildren()){
				String childBgColor = child.getBackground();
				if(!childBgColor.equals("transparent")){
					if(!bgColor.equals("transparent") && !bgColor.equals(childBgColor))
						return true;
					bgColor = childBgColor;
				}
			}
		}
		return false;
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
	
	public void printNode(String indent) {
		System.out.println(indent + getPath());
		for (VipsNode child : getChildren()) {
			child.printNode(indent + "  ");
		}
	}
}
