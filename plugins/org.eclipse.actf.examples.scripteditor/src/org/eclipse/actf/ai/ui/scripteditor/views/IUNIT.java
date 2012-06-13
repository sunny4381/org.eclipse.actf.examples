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
	static final int TL_SYNC_MEDIA_TIME = 50; // (50 msec is for Audio interval
												// and 50*3 is for Video
												// interval.

	// Default Scale size : TimeLine view
	static final int TL_DEF_SCALESIZE = 50;
	// Minimum Label size : TimeLine view
	static final int TL_MIN_SCALESIZE = 5;
	// Position : Audio 1 Y position
	static final int TL_AUDIO1_SY = 18;// 8;
	static final int TL_DEFAULT_POINT_SY = 6;// 8;

	// Sampling graph amplify sampling level * TL_AUDIO_AMPLIFY_LEVEL
	static final int TL_AUDIO_AMPLIFY_LEVEL = 10;

	// Position : Audio 1 Y position for Mouse drag event
	static final int TL_AUDIO1_MDRAG_SY = 18;
	// Layout : Audio Label Height
	static final int TL_LABEL_HEIGHT = 13;;
	// Layout : Mouse drag Label Height
	static final int TL_LABEL_MDRAG_HEIGHT = 8;

	// Unit of Default Start Time for TimeLine : 0sec
	static final int TL_DEF_STIME = 0;
	// Unit of Default End Time for TimeLine : 10min->600sec->600000msec
	static final int TL_DEF_ETIME = 600000;
	// Margin time(=100msec) for 1st Script Data case of StartTime '00:00.000'
	static final int TL_MARGIN_STARTTIME = 100;
	// Default lines of Audio Label Composite of Time Line view
	static final int TL_DEF_LINES = 3;

	// Definition of Event of EndTimeLine
	static final int TL_NO_EVENT = 0; // No event
	static final int TL_OVER_MAX_LIMIT = 1; // Over max limit
	static final int TL_OVER_MIN_LIMIT = -1; // Over min limit

	// Definition of Control scroll Composite
	static final int TL_DEF_SCROL_COMP_SCALE = 300000; // Size of one scroll
														// Composite(5mins=300000msec)
	static final int TL_DEF_SCROL_COMP_T2P = 15000; // time to pixel of 5mins
	static final int TL_MAX_ENDTIME_COUNT = 40; // Max Counter of
												// EndTimeLine(40=200mins/5mins)
	static final int TL_MAX_ENDTIME_MSEC = 6000000; // Max Time of
													// EndTimeLine(100mins=6000000msec)

	// Definition of End of ScriptList
	static final int TL_EOL = -1; // -1 : End of Play (no more script data)
	static final int TL_NODATA = -2; // -2 : No Data

	// Status of Preview movie
	static final int TL_STAT_IDLE = 0; // 0 : Idle (stop movie or no content)
	static final int TL_STAT_PLAY = 1; // 1 : Auto play movie (automatic time
	// line)
	static final int TL_STAT_TRACK = 2; // 2 : Manual track play movie (manual
	// time line)
	static final int TL_STAT_RESUME = 3; // 3 : Resume auto play movie
	// (automatic time line)
	static final int TL_STAT_PAUSE = 4; // 4 : Pause movie (stop time line)
	static final int TL_STAT_EXTENDED = 5; // 5 : Play Extended text(suspend
											// time line)
	static final int TL_STAT_DISPOSE = -1; // -1 : Dispose own thread

	// Audio Label control
	static final long TL_SINGLE_CLICK_TIME = 500; // single click action of
													// TimeLine's Marker
													// (duration : 500msec)

	// Status of Media action
	static final int V_STAT_NOMEDIA = -1; // -1 : no media
	static final int V_STAT_UNKNOWN = 0; // 0 : Unknown media
	static final int V_STAT_PLAY = 1; // 1 : Play media
	static final int V_STAT_STOP = 2; // 2 : Stop media
	static final int V_STAT_PAUSE = 3; // 3 : Pause media
	static final int V_STAT_FASTFORWARD = 4; // 4 : Fast forward media
	static final int V_STAT_FASTREVERSE = 5; // 5 : Fast Reverse media
	static final int V_STAT_WAITING = 6; // 6 : Wait media

	// Process mode of Sound Mix
	static final int SM_PMODE_CAPTURE = 0; // 0 : Capture audio mode
	static final int SM_PMODE_FSAVE = 1; // 1 : Save fine(.wav) mode
	static final int SM_MAX_FREERUN_COUNT = 5; // 5 : Limit counter of free
												// running capture (less than
												// 100msec)
	static final float SM_CAP_RATE_FAST = 44100; // 44100Hz : Sampling rate of
													// capture voice (fast mode)
	static final float SM_CAP_RATE_NOM = 22050; // 22050Hz : Sampling rate of
												// capture voice (normal mode)
	static final float SM_CAP_RATE_LATE = 11025; // 11025Hz : Sampling rate of
													// capture voice (late mode)


	// file type for Drag&Drop file reader
	static final int LD_FTYPE_XML = 0; // file type : XML file
	static final int LD_FTYPE_CSV = 1; // file type : CSV file

	// Preference data
	static final int CSV_SAVE_RULE_INSERT = 0;
	static final int CSV_SAVE_RULE_RENEWAL = 1;
	static final int CSV_WAV_RULE_DROP = 0;
	static final int CSV_WAV_RULE_CONFIRM = 1;
	static final int CSV_WAV_RULE_IGNORE = 2;

	static final int SRT_SAVE_RULE_INSERT = 0;
	static final int SRT_SAVE_RULE_RENEWAL = 1;
	static final int SRT_WAV_RULE_DROP = 0;
	static final int SRT_WAV_RULE_CONFIRM = 1;
	static final int SRT_WAV_RULE_IGNORE = 2;

	// WAV file management
	static final int WAV_STAT_INVALID = -1; // -1 : Status of WAV file as
											// INVALID data mode

	static final int APPEND_TYPE_CSV = 0; // 0 append data is CSV
	static final int APPEND_TYPE_XML = 1; // 1 append data is XML
	static final int APPEND_TYPE_SCRIPT = 2; // 2 append data is
												// SCRIPT
	static final int APPEND_TYPE_CAPTION = 3; // 3 append data is
												// CAPTION

	static final int NO_UPDATE_ENDTIME = -1; // -1 No Update

	static final int NO_MARK = 0;
	static final int CAPTION_MARK = 1;
	static final int PLAY_MARK = 2;
	static final int VOICE_MARK = 3;
	static final int WAV_MARK = 4;

	static final int OUTPUT_CSV_TYPE_ALL = 0;
	static final int OUTPUT_CSV_TYPE_VG = 1;
	static final int OUTPUT_CSV_TYPE_SC = 2;

	static final int GET_SCRIPTDATA_AND_CAPTIONDATA = 0;
	static final int GET_CAPTIONDATA = 1;

	static final int MODE_MOVE = 0;
	static final int MODE_PUT = 1;
	static final int MODE_UPDATE = 2;
	// static final int MODE_PUT_CHILD = 3;

}
