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
package org.eclipse.actf.ai.ui.scripteditor.views;

/**
 * @category Implements UNIT data
 * 
 */
public interface IUNIT {

	// Unit of Millisecond
	static final int MSEC = 1000;

	// Exchange from Time to Pixel (=Time/20msec)
	static final int TIME2PIXEL = 20;
	
	// Timer counter : Synchronize movie position = 100msec
	static final int TL_SYNC_MEDIA_TIME = 100;
	// Timer counter : Sampling audio level = 20msec
	static final int TL_AUDIO_SAMPLE_TIME = 20;

	// Default Label : TimeLine view
	static final String TL_DEF_LABEL = " ";
	// Default Width : TimeLine view
	static final int TL_DEF_WIDTH = 540;
	// Default Height : TimeLine view
	static final int TL_DEF_HEIGHT = 147;
	// Default Scale size : TimeLine view
	static final int TL_DEF_SCALESIZE = 50;
	// Minimum Label size : TimeLine view
	static final int TL_MIN_SCALESIZE = 5;
	// Position : Audio 1 Y position
	static final int TL_AUDIO1_SY = 8;
	// Position : Audio 1 Y position for Mouse drag event
	static final int TL_AUDIO1_MDRAG_SY = 18;
	// Layout : Audio Label Height
	static final int TL_LABEL_HEIGHT = 16;
	// Layout : Mouse drag Label Height
	static final int TL_LABEL_MDRAG_HEIGHT = 8;

	// Unit of Default Start Time for TimeLine : 0sec
	static final int TL_DEF_STIME = 0;
	// Unit of Default End Time for TimeLine : 10min->600sec->600000msec
	static final int TL_DEF_ETIME = 600000;
	// Unit of Default TimeLine Scale : 20msec
	static final int TL_DEF_SCALE = 20;
	// Nominal time(100msec) of Duration as 1character play
	static final int TL_NOM_SAMPLE_DURATION = 100;
	// Margin time(=100msec) for 1st Script Data case of StartTime '00:00.000'
	static final int TL_MARGIN_STARTTIME = 100;
	// Unit of Adjust current TimeLine timing : each 200msec
	static final int TL_ADJ_TIMELINE = 200;
	// Default lines of Audio Label Composite of Time Line view
	static final int TL_DEF_LINES = 3;

	// Definition of Event of EndTimeLine
	static final int TL_NO_EVENT = 0;				// No event
	static final int TL_OVER_MAX_LIMIT = 1;			// Over max limit
	static final int TL_OVER_MIN_LIMIT = -1;		// Over min limit

	// Definition of Control scroll Composite
	static final int TL_DEF_SCROL_COMP_SCALE = 300000;	// Size of one scroll Composite(5mins=300000msec)
	static final int TL_DEF_SCROL_COMP_T2P = 15000;     // time to pixel of 5mins
	static final int TL_MAX_ENDTIME_COUNT = 20;			// Max Counter of EndTimeLine(20=100mins/5mins)
	static final int TL_MAX_ENDTIME_MSEC = 6000000;		// Max Time of EndTimeLine(100mins=6000000msec)
	static final int TL_LIMIT_STARTTIME = 100;			// Limit Start TimeLine (under 100mins)

	// Definition of End of ScriptList
	static final int TL_EOL = -1;		// -1 : End of Play (no more script data)
	static final int TL_NODATA = -2;	// -2 : No Data
	static final int TL_EOT = -3;       // -3 : End of TimeLine

	// Status of Preview movie
	static final int TL_STAT_IDLE = 0; // 0 : Idle (stop movie or no content)
	static final int TL_STAT_PLAY = 1; // 1 : Auto play movie (automatic time
	// line)
	static final int TL_STAT_TRACK = 2; // 2 : Manual track play movie (manual
	// time line)
	static final int TL_STAT_RESUME = 3; // 3 : Resume auto play movie
	// (automatic time line)
	static final int TL_STAT_PAUSE = 4; // 4 : Pause movie (stop time line)
	static final int TL_STAT_EXTENDED = 5;    // 5 : Play Extended text(suspend time line)
	static final int TL_STAT_DISPOSE = -1; // -1 : Dispose own thread

	// Audio Label control
	static final long TL_SINGLE_CLICK_TIME = 500;	// single click action of TimeLine's Marker (duration : 500msec)

	// Symbolic Definition for Voice Engine
	static final int VOICE_SAPI = 0; // 0 : Use VoiceManager(SAPI) engine
	static final int VOICE_PROTALKER = 1; // 1 : Use ProTalker engine
	static final int VE_EXCEPTION = -1; // -1 : Occur some problem in current
	// voice engine
	static final int VE_NOSUPPORT = -1; // -1 : No support function in current
	static final int VE_TIME_MORA_EN = 110;    // Time of 1Mora (=110msec)

	// TTS FLAGMENT
	static final boolean TTSFLAG_FLUSH = true;		// TTS FLAG : Flush voice data
	static final boolean TTSFLAG_DEFAULT = false;	// TTS FLAG : Normal play 

	// Audio Label control
	static final long AL_SINGLE_CLICK_TIME = 200;	// single click action of Audio Label (duration : 200msec)

	// Status of Media action
	static final int V_STAT_NOMEDIA = -1;  		// -1 : no media
	static final int V_STAT_UNKNOWN = 0;   		//  0 : Unknown media
	static final int V_STAT_PLAY = 1;      		//  1 : Play media
	static final int V_STAT_STOP = 2;      		//  2 : Stop media
	static final int V_STAT_PAUSE = 3;      	//  3 : Pause media
	static final int V_STAT_FASTFORWARD = 4;    //  4 : Fast forward media
	static final int V_STAT_FASTREVERSE = 5;    //  5 : Fast Reverse media
	static final int V_STAT_WAITING = 6;      	//  6 : Wait media

	// Process mode of Sound Mix
	static final int SM_PMODE_CAPTURE = 0;			// 0 : Capture audio mode
	static final int SM_PMODE_FSAVE = 1;			// 1 : Save fine(.wav) mode
	static final int SM_MAX_FREERUN_COUNT = 5;		// 5 : Limit counter of free running capture (less than 100msec)
	static final float SM_CAP_RATE_FAST = 44100;	// 44100Hz : Sampling rate of capture voice (fast mode)
	static final float SM_CAP_RATE_NOM = 22050;		// 22050Hz : Sampling rate of capture voice (normal mode)
	static final float SM_CAP_RATE_LATE = 11025;	// 11025Hz : Sampling rate of capture voice (late mode)

	// Language of Description
	static final String[] itemLang = { "English", "Japanese" };
	static final int DESC_LANG_EN = 0;
	static final int DESC_LANG_JA = 1;
	
	// temporary file interface
	static final String DIR_TEMP_VOLLVL = "VOLLVL";
	static final String FILE_TEMP_VOLLVL_PREFIX = "temp";
	static final String FILE_TEMP_VOLLVL_SUFFIX = ".lvl";
	static final String DIR_TEMP_WAVE = "WAV";
	static final String FILE_TEMP_WAVE_PREFIX = "temp";
	static final String FILE_TEMP_WAVE_SUFFIX = ".wav";
	
	// file type for Drag&Drop file reader
	static final int LD_FTYPE_XML = 0;				// file type : XML file
	static final int LD_FTYPE_CSV = 1;				// file type : CSV file

	// Preference data
	static final int CSV_SAVE_RULE_INSERT = 0;
	static final int CSV_SAVE_RULE_RENEWAL = 1;
	static final int CSV_WAV_RULE_DROP = 0;
	static final int CSV_WAV_RULE_CONFIRM = 1;
	static final int CSV_WAV_RULE_IGNORE = 2;
	
	// WAV file management
	static final int WAV_STAT_INVALID = -1;		// -1 : Status of WAV file as INVALID data mode

	// MessageBox part
	static final int MB_STYLE_CONFIRM = 1;		// 1 : Style of MessageBox is Confirmation mode
	static final int MB_STYLE_OVERWR = 2;		// 2 : Style of MessageBox is Overwrite mode
	static final int MB_STYLE_NODESC = 3;		// 3 : Style of MessageBox is No exist description mode
	static final int MB_STYLE_NOEXIST = 4;		// 4 : Style of MessageBox is No exist script ID mode
	static final int MB_STYLE_MODIFY = 5;		// 5 : Style of MessageBox is Modify current script list
	static final int MB_STYLE_WAV_CONFIRM = 6;	// 6 : Style of MessageBox is WAV file confirmation mode
	static final int MB_STYLE_ACCESS_DENIED = 7;	// 7 : Style of MessageBox is Notice access denied during play movie

}
