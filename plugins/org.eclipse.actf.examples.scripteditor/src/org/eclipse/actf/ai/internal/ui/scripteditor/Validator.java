/*******************************************************************************
 * Copyright (c) 2011, 2012 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.ai.internal.ui.scripteditor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

public class Validator {

	public static boolean checkNull(String data) {
		if (data != null && data.length() != 0) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean checkTimeFormat(String data) {
		// TODO short format HH':'mm ...
		SimpleDateFormat sdf1 = new SimpleDateFormat("HH' : 'mm' : 'ss' . 'SSS");
		try {
			sdf1.parse(data);
			return true;
		} catch (ParseException e) {
			return false;
		}

	}

	public static String format(String time) {

		StringBuffer sb = new StringBuffer();
		try {
			StringTokenizer token = new StringTokenizer(time, ".");
			if (token.countTokens() == 2) {
				String hhmmss = token.nextToken();
				StringTokenizer token2 = new StringTokenizer(hhmmss, ":");
				if (token2.countTokens() == 3) {
					sb.append(token2.nextToken().trim()).append(" : ");
					sb.append(token2.nextToken().trim()).append(" : ");
					sb.append(token2.nextToken().trim()).append(" . ");
				} else {
					return time;
				}

				sb.append(token.nextToken().trim());

			} else {
				return time;
			}
		} catch (Exception e) {
			return sb.toString();
		}
		return sb.toString();

	}
}
