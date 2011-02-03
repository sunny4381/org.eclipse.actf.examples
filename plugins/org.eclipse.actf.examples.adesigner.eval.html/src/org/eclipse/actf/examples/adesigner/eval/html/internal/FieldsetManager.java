/*******************************************************************************
 * Copyright (c) 2010,2011 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.examples.adesigner.eval.html.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

class FieldsetManager {
	private Map<String, List<Element>> ctrlMap = new HashMap<String, List<Element>>();
	private Map<String, Set<Integer>> fieldSetMap = new HashMap<String, Set<Integer>>();

	/**
	 * Adds a form control with the specified name attribute and contained in a
	 * fieldset with the specified index.
	 * 
	 * @param name
	 * @param ctrl
	 * @param fieldsetIndex
	 */
	public void addEntry(String name, Element ctrl, int fieldsetIndex) {
		List<Element> list;
		Set<Integer> set;
		if (ctrlMap.containsKey(name)) {
			list = ctrlMap.get(name);
			set = fieldSetMap.get(name);
		} else {
			list = new ArrayList<Element>();
			ctrlMap.put(name, list);
			set = new HashSet<Integer>();
			fieldSetMap.put(name, set);
		}
		list.add(ctrl);
		set.add(fieldsetIndex);
	}

	public List<Vector<Node>> getErrorList() {
		List<Vector<Node>> returns = new ArrayList<Vector<Node>>();
		for (String key : fieldSetMap.keySet()) {
			if (fieldSetMap.get(key).size() > 1) {
				returns.add(new Vector<Node>(ctrlMap.get(key)));
			}
		}
		return returns;
	}
}
