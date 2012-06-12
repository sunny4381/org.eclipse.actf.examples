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
package org.eclipse.actf.ai.scripteditor.reader;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.actf.ai.internal.ui.scripteditor.FileInfoStore;
import org.eclipse.actf.ai.scripteditor.data.DataUtil;
import org.eclipse.actf.ai.scripteditor.data.IScriptData;
import org.eclipse.actf.ai.scripteditor.data.ScriptDataFactory;
import org.eclipse.actf.ai.scripteditor.data.ScriptDataManager;
import org.eclipse.actf.ai.scripteditor.data.event.DataEventManager;
import org.eclipse.actf.ai.scripteditor.data.event.GuideListEvent;
import org.eclipse.actf.ai.scripteditor.util.TimeFormatUtil;
import org.eclipse.actf.ai.scripteditor.util.VoicePlayerFactory;
import org.eclipse.actf.ai.ui.scripteditor.views.IUNIT;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @category Default Handler for SAX I/F
 * 
 */
public class SAXReader extends DefaultHandler {

	private SAXParserFactory spf;
	private SAXParser sp;

	// XML TAGs
	private static final String XML_TAG_URI = "targetSite";
	private static final String XML_ATTR_URI = "uri";
	private static final String XML_TAG_ITEM = "item";
	private static final String XML_TAG_START = "start";
	private static final String XML_TAG_DURATION = "duration";
	private static final String XML_TAG_DESC = "description";
	private static final String XML_TAG_CAPTION = "caption";
	private static final String XML_TAG_SCENARIO = "scenario";
	private static final String XML_TAG_COMMENT = "comment";
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
	private String bkup_lang = "en-US"; // TODO use locale
	private String bkup_vollvl_loc = "";
	private String bkup_scenario = "";
	private String bkup_caption = "";
	private String bkup_comment = "";
	private int bkup_dataType;

	// for WAV file
	private boolean statWavTag = false;
	private boolean bkup_wav_ena = false;
	private String bkup_wav_uri = URI_BLANK;
	private String bkup_wav_loc = null;
	private String bkup_wav_duration = "00:00:000";
	private String bkup_wav_speed = "100";

	// process status
	private static final int SAX_STAT_IDLE = 0; // wait get element
	private static final int SAX_STAT_URI = 1; // loading "URI" value
	private static final int SAX_STAT_SITEM = 2; // start loading "item" node
	private static final int SAX_STAT_START = 3; // loading "start" value
	private static final int SAX_STAT_DURATION = 4; // loading "duration" value
	private static final int SAX_STAT_DESC = 5; // loading "description" value
	private static final int SAX_STAT_WAVE = 6; // loading "wave" value

	// private static final int SAX_STAT_EITEM = 7; // end of item, write to
	// // ScriptData

	private static final int SAX_STAT_VOLLVL = 8; // loading "volumeLevel" value

	private static final int SAX_STAT_SCENARIO = 9; //

	private static final int SAX_STAT_CAPTION = 10; //
	private static final int SAX_STAT_COMMENT = 11; //

	private int currentStatus = SAX_STAT_IDLE;
	private int currentChildStatus = SAX_STAT_IDLE;

	private ScriptDataManager scriptManager = null;
	private DataEventManager dataEventManager = null;

	/**
	 * @throws IOException
	 * @category Start Loading XML file by SAX I/F
	 */
	public void startSAXReader(String fname) throws SAXException,
			ParserConfigurationException {
		try {
			if (scriptManager == null) {
				scriptManager = ScriptDataManager.getInstance();
			}
			if (dataEventManager == null) {
				dataEventManager = DataEventManager.getInstance();
			}

			spf = SAXParserFactory.newInstance();
			sp = spf.newSAXParser();
			sp.parse(new File(fname), this);
		} catch (Exception e) {
		}
	}

	/**
	 * @category Get URI string
	 */
	public String getUri() {
		if (bkup_uri == null)
			bkup_uri = URI_BLANK;
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
				bkup_scenario = "";
				bkup_caption = "";
				bkup_comment = "";
				bkup_dataType = IScriptData.TYPE_AUDIO;
				bkup_extended = "false";
				bkup_speed = "50";
				bkup_gender = "male";
				bkup_lang = "en-US"; // TODO
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
						bkup_vollvl_loc = attr.getValue(i);
					}
				}
				// Change status
				currentStatus = SAX_STAT_VOLLVL;
			} else if (XML_TAG_SCENARIO.equals(qName)) {
				bkup_scenario = "";
				currentChildStatus = SAX_STAT_SCENARIO;
			} else if (XML_TAG_CAPTION.equals(qName)) {
				bkup_caption = "";
				currentChildStatus = SAX_STAT_CAPTION;
			} else if (XML_TAG_COMMENT.equals(qName)) {
				bkup_comment = "";
				currentChildStatus = SAX_STAT_COMMENT;
			}
		} catch (Exception e) {

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
			bkup_start = new String(ch, offset, length);
		}
		// Get value of "duration" node of "item" element
		else if ((currentChildStatus == SAX_STAT_DURATION)
				&& (currentStatus == SAX_STAT_SITEM)) {
			bkup_duration = new String(ch, offset, length);
		}
		// Get value of "description" node of "item" element
		else if ((currentChildStatus == SAX_STAT_DESC)
				&& (currentStatus == SAX_STAT_SITEM)) {
			bkup_desc += new String(ch, offset, length);
			bkup_dataType = IScriptData.TYPE_AUDIO;
		} else if ((currentChildStatus == SAX_STAT_SCENARIO)
				&& (currentStatus == SAX_STAT_SITEM)) {
			bkup_scenario += new String(ch, offset, length);
			bkup_dataType = IScriptData.TYPE_SCENARIO;
		} else if ((currentChildStatus == SAX_STAT_CAPTION)
				&& (currentStatus == SAX_STAT_SITEM)) {
			bkup_caption += new String(ch, offset, length);
			bkup_dataType = IScriptData.TYPE_CAPTION;
		} else if ((currentChildStatus == SAX_STAT_COMMENT)
				&& (currentStatus == SAX_STAT_SITEM)) {
			bkup_comment += new String(ch, offset, length);
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

			IScriptData data = ScriptDataFactory.createNewData();
			data.setStartTimeString(bkup_start);
			data.setScenario(bkup_scenario);
			data.setDescription(bkup_desc);
			data.setCaption(bkup_caption);
			data.setScriptComment(bkup_comment);
			data.setExtended(Boolean.parseBoolean(bkup_extended));
			data.setVgGender("male".equalsIgnoreCase(bkup_gender));
			data.setVgPlaySpeed(Integer.parseInt(bkup_speed));
			data.setVgPitch(Integer.parseInt(strPitch));
			data.setVgVolume(Integer.parseInt(strVolume));

			// TODO backward compatibility
			if ("ja".equals(bkup_lang)) {
				bkup_lang = "ja-JP";
			} else if ("en".equals(bkup_lang)) {
				bkup_lang = "en-US";
			}
			data.setLang(bkup_lang);
			data.setDataCommit(true);
			// data.setType(DataUtil.checkDataType(data));
			data.setType(bkup_dataType);
			if (statWavTag) {
				data.setWavEndTime(data.getStartTime()
						+ TimeFormatUtil.parseIntStartTime(bkup_wav_duration));
				data.setWavPlaySpeed(Float.parseFloat(bkup_wav_speed) / 100);
				try {
					data.setWavURI(new URI(bkup_wav_loc));
					data.setWavEnabled(bkup_wav_ena);
				} catch (URISyntaxException e) {
					// TODO : preference, existence?
					data.setDataCommit(false);
				}
			}
			
			int length = VoicePlayerFactory.getInstance().getSpeakLength(data);
			if (length > 0) {
				data.setEndTime(data.getStartTime() + length);
				data.setEndTimeAccurate(true);
			} else {
				data.setEndTime(data.getStartTime()
						+ TimeFormatUtil.parseIntStartTime(bkup_duration));
			}
			
			dataEventManager.fireGuideListEvent(new GuideListEvent(
					GuideListEvent.ADD_DATA, data, this));

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
					FileInfoStore.setVolumeLevelFilePath(bkup_vollvl_loc);
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
