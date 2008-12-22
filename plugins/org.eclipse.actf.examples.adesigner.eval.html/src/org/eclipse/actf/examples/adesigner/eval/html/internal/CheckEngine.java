/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.examples.adesigner.eval.html.internal;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.actf.visualization.eval.html.HtmlEvalUtil;
import org.eclipse.actf.visualization.eval.html.HtmlTagUtil;
import org.eclipse.actf.visualization.eval.problem.HighlightTargetNodeInfo;
import org.eclipse.actf.visualization.eval.problem.IProblemItem;
import org.eclipse.actf.visualization.eval.problem.ProblemItemImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLImageElement;

@SuppressWarnings("unused")
public class CheckEngine extends HtmlTagUtil {

	// use org.w3c.dom.traversal, XPath

	private static final String SPACE_STR = " ";

	private static final String WINDOW_OPEN = "window.open";

	public static final int ITEM_COUNT = 90;

	private static final int QUOTATION_SHORT_NUM = 10;

	private static final int TABLE_CELL_ABBR_CHARS = 30;

	private static final int TABLE_CELL_ABBR_WORDS = 10;

	private static final String[] ASCII_ART_CHAR = { "\u2227", "\uff3f",
			"\uffe3", "\uff20", "\uff0f", "\uff3c", "\u03b3", "\u03a6",
			"\u2229", "\u222a", "\u03b9", "\uff2f", "\u2282", "\uff9f",
			"\u0414", "\u03c3", "\uff65", "\u2200", "\u2211", "i", "o", "0",
			"_", "\uff3f", "\uffe3", "\u00b4", "\uff40", "\u30fe" };

	private static Set<String> artCharSet;

	static {
		artCharSet = new HashSet<String>();
		for (int i = 0; i < ASCII_ART_CHAR.length; i++) {
			artCharSet.add(ASCII_ART_CHAR[i]);
		}
	}

	private static Method[] checkMethods;
	private static Method[] mobileCheckMethods;

	static {
		Method[] tmpM = CheckEngine.class.getDeclaredMethods();
		checkMethods = new Method[100];// TODO
		mobileCheckMethods = new Method[100];// TODO
		for (Method m : tmpM) {
			String name = m.getName();
			if (name.startsWith("item_")) {
				try {
					int itemNum = Integer.parseInt(name.substring(5));
					checkMethods[itemNum] = m;
				} catch (Exception e) {
				}
			} else if (name.startsWith("mobile_")) {
				try {
					int itemNum = Integer.parseInt(name.substring(7));
					mobileCheckMethods[itemNum] = m;
				} catch (Exception e) {
				}
			}
		}
	}

	private static final String[] AUDIO_FILE_EXTENSION = {
			"mp3", "mid", "mrm", "mrl", "vqf", "wav" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

	private static final String[] MULTIMEDIA_FILE_EXTENSION = {
			"avi", "ram", "rm", "asf", "wm", "wmx", "wmv", "asx", "mpeg", "mpg" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$

	private Set<String> blockEleSet = HtmlTagUtil.getBlockElementSet();

	private Document target;

	private Document resultDoc;

	private URL baseUrl;

	private Map<Node, Integer> document2IdMap;

	// private Vector html2ViewMapData;

	private boolean isDBCS;

	private Vector<IProblemItem> result;

	private boolean[] items;

	private int validate_str_len;

	private int valid_total_text_len;

	private boolean hasAwithHref = false;

	private Element[] aWithHref_elements;

	private String[] aWithHref_hrefs;

	private String[] aWithHref_strings;

	private HTMLImageElement[] img_elements;

	private Element[] table_elements;

	private Element[] body_elements;

	private Element[] frame_elements;

	private Element[] iframe_elements;

	private Element[] object_elements;

	private Element[] parent_table_elements;

	private Element[] bottom_data_tables;

	private Element[] bottom_1row1col_tables;

	private Element[] bottom_notdata_tables;

	private Element[] headings;

	private double invalidLinkRatio;

	// private int invisibleElementCount = 0;

	// private String[] invisibleLinkStrings = new String[0];

	private HtmlEvalUtil edu;

	/**
	 * 
	 */
	public CheckEngine(HtmlEvalUtil edu, boolean[] checkItems) {
		this.edu = edu;
		this.target = edu.getTarget();
		this.resultDoc = edu.getResult();

		baseUrl = edu.getBaseUrl();

		invalidLinkRatio = 0;

		this.items = checkItems;

		this.document2IdMap = edu.getDocument2IdMap();
		// this.html2ViewMapData = edu.getHtml2ViewMapData();

		this.isDBCS = edu.isDBCS();
		if (isDBCS) {
			validate_str_len = 5;
			valid_total_text_len = 50;
		} else {
			validate_str_len = 30;
			valid_total_text_len = 100;
		}
		result = new Vector<IProblemItem>();

		hasAwithHref = edu.isHasAwithHref();

		aWithHref_elements = edu.getAWithHref_elements();
		aWithHref_hrefs = edu.getAWithHref_hrefs();
		aWithHref_strings = edu.getAWithHref_strings();

		img_elements = edu.getImg_elements();

		table_elements = edu.getTable_elements();
		bottom_data_tables = edu.getBottom_data_tables();
		bottom_1row1col_tables = edu.getBottom_1row1col_tables();
		bottom_notdata_tables = edu.getBottom_notdata_tables();
		parent_table_elements = edu.getParent_table_elements();
		body_elements = edu.getBody_elements();
		frame_elements = edu.getFrame_elements();
		iframe_elements = edu.getIframe_elements();
		object_elements = edu.getObject_elements();

		headings = edu.getHeadings();

	}

	public Vector<IProblemItem> check() {

		checkDomDifference();

		validateHtml();

		Object[] tmpO = null;
		for (int i = 0; i < items.length; i++) {
			if (items[i] && null != checkMethods[i]) {
				try {
					checkMethods[i].invoke(this, tmpO);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// always
		if (items.length < 90 || !items[89]) {
			item_89();
		}

		for (int i = 0; i < mobileCheckMethods.length; i++) {
			if (null != mobileCheckMethods[i]) {
				try {
					mobileCheckMethods[i].invoke(this, tmpO);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		edu.getPageData().setInvalidLinkRatio(invalidLinkRatio);

		return (result);
	}

	private IProblemItem addCheckerProblem(String strId) {
		IProblemItem tmpCP = new ProblemItemImpl(strId);
		result.add(tmpCP);
		return (tmpCP);
	}

	private IProblemItem addCheckerProblem(String strId, String targetStr) {
		IProblemItem tmpCP = new ProblemItemImpl(strId);
		tmpCP.setTargetString(targetStr);
		result.add(tmpCP);
		return (tmpCP);
	}

	private IProblemItem addCheckerProblem(String strId, Element target) {
		return (addCheckerProblem(strId, "", target)); //$NON-NLS-1$
	}

	private IProblemItem addCheckerProblem(String strId, String targetStr,
			Element target) {
		IProblemItem tmpCP = new ProblemItemImpl(strId, target);
		tmpCP.setHighlightTargetNodeInfo(new HighlightTargetNodeInfo(target));
		tmpCP.setTargetString(targetStr);
		result.add(tmpCP);

		return (tmpCP);
	}

	private IProblemItem addCheckerProblem(String strId, Element startTarget,
			Element endTarget) {
		IProblemItem tmpCP = new ProblemItemImpl(strId, startTarget);
		tmpCP.setHighlightTargetNodeInfo(new HighlightTargetNodeInfo(
				startTarget, endTarget));
		result.add(tmpCP);
		return (tmpCP);
	}

	private IProblemItem addCheckerProblem(String strId, String targetStr,
			Vector<Node> targetV) {
		IProblemItem tmpCP = new ProblemItemImpl(strId);
		tmpCP.setTargetString(targetStr);
		tmpCP.setHighlightTargetNodeInfo(new HighlightTargetNodeInfo(targetV));
		result.add(tmpCP);
		return tmpCP;
	}

	private void item_0() {
		NodeList nl = target.getElementsByTagName("applet"); //$NON-NLS-1$
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Element el = (Element) nl.item(i);
			boolean bHasAlt = false;
			boolean bHasText = false;

			// justify if has alt attribute

			String strAlt = el.getAttribute(ATTR_ALT); //$NON-NLS-1$
			if (strAlt.length() > 0) {
				bHasAlt = true;
			}

			String desText = getTextAltDescendant(el);
			bHasText = (desText.length() > 0);

			if (bHasAlt) {
				if (bHasText) {
					// check alt == text??
				} else {
					addCheckerProblem("C_0.1", el);
				}

			} else {
				if (bHasText) {
					addCheckerProblem("C_0.0", el); //$NON-NLS-1$
				} else {
					addCheckerProblem("C_0.2", el); //$NON-NLS-1$
				}
				// alternative link alert
				// check descendant text or image
				// addCheckerProblem("C_0.1", el);
			}

		}
	}

	private void item_1() {

		for (int i = 0; i < object_elements.length; i++) {
			Element el = object_elements[i];

			boolean bReported = false;

			boolean bHasTextImg = hasTextDescendant(el);
			if (!bHasTextImg) {
				NodeList imgNl = el.getElementsByTagName("img"); //$NON-NLS-1$
				if (imgNl.getLength() > 0) {
					// need alt description check
					bHasTextImg = true;
				}
			}
			if (!bHasTextImg) {
				// text or img check
				addCheckerProblem("C_1.1", el); //$NON-NLS-1$
				bReported = true;
			}

			if (!bReported) {
				// alert
				// check links inside an OBJECT element, or a description link.
				// addCheckerProblem("C_1.2", el);
			}
		}

	}

	private void item_2() {

		NodeList nl = target.getElementsByTagName("input"); //$NON-NLS-1$
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Element el = (Element) nl.item(i);
			String strType = el.getAttribute("type"); //$NON-NLS-1$
			if (strType != null && strType.equalsIgnoreCase("image")) { //$NON-NLS-1$
				String strUsemap;
				boolean bIsMap;
				try {
					bIsMap = ((HTMLImageElement) el).getIsMap();
					strUsemap = ((HTMLImageElement) el).getUseMap();
				} catch (RuntimeException e) {
					bIsMap = false;
					strUsemap = ""; //$NON-NLS-1$
				}
				if (bIsMap || !strUsemap.equals("")) { //$NON-NLS-1$
					// 1.1 server-side image map as submit button alert
					addCheckerProblem("C_2.0", el); //$NON-NLS-1$

					// // 9.1 alert: please use client-side image map
					// addCheckerProblem("C_2.2",el);
				}
			}
		}

		// nl = target.getElementsByTagName("img");
		// length = nl.getLength();
		for (int i = 0; i < img_elements.length; i++) {
			Element el = img_elements[i];
			String strUsemap = ((HTMLImageElement) el).getUseMap();
			if (((HTMLImageElement) el).getIsMap() && strUsemap.equals("")) { //$NON-NLS-1$
				// 1.2 server side image map
				addCheckerProblem("C_2.1", el); //$NON-NLS-1$

				// 9.1 alert: please use client-side image map
				addCheckerProblem("C_2.2", el); //$NON-NLS-1$
			}
		}
	}

	private void item_3() {
		for (int i = 0; i < img_elements.length; i++) {
			Element el = img_elements[i];
			if (el.hasAttribute("longdesc")) { //$NON-NLS-1$
				String strLongDesc = el.getAttribute("longdesc"); //$NON-NLS-1$
				if (strLongDesc.length() > 0) {
					boolean isDlink = false;
					// need to check distance
					int length = aWithHref_hrefs.length;
					for (int j = 0; j < length; j++) {
						// must use URL as in 58?
						if (strLongDesc.equalsIgnoreCase(aWithHref_hrefs[j])) {
							String strValue = aWithHref_strings[j];
							if (strValue.trim().equalsIgnoreCase("d")) { //$NON-NLS-1$
								isDlink = true;
								break;
							}
						}
					}
					if (!isDlink) { // d link check
						addCheckerProblem("C_3.0", el); //$NON-NLS-1$
					}
				}
			}
		}
	}

	private void item_4() {
		// need to refine conditions
		for (int i = 0; i < img_elements.length; i++) {
			Element el = img_elements[i];
			if (isNormalImage(el)) {
				if (el.hasAttribute(ATTR_ALT)) { //$NON-NLS-1$
					String strAlt = el.getAttribute(ATTR_ALT); //$NON-NLS-1$
					if (getWordCount(strAlt) >= 3
							|| strAlt.length() >= validate_str_len) {
						if (!strAlt.matches("\\p{ASCII}*")
								|| strAlt.length() > 30) {
							addCheckerProblem("C_4.0", el); //$NON-NLS-1$
						}
					}
				}
			}
		}
	}

	private void item_5() {
		int length = aWithHref_elements.length;
		for (int i = 0; i < length; i++) {
			Element el = aWithHref_elements[i];
			String strHref = aWithHref_hrefs[i];
			if (strHref != null && strHref.length() > 0) {
				String strExt = getFileExtension(strHref);
				if (isAudioFileExt(strExt)) {
					// 1.1 audio alert
					addCheckerProblem("C_5.0", el); //$NON-NLS-1$
					break;
				} else if (isMultimediaFileExt(strExt)) {
					// 1.1
					addCheckerProblem("C_5.1", el); //$NON-NLS-1$
					// 1.3
					addCheckerProblem("C_5.2", el); //$NON-NLS-1$
					// 1.4
					addCheckerProblem("C_5.3", el); //$NON-NLS-1$
					break;
				}
			}
		}
	}

	private void item_6() {
		if (body_elements.length > 0) {
			Element bodyEl = body_elements[0];
			Stack<Node> stack = new Stack<Node>();
			Node curNode = bodyEl;

			while (curNode != null) {
				boolean isArtStr = false;
				if (isLeafBlockEle(curNode)
						&& isAsciiArtString(getTextAltDescendant(curNode))) {
					addCheckerProblem("C_6.0", (Element) curNode); //$NON-NLS-1$
					isArtStr = true;
				}

				if (!isArtStr && curNode.hasChildNodes()) {
					stack.push(curNode);
					curNode = curNode.getFirstChild();
				} else if (curNode.getNextSibling() != null) {
					curNode = curNode.getNextSibling();
				} else {
					curNode = null;
					while ((curNode == null) && (stack.size() > 0)) {
						curNode = (Node) stack.pop();
						curNode = curNode.getNextSibling();
					}
				}
			}
		}
	}

	private void item_7() {
		int aLength = aWithHref_elements.length;
		NodeList nl = target.getElementsByTagName("area"); //$NON-NLS-1$
		int areaLen = nl.getLength();
		for (int i = 0; i < areaLen; i++) {
			boolean bHasLink = false;
			Element el = (Element) nl.item(i);
			String strHref = ""; //$NON-NLS-1$
			if (el.hasAttribute(HtmlTagUtil.ATTR_HREF)) { //$NON-NLS-1$
				strHref = el.getAttribute(HtmlTagUtil.ATTR_HREF); //$NON-NLS-1$
				if (strHref.length() > 0) {
					for (int j = 0; j < aLength; j++) {
						// TODO use URL
						if (strHref.equalsIgnoreCase(aWithHref_hrefs[j])) {
							bHasLink = true;
							break;
						}
					}
				}
			}
			if (!bHasLink) { // link check
				addCheckerProblem("C_7.0", " (href=\"" + strHref + "\")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
	}

	private void item_8() {
		// TODO add color info to Description
		NodeList nl = target.getElementsByTagName("font"); //$NON-NLS-1$
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Element el = (Element) nl.item(i);
			if (el.hasAttribute("color") || el.hasAttribute("bgcolor")) { //$NON-NLS-1$ //$NON-NLS-2$
				String strColor, strBgColor;
				strColor = el.getAttribute("color"); //$NON-NLS-1$
				strBgColor = el.getAttribute("bgcolor"); //$NON-NLS-1$
				if (!strColor.equals("") || !strBgColor.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
					addCheckerProblem("C_8.0", el); //$NON-NLS-1$
				}
			}
		}
		// need to handle CSS
	}

	private void item_9() {
		// Use markup language alert
		boolean bHasNormalImage = false;
		for (int i = 0; i < img_elements.length; i++) {
			Element el = img_elements[i];
			if (isNormalImage(el)) {
				bHasNormalImage = true;
				break;
			}
		}
		if (bHasNormalImage)
			addCheckerProblem("C_9.0"); //$NON-NLS-1$
	}

	private void item_10() {
		// formal grammars alert
		addCheckerProblem("C_10.0"); //$NON-NLS-1$
	}

	private void item_12() {
		for (int i = 0; i < parent_table_elements.length; i++) {
			Element el = parent_table_elements[i];
			Stack<Node> stack = new Stack<Node>();
			Node curNode = el.getFirstChild();
			int tableCount = 0;
			int maxCount = 0;
			String strName = ""; //$NON-NLS-1$
			while (curNode != null) {
				strName = curNode.getNodeName();
				if (curNode.getNodeType() == Node.ELEMENT_NODE
						&& strName.equalsIgnoreCase("table")) { //$NON-NLS-1$
					tableCount++;
					if (maxCount < tableCount)
						maxCount = tableCount;
				}

				if (curNode.hasChildNodes()) {
					stack.push(curNode);
					curNode = curNode.getFirstChild();
				} else if (curNode.getNextSibling() != null) {
					if ((curNode.getNodeType() == Node.ELEMENT_NODE)
							&& curNode.getNodeName().equals("table")) { //$NON-NLS-1$
						tableCount--;
					}
					curNode = curNode.getNextSibling();
				} else {
					if ((curNode.getNodeType() == Node.ELEMENT_NODE)
							&& curNode.getNodeName().equals("table")) { //$NON-NLS-1$
						tableCount--;
					}
					curNode = null;
					while ((curNode == null) && (stack.size() > 0)) {
						curNode = (Node) stack.pop();
						if ((curNode.getNodeType() == Node.ELEMENT_NODE)
								&& curNode.getNodeName().equals("table")) { //$NON-NLS-1$
							tableCount--;
						}
						curNode = curNode.getNextSibling();
					}
				}
			}
			if (maxCount > 0) {
				String str = null;
				if (maxCount == 1) {
					str = Messages.CheckEngine_ChildTable;
				} else {
					str = MessageFormat.format(
							Messages.CheckEngine_TieredChildTable, String
									.valueOf(maxCount)); //$NON-NLS-1$ //$NON-NLS-2$
				}
				addCheckerProblem("C_12.0", str, el); //$NON-NLS-1$
			}
		}
		for (int i = 0; i < bottom_1row1col_tables.length; i++) {
			addCheckerProblem("C_12.1", //$NON-NLS-1$
					bottom_1row1col_tables[i]);
		}

		for (int i = 0; i < bottom_notdata_tables.length; i++) {
			addCheckerProblem("C_12.2", //$NON-NLS-1$
					bottom_notdata_tables[i]);
		}
	}

	private void item_13() {
		// font
		NodeList nl = target.getElementsByTagName("font"); //$NON-NLS-1$
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Element el = (Element) nl.item(i);
			if (el.hasAttribute("size")) { //$NON-NLS-1$
				String strSize = el.getAttribute("size"); //$NON-NLS-1$
				if (strSize != null && strSize.length() > 0) {
					if (strSize.indexOf("+") == -1 //$NON-NLS-1$
							&& strSize.indexOf("-") == -1) { // absolute size
						// //$NON-NLS-1$
						addCheckerProblem("C_13.0", el); //$NON-NLS-1$
					}
				}
			}
		} // table
		checkAbsoluteSize("table"); //$NON-NLS-1$
		// tr
		checkAbsoluteSize("tr"); //$NON-NLS-1$
		// td
		checkAbsoluteSize("td"); //$NON-NLS-1$
		// col
		checkAbsoluteSize("col"); //$NON-NLS-1$
		// frames,style sheets?
	}

	private void item_14() {

		int curLevel = 0;
		int lastLevel = 0;
		for (int i = 0; i < headings.length; i++) {
			curLevel = edu.getHeadingLevel(headings[i].getNodeName());
			if (curLevel > 0) {
				if (lastLevel > 0) {
					if (curLevel - lastLevel > 1) {
						// heading level check

						String targetStr = MessageFormat.format(
								Messages.CheckEngine_Headings, new Object[] {
										curLevel, lastLevel });
						Vector<Node> tmpV = new Vector<Node>();
						tmpV.add(headings[i - 1]);
						tmpV.add(headings[i]);
						addCheckerProblem("C_14.0", targetStr, tmpV);
					}
				}
				lastLevel = curLevel;
			}
		}

	}

	private void item_15() {
		Vector<Node> targetV = new Vector<Node>();

		for (int i = 0; i < headings.length; i++) {
			targetV.add(headings[i]);
		}

		if (targetV.size() > 0) {
			addCheckerProblem("C_15.0", "", targetV);
		}
	}

	private void item_16() {
		// DL, DT, DD, UL, OL, LI
		// checkListElement("dl");
		// checkListElement("dt");
		// checkListElement("dd");
		// checkListElement("ul");
		// checkListElement("ol");
		// checkListElement("li");
		NodeList nl = target.getElementsByTagName("ol"); //$NON-NLS-1$
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Element el = (Element) nl.item(i);
			NodeList liNl = el.getElementsByTagName("li"); //$NON-NLS-1$
			if (liNl.getLength() == 0) { // list element alert
				addCheckerProblem("C_16.1", el); //$NON-NLS-1$
			}
		}

		nl = target.getElementsByTagName("ul"); //$NON-NLS-1$
		length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Element el = (Element) nl.item(i);
			NodeList liNl = el.getElementsByTagName("li"); //$NON-NLS-1$
			if (liNl.getLength() == 0) { // list element alert
				addCheckerProblem("C_16.1", el); //$NON-NLS-1$
			}
		}

		nl = target.getElementsByTagName("li"); //$NON-NLS-1$
		length = nl.getLength();
		Vector<Node> liVec = new Vector<Node>();
		for (int i = 0; i < length; i++) {
			boolean bHasUlOl = false;
			Node curNode = nl.item(i);
			if (liVec.contains(curNode))
				continue;
			String strName = ""; //$NON-NLS-1$
			while (curNode != null) {
				strName = curNode.getNodeName();
				if (strName.equalsIgnoreCase("ol") //$NON-NLS-1$
						|| strName.equalsIgnoreCase("ul")) { //$NON-NLS-1$
					bHasUlOl = true;
					break;
				} else if (strName.equalsIgnoreCase("body")) { //$NON-NLS-1$
					break;
				}
				curNode = curNode.getParentNode();
			}
			if (!bHasUlOl) {
				Node startNode = nl.item(i);
				Node endNode = nl.item(i);
				liVec.add(nl.item(i));
				Node preNode = nl.item(i).getPreviousSibling();
				while (preNode != null) {
					if (preNode.getNodeName().equalsIgnoreCase("li") //$NON-NLS-1$
							&& !liVec.contains(preNode)) {
						liVec.add(preNode);
						startNode = preNode;
					} else
						break;
					preNode = preNode.getPreviousSibling();
				}
				Node nextNode = nl.item(i).getNextSibling();
				while (nextNode != null) {
					if (nextNode.getNodeName().equalsIgnoreCase("li") //$NON-NLS-1$
							&& !liVec.contains(nextNode)) {
						liVec.add(nextNode);
						endNode = nextNode;
					} else
						break;
					nextNode = nextNode.getNextSibling();
				}
				// list element alert
				addCheckerProblem(
						"C_16.2", (Element) startNode, (Element) endNode); //$NON-NLS-1$
			}
		}

		// dl,dt,dd
		// dir,menu > obsolete
	}

	private void item_17() {
		NodeList nl = target.getElementsByTagName("q"); //$NON-NLS-1$
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			boolean bHasText = false;
			Node qNode = nl.item(i);
			bHasText = hasTextDescendant(qNode);
			if (!bHasText) { // quotation check
				Element el = (Element) qNode;
				addCheckerProblem("C_17.0", el); //$NON-NLS-1$
			}
		} // blockquote
		nl = target.getElementsByTagName("blockquote"); //$NON-NLS-1$
		length = nl.getLength();
		for (int i = 0; i < length; i++) {
			boolean bHasText = false;
			Node qNode = nl.item(i);
			bHasText = hasTextDescendant(qNode);
			if (!bHasText) { // quotation check
				Element el = (Element) qNode;
				addCheckerProblem("C_17.0", el); //$NON-NLS-1$
			}
		}
	}

	private void item_18() {
		NodeList nl = target.getElementsByTagName("q"); //$NON-NLS-1$
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Node qNode = nl.item(i);
			String str = getTextDescendant(qNode);
			if (str.length() > QUOTATION_SHORT_NUM) { // longer quotation
				// check
				Element el = (Element) qNode;
				addCheckerProblem("C_18.0", el); //$NON-NLS-1$

				str = el.getAttribute("cite"); //$NON-NLS-1$
				if (str.equals("")) { //$NON-NLS-1$
					// cite attribute check
					addCheckerProblem("C_18.2", el); //$NON-NLS-1$
				}
			}
		}

		// blockquote
		nl = target.getElementsByTagName("blockquote"); //$NON-NLS-1$
		length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Node qNode = nl.item(i);
			Element el = (Element) qNode;
			String str = getTextDescendant(qNode);
			if (str.length() <= QUOTATION_SHORT_NUM) {
				// short quotation check
				addCheckerProblem("C_18.1", el); //$NON-NLS-1$
			}

			str = el.getAttribute("cite"); //$NON-NLS-1$
			if (str.equals("")) { //$NON-NLS-1$
				// cite attribute check
				addCheckerProblem("C_18.2", el); //$NON-NLS-1$
			}
		}
	}

	private void item_19() {
		// language check
		addCheckerProblem("C_19.0"); //$NON-NLS-1$
	}

	private void item_20() {
		// Use the ABBR and ACRONYM elements alert
		// check sequence of capital
		addCheckerProblem("C_20.0"); //$NON-NLS-1$
	}

	private void item_21() {
		NodeList nl = target.getElementsByTagName("html"); //$NON-NLS-1$
		if (nl.getLength() > 0) {
			boolean bValidLan = false;
			Element el = (Element) nl.item(0);
			String strLan = el.getAttribute("lang"); //$NON-NLS-1$
			if (strLan != null && strLan.length() > 0) {
				bValidLan = isValidLang(strLan);
			}
			if (!bValidLan) { // language check
				addCheckerProblem("C_21.0", el); //$NON-NLS-1$
			}
		}
	}

	private void item_22() {
		if (table_elements.length > 0) {
			addCheckerProblem("C_22.0"); //$NON-NLS-1$
		}

	}

	private void item_23() {
		for (int i = 0; i < parent_table_elements.length; i++) {
			boolean bHasTh = false;
			Element tNode = parent_table_elements[i];
			Stack<Node> stack = new Stack<Node>();
			Node curNode = tNode.getFirstChild();
			while (curNode != null) {
				String strName = ""; //$NON-NLS-1$
				strName = curNode.getNodeName();
				if (strName.equalsIgnoreCase("th")) { //$NON-NLS-1$
					bHasTh = true;
					break;
				}

				if (curNode.hasChildNodes()
						&& !strName.equalsIgnoreCase("table")) { //$NON-NLS-1$
					stack.push(curNode);
					curNode = curNode.getFirstChild();
				} else if (curNode.getNextSibling() != null) {
					curNode = curNode.getNextSibling();
				} else {
					curNode = null;
					while ((curNode == null) && (stack.size() > 0)) {
						curNode = (Node) stack.pop();
						curNode = curNode.getNextSibling();
					}
				}
			}

			if (bHasTh) { // table aleret
				addCheckerProblem("C_23.0", tNode); //$NON-NLS-1$
			}

			// bottom table check(layout / not)

		}
	}

	private void item_25() {
		for (int i = 0; i < bottom_data_tables.length; i++) {
			Element el = bottom_data_tables[i];
			if (el.getElementsByTagName("th").getLength() > 0) { //$NON-NLS-1$
				NodeList capNl = el.getElementsByTagName("caption"); //$NON-NLS-1$
				String strSum = el.getAttribute("summary"); //$NON-NLS-1$
				if (capNl.getLength() == 0) {
					if (strSum.equals("")) { //$NON-NLS-1$
						// caption and summary alert
						addCheckerProblem("C_25.0", el); //$NON-NLS-1$
					} else {
						// caption alert
						addCheckerProblem("C_25.1", el); //$NON-NLS-1$
					}
				} else if (strSum.equals("")) { //$NON-NLS-1$
					// summary alert
					addCheckerProblem("C_25.2", el); //$NON-NLS-1$
				}
			}
		}
	}

	private void item_26() {
		NodeList nl = target.getElementsByTagName("th"); //$NON-NLS-1$
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Element el = (Element) nl.item(i);
			String str = getTextAltDescendant(el);
			if (isDBCS) {
				if (str.length() > TABLE_CELL_ABBR_CHARS) {
					addCheckerProblem("C_26.0", el); //$NON-NLS-1$            		
				}
			} else {
				if (getWordCount(str) > TABLE_CELL_ABBR_WORDS) {
					addCheckerProblem("C_26.0", el); //$NON-NLS-1$
				}
			}
		}
	}

	private void item_27() {
		for (int i = 0; i < frame_elements.length; i++) {
			Element el = frame_elements[i];
			String strSrc = el.getAttribute(ATTR_SRC); //$NON-NLS-1$

			if (!isHtmlFile(strSrc)) { // no-html file check
				addCheckerProblem("C_27.0", el); //$NON-NLS-1$
			}
		}

		// nl = target.getElementsByTagName("iframe"); //$NON-NLS-1$
		// length = nl.getLength();
		// for (int i = 0; i < length; i++) {
		// boolean bHasHtml = true;
		// Element el = (Element)nl.item(i);
		// String strSrc = el.getAttribute("src"); //$NON-NLS-1$
		//
		// if (!isHtmlFile(strSrc)) { // no-html file check
		// addCheckerProblem("C_27.1", el); //$NON-NLS-1$
		// }
		// }
	}

	private void item_28() {
		// need to check script/noscript mapping
		int scrSize = edu.getScript_elements().length;
		if (scrSize > 0) {
			NodeList nl = target.getElementsByTagName("noscript"); //$NON-NLS-1$
			if (scrSize > nl.getLength()) { // noscript check
				addCheckerProblem("C_28.0"); //$NON-NLS-1$
			}
		}
	}

	private void item_29() {
		NodeList aNl = target.getElementsByTagName("applet"); //$NON-NLS-1$

		if (edu.isHasJavascript() || edu.getScript_elements().length > 0) {
			addCheckerProblem("C_29.0"); //$NON-NLS-1$
		}

		int appletNum = aNl.getLength();
		for (int i = 0; i < appletNum; i++) {
			addCheckerProblem("C_29.2", //$NON-NLS-1$
					(Element) aNl.item(i));
		}

		for (int i = 0; i < aWithHref_hrefs.length; i++) {
			String str = aWithHref_hrefs[i];
			if (str.toLowerCase().startsWith("javascript:")) { //$NON-NLS-1$
				addCheckerProblem("C_29.1", //$NON-NLS-1$
						" (href=\"" + str + "\")", //$NON-NLS-2$ //$NON-NLS-1$
						aWithHref_elements[i]);
			}
		}
	}

	private void item_30() {
		boolean bHasObject = false;
		if (object_elements.length > 0)
			bHasObject = true;
		else {
			NodeList nl = target.getElementsByTagName("applet"); //$NON-NLS-1$
			if (nl.getLength() > 0)
				bHasObject = true;
		}
		if (bHasObject) { // object alert
			result.add(new ProblemItemImpl("C_30.0")); //$NON-NLS-1$
		}

	}

	private void item_31() {
		NodeList nl = target.getElementsByTagName("frameset"); //$NON-NLS-1$
		if (nl.getLength() > 0) {
			NodeList noNl = target.getElementsByTagName("noframes"); //$NON-NLS-1$
			if (noNl.getLength() == 0) { // noframes check
				Element el = (Element) nl.item(0);
				addCheckerProblem("C_31.0", el); //$NON-NLS-1$
			}
		}
	}

	private void item_32() {
		if (object_elements.length > 0) { // object alert
			result.add(new ProblemItemImpl("C_32.0")); //$NON-NLS-1$
		} else {
			NodeList nl = target.getElementsByTagName("applet"); //$NON-NLS-1$
			if (nl.getLength() > 0) { // object alert
				result.add(new ProblemItemImpl("C_32.0")); //$NON-NLS-1$
			}
		}
	}

	private void item_33() {
		NodeList nl = target.getElementsByTagName("blink"); //$NON-NLS-1$
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			boolean bHasText = false;
			Element el = (Element) nl.item(i);
			bHasText = hasTextDescendant(el);
			if (bHasText) { // blink text check
				addCheckerProblem("C_33.0", el); //$NON-NLS-1$
			}
		}
	}

	private void item_34() {
		NodeList nl = target.getElementsByTagName("marquee"); //$NON-NLS-1$
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Element el = (Element) nl.item(i);
			// marquee check
			addCheckerProblem("C_34.0", el); //$NON-NLS-1$
		}
	}

	private void item_35() {
		for (int i = 0; i < img_elements.length; i++) {
			Element el = img_elements[i];
			if (isNormalImage(el) && el.hasAttribute(ATTR_SRC)) { //$NON-NLS-1$
				String strSrc = el.getAttribute(ATTR_SRC); //$NON-NLS-1$
				if (strSrc != null && strSrc.length() > 0) {
					String strExt = getFileExtension(strSrc);
					if (strExt.equalsIgnoreCase("gif")) { //$NON-NLS-1$
						// gif image check
						addCheckerProblem("C_35.0", //$NON-NLS-1$
								" (src=\"" + strSrc + "\")", //$NON-NLS-1$ //$NON-NLS-2$
								el);
					}
				}
			}
		}
	}

	private void item_36() {
		NodeList nl = target.getElementsByTagName("meta"); //$NON-NLS-1$
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Element el = (Element) nl.item(i);

			if (el.hasAttribute("http-equiv")) { //$NON-NLS-1$
				String strMeta = el.getAttribute("http-equiv"); //$NON-NLS-1$
				if (strMeta != null && strMeta.equalsIgnoreCase("refresh")) { //$NON-NLS-1$
					String strCon = el.getAttribute("content"); //$NON-NLS-1$
					if (strCon == null
							|| strCon.toLowerCase().indexOf("url") < 0) { //$NON-NLS-1$
						// refresh itself check
						addCheckerProblem("C_36.0", el); //$NON-NLS-1$
					} else {
						// if (strCon != null
						// && strCon.toLowerCase().indexOf("url") >= 0) {
						// //$NON-NLS-1$
						// redirect check
						addCheckerProblem("C_36.1", el); //$NON-NLS-1$
					}
				}
			}
		}
	}

	private void item_38() {
		Element[] mouseButton = edu.getEventMouseButtonElements();
		Element[] mouseFocus = edu.getEventOnMouseElements();
		// Element[] onKey = edu.getEventOnKeyElements();

		HashSet<Element> tmpSet = new HashSet<Element>();
		for (int i = 0; i < mouseButton.length; i++) {
			Element el = mouseButton[i];
			tmpSet.add(el);
			if (el.hasAttribute(ATTR_ONKEYDOWN)
					|| el.hasAttribute(ATTR_ONKEYPRESS)
					|| el.hasAttribute(ATTR_ONKEYUP)) {
				// info (confirm)
			} else {
				addCheckerProblem("C_38.0", el);
			}
		}
		for (int i = 0; i < mouseFocus.length; i++) {
			Element el = mouseFocus[i];
			if (tmpSet.add(el)) {
				if (el.hasAttribute(ATTR_ONFOCUS)
						|| el.hasAttribute(ATTR_ONBLUR)
						|| el.hasAttribute(ATTR_ONSELECT)) {
					// info (confirm)
				} else {
					addCheckerProblem("C_38.0", el);
				}
			}
		}

	}

	private void item_39() {
		boolean bHasTabIndex = false;
		if (body_elements.length > 0) {
			Element bodyEl = body_elements[0];
			Stack<Node> stack = new Stack<Node>();
			Node curNode = bodyEl;
			while (curNode != null) {
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					Element el = (Element) curNode;
					if (el.hasAttribute("tabindex")) { //$NON-NLS-1$
						String str = el.getAttribute("tabindex"); //$NON-NLS-1$
						if (!str.equals("")) { //$NON-NLS-1$
							bHasTabIndex = true;
							break;
						}
					}
				}

				if (curNode.hasChildNodes()) {
					stack.push(curNode);
					curNode = curNode.getFirstChild();
				} else if (curNode.getNextSibling() != null) {
					curNode = curNode.getNextSibling();
				} else {
					curNode = null;
					while ((curNode == null) && (stack.size() > 0)) {
						curNode = (Node) stack.pop();
						curNode = curNode.getNextSibling();
					}
				}
			}
		}
		if (!bHasTabIndex) {
			// tabindex check
			addCheckerProblem("C_39.0"); //$NON-NLS-1$
		}
	}

	private void item_40() {
		boolean bHasAccess = false;
		for (int i = 0; i < aWithHref_elements.length; i++) {
			Element el = aWithHref_elements[i];
			if (el.hasAttribute("accesskey")) { //$NON-NLS-1$
				String str = el.getAttribute("accesskey"); //$NON-NLS-1$
				if (str.length() > 0) {
					bHasAccess = true;
					break;
				}
			}
		}
		if (!bHasAccess) {
			// alert to add keyboard shortcut to frequently used links
			addCheckerProblem("C_40.0"); //$NON-NLS-1$
		}
	}

	private void item_41() {
		boolean bHasAccess = false;
		NodeList nl = target.getElementsByTagName("form"); //$NON-NLS-1$
		int length = nl.getLength();
		if (length == 0)
			return;
		for (int i = 0; i < length; i++) {
			Element formEl = (Element) nl.item(i);
			Stack<Node> stack = new Stack<Node>();
			Node curNode = formEl.getFirstChild();
			while (curNode != null) {
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					Element el = (Element) curNode;
					if (el.hasAttribute("accesskey")) { //$NON-NLS-1$
						String str = el.getAttribute("accesskey"); //$NON-NLS-1$
						if (!str.equals("")) { //$NON-NLS-1$
							bHasAccess = true;
							break;
						}
					}
				}

				if (curNode.hasChildNodes()) {
					stack.push(curNode);
					curNode = curNode.getFirstChild();
				} else if (curNode.getNextSibling() != null) {
					curNode = curNode.getNextSibling();
				} else {
					curNode = null;
					while ((curNode == null) && (stack.size() > 0)) {
						curNode = (Node) stack.pop();
						curNode = curNode.getNextSibling();
					}
				}
			}

			if (bHasAccess)
				break;
		}
		if (!bHasAccess) {
			// alert to furnish keyboard shortcut for form elements
			addCheckerProblem("C_41.0"); //$NON-NLS-1$
		}

	}

	private void item_42() {
		for (int i = 0; i < aWithHref_elements.length; i++) {
			Element el = aWithHref_elements[i];
			if (el.hasAttribute("target")) { //$NON-NLS-1$
				String strTarget = el.getAttribute("target"); //$NON-NLS-1$
				if (!strTarget.equals("") //$NON-NLS-1$
						&& !strTarget.equalsIgnoreCase("_top") //$NON-NLS-1$
						&& !strTarget.equalsIgnoreCase("_self") //$NON-NLS-1$
						&& !strTarget.equalsIgnoreCase("top") //$NON-NLS-1$
						&& !strTarget.equalsIgnoreCase("self")) { //$NON-NLS-1$
					// && (strTarget.equalsIgnoreCase("_blank")
					// || strTarget.equalsIgnoreCase("blank")
					// || strTarget.equalsIgnoreCase("_new")
					// || strTarget.equalsIgnoreCase("new")
					// || strTarget.equalsIgnoreCase("_top")
					// || strTarget.equalsIgnoreCase("top"))) {
					// popup new window alert
					addCheckerProblem("C_42.0", el); //$NON-NLS-1$
				}
			}
		}
	}

	private void item_43() {

		boolean bHasProblem = false;
		Element[] tmpE = edu.getScript_elements();
		int length = tmpE.length;
		for (int i = 0; i < length; i++) {
			Element el = tmpE[i];
			Node curChild = el.getFirstChild();
			while (curChild != null) {
				if (curChild.getNodeType() == Node.CDATA_SECTION_NODE) {
					String strTxt = curChild.getNodeValue();
					if (strTxt != null
							&& strTxt.toLowerCase().indexOf(WINDOW_OPEN) >= 0) { //$NON-NLS-1$
						bHasProblem = true;
						break;
					}
				}
				curChild = curChild.getNextSibling();
			}
			if (bHasProblem) {
				result.add(new ProblemItemImpl("C_43.0")); //$NON-NLS-1$  
				break;
			}
		}

		HashSet<Element> tmpSet = new HashSet<Element>();
		tmpE = edu.getEventFocusElements();
		for (int i = 0; i < tmpE.length; i++) {
			if (hasOpenWndEvent(tmpE[i], HtmlEvalUtil.EVENT_FOCUS)
					&& tmpSet.add(tmpE[i])) {
				result.add(new ProblemItemImpl("C_43.0", tmpE[i]));
			}
		}
		tmpE = edu.getEventLoadElements();
		for (int i = 0; i < tmpE.length; i++) {
			if (hasOpenWndEvent(tmpE[i], HtmlEvalUtil.EVENT_LOAD)
					&& tmpSet.add(tmpE[i])) {
				result.add(new ProblemItemImpl("C_43.0", tmpE[i]));
			}
		}
		tmpE = edu.getEventMouseButtonElements();
		for (int i = 0; i < tmpE.length; i++) {
			if (hasOpenWndEvent(tmpE[i], HtmlEvalUtil.EVENT_MOUSE_BUTTON)
					&& tmpSet.add(tmpE[i])) {
				result.add(new ProblemItemImpl("C_43.0", tmpE[i]));
			}
		}
		tmpE = edu.getEventOnMouseElements();
		for (int i = 0; i < tmpE.length; i++) {
			if (hasOpenWndEvent(tmpE[i], HtmlEvalUtil.EVENT_MOUSE_FOCUS)
					&& tmpSet.add(tmpE[i])) {
				result.add(new ProblemItemImpl("C_43.0", tmpE[i]));
			}
		}
		tmpE = edu.getEventOnKeyElements();
		for (int i = 0; i < tmpE.length; i++) {
			if (hasOpenWndEvent(tmpE[i], HtmlEvalUtil.EVENT_ON_KEY)
					&& tmpSet.add(tmpE[i])) {
				result.add(new ProblemItemImpl("C_43.0", tmpE[i]));
			}
		}
		tmpE = edu.getEventWindowElements();
		for (int i = 0; i < tmpE.length; i++) {
			if (hasOpenWndEvent(tmpE[i], HtmlEvalUtil.EVENT_WINDOW)
					&& tmpSet.add(tmpE[i])) {
				result.add(new ProblemItemImpl("C_43.0", tmpE[i]));
			}
		}
	}

	private void item_45() {
		// input: text, text area, radio
		NodeList nl = target.getElementsByTagName("input"); //$NON-NLS-1$
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			boolean bHasDefault = false;
			Element el = (Element) nl.item(i);
			if (!el.hasAttribute("type")) //$NON-NLS-1$
				continue;
			String strType = el.getAttribute("type"); //$NON-NLS-1$
			if (strType.equalsIgnoreCase("text") //$NON-NLS-1$
					|| strType.equalsIgnoreCase("textbox") //$NON-NLS-1$
					|| strType.equalsIgnoreCase("textarea") //$NON-NLS-1$
					|| strType.equals("")) { //$NON-NLS-1$
				String strValue = el.getAttribute("value"); //$NON-NLS-1$
				if (strValue != null && strValue.length() > 0)
					bHasDefault = true;
				else {
					bHasDefault = hasTextDescendant(el);
				}

				if (!bHasDefault) { // default value check
					addCheckerProblem("C_45.0", el); //$NON-NLS-1$
				}
			} else if (strType.equalsIgnoreCase("radio")) { //$NON-NLS-1$
				String strChecked = el.getAttribute("checked"); //$NON-NLS-1$
				if (strChecked != null && !strChecked.equals("")) //$NON-NLS-1$
					bHasDefault = true;
				else {
					if (!el.hasAttribute("name")) //$NON-NLS-1$
						continue;
					String strName1 = el.getAttribute("name"); //$NON-NLS-1$

					for (int j = 0; j < length; j++) {
						if (i == j)
							continue;
						Element el2 = (Element) nl.item(j);
						if (!el2.hasAttribute("type")) //$NON-NLS-1$
							continue;
						String strType2 = el2.getAttribute("type"); //$NON-NLS-1$

						if (strType2.equalsIgnoreCase("radio")) { //$NON-NLS-1$
							String strName2 = el2.getAttribute("name"); //$NON-NLS-1$
							if (strName2 != null
									&& strName1.equalsIgnoreCase(strName2)) { // this
								// radio
								// group
								// has
								// been
								// checked
								if (j < i) {
									bHasDefault = true;
									break;
								}
								String strChecked2 = el2
										.getAttribute("checked"); //$NON-NLS-1$
								if (strChecked2 != null
										&& !strChecked2.equals("")) { //$NON-NLS-1$
									bHasDefault = true;
									break;
								}
							}
						}
					}
				}

				if (!bHasDefault) { // default value check
					addCheckerProblem("C_45.1", el); //$NON-NLS-1$
				}
			} else {
				continue;
			}

		}

		// select
		nl = target.getElementsByTagName("select"); //$NON-NLS-1$
		length = nl.getLength();
		for (int i = 0; i < length; i++) {
			boolean bHasDefault = false;
			Element el = (Element) nl.item(i);
			NodeList opNl = el.getElementsByTagName("option"); //$NON-NLS-1$
			int opLength = opNl.getLength();
			for (int j = 0; j < opLength; j++) {
				String strSelected = ((Element) opNl.item(j))
						.getAttribute("selected"); //$NON-NLS-1$
				if (strSelected != null) {
					bHasDefault = true;
					break;
				}
			}
			if (!bHasDefault) { // default value check
				addCheckerProblem("C_45.1", el); //$NON-NLS-1$
			}
		} // html:text
		nl = target.getElementsByTagName("html:text"); //$NON-NLS-1$
		length = nl.getLength();
		for (int i = 0; i < length; i++) {
			boolean bHasDefault = false;
			Element el = (Element) nl.item(i);
			String strValue = el.getAttribute("value"); //$NON-NLS-1$
			if (strValue != null && strValue.length() > 0) {
				bHasDefault = true;
			} else {
				bHasDefault = hasTextDescendant(el);
			}
			if (!bHasDefault) { // default value check
				addCheckerProblem("C_45.0", el); //$NON-NLS-1$
			}
		} // textarea
		nl = target.getElementsByTagName("textarea"); //$NON-NLS-1$
		length = nl.getLength();
		for (int i = 0; i < length; i++) {
			boolean bHasDefault = false;
			Element el = (Element) nl.item(i);
			String strValue = el.getAttribute("value"); //$NON-NLS-1$
			if (strValue != null && strValue.length() > 0) {
				bHasDefault = true;
			} else {
				bHasDefault = hasTextDescendant(el);
			}
			if (!bHasDefault) { // default value check
				addCheckerProblem("C_45.0", el); //$NON-NLS-1$
			}
		} // html:radio
		nl = target.getElementsByTagName("html:radio"); //$NON-NLS-1$
		length = nl.getLength();
		for (int i = 0; i < length; i++) {
			boolean bHasDefault = false;
			Element el = (Element) nl.item(i);
			String strName1 = el.getAttribute("name"); //$NON-NLS-1$
			if (strName1 == null)
				continue;
			for (int j = i + 1; i < length; j++) {
				Element el2 = (Element) nl.item(j);
				String strName2 = el2.getAttribute("name"); //$NON-NLS-1$
				if (strName2 != null && strName1.equalsIgnoreCase(strName2)) {
					String strChecked2 = el2.getAttribute("checked"); //$NON-NLS-1$
					if (strChecked2 != null) {
						bHasDefault = true;
						break;
					}
				}
			}
			if (!bHasDefault) { // default value check
				addCheckerProblem("C_45.1", el); //$NON-NLS-1$
			}
		}
	}

	private void item_46() {
		Vector<Element> eleVec = new Vector<Element>();
		for (int i = 0; i < aWithHref_elements.length; i++) {
			boolean bAdjacentLink = false;
			Element el = aWithHref_elements[i];
			if (eleVec.contains(el))
				continue;
			Element endEl = null;
			Node nextNode = el.getNextSibling();
			String url1 = aWithHref_hrefs[i];
			String url2;
			try {
				url1 = new URL(baseUrl, aWithHref_hrefs[i]).toString();
			} catch (MalformedURLException e) {
			}
			while (nextNode != null) {
				if (nextNode.getNodeType() == Node.ELEMENT_NODE) {
					String strName = nextNode.getNodeName();
					if (strName != null) {
						if (strName.equalsIgnoreCase("br") //$NON-NLS-1$
								|| strName.equalsIgnoreCase("p")) { //$NON-NLS-1$
							nextNode = nextNode.getNextSibling();
							continue;
						} else if (strName.equalsIgnoreCase("a")) { //$NON-NLS-1$
							try {
								url2 = new URL(baseUrl, ((Element) nextNode)
										.getAttribute(HtmlTagUtil.ATTR_HREF)) //$NON-NLS-1$
										.toString();
							} catch (MalformedURLException e) {
								url2 = ((Element) nextNode)
										.getAttribute(HtmlTagUtil.ATTR_HREF); //$NON-NLS-1$
							}
							if (!url1.equals(url2)) {
								endEl = (Element) nextNode;
								if (!eleVec.contains(el))
									eleVec.add(el);
								if (!eleVec.contains(endEl))
									eleVec.add(endEl);
								bAdjacentLink = true;
							} else
								break;
						} else {
							break;
						}
					}
					// break;
				} else if (nextNode.getNodeType() == Node.TEXT_NODE) {
					String strText = nextNode.getNodeValue();
					if (strText != null && !strText.trim().equals("")) //$NON-NLS-1$
						break;
				}
				nextNode = nextNode.getNextSibling();
			}
			if (bAdjacentLink) { // adjacent link check
				addCheckerProblem("C_46.0", el, endEl); //$NON-NLS-1$
			}
		}
	}

	private void item_47() {
		// alert to use latest technology
		addCheckerProblem("C_47.0"); //$NON-NLS-1$
	}

	private void item_48() {
		// Applet
		checkObsoluteEle("C_48.1", "applet"); //$NON-NLS-1$
		// BASEFONT, CENTER, FONT, STRIKE, U
		checkObsoluteEle("C_48.2", "basefont"); //$NON-NLS-1$
		checkObsoluteEle("C_48.2", "center"); //$NON-NLS-1$
		checkObsoluteEle("C_48.2", "font"); //$NON-NLS-1$
		// ?
		checkObsoluteEle("C_48.2", "strike"); //$NON-NLS-1$
		checkObsoluteEle("C_48.2", "u"); //$NON-NLS-1$
		checkObsoluteEle("C_48.2", "s"); //$NON-NLS-1$
		checkObsoluteEle("C_48.2", "i"); //$NON-NLS-1$
		// DIR, MENU
		checkObsoluteEle("C_48.3", "dir"); //$NON-NLS-1$
		checkObsoluteEle("C_48.3", "menu"); //$NON-NLS-1$
		// ISINDEX
		checkObsoluteEle("C_48.4", "isindex"); //$NON-NLS-1$
		// LISTING, PLAINTEXT, XMP
		checkObsoluteEle("C_48.5", "listing"); //$NON-NLS-1$
		checkObsoluteEle("C_48.5", "plaintext"); //$NON-NLS-1$
		checkObsoluteEle("C_48.5", "xmp"); //$NON-NLS-1$
	}

	private void item_49() {
		// alert about customize experience
		addCheckerProblem("C_49.0"); //$NON-NLS-1$
	}

	private void item_50() {
		// alert about accessible version
		addCheckerProblem("C_50.0"); //$NON-NLS-1$
	}

	private void item_51() {
		for (int i = 0; i < frame_elements.length; i++) {
			Element el = frame_elements[i];
			String strTitle = el.getAttribute("title"); //$NON-NLS-1$
			if (strTitle == null || strTitle.length() == 0) { // frame title
				// check
				addCheckerProblem("C_51.0", el); //$NON-NLS-1$
			}
		}

		for (int i = 0; i < iframe_elements.length; i++) {
			Element el = iframe_elements[i];
			if (!el.hasAttribute("title")) { //$NON-NLS-1$
				addCheckerProblem("C_51.1", //$NON-NLS-1$
						": src=" + el.getAttribute(ATTR_SRC), //$NON-NLS-1$ //$NON-NLS-2$
						el);
			}
		}
	}

	private void item_52() {
		// add longdesc/title into description
		for (int i = 0; i < frame_elements.length; i++) {
			Element el = frame_elements[i];
			String strTitle = el.getAttribute("title"); //$NON-NLS-1$
			if (!strTitle.equals("")) { //$NON-NLS-1$
				String strLongdesc = el.getAttribute("longdesc"); //$NON-NLS-1$
				if (strLongdesc == null || strLongdesc.equals("")) { // alert
					// about
					// frame
					// description
					// //$NON-NLS-1$
					addCheckerProblem("C_52.0", el); //$NON-NLS-1$
				}
			}
		}
		// www.cnn.com --target has <iframe but source does not
		for (int i = 0; i < iframe_elements.length; i++) {
			Element el = iframe_elements[i];
			String strTitle = el.getAttribute("title"); //$NON-NLS-1$
			if (!strTitle.equals("")) { //$NON-NLS-1$
				String strLongdesc = el.getAttribute("longdesc"); //$NON-NLS-1$
				if (strLongdesc == null || strLongdesc.equals("")) { // alert
					// about
					// frame
					// description
					// //$NON-NLS-1$
					addCheckerProblem("C_52.1", el); //$NON-NLS-1$
				}
			}
		}
	}

	private void item_53() {
		NodeList nl = target.getElementsByTagName("select"); //$NON-NLS-1$
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			// alert to group long lists of selections
			Element el = (Element) nl.item(i);
			NodeList optGpNl = el.getElementsByTagName("optgroup"); //$NON-NLS-1$
			if (optGpNl.getLength() == 0) {
				// alert to use optgroup
				NodeList optList = el.getElementsByTagName("option"); //$NON-NLS-1$
				if (optList.getLength() > 10)
					addCheckerProblem("C_53.1", el); //$NON-NLS-1$
			}
		}
	}

	private void item_54() {
		NodeList nl = target.getElementsByTagName("form"); //$NON-NLS-1$
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Element el = (Element) nl.item(i);
			if (getFormControlNum(el) <= 1)
				continue;
			NodeList fieldSetNl = el.getElementsByTagName("fieldset"); //$NON-NLS-1$
			int fsLength = fieldSetNl.getLength();
			if (fsLength == 0) {
				// check to use fieldset
				addCheckerProblem("C_54.0", el); //$NON-NLS-1$
			} else {
				for (int j = 0; j < fsLength; j++) {
					Element fieldSetEl = (Element) fieldSetNl.item(j);
					NodeList legendNl = fieldSetEl
							.getElementsByTagName("legend"); //$NON-NLS-1$
					if (legendNl.getLength() == 0) {
						// check to use fieldset with legend for form
						// not always
						addCheckerProblem("C_54.1", el); //$NON-NLS-1$
						break;
					}
				}
			}
		}
	}

	private void item_55() {
		// alert to group related elements
		addCheckerProblem("C_55.0"); //$NON-NLS-1$
	}

	private void item_56() {
		// link phrase alert
		if (hasAwithHref) {
			addCheckerProblem("C_56.1"); //$NON-NLS-1$
		}
	}

	private void item_57() {
		// need more detailed check
		Element el = null;
		int errorCount = 0;
		int exceptCount = 0;

		int length = aWithHref_elements.length;
		for (int i = 0; i < length; i++) {
			el = aWithHref_elements[i];

			String strTxt = aWithHref_strings[i];
			if (getWordCount(strTxt) < 3 && strTxt.length() < validate_str_len) {
				String strTitle = el.getAttribute("title"); //$NON-NLS-1$
				if (strTitle.equals("")) { //$NON-NLS-1$
					if (strTxt.trim().length() > 0) {
						addCheckerProblem("C_57.0", //$NON-NLS-1$
								" (linktext=\"" //$NON-NLS-1$
										+ strTxt + "\", href=\"" //$NON-NLS-1$
										+ aWithHref_hrefs[i] + "\")", //$NON-NLS-1$
								el);
					} else {
						// can't use link

						if (!aWithHref_hrefs[i].startsWith("#")) { //$NON-NLS-1$

							String noScriptText = getNoScriptText(el);

							if ((!el.hasChildNodes() || el
									.getElementsByTagName("img").getLength() == 0)) {
								exceptCount++;
								// alert
							} else if (noScriptText.length() > 0) {
								// script + noscript(Text)
								exceptCount++;
								// alert
							} else {
								errorCount++;
								String current = aWithHref_hrefs[i];
								boolean sequenceOk = false;
								if (i - 1 > 0) {
									if (current.equals(aWithHref_hrefs[i - 1])
											&& aWithHref_strings[i - 1]
													.length() > 0) {
										sequenceOk = true;
									}
								}
								if (!sequenceOk
										&& i + 1 < aWithHref_hrefs.length) {
									if (current.equals(aWithHref_hrefs[i + 1])
											&& aWithHref_strings[i + 1]
													.length() > 0) {
										sequenceOk = true;
									}
								}

								if (!sequenceOk) {
									IProblemItem tmpCP = addCheckerProblem(
											"C_57.2", //$NON-NLS-1$
											" (href=\"" + aWithHref_hrefs[i]
													+ "\")", el);

									try {
										String id = document2IdMap.get(el)
												.toString();
										Element tmpE = resultDoc
												.getElementById("id" + id);
										if (tmpE.getElementsByTagName("img")
												.getLength() == 0) {
											Element errorImg = resultDoc
													.createElement("img");
											errorImg.setAttribute(ATTR_ALT,
													"error icon");
											errorImg.setAttribute("title",
													tmpCP.getDescription());
											errorImg.setAttribute(ATTR_SRC,
													"img/exclawhite21.gif");
											tmpE.appendChild(errorImg);
										}
									} catch (Exception e) {

									}
								}
							}
						} else {
							// need to count # (intra page link)
							errorCount++;
						}
					}
				} else if (getWordCount(strTitle) < 3
						&& strTitle.length() < validate_str_len) {
					// link title check
					// show always?
					addCheckerProblem("C_57.1", //$NON-NLS-1$
							" (linktext=\"" //$NON-NLS-1$
									+ strTxt + "\", title=\"" //$NON-NLS-1$
									+ strTitle + "\", href=\"" //$NON-NLS-1$
									+ aWithHref_hrefs[i] + "\")", //$NON-NLS-1$
							el);
				}
			}
		}

		// need URL check

		length = length - exceptCount;

		if (length > 0) {
			invalidLinkRatio = (double) errorCount / (double) length;
			// System.out.println(invalidLinkRatio);
		}

	}

	private void item_58() {

		int[] countStrLen = { 0, 0, 0, 0, 0 };
		for (int i = 0; i < aWithHref_elements.length; i++) {
			// strArray[i] = getTextAltDescendant(nl.item(i));
			int length = aWithHref_strings[i].length();

			if (length == 0) {
				continue;
			}

			if (length > 9) {
				if (length < 20) {
					countStrLen[3]++;
				} else {
					countStrLen[4]++;
				}
			} else if (length < 7) {
				if (length < 4)
					countStrLen[0]++;
				else
					countStrLen[1]++;
			} else {
				countStrLen[2]++;
			}
		}

		// 0 (exclusion)
		// length 1-3
		// length 4-6
		// length 7-9
		// length 10-20
		// length 20-

		int[][] strHash = new int[5][];
		Node[][] nodeArray = new Node[5][];
		String[][] strArray = new String[5][];
		String[][] urlArray = new String[5][];
		boolean[][] bDuplicate = new boolean[5][];
		for (int i = 0; i < 5; i++) {
			strHash[i] = new int[countStrLen[i]];
			bDuplicate[i] = new boolean[countStrLen[i]];
			nodeArray[i] = new Node[countStrLen[i]];
			strArray[i] = new String[countStrLen[i]];
			urlArray[i] = new String[countStrLen[i]];
			// System.out.print(countStrLen[i] + " ");
			countStrLen[i] = 0;
		}
		// System.out.println();

		int nlLength = aWithHref_elements.length;
		for (int i = 0; i < nlLength; i++) {
			int length = aWithHref_strings[i].length();
			if (length == 0)
				continue;
			if (length > 9) {
				if (length < 20) {
					strHash[3][countStrLen[3]] = aWithHref_strings[i]
							.hashCode();
					nodeArray[3][countStrLen[3]] = aWithHref_elements[i];
					strArray[3][countStrLen[3]] = aWithHref_strings[i];
					urlArray[3][countStrLen[3]] = aWithHref_hrefs[i];
					countStrLen[3]++;
				} else {
					strHash[4][countStrLen[4]] = aWithHref_strings[i]
							.hashCode();
					nodeArray[4][countStrLen[4]] = aWithHref_elements[i];
					strArray[4][countStrLen[4]] = aWithHref_strings[i];
					urlArray[4][countStrLen[4]] = aWithHref_hrefs[i];
					countStrLen[4]++;
				}
			} else if (length < 7) {
				if (length < 4) {
					strHash[0][countStrLen[0]] = aWithHref_strings[i]
							.hashCode();
					nodeArray[0][countStrLen[0]] = aWithHref_elements[i];
					strArray[0][countStrLen[0]] = aWithHref_strings[i];
					urlArray[0][countStrLen[0]] = aWithHref_hrefs[i];
					countStrLen[0]++;
				} else {
					strHash[1][countStrLen[1]] = aWithHref_strings[i]
							.hashCode();
					nodeArray[1][countStrLen[1]] = aWithHref_elements[i];
					strArray[1][countStrLen[1]] = aWithHref_strings[i];
					urlArray[1][countStrLen[1]] = aWithHref_hrefs[i];
					countStrLen[1]++;
				}
			} else {
				strHash[2][countStrLen[2]] = aWithHref_strings[i].hashCode();
				nodeArray[2][countStrLen[2]] = aWithHref_elements[i];
				strArray[2][countStrLen[2]] = aWithHref_strings[i];
				urlArray[2][countStrLen[2]] = aWithHref_hrefs[i];
				countStrLen[2]++;
			}
		}

		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < strHash[i].length - 1; j++) {
				if (bDuplicate[i][j])
					continue;
				Vector<Node> idVec = new Vector<Node>();

				for (int k = j + 1; k < strHash[i].length; k++) {
					if (bDuplicate[i][k])
						continue;
					if (strHash[i][j] == strHash[i][k]) {

						String url1 = urlArray[i][j];
						try {
							url1 = new URL(baseUrl, url1).toString();
						} catch (MalformedURLException e) {
							// e.printStackTrace();
						}

						String url2 = urlArray[i][k];
						try {
							url2 = new URL(baseUrl, url2).toString();
						} catch (MalformedURLException e1) {
							// e1.printStackTrace();
						}

						// System.out.println(url1 + " " + url2);
						if (!url1.equalsIgnoreCase(url2)) {
							if (!bDuplicate[i][j]) {
								idVec.add(nodeArray[i][j]);
								bDuplicate[i][j] = true;
							}
							idVec.add(nodeArray[i][k]);
							bDuplicate[i][k] = true;
						}
					}

				}

				if (idVec.size() > 0) {
					addCheckerProblem("C_58.0", " (linktext=\""
							+ strArray[i][j] + "\")", idVec);
				}
			}
		}

	}

	private void item_59() {

		// 59.0 was identical with 68

		boolean bHasKeyWdDesc = false;
		NodeList nl = target.getElementsByTagName("meta"); //$NON-NLS-1$
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Element el = (Element) nl.item(i);
			String strName = el.getAttribute("name"); //$NON-NLS-1$
			if (strName != null && strName.equalsIgnoreCase("keywords")) //$NON-NLS-1$
				bHasKeyWdDesc = true;
			else if (strName != null && strName.equalsIgnoreCase("description")) //$NON-NLS-1$
				bHasKeyWdDesc = true;
		}
		if (!bHasKeyWdDesc) {
			// Use the keywords
			addCheckerProblem("C_59.1"); //$NON-NLS-1$
		}

		nl = target.getElementsByTagName("address"); //$NON-NLS-1$
		if (nl.getLength() == 0) {
			// Use the ADDRESS element
			addCheckerProblem("C_59.2"); //$NON-NLS-1$
		}

		// Use the Resource Description Framework (RDF) in the header of your
		// document
		addCheckerProblem("C_59.3"); //$NON-NLS-1$
	}

	private void item_60() {
		boolean bHasTitle = false;
		NodeList nl = target.getElementsByTagName("head"); //$NON-NLS-1$
		if (nl.getLength() > 0) {
			NodeList hdNl = ((Element) nl.item(0))
					.getElementsByTagName("title"); //$NON-NLS-1$
			int length = hdNl.getLength();
			for (int i = 0; i < length; i++) {
				Element titleEl = (Element) hdNl.item(i);
				bHasTitle = hasTextDescendant(titleEl);
			}
		}
		if (!bHasTitle) { // document title check
			result.add(new ProblemItemImpl("C_60.0")); //$NON-NLS-1$
		}
	}

	private void item_61() {
		// alert if there is a site map or...
		addCheckerProblem("C_61.0"); //$NON-NLS-1$
	}

	private void item_62() {
		// alert if there is a clear navigation structure
		addCheckerProblem("C_62.0"); //$NON-NLS-1$
	}

	private void item_63() {
		// TBD estimation
		// alert if there are navigation bars for navigation structure
		addCheckerProblem("C_63.0"); //$NON-NLS-1$
	}

	private void item_64() {
		if (hasAwithHref) {
			// TBD estimation
			// alert to identify logical groups of links
			addCheckerProblem("C_64.0"); //$NON-NLS-1$
		}
	}

	private void item_65() {
		if (hasAwithHref) {
			addCheckerProblem("C_65.0"); //$NON-NLS-1$
		}
	}

	private void item_66() {
		// need to check descendant text (or href txt) contains "search","go",
		// etc.
		// exist text box / text area

		NodeList nl = target.getElementsByTagName("form"); //$NON-NLS-1$
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			// alert if there are different types of searches
			addCheckerProblem("C_66.0", (Element) nl.item(i)); //$NON-NLS-1$
		}
	}

	private void item_67() {
		// alert if there is distinguishing information
		addCheckerProblem("C_67.0"); //$NON-NLS-1$
	}

	private void item_68() {
		boolean bHasRel = false;
		NodeList nl = target.getElementsByTagName("link"); //$NON-NLS-1$
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Element el = (Element) nl.item(i);
			String strRel = el.getAttribute("rel"); //$NON-NLS-1$
			if (strRel != null && !strRel.equals("") //$NON-NLS-1$
					&& !strRel.equalsIgnoreCase("stylesheet")) { //$NON-NLS-1$
				bHasRel = true;
				break;
			} else {
				strRel = el.getAttribute("rev"); //$NON-NLS-1$
				if (strRel != null && !strRel.equals("")) { //$NON-NLS-1$
					bHasRel = true;
					break;
				}
			}
		}
		if (!bHasRel) { // alert about identifying document location
			addCheckerProblem("C_68.0"); //$NON-NLS-1$
		}

		// //alert to use the Resource Description Framework (RDF) in the header
		// result.add(new CheckerProblem("68.1"));
	}

	private void item_69() {
		if (body_elements.length > 0) {
			Element bodyEl = body_elements[0];
			Stack<Node> stack = new Stack<Node>();
			Node curNode = bodyEl;

			while (curNode != null) {
				// TODO have means to skip over?
				boolean isArtStr = false;
				if (isLeafBlockEle(curNode)
						&& isAsciiArtString(getTextAltDescendant(curNode))) {
					addCheckerProblem("C_69.0", (Element) curNode); //$NON-NLS-1$
					isArtStr = true;
				}

				if (!isArtStr && curNode.hasChildNodes()) {
					stack.push(curNode);
					curNode = curNode.getFirstChild();
				} else if (curNode.getNextSibling() != null) {
					curNode = curNode.getNextSibling();
				} else {
					curNode = null;
					while ((curNode == null) && (stack.size() > 0)) {
						curNode = (Node) stack.pop();
						curNode = curNode.getNextSibling();
					}
				}

			}
		}
	}

	private void item_70() {
		// alert about language
		addCheckerProblem("C_70.0"); //$NON-NLS-1$
	}

	private void item_71() {
		// alert
		// bobby does not report
		addCheckerProblem("C_71.0"); //$NON-NLS-1$
	}

	private void item_72() {
		// alert about consistant style
		addCheckerProblem("C_72.0"); //$NON-NLS-1$
	}

	private void item_73() {
		for (int i = 0; i < object_elements.length; i++) {
			Element el = object_elements[i];
			String str = el.getAttribute("data"); //$NON-NLS-1$
			str = getFileExtension(str);
			if (str.equalsIgnoreCase("pdf") //$NON-NLS-1$
					|| str.equalsIgnoreCase("ppt") //$NON-NLS-1$
					|| isAudioFileExt(str) || isMultimediaFileExt(str)) {
				addCheckerProblem("C_73.0", el); //$NON-NLS-1$
			}
		}
		for (int i = 0; i < aWithHref_hrefs.length; i++) {
			Element el = aWithHref_elements[i];
			String str = aWithHref_hrefs[i];
			str = getFileExtension(str);
			if (str.equalsIgnoreCase("pdf") //$NON-NLS-1$
					|| str.equalsIgnoreCase("ppt") //$NON-NLS-1$
					|| isAudioFileExt(str) || isMultimediaFileExt(str)) {
				addCheckerProblem("C_73.0", el); //$NON-NLS-1$
			}
		}

		NodeList nl = target.getElementsByTagName("applet"); //$NON-NLS-1$
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Element el = (Element) nl.item(i);
			NodeList parNl = el.getElementsByTagName("param"); //$NON-NLS-1$
			int parLength = parNl.getLength();
			for (int j = 0; j < parLength; j++) {
				String str = ((Element) parNl.item(j)).getAttribute("value"); //$NON-NLS-1$
				str = getFileExtension(str);
				if (str.equalsIgnoreCase("pdf") //$NON-NLS-1$
						|| str.equalsIgnoreCase("ppt") //$NON-NLS-1$
						|| isAudioFileExt(str) || isMultimediaFileExt(str)) {
					addCheckerProblem("C_73.0", el); //$NON-NLS-1$
					break;
				}
			}
		}
	}

	private void item_74() {
		boolean hasHeader = false;
		NodeList nl = target.getElementsByTagName("meta"); //$NON-NLS-1$
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Element el = (Element) nl.item(i);
			String strMeta = el.getAttribute("http-equiv"); //$NON-NLS-1$
			if (strMeta.equalsIgnoreCase("refresh") //$NON-NLS-1$
					|| strMeta.equalsIgnoreCase("location")) { //$NON-NLS-1$
				addCheckerProblem("C_74.1", el); //$NON-NLS-1$
				hasHeader = true;
			}
		}
		// timeout process alert
		if (!hasHeader) {
			addCheckerProblem("C_74.0"); //$NON-NLS-1$
		}

		nl = target.getElementsByTagName("form");
		if (nl.getLength() > 0) {
			addCheckerProblem("C_74.2");
		}
	}

	private void item_75() {
		for (int i = 0; i < bottom_data_tables.length; i++) {
			Element tNode = bottom_data_tables[i];
			NodeList nl = tNode.getElementsByTagName("th"); //$NON-NLS-1$
			if (nl.getLength() == 0) {
				// table header check
				addCheckerProblem("C_75.0", tNode); //$NON-NLS-1$
			}
		}
	}

	private void item_76() {
		for (int i = 0; i < bottom_data_tables.length; i++) {
			Element el = bottom_data_tables[i];
			// if (!isDataTable(el))
			// continue;
			NodeList trNl = el.getElementsByTagName("tr"); //$NON-NLS-1$
			int trLength = trNl.getLength();
			int thNum = 0;
			boolean hasScopeAxis = false;
			for (int j = 0; j < trLength; j++) {
				NodeList thNl = ((Element) trNl.item(j))
						.getElementsByTagName("th"); //$NON-NLS-1$
				int thLength = thNl.getLength();
				NodeList tdNl = ((Element) trNl.item(j))
						.getElementsByTagName("td"); //$NON-NLS-1$
				int tdLength = tdNl.getLength();
				if (thLength > 0)
					thNum++;
				for (int k = 0; k < thLength; k++) {
					if (!((Element) thNl.item(k)).getAttribute("scope") //$NON-NLS-1$
							.equals("")) { //$NON-NLS-1$
						hasScopeAxis = true;
						break;
					} else if (!((Element) thNl.item(k))
							.getAttribute("axis").equals(//$NON-NLS-1$
									"")) { //$NON-NLS-1$
						hasScopeAxis = true;
						break;
					}
				}
				if (hasScopeAxis)
					break;
				for (int k = 0; k < tdLength; k++) {
					if (!((Element) tdNl.item(k)).getAttribute("scope") //$NON-NLS-1$
							.equals("")) { //$NON-NLS-1$
						hasScopeAxis = true;
						break;
					} else if (!((Element) tdNl.item(k))
							.getAttribute("axis").equals(//$NON-NLS-1$
									"")) { //$NON-NLS-1$
						hasScopeAxis = true;
						break;
					}
				}
				if (hasScopeAxis)
					break;
			}
			if (!hasScopeAxis && thNum > 1) {
				// alert: two or more rows or columns as table header
				addCheckerProblem("C_76.0", el); //$NON-NLS-1$
			}

			// need to check structure (axis, scope)

			boolean bHasRowColSpan = false;
			NodeList thNl = el.getElementsByTagName("th");
			int thLen = thNl.getLength();
			for (int j = 0; j < thLen; j++) {
				Element thEl = (Element) thNl.item(j);
				if (thEl.hasAttribute("rowspan")
						|| thEl.hasAttribute("colspan")) {
					bHasRowColSpan = true;
					break;
				}
			}
			if (!bHasRowColSpan) {
				NodeList tdNl = el.getElementsByTagName("td");
				int tdLen = tdNl.getLength();
				for (int j = 0; j < tdLen; j++) {
					Element tdEl = (Element) tdNl.item(j);
					if (tdEl.hasAttribute("rowspan")
							|| tdEl.hasAttribute("colspan")) {
						bHasRowColSpan = true;
						break;
					}
				}
			}

			if (bHasRowColSpan) {
				// has rowspan or colspan
				addCheckerProblem("C_76.1");
			}
		}
	}

	private void item_77() {
		Element el = null;
		boolean bHasCSS = false;
		NodeList nl = target.getElementsByTagName("link"); //$NON-NLS-1$
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			el = (Element) nl.item(i);
			String strRel = el.getAttribute("rel"); //$NON-NLS-1$
			if (strRel != null && strRel.equalsIgnoreCase("stylesheet")) { //$NON-NLS-1$
				bHasCSS = true;
				break;
			}
		}
		if (bHasCSS) { // alert about style sheet
			addCheckerProblem("C_77.0", //$NON-NLS-1$
					// AdditionalDescription.getString("CheckEngine._(link_tag_for_include_stylesheet)_492"),
					// //$NON-NLS-1$
					el);
			// multi?
		}

		nl = target.getElementsByTagName("style"); //$NON-NLS-1$
		length = nl.getLength();
		for (int i = 0; i < length; i++) {
			addCheckerProblem("C_77.1", //$NON-NLS-1$
					// AdditionalDescription.getString("CheckEngine._(style_tag)_495"),
					// //$NON-NLS-1$
					(Element) nl.item(i));
		}

		if (body_elements.length > 0) {
			Element bodyEl = body_elements[0];
			Stack<Node> stack = new Stack<Node>();
			Node curNode = bodyEl;
			while (curNode != null) {
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					el = (Element) curNode;
					String strAlt = el.getAttribute("style"); //$NON-NLS-1$
					if (!strAlt.equals("")) { //$NON-NLS-1$
						// alert about style
						addCheckerProblem("C_77.2", //$NON-NLS-1$
								// AdditionalDescription.getString("CheckEngine._(style_attribute_for_tag___499")
								// + //$NON-NLS-1$
								SPACE_STR + curNode.getNodeName() + SPACE_STR,
								// +
								// AdditionalDescription.getString("CheckEngine.)_488"),
								// //$NON-NLS-1$
								el);
					}
				}

				if (curNode.hasChildNodes()) {
					stack.push(curNode);
					curNode = curNode.getFirstChild();
				} else if (curNode.getNextSibling() != null) {
					curNode = curNode.getNextSibling();
				} else {
					curNode = null;
					while ((curNode == null) && (stack.size() > 0)) {
						curNode = (Node) stack.pop();
						curNode = curNode.getNextSibling();
					}
				}
			}
		}
	}

	private void item_78() {
		NodeList nl = target.getElementsByTagName("input");
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Element el = (Element) nl.item(i);
			if (!el.hasAttribute("type"))
				continue;
			String strType = el.getAttribute("type");
			if (strType.equalsIgnoreCase("text")
					|| strType.equalsIgnoreCase("textbox")
					|| strType.equals("")) {
				addCheckerProblem("C_78.2", el);
			}
		}

		nl = target.getElementsByTagName("html:text");
		length = nl.getLength();
		for (int i = 0; i < length; i++) {
			addCheckerProblem("C_78.2", (Element) nl.item(i));
		}
	}

	private void item_79() {
		NodeList nl = target.getElementsByTagName("form"); //$NON-NLS-1$
		int length = nl.getLength();
		NodeList labelNl = target.getElementsByTagName("label");
		for (int i = 0; i < length; i++) {
			Element fEl = (Element) nl.item(i);
			Vector<Element> fcVector = getFormControl(fEl);
			int labelLen = labelNl.getLength();
			for (int j = 0; j < fcVector.size(); j++) {
				boolean bHasLabel = false;
				Element el = (Element) fcVector.get(j);
				String strid = el.getAttribute("id"); //$NON-NLS-1$
				if (!strid.equals("")) { //$NON-NLS-1$
					for (int k = 0; k < labelLen; k++) {
						String strFor = ((Element) labelNl.item(k))
								.getAttribute("for"); //$NON-NLS-1$
						if (strFor != null && strFor.equalsIgnoreCase(strid)) {
							bHasLabel = true;
							break;
						}
					}

				}
				if (!bHasLabel) {
					// form control label check
					addCheckerProblem("C_79.0", el); //$NON-NLS-1$
				} else {
					String strType = getFormControlType(el);
					boolean bRadioCheckbox = strType.indexOf("radio") >= 0 //$NON-NLS-1$
							|| strType.indexOf("checkbox") >= 0; //$NON-NLS-1$
					if (!hasProperLabel(el, bRadioCheckbox)) {
						// proper label check
						if (strType.equals("")) { //$NON-NLS-1$
							// TYPE removed by IE
							addCheckerProblem(
									"C_79.1", " (input type: text)", el); //$NON-NLS-1$ //$NON-NLS-2$
						} else {
							addCheckerProblem("C_79.1", //$NON-NLS-1$
									" (input type: " + strType + ")", //$NON-NLS-1$ //$NON-NLS-2$
									el);
						}
					}

				}
			}
		}
	}

	private void item_80() {
		// NodeList bodyNl = target.getElementsByTagName("body");
		if (body_elements.length > 0) {
			Element bodyEl = body_elements[0];
			Stack<Node> stack = new Stack<Node>();
			Node curNode = bodyEl;
			while (curNode != null) {
				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					Element el = (Element) curNode;
					String strAlt = el.getAttribute(ATTR_ALT); //$NON-NLS-1$
					if (strAlt != null && strAlt.length() > 150) {
						// alt text length check
						addCheckerProblem("C_80.0", el); //$NON-NLS-1$
					}
				}

				if (curNode.hasChildNodes()) {
					stack.push(curNode);
					curNode = curNode.getFirstChild();
				} else if (curNode.getNextSibling() != null) {
					curNode = curNode.getNextSibling();
				} else {
					curNode = null;
					while ((curNode == null) && (stack.size() > 0)) {
						curNode = (Node) stack.pop();
						curNode = curNode.getNextSibling();
					}
				}
			}
		}
	}

	private void item_81() {
		if (hasAwithHref) {
			addCheckerProblem("C_81.0");
		}
	}

	private void item_82() {
		addCheckerProblem("C_82.0");
	}

	private void item_83() {
		addCheckerProblem("C_83.0");
	}

	private void item_84() {
		addCheckerProblem("C_84.0");
	}

	private void item_85() {
		boolean bHasBgsound = false;
		NodeList nl = target.getElementsByTagName("head");
		Element targetE = null;
		for (int i = 0; i < nl.getLength(); i++) {
			NodeList bgNl = ((Element) nl.item(i))
					.getElementsByTagName("bgsound");
			if (bgNl.getLength() > 0) {
				bHasBgsound = true;
				targetE = (Element) bgNl.item(0);
				break;
			}
		}
		if (bHasBgsound) {
			result.add(addCheckerProblem("C_85.0", targetE)); //$NON-NLS-1$
		}
	}

	private void item_86() {
		boolean bHasMulti = false;
		for (int i = 0; i < object_elements.length; i++) {
			Element el = object_elements[i];
			String str = el.getAttribute("data");
			str = getFileExtension(str);
			if (isAudioFileExt(str) || isMultimediaFileExt(str)) {
				bHasMulti = true;
				break;
			}
		}

		if (!bHasMulti) {
			NodeList nl = target.getElementsByTagName("applet");
			int length = nl.getLength();
			for (int i = 0; i < length; i++) {
				Element el = (Element) nl.item(i);
				NodeList parNl = el.getElementsByTagName("param");
				int parLength = parNl.getLength();
				for (int j = 0; j < parLength; j++) {
					String str = ((Element) parNl.item(j))
							.getAttribute("value");
					str = getFileExtension(str);
					if (isAudioFileExt(str) || isMultimediaFileExt(str)) {
						bHasMulti = true;
						break;
					}
				}
				if (bHasMulti) {
					break;
				}
			}
		}

		if (!bHasMulti) {
			for (int i = 0; i < aWithHref_hrefs.length; i++) {
				// Element el = aWithHref_elements[i];
				String str = aWithHref_hrefs[i];
				str = getFileExtension(str);
				if (isAudioFileExt(str) || isMultimediaFileExt(str)) {
					bHasMulti = true;
					break;
				}
			}
		}

		if (bHasMulti) {
			addCheckerProblem("C_86.0");
		}

	}

	private void item_87() {
		addCheckerProblem("C_87.0");
	}

	private void item_88() {
		String charset = "";
		NodeList nl = target.getElementsByTagName("meta");
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Element el = (Element) nl.item(i);
			if (el.hasAttribute("http-equiv") && el.hasAttribute("content")) { //$NON-NLS-1$
				String strMeta = el.getAttribute("http-equiv"); //$NON-NLS-1$
				String strCon = el.getAttribute("content");
				if (strMeta != null && strMeta.equalsIgnoreCase("Content-Type")
						&& strCon != null) {
					int index = strCon.toLowerCase().indexOf("text/html");
					if (index >= 0) {
						strCon = strCon.substring(index + 9);
						index = strCon.indexOf(";");
						if (index >= 0) {
							strCon = strCon.substring(index + 1);
							index = strCon.toLowerCase().indexOf("charset");
							if (index >= 0) {
								strCon = strCon.substring(index + 7);
								index = strCon.indexOf("=");
								if (index >= 0) {
									charset = strCon.substring(index + 1);
								}
							}
						}
					}
				}
			}
		}

		// System.out.println("charset: " + charset);
		if (charset.length() == 0) {
			result.add(new ProblemItemImpl("C_88.0")); //$NON-NLS-1$
		} else {
			// check coding (EUC-JP, Shift_JIS, UTF-8...)
			// addCheckerProblem("C_88.1", charset); //$NON-NLS-1$
		}
	}

	private void item_89() {

		if (body_elements.length == 1
				&& target.getElementsByTagName("frameset").getLength() == 0) {
			Node curNode = body_elements[0].getFirstChild();
			StringBuffer strBuf = new StringBuffer(512);
			Stack<Node> stack = new Stack<Node>();
			while (curNode != null && strBuf.length() < valid_total_text_len) {
				if (curNode.getNodeType() == Node.TEXT_NODE) {
					// &#nbsp; (160)
					strBuf.append(curNode.getNodeValue().replaceAll(
							String.valueOf((char) 160), "").trim());
				} else if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					Element tmpE = (Element) curNode;

					// need to check element name

					if (tmpE.hasAttribute(ATTR_ALT)) {
						strBuf.append(tmpE.getAttribute(ATTR_ALT).replaceAll(
								String.valueOf((char) 160), "").trim()); //$NON-NLS-1$
					}
					if (tmpE.hasAttribute("title")) {
						strBuf.append(tmpE.getAttribute("title").replaceAll(
								String.valueOf((char) 160), "").trim());
					}
				}

				if (curNode.hasChildNodes()) {
					stack.push(curNode);
					curNode = curNode.getFirstChild();
				} else if (curNode.getNextSibling() != null) {
					curNode = curNode.getNextSibling();
				} else {
					curNode = null;
					while ((curNode == null) && (stack.size() > 0)) {
						curNode = (Node) stack.pop();
						curNode = curNode.getNextSibling();
					}
				}
			}
			if (strBuf.length() == 0) {
				addCheckerProblem("C_89.0");
			} else if (strBuf.length() < valid_total_text_len) {
				if (img_elements.length > 0) {
					addCheckerProblem("C_89.1");
				} else {
					addCheckerProblem("C_89.2");
				}
			}
		}

	}

	// Mobile Web Evaluation (from here)
	private void mobile_1() {
		// TODO implement evaluation
		addCheckerProblem("M_1");
	}

	// Mobile Web Evaluation (end here)

	private void validateHtml() {
		if (body_elements.length > 1) {
			addCheckerProblem("C_1000.0");
		} else if (body_elements.length == 0) {
			addCheckerProblem("C_1000.4");
		}
	}

	private void checkDomDifference() {

		if (edu.getInvisibleElementCount() > 0) {
			addCheckerProblem("C_201.0");
		}
		String[] invisibleLinkStrings = edu.getInvisibleLinkStrings();
		for (int i = 0; i < invisibleLinkStrings.length; i++) {
			if (invisibleLinkStrings[i].trim().length() > 0) {
				addCheckerProblem("C_201.1", " (href="
						+ invisibleLinkStrings[i] + ")");
			}
		}

		Set<String> notExistSet = edu.getNotExistHrefSet();
		for (String href : notExistSet) {
			addCheckerProblem("C_200.0", " (href=" + href + ")");
		}
		if (notExistSet.size() > 10) {
			addCheckerProblem("C_200.1");
		}

	}

	private void checkAbsoluteSize(String strName) {
		NodeList nl = target.getElementsByTagName(strName);
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Element el = (Element) nl.item(i);
			String strWidth = el.getAttribute("width"); //$NON-NLS-1$
			String strHeight = el.getAttribute("height"); //$NON-NLS-1$
			if ((strWidth != null && !strWidth.equals("") //$NON-NLS-1$
			&& strWidth.indexOf("%") == -1) //$NON-NLS-1$
					|| (strHeight != null && !strHeight.equals("") //$NON-NLS-1$
					&& strHeight.indexOf("%") == -1)) { //$NON-NLS-1$
				// absolute width or height
				addCheckerProblem("C_13.0", el); //$NON-NLS-1$
			}

		}
	}

	private boolean hasOpenWndEvent(Element element, String[] targetAttrs) {

		// need to check open(...) (can use without 'window.')

		for (int i = 0; i < targetAttrs.length; i++) {
			String str = element.getAttribute(targetAttrs[i]);
			if (str.toLowerCase().indexOf(WINDOW_OPEN) >= 0) { //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}

	private boolean isValidLang(String strLang) {
		// TODO: language check
		return true;
	}

	private void checkObsoluteEle(String problemId, String strEle) {
		NodeList nl = target.getElementsByTagName(strEle);
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Element el = (Element) nl.item(i);
			// obsolute elemnet check
			addCheckerProblem(problemId, // AdditionalDescription.getString("CheckEngine._(obsolete_tag___578")
					SPACE_STR + strEle + SPACE_STR, el); //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-3$
		}
	}

	private String getFormControlType(Element el) {
		String strName = el.getNodeName().toLowerCase();
		if (strName.equals("input")) { //$NON-NLS-1$
			return el.getAttribute("type").toLowerCase(); //$NON-NLS-1$
		} else {
			return strName;
		}
	}

	private Vector<Element> getFormControl(Element formEl) {
		Vector<Element> fcVector = new Vector<Element>();
		NodeList nl = formEl.getElementsByTagName("input"); //$NON-NLS-1$
		int length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Element el = (Element) nl.item(i);
			String strType = el.getAttribute("type").toLowerCase(); //$NON-NLS-1$
			if (strType.equals("") // default is text? //$NON-NLS-1$
					|| strType.equals("text") //$NON-NLS-1$
					|| strType.equals("textarea") //$NON-NLS-1$
					|| strType.equals("radio") //$NON-NLS-1$
					|| strType.equals("checkbox") //$NON-NLS-1$
					|| strType.equals("password")) { //$NON-NLS-1$
				fcVector.add(el);
			}

		}

		nl = formEl.getElementsByTagName("textarea"); //$NON-NLS-1$
		length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Element el = (Element) nl.item(i);
			fcVector.add(el);
		}

		nl = formEl.getElementsByTagName("select"); //$NON-NLS-1$
		length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Element el = (Element) nl.item(i);
			fcVector.add(el);
		}

		nl = formEl.getElementsByTagName("html:text"); //$NON-NLS-1$
		length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Element el = (Element) nl.item(i);
			fcVector.add(el);
		}

		nl = formEl.getElementsByTagName("html:radio"); //$NON-NLS-1$
		length = nl.getLength();
		for (int i = 0; i < length; i++) {
			Element el = (Element) nl.item(i);
			fcVector.add(el);
		}

		return fcVector;
	}

	private int getFormControlNum(Element formEl) {
		int iNum = 0;
		NodeList nl = formEl.getElementsByTagName("input"); //$NON-NLS-1$
		iNum += nl.getLength();
		nl = formEl.getElementsByTagName("select"); //$NON-NLS-1$
		iNum += nl.getLength();
		nl = formEl.getElementsByTagName("textarea"); //$NON-NLS-1$
		iNum += nl.getLength();
		nl = formEl.getElementsByTagName("html:text"); //$NON-NLS-1$
		iNum += nl.getLength();
		nl = formEl.getElementsByTagName("html:radio"); //$NON-NLS-1$
		iNum += nl.getLength();
		return iNum;
	}

	private boolean hasProperLabel(Element el, boolean bRadioCheckbox) {

		// need to consider position

		Node node = el;
		if (bRadioCheckbox) {
			while (node.getNextSibling() == null) {
				if (node.getNodeName().equalsIgnoreCase("body")) { //$NON-NLS-1$
					return false;
				}
				node = node.getParentNode();
			}
			if (node.getNextSibling().getNodeType() != Node.ELEMENT_NODE)
				return false;
			Element nextEl = (Element) node.getNextSibling();
			Element labelEl;
			if (nextEl.getNodeName().equalsIgnoreCase("label")) { //$NON-NLS-1$
				labelEl = nextEl;
			} else {
				NodeList nl = nextEl.getElementsByTagName("label"); //$NON-NLS-1$
				if (nl.getLength() == 0) {
					return false;
				} else {
					labelEl = (Element) nl.item(0);
				}
			}

			String strId = el.getAttribute("id"); //$NON-NLS-1$
			String strFor = labelEl.getAttribute("for"); //$NON-NLS-1$
			if (!strId.equals(strFor)) {
				return false;
			} else {
				if (getTextAltDescendant(nextEl).trim().indexOf(
						getTextAltDescendant(labelEl).trim()) == 0) {
					return true;
				} else {
					return false;
				}
			}

		} else {
			while (node.getPreviousSibling() == null) {
				if (node.getNodeName().equalsIgnoreCase("body")) { //$NON-NLS-1$
					return false;
				}
				node = node.getParentNode();
			}
			if (node.getPreviousSibling().getNodeType() != Node.ELEMENT_NODE)
				return false;
			Element preEl = (Element) node.getPreviousSibling();
			Element labelEl;
			if (preEl.getNodeName().equalsIgnoreCase("label")) { //$NON-NLS-1$
				labelEl = preEl;
			} else {
				NodeList nl = preEl.getElementsByTagName("label"); //$NON-NLS-1$
				if (nl.getLength() == 0) {
					return false;
				} else {
					labelEl = (Element) nl.item(nl.getLength() - 1);
				}
			}
			String strId = el.getAttribute("id"); //$NON-NLS-1$
			String strFor = labelEl.getAttribute("for"); //$NON-NLS-1$
			if (!strId.equals(strFor)) {
				return false;
			} else {
				String strWhole = getTextAltDescendant(preEl).trim();
				String strLabel = getTextAltDescendant(labelEl).trim();
				if (strWhole.indexOf(strLabel) == strWhole.length()
						- strLabel.length()) {
					return true;
				} else {
					return false;
				}
			}

		}
	}

	private boolean isAsciiArtString(String str) {

		if (str == null) {
			return false;
		}

		// int realTotal = str.length();
		String[] tmpS = str.split("\\p{Space}");
		int num = 0;
		int total = 0;

		for (int j = 0; j < tmpS.length; j++) {
			String target = tmpS[j];
			int strLength = target.length();
			total += strLength;

			target = target.replaceAll("\\p{Punct}", "");
			num += strLength - target.length();

			strLength = target.length();
			for (int i = 0; i < strLength; i++) {
				if (artCharSet.contains(target.substring(i, i + 1))) {
					num++;
				}
			}

		}
		if (total == 0)
			total = 1;
		if (num > 30 && (double) num / total > 0.8) {
			// System.out.println(str);
			return true;
		} else {
			return false;
		}
	}

	private boolean isNormalImage(Element imgEl) {
		String strWidth = imgEl.getAttribute("width"); //$NON-NLS-1$
		String strHeight = imgEl.getAttribute("height"); //$NON-NLS-1$
		int iWidth = 0, iHeight = 0;
		try {
			if (strWidth != null)
				iWidth = Integer.valueOf(strWidth).intValue();
			if (strHeight != null)
				iHeight = Integer.valueOf(strHeight).intValue();

		} catch (NumberFormatException e) {
			iWidth = 100;
		}
		// ignore small image according to bobby
		if (iWidth < 50 || iHeight < 50) {
			int iBig, iSmall;
			if (iWidth > iHeight) {
				iBig = iWidth;
				iSmall = iHeight;
			} else {
				iBig = iHeight;
				iSmall = iWidth;
			}
			if (iBig < 50) {
				return false;
			} else if ((double) iSmall / iBig < 0.2) {
				return false;
			}
		}
		return true;
	}

	// TODO check
	private boolean isHtmlFile(String strFile) {
		int iPos = strFile.lastIndexOf("."); //$NON-NLS-1$
		if (iPos > 0) {
			String strExt = strFile.substring(iPos + 1).toLowerCase();
			if (strExt.equals("html") //$NON-NLS-1$
					|| strExt.equals("htm") //$NON-NLS-1$
					|| strExt.equals("jsp") //$NON-NLS-1$
					|| strExt.equals("php") //$NON-NLS-1$
					|| strExt.equals("shtml")) //$NON-NLS-1$
				return true;
			else if (strFile.toLowerCase().indexOf(".cgi") > 0) //$NON-NLS-1$
				return true;
		}
		return false;
	}

	private boolean isAudioFileExt(String strFileExt) {
		for (int i = 0; i < AUDIO_FILE_EXTENSION.length; i++) {
			if (strFileExt.equalsIgnoreCase(AUDIO_FILE_EXTENSION[i])) {
				return true;
			}
		}
		return false;
	}

	private boolean isMultimediaFileExt(String strFileExt) {
		for (int i = 0; i < MULTIMEDIA_FILE_EXTENSION.length; i++) {
			if (strFileExt.equalsIgnoreCase(MULTIMEDIA_FILE_EXTENSION[i])) {
				return true;
			}
		}
		return false;
	}

	private boolean isLeafBlockEle(Node node) {
		String str = node.getNodeName().toLowerCase();
		if (!blockEleSet.contains(str)) {
			return false;
		}
		if (!node.hasChildNodes()) {
			return true;
		}
		Node curNode = node.getFirstChild();
		Stack<Node> stack = new Stack<Node>();
		while (curNode != null) {
			str = curNode.getNodeName().toLowerCase();
			if (blockEleSet.contains(str)) {
				return false;
			}

			if (curNode.hasChildNodes()) {
				stack.push(curNode);
				curNode = curNode.getFirstChild();
			} else if (curNode.getNextSibling() != null) {
				curNode = curNode.getNextSibling();
			} else {
				curNode = null;
				while ((curNode == null) && (stack.size() > 0)) {
					curNode = (Node) stack.pop();
					curNode = curNode.getNextSibling();
				}
			}

		}
		return true;
	}

	private int getWordCount(String str) {
		StringTokenizer st = new StringTokenizer(str,
				" \t\n\r\f,.[]()<>!?:/\"\u3001\u3002\u300c\u300d\u30fb\u3008\u3009\u3000"); //$NON-NLS-1$
		return st.countTokens();

	}

	private String getFileExtension(String strName) {
		int iPos = strName.lastIndexOf(".");
		if (iPos > 0) {
			return strName.substring(iPos + 1);
		}
		return "";
	}
}
