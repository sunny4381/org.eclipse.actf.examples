/*******************************************************************************
 * Copyright (c) 2009, 2012 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.ai.scripteditor.util;

/**
 * 
 */
public class TimeFormatUtil {

	private static final int TIME_BLANK = -1;
	private static final int TIME_FORMAT_SHORT = 0;
	private static final int TIME_FORMAT_LONG = 1;

	public static String makeFormatHHMMSSMS(int totalSec) {
		return (makeFormatHHMMSSMS_proc(totalSec, TIME_FORMAT_LONG));
	}

	public static String makeFormatHHMMSSMS_short(int totalSec) {
		return (makeFormatHHMMSSMS_proc(totalSec, TIME_FORMAT_SHORT));
	}

	private static String makeFormatHHMMSSMS_proc(int totalSec, int outputType) {

		String formTime = new String();
		Integer hh;
		Integer mm;
		Integer ss;
		Integer ms;

		// Make format "HH:MM:SS.MSec"
		if (totalSec == TIME_BLANK) {
			formTime = "";
		} else if (totalSec <= 0) {
			if (outputType == TIME_FORMAT_LONG) {
				formTime = "00 : 00 : 00 . 000";
			} else {
				formTime = "00:00:00.000";
			}
		} else {

			// Integer to String
			hh = ((totalSec / 1000) / 60) / 60;
			totalSec = totalSec - (hh * 3600000);
			mm = (totalSec / 1000) / 60;
			ss = (totalSec / 1000) % 60;
			totalSec = totalSec - ((mm * 60000) + (ss * 1000));
			ms = totalSec % 1000;
			formTime = "";

			// HH
			if (hh < 10)
				formTime = formTime + "0";
			formTime = formTime + hh.toString();
			// separator
			if (outputType == TIME_FORMAT_LONG) {
				formTime += " : ";
			} else {
				formTime += ":";
			}
			// MM
			if (mm < 10)
				formTime = formTime + "0";
			formTime = formTime + mm.toString();
			// separator
			if (outputType == TIME_FORMAT_LONG) {
				formTime += " : ";
			} else {
				formTime += ":";
			}
			// SS
			if (ss < 10)
				formTime = formTime + "0";
			formTime = formTime + ss.toString();
			// separator
			if (outputType == TIME_FORMAT_LONG) {
				formTime += " . ";
			} else {
				formTime += ":";
			}
			// Milli Sec
			if (ms < 10)
				formTime = formTime + "00";
			else if ((ms < 100) && (ms >= 10))
				formTime = formTime + "0";
			formTime = formTime + ms.toString();
		}
		return (formTime);
	}

	public static String makeFormatHHMMSS(int totalSec) {
		return (makeFormatHHMMSS_proc(totalSec, TIME_FORMAT_LONG));
	}

	public static String makeFormatHHMMSS_short(int totalSec) {
		return (makeFormatHHMMSS_proc(totalSec, TIME_FORMAT_SHORT));
	}

	private static String makeFormatHHMMSS_proc(int totalSec, int outputType) {

		String formTime = new String();
		Integer hh;
		Integer mm;
		Integer ss;

		// Make format "HH:MM:SS"
		if (totalSec == TIME_BLANK) {
			formTime = "";
		} else if (totalSec <= 0) {
			if (outputType == TIME_FORMAT_LONG) {
				formTime = "00 : 00 : 00";
			} else {
				formTime = "00:00:00";
			}
		} else {
			// Integer to String
			hh = totalSec / 3600;
			mm = (totalSec / 60) - (hh * 60);
			ss = totalSec % 60;
			formTime = "";

			// HH
			if (hh < 10)
				formTime = formTime + "0";
			formTime = formTime + hh.toString();
			// separator
			if (outputType == TIME_FORMAT_LONG) {
				formTime += " : ";
			} else {
				formTime += ":";
			}
			// MM
			if (mm < 10)
				formTime = formTime + "0";
			formTime = formTime + mm.toString();
			// separator
			if (outputType == TIME_FORMAT_LONG) {
				formTime += " : ";
			} else {
				formTime += ":";
			}
			// SS
			if (ss < 10)
				formTime = formTime + "0";
			formTime = formTime + ss.toString();
		}
		return (formTime);
	}

	public static String makeFormatHH(int totalSec) {

		String formTime = new String();
		Integer tm, mm, hm;

		// Make format "HH"
		if (totalSec == TIME_BLANK) {
			// blank
			formTime = "";
		} else if (totalSec <= 0) {
			// default
			formTime = "00";
		} else {
			tm = totalSec / 1000;
			// Integer to String
			mm = tm / 60;
			hm = mm / 60;
			formTime = "";

			// HM
			if (hm < 10)
				formTime = formTime + "0";
			formTime = formTime + hm.toString();
		}
		return (formTime);
	}

	public static String makeFormatMM(int totalSec) {

		String formTime = new String();
		Integer tm, mm;

		// Make format "MM"
		if (totalSec == TIME_BLANK) {
			formTime = "";
		} else if (totalSec <= 0) {
			formTime = "00";
		} else {
			tm = totalSec / 1000;
			// Integer to String
			mm = tm / 60;
			mm = mm % 60;
			formTime = "";
			// MM
			if (mm < 10)
				formTime = formTime + "0";
			formTime = formTime + mm.toString();
		}
		return (formTime);
	}

	public static String makeFormatSS(int totalSec) {

		String formTime = new String();
		Integer ss;

		// Make format "SS"
		if (totalSec == TIME_BLANK) {
			formTime = "";
		} else if (totalSec <= 0) {
			formTime = "00";
		} else {
			// Integer to String
			ss = (totalSec / 1000) % 60;
			formTime = "";
			// SS
			if (ss < 10)
				formTime = formTime + "0";
			formTime = formTime + ss.toString();
		}
		return (formTime);
	}

	public static String makeFormatMS(int totalSec) {

		String formTime = new String();
		Integer ms;

		// Make format "Milli Sec"
		if (totalSec == TIME_BLANK) {
			formTime = "";
		} else if (totalSec <= 0) {
			formTime = "000";
		} else {
			// Integer to String
			ms = totalSec % 1000;
			formTime = "";

			// Milli Sec
			if (ms < 10)
				formTime = "00";
			else if ((ms < 100) && (ms >= 10))
				formTime = "0";

			formTime = formTime + ms.toString();
		}
		return (formTime);
	}

	public static int parseIntStartTime(String HH, String MM, String SS,
			String MS) {

		int startTime;

		if (HH == null) {
			HH = "";
		}
		if (MM == null) {
			MM = "";
		}
		if (SS == null) {
			SS = "";
		}
		if (MS == null) {
			MS = "";
		}

		if ((HH == "") || (MM == "") || (SS == "") || (MS == "")) {
			startTime = TIME_BLANK;
		} else {
			startTime = (Integer.parseInt(HH) * 3600)
					+ (Integer.parseInt(MM) * 60) + Integer.parseInt(SS);
			startTime = (startTime * 1000) + Integer.parseInt(MS);
		}
		return (startTime);
	}

	public static int parseIntStartTime(String HHMMSSMS) {

		int startTime;
		if (HHMMSSMS == null) {
			startTime = TIME_BLANK;
		} else if (HHMMSSMS == "") {
			startTime = TIME_BLANK;
		} else {
			// casting start time String to Integer
			String[] eachTime = HHMMSSMS.split(":", 0);
			String msTime = "000";
			if (eachTime[0] != null) {
				eachTime[0] = eachTime[0].trim();
			}
			if (eachTime[1] != null) {
				eachTime[1] = eachTime[1].trim();
			}
			if (eachTime[2] != null) {
				eachTime[2] = eachTime[2].trim();

				String[] tmp_ms = eachTime[2].split("\\.", 0);
				if (tmp_ms.length > 1) {
					eachTime[2] = tmp_ms[0].trim();
					msTime = tmp_ms[1].trim();
				} else {
					if (eachTime.length >= 4) {
						if (eachTime[3] != null) {
							eachTime[3] = eachTime[3].trim();
							msTime = eachTime[3];
						}
					} else if (eachTime.length == 3) {
						msTime = tmp_ms[0].trim();
						eachTime[2] = eachTime[1];
						eachTime[1] = eachTime[0];
						eachTime[0] = "0";
					}
				}

			}
			startTime = (Integer.parseInt(eachTime[0]) * 3600)
					+ (Integer.parseInt(eachTime[1]) * 60)
					+ Integer.parseInt(eachTime[2]);
			startTime = (startTime) * 1000 + Integer.parseInt(msTime);
		}
		return (startTime);
	}
}