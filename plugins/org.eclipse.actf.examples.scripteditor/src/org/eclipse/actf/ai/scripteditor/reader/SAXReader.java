/*******************************************************************************
 * Copyright (c) 2009, 2010 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.ai.scripteditor.reader;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.actf.ai.internal.ui.scripteditor.EditPanelTab;
import org.eclipse.actf.ai.internal.ui.scripteditor.VolumeLevelCanvas;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @category Default Handler for SAX I/F
 * 
 */
public class SAXReader extends DefaultHandler {
	// Local data
	private SAXParserFactory spf;
	private SAXParser sp;
	private EditPanelTab instParent;

	// XML TAGs
	private static final String XML_TAG_URI = "targetSite";
	private static final String XML_ATTR_URI = "uri";
	private static final String XML_TAG_ITEM = "item";
	private static final String XML_TAG_START = "start";
	private static final String XML_TAG_DURATION = "duration";
	private static final String XML_TAG_DESC = "description";
	private static final String XML_ATTR_SPEED = "speed";
	private static final String XML_ATTR_GENDER = "gender";
	private static final String XML_ATTR_EXTENDED = "extended";
	private static final String XML_ATTR_LANG = "xml:lang";
	private static final String XML_TAG_WAVE = "wave";
	private static final String XML_ATTR_LOCAL = "local";
	private static final String XML_ATTR_DURATION = "duration";
	private static final String XML_ATTR_ENABLED = "enabled";
	private static final String XML_TAG_VOLLVL = "volumeLevel";

	private static final String URI_BLANK = "about:blank";

	// BackUP data
	private String bkup_uri = URI_BLANK;
	private String bkup_start = "00:00:000";
	private String bkup_duration = "00:00:000";
	private String bkup_desc = "";
	private String bkup_extended = "false";
	private String bkup_speed = "50";
	private String bkup_gender = "male";
	private String bkup_lang = "en";
	private String bkup_vollvl_loc = "";

	// for WAV file
	private boolean statWavTag = false;
	private boolean bkup_wav_ena = false;
	private String bkup_wav_uri = URI_BLANK;
	private String bkup_wav_loc = null;
	private String bkup_wav_duration = "00:00:000";
	private String bkup_wav_speed = "100";

	// process status
	private static final int SAX_STAT_IDLE = 0; // 0 : wait get element
	private static final int SAX_STAT_URI = 1; // 1 : now loading "URI" value
	private static final int SAX_STAT_SITEM = 2; // 2 : start loading "item"
	// node
	private static final int SAX_STAT_START = 3; // 3 : now loading "start"
	// value
	private static final int SAX_STAT_DURATION = 4; // 4 : now loading
	// "duration" value
	private static final int SAX_STAT_DESC = 5; // 5 : now loading "description"
	// value
	private static final int SAX_STAT_WAVE = 6; // 6 : now loading "wave" value
	private static final int SAX_STAT_EITEM = 7; // 7 : end of item, and Write
	// to ScriptData
	private static final int SAX_STAT_VOLLVL = 8; // 8 : now loading
	// "volumeLevel" value

	// own mode
	private int currentStatus = SAX_STAT_IDLE;
	private int currentChildStatus = SAX_STAT_IDLE;

	/**
	 * @throws IOException
	 * @category Start Loading XML file by SAX I/F
	 */
	public void startSAXReader(String fname, EditPanelTab parent)
			throws SAXException, ParserConfigurationException {
		try {
			// Store parent instance
			instParent = parent;

			// MakeUP SAX parser
			spf = SAXParserFactory.newInstance();
			sp = spf.newSAXParser();
			// Load XML file by DefaultHandler
			sp.parse(new File(fname), this);
		} catch (IOException ioe) {
		} catch (SAXException se) {
		} catch (ParserConfigurationException pe) {
		}
	}

	/**
	 * @category Get URI string
	 */
	public String getUri() {
		// check string (never return null code)
		if (bkup_uri == null)
			bkup_uri = URI_BLANK;
		// return current URI string
		return (bkup_uri);
	}

	/**
	 * @category pre process before load document
	 */
	public void startDocument() throws SAXException {
		// Clear status
		currentStatus = SAX_STAT_IDLE;
	}

	/**
	 * @category start reading element
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes attr) throws SAXException {

		try {
			// Get "targetSite" element
			if (XML_TAG_URI.equals(qName)) {
				// Store attribute(URI) to local area
				for (int i = 0; i < attr.getLength(); i++) {
					if (XML_ATTR_URI.equals(attr.getQName(i))) {
						bkup_uri = attr.getValue(i);
					}
				}
				// Change status
				currentStatus = SAX_STAT_URI;
			}
			// Get "item" element
			else if (XML_TAG_ITEM.equals(qName)) {
				// Change status
				currentStatus = SAX_STAT_SITEM;
				currentChildStatus = SAX_STAT_IDLE;
				// Initialize all attribute's value
				bkup_start = "00:00:000";
				bkup_duration = "00:00:000";
				bkup_desc = "";
				bkup_extended = "false";
				bkup_speed = "50";
				bkup_gender = "male";
				bkup_lang = "en";
				// for WAV file
				statWavTag = false;
				bkup_wav_ena = false;
				bkup_wav_uri = "about:blank";
				bkup_wav_loc = null;
				bkup_wav_duration = "00:00:000";
				bkup_wav_speed = "100";
			}
			// Get "start" child node of "item" element
			else if (XML_TAG_START.equals(qName)) {
				// Change status
				currentChildStatus = SAX_STAT_START;
			}
			// Get "duration" child node of "item" element
			else if (XML_TAG_DURATION.equals(qName)) {
				// Change status
				currentChildStatus = SAX_STAT_DURATION;
			}
			// Get "description" child node of "item" element
			else if (XML_TAG_DESC.equals(qName)) {
				// Store attribute(speed, gender) to local area
				for (int i = 0; i < attr.getLength(); i++) {
					if (XML_ATTR_SPEED.equals(attr.getQName(i))) {
						// Store "speed" value
						bkup_speed = attr.getValue(i);
					} else if (XML_ATTR_GENDER.equals(attr.getQName(i))) {
						// Store "gender" value
						bkup_gender = attr.getValue(i);
					} else if (XML_ATTR_EXTENDED.equals(attr.getQName(i))) {
						// Store "extended" value
						bkup_extended = attr.getValue(i);
					} else if (XML_ATTR_LANG.equals(attr.getQName(i))) {
						// Store "lang" value
						bkup_lang = attr.getValue(i);
					}
				}
				// Change status
				currentChildStatus = SAX_STAT_DESC;
			}
			// Get "wave" child node of "item" element
			else if (XML_TAG_WAVE.equals(qName)) {
				// Store attribute(speed, gender) to local area
				bkup_wav_ena = true; // default true
				for (int i = 0; i < attr.getLength(); i++) {
					if (XML_ATTR_URI.equals(attr.getQName(i))) {
						bkup_wav_uri = attr.getValue(i);
					} else if (XML_ATTR_LOCAL.equals(attr.getQName(i))) {
						// Store "local" value
						bkup_wav_loc = attr.getValue(i);
					} else if (XML_ATTR_DURATION.equals(attr.getQName(i))) {
						// Store "duration" value
						bkup_wav_duration = attr.getValue(i);
					} else if (XML_ATTR_SPEED.equals(attr.getQName(i))) {
						// Store "speed" value
						bkup_wav_speed = attr.getValue(i);
					} else if (XML_ATTR_ENABLED.equals(attr.getQName(i))) {
						// Store "enable" value
						bkup_wav_ena = (attr.getValue(i).equals("false") ? false
								: true);
					}
				}
				// Change status
				currentChildStatus = SAX_STAT_WAVE;
				statWavTag = true;
			}
			// Get "volumeLevel" element
			else if (XML_TAG_VOLLVL.equals(qName)) {
				// Store attribute(local) to local area
				for (int i = 0; i < attr.getLength(); i++) {
					if (XML_ATTR_LOCAL.equals(attr.getQName(i))) {
						// Store "local" value
						bkup_vollvl_loc = attr.getValue(i);
					}
				}
				// Change status
				currentStatus = SAX_STAT_VOLLVL;
			}
		} catch (Exception ee) {

		}

	}

	/**
	 * @category start reading text
	 */
	public void characters(char[] ch, int offset, int length)
			throws SAXException {

		// Get value of "start" node of "item" element
		if ((currentChildStatus == SAX_STAT_START)
				&& (currentStatus == SAX_STAT_SITEM)) {
			// Store value
			bkup_start = new String(ch, offset, length);
		}
		// Get value of "duration" node of "item" element
		else if ((currentChildStatus == SAX_STAT_DURATION)
				&& (currentStatus == SAX_STAT_SITEM)) {
			// Store value
			bkup_duration = new String(ch, offset, length);
		}
		// Get value of "description" node of "item" element
		else if ((currentChildStatus == SAX_STAT_DESC)
				&& (currentStatus == SAX_STAT_SITEM)) {
			// Store value
			bkup_desc += new String(ch, offset, length);
		}
	}

	/**
	 * @category end reading element
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		// Clear current status
		if (XML_TAG_ITEM.equals(qName) && (currentStatus == SAX_STAT_SITEM)) {
			// no-supported parameters
			String strPitch = new String("50");
			String strVolume = new String("50");

			// Update ScriptList
			instParent.appendScriptData(bkup_start, bkup_duration, bkup_desc,
					bkup_extended, bkup_gender, bkup_speed, strPitch,
					strVolume, bkup_lang);

			// Check exist "wave" tag
			if (statWavTag) {
				// Update WAV file list
				instParent.appendDataWavList(bkup_start, bkup_wav_duration,
						bkup_wav_loc, bkup_wav_speed, bkup_wav_ena);
				statWavTag = false;
			}

			// Clear status
			currentStatus = SAX_STAT_IDLE;
			currentChildStatus = SAX_STAT_IDLE;
		} else if (XML_TAG_START.equals(qName)
				&& (currentStatus == SAX_STAT_SITEM)) {
			// Clear child status
			currentChildStatus = SAX_STAT_IDLE;
		} else if (XML_TAG_DURATION.equals(qName)
				&& (currentStatus == SAX_STAT_SITEM)) {
			// Clear child status
			currentChildStatus = SAX_STAT_IDLE;
		} else if (XML_TAG_DESC.equals(qName)
				&& (currentStatus == SAX_STAT_SITEM)) {
			// Clear child status
			currentChildStatus = SAX_STAT_IDLE;
		} else if (XML_TAG_URI.equals(qName) && (currentStatus == SAX_STAT_URI)) {
			// Clear status
			currentStatus = SAX_STAT_IDLE;
		} else if (XML_TAG_VOLLVL.equals(qName)
				&& (currentStatus == SAX_STAT_VOLLVL)) {
			// Store URI of volume level temporary file
			if (bkup_vollvl_loc != null) {
				try {
					VolumeLevelCanvas.getInstance().setSavePathVolLvl(
							bkup_vollvl_loc);
				} catch (Exception ee) {
				}
			}
			// Clear status
			currentStatus = SAX_STAT_IDLE;
		}
	}

	/**
	 * @category post process before load document
	 */
	public void endDocument() throws SAXException {
		// Clear status
		currentStatus = SAX_STAT_IDLE;
	}

}
