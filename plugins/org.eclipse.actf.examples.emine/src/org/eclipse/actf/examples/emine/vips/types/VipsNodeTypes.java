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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * 
 */
public class VipsNodeTypes {
	
	/**
	 * Invalid nodes does not appear in the visual page content,
	 * therefore, they are omitted in visual block extraction.
	 */
	public static final Set<String> INVALID_NODES = new HashSet<String>(
			Arrays.asList(new String[] { "AREA", "BASE", "BASEFONT", "COL",
					"COLGROUP", "LINK", "MAP", "META", "PARAM", "SCRIPT",
					"STYLE", "TITLE", "!DOCTYPE", "NOSCRIPT" }));
	
	/**
	 * Inline nodes does not cause a new line when they appear in a page.
	 * Consequently, they do not create a block individually, but they form a composite block 
	 * as a group.
	 */
	public static final Set<String> INLINE_NODES = new HashSet<String>(
			Arrays.asList(new String[] { "A", "ABBR", "ACRONYM", "B", "BDO",
					"BIG", "BUTTON", "CITE", "CODE", "DEL", "DFN", "EM",
					"FONT", "I", "IMG", "INPUT", "INS", "KBD", "LABEL",
					"OBJECT", "Q", "S", "SAMP", "SMALL", "SPAN", "STRIKE",
					"STRONG", "SUB", "SUP", "TT", "U", "VAR", "APPLET",
					"SELECT", "TEXTAREA" }));
	
	/**
	 * Linebreak terminal nodes are those which are linebreak nodes
	 * and cannot have a child, do to their nature in HTML syntax or their children are omitted
	 * in visual block extraction, i.e. Object. 
	 */
	public static final Set<String> LINEBREAK_TERMINAL_NODES = new HashSet<String>(
			Arrays.asList(new String[] {"IMG", "OBJECT", "AUDIO", "COMMAND", "EMBED",
					"KEYGEN", "METER", "OUTPUT", "PROGRESS", "VIDEO"}));
	/**
	 * Inline terminal nodes are those which are inline nodes
	 * and cannot have a child, do to their nature in HTML syntax or their children are omitted
	 * in visual block extraction, i.e. Select
	 */
	public static final Set<String> INLINE_TERMINAL_NODES = new HashSet<String>(
			Arrays.asList(new String[] {"TEXTAREA", "SELECT", "BUTTON"}));
}
